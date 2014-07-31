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
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.processor.CollectionProcessor;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.ExceptionProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class ODataHandler {

  private final OData odata;
  private final Edm edm;
  private final Map<Class<? extends Processor>, Processor> processors =
      new HashMap<Class<? extends Processor>, Processor>();

  public ODataHandler(final OData server, final Edm edm) {
    odata = server;
    this.edm = edm;

    register(new DefaultProcessor());
    register(new DefaultRedirectProcessor());
  }

  public ODataResponse process(final ODataRequest request) {
    ContentType requestedContentType = null;
    ODataResponse response = new ODataResponse();
    try {
      validateODataVersion(request, response);

      Parser parser = new Parser();
      String odUri =
          request.getRawODataPath() + (request.getRawQueryPath() == null ? "" : "?" + request.getRawQueryPath());
      UriInfo uriInfo = parser.parseUri(odUri, edm);

      UriValidator validator = new UriValidator();
      validator.validate(uriInfo, request.getMethod());

      switch (uriInfo.getKind()) {
      case metadata:
        MetadataProcessor mp = selectProcessor(MetadataProcessor.class, response);

        requestedContentType =
            ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, mp, MetadataProcessor.class);

        mp.readMetadata(request, response, uriInfo, requestedContentType);
        break;
      case service:
        if ("".equals(request.getRawODataPath())) {
          RedirectProcessor rdp = selectProcessor(RedirectProcessor.class, response);
          rdp.redirect(request, response);
        } else {
          ServiceDocumentProcessor sdp = selectProcessor(ServiceDocumentProcessor.class, response);

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
        throw new ODataTranslatedException("not implemented",
            ODataTranslatedException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
    } catch (ODataTranslatedException e) {
      Locale requestedLocale = null;
      ODataServerError serverError =
          ODataExceptionHelper.createServerErrorObject(e, response.getStatusCode(), requestedLocale);
      handleException(request, response, serverError, requestedContentType);
    } catch (ODataApplicationException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleException(request, response, serverError, requestedContentType);
    } catch (Exception e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, response.getStatusCode());
      handleException(request, response, serverError, requestedContentType);
    }
    return response;
  }

  private void handleException(ODataRequest request, ODataResponse response, ODataServerError serverError,
      ContentType requestedContentType) {
    ExceptionProcessor exceptionProcessor;
    try {
      exceptionProcessor = selectProcessor(ExceptionProcessor.class, response);
    } catch (ODataTranslatedException e) {
      exceptionProcessor = new DefaultProcessor();
    }
      if (requestedContentType == null) {
        requestedContentType = ODataFormat.JSON.getContentType(ODataServiceVersion.V40);
      }
      exceptionProcessor.processException(request, response, serverError, requestedContentType);
  }

  private void handleResourceDispatching(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo) throws ODataTranslatedException {
    int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);
    ContentType requestedContentType = null;

    switch (lastPathSegment.getKind()) {
    case entitySet:
      if (((UriResourcePartTyped) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          CollectionProcessor cp = selectProcessor(CollectionProcessor.class, response);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, cp, CollectionProcessor.class);

          cp.readCollection(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataTranslatedException("not implemented",
              ODataTranslatedException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class, response);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, EntityProcessor.class);

          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataTranslatedException("not implemented",
              ODataTranslatedException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      }
      break;
    case navigationProperty:
      if (((UriResourceNavigation) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          CollectionProcessor cp = selectProcessor(CollectionProcessor.class, response);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, cp, CollectionProcessor.class);

          cp.readCollection(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataTranslatedException("not implemented",
              ODataTranslatedException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class, response);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, EntityProcessor.class);

          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataTranslatedException("not implemented",
              ODataTranslatedException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      }
      break;
    default:
      throw new ODataTranslatedException("not implemented",
          ODataTranslatedException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
    }
  }

  private void validateODataVersion(final ODataRequest request, final ODataResponse response)
      throws ODataTranslatedException {
    String maxVersion = request.getHeader(HttpHeader.ODATA_MAX_VERSION);
    response.setHeader(HttpHeader.ODATA_VERSION, ODataServiceVersion.V40.toString());

    if (maxVersion != null) {
      if (ODataServiceVersion.isBiggerThan(ODataServiceVersion.V40.toString(), maxVersion)) {
        response.setStatusCode(400);
        throw new ODataTranslatedException("ODataVersion not supported: " + maxVersion,
            ODataTranslatedException.MessageKeys.ODATA_VERSION_NOT_SUPPORTED, maxVersion);
      }
    }
  }

  private <T extends Processor> T selectProcessor(final Class<T> cls, ODataResponse response)
      throws ODataTranslatedException {
    @SuppressWarnings("unchecked")
    T p = (T) processors.get(cls);

    if (p == null) {
      response.setStatusCode(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode());
      throw new ODataTranslatedException("Processor: " + cls.getName() + " not registered.",
          ODataTranslatedException.MessageKeys.PROCESSOR_NOT_IMPLEMENTED, cls.getName());
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