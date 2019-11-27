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
package org.apache.olingo.netty.server.core;

import static org.junit.Assert.assertNotNull;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.netty.server.api.ODataNetty;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.Test;

public class ODataNettyImplTest {

  private final ODataNetty odata = ODataNetty.newInstance();

  @Test
  public void serializerSupportedFormats() throws SerializerException {
    assertNotNull(odata.createSerializer(ContentType.JSON_NO_METADATA));
    assertNotNull(odata.createSerializer(ContentType.JSON));
    assertNotNull(odata.createSerializer(ContentType.APPLICATION_JSON));
    assertNotNull(odata.createSerializer(ContentType.JSON_FULL_METADATA));
    
  }

  @Test
  public void deserializerSupportedFormats() throws DeserializerException {
    assertNotNull(odata.createDeserializer(ContentType.JSON_NO_METADATA));
    assertNotNull(odata.createDeserializer(ContentType.JSON));
    assertNotNull(odata.createDeserializer(ContentType.JSON_FULL_METADATA));
    assertNotNull(odata.createDeserializer(ContentType.APPLICATION_JSON));
  }

  @Test
  public void serializerFixedFormat() throws DeserializerException {
    assertNotNull(odata.createFixedFormatSerializer());
  }
  
  @Test
  public void deserializerFixedFormat() throws DeserializerException {
    assertNotNull(odata.createFixedFormatDeserializer());
  }
  
  @Test
  public void testCreateETagHelper() {
    assertNotNull(odata.createETagHelper());
  }
  
  @Test
  public void testCreateUriHelper() {
    assertNotNull(odata.createUriHelper());
  }
  
  @Test
  public void testCreateDebugResponseHelper() {
    assertNotNull(odata.createDebugResponseHelper("json"));
  }
}
