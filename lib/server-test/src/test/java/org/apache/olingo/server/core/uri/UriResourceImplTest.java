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
package org.apache.olingo.server.core.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.core.uri.queryoption.expression.ExpressionImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.junit.Test;

public class UriResourceImplTest {

  private static final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  @Test
  public void testUriParameterImpl() {
    UriParameterImpl impl = new UriParameterImpl();
    ExpressionImpl expression = new LiteralImpl().setText("Expression");

    impl.setText("Text");
    impl.setName("A");
    impl.setAlias("@A");
    impl.setExpression(expression);

    assertEquals("Text", impl.getText());
    assertEquals("A", impl.getName());
    assertEquals("@A", impl.getAlias());
    assertEquals(expression, impl.getExpression());
  }

  @Test
  public void testUriResourceActionImpl() {
    UriResourceActionImpl impl = new UriResourceActionImpl();
    assertEquals(UriResourceKind.action, impl.getKind());
    assertEquals("", impl.toString());

    // action
    EdmAction action = edm.getUnboundAction(ActionProvider.nameUARTETTwoKeyTwoPrimParam);
    impl.setAction(action);
    assertEquals(action, impl.getAction());
    assertEquals(ActionProvider.nameUARTETTwoKeyTwoPrimParam.getName(), impl.toString());

    // action import
    impl = new UriResourceActionImpl();
    EdmActionImport actionImport = edm.getEntityContainer(null).getActionImport("AIRTCTTwoPrimParam");
    impl.setActionImport(actionImport);
    assertEquals(actionImport, impl.getActionImport());
    assertEquals(actionImport.getUnboundAction(), impl.getAction());
    assertFalse(impl.isCollection());
    assertEquals("AIRTCTTwoPrimParam", impl.toString());
    assertEquals(actionImport.getUnboundAction().getReturnType().getType(), impl.getType());

    actionImport = edm.getEntityContainer(null).getActionImport("AIRT");
    impl.setActionImport(actionImport);
    assertFalse(impl.isCollection());
    assertNull(impl.getType());
  }

  @Test
  public void testUriResourceLambdaAllImpl() {
    UriResourceLambdaAllImpl impl = new UriResourceLambdaAllImpl();
    assertEquals(UriResourceKind.lambdaAll, impl.getKind());

    ExpressionImpl expression = new LiteralImpl().setText("Expression");
    impl.setExpression(expression);
    impl.setLamdaVariable("A");

    assertFalse(impl.isCollection());
    assertEquals(expression, impl.getExpression());
    assertEquals("A", impl.getLambdaVariable());
    assertEquals(OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean), impl.getType());
    assertEquals("all", impl.toString());
  }

  @Test
  public void testUriResourceLambdaAnyImpl() {
    UriResourceLambdaAnyImpl impl = new UriResourceLambdaAnyImpl();
    assertEquals(UriResourceKind.lambdaAny, impl.getKind());

    ExpressionImpl expression = new LiteralImpl().setText("Expression");
    impl.setExpression(expression);
    impl.setLamdaVariable("A");

    assertFalse(impl.isCollection());
    assertEquals(expression, impl.getExpression());
    assertEquals("A", impl.getLambdaVariable());
    assertEquals(OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean), impl.getType());
    assertEquals("any", impl.toString());
  }

  @Test
  public void testUriResourceComplexPropertyImpl() {
    UriResourceComplexPropertyImpl impl = new UriResourceComplexPropertyImpl();
    assertEquals(UriResourceKind.complexProperty, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETKeyNav);
    EdmProperty property = (EdmProperty) entityType.getProperty("PropertyCompNav");
    impl.setProperty(property);

    assertEquals(property, impl.getProperty());
    assertEquals(property.getName(), impl.toString());
    assertFalse(impl.isCollection());
    assertEquals(property.getType(), impl.getType());
    assertEquals(property.getType(), impl.getComplexType());
    impl.getComplexType();

    EdmComplexType complexTypeImplType = edm.getComplexType(ComplexTypeProvider.nameCTBasePrimCompNav);

    impl.setTypeFilter(complexTypeImplType);
    assertEquals(complexTypeImplType, impl.getTypeFilter());
    assertEquals(complexTypeImplType, impl.getComplexTypeFilter());
    impl.getComplexTypeFilter();

  }

  @Test
  public void testUriResourcePrimitivePropertyImpl() {
    UriResourcePrimitivePropertyImpl impl = new UriResourcePrimitivePropertyImpl();
    assertEquals(UriResourceKind.primitiveProperty, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETKeyNav);
    EdmProperty property = (EdmProperty) entityType.getProperty("PropertyInt16");
    impl.setProperty(property);

    assertEquals(property, impl.getProperty());
    assertEquals(property.getName(), impl.toString());
    assertFalse(impl.isCollection());
    assertEquals(property.getType(), impl.getType());
  }

  @Test
  public void testUriResourceCountImpl() {
    UriResourceCountImpl impl = new UriResourceCountImpl();
    assertEquals(UriResourceKind.count, impl.getKind());
    assertEquals("$count", impl.toString());
  }

  @Test
  public void testUriResourceEntitySetImpl() {
    UriResourceEntitySetImpl impl = new UriResourceEntitySetImpl();
    assertEquals(UriResourceKind.entitySet, impl.getKind());

    EdmEntitySet entitySet = edm.getEntityContainer(null).getEntitySet("ESAllPrim");
    impl.setEntitSet(entitySet);

    assertEquals("ESAllPrim", impl.toString());
    assertEquals(entitySet, impl.getEntitySet());

    assertEquals(entitySet.getEntityType(), impl.getType());
    assertEquals(entitySet.getEntityType(), impl.getEntityType());
    impl.getEntityType();

    // is Collection
    assertTrue(impl.isCollection());
    impl.setKeyPredicates(Collections.<UriParameter> emptyList());
    assertFalse(impl.isCollection());
  }

  @Test
  public void testUriResourceFunctionImpl() {
    UriResourceFunctionImpl impl = new UriResourceFunctionImpl();
    assertEquals(UriResourceKind.function, impl.getKind());
    assertEquals("", impl.toString());

    // function
    EdmFunction function = edm.getEntityContainer(null).getFunctionImport("FINRTInt16")
        .getUnboundFunction(Collections.<String> emptyList());
    assertNotNull(function);
    impl.setFunction(function);

    assertEquals(function, impl.getFunction());
    assertEquals("UFNRTInt16", impl.toString());
    assertEquals(function.getReturnType().getType(), impl.getType());
    assertFalse(impl.isParameterListFilled());

    // function import
    impl = new UriResourceFunctionImpl();
    EdmFunctionImport functionImport = edm.getEntityContainer(null).getFunctionImport("FINRTInt16");
    impl.setFunctionImport(functionImport, Collections.<UriParameter> emptyList());
    assertEquals(functionImport, impl.getFunctionImport());
    assertEquals("FINRTInt16", impl.toString());

    // function collection
    impl = new UriResourceFunctionImpl();
    functionImport = edm.getEntityContainer(null).getFunctionImport("FICRTCollESTwoKeyNavParam");
    assertNotNull(function);
    UriParameter parameter = new UriParameterImpl().setName("ParameterInt16");
    impl.setFunctionImport(functionImport, Collections.singletonList(parameter));
    assertEquals("FICRTCollESTwoKeyNavParam", impl.toString());

    impl.setFunction(functionImport.getUnboundFunction(Collections.singletonList("ParameterInt16")));
    assertTrue(impl.isCollection());
    impl.setKeyPredicates(Collections.<UriParameter> emptyList());
    assertFalse(impl.isCollection());

    assertEquals(parameter, impl.getParameters().get(0));
    assertTrue(impl.isParameterListFilled());
  }

  @Test
  public void testUriResourceImplKeyPred() {
    final EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav);
    class Mock extends UriResourceWithKeysImpl {

      public Mock() {
        super(UriResourceKind.action);
      }

      @Override
      public EdmType getType() {
        return entityType;
      }

      @Override
      public boolean isCollection() {
        return false;
      }

      @Override
      public String getSegmentValue() {
        return "mock";
      }
    }

    Mock impl = new Mock();
    EdmEntityType entityTypeBaseColl = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    EdmEntityType entityTypeBaseEntry = edm.getEntityType(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    assertEquals(entityType, impl.getType());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock", impl.toString(true));

    // set both
    impl.setCollectionTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getTypeFilterOnCollection());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/olingo.odata.test1.ETBaseTwoKeyNav", impl.toString(true));
    impl.setEntryTypeFilter(entityTypeBaseEntry);
    assertEquals(entityTypeBaseEntry, impl.getTypeFilterOnEntry());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/olingo.odata.test1.ETBaseTwoKeyNav/()olingo.odata.test1.ETTwoBaseTwoKeyNav",
        impl.toString(true));

    // set entry
    impl = new Mock();
    impl.setEntryTypeFilter(entityTypeBaseEntry);
    assertEquals(entityTypeBaseEntry, impl.getTypeFilterOnEntry());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/olingo.odata.test1.ETTwoBaseTwoKeyNav", impl.toString(true));

    // set collection
    impl = new Mock();
    impl.setCollectionTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getTypeFilterOnCollection());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/olingo.odata.test1.ETBaseTwoKeyNav", impl.toString(true));

    impl.setKeyPredicates(
        Collections.singletonList(
            (UriParameter) new UriParameterImpl().setName("ParameterInt16")));
    assertNotNull(null, impl.getKeyPredicates());
  }

  @Test
  public void testUriResourceImplTyped() {
    final EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav);
    class Mock extends UriResourceTypedImpl {

      public Mock() {
        super(UriResourceKind.action);
      }

      @Override
      public EdmType getType() {
        return entityType;
      }

      @Override
      public boolean isCollection() {
        return false;
      }

      @Override
      public String getSegmentValue() {
        return "mock";
      }
    }

    Mock impl = new Mock();
    EdmEntityType entityTypeBaseColl = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    edm.getEntityType(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    assertEquals("mock", impl.toString(true));
    assertEquals("mock", impl.toString(false));

    impl.setTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getTypeFilter());
    assertEquals("mock/olingo.odata.test1.ETBaseTwoKeyNav", impl.toString(true));
    assertEquals("mock", impl.toString(false));
  }

  @Test
  public void testUriResourceItImpl() {
    UriResourceItImpl impl = new UriResourceItImpl();
    assertEquals(UriResourceKind.it, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav);
    assertEquals("$it", impl.toString());

    impl.setType(entityType);
    assertEquals(entityType, impl.getType());

    assertFalse(impl.isCollection());
    impl.setCollection(true);
    assertTrue(impl.isCollection());
    impl.setKeyPredicates(Collections.singletonList(
        (UriParameter) new UriParameterImpl().setName("ParameterInt16")));
    assertFalse(impl.isCollection());
  }

  @Test
  public void testUriResourceNavigationPropertyImpl() {
    UriResourceNavigationPropertyImpl impl = new UriResourceNavigationPropertyImpl();
    assertEquals(UriResourceKind.navigationProperty, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav);
    EdmNavigationProperty property = (EdmNavigationProperty) entityType.getProperty("NavPropertyETKeyNavMany");
    assertNotNull(property);

    impl.setNavigationProperty(property);
    assertEquals(property, impl.getProperty());

    assertEquals("NavPropertyETKeyNavMany", impl.toString());
    assertEquals(property.getType(), impl.getType());

    assertTrue(impl.isCollection());
    impl.setKeyPredicates(Collections.singletonList(
        (UriParameter) new UriParameterImpl().setName("ParameterInt16")));
    assertFalse(impl.isCollection());
  }

  @Test
  public void testUriResourceRefImpl() {
    UriResourceRefImpl impl = new UriResourceRefImpl();
    assertEquals(UriResourceKind.ref, impl.getKind());
    assertEquals("$ref", impl.toString());
  }

  @Test
  public void testUriResourceRootImpl() {
    UriResourceRootImpl impl = new UriResourceRootImpl();
    assertEquals(UriResourceKind.root, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav);
    assertEquals("$root", impl.toString());

    impl.setType(entityType);
    assertEquals(entityType, impl.getType());

    assertFalse(impl.isCollection());
    impl.setCollection(true);
    assertTrue(impl.isCollection());
    impl.setKeyPredicates(Collections.singletonList(
        (UriParameter) new UriParameterImpl().setName("ParameterInt16")));
    assertFalse(impl.isCollection());
  }

  @Test
  public void testUriResourceSingletonImpl() {
    UriResourceSingletonImpl impl = new UriResourceSingletonImpl();
    assertEquals(UriResourceKind.singleton, impl.getKind());

    EdmSingleton singleton = edm.getEntityContainer(null).getSingleton("SINav");
    EdmEntityType entityTypeBaseColl = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    impl.setSingleton(singleton);

    assertEquals("SINav", impl.toString());
    assertEquals(singleton, impl.getSingleton());

    assertEquals(singleton.getEntityType(), impl.getType());
    assertEquals(singleton.getEntityType(), impl.getEntityType());
    impl.getEntityType();

    impl.setTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getEntityTypeFilter());

    // is Collection
    assertFalse(impl.isCollection());
  }

  @Test
  public void testUriResourceValueImpl() {
    UriResourceValueImpl impl = new UriResourceValueImpl();
    assertEquals(UriResourceKind.value, impl.getKind());
    assertEquals("$value", impl.toString());
  }

  @Test
  public void testUriResourceLambdaVarImpl() {
    UriResourceLambdaVarImpl impl = new UriResourceLambdaVarImpl();
    assertEquals(UriResourceKind.lambdaVariable, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav);
    impl.setType(entityType);
    impl.setVariableText("A");

    assertEquals("A", impl.toString());
    assertEquals(entityType, impl.getType());
    assertEquals("A", impl.getVariableName());
    assertFalse(impl.isCollection());
    impl.setCollection(true);
    assertTrue(impl.isCollection());
  }

  @Test
  public void testUriResourceStartingTypeFilterImpl() {
    UriResourceStartingTypeFilterImpl impl = new UriResourceStartingTypeFilterImpl();

    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETTwoKeyNav);

    impl.setType(entityType);
    assertEquals("olingo.odata.test1.ETTwoKeyNav", impl.toString());
    assertEquals(entityType, impl.getType());

    assertFalse(impl.isCollection());
    impl.setCollection(true);
    assertTrue(impl.isCollection());
    impl.setKeyPredicates(Collections.singletonList(
        (UriParameter) new UriParameterImpl().setName("ParameterInt16")));
    assertFalse(impl.isCollection());
  }
}
