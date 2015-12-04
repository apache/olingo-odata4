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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * All Tests which involves the <code>Parser</code> implementation
 * (and with that also the <code>UriParseTreeVisitor</code>).
 */
public class ParserTest {

  /**
   * Test for EntitySet and NavigationProperty with same name defined in metadata.
   * (related to Olingo issue OLINGO-741)
   */
  @Test
  public void parseEntitySetAndNavigationPropertyWithSameName() throws Exception {
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
    Mockito.when(typeCategory.getProperty("Products")).thenReturn(productsNavigation);
    Mockito.when(container.getEntitySet("Category")).thenReturn(esCategory);
    Mockito.when(container.getEntitySet("Products")).thenReturn(esProduct);
    Mockito.when(productsType.getFullQualifiedName()).thenReturn(nameProducts);
    Mockito.when(productsType.getNamespace()).thenReturn("NS");
    Mockito.when(productsNavigation.getType()).thenReturn(productsType);

    // test and verify
    testUri.setEdm(mockEdm)
        .run("Category", "$expand=Products")
        .isKind(UriInfoKind.resource).goPath().goExpand()
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
          .isKind(UriInfoKind.resource).goPath().goExpand()
          .first()
          .goPath().first()
          .isType(new FullQualifiedName("NS", "Category"), false);
      fail("Expected exception was not thrown.");
    } catch (final UriParserException e) {
      assertEquals("NavigationProperty 'Category' not found in type 'NS.Products'", e.getMessage());
    }
  }
}