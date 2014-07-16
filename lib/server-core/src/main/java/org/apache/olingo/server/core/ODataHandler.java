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

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.CollectionProcessor;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.validator.UriValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ODataHandler {

  private final static Logger LOG = LoggerFactory.getLogger(ODataHandler.class);

  private final OData odata;
  private final Edm edm;
  private Map<Class<? extends Processor>, Processor> processors = new HashMap<Class<? extends Processor>, Processor>();

  public ODataHandler(final OData server, final Edm edm) {
    odata = server;
    this.edm = edm;

    register(new DefaultProcessor());
    register(new DefaultRedirectProcessor());
  }

  public ODataResponse process(final ODataRequest request) {
    try {
      ODataResponse response = new ODataResponse();

      validateODataVersion(request, response);

      Parser parser = new Parser();
      String odUri =
          request.getRawODataPath() + (request.getRawQueryPath() == null ? "" : "?" + request.getRawQueryPath());
      UriInfo uriInfo = parser.parseUri(odUri, edm);

      UriValidator validator = new UriValidator();
      validator.validate(uriInfo, request.getMethod());

      ContentType requestedContentType = null;
      switch (uriInfo.getKind()) {
      case metadata:
        MetadataProcessor mp = selectProcessor(MetadataProcessor.class);

        requestedContentType =
            ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, mp, MetadataProcessor.class);

        mp.readMetadata(request, response, uriInfo, requestedContentType);
        break;
      case service:
        if ("".equals(request.getRawODataPath())) {
          RedirectProcessor rdp = selectProcessor(RedirectProcessor.class);
          rdp.redirect(request, response);
        } else {
          ServiceDocumentProcessor sdp = selectProcessor(ServiceDocumentProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, sdp,
                  ServiceDocumentProcessor.class);

          sdp.readServiceDocument(request, response, uriInfo, requestedContentType);
        }
        break;
      case resource:
        handleResourceDispatching(request, response, uriInfo);
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

  private void
      handleResourceDispatching(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo) {
    int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);
    ContentType requestedContentType = null;

    switch (lastPathSegment.getKind()) {
    case entitySet:
      if (((UriResourcePartTyped) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          CollectionProcessor cp = selectProcessor(CollectionProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, cp, CollectionProcessor.class);

          cp.readCollection(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, EntityProcessor.class);

          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      }
      break;
    case navigationProperty:
      if (((UriResourceNavigation) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          CollectionProcessor cp = selectProcessor(CollectionProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, cp, CollectionProcessor.class);

          cp.readCollection(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, EntityProcessor.class);

          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      }
      break;
    default:
      throw new ODataRuntimeException("not implemented");
    }
  }

  private void validateODataVersion(final ODataRequest request, final ODataResponse response) {
    String maxVersion = request.getHeader(HttpHeader.ODATA_MAX_VERSION);

    if (maxVersion != null) {
      if (ODataServiceVersion.isBiggerThan(ODataServiceVersion.V40.toString(), maxVersion)) {
        throw new ODataRuntimeException("400 Bad Request - ODataVersion not supported: " + maxVersion);
      }
    }

    response.setHeader(HttpHeader.ODATA_VERSION, ODataServiceVersion.V40.toString());
  }

  private <T extends Processor> T selectProcessor(final Class<T> cls) {
    @SuppressWarnings("unchecked")
    T p = (T) processors.get(cls);

    if (p == null) {
      throw new ODataRuntimeException("Not implemented");
    }

    return p;
  }

  public void register(final Processor processor) {

    processor.init(odata, edm);

    for (Class<?> cls : processor.getClass().getInterfaces()) {
      if (Processor.class.isAssignableFrom(cls) && cls != Processor.class) {
        @SuppressWarnings("unchecked")
        Class<? extends Processor> procClass = (Class<? extends Processor>) cls;
        processors.put(procClass, processor);
      }
    }
  }
}