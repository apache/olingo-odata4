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
package org.apache.olingo.server.core.serializer.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODataJsonSerializerTest {

  public static final String PROPERTY_1 = "Property1";

  private ContextURL contextUrl;
  private EdmEntityType edmEntityType;
  private final Logger LOG = LoggerFactory.getLogger(ODataJsonSerializerTest.class);

  private ODataJsonSerializer serializer = new ODataJsonSerializer();

  @Before
  public void prepare() throws Exception {
    contextUrl = ContextURL.getInstance(new URI("http://localhost:8080/test.svc"));
    edmEntityType = Mockito.mock(EdmEntityType.class);
    List<String> propertyNames = Arrays.asList(PROPERTY_1);
    Mockito.when(edmEntityType.getPropertyNames()).thenReturn(propertyNames);

    EdmProperty edmElement = Mockito.mock(EdmProperty.class);
    Mockito.when(edmElement.getName()).thenReturn(PROPERTY_1);
    Mockito.when(edmElement.isPrimitive()).thenReturn(true);
    Mockito.when(edmElement.getMaxLength()).thenReturn(20);
    Mockito.when(edmElement.getType()).thenReturn(EdmString.getInstance());
    Mockito.when(edmEntityType.getProperty(PROPERTY_1)).thenReturn(edmElement);

  }

  @Test
  public void entitySimple() throws Exception {

//    Entity entity = new EntityImpl();
//    entity.addProperty(new PropertyImpl("Edm.String", PROPERTY_1, ValueType.PRIMITIVE, "Value_1"));
    Entity entity = new EntityImpl();
    PropertyImpl property = new PropertyImpl("Edm.String", PROPERTY_1);
    property.setValue(ValueType.PRIMITIVE, "Value_1");
    entity.addProperty(property);

    InputStream result = serializer.entity(edmEntityType, entity, contextUrl);
    String resultString = streamToString(result);
//    System.out.println(resultString);
    Assert.assertEquals("{\"@odata.context\":\"http://localhost:8080/test.svc\",\"Property1\":\"Value_1\"}",
        resultString);
  }

  private String streamToString(final InputStream result) throws IOException {
    byte[] buffer = new byte[8192];
    StringBuilder sb = new StringBuilder();

    int count = result.read(buffer);
    while (count >= 0) {
      sb.append(new String(buffer, 0, count, "UTF-8"));
      count = result.read(buffer);
    }

    return sb.toString();
  }
}
