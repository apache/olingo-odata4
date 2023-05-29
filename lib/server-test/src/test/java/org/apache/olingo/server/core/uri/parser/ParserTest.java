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
package org.apache.olingo.server.core.uri.parser;

import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests of the <code>Parser</code> implementation that require mocking of the EDM.
 */
public class ParserTest {

  @Test
  public void keyPropertyGuid() throws Exception {
    final String entitySetName = "ESGuid";
    final String keyPropertyName = "a";
    EdmProperty keyProperty = Mockito.mock(EdmProperty.class);
    Mockito.when(keyProperty.getType())
        .thenReturn(OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Guid));
    EdmKeyPropertyRef keyPropertyRef = Mockito.mock(EdmKeyPropertyRef.class);
    Mockito.when(keyPropertyRef.getName()).thenReturn(keyPropertyName);
    Mockito.when(keyPropertyRef.getProperty()).thenReturn(keyProperty);
    EdmEntityType entityType = Mockito.mock(EdmEntityType.class);
    Mockito.when(entityType.getKeyPredicateNames()).thenReturn(Collections.singletonList(keyPropertyName));
    Mockito.when(entityType.getKeyPropertyRefs()).thenReturn(Collections.singletonList(keyPropertyRef));
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn(entitySetName);
    Mockito.when(entitySet.getEntityType()).thenReturn(entityType);
    EdmEntityContainer container = Mockito.mock(EdmEntityContainer.class);
    Mockito.when(container.getEntitySet(entitySetName)).thenReturn(entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    new TestUriValidator().setEdm(mockedEdm)
        .run("ESGuid(f89dee73-af9f-4cd4-b330-db93c25ff3c7)")
        .goPath()
        .at(0).isEntitySet(entitySetName)
        .at(0).isKeyPredicate(0, keyPropertyName, "f89dee73-af9f-4cd4-b330-db93c25ff3c7");

    new TestUriValidator().setEdm(mockedEdm)
        .run("ESGuid(889e3e73-af9f-4cd4-b330-db93c25ff3c7)")
        .goPath()
        .at(0).isEntitySet(entitySetName)
        .at(0).isKeyPredicate(0, keyPropertyName, "889e3e73-af9f-4cd4-b330-db93c25ff3c7");
  }

  @Test
  public void keyPropertyGuidStartsWithNumber() throws Exception {
    final String entitySetName = "ESGuid";
    final String keyPropertyName = "a";
    EdmProperty keyProperty = Mockito.mock(EdmProperty.class);
    Mockito.when(keyProperty.getType())
        .thenReturn(OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Guid));
    EdmKeyPropertyRef keyPropertyRef = Mockito.mock(EdmKeyPropertyRef.class);
    Mockito.when(keyPropertyRef.getName()).thenReturn(keyPropertyName);
    Mockito.when(keyPropertyRef.getProperty()).thenReturn(keyProperty);
    EdmEntityType entityType = Mockito.mock(EdmEntityType.class);
    Mockito.when(entityType.getKeyPredicateNames()).thenReturn(Collections.singletonList(keyPropertyName));
    Mockito.when(entityType.getKeyPropertyRefs()).thenReturn(Collections.singletonList(keyPropertyRef));
    Mockito.when(entityType.getPropertyNames()).thenReturn(Collections.singletonList(keyPropertyName));
    Mockito.when(entityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn(entitySetName);
    Mockito.when(entitySet.getEntityType()).thenReturn(entityType);
    EdmEntityContainer container = Mockito.mock(EdmEntityContainer.class);
    Mockito.when(container.getEntitySet(entitySetName)).thenReturn(entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    new TestUriValidator().setEdm(mockedEdm)
        .run("ESGuid", "$filter=a eq 889e3e73-af9f-4cd4-b330-db93c25ff3c7");
  }

  @Test
  public void navPropertySameNameAsEntitySet() throws Exception {
    final String namespace = "namespace";
    final String entityTypeName = "ETNavProp";
    final FullQualifiedName nameETNavProp = new FullQualifiedName(namespace, entityTypeName);
    final String entitySetName = "ESNavProp";
    final String keyPropertyName = "a";
    EdmProperty keyProperty = Mockito.mock(EdmProperty.class);
    Mockito.when(keyProperty.getType())
        .thenReturn(OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte));
    EdmKeyPropertyRef keyPropertyRef = Mockito.mock(EdmKeyPropertyRef.class);
    Mockito.when(keyPropertyRef.getName()).thenReturn(keyPropertyName);
    Mockito.when(keyPropertyRef.getProperty()).thenReturn(keyProperty);
    EdmNavigationProperty navProperty = Mockito.mock(EdmNavigationProperty.class);
    Mockito.when(navProperty.getName()).thenReturn(entitySetName);
    Mockito.when(navProperty.isCollection()).thenReturn(true);
    EdmEntityType entityType = Mockito.mock(EdmEntityType.class);
    Mockito.when(entityType.getFullQualifiedName()).thenReturn(nameETNavProp);
    Mockito.when(entityType.getKeyPredicateNames()).thenReturn(Collections.singletonList(keyPropertyName));
    Mockito.when(entityType.getKeyPropertyRefs()).thenReturn(Collections.singletonList(keyPropertyRef));
    Mockito.when(entityType.getNavigationProperty(entitySetName)).thenReturn(navProperty);
    Mockito.when(navProperty.getType()).thenReturn(entityType);
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn(entitySetName);
    Mockito.when(entitySet.getEntityType()).thenReturn(entityType);
    EdmEntityContainer container = Mockito.mock(EdmEntityContainer.class);
    Mockito.when(container.getEntitySet(entitySetName)).thenReturn(entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    new TestUriValidator().setEdm(mockedEdm)
        .run("ESNavProp(1)/ESNavProp(2)/ESNavProp(3)/ESNavProp")
        .goPath()
        .at(0).isEntitySet(entitySetName)
        .at(0).isKeyPredicate(0, keyPropertyName, "1")
        .at(1).isNavProperty(entitySetName, nameETNavProp, false)
        .at(1).isKeyPredicate(0, keyPropertyName, "2")
        .at(2).isNavProperty(entitySetName, nameETNavProp, false)
        .at(2).isKeyPredicate(0, keyPropertyName, "3")
        .at(3).isNavProperty(entitySetName, nameETNavProp, true);
  }

  /**
   * Test for EntitySet and NavigationProperty with same name defined in metadata.
   * (related to Olingo issue OLINGO-741)
   */
  @Test
  public void expandNavigationPropertyWithSameNameAsEntitySet() throws Exception {
    TestUriValidator testUri = new TestUriValidator();

    Edm mockEdm = Mockito.mock(Edm.class);
    EdmEntitySet esCategory = Mockito.mock(EdmEntitySet.class);
    EdmEntitySet esProduct = Mockito.mock(EdmEntitySet.class);
    EdmEntityType typeCategory = Mockito.mock(EdmEntityType.class);
    EdmEntityContainer container = Mockito.mock(EdmEntityContainer.class);
    EdmNavigationProperty productsNavigation = Mockito.mock(EdmNavigationProperty.class);
    EdmEntityType productsType = Mockito.mock(EdmEntityType.class);

    final FullQualifiedName nameProducts = new FullQualifiedName("NS", "Products");
    Mockito.when(mockEdm.getEntityContainer()).thenReturn(container);
    Mockito.when(typeCategory.getName()).thenReturn("Category");
    Mockito.when(typeCategory.getNamespace()).thenReturn("NS");
    Mockito.when(esCategory.getEntityType()).thenReturn(typeCategory);
    Mockito.when(productsNavigation.getName()).thenReturn("Products");
    Mockito.when(typeCategory.getNavigationProperty("Products")).thenReturn(productsNavigation);
    Mockito.when(container.getEntitySet("Category")).thenReturn(esCategory);
    Mockito.when(container.getEntitySet("Products")).thenReturn(esProduct);
    Mockito.when(productsType.getFullQualifiedName()).thenReturn(nameProducts);
    Mockito.when(productsType.getNamespace()).thenReturn("NS");
    Mockito.when(productsNavigation.getType()).thenReturn(productsType);

    // test and verify
    testUri.setEdm(mockEdm)
        .run("Category", "$expand=Products")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("Products", nameProducts, false)
        .isType(nameProducts, false);
    Mockito.verifyZeroInteractions(esProduct);
  }

  /**
   * Test for EntitySet with navigation to a not existing NavigationProperty (name)
   * but with another EntitySet with this name defined in metadata.
   * (related to Olingo issue OLINGO-755)
   */
  @Test
  public void entitySetWoNavigationButWithEntitySetWithSameName() throws Exception {
    TestUriValidator testUri = new TestUriValidator();

    Edm mockEdm = Mockito.mock(Edm.class);
    EdmEntitySet esCategory = Mockito.mock(EdmEntitySet.class);
    EdmEntitySet esProduct = Mockito.mock(EdmEntitySet.class);
    EdmEntityType typeCategory = Mockito.mock(EdmEntityType.class);
    FullQualifiedName fqnCategory = new FullQualifiedName("NS", "Category");
    EdmEntityContainer container = Mockito.mock(EdmEntityContainer.class);
    EdmNavigationProperty productsNavigation = Mockito.mock(EdmNavigationProperty.class);
    EdmEntityType typeProduct = Mockito.mock(EdmEntityType.class);
    FullQualifiedName fqnProduct = new FullQualifiedName("NS", "Products");

    Mockito.when(mockEdm.getEntityContainer()).thenReturn(container);
    Mockito.when(typeCategory.getName()).thenReturn(fqnCategory.getName());
    Mockito.when(typeCategory.getNamespace()).thenReturn(fqnCategory.getNamespace());
    Mockito.when(typeCategory.getFullQualifiedName()).thenReturn(fqnCategory);
    Mockito.when(esCategory.getEntityType()).thenReturn(typeCategory);
    Mockito.when(esProduct.getEntityType()).thenReturn(typeProduct);
    Mockito.when(productsNavigation.getName()).thenReturn("Products");
    Mockito.when(typeCategory.getProperty("Products")).thenReturn(productsNavigation);
    Mockito.when(container.getEntitySet("Category")).thenReturn(esCategory);
    Mockito.when(container.getEntitySet("Products")).thenReturn(esProduct);
    Mockito.when(typeProduct.getName()).thenReturn(fqnProduct.getName());
    Mockito.when(typeProduct.getNamespace()).thenReturn(fqnProduct.getNamespace());
    Mockito.when(typeProduct.getFullQualifiedName()).thenReturn(fqnProduct);
    Mockito.when(productsNavigation.getType()).thenReturn(typeProduct);

    try {
      // test and verify
      testUri.setEdm(mockEdm)
          .run("Products", "$expand=Category")
          .isKind(UriInfoKind.resource).goExpand()
          .first()
          .goPath().first()
          .isType(new FullQualifiedName("NS", "Category"), false);
      fail("Expected exception was not thrown.");
    } catch (final UriParserException e) {
      assertEquals("Navigation Property 'Category' not found in type 'NS.Products'.", e.getMessage());
    }
  }
}