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
package org.apache.olingo.commons.core.edm.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.annotation.EdmApply;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlApply;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression;
//CHECKSTYLE:OFF
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression.LogicalOrComparisonExpressionType;
//CHECKSTYLE:ON
import org.apache.olingo.commons.core.edm.annotation.AbstractEdmExpression;
import org.junit.Test;

public class EdmApplyImplTest extends AbstractAnnotationTest {

  @Test
  public void initialApply() {
    EdmExpression apply = AbstractEdmExpression.getExpression(mock(Edm.class), new CsdlApply());

    EdmDynamicExpression dynExp = assertDynamic(apply);
    assertTrue(dynExp.isApply());
    assertNotNull(dynExp.asApply());

    assertEquals("Apply", dynExp.getExpressionName());
    assertEquals(EdmExpressionType.Apply, dynExp.getExpressionType());
    assertSingleKindDynamicExpression(dynExp);

    EdmApply asApply = dynExp.asApply();

    try {
      asApply.getFunction();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("An Apply expression must specify a function.", e.getMessage());
    }
    assertNotNull(asApply.getParameters());
    assertTrue(asApply.getParameters().isEmpty());

    assertNotNull(asApply.getAnnotations());
    assertTrue(asApply.getAnnotations().isEmpty());
  }

  @Test
  public void functionAndNoParameters() {
    EdmExpression apply = AbstractEdmExpression.getExpression(mock(Edm.class), new CsdlApply().setFunction("Function"));

    EdmDynamicExpression dynExp = assertDynamic(apply);
    EdmApply asApply = dynExp.asApply();

    assertEquals("Function", asApply.getFunction());
    assertNotNull(asApply.getParameters());
    assertTrue(asApply.getParameters().isEmpty());
  }

  @Test
  public void functionWithParameters() {
    CsdlApply csdlApply = new CsdlApply();
    csdlApply.setFunction("Function");

    List<CsdlExpression> parameters = new ArrayList<CsdlExpression>();
    parameters.add(new CsdlConstantExpression(ConstantExpressionType.String));
    parameters.add(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.And));
    csdlApply.setParameters(parameters);

    List<CsdlAnnotation> csdlAnnotations = new ArrayList<CsdlAnnotation>();
    csdlAnnotations.add(new CsdlAnnotation().setTerm("ns.term"));
    csdlApply.setAnnotations(csdlAnnotations);

    EdmExpression apply = AbstractEdmExpression.getExpression(mock(Edm.class), csdlApply);

    EdmDynamicExpression dynExp = assertDynamic(apply);
    EdmApply asApply = dynExp.asApply();

    assertEquals("Function", asApply.getFunction());
    assertNotNull(asApply.getParameters());
    assertEquals(2, asApply.getParameters().size());
    assertTrue(asApply.getParameters().get(0).isConstant());
    assertTrue(asApply.getParameters().get(1).isDynamic());

    assertNotNull(asApply.getAnnotations());
    assertEquals(1, asApply.getAnnotations().size());
  }
}
