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

import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EntityTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  private void readAndWrite(final ODataFormat format) throws ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("Customer_-10." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
        getClient().getDeserializer(format).toEntity(input));
    assertNotNull(entity);

    assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", entity.getTypeName().toString());
    assertTrue(entity.getEditLink().toASCIIString().endsWith("/Customer(-10)"));
    assertEquals(5, entity.getNavigationLinks().size());
    assertEquals(2, entity.getMediaEditLinks().size());

    boolean check = false;

    for (ODataLink link : entity.getNavigationLinks()) {
      if ("Wife".equals(link.getName()) && (link.getLink().toASCIIString().endsWith("/Customer(-10)/Wife"))) {
        check = true;
      }
    }

    assertTrue(check);

    final ODataEntity written = getClient().getBinder().getODataEntity(
        new ResWrap<Entity>((URI) null, null, getClient().getBinder().getEntity(entity)));
    assertEquals(entity, written);
  }

  @Test
  public void fromAtom() throws Exception {
    readAndWrite(ODataFormat.ATOM);
  }

  @Test
  public void fromJSON() throws Exception {
    readAndWrite(ODataFormat.JSON_FULL_METADATA);
  }

  private void readGeospatial(final ODataFormat format) throws ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("AllGeoTypesSet_-8." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
        getClient().getDeserializer(format).toEntity(input));
    assertNotNull(entity);

    boolean found = false;
    for (ODataProperty property : entity.getProperties()) {
      if ("GeogMultiLine".equals(property.getName())) {
        found = true;
        assertTrue(property.hasPrimitiveValue());
        assertEquals(EdmPrimitiveTypeKind.GeographyMultiLineString, property.getPrimitiveValue().getTypeKind());
      }
    }
    assertTrue(found);

    final ODataEntity written = getClient().getBinder().getODataEntity(
        new ResWrap<Entity>((URI) null, null, getClient().getBinder().getEntity(entity)));
    assertEquals(entity, written);
  }

  @Test
  public void withGeospatialFromAtom() throws Exception {
    readGeospatial(ODataFormat.ATOM);
  }

  @Test
  public void withGeospatialFromJSON() throws Exception {
    // this needs to be full, otherwise there is no mean to recognize geospatial types
    readGeospatial(ODataFormat.JSON_FULL_METADATA);
  }

  private void withActions(final ODataFormat format) throws ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("ComputerDetail_-10." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
        getClient().getDeserializer(format).toEntity(input));
    assertNotNull(entity);

    assertEquals(1, entity.getOperations().size());
    assertEquals("ResetComputerDetailsSpecifications", entity.getOperations().get(0).getTitle());

    final ODataEntity written = getClient().getBinder().getODataEntity(
        new ResWrap<Entity>((URI) null, null, getClient().getBinder().getEntity(entity)));
    entity.getOperations().clear();
    assertEquals(entity, written);
  }

  @Test
  public void withActionsFromAtom() throws Exception {
    withActions(ODataFormat.ATOM);
  }

  @Test
  public void withActionsFromJSON() throws Exception {
    // this needs to be full, otherwise actions will not be provided
    withActions(ODataFormat.JSON_FULL_METADATA);
  }

  private void mediaEntity(final ODataFormat format) throws ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("Car_16." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
        getClient().getDeserializer(format).toEntity(input));
    assertNotNull(entity);
    assertTrue(entity.isMediaEntity());
    assertNotNull(entity.getMediaContentSource());
    assertNotNull(entity.getMediaContentType());

    final ODataEntity written = getClient().getBinder().getODataEntity(
        new ResWrap<Entity>((URI) null, null, getClient().getBinder().getEntity(entity)));
    assertEquals(entity, written);
  }

  @Test
  public void mediaEntityFromAtom() throws Exception {
    mediaEntity(ODataFormat.ATOM);
  }

  @Test
  public void mediaEntityFromJSON() throws Exception {
    mediaEntity(ODataFormat.JSON_FULL_METADATA);
  }

  private void issue128(final ODataFormat format) throws EdmPrimitiveTypeException, ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("AllGeoTypesSet_-5." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
        getClient().getDeserializer(format).toEntity(input));
    assertNotNull(entity);

    final ODataProperty geogCollection = entity.getProperty("GeogCollection");
    assertEquals(EdmPrimitiveTypeKind.GeographyCollection, geogCollection.getPrimitiveValue().getTypeKind());

    int count = 0;
    for (Geospatial geo : geogCollection.getPrimitiveValue().toCastValue(GeospatialCollection.class)) {
      assertNotNull(geo);
      count++;
    }
    assertEquals(2, count);
  }

  @Test
  public void issue128FromAtom() throws Exception {
    issue128(ODataFormat.ATOM);
  }

  @Test
  public void issue128FromJSON() throws Exception {
    issue128(ODataFormat.JSON_FULL_METADATA);
  }
}
