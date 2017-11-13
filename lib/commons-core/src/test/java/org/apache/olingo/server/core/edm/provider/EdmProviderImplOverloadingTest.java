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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmProviderImplOverloadingTest {

  private Edm edm;
  private final FullQualifiedName operationName1 = new FullQualifiedName("n", "o1");
  private final FullQualifiedName operationType1 = new FullQualifiedName("n", "t1");
  private final FullQualifiedName operationType2 = new FullQualifiedName("n", "t2");
  private final FullQualifiedName wrongOperationName = new FullQualifiedName("wrong", "wrong");
  private final FullQualifiedName badOperationName = new FullQualifiedName("bad", "bad");

  @Before
  public void setup() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);

    List<CsdlAction> actions = new ArrayList<CsdlAction>();
    CsdlAction action = new CsdlAction().setName(operationName1.getName());
    actions.add(action);
    List<CsdlParameter> action1Parameters = new ArrayList<CsdlParameter>();
    action1Parameters.add(new CsdlParameter().setType(operationType1).setCollection(false));
    action =
        new CsdlAction().setName(operationName1.getName()).setBound(true).setParameters(action1Parameters);
    actions.add(action);
    List<CsdlParameter> action2Parameters = new ArrayList<CsdlParameter>();
    action2Parameters.add(new CsdlParameter().setType(operationType1).setCollection(true));
    action =
        new CsdlAction().setName(operationName1.getName()).setBound(true).setParameters(action2Parameters);
    actions.add(action);
    when(provider.getActions(operationName1)).thenReturn(actions);
    CsdlEntityType type = new CsdlEntityType().setProperties(new ArrayList<CsdlProperty>());
    when(provider.getEntityType(operationType1)).thenReturn(type);
    when(provider.getEntityType(operationType2)).thenReturn(type);
    List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
    CsdlFunction function = new CsdlFunction().setName(operationName1.getName());
    functions.add(function);
    List<CsdlParameter> function1Parameters = new ArrayList<CsdlParameter>();
    function1Parameters.add(new CsdlParameter().setType(operationType1).setName("a"));
    function = new CsdlFunction().setName(operationName1.getName()).setParameters(function1Parameters);
    functions.add(function);
    List<CsdlParameter> function2Parameters = new ArrayList<CsdlParameter>();
    function2Parameters.add(new CsdlParameter().setType(operationType1).setName("b"));
    function = new CsdlFunction().setName(operationName1.getName()).setParameters(function2Parameters);
    functions.add(function);
    List<CsdlParameter> function3Parameters = new ArrayList<CsdlParameter>();
    function3Parameters.add(new CsdlParameter().setName("a").setType(operationType1));
    function3Parameters.add(new CsdlParameter().setName("b").setType(operationType1));
    function = new CsdlFunction().setName(operationName1.getName()).setParameters(function3Parameters).setBound(true);
    functions.add(function);
    List<CsdlParameter> function4Parameters = new ArrayList<CsdlParameter>();
    function4Parameters.add(new CsdlParameter().setName("a").setType(operationType2));
    function4Parameters.add(new CsdlParameter().setName("b").setType(operationType2));
    function = new CsdlFunction().setName(operationName1.getName()).setParameters(function4Parameters).setBound(true);
    functions.add(function);
    when(provider.getFunctions(operationName1)).thenReturn(functions);

    List<CsdlFunction> badFunctions = new ArrayList<CsdlFunction>();
    CsdlFunction badFunction = new CsdlFunction().setName(operationName1.getName()).setBound(true).setParameters(null);
    badFunctions.add(badFunction);

    when(provider.getFunctions(badOperationName)).thenReturn(badFunctions);

    edm = new EdmProviderImpl(provider);
  }

  @Test
  public void simpleActionGet() {
    EdmAction action = edm.getUnboundAction(operationName1);
    assertNotNull(action);
    assertEquals(operationName1.getNamespace(), action.getNamespace());
    assertEquals(operationName1.getName(), action.getName());

    assertNull(edm.getUnboundAction(wrongOperationName));
  }

  @Test
  public void boundActionOverloading() {
    EdmAction action = edm.getBoundAction(operationName1, operationType1, false);
    assertNotNull(action);
    assertEquals(operationName1.getNamespace(), action.getNamespace());
    assertEquals(operationName1.getName(), action.getName());
    assertTrue(action == edm.getBoundAction(operationName1, operationType1, false));

    EdmAction action2 = edm.getBoundAction(operationName1, operationType1, true);
    assertNotNull(action2);
    assertEquals(operationName1.getNamespace(), action2.getNamespace());
    assertEquals(operationName1.getName(), action2.getName());
    assertTrue(action2 == edm.getBoundAction(operationName1, operationType1, true));

    assertNotSame(action, action2);
  }

  @Test
  public void simpleFunctionGet() {
    EdmFunction function = edm.getUnboundFunction(operationName1, null);
    assertNotNull(function);
    assertEquals(operationName1.getNamespace(), function.getNamespace());
    assertEquals(operationName1.getName(), function.getName());

    EdmFunction function2 = edm.getUnboundFunction(operationName1, new ArrayList<String>());
    assertNotNull(function2);
    assertEquals(operationName1.getNamespace(), function2.getNamespace());
    assertEquals(operationName1.getName(), function2.getName());

    assertEquals(function, function2);

    assertNull(edm.getUnboundFunction(wrongOperationName, new ArrayList<String>()));
  }

  @Test
  public void functionOverloading() {
    ArrayList<String> parameter1Names = new ArrayList<String>();
    parameter1Names.add("a");
    List<String> parameter2Names = new ArrayList<String>();
    parameter2Names.add("b");
    EdmFunction function = edm.getUnboundFunction(operationName1, new ArrayList<String>());
    assertNotNull(function);
    assertFalse(function.isBound());

    EdmFunction function1 = edm.getUnboundFunction(operationName1, parameter1Names);
    assertNotNull(function1);
    assertFalse(function1.isBound());

    assertFalse(function == function1);
    assertNotSame(function, function1);

    EdmFunction function2 = edm.getUnboundFunction(operationName1, parameter2Names);
    assertNotNull(function2);
    assertFalse(function2.isBound());

    assertFalse(function1 == function2);
    assertNotSame(function1, function2);

    EdmFunction function3 = edm.getBoundFunction(operationName1, operationType1, false, parameter2Names);
    assertNotNull(function3);
    assertTrue(function3.isBound());
    EdmFunction function4 = edm.getBoundFunction(operationName1, operationType2, false, parameter2Names);
    assertNotNull(function4);
    assertTrue(function4.isBound());

    assertFalse(function3 == function4);
    assertNotSame(function3, function4);

    assertFalse(function1 == function3);
    assertFalse(function1 == function4);
    assertFalse(function2 == function3);
    assertFalse(function2 == function4);
    assertNotSame(function1, function3);
    assertNotSame(function1, function4);
    assertNotSame(function2, function3);
    assertNotSame(function2, function4);
  }

  @Test(expected = EdmException.class)
  public void noParametersAtBoundFunctionReslutsInException() {
    edm.getBoundFunction(badOperationName, operationType1, true, null);
  }

}
