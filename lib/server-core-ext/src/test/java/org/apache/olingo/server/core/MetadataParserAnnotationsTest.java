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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.util.Arrays;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlAnnotationPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlApply;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCast;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCollection;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlIf;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlIsOf;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLabeledElement;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLabeledElementReference;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlNavigationPropertyPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlNull;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPath;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlRecord;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlUrlRef;
import org.apache.olingo.commons.api.ex.ODataException;
import org.junit.Before;
import org.junit.Test;

public class MetadataParserAnnotationsTest {
  final String NS = "Org.OData.AnnoatationTest";
  final FullQualifiedName NSF = new FullQualifiedName(NS);

  CsdlEdmProvider provider = null;

  @Before
  public void setUp() throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.parseAnnotations(true);
    parser.useLocalCoreVocabularies(true);
    provider = (CsdlEdmProvider) parser.buildEdmProvider(new FileReader("src/test/resources/annotations.xml"));
  }

  CsdlAnnotation annotation(String term) throws ODataException {
    CsdlSchema schema  = provider.getSchemas().get(0);
    assertNotNull(schema.getAnnotations());
    
    return schema.getAnnotation(term);
  }
  
  @Test
  public void testConstantExpressionAttribute() throws ODataException {
    CsdlAnnotation a = annotation("Core.Description");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlConstantExpression);
    assertEquals("Core terms needed to write vocabularies", ((CsdlConstantExpression)a.getExpression()).getValue());
    assertEquals(ConstantExpressionType.String, ((CsdlConstantExpression)a.getExpression()).getType());
    
    a = annotation("org.example.display.LastUpdated");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlConstantExpression);
    assertEquals("2000-01-01T16:00:00.000-09:00", ((CsdlConstantExpression)a.getExpression()).getValue());
    assertEquals(ConstantExpressionType.DateTimeOffset, ((CsdlConstantExpression)a.getExpression()).getType());
  }

  @Test
  public void testCollection() throws ODataException {
    CsdlAnnotation a = annotation("UI.CollectionFacet");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlCollection);
    CsdlCollection exprs = ((CsdlCollection)a.getExpression());
    assertEquals(2, exprs.getItems().size());
    assertTrue(exprs.getItems().get(0) instanceof CsdlAnnotationPath);
    CsdlAnnotationPath path = (CsdlAnnotationPath) exprs.getItems().get(0);
    assertEquals("Supplier/@Communication.Contact", path.getValue());
  }
  
  @Test
  public void testApply() throws ODataException {
    CsdlAnnotation a = annotation("org.example.display.DisplayNameApply");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlApply);
    CsdlApply apply = ((CsdlApply)a.getExpression());

    assertEquals("odata.concat", apply.getFunction());
    assertEquals(7, apply.getParameters().size());
    assertTrue(apply.getParameters().get(1) instanceof CsdlPath);
    assertTrue(apply.getParameters().get(4) instanceof CsdlConstantExpression);
    
    assertEquals("OData.Description", apply.getAnnotations().get(0).getTerm());
    assertEquals("concat apply", apply.getAnnotations().get(0).getExpression().asConstant().getValue());
  }  
  
  @Test
  public void testCast() throws ODataException {
    CsdlAnnotation a = annotation("org.example.display.Threshold");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlCast);
    CsdlCast cast= (CsdlCast)a.getExpression();

    assertEquals("Edm.Decimal", cast.getType());
    assertTrue(cast.getValue() instanceof CsdlPath);
    assertEquals("Average", ((CsdlPath)cast.getValue()).getValue());
  }  
  
  @Test
  public void testIf() throws ODataException {
    CsdlAnnotation a = annotation("org.example.person.Gender");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlIf);
    CsdlIf ifexpr = (CsdlIf)a.getExpression();

    assertTrue(ifexpr.getGuard() instanceof CsdlPath);
    assertTrue(ifexpr.getThen() instanceof CsdlConstantExpression);
    assertTrue(ifexpr.getElse() instanceof CsdlConstantExpression);
    
    assertEquals("IsFemale", ((CsdlPath)ifexpr.getGuard()).getValue());
    assertEquals("Female", ((CsdlConstantExpression)ifexpr.getThen()).getValue());
    assertEquals("Male", ((CsdlConstantExpression)ifexpr.getElse()).getValue());
  }  
  
  @Test
  public void testIsOf() throws ODataException {
    CsdlAnnotation a = annotation("Self.IsPreferredCustomer");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlIsOf);
    CsdlIsOf isOf = (CsdlIsOf)a.getExpression();
    assertEquals("Self.PreferredCustomer", isOf.getType());
    assertTrue(isOf.getValue() instanceof CsdlPath);
    
    assertEquals("OData.Description", isOf.getAnnotations().get(0).getTerm());
    assertEquals("preferred customer", isOf.getAnnotations().get(0).getExpression().asConstant().getValue());
    
  }
  
  @Test
  public void testLableledElement() throws ODataException {
    CsdlAnnotation a = annotation("org.example.display.DisplayNameLabel");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlLabeledElement);
    CsdlLabeledElement expr = (CsdlLabeledElement)a.getExpression();
    assertEquals("CustomerFirstName", expr.getName());
    assertTrue(expr.getValue() instanceof CsdlPath);
  }
  
  @Test
  public void testLableledReference() throws ODataException {
    CsdlAnnotation a = annotation("org.example.display.DisplayNameLabelReference");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlLabeledElementReference);
    CsdlLabeledElementReference expr = (CsdlLabeledElementReference)a.getExpression();
    assertEquals("Model.CustomerFirstName", expr.getValue());
  }
  
  @Test
  public void testNull() throws ODataException {
    CsdlAnnotation a = annotation("org.example.display.DisplayNameNull");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlNull);
  }
  
  @Test
  public void testRecord() throws ODataException {
    CsdlAnnotation a = annotation("Capabilities.UpdateRestrictions");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlRecord);
    CsdlRecord expr = (CsdlRecord)a.getExpression();
    assertEquals(1, expr.getPropertyValues().size());
    CsdlPropertyValue value = expr.getPropertyValues().get(0);
    assertEquals("NonUpdatableNavigationProperties", value.getProperty());
    assertTrue(value.getValue() instanceof CsdlCollection);
    assertEquals("OData.Description", expr.getAnnotations().get(0).getTerm());
    assertEquals("descripiton test", expr.getAnnotations().get(0).getExpression().asConstant().getValue());
    
    CsdlCollection collection = (CsdlCollection)value.getValue(); 
    assertEquals(2, collection.getItems().size());
    assertEquals("Category", ((CsdlNavigationPropertyPath)collection.getItems().get(1)).getValue());
  }
  
  @Test
  public void testUrlRef() throws ODataException {
    CsdlAnnotation a = annotation("Vocab.Supplier");
    assertNotNull(a);
    assertTrue(a.getExpression() instanceof CsdlUrlRef);
    CsdlUrlRef expr = (CsdlUrlRef)a.getExpression();
    assertTrue(expr.getValue() instanceof CsdlApply);
    assertEquals(2, ((CsdlApply)expr.getValue()).getParameters().size());
  }
  
  @Test
  public void testTermAppliesTo() throws ODataException {
    CsdlTerm term = this.provider.getTerm(new FullQualifiedName(NS, "IsURI"));
    assertEquals(Arrays.asList("Property", "PropertyPath"), term.getAppliesTo());
  }
  
  @Test
  public void checkCoreVocabularies() throws ODataException {
    CsdlTerm term = this.provider.getTerm(new FullQualifiedName("Org.OData.Core.V1", "Description"));
    assertEquals("Edm.String", term.getType());
  }
  
  @Test
  public void testAnnotationGroup() throws ODataException {
    CsdlAnnotations annotations = this.provider.getAnnotationsGroup(
        new FullQualifiedName("Org.OData.AnnoatationTest.TagX"), null);
    assertEquals(3, annotations.getAnnotations().size());
  }  
}
