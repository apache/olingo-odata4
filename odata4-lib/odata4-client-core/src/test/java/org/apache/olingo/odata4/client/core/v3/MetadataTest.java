/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.client.core.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import org.apache.olingo.odata4.client.api.http.HttpMethod;
import org.apache.olingo.odata4.client.core.AbstractTest;
import org.apache.olingo.odata4.client.core.ODataV3Client;
import org.apache.olingo.odata4.client.core.edm.xml.v3.ComplexTypeImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v3.EntityContainerImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v3.EntityTypeImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v3.FunctionImportImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v3.SchemaImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v3.XMLMetadataImpl;
import org.junit.Test;

public class MetadataTest extends AbstractTest {

  @Override
  protected ODataV3Client getClient() {
    return v3Client;
  }

  @Test
  public void parse() {
    final XMLMetadataImpl metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    final EntityTypeImpl order = metadata.getSchemas().get(0).getEntityType("Order");
    assertNotNull(order);
    assertEquals("Order", order.getName());

    final List<FunctionImportImpl> functionImports = metadata.getSchemas().get(0).
            getDefaultEntityContainer().getFunctionImports();
    int legacyGetters = 0;
    int legacyPosters = 0;
    int actions = 0;
    int functions = 0;
    for (FunctionImportImpl functionImport : functionImports) {
      if (HttpMethod.GET.name().equals(functionImport.getHttpMethod())) {
        legacyGetters++;
      } else if (HttpMethod.POST.name().equals(functionImport.getHttpMethod())) {
        legacyPosters++;
      } else if (functionImport.getHttpMethod() == null) {
        if (functionImport.isSideEffecting()) {
          actions++;
        } else {
          functions++;
        }
      }
    }
    assertEquals(6, legacyGetters);
    assertEquals(1, legacyPosters);
    assertEquals(5, actions);
    assertEquals(0, functions);
  }

  @Test
  public void multipleSchemas() {
    final XMLMetadataImpl metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);

    final SchemaImpl first = metadata.getSchema("NorthwindModel");
    assertNotNull(first);

    final SchemaImpl second = metadata.getSchema("ODataWebV3.Northwind.Model");
    assertNotNull(second);

    final EntityContainerImpl entityContainer = second.getDefaultEntityContainer();
    assertNotNull(entityContainer);
    assertEquals("NorthwindEntities", entityContainer.getName());
  }

  @Test
  public void entityType() {
    final XMLMetadataImpl metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    final EntityContainerImpl container = metadata.getSchema(0).getEntityContainers().get(0);
    assertNotNull(container);
    final EntityTypeImpl type = metadata.getSchema(0).getEntityType("ProductReview");
    assertNotNull(type);

    assertFalse(type.getProperties().isEmpty());
    assertNotNull(type.getProperties().get(0));

    assertFalse(type.getKey().getPropertyRefs().isEmpty());
    assertNotNull(type.getKey().getPropertyRefs().get(0));
  }

  @Test
  public void complexType() {
    final XMLMetadataImpl metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    final EntityContainerImpl container = metadata.getSchema(0).getEntityContainers().get(0);
    assertNotNull(container);
    final ComplexTypeImpl type = metadata.getSchema(0).getComplexType("ContactDetails");
    assertNotNull(type);

    assertFalse(type.getProperties().isEmpty());
    assertNotNull(type.getProperties().get(0));
  }

  @Test
  public void functionImport() {
    final XMLMetadataImpl metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    final EntityContainerImpl container = metadata.getSchema(0).getEntityContainers().get(0);
    assertNotNull(container);
    final FunctionImportImpl funcImp = container.getFunctionImport("GetArgumentPlusOne");
    assertNotNull(funcImp);

    assertNotNull(funcImp.getParameters().get(0));
  }
}
