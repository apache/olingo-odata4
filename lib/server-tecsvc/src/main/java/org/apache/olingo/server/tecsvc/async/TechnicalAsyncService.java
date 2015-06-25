/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.tecsvc.async;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.Processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TechnicalAsyncService {

  public static final String TEC_ASYNC_SLEEP = "tec.sleep";

  private static final Map<String, AsyncRunner> LOCATION_2_ASYNC_RUNNER =
      Collections.synchronizedMap(new HashMap<String, AsyncRunner>());
  private static final ExecutorService ASYNC_REQUEST_EXECUTOR = Executors.newFixedThreadPool(10);
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
  public static final String STATUS_MONITOR_TOKEN = "async";

  private static class MyInvocationHandler implements InvocationHandler {
    private final Object wrappedInstance;
    private Method invokeMethod;
    private Object[] invokeParameters;

    public MyInvocationHandler(Object wrappedInstance) {
      this.wrappedInstance = wrappedInstance;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
      if(Processor.class.isAssignableFrom(method.getDeclaringClass())) {
        invokeMethod = method;
        invokeParameters = objects;
      }

      return null;
    }

    ODataResponse processResponse;

    public Object process() throws InvocationTargetException, IllegalAccessException {
      processResponse = new ODataResponse();
      replaceInvokeParameter(processResponse);
      return invokeMethod.invoke(wrappedInstance, invokeParameters);
    }

    public Object[] getInvokeParameters() {
      return invokeParameters;
    }

    public <P> void replaceInvokeParameter(P replacement) {
      if(replacement == null) {
        return;
      }

      List<Object> copy = new ArrayList<Object>();
      for (Object parameter : invokeParameters) {
        if (replacement.getClass() == parameter.getClass()) {
          copy.add(replacement);
        } else {
          copy.add(parameter);
        }
      }
      invokeParameters = copy.toArray();
    }

    private <P> P getParameter(Class<P> parameterClass) {
      for (Object parameter : invokeParameters) {
        if (parameter != null && parameterClass == parameter.getClass()) {
          return parameterClass.cast(parameter);
        }
      }
      return null;
    }

    public ODataResponse getProcessResponse() {
      return processResponse;
    }
  }

  public class AsyncProcessor<T extends Processor> {
    private final MyInvocationHandler handler;
    private final TechnicalAsyncService service;
    private final Object proxyInstance;
    private final T proxyProcessor;
    private String location;
    private String preferHeader;

    public AsyncProcessor(T processor, Class<T> processorInterface, TechnicalAsyncService service) {
      Class<? extends Processor> aClass = processor.getClass();
      Class[] interfaces = aClass.getInterfaces();
      handler = new MyInvocationHandler(processor);
      proxyInstance = Proxy.newProxyInstance(aClass.getClassLoader(), interfaces, handler);
      proxyProcessor = processorInterface.cast(proxyInstance);
      this.service = service;
    }

    public T prepareFor() {
      return proxyProcessor;
    }

    public ODataRequest getRequest() {
      return getParameter(ODataRequest.class);
    }

    public ODataResponse getResponse() {
      return getParameter(ODataResponse.class);
    }

    public ODataResponse getProcessResponse() {
      return handler.getProcessResponse();
    }

    private <P> P getParameter(Class<P> parameterClass) {
      for (Object parameter : handler.getInvokeParameters()) {
        if (parameter != null && parameterClass == parameter.getClass()) {
          return parameterClass.cast(parameter);
        }
      }
      return null;
    }

    public String processAsync() throws ODataApplicationException, ODataLibraryException {
      preferHeader = getRequest().getHeader(HttpHeader.PREFER);
      ODataRequest request = copyRequest(getRequest());
      handler.replaceInvokeParameter(request);
      handler.replaceInvokeParameter(new ODataResponse());
      return service.processAsynchronous(this);
    }

    private Object process() throws InvocationTargetException, IllegalAccessException {
      return handler.process();
    }

    private ODataRequest copyRequest(ODataRequest request) {
      ODataRequest req = new ODataRequest();
      req.setBody(request.getBody());
      req.setMethod(request.getMethod());
      req.setRawBaseUri(request.getRawBaseUri());
      req.setRawODataPath(request.getRawODataPath());
      req.setRawQueryPath(request.getRawQueryPath());
      req.setRawRequestUri(request.getRawRequestUri());
      req.setRawServiceResolutionUri(request.getRawServiceResolutionUri());

      for (Map.Entry<String, List<String>> header : request.getAllHeaders().entrySet()) {
        if(HttpHeader.PREFER.toLowerCase().equals(
                header.getKey().toLowerCase())) {
          preferHeader = header.getValue().get(0);
        } else {
          req.addHeader(header.getKey(), header.getValue());
        }
      }

      return req;
    }

    public String getPreferHeader() {
      return preferHeader;
    }

    public String getLocation() {
      return location;
    }

    private void setLocation(String loc) {
      this.location = loc;
    }
  }


  public <T extends Processor> AsyncProcessor<T> register(T processor, Class<T> processorInterface) {
    return new AsyncProcessor<T>(processor, processorInterface, this);
  }


  private static final class AsyncProcessorHolder {
    private static final TechnicalAsyncService INSTANCE = new TechnicalAsyncService();
  }

  public static TechnicalAsyncService getInstance() {
    return AsyncProcessorHolder.INSTANCE;
  }

  public void shutdownThreadPool() {
    ASYNC_REQUEST_EXECUTOR.shutdown();
  }

  public boolean isStatusMonitorResource(HttpServletRequest request) {
    return request.getRequestURI() != null && request.getRequestURI().contains(STATUS_MONITOR_TOKEN);
  }

  private String processAsynchronous(AsyncProcessor dispatchedProcessor)
      throws ODataApplicationException, ODataLibraryException {
    // use executor thread pool
    String location = createNewAsyncLocation(dispatchedProcessor.getRequest());
    dispatchedProcessor.setLocation(location);
    AsyncRunner run = new AsyncRunner(dispatchedProcessor);
    LOCATION_2_ASYNC_RUNNER.put(location, run);
    ASYNC_REQUEST_EXECUTOR.execute(run);
    //
    return location;
  }

  public void status(ODataRequest request, ODataResponse response)
          throws ODataApplicationException, ODataLibraryException {

  }

  public void cancel(ODataRequest request, ODataResponse response)
          throws ODataApplicationException, ODataLibraryException {

  }

  public void handle(HttpServletRequest request, HttpServletResponse response) {
    String location = getAsyncLocation(request);
    AsyncRunner runner = LOCATION_2_ASYNC_RUNNER.get(location);

    if(runner == null) {
      response.setStatus(HttpStatusCode.NOT_FOUND.getStatusCode());
    } else {
      if(runner.isFinished()) {
        ODataResponse wrapResult = runner.getDispatched().getProcessResponse();
        convertToHttp(response, wrapResult);
        LOCATION_2_ASYNC_RUNNER.remove(location);
      } else {
        response.setStatus(HttpStatusCode.ACCEPTED.getStatusCode());
        response.setHeader(HttpHeader.LOCATION, location);
        String content = "In progress for async location = " + location;
        writeToResponse(response, content);
      }
    }
  }

  private void writeToResponse(HttpServletResponse response, String content) {
    OutputStream output = null;
    try {
      output = response.getOutputStream();
      output.write(content.getBytes());
    } catch (IOException e) {
      throw new ODataRuntimeException(e);
    } finally {
      closeStream(output);
    }
  }

  static void convertToHttp(final HttpServletResponse response, final ODataResponse odResponse) {
    response.setStatus(odResponse.getStatusCode());

    for (Map.Entry<String, String> entry : odResponse.getHeaders().entrySet()) {
      response.setHeader(entry.getKey(), entry.getValue());
    }

    InputStream input = odResponse.getContent();
    if (input != null) {
      OutputStream output = null;
      try {
        output = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int n;
        while (-1 != (n = input.read(buffer))) {
          output.write(buffer, 0, n);
        }
      } catch (IOException e) {
        throw new ODataRuntimeException(e);
      } finally {
        closeStream(output);
        closeStream(input);
      }
    }
  }

  private static void closeStream(final Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }


  private String createNewAsyncLocation(ODataRequest request) {
    return request.getRawBaseUri() + "/" + STATUS_MONITOR_TOKEN + request.getRawODataPath() +
            "?" + STATUS_MONITOR_TOKEN + "=" + ID_GENERATOR.incrementAndGet();
  }

  private String getAsyncLocation(HttpServletRequest request) {
    return "http://localhost:8080" + request.getRequestURI() + "?" + request.getQueryString();
  }

  private String getAsyncQueryPart(ODataRequest request) {
    String rawQueryPath = request.getRawQueryPath();
    if(rawQueryPath != null) {
      Matcher m = Pattern.compile("(" + STATUS_MONITOR_TOKEN + "=\\d*)").matcher(rawQueryPath);
      if(m.find()) {
        return m.group();
      }
    }
    return "";
  }

  private static class AsyncRunner implements Runnable {
    private final AsyncProcessor dispatched;
    private int defaultSleepTimeInSeconds = 0;
    private Exception exception;
    boolean finished = false;

    public AsyncRunner(AsyncProcessor wrap) {
      this(wrap, 0);
    }

    public AsyncRunner(AsyncProcessor wrap, int defaultSleepTimeInSeconds) {
      this.dispatched = wrap;
      if(defaultSleepTimeInSeconds > 0) {
        this.defaultSleepTimeInSeconds = defaultSleepTimeInSeconds;
      }
    }

    @Override
    public void run() {
      try {
        int sleep = getSleepTime(dispatched);
        TimeUnit.SECONDS.sleep(sleep);
        dispatched.process();
      } catch (Exception e) {
        exception = e;
      }
      finished = true;
    }

    private int getSleepTime(AsyncProcessor wrap) {
//      String preferHeader = wrap.getRequest().getHeader(HttpHeader.PREFER);
      String preferHeader = wrap.getPreferHeader();
      Matcher matcher = Pattern.compile("(" + TEC_ASYNC_SLEEP +
              "=)(\\d*)").matcher(preferHeader);
      if (matcher.find()) {
        String waitTimeAsString = matcher.group(2);
        return Integer.parseInt(waitTimeAsString);
      }
      return defaultSleepTimeInSeconds;
    }

    public Exception getException() {
      return exception;
    }

    public boolean isFinished() {
      return finished;
    }

    public AsyncProcessor getDispatched() {
      return dispatched;
    }
  }
}
