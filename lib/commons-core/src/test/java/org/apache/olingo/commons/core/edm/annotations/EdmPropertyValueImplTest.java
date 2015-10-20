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
import org.apache.olingo.commons.api.edm.annotation.EdmPropertyValue;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.core.edm.annotation.EdmPropertyValueImpl;
import org.junit.Test;

public class EdmPropertyValueImplTest extends AbstractAnnotationTest {
  @Test
  public void initialPropertyValue() {
    EdmPropertyValue asPropValue = new EdmPropertyValueImpl(mock(Edm.class), new CsdlPropertyValue());
    try {
      asPropValue.getProperty();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("PropertyValue expressions require a referenced property value.", e.getMessage());
    }

    try {
      asPropValue.getValue();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("PropertyValue expressions require an expression value.", e.getMessage());
    }

    assertNotNull(asPropValue.getAnnotations());
    assertTrue(asPropValue.getAnnotations().isEmpty());
  }

  @Test
  public void propertyValue() {
    CsdlPropertyValue csdlPropertyValue = new CsdlPropertyValue();
    csdlPropertyValue.setProperty("property");
    csdlPropertyValue.setValue(new CsdlConstantExpression(ConstantExpressionType.String));
    List<CsdlAnnotation> csdlAnnotations = new ArrayList<CsdlAnnotation>();
    csdlAnnotations.add(new CsdlAnnotation().setTerm("ns.term"));
    csdlPropertyValue.setAnnotations(csdlAnnotations);
    EdmPropertyValue asPropValue = new EdmPropertyValueImpl(mock(Edm.class), csdlPropertyValue);

    assertNotNull(asPropValue.getProperty());
    assertEquals("property", asPropValue.getProperty());
    assertNotNull(asPropValue.getValue());
    assertTrue(asPropValue.getValue().isConstant());

    assertNotNull(asPropValue.getAnnotations());
    assertEquals(1, asPropValue.getAnnotations().size());
  }
}
