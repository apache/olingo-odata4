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
package org.apache.olingo.server.tecsvc.processor;

import java.net.URI;
import java.util.*;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.EntitySetProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleJsonProcessor implements EntitySetProcessor, EntityProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SampleJsonProcessor.class);

    private OData odata;
    private Edm edm;

    @Override
    public void init(OData odata, Edm edm) {
      this.odata = odata;
      this.edm = edm;
    }

    @Override
    public void readEntitySet(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      long time = System.nanoTime();

      LOG.info((System.nanoTime() - time) / 1000 + " microseconds");
      time = System.nanoTime();
      ODataSerializer serializer = odata.createSerializer(ODataFormat.JSON);
      EdmEntitySet edmEntitySet = getEntitySet(uriInfo);
      ContextURL contextUrl = getContextUrl(request, edmEntitySet.getEntityType());
      EntitySet entitySet = createEntitySet(edmEntitySet.getEntityType(), contextUrl.getURI().toASCIIString());
      response.setContent(serializer.entitySet(edmEntitySet, entitySet, contextUrl));
      LOG.info("Finished in " + (System.nanoTime() - time) / 1000 + " microseconds");

      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader("Content-Type", ContentType.APPLICATION_JSON.toContentTypeString());
    }

    @Override
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      long time = System.nanoTime();

      LOG.info((System.nanoTime() - time) / 1000 + " microseconds");
      time = System.nanoTime();
      ODataSerializer serializer = odata.createSerializer(ODataFormat.JSON);
      EdmEntityType entityType = getEntityType(uriInfo);
      Entity entity = createEntity(entityType);

      response.setContent(serializer.entity(entityType, entity,
              getContextUrl(request, entityType)));
      LOG.info("Finished in " + (System.nanoTime() - time) / 1000 + " microseconds");

      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader("Content-Type", ContentType.APPLICATION_JSON.toContentTypeString());
    }

  private ContextURL getContextUrl(ODataRequest request, EdmEntityType entityType) {
    return ContextURL.getInstance(URI.create(request.getRawBaseUri() + "/" + entityType.getName()));
  }

  public EdmEntityType getEntityType(UriInfo uriInfo) {
    return getEntitySet(uriInfo).getEntityType();
  }

  public EdmEntitySet getEntitySet(UriInfo uriInfo) {
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    if(resourcePaths.isEmpty()) {
      throw new RuntimeException("Invalid resource path.");
    }
    String entitySetName = resourcePaths.get(resourcePaths.size()-1).toString();
    return edm.getEntityContainer(new FullQualifiedName("com.sap.odata.test1", "Container"))
            .getEntitySet(entitySetName);
  }

  protected Entity createEntity(EdmEntityType entityType) {
    boolean complex = (entityType.getName().contains("Comp"));
    if(entityType.getName().contains("Coll")) {
      return createEntityWithCollection(complex);
    }
    return createEntity(complex);
  }

  protected Entity createEntity(boolean complex) {
    Entity entity = new EntityImpl();
    Property property = new PropertyImpl();
    property.setName("PropertyString");
    property.setValue(ValueType.PRIMITIVE, "dummyValue");
    entity.getProperties().add(property);
    Property propertyInt = new PropertyImpl();
    propertyInt.setName("PropertyInt16");
    propertyInt.setValue(ValueType.PRIMITIVE, 42);
    entity.getProperties().add(propertyInt);
    Property propertyGuid = new PropertyImpl();
    propertyGuid.setName("PropertyGuid");
    propertyGuid.setValue(ValueType.PRIMITIVE, UUID.randomUUID());
    entity.getProperties().add(propertyGuid);

    if(complex) {
      entity.addProperty(createComplexProperty());
    }

    return entity;
  }

  protected Entity createEntityWithCollection(boolean complex) {
    Entity entity = new EntityImpl();
    Property propertyInt = new PropertyImpl();
    propertyInt.setName("PropertyInt16");
    propertyInt.setValue(ValueType.PRIMITIVE, 42);
    Property property = new PropertyImpl();
    property.setName("CollPropertyString");
    property.setValue(ValueType.COLLECTION_PRIMITIVE, Arrays.asList("dummyValue", "dummyValue_2"));
    entity.getProperties().add(property);
    entity.getProperties().add(propertyInt);
    Property propertyGuid = new PropertyImpl();
    propertyGuid.setName("CollPropertyGuid");
    propertyGuid.setValue(ValueType.COLLECTION_PRIMITIVE, Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
    entity.getProperties().add(propertyGuid);

    if(complex) {
      entity.addProperty(createCollectionOfComplexProperty());
    }

    return entity;
  }

  protected Property createComplexProperty() {
    List<Property> properties = new ArrayList<Property>();
    Property property = new PropertyImpl();
    property.setName("PropertyString");
    property.setValue(ValueType.PRIMITIVE, "dummyValue");
    properties.add(property);
    Property propertyInt = new PropertyImpl();
    propertyInt.setName("PropertyInt16");
    propertyInt.setValue(ValueType.PRIMITIVE, 42);
    properties.add(propertyInt);
    Property propertyGuid = new PropertyImpl();
    propertyGuid.setName("PropertyGuid");
    propertyGuid.setValue(ValueType.PRIMITIVE, UUID.randomUUID());
    properties.add(propertyGuid);

    return new PropertyImpl("com.sap.odata.test1.ETCompAllPrim", "PropertyComplex", ValueType.COMPLEX,
            properties);
  }

  protected Property createCollectionOfComplexProperty() {
    List<Property> properties = new ArrayList<Property>();
    Property property = new PropertyImpl();
    property.setName("PropertyString");
    property.setValue(ValueType.PRIMITIVE, "dummyValue");
    properties.add(property);
    Property propertyInt = new PropertyImpl();
    propertyInt.setName("PropertyInt16");
    propertyInt.setValue(ValueType.PRIMITIVE, 42);
    properties.add(propertyInt);
    Property propertyGuid = new PropertyImpl();
    propertyGuid.setName("PropertyGuid");
    propertyGuid.setValue(ValueType.PRIMITIVE, UUID.randomUUID());
    properties.add(propertyGuid);

    List<Property> properties2 = new ArrayList<Property>();
    Property property2 = new PropertyImpl();
    property2.setName("PropertyString");
    property2.setValue(ValueType.PRIMITIVE, "dummyValue2");
    properties2.add(property2);
    Property property2Int = new PropertyImpl();
    property2Int.setName("PropertyInt16");
    property2Int.setValue(ValueType.PRIMITIVE, 44);
    properties2.add(property2Int);
    Property property2Guid = new PropertyImpl();
    property2Guid.setName("PropertyGuid");
    property2Guid.setValue(ValueType.PRIMITIVE, UUID.randomUUID());
    properties2.add(property2Guid);

    return new PropertyImpl("com.sap.odata.test1.ETCompAllPrim", "PropertyComplex", ValueType.COMPLEX,
            Arrays.asList(properties, properties2));
  }


  protected EntitySet createEntitySet(EdmEntityType edmEntityType, String baseUri) {
    EntitySet entitySet = new EntitySetImpl();
    int count = (int) ((Math.random() * 50) + 1);
    entitySet.setCount(count);
    entitySet.setNext(URI.create(baseUri + "nextLink"));
    for (int i = 0; i < count; i++) {
      entitySet.getEntities().add(createEntity(edmEntityType));
    }
    return entitySet;
  }
}
