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
package org.apache.olingo.client.core.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.serialization.ODataSerializerException;
import org.junit.Test;

public class PropertyTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  @Test
  public void readPropertyValue() throws IOException {
    final InputStream input = getClass().getResourceAsStream("Customer_-10_CustomerId_value.txt");

    final ODataPrimitiveValue value = getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.String).
            setValue(IOUtils.toString(input)).
            build();
    assertNotNull(value);
    assertEquals("-10", value.toString());
  }

  private void primitive(final ODataFormat format)
      throws EdmPrimitiveTypeException, ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Customer_-10_CustomerId." + getSuffix(format));
    final ODataProperty property = getClient().getReader().readProperty(input, format);
    assertNotNull(property);
    assertTrue(property.hasPrimitiveValue());
    assertTrue(-10 == property.getPrimitiveValue().toCastValue(Integer.class));

    ODataProperty comparable;
    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, format), format);
    if (format == ODataFormat.XML) {
      comparable = written;
    } else {
      // This is needed because type information gets lost with JSON serialization
      final ODataPrimitiveValue typedValue = getClient().getObjectFactory().newPrimitiveValueBuilder().
              setType(property.getPrimitiveValue().getTypeKind()).
              setValue(written.getPrimitiveValue().toValue()).
              build();
      comparable = getClient().getObjectFactory().newPrimitiveProperty(written.getName(), typedValue);
    }

    assertEquals(property, comparable);
  }

  @Test
  public void xmlPrimitive() throws Exception {
    primitive(ODataFormat.XML);
  }

  @Test
  public void jsonPrimitive() throws Exception {
    primitive(ODataFormat.JSON);
  }

  private void complex(final ODataFormat format) throws ODataDeserializerException, ODataSerializerException {
    final InputStream input = getClass().getResourceAsStream("Customer_-10_PrimaryContactInfo." + getSuffix(format));
    final ODataProperty property = getClient().getReader().readProperty(input, format);
    assertNotNull(property);
    assertTrue(property.hasComplexValue());
    assertEquals(6, property.getComplexValue().size());

    ODataProperty comparable;
    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, format), format);
    if (format == ODataFormat.XML) {
      comparable = written;
    } else {
      // This is needed because type information gets lost with JSON serialization
      final ODataComplexValue<ODataProperty> typedValue = getClient().getObjectFactory().
              newComplexValue(property.getComplexValue().getTypeName());
      for (final Iterator<ODataProperty> itor = written.getComplexValue().iterator(); itor.hasNext();) {
        final ODataProperty prop = itor.next();
        typedValue.add(prop);
      }
      comparable = getClient().getObjectFactory().newComplexProperty(written.getName(), typedValue);
    }

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
    final InputStream input = getClass().getResourceAsStream("Customer_-10_BackupContactInfo." + getSuffix(format));
    final ODataProperty property = getClient().getReader().readProperty(input, format);
    assertNotNull(property);
    assertTrue(property.hasCollectionValue());
    assertEquals(9, property.getCollectionValue().size());

    ODataProperty comparable;
    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, format), format);
    if (format == ODataFormat.XML) {
      comparable = written;
    } else {
      // This is needed because type information gets lost with JSON serialization
      final ODataCollectionValue<ODataValue> typedValue = getClient().getObjectFactory().
              newCollectionValue(property.getCollectionValue().getTypeName());
      for (final Iterator<ODataValue> itor = written.getCollectionValue().iterator(); itor.hasNext();) {
        final ODataValue value = itor.next();
        typedValue.add(value);
      }
      comparable = getClient().getObjectFactory().newCollectionProperty(written.getName(), typedValue);
    }

    assertEquals(property, comparable);
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
