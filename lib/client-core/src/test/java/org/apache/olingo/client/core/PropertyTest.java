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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.api.serialization.ODataSerializerException;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class PropertyTest extends AbstractTest {

  private void _enum(final ContentType contentType) throws ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Products_5_SkinColor." + getSuffix(contentType));
    final ClientProperty property = client.getReader().readProperty(input, contentType);
    assertNotNull(property);
    assertTrue(property.hasEnumValue());

    final ClientProperty written = client.getReader().readProperty(
            client.getWriter().writeProperty(property, contentType), contentType);
    // This is needed because type information gets lost with serialization
    if (contentType.isCompatible(ContentType.APPLICATION_XML)) {
      final ClientProperty comparable = client.getObjectFactory().newEnumProperty(property.getName(),
              client.getObjectFactory().
              newEnumValue(property.getEnumValue().getTypeName(), written.getEnumValue().getValue()));

      assertEquals(property, comparable);
    }
  }

  @Test
  public void xmlEnum() throws Exception {
    _enum(ContentType.APPLICATION_XML);
  }

  @Test
  public void jsonEnum() throws Exception {
    _enum(ContentType.JSON);
  }

  private void complex(final ContentType contentType) throws ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Employees_3_HomeAddress." + getSuffix(contentType));
    final ClientProperty property = client.getReader().readProperty(input, contentType);
    assertNotNull(property);
    assertTrue(property.hasComplexValue());
    assertEquals(3, property.getComplexValue().size());

    final ClientProperty written = client.getReader().readProperty(
            client.getWriter().writeProperty(property, contentType), contentType);
    // This is needed because type information gets lost with JSON serialization
    final ClientComplexValue typedValue = client.getObjectFactory().
            newComplexValue(property.getComplexValue().getTypeName());
    for (final Iterator<ClientProperty> itor = written.getComplexValue().iterator(); itor.hasNext();) {
      final ClientProperty prop = itor.next();
      typedValue.add(prop);
    }
    final ClientProperty comparable = client.getObjectFactory().
            newComplexProperty(property.getName(), typedValue);

    assertEquals(property, comparable);
  }

  @Test
  public void xmlComplex() throws Exception {
    complex(ContentType.APPLICATION_XML);
  }

  @Test
  public void jsonComplex() throws Exception {
    complex(ContentType.JSON);
  }

  private void collection(final ContentType contentType) throws ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Products_5_CoverColors." + getSuffix(contentType));
    final ClientProperty property = client.getReader().readProperty(input, contentType);
    assertNotNull(property);
    assertTrue(property.hasCollectionValue());
    assertEquals(3, property.getCollectionValue().size());

    final ClientProperty written = client.getReader().readProperty(
            client.getWriter().writeProperty(property, contentType), contentType);
    // This is needed because type information gets lost with JSON serialization
    if(contentType.isCompatible(ContentType.APPLICATION_XML)) {
      final ClientCollectionValue<ClientValue> typedValue = client.getObjectFactory().
              newCollectionValue(property.getCollectionValue().getTypeName());
      for (final Iterator<ClientValue> itor = written.getCollectionValue().iterator(); itor.hasNext();) {
        final ClientValue value = itor.next();
        typedValue.add(value);
      }
      final ClientProperty comparable = client.getObjectFactory().
              newCollectionProperty(property.getName(), typedValue);

      assertEquals(property, comparable);
    }
  }

  @Test
  public void xmlCollection() throws Exception {
    collection(ContentType.APPLICATION_XML);
  }

  @Test
  public void jsonCollection() throws Exception {
    collection(ContentType.JSON);
  }
}
