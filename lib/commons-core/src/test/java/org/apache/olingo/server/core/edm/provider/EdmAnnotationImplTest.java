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
package org.apache.olingo.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.core.edm.EdmAnnotationImpl;
import org.junit.Test;

public class EdmAnnotationImplTest {

  @Test
  public void initialAnnotation() {
    EdmAnnotation anno = new EdmAnnotationImpl(mock(Edm.class), new CsdlAnnotation());

    assertNull(anno.getQualifier());
    assertNotNull(anno.getAnnotations());
    assertTrue(anno.getAnnotations().isEmpty());
    assertNull(anno.getExpression());
    try {
      anno.getTerm();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("Term must not be null for an annotation.", e.getMessage());
    }
  }

  @Test
  public void simpleAnnotationNoExpression() {
    Edm mock = mock(Edm.class);
    EdmTerm termMock = mock(EdmTerm.class);
    when(mock.getTerm(new FullQualifiedName("ns", "termName"))).thenReturn(termMock);
    EdmAnnotation anno =
        new EdmAnnotationImpl(mock, new CsdlAnnotation().setQualifier("Qualifier").setTerm("ns.termName"));

    assertEquals("Qualifier", anno.getQualifier());
    assertNotNull(anno.getAnnotations());
    assertTrue(anno.getAnnotations().isEmpty());
    assertNotNull(anno.getTerm());
    assertEquals(termMock, anno.getTerm());
  }

  @Test
  public void simpleAnnotationWitConstantExpression() {
    EdmAnnotation anno =
        new EdmAnnotationImpl(mock(Edm.class), new CsdlAnnotation()
            .setExpression(new CsdlConstantExpression(ConstantExpressionType.String).setValue("value")));

    assertEquals("value", anno.getExpression().asConstant().getValueAsString());
  }

}
