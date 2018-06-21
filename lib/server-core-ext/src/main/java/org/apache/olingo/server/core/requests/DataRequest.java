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
package org.apache.olingo.server.core.requests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoCrossjoin;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.UriResourceSingleton;
import org.apache.olingo.server.core.ContentNegotiator;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ODataHandlerException;
import org.apache.olingo.server.core.ReturnRepresentation;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.ServiceRequest;
import org.apache.olingo.server.core.responses.CountResponse;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.EntitySetResponse;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.PrimitiveValueResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;
import org.apache.olingo.server.core.responses.StreamResponse;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;

public class DataRequest extends ServiceRequest {
  public static final int DEFAULT_BUFFER_SIZE = 8192;
  protected UriResourceEntitySet uriResourceEntitySet;
  private boolean countRequest;
  private UriResourceProperty uriResourceProperty;
  private boolean valueRequest;
  private final LinkedList<UriResourceNavigation> uriNavigations = new LinkedList<UriResourceNavigation>();
  private boolean references;

  private RequestType type;
  private UriResourceSingleton uriResourceSingleton;

  /**
   * This sub-categorizes the request so that code can be simplified
   */
  interface RequestType {
    public HttpMethod[] allowedMethods();
    
    public boolean assertHttpMethod(ODataResponse response) throws ODataHandlerException;
    
    public ContentType getResponseContentType() throws ContentNegotiatorException;

    public ContextURL getContextURL(OData odata) throws SerializerException;

    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException;
  }

  public DataRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
  }

  public UriResourceEntitySet getUriResourceEntitySet() {
    return uriResourceEntitySet;
  }

  public void setUriResourceEntitySet(UriResourceEntitySet uriResourceEntitySet) {
    this.uriResourceEntitySet = uriResourceEntitySet;
    this.type = new EntityRequest();
  }

  public void setCrossJoin(UriInfoCrossjoin info) {
    this.type = new CrossJoinRequest(info.getEntitySetNames());
  }

  public boolean isSingleton() {
    return this.uriResourceSingleton != null;
  }

  public boolean isCollection() {
    if (!this.uriNavigations.isEmpty()) {
      return this.uriNavigations.getLast().isCollection();
    }
    return this.uriResourceEntitySet != null && this.uriResourceEntitySet.isCollection();
  }

  public EdmEntitySet getEntitySet() {
    return this.uriResourceEntitySet.getEntitySet();
  }

  public boolean isCountRequest() {
    return countRequest;
  }

  public void setCountRequest(boolean countRequest) {
    this.countRequest = countRequest;
    this.type = new CountRequest();
  }

  public boolean isPropertyRequest() {
    return this.uriResourceProperty != null;
  }

  public boolean isPropertyComplex() {
    return (this.uriResourceProperty instanceof UriResourceComplexProperty);
  }

  public boolean isPropertyStream() {
    if (isPropertyComplex()) {
      return false;
    }
    EdmProperty property = ((UriResourcePrimitiveProperty)this.uriResourceProperty).getProperty();
    return property.getType() == odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Stream);
  }

  public UriResourceProperty getUriResourceProperty() {
    return uriResourceProperty;
  }

  public void setUriResourceProperty(UriResourceProperty uriResourceProperty) {
    this.uriResourceProperty = uriResourceProperty;
    this.type = new PropertyRequest();
  }

  public LinkedList<UriResourceNavigation> getNavigations() {
    return this.uriNavigations;
  }

  public void addUriResourceNavigation(UriResourceNavigation uriResourceNavigation) {
    this.uriNavigations.add(uriResourceNavigation);
  }

  public UriResourceSingleton getUriResourceSingleton() {
    return this.uriResourceSingleton;
  }

  public void setUriResourceSingleton(UriResourceSingleton info) {
    this.uriResourceSingleton = info;
    this.type = new SingletonRequest();
  }

  public List<UriParameter> getKeyPredicates() {
    if (this.uriResourceEntitySet != null) {
      return this.uriResourceEntitySet.getKeyPredicates();
    }
    return null;
  }

  public boolean isReferenceRequest() {
    return this.references;
  }

  public void setReferenceRequest(boolean ref) {
    this.references = ref;
    this.type = new ReferenceRequest();
  }

  public boolean isValueRequest() {
    return valueRequest;
  }

  public void setValueRequest(boolean valueRequest) {
    this.valueRequest = valueRequest;
    this.type = new ValueRequest();
  }

  public boolean assertHttpMethod(ODataResponse response)
      throws ODataHandlerException {
    return this.type.assertHttpMethod(response);
  }
  
  @Override
  public HttpMethod[] allowedMethods() {
    return this.type.allowedMethods();
  }

  public ContextURL getContextURL(OData odata) throws SerializerException {
    return type.getContextURL(odata);
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException {

    // check for valid HTTP Verb
    assertHttpMethod(response);

    this.type.execute(handler, response);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getSerializerOptions(Class<T> serilizerOptions, ContextURL contextUrl, boolean references)
      throws ContentNegotiatorException {   
    if (serilizerOptions.isAssignableFrom(PrimitiveSerializerOptions.class)) {
      
      String xmlReplacement = null;
      if (getResponseContentType().isCompatible(ContentType.APPLICATION_XML)
          || getResponseContentType().isCompatible(ContentType.APPLICATION_ATOM_XML)) {
        xmlReplacement = xml10IncompatibleCharReplacement();
      }      
      
      return (T) PrimitiveSerializerOptions.with().contextURL(contextUrl)
          .facetsFrom(getUriResourceProperty().getProperty())
          .xml10InvalidCharReplacement(xmlReplacement)
          .build();
    }
    return super.getSerializerOptions(serilizerOptions, contextUrl, references);
  }

  @Override
  public ContentType getResponseContentType() throws ContentNegotiatorException {
    return type.getResponseContentType();
  }

  class EntityRequest implements RequestType {

    @Override
    public boolean assertHttpMethod(ODataResponse response) throws ODataHandlerException {
      // the create/update/delete to navigation property is done through references
      // see # 11.4.6
      if (!getNavigations().isEmpty() && !isGET()) {
        return methodNotAllowed(response, httpMethod(), 
            "create/update/delete to navigation property is done through references", 
            allowedMethods());
      }
      
      if ((isGET() || isDELETE()) && getReturnRepresentation() != ReturnRepresentation.NONE) {
        return methodNotAllowed(response, httpMethod(), 
            "Invalid prefer header used", 
            allowedMethods());
      }
      
      // in update, delete entity cases, predicate must be there
      if ((isPATCH() || isPUT() || isDELETE()) 
          && (getKeyPredicates() == null || getKeyPredicates().isEmpty())) {
        return methodNotAllowed(response, httpMethod(),
            "No key predicates provided",allowedMethods());
      }
      return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
    }

    @Override
    public HttpMethod[] allowedMethods() {
      return new HttpMethod[] { HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
          HttpMethod.PATCH, HttpMethod.DELETE };
    }     
    
    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), getODataRequest(),
          getCustomContentTypeSupport(), isCollection() ? RepresentationType.COLLECTION_ENTITY
              : RepresentationType.ENTITY);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException {

      EntityResponse entityResponse = EntityResponse.getInstance(DataRequest.this,
          getContextURL(odata), false, response);

      if (isGET()) {
        if (isCollection()) {
          handler.read(DataRequest.this,
              EntitySetResponse.getInstance(DataRequest.this, getContextURL(odata), false, response));
        } else {
          handler.read(DataRequest.this,entityResponse);
        }
      } else if (isPUT() || isPATCH()) {
        // RFC 2616: he result of a request having both an If-Match header field and either
        // an If-None-Match or an If-Modified-Since header fields is undefined
        // by this specification.
        boolean ifMatch = getHeader(HttpHeader.IF_MATCH) != null;
        boolean ifNoneMatch = (getHeader(HttpHeader.IF_NONE_MATCH)!= null 
            && "*".equals(getHeader(HttpHeader.IF_NONE_MATCH)));
        if(ifMatch) {
          handler.updateEntity(DataRequest.this, getEntityFromClient(), isPATCH(), getETag(),
              entityResponse);
        } else if (ifNoneMatch) {
          // 11.4.4
          entityResponse = EntityResponse.getInstance(DataRequest.this,
              getContextURL(odata), false, response, getReturnRepresentation());
          handler.createEntity(DataRequest.this, getEntityFromClient(), entityResponse);
        } else {
          handler.upsertEntity(DataRequest.this, getEntityFromClient(), isPATCH(), getETag(),
              entityResponse);
        }
      } else if (isPOST()) {
        entityResponse = EntityResponse.getInstance(DataRequest.this,
            getContextURL(odata), false, response, getReturnRepresentation());
        handler.createEntity(DataRequest.this, getEntityFromClient(),entityResponse);
      } else if (isDELETE()) {
        handler.deleteEntity(DataRequest.this, getETag(), entityResponse);
      }
    }

    private Entity getEntityFromClient() throws DeserializerException {
      ODataDeserializer deserializer = odata.createDeserializer(getRequestContentType(), getServiceMetaData());
      return deserializer.entity(getODataRequest().getBody(), getEntitySet().getEntityType()).getEntity();
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      // EntitySet based return
      final UriHelper helper = odata.createUriHelper();
      ContextURL.Builder builder = buildEntitySetContextURL(helper, getEntitySet(),
          getKeyPredicates(), getUriInfo(), getNavigations(), isCollection(), false, getODataRequest());
      return builder.build();
    }
  }

  class CountRequest implements RequestType {

    @Override
    public boolean assertHttpMethod(ODataResponse response) throws ODataHandlerException {
      if (getReturnRepresentation() != ReturnRepresentation.NONE) {
        return methodNotAllowed(response, httpMethod(), "Invalid Prefer header used", 
            allowedMethods());
      }
      return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
    }

    @Override
    public HttpMethod[] allowedMethods() {
      return new HttpMethod[] { HttpMethod.GET};
    }     
    
    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentType.TEXT_PLAIN;
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException {
      handler.read(DataRequest.this, CountResponse.getInstance(DataRequest.this, response));
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      return null;
    }
  }

  /**
   * Is NavigationProperty Reference.
   */
  class ReferenceRequest implements RequestType {

    @Override
    public boolean assertHttpMethod(ODataResponse response) throws ODataHandlerException {
      if ((isGET() || isDELETE()) && getReturnRepresentation() != ReturnRepresentation.NONE) {
        return methodNotAllowed(response, httpMethod(), "Invalid Prefer header used", 
            allowedMethods());
      }
      
      // references are only allowed on the navigation properties
      if (getNavigations().isEmpty()) {
        return methodNotAllowed(response, httpMethod(),
            "References can be only used on navigation properties",
            allowedMethods());
      }

      // 11.4.6.1 - post allowed on only collection valued navigation
      if (isPOST() && !getNavigations().getLast().isCollection()) {
        return methodNotAllowed(response, httpMethod(),
            "POST only allowed on collection valued navigation",
            allowedMethods());
      }

      // 11.4.6.3 - PUT allowed on single valued navigation
      if (isPUT() && getNavigations().getLast().isCollection()) {
        return methodNotAllowed(response, httpMethod(),
            "PUT only allowed on single valued navigation", allowedMethods());
      }
      //PATCH is not defined in spec
      return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
    }

    @Override
    public HttpMethod[] allowedMethods() {
      return new HttpMethod[] { HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
          HttpMethod.DELETE };
    }
    
    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), getODataRequest(),
          getCustomContentTypeSupport(), isCollection() ? RepresentationType.COLLECTION_REFERENCE
              : RepresentationType.REFERENCE);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException {
      if (isGET()) {
        if (isCollection()) {
          handler.read(DataRequest.this,
              EntitySetResponse.getInstance(DataRequest.this, getContextURL(odata), true, response));
        } else {
          handler.read(DataRequest.this,
              EntityResponse.getInstance(DataRequest.this, getContextURL(odata), true, response));
        }
      } else if (isDELETE()) {
        // if this against the collection, user need to look at $id param for entity ref #11.4.6.2
        String id = getQueryParameter("$id");
        if (id == null && isCollection()) {
          throw new UriParserSemanticException("$id not specified for delete of reference", 
              UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);
        }
        if (id == null) {
          handler.deleteReference(DataRequest.this, null, getETag(), new NoContentResponse(
              getServiceMetaData(), response));
        } else {
          try {
            handler.deleteReference(DataRequest.this, new URI(id), getETag(), new NoContentResponse(
                getServiceMetaData(), response));
          } catch (URISyntaxException e) {
            throw new DeserializerException("failed to read $id", e, MessageKeys.UNKNOWN_CONTENT);
          }
        }
      } else if (isPUT()) {
        // note this is always against single reference
        handler.updateReference(DataRequest.this, getETag(), getPayload().get(0), new NoContentResponse(
            getServiceMetaData(), response));
      } else if (isPOST()) {
        // this needs to be against collection of references
        handler.addReference(DataRequest.this, getETag(), getPayload().get(0), new NoContentResponse(
            getServiceMetaData(), response));
      }
    }

    // http://docs.oasis-open.org/odata/odata-json-format/v4.0/errata02/os
    // /odata-json-format-v4.0-errata02-os-complete.html#_Toc403940643
    // The below code reads as property and converts to an URI
    private List<URI> getPayload() throws DeserializerException {
      ODataDeserializer deserializer = odata.createDeserializer(getRequestContentType(), getServiceMetaData());
      return deserializer.entityReferences(getODataRequest().getBody()).getEntityReferences();
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      ContextURL.Builder builder = ContextURL.with().suffix(Suffix.REFERENCE);
      if (isCollection()) {
        builder.asCollection();
      }
      setServiceRoot(builder, getODataRequest());
      return builder.build();
    }
  }

  class PropertyRequest implements RequestType {

    @Override
    public boolean assertHttpMethod(ODataResponse response) throws ODataHandlerException {
      if ((isGET() || isDELETE() || isPropertyStream())
          && getReturnRepresentation() != ReturnRepresentation.NONE) {
        return methodNotAllowed(response, httpMethod(), "Invalid Prefer header used", 
            allowedMethods());
      }
      
      // create of properties is not allowed,
      // only read, update, delete. Note that delete is
      // same as update with null

      // 11.4.9.4, collection properties are not supported with merge
      if (isPATCH() && (isCollection() || isPropertyStream())) {
        return methodNotAllowed(response, httpMethod(),
            "collection properties are not supported for merge",
            allowedMethods());
      }
      
      return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
    }

    @Override
    public HttpMethod[] allowedMethods() {
      return new HttpMethod[] { HttpMethod.GET, HttpMethod.PUT,
          HttpMethod.PATCH, HttpMethod.DELETE };
    }
    
    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      if (isPropertyComplex()) {
        return ContentNegotiator.doContentNegotiation(getUriInfo().getFormatOption(),
            getODataRequest(), getCustomContentTypeSupport(),
            isCollection() ? RepresentationType.COLLECTION_COMPLEX : RepresentationType.COMPLEX);
      } else if (isPropertyStream()) {
        return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request,
            getCustomContentTypeSupport(), RepresentationType.BINARY);
      }
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), getODataRequest(),
          getCustomContentTypeSupport(), isCollection() ? RepresentationType.COLLECTION_PRIMITIVE
              : RepresentationType.PRIMITIVE);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException {

      EdmProperty edmProperty = getUriResourceProperty().getProperty();

      if (isGET()) {
        if (isPropertyStream()) {
          handler.read(DataRequest.this, new StreamResponse(getServiceMetaData(), response));
        } else {
          handler.read(DataRequest.this, buildResponse(response, edmProperty));
        }
      } else if (isPATCH()) {
        handler.updateProperty(DataRequest.this, getPropertyValueFromClient(edmProperty), false, true,
            getETag(), buildResponse(response, edmProperty));
      } else if (isPUT()) {
        if (isPropertyStream()) {
          handler.upsertStreamProperty(DataRequest.this, getETag(), request.getBody(),
              new NoContentResponse(getServiceMetaData(), response));
        } else {
          handler.updateProperty(DataRequest.this, getPropertyValueFromClient(edmProperty), false, false,
              getETag(), buildResponse(response, edmProperty));
        }
      } else if (isDELETE()) {
        if (isPropertyStream()) {
          handler.upsertStreamProperty(DataRequest.this, getETag(), null,
              new NoContentResponse(getServiceMetaData(), response));
        } else {
          Property property = new Property(edmProperty.getType().getFullQualifiedName()
              .getFullQualifiedNameAsString(), edmProperty.getName());
          handler.updateProperty(DataRequest.this, property, false, false, getETag(),
              buildResponse(response, edmProperty));
        }
      }
    }

    private PropertyResponse buildResponse(ODataResponse response, EdmProperty edmProperty)
        throws ContentNegotiatorException, SerializerException {
      PropertyResponse propertyResponse = PropertyResponse.getInstance(DataRequest.this, response,
          edmProperty.getType(), getContextURL(odata), edmProperty.isCollection());
      return propertyResponse;
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      final UriHelper helper = odata.createUriHelper();
      EdmProperty edmProperty = getUriResourceProperty().getProperty();

      ContextURL.Builder builder =
          ContextURL.with().entitySetOrSingletonOrType(getTargetEntitySet(getEntitySet(), getNavigations()));
      builder.keyPath(helper.buildContextURLKeyPredicate(getUriResourceEntitySet()
          .getKeyPredicates()));
      String navPath = buildNavPath(helper, getEntitySet().getEntityType(), getNavigations(), true);
      if (navPath != null && !navPath.isEmpty()) {
        builder.navOrPropertyPath(navPath+"/"+edmProperty.getName());
      } else {
        builder.navOrPropertyPath(edmProperty.getName());
      }
      setServiceRoot(builder, getODataRequest());      
      if (isPropertyComplex()) {
        EdmComplexType complexType = ((UriResourceComplexProperty) uriResourceProperty).getComplexType();
        String select = helper.buildContextURLSelectList(complexType, getUriInfo().getExpandOption(),
            getUriInfo().getSelectOption());
        builder.selectList(select);
      }
      return builder.build();
    }
  }

  class ValueRequest extends PropertyRequest {

    @Override
    public boolean assertHttpMethod(ODataResponse response)
        throws ODataHandlerException {
      //part2-url-conventions # 4.7
      // Properties of type Edm.Stream already return the raw value of 
      // the media stream and do not support appending the $value segment.
      if (isPropertyStream() && isGET()) {
        return methodNotAllowed(response, httpMethod(), "Edm.Stream properties do not support $value", 
            allowedMethods());
      }

      if ((isGET() || isDELETE() || isPropertyStream())
          && getReturnRepresentation() != ReturnRepresentation.NONE) {
        return methodNotAllowed(response, httpMethod(), "Invalid Prefer header used", 
            allowedMethods());
      }

      return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
    }

    @Override
    public HttpMethod[] allowedMethods() {
      return new HttpMethod[] { HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE };
    }
    
    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      RepresentationType valueRepresentationType =
          uriResourceProperty.getType() == odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Binary) ?
              RepresentationType.BINARY :
              RepresentationType.VALUE;
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request,
          getCustomContentTypeSupport(), valueRepresentationType);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException {
      EdmProperty edmProperty = getUriResourceProperty().getProperty();
      if (isGET()) {
        handler.read(DataRequest.this, PrimitiveValueResponse.getInstance(DataRequest.this,
            response, isCollection(), getUriResourceProperty().getProperty()));
      } else if (isDELETE()) {
        Property property = new Property(
            edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString(),
            edmProperty.getName());
        PropertyResponse propertyResponse = PropertyResponse.getInstance(DataRequest.this, response,
            edmProperty.getType(), getContextURL(odata), edmProperty.isCollection());
        handler.updateProperty(DataRequest.this, property, true, false, getETag(), propertyResponse);
      } else if (isPUT()) {
        PropertyResponse propertyResponse = PropertyResponse.getInstance(DataRequest.this, response,
            edmProperty.getType(), getContextURL(odata), edmProperty.isCollection());
        Property property = new Property(
            edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString(),
            edmProperty.getName());
        property.setValue(ValueType.PRIMITIVE, getRawValueFromClient());
        handler.updateProperty(DataRequest.this, property, true, false,
            getETag(), propertyResponse);
      }
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      return null;
    }
  }

  class SingletonRequest implements RequestType {

    @Override
    public boolean assertHttpMethod(ODataResponse response)
        throws ODataHandlerException {
      return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
    }
    
    @Override
    public HttpMethod[] allowedMethods() {
      return new HttpMethod[] {HttpMethod.GET};
    }
    
    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), getODataRequest(),
          getCustomContentTypeSupport(), RepresentationType.ENTITY);
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      final UriHelper helper = odata.createUriHelper();
      ContextURL.Builder builder = buildEntitySetContextURL(helper,
          uriResourceSingleton.getSingleton(), null, getUriInfo(), getNavigations(), isCollection(), true, 
          getODataRequest());
      return builder.build();
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException {
      handler.read(DataRequest.this,
          EntityResponse.getInstance(DataRequest.this, getContextURL(odata), false, response));
    }
  }

  class CrossJoinRequest implements RequestType {
    private final List<String> entitySetNames;

    public CrossJoinRequest(List<String> entitySetNames) {
      this.entitySetNames = entitySetNames;
    }

    @Override
    public boolean assertHttpMethod(ODataResponse response)
        throws ODataHandlerException {
      return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
    }
    
    @Override
    public HttpMethod[] allowedMethods() {
      return new HttpMethod[] {HttpMethod.GET};
    }

    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentNegotiator.doContentNegotiation(getUriInfo().getFormatOption(),
          getODataRequest(), getCustomContentTypeSupport(), RepresentationType.COLLECTION_COMPLEX);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataLibraryException, ODataApplicationException {
      handler.crossJoin(DataRequest.this, this.entitySetNames, response);
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      ContextURL.Builder builder = ContextURL.with().asCollection();
      return builder.build();
    }
  }

  private org.apache.olingo.commons.api.data.Property getPropertyValueFromClient(
      EdmProperty edmProperty) throws DeserializerException {
    ODataDeserializer deserializer = odata.createDeserializer(getRequestContentType(), getServiceMetaData());
    return deserializer.property(getODataRequest().getBody(), edmProperty).getProperty();
  }
  
  private Object getRawValueFromClient() throws DeserializerException {
    InputStream input = getODataRequest().getBody();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    if (input != null) {
      try {
        ByteBuffer inBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        ReadableByteChannel ic = Channels.newChannel(input);
        WritableByteChannel oc = Channels.newChannel(buffer);
        while (ic.read(inBuffer) > 0) {
          inBuffer.flip();
          oc.write(inBuffer);
          inBuffer.rewind();
        }
        return buffer.toByteArray();
      } catch (IOException e) {
        throw new ODataRuntimeException("Error on reading content");
      }
    }
    return null;
  }

  static ContextURL.Builder buildEntitySetContextURL(UriHelper helper,
      EdmBindingTarget edmEntitySet, List<UriParameter> keyPredicates, UriInfo uriInfo,
      LinkedList<UriResourceNavigation> navigations, boolean collectionReturn, boolean singleton, ODataRequest request)
      throws SerializerException {

    ContextURL.Builder builder =
        ContextURL.with().entitySetOrSingletonOrType(getTargetEntitySet(edmEntitySet, navigations));
    String select = helper.buildContextURLSelectList(edmEntitySet.getEntityType(),
        uriInfo.getExpandOption(), uriInfo.getSelectOption());
    if (!singleton) {
      builder.suffix(collectionReturn ? null : Suffix.ENTITY);
    }

    setServiceRoot(builder, request);    
    builder.selectList(select);

    final UriInfoResource resource = uriInfo.asUriInfoResource();
    final List<UriResource> resourceParts = resource.getUriResourceParts();
    final List<String> path = getPropertyPath(resourceParts);
    String propertyPath = buildPropertyPath(path);
    final String navPath = buildNavPath(helper, edmEntitySet.getEntityType(), navigations, collectionReturn);
    if (navPath != null && !navPath.isEmpty()) {
      if (keyPredicates != null) {
        builder.keyPath(helper.buildContextURLKeyPredicate(keyPredicates));
      }
      if (propertyPath != null) {
        propertyPath = navPath+"/"+propertyPath;
      } else {
        propertyPath = navPath;
      }
    }
    builder.navOrPropertyPath(propertyPath);
    return builder;
  }

  private static void setServiceRoot(ContextURL.Builder builder, ODataRequest request) {
    String serviceRoot = request.getRawBaseUri();
    if (serviceRoot != null) {
      try {
        if (!serviceRoot.endsWith("/")) {
          serviceRoot = serviceRoot + "/";
        }
        builder.serviceRoot(URI.create(serviceRoot));
      } catch (IllegalArgumentException e) {
        // ignore
      }
    }
  }

  private static List<String> getPropertyPath(final List<UriResource> path) {
    List<String> result = new LinkedList<String>();
    int index = 1;
    while (index < path.size() && path.get(index) instanceof UriResourceProperty) {
      result.add(((UriResourceProperty) path.get(index)).getProperty().getName());
      index++;
    }
    return result;
  }

  private static String buildPropertyPath(final List<String> path) {
    StringBuilder result = new StringBuilder();
    for (final String segment : path) {
      result.append(result.length() == 0 ? "" : '/').append(segment); //$NON-NLS-1$
    }
    return result.length() == 0?null:result.toString();
  }

  static String getTargetEntitySet(EdmBindingTarget root, LinkedList<UriResourceNavigation> navigations) {
    EdmEntityType type = root.getEntityType();
    EdmBindingTarget targetEntitySet = root;
    String targetEntitySetName = root.getName();
    String name = null;
    for (UriResourceNavigation nav:navigations) {
      name = nav.getProperty().getName();
      EdmNavigationProperty property = type.getNavigationProperty(name);
      if (property.containsTarget()) {
        return root.getName();
      }
      type = nav.getProperty().getType();
      
      for(EdmNavigationPropertyBinding enb:targetEntitySet.getNavigationPropertyBindings()) {
        if (enb.getPath().equals(name)) {
          targetEntitySetName = enb.getTarget();
        } else if (enb.getPath().endsWith("/"+name)) {
          targetEntitySetName = enb.getTarget();
        }
      }
    }
    return targetEntitySetName;
  }
  
  static String buildNavPath(UriHelper helper, EdmEntityType rootType,
      LinkedList<UriResourceNavigation> navigations, boolean includeLastPredicates)
      throws SerializerException {
    if (navigations.isEmpty()) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    boolean containsTarget = false;
    EdmEntityType type = rootType;
    for (UriResourceNavigation nav:navigations) {
      String name = nav.getProperty().getName();
      EdmNavigationProperty property = type.getNavigationProperty(name);
      if (property.containsTarget()) {
        containsTarget = true;
      }
      type = nav.getProperty().getType();
    }

    if (containsTarget) {
      for (int i = 0; i < navigations.size(); i++) {
        UriResourceNavigation nav = navigations.get(i);
        if (i > 0) {
          sb.append("/");
        }
        sb.append(nav.getProperty().getName());

        boolean skipKeys = false;
        if (navigations.size() == i+1 && !includeLastPredicates ) {
          skipKeys = true;
        }

        if (!skipKeys && !nav.getKeyPredicates().isEmpty()) {
          sb.append("(");
          sb.append(helper.buildContextURLKeyPredicate(nav.getKeyPredicates()));
          sb.append(")");
        }

        if (nav.getTypeFilterOnCollection() != null) {
          sb.append("/")
            .append(nav.getTypeFilterOnCollection().getFullQualifiedName().getFullQualifiedNameAsString());
        } else if (nav.getTypeFilterOnEntry() != null) {
          sb.append("/")
            .append(nav.getTypeFilterOnEntry().getFullQualifiedName().getFullQualifiedNameAsString());
        }
      }
    }
    return sb.toString();
  }
}
