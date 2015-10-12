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
package org.apache.olingo.commons.core.edm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Test;

public class ActionMapKeyTest {

  private final FullQualifiedName fqn = new FullQualifiedName("namespace", "name");
  private final FullQualifiedName fqnType = new FullQualifiedName("namespace2", "name2");

  @Test
  public void invalidParametersTest() {
    createAndCheckForEdmException(null, null, null);
    createAndCheckForEdmException(fqn, null, null);
    createAndCheckForEdmException(fqn, fqnType, null);
    createAndCheckForEdmException(fqn, null, true);
    createAndCheckForEdmException(null, fqnType, true);
    createAndCheckForEdmException(null, fqnType, null);
    createAndCheckForEdmException(null, null, true);

  }

  private void createAndCheckForEdmException(final FullQualifiedName fqn, final FullQualifiedName typeName,
      final Boolean collection) {
    try {
      new ActionMapKey(fqn, typeName, collection);
    } catch (EdmException e) {
      return;
    }
    fail("EdmException expected for parameters: " + fqn + " " + typeName + " " + collection);
  }
  
  @Test
  public void testNotEquals() {
    ActionMapKey key;
    ActionMapKey someKey;

    key = new ActionMapKey(fqn, fqnType, false);
    someKey = new ActionMapKey(fqnType, fqnType, false);
    assertNotSame(key, someKey);
    
    key = new ActionMapKey(fqn, fqnType, false);
    someKey = new ActionMapKey(fqnType, fqnType, true);
    assertNotSame(key, someKey);
    
    key = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key, null);
  }

  @Test
  public void testEqualsMethod() {
    ActionMapKey key;
    ActionMapKey someKey;

    key = new ActionMapKey(fqn, fqnType, false);
    assertEquals(key, key);
    
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertEquals(key, someKey);

    key = new ActionMapKey(fqn, fqnType, new Boolean(false));
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertEquals(key, someKey);

    key = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key, someKey);

    key = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, new Boolean(false));
    assertNotSame(key, someKey);
  }

  @Test
  public void testHashMethod() {
    ActionMapKey key;
    ActionMapKey someKey;

    key = new ActionMapKey(fqn, fqnType, false);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertEquals(key.hashCode(), someKey.hashCode());

    key = new ActionMapKey(fqn, fqnType, new Boolean(false));
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertEquals(key.hashCode(), someKey.hashCode());

    key = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key.hashCode(), someKey.hashCode());

    key = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, new Boolean(false));
    assertNotSame(key.hashCode(), someKey.hashCode());
  }

}
