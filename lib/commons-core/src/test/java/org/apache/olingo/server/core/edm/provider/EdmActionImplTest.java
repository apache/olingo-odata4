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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.core.edm.EdmActionImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmActionImplTest {

  private EdmAction actionImpl1;
  private EdmAction actionImpl2;
  private EdmAction actionImpl3;

  @Before
  public void setup() {
    EdmProviderImpl provider = mock(EdmProviderImpl.class);
    List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
    parameters.add(new CsdlParameter().setName("Id").setType(new FullQualifiedName("namespace", "name")));
    FullQualifiedName action1Name = new FullQualifiedName("namespace", "action1");
    CsdlAction action1 = new CsdlAction().setName("action1").setBound(true).setParameters(parameters);
    actionImpl1 = new EdmActionImpl(provider, action1Name, action1);

    FullQualifiedName action2Name = new FullQualifiedName("namespace", "action2");
    FullQualifiedName returnTypeName = new FullQualifiedName("Edm", "String");
    CsdlReturnType returnType = new CsdlReturnType().setType(returnTypeName);
    CsdlAction action2 = new CsdlAction().setName("action2").setParameters(parameters).setReturnType(returnType);
    actionImpl2 = new EdmActionImpl(provider, action2Name, action2);

    FullQualifiedName action3Name = new FullQualifiedName("namespace", "action3");
    CsdlAction action3 =
        new CsdlAction().setName("action3").setParameters(parameters).setReturnType(returnType).setEntitySetPath(
            "path/Id");
    actionImpl3 = new EdmActionImpl(provider, action3Name, action3);
  }

  @Test
  public void action1BasicMethodCalls() {
    assertTrue(actionImpl1.isBound());
    assertEquals(EdmTypeKind.ACTION, actionImpl1.getKind());
    assertNull(actionImpl1.getReturnType());
    // assertEquals("returnName", actionImpl1.getReturnType().getType().getName());
    assertNotNull(actionImpl1.getParameterNames());

    for (String name : actionImpl1.getParameterNames()) {
      EdmParameter parameter = actionImpl1.getParameter(name);
      assertNotNull(parameter);
      assertEquals(name, parameter.getName());
    }

    assertNull(actionImpl1.getReturnedEntitySet(null));
    assertNull(actionImpl1.getReturnedEntitySet(mock(EdmEntitySet.class)));
  }

  @Test
  public void action2BasicMethodCalls() {
    assertFalse(actionImpl2.isBound());
    assertEquals(EdmTypeKind.ACTION, actionImpl2.getKind());
    assertEquals("String", actionImpl2.getReturnType().getType().getName());
    assertNotNull(actionImpl2.getParameterNames());

    for (String name : actionImpl2.getParameterNames()) {
      EdmParameter parameter = actionImpl2.getParameter(name);
      assertNotNull(parameter);
      assertEquals(name, parameter.getName());
    }

    assertNull(actionImpl2.getReturnedEntitySet(null));
    assertNull(actionImpl2.getReturnedEntitySet(mock(EdmEntitySet.class)));
  }

  @Test
  public void action3BasicMethodCalls() {
    assertFalse(actionImpl3.isBound());
    assertEquals(EdmTypeKind.ACTION, actionImpl3.getKind());
    assertEquals("String", actionImpl3.getReturnType().getType().getName());
    assertNotNull(actionImpl3.getParameterNames());

    for (String name : actionImpl3.getParameterNames()) {
      EdmParameter parameter = actionImpl3.getParameter(name);
      assertNotNull(parameter);
      assertEquals(name, parameter.getName());
    }

    actionImpl3.getReturnedEntitySet(null);
  }

  @Test
  public void action3getReturnedEntitySetWithEntitySet() {
    EdmEntitySet set = mock(EdmEntitySet.class);
    when(set.getRelatedBindingTarget("path/Id")).thenReturn(set);

    EdmEntitySet returnedEntitySet = actionImpl3.getReturnedEntitySet(set);

    assertEquals(set, returnedEntitySet);
  }

  @Test(expected = EdmException.class)
  public void action3getReturnedEntitySetWithNullReturn() {
    EdmEntitySet set = mock(EdmEntitySet.class);
    when(set.getRelatedBindingTarget("path")).thenReturn(null);

    actionImpl3.getReturnedEntitySet(set);
    fail();
  }

  @Test(expected = EdmException.class)
  public void action3getReturnedEntitySetWithSingleton() {
    EdmSingleton singleton = mock(EdmSingleton.class);
    EdmEntitySet set = mock(EdmEntitySet.class);
    when(set.getRelatedBindingTarget("path")).thenReturn(singleton);

    actionImpl3.getReturnedEntitySet(set);
    fail();
  }

}
