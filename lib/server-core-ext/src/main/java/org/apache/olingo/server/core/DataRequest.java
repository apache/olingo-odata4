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

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.edm.primitivetype.EdmStream;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.UriResourceSingleton;

public class DataRequest extends ServiceRequest {
  protected UriResourceEntitySet uriResourceEntitySet;
  private boolean countRequest;
  private UriResourceProperty uriResourceProperty;
  private boolean valueRequest;
  private UriResourceNavigation uriResourceNavigation;
  private boolean references;

  private RequestType type;
  private UriResourceSingleton uriResourceSingleton;

  /**
   * This sub-categorizes the request so that code can be simplified
   */
  interface RequestType {
    public boolean allowedMethod();

    public ContentType getResponseContentType() throws ContentNegotiatorException;

    public ContextURL getContextURL(OData odata) throws SerializerException;

    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataTranslatedException, ODataApplicationException;
  }

  public DataRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
  }

  public UriResourceEntitySet getUriResourceEntitySet() {
    return uriResourceEntitySet;
  }

  protected void setUriResourceEntitySet(UriResourceEntitySet uriResourceEntitySet) {
    this.uriResourceEntitySet = uriResourceEntitySet;
    this.type = new EntityRequest();
  }

  public boolean isSingleton() {
    return this.uriResourceSingleton != null;
  }

  public boolean isCollection() {
    return this.uriResourceEntitySet != null && this.uriResourceEntitySet.isCollection();
  }

  public EdmEntitySet getEntitySet() {
    return this.uriResourceEntitySet.getEntitySet();
  }

  public boolean isCountRequest() {
    return countRequest;
  }

  protected void setCountRequest(boolean countRequest) {
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
    return (property.getType() instanceof EdmStream);
  }

  public UriResourceProperty getUriResourceProperty() {
    return uriResourceProperty;
  }

  protected void setUriResourceProperty(UriResourceProperty uriResourceProperty) {
    this.uriResourceProperty = uriResourceProperty;
    this.type = new PropertyRequest();
  }

  public UriResourceNavigation getUriResourceNavigation() {
    return uriResourceNavigation;
  }

  protected void setUriResourceNavigation(UriResourceNavigation uriResourceNavigation) {
    this.uriResourceNavigation = uriResourceNavigation;
  }

  public UriResourceSingleton getUriResourceSingleton() {
    return this.uriResourceSingleton;
  }

  protected void setUriResourceSingleton(UriResourceSingleton info) {
    this.uriResourceSingleton = info;
    this.type = new SingletonRequest();
  }

  protected List<UriParameter> getKeyPredicates() {
    if (this.uriResourceEntitySet != null) {
      return this.uriResourceEntitySet.getKeyPredicates();
    }
    return null;
  }

  public boolean isReferenceRequest() {
    return this.references;
  }

  protected void setReferenceRequest(boolean ref) {
    this.references = ref;
    this.type = new ReferenceRequest();
  }

  public boolean isValueRequest() {
    return valueRequest;
  }

  private boolean hasMediaStream() {
    return this.uriResourceEntitySet != null && this.uriResourceEntitySet.getEntityType().hasStream();
  }

  private InputStream getMediaStream() {
    return this.request.getBody();
  }

  protected void setValueRequest(boolean valueRequest) {
    this.valueRequest = valueRequest;
    this.type = new ValueRequest();
  }

  @Override
  public boolean allowedMethod() {
    return this.type.allowedMethod();
  }

  public ContextURL getContextURL(OData odata) throws SerializerException {
    return type.getContextURL(odata);
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataTranslatedException, ODataApplicationException {

    if (!this.type.allowedMethod()) {
      methodNotAllowed();
    }

    this.type.execute(handler, response);
  }

  @Override
  public <T> T getSerializerOptions(Class<T> serilizerOptions, ContextURL contextUrl)
      throws ContentNegotiatorException {
    if (serilizerOptions.isAssignableFrom(PrimitiveSerializerOptions.class)) {
      return (T) PrimitiveSerializerOptions.with().contextURL(contextUrl)
          .facetsFrom(getUriResourceProperty().getProperty()).build();
    }
    return super.getSerializerOptions(serilizerOptions, contextUrl);
  }

  @Override
  public ContentType getResponseContentType() throws ContentNegotiatorException {
    return type.getResponseContentType();
  }

  class EntityRequest implements RequestType {

    @Override
    public boolean allowedMethod() {
      return true;
    }

    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), getODataRequest(),
          getCustomContentTypeSupport(), isCollection() ? RepresentationType.COLLECTION_ENTITY
              : RepresentationType.ENTITY);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataTranslatedException, ODataApplicationException {

      EntityResponse entityResponse = EntityResponse.getInstance(DataRequest.this,
          getContextURL(odata), response);

      if (isGET()) {
        if (isCollection()) {
          handler.read(DataRequest.this,
              EntitySetResponse.getInstance(DataRequest.this, getContextURL(odata), response));
        } else {
          handler.read(DataRequest.this,entityResponse);
        }
      } else if (isPUT() || isPATCH()) {
        // RFC 2616: he result of a request having both an If-Match header field and either
        // an If-None-Match or an If-Modified-Since header fields is undefined
        // by this specification.
        boolean ifMatch = getHeader(HttpHeader.IF_MATCH) != null;
        boolean ifNoneMatch = getHeader(HttpHeader.IF_NONE_MATCH).equals("*");
        if(ifMatch) {
          handler.updateEntity(DataRequest.this, getEntityFromClient(), isPATCH(), getETag(),
              entityResponse);
        } else if (ifNoneMatch) {
          // 11.4.4
          handler.createEntity(DataRequest.this, getEntityFromClient(),
              entityResponse.setReturnRepresentation(getReturnRepresentation()));
        } else {
          handler.updateEntity(DataRequest.this, getEntityFromClient(), isPATCH(), getETag(),
              entityResponse);
        }
      } else if (isPOST()) {
        if (hasMediaStream()) {
          handler.createMediaEntity(DataRequest.this, getEntityFromClient(), getMediaStream(),
              entityResponse.setReturnRepresentation(getReturnRepresentation()));
        } else {
          handler.createEntity(DataRequest.this, getEntityFromClient(),
              entityResponse.setReturnRepresentation(getReturnRepresentation()));
        }
      } else if (isDELETE()) {
        handler.deleteEntity(DataRequest.this, getETag(), entityResponse);
      }
    }

    private Entity getEntityFromClient() throws DeserializerException {
      ODataDeserializer deserializer = odata.createDeserializer(ODataFormat
          .fromContentType(getRequestContentType()));
      return deserializer.entity(getODataRequest().getBody(), getEntitySet().getEntityType());
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      // EntitySet based return
      final UriHelper helper = odata.createUriHelper();
      ContextURL.Builder builder = buildEntitySetContextURL(helper, getEntitySet(), getUriInfo(),
          isCollection(), false);
      return builder.build();
    }
  }

  class CountRequest implements RequestType {

    @Override
    public boolean allowedMethod() {
      return isGET();
    }

    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentType.TEXT_PLAIN;
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataTranslatedException, ODataApplicationException {
      handler.read(DataRequest.this, CountResponse.getInstance(DataRequest.this, response));
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      return null;
    }
  }

  class ReferenceRequest implements RequestType {

    @Override
    public boolean allowedMethod() {
      if (isPATCH()) {
        return false;
      }
      return true;
    }

    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), getODataRequest(),
          getCustomContentTypeSupport(), isCollection() ? RepresentationType.COLLECTION_REFERENCE
              : RepresentationType.REFERENCE);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataTranslatedException, ODataApplicationException {
      if (isGET()) {
        if (isCollection()) {
          handler.read(DataRequest.this,
              EntitySetResponse.getInstance(DataRequest.this, getContextURL(odata), response)
                  .sendReferences(true));
        } else {
          handler.read(DataRequest.this,EntityResponse.getInstance(DataRequest.this,
              getContextURL(odata), response).sendReferences(true));
        }
      } else if (isDELETE()) {
        handler.deleteReference(DataRequest.this, getETag(), new NoContentResponse(response));
      } else if (isPUT()) {
        handler.updateReference(DataRequest.this, getETag(), getPayload(), new NoContentResponse(response));
      } else if (isPOST()) {
        // this needs to be against collection of references
        handler.addReference(DataRequest.this, getETag(), getPayload(), new NoContentResponse(response));
      }
    }

    // the payload will be like
    // {
    // "@odata.id": "serviceRoot/People('vincentcalabrese')"
    // }
    // The below code reads as proeprty and converts to an URI
    private URI getPayload() throws DeserializerException {

      ODataDeserializer deserializer = odata.createDeserializer(ODataFormat
          .fromContentType(getRequestContentType()));
      Map<String, String> values = deserializer.read(getODataRequest().getBody(), "@odata.id");
      try {
        return new URI(values.get("@odata.id"));
      } catch (URISyntaxException e) {
        throw new DeserializerException("failed to read @odata.id", e, MessageKeys.UNKOWN_CONTENT);
      }
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      ContextURL.Builder builder = ContextURL.with().suffix(Suffix.REFERENCE);
      if (isCollection()) {
        builder.asCollection();
      }
      return builder.build();
    }
  }

  class PropertyRequest implements RequestType {

    @Override
    public boolean allowedMethod() {
      // create of properties is not allowed,
      // only read, update, delete. Note that delete is
      // same as update with null
      if (isPOST()) {
        return false;
      }

      // 11.4.9.4, collection properties are not supported with merge
      if (isPATCH() && (isCollection() || isPropertyStream())) {
        return false;
      }
      return true;
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
        throws ODataTranslatedException, ODataApplicationException {

      EdmProperty edmProperty = getUriResourceProperty().getProperty();

      PropertyResponse propertyResponse = PropertyResponse.getInstance(DataRequest.this, response,
          edmProperty.getType(), getContextURL(odata), edmProperty.isCollection());

      if (isGET()) {
        handler.read(DataRequest.this, propertyResponse);
      } else if (isPATCH()) {
        handler.updateProperty(DataRequest.this, getPropertyValueFromClient(edmProperty), true,
            getETag(), propertyResponse);
      } else if (isPUT()) {
        if (isPropertyStream()) {
          handler.updateStreamProperty(DataRequest.this, request.getBody(), getETag(),
              new StreamResponse(response));
        } else {
          handler.updateProperty(DataRequest.this, getPropertyValueFromClient(edmProperty), false,
              getETag(), propertyResponse);
        }
      } else if (isDELETE()) {
        Property property = new PropertyImpl();
        property.setName(edmProperty.getName());
        property.setType(edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString());
        handler.updateProperty(DataRequest.this, property, false, getETag(), propertyResponse);
      }
    }

    @Override
    public ContextURL getContextURL(OData odata) throws SerializerException {
      final UriHelper helper = odata.createUriHelper();
      EdmProperty edmProperty = getUriResourceProperty().getProperty();

      ContextURL.Builder builder = ContextURL.with().entitySet(getEntitySet());
      builder = ContextURL.with().entitySet(getEntitySet());
      builder.keyPath(helper.buildContextURLKeyPredicate(getUriResourceEntitySet()
          .getKeyPredicates()));
      builder.navOrPropertyPath(edmProperty.getName());
      if (isPropertyComplex()) {
        EdmComplexType type = ((UriResourceComplexProperty) uriResourceProperty).getComplexType();
        String select = helper.buildContextURLSelectList(type, getUriInfo().getExpandOption(),
            getUriInfo().getSelectOption());
        builder.selectList(select);
      }
      return builder.build();
    }
  }

  class ValueRequest extends PropertyRequest {

    @Override
    public boolean allowedMethod() {
      return isGET() || isDELETE() || isPUT();
    }

    @Override
    public ContentType getResponseContentType() throws ContentNegotiatorException {
      RepresentationType valueRepresentationType = uriResourceProperty.getType() == EdmPrimitiveTypeFactory
          .getInstance(EdmPrimitiveTypeKind.Binary) ? RepresentationType.BINARY
          : RepresentationType.VALUE;
      return ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(), request,
          getCustomContentTypeSupport(), valueRepresentationType);
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataTranslatedException, ODataApplicationException {
      EdmProperty edmProperty = getUriResourceProperty().getProperty();
      if (isGET()) {
        handler.read(DataRequest.this, PrimitiveValueResponse.getInstance(DataRequest.this,
            response, isCollection(), getUriResourceProperty().getProperty()));
      } else if (isDELETE()) {
        Property property = new PropertyImpl();
        property.setName(edmProperty.getName());
        property.setType(edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString());

        PropertyResponse propertyResponse = PropertyResponse.getInstance(DataRequest.this, response,
            edmProperty.getType(), getContextURL(odata), edmProperty.isCollection());
        handler.updateProperty(DataRequest.this, property, false, getETag(), propertyResponse);
      } else if (isPUT()) {
        PropertyResponse propertyResponse = PropertyResponse.getInstance(DataRequest.this, response,
            edmProperty.getType(), getContextURL(odata), edmProperty.isCollection());
        handler.updateProperty(DataRequest.this, getPropertyValueFromClient(edmProperty), false,
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
    public boolean allowedMethod() {
      return isGET();
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
          uriResourceSingleton.getSingleton(), getUriInfo(), isCollection(), true);
      return builder.build();
    }

    @Override
    public void execute(ServiceHandler handler, ODataResponse response)
        throws ODataTranslatedException, ODataApplicationException {
      handler.read(DataRequest.this,
          EntityResponse.getInstance(DataRequest.this, getContextURL(odata), response));
    }
  }

  private org.apache.olingo.commons.api.data.Property getPropertyValueFromClient(
      EdmProperty edmProperty) throws DeserializerException {
    // TODO:this is not right, we should be deserializing the property
    // (primitive, complex, collection of)
    // for now it is responsibility of the user
    ODataDeserializer deserializer = odata.createDeserializer(ODataFormat
        .fromContentType(getRequestContentType()));
    return deserializer.property(getODataRequest().getBody(), edmProperty);
  }
}
