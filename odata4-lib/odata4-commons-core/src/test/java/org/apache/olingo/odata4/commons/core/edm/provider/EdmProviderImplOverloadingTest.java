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
package org.apache.olingo.odata4.commons.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.provider.Action;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.Function;
import org.apache.olingo.odata4.commons.api.edm.provider.Parameter;
import org.junit.Before;
import org.junit.Test;

public class EdmProviderImplOverloadingTest {

  private Edm edm;
  private final FullQualifiedName FQN = new FullQualifiedName("testNamespace", "testName");
  private final FullQualifiedName WRONG_FQN = new FullQualifiedName("wrong", "wrong");

  @Before
  public void setup() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);

    Action action = new Action().setName(FQN.getName());
    List<Action> actions = new ArrayList<Action>();
    actions.add(action);
    when(provider.getActions(FQN)).thenReturn(actions);

    Function function = new Function().setName(FQN.getName()).setParameters(new ArrayList<Parameter>());
    List<Function> functions = new ArrayList<Function>();
    functions.add(function);
    when(provider.getFunctions(FQN)).thenReturn(functions);
    edm = new EdmProviderImpl(provider);
  }

  @Test
  public void simpleActionGet() {
    EdmAction action = edm.getAction(FQN, null, null);
    assertNotNull(action);
    assertEquals(FQN.getNamespace(), action.getNamespace());
    assertEquals(FQN.getName(), action.getName());

    assertNull(edm.getAction(WRONG_FQN, null, null));
  }

  @Test
  public void simpleFunctionGet() {
    EdmFunction function = edm.getFunction(FQN, null, null, new ArrayList<String>());
    assertNotNull(function);
    assertEquals(FQN.getNamespace(), function.getNamespace());
    assertEquals(FQN.getName(), function.getName());

    assertNull(edm.getFunction(WRONG_FQN, null, null, new ArrayList<String>()));
  }

}
