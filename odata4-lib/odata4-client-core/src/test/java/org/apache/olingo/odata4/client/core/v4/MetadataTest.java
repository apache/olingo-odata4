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
package org.apache.olingo.odata4.client.core.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.apache.olingo.odata4.client.api.data.EdmSimpleType;
import org.apache.olingo.odata4.client.core.AbstractTest;
import org.apache.olingo.odata4.client.core.ODataV4Client;
import org.apache.olingo.odata4.client.core.edm.v4.FunctionImportImpl;
import org.apache.olingo.odata4.client.core.edm.v4.ActionImpl;
import org.apache.olingo.odata4.client.core.edm.v4.AnnotationImpl;
import org.apache.olingo.odata4.client.core.edm.v4.AnnotationsImpl;
import org.apache.olingo.odata4.client.core.edm.v4.ComplexTypeImpl;
import org.apache.olingo.odata4.client.core.edm.v4.EdmMetadataImpl;
import org.apache.olingo.odata4.client.core.edm.v4.EdmTypeImpl;
import org.apache.olingo.odata4.client.core.edm.v4.EntityContainerImpl;
import org.apache.olingo.odata4.client.core.edm.v4.EntitySetImpl;
import org.apache.olingo.odata4.client.core.edm.v4.EntityTypeImpl;
import org.apache.olingo.odata4.client.core.edm.v4.EnumTypeImpl;
import org.apache.olingo.odata4.client.core.edm.v4.FunctionImpl;
import org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl;
import org.apache.olingo.odata4.client.core.edm.v4.SingletonImpl;
import org.apache.olingo.odata4.client.core.edm.v4.annotation.Apply;
import org.apache.olingo.odata4.client.core.edm.v4.annotation.Collection;
import org.apache.olingo.odata4.client.core.edm.v4.annotation.ConstExprConstruct;
import org.apache.olingo.odata4.client.core.edm.v4.annotation.Path;
import org.apache.olingo.odata4.commons.api.edm.constants.StoreGeneratedPattern;
import org.junit.Test;

public class MetadataTest extends AbstractTest {

  @Override
  protected ODataV4Client getClient() {
    return v4Client;
  }

  @Test
  public void parse() {
    final EdmMetadataImpl metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(metadata);

    // 1. Enum
    final EnumTypeImpl responseEnumType = metadata.getSchema(0).getEnumType("ResponseType");
    assertNotNull(responseEnumType);
    assertEquals(6, responseEnumType.getMembers().size());
    assertEquals(3, responseEnumType.getMember("Accepted").getValue().intValue());
    assertEquals("Accepted", responseEnumType.getMember(3).getName());

    final EdmTypeImpl responseType = new EdmTypeImpl(metadata,
            "Microsoft.Exchange.Services.OData.Model.ResponseType");
    assertNotNull(responseType);
    assertFalse(responseType.isCollection());
    assertFalse(responseType.isSimpleType());
    assertTrue(responseType.isEnumType());
    assertFalse(responseType.isComplexType());
    assertFalse(responseType.isEntityType());

    // 2. Complex
    final ComplexTypeImpl responseStatus = metadata.getSchema(0).getComplexType("ResponseStatus");
    assertNotNull(responseStatus);
    assertTrue(responseStatus.getNavigationProperties().isEmpty());
    assertEquals(EdmSimpleType.DateTimeOffset,
            EdmSimpleType.fromValue(responseStatus.getProperty("Time").getType()));

    // 3. Entity
    final EntityTypeImpl user = metadata.getSchema(0).getEntityType("User");
    assertNotNull(user);
    assertEquals("Microsoft.Exchange.Services.OData.Model.Entity", user.getBaseType());
    assertFalse(user.getProperties().isEmpty());
    assertFalse(user.getNavigationProperties().isEmpty());
    assertEquals("Microsoft.Exchange.Services.OData.Model.Folder", user.getNavigationProperty("Inbox").getType());

    // 4. Action
    final List<ActionImpl> moves = metadata.getSchema(0).getActions("Move");
    assertFalse(moves.isEmpty());
    ActionImpl move = null;
    for (ActionImpl action : moves) {
      if ("Microsoft.Exchange.Services.OData.Model.EmailMessage".equals(action.getReturnType().getType())) {
        move = action;
      }
    }
    assertNotNull(move);
    assertTrue(move.isBound());
    assertEquals("bindingParameter", move.getEntitySetPath());
    assertEquals(2, move.getParameters().size());
    assertEquals("Microsoft.Exchange.Services.OData.Model.EmailMessage", move.getParameters().get(0).getType());

    // 5. EntityContainer
    final EntityContainerImpl container = metadata.getSchema(0).getEntityContainer();
    assertNotNull(container);
    final EntitySetImpl users = container.getEntitySet("Users");
    assertNotNull(users);
    assertEquals(metadata.getSchema(0).getNamespace() + "." + user.getName(), users.getEntityType());
    assertEquals(user.getNavigationProperties().size(), users.getNavigationPropertyBindings().size());
  }

  @Test
  public void demo() {
    final EdmMetadataImpl metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("demo-metadata.xml"));
    assertNotNull(metadata);

    assertFalse(metadata.getSchema(0).getAnnotationsList().isEmpty());
    AnnotationsImpl annots = metadata.getSchema(0).getAnnotationsList("ODataDemo.DemoService/Suppliers");
    assertNotNull(annots);
    assertFalse(annots.getAnnotations().isEmpty());
    assertEquals(ConstExprConstruct.Type.String,
            annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getConstExpr().getType());
    assertEquals("http://www.odata.org/",
            annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getConstExpr().getValue());
  }

  @Test
  public void multipleSchemas() {
    final EdmMetadataImpl metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);

    final SchemaImpl first = metadata.getSchema("NorthwindModel");
    assertNotNull(first);

    final SchemaImpl second = metadata.getSchema("ODataWebExperimental.Northwind.Model");
    assertNotNull(second);

    assertEquals(StoreGeneratedPattern.Identity,
            first.getEntityType("Category").getProperty("CategoryID").getStoreGeneratedPattern());

    final EntityContainerImpl entityContainer = second.getDefaultEntityContainer();
    assertNotNull(entityContainer);
    assertEquals("NorthwindEntities", entityContainer.getName());
    assertTrue(entityContainer.isLazyLoadingEnabled());
  }

  /**
   * Tests Example 85 from CSDL specification.
   */
  @Test
  public void fromdoc1() {
    final EdmMetadataImpl metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("fromdoc1-metadata.xml"));
    assertNotNull(metadata);

    assertFalse(metadata.getReferences().isEmpty());
    assertEquals("Org.OData.Measures.V1", metadata.getReferences().get(1).getIncludes().get(0).getNamespace());

    final EntityTypeImpl product = metadata.getSchema(0).getEntityType("Product");
    assertTrue(product.isHasStream());
    assertEquals("UoM.ISOCurrency", product.getProperty("Price").getAnnotation().getTerm());
    //assertEquals("Currency", product.getProperty("Price").getAnnotation().));
    assertEquals("Products", product.getNavigationProperty("Supplier").getPartner());

    final EntityTypeImpl category = metadata.getSchema(0).getEntityType("Category");
    final EdmTypeImpl type = new EdmTypeImpl(metadata, category.getNavigationProperty("Products").getType());
    assertNotNull(type);
    assertTrue(type.isCollection());
    assertFalse(type.isSimpleType());

    final ComplexTypeImpl address = metadata.getSchema(0).getComplexType("Address");
    assertFalse(address.getNavigationProperty("Country").getReferentialConstraints().isEmpty());
    assertEquals("Name",
            address.getNavigationProperty("Country").getReferentialConstraints().get(0).getReferencedProperty());

    final FunctionImpl productsByRating = metadata.getSchema(0).getFunctions("ProductsByRating").get(0);
    assertNotNull(productsByRating.getParameter("Rating"));
    assertEquals("Edm.Int32", productsByRating.getParameter("Rating").getType());
    assertEquals("Collection(ODataDemo.Product)", productsByRating.getReturnType().getType());

    final SingletonImpl contoso = metadata.getSchema(0).getEntityContainer().getSingleton("Contoso");
    assertNotNull(contoso);
    assertFalse(contoso.getNavigationPropertyBindings().isEmpty());
    assertEquals("Products", contoso.getNavigationPropertyBindings().get(0).getPath());

    final FunctionImportImpl functionImport = metadata.getSchema(0).getEntityContainer().
            getFunctionImport("ProductsByRating");
    assertNotNull(functionImport);
    assertEquals(metadata.getSchema(0).getNamespace() + "." + productsByRating.getName(),
            functionImport.getFunction());
  }

  /**
   * Tests Example 86 from CSDL specification.
   */
  @Test
  public void fromdoc2() {
    final EdmMetadataImpl metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("fromdoc2-metadata.xml"));
    assertNotNull(metadata);

    // Check displayName
    final AnnotationImpl displayName = metadata.getSchema(0).getAnnotationsList("ODataDemo.Supplier").
            getAnnotation("Vocabulary1.DisplayName");
    assertNotNull(displayName);
    assertNull(displayName.getConstExpr());
    assertNotNull(displayName.getDynExpr());

    assertTrue(displayName.getDynExpr() instanceof Apply);
    final Apply apply = (Apply) displayName.getDynExpr();
    assertEquals(Apply.CANONICAL_FUNCTION_CONCAT, apply.getFunction());
    assertEquals(3, apply.getParameters().size());

    final Path firstArg = new Path();
    firstArg.setValue("Name");
    assertEquals(firstArg, apply.getParameters().get(0));

    final ConstExprConstruct secondArg = new ConstExprConstruct();
    secondArg.setType(ConstExprConstruct.Type.String);
    secondArg.setValue(" in ");
    assertEquals(secondArg, apply.getParameters().get(1));

    final Path thirdArg = new Path();
    thirdArg.setValue("Address/CountryName");
    assertEquals(thirdArg, apply.getParameters().get(2));

    // Check Tags
    final AnnotationImpl tags = metadata.getSchema(0).getAnnotationsList("ODataDemo.Product").
            getAnnotation("Vocabulary1.Tags");
    assertNotNull(tags);
    assertNull(tags.getConstExpr());
    assertNotNull(tags.getDynExpr());

    assertTrue(tags.getDynExpr() instanceof Collection);
    final Collection collection = (Collection) tags.getDynExpr();
    assertEquals(1, collection.getItems().size());
    assertEquals(ConstExprConstruct.Type.String, ((ConstExprConstruct) collection.getItems().get(0)).getType());
    assertEquals("MasterData", ((ConstExprConstruct) collection.getItems().get(0)).getValue());
  }

  /**
   * Various annotation examples taken from CSDL specification.
   */
  @Test
  public void fromdoc3() {
    final EdmMetadataImpl metadata = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("fromdoc3-metadata.xml"));
    assertNotNull(metadata);
  }

}
