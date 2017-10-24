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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmConstantExpression;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.requests.DataRequest;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

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

  public static boolean  assertHttpMethod(HttpMethod issued, HttpMethod[] allowedMethods,
      ODataResponse response) throws ODataHandlerException {
    boolean allowed = false; 
    for (HttpMethod method :allowedMethods) {
      if (issued.equals(method)) {
        allowed = true;
      }
    }
    if (!allowed) {
      return methodNotAllowed(response, issued, null, allowedMethods); 
    }
    return true;
  }
  
  public boolean assertHttpMethod(ODataResponse response) throws ODataHandlerException {
    boolean allowed = false; 
    HttpMethod issued = this.request.getMethod();
    for (HttpMethod method :allowedMethods()) {
      if (issued.equals(method)) {
        allowed = true;
      }
    }
    if (!allowed) {
      return methodNotAllowed(response, issued, null, allowedMethods()); 
    }
    return true;
  }  

  public HttpMethod[] allowedMethods() {
    return new HttpMethod[] {HttpMethod.GET};
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
      throws ODataLibraryException, ODataApplicationException;

  public abstract ContentType getResponseContentType() throws ContentNegotiatorException;

  public static boolean methodNotAllowed(ODataResponse response, HttpMethod issued, String reason,
      HttpMethod... allowed) throws ODataHandlerException {
    
    StringBuilder sb = new StringBuilder();
    for (HttpMethod method:allowed) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(method.name());
    }
    response.setHeader("Allow", sb.toString());
    if (reason == null) {
      reason = "HTTP method " + issued + " is not allowed.";
    }
    throw new ODataHandlerException(reason,
        ODataHandlerException.MessageKeys.HTTP_METHOD_NOT_ALLOWED, issued.name());
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
  
  protected HttpMethod httpMethod() {
    return this.request.getMethod();
  }  

  private static FullQualifiedName XML10_CHAR_REPLACE_FQN = new FullQualifiedName(
      "org.apache.olingo.v1.xml10-incompatible-char-replacement");
  /**
   * Replacement character for the XML10 characters that are not supported.
   * @return
   */
  protected String xml10IncompatibleCharReplacement() {
    for (EdmSchema schema : getServiceMetaData().getEdm().getSchemas()) {
      if (schema.getEntityContainer() != null) {
        for (EdmAnnotation annotation:schema.getAnnotations()) {
          if (annotation.getTerm() != null
              && annotation.getTerm().getFullQualifiedName().equals(XML10_CHAR_REPLACE_FQN)) {
            EdmConstantExpression expr = annotation.getExpression().asConstant();
            return expr.getValueAsString();            
          }
        }
      }
    }
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public <T> T getSerializerOptions(Class<T> serilizerOptions,
      ContextURL contextUrl, boolean references) throws ContentNegotiatorException {
    
    String xmlReplacement = null;
    if (getResponseContentType().isCompatible(ContentType.APPLICATION_XML)
        || getResponseContentType().isCompatible(ContentType.APPLICATION_ATOM_XML)) {
      xmlReplacement = xml10IncompatibleCharReplacement();
    }
    
    if (serilizerOptions.isAssignableFrom(EntitySerializerOptions.class)) {
      return (T) EntitySerializerOptions.with()
          .contextURL(contextUrl)
          .expand(uriInfo.getExpandOption()).select(this.uriInfo.getSelectOption())
          .writeOnlyReferences(references)
          .xml10InvalidCharReplacement(xmlReplacement)
          .build();
    } else if (serilizerOptions.isAssignableFrom(EntityCollectionSerializerOptions.class)) {
      return (T) EntityCollectionSerializerOptions.with()
          .contextURL(contextUrl)
          .count(uriInfo.getCountOption()).expand(uriInfo.getExpandOption())
          .select(uriInfo.getSelectOption()).writeOnlyReferences(references)
          .id(getODataRequest().getRawBaseUri() + getODataRequest().getRawODataPath())
          .xml10InvalidCharReplacement(xmlReplacement)
          .build();
    } else if (serilizerOptions.isAssignableFrom(ComplexSerializerOptions.class)) {
      return (T) ComplexSerializerOptions.with().contextURL(contextUrl)
          .expand(this.uriInfo.getExpandOption()).select(this.uriInfo.getSelectOption())
          .xml10InvalidCharReplacement(xmlReplacement)
          .build();
    } else if (serilizerOptions.isAssignableFrom(PrimitiveSerializerOptions.class)) {
      return (T) PrimitiveSerializerOptions.with().contextURL(contextUrl)
          .xml10InvalidCharReplacement(xmlReplacement)
          .build();
    }
    return null;
  }

  public ReturnRepresentation getReturnRepresentation() {
    String prefer = this.request.getHeader(HttpHeader.PREFER);
    if (prefer != null) {
      if (prefer.contains("return=minimal")) { //$NON-NLS-1$
        return ReturnRepresentation.MINIMAL;
      } else if (prefer.contains("return=representation")) {    
        return ReturnRepresentation.REPRESENTATION;
      }
    }
    return ReturnRepresentation.NONE;
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

  public ODataSerializer getSerializer() throws ContentNegotiatorException,
      SerializerException {
    return this.odata.createSerializer(getResponseContentType());
  }
  
  public ODataSerializer getSerializer(ContentType type) throws ContentNegotiatorException,
      SerializerException {
    return this.odata.createSerializer(type);
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

  public String getPreference(String key) {
    return getPreferences().get(key);
  }

  public String getQueryParameter(String param) {
    String queryPath = getODataRequest().getRawQueryPath();
    if (queryPath != null) {
      StringTokenizer st = new StringTokenizer(queryPath, ",");
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        int index = token.indexOf('=');
        if (index != -1) {
          String key = token.substring(0, index);
          String value = token.substring(index+1);
          if (key.equals(param)) {
            return value;
          }
        }
      }
    }
    return null;
  }

  public DataRequest parseLink(URI uri) throws UriParserException, UriValidationException  {
    String path = "/";
    URI servicePath = URI.create(getODataRequest().getRawBaseUri());
    path = servicePath.getPath();
    
    String rawPath = uri.getPath();
    int e = rawPath.indexOf(path);
    if (-1 == e) {
      rawPath = uri.getPath();
    } else {
      rawPath = rawPath.substring(e+path.length());
    }

    UriInfo reqUriInfo = new Parser(serviceMetadata.getEdm(), odata).parseUri(rawPath, uri.getQuery(), null, 
        getODataRequest().getRawBaseUri());
    ServiceDispatcher dispatcher = new ServiceDispatcher(odata, serviceMetadata, null, customContentType);
    dispatcher.visit(reqUriInfo);
    dispatcher.request.setUriInfo(reqUriInfo);
    return (DataRequest)dispatcher.request;
  }
}
