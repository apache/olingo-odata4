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

import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.Processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Async processor "wraps" an Processor (or subclass of) to provide asynchronous support functionality
 * in combination with the TechnicalAsyncService.
 *
 * @param <T> "wrapped" Processor
 */
public class AsyncProcessor<T extends Processor> {
  private final ProcessorInvocationHandler handler;
  private final TechnicalAsyncService service;
  private final T proxyProcessor;
  private String location;
  private String preferHeader;

  /**
   * InvocationHandler which is used as proxy for the Processor method.
   */
  private static class ProcessorInvocationHandler implements InvocationHandler {
    private final Object wrappedInstance;
    private Method invokeMethod;
    private Object[] invokeParameters;
    private ODataRequest processRequest;
    private ODataResponse processResponse;

    public ProcessorInvocationHandler(Object wrappedInstance) {
      this.wrappedInstance = wrappedInstance;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
      if (Processor.class.isAssignableFrom(method.getDeclaringClass())) {
        invokeMethod = method;
        invokeParameters = Arrays.copyOf(objects, objects.length);
      } else {
        throw new ODataRuntimeException("Invalid class '" + method.getDeclaringClass() +
            "' can not wrapped for asynchronous processing.");
      }

      return null;
    }

    /**
     * Prepare the handler for the <code>process()</code> call (which is asynchronous and can be at any time in
     * the future).
     */
    void prepareForAsync() {
      processRequest = copyRequest(getParameter(ODataRequest.class));
      processResponse = createODataResponse(getParameter(ODataResponse.class));
    }

    Object process() throws InvocationTargetException, IllegalAccessException {
      if(processRequest == null || processResponse == null) {
        throw new ODataRuntimeException("ProcessInvocationHandler was not correct prepared for async processsing.");
      }
      replaceInvokeParameter(processRequest);
      replaceInvokeParameter(processResponse);
      return invokeMethod.invoke(wrappedInstance, invokeParameters);
    }

    <P> void replaceInvokeParameter(P replacement) {
      if (replacement == null) {
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

    /**
     * Get the ODataResponse which is used when this ProcessorInvocationHandler
     * is called (via its <code>process()</code> method)
     *
     * @return ODataResponse which is used when this ProcessorInvocationHandler is called
     */
    ODataResponse getProcessResponse() {
      return processResponse;
    }

    Object getWrappedInstance() {
      return this.wrappedInstance;
    }

    <P> P getParameter(Class<P> parameterClass) {
      for (Object parameter : invokeParameters) {
        if (parameter != null && parameterClass == parameter.getClass()) {
          return parameterClass.cast(parameter);
        }
      }
      return null;
    }
  }


  public AsyncProcessor(T processor, Class<T> processorInterface, TechnicalAsyncService service) {
    Class<? extends Processor> aClass = processor.getClass();
    Class<?>[] interfaces = aClass.getInterfaces();
    handler = new ProcessorInvocationHandler(processor);
    Object proxyInstance = Proxy.newProxyInstance(aClass.getClassLoader(), interfaces, handler);
    proxyProcessor = processorInterface.cast(proxyInstance);
    this.service = service;
  }

  public T prepareFor() {
    return proxyProcessor;
  }

  public ODataRequest getRequest() {
    return handler.getParameter(ODataRequest.class);
  }

  public ODataResponse getResponse() {
    return handler.getParameter(ODataResponse.class);
  }

  public ODataResponse getProcessResponse() {
    return handler.getProcessResponse();
  }

  public String getPreferHeader() {
    return preferHeader;
  }

  public String getLocation() {
    return location;
  }

  public Class<?> getProcessorClass() {
    return handler.getWrappedInstance().getClass();
  }

  /**
   * Start the asynchronous processing and returns the id for this process
   *
   * @return the id for this process
   * @throws ODataApplicationException
   * @throws ODataLibraryException
   */
  public String processAsync() throws ODataApplicationException, ODataLibraryException {
    preferHeader = getRequest().getHeader(HttpHeader.PREFER);
    handler.prepareForAsync();
    return service.processAsynchronous(this);
  }

  private static ODataResponse createODataResponse(ODataResponse response) {
    ODataResponse created = new ODataResponse();
    for (Map.Entry<String, List<String>> header : response.getAllHeaders().entrySet()) {
      created.addHeader(header.getKey(), header.getValue());
    }
    return created;
  }

  Object process() throws InvocationTargetException, IllegalAccessException {
    return handler.process();
  }

  void setLocation(String loc) {
    this.location = loc;
  }

  static ODataRequest copyRequest(ODataRequest request) {
    ODataRequest req = new ODataRequest();
    req.setBody(copyRequestBody(request));
    req.setMethod(request.getMethod());
    req.setRawBaseUri(request.getRawBaseUri());
    req.setRawODataPath(request.getRawODataPath());
    req.setRawQueryPath(request.getRawQueryPath());
    req.setRawRequestUri(request.getRawRequestUri());
    req.setRawServiceResolutionUri(request.getRawServiceResolutionUri());

    for (Map.Entry<String, List<String>> header : request.getAllHeaders().entrySet()) {
      if (!HttpHeader.PREFER.toLowerCase().equals(
          header.getKey().toLowerCase())) {
        req.addHeader(header.getKey(), header.getValue());
      }
    }

    return req;
  }

  static InputStream copyRequestBody(ODataRequest request) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    InputStream input = request.getBody();
    if (input != null) {
      try {
        ByteBuffer inBuffer = ByteBuffer.allocate(8192);
        ReadableByteChannel ic = Channels.newChannel(input);
        WritableByteChannel oc = Channels.newChannel(buffer);
        while (ic.read(inBuffer) > 0) {
          inBuffer.flip();
          oc.write(inBuffer);
          inBuffer.rewind();
        }
        return new ByteArrayInputStream(buffer.toByteArray());
      } catch (IOException e) {
        throw new ODataRuntimeException("Error on reading request content");
      }
    }
    return null;
  }
}
