/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.v4;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.serialization.ODataSerializerException;
import org.junit.Test;

import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PropertyTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  private void _enum(final ODataFormat format) throws ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Products_5_SkinColor." + getSuffix(format));
    final ODataProperty property = getClient().getReader().readProperty(input, format);
    assertNotNull(property);
    assertTrue(property.hasEnumValue());

    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, format), format);
    // This is needed because type information gets lost with serialization
    if (format == ODataFormat.XML) {
      final ODataProperty comparable = getClient().getObjectFactory().newEnumProperty(property.getName(),
              getClient().getObjectFactory().
              newEnumValue(property.getEnumValue().getTypeName(), written.getEnumValue().getValue()));

      assertEquals(property, comparable);
    }
  }

  @Test
  public void xmlEnum() throws Exception {
    _enum(ODataFormat.XML);
  }

  @Test
  public void jsonEnum() throws Exception {
    _enum(ODataFormat.JSON);
  }

  private void complex(final ODataFormat format) throws ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Employees_3_HomeAddress." + getSuffix(format));
    final ODataProperty property = getClient().getReader().readProperty(input, format);
    assertNotNull(property);
    assertTrue(property.hasComplexValue());
    assertEquals(3, property.getComplexValue().size());

    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, format), format);
    // This is needed because type information gets lost with JSON serialization
    final ODataComplexValue typedValue = getClient().getObjectFactory().
            newComplexValue(property.getComplexValue().getTypeName());
    for (final Iterator<ODataProperty> itor = written.getComplexValue().iterator(); itor.hasNext();) {
      final ODataProperty prop = itor.next();
      typedValue.add(prop);
    }
    final ODataProperty comparable = getClient().getObjectFactory().
            newComplexProperty(property.getName(), typedValue);

    assertEquals(property, comparable);
  }

  @Test
  public void xmlComplex() throws Exception {
    complex(ODataFormat.XML);
  }

  @Test
  public void jsonComplex() throws Exception {
    complex(ODataFormat.JSON);
  }

  private void collection(final ODataFormat format) throws ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Products_5_CoverColors." + getSuffix(format));
    final ODataProperty property = getClient().getReader().readProperty(input, format);
    assertNotNull(property);
    assertTrue(property.hasCollectionValue());
    assertEquals(3, property.getCollectionValue().size());

    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, format), format);
    // This is needed because type information gets lost with JSON serialization
    if (format == ODataFormat.XML) {
      final ODataCollectionValue<ODataValue> typedValue = getClient().getObjectFactory().
              newCollectionValue(property.getCollectionValue().getTypeName());
      for (final Iterator<ODataValue> itor = written.getCollectionValue().iterator(); itor.hasNext();) {
        final ODataValue value = itor.next();
        typedValue.add(value);
      }
      final ODataProperty comparable = getClient().getObjectFactory().
              newCollectionProperty(property.getName(), typedValue);

      assertEquals(property, comparable);
    }
  }

  @Test
  public void xmlCollection() throws Exception {
    collection(ODataFormat.XML);
  }

  @Test
  public void jsonCollection() throws Exception {
    collection(ODataFormat.JSON);
  }
}
