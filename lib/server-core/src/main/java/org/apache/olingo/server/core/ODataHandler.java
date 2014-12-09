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

import org.apache.olingo.commons.api.edm.EdmEntityType;
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
import org.apache.olingo.server.api.batch.exception.BatchException;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.processor.ComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ComplexProcessor;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.ExceptionProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.PrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.processor.PrimitiveValueProcessor;
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
import org.apache.olingo.server.core.batchhandler.BatchHandler;
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
    } catch (DeserializerException e) {
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
      ODataApplicationException, SerializerException, DeserializerException, BatchException {
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
    case batch:
      final BatchProcessor bp = selectProcessor(BatchProcessor.class);
      final BatchHandler handler = new BatchHandler(this, bp);
      
      handler.process(request, response, true);
      
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
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
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

          selectProcessor(EntityCollectionProcessor.class)
              .readEntityCollection(request, response, uriInfo, requestedContentType);
        } else if (method.equals(HttpMethod.POST)) {
          if (isMedia(lastPathSegment)) {
            final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
                request, customContentTypeSupport, RepresentationType.ENTITY);
            final ContentType requestFormat =
                    ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
            selectProcessor(MediaEntityProcessor.class)
                .createEntity(request, response, uriInfo, requestFormat, responseFormat);
          } else {
            throw new ODataHandlerException("not implemented",
                ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
          }
        } else {
          throw new ODataHandlerException("HTTP method not allowed.",
              ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
        }
      } else {
        if (method.equals(HttpMethod.GET)) {
          final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
              request, customContentTypeSupport, RepresentationType.ENTITY);

          selectProcessor(EntityProcessor.class)
              .readEntity(request, response, uriInfo, requestedContentType);
        } else if (method.equals(HttpMethod.DELETE)) {
          if (isMedia(lastPathSegment)) {
            selectProcessor(MediaEntityProcessor.class)
                .deleteEntity(request, response, uriInfo);
          } else {
            throw new ODataHandlerException("not implemented",
                ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
          }
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
          selectProcessor(CountEntityCollectionProcessor.class)
              .countEntityCollection(request, response, uriInfo, ContentType.TEXT_PLAIN);
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
          selectProcessor(PrimitiveProcessor.class)
              .readPrimitive(request, response, uriInfo, requestedContentType);
        } else {
          selectProcessor(PrimitiveCollectionProcessor.class)
              .readPrimitiveCollection(request, response, uriInfo, requestedContentType);
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
          selectProcessor(ComplexProcessor.class)
              .readComplex(request, response, uriInfo, requestedContentType);
        } else {
          selectProcessor(ComplexCollectionProcessor.class)
              .readComplexCollection(request, response, uriInfo, requestedContentType);
        }
      } else {
        throw new ODataHandlerException("not implemented",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
      break;

    case value:
      final UriResource resource = uriInfo.getUriResourceParts().get(lastPathSegmentIndex - 1);
      if (resource instanceof UriResourceProperty) {
        if (method.equals(HttpMethod.GET)) {
          final RepresentationType representationType =
              (EdmPrimitiveType) ((UriResourceProperty) resource).getType() ==
              EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary) ?
                  RepresentationType.BINARY : RepresentationType.VALUE;
          final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
              request, customContentTypeSupport, representationType);

          selectProcessor(PrimitiveValueProcessor.class)
              .readPrimitiveValue(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
      } else {
        if (method.equals(HttpMethod.GET)) {
          final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
              request, customContentTypeSupport, RepresentationType.MEDIA);
          selectProcessor(MediaEntityProcessor.class)
              .readMediaEntity(request, response, uriInfo, requestedContentType);
        } else if (method.equals(HttpMethod.PUT)) {
          final ContentType requestFormat =
                  ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
          final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
                  request, customContentTypeSupport, RepresentationType.ENTITY);
          selectProcessor(MediaEntityProcessor.class)
              .updateMediaEntity(request, response, uriInfo, requestFormat, responseFormat);
        } else if (method.equals(HttpMethod.DELETE)) {
          selectProcessor(MediaEntityProcessor.class)
              .deleteEntity(request, response, uriInfo);
        } else {
          throw new ODataHandlerException("not implemented",
              ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
        }
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

  private boolean isMedia(final UriResource pathSegment) {
    return pathSegment instanceof UriResourceEntitySet
        && ((UriResourceEntitySet) pathSegment).getEntityType().hasStream()
        || pathSegment instanceof UriResourceNavigation
        && ((EdmEntityType) ((UriResourceNavigation) pathSegment).getType()).hasStream();
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
