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
package org.apache.olingo.server.core.serializer.json;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.data.ComplexValueImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.junit.Test;

public class ODataJsonSerializerTest {
  @Test
  public void testCollectionComplex() throws ODataException, IOException {
    final List<ComplexValue> col = new ArrayList<ComplexValue>();
    col.add(getValues(1));
    col.add(getValues(2));
    final Property complexCollection = new PropertyImpl(null, "ComplexCol", ValueType.COLLECTION_COMPLEX, col);
    
    final ODataJsonSerializer serializer = new ODataJsonSerializer(ODataFormat.APPLICATION_JSON);
    final ComplexSerializerOptions options = ComplexSerializerOptions.with()
        .contextURL(ContextURL.with().selectList("ComplexCollection").build()).build();
    final InputStream in = serializer.complexCollection(null, ComplexTypeHelper.createType(),
        complexCollection, options);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    String line;
    while ((line = reader.readLine()) != null) {
      if (line.contains("value")) {
        assertEquals(
            "{\"@odata.context\":\"$metadata(ComplexCollection)\",\"value\":"
                + "[{\"prop1\":\"test1\",\"prop2\":\"test11\"},{\"prop1\":\"test2\",\"prop2\":\"test22\"}]}",
            line);
      }
    }

  }

  private ComplexValue getValues(int i) {
    ComplexValue value = new ComplexValueImpl();
    value.getValue().add(new PropertyImpl(null, "prop1", ValueType.PRIMITIVE, "test" + i));
    value.getValue().add(new PropertyImpl(null, "prop2", ValueType.PRIMITIVE, "test" + i + i));
    return value;
  }
}
