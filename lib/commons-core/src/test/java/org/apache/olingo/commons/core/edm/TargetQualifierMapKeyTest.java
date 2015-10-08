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

public class TargetQualifierMapKeyTest {

  private static final FullQualifiedName TARGET_NAME_1 = new FullQualifiedName("namespace", "name");

  @Test
  public void invalidParametersTest() {
    createAndCheckForEdmException(null, null);
    createAndCheckForEdmException(null, "qualifier");
  }

  @Test
  public void validParametersTest() {
    new TargetQualifierMapKey(TARGET_NAME_1, null);
    new TargetQualifierMapKey(TARGET_NAME_1, "qualifier");
  }

  @Test
  public void testEqualsMethod() {
    TargetQualifierMapKey key1 = new TargetQualifierMapKey(TARGET_NAME_1, "qualifier");
    TargetQualifierMapKey key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), "qualifier");
    assertEquals(key1, key1);

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, "qualifier");
    key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), "qualifier");
    assertEquals(key1, key2);

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    key2 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    assertEquals(key1, key2);

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), null);
    assertEquals(key1, key2);

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, "qualifier");
    key2 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    assertNotSame(key1, key2);

    key1 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), null);
    key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "wrong"), null);
    assertNotSame(key1, key2);
  }

  @Test
  public void testHashMethod() {
    TargetQualifierMapKey key1 = new TargetQualifierMapKey(TARGET_NAME_1, "qualifier");
    TargetQualifierMapKey key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), "qualifier");
    assertEquals(key1.hashCode(), key1.hashCode());

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, "qualifier");
    key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), "qualifier");
    assertEquals(key1.hashCode(), key2.hashCode());

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    key2 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    assertEquals(key1.hashCode(), key2.hashCode());

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), null);
    assertEquals(key1.hashCode(), key2.hashCode());

    key1 = new TargetQualifierMapKey(TARGET_NAME_1, "qualifier");
    key2 = new TargetQualifierMapKey(TARGET_NAME_1, null);
    assertNotSame(key1.hashCode(), key2.hashCode());

    key1 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "name"), null);
    key2 = new TargetQualifierMapKey(new FullQualifiedName("namespace", "wrong"), null);
    assertNotSame(key1.hashCode(), key2.hashCode());
  }

  private void createAndCheckForEdmException(final FullQualifiedName fqn, final String qualifier) {
    try {
      new TargetQualifierMapKey(fqn, qualifier);
    } catch (EdmException e) {
      return;
    }
    fail("EdmException expected for parameters: " + fqn + " " + qualifier);
  }

}
