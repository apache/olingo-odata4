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
package org.apache.olingo.server.tecsvc.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.tecsvc.data.DataProvider.DataProviderException;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class ActionDataProviderTest {

  private final OData oData = OData.newInstance();
  private final Edm edm =
      oData.createServiceMetadata(new EdmTechProvider(), Collections.<EdmxReference> emptyList())
      .getEdm();
  private final Map<String, EntityCollection> data = new DataCreator(oData, edm).getData();

  @Test
  public void actionUARTString() throws Exception {
    Property result = ActionData.primitiveAction("UARTString", null);
    assertNotNull(result);
    assertEquals("UARTString string value", result.asPrimitive());

    result = ActionData.primitiveAction("UARTString", Collections.<String, Parameter> emptyMap());
    assertNotNull(result);
    assertEquals("UARTString string value", result.asPrimitive());
  }

  @Test(expected = DataProviderException.class)
  public void actionUARTStringNegative() throws Exception {
    ActionData.primitiveAction("Wrong", null);
  }

  @Test
  public void actionUARTCollStringTwoParam() throws Exception {
    Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    Parameter paramInt16 = new Parameter();
    paramInt16.setName("ParameterInt16");
    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 3));
    parameters.put("ParameterInt16", paramInt16);

    Parameter paramDuration = new Parameter();
    paramDuration.setName("ParameterDuration");
    paramDuration.setValue(ValueType.PRIMITIVE, new BigDecimal(2));
    parameters.put("ParameterDuration", paramDuration);

    Property result = ActionData.primitiveCollectionAction("UARTCollStringTwoParam", parameters, oData);
    assertNotNull(result);
    assertEquals(3, result.asCollection().size());
  }

  @Test
  public void actionUARTCTTwoPrimParam() throws Exception {
    Parameter paramInt16 = new Parameter();
    paramInt16.setName("ParameterInt16");
    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 3));
    final Map<String, Parameter> parameters = Collections.singletonMap("ParameterInt16", paramInt16);

    Property result = ActionData.complexAction("UARTCTTwoPrimParam", parameters);
    assertNotNull(result);
    ComplexValue value = result.asComplex();
    assertEquals((short) 3, value.getValue().get(0).asPrimitive());

    result = ActionData.complexAction("UARTCTTwoPrimParam", Collections.<String, Parameter> emptyMap());
    assertNotNull(result);
    value = result.asComplex();
    assertEquals((short) 32767, value.getValue().get(0).asPrimitive());
  }

  @Test
  public void actionUARTCollCTTwoPrimParam() throws Exception {
    Parameter paramInt16 = new Parameter();
    paramInt16.setName("ParameterInt16");
    final Map<String, Parameter> parameters = Collections.singletonMap("ParameterInt16", paramInt16);

    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 0));
    Property result = ActionData.complexCollectionAction("UARTCollCTTwoPrimParam", parameters);
    assertNotNull(result);
    assertEquals((short) 0, result.asCollection().size());

    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 1));
    result = ActionData.complexCollectionAction("UARTCollCTTwoPrimParam", parameters);
    assertNotNull(result);
    assertEquals((short) 1, result.asCollection().size());
    ComplexValue value = (ComplexValue) result.asCollection().get(0);
    assertEquals("Test123", value.getValue().get(1).asPrimitive());

    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 2));
    result = ActionData.complexCollectionAction("UARTCollCTTwoPrimParam", parameters);
    assertNotNull(result);
    assertEquals((short) 2, result.asCollection().size());
    value = (ComplexValue) result.asCollection().get(0);
    assertEquals("Test123", value.getValue().get(1).asPrimitive());
    value = (ComplexValue) result.asCollection().get(1);
    assertEquals("Test456", value.getValue().get(1).asPrimitive());

    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 3));
    result = ActionData.complexCollectionAction("UARTCollCTTwoPrimParam", parameters);
    assertNotNull(result);
    assertEquals((short) 3, result.asCollection().size());
    value = (ComplexValue) result.asCollection().get(0);
    assertEquals("Test123", value.getValue().get(1).asPrimitive());
    value = (ComplexValue) result.asCollection().get(1);
    assertEquals("Test456", value.getValue().get(1).asPrimitive());
    value = (ComplexValue) result.asCollection().get(2);
    assertEquals("Test678", value.getValue().get(1).asPrimitive());

    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 4));
    result = ActionData.complexCollectionAction("UARTCollCTTwoPrimParam", parameters);
    assertNotNull(result);
    assertEquals((short) 3, result.asCollection().size());
    value = (ComplexValue) result.asCollection().get(0);
    assertEquals("Test123", value.getValue().get(1).asPrimitive());
    value = (ComplexValue) result.asCollection().get(1);
    assertEquals("Test456", value.getValue().get(1).asPrimitive());
    value = (ComplexValue) result.asCollection().get(2);
    assertEquals("Test678", value.getValue().get(1).asPrimitive());
  }

  @Test
  public void actionUARTETTwoKeyTwoPrimParam() throws Exception {
    Parameter paramInt16 = new Parameter();
    paramInt16.setName("ParameterInt16");
    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 32767));
    final Map<String, Parameter> parameters = Collections.singletonMap("ParameterInt16", paramInt16);

    EntityActionResult result = ActionData.entityAction("UARTETTwoKeyTwoPrimParam", parameters, data, oData, edm);
    assertNotNull(result);
    assertFalse(result.isCreated());
    assertEquals((short) 32767, result.getEntity().getProperty("PropertyInt16").asPrimitive());
  }

  @Test
  public void actionUARTETTwoKeyTwoPrimParamNegative() throws Exception {
    Parameter paramInt16 = new Parameter();
    paramInt16.setName("ParameterInt16");
    paramInt16.setValue(ValueType.PRIMITIVE, new Short((short) 12345));
    final Map<String, Parameter> parameters = Collections.singletonMap("ParameterInt16", paramInt16);

    try {
      ActionData.entityAction("UARTETTwoKeyTwoPrimParam", parameters, data, oData, edm);
      fail("Expected a DataProviderException but wasn't thrown");
    } catch (DataProviderException e) {
      assertEquals("Entity not found with key: 12345", e.getMessage());
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusCode());
    }
  }

  @Test
  public void actionUARTETAllPrimParamWithoutParam() throws Exception {
    final EntityActionResult result = ActionData.entityAction("UARTETAllPrimParam",
        Collections.<String, Parameter> emptyMap(), data, oData, edm);
    assertNotNull(result);
    assertFalse(result.isCreated());
    assertEquals(Short.MAX_VALUE, result.getEntity().getProperty("PropertyInt16").asPrimitive());
  }

  @Test
  public void actionUARTETAllPrimParamWithParam() throws Exception {
    Parameter paramDate = new Parameter();
    paramDate.setName("ParameterDate");
    paramDate.setValue(ValueType.PRIMITIVE, null);
    final Map<String, Parameter> parameters = Collections.singletonMap("ParameterDate", paramDate);

    EntityActionResult result = ActionData.entityAction("UARTETAllPrimParam", parameters, data, oData, edm);
    assertNotNull(result);
    assertTrue(result.isCreated());
    assertEquals((short) 1, result.getEntity().getProperty("PropertyInt16").asPrimitive());
  }

  @Test
  public void actionUARTCollETKeyNavParam() throws Exception {
    Parameter paramInt16 = new Parameter();
    paramInt16.setName("ParameterInt16");
    paramInt16.setValue(ValueType.PRIMITIVE, Short.valueOf((short) 5));
    final Map<String, Parameter> parameters = Collections.singletonMap("ParameterInt16", paramInt16);

    EntityCollection result = ActionData.entityCollectionAction("UARTCollETKeyNavParam", parameters, oData, edm);
    assertNotNull(result);
    assertEquals(5, result.getEntities().size());
  }

  @Test
  public void actionUARTCollETAllPrimParam() throws Exception {
    Parameter paramTimeOfDay = new Parameter();
    paramTimeOfDay.setName("ParameterTimeOfDay");
    paramTimeOfDay.setValue(ValueType.PRIMITIVE, getTime(5, 0, 0));
    final Map<String, Parameter> parameters = Collections.singletonMap("ParameterTimeOfDay", paramTimeOfDay);

    EntityCollection result = ActionData.entityCollectionAction("UARTCollETAllPrimParam", parameters, oData, edm);
    assertNotNull(result);
    assertEquals(5, result.getEntities().size());
  }

  @Test
  public void actionUARTCollETAllPrimParamNoParam() throws Exception {
    final EntityCollection result = ActionData.entityCollectionAction("UARTCollETAllPrimParam",
        Collections.<String, Parameter> emptyMap(), oData, edm);
    assertNotNull(result);
    assertEquals(0, result.getEntities().size());
  }

  private Calendar getTime(final int hour, final int minute, final int second) {
    Calendar time = Calendar.getInstance();
    time.clear();
    time.set(Calendar.HOUR_OF_DAY, hour);
    time.set(Calendar.MINUTE, minute);
    time.set(Calendar.SECOND, second);
    return time;
  }
}
