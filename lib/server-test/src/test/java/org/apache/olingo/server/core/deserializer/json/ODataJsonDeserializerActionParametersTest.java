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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.junit.Test;

public class ODataJsonDeserializerActionParametersTest extends AbstractODataDeserializerTest {

  @Test
  public void empty() throws Exception {
    final String input = "{}";
    final Entity entity = deserialize(input, "UART");
    assertNotNull(entity);
    final List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertTrue(properties.isEmpty());
  }

  @Test
  public void primitive() throws Exception {
    final String input = "{\"ParameterDuration\":\"P42DT11H22M33S\",\"ParameterInt16\":42}";
    final Entity entity = deserialize(input, "UARTTwoParam");
    assertNotNull(entity);
    final List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());
    Property property = properties.get(0);
    assertNotNull(property);
    assertEquals((short) 42, property.getValue());
    property = properties.get(1);
    assertNotNull(property);
    assertEquals(BigDecimal.valueOf(3669753), property.getValue());
  }

  @Test(expected = DeserializerException.class)
  public void missingParameter() throws Exception {
    deserialize("{}", "UARTCTTwoPrimParam");
  }

  @Test(expected = DeserializerException.class)
  public void parameterTwice() throws Exception {
    deserialize("{\"ParameterInt16\":1,\"ParameterInt16\":2}", "UARTParam");
  }

  @Test(expected = DeserializerException.class)
  public void wrongType() throws Exception {
    deserialize("{\"ParameterInt16\":\"42\"}", "UARTParam");
  }

  private Entity deserialize(final String input, final String actionName) throws DeserializerException {
    return OData.newInstance().createDeserializer(ODataFormat.JSON)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            edm.getUnboundAction(new FullQualifiedName("Namespace1_Alias", actionName)));
  }
}
