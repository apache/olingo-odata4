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
package org.apache.olingo.server.core.deserializer.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;

import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.junit.Test;

public class ODataJsonDeserializerBasicTest {

  @Test
  public void checkSupportedJsonFormats() throws Exception {
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    assertNotNull(deserializer);
    deserializer = null;

    deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON_NO_METADATA);
    assertNotNull(deserializer);
    deserializer = null;

    deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON_FULL_METADATA);
    assertNotNull(deserializer);
    deserializer = null;
  }

  @Test
  public void testReadingCollectionProperties() throws Exception {
    String payload = "{\n" +
        "  \"@odata.context\": \"http://host/service/$metadata#Collection($ref)\",\n" +
        "  \"value\": [\n" +
        "    { \"@odata.id\": \"Orders(10643)\" },\n" +
        "    { \"@odata.id\": \"Orders(10759)\" }\n" +
        "  ]\n" +
        "}";
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    List<URI> values = deserializer.entityReferences(new ByteArrayInputStream(payload.getBytes()));
    assertEquals(2, values.size());
    assertEquals("Orders(10643)", values.get(0).toASCIIString());
    assertEquals("Orders(10759)", values.get(1).toASCIIString());
  }

  @Test
  public void testReadingProperties() throws Exception {
    String payload = "{\n" +
        "  \"@odata.context\": \"http://host/service/$metadata#$ref\",\n" +
        "  \"@odata.id\": \"Orders(10643)\"\n" +
        "}";
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    List<URI> values = deserializer.entityReferences(new ByteArrayInputStream(payload
        .getBytes()));
    assertEquals(1, values.size());
    assertEquals("Orders(10643)", values.get(0).toASCIIString());
  }
}
