/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmActionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmComplexTypeImpl;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmEntitySetImpl;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmSingletonImpl;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.ExpressionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.LiteralImpl;
import org.junit.Test;

public class UriParameterImplTest {
  Edm edm = new EdmProviderImpl(new EdmTechTestProvider());

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
    assertEquals(expression, impl.getExression());
  }

  @Test
  public void testUriResourceActionImpl() {
    UriResourceActionImpl impl = new UriResourceActionImpl();
    assertEquals(UriResourceKind.action, impl.getKind());
    assertEquals("", impl.toString());

    // action
    EdmAction action = edm.getAction(EdmTechTestProvider.nameUARTETParam, null, null);
    impl.setAction(action);
    assertEquals(action, impl.getAction());
    assertEquals(EdmTechTestProvider.nameUARTETParam.getName(), impl.toString());

    // action import
    impl = new UriResourceActionImpl();
    EdmActionImport actionImport = edm.getEntityContainer(null).getActionImport("AIRTPrimParam");
    impl.setActionImport(actionImport);
    assertEquals(actionImport, impl.getActionImport());
    assertEquals(actionImport.getAction(), impl.getAction());
    assertEquals(false, impl.isCollection());
    assertEquals("AIRTPrimParam", impl.toString());
    assertEquals(actionImport.getAction().getReturnType().getType(), impl.getType());
  }

  @Test
  public void testUriResourceLambdaAllImpl() {
    UriResourceLambdaAllImpl impl = new UriResourceLambdaAllImpl();
    assertEquals(UriResourceKind.lambdaAll, impl.getKind());

    ExpressionImpl expression = new LiteralImpl().setText("Expression");
    impl.setExpression(expression);
    impl.setLamdaVariable("A");

    assertEquals(false, impl.isCollection());
    assertEquals(expression, impl.getExpression());
    assertEquals("A", impl.getLamdaVariable());
    assertEquals(EdmPrimitiveTypeKind.Boolean.getEdmPrimitiveTypeInstance(), impl.getType());
    assertEquals("all", impl.toString());
  }

  @Test
  public void testUriResourceLambdaAnyImpl() {
    UriResourceLambdaAnyImpl impl = new UriResourceLambdaAnyImpl();
    assertEquals(UriResourceKind.lambdaAny, impl.getKind());

    ExpressionImpl expression = new LiteralImpl().setText("Expression");
    impl.setExpression(expression);
    impl.setLamdaVariable("A");

    assertEquals(false, impl.isCollection());
    assertEquals(expression, impl.getExpression());
    assertEquals("A", impl.getLamdaVariable());
    assertEquals(EdmPrimitiveTypeKind.Boolean.getEdmPrimitiveTypeInstance(), impl.getType());
    assertEquals("any", impl.toString());
  }

  @Test
  public void testUriResourceComplexPropertyImpl() {
    UriResourceComplexPropertyImpl impl = new UriResourceComplexPropertyImpl();
    assertEquals(UriResourceKind.complexProperty, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETKeyNav);
    EdmProperty property = (EdmProperty) entityType.getProperty("PropertyComplex");
    impl.setProperty(property);

    assertEquals(property, impl.getProperty());
    assertEquals(property.getName(), impl.toString());
    assertEquals(false, impl.isCollection());
    assertEquals(property.getType(), impl.getType());
    assertEquals(property.getType(), impl.getComplexType());
    EdmComplexType complexType = impl.getComplexType(); // must work without cast

    EdmComplexTypeImpl complexTypeImplType =
        (EdmComplexTypeImpl) edm.getComplexType(EdmTechProvider.nameCTBasePrimCompNav);

    impl.setTypeFilter(complexTypeImplType);
    assertEquals(complexTypeImplType, impl.getTypeFilter());
    assertEquals(complexTypeImplType, impl.getComplexTypeFilter());
    complexType = impl.getComplexTypeFilter(); // must work without cast

  }

  @Test
  public void testUriResourcePrimitivePropertyImpl() {
    UriResourcePrimitivePropertyImpl impl = new UriResourcePrimitivePropertyImpl();
    assertEquals(UriResourceKind.primitiveProperty, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETKeyNav);
    EdmProperty property = (EdmProperty) entityType.getProperty("PropertyInt16");
    impl.setProperty(property);

    assertEquals(property, impl.getProperty());
    assertEquals(property.getName(), impl.toString());
    assertEquals(false, impl.isCollection());
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

    EdmEntitySetImpl entitySet = (EdmEntitySetImpl) edm.getEntityContainer(null).getEntitySet("ESAllPrim");
    impl.setEntitSet(entitySet);

    assertEquals("ESAllPrim", impl.toString());
    assertEquals(entitySet, impl.getEntitySet());

    assertEquals(entitySet.getEntityType(), impl.getType());
    assertEquals(entitySet.getEntityType(), impl.getEntityType());
    EdmEntityType entityType = impl.getEntityType(); // must work without cast

    // is Collection
    assertEquals(true, impl.isCollection());
    impl.setKeyPredicates(new ArrayList<UriParameterImpl>());
    assertEquals(false, impl.isCollection());
  }

  @Test
  public void testUriResourceFunctionImpl() {
    UriResourceFunctionImpl impl = new UriResourceFunctionImpl();
    assertEquals(UriResourceKind.function, impl.getKind());
    assertEquals("", impl.toString());

    // function
    EdmFunction function = (EdmFunction) edm.getEntityContainer(null).getFunctionImport("FINRTInt16")
        .getFunction(new ArrayList<String>());
    assertNotNull(function);
    impl.setFunction(function);

    assertEquals(function, impl.getFunction());
    assertEquals("UFNRTInt16", impl.toString());
    assertEquals(function.getReturnType().getType(), impl.getType());

    // function import
    impl = new UriResourceFunctionImpl();
    EdmFunctionImport functionImport = edm.getEntityContainer(null).getFunctionImport("FINRTInt16");
    impl.setFunctionImport(functionImport, new ArrayList<UriParameterImpl>());
    assertEquals(functionImport, impl.getFunctionImport());
    assertEquals("FINRTInt16", impl.toString());

    // function collection
    impl = new UriResourceFunctionImpl();
    functionImport = edm.getEntityContainer(null).getFunctionImport("FICRTESTwoKeyNavParam");
    assertNotNull(function);
    UriParameterImpl parameter = new UriParameterImpl().setName("ParameterInt16");
    impl.setFunctionImport(functionImport, Arrays.asList(parameter));
    assertEquals("FICRTESTwoKeyNavParam", impl.toString());
    
    
    impl.setFunction(functionImport.getFunction(Arrays.asList("ParameterInt16")));
    assertEquals(true, impl.isCollection());
    impl.setKeyPredicates(new ArrayList<UriParameterImpl>());
    assertEquals(false, impl.isCollection());

    assertEquals(parameter, impl.getParameters().get(0));
  }

  @Test
  public void testUriResourceImplKeyPred() {
    class Mock extends UriResourceImplKeyPred {
      EdmType type;

      public Mock() {
        super(UriResourceKind.action);
      }

      @Override
      public EdmType getType() {
        return type;
      }

      public Mock setType(EdmType type) {
        this.type = type;
        return this;
      }

      @Override
      public boolean isCollection() {
        return false;
      }

      @Override
      public String toString() {
        return "mock";
      }
    }

    Mock impl = new Mock();
    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETTwoKeyNav);
    EdmEntityType entityTypeBaseColl = edm.getEntityType(EdmTechProvider.nameETBaseTwoKeyNav);
    EdmEntityType entityTypeBaseEntry = edm.getEntityType(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    impl.setType(entityType);
    assertEquals(entityType, impl.getType());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock", impl.toString(true));

    // set both
    impl.setCollectionTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getTypeFilterOnCollection());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/com.sap.odata.test1.ETBaseTwoKeyNav", impl.toString(true));
    impl.setEntryTypeFilter(entityTypeBaseEntry);
    assertEquals(entityTypeBaseEntry, impl.getTypeFilterOnEntry());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/com.sap.odata.test1.ETBaseTwoKeyNav/()com.sap.odata.test1.ETTwoBaseTwoKeyNav",
        impl.toString(true));

    // set entry
    impl = new Mock();
    impl.setType(entityType);
    impl.setEntryTypeFilter(entityTypeBaseEntry);
    assertEquals(entityTypeBaseEntry, impl.getTypeFilterOnEntry());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/com.sap.odata.test1.ETTwoBaseTwoKeyNav", impl.toString(true));
    assertEquals(entityTypeBaseEntry, impl.getTypeFilter());

    // set collection
    impl = new Mock();
    impl.setType(entityType);
    impl.setCollectionTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getTypeFilterOnCollection());
    assertEquals("mock", impl.toString(false));
    assertEquals("mock/com.sap.odata.test1.ETBaseTwoKeyNav", impl.toString(true));
    assertEquals(entityTypeBaseColl, impl.getTypeFilter());

    impl = new Mock();
    UriParameterImpl parameter = new UriParameterImpl().setName("ParameterInt16");
    List<UriParameterImpl> keyPredicates = new ArrayList<UriParameterImpl>();
    keyPredicates.add(parameter);

    impl.setKeyPredicates(keyPredicates);
    assertNotNull(null, impl.getKeyPredicates());

  }

  @Test
  public void testUriResourceImplTyped() {
    class Mock extends UriResourceImplTyped {
      EdmType type;

      public Mock() {
        super(UriResourceKind.action);
      }

      @Override
      public EdmType getType() {
        return type;
      }

      @Override
      public boolean isCollection() {
        return false;
      }

      public Mock setType(EdmType type) {
        this.type = type;
        return this;
      }

      @Override
      public String toString() {
        return "mock";
      }

    }

    Mock impl = new Mock();
    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETTwoKeyNav);
    EdmEntityType entityTypeBaseColl = edm.getEntityType(EdmTechProvider.nameETBaseTwoKeyNav);
    EdmEntityType entityTypeBaseEntry = edm.getEntityType(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    impl.setType(entityType);
    assertEquals("mock", impl.toString());
    assertEquals("mock", impl.toString(true));
    assertEquals("mock", impl.toString(false));

    impl.setTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getTypeFilter());
    assertEquals("mock", impl.toString());
    assertEquals("mock/com.sap.odata.test1.ETBaseTwoKeyNav", impl.toString(true));
    assertEquals("mock", impl.toString(false));
    //
  }

  @Test
  public void testUriResourceItImpl() {
    UriResourceItImpl impl = new UriResourceItImpl();
    assertEquals(UriResourceKind.it, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETTwoKeyNav);
    assertEquals("$it", impl.toString());

    impl.setType(entityType);
    assertEquals(entityType, impl.getType());

    UriParameterImpl parameter = new UriParameterImpl().setName("ParameterInt16");
    List<UriParameterImpl> keyPredicates = new ArrayList<UriParameterImpl>();
    keyPredicates.add(parameter);

    assertEquals(false, impl.isCollection());
    impl.setCollection(true);
    assertEquals(true, impl.isCollection());
    impl.setKeyPredicates(keyPredicates);
    assertEquals(false, impl.isCollection());
  }

  @Test
  public void testUriResourceNavigationPropertyImpl() {
    UriResourceNavigationPropertyImpl impl = new UriResourceNavigationPropertyImpl();
    assertEquals(UriResourceKind.navigationProperty, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETTwoKeyNav);
    EdmNavigationProperty property = (EdmNavigationProperty) entityType.getProperty("NavPropertyETKeyNavMany");
    assertNotNull(property);

    impl.setNavigationProperty(property);
    assertEquals(property, impl.getProperty());

    assertEquals("NavPropertyETKeyNavMany", impl.toString());
    assertEquals(property.getType(), impl.getType());

    UriParameterImpl parameter = new UriParameterImpl().setName("ParameterInt16");
    List<UriParameterImpl> keyPredicates = new ArrayList<UriParameterImpl>();
    keyPredicates.add(parameter);

    assertEquals(true, impl.isCollection());
    impl.setKeyPredicates(keyPredicates);
    assertEquals(false, impl.isCollection());
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

    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETTwoKeyNav);
    assertEquals("$root", impl.toString());

    impl.setType(entityType);
    assertEquals(entityType, impl.getType());

    UriParameterImpl parameter = new UriParameterImpl().setName("ParameterInt16");
    List<UriParameterImpl> keyPredicates = new ArrayList<UriParameterImpl>();
    keyPredicates.add(parameter);

    assertEquals(false, impl.isCollection());
    impl.setCollection(true);
    assertEquals(true, impl.isCollection());
    impl.setKeyPredicates(keyPredicates);
    assertEquals(false, impl.isCollection());
  }
  
  @Test
  public void testUriResourceSingletonImpl() {
    UriResourceSingletonImpl impl = new UriResourceSingletonImpl();
    assertEquals(UriResourceKind.singleton, impl.getKind());

    EdmSingletonImpl singleton = (EdmSingletonImpl) edm.getEntityContainer(null).getSingleton("SINav");
    EdmEntityType entityTypeBaseColl = edm.getEntityType(EdmTechProvider.nameETBaseTwoKeyNav);
    impl.setSingleton(singleton);

    assertEquals("SINav", impl.toString());
    assertEquals(singleton, impl.getSingleton());

    assertEquals(singleton.getEntityType(), impl.getType());
    assertEquals(singleton.getEntityType(), impl.getEntityType());
    EdmEntityType entityType = impl.getEntityType(); // must work without cast
    
    
    impl.setTypeFilter(entityTypeBaseColl);
    assertEquals(entityTypeBaseColl, impl.getEntityTypeFilter());

    // is Collection
    assertEquals(false, impl.isCollection());
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
    
    
    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETTwoKeyNav);
    impl.setType(entityType);
    impl.setVariableText("A");
    
    assertEquals("A", impl.toString());
    assertEquals(entityType, impl.getType());
    assertEquals("A", impl.getVariableText());
    assertEquals(false, impl.isCollection());
    impl.setCollection(true);
    assertEquals(true, impl.isCollection());
  }
  
  @Test
  public void testUriResourceStartingTypeFilterImpl () {
    UriResourceStartingTypeFilterImpl impl = new UriResourceStartingTypeFilterImpl();
    assertEquals(UriResourceKind.startingTypeFilter, impl.getKind());

    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETTwoKeyNav);
    

    impl.setType(entityType);
    assertEquals("com.sap.odata.test1.ETTwoKeyNav", impl.toString());
    assertEquals(entityType, impl.getType());

    UriParameterImpl parameter = new UriParameterImpl().setName("ParameterInt16");
    List<UriParameterImpl> keyPredicates = new ArrayList<UriParameterImpl>();
    keyPredicates.add(parameter);

    assertEquals(false, impl.isCollection());
    impl.setCollection(true);
    assertEquals(true, impl.isCollection());
    impl.setKeyPredicates(keyPredicates);
    assertEquals(false, impl.isCollection());
  
  }
}
