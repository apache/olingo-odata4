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

import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.junit.Test;

public class ODataJsonDeserializerActionParametersTest extends AbstractODataDeserializerTest {

  @Test
  public void empty() throws Exception {
    final String input = "{}";
    final List<Parameter> parameters = deserialize(input, "UART");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void primitive() throws Exception {
    final String input = "{\"ParameterDuration\":\"P42DT11H22M33S\",\"ParameterInt16\":42}";
    final List<Parameter> parameters = deserialize(input, "UARTTwoParam");
    assertNotNull(parameters);
    assertEquals(2, parameters.size());
    Parameter parameter = parameters.get(0);
    assertNotNull(parameter);
    assertEquals((short) 42, parameter.getValue());
    parameter = parameters.get(1);
    assertNotNull(parameter);
    assertEquals(BigDecimal.valueOf(3669753), parameter.getValue());
  }

  @Test
  public void boundEmpty() throws Exception {
    final String input = "{}";
    final List<Parameter> parameters = deserialize(input, "BAETAllPrimRT", "ETAllPrim");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test(expected = DeserializerException.class)
  public void bindingParameter() throws Exception {
    deserialize("{\"ParameterETAllPrim\":{\"PropertyInt16\":42}}", "BAETAllPrimRT", "ETAllPrim");
  }

  @Test(expected = DeserializerException.class)
  public void wrongName() throws Exception {
    deserialize("{\"ParameterWrong\":null}", "UARTParam");
  }

  @Test(expected = DeserializerException.class)
  public void nullNotNullable() throws Exception {
    deserialize("{\"ParameterInt16\":null}", "UARTCTTwoPrimParam");
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

  private List<Parameter> deserialize(final String input, final String actionName) throws DeserializerException {
    return OData.newInstance().createDeserializer(ODataFormat.JSON)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            edm.getUnboundAction(new FullQualifiedName("Namespace1_Alias", actionName))).getActionParameter();
  }

  private List<Parameter> deserialize(final String input, final String actionName, final String typeName)
      throws DeserializerException {
    return OData.newInstance().createDeserializer(ODataFormat.JSON)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            edm.getBoundAction(new FullQualifiedName("Namespace1_Alias", actionName),
                new FullQualifiedName("Namespace1_Alias", typeName), false)).getActionParameter();
  }
}
