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
package org.apache.olingo.server.core.serializer.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextURLBuilderTest {

  private static final URI serviceRoot = URI.create("http://host/service/");

  @Test
  public void buildServiceDocument() {
    final ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot).build();
    assertEquals(serviceRoot + "$metadata", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildRelative() {
    final ContextURL contextURL = ContextURL.with().build();
    assertEquals("$metadata", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    final ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .build();
    assertEquals(serviceRoot + "$metadata#Customers", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildDerivedEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    final ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .derived(derivedType)
        .build();
    assertEquals(serviceRoot + "$metadata#Customers/Model.VipCustomer",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildEntitySetWithEntitySuffix() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    final ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .suffix(Suffix.ENTITY)
        .build();
    assertEquals(serviceRoot + "$metadata#Customers/$entity",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildEntity() {
    EdmEntityType entityType = mock(EdmEntityType.class);
    when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("namespace", "entityType"));
    ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .type(entityType)
        .build();
    assertEquals(serviceRoot + "$metadata#namespace.entityType",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .type(entityType)
        .asCollection()
        .build();
    assertEquals(serviceRoot + "$metadata#Collection(namespace.entityType)",
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
    final ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .derived(derivedType)
        .suffix(Suffix.ENTITY)
        .build();
    assertEquals(serviceRoot + "$metadata#Customers/Model.VipCustomer/$entity",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildProperty() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .keyPath("1")
        .navOrPropertyPath("Name")
        .build();
    assertEquals(serviceRoot + "$metadata#Customers(1)/Name",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .keyPath("one=1,two='two'")
        .navOrPropertyPath("ComplexName")
        .selectList("Part1")
        .build();
    assertEquals(serviceRoot + "$metadata#Customers(one=1,two='two')/ComplexName(Part1)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildPrimitiveType() {
    ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .type(OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String))
        .build();
    assertEquals(serviceRoot + "$metadata#Edm.String",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .type(OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String)).asCollection()
        .build();
    assertEquals(serviceRoot + "$metadata#Collection(Edm.String)",
        ContextURLBuilder.create(contextURL).toString());
  }

  @Test
  public void buildComplexType() throws Exception {
    EdmComplexType baseType = mock(EdmComplexType.class);
    when(baseType.getFullQualifiedName()).thenReturn(new FullQualifiedName("namespace", "BaseTypeName"));

    ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .type(baseType)
        .build();
    assertEquals(serviceRoot + "$metadata#namespace.BaseTypeName",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .type(baseType)
        .asCollection()
        .build();
    assertEquals(serviceRoot + "$metadata#Collection(namespace.BaseTypeName)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildSuffixWithoutEntitySet() {
    ContextURLBuilder.create(ContextURL.with().suffix(Suffix.ENTITY).build());
  }

  @Test
  public void buildReference() {
    final ContextURL contextURL = ContextURL.with().suffix(Suffix.REFERENCE).build();
    assertEquals("../$metadata#$ref", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildReferenceWithEntitySet() {
    EdmEntitySet entitySet = mock(EdmEntitySet.class);
    when(entitySet.getName()).thenReturn("Customers");
    ContextURLBuilder.create(ContextURL.with().entitySet(entitySet).suffix(Suffix.REFERENCE).build());
  }

  @Test
  public void buildWithCharactersToBeEscaped() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Entitäten");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(
        new FullQualifiedName("Namensräumchen", "UnüblicherName"));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet).derived(derivedType).build();
    assertEquals("$metadata#Entit%C3%A4ten/Namensr%C3%A4umchen.Un%C3%BCblicherName",
        ContextURLBuilder.create(contextURL).toString());
  }
  
  @Test
  public void buildWithComplexDerivedTypeInSelect1() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("ESCompCollDerived");
    ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .selectList("PropertyCompAno/olingo.odata.test1.CTBaseAno/AdditionalPropString")
        .build();
    assertEquals(serviceRoot + "$metadata#ESCompCollDerived(PropertyCompAno/"
        + "olingo.odata.test1.CTBaseAno/AdditionalPropString)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildWithComplexDerivedTypeInSelect2() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("ESCompCollComp");
    ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .selectList("PropertyComp/CollPropertyComp/olingo.odata.test1.CTBase/AdditionalPropString")
        .build();
    assertEquals(serviceRoot + "$metadata#ESCompCollComp(PropertyComp/CollPropertyComp/"
        + "olingo.odata.test1.CTBase/AdditionalPropString)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildWithNavPropertyInSelect() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("ESTwoKeyNav");
    ContextURL contextURL = ContextURL.with().serviceRoot(serviceRoot)
        .entitySet(entitySet)
        .selectList("CollPropertyCompNav/NavPropertyETTwoKeyNavMany")
        .build();
    assertEquals(serviceRoot + "$metadata#ESTwoKeyNav(CollPropertyCompNav/NavPropertyETTwoKeyNavMany)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
}
