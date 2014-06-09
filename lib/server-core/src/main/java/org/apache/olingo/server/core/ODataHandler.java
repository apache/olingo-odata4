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

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.PrimitiveValueImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.op.InjectableSerializerProvider;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.DefaultProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.EntitySetProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.validator.UriValidator;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ODataHandler {

  private final OData odata;
  private final Edm edm;
  private Map<Class<? extends Processor>, Processor> processors = new HashMap<Class<? extends Processor>, Processor>();

  public ODataHandler(final OData server, final Edm edm) {
    this.odata = server;
    this.edm = edm;

    register(new DefaultProcessor());
    register(new DefaultRedirectProcessor());
  }

  public ODataResponse process(final ODataRequest request) {
    try {
      ODataResponse response = new ODataResponse();

      validateODataVersion(request, response);

      Parser parser = new Parser();
      String odUri =
          request.getRawODataPath() + (request.getRawQueryPath() == null ? "" : "?" + request.getRawQueryPath());
      UriInfo uriInfo = parser.parseUri(odUri, edm);

      UriValidator validator = new UriValidator();
      validator.validate(uriInfo, request.getMethod());

      String requestedContentType = doContentNegotiation();

      switch (uriInfo.getKind()) {
      case metadata:
        MetadataProcessor mp = selectProcessor(MetadataProcessor.class);
        mp.readMetadata(request, response, uriInfo, HttpContentType.APPLICATION_XML);
        break;
      case service:
        if ("".equals(request.getRawODataPath())) {
          RedirectProcessor rdp = selectProcessor(RedirectProcessor.class);
          rdp.redirect(request, response);
        } else {
          ServiceDocumentProcessor sdp = selectProcessor(ServiceDocumentProcessor.class);
          sdp.readServiceDocument(request, response, uriInfo, requestedContentType);
        }
        break;
      case resource:
        handleResourceDispatching(request, response, uriInfo, requestedContentType);
        break;
      default:
        throw new ODataRuntimeException("not implemented");
      }

      return response;
    } catch (Exception e) {
      // TODO OData error message handling
      throw new RuntimeException(e);
    }
  }

  private String doContentNegotiation() {
    // TODO: Content Negotiation
    return HttpContentType.APPLICATION_JSON;
  }

  private void handleResourceDispatching(final ODataRequest request, ODataResponse response, UriInfo uriInfo,
      String requestedContentType) {
    int lastPathSegmentIndex = uriInfo.getUriResourceParts().size() - 1;
    UriResource lastPathSegment = uriInfo.getUriResourceParts().get(lastPathSegmentIndex);
    switch (lastPathSegment.getKind()) {
    case entitySet:
      long time = System.nanoTime();
      ResWrap<EntitySet> wrap = new ResWrap<EntitySet>(
          ContextURL.getInstance(URI.create("dummyContextURL")), "dummyMetadataETag",
          createEntitySet());
      System.out.println((System.nanoTime() - time) / 1000 + " microseconds");
      time = System.nanoTime();
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      if (false) {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
        mapper.setInjectableValues(new InjectableValues.Std()
            .addValue(ODataServiceVersion.class, ODataServiceVersion.V40)
            .addValue(Boolean.class, Boolean.TRUE));
        mapper.setSerializerProvider(new InjectableSerializerProvider(mapper.getSerializerProvider(),
            mapper.getSerializationConfig()
                .withAttribute(ODataServiceVersion.class, ODataServiceVersion.V40)
                .withAttribute(Boolean.class, Boolean.TRUE),
            mapper.getSerializerFactory()));
        try {
          mapper.writeValue(buffer.getOutputStream(), wrap);
        } catch (final IOException e) {}
        response.setContent(buffer.getInputStream());
      } else {
        ODataSerializer serializer = odata.createSerializer(org.apache.olingo.server.api.serializer.ODataFormat.JSON);
        response.setContent(serializer.entitySet(
            edm.getEntityContainer(new FullQualifiedName("com.sap.odata.test1", "Container"))
                .getEntitySet("ESAllPrim"),
            wrap.getPayload(),
            ContextURL.getInstance(URI.create("dummyContextURL"))));
      }
      System.out.println((System.nanoTime() - time) / 1000 + " microseconds");
      response.setStatusCode(200);
      response.setHeader("Content-Type", ContentType.APPLICATION_JSON);

      if (((UriResourcePartTyped) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntitySetProcessor esp = selectProcessor(EntitySetProcessor.class);
          esp.readEntitySet(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class);
          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      }
      break;
    case navigationProperty:
      if (((UriResourceNavigation) lastPathSegment).isCollection()) {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntitySetProcessor esp = selectProcessor(EntitySetProcessor.class);
          esp.readEntitySet(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      } else {
        if (request.getMethod().equals(HttpMethod.GET)) {
          EntityProcessor ep = selectProcessor(EntityProcessor.class);
          ep.readEntity(request, response, uriInfo, requestedContentType);
        } else {
          throw new ODataRuntimeException("not implemented");
        }
      }
      break;
    default:
      throw new ODataRuntimeException("not implemented");
    }
  }

  private void validateODataVersion(ODataRequest request, ODataResponse response) {
    List<String> maxVersionHeader = request.getHeader(HttpHeader.ODATA_MAX_VERSION);

    if (maxVersionHeader != null && maxVersionHeader.size() > 0) {
      if (ODataServiceVersion.isBiggerThan(ODataServiceVersion.V40.toString(), maxVersionHeader.get(0))) {
        throw new ODataRuntimeException("400 Bad Request - ODataVersion not supported: " + maxVersionHeader.get(0));
      }
    }

    response.setHeader(HttpHeader.ODATA_VERSION, ODataServiceVersion.V40.toString());
  }

  private <T extends Processor> T selectProcessor(Class<T> cls) {
    @SuppressWarnings("unchecked")
    T p = (T) processors.get(cls);

    if (p == null) {
      throw new ODataRuntimeException("Not implemented");
    }

    return p;
  }

  public void register(Processor processor) {

    processor.init(odata, edm);

    for (Class<?> cls : processor.getClass().getInterfaces()) {
      if (Processor.class.isAssignableFrom(cls) && cls != Processor.class) {
        @SuppressWarnings("unchecked")
        Class<? extends Processor> procClass = (Class<? extends Processor>) cls;
        processors.put(procClass, processor);
      }
    }
  }

  protected Entity createEntity() {
    Entity entity = new EntityImpl();
    Property property = new PropertyImpl();
    property.setName("PropertyString");
    property.setType("String"); //"dummyType");
    property.setValue(new PrimitiveValueImpl("dummyValue"));
    entity.getProperties().add(property);
    Property propertyInt = new PropertyImpl();
    propertyInt.setName("PropertyInt16");
    // propertyInt.setType("Edm.Int32");
    propertyInt.setValue(new PrimitiveValueImpl("042"));
    entity.getProperties().add(propertyInt);
    Property propertyGuid = new PropertyImpl();
    propertyGuid.setName("PropertyGuid");
    propertyGuid.setType("Edm.Guid");
    propertyGuid.setValue(new PrimitiveValueImpl(UUID.randomUUID().toString()));
    entity.getProperties().add(propertyGuid);
    return entity;
  }

  protected EntitySet createEntitySet() {
    EntitySet entitySet = new EntitySetImpl();
    entitySet.setCount(4242);
    entitySet.setNext(URI.create("nextLinkURI"));
    for (int i = 0; i < 1000; i++) {
      entitySet.getEntities().add(createEntity());
    }
    return entitySet;
  }
}
