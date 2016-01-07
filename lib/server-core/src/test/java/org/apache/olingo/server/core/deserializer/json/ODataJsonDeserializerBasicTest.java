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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.junit.Test;

public class ODataJsonDeserializerBasicTest {

  private final ODataDeserializer deserializer;

  public ODataJsonDeserializerBasicTest() throws DeserializerException {
    deserializer = OData.newInstance().createDeserializer(ContentType.JSON);
  }

  @Test
  public void reference() throws Exception {
    String entityString = "{"
        + "\"@odata.context\": \"$metadata#$ref\","
        + "\"@odata.id\": \"ESAllPrim(0)\""
        + "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    final List<URI> entityReferences = deserializer.entityReferences(stream).getEntityReferences();
    assertEquals(1, entityReferences.size());
    assertEquals("ESAllPrim(0)", entityReferences.get(0).toASCIIString());
  }

  @Test
  public void references() throws Exception {
    String entityString = "{" +
        "  \"@odata.context\": \"$metadata#Collection($ref)\"," +
        "  \"value\": [" +
        "    { \"@odata.id\": \"ESAllPrim(0)\" }," +
        "    { \"@odata.id\": \"ESAllPrim(1)\" }" +
        "  ]" +
        "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    final List<URI> entityReferences = deserializer.entityReferences(stream).getEntityReferences();

    assertEquals(2, entityReferences.size());
    assertEquals("ESAllPrim(0)", entityReferences.get(0).toASCIIString());
    assertEquals("ESAllPrim(1)", entityReferences.get(1).toASCIIString());
  }

  @Test
  public void referencesWithOtherAnnotations() throws Exception {
    String entityString = "{" +
        "  \"@odata.context\": \"$metadata#Collection($ref)\"," +
        "  \"value\": [" +
        "    { \"@odata.id\": \"ESAllPrim(0)\" }," +
        "    { \"@odata.nonExistingODataAnnotation\": \"ESAllPrim(1)\" }" +
        "  ]" +
        "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    final List<URI> entityReferences = deserializer.entityReferences(stream).getEntityReferences();

    assertEquals(1, entityReferences.size());
    assertEquals("ESAllPrim(0)", entityReferences.get(0).toASCIIString());
  }

  @Test
  public void referencesWithCustomAnnotation() throws Exception {
    String entityString = "{" +
        "  \"@odata.context\": \"$metadata#Collection($ref)\"," +
        "  \"value\": [" +
        "    { \"@odata.id\": \"ESAllPrim(0)\" }," +
        "    { \"@invalid\": \"ESAllPrim(1)\" }" +
        "  ]" +
        "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    final List<URI> entityReferences = deserializer.entityReferences(stream).getEntityReferences();

    assertEquals(1, entityReferences.size());
    assertEquals("ESAllPrim(0)", entityReferences.get(0).toASCIIString());
  }

  @Test
  public void referenceEmpty() throws Exception {
    final String entityString = "{\"@odata.context\": \"$metadata#Collection($ref)\","
        + "  \"value\": [ ]"
        + "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    final List<URI> entityReferences = deserializer.entityReferences(stream).getEntityReferences();
    assertEquals(0, entityReferences.size());
  }

  @Test(expected = DeserializerException.class)
  public void referencesEmpty() throws Exception {
    /*
     * See OData JSON Format chapter 13
     * ... the object that MUST contain the id of the referenced entity
     */
    InputStream stream = new ByteArrayInputStream(new byte[] { '{', '}' });
    deserializer.entityReferences(stream).getEntityReferences();
  }

  @Test(expected = DeserializerException.class)
  public void referencesNoContent() throws Exception {
    deserializer.entityReferences(new ByteArrayInputStream(new byte[] {}));
  }

  @Test(expected = DeserializerException.class)
  public void referencesInvalidJson() throws Exception {
    deserializer.entityReferences(new ByteArrayInputStream(new byte[] { 'A' }));
  }

  @Test(expected = DeserializerException.class)
  public void referenceValueIsNotAnArray() throws Exception {
    String entityString = "{" +
        "  \"@odata.context\": \"$metadata#Collection($ref)\"," +
        "  \"value\": \"ESAllPrim(0)\"" + // This is not allowed. Value must be followed by an array
        "}";
    deserializer.entityReferences(new ByteArrayInputStream(entityString.getBytes()));
  }
}
