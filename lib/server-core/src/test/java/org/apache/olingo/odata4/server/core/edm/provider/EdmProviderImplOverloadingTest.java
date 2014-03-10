/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.server.core.edm.provider;

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

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.server.api.edm.provider.Action;
import org.apache.olingo.odata4.server.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.server.api.edm.provider.Function;
import org.apache.olingo.odata4.server.api.edm.provider.Parameter;
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
    EdmProvider provider = mock(EdmProvider.class);

    List<Action> actions = new ArrayList<Action>();
    Action action = new Action().setName(operationName1.getName());
    actions.add(action);
    List<Parameter> action1Parameters = new ArrayList<Parameter>();
    action1Parameters.add(new Parameter().setType(operationType1).setCollection(false));
    action =
        new Action().setName(operationName1.getName()).setBound(true).setParameters(action1Parameters);
    actions.add(action);
    List<Parameter> action2Parameters = new ArrayList<Parameter>();
    action2Parameters.add(new Parameter().setType(operationType1).setCollection(true));
    action =
        new Action().setName(operationName1.getName()).setBound(true).setParameters(action2Parameters);
    actions.add(action);
    when(provider.getActions(operationName1)).thenReturn(actions);

    List<Function> functions = new ArrayList<Function>();
    Function function = new Function().setName(operationName1.getName());
    functions.add(function);
    List<Parameter> function1Parameters = new ArrayList<Parameter>();
    function1Parameters.add(new Parameter().setType(operationType1).setName("a"));
    function = new Function().setName(operationName1.getName()).setParameters(function1Parameters);
    functions.add(function);
    List<Parameter> function2Parameters = new ArrayList<Parameter>();
    function2Parameters.add(new Parameter().setType(operationType1).setName("b"));
    function = new Function().setName(operationName1.getName()).setParameters(function2Parameters);
    functions.add(function);
    List<Parameter> function3Parameters = new ArrayList<Parameter>();
    function3Parameters.add(new Parameter().setName("a").setType(operationType1));
    function3Parameters.add(new Parameter().setName("b"));
    function = new Function().setName(operationName1.getName()).setParameters(function3Parameters).setBound(true);
    functions.add(function);
    List<Parameter> function4Parameters = new ArrayList<Parameter>();
    function4Parameters.add(new Parameter().setName("a").setType(operationType2));
    function4Parameters.add(new Parameter().setName("b"));
    function = new Function().setName(operationName1.getName()).setParameters(function4Parameters).setBound(true);
    functions.add(function);
    when(provider.getFunctions(operationName1)).thenReturn(functions);

    List<Function> badFunctions = new ArrayList<Function>();
    Function badFunction = new Function().setName(operationName1.getName()).setBound(true).setParameters(null);
    badFunctions.add(badFunction);

    when(provider.getFunctions(badOperationName)).thenReturn(badFunctions);

    edm = new EdmProviderImpl(provider);
  }

  @Test
  public void simpleActionGet() {
    EdmAction action = edm.getAction(operationName1, null, null);
    assertNotNull(action);
    assertEquals(operationName1.getNamespace(), action.getNamespace());
    assertEquals(operationName1.getName(), action.getName());

    assertNull(edm.getAction(wrongOperationName, null, null));
  }

  @Test
  public void boundActionOverloading() {
    EdmAction action = edm.getAction(operationName1, operationType1, false);
    assertNotNull(action);
    assertEquals(operationName1.getNamespace(), action.getNamespace());
    assertEquals(operationName1.getName(), action.getName());
    assertTrue(action == edm.getAction(operationName1, operationType1, false));

    EdmAction action2 = edm.getAction(operationName1, operationType1, true);
    assertNotNull(action2);
    assertEquals(operationName1.getNamespace(), action2.getNamespace());
    assertEquals(operationName1.getName(), action2.getName());
    assertTrue(action2 == edm.getAction(operationName1, operationType1, true));

    assertNotSame(action, action2);
  }

  @Test
  public void simpleFunctionGet() {
    EdmFunction function = edm.getFunction(operationName1, null, null, null);
    assertNotNull(function);
    assertEquals(operationName1.getNamespace(), function.getNamespace());
    assertEquals(operationName1.getName(), function.getName());

    EdmFunction function2 = edm.getFunction(operationName1, null, null, new ArrayList<String>());
    assertNotNull(function2);
    assertEquals(operationName1.getNamespace(), function2.getNamespace());
    assertEquals(operationName1.getName(), function2.getName());

    assertEquals(function, function2);

    assertNull(edm.getFunction(wrongOperationName, null, null, new ArrayList<String>()));
  }

  @Test
  public void functionOverloading() {
    ArrayList<String> parameter1Names = new ArrayList<String>();
    parameter1Names.add("a");
    List<String> parameter2Names = new ArrayList<String>();
    parameter2Names.add("b");
    EdmFunction function = edm.getFunction(operationName1, null, null, new ArrayList<String>());
    assertNotNull(function);
    assertFalse(function.isBound());

    EdmFunction function1 = edm.getFunction(operationName1, null, null, parameter1Names);
    assertNotNull(function1);
    assertFalse(function1.isBound());

    assertFalse(function == function1);
    assertNotSame(function, function1);

    EdmFunction function2 = edm.getFunction(operationName1, null, null, parameter2Names);
    assertNotNull(function2);
    assertFalse(function2.isBound());

    assertFalse(function1 == function2);
    assertNotSame(function1, function2);

    EdmFunction function3 = edm.getFunction(operationName1, operationType1, false, parameter2Names);
    assertNotNull(function3);
    assertTrue(function3.isBound());
    EdmFunction function4 = edm.getFunction(operationName1, operationType2, false, parameter2Names);
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
    edm.getFunction(badOperationName, operationType1, true, null);
  }

}
