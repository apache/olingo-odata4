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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceProperty;

public abstract class ServiceRequest {
  protected OData odata;
  protected UriInfo uriInfo;
  protected ServiceMetadata serviceMetadata;
  protected CustomContentTypeSupport customContentType;
  protected ODataRequest request;

  public ServiceRequest(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  public OData getOdata() {
    return odata;
  }

  public ServiceMetadata getServiceMetaData() {
    return this.serviceMetadata;
  }

  public UriInfo getUriInfo() {
    return uriInfo;
  }

  protected void setUriInfo(UriInfo uriInfo) {
    this.uriInfo = uriInfo;
  }

  public boolean allowedMethod() {
    return isGET();
  }

  public CustomContentTypeSupport getCustomContentTypeSupport() {
    return this.customContentType;
  }

  public void setCustomContentTypeSupport(CustomContentTypeSupport support) {
    this.customContentType = support;
  }

  public ODataRequest getODataRequest() {
    return this.request;
  }

  protected void setODataRequest(ODataRequest request) {
    this.request = request;
  }

  public ContentType getRequestContentType() {
    if (this.request.getHeader(HttpHeader.CONTENT_TYPE) != null) {
      return ContentType.parse(this.request.getHeader(HttpHeader.CONTENT_TYPE));
    }
    return ContentType.APPLICATION_OCTET_STREAM;
  }

  public abstract void execute(ServiceHandler handler, ODataResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  public abstract ContentType getResponseContentType() throws ContentNegotiatorException;

  public void methodNotAllowed() throws ODataHandlerException {
    throw new ODataHandlerException("HTTP method " + this.request.getMethod() + " is not allowed.",
        ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, this.request.getMethod()
            .toString());
  }

  public void notImplemented() throws ODataHandlerException {
    throw new ODataHandlerException("not implemented", //$NON-NLS-1$
        ODataHandlerException.MessageKeys.FUNCTIONALITY_NOT_IMPLEMENTED);
  }

  protected boolean isGET() {
    return this.request.getMethod() == HttpMethod.GET;
  }

  protected boolean isPUT() {
    return this.request.getMethod() == HttpMethod.PUT;
  }

  protected boolean isDELETE() {
    return this.request.getMethod() == HttpMethod.DELETE;
  }

  protected boolean isPATCH() {
    return this.request.getMethod() == HttpMethod.PATCH;
  }

  protected boolean isPOST() {
    return this.request.getMethod() == HttpMethod.POST;
  }

  public <T> T getSerializerOptions(Class<T> serilizerOptions, ContextURL contextUrl)
      throws ContentNegotiatorException {
    final ODataFormat format = ODataFormat.fromContentType(getResponseContentType());

    if (serilizerOptions.isAssignableFrom(EntitySerializerOptions.class)) {
      return (T) EntitySerializerOptions.with()
          .contextURL(format == ODataFormat.JSON_NO_METADATA ? null : contextUrl)
          .expand(uriInfo.getExpandOption()).select(this.uriInfo.getSelectOption()).build();
    } else if (serilizerOptions.isAssignableFrom(EntityCollectionSerializerOptions.class)) {
      return (T) EntityCollectionSerializerOptions.with()
          .contextURL(format == ODataFormat.JSON_NO_METADATA ? null : contextUrl)
          .count(uriInfo.getCountOption()).expand(uriInfo.getExpandOption())
          .select(uriInfo.getSelectOption()).build();
    } else if (serilizerOptions.isAssignableFrom(ComplexSerializerOptions.class)) {
      return (T) ComplexSerializerOptions.with().contextURL(contextUrl)
          .expand(this.uriInfo.getExpandOption()).select(this.uriInfo.getSelectOption()).build();
    }
    return null;
  }

  static ContextURL.Builder buildEntitySetContextURL(UriHelper helper, EdmBindingTarget edmEntitySet,
      UriInfo uriInfo, boolean collectionReturn, boolean singleton) throws SerializerException {
    ContextURL.Builder builder = ContextURL.with().entitySetOrSingletonOrType(edmEntitySet.getName());
    String select = helper.buildContextURLSelectList(edmEntitySet.getEntityType(),
        uriInfo.getExpandOption(), uriInfo.getSelectOption());
    if (!singleton) {
      builder.suffix(collectionReturn ? null : Suffix.ENTITY);
    }
    builder.selectList(select);

    final UriInfoResource resource = uriInfo.asUriInfoResource();
    final List<UriResource> resourceParts = resource.getUriResourceParts();
    final List<String> path = getPropertyPath(resourceParts);
    builder.navOrPropertyPath(buildPropertyPath(path));
    return builder;
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

  enum ReturnRepresentation {
    REPRESENTATION, MINIMAL
  };

  public ReturnRepresentation getReturnRepresentation() {
    String prefer = this.request.getHeader(HttpHeader.PREFER);
    if (prefer == null) {
      return ReturnRepresentation.REPRESENTATION;
    }
    if (prefer.contains("return=minimal")) { //$NON-NLS-1$
      return ReturnRepresentation.MINIMAL;
    }
    return ReturnRepresentation.REPRESENTATION;
  }

  public String getHeader(String key) {
    return this.request.getHeader(key);
  }

  public String getETag() {
    String etag = getHeader(HttpHeader.IF_MATCH);
    if (etag == null) {
      etag = getHeader(HttpHeader.IF_NONE_MATCH);
    }
    return ((etag == null) ? "*" : etag); //$NON-NLS-1$
  }

  protected ODataSerializer getSerializer() throws ContentNegotiatorException,
      SerializerException {
    ODataFormat format = ODataFormat.fromContentType(getResponseContentType());
    return this.odata.createSerializer(format);
  }

  public Map<String, String> getPreferences(){
    HashMap<String, String> map = new HashMap<String, String>();
    List<String> headers = request.getHeaders(HttpHeader.PREFER);
    if (headers != null) {
      for (String header:headers) {
        int idx = header.indexOf('=');
        if (idx != -1) {
          String key = header.substring(0, idx);
          String value = header.substring(idx+1);
          if (value.startsWith("\"")) {
            value = value.substring(1);
          }
          if (value.endsWith("\"")) {
            value = value.substring(0, value.length()-1);
          }
          map.put(key, value);
        } else {
          map.put(header, "true");
        }
      }
    }
    return map;
  }
}
