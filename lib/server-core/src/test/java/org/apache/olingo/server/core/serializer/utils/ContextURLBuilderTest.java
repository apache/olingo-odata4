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
package org.apache.olingo.server.core.serializer.utils;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextURLBuilderTest {

  @Test
  public void buildServiceDocument() {
    ContextURL contextURL = ContextURL.with()
        .serviceRoot(URI.create("http://host/service/")).build();
    assertEquals("http://host/service/$metadata", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildRelative() {
    ContextURL contextURL = ContextURL.with().build();
    assertEquals("$metadata", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .build();
    assertEquals("http://host/service/$metadata#Customers", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildDerivedEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .derived(derivedType)
        .build();
    assertEquals("http://host/service/$metadata#Customers/Model.VipCustomer",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildDerivedEntitySetWithoutEntitySet() {
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    ContextURLBuilder.create(ContextURL.with().derived(derivedType).build());
  }

  @Test
  public void buildDerivedEntity() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .derived(derivedType)
        .suffix(Suffix.ENTITY)
        .build();
    assertEquals("http://host/service/$metadata#Customers/Model.VipCustomer/$entity",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildSuffixWithoutEntitySet() {
    ContextURLBuilder.create(ContextURL.with().suffix(Suffix.ENTITY).build());
  }

  @Test
  public void buildReference() {
    ContextURL contextURL = ContextURL.with().suffix(Suffix.REFERENCE).build();
    assertEquals("$metadata#$ref", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildReferenceWithEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURLBuilder.create(ContextURL.with().entitySet(entitySet).suffix(Suffix.REFERENCE).build());
  }

  @Test
  public void buildWithCharactersToBeEscaped() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Entitäten");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(
        new FullQualifiedName("Namensräumchen", "UnüblicherName"));
    ContextURL contextURL = ContextURL.with().entitySet(entitySet).derived(derivedType).build();
    assertEquals("$metadata#Entit%C3%A4ten/Namensr%C3%A4umchen.Un%C3%BCblicherName",
        ContextURLBuilder.create(contextURL).toString());
  }
}
