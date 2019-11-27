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
package org.apache.olingo.server.core.uri;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UriHelperTest {

  private static final OData odata = OData.newInstance();
  private static final Edm edm = odata.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();
  private static final EdmEntityContainer container = edm.getEntityContainer();
  private static final UriHelper helper = odata.createUriHelper();
  private final DataProvider data = new DataProvider(odata, edm);

  @Test
  public void canonicalURL() throws Exception {
    final EdmEntitySet entitySet = container.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(entitySet).getEntities().get(0);
    Assert.assertEquals("ESAllPrim(32767)", helper.buildCanonicalURL(entitySet, entity));
  }

  @Test
  public void canonicalURLLong() throws Exception {
    final EdmEntitySet entitySet = container.getEntitySet("ESAllKey");
    final Entity entity = data.readAll(entitySet).getEntities().get(0);
    Assert.assertEquals("ESAllKey("
        + "PropertyString='First',"
        + "PropertyBoolean=true,"
        + "PropertyByte=255,"
        + "PropertySByte=127,"
        + "PropertyInt16=32767,"
        + "PropertyInt32=2147483647,"
        + "PropertyInt64=9223372036854775807,"
        + "PropertyDecimal=34,"
        + "PropertyDate=2012-12-03,"
        + "PropertyDateTimeOffset=2012-12-03T07%3A16%3A23Z,"
        + "PropertyDuration=duration'PT6S',"
        + "PropertyGuid=01234567-89ab-cdef-0123-456789abcdef,"
        + "PropertyTimeOfDay=02%3A48%3A21)",
        helper.buildCanonicalURL(entitySet, entity));
  }

  @Test(expected = SerializerException.class)
  public void canonicalURLWrong() throws Exception {
    final EdmEntitySet entitySet = container.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(entitySet).getEntities().get(0);
    entity.getProperty("PropertyInt16").setValue(ValueType.PRIMITIVE, "wrong");
    helper.buildCanonicalURL(entitySet, entity);
  }
  
  @Rule
  public ExpectedException expectedEx = ExpectedException.none(); 
  
  @Test(expected = SerializerException.class)
  public void canonicalURLWithoutKeys() throws Exception {
    final EdmEntitySet entitySet = container.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(entitySet).getEntities().get(0);
    List<Property> properties = entity.getProperties();
    properties.remove(0);
    helper.buildCanonicalURL(entitySet, entity);
    expectedEx.expect(SerializerException.class);
    expectedEx.expectMessage("Key Value Cannot be null for property: PropertyInt16");
  }
  
  @Test(expected = SerializerException.class)
  public void canonicalURLWithKeyHavingNullValue() throws Exception {
    final EdmEntitySet entitySet = container.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(entitySet).getEntities().get(0);
    Property property = entity.getProperties().get(0);
    property.setValue(property.getValueType(), null);
    helper.buildCanonicalURL(entitySet, entity);
    expectedEx.expect(SerializerException.class);
    expectedEx.expectMessage("Wrong key value!");
  }
}
