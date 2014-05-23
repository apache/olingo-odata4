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
package org.apache.olingo.server.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class ODataHandler {

  private final OData odata;
  private final Edm edm;
  private Map<Class<? extends Processor>, Processor> processors = new HashMap<Class<? extends Processor>, Processor>();

  public ODataHandler(final OData server, final Edm edm) {
    this.odata = server;
    this.edm = edm;

    register(new DefaultProcessor());
  }

  public ODataResponse process(final ODataRequest request) {
    try {
      ODataResponse response = new ODataResponse();

      Parser parser = new Parser();
      String odUri =
          request.getRawODataPath() + (request.getRawQueryPath() == null ? "" : "?" + request.getRawQueryPath());
      UriInfo uriInfo = parser.parseUri(odUri, edm);

      UriValidator validator = new UriValidator();
      validator.validate(uriInfo, request.getMethod());

      switch (uriInfo.getKind()) {
      case metadata:
        MetadataProcessor mp = selectProcessor(MetadataProcessor.class);
        mp.readMetadata(request, response, uriInfo, HttpContentType.APPLICATION_XML);
        break;
      case service:
        if ("".equals(request.getRawODataPath())) {
          RedirectProcessor rdp = selectProcessor(RedirectProcessor.class);
          rdp.redirect(request, response);
        }else{
          ServiceDocumentProcessor sdp = selectProcessor(ServiceDocumentProcessor.class);
          sdp.readServiceDocument(request, response, uriInfo, HttpContentType.APPLICATION_JSON);
        }
        break;
      default:
        throw new ODataRuntimeException("not implemented");
      }

      return response;
    } catch (Exception e) {
      // TODO OData error message handling
      throw new RuntimeException(e);
    }
  }

  private <T extends Processor> T selectProcessor(Class<T> cls) {
    @SuppressWarnings("unchecked")
    T p = (T) processors.get(cls);

    if (p == null) {
      throw new ODataRuntimeException("Not implemented");
    }

    return p;
  }

  public void register(Processor processor) {

    processor.init(odata, edm);

    for (Class<?> cls : processor.getClass().getInterfaces()) {
      if (Processor.class.isAssignableFrom(cls)) {
        @SuppressWarnings("unchecked")
        Class<? extends Processor> procClass = (Class<? extends Processor>) cls;
        processors.put(procClass, processor);
      }
    }
  }
}
