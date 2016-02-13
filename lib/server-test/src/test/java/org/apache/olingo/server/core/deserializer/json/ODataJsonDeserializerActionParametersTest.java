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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.junit.Test;

public class ODataJsonDeserializerActionParametersTest extends AbstractODataDeserializerTest {

  @Test
  public void empty() throws Exception {
    final Map<String, Parameter> parameters = deserialize("{}", "UART", null);
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void primitive() throws Exception {
    final Map<String, Parameter> parameters = deserialize(
        "{\"ParameterDuration\":\"P42DT11H22M33S\",\"ParameterInt16\":42}",
        "UARTTwoParam", null);
    assertNotNull(parameters);
    assertEquals(2, parameters.size());
    Parameter parameter = parameters.get("ParameterInt16");
    assertNotNull(parameter);
    assertTrue(parameter.isPrimitive());
    assertFalse(parameter.isCollection());
    assertEquals((short) 42, parameter.getValue());
    parameter = parameters.get("ParameterDuration");
    assertNotNull(parameter);
    assertEquals(BigDecimal.valueOf(3669753), parameter.getValue());
  }

  @Test
  public void primitiveCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterByte", "[1,42]");
    assertTrue(parameter.isPrimitive());
    assertTrue(parameter.isCollection());
    assertEquals((short) 1, parameter.asCollection().get(0));
    assertEquals((short) 42, parameter.asCollection().get(1));
  }

  @Test
  public void enumeration() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("ParameterEnum", "\"String3,String1\"");
    assertTrue(parameter.isEnum());
    assertFalse(parameter.isCollection());
    assertEquals((short) 5, parameter.getValue());
  }

  @Test
  public void enumCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterEnum",
        "[ \"String1,String2\", \"String3,String3,String3\" ]");
    assertTrue(parameter.isEnum());
    assertTrue(parameter.isCollection());
    assertEquals((short) 3, parameter.asCollection().get(0));
    assertEquals((short) 4, parameter.asCollection().get(1));
  }

  @Test
  public void typeDefinition() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("ParameterDef", "\"Test String\"");
    assertTrue(parameter.isPrimitive());
    assertFalse(parameter.isCollection());
    assertEquals("Test String", parameter.getValue());
  }

  @Test
  public void typeDefinitionCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterDef",
        "[ \"Test String\", \"Another String\" ]");
    assertTrue(parameter.isPrimitive());
    assertTrue(parameter.isCollection());
    assertEquals("Test String", parameter.asCollection().get(0));
    assertEquals("Another String", parameter.asCollection().get(1));
  }

  @Test
  public void complex() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("ParameterComp",
        "{ \"PropertyString\": \"Yes\", \"PropertyInt16\": 42 }");
    assertTrue(parameter.isComplex());
    assertFalse(parameter.isCollection());
    final List<Property> complexValues = parameter.asComplex().getValue();
    assertEquals((short) 42, complexValues.get(0).getValue());
    assertEquals("Yes", complexValues.get(1).getValue());
  }

  @Test
  public void complexCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterComp",
        "[ { \"PropertyInt16\": 9999, \"PropertyString\": \"One\" },"
        + "  { \"PropertyInt16\": -123, \"PropertyString\": \"Two\" }]");
    assertTrue(parameter.isComplex());
    assertTrue(parameter.isCollection());
    ComplexValue complexValue = (ComplexValue) parameter.asCollection().get(0);
    assertEquals((short) 9999, complexValue.getValue().get(0).getValue());
    assertEquals("One", complexValue.getValue().get(1).getValue());

    complexValue = (ComplexValue) parameter.asCollection().get(1);
    assertEquals((short) -123, complexValue.getValue().get(0).getValue());
    assertEquals("Two", complexValue.getValue().get(1).getValue());
  }

  @Test
  public void entity() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("ParameterETTwoPrim",
        "{ \"PropertyInt16\": 42, \"PropertyString\": \"Yes\" }");
    assertTrue(parameter.isEntity());
    assertFalse(parameter.isCollection());
    final List<Property> entityValues = parameter.asEntity().getProperties();
    assertEquals((short) 42, entityValues.get(0).getValue());
    assertEquals("Yes", entityValues.get(1).getValue());
  }

  @Test
  public void entityCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterETTwoPrim",
        "[ { \"PropertyInt16\": 1234, \"PropertyString\": \"One\" },"
        + "  { \"PropertyInt16\": -321, \"PropertyString\": \"Two\" }]");
    assertTrue(parameter.isEntity());
    assertTrue(parameter.isCollection());
    Entity entity = ((EntityCollection) parameter.getValue()).getEntities().get(0);
    assertEquals((short) 1234, entity.getProperties().get(0).getValue());
    assertEquals("One", entity.getProperties().get(1).getValue());

    entity = ((EntityCollection) parameter.getValue()).getEntities().get(1);
    assertEquals((short) -321, entity.getProperties().get(0).getValue());
    assertEquals("Two", entity.getProperties().get(1).getValue());
  }

  @Test
  public void boundEmpty() throws Exception {
    final Map<String, Parameter> parameters = deserialize("{}", "BAETAllPrimRT", "ETAllPrim");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void ignoreODataAnnotations() throws Exception {
    final String input =
        "{\"ParameterDuration@odata.type\":\"Edm.Duration\","
            + "\"ParameterDuration\":\"P42DT11H22M33S\",\"ParameterInt16\":42}";
    final Map<String, Parameter> parameters = deserialize(input, "UARTTwoParam", null);
    assertNotNull(parameters);
    assertEquals(2, parameters.size());
    Parameter parameter = parameters.get("ParameterInt16");
    assertNotNull(parameter);
    assertEquals((short) 42, parameter.getValue());
    parameter = parameters.get("ParameterDuration");
    assertNotNull(parameter);
    assertEquals(BigDecimal.valueOf(3669753), parameter.getValue());
  }

  @Test
  public void parameterWithNullLiteral() throws Exception {
    final Map<String, Parameter> parameters = deserialize("{\"ParameterInt16\":1,\"ParameterDuration\":null}",
        "UARTCollStringTwoParam", null);
    assertNotNull(parameters);
    assertEquals(2, parameters.size());
    Parameter parameter = parameters.get("ParameterInt16");
    assertNotNull(parameter);
    assertEquals((short) 1, parameter.getValue());
    parameter = parameters.get("ParameterDuration");
    assertNotNull(parameter);
    assertEquals(null, parameter.getValue());
  }

  @Test
  public void noContent() throws Exception {
    expectException("", "UARTTwoParam", null, MessageKeys.JSON_SYNTAX_EXCEPTION);
    expectException("", "BAETAllPrimRT", "ETAllPrim", MessageKeys.JSON_SYNTAX_EXCEPTION);
  }

  @Test
  public void bindingParameter() throws Exception {
    expectException("{\"ParameterETAllPrim\":{\"PropertyInt16\":42}}", "BAETAllPrimRT", "ETAllPrim",
        MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void missingParameter() throws Exception {
    expectException("{\"ParameterWrong\":null}", "UARTParam", null, MessageKeys.UNKNOWN_CONTENT);
    expectException("{}", "UARTCTTwoPrimParam", null, MessageKeys.INVALID_NULL_PARAMETER);
  }

  @Test
  public void parameterTwice() throws Exception {
    expectException("{\"ParameterInt16\":1,\"ParameterInt16\":2}", "UARTParam", null, MessageKeys.DUPLICATE_PROPERTY);
  }

  @Test
  public void wrongType() throws Exception {
    expectException("{\"ParameterInt16\":null}", "UARTCTTwoPrimParam", null, MessageKeys.INVALID_NULL_PARAMETER);
    expectException("{\"ParameterInt16\":\"42\"}", "UARTParam", null, MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"ParameterInt16\":123456}", "UARTParam", null, MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"ParameterInt16\":[42]}", "UARTParam", null, MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY);
  }

  private Parameter deserializeUARTByteNineParam(final String parameterName, final String parameterJsonValue)
      throws DeserializerException {
    final Map<String, Parameter> parameters = deserialize(
        "{" + (parameterName.equals("CollParameterByte") ? "" : "\"CollParameterByte\":[],")
            + (parameterName.equals("CollParameterEnum") ? "" : "\"CollParameterEnum\":[],")
            + (parameterName.equals("CollParameterDef") ? "" : "\"CollParameterDef\":[],")
            + (parameterName.equals("CollParameterComp") ? "" : "\"CollParameterComp\":[],")
            + (parameterName.equals("CollParameterETTwoPrim") ? "" : "\"CollParameterETTwoPrim\":[],")
            + "\"" + parameterName + "\":" + parameterJsonValue + "}",
        "UARTByteNineParam", null);
    assertNotNull(parameters);
    assertEquals(9, parameters.size());
    Parameter parameter = parameters.get(parameterName);
    assertNotNull(parameter);
    return parameter;
  }

  private Map<String, Parameter> deserialize(final String input, final String actionName, final String bindingTypeName)
      throws DeserializerException {
    return OData.newInstance().createDeserializer(ContentType.JSON, metadata)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            bindingTypeName == null ?
                edm.getUnboundAction(new FullQualifiedName(NAMESPACE, actionName)) :
                edm.getBoundAction(new FullQualifiedName(NAMESPACE, actionName),
                    new FullQualifiedName(NAMESPACE, bindingTypeName),
                    false))
        .getActionParameters();
  }

  private void expectException(final String input, final String actionName, final String bindingTypeName,
      final DeserializerException.MessageKeys messageKey) {
    try {
      deserialize(input, actionName, bindingTypeName);
      fail("Expected exception not thrown.");
    } catch (final DeserializerException e) {
      assertEquals(messageKey, e.getMessageKey());
    }
  }
}
