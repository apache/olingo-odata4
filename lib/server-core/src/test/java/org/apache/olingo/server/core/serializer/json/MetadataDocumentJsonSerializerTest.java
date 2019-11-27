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
package org.apache.olingo.server.core.serializer.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmConstantExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlReferentialConstraint;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlAnnotationPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlApply;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCast;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCollection;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlIf;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlIsOf;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLabeledElement;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLabeledElementReference;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.
CsdlLogicalOrComparisonExpression.LogicalOrComparisonExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlNavigationPropertyPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlNull;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlRecord;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlUrlRef;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.commons.api.edmx.EdmxReferenceIncludeAnnotation;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ServiceMetadataImpl;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetadataDocumentJsonSerializerTest {

  private static ODataSerializer serializer;
  
  @BeforeClass
  public static void init() throws SerializerException {
    serializer = OData.newInstance().createSerializer(ContentType.APPLICATION_JSON);
  }
  
  @Test
  public void writeMetadataWithEmptyMockedEdm() throws Exception {
    final Edm edm = mock(Edm.class);
    ServiceMetadata metadata = mock(ServiceMetadata.class);
    when(metadata.getEdm()).thenReturn(edm);

    assertEquals("{\"$Version\":\"4.01\"}",
        IOUtils.toString(serializer.metadataDocument(metadata).getContent()));
  }
  
  @Test
  public void writeEdmxWithLocalTestEdm() throws Exception {
    List<EdmxReference> edmxReferences = new ArrayList<EdmxReference>();
    EdmxReference reference = new EdmxReference(URI.create("http://example.com"));
    edmxReferences.add(reference);

    EdmxReference referenceWithInclude = new EdmxReference(
        URI.create("http://localhost/odata/odata/v4.0/referenceWithInclude"));
    EdmxReferenceInclude include = new EdmxReferenceInclude("Org.OData.Core.V1", "Core");
    referenceWithInclude.addInclude(include);
    edmxReferences.add(referenceWithInclude);

    EdmxReference referenceWithTwoIncludes = new EdmxReference(
        URI.create("http://localhost/odata/odata/v4.0/referenceWithTwoIncludes"));
    referenceWithTwoIncludes.addInclude(new EdmxReferenceInclude("Org.OData.Core.2", "Core2"));
    referenceWithTwoIncludes.addInclude(new EdmxReferenceInclude("Org.OData.Core.3", "Core3"));
    edmxReferences.add(referenceWithTwoIncludes);

    EdmxReference referenceWithIncludeAnnos = new EdmxReference(
        URI.create("http://localhost/odata/odata/v4.0/referenceWithIncludeAnnos"));
    referenceWithIncludeAnnos.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("TermNs.2", "Q.2", "TargetNS.2"));
    referenceWithIncludeAnnos.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("TermNs.3", "Q.3", "TargetNS.3"));
    edmxReferences.add(referenceWithIncludeAnnos);

    EdmxReference referenceWithAll = new EdmxReference(
        URI.create("http://localhost/odata/odata/v4.0/referenceWithAll"));
    referenceWithAll.addInclude(new EdmxReferenceInclude("ReferenceWithAll.1", "Core1"));
    referenceWithAll.addInclude(new EdmxReferenceInclude("ReferenceWithAll.2", "Core2"));
    referenceWithAll.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermNs.4", "Q.4", "TargetNS.4"));
    referenceWithAll.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermNs.5", "Q.5", "TargetNS.5"));
    edmxReferences.add(referenceWithAll);

    EdmxReference referenceWithAllAndNull = new EdmxReference(
        URI.create("http://localhost/odata/odata/v4.0/referenceWithAllAndNull"));
    referenceWithAllAndNull.addInclude(new EdmxReferenceInclude("referenceWithAllAndNull.1"));
    referenceWithAllAndNull.addInclude(new EdmxReferenceInclude("referenceWithAllAndNull.2", null));
    referenceWithAllAndNull.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermNs.4"));
    referenceWithAllAndNull.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermAndNullNs.5", "Q.5", null));
    referenceWithAllAndNull.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermAndNullNs.6", null, "TargetNS"));
    referenceWithAllAndNull.addIncludeAnnotation(
        new EdmxReferenceIncludeAnnotation("ReferenceWithAllTermAndNullNs.7", null, null));
    edmxReferences.add(referenceWithAllAndNull);

    ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
    final Edm edm = mock(Edm.class);
    when(serviceMetadata.getEdm()).thenReturn(edm);
    when(serviceMetadata.getReferences()).thenReturn(edmxReferences);

    InputStream metadata = serializer.metadataDocument(serviceMetadata).getContent();
    assertNotNull(metadata);
    final String metadataString = IOUtils.toString(metadata);
    // edmx reference
    assertTrue(metadataString.contains(
        "\"$Reference\":{\"http://example.com\":{},"));
    assertTrue(metadataString.contains("\"http://localhost/odata/odata/v4.0/referenceWithInclude\":"
        + "{\"$Include\":[{\"$Namespace\":\"Org.OData.Core.V1\",\"$Alias\":\"Core\"}]}"));
    assertTrue(metadataString.contains(
        "\"http://localhost/odata/odata/v4.0/referenceWithTwoIncludes\":"
        + "{\"$Include\":["
        + "{\"$Namespace\":\"Org.OData.Core.2\",\"$Alias\":\"Core2\"},"
        + "{\"$Namespace\":\"Org.OData.Core.3\",\"$Alias\":\"Core3\"}]}"));
    assertTrue(metadataString.contains(
        "\"http://localhost/odata/odata/v4.0/referenceWithIncludeAnnos\":"
        + "{\"$IncludeAnnotations\":"
        + "[{\"$TermNamespace\":\"TermNs.2\",\"$Qualifier\":\"Q.2\","
        + "\"$TargetNamespace\":\"TargetNS.2\"},"
        + "{\"$TermNamespace\":\"TermNs.3\",\"$Qualifier\":\"Q.3\","
        + "\"$TargetNamespace\":\"TargetNS.3\"}]}"));
    assertTrue(metadataString.contains(
        "\"http://localhost/odata/odata/v4.0/referenceWithAll\":"
        + "{\"$Include\":[{\"$Namespace\":\"ReferenceWithAll.1\","
        + "\"$Alias\":\"Core1\"},"
        + "{\"$Namespace\":\"ReferenceWithAll.2\",\"$Alias\":\"Core2\"}],"
        + "\"$IncludeAnnotations\":"
        + "[{\"$TermNamespace\":\"ReferenceWithAllTermNs.4\",\"$Qualifier\":\"Q.4\","
        + "\"$TargetNamespace\":\"TargetNS.4\"},"
        + "{\"$TermNamespace\":\"ReferenceWithAllTermNs.5\",\"$Qualifier\":\"Q.5\","
        + "\"$TargetNamespace\":\"TargetNS.5\"}]}"));
    assertTrue(metadataString.contains(
        "\"http://localhost/odata/odata/v4.0/referenceWithAllAndNull\":"
        + "{\"$Include\":[{\"$Namespace\":\"referenceWithAllAndNull.1\"},"
        + "{\"$Namespace\":\"referenceWithAllAndNull.2\"}],\"$IncludeAnnotations\":"
        + "[{\"$TermNamespace\":\"ReferenceWithAllTermNs.4\"},"
        + "{\"$TermNamespace\":\"ReferenceWithAllTermAndNullNs.5\",\"$Qualifier\":\"Q.5\"},"
        + "{\"$TermNamespace\":\"ReferenceWithAllTermAndNullNs.6\","
        + "\"$TargetNamespace\":\"TargetNS\"},"
        + "{\"$TermNamespace\":\"ReferenceWithAllTermAndNullNs.7\"}]}"));
  }
  
  /** Test if annotations on EnumType Members are added as children of the Member element
   *  in compliance with OData v4.01, section 10
   */
  @Test
  public void testAnnotationsNestedInEnumMembers() throws Exception {
    // Create mock schema
    EdmSchema schema = mock(EdmSchema.class);
    when(schema.getNamespace()).thenReturn("MyNamespace");
    Edm edm = mock(Edm.class);
    when(edm.getSchemas()).thenReturn(Arrays.asList(schema));
    
    // create mock metadata
    ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
    when(serviceMetadata.getEdm()).thenReturn(edm);
    
    // add mock enums to schema
    EdmEnumType enumType = mock(EdmEnumType.class);
    when(schema.getEnumTypes()).thenReturn(Arrays.asList(enumType));
    when(enumType.getName()).thenReturn("MyEnum");
    when(enumType.getKind()).thenReturn(EdmTypeKind.ENUM);
    EdmPrimitiveType int32Type = OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32);
    when(enumType.getUnderlyingType()).thenReturn(int32Type);
    
    // mock enum member values
    when(enumType.getMemberNames()).thenReturn(Collections.singletonList("MyMember"));
    EdmMember member = mock(EdmMember.class);
    when(enumType.getMember("MyMember")).thenReturn(member);
    when(member.getName()).thenReturn("MyMember");
    when(member.getValue()).thenReturn("0");
    
    EdmAnnotation annotation = mock(EdmAnnotation.class);
    when(member.getAnnotations()).thenReturn(Collections.singletonList(annotation));
    when(annotation.getQualifier()).thenReturn("Core.Description");
    EdmConstantExpression expression = mock(EdmConstantExpression.class);
    when(expression.isConstant()).thenReturn(true);
    when(expression.asConstant()).thenReturn(expression);
    when(expression.getExpressionType()).thenReturn(EdmExpressionType.String);
    when(expression.getExpressionName()).thenReturn("String");
    when(expression.getValueAsString()).thenReturn("MyDescription");
    when(annotation.getExpression()).thenReturn(expression);
    
    InputStream metadata = serializer.metadataDocument(serviceMetadata).getContent();
    assertNotNull(metadata);
    String metadataString = IOUtils.toString(metadata);
    
    
    assertTrue(metadataString.contains(
        "{\"$Version\":\"4.01\","
        + "\"MyNamespace\":{\"MyEnum\":"
        + "{\"$Kind\":\"EnumType\",\"$IsFlags\":false,"
        + "\"$UnderlyingType\":\"Edm.Int32\",\"MyMember\":\"0\","
        + "\"MyMember#Core.Description\":\"MyDescription\"}}}"));

  }
  
  /** Writes simplest (empty) Schema. */
  @Test
  public void writeMetadataWithEmptySchema() throws Exception {
    EdmSchema schema = mock(EdmSchema.class);
    when(schema.getNamespace()).thenReturn("MyNamespace");
    Edm edm = mock(Edm.class);
    when(edm.getSchemas()).thenReturn(Arrays.asList(schema));
    ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
    when(serviceMetadata.getEdm()).thenReturn(edm);

    InputStream metadata = serializer.metadataDocument(serviceMetadata).getContent();
    assertNotNull(metadata);
    assertEquals("{\"$Version\":\"4.01\",\"MyNamespace\":{}}",
        IOUtils.toString(metadata));
  }
  
  @Test(expected=SerializerException.class)
  public void testNullMetadata() throws Exception {
    serializer.metadataDocument(null).getContent();
  }
  
  @Test(expected=SerializerException.class)
  public void testNullEdm() throws Exception {
    ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
    when(serviceMetadata.getEdm()).thenReturn(null);
    serializer.metadataDocument(serviceMetadata).getContent();
  }
  
  @Test
  public void writeMetadataWithTypeDefinitions() throws Exception {
    EdmSchema schema = mock(EdmSchema.class);
    when(schema.getNamespace()).thenReturn("MyNamespace");
    Edm edm = mock(Edm.class);
    when(edm.getSchemas()).thenReturn(Arrays.asList(schema));
    EdmTypeDefinition typeDefinition = mock(EdmTypeDefinition.class);
    when (schema.getTypeDefinitions()).thenReturn(Arrays.asList(typeDefinition));
    when(typeDefinition.getMaxLength()).thenReturn(10);
    when(typeDefinition.getScale()).thenReturn(2);
    when(typeDefinition.getPrecision()).thenReturn(10);
    when(typeDefinition.getSrid()).thenReturn(SRID.valueOf("123"));
    when(typeDefinition.getName()).thenReturn("MyTypeDefinition");
    when(typeDefinition.getKind()).thenReturn(EdmTypeKind.DEFINITION);
    EdmPrimitiveType int32Type = OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32);
    when(typeDefinition.getUnderlyingType()).thenReturn(int32Type);
    
    EdmAnnotation annotation = mock(EdmAnnotation.class);
    when(typeDefinition.getAnnotations()).thenReturn(Collections.singletonList(annotation));
    EdmTerm term = mock(EdmTerm.class);
    when(term.getName()).thenReturn("Unit");
    when(term.getFullQualifiedName()).thenReturn(new FullQualifiedName("Measures", "Unit"));
    when(annotation.getTerm()).thenReturn(term);
    EdmConstantExpression expression = mock(EdmConstantExpression.class);
    when(expression.isConstant()).thenReturn(true);
    when(expression.asConstant()).thenReturn(expression);
    when(expression.getExpressionType()).thenReturn(EdmExpressionType.String);
    when(expression.getExpressionName()).thenReturn("String");
    when(expression.getValueAsString()).thenReturn("Centimeters");
    when(annotation.getExpression()).thenReturn(expression);
    
    ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
    when(serviceMetadata.getEdm()).thenReturn(edm);
    
    InputStream metadata = serializer.metadataDocument(serviceMetadata).getContent();
    assertNotNull(metadata);
    String metadataStr = IOUtils.toString(metadata);
    assertEquals("{\"$Version\":\"4.01\","
        + "\"MyNamespace\":"
        + "{\"MyTypeDefinition\":{"
        + "\"$Kind\":\"DEFINITION\","
        + "\"$UnderlyingType\":\"Edm.Int32\","
        + "\"$MaxLength\":\"10\",\"$Precision\":\"10\","
        + "\"$Scale\":\"2\",\"$SRID\":\"123\","
        + "\"@Measures.Unit\":\"Centimeters\"}}}",
        metadataStr);
  }
  
  @Test
  public void aliasTest() throws Exception {
    String metadata = localMetadata();
    assertTrue(metadata.contains("\"ENString\":{\"$Kind\":\"EnumType\",\"$IsFlags\":true,"
        + "\"$UnderlyingType\":\"Edm.Int16\",\"String1\":\"1\","
        + "\"String1@Core.Description#Target\":\"Description of Enum Member\"}"));
    assertTrue(metadata.contains("\"ETAbstract\":{\"$Kind\":\"EntityType\",\"$Abstract\":true,"
        + "\"PropertyString\":{\"$Type\":\"Edm.String\"},\"NavPropertyETTwoKeyNavOne\":"
        + "{\"$Kind\":\"NavigationProperty\",\"$Type\":\"Alias.ETTwoKeyNavOne\"}},"
        + "\"ETAbstractBase\":{\"$Kind\":\"EntityType\",\"$BaseType\":\"Alias.ETAbstract\","
        + "\"$Key\":[\"PropertyInt16\"],\"PropertyInt16\":{\"$Type\":\"Edm.Int16\","
        + "\"$Nullable\":false,\"@Core.Description#Target\":\"Description of Type\"},"
        + "\"@Core.Description#Target\":\"Description of Type\"}"));
    assertTrue(metadata.contains("\"CTTwoPrim\":{\"$Kind\":\"ComplexType\","
        + "\"$Abstract\":true,\"PropertyInt16\":"
        + "{\"$Type\":\"Edm.Int16\",\"$Nullable\":false,"
        + "\"@Core.Description#Target\":\"Description of Type\"},"
        + "\"PropertyString\":{\"$Type\":\"Edm.String\"}},"
        + "\"CTTwoPrimBase\":{\"$Kind\":\"ComplexType\",\"$BaseType\":\"Alias.CTTwoPrim\","
        + "\"@Core.Description#Target\":\"Description of Complex Type\"}"));
    assertTrue(metadata.contains("\"ET\":{\"$Kind\":\"EntityType\","
        + "\"$Key\":[{\"EntityInfoID\":\"Info/ID\"},\"name\"],"
        + "\"name\":{\"$Type\":\"Edm.String\""
        + "},\"Info\":"
        + "{\"$Type\":\"Alias.CTEntityInfo\"},"
        + "\"NavPropertyETOne\":{\"$Kind\":\"NavigationProperty\","
        + "\"$Type\":\"Alias.ETOne\"}}"));
    assertTrue(metadata.contains("\"BAETTwoKeyNavRTETTwoKeyNavParam\":"
        + "[{\"$Kind\":\"Action\",\"$EntitySetPath\":\"BindingParam/NavPropertyETTwoKeyNavOne\","
        + "\"$IsBound\":true,\"$Parameter\":[{\"$Name\":\"BindingParam\",\"$Type\":\"Alias.ETTwoKeyNav\"},"
        + "{\"$Name\":\"PropertyComp\",\"$Type\":\"Alias.CTPrimComp\"}],\"$ReturnType\":"
        + "{\"$Type\":\"Alias.ETTwoKeyNav\",\"$Collection\":true}},{\"$Kind\":\"Action\","
        + "\"$EntitySetPath\":\"BindingParam/NavPropertyET\",\"$IsBound\":true,\"$Parameter\":"
        + "[{\"$Name\":\"BindingParam\",\"$Type\":\"Alias.ET\"}],\"$ReturnType\":{\"$Type\":"
        + "\"Alias.ET\",\"$Nullable\":false}},{\"$Kind\":\"Action\",\"$IsBound\":false,"
        + "\"$Parameter\":[{\"$Name\":\"PropertyComp\",\"$Type\":\"Alias.CTPrimComp\"}],"
        + "\"$ReturnType\":{\"$Type\":\"Alias.ET\",\"$Nullable\":false}}]"));
    assertTrue(metadata.contains("\"UARTPrimParam\":[{\"$Kind\":\"Action\","
        + "\"$IsBound\":false,\"$Parameter\":[{\"$Name\":\"ParameterInt16\","
        + "\"$Type\":\"Edm.Int16\"}],\"$ReturnType\":{\"$Type\":\"Edm.String\"}}]"));
    assertTrue(metadata.contains("\"UFNRTInt16\":"
        + "[{\"$Kind\":\"Function\","
        + "\"$ReturnType\":{\"$Type\":\"Edm.Int16\"}}]"));
    assertTrue(metadata.contains("\"BFETTwoKeyNavRTETTwoKeyNavParam\":"
        + "[{\"$Kind\":\"Function\",\"$EntitySetPath\":"
        + "\"BindingParam/NavPropertyETTwoKeyNavOne\",\"$IsBound\":true,"
        + "\"$IsComposable\":true,\"$Parameter\":[{\"$Name\":\"BindingParam\","
        + "\"$Type\":\"Alias.ETTwoKeyNav\"},{\"$Name\":\"PropertyComp\","
        + "\"$Type\":\"Alias.CTPrimComp\"}],\"$ReturnType\":{\"$Type\":"
        + "\"Alias.ETTwoKeyNav\",\"$Collection\":true}},{\"$Kind\":\"Function\","
        + "\"$EntitySetPath\":\"BindingParam/NavPropertyET\",\"$IsBound\":true,"
        + "\"$Parameter\":[{\"$Name\":\"BindingParam\",\"$Type\":\"Alias.ET\"}],"
        + "\"$ReturnType\":{\"$Type\":\"Alias.ET\",\"$Nullable\":false}}]"));
    assertTrue(metadata.contains("\"term\":{\"$Kind\":\"Term\",\"$Type\":\"Edm.String\"},"
        + "\"Term1\":{\"$Kind\":\"Term\",\"$Type\":\"Edm.String\"},"
        + "\"Term2\":{\"$Kind\":\"Term\",\"$Type\":\"Edm.String\",\"$Nullable\":false,"
        + "\"$DefaultValue\":\"default\",\"$MaxLength\":1,"
        + "\"$Precision\":2,\"$Scale\":3},"
        + "\"Term3\":{\"$Kind\":\"Term\",\"$Type\":\"Edm.String\","
        + "\"$AppliesTo\":\"Property EntitySet Schema\"},"
        + "\"Term4\":{\"$Kind\":\"Term\",\"$Type\":\"Edm.String\",\"$BaseTerm\":\"Alias.Term1\"}"));
    assertTrue(metadata.contains("\"ESTwoKeyNav\":{\"$Kind\":\"EntitySet\","
        + "\"$Type\":\"Alias.ETTwoKeyNav\",\"$NavigationPropertyBinding\":{"
        + "\"NavPropertyETTwoKeyNavOne/namespace.ETOne/NavPropertyET\":\"ES\","
        + "\"NavPropertyETOne\":\"ESOne\"}}"));
    assertTrue(metadata.contains("\"SIBinding\":{\"$Kind\":\"Singleton\","
        + "\"$Type\":\"Alias.ET\",\"$NavigationPropertyBinding\":{\"NavPropertyETOne\":\"ESOne\"}}"));
    assertTrue(metadata.contains("\"AIRTPrimParam\":{\"$Kind\":\"ActionImport\","
        + "\"$Action\":\"Alias.UARTPrimParam\",\"$EntitySet\":\"Alias.ESTwoKeyNav\"}"));
    assertTrue(metadata.contains("\"FINRTInt16\":{\"$Kind\":\"FunctionImport\","
        + "\"$Function\":\"Alias.UFNRTInt16\",\"$EntitySet\":\"Alias.ESTwoKeyNavOne\","
        + "\"$IncludeInServiceDocument\":true}"));
    assertTrue(metadata.contains("\"ETTwoKeyNavOne\":{\"$Kind\":\"EntityType\","
        + "\"$HasStream\":true,\"$BaseType\":\"Alias.ETOne\","
        + "\"PropertyString\":{\"$Type\":\"Edm.String\"},"
        + "\"NavPropertyETAbstract\":{\"$Kind\":\"NavigationProperty\","
        + "\"$Type\":\"Alias.ETAbstract\",\"$Collection\":true,"
        + "\"$Nullable\":false,\"$Partner\":"
        + "\"NavPropertyETTwoKeyNavOne\",\"$ContainsTarget\":true,\"$ReferentialConstraint\":"
        + "{\"PropertyString\":\"PropertyString\"}}}"));
    assertTrue(metadata.contains("\"$Annotations\":{\"Alias.ETAbstract#Tablett\":"
        + "{\"@ns.term#T1\":{\"$Binary\":\"qrvM3e7_\"},"
        + "\"@ns.term#T2\":true,"
        + "\"@ns.term#T3\":{\"$Date\":\"2012-02-29\"},"
        + "\"@ns.term#T4\":{\"$DateTimeOffset\":\"2012-02-29T01:02:03Z\"},"
        + "\"@ns.term#T5\":{\"$Decimal\":\"-12345678901234567234567890\"},"
        + "\"@ns.term#T6\":{\"$Duration\":\"PT10S\"},"
        + "\"@ns.term#T7\":{\"$EnumMember\":\"enumMember\"},"
        + "\"@ns.term#T8\":{\"$Float\":\"1.42\"},"
        + "\"@ns.term#T9\":{\"$Guid\":\"aabbccdd-aabb-ccdd-eeff-aabbccddeeff\"},"
        + "\"@ns.term#T10\":{\"$Int\":\"42\"},\"@ns.term#T11\":\"ABCD\","
        + "\"@ns.term#T12\":{\"$TimeOfDay\":\"00:00:00.999\"},"
        + "\"@ns.term#T13\":{\"$And\":[true,false],\"@ns.term\":true},"
        + "\"@ns.term#T14\":{\"$Or\":[true,false],\"@ns.term\":true}"));
    assertTrue(metadata.contains("\"@ns.term#T15\":{\"$Eq\":[true,false],\"@ns.term\":true},"
        + "\"@ns.term#T16\":{\"$Ne\":[true,false],\"@ns.term\":true},"
        + "\"@ns.term#T17\":{\"$Gt\":[true,false],\"@ns.term\":true},"
        + "\"@ns.term#T18\":{\"$Ge\":[true,false],\"@ns.term\":true},"
        + "\"@ns.term#T19\":{\"$Lt\":[true,false],\"@ns.term\":true},"
        + "\"@ns.term#T20\":{\"$Le\":[true,false],\"@ns.term\":true},"
        + "\"@ns.term#T21\":{\"$Path\":\"AnnoPathValue\"},"
        + "\"@ns.term#T22\":{\"$Apply\":[true],\"$Function\":\"odata.concat\",\"@ns.term\":true}"));
    assertTrue(metadata.contains("\"@ns.term#T23\":[true,false,\"String\"],"
        + "\"@ns.term#T24\":{\"$If\":[true,\"Then\",\"Else\"],\"@ns.term\":true},"
        + "\"@ns.term#T25\":{\"$LabeledElementReference\":\"LabeledElementReferenceValue\"},"
        + "\"@ns.term#T26\":{\"$Null\":null,\"@ns.term\":true},"
        + "\"@ns.term#T27\":{\"$NavigationPropertyPath\":\"NavigationPropertyPathValue\"},"
        + "\"@ns.term#T28\":{\"$Path\":\"PathValue\"},"
        + "\"@ns.term#T29\":{\"$PropertyPath\":\"PropertyPathValue\"}"));
    assertTrue(metadata.contains("\"@ns.term#T30\":{\"$Not\":true,\"@ns.term\":true}"));
    assertTrue(metadata.contains("\"@ns.term#T300\":{\"$Cast\":\"value\","
        + "\"$Type\":\"Edm.String\",\"$MaxLength\":1,\"$Precision\":2,"
        + "\"$Scale\":3,\"@ns.term\":true}"));
    assertTrue(metadata.contains("\"@ns.term#T31\":{\"$IsOf\":\"value\",\"$Type\":\"Edm.String\","
        + "\"$MaxLength\":1,\"$Precision\":2,\"$Scale\":3,\"@ns.term\":true}"));
    assertTrue(metadata.contains("\"@ns.term#T32\":{\"$LabeledElement\":\"value\","
        + "\"$Name\":\"NameAtt\",\"@ns.term\":true}"));
    assertTrue(metadata.contains("\"@ns.term#T33\":{\"$Type\":\"Alias.ETAbstract\","
        + "\"PropName\":\"value\",\"PropName@ns.term\":true,\"@ns.term\":true},"
        + "\"@ns.term#T34\":{\"$UrlRef\":\"URLRefValue\",\"@ns.term\":true}"));
  }
  
  private String localMetadata() throws SerializerException, IOException {
    CsdlEdmProvider provider = new LocalProvider();
    ServiceMetadata serviceMetadata = new ServiceMetadataImpl(provider, Collections.<EdmxReference> emptyList(), null);
    InputStream metadataStream = serializer.metadataDocument(serviceMetadata).getContent();
    String metadata = IOUtils.toString(metadataStream);
    assertNotNull(metadata);
    return metadata;
  }
  
  static class LocalProvider implements CsdlEdmProvider {
    private final static String nameSpace = "namespace";
    private final static String nameSpace1 = "namespace1";
    private final static String nameSpace2 = "namespace2";

    private final FullQualifiedName nameETAbstract = new FullQualifiedName(nameSpace, "ETAbstract");
    private final FullQualifiedName nameETAbstractBase = new FullQualifiedName(nameSpace, "ETAbstractBase");
    private final FullQualifiedName nameET = new FullQualifiedName(nameSpace, "ET");
    private final FullQualifiedName nameETTwoKeyNav = new FullQualifiedName(nameSpace, "ETTwoKeyNav");
    private final FullQualifiedName nameETTwoKeyNavOne = new FullQualifiedName(nameSpace, "ETTwoKeyNavOne");
    private final FullQualifiedName nameETOne = new FullQualifiedName(nameSpace, "ETOne");

    private final FullQualifiedName nameInt16 = EdmPrimitiveTypeKind.Int16.getFullQualifiedName();
    private final FullQualifiedName nameDateTimeOffset = EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName();
    private final FullQualifiedName nameString = EdmPrimitiveTypeKind.String.getFullQualifiedName();
    private final FullQualifiedName nameUARTPrimParam = new FullQualifiedName(nameSpace, "UARTPrimParam");
    private final FullQualifiedName nameCTEntityInfo = new FullQualifiedName(nameSpace, "CTEntityInfo");
    private final CsdlProperty propertyInt16_NotNullable = new CsdlProperty()
    .setName("PropertyInt16")
    .setType(nameInt16)
    .setNullable(false);
    private final CsdlProperty propertyString = new CsdlProperty()
    .setName("PropertyString")
    .setType(nameString);
    private final CsdlProperty nameProperty = new CsdlProperty()
        .setName("name")
        .setType(nameString)
        .setNullable(true);
    private final CsdlProperty infoProperty = new CsdlProperty()
        .setName("Info")
        .setType(nameCTEntityInfo);
    private final CsdlProperty idProperty = new CsdlProperty()
        .setName("ID")
        .setType(nameInt16)
        .setCollection(true);
    private final CsdlProperty createdProperty = new CsdlProperty()
        .setName("Created")
        .setType(nameDateTimeOffset)
        .setPrecision(20)
        .setScale(2)
        .setDefaultValue("10-2-2017:20:30:40")
        .setMaxLength(30);
    
    private final FullQualifiedName nameCTTwoPrim = new FullQualifiedName(nameSpace, "CTTwoPrim");
    private final FullQualifiedName nameCTTwoPrimBase = new FullQualifiedName(nameSpace, "CTTwoPrimBase");
    private final FullQualifiedName nameCTPrimComp = new FullQualifiedName(nameSpace, "CTPrimComp");
    private final FullQualifiedName nameUFNRTInt16 = new FullQualifiedName(nameSpace, "UFNRTInt16");
    private final FullQualifiedName nameUFNRTInt161 = new FullQualifiedName("nameSpace2", "UFNRTInt161");
    private final FullQualifiedName nameContainer = new FullQualifiedName(nameSpace, "container");
    private final FullQualifiedName nameContainer1 = new FullQualifiedName(nameSpace1, "container1");
    private final FullQualifiedName nameContainer2 = new FullQualifiedName(nameSpace2, "container2");
    private final FullQualifiedName nameENString = new FullQualifiedName(nameSpace, "ENString");
    private final FullQualifiedName nameBAETTwoKeyNavRTETTwoKeyNavParam = 
        new FullQualifiedName(nameSpace, "BAETTwoKeyNavRTETTwoKeyNavParam");
    private final FullQualifiedName nameBFETTwoKeyNavRTETTwoKeyNavParam = 
        new FullQualifiedName(nameSpace, "BFETTwoKeyNavRTETTwoKeyNavParam");
    private final FullQualifiedName nameBAProp = 
        new FullQualifiedName(nameSpace, "BAProp");

    @Override
    public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
      return Collections.singletonList(new CsdlAliasInfo().setAlias("Alias").setNamespace(nameSpace));
    }

    @Override
    public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
      
      if (nameENString.equals(enumTypeName)) {
        
        CsdlAnnotation memberAnnotation = new CsdlAnnotation()
            .setTerm("Core.Description")
            .setQualifier("Target")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.String, "Description of Enum Member"));
        
        return new CsdlEnumType()
        .setName(nameENString.getName())
        .setFlags(true)
        .setUnderlyingType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
        .setMembers(Collections.singletonList(
            new CsdlEnumMember().setName("String1").setValue("1").setAnnotations(Arrays.asList(memberAnnotation))));
      }
      return null;
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
      if (entityTypeName.equals(nameETAbstract)) {
        return new CsdlEntityType()
        .setName("ETAbstract")
        .setAbstract(true)
        .setNavigationProperties(Arrays.asList(
            new CsdlNavigationProperty().setName("NavPropertyETTwoKeyNavOne").setType(nameETTwoKeyNavOne)))
        .setProperties(Collections.singletonList(propertyString));
      } else if (entityTypeName.equals(nameETAbstractBase)) {
        CsdlAnnotation annotation = new CsdlAnnotation()
            .setTerm("Core.Description")
            .setQualifier("Target")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.String, "Description of Type"));
        propertyInt16_NotNullable.setAnnotations(Arrays.asList(annotation));
        
        return new CsdlEntityType()
        .setName("ETAbstractBase")
        .setBaseType(nameETAbstract)
        .setKey(Collections.singletonList(new CsdlPropertyRef().setName("PropertyInt16")))
        .setProperties(Collections.singletonList(propertyInt16_NotNullable))
        .setAnnotations(Arrays.asList(annotation));
      } else if (entityTypeName.equals(nameET)) {
        return new CsdlEntityType()
            .setName("ET")
            .setKey(Arrays.asList(new CsdlPropertyRef().setAlias("EntityInfoID").setName("Info/ID"), 
                new CsdlPropertyRef().setName("name")))
            .setNavigationProperties(Arrays.asList(
                new CsdlNavigationProperty().setName("NavPropertyETOne").setType(nameETOne)))
            .setProperties(Arrays.asList(nameProperty, infoProperty));
      } else if (entityTypeName.equals(nameETTwoKeyNav)) {
        return new CsdlEntityType()
            .setName("ETTwoKeyNav")
            .setKey(Arrays.asList(new CsdlPropertyRef().setName("PropertyInt16"), 
                new CsdlPropertyRef().setName("PropertyString")))
            .setNavigationProperties(Arrays.asList(
                new CsdlNavigationProperty().setName("NavPropertyETTwoKeyNavOne").setType(nameETTwoKeyNavOne),
                new CsdlNavigationProperty().setName("NavPropertyETOne").setType(nameETOne)))
            .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString));
      } else if (entityTypeName.equals(nameETOne)) {
        return new CsdlEntityType()
            .setName("ETOne")
            .setKey(Collections.singletonList(new CsdlPropertyRef().setName("PropertyInt16")))
            .setNavigationProperties(Arrays.asList(
                new CsdlNavigationProperty().setName("NavPropertyET").setType(nameET)))
            .setProperties(Collections.singletonList(propertyInt16_NotNullable));
      } else if (entityTypeName.equals(nameETTwoKeyNavOne)) {
        return new CsdlEntityType()
            .setName("ETTwoKeyNavOne")
            .setBaseType(nameETOne)
            .setHasStream(true)
            .setProperties(Collections.singletonList(propertyString))
            .setNavigationProperties(Arrays.asList(
                new CsdlNavigationProperty().setName("NavPropertyETAbstract")
                .setCollection(true).setType(nameETAbstract)
                .setContainsTarget(true).setPartner("NavPropertyETTwoKeyNavOne").setNullable(false)
                .setReferentialConstraints(Collections.singletonList(new CsdlReferentialConstraint()
                    .setProperty("PropertyString").setReferencedProperty("PropertyString")
                    .setAnnotations(Collections.singletonList(new CsdlAnnotation()
            .setTerm("Core.Description")
            .setQualifier("Target")
            .setExpression(new CsdlConstantExpression(
                ConstantExpressionType.String, "Description of Complex Type"))))))));
      }
      return null;
    }

    @Override
    public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
      if (complexTypeName.equals(nameCTTwoPrim)) {
        return new CsdlComplexType()
        .setName("CTTwoPrim")
        .setAbstract(true)
        .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString));

      }
      if (complexTypeName.equals(nameCTTwoPrimBase)) {
        CsdlAnnotation annotation = new CsdlAnnotation()
            .setTerm("Core.Description")
            .setQualifier("Target")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.String, "Description of Complex Type"));
        
        return new CsdlComplexType()
        .setName("CTTwoPrimBase")
        .setBaseType(nameCTTwoPrim)
        .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString))
        .setAnnotations(Arrays.asList(annotation));
      }
      if (complexTypeName.equals(nameCTEntityInfo)) {
        return new CsdlComplexType()
            .setName("CTEntityInfo")
            .setProperties(Arrays.asList(idProperty, createdProperty));
      }
      if (complexTypeName.equals(nameCTPrimComp)) {
        return new CsdlComplexType()
        .setName("CTPrimComp")
        .setProperties(Arrays.asList(propertyString));
      }
      return null;

    }

    @Override
    public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
      if (actionName.equals(nameUARTPrimParam)) {
        return Collections.singletonList(
            new CsdlAction().setName("UARTPrimParam")
            .setParameters(Collections.singletonList(
                new CsdlParameter().setName("ParameterInt16").setType(nameInt16)))
                .setReturnType(new CsdlReturnType().setType(nameString)));
      }
      if (actionName.equals(nameBAETTwoKeyNavRTETTwoKeyNavParam)) {
        return Arrays.asList(
            new CsdlAction().setName("BAETTwoKeyNavRTETTwoKeyNavParam")
            .setParameters(Arrays.asList(
                new CsdlParameter().setName("BindingParam").setType(nameETTwoKeyNav),
                new CsdlParameter().setName("PropertyComp").setType(nameCTPrimComp)))
            .setReturnType(new CsdlReturnType().setType(nameETTwoKeyNav).setCollection(true))
            .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavOne")
            .setBound(true),
            new CsdlAction().setName("BAETTwoKeyNavRTETTwoKeyNavParam")
            .setParameters(Arrays.asList(
                new CsdlParameter().setName("BindingParam").setType(nameET)))
            .setReturnType(new CsdlReturnType().setNullable(false).setType(nameET))
            .setBound(true)
            .setEntitySetPath("BindingParam/NavPropertyET"),
            new CsdlAction().setName("BAETTwoKeyNavRTETTwoKeyNavParam")
            .setParameters(Arrays.asList(
                new CsdlParameter().setName("PropertyComp").setType(nameCTPrimComp)))
            .setReturnType(new CsdlReturnType().setNullable(false).setType(nameET)));
      }
      if (actionName.equals(nameBAProp)) {
        return Collections.singletonList(new CsdlAction().setName("BAProp")
        .setParameters(Arrays.asList(
            new CsdlParameter().setName("BindingParam").setType(nameET),
            new CsdlParameter().setName("PropertyInt").setType(nameInt16).
            setPrecision(10).setScale(3).setMaxLength(10).setNullable(false).setCollection(true)))
        .setReturnType(new CsdlReturnType().setNullable(true).setType(nameInt16)
            .setPrecision(10).setScale(3).setMaxLength(10))
        .setBound(true)
        .setEntitySetPath("BindingParam/NavPropertyET"));
      }
      return null;
    }

    @Override
    public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {
      if (functionName.equals(nameUFNRTInt16)) {
        return Collections.singletonList(
            new CsdlFunction()
            .setName("UFNRTInt16")
            .setParameters(Collections.<CsdlParameter> emptyList())
            .setReturnType(new CsdlReturnType().setType(nameInt16)));
      }
      if (functionName.equals(nameBFETTwoKeyNavRTETTwoKeyNavParam)) {
        return Arrays.asList(
            new CsdlFunction().setName("BFETTwoKeyNavRTETTwoKeyNavParam")
            .setParameters(Arrays.asList(
                new CsdlParameter().setName("BindingParam").setType(nameETTwoKeyNav),
                new CsdlParameter().setName("PropertyComp").setType(nameCTPrimComp)))
            .setReturnType(new CsdlReturnType().setType(nameETTwoKeyNav).setCollection(true))
            .setEntitySetPath("BindingParam/NavPropertyETTwoKeyNavOne")
            .setBound(true).setComposable(true),
            new CsdlFunction().setName("BFETTwoKeyNavRTETTwoKeyNavParam")
            .setParameters(Arrays.asList(
                new CsdlParameter().setName("BindingParam").setType(nameET)))
            .setReturnType(new CsdlReturnType().setNullable(false).setType(nameET))
            .setBound(true)
            .setEntitySetPath("BindingParam/NavPropertyET"));
      }
      return null;
    }

    @Override
    public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
        throws ODataException {
      if (entitySetName.equals("ESAllPrim")) {
        return new CsdlEntitySet()
        .setName("ESAllPrim")
        .setType(nameETAbstractBase);
      }
      if (entitySetName.equals("ESOne")) {
        return new CsdlEntitySet()
            .setName("ESOne")
            .setType(nameETOne);
      }
      if (entitySetName.equals("ESTwoKeyNav")) {
        return new CsdlEntitySet()
            .setName("ESTwoKeyNav")
            .setType(nameETTwoKeyNav)
            .setNavigationPropertyBindings(Arrays.asList(new CsdlNavigationPropertyBinding()
                .setPath("NavPropertyETTwoKeyNavOne/namespace.ETOne/NavPropertyET")
                .setTarget("ES"),
                new CsdlNavigationPropertyBinding()
                .setPath("NavPropertyETOne")
                .setTarget("ESOne")));
      }
      if (entitySetName.equals("ESTwoKeyNavOne")) {
        return new CsdlEntitySet()
            .setName("ESTwoKeyNavOne")
            .setType(nameETTwoKeyNavOne)
            .setIncludeInServiceDocument(true);
      }
      if (entitySetName.equals("ES")) {
        return new CsdlEntitySet()
            .setName("ES")
            .setType(nameET)
            .setIncludeInServiceDocument(false)
            .setNavigationPropertyBindings(Collections.singletonList(new CsdlNavigationPropertyBinding()
                .setPath("NavPropertyETOne")
                .setTarget("ESOne")));
      }
      return null;
    }

    @Override
    public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
        throws ODataException {
      if (singletonName.equals("SI")) {
        return new CsdlSingleton()
        .setName("SI")
        .setType(nameETAbstractBase);
      }
      if (singletonName.equals("SIBinding")) {
        return new CsdlSingleton()
            .setName("SIBinding")
            .setType(nameET)
            .setNavigationPropertyBindings(Collections.singletonList(new CsdlNavigationPropertyBinding()
                .setPath("NavPropertyETOne")
                .setTarget("ESOne")));
      }
      return null;
    }

    @Override
    public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
        throws ODataException {
      if (entityContainer.equals(nameContainer)) {
        if (actionImportName.equals("AIRTPrimParam")) {
          return new CsdlActionImport()
          .setName("AIRTPrimParam")
          .setAction(nameUARTPrimParam)
          .setEntitySet("ESTwoKeyNav");
        }
      }
      return null;
    }

    @Override
    public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer,
        final String functionImportName)
            throws ODataException {
      if (null != entityContainer && entityContainer.equals(nameContainer)) {
        if (functionImportName.equals("FINRTInt16")) {
          return new CsdlFunctionImport()
          .setName("FINRTInt16")
          .setFunction(nameUFNRTInt16)
          .setIncludeInServiceDocument(true)
          .setEntitySet("ESTwoKeyNavOne");
        }
      } else {
        if (functionImportName.equals("FINRTInt161")) {
          return new CsdlFunctionImport()
          .setName("FINRTInt161")
          .setFunction(nameUFNRTInt161)
          .setIncludeInServiceDocument(true)
          .setEntitySet("ESTwoKeyNavOne");
        }
      }
      return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
      List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
      CsdlSchema schema1 = new CsdlSchema();
      schema1.setNamespace(nameSpace1);
      schema1.setAlias("Alias1");
      schemas.add(schema1);
   // EntityContainer
      schema1.setEntityContainer(getEntityContainer1());
      
      CsdlSchema schema2 = new CsdlSchema();
      schema2.setNamespace(nameSpace2);
      schemas.add(schema2);
   // EntityContainer
      schema2.setEntityContainer(getEntityContainer2());
   
      
      CsdlSchema schema = new CsdlSchema();
      schema.setNamespace(nameSpace);
      schema.setAlias("Alias");
      schemas.add(schema);

      // EnumTypes
      schema.setEnumTypes(Collections.singletonList(getEnumType(nameENString)));

      // EntityTypes
      schema.setEntityTypes(Arrays.asList(
          getEntityType(nameETAbstract),
          getEntityType(nameETAbstractBase),
          getEntityType(nameET),
          getEntityType(nameETOne),
          getEntityType(nameETTwoKeyNav),
          getEntityType(nameETTwoKeyNavOne)));

      // ComplexTypes
      schema.setComplexTypes(Arrays.asList(
          getComplexType(nameCTTwoPrim),
          getComplexType(nameCTTwoPrimBase),
          getComplexType(nameCTEntityInfo),
          getComplexType(nameCTPrimComp)));

      // TypeDefinitions

      // Actions
      List<CsdlAction> actions1 = getActions(nameUARTPrimParam);
      List<CsdlAction> actions2 = getActions(nameBAETTwoKeyNavRTETTwoKeyNavParam);
      List<CsdlAction> actions3 = getActions(nameBAProp);
      List<CsdlAction> actions = new ArrayList<CsdlAction>();
      actions.addAll(actions1);
      actions.addAll(actions2);
      actions.addAll(actions3);
      
      schema.setActions(actions);

      // Functions
      List<CsdlFunction> function1 = getFunctions(nameUFNRTInt16);
      List<CsdlFunction> function2 = getFunctions(nameBFETTwoKeyNavRTETTwoKeyNavParam);
      List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
      functions.addAll(function1);
      functions.addAll(function2);
      
      schema.setFunctions(functions);

      // EntityContainer
      schema.setEntityContainer(getEntityContainer());

      // Terms
      schema.setTerms(Arrays.asList(
          getTerm(new FullQualifiedName("ns", "term")),
          getTerm(new FullQualifiedName("namespace", "Term1")),
          getTerm(new FullQualifiedName("ns", "Term2")),
          getTerm(new FullQualifiedName("ns", "Term3")),
          getTerm(new FullQualifiedName("ns", "Term4"))));

      // Annotationgroups
      schema.setAnnotationsGroup(Arrays.asList(
          getAnnotationsGroup(new FullQualifiedName("Alias", "ETAbstract"), "Tablett"),
          getAnnotationsGroup(new FullQualifiedName("Alias", "ET"), "T")));
      
      return schemas;
    }

    public CsdlEntityContainer getEntityContainer1() throws ODataException {
      CsdlEntityContainer container = new CsdlEntityContainer();
      container.setName("container1");

      // EntitySets
      container.setEntitySets(Arrays.asList(getEntitySet(nameContainer1, "ESAllPrim")));
      container.setExtendsContainer(nameContainer2.getFullQualifiedNameAsString());

      return container;
    }
    
    public CsdlEntityContainer getEntityContainer2() throws ODataException {
      CsdlEntityContainer container = new CsdlEntityContainer();
      container.setName("container2");

      return container;
    }
    
    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
        throws ODataException {
      if (entityContainerName == null) {
        return new CsdlEntityContainerInfo().setContainerName(new FullQualifiedName("org.olingo", "container"));
      }
      return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
      CsdlEntityContainer container = new CsdlEntityContainer();
      container.setName("container");

      // EntitySets
      container.setEntitySets(Arrays.asList(getEntitySet(nameContainer, "ESAllPrim"),
          getEntitySet(nameContainer, "ES"),
          getEntitySet(nameContainer, "ESOne"),
          getEntitySet(nameContainer, "ESTwoKeyNav"),
          getEntitySet(nameContainer, "ESTwoKeyNavOne")));

      // Singletons
      container.setSingletons(Arrays.asList(getSingleton(nameContainer, "SI"),
          getSingleton(nameContainer, "SIBinding")));

      // ActionImports
      container.setActionImports(Collections.singletonList(getActionImport(nameContainer, "AIRTPrimParam")));

      // FunctionImports
      container.setFunctionImports(Arrays.asList(getFunctionImport(nameContainer, "FINRTInt16"),
          getFunctionImport(null, "FINRTInt161")));
      
      container.setExtendsContainer(new FullQualifiedName(nameSpace1, "container1").getFullQualifiedNameAsString());

      return container;
    }

    @Override
    public CsdlTypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
      return null;
    }

    @Override
    public CsdlTerm getTerm(final FullQualifiedName termName) throws ODataException {
      if (new FullQualifiedName("ns", "term").equals(termName)) {
        return new CsdlTerm().setType("Edm.String").setName("term");

      } else if (new FullQualifiedName("namespace", "Term1").equals(termName)) {
        return new CsdlTerm().setType("Edm.String").setName("Term1");

      } else if (new FullQualifiedName("ns", "Term2").equals(termName)) {
        return new CsdlTerm().setType("Edm.String").setName("Term2")
            .setNullable(false).setDefaultValue("default").setMaxLength(1).setPrecision(2).setScale(3);

      } else if (new FullQualifiedName("ns", "Term3").equals(termName)) {
        return new CsdlTerm().setType("Edm.String").setName("Term3")
            .setAppliesTo(Arrays.asList("Property", "EntitySet", "Schema"));

      } else if (new FullQualifiedName("ns", "Term4").equals(termName)) {
        return new CsdlTerm().setType("Edm.String").setName("Term4").setBaseTerm("namespace.Term1");

      } else if (new FullQualifiedName("Core", "Description").equals(termName)) {
        return new CsdlTerm().setType("Edm.String").setName("Description");

      }
      return null;
    }

    @Override
    public CsdlAnnotations getAnnotationsGroup(final FullQualifiedName targetName, final String qualifier)
        throws ODataException {
      if (new FullQualifiedName("Alias", "ETAbstract").equals(targetName) && "Tablett".equals(qualifier)) {
        CsdlAnnotations annoGroup = new CsdlAnnotations();
        annoGroup.setTarget("Alias.ETAbstract").setQualifier("Tablett");

        List<CsdlAnnotation> innerAnnotations = Collections.singletonList(
            new CsdlAnnotation().setTerm("ns.term"));

        List<CsdlAnnotation> annotationsList = new ArrayList<CsdlAnnotation>();
        annoGroup.setAnnotations(annotationsList);
        // Constant Annotations
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T1")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Binary).setValue("qrvM3e7_")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T2")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Bool, "true")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T3")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Date, "2012-02-29")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T4")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.DateTimeOffset, "2012-02-29T01:02:03Z")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T5")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Decimal, "-12345678901234567234567890")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T6")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Duration, "PT10S")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T7")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.EnumMember, "enumMember")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T8")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Float, "1.42")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T9")
            .setExpression(
                new CsdlConstantExpression(ConstantExpressionType.Guid, "aabbccdd-aabb-ccdd-eeff-aabbccddeeff")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T10")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Int, "42")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T11")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.String, "ABCD")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T12")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.TimeOfDay, "00:00:00.999")));

        // logical expressions
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T13")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.And)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T14")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Or)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        
        // comparison
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T15")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Eq)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T16")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Ne)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T17")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Gt)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T18")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Ge)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T19")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Lt)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T20")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Le)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));

        // Other
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T21")
            .setExpression(new CsdlAnnotationPath().setValue("AnnoPathValue")));

        List<CsdlExpression> parameters = new ArrayList<CsdlExpression>();
        parameters.add(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T22")
            .setExpression(new CsdlApply().setFunction("odata.concat")
                .setParameters(parameters)
                .setAnnotations(innerAnnotations)));

        List<CsdlExpression> items = new ArrayList<CsdlExpression>();
        items.add(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"));
        items.add(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"));
        items.add(new CsdlConstantExpression(ConstantExpressionType.String, "String"));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T23")
            .setExpression(new CsdlCollection().setItems(items)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T24")
            .setExpression(new CsdlIf()
            .setGuard(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setThen(new CsdlConstantExpression(ConstantExpressionType.String, "Then"))
            .setElse(new CsdlConstantExpression(ConstantExpressionType.String, "Else"))
            .setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T25")
            .setExpression(new CsdlLabeledElementReference().setValue("LabeledElementReferenceValue")));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T26")
            .setExpression(new CsdlNull().setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T27")
            .setExpression(new CsdlNavigationPropertyPath().setValue("NavigationPropertyPathValue")));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T28")
            .setExpression(new CsdlPath().setValue("PathValue")));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T29")
            .setExpression(new CsdlPropertyPath().setValue("PropertyPathValue")));
        
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T30")
        .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Not)
        .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
        .setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T300")
        .setExpression(new CsdlCast()
        .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
        .setMaxLength(1)
        .setPrecision(2)
        .setScale(3)
        .setType("Edm.String")
        .setAnnotations(innerAnnotations))); 
        
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T31")
        .setExpression(new CsdlIsOf()
        .setMaxLength(1)
        .setPrecision(2)
        .setScale(3)
        .setType("Edm.String")
        .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
        .setAnnotations(innerAnnotations))); 

    annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T32")
        .setExpression(new CsdlLabeledElement()
        .setName("NameAtt")
        .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
        .setAnnotations(innerAnnotations)));
        
        CsdlPropertyValue prop = new CsdlPropertyValue()
        .setProperty("PropName")
        .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
        .setAnnotations(innerAnnotations);
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T33")
            .setExpression(new CsdlRecord().setType("Alias.ETAbstract")
                .setPropertyValues(Arrays.asList(prop))
                .setAnnotations(innerAnnotations))); 

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T34")
            .setExpression(new CsdlUrlRef()
            .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "URLRefValue"))
            .setAnnotations(innerAnnotations)));
        
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term").setQualifier("T35")
            .setExpression(new CsdlNull()));

        return annoGroup;
      } else if (new FullQualifiedName("Alias", "ET").equals(targetName)) {
        CsdlAnnotations annoGroup = new CsdlAnnotations();
        annoGroup.setTarget("Alias.ET");
        return annoGroup;
      }
      return null;
    }
  }
}
