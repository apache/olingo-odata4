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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.EntitySetProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.ExceptionProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.PropertyProcessor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class ODataHandler {

  private final OData odata;
  private final ServiceMetadata serviceMetadata;
  private final Map<Class<? extends Processor>, Processor> processors =
      new HashMap<Class<? extends Processor>, Processor>();
  private ContentType requestedContentType;

  public ODataHandler(final OData server, final ServiceMetadata serviceMetadata) {
    odata = server;
    this.serviceMetadata = serviceMetadata;

    register(new DefaultProcessor());
    register(new DefaultRedirectProcessor());
  }

  public ODataResponse process(final ODataRequest request) {
    ODataResponse response = new ODataResponse();
    try {

      processInternal(request, response);

    } catch (final UriValidationException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, null);
    } catch (final UriParserSemanticException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, null);
    } catch (final UriParserSyntaxException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, null);
    } catch (final UriParserException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, null);
    } catch (ContentNegotiatorException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, null);
    } catch (SerializerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, requestedContentType);
    } catch (ODataHandlerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError, requestedContentType);
    } catch (ODataApplicationException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleException(request, response, serverError, requestedContentType);
    } catch (Exception e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleException(request, response, serverError, requestedContentType);
    }
    return response;
  }

  private void processInternal(final ODataRequest request, final ODataResponse response)
      throws ODataHandlerException, UriParserException, UriValidationException, ContentNegotiatorException,
      ODataApplicationException, SerializerException {
    validateODataVersion(request, response);

    Parser parser = new Parser();
    final UriInfo uriInfo = parser.parseUri(
            request.getRawODataPath(), request.getRawQueryPath(),
            null, serviceMetadata.getEdm());

    UriValidator validator = new UriValidator();
    validator.validate(uriInfo, request.getMethod());

    switch (uriInfo.getKind()) {
    case metadata:
      if (request.getMethod().equals(HttpMethod.GET)) {
        MetadataProcessor mp = selectProcessor(MetadataProcessor.class);

        requestedContentType =
            ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, mp, MetadataProcessor.class);
        mp.readMetadata(request, response, uriInfo, requestedContentType);
      } else {
        throw new ODataHandlerException("HttpMethod " + request.getMethod() + " not allowed for metadata document",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, request.getMethod().toString());
      }
      break;
    case service:
      if (request.getMethod().equals(HttpMethod.GET)) {
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
      } else {
        throw new ODataHandlerException("HttpMethod " + request.getMethod() + " not allowed for service document",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, request.getMethod().toString());
      }
      break;
    case resource:
      handleResourceDispatching(request, response, uriInfo);
      break;
    default:
      throw new ODataHandlerException("not implemented",
          ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
    }
  }

  public void handleException(ODataRequest request, ODataResponse response, ODataServerError serverError,
      ContentType requestedContentType) {
    ExceptionProcessor exceptionProcessor;
    try {
      exceptionProcessor = selectProcessor(ExceptionProcessor.class);
    } catch (ODataTranslatedException e) {
      // This cannot happen since there is always an ExceptionProcessor registered
      exceptionProcessor = new DefaultProcessor();
    }
    if (requestedContentType == null) {
      requestedContentType = ODataFormat.JSON.getContentType(ODataServiceVersion.V40);
    }
    exceptionProcessor.processException(request, response, serverError, requestedContentType);
  }

  private void handleResourceDispatching(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo) throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException {
    int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);

    switch (lastPathSegment.getKind()) {
    case entitySet:
      if (((UriResourcePartTyped) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntitySetProcessor cp = selectProcessor(EntitySetProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, cp,
                  EntitySetProcessor.class);

          cp.readEntitySet(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, EntityProcessor.class);

          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      }
      break;
    case navigationProperty:
      if (((UriResourceNavigation) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntitySetProcessor cp = selectProcessor(EntitySetProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, cp,
                  EntitySetProcessor.class);

          cp.readEntitySet(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class);

          requestedContentType =
              ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, EntityProcessor.class);

          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      }
      break;
    case count:
      if (request.getMethod().equals(HttpMethod.GET)) {
        EntitySetProcessor cp = selectProcessor(EntitySetProcessor.class);
        cp.countEntitySet(request, response, uriInfo);
      } else {
        throw new ODataHandlerException("not implemented",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
      break;
    case primitiveProperty:
    case complexProperty:
      if (request.getMethod().equals(HttpMethod.GET)) {
        PropertyProcessor ep = selectProcessor(PropertyProcessor.class);

        requestedContentType =
            ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, PropertyProcessor.class);

        ep.readProperty(request, response, uriInfo, requestedContentType);
      } else {
        throw new ODataHandlerException("not implemented",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
      break;
    case value:
      if (request.getMethod().equals(HttpMethod.GET)) {
        PropertyProcessor ep = selectProcessor(PropertyProcessor.class);
        requestedContentType =
                ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request, ep, PropertyProcessor.class);
        ep.readPropertyValue(request, response, uriInfo, requestedContentType);
      } else {
        throw new ODataHandlerException("not implemented",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
      break;
    default:
      throw new ODataHandlerException("not implemented",
          ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
    }
  }

  private void validateODataVersion(final ODataRequest request, final ODataResponse response)
      throws ODataHandlerException {
    String maxVersion = request.getHeader(HttpHeader.ODATA_MAX_VERSION);
    response.setHeader(HttpHeader.ODATA_VERSION, ODataServiceVersion.V40.toString());

    if (maxVersion != null) {
      if (ODataServiceVersion.isBiggerThan(ODataServiceVersion.V40.toString(), maxVersion)) {
        throw new ODataHandlerException("ODataVersion not supported: " + maxVersion,
            ODataHandlerException.MessageKeys.ODATA_VERSION_NOT_SUPPORTED, maxVersion);
      }
    }
  }

  private <T extends Processor> T selectProcessor(final Class<T> cls) throws ODataHandlerException {
    @SuppressWarnings("unchecked")
    T p = (T) processors.get(cls);

    if (p == null) {
      throw new ODataHandlerException("Processor: " + cls.getName() + " not registered.",
          ODataHandlerException.MessageKeys.PROCESSOR_NOT_IMPLEMENTED, cls.getName());
    }

    return p;
  }

  public void register(final Processor processor) {
    processor.init(odata, serviceMetadata);

    for (Class<?> cls : processor.getClass().getInterfaces()) {
      if (Processor.class.isAssignableFrom(cls) && cls != Processor.class) {
        @SuppressWarnings("unchecked")
        Class<? extends Processor> procClass = (Class<? extends Processor>) cls;
        processors.put(procClass, processor);
      }
    }
  }
}
