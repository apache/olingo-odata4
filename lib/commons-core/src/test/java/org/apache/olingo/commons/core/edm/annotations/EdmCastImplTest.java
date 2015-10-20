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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.annotation.EdmCast;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCast;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.core.edm.annotation.AbstractEdmExpression;
import org.junit.Test;

public class EdmCastImplTest extends AbstractAnnotationTest{
  
  @Test
  public void initialCast() {
    EdmExpression cast = AbstractEdmExpression.getExpression(mock(Edm.class), new CsdlCast());

    EdmDynamicExpression dynExp = assertDynamic(cast);
    assertTrue(dynExp.isCast());
    assertNotNull(dynExp.asCast());

    assertEquals("Cast", dynExp.getExpressionName());
    assertEquals(EdmExpressionType.Cast, dynExp.getExpressionType());
    assertSingleKindDynamicExpression(dynExp);
    try {
      dynExp.asCast().getValue();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("Cast expressions require an expression value.", e.getMessage());
    }

    EdmCast asCast = dynExp.asCast();
    assertNull(asCast.getMaxLength());
    assertNull(asCast.getPrecision());
    assertNull(asCast.getScale());
    assertNull(asCast.getSrid());
    try {
      asCast.getType();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("Must specify a type for a Cast expression.", e.getMessage());
    }

    assertNotNull(asCast.getAnnotations());
    assertTrue(asCast.getAnnotations().isEmpty());
  }

  @Test
  public void castWithExpression() {
    CsdlCast csdlExp = new CsdlCast();
    csdlExp.setMaxLength(new Integer(1));
    csdlExp.setPrecision(new Integer(2));
    csdlExp.setScale(new Integer(3));
    csdlExp.setType("Edm.String");
    csdlExp.setValue(new CsdlConstantExpression(ConstantExpressionType.String));
    List<CsdlAnnotation> csdlAnnotations = new ArrayList<CsdlAnnotation>();
    csdlAnnotations.add(new CsdlAnnotation().setTerm("ns.term"));
    csdlExp.setAnnotations(csdlAnnotations);
    EdmExpression isOf = AbstractEdmExpression.getExpression(mock(Edm.class), csdlExp);

    EdmCast asIsOf = isOf.asDynamic().asCast();

    assertEquals(new Integer(1), asIsOf.getMaxLength());
    assertEquals(new Integer(2), asIsOf.getPrecision());
    assertEquals(new Integer(3), asIsOf.getScale());

    assertNotNull(asIsOf.getType());
    assertTrue(asIsOf.getType() instanceof EdmPrimitiveType);

    assertNotNull(asIsOf.getValue());
    assertTrue(asIsOf.getValue().isConstant());

    assertNotNull(asIsOf.getAnnotations());
    assertEquals(1, asIsOf.getAnnotations().size());
  }
}
