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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmRecord;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlPropertyValue;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlRecord;
import org.apache.olingo.commons.core.edm.annotation.AbstractEdmExpression;
import org.junit.Test;

public class EdmRecordImplTest extends AbstractAnnotationTest {

  @Test
  public void initialRecord() {
    EdmExpression record = AbstractEdmExpression.getExpression(mock(Edm.class), new CsdlRecord());

    EdmDynamicExpression dynExp = assertDynamic(record);
    assertTrue(dynExp.isRecord());
    assertNotNull(dynExp.asRecord());

    assertEquals("Record", dynExp.getExpressionName());
    assertEquals(EdmExpressionType.Record, dynExp.getExpressionType());
    assertNotNull(dynExp.asRecord().getPropertyValues());
    assertTrue(dynExp.asRecord().getPropertyValues().isEmpty());

    assertSingleKindDynamicExpression(dynExp);

    EdmRecord asRecord = dynExp.asRecord();

    assertNotNull(asRecord.getAnnotations());
    assertTrue(asRecord.getAnnotations().isEmpty());
  }

  @Test
  public void recordWithEntityTypeAndPropValues() {
    CsdlRecord csdlRecord = new CsdlRecord();
    csdlRecord.setType("ns.et");
    Edm mock = mock(Edm.class);
    when(mock.getEntityType(new FullQualifiedName("ns", "et"))).thenReturn(mock(EdmEntityType.class));
    List<CsdlPropertyValue> propertyValues = new ArrayList<CsdlPropertyValue>();
    propertyValues.add(new CsdlPropertyValue());
    csdlRecord.setPropertyValues(propertyValues);
    List<CsdlAnnotation> csdlAnnotations = new ArrayList<CsdlAnnotation>();
    csdlAnnotations.add(new CsdlAnnotation().setTerm("ns.term"));
    csdlRecord.setAnnotations(csdlAnnotations);
    EdmExpression record = AbstractEdmExpression.getExpression(mock, csdlRecord);

    EdmDynamicExpression dynExp = assertDynamic(record);
    EdmRecord asRecord = dynExp.asRecord();

    assertNotNull(asRecord.getPropertyValues());
    assertEquals(1, asRecord.getPropertyValues().size());

    assertNotNull(asRecord.getType());
    assertTrue(asRecord.getType() instanceof EdmEntityType);

    assertNotNull(asRecord.getAnnotations());
    assertEquals(1, asRecord.getAnnotations().size());
  }

  @Test
  public void recordWithComplexType() {
    CsdlRecord csdlRecord = new CsdlRecord();
    csdlRecord.setType("ns.ct");
    Edm mock = mock(Edm.class);
    when(mock.getComplexType(new FullQualifiedName("ns", "ct"))).thenReturn(mock(EdmComplexType.class));
    EdmExpression record = AbstractEdmExpression.getExpression(mock, csdlRecord);

    EdmDynamicExpression dynExp = assertDynamic(record);
    EdmRecord asRecord = dynExp.asRecord();

    assertNotNull(asRecord.getType());
    assertTrue(asRecord.getType() instanceof EdmComplexType);
  }

  @Test
  public void recordWithInvalidTypes() {
    Edm edm = mock(Edm.class);
    EdmExpression exp = AbstractEdmExpression.getExpression(edm, new CsdlRecord().setType("ns.invalid"));
    EdmRecord record = exp.asDynamic().asRecord();
    try {
      record.getType();
    } catch (EdmException e) {
      assertEquals("Record expressions must specify a complex or entity type.", e.getMessage());
    }

    // Primitive
    exp = AbstractEdmExpression.getExpression(edm, new CsdlRecord().setType("Edm.String"));
    record = exp.asDynamic().asRecord();
    try {
      record.getType();
    } catch (EdmException e) {
      assertEquals("Record expressions must specify a complex or entity type.", e.getMessage());
    }

    // Enum
    when(edm.getEnumType(new FullQualifiedName("ns", "enum"))).thenReturn(mock(EdmEnumType.class));
    exp = AbstractEdmExpression.getExpression(edm, new CsdlRecord().setType("ns.enum"));
    record = exp.asDynamic().asRecord();
    try {
      record.getType();
    } catch (EdmException e) {
      assertEquals("Record expressions must specify a complex or entity type.", e.getMessage());
    }

    // Typedef
    when(edm.getTypeDefinition(new FullQualifiedName("ns", "typedef"))).thenReturn(mock(EdmTypeDefinition.class));
    exp = AbstractEdmExpression.getExpression(edm, new CsdlRecord().setType("ns.typedef"));
    record = exp.asDynamic().asRecord();
    try {
      record.getType();
    } catch (EdmException e) {
      assertEquals("Record expressions must specify a complex or entity type.", e.getMessage());
    }
  }

}
