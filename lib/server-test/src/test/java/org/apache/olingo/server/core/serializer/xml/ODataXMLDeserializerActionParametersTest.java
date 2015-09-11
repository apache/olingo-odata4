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
package org.apache.olingo.server.core.serializer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmActionImpl;
import org.apache.olingo.commons.core.edm.EdmComplexTypeImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataXMLDeserializerActionParametersTest {

  @Test
  public void empty() throws Exception {
    final String input = "";
    final Map<String, Parameter> parameters = deserialize(input, "UART");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void primitive() throws Exception {
    final String input = "<?xml version='1.0' encoding='UTF-8'?>"
        +"<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">"        
        +"<ParameterInt16>42</ParameterInt16>"
        +"<ParameterDuration>P42DT11H22M33S</ParameterDuration>"
        +"</metadata:parameters>";
    
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
    
    final String input = "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">\n" + 
        "  <param1>42</param1>\n" + 
        "  <param2 metadata:type=\"#namespace.Address\">\n" + 
        "    <Street>One Microsoft Way</Street>\n" + 
        "    <Zip>98052</Zip>\n" + 
        "  </param2>\n" + 
        "  <param3>\n" + 
        "    <element>1</element>\n" + 
        "    <element>42</element>\n" + 
        "    <element>99</element>\n" + 
        "  </param3>\n" + 
        "  <param4 metadata:null=\"true\"/>\n" + 
        "</metadata:parameters>";
    final Map<String, Parameter> response = OData.newInstance().createDeserializer(ContentType.APPLICATION_XML)
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
    
    final String input = "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">\n" + 
        "  <param1>42</param1>\n" + 
        "  <param2 metadata:type=\"#namespace.Address\">\n" +
        "    <element>" +
        "    <Street>One Microsoft Way</Street>\n" + 
        "    <Zip>98052</Zip>\n" +
        "    </element>" +
        "    <element>" +
        "    <Street>Two Microsoft Way</Street>\n" + 
        "    <Zip>98052</Zip>\n" +
        "    </element>" +        
        "  </param2>\n" + 
        "  <param3>\n" + 
        "    <element>1</element>\n" + 
        "    <element>42</element>\n" + 
        "    <element>99</element>\n" + 
        "  </param3>\n" + 
        "  <param4 metadata:null=\"true\"/>\n" + 
        "</metadata:parameters>";
    final Map<String, Parameter> response = OData.newInstance().createDeserializer(ContentType.APPLICATION_XML)
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
    final String input = "";
    final Map<String, Parameter> parameters = deserialize(input, "BAETAllPrimRT", "ETAllPrim");
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
  }

  @Test
  public void testParameterWithNullLiteral() throws Exception {
    final String input = "<?xml version='1.0' encoding='UTF-8'?>"
        +"<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">"        
        +"<ParameterInt16>1</ParameterInt16>"
        +"</metadata:parameters>";
    
    final Map<String, Parameter> parameters = deserialize(input, 
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
  
  @Test
  public void bindingParameter() throws Exception {
    final String input = "<?xml version='1.0' encoding='UTF-8'?>"
        +"<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">"        
        +"<ParameterETAllPrim>1</ParameterETAllPrim>"
        +"</metadata:parameters>";    
    deserialize(input, "BAETAllPrimRT", "ETAllPrim");
  }

  @Test(expected = DeserializerException.class)
  public void wrongName() throws Exception {
    final String input = "<?xml version='1.0' encoding='UTF-8'?>"
        +"<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">"        
        +"<ParameterWrong>1</ParameterWrong>"
        +"</metadata:parameters>";      
    deserialize(input, "UARTParam");
  }

  @Test(expected = DeserializerException.class)
  public void nullNotNullable() throws Exception {
    final String input = "<?xml version='1.0' encoding='UTF-8'?>"
        +"<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">"        
        +"<ParameterInt16>null</ParameterInt16>"
        +"</metadata:parameters>";     
    deserialize(input, "UARTCTTwoPrimParam");
  }

  @Test(expected = DeserializerException.class)
  public void missingParameter() throws Exception {
    deserialize("", "UARTCTTwoPrimParam");
  }

  @Test(expected = DeserializerException.class)
  public void parameterTwice() throws Exception {
    final String input = "<?xml version='1.0' encoding='UTF-8'?>"
        +"<metadata:parameters xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">"        
        +"<ParameterInt16>1</ParameterInt16>"
        +"<ParameterInt16>2</ParameterInt16>"
        +"</metadata:parameters>";      
    deserialize(input, "UARTParam");
  }
  
  protected static final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();
  
  private Map<String, Parameter> deserialize(final String input, final String actionName) throws DeserializerException {
    return OData.newInstance().createDeserializer(ContentType.APPLICATION_XML)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            edm.getUnboundAction(new FullQualifiedName("Namespace1_Alias", actionName))).getActionParameters();
  }

  private Map<String, Parameter> deserialize(final String input, final String actionName, final String typeName)
      throws DeserializerException {
    return OData.newInstance().createDeserializer(ContentType.APPLICATION_XML)
        .actionParameters(new ByteArrayInputStream(input.getBytes()),
            edm.getBoundAction(new FullQualifiedName("Namespace1_Alias", actionName),
                new FullQualifiedName("Namespace1_Alias", typeName), false)).getActionParameters();
  }
}
