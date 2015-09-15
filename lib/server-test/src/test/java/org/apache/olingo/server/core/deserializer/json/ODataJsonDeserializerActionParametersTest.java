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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.core.edm.EdmActionImpl;
import org.apache.olingo.commons.core.edm.EdmComplexTypeImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataJsonDeserializerActionParametersTest extends AbstractODataDeserializerTest {

  @Test
  public void empty() throws Exception {
    final String input = "{}";
    final Map<String, Parameter> parameters = deserialize(input, "UART");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void primitive() throws Exception {
    final String input = "{\"ParameterDuration\":\"P42DT11H22M33S\",\"ParameterInt16\":42}";
    final Map<String, Parameter> parameters = deserialize(input, "UARTTwoParam");
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
  public void complex() throws Exception {
    EdmProviderImpl provider = mock(EdmProviderImpl.class);
    CsdlComplexType address = new CsdlComplexType();
    address.setProperties(Arrays.asList(createProperty("Street", "Edm.String"), 
        createProperty("Zip", "Edm.Int32")));
    address.setName("Address");
    EdmComplexTypeImpl edmAddress = new EdmComplexTypeImpl(provider, 
        new FullQualifiedName("namespace.Address"), address);    
    Mockito.stub(provider.getComplexType(Mockito.any(FullQualifiedName.class))).toReturn(edmAddress);
    
    List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
    parameters.add(createParam("param1", "Edm.Int16"));
    parameters.add(createParam("param2", "namespace.Address"));
    parameters.add(createParam("param3", "Edm.Int32").setCollection(true));
    parameters.add(createParam("param4", "Edm.String").setNullable(true));
    
    FullQualifiedName actionName = new FullQualifiedName("namespace", "action");
    CsdlAction csdlAction = new CsdlAction().setName("action1").setParameters(parameters);
    EdmAction action = new EdmActionImpl(provider, actionName, csdlAction);
    
    final String input = "{\n" + 
        "  \"param1\": 42,\n" + 
        "  \"param2\": {\n" + 
        "    \"Street\": \"One Microsoft Way\",\n" + 
        "    \"Zip\": 98052\n" + 
        "  },\n" + 
        "  \"param3\": [ 1, 42, 99 ],\n" + 
        "  \"param4\": null\n" + 
        "}";
    final Map<String, Parameter> response = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON)
        .actionParameters(new ByteArrayInputStream(input.getBytes()), action).getActionParameters();
    
    assertNotNull(response);
    assertEquals(4, response.size());
    Parameter parameter = response.get("param1");
    assertNotNull(response);
    assertEquals((short) 42, parameter.getValue());
    parameter = response.get("param2");
    assertNotNull(parameter);
    ComplexValue addressValue = (ComplexValue)parameter.getValue();
    assertEquals("Street", addressValue.getValue().get(0).getName());
    assertEquals("One Microsoft Way", addressValue.getValue().get(0).getValue());
    assertEquals("Zip", addressValue.getValue().get(1).getName());
    assertEquals(98052, addressValue.getValue().get(1).getValue());
    
    parameter = response.get("param3");
    assertNotNull(parameter);
    assertEquals(Arrays.asList(1, 42, 99), parameter.getValue());
    
    parameter = response.get("param4");
    assertNull(parameter.getValue());
  }
  
  @Test
  public void complexCollection() throws Exception {
    EdmProviderImpl provider = mock(EdmProviderImpl.class);
    CsdlComplexType address = new CsdlComplexType();
    address.setProperties(Arrays.asList(createProperty("Street", "Edm.String"), 
        createProperty("Zip", "Edm.Int32")));
    address.setName("Address");
    EdmComplexTypeImpl edmAddress = new EdmComplexTypeImpl(provider, 
        new FullQualifiedName("namespace.Address"), address);    
    Mockito.stub(provider.getComplexType(Mockito.any(FullQualifiedName.class))).toReturn(edmAddress);
    
    List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
    parameters.add(createParam("param1", "Edm.Int16"));
    parameters.add(createParam("param2", "namespace.Address").setCollection(true));
    parameters.add(createParam("param3", "Edm.Int32").setCollection(true));
    parameters.add(createParam("param4", "Edm.String").setNullable(true));
    
    FullQualifiedName actionName = new FullQualifiedName("namespace", "action");
    CsdlAction csdlAction = new CsdlAction().setName("action1").setParameters(parameters);
    EdmAction action = new EdmActionImpl(provider, actionName, csdlAction);
    
    final String input = "{\n" + 
        "  \"param1\": 42,\n" + 
        "  \"param2\": [{\n" + 
        "    \"Street\": \"One Microsoft Way\",\n" + 
        "    \"Zip\": 98052\n" + 
        "  },\n" +
        "  {\n" + 
        "    \"Street\": \"Two Microsoft Way\",\n" + 
        "    \"Zip\": 98052\n" + 
        "  }],\n" +         
        "  \"param3\": [ 1, 42, 99 ],\n" + 
        "  \"param4\": null\n" + 
        "}";
    final Map<String, Parameter> response = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON)
        .actionParameters(new ByteArrayInputStream(input.getBytes()), action).getActionParameters();
    
    assertNotNull(response);
    assertEquals(4, response.size());
    Parameter parameter = response.get("param1");
    assertNotNull(response);
    assertEquals((short) 42, parameter.getValue());
    parameter = response.get("param2");
    assertNotNull(parameter);
    ComplexValue addressValue = (ComplexValue)((List<?>)parameter.getValue()).get(0);
    assertEquals("One Microsoft Way", addressValue.getValue().get(0).getValue());
    assertEquals(98052, addressValue.getValue().get(1).getValue());

    addressValue = (ComplexValue)((List<?>)parameter.getValue()).get(1);
    assertEquals("Two Microsoft Way", addressValue.getValue().get(0).getValue());
    assertEquals(98052, addressValue.getValue().get(1).getValue());
    
    parameter = response.get("param3");
    assertNotNull(parameter);
    assertEquals(Arrays.asList(1, 42, 99), parameter.getValue());
    
    parameter = response.get("param4");
    assertNull(parameter.getValue());
  }  

  private CsdlParameter createParam(String name, String type) {
    return new CsdlParameter().setName(name).setType(new FullQualifiedName(type));
  }  

  private CsdlProperty createProperty(String name, String type) {
    return new CsdlProperty().setName(name).setType(type);
  }
  
  @Test
  public void boundEmpty() throws Exception {
    final String input = "{}";
    final Map<String, Parameter> parameters = deserialize(input, "BAETAllPrimRT", "ETAllPrim");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void ignoreODataAnnotations() throws Exception {
    final String input =
        "{\"ParameterDuration@odata.type\":\"Edm.Duration\","
            + "\"ParameterDuration\":\"P42DT11H22M33S\",\"ParameterInt16\":42}";
    final Map<String, Parameter> parameters = deserialize(input, "UARTTwoParam");
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
  public void testParameterWithNullLiteral() throws Exception {
    final Map<String, Parameter> parameters = deserialize("{\"ParameterInt16\":1,\"ParameterDuration\":null}", 
        "UARTCollStringTwoParam");
    assertNotNull(parameters);
    assertEquals(2, parameters.size());
    Parameter parameter = parameters.get("ParameterInt16");
    assertNotNull(parameter);
    assertEquals((short) 1, parameter.getValue());
    parameter = parameters.get("ParameterDuration");
    assertNotNull(parameter);
    assertEquals(null, parameter.getValue());
  }

  @Test(expected = DeserializerException.class)
  public void noContent() throws Exception {
    deserialize("", "BAETAllPrimRT", "ETAllPrim");
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
  
  private Map<String, Parameter> deserialize(final String input, final String actionName) throws DeserializerException {
    return OData.newInstance().createDeserializer(CONTENT_TYPE_JSON)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            edm.getUnboundAction(new FullQualifiedName("Namespace1_Alias", actionName))).getActionParameters();
  }

  private Map<String, Parameter> deserialize(final String input, final String actionName, final String typeName)
      throws DeserializerException {
    return OData.newInstance().createDeserializer(CONTENT_TYPE_JSON)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            edm.getBoundAction(new FullQualifiedName("Namespace1_Alias", actionName),
                new FullQualifiedName("Namespace1_Alias", typeName), false)).getActionParameters();
  }
}
