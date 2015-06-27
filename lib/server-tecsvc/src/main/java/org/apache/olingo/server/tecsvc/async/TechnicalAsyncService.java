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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.serializer.SerializerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TechnicalAsyncService {

  public static final String TEC_ASYNC_SLEEP = "tec.sleep";

  private static final Map<String, AsyncRunner> LOCATION_2_ASYNC_RUNNER =
      Collections.synchronizedMap(new HashMap<String, AsyncRunner>());
  private static final ExecutorService ASYNC_REQUEST_EXECUTOR = Executors.newFixedThreadPool(10);
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
  public static final String STATUS_MONITOR_TOKEN = "status";




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
    return request.getRequestURL() != null && request.getRequestURL().toString().contains(STATUS_MONITOR_TOKEN);
  }

  String processAsynchronous(AsyncProcessor dispatchedProcessor)
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

  public void handle(HttpServletRequest request, HttpServletResponse response) throws SerializerException, IOException {
    String location = getAsyncLocation(request);
    AsyncRunner runner = LOCATION_2_ASYNC_RUNNER.get(location);

    if(runner == null) {
      response.setStatus(HttpStatusCode.NOT_FOUND.getStatusCode());
    } else {
      if(runner.isFinished()) {
        ODataResponse wrapResult = runner.getDispatched().getProcessResponse();
        wrapToAsyncHttpResponse(wrapResult, response);
        LOCATION_2_ASYNC_RUNNER.remove(location);
      } else {
        response.setStatus(HttpStatusCode.ACCEPTED.getStatusCode());
        response.setHeader(HttpHeader.LOCATION, location);
        String content = "In progress for async location = " + location;
        writeToResponse(response, content);
      }
    }
  }

  private static void writeToResponse(HttpServletResponse response, InputStream input) throws IOException {
    copy(input, response.getOutputStream());
  }

  private void writeToResponse(HttpServletResponse response, String content) {
    writeToResponse(response, content.getBytes());
  }

  private static void writeToResponse(HttpServletResponse response, byte[] content) {
    OutputStream output = null;
    try {
      output = response.getOutputStream();
      output.write(content);
    } catch (IOException e) {
      throw new ODataRuntimeException(e);
    } finally {
      closeStream(output);
    }
  }

  static void wrapToAsyncHttpResponse(final ODataResponse odResponse, final HttpServletResponse response)
      throws SerializerException, IOException {
    OData odata = OData.newInstance();
    InputStream odResponseStream = odata.createFixedFormatSerializer().asyncResponse(odResponse);

    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_HTTP.toContentTypeString());
    response.setHeader(HttpHeader.CONTENT_ENCODING, "binary");
    response.setStatus(HttpStatusCode.OK.getStatusCode());

    writeToResponse(response, odResponseStream);
  }

  static void writeHttpResponse(final ODataResponse odResponse, final HttpServletResponse response) throws IOException {
    response.setStatus(odResponse.getStatusCode());

    for (Map.Entry<String, String> entry : odResponse.getHeaders().entrySet()) {
      response.setHeader(entry.getKey(), entry.getValue());
    }

    copy(odResponse.getContent(), response.getOutputStream());
  }

  static void copy(final InputStream input, final OutputStream output) {
    if(output == null || input == null) {
      return;
    }

    try {
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
    int pos = request.getRawBaseUri().lastIndexOf("/") + 1;
    return request.getRawBaseUri().substring(0, pos) + STATUS_MONITOR_TOKEN + "/" + ID_GENERATOR.incrementAndGet();
  }

  private String getAsyncLocation(HttpServletRequest request) {
    return request.getRequestURL().toString();
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
