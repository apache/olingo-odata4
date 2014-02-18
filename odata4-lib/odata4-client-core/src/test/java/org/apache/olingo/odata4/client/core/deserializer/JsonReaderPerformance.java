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
package org.apache.olingo.odata4.client.core.deserializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.olingo.odata4.client.api.deserializer.ComplexValue;
import org.apache.olingo.odata4.client.api.deserializer.Property;
import org.apache.olingo.odata4.client.api.deserializer.StructuralProperty;
import org.apache.olingo.odata4.client.api.deserializer.Value;
import org.apache.olingo.odata4.client.core.deserializer.JsonReader;
import org.apache.olingo.odata4.client.core.testutil.StringHelper;
import org.junit.Test;

public class JsonReaderPerformance {

  @Test
  public void testComplexPropertyPerformance() throws Exception {
    JsonReader consumer = new JsonReader();
    int runs = 1000; // * 100;

    InputStream in = JsonReaderPerformance.class.getResourceAsStream("/complexProperty.json");
    String content = StringHelper.inputStreamToString(in);

//    System.out.println("Started...");
    ComplexValue complex = null;
//    long startTime = System.currentTimeMillis();
    for (int i = 0; i < runs; i++) {
      Property property = consumer.readProperty(StringHelper.encapsulate(content));
      complex = (ComplexValue) ((StructuralProperty) property).getValue();
    }
//    long endTime = System.currentTimeMillis();

//    long duration = endTime - startTime;
//    System.out.println("Duration: " + duration + " ms");
//    System.out.println("Duration per run: " + (duration / (float) runs) + " ms");

    testComplexProperty(complex);
  }

  private void testComplexProperty(final Value value) {
    assertNotNull(value);
    assertTrue(value.isComplex());

    ComplexValue complex = (ComplexValue) value;

    assertEquals("Obere Str. 57", complex.getValue("Street").getContent());
    assertEquals("Berlin", complex.getValue("City").getContent());
    assertNull(complex.getValue("Region").getContent());
    assertEquals("D-12209", complex.getValue("PostalCode").getContent());
  }
}
