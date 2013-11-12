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
package org.apache.olingo.commons.core.edm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.junit.Test;

public class ActionMapKeyTest {

  private final FullQualifiedName fqn = new FullQualifiedName("namespace", "name");
  private final FullQualifiedName fqnType = new FullQualifiedName("namespace2", "name2");

  @Test
  public void testEqualsMethod() {
    ActionMapKey key1 = new ActionMapKey(fqn, null, null);
    ActionMapKey someKey = new ActionMapKey(fqn, null, null);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, null, new Boolean(true));
    someKey = new ActionMapKey(fqn, null, true);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, false);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, null);
    someKey = new ActionMapKey(fqn, fqnType, null);
    assertEquals(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, null);
    assertNotSame(key1, someKey);

    key1 = new ActionMapKey(fqn, fqnType, true);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key1, someKey);

    key1 = new ActionMapKey(fqn, null, true);
    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key1, someKey);

    key1 = new ActionMapKey(fqn, null, true);
    someKey = new ActionMapKey(fqn, null, false);
    assertNotSame(key1, someKey);
  }

  @Test
  public void testHashMethod() {
    ActionMapKey key1 = new ActionMapKey(fqn, null, null);
    ActionMapKey someKey = new ActionMapKey(fqn, null, null);
    assertEquals(key1.hashCode(), someKey.hashCode());

    key1 = new ActionMapKey(fqn, null, new Boolean(true));
    someKey = new ActionMapKey(fqn, null, true);
    assertEquals(key1.hashCode(), someKey.hashCode());

    someKey = new ActionMapKey(fqn, fqnType, true);
    assertNotSame(key1.hashCode(), someKey.hashCode());

    someKey = new ActionMapKey(fqn, fqnType, false);
    assertNotSame(key1.hashCode(), someKey.hashCode());
  }

}
