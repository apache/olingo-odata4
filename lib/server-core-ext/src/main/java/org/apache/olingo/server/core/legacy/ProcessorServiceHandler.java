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
package org.apache.olingo.server.core.legacy;

import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.ComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ComplexProcessor;
import org.apache.olingo.server.api.processor.CountComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.CountPrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.PrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.processor.PrimitiveValueProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ReferenceProcessor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.core.ODataHandlerException;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.requests.ActionRequest;
import org.apache.olingo.server.core.requests.DataRequest;
import org.apache.olingo.server.core.requests.FunctionRequest;
import org.apache.olingo.server.core.requests.MediaRequest;
import org.apache.olingo.server.core.requests.MetadataRequest;
import org.apache.olingo.server.core.requests.ServiceDocumentRequest;
import org.apache.olingo.server.core.responses.CountResponse;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.EntitySetResponse;
import org.apache.olingo.server.core.responses.ErrorResponse;
import org.apache.olingo.server.core.responses.MetadataResponse;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.PrimitiveValueResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;
import org.apache.olingo.server.core.responses.ServiceDocumentResponse;
import org.apache.olingo.server.core.responses.ServiceResponse;
import org.apache.olingo.server.core.responses.ServiceResponseVisior;
import org.apache.olingo.server.core.responses.StreamResponse;

public class ProcessorServiceHandler implements ServiceHandler {
  private final List<Processor> processors = new LinkedList<Processor>();
  private OData odata;
  private ServiceMetadata serviceMetadata;

  @Override
  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  public void register(Processor processor) {
    this.processors.add(processor);
    processor.init(odata, serviceMetadata);
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

  @Override
  public void readMetadata(MetadataRequest request, MetadataResponse response)
      throws ODataLibraryException, ODataApplicationException {
    selectProcessor(MetadataProcessor.class).readMetadata(request.getODataRequest(),
        response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
  }

  @Override
  public void readServiceDocument(ServiceDocumentRequest request, ServiceDocumentResponse response)
      throws ODataLibraryException, ODataApplicationException {
    selectProcessor(ServiceDocumentProcessor.class).readServiceDocument(request.getODataRequest(),
        response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
  }

  @Override
  public <T extends ServiceResponse> void read(final DataRequest request, final T response)
      throws ODataLibraryException, ODataApplicationException {
    response.accepts(new ServiceResponseVisior() {
      @Override
      public void visit(CountResponse response) throws ODataLibraryException, ODataApplicationException {
        if (request.getUriResourceProperty() != null) {
          EdmProperty edmProperty = request.getUriResourceProperty().getProperty();
          if (edmProperty.isPrimitive()) {
            selectProcessor(CountPrimitiveCollectionProcessor.class).countPrimitiveCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo());
          } else {
            selectProcessor(CountComplexCollectionProcessor.class).countComplexCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo());
          }
        } else {
          selectProcessor(CountEntityCollectionProcessor.class).countEntityCollection(
              request.getODataRequest(), response.getODataResponse(), request.getUriInfo());
        }
      }

      @Override
      public void visit(EntityResponse response) throws ODataLibraryException,
          ODataApplicationException {
        selectProcessor(EntityProcessor.class).readEntity(request.getODataRequest(),
            response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
      }

      @Override
      public void visit(PrimitiveValueResponse response) throws ODataLibraryException,
          ODataApplicationException {
        selectProcessor(PrimitiveValueProcessor.class).readPrimitiveValue(
            request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
            request.getResponseContentType());
      }

      @Override
      public void visit(PropertyResponse response) throws ODataLibraryException,
          ODataApplicationException {
        EdmProperty edmProperty = request.getUriResourceProperty().getProperty();
        if (edmProperty.isPrimitive()) {
          if(edmProperty.isCollection()) {
            selectProcessor(PrimitiveCollectionProcessor.class).readPrimitiveCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());

          } else {
            selectProcessor(PrimitiveProcessor.class).readPrimitive(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());
          }
        } else {
          if(edmProperty.isCollection()) {
            selectProcessor(ComplexCollectionProcessor.class).readComplexCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());

          } else {
            selectProcessor(ComplexProcessor.class).readComplex(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());

          }
        }
      }

      @Override
      public void visit(StreamResponse response) throws ODataLibraryException,
          ODataApplicationException {
        response.writeServerError(true);
      }

      @Override
      public void visit(EntitySetResponse response) throws ODataLibraryException,
          ODataApplicationException {
        selectProcessor(EntityCollectionProcessor.class).readEntityCollection(request.getODataRequest(),
            response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
      }
    });
  }

  @Override
  public void createEntity(DataRequest request, Entity entity, EntityResponse response)
      throws ODataLibraryException, ODataApplicationException {
    if (request.getEntitySet().getEntityType().hasStream()) {
      selectProcessor(MediaEntityProcessor.class).createMediaEntity(
          request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
          request.getRequestContentType(),request.getResponseContentType());
    } else {
      selectProcessor(EntityProcessor.class).createEntity(request.getODataRequest(),
          response.getODataResponse(), request.getUriInfo(), request.getRequestContentType(),
          request.getResponseContentType());
    }
  }

  @Override
  public void updateEntity(DataRequest request, Entity entity, boolean merge, String entityETag,
      EntityResponse response) throws ODataLibraryException, ODataApplicationException {
    if (request.getEntitySet().getEntityType().hasStream()) {
      selectProcessor(MediaEntityProcessor.class).updateMediaEntity(
          request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
          request.getRequestContentType(),request.getResponseContentType());
    } else {
    selectProcessor(EntityProcessor.class).updateEntity(request.getODataRequest(),
        response.getODataResponse(), request.getUriInfo(), request.getRequestContentType(),
        request.getResponseContentType());
    }
  }

  @Override
  public void deleteEntity(DataRequest request, String entityETag, EntityResponse response)
      throws ODataLibraryException, ODataApplicationException {
    selectProcessor(EntityProcessor.class).deleteEntity(request.getODataRequest(),
        response.getODataResponse(), request.getUriInfo());
  }

  @Override
  public void updateProperty(DataRequest request, Property property, boolean rawValue, boolean merge,
      String entityETag, PropertyResponse response) throws ODataLibraryException,
      ODataApplicationException {
    if (property.isPrimitive()) {
      if (property.isCollection()) {
        selectProcessor(PrimitiveCollectionProcessor.class).updatePrimitiveCollection(
            request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
            request.getRequestContentType(), request.getResponseContentType());
      } else {
        selectProcessor(PrimitiveProcessor.class).updatePrimitive(
            request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
            request.getRequestContentType(), request.getResponseContentType());
      }
    } else {
      if (property.isCollection()) {
        selectProcessor(ComplexCollectionProcessor.class).updateComplexCollection(
            request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
            request.getRequestContentType(), request.getResponseContentType());
      } else {
        selectProcessor(ComplexProcessor.class).updateComplex(
            request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
            request.getRequestContentType(), request.getResponseContentType());
      }
    }
  }

  @Override
  public void upsertStreamProperty(DataRequest request, String entityETag,
      InputStream streamContent, NoContentResponse response) throws ODataLibraryException,
      ODataApplicationException {
    throw new ODataHandlerException("not implemented",
        ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
  }

  @Override
  public <T extends ServiceResponse> void invoke(final FunctionRequest request, HttpMethod method,
      final T response) throws ODataLibraryException, ODataApplicationException {
    if (method != HttpMethod.GET) {
      throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
          ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
    }

    response.accepts(new ServiceResponseVisior() {
      @Override
      public void visit(EntityResponse response) throws ODataLibraryException,
          ODataApplicationException {
        selectProcessor(EntityProcessor.class).readEntity(request.getODataRequest(),
            response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
      }

      @Override
      public void visit(PropertyResponse response) throws ODataLibraryException,
          ODataApplicationException {
        if (request.isReturnTypePrimitive()) {
          if(request.isCollection()) {
            selectProcessor(PrimitiveCollectionProcessor.class).readPrimitiveCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());

          } else {
            selectProcessor(PrimitiveProcessor.class).readPrimitive(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());
          }
        } else {
          if(request.isCollection()) {
            selectProcessor(ComplexCollectionProcessor.class).readComplexCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());

          } else {
            selectProcessor(ComplexProcessor.class).readComplex(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());
          }
        }
      }
      @Override
      public void visit(EntitySetResponse response) throws ODataLibraryException,
          ODataApplicationException {
        selectProcessor(EntityCollectionProcessor.class).readEntityCollection(request.getODataRequest(),
            response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
      }
    });
  }

  @Override
  public <T extends ServiceResponse> void invoke(final ActionRequest request, String eTag, final T response)
      throws ODataLibraryException, ODataApplicationException {
    final HttpMethod method = request.getODataRequest().getMethod();
    if (method != HttpMethod.POST) {
      throw new ODataHandlerException("HTTP method " + method + " is not allowed.",
          ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, method.toString());
    }
    response.accepts(new ServiceResponseVisior() {
      @Override
      public void visit(EntityResponse response) throws ODataLibraryException,
          ODataApplicationException {
        selectProcessor(EntityProcessor.class).readEntity(request.getODataRequest(),
            response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
      }

      @Override
      public void visit(PropertyResponse response) throws ODataLibraryException,
          ODataApplicationException {
        if (request.isReturnTypePrimitive()) {
          if(request.isCollection()) {
            selectProcessor(PrimitiveCollectionProcessor.class).readPrimitiveCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());

          } else {
            selectProcessor(PrimitiveProcessor.class).readPrimitive(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());
          }
        } else {
          if(request.isCollection()) {
            selectProcessor(ComplexCollectionProcessor.class).readComplexCollection(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());

          } else {
            selectProcessor(ComplexProcessor.class).readComplex(
                request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
                request.getResponseContentType());
          }
        }
      }
      @Override
      public void visit(EntitySetResponse response) throws ODataLibraryException,
          ODataApplicationException {
        selectProcessor(EntityCollectionProcessor.class).readEntityCollection(request.getODataRequest(),
            response.getODataResponse(), request.getUriInfo(), request.getResponseContentType());
      }
    });
  }


  @Override
  public void readMediaStream(MediaRequest request, StreamResponse response)
      throws ODataLibraryException, ODataApplicationException {
    selectProcessor(MediaEntityProcessor.class).readMediaEntity(
        request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
        request.getResponseContentType());
  }

  @Override
  public void upsertMediaStream(MediaRequest request, String entityETag, InputStream mediaContent,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
    selectProcessor(MediaEntityProcessor.class).updateMediaEntity(
        request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
        request.getRequestContentType(), request.getResponseContentType());
  }

  @Override
  public void anyUnsupported(ODataRequest request, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException {
    throw new ODataHandlerException("not implemented",
        ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
  }

  @Override
  public void addReference(DataRequest request, String entityETag, URI referenceId,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
      selectProcessor(ReferenceProcessor.class).createReference(
          request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
          request.getResponseContentType());
  }

  @Override
  public void updateReference(DataRequest request, String entityETag, URI referenceId,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
    selectProcessor(ReferenceProcessor.class).updateReference(
        request.getODataRequest(), response.getODataResponse(), request.getUriInfo(),
        request.getResponseContentType());
  }

  @Override
  public void deleteReference(DataRequest request, URI deleteId, String entityETag,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
    selectProcessor(ReferenceProcessor.class).deleteReference(
        request.getODataRequest(), response.getODataResponse(), request.getUriInfo());
  }

  @Override
  public String startTransaction() {
    return null;
  }

  @Override
  public void commit(String txnId) {
  }

  @Override
  public void rollback(String txnId) {
  }

  @Override
  public void crossJoin(DataRequest dataRequest, List<String> entitySetNames, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException {
    throw new ODataHandlerException("not implemented",
        ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
  }

  @Override
  public void upsertEntity(DataRequest request, Entity entity, boolean merge, String entityETag, 
      EntityResponse response) throws ODataLibraryException, ODataApplicationException {
    throw new ODataHandlerException("not implemented",
        ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
  }

  @Override
  public boolean supportsDataIsolation() {
    return false;
  }

  @Override
  public void processError(ODataServerError error, ErrorResponse response) {
    response.writeError(error);
  }
}
