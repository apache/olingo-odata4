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
package org.apache.olingo.server.core.serializer.xml;

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
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmConstantExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
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
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
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
//CHECKSTYLE:OFF
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression.LogicalOrComparisonExpressionType;
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
//CHECKSTYLE:ON
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ServiceMetadataImpl;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetadataDocumentXmlSerializerTest {

  private static ODataSerializer serializer;

  @BeforeClass
  public static void init() throws SerializerException {
    serializer = OData.newInstance().createSerializer(ContentType.APPLICATION_XML);
  }

  @Test
  public void writeMetadataWithEmptyMockedEdm() throws Exception {
    final Edm edm = mock(Edm.class);
    ServiceMetadata metadata = mock(ServiceMetadata.class);
    when(metadata.getEdm()).thenReturn(edm);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
        + "<edmx:DataServices></edmx:DataServices></edmx:Edmx>",
        IOUtils.toString(serializer.metadataDocument(metadata).getContent()));
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
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">"
        + "<edmx:DataServices>"
        + "<Schema xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Namespace=\"MyNamespace\"></Schema>"
        + "</edmx:DataServices>"
        + "</edmx:Edmx>",
        IOUtils.toString(metadata));
  }
  
  /** Test if annotations on EnumType Members are added as children of the Member element
   *  in compliance with OData v4.0, part 3: CSDL, section 14.3
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
        "<EnumType Name=\"MyEnum\" IsFlags=\"false\" UnderlyingType=\"Edm.Int32\">" +
          "<Member Name=\"MyMember\" Value=\"0\">" +
            "<Annotation Qualifier=\"Core.Description\">" +
              "<String>MyDescription</String>" +
            "</Annotation>" +
          "</Member>" +
        "</EnumType>"));

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
        "<edmx:Reference Uri=\"http://example.com\"></edmx:Reference>"));
    assertTrue(metadataString.contains(
        "<edmx:Reference " +
            "Uri=\"http://localhost/odata/odata/v4.0/referenceWithInclude\">" +
            "<edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\"></edmx:Include>" +
        "</edmx:Reference>"));
    assertTrue(metadataString.contains(
        "<edmx:Reference " +
            "Uri=\"http://localhost/odata/odata/v4.0/referenceWithTwoIncludes\">" +
            "<edmx:Include Namespace=\"Org.OData.Core.2\" Alias=\"Core2\"></edmx:Include>" +
            "<edmx:Include Namespace=\"Org.OData.Core.3\" Alias=\"Core3\"></edmx:Include>" +
        "</edmx:Reference>"));
    assertTrue(metadataString.contains(
        "<edmx:Reference Uri=\"http://localhost/odata/odata/v4.0/referenceWithIncludeAnnos\">" +
            "<edmx:IncludeAnnotations TermNamespace=\"TermNs.2\" Qualifier=\"Q.2\" "
            + "TargetNamespace=\"TargetNS.2\"></edmx:IncludeAnnotations>" +
            "<edmx:IncludeAnnotations TermNamespace=\"TermNs.3\" Qualifier=\"Q.3\" "
            + "TargetNamespace=\"TargetNS.3\"></edmx:IncludeAnnotations>" +
        "</edmx:Reference>"));
    assertTrue(metadataString.contains(
            "<edmx:Reference Uri=\"http://localhost/odata/odata/v4.0/referenceWithAll\">" +
                "<edmx:Include Namespace=\"ReferenceWithAll.1\" Alias=\"Core1\"></edmx:Include>" +
                "<edmx:Include Namespace=\"ReferenceWithAll.2\" Alias=\"Core2\"></edmx:Include>" +
                "<edmx:IncludeAnnotations TermNamespace=\"ReferenceWithAllTermNs.4\" " +
                "Qualifier=\"Q.4\" TargetNamespace=\"TargetNS.4\"></edmx:IncludeAnnotations>" +
                "<edmx:IncludeAnnotations TermNamespace=\"ReferenceWithAllTermNs.5\" " +
                "Qualifier=\"Q.5\" TargetNamespace=\"TargetNS.5\"></edmx:IncludeAnnotations>" +
            "</edmx:Reference>"));
    assertTrue(metadataString.contains(
        "<edmx:Reference Uri=\"http://localhost/odata/odata/v4.0/referenceWithAllAndNull\">" +
            "<edmx:Include Namespace=\"referenceWithAllAndNull.1\"></edmx:Include>" +
            "<edmx:Include Namespace=\"referenceWithAllAndNull.2\"></edmx:Include>" +
            "<edmx:IncludeAnnotations TermNamespace=\"ReferenceWithAllTermNs.4\"></edmx:IncludeAnnotations>" +
            "<edmx:IncludeAnnotations TermNamespace=\"ReferenceWithAllTermAndNullNs.5\" " +
            "Qualifier=\"Q.5\"></edmx:IncludeAnnotations>" +
            "<edmx:IncludeAnnotations TermNamespace=\"ReferenceWithAllTermAndNullNs.6\" " +
            "TargetNamespace=\"TargetNS\"></edmx:IncludeAnnotations>" +
            "<edmx:IncludeAnnotations TermNamespace=\"ReferenceWithAllTermAndNullNs.7\"></edmx:IncludeAnnotations>" +
        "</edmx:Reference>"));
  }

  @Test
  public void aliasTest() throws Exception {
    String metadata = localMetadata();

    assertTrue(metadata.contains("<EnumType Name=\"ENString\" IsFlags=\"true\" UnderlyingType=\"Edm.Int16\">"));
    assertTrue(metadata.contains("<EntityType Name=\"ETAbstractBase\" BaseType=\"Alias.ETAbstract\">"));
    assertTrue(metadata.contains("<ComplexType Name=\"CTTwoPrimBase\" BaseType=\"Alias.CTTwoPrim\"></ComplexType>"));
    assertTrue(metadata.contains("<Property Name=\"PropertyInt16\" Type=\"Edm.Int16\" Nullable=\"false\"></Property>"));
    assertTrue(metadata.contains("<EntitySet Name=\"ESAllPrim\" EntityType=\"Alias.ETAbstractBase\"></EntitySet>"));
    assertTrue(metadata.contains("<Singleton Name=\"SI\" Type=\"Alias.ETAbstractBase\"></Singleton>"));
    assertTrue(metadata.contains("<ActionImport Name=\"AIRTPrimParam\" Action=\"Alias.UARTPrimParam\"></ActionImport"));
    assertTrue(metadata.contains("<FunctionImport Name=\"FINRTInt16\" " +
        "Function=\"Alias.UFNRTInt16\" IncludeInServiceDocument=\"true\"></FunctionImport>"));
  }

  @Test
  public void terms() throws Exception {
    String metadata = localMetadata();
    assertTrue(metadata.contains("<Term Name=\"Term1\" Type=\"Edm.String\"></Term>"));
    assertTrue(metadata
        .contains("<Term Name=\"Term2\" Type=\"Edm.String\" Nullable=\"false\" DefaultValue=\"default\" "
            + "MaxLength=\"1\" Precision=\"2\" Scale=\"3\"></Term>"));
    assertTrue(metadata.contains("<Term Name=\"Term3\" Type=\"Edm.String\" "
    		+ "AppliesTo=\"Property EntitySet Schema\"></Term>"));
    assertTrue(metadata.contains("<Term Name=\"Term4\" Type=\"Edm.String\" BaseTerm=\"Alias.Term1\"></Term>"));
  }

  @Test
  public void annotationsTest() throws Exception {
    String metadata = localMetadata();
    // All constant expressions
    assertTrue(metadata.contains("<Annotations Target=\"Alias.ETAbstract\" Qualifier=\"Tablett\">"));
    assertTrue(metadata.contains("</Annotations>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><Binary>qrvM3e7_</Binary></Annotation>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><Bool>true</Bool></Annotation>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><Date>2012-02-29</Date></Annotation>"));
    assertTrue(metadata
        .contains("<Annotation Term=\"ns.term\"><DateTimeOffset>2012-02-29T01:02:03Z</DateTimeOffset></Annotation>"));
    assertTrue(metadata
        .contains("<Annotation Term=\"ns.term\"><Decimal>-12345678901234567234567890</Decimal></Annotation>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><Duration>PT10S</Duration></Annotation>"));
    assertTrue(metadata
        .contains("<Annotation Term=\"ns.term\"><EnumMember>Enum/enumMember</EnumMember></Annotation>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><Float>1.42</Float></Annotation>"));
    assertTrue(metadata
        .contains("<Annotation Term=\"ns.term\"><Guid>aabbccdd-aabb-ccdd-eeff-aabbccddeeff</Guid></Annotation>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><Int>42</Int></Annotation>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><String>ABCD</String></Annotation>"));
    assertTrue(metadata.contains("<Annotation Term=\"ns.term\"><TimeOfDay>00:00:00.999</TimeOfDay></Annotation>"));

    // All dynamic expressions
    // Logical expressions
    assertTrue(metadata.contains("<And><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></And>"));
    assertTrue(metadata.contains("<Or><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></Or>"));
    assertTrue(metadata.contains("<Not><Bool>true</Bool><Annotation Term=\"ns.term\"></Annotation></Not>"));

    // Comparison expressions
    assertTrue(metadata.contains("<Eq><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></Eq>"));
    assertTrue(metadata.contains("<Ne><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></Ne>"));
    assertTrue(metadata.contains("<Gt><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></Gt>"));
    assertTrue(metadata.contains("<Ge><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></Ge>"));
    assertTrue(metadata.contains("<Lt><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></Lt>"));
    assertTrue(metadata.contains("<Le><Bool>true</Bool><Bool>false</Bool>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></Le>"));

    // Other
    assertTrue(metadata.contains("<AnnotationPath>AnnoPathValue</AnnotationPath>"));
    assertTrue(metadata
        .contains("<Apply Function=\"odata.concat\"><Bool>true</Bool>"
        		+ "<Annotation Term=\"ns.term\"></Annotation></Apply>"));
    assertTrue(metadata
        .contains("<Cast Type=\"Edm.String\" MaxLength=\"1\" Precision=\"2\" Scale=\"3\">"
            + "<String>value</String><Annotation Term=\"ns.term\"></Annotation></Cast>"));
    assertTrue(metadata.contains("<Collection><Bool>true</Bool>"
        + "<Bool>false</Bool><String>String</String></Collection>"));
    assertTrue(metadata
        .contains("<If><Bool>true</Bool><String>Then</String>"
            + "<String>Else</String><Annotation Term=\"ns.term\"></Annotation></If>"));
    assertTrue(metadata
        .contains("<IsOf Type=\"Edm.String\" MaxLength=\"1\" Precision=\"2\" Scale=\"3\">"
            + "<String>value</String><Annotation Term=\"ns.term\"></Annotation></IsOf>"));
    assertTrue(metadata
        .contains("<LabeledElement Name=\"NameAtt\">"
            + "<String>value</String><Annotation Term=\"ns.term\"></Annotation></LabeledElement>"));
    assertTrue(metadata.contains("<LabeledElementReference>LabeledElementReferenceValue</LabeledElementReference>"));
    assertTrue(metadata.contains("<NavigationPropertyPath>NavigationPropertyPathValue</NavigationPropertyPath>"));
    assertTrue(metadata.contains("<Path>PathValue</Path>"));
    assertTrue(metadata.contains("<PropertyPath>PropertyPathValue</PropertyPath>"));
    assertTrue(metadata
        .contains("<Record Type=\"Alias.ETAbstract\"><PropertyValue Property=\"PropName\"><String>value</String>"
            + "<Annotation Term=\"ns.term\"></Annotation></PropertyValue>"
            + "<Annotation Term=\"ns.term\"></Annotation></Record>"));
    assertTrue(metadata.contains("<UrlRef><String>URLRefValue</String>"
    		+ "<Annotation Term=\"ns.term\"></Annotation></UrlRef>"));

  }

  private String localMetadata() throws SerializerException, IOException {
    CsdlEdmProvider provider = new LocalProvider();
    ServiceMetadata serviceMetadata = new ServiceMetadataImpl(provider, Collections.<EdmxReference> emptyList(), null);
    InputStream metadataStream = serializer.metadataDocument(serviceMetadata).getContent();
    String metadata = IOUtils.toString(metadataStream);
    assertNotNull(metadata);
    return metadata;
  }

  @Test
  public void writeAbstractComplexType() throws Exception {
    EdmSchema schema = mock(EdmSchema.class);
    when(schema.getNamespace()).thenReturn("MyNamespace");
    Edm edm = mock(Edm.class);
    when(edm.getSchemas()).thenReturn(Arrays.asList(schema));
    ServiceMetadata serviceMetadata = mock(ServiceMetadata.class);
    when(serviceMetadata.getEdm()).thenReturn(edm);
    List<EdmComplexType> complexTypes = new ArrayList<EdmComplexType>();

    FullQualifiedName name = new FullQualifiedName("namespace", "ComplexType");
    EdmComplexType complexType = mock(EdmComplexType.class);
    when(complexType.isAbstract()).thenReturn(true);
    when(complexType.getFullQualifiedName()).thenReturn(name);
    when(complexType.getName()).thenReturn(name.getName());
    when(complexType.isOpenType()).thenReturn(true);

    final EdmPrimitiveType stringType = OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String);
    when(complexType.getPropertyNames()).thenReturn(Arrays.asList("prop1", "prop2"));
    EdmProperty prop1 = mock(EdmProperty.class);
    when(prop1.isPrimitive()).thenReturn(true);
    when(prop1.getType()).thenReturn(stringType);
    when(prop1.isNullable()).thenReturn(true);
    when(prop1.getMaxLength()).thenReturn(null);
    when(prop1.getPrecision()).thenReturn(null);
    when(prop1.getScale()).thenReturn(null);
    when(prop1.isUnicode()).thenReturn(true);
    when(complexType.getStructuralProperty("prop1")).thenReturn(prop1);
    EdmProperty prop2 = mock(EdmProperty.class);
    when(prop2.isPrimitive()).thenReturn(true);
    when(prop2.getType()).thenReturn(stringType);
    when(prop2.isNullable()).thenReturn(true);
    when(prop2.getMaxLength()).thenReturn(null);
    when(prop2.getPrecision()).thenReturn(null);
    when(prop2.getScale()).thenReturn(null);
    when(prop2.isUnicode()).thenReturn(true);
    when(complexType.getStructuralProperty("prop2")).thenReturn(prop2);
    complexTypes.add(complexType);

    EdmComplexType c1 = mock(EdmComplexType.class);
    when(c1.getFullQualifiedName()).thenReturn(new FullQualifiedName("namespace", "C1"));
    when(c1.getName()).thenReturn("C1");
    when(c1.getBaseType()).thenReturn(complexType);
    complexTypes.add(c1);

    when(schema.getComplexTypes()).thenReturn(complexTypes);

    InputStream metadataStream = serializer.metadataDocument(serviceMetadata).getContent();
    String metadata = IOUtils.toString(metadataStream);
    assertTrue(metadata.contains("<ComplexType Name=\"ComplexType\" Abstract=\"true\" OpenType=\"true\">"
        + "<Property Name=\"prop1\" Type=\"Edm.String\"></Property>"
        + "<Property Name=\"prop2\" Type=\"Edm.String\"></Property>"
        + "</ComplexType>"));
  }

  static class LocalProvider implements CsdlEdmProvider {
    private final static String nameSpace = "namespace";

    private final FullQualifiedName nameETAbstract = new FullQualifiedName(nameSpace, "ETAbstract");
    private final FullQualifiedName nameETAbstractBase = new FullQualifiedName(nameSpace, "ETAbstractBase");

    private final FullQualifiedName nameInt16 = EdmPrimitiveTypeKind.Int16.getFullQualifiedName();
    private final FullQualifiedName nameString = EdmPrimitiveTypeKind.String.getFullQualifiedName();
    private final FullQualifiedName nameUARTPrimParam = new FullQualifiedName(nameSpace, "UARTPrimParam");
    private final CsdlProperty propertyInt16_NotNullable = new CsdlProperty()
    .setName("PropertyInt16")
    .setType(nameInt16)
    .setNullable(false);
    private final CsdlProperty propertyString = new CsdlProperty()
    .setName("PropertyString")
    .setType(nameString);

    private final FullQualifiedName nameCTTwoPrim = new FullQualifiedName(nameSpace, "CTTwoPrim");
    private final FullQualifiedName nameCTTwoPrimBase = new FullQualifiedName(nameSpace, "CTTwoPrimBase");
    private final FullQualifiedName nameUFNRTInt16 = new FullQualifiedName(nameSpace, "UFNRTInt16");
    private final FullQualifiedName nameContainer = new FullQualifiedName(nameSpace, "container");
    private final FullQualifiedName nameENString = new FullQualifiedName(nameSpace, "ENString");

    @Override
    public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
      return Collections.singletonList(new CsdlAliasInfo().setAlias("Alias").setNamespace(nameSpace));
    }

    @Override
    public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
      if (nameENString.equals(enumTypeName)) {
        return new CsdlEnumType()
        .setName(nameENString.getName())
        .setFlags(true)
        .setUnderlyingType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName())
        .setMembers(Collections.singletonList(new CsdlEnumMember().setName("String1").setValue("1")));
      }
      return null;
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
      if (entityTypeName.equals(nameETAbstract)) {
        return new CsdlEntityType()
        .setName("ETAbstract")
        .setAbstract(true)
        .setProperties(Collections.singletonList(propertyString));

      } else if (entityTypeName.equals(nameETAbstractBase)) {
        return new CsdlEntityType()
        .setName("ETAbstractBase")
        .setBaseType(nameETAbstract)
        .setKey(Collections.singletonList(new CsdlPropertyRef().setName("PropertyInt16")))
        .setProperties(Collections.singletonList(propertyInt16_NotNullable));
      }
      return null;
    }

    @Override
    public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
      if (complexTypeName.equals(nameCTTwoPrim)) {
        return new CsdlComplexType()
        .setName("CTTwoPrim")
        .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString));

      }
      if (complexTypeName.equals(nameCTTwoPrimBase)) {
        return new CsdlComplexType()
        .setName("CTTwoPrimBase")
        .setBaseType(nameCTTwoPrim)
        .setProperties(Arrays.asList(propertyInt16_NotNullable, propertyString));
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
      return null;
    }

    @Override
    public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
        throws ODataException {
      if (entityContainer.equals(nameContainer)) {
        if (actionImportName.equals("AIRTPrimParam")) {
          return new CsdlActionImport()
          .setName("AIRTPrimParam")
          .setAction(nameUARTPrimParam);
        }
      }
      return null;
    }

    @Override
    public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer,
        final String functionImportName)
            throws ODataException {
      if (entityContainer.equals(nameContainer)) {
        if (functionImportName.equals("FINRTInt16")) {
          return new CsdlFunctionImport()
          .setName("FINRTInt16")
          .setFunction(nameUFNRTInt16)
          .setIncludeInServiceDocument(true);
        }
      }
      return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
      List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
      CsdlSchema schema = new CsdlSchema();
      schema.setNamespace(nameSpace);
      schema.setAlias("Alias");
      schemas.add(schema);

      // EnumTypes
      schema.setEnumTypes(Collections.singletonList(getEnumType(nameENString)));

      // EntityTypes
      schema.setEntityTypes(Arrays.asList(
          getEntityType(nameETAbstract),
          getEntityType(nameETAbstractBase)));

      // ComplexTypes
      schema.setComplexTypes(Arrays.asList(
          getComplexType(nameCTTwoPrim),
          getComplexType(nameCTTwoPrimBase)));

      // TypeDefinitions

      // Actions
      schema.setActions(getActions(nameUARTPrimParam));

      // Functions
      schema.setFunctions(getFunctions(nameUFNRTInt16));

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
      schema.setAnnotationsGroup(Collections.singletonList(
          getAnnotationsGroup(new FullQualifiedName("Alias", "ETAbstract"), "Tablett")));

      return schemas;
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
      container.setEntitySets(Collections.singletonList(getEntitySet(nameContainer, "ESAllPrim")));

      // Singletons
      container.setSingletons(Collections.singletonList(getSingleton(nameContainer, "SI")));

      // ActionImports
      container.setActionImports(Collections.singletonList(getActionImport(nameContainer, "AIRTPrimParam")));

      // FunctionImports
      container.setFunctionImports(Collections.singletonList(getFunctionImport(nameContainer, "FINRTInt16")));

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

      }
      return null;
    }

    @Override
    public CsdlAnnotations getAnnotationsGroup(final FullQualifiedName targetName, final String qualifier)
        throws ODataException {
      if (new FullQualifiedName("Alias", "ETAbstract").equals(targetName) && "Tablett".equals(qualifier)) {
        CsdlAnnotations annoGroup = new CsdlAnnotations();
        annoGroup.setTarget("Alias.ETAbstract");
        annoGroup.setQualifier("Tablett");

        List<CsdlAnnotation> innerAnnotations = Collections.singletonList(
            new CsdlAnnotation().setTerm("ns.term"));

        List<CsdlAnnotation> annotationsList = new ArrayList<CsdlAnnotation>();
        annoGroup.setAnnotations(annotationsList);
        // Constant Annotations
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Binary).setValue("qrvM3e7_")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Bool, "true")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Date, "2012-02-29")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.DateTimeOffset, "2012-02-29T01:02:03Z")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Decimal, "-12345678901234567234567890")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Duration, "PT10S")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.EnumMember, "Enum/enumMember")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Float, "1.42")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(
                new CsdlConstantExpression(ConstantExpressionType.Guid, "aabbccdd-aabb-ccdd-eeff-aabbccddeeff")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.Int, "42")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.String, "ABCD")));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.TimeOfDay, "00:00:00.999")));

        // logical expressions
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.And)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Or)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Not)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setAnnotations(innerAnnotations)));

        // comparison
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Eq)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Ne)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Gt)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Ge)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Lt)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.Le)
            .setLeft(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setRight(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"))
            .setAnnotations(innerAnnotations)));

        // Other
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlAnnotationPath().setValue("AnnoPathValue")));

        List<CsdlExpression> parameters = new ArrayList<CsdlExpression>();
        parameters.add(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlApply().setFunction("odata.concat")
                .setParameters(parameters)
                .setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlCast()
            .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
            .setMaxLength(1)
            .setPrecision(2)
            .setScale(3)
            .setType("Edm.String")
            .setAnnotations(innerAnnotations)));

        List<CsdlExpression> items = new ArrayList<CsdlExpression>();
        items.add(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"));
        items.add(new CsdlConstantExpression(ConstantExpressionType.Bool, "false"));
        items.add(new CsdlConstantExpression(ConstantExpressionType.String, "String"));
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlCollection().setItems(items)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlIf()
            .setGuard(new CsdlConstantExpression(ConstantExpressionType.Bool, "true"))
            .setThen(new CsdlConstantExpression(ConstantExpressionType.String, "Then"))
            .setElse(new CsdlConstantExpression(ConstantExpressionType.String, "Else"))
            .setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlIsOf()
            .setMaxLength(1)
            .setPrecision(2)
            .setScale(3)
            .setType("Edm.String")
            .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
            .setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLabeledElement()
            .setName("NameAtt")
            .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
            .setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlLabeledElementReference().setValue("LabeledElementReferenceValue")));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlNull().setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlNavigationPropertyPath().setValue("NavigationPropertyPathValue")));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlPath().setValue("PathValue")));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlPropertyPath().setValue("PropertyPathValue")));

        CsdlPropertyValue prop = new CsdlPropertyValue()
        .setProperty("PropName")
        .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "value"))
        .setAnnotations(innerAnnotations);
        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlRecord().setType("Alias.ETAbstract")
                .setPropertyValues(Arrays.asList(prop))
                .setAnnotations(innerAnnotations)));

        annotationsList.add(new CsdlAnnotation().setTerm("ns.term")
            .setExpression(new CsdlUrlRef()
            .setValue(new CsdlConstantExpression(ConstantExpressionType.String, "URLRefValue"))
            .setAnnotations(innerAnnotations)));

        return annoGroup;
      }
      return null;
    }
  }
}
