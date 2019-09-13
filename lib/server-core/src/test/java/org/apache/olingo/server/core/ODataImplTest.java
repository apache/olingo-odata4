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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataImplTest {

  private final OData odata = OData.newInstance();

  @Test
  public void serializerSupportedFormats() throws SerializerException {
    assertNotNull(odata.createSerializer(ContentType.JSON_NO_METADATA));
    assertNotNull(odata.createSerializer(ContentType.JSON));
    assertNotNull(odata.createSerializer(ContentType.APPLICATION_JSON));
    assertNotNull(odata.createSerializer(ContentType.JSON_FULL_METADATA));
    List<String> versions = new ArrayList<String>();
    versions.add("4.01");
    assertNotNull(odata.createSerializer(ContentType.JSON_FULL_METADATA, versions));
  }

  @Test
  public void deserializerSupportedFormats() throws DeserializerException {
    assertNotNull(odata.createDeserializer(ContentType.JSON_NO_METADATA));
    assertNotNull(odata.createDeserializer(ContentType.JSON));
    assertNotNull(odata.createDeserializer(ContentType.JSON_FULL_METADATA));
    assertNotNull(odata.createDeserializer(ContentType.APPLICATION_JSON));
    List<String> versions = new ArrayList<String>();
    versions.add("4.01");
    assertNotNull(odata.createDeserializer(ContentType.APPLICATION_JSON, versions));
  }

  public void xmlDeserializer() throws DeserializerException {
    assertNotNull(odata.createDeserializer(ContentType.APPLICATION_XML));
  }
  
  @Test(expected=DeserializerException.class)
  public void deserializerWithoutContentType() throws DeserializerException {
    odata.createDeserializer(null);
  }
  
  @Test(expected=DeserializerException.class)
  public void deserializerWithoutContentTypeAndWithVersions() throws DeserializerException {
    List<String> versions = new ArrayList<String>();
    versions.add("4.01");
    odata.createDeserializer(null, versions);
  }
  
  @Test(expected=SerializerException.class)
  public void deltaSerializer() throws SerializerException {
    List<String> versions = new ArrayList<String>();
    versions.add("4.01");
    odata.createEdmDeltaSerializer(null, versions);
  }
  
  @Test(expected=SerializerException.class)
  public void edmAssitedSerializer() throws SerializerException {    
    odata.createEdmAssistedSerializer(null);
  }
  
  @Test(expected=DeserializerException.class)
  public void deserializer1() throws DeserializerException {
    List<String> versions = new ArrayList<String>();
    versions.add("4.01");
    odata.createDeserializer(null, null, versions);
  }
  
  @Test(expected=DeserializerException.class)
  public void deserializer2() throws DeserializerException {
    odata.createDeserializer(null, Mockito.mock(ServiceMetadata.class));
  }
  
  @Test(expected=SerializerException.class)
  public void serializerWithVersions() throws SerializerException {
    List<String> versions = new ArrayList<String>();
    versions.add("4.01");
    odata.createSerializer(null, versions);
  }
  
  @Test(expected=SerializerException.class)
  public void serializer() throws SerializerException {
    odata.createSerializer(null);
  }
}
