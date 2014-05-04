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
package org.apache.olingo.client.core.v4;

import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.Annotations;
import org.apache.olingo.client.api.edm.xml.v4.ComplexType;
import org.apache.olingo.client.api.edm.xml.v4.EntityContainer;
import org.apache.olingo.client.api.edm.xml.v4.EntityType;
import org.apache.olingo.client.api.edm.xml.v4.Function;
import org.apache.olingo.client.api.edm.xml.v4.FunctionImport;
import org.apache.olingo.client.api.edm.xml.v4.Schema;
import org.apache.olingo.client.api.edm.xml.v4.Singleton;
import org.apache.olingo.client.api.edm.xml.v4.XMLMetadata;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Apply;
import org.apache.olingo.client.api.edm.xml.v4.annotation.Collection;
import org.apache.olingo.client.api.edm.xml.v4.annotation.ConstantAnnotationExpression;
import org.apache.olingo.client.api.edm.xml.v4.annotation.TwoParamsOpDynamicAnnotationExpression;
import org.apache.olingo.client.api.edm.xml.v4.annotation.UrlRef;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.client.core.edm.xml.v4.annotation.ConstantAnnotationExpressionImpl;
import org.apache.olingo.client.core.edm.xml.v4.annotation.PathImpl;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmAnnotationsTarget;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmFunctionImportInfo;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmUrlRef;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MetadataTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  @Test
  public void parse() {
    final Edm edm = getClient().getReader().readMetadata(getClass().getResourceAsStream("metadata.xml"));
    assertNotNull(edm);

    // 1. Enum
    final EdmEnumType responseEnumType = edm.getEnumType(
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "ResponseType"));
    assertNotNull(responseEnumType);
    assertEquals(6, responseEnumType.getMemberNames().size());
    assertEquals("3", responseEnumType.getMember("Accepted").getValue());
    assertEquals(EdmTypeKind.ENUM, responseEnumType.getKind());

    // 2. Complex
    final EdmComplexType responseStatus = edm.getComplexType(
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "ResponseStatus"));
    assertNotNull(responseStatus);
    assertTrue(responseStatus.getNavigationPropertyNames().isEmpty());
    assertEquals("Recipient", responseStatus.getBaseType().getName());
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.DateTimeOffset),
            responseStatus.getProperty("Time").getType());

    // 3. Entity
    final EdmEntityType user = edm.getEntityType(
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "User"));
    assertNotNull(user);
    final EdmEntityType entity = edm.getEntityType(
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Entity"));
    assertEquals(entity, user.getBaseType());
    assertFalse(user.getPropertyNames().isEmpty());
    assertFalse(user.getNavigationPropertyNames().isEmpty());
    final EdmEntityType folder = edm.getEntityType(
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Folder"));
    assertEquals(folder, user.getNavigationProperty("Inbox").getType());

    // 4. Action
    final EdmAction move = edm.getBoundAction(
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Move"),
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Folder"),
            false);
    assertNotNull(move);
    assertTrue(move.isBound());
    assertEquals(2, move.getParameterNames().size());
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String),
            move.getParameter("DestinationId").getType());

    // 5. EntityContainer
    final EdmEntityContainer container = edm.getEntityContainer(
            new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "EntityContainer"));
    assertNotNull(container);
    final EdmEntitySet users = container.getEntitySet("Users");
    assertNotNull(users);
    assertEquals(edm.getEntityType(new FullQualifiedName(container.getNamespace(), "User")),
            users.getEntityType());
    assertEquals(container.getEntitySet("Folders"), users.getRelatedBindingTarget("Folders"));
  }

  @Test
  public void demo() {
    final XMLMetadata metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("demo-metadata.xml"));
    assertNotNull(metadata);

    assertFalse(metadata.getSchema(0).getAnnotationGroups().isEmpty());
    final Annotations annots = metadata.getSchema(0).getAnnotationGroup("ODataDemo.DemoService/Suppliers");
    assertNotNull(annots);
    assertFalse(annots.getAnnotations().isEmpty());
    assertEquals(ConstantAnnotationExpression.Type.String,
            annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getExpression().asConstant().getType());
    assertEquals("http://www.odata.org/",
            annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getExpression().asConstant().getValue());

    // Now let's test some edm:Annotations
    final Edm edm = getClient().getReader().
            readMetadata(getClass().getResourceAsStream("demo-metadata.xml"));
    assertNotNull(edm);

    final EdmSchema schema = edm.getSchema("ODataDemo");
    assertNotNull(schema);
    assertTrue(schema.getAnnotations().isEmpty());
    assertFalse(schema.getAnnotationGroups().isEmpty());

    final EdmAnnotations annotationGroup = schema.getAnnotationGroups().get(2);
    assertNotNull(annotationGroup);
    final EdmAnnotationsTarget annotationsTarget = annotationGroup.getTarget();
    assertNotNull(annotationsTarget);
    assertTrue(EdmAnnotationsTarget.TargetType.Property == annotationsTarget.getAnnotationsTargetType());
    assertEquals(new FullQualifiedName("ODataDemo.Product"), annotationsTarget.getAnnotationsTargetFQN());
    assertEquals("Name", annotationsTarget.getAnnotationsTargetPath());

    final EdmAnnotation annotation = annotationGroup.getAnnotations().get(0);
    assertNotNull(annotation);
    assertTrue(annotation.getExpression().isConstant());
    assertEquals("Edm.String", annotation.getExpression().asConstant().getValue().getTypeName());

    assertEquals(10, schema.getAnnotationGroups().get(3).getAnnotations().size());
  }

  @Test
  public void multipleSchemas() {
    final XMLMetadata metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);

    final Schema first = metadata.getSchema("NorthwindModel");
    assertNotNull(first);

    final Schema second = metadata.getSchema("ODataWebExperimental.Northwind.Model");
    assertNotNull(second);

    final EntityContainer entityContainer = second.getEntityContainer();
    assertNotNull(entityContainer);
    assertEquals("NorthwindEntities", entityContainer.getName());
    assertTrue(entityContainer.isLazyLoadingEnabled());
  }

  /**
   * Tests Example 85 from CSDL specification.
   */
  @Test
  public void fromdoc1() {
    final XMLMetadata metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("fromdoc1-metadata.xml"));
    assertNotNull(metadata);

    assertFalse(metadata.getReferences().isEmpty());
    assertEquals("Org.OData.Measures.V1", metadata.getReferences().get(1).getIncludes().get(0).getNamespace());

    final EntityType product = metadata.getSchema(0).getEntityType("Product");
    assertTrue(product.isHasStream());
    assertEquals("UoM.ISOCurrency", product.getProperty("Price").getAnnotations().get(0).getTerm());
    assertEquals("Products", product.getNavigationProperty("Supplier").getPartner());

    final EntityType category = metadata.getSchema(0).getEntityType("Category");
    assertNotNull(category);

    final ComplexType address = metadata.getSchema(0).getComplexType("Address");
    assertFalse(address.getNavigationProperty("Country").getReferentialConstraints().isEmpty());
    assertEquals("Name",
            address.getNavigationProperty("Country").getReferentialConstraints().get(0).getReferencedProperty());

    final Function productsByRating = metadata.getSchema(0).getFunctions("ProductsByRating").get(0);
    assertNotNull(productsByRating.getParameter("Rating"));
    assertEquals("Edm.Int32", productsByRating.getParameter("Rating").getType());
    assertEquals("Collection(ODataDemo.Product)", productsByRating.getReturnType().getType());

    final Singleton contoso = metadata.getSchema(0).getEntityContainer().getSingleton("Contoso");
    assertNotNull(contoso);
    assertFalse(contoso.getNavigationPropertyBindings().isEmpty());
    assertEquals("Products", contoso.getNavigationPropertyBindings().get(0).getPath());

    final FunctionImport functionImport = metadata.getSchema(0).getEntityContainer().
            getFunctionImport("ProductsByRating");
    assertNotNull(functionImport);
    assertEquals(metadata.getSchema(0).getNamespace() + "." + productsByRating.getName(),
            functionImport.getFunction());

    // Now let's go high-level
    final Edm edm = getClient().getReader().readMetadata(getClass().getResourceAsStream("fromdoc1-metadata.xml"));
    assertNotNull(edm);

    final EdmFunctionImportInfo fiInfo = edm.getServiceMetadata().getFunctionImportInfos().get(0);
    final EdmEntityContainer demoService = edm.getEntityContainer(
            new FullQualifiedName(metadata.getSchema(0).getNamespace(), fiInfo.getEntityContainerName()));
    assertNotNull(demoService);
    final EdmFunctionImport fi = demoService.getFunctionImport(fiInfo.getFunctionImportName());
    assertNotNull(fi);
    assertEquals(demoService.getEntitySet("Products"), fi.getReturnedEntitySet());

    final EdmFunction function = edm.getUnboundFunction(
            new FullQualifiedName(metadata.getSchema(0).getNamespace(), "ProductsByRating"), null);
    assertNotNull(function);
    assertEquals(function.getName(), fi.getUnboundFunction(null).getName());
    assertEquals(function.getNamespace(), fi.getUnboundFunction(null).getNamespace());
    assertEquals(function.getParameterNames(), fi.getUnboundFunction(null).getParameterNames());
    assertEquals(function.getReturnType().getType().getName(),
            fi.getUnboundFunction(null).getReturnType().getType().getName());
    assertEquals(function.getReturnType().getType().getNamespace(),
            fi.getUnboundFunction(null).getReturnType().getType().getNamespace());

    final EdmTypeDefinition weight = edm.getTypeDefinition(new FullQualifiedName("ODataDemo", "Weight"));
    assertNotNull(weight);
    assertEquals(EdmInt32.getInstance(), weight.getUnderlyingType());
    assertFalse(weight.getAnnotations().isEmpty());
    assertEquals("Kilograms", weight.getAnnotations().get(0).getExpression().asConstant().getValue().toString());
  }

  /**
   * Tests Example 86 from CSDL specification.
   */
  @Test
  public void fromdoc2() {
    final XMLMetadata metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("fromdoc2-metadata.xml"));
    assertNotNull(metadata);

    // Check displayName
    final Annotation displayName = metadata.getSchema(0).getAnnotationGroup("ODataDemo.Supplier").
            getAnnotation("Vocabulary1.DisplayName");
    assertNotNull(displayName);
    assertTrue(displayName.getExpression().isDynamic());

    assertTrue(displayName.getExpression().asDynamic().isApply());
    final Apply apply = displayName.getExpression().asDynamic().asApply();
    assertEquals(Constants.CANONICAL_FUNCTION_CONCAT, apply.getFunction());
    assertEquals(3, apply.getParameters().size());

    final PathImpl firstArg = new PathImpl();
    firstArg.setValue("Name");
    assertEquals(firstArg, apply.getParameters().get(0));

    final ConstantAnnotationExpression secondArg = new ConstantAnnotationExpressionImpl();
    secondArg.setType(ConstantAnnotationExpression.Type.String);
    secondArg.setValue(" in ");
    assertEquals(secondArg, apply.getParameters().get(1));

    final PathImpl thirdArg = new PathImpl();
    thirdArg.setValue("Address/CountryName");
    assertEquals(thirdArg, apply.getParameters().get(2));

    // Check Tags
    final Annotation tags = metadata.getSchema(0).getAnnotationGroup("ODataDemo.Product").
            getAnnotation("Vocabulary1.Tags");
    assertNotNull(tags);
    assertTrue(tags.getExpression().isDynamic());

    assertTrue(tags.getExpression().asDynamic().isCollection());
    final Collection collection = tags.getExpression().asDynamic().asCollection();
    assertEquals(1, collection.getItems().size());
    assertEquals(ConstantAnnotationExpression.Type.String, collection.getItems().get(0).asConstant().getType());
    assertEquals("MasterData", collection.getItems().get(0).asConstant().getValue());
  }

  /**
   * Various annotation examples taken from CSDL specification.
   */
  @Test
  public void fromdoc3() {
    final Edm edm = getClient().getReader().readMetadata(getClass().getResourceAsStream("fromdoc3-metadata.xml"));
    assertNotNull(edm);

    final EdmAnnotations group = edm.getSchema("Annotations").getAnnotationGroups().get(0);
    assertNotNull(group);

    final EdmAnnotation time1 = group.getAnnotations().get(0);
    assertEquals("Edm.TimeOfDay", time1.getExpression().asConstant().getValue().getTypeName());

    final EdmAnnotation time2 = group.getAnnotations().get(1);
    assertEquals("Edm.TimeOfDay", time2.getExpression().asConstant().getValue().getTypeName());
  }

  /**
   * Various annotation examples taken from CSDL specification.
   */
  @Test
  public void fromdoc4() {
    final XMLMetadata metadata = getClient().getDeserializer().
            toMetadata(getClass().getResourceAsStream("fromdoc4-metadata.xml"));
    assertNotNull(metadata);

    final Annotations group = metadata.getSchema(0).getAnnotationGroups().get(0);
    assertNotNull(group);

    Annotation annotation = group.getAnnotations().get(0);
    assertTrue(annotation.getExpression().isDynamic());
    assertTrue(annotation.getExpression().asDynamic().isCast());
    assertEquals("Edm.Decimal", annotation.getExpression().asDynamic().asCast().getType());

    annotation = group.getAnnotation("And");
    assertTrue(annotation.getExpression().isDynamic());
    assertTrue(annotation.getExpression().asDynamic().isTwoParamsOp());
    assertEquals(TwoParamsOpDynamicAnnotationExpression.Type.And,
            annotation.getExpression().asDynamic().asTwoParamsOp().getType());
    assertTrue(annotation.getExpression().asDynamic().asTwoParamsOp().getLeftExpression().isPath());

    annotation = group.getAnnotation("Vocab.Supplier");
    assertNotNull(annotation);
    assertTrue(annotation.getExpression().isDynamic());
    assertTrue(annotation.getExpression().asDynamic().isUrlRef());
    final UrlRef urlRef = annotation.getExpression().asDynamic().asUrlRef();
    assertTrue(urlRef.getValue().isDynamic());
    assertTrue(urlRef.getValue().asDynamic().isApply());

    // Now let's go high-level    
    final Edm edm = getClient().getReader().readMetadata(getClass().getResourceAsStream("fromdoc4-metadata.xml"));
    assertNotNull(edm);

    final EdmAnnotations edmGroup = edm.getSchemas().get(0).getAnnotationGroups().get(0);
    assertNotNull(edmGroup);

    EdmAnnotation edmAnnotation = edmGroup.getAnnotations().get(0);
    assertTrue(edmAnnotation.getExpression().isDynamic());
    assertTrue(edmAnnotation.getExpression().asDynamic().isCast());
    assertEquals(EdmDecimal.getInstance(), edmAnnotation.getExpression().asDynamic().asCast().getType());

    edmAnnotation = edmGroup.getAnnotations().get(1);
    assertTrue(edmAnnotation.getExpression().isDynamic());
    assertTrue(edmAnnotation.getExpression().asDynamic().isAnd());
    assertTrue(edmAnnotation.getExpression().asDynamic().asAnd().getLeftExpression().isPath());

    edmAnnotation = edmGroup.getAnnotations().get(edmGroup.getAnnotations().size() - 2);
    assertNotNull(edmAnnotation);
    assertTrue(edmAnnotation.getExpression().isDynamic());
    assertTrue(edmAnnotation.getExpression().asDynamic().isUrlRef());
    final EdmUrlRef edmUrlRef = edmAnnotation.getExpression().asDynamic().asUrlRef();
    assertTrue(edmUrlRef.getValue().isDynamic());
    assertTrue(edmUrlRef.getValue().asDynamic().isApply());
  }
}
