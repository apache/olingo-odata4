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

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.PrimitiveValueImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.EntitySetProcessor;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;

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

      EntitySet entitySet = createEntitySet();

      LOG.info((System.nanoTime() - time) / 1000 + " microseconds");
      time = System.nanoTime();
      ODataSerializer serializer = odata.createSerializer(ODataFormat.JSON);
      response.setContent(serializer.entitySet(
              edm.getEntityContainer(new FullQualifiedName("com.sap.odata.test1", "Container"))
                      .getEntitySet("ESAllPrim"),
              entitySet,
              ContextURL.getInstance(URI.create("dummyContextURL"))));
      LOG.info("Finished in " + (System.nanoTime() - time) / 1000 + " microseconds");

      response.setStatusCode(200);
      response.setHeader("Content-Type", ContentType.APPLICATION_JSON);
    }

    @Override
    public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      long time = System.nanoTime();
      Entity entity = createEntity();

      LOG.info((System.nanoTime() - time) / 1000 + " microseconds");
      time = System.nanoTime();
      ODataSerializer serializer = odata.createSerializer(ODataFormat.JSON);
      response.setContent(serializer.entity(
              edm.getEntityContainer(new FullQualifiedName("com.sap.odata.test1", "Container"))
                      .getEntitySet("ESAllPrim").getEntityType(),
              entity,
              ContextURL.getInstance(URI.create("dummyContextURL"))));
      LOG.info("Finished in " + (System.nanoTime() - time) / 1000 + " microseconds");

      response.setStatusCode(200);
      response.setHeader("Content-Type", ContentType.APPLICATION_JSON);
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
      propertyInt.setValue(new PrimitiveValueImpl("42"));
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
