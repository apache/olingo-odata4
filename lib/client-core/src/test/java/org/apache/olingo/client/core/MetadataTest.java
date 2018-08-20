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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmPropertyValue;
import org.apache.olingo.commons.api.edm.annotation.EdmRecord;
import org.apache.olingo.commons.api.edm.annotation.EdmUrlRef;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlApply;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCollection;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
//CHECKSTYLE:OFF
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression.LogicalOrComparisonExpressionType;
//CHECKSTYLE:ON
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlRecord;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlUrlRef;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Test;

public class MetadataTest extends AbstractTest {

  @Test
  public void parse() {
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("metadata.xml"));
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
    assertFalse(user.getPropertyNames().isEmpty());
    assertFalse(user.getNavigationPropertyNames().isEmpty());

    final EdmEntityType entity = edm.getEntityType(
        new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Entity"));
    assertEquals(entity, user.getBaseType());
    assertFalse(entity.getPropertyNames().isEmpty());
    assertTrue(entity.getNavigationPropertyNames().isEmpty());

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
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("demo-metadata.xml"));
    assertNotNull(metadata);

    assertFalse(metadata.getSchema(0).getAnnotationGroups().isEmpty());
    final CsdlAnnotations annots = metadata.getSchema(0).getAnnotationGroup("ODataDemo.DemoService/Suppliers", null);
    assertNotNull(annots);
    assertFalse(annots.getAnnotations().isEmpty());
    assertEquals(CsdlConstantExpression.ConstantExpressionType.String,
        annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getExpression().asConstant().getType());
    assertEquals("http://www.odata.org/",
        annots.getAnnotation("Org.OData.Publication.V1.PrivacyPolicyUrl").getExpression().asConstant().getValue());

    // Now let's test some edm:Annotations
    final Edm edm = client.getReader().
        readMetadata(getClass().getResourceAsStream("demo-metadata.xml"));
    assertNotNull(edm);

    final EdmSchema schema = edm.getSchema("ODataDemo");
    assertNotNull(schema);
    assertTrue(schema.getAnnotations().isEmpty());
    assertFalse(schema.getAnnotationGroups().isEmpty());

    final EdmAnnotations annotationGroup = schema.getAnnotationGroups().get(2);
    assertNotNull(annotationGroup);
//TODO; Once there is a working getTarget method comment back in    
//    final EdmAnnotationsTarget annotationsTarget = annotationGroup.getTarget();
//    assertNotNull(annotationsTarget);
//    assertTrue(EdmAnnotationsTarget.TargetType.Property == annotationsTarget.getAnnotationsTargetType());
    assertEquals("ODataDemo.Product/Name", annotationGroup.getTargetPath());

    final EdmAnnotation annotation = annotationGroup.getAnnotations().get(0);
    assertNotNull(annotation);
    assertTrue(annotation.getExpression().isConstant());
    assertEquals("String", annotation.getExpression().asConstant().getExpressionName());

    assertEquals(10, schema.getAnnotationGroups().get(3).getAnnotations().size());
  }

  @Test
  public void multipleSchemas() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("northwind-metadata.xml"));
    assertNotNull(metadata);

    final CsdlSchema first = metadata.getSchema("NorthwindModel");
    assertNotNull(first);

    final CsdlSchema second = metadata.getSchema("ODataWebExperimental.Northwind.Model");
    assertNotNull(second);

    final CsdlEntityContainer entityContainer = second.getEntityContainer();
    assertNotNull(entityContainer);
    assertEquals("NorthwindEntities", entityContainer.getName());
  }

  @Test
  public void getContainerWithoutCallingGetSchemas() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("fromdoc1-metadata.xml"));

    Edm edm = client.getReader().readMetadata(metadata.getSchemaByNsOrAlias());

    assertNotNull(edm.getEntityContainer());
  }

  /**
   * Tests Example 85 from CSDL specification.
   */
  @Test
  public void fromdoc1() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("fromdoc1-metadata.xml"));
    assertNotNull(metadata);

    assertFalse(metadata.getReferences().isEmpty());
    assertEquals("Org.OData.Measures.V1", metadata.getReferences().get(1).getIncludes().get(0).getNamespace());

    final CsdlEntityType product = metadata.getSchema(0).getEntityType("Product");
    assertTrue(product.hasStream());
    assertEquals("UoM.ISOCurrency", product.getProperty("Price").getAnnotations().get(0).getTerm());
    assertEquals("Products", product.getNavigationProperty("Supplier").getPartner());

    final CsdlEntityType category = metadata.getSchema(0).getEntityType("Category");
    assertNotNull(category);

    final CsdlComplexType address = metadata.getSchema(0).getComplexType("Address");
    assertFalse(address.getNavigationProperty("Country").getReferentialConstraints().isEmpty());
    assertEquals("Name",
        address.getNavigationProperty("Country").getReferentialConstraints().get(0).getReferencedProperty());

    final CsdlFunction productsByRating = metadata.getSchema(0).getFunctions("ProductsByRating").get(0);
    assertNotNull(productsByRating.getParameter("Rating"));
    assertEquals("Edm.Int32", productsByRating.getParameter("Rating").getType());
    assertEquals("ODataDemo.Product", productsByRating.getReturnType().getType());
    assertTrue(productsByRating.getReturnType().isCollection());

    final CsdlSingleton contoso = metadata.getSchema(0).getEntityContainer().getSingleton("Contoso");
    assertNotNull(contoso);
    assertFalse(contoso.getNavigationPropertyBindings().isEmpty());
    assertEquals("Products", contoso.getNavigationPropertyBindings().get(0).getPath());

    final CsdlFunctionImport functionImport = metadata.getSchema(0).getEntityContainer().
        getFunctionImport("ProductsByRating");
    assertNotNull(functionImport);
    assertEquals(metadata.getSchema(0).getNamespace() + "." + productsByRating.getName(),
        functionImport.getFunction());

    // Now let's go high-level
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("fromdoc1-metadata.xml"));
    assertNotNull(edm);

    List<EdmSchema> schemaList = edm.getSchemas();
    assertNotNull(schemaList);
    assertEquals(1, schemaList.size());
    EdmSchema schema = schemaList.get(0);

    EdmEntityContainer demoService = schema.getEntityContainer();
    assertNotNull(demoService);
    EdmFunction function = schema.getFunctions().get(0);
      final EdmFunctionImport fi = demoService.getFunctionImport(function.getName());
      assertNotNull(fi);
      assertEquals(demoService.getEntitySet("Products"), fi.getReturnedEntitySet());

      final EdmFunction edmFunction =
          edm.getUnboundFunction(
              new FullQualifiedName(metadata.getSchema(0).getNamespace(), "ProductsByRating"), function
                  .getParameterNames());
      assertNotNull(edmFunction);
      assertEquals(edmFunction.getName(), fi.getUnboundFunction(function.getParameterNames()).getName());
      assertEquals(edmFunction.getNamespace(), fi.getUnboundFunction(function.getParameterNames()).getNamespace());
      assertEquals(edmFunction.getParameterNames(), fi.getUnboundFunction(function.getParameterNames())
          .getParameterNames());
      assertEquals(edmFunction.getReturnType().getType().getName(),
          fi.getUnboundFunction(function.getParameterNames()).getReturnType().getType().getName());
      assertEquals(edmFunction.getReturnType().getType().getNamespace(),
          fi.getUnboundFunction(function.getParameterNames()).getReturnType().getType().getNamespace());

    final EdmTypeDefinition weight = edm.getTypeDefinition(new FullQualifiedName("ODataDemo", "Weight"));
    assertNotNull(weight);
    assertEquals(EdmInt32.getInstance(), weight.getUnderlyingType());
    assertFalse(weight.getAnnotations().isEmpty());
    assertEquals("Kilograms", weight.getAnnotations().get(0).getExpression().asConstant().getValueAsString());
  }

  /**
   * Tests Example 86 from CSDL specification.
   */
  @Test
  public void fromdoc2() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML)
        .toMetadata(getClass().getResourceAsStream("fromdoc2-metadata.xml"));
    assertNotNull(metadata);

    // Check displayName
    final CsdlAnnotation displayName = metadata.getSchema(0).getAnnotationGroup("ODataDemo.Supplier", null).
        getAnnotation("Vocabulary1.DisplayName");
    assertNotNull(displayName);
    assertTrue(displayName.getExpression().isDynamic());

    assertTrue(displayName.getExpression().asDynamic().isApply());
    final CsdlApply apply = displayName.getExpression().asDynamic().asApply();
    assertEquals(Constants.CANONICAL_FUNCTION_CONCAT, apply.getFunction());
    assertEquals(3, apply.getParameters().size());

    CsdlPath path = (CsdlPath) apply.getParameters().get(0);
    assertEquals("Name", path.getValue());

    CsdlConstantExpression expression =
        (CsdlConstantExpression) apply.getParameters().get(1);
    assertEquals(" in ", expression.getValue());
    assertEquals(CsdlConstantExpression.ConstantExpressionType.String, expression.getType());

    CsdlPath thirdArg = (CsdlPath) apply.getParameters().get(2);
    assertEquals("Address/CountryName", thirdArg.getValue());

    // Check Tags
    final CsdlAnnotation tags = metadata.getSchema(0).getAnnotationGroup("ODataDemo.Product", null).
        getAnnotation("Vocabulary1.Tags");
    assertNotNull(tags);
    assertTrue(tags.getExpression().isDynamic());

    assertTrue(tags.getExpression().asDynamic().isCollection());
    final CsdlCollection collection = tags.getExpression().asDynamic().asCollection();
    assertEquals(1, collection.getItems().size());
    assertEquals(CsdlConstantExpression.ConstantExpressionType.String, collection.getItems().get(0).asConstant()
        .getType());
    assertEquals("MasterData", collection.getItems().get(0).asConstant().getValue());
  }

  /**
   * Various annotation examples taken from CSDL specification.
   */
  @Test
  public void fromdoc3() {
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("fromdoc3-metadata.xml"));
    assertNotNull(edm);

    final EdmAnnotations group = edm.getSchema("Annotations").getAnnotationGroups().get(0);
    assertNotNull(group);

    final EdmAnnotation time1 = group.getAnnotations().get(0);
    assertEquals("TimeOfDay", time1.getExpression().asConstant().getExpressionName());

    final EdmAnnotation time2 = group.getAnnotations().get(1);
    assertEquals("TimeOfDay", time2.getExpression().asConstant().getExpressionName());
  }

  /**
   * Various annotation examples taken from CSDL specification.
   */
  @Test
  public void fromdoc4() {
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).
        toMetadata(getClass().getResourceAsStream("fromdoc4-metadata.xml"));
    assertNotNull(metadata);

    final CsdlAnnotations group = metadata.getSchema(0).getAnnotationGroups().get(0);
    assertNotNull(group);

    CsdlAnnotation annotation = group.getAnnotations().get(0);
    assertTrue(annotation.getExpression().isDynamic());
    assertTrue(annotation.getExpression().asDynamic().isCast());
    assertEquals("Edm.Decimal", annotation.getExpression().asDynamic().asCast().getType());

    annotation = group.getAnnotation("And");
    assertTrue(annotation.getExpression().isDynamic());
    assertTrue(annotation.getExpression().asDynamic().isLogicalOrComparison());
    assertEquals(LogicalOrComparisonExpressionType.And,
        annotation.getExpression().asDynamic().asLogicalOrComparison().getType());
    assertTrue(annotation.getExpression().asDynamic().asLogicalOrComparison().getLeft().asDynamic().isPath());

    annotation = group.getAnnotation("Vocab.Supplier");
    assertNotNull(annotation);
    assertTrue(annotation.getExpression().isDynamic());
    assertTrue(annotation.getExpression().asDynamic().isUrlRef());
    final CsdlUrlRef urlRef = annotation.getExpression().asDynamic().asUrlRef();
    assertTrue(urlRef.getValue().isDynamic());
    assertTrue(urlRef.getValue().asDynamic().isApply());

    // Now let's go high-level
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("fromdoc4-metadata.xml"));
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
    assertTrue(edmAnnotation.getExpression().asDynamic().asAnd().getLeftExpression().asDynamic().isPath());

    edmAnnotation = edmGroup.getAnnotations().get(edmGroup.getAnnotations().size() - 2);
    assertNotNull(edmAnnotation);
    assertTrue(edmAnnotation.getExpression().isDynamic());
    assertTrue(edmAnnotation.getExpression().asDynamic().isUrlRef());
    final EdmUrlRef edmUrlRef = edmAnnotation.getExpression().asDynamic().asUrlRef();
    assertTrue(edmUrlRef.getValue().isDynamic());
    assertTrue(edmUrlRef.getValue().asDynamic().isApply());
  }

  @Test
  public void metadataWithCapabilities() throws Exception {
    InputStream input = getClass().getResourceAsStream("Metadata-With-Capabilities.xml");
    final XMLMetadata metadata = client.getDeserializer(ContentType.APPLICATION_XML).toMetadata(input);

    CsdlSchema schema = metadata.getSchema("Capabilities");
    assertNotNull(schema);
    assertEquals(23, schema.getTerms().size());

    final CsdlTerm deleteRestrictions = schema.getTerm("DeleteRestrictions");
    assertNotNull(deleteRestrictions);
    assertEquals("Capabilities.DeleteRestrictionsType", deleteRestrictions.getType());
  }
  
  @Test
  public void readPropertyAnnotations() {
    List<InputStream> streams = new ArrayList<InputStream>();
    streams.add(getClass().getResourceAsStream("VOC_Core.xml"));
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("edmxWithCoreAnnotation.xml"),
        streams);
    assertNotNull(edm);
    
    final EdmEntityType person = edm.getEntityType(
        new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Person"));
    assertNotNull(person);
    EdmProperty concurrency = (EdmProperty) person.getProperty("Concurrency");
    List<EdmAnnotation> annotations = concurrency.getAnnotations();
    for (EdmAnnotation annotation : annotations) {
      EdmTerm term = annotation.getTerm();
      assertNotNull(term);
      assertEquals("Computed", term.getName());
      assertEquals("Org.OData.Core.V1.Computed",
          term.getFullQualifiedName().getFullQualifiedNameAsString());
      assertEquals(1, term.getAnnotations().size());
    }
    EdmProperty userName = (EdmProperty) person.getProperty("UserName");
    List<EdmAnnotation> userNameAnnotations = userName.getAnnotations();
    for (EdmAnnotation annotation : userNameAnnotations) {
      EdmTerm term = annotation.getTerm();
      assertNotNull(term);
      assertEquals("Permissions", term.getName());
      assertEquals("Org.OData.Core.V1.Permissions",
          term.getFullQualifiedName().getFullQualifiedNameAsString());
      EdmExpression expression = annotation.getExpression();
      assertNotNull(expression);
      assertTrue(expression.isConstant());
      assertEquals("Org.OData.Core.V1.Permission/Read", expression.asConstant().getValueAsString());
      assertEquals("EnumMember", expression.getExpressionName());
    }
  }
  @Test
  public void testOLINGO1100() {
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("olingo1100.xml"));
    assertNotNull(edm);
    final EdmEntityContainer container = edm.getEntityContainer(
        new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "EntityContainer"));
    assertNotNull(container);
    final EdmEntitySet providers = container.getEntitySet("Provider");
    assertNotNull(providers);
    assertEquals(edm.getEntityType(new FullQualifiedName(container.getNamespace(), "Provider")),
        providers.getEntityType());
    assertEquals(container.getEntitySet("ProviderLicense"), providers.getRelatedBindingTarget("ProviderLicense"));
    assertNull(providers.getRelatedBindingTarget("ProviderLicensePractice"));
    assertNull(providers.getRelatedBindingTarget("Provider"));
    final EdmEntitySet providerLicenses = container.getEntitySet("ProviderLicense");
    assertEquals(edm.getEntityType(new FullQualifiedName(container.getNamespace(), "ProviderLicense")),
        providerLicenses.getEntityType());
    assertEquals(container.getEntitySet("ProviderLicensePractice"), 
        providerLicenses.getRelatedBindingTarget("ProviderLicensePractice"));
    assertNull(providerLicenses.getRelatedBindingTarget("ProviderLicense"));
    assertNull(providerLicenses.getRelatedBindingTarget("Provider"));
    final EdmEntitySet providerLicensePractices = container.getEntitySet("ProviderLicensePractice");
    assertNull(providerLicensePractices.getRelatedBindingTarget("ProviderLicensePractice"));
    assertNull(providerLicensePractices.getRelatedBindingTarget("Provider"));
    assertNull(providerLicenses.getRelatedBindingTarget("ProviderLicense"));
  }
  
  @Test
  public void issueOLINGO1232() {
    XMLMetadata xmlMetadata  = client.getDeserializer(ContentType.APPLICATION_XML).
    toMetadata(getClass().getResourceAsStream("caps.products.CatalogService_default.xml"));
    assertNotNull(xmlMetadata);
    assertEquals(94, xmlMetadata.getSchema(0).getAnnotationGroups().size());
    List<CsdlExpression> expressions = xmlMetadata.getSchema(0).getAnnotationGroups().get(0).
        getAnnotation("UI.LineItem").getExpression().asDynamic().asCollection().getItems();
    assertEquals(6, expressions.size());
    CsdlRecord record = (CsdlRecord) expressions.get(0);
    assertEquals("UI.DataField", record.getType());
    assertEquals(1, record.getAnnotations().size());
    assertEquals("Value", record.getPropertyValues().get(0).getProperty());
    assertEquals("image", record.getPropertyValues().get(0).getValue().asDynamic().asPath().getValue());
  }
  
  @Test
  public void readPropertyAnnotationsTest() {
    List<InputStream> streams = new ArrayList<InputStream>();
    streams.add(getClass().getResourceAsStream("VOC_Core.xml"));
    final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("edmxWithCsdlAnnotationPath.xml"),
        streams);
    assertNotNull(edm);
    
    final EdmEntityType person = edm.getEntityType(
        new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Person"));
    assertNotNull(person);
    EdmProperty userName = (EdmProperty) person.getProperty("UserName");
    List<EdmAnnotation> userNameAnnotations = userName.getAnnotations();
    for (EdmAnnotation annotation : userNameAnnotations) {
      EdmTerm term = annotation.getTerm();
      assertNotNull(term);
      assertEquals("Permissions", term.getName());
      assertEquals("Org.OData.Core.V1.Permissions",
          term.getFullQualifiedName().getFullQualifiedNameAsString());
      EdmExpression expression = annotation.getExpression();
      assertNotNull(expression);
      assertTrue(expression.isDynamic());
      assertEquals("AnnotationPath", expression.asDynamic().getExpressionName());
    }
  }
  
  @Test
 public void readAnnotationOnAnEntityType() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmEntityType entity = edm.getEntityTypeWithAnnotations(
       new FullQualifiedName("SEPMRA_SO_MAN2", "SEPMRA_C_CountryVHType"));
   assertEquals(1, entity.getAnnotations().size());
   assertNotNull(entity.getAnnotations().get(0).getTerm());
   assertEquals("HeaderInfo", entity.getAnnotations().get(0).getTerm().getName());
   assertNotNull(entity.getAnnotations().get(0).getExpression());
   
   EdmEntityType entity1 = edm.getEntityTypeWithAnnotations(
       new FullQualifiedName("SEPMRA_SO_MAN2", "SEPMRA_C_SalesOrderCustCntctVHType"));
   EdmAnnotation annotation = entity1.getAnnotations().get(0);
   assertNotNull(annotation);
   assertEquals(5, entity1.getAnnotations().size());
   assertEquals("FieldGroup", annotation.getTerm().getName());
   assertEquals("ContactPerson", annotation.getQualifier());
   EdmExpression expression = annotation.getExpression();
   assertNotNull(expression);
   assertTrue(expression.isDynamic());
   EdmRecord record = expression.asDynamic().asRecord();
   assertNotNull(record);
   assertEquals(2, record.asRecord().getPropertyValues().size());
   List<EdmPropertyValue> propertyValues = record.asRecord().getPropertyValues();
   assertEquals("Data", propertyValues.get(0).getProperty());
   assertTrue(propertyValues.get(0).getValue().isDynamic());
   List<EdmExpression> items = propertyValues.get(0).getValue().asDynamic().asCollection().getItems();
   assertEquals(4, items.size());
   assertEquals("Label", propertyValues.get(1).getProperty());
   assertEquals("Contact Person", propertyValues.get(1).getValue().asConstant().asPrimitive());
   
   assertEquals(1, entity1.getNavigationProperty("to_Customer").getAnnotations().size());
   EdmNavigationProperty navProperty = entity1.getNavigationProperty("to_Customer");
   assertEquals("ThingPerspective", navProperty.
       getAnnotations().get(0).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnAProperty() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmEntityType entity = edm.getEntityTypeWithAnnotations(
       new FullQualifiedName("SEPMRA_SO_MAN2", "I_DraftAdministrativeDataType"));
   EdmProperty property = (EdmProperty) entity.getProperty("DraftUUID");
   assertNotNull(property.getAnnotations());
   assertEquals(1, property.getAnnotations().size());
   assertEquals("UI.HeaderInfo", property.getAnnotations().get(0).getTerm().
       getFullQualifiedName().getFullQualifiedNameAsString());
 }
 
 @Test
 public void readAnnotationOnActionImport() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmEntityContainer container = edm.getEntityContainer();
   EdmActionImport actionImport = container.getActionImport("AIRTString");
   assertEquals(3, actionImport.getAnnotations().size());
   assertEquals("Description", actionImport.getAnnotations().get(0).getTerm().getName());
   assertEquals("HeaderInfo", actionImport.getAnnotations().get(2).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnASingleton() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmEntityContainer container = edm.getEntityContainer();
   EdmSingleton singleton = container.getSingleton("SINav");
   assertEquals(1, singleton.getAnnotations().size());
   assertEquals("HeaderInfo", singleton.getAnnotations().get(0).getTerm().getName());
   
   EdmEntityType singletonET = singleton.getEntityType();
   EdmProperty singlComplexProp = (EdmProperty)singletonET.getProperty("ComplexProperty");
   EdmComplexType singlCompType = (EdmComplexType) singlComplexProp.getTypeWithAnnotations();
   EdmNavigationProperty singlNavProp = (EdmNavigationProperty) singlCompType.
       getNavigationProperty("NavPropertyDraftAdministrativeDataType");
   assertEquals(1, singlNavProp.getAnnotations().size());
   assertEquals("AdditionalInfo", singlNavProp.getAnnotations().get(0).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnBoundFunction() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   List<String> parameterNames = new ArrayList<String>();
   EdmFunction function = edm.getBoundFunction(new FullQualifiedName("SEPMRA_SO_MAN2", "_FC_RTTimeOfDay_"), 
       new FullQualifiedName("Edm","TimeOfDay"), false, parameterNames);
   assertEquals(1, function.getAnnotations().size());
   assertEquals("HeaderInfo", function.getAnnotations().get(0).getTerm().getName());
   
   // Annotations on Bound Function parameter
   assertEquals(1, function.getParameter("ParameterTimeOfDay").getAnnotations().size());
   assertEquals("HeaderInfo", function.getParameter("ParameterTimeOfDay")
       .getAnnotations().get(0).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnSchema() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmSchema schema = edm.getSchema("sepmra_so_man2_anno_mdl.v1");
   assertNotNull(schema);
   assertEquals(112, schema.getAnnotationGroups().size());
   
   EdmAnnotations annotations = edm.getSchema("SEPMRA_SO_MAN2").getAnnotationGroups().get(22);
   assertEquals("SEPMRA_SO_MAN2.SEPMRA_C_SalesOrderCustCntctVHType", annotations.getTargetPath());
   assertEquals(1, annotations.getAnnotations().size());
   assertEquals("SelectionFields", annotations.getAnnotations()
       .get(0).getTerm().getName());
   assertTrue(annotations.getAnnotations().get(0).getExpression().isDynamic());
 }
 
 @Test
 public void readAnnotationOnContainer() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmEntityContainer container = edm.getEntityContainer();
   assertEquals(1, container.getAnnotations().size());
   assertEquals("HeaderInfo", container.getAnnotations().get(0).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnComplexType() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmComplexType complexType = edm.getComplexTypeWithAnnotations(
       new FullQualifiedName("SEPMRA_SO_MAN2", "CTPrim"));
   assertEquals(1, complexType.getAnnotations().size());
   assertEquals("HeaderInfo", complexType.getAnnotations().get(0).getTerm().getName());
   // Annotations on complex type property
   EdmProperty complexTypeProp = (EdmProperty) complexType.getProperty("PropertyInt16");
   assertEquals(1, complexTypeProp.getAnnotations().size());
   assertEquals("HeaderInfo", complexTypeProp.getAnnotations().get(0).getTerm().getName());
   // Annotations on complex type navigation property
   EdmNavigationProperty complexTypeNavProp = complexType.
       getNavigationProperty("NavPropertyDraftAdministrativeDataType");
   assertEquals(1, complexTypeNavProp.getAnnotations().size());
   assertEquals("HeaderInfo", complexTypeNavProp.getAnnotations().get(0).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnTypeDefinitions() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmTypeDefinition typeDefn = edm.getTypeDefinition(new FullQualifiedName("SEPMRA_SO_MAN2", "TDString"));
   assertEquals(1, typeDefn.getAnnotations().size());
   assertEquals("HeaderInfo", typeDefn.getAnnotations().get(0).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnBoundActions() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmAction action = edm.getBoundAction(new FullQualifiedName("SEPMRA_SO_MAN2", "BA_RTCountryVHType"), 
       new FullQualifiedName("SEPMRA_SO_MAN2","I_DraftAdministrativeDataType"), false);
   assertEquals(1, action.getAnnotations().size());
   assertEquals("HeaderInfo", action.getAnnotations().get(0).getTerm().getName());
   
   //Annotations on Bound Action parameter
   assertEquals(1, action.getParameter("ParameterCTPrim").getAnnotations().size());
   assertEquals("HeaderInfo", action.getParameter("ParameterCTPrim")
       .getAnnotations().get(0).getTerm().getName());
 }
 
 @Test
 public void readAnnotationOnEntitySet() {
   final Edm edm = fetchEdm();
   assertNotNull(edm);
   EdmEntityContainer container = edm.getEntityContainer();
   EdmEntitySet entitySet = container.getEntitySet("I_DraftAdministrativeData");
   assertEquals(1, entitySet.getAnnotations().size());
   assertEquals("HeaderInfo", entitySet.getAnnotations().get(0).getTerm().getName());
   
   
   
   EdmEntityType entityType50 = edm.getEntityTypeWithAnnotations(
       new FullQualifiedName("SEPMRA_SO_MAN2", "I_DraftAdministrativeDataType"));
   assertEquals(1, ((EdmProperty)entityType50.getProperty("DraftUUID")).getAnnotations().size());
   assertEquals("UI.HeaderInfo", ((EdmProperty)entityType50.getProperty("DraftUUID")).
       getAnnotations().get(0).getTerm().getFullQualifiedName().getFullQualifiedNameAsString());
   
   
   
   // Annotations on properties of entity type included in EntitySet
   EdmEntityType entityType3 = entitySet.getEntityTypeWithAnnotations();
   assertEquals(2, ((EdmProperty)entityType3.getProperty("DraftUUID")).getAnnotations().size());
   assertEquals("AdditionalInfo", ((EdmProperty)entityType3.getProperty("DraftUUID"))
       .getAnnotations().get(0).getTerm().getName());
   assertEquals("HeaderInfo", ((EdmProperty)entityType3.getProperty("DraftUUID"))
       .getAnnotations().get(1).getTerm().getName());
   
   // Annotations on navigation properties of entity type included in EntitySet
   EdmEntitySet entitySet1 = container.getEntitySet("SEPMRA_C_SalesOrderCustCntctVH");
   EdmEntityType entityType5 = entitySet1.getEntityTypeWithAnnotations();
   assertEquals(2, ((EdmNavigationProperty)entityType5.getNavigationProperty("to_Customer"))
       .getAnnotations().size());
   assertEquals("AdditionalInfo", ((EdmNavigationProperty)entityType5
       .getNavigationProperty("to_Customer"))
       .getAnnotations().get(0).getTerm().getName());
   assertEquals("HeaderInfo", ((EdmNavigationProperty)entityType5
       .getNavigationProperty("to_Customer"))
       .getAnnotations().get(1).getTerm().getName());
   
   
   
   EdmComplexType complexType = edm.getComplexTypeWithAnnotations(
       new FullQualifiedName("SEPMRA_SO_MAN2", "CTPrim"));
   EdmProperty complexTypeProp = (EdmProperty) complexType.getProperty("PropertyInt16");
   assertEquals(1, complexTypeProp.getAnnotations().size());
   assertEquals("HeaderInfo", complexTypeProp.getAnnotations().get(0).getTerm().getName());
   
   
   
   // Annotations on properties of complex properties of entity type included in EntitySet
   EdmProperty complexProp = (EdmProperty) entityType3.getProperty("ComplexProperty");
   EdmComplexType compType = (EdmComplexType) complexProp.getTypeWithAnnotations();
   EdmProperty prop = (EdmProperty) compType.getProperty("PropertyInt16");
   assertEquals(1, prop.getAnnotations().size());
   assertEquals("AdditionalInfo", prop.getAnnotations().get(0).getTerm().getName());
   
   // Annotations on navigation properties of complex properties of entity type included in EntitySet
   EdmNavigationProperty navProp = (EdmNavigationProperty) compType
       .getProperty("NavPropertyDraftAdministrativeDataType");
   assertEquals(1, navProp.getAnnotations().size());
   assertEquals("AdditionalInfo", navProp.getAnnotations().get(0).getTerm().getName());
 }
 
 private Edm fetchEdm() {
   List<InputStream> streams = new ArrayList<InputStream>();
   streams.add(getClass().getResourceAsStream("annotations.xml"));
   streams.add(getClass().getResourceAsStream("VOC_Core.xml"));
   streams.add(getClass().getResourceAsStream("UI.xml"));
   final Edm edm = client.getReader().readMetadata(getClass().getResourceAsStream("$metadata.xml"),
       streams);
  return edm;
 }
}
