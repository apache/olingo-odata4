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

import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.ComplexTypeCollectionProcessor;
import org.apache.olingo.server.api.processor.ComplexTypeProcessor;
import org.apache.olingo.server.api.processor.CountEntityTypeCollectionProcessor;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.EntityTypeCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityTypeProcessor;
import org.apache.olingo.server.api.processor.ExceptionProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.PrimitiveTypeCollectionProcessor;
import org.apache.olingo.server.api.processor.PrimitiveTypeProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class ODataHandler {

  private final OData odata;
  private final ServiceMetadata serviceMetadata;
  private List<Processor> processors = new LinkedList<Processor>();
  private CustomContentTypeSupport customContentTypeSupport = null;

  private UriInfo uriInfo;

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
      handleException(request, response, serverError);
    } catch (final UriParserSemanticException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError);
    } catch (final UriParserSyntaxException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError);
    } catch (final UriParserException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError);
    } catch (ContentNegotiatorException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError);
    } catch (SerializerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError);
    } catch (ODataHandlerException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e, null);
      handleException(request, response, serverError);
    } catch (ODataApplicationException e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleException(request, response, serverError);
    } catch (Exception e) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleException(request, response, serverError);
    }
    return response;
  }

  private void processInternal(final ODataRequest request, final ODataResponse response)
      throws ODataHandlerException, UriParserException, UriValidationException, ContentNegotiatorException,
      ODataApplicationException, SerializerException {
    validateODataVersion(request, response);

    uriInfo = new Parser().parseUri(request.getRawODataPath(), request.getRawQueryPath(), null,
        serviceMetadata.getEdm());

    final HttpMethod method = request.getMethod();
    new UriValidator().validate(uriInfo, method);

    switch (uriInfo.getKind()) {
    case metadata:
      if (method.equals(HttpMethod.GET)) {
        final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.METADATA);
        selectProcessor(MetadataProcessor.class)
            .readMetadata(request, response, uriInfo, requestedContentType);
      } else {
        throw new ODataHandlerException("HttpMethod " + method + " not allowed for metadata document",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
      break;
    case service:
      if (method.equals(HttpMethod.GET)) {
        if ("".equals(request.getRawODataPath())) {
          selectProcessor(RedirectProcessor.class).redirect(request, response);
        } else {
          final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
              request, customContentTypeSupport, RepresentationType.SERVICE);

          selectProcessor(ServiceDocumentProcessor.class)
              .readServiceDocument(request, response, uriInfo, requestedContentType);
        }
      } else {
        throw new ODataHandlerException("HttpMethod " + method + " not allowed for service document",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
      break;
    case resource:
      handleResourceDispatching(request, response);
      break;
    default:
      throw new ODataHandlerException("not implemented",
          ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
    }
  }

  public void handleException(ODataRequest request, ODataResponse response, ODataServerError serverError) {
    ExceptionProcessor exceptionProcessor;
    try {
      exceptionProcessor = selectProcessor(ExceptionProcessor.class);
    } catch (ODataHandlerException e) {
      // This cannot happen since there is always an ExceptionProcessor registered.
      exceptionProcessor = new DefaultProcessor();
    }
    ContentType requestedContentType;
    try {
      requestedContentType = ContentNegotiator.doContentNegotiation(
          uriInfo == null ? null : uriInfo.getFormatOption(), request, customContentTypeSupport,
          RepresentationType.ERROR);
    } catch (final ContentNegotiatorException e) {
      requestedContentType = ODataFormat.JSON.getContentType(ODataServiceVersion.V40);
    }
    exceptionProcessor.processException(request, response, serverError, requestedContentType);
  }

  private void handleResourceDispatching(final ODataRequest request, final ODataResponse response)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException, SerializerException {
    final HttpMethod method = request.getMethod();
    final int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    final UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);

    switch (lastPathSegment.getKind()) {
    case entitySet:
    case navigationProperty:
      if (((UriResourcePartTyped) lastPathSegment).isCollection()) {
        if (method.equals(HttpMethod.GET)) {
          final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
              request, customContentTypeSupport, RepresentationType.COLLECTION_ENTITY);

          selectProcessor(EntityTypeCollectionProcessor.class)
              .readEntityTypeCollection(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      } else {
        if (method.equals(HttpMethod.GET)) {
          final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
              request, customContentTypeSupport, RepresentationType.ENTITY);

          selectProcessor(EntityTypeProcessor.class)
              .readEntityType(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      }
      break;

    case count:
      if (method.equals(HttpMethod.GET)) {
        final UriResource resource = uriInfo.getUriResourceParts().get(lastPathSegmentIndex - 1);
        if (resource instanceof UriResourceEntitySet || resource instanceof UriResourceNavigation) {
          selectProcessor(CountEntityTypeCollectionProcessor.class)
              .countEntityTypeCollection(request, response, uriInfo);
        } else {
          throw new ODataHandlerException(
              "Count of collections of primitive-type or complex-type instances is not implemented.",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      } else {
        throw new ODataHandlerException("HTTP method " + method + " is not allowed for count.",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
      break;

    case primitiveProperty:
      if (method.equals(HttpMethod.GET)) {
        final UriResourceProperty propertyResource = (UriResourceProperty) lastPathSegment;
        final RepresentationType representationType = propertyResource.isCollection() ?
            RepresentationType.COLLECTION_PRIMITIVE : RepresentationType.PRIMITIVE;
        final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, representationType);
        if (representationType == RepresentationType.PRIMITIVE) {
          selectProcessor(PrimitiveTypeProcessor.class)
              .readPrimitiveType(request, response, uriInfo, requestedContentType);
        } else {
          selectProcessor(PrimitiveTypeCollectionProcessor.class)
              .readPrimitiveTypeCollection(request, response, uriInfo, requestedContentType);
        }
      } else {
        throw new ODataHandlerException("not implemented",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
      break;

    case complexProperty:
      if (method.equals(HttpMethod.GET)) {
        final UriResourceProperty propertyResource = (UriResourceProperty) lastPathSegment;
        final RepresentationType representationType = propertyResource.isCollection() ?
            RepresentationType.COLLECTION_COMPLEX : RepresentationType.COMPLEX;
        final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, representationType);
        if (representationType == RepresentationType.COMPLEX) {
          selectProcessor(ComplexTypeProcessor.class)
              .readComplexType(request, response, uriInfo, requestedContentType);
        } else {
          selectProcessor(ComplexTypeCollectionProcessor.class)
              .readComplexTypeCollection(request, response, uriInfo, requestedContentType);
        }
      } else {
        throw new ODataHandlerException("not implemented",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
      break;

    case value:
      if (method.equals(HttpMethod.GET)) {
        final UriResource resource = uriInfo.getUriResourceParts().get(lastPathSegmentIndex - 1);
        if (resource instanceof UriResourceProperty) {
          final RepresentationType representationType =
              (EdmPrimitiveType) ((UriResourceProperty) resource).getType() ==
              EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary) ?
                  RepresentationType.BINARY : RepresentationType.VALUE;
          final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
              request, customContentTypeSupport, representationType);

          selectProcessor(PrimitiveTypeProcessor.class)
              .readPrimitiveTypeAsValue(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("Media Entity is not implemented.",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
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
    final String maxVersion = request.getHeader(HttpHeader.ODATA_MAX_VERSION);
    response.setHeader(HttpHeader.ODATA_VERSION, ODataServiceVersion.V40.toString());

    if (maxVersion != null) {
      if (ODataServiceVersion.isBiggerThan(ODataServiceVersion.V40.toString(), maxVersion)) {
        throw new ODataHandlerException("ODataVersion not supported: " + maxVersion,
            ODataHandlerException.MessageKeys.ODATA_VERSION_NOT_SUPPORTED, maxVersion);
      }
    }
  }

  private <T extends Processor> T selectProcessor(final Class<T> cls) throws ODataHandlerException {
    for (final Processor processor : processors) {
      if (cls.isAssignableFrom(processor.getClass())) {
        processor.init(odata, serviceMetadata);
        return cls.cast(processor);
      }
    }
    throw new ODataHandlerException("Processor: " + cls.getSimpleName() + " not registered.",
        ODataHandlerException.MessageKeys.PROCESSOR_NOT_IMPLEMENTED, cls.getSimpleName());
  }

  public void register(final Processor processor) {
    processors.add(0, processor);
  }

  public void register(final CustomContentTypeSupport customContentTypeSupport) {
    this.customContentTypeSupport = customContentTypeSupport;
  }
}
