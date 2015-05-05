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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.core.edm.EdmFunctionImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmFunctionImplTest {

  private EdmFunction functionImpl1;
  private EdmFunction functionImpl2;

  @Before
  public void setupFunctions() {
    EdmProviderImpl provider = mock(EdmProviderImpl.class);

    CsdlFunction function1 = new CsdlFunction().setReturnType(
        new CsdlReturnType().setType(new FullQualifiedName("Edm", "String")));
    functionImpl1 = new EdmFunctionImpl(provider, new FullQualifiedName("namespace", "name"), function1);
    CsdlFunction function2 = new CsdlFunction().setComposable(true);
    functionImpl2 = new EdmFunctionImpl(provider, new FullQualifiedName("namespace", "name"), function2);
  }

  @Test
  public void isComposableDefaultFalse() {
    assertFalse(functionImpl1.isComposable());
  }

  @Test
  public void isComposableSetToTrue() {
    assertTrue(functionImpl2.isComposable());
  }

  @Test
  public void existingReturnTypeGetsReturned() {
    EdmReturnType returnType = functionImpl1.getReturnType();
    assertNotNull(returnType);
    assertEquals("String", returnType.getType().getName());
  }

  @Test(expected = EdmException.class)
  public void nonExistingReturnTypeResultsInException() {
    functionImpl2.getReturnType();
    fail();
  }

}
