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
package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertNull;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmNullTest extends PrimitiveTypeBaseTest {

  @Test
  public void checkNull() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertNull(instance.valueToString(null, null, null, null, null, null));
      assertNull(instance.valueToString(null, true, null, null, null, null));

      expectNullErrorInValueToString(instance);
    }
  }

  @Test
  public void checkValueOfNull() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertNull(instance.valueOfString(null, null, null, null, null, null, instance.getDefaultType()));
      assertNull(instance.valueOfString(null, true, null, null, null, null, instance.getDefaultType()));

      expectNullErrorInValueOfString(instance);
    }
  }
}
