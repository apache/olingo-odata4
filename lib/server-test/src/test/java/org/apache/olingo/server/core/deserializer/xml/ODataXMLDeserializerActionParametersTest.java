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
package org.apache.olingo.server.core.deserializer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.junit.Test;

public class ODataXMLDeserializerActionParametersTest extends AbstractODataDeserializerTest {

  private static final String PREAMBLE = "<?xml version='1.0' encoding='UTF-8'?>"
      + "<metadata:parameters xmlns:data=\"" + Constants.NS_DATASERVICES + "\""
      + " xmlns:metadata=\"" + Constants.NS_METADATA + "\">";
  private static final String POSTAMBLE = "</metadata:parameters>";

  @Test
  public void empty() throws Exception {
    final Map<String, Parameter> parameters = deserialize(PREAMBLE + POSTAMBLE, "UART", null);
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void primitive() throws Exception {
    final String input = PREAMBLE
        + "<data:ParameterDuration>P42DT11H22M33S</data:ParameterDuration>"
        + "<data:ParameterInt16>42</data:ParameterInt16>"
        + POSTAMBLE;

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
  public void primitiveCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterByte",
        "<metadata:element>0</metadata:element>"
        + "<metadata:element>42</metadata:element>"
        + "<metadata:element>255</metadata:element>");
    assertNotNull(parameter);
    assertTrue(parameter.isPrimitive());
    assertTrue(parameter.isCollection());
    assertEquals((short) 0, parameter.asCollection().get(0));
    assertEquals((short) 42, parameter.asCollection().get(1));
    assertEquals((short) 255, parameter.asCollection().get(2));
  }

  @Test
  public void enumeration() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("ParameterEnum", "String3,String1");
    assertTrue(parameter.isEnum());
    assertFalse(parameter.isCollection());
    assertEquals((short) 5, parameter.getValue());
  }

  @Test
  public void enumCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterEnum",
        "<metadata:element>String1,String2</metadata:element>"
        + "<metadata:element>String3,String3,String3</metadata:element>");
    assertTrue(parameter.isEnum());
    assertTrue(parameter.isCollection());
    assertEquals((short) 3, parameter.asCollection().get(0));
    assertEquals((short) 4, parameter.asCollection().get(1));
  }

  @Test
  public void typeDefinition() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("ParameterDef", "Test String");
    assertTrue(parameter.isPrimitive());
    assertFalse(parameter.isCollection());
    assertEquals("Test String", parameter.getValue());
  }

  @Test
  public void typeDefinitionCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterDef",
        "<metadata:element>Test String</metadata:element>"
        + "<metadata:element>Another String</metadata:element>");
    assertTrue(parameter.isPrimitive());
    assertTrue(parameter.isCollection());
    assertEquals("Test String", parameter.asCollection().get(0));
    assertEquals("Another String", parameter.asCollection().get(1));
  }

  @Test
  public void complex() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("ParameterComp",
        "<data:PropertyInt16>42</data:PropertyInt16>    <data:PropertyString>Yes</data:PropertyString>");
    assertNotNull(parameter);
    assertTrue(parameter.isComplex());
    assertFalse(parameter.isCollection());
    final List<Property> complexValues = parameter.asComplex().getValue();
    assertEquals((short) 42, complexValues.get(0).getValue());
    assertEquals("Yes", complexValues.get(1).getValue());
  }

  @Test
  public void complexCollection() throws Exception {
    final Parameter parameter = deserializeUARTByteNineParam("CollParameterComp",
        "<metadata:element>"
        + "<data:PropertyInt16>9999</data:PropertyInt16><data:PropertyString>One</data:PropertyString>"
        + "</metadata:element>"
        + "<metadata:element>"
        + "<data:PropertyInt16>-123</data:PropertyInt16><data:PropertyString>Two</data:PropertyString>"
        + "</metadata:element>");
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
  public void boundEmpty() throws Exception {
    final Map<String, Parameter> parameters = deserialize(PREAMBLE + POSTAMBLE,
        "BAETAllPrimRT", "ETAllPrim");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void parameterWithNullLiteral() throws Exception {
    final String input = PREAMBLE
        + "<data:ParameterInt16>1</data:ParameterInt16>"
        + "<data:ParameterDuration metadata:null=\"true\" />"
        + POSTAMBLE;
    final Map<String, Parameter> parameters = deserialize(input, "UARTCollStringTwoParam", null);
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
  public void bindingParameter() throws Exception {
    final String input = PREAMBLE + "<data:ParameterETAllPrim>1</data:ParameterETAllPrim>" + POSTAMBLE;
    deserialize(input, "BAETAllPrimRT", "ETAllPrim");
  }

  @Test
  public void wrongName() throws Exception {
    expectException(PREAMBLE + "<data:ParameterWrong>1</data:ParameterWrong>" + POSTAMBLE,
        "UARTParam", null, MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void nullNotNullable() throws Exception {
    expectException(PREAMBLE + "<data:ParameterInt16>null</data:ParameterInt16>" + POSTAMBLE,
        "UARTCTTwoPrimParam", null, MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void missingParameter() throws Exception {
    expectException(PREAMBLE + POSTAMBLE, "UARTCTTwoPrimParam", null, MessageKeys.INVALID_NULL_PARAMETER);
  }

  @Test
  public void parameterTwice() throws Exception {
    expectException(PREAMBLE
        + "<data:ParameterInt16>1</data:ParameterInt16>"
        + "<data:ParameterInt16>2</data:ParameterInt16>"
        + POSTAMBLE,
        "UARTParam", null, MessageKeys.DUPLICATE_PROPERTY);
  }

  private Parameter deserializeUARTByteNineParam(final String parameterName, final String parameterXmlValue)
      throws DeserializerException {
    final Map<String, Parameter> parameters = deserialize(
        PREAMBLE + (parameterName.equals("CollParameterByte") ? "" : "<data:CollParameterByte />")
            + (parameterName.equals("CollParameterEnum") ? "" : "<data:CollParameterEnum />")
            + (parameterName.equals("CollParameterDef") ? "" : "<data:CollParameterDef />")
            + (parameterName.equals("CollParameterComp") ? "" : "<data:CollParameterComp />")
            + (parameterName.equals("CollParameterETTwoPrim") ? "" : "<data:CollParameterETTwoPrim />")
            + "<data:" + parameterName + ">" + parameterXmlValue + "</data:" + parameterName + ">"
            + POSTAMBLE,
        "UARTByteNineParam", null);
    assertNotNull(parameters);
    assertEquals(9, parameters.size());
    Parameter parameter = parameters.get(parameterName);
    assertNotNull(parameter);
    return parameter;
  }

  private Map<String, Parameter> deserialize(final String input, final String actionName, final String bindingTypeName)
      throws DeserializerException {
    return OData.newInstance().createDeserializer(ContentType.APPLICATION_XML, metadata)
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
