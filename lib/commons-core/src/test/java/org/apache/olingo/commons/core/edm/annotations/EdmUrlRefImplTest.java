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
import org.apache.olingo.commons.api.edm.annotation.EdmUrlRef;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlNull;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlUrlRef;
import org.apache.olingo.commons.core.edm.annotation.AbstractEdmExpression;
import org.junit.Test;

public class EdmUrlRefImplTest extends AbstractAnnotationTest {

  @Test
  public void initialUrlRef() {
    EdmExpression exp = AbstractEdmExpression.getExpression(mock(Edm.class), new CsdlUrlRef());

    EdmDynamicExpression dynExp = assertDynamic(exp);
    assertTrue(dynExp.isUrlRef());
    assertNotNull(dynExp.asUrlRef());

    assertEquals("UrlRef", dynExp.getExpressionName());
    assertEquals(EdmExpressionType.UrlRef, dynExp.getExpressionType());
    assertSingleKindDynamicExpression(dynExp);

    EdmUrlRef asUrlRef = dynExp.asUrlRef();
    try {
      asUrlRef.getValue();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("URLRef expressions require an expression value.", e.getMessage());
    }

    assertNotNull(asUrlRef.getAnnotations());
    assertTrue(asUrlRef.getAnnotations().isEmpty());
  }

  @Test
  public void urlRefWithValue() {
    CsdlUrlRef csdlUrlRef = new CsdlUrlRef();
    csdlUrlRef.setValue(new CsdlConstantExpression(ConstantExpressionType.String));
    List<CsdlAnnotation> csdlAnnotations = new ArrayList<CsdlAnnotation>();
    csdlAnnotations.add(new CsdlAnnotation().setTerm("ns.term"));
    csdlUrlRef.setAnnotations(csdlAnnotations);
    EdmExpression exp = AbstractEdmExpression.getExpression(mock(Edm.class), csdlUrlRef);
    EdmUrlRef asUrlRef = exp.asDynamic().asUrlRef();
    assertNotNull(asUrlRef.getValue());
    assertTrue(asUrlRef.getValue().isConstant());

    assertNotNull(asUrlRef.getAnnotations());
    assertEquals(1, asUrlRef.getAnnotations().size());
  }

  @Test
  public void urlRefWithInvalidValue() {
    CsdlUrlRef csdlUrlRef = new CsdlUrlRef();
    csdlUrlRef.setValue(new CsdlConstantExpression(ConstantExpressionType.Bool));
    EdmExpression exp = AbstractEdmExpression.getExpression(mock(Edm.class), csdlUrlRef);
    EdmUrlRef asUrlRef = exp.asDynamic().asUrlRef();
    assertNotNull(asUrlRef.getValue());
    assertTrue(asUrlRef.getValue().isConstant());

    csdlUrlRef = new CsdlUrlRef();
    csdlUrlRef.setValue(new CsdlNull());
    exp = AbstractEdmExpression.getExpression(mock(Edm.class), csdlUrlRef);
    asUrlRef = exp.asDynamic().asUrlRef();
    assertNotNull(asUrlRef.getValue());
    assertTrue(asUrlRef.getValue().isDynamic());
    assertTrue(asUrlRef.getValue().asDynamic().isNull());
    assertNotNull(asUrlRef.getValue().asDynamic().asNull());
  }
}
