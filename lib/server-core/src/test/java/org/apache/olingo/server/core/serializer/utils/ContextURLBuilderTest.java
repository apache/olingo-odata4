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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.core.edm.provider.EdmComplexTypeImpl;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextURLBuilderTest {

  @Test
  public void buildServiceDocument() {
    final ContextURL contextURL = ContextURL.with()
        .serviceRoot(URI.create("http://host/service/")).build();
    assertEquals("http://host/service/$metadata", ContextURLBuilder.create(contextURL).toASCIIString());
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
    final ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
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
    final ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
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
    final ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .derived(derivedType)
        .suffix(Suffix.ENTITY)
        .build();
    assertEquals("http://host/service/$metadata#Customers/Model.VipCustomer/$entity",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildProperty() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .keyPath("1")
        .navOrPropertyPath("Name")
        .build();
    assertEquals("http://host/service/$metadata#Customers(1)/Name",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .keyPath("one=1,two='two'")
        .navOrPropertyPath("Name")
        .build();
    assertEquals("http://host/service/$metadata#Customers(one=1,two='two')/Name",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }  

  @Test
  public void buildPrimitiveType() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(EdmString.getInstance())
        .build();
    assertEquals("http://host/service/$metadata#Edm.String",
        ContextURLBuilder.create(contextURL).toASCIIString());
    
    contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(EdmString.getInstance()).asCollection()
        .build();
    assertEquals("http://host/service/$metadata#Collection(Edm.String)",
        ContextURLBuilder.create(contextURL).toString());
  }  

  @Test
  public void buildComplexType() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    ComplexType baseComplexType = new ComplexType();
    List<Property> baseProperties = new ArrayList<Property>();
    baseProperties.add(new Property().setName("prop1").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    List<NavigationProperty> baseNavigationProperties = new ArrayList<NavigationProperty>();
    baseNavigationProperties.add(new NavigationProperty().setName("nav1"));
    baseComplexType.setName("BaseTypeName").setAbstract(false).setOpenType(false).setProperties(baseProperties)
        .setNavigationProperties(baseNavigationProperties);
    when(provider.getComplexType(baseName)).thenReturn(baseComplexType);

    EdmComplexType baseType = EdmComplexTypeImpl.getInstance(edm, baseName, baseComplexType);    
    
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(baseType)
        .build();
    assertEquals("http://host/service/$metadata#namespace.BaseTypeName",
        ContextURLBuilder.create(contextURL).toASCIIString());    
  }  

  @Test(expected = IllegalArgumentException.class)
  public void buildSuffixWithoutEntitySet() {
    ContextURLBuilder.create(ContextURL.with().suffix(Suffix.ENTITY).build());
  }

  @Test
  public void buildReference() {
    final ContextURL contextURL = ContextURL.with().suffix(Suffix.REFERENCE).build();
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
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet).derived(derivedType).build();
    assertEquals("$metadata#Entit%C3%A4ten/Namensr%C3%A4umchen.Un%C3%BCblicherName",
        ContextURLBuilder.create(contextURL).toString());
  }
}
