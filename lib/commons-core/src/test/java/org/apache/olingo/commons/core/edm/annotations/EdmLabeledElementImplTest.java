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
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmLabeledElement;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLabeledElement;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.core.edm.annotation.AbstractEdmExpression;
import org.junit.Test;

public class EdmLabeledElementImplTest extends AbstractAnnotationTest {

  @Test
  public void initialLabeledElement() {
    EdmExpression exp = AbstractEdmExpression.getExpression(mock(Edm.class), new CsdlLabeledElement());

    EdmDynamicExpression dynExp = assertDynamic(exp);
    assertTrue(dynExp.isLabeledElement());
    assertNotNull(dynExp.asLabeledElement());

    assertEquals("LabeledElement", dynExp.getExpressionName());
    assertEquals(EdmExpressionType.LabeledElement, dynExp.getExpressionType());
    assertSingleKindDynamicExpression(dynExp);

    EdmLabeledElement asLabeled = dynExp.asLabeledElement();

    try {
      asLabeled.getName();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("The LabeledElement expression must have a name attribute.", e.getMessage());
    }

    try {
      asLabeled.getValue();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("The LabeledElement expression must have a child expression", e.getMessage());
    }

    assertNotNull(asLabeled.getAnnotations());
    assertTrue(asLabeled.getAnnotations().isEmpty());
  }

  @Test
  public void labeledElementWithNameAndValue() {
    CsdlLabeledElement csdlLabeledElement = new CsdlLabeledElement();
    csdlLabeledElement.setName("name");
    csdlLabeledElement.setValue(new CsdlConstantExpression(ConstantExpressionType.String));
    List<CsdlAnnotation> csdlAnnotations = new ArrayList<CsdlAnnotation>();
    csdlAnnotations.add(new CsdlAnnotation().setTerm("ns.term"));
    csdlLabeledElement.setAnnotations(csdlAnnotations);
    EdmExpression exp = AbstractEdmExpression.getExpression(mock(Edm.class), csdlLabeledElement);
    EdmLabeledElement asLabeled = exp.asDynamic().asLabeledElement();

    assertEquals("name", asLabeled.getName());
    assertNotNull(asLabeled.getValue());
    assertTrue(asLabeled.getValue().isConstant());

    assertNotNull(asLabeled.getAnnotations());
    assertEquals(1, asLabeled.getAnnotations().size());
  }
}
