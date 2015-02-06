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
package org.apache.olingo.client.core.v3;

import org.apache.olingo.client.api.edm.xml.EntityContainer;
import org.apache.olingo.client.api.edm.xml.EntityType;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MetadataTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  @Test
  public void parseWithEdm() {
    final Edm edm = getClient().getReader().readMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(edm);

    // 1. Complex
    final EdmComplexType responseStatus = edm.getComplexType(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "ContactDetails"));
    assertNotNull(responseStatus);
    assertTrue(responseStatus.getNavigationPropertyNames().isEmpty());
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String),
            responseStatus.getProperty("EmailBag").getType());

    // 2. Entity
    final EdmEntityType product = edm.getEntityType(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Product"));
    assertNotNull(product);
    assertFalse(product.getPropertyNames().isEmpty());
    assertFalse(product.getNavigationPropertyNames().isEmpty());

    final EdmNavigationProperty detail = product.getNavigationProperty("Detail");
    assertNotNull(detail);
    assertEquals("Product", detail.getPartner().getName());
    assertFalse(detail.isCollection());
    assertTrue(detail.isNullable());

    final EdmNavigationProperty relatedProducts = product.getNavigationProperty("RelatedProducts");
    assertNotNull(relatedProducts);
    assertEquals("RelatedProducts", relatedProducts.getPartner().getName());
    assertTrue(relatedProducts.isCollection());
    assertFalse(relatedProducts.isNullable());

    final EdmEntityType order = edm.getEntityType(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Order"));
    assertFalse(order.getPropertyNames().isEmpty());
    assertFalse(order.getNavigationPropertyNames().isEmpty());

    final EdmEntityType customer = edm.getEntityType(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Customer"));
    assertEquals(order, customer.getNavigationProperty("Orders").getType());

    // 3. Action
    final EdmAction sack = edm.getBoundAction(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Sack"),
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Employee"),
            false);
    assertNotNull(sack);
    assertTrue(sack.isBound());
    assertEquals(1, sack.getParameterNames().size());

    final EdmAction sack2 = edm.getBoundAction(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Sack"),
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Employee"),
            true);
    assertNull(sack2);

    final EdmFunction sack3 = edm.getBoundFunction(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Sack"),
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Employee"),
            false,
            null);
    assertNull(sack3);

    boolean found = false;
    for (EdmAction action : edm.getSchemas().get(0).getActions()) {
      if ("Sack".equals(action.getName()) && action.isBound()) {
        found = true;
      }
    }
    assertTrue(found);

    final EdmAction increaseSalaries = edm.getBoundAction(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "IncreaseSalaries"),
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "Employee"),
            true);
    assertNotNull(increaseSalaries);
    assertTrue(increaseSalaries.isBound());
    assertEquals(2, increaseSalaries.getParameterNames().size());

    // 4. EntityContainer
    final EdmEntityContainer container = edm.getEntityContainer(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "DefaultContainer"));
    assertNotNull(container);
    final EdmEntitySet logins = container.getEntitySet("Login");
    assertNotNull(logins);
    assertEquals(edm.getEntityType(new FullQualifiedName(container.getNamespace(), "Login")), logins.getEntityType());
    assertEquals(container.getEntitySet("Customer").getEntityContainer().getFullQualifiedName(),
            logins.getRelatedBindingTarget("Customer").getEntityContainer().getFullQualifiedName());
    assertEquals(container.getEntitySet("Customer").getName(), logins.getRelatedBindingTarget("Customer").getName());
    assertEquals(6, container.getFunctionImports().size());
    assertEquals(1, container.getActionImports().size());
    assertNotNull(container.getActionImports().iterator().next().getUnboundAction());
    assertEquals("ResetDataSource", container.getActionImports().iterator().next().getUnboundAction().getName());

    // 5. Operation
    final EdmFunctionImport funcImp = container.getFunctionImport("InStreamErrorGetCustomer");
    assertNotNull(funcImp);
    final EdmEntitySet returnedEntitySet = funcImp.getReturnedEntitySet();
    assertNotNull(returnedEntitySet);
    assertEquals("Customer", returnedEntitySet.getName());

    final EdmFunction function = funcImp.getUnboundFunction(null);
    assertNotNull(function);
    final EdmReturnType returnType = function.getReturnType();
    assertNotNull(returnType);
    assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer",
            returnType.getType().getFullQualifiedName().toString());
    assertTrue(returnType.isCollection());
  }

  @Test
  public void parseWithXMLMetadata() {
    final XMLMetadata metadata = getClient().getDeserializer(ODataFormat.XML).
            toMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    final EntityType order = metadata.getSchemas().get(0).getEntityType("Order");
    assertNotNull(order);
    assertEquals("Order", order.getName());
    assertFalse(order.getNavigationProperties().isEmpty());

    @SuppressWarnings("unchecked")
    final List<FunctionImport> functionImports = (List<FunctionImport>) metadata.getSchemas().get(0).
            getDefaultEntityContainer().getFunctionImports();
    int legacyGetters = 0;
    int legacyPosters = 0;
    int actions = 0;
    int functions = 0;
    for (FunctionImport functionImport : functionImports) {
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
    final XMLMetadata metadata = getClient().getDeserializer(ODataFormat.XML).
            toMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);

    final Schema first = metadata.getSchema("NorthwindModel");
    assertNotNull(first);

    final Schema second = metadata.getSchema("ODataWebV3.Northwind.Model");
    assertNotNull(second);

    final EntityContainer entityContainer = second.getDefaultEntityContainer();
    assertNotNull(entityContainer);
    assertEquals("NorthwindEntities", entityContainer.getName());
  }

  @Test
  public void complexAndEntityType() {
    final Edm metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    final EdmEntityContainer container = metadata.getEntityContainer(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "DefaultContainer"));
    assertNotNull(container);

    final EdmComplexType complex = metadata.getComplexType(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "ContactDetails"));
    assertNotNull(complex);
    assertFalse(complex.getPropertyNames().isEmpty());
    assertTrue(complex.getProperty("EmailBag").isCollection());

    final EdmEntityType entity = metadata.getEntityType(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "ProductReview"));
    assertNotNull(entity);
    assertFalse(entity.getPropertyNames().isEmpty());
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32),
            entity.getProperty("ProductId").getType());

    assertFalse(entity.getKeyPropertyRefs().isEmpty());
    assertNotNull("ProductId", entity.getKeyPropertyRef("ProductId").getName());
  }

  @Test
  public void functionImport() {
    final Edm metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);
    final EdmSchema schema = metadata.getSchemas().get(0);

    final Set<String> actionImports = new HashSet<String>();
    for (EdmAction info : schema.getActions()) {
      actionImports.add(info.getName());
    }
    final Set<String> expectedAI = new HashSet<String>(Arrays.asList(new String[] {
      "ResetDataSource",
      "IncreaseSalaries",
      "Sack",
      "GetComputer",
      "ChangeProductDimensions",
      "ResetComputerDetailsSpecifications"}));
    assertEquals(expectedAI, actionImports);
    final Set<String> functionImports = new HashSet<String>();
    for (EdmFunction info : schema.getFunctions()) {
      functionImports.add(info.getName());
    }
    final Set<String> expectedFI = new HashSet<String>(Arrays.asList(new String[] {
      "GetPrimitiveString",
      "GetSpecificCustomer",
      "GetCustomerCount",
      "GetArgumentPlusOne",
      "EntityProjectionReturnsCollectionOfComplexTypes",
      "InStreamErrorGetCustomer"}));
    assertEquals(expectedFI, functionImports);

    final EdmEntityContainer container = metadata.getEntityContainer(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "DefaultContainer"));
    assertNotNull(container);

    final EdmFunctionImport getArgumentPlusOne = container.getFunctionImport("GetArgumentPlusOne");
    assertNotNull(getArgumentPlusOne);
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32),
            getArgumentPlusOne.getUnboundFunction(null).getReturnType().getType());

    final EdmActionImport resetDataSource = container.getActionImport("ResetDataSource");
    assertNotNull(resetDataSource);
    assertTrue(resetDataSource.getUnboundAction().getParameterNames().isEmpty());
    assertNull(resetDataSource.getUnboundAction().getReturnType());

    final EdmEntityType computer = metadata.getEntityType(new FullQualifiedName(container.getNamespace(), "Computer"));
    assertNotNull(computer);

    final EdmAction getComputer = metadata.getBoundAction(
            new FullQualifiedName(container.getNamespace(), "GetComputer"),
            new FullQualifiedName(container.getNamespace(), computer.getName()),
            false);
    assertNotNull(getComputer);
    assertEquals(computer, getComputer.getParameter("computer").getType());
    assertEquals(computer, getComputer.getReturnType().getType());

    final EdmAction resetDataSource2 = metadata.getUnboundAction(
            new FullQualifiedName(container.getNamespace(), "ResetDataSource"));
    assertNotNull(resetDataSource2);
  }

  @Test
  public void navigation() {
    final Edm metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    final EdmEntityContainer container = metadata.getEntityContainer(
            new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService", "DefaultContainer"));
    assertNotNull(container);

    final EdmEntitySet customer = container.getEntitySet("Customer");
    assertNotNull(customer);

    final EdmBindingTarget order = customer.getRelatedBindingTarget("Orders");
    assertNotNull(order);
    assertTrue(order instanceof EdmEntitySet);

    final EdmBindingTarget customerBindingTarget = ((EdmEntitySet) order).getRelatedBindingTarget("Customer");
    assertEquals(customer.getEntityType().getName(), customerBindingTarget.getEntityType().getName());
  }
}
