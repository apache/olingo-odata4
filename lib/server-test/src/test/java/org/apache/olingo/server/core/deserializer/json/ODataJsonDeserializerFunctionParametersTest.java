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

import java.util.List;

import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.junit.Test;

public class ODataJsonDeserializerFunctionParametersTest extends AbstractODataDeserializerTest {

  @Test
  public void empty() throws Exception {
    final Parameter parameter = deserialize("{}", "UFCRTETTwoKeyNavParamCTTwoPrim", "ParameterCTTwoPrim");
    assertNotNull(parameter);
    assertTrue(parameter.isComplex());
  }

  @Test
  public void primitive() throws Exception {
    final Parameter parameter = deserialize("'test'", "UFCRTCollStringTwoParam", "ParameterString");
    assertNotNull(parameter);
    assertTrue(parameter.isPrimitive());
    assertFalse(parameter.isCollection());
    assertEquals("test", parameter.getValue());
  }

  @Test
  public void complex() throws Exception {
    final Parameter parameter = deserialize("{ \"PropertyString\": \"Yes\", \"PropertyInt16\": 42 }",
        "UFCRTETTwoKeyNavParamCTTwoPrim", "ParameterCTTwoPrim");
    assertNotNull(parameter);
    assertTrue(parameter.isComplex());
    assertFalse(parameter.isCollection());
    final List<Property> complexValues = parameter.asComplex().getValue();
    assertEquals((short) 42, complexValues.get(0).getValue());
    assertEquals("Yes", complexValues.get(1).getValue());
  }

  @Test
  public void ignoreODataAnnotations() throws Exception {
    final Parameter parameter = deserialize("{\"PropertyInt16@odata.type\":\"Edm.Int16\",\"PropertyInt16\":42,"
        + "\"PropertyString\":\"Test\"}",
        "UFCRTETTwoKeyNavParamCTTwoPrim", "ParameterCTTwoPrim");
    assertNotNull(parameter);
    assertTrue(parameter.isComplex());
    assertFalse(parameter.isCollection());
    final List<Property> complexValues = parameter.asComplex().getValue();
    assertEquals((short) 42, complexValues.get(0).getValue());
    assertEquals("Test", complexValues.get(1).getValue());
  }

  @Test
  public void parameterWithNullLiteral() throws Exception {
    final Parameter parameter = deserialize(null, "UFCRTCollCTTwoPrimTwoParam", "ParameterString");
    assertNotNull(parameter);
    assertEquals(null, parameter.getValue());

    expectException(null, "UFCRTStringTwoParam", "ParameterInt16", MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void noContent() throws Exception {
    expectException("", "UFCRTETTwoKeyNavParamCTTwoPrim", "ParameterCTTwoPrim", MessageKeys.JSON_SYNTAX_EXCEPTION);
  }

  @Test
  public void wrongType() throws Exception {
    expectException("null", "UFCRTStringTwoParam", "ParameterInt16", MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("\"42\"", "UFCRTStringTwoParam", "ParameterInt16", MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("'42'", "UFCRTStringTwoParam", "ParameterInt16", MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("123456", "UFCRTStringTwoParam", "ParameterInt16", MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("[42]", "UFCRTStringTwoParam", "ParameterInt16", MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  private Parameter deserialize(final String input, final String functionName, final String parameterName)
      throws DeserializerException {
    return OData.newInstance().createFixedFormatDeserializer()
        .parameter(input,
            edm.getUnboundFunctions(new FullQualifiedName(NAMESPACE, functionName)).get(0)
                .getParameter(parameterName));
  }

  private void expectException(final String input, final String functionName, final String parameterName,
      final DeserializerException.MessageKeys messageKey) {
    try {
      deserialize(input, functionName, parameterName);
      fail("Expected exception not thrown.");
    } catch (final DeserializerException e) {
      assertEquals(messageKey, e.getMessageKey());
    }
  }
}
