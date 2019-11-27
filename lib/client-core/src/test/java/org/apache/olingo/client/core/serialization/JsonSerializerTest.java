/*
 * Copyright 2018 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.client.core.serialization;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.serialization.ODataSerializerException;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class JsonSerializerTest {

  @Test
  public void testClientEntityJSONWithNull() throws ODataSerializerException {
    String expectedJson = "{\"@odata.type\":\"#test.testClientEntity\","
        + "\"testInt32@odata.type\":\"Int32\","
        + "\"testInt32\":12,"
        + "\"testInt32Null@odata.type\":\"Int32\""
        + ",\"testInt32Null\":null,"
        + "\"testString@odata.type\":\"String\","
        + "\"testString\":\"testString\","
        + "\"testStringNull@odata.type\":\"String\","
        + "\"testStringNull\":null}";

    ODataClient odataClient = ODataClientFactory.getClient();
    ClientObjectFactory objFactory = odataClient.getObjectFactory();
    ClientEntity clientEntity = objFactory.newEntity(new FullQualifiedName("test", "testClientEntity"));

    clientEntity.getProperties().add(
        objFactory.newPrimitiveProperty(
            "testInt32",
            objFactory.newPrimitiveValueBuilder().buildInt32(12)));
    clientEntity.getProperties().add(
        objFactory.newPrimitiveProperty(
            "testInt32Null",
            objFactory.newPrimitiveValueBuilder().buildInt32(null)));
    clientEntity.getProperties().add(
        objFactory.newPrimitiveProperty(
            "testString",
            objFactory.newPrimitiveValueBuilder().buildString("testString")));
    clientEntity.getProperties().add(
        objFactory.newPrimitiveProperty(
            "testStringNull",
            objFactory.newPrimitiveValueBuilder().buildString(null)));

    JsonSerializer jsonSerializer = new JsonSerializer(false, ContentType.JSON_FULL_METADATA);

    StringWriter writer = new StringWriter();
    jsonSerializer.write(writer, odataClient.getBinder().getEntity(clientEntity));
    assertThat(writer.toString(), is(expectedJson));
  }
}
