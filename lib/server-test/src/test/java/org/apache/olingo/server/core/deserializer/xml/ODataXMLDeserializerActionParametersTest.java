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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
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
        + "<ParameterDuration>P42DT11H22M33S</ParameterDuration>"
        + "<ParameterInt16>42</ParameterInt16>"
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
    EdmParameter parameter = mock(EdmParameter.class);
    when(parameter.getType()).thenReturn(
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration));
    when(parameter.isCollection()).thenReturn(true);
    EdmAction action = mock(EdmAction.class);
    when(action.getParameterNames()).thenReturn(Collections.singletonList("Parameter"));
    when(action.getParameter("Parameter")).thenReturn(parameter);

    final String input = PREAMBLE
        + "<Parameter>"
        + "<metadata:element>PT0S</metadata:element>"
        + "<metadata:element>PT42S</metadata:element>"
        + "<metadata:element>PT1H2M3S</metadata:element>"
        + "</Parameter>"
        + POSTAMBLE;
    final Map<String, Parameter> parameters = deserialize(input, action);

    assertNotNull(parameters);
    assertEquals(1, parameters.size());
    Parameter parameterData = parameters.get("Parameter");
    assertNotNull(parameterData);
    assertTrue(parameterData.isPrimitive());
    assertTrue(parameterData.isCollection());
    assertEquals(BigDecimal.ZERO, parameterData.asCollection().get(0));
    assertEquals(BigDecimal.valueOf(42), parameterData.asCollection().get(1));
    assertEquals(BigDecimal.valueOf(3723), parameterData.asCollection().get(2));
  }

  @Test
  public void complex() throws Exception {
    EdmParameter parameter = mock(EdmParameter.class);
    when(parameter.getType()).thenReturn(edm.getComplexType(new FullQualifiedName(NAMESPACE, "CTTwoPrim")));
    EdmAction action = mock(EdmAction.class);
    when(action.getParameterNames()).thenReturn(Collections.singletonList("Parameter"));
    when(action.getParameter("Parameter")).thenReturn(parameter);

    final String input = PREAMBLE
        + "<Parameter>"
        + "<PropertyInt16>42</PropertyInt16>"
        + "<PropertyString>Yes</PropertyString>"
        + "</Parameter>"
        + POSTAMBLE;
    final Map<String, Parameter> parameters = deserialize(input, action);

    assertNotNull(parameters);
    assertEquals(1, parameters.size());
    final Parameter parameterData = parameters.get("Parameter");
    assertNotNull(parameterData);
    assertTrue(parameterData.isComplex());
    assertFalse(parameterData.isCollection());
    final List<Property> complexValues = parameterData.asComplex().getValue();
    assertEquals((short) 42, complexValues.get(0).getValue());
    assertEquals("Yes", complexValues.get(1).getValue());
  }

  @Test
  public void complexCollection() throws Exception {
    EdmParameter parameter = mock(EdmParameter.class);
    when(parameter.getType()).thenReturn(edm.getComplexType(new FullQualifiedName(NAMESPACE, "CTTwoPrim")));
    when(parameter.isCollection()).thenReturn(true);
    EdmAction action = mock(EdmAction.class);
    when(action.getParameterNames()).thenReturn(Collections.singletonList("Parameter"));
    when(action.getParameter("Parameter")).thenReturn(parameter);

    final String input = PREAMBLE
        + "<Parameter>"
        + "<metadata:element>"
        + "<PropertyInt16>9999</PropertyInt16><PropertyString>One</PropertyString>"
        + "</metadata:element>"
        + "<metadata:element>"
        + "<PropertyInt16>-123</PropertyInt16><PropertyString>Two</PropertyString>"
        + "</metadata:element>"
        + "</Parameter>"
        + POSTAMBLE;
    final Map<String, Parameter> parameters = deserialize(input, action);

    assertNotNull(parameters);
    assertEquals(1, parameters.size());
    Parameter parameterData = parameters.get("Parameter");
    assertNotNull(parameterData);
    assertTrue(parameterData.isComplex());
    assertTrue(parameterData.isCollection());
    ComplexValue complexValue = (ComplexValue) parameterData.asCollection().get(0);
    assertEquals((short) 9999, complexValue.getValue().get(0).getValue());
    assertEquals("One", complexValue.getValue().get(1).getValue());

    complexValue = (ComplexValue) parameterData.asCollection().get(1);
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
        + "<ParameterInt16>1</ParameterInt16>"
        + "<ParameterDuration metadata:null=\"true\" />"
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
    final String input = PREAMBLE + "<ParameterETAllPrim>1</ParameterETAllPrim>" + POSTAMBLE;
    deserialize(input, "BAETAllPrimRT", "ETAllPrim");
  }

  @Test
  public void wrongName() throws Exception {
    expectException(PREAMBLE + "<ParameterWrong>1</ParameterWrong>" + POSTAMBLE,
        "UARTParam", null, MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void nullNotNullable() throws Exception {
    expectException(PREAMBLE + "<ParameterInt16>null</ParameterInt16>" + POSTAMBLE,
        "UARTCTTwoPrimParam", null, MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void missingParameter() throws Exception {
    expectException(PREAMBLE + POSTAMBLE, "UARTCTTwoPrimParam", null, MessageKeys.INVALID_NULL_PARAMETER);
  }

  @Test
  public void parameterTwice() throws Exception {
    expectException(PREAMBLE
        + "<ParameterInt16>1</ParameterInt16>"
        + "<ParameterInt16>2</ParameterInt16>"
        + POSTAMBLE,
        "UARTParam", null, MessageKeys.DUPLICATE_PROPERTY);
  }

  private Map<String, Parameter> deserialize(final String input, final EdmAction action) throws DeserializerException {
    return OData.newInstance().createDeserializer(ContentType.APPLICATION_XML)
        .actionParameters(new ByteArrayInputStream(input.getBytes()), action)
        .getActionParameters();
  }

  private Map<String, Parameter> deserialize(final String input, final String actionName, final String bindingTypeName)
      throws DeserializerException {
    return deserialize(input,
        bindingTypeName == null ?
            edm.getUnboundAction(new FullQualifiedName(NAMESPACE, actionName)) :
            edm.getBoundAction(new FullQualifiedName(NAMESPACE, actionName),
                new FullQualifiedName(NAMESPACE, bindingTypeName),
                false));
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
