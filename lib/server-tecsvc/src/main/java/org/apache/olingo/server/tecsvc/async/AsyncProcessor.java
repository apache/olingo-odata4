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

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.Processor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AsyncProcessor<T extends Processor> {
    private final MyInvocationHandler handler;
    private final TechnicalAsyncService service;
    private final T proxyProcessor;
    private String location;
    private String preferHeader;

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


    public ODataResponse getProcessResponse() {
      return processResponse;
    }
  }


  public AsyncProcessor(T processor, Class<T> processorInterface, TechnicalAsyncService service) {
      Class<? extends Processor> aClass = processor.getClass();
      Class[] interfaces = aClass.getInterfaces();
      handler = new MyInvocationHandler(processor);
      Object proxyInstance = Proxy.newProxyInstance(aClass.getClassLoader(), interfaces, handler);
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

    Object process() throws InvocationTargetException, IllegalAccessException {
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

    void setLocation(String loc) {
      this.location = loc;
    }
  }
