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

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.annotation.EdmCollection;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression.EdmExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCollection;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression.ConstantExpressionType;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression;
//CHECKSTYLE:OFF
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression.LogicalOrComparisonExpressionType;
//CHECKSTYLE:ON
import org.apache.olingo.commons.core.edm.annotation.AbstractEdmExpression;
import org.junit.Test;

public class EdmCollectionImplTest extends AbstractAnnotationTest {

  @Test
  public void initialCollection() {
    EdmExpression exp = AbstractEdmExpression.getExpression(mock(Edm.class), new CsdlCollection());

    EdmDynamicExpression dynExp = assertDynamic(exp);
    assertTrue(dynExp.isCollection());
    assertNotNull(dynExp.asCollection());

    assertEquals("Collection", dynExp.getExpressionName());
    assertEquals(EdmExpressionType.Collection, dynExp.getExpressionType());
    assertSingleKindDynamicExpression(dynExp);

    EdmCollection asCollection = dynExp.asCollection();

    assertNotNull(asCollection.getItems());
    assertTrue(asCollection.getItems().isEmpty());
  }

  @Test
  public void collectionWithThreeItems() {
    CsdlCollection csdlCollection = new CsdlCollection();
    List<CsdlExpression> items = new ArrayList<CsdlExpression>();
    items.add(new CsdlConstantExpression(ConstantExpressionType.String));
    items.add(new CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType.And));
    items.add(new CsdlConstantExpression(ConstantExpressionType.Bool));
    csdlCollection.setItems(items);
    EdmExpression exp = AbstractEdmExpression.getExpression(mock(Edm.class), csdlCollection);
    EdmCollection asCollection = exp.asDynamic().asCollection();

    assertNotNull(asCollection.getItems());
    assertEquals(3, asCollection.getItems().size());

    assertTrue(asCollection.getItems().get(0).isConstant());
    assertTrue(asCollection.getItems().get(1).isDynamic());
    assertTrue(asCollection.getItems().get(2).isConstant());
  }

}
