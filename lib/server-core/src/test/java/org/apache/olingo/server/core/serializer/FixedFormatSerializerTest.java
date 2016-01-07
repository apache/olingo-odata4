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
package org.apache.olingo.server.core.serializer;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.serializer.FixedFormatSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveValueSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.Test;

public class FixedFormatSerializerTest {

  private final FixedFormatSerializer serializer;

  public FixedFormatSerializerTest() throws SerializerException {
    serializer = OData.newInstance().createFixedFormatSerializer();
  }

  @Test
  public void binary() throws Exception {
    assertEquals("ABC", IOUtils.toString(serializer.binary(new byte[] { 0x41, 0x42, 0x43 })));
  }

  @Test
  public void count() throws Exception {
    assertEquals("42", IOUtils.toString(serializer.count(42)));
  }

  @Test
  public void primitiveValue() throws Exception {
    final EdmPrimitiveType type = OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32);
    assertEquals("42", IOUtils.toString(serializer.primitiveValue(type, 42,
        PrimitiveValueSerializerOptions.with().nullable(true).build())));
  }
}
