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

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.ActionComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionComplexProcessor;
import org.apache.olingo.server.api.processor.ActionEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionEntityProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveProcessor;
import org.apache.olingo.server.api.processor.ActionVoidProcessor;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.processor.ComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ComplexProcessor;
import org.apache.olingo.server.api.processor.CountComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.CountPrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.ErrorProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.PrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.processor.PrimitiveValueProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ReferenceCollectionProcessor;
import org.apache.olingo.server.api.processor.ReferenceProcessor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
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

    register(new DefaultRedirectProcessor());
    register(new DefaultProcessor());
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
      ODataApplicationException, SerializerException, DeserializerException {
    validateODataVersion(request, response);

    uriInfo = new Parser().parseUri(request.getRawODataPath(), request.getRawQueryPath(), null,
        serviceMetadata.getEdm());

    final HttpMethod method = request.getMethod();
    new UriValidator().validate(uriInfo, method);

    switch (uriInfo.getKind()) {
    case metadata:
      checkMethod(method, HttpMethod.GET);
      final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
          request, customContentTypeSupport, RepresentationType.METADATA);
      selectProcessor(MetadataProcessor.class)
          .readMetadata(request, response, uriInfo, requestedContentType);
      break;

    case service:
      checkMethod(method, HttpMethod.GET);
      if ("".equals(request.getRawODataPath())) {
        selectProcessor(RedirectProcessor.class)
            .redirect(request, response);
      } else {
        final ContentType serviceContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.SERVICE);
        selectProcessor(ServiceDocumentProcessor.class)
            .readServiceDocument(request, response, uriInfo, serviceContentType);
      }
      break;

    case resource:
      handleResourceDispatching(request, response);
      break;

    case batch:
      checkMethod(method, HttpMethod.POST);
      new BatchHandler(this, selectProcessor(BatchProcessor.class))
          .process(request, response, true);
      break;

    default:
      throw new ODataHandlerException("not implemented",
          ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
    }
  }

  public void handleException(final ODataRequest request, final ODataResponse response,
      final ODataServerError serverError) {

    ErrorProcessor exceptionProcessor;
    try {
      exceptionProcessor = selectProcessor(ErrorProcessor.class);
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
      requestedContentType = ODataFormat.JSON.getContentType();
    }
    exceptionProcessor.processError(request, response, serverError, requestedContentType);
  }

  private void handleResourceDispatching(final ODataRequest request, final ODataResponse response)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {

    final int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    final UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);

    switch (lastPathSegment.getKind()) {
    case action:
      checkMethod(request.getMethod(), HttpMethod.POST);
      handleActionDispatching(request, response, (UriResourceAction) lastPathSegment);
      break;

    case function:
      checkMethod(request.getMethod(), HttpMethod.GET);
      handleFunctionDispatching(request, response, (UriResourceFunction) lastPathSegment);
      break;

    case entitySet:
    case navigationProperty:
      handleEntityDispatching(request, response,
          ((UriResourcePartTyped) lastPathSegment).isCollection(), isMedia(lastPathSegment));
      break;

    case count:
      checkMethod(request.getMethod(), HttpMethod.GET);
      handleCountDispatching(request, response, lastPathSegmentIndex);
      break;

    case primitiveProperty:
      handlePrimitiveDispatching(request, response,
          ((UriResourceProperty) lastPathSegment).isCollection());
      break;

    case complexProperty:
      handleComplexDispatching(request, response,
          ((UriResourceProperty) lastPathSegment).isCollection());
      break;

    case value:
      handleValueDispatching(request, response, lastPathSegmentIndex);
      break;

    case ref:
      handleReferenceDispatching(request, response, lastPathSegmentIndex);
      break;

    default:
      throw new ODataHandlerException("not implemented",
          ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
    }
  }

  private void handleFunctionDispatching(final ODataRequest request, final ODataResponse response,
      final UriResourceFunction uriResourceFunction)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
    EdmFunction function = uriResourceFunction.getFunction();
    if (function == null) {
      function = uriResourceFunction.getFunctionImport().getUnboundFunctions().get(0);
    }
    final EdmReturnType returnType = function.getReturnType();
    switch (returnType.getType().getKind()) {
    case ENTITY:
      handleEntityDispatching(request, response,
          returnType.isCollection() && uriResourceFunction.getKeyPredicates().isEmpty(),
          false);
      break;
    case PRIMITIVE:
      handlePrimitiveDispatching(request, response, returnType.isCollection());
      break;
    case COMPLEX:
      handleComplexDispatching(request, response, returnType.isCollection());
      break;
    default:
      throw new ODataHandlerException("not implemented",
          ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
    }
  }

  private void handleActionDispatching(final ODataRequest request, final ODataResponse response,
      final UriResourceAction uriResourceAction)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
    final EdmAction action = uriResourceAction.getAction();
    final EdmReturnType returnType = action.getReturnType();
    final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
    checkContentTypeSupport(requestFormat, RepresentationType.ACTION_PARAMETERS);

    if (returnType == null) {
      selectProcessor(ActionVoidProcessor.class)
          .processActionVoid(request, response, uriInfo, requestFormat);
    } else {
      final boolean isCollection = returnType.isCollection();
      ContentType responseFormat = null;
      switch (returnType.getType().getKind()) {
      case ENTITY:
        responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport,
            isCollection ? RepresentationType.COLLECTION_ENTITY : RepresentationType.ENTITY);
        if (isCollection) {
          selectProcessor(ActionEntityCollectionProcessor.class)
              .processActionEntityCollection(request, response, uriInfo, requestFormat, responseFormat);
        } else {
          selectProcessor(ActionEntityProcessor.class)
              .processActionEntity(request, response, uriInfo, requestFormat, responseFormat);
        }
        break;

      case PRIMITIVE:
        responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport,
            isCollection ? RepresentationType.COLLECTION_PRIMITIVE : RepresentationType.PRIMITIVE);
        if (isCollection) {
          selectProcessor(ActionPrimitiveCollectionProcessor.class)
              .processActionPrimitiveCollection(request, response, uriInfo, requestFormat, responseFormat);
        } else {
          selectProcessor(ActionPrimitiveProcessor.class)
              .processActionPrimitive(request, response, uriInfo, requestFormat, responseFormat);
        }
        break;

      case COMPLEX:
        responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport,
            isCollection ? RepresentationType.COLLECTION_COMPLEX : RepresentationType.COMPLEX);
        if (isCollection) {
          selectProcessor(ActionComplexCollectionProcessor.class)
              .processActionComplexCollection(request, response, uriInfo, requestFormat, responseFormat);
        } else {
          selectProcessor(ActionComplexProcessor.class)
              .processActionComplex(request, response, uriInfo, requestFormat, responseFormat);
        }
        break;

      default:
        throw new ODataHandlerException("not implemented",
            ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
      }
    }
  }

  private void handleReferenceDispatching(final ODataRequest request, final ODataResponse response,
      final int lastPathSegmentIndex)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
    final HttpMethod method = request.getMethod();
    if (((UriResourcePartTyped) uriInfo.getUriResourceParts().get(lastPathSegmentIndex - 1)).isCollection()) {
      if (method == HttpMethod.GET) {
        final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.COLLECTION_REFERENCE);
        selectProcessor(ReferenceCollectionProcessor.class)
            .readReferenceCollection(request, response, uriInfo, responseFormat);
      } else if (method == HttpMethod.POST) {
        final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
        checkContentTypeSupport(requestFormat, RepresentationType.REFERENCE);
        selectProcessor(ReferenceProcessor.class)
            .createReference(request, response, uriInfo, requestFormat);
      } else {
        throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
    } else {
      if (method == HttpMethod.GET) {
        final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.REFERENCE);
        selectProcessor(ReferenceProcessor.class).readReference(request, response, uriInfo, responseFormat);
      } else if (method == HttpMethod.PUT || method == HttpMethod.PATCH) {
        final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
        checkContentTypeSupport(requestFormat, RepresentationType.REFERENCE);
        selectProcessor(ReferenceProcessor.class)
            .updateReference(request, response, uriInfo, requestFormat);
      } else if (method == HttpMethod.DELETE) {
        selectProcessor(ReferenceProcessor.class)
            .deleteReference(request, response, uriInfo);
      } else {
        throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
    }
  }

  private void handleValueDispatching(final ODataRequest request, final ODataResponse response,
      final int lastPathSegmentIndex)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
    final HttpMethod method = request.getMethod();
    final UriResource resource = uriInfo.getUriResourceParts().get(lastPathSegmentIndex - 1);
    if (resource instanceof UriResourceProperty
        || resource instanceof UriResourceFunction
        && ((UriResourceFunction) resource).getType().getKind() == EdmTypeKind.PRIMITIVE) {
      final EdmType type = resource instanceof UriResourceProperty ?
          ((UriResourceProperty) resource).getType() : ((UriResourceFunction) resource).getType();
      final RepresentationType valueRepresentationType =
          type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary) ?
              RepresentationType.BINARY : RepresentationType.VALUE;
      if (method == HttpMethod.GET) {
        final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, valueRepresentationType);

        selectProcessor(PrimitiveValueProcessor.class)
            .readPrimitiveValue(request, response, uriInfo, requestedContentType);
      } else if (method == HttpMethod.PUT && resource instanceof UriResourceProperty) {
        final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
        checkContentTypeSupport(requestFormat, valueRepresentationType);
        final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, valueRepresentationType);
        selectProcessor(PrimitiveValueProcessor.class)
            .updatePrimitive(request, response, uriInfo, requestFormat, responseFormat);
      } else if (method == HttpMethod.DELETE && resource instanceof UriResourceProperty) {
        selectProcessor(PrimitiveValueProcessor.class).deletePrimitive(request, response, uriInfo);
      } else {
        throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
    } else {
      if (method == HttpMethod.GET) {
        final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.MEDIA);
        selectProcessor(MediaEntityProcessor.class)
            .readMediaEntity(request, response, uriInfo, requestedContentType);
      } else if (method == HttpMethod.PUT && resource instanceof UriResourceEntitySet) {
        final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
        final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.ENTITY);
        selectProcessor(MediaEntityProcessor.class)
            .updateMediaEntity(request, response, uriInfo, requestFormat, responseFormat);
      } else if (method == HttpMethod.DELETE && resource instanceof UriResourceEntitySet) {
        selectProcessor(MediaEntityProcessor.class).deleteEntity(request, response, uriInfo);
      } else {
        throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
    }
  }

  private void handleComplexDispatching(final ODataRequest request, final ODataResponse response,
      final boolean isCollection)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
    final HttpMethod method = request.getMethod();
    final RepresentationType complexRepresentationType = isCollection ?
        RepresentationType.COLLECTION_COMPLEX : RepresentationType.COMPLEX;
    if (method == HttpMethod.GET) {
      final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
          request, customContentTypeSupport, complexRepresentationType);
      if (isCollection) {
        selectProcessor(ComplexCollectionProcessor.class)
            .readComplexCollection(request, response, uriInfo, requestedContentType);
      } else {
        selectProcessor(ComplexProcessor.class)
            .readComplex(request, response, uriInfo, requestedContentType);
      }
    } else if (method == HttpMethod.PUT || method == HttpMethod.PATCH) {
      final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
      checkContentTypeSupport(requestFormat, complexRepresentationType);
      final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
          request, customContentTypeSupport, complexRepresentationType);
      if (isCollection) {
        selectProcessor(ComplexCollectionProcessor.class)
            .updateComplexCollection(request, response, uriInfo, requestFormat, responseFormat);
      } else {
        selectProcessor(ComplexProcessor.class)
            .updateComplex(request, response, uriInfo, requestFormat, responseFormat);
      }
    } else if (method == HttpMethod.DELETE) {
      if (isCollection) {
        selectProcessor(ComplexCollectionProcessor.class)
            .deleteComplexCollection(request, response, uriInfo);
      } else {
        selectProcessor(ComplexProcessor.class)
            .deleteComplex(request, response, uriInfo);
      }
    } else {
      throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
          ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
    }
  }

  private void handlePrimitiveDispatching(final ODataRequest request, final ODataResponse response,
      final boolean isCollection)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
    final HttpMethod method = request.getMethod();
    final RepresentationType representationType = isCollection ?
        RepresentationType.COLLECTION_PRIMITIVE : RepresentationType.PRIMITIVE;
    if (method == HttpMethod.GET) {
      final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
          request, customContentTypeSupport, representationType);
      if (isCollection) {
        selectProcessor(PrimitiveCollectionProcessor.class)
            .readPrimitiveCollection(request, response, uriInfo, requestedContentType);
      } else {
        selectProcessor(PrimitiveProcessor.class)
            .readPrimitive(request, response, uriInfo, requestedContentType);
      }
    } else if (method == HttpMethod.PUT || method == HttpMethod.PATCH) {
      final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
      checkContentTypeSupport(requestFormat, representationType);
      final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
          request, customContentTypeSupport, representationType);
      if (isCollection) {
        selectProcessor(PrimitiveCollectionProcessor.class)
            .updatePrimitiveCollection(request, response, uriInfo, requestFormat, responseFormat);
      } else {
        selectProcessor(PrimitiveProcessor.class)
            .updatePrimitive(request, response, uriInfo, requestFormat, responseFormat);
      }
    } else if (method == HttpMethod.DELETE) {
      if (isCollection) {
        selectProcessor(PrimitiveCollectionProcessor.class)
            .deletePrimitiveCollection(request, response, uriInfo);
      } else {
        selectProcessor(PrimitiveProcessor.class)
            .deletePrimitive(request, response, uriInfo);
      }
    } else {
      throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
          ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
    }
  }

  private void handleCountDispatching(final ODataRequest request, final ODataResponse response,
      final int lastPathSegmentIndex)
      throws ODataHandlerException, ODataApplicationException, SerializerException {
    final HttpMethod method = request.getMethod();
    if (method == HttpMethod.GET) {
      final UriResource resource = uriInfo.getUriResourceParts().get(lastPathSegmentIndex - 1);
      if (resource instanceof UriResourceEntitySet
          || resource instanceof UriResourceNavigation
          || resource instanceof UriResourceFunction
          && ((UriResourceFunction) resource).getType().getKind() == EdmTypeKind.ENTITY) {
        selectProcessor(CountEntityCollectionProcessor.class)
            .countEntityCollection(request, response, uriInfo);
      } else if (resource instanceof UriResourcePrimitiveProperty
          || resource instanceof UriResourceFunction
          && ((UriResourceFunction) resource).getType().getKind() == EdmTypeKind.PRIMITIVE) {
        selectProcessor(CountPrimitiveCollectionProcessor.class)
            .countPrimitiveCollection(request, response, uriInfo);
      } else {
        selectProcessor(CountComplexCollectionProcessor.class)
            .countComplexCollection(request, response, uriInfo);
      }
    } else {
      throw new ODataHandlerException("HTTP method " + method + " is not allowed for count.",
          ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
    }
  }

  private void handleEntityDispatching(final ODataRequest request, final ODataResponse response,
      final boolean isCollection, final boolean isMedia)
      throws ODataHandlerException, ContentNegotiatorException, ODataApplicationException,
      SerializerException, DeserializerException {
    final HttpMethod method = request.getMethod();
    if (isCollection) {
      if (method == HttpMethod.GET) {
        final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.COLLECTION_ENTITY);
        selectProcessor(EntityCollectionProcessor.class)
            .readEntityCollection(request, response, uriInfo, requestedContentType);
      } else if (method == HttpMethod.POST) {
        final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
        final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.ENTITY);
        if (isMedia) {
          selectProcessor(MediaEntityProcessor.class)
              .createMediaEntity(request, response, uriInfo, requestFormat, responseFormat);
        } else {
          checkContentTypeSupport(requestFormat, RepresentationType.ENTITY);
          selectProcessor(EntityProcessor.class)
              .createEntity(request, response, uriInfo, requestFormat, responseFormat);
        }
      } else {
        throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
    } else {
      if (method == HttpMethod.GET) {
        final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.ENTITY);
        selectProcessor(EntityProcessor.class)
            .readEntity(request, response, uriInfo, requestedContentType);
      } else if (method == HttpMethod.PUT || method == HttpMethod.PATCH) {
        final ContentType requestFormat = ContentType.parse(request.getHeader(HttpHeader.CONTENT_TYPE));
        checkContentTypeSupport(requestFormat, RepresentationType.ENTITY);
        final ContentType responseFormat = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
            request, customContentTypeSupport, RepresentationType.ENTITY);
        selectProcessor(EntityProcessor.class)
            .updateEntity(request, response, uriInfo, requestFormat, responseFormat);
      } else if (method == HttpMethod.DELETE) {
        selectProcessor(isMedia ? MediaEntityProcessor.class : EntityProcessor.class)
            .deleteEntity(request, response, uriInfo);
      } else {
        throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
            ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
      }
    }
  }

  private void checkMethod(final HttpMethod requestMethod, final HttpMethod allowedMethod)
      throws ODataHandlerException {
    if (requestMethod != allowedMethod) {
      throw new ODataHandlerException("HTTP method " + requestMethod + " is not allowed.",
          ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, requestMethod.toString());
    }
  }

  private void checkContentTypeSupport(final ContentType requestFormat, final RepresentationType representationType)
      throws ODataHandlerException, ContentNegotiatorException {
    ContentNegotiator.checkSupport(requestFormat, customContentTypeSupport, representationType);
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
