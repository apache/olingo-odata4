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

import java.io.InputStream;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.core.op.ResourceFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.junit.Test;

public class EntityTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  private void readAndWrite(final ODataPubFormat format) {
    final InputStream input = getClass().getResourceAsStream("Customer_-10." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
            getClient().getDeserializer().toEntry(input, format));
    assertNotNull(entity);

    assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", entity.getName());
    assertTrue(entity.getEditLink().toASCIIString().endsWith("/Customer(-10)"));
    assertEquals(5, entity.getNavigationLinks().size());
    assertEquals(2, entity.getEditMediaLinks().size());

    boolean check = false;

    for (ODataLink link : entity.getNavigationLinks()) {
      if ("Wife".equals(link.getName()) && (link.getLink().toASCIIString().endsWith("/Customer(-10)/Wife"))) {
        check = true;
      }
    }

    assertTrue(check);

    final ODataEntity written = getClient().getBinder().getODataEntity(getClient().getBinder().
            getEntry(entity, ResourceFactory.entryClassForFormat(format == ODataPubFormat.ATOM)));
    assertEquals(entity, written);
  }

  @Test
  public void fromAtom() {
    readAndWrite(ODataPubFormat.ATOM);
  }

  @Test
  public void fromJSON() {
    readAndWrite(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void readGeospatial(final ODataPubFormat format) {
    final InputStream input = getClass().getResourceAsStream("AllGeoTypesSet_-8." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
            getClient().getDeserializer().toEntry(input, format));
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

    final ODataEntity written = getClient().getBinder().getODataEntity(getClient().getBinder().
            getEntry(entity, ResourceFactory.entryClassForFormat(format == ODataPubFormat.ATOM)));
    assertEquals(entity, written);
  }

  @Test
  public void withGeospatialFromAtom() {
    readGeospatial(ODataPubFormat.ATOM);
  }

  @Test
  public void withGeospatialFromJSON() {
    // this needs to be full, otherwise there is no mean to recognize geospatial types
    readGeospatial(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void withActions(final ODataPubFormat format) {
    final InputStream input = getClass().getResourceAsStream("ComputerDetail_-10." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
            getClient().getDeserializer().toEntry(input, format));
    assertNotNull(entity);

    assertEquals(1, entity.getOperations().size());
    assertEquals("ResetComputerDetailsSpecifications", entity.getOperations().get(0).getTitle());

    final ODataEntity written = getClient().getBinder().getODataEntity(getClient().getBinder().
            getEntry(entity, ResourceFactory.entryClassForFormat(format == ODataPubFormat.ATOM)));
    entity.getOperations().clear();
    assertEquals(entity, written);
  }

  @Test
  public void withActionsFromAtom() {
    withActions(ODataPubFormat.ATOM);
  }

  @Test
  public void withActionsFromJSON() {
    // this needs to be full, otherwise actions will not be provided
    withActions(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void mediaEntity(final ODataPubFormat format) {
    final InputStream input = getClass().getResourceAsStream("Car_16." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
            getClient().getDeserializer().toEntry(input, format));
    assertNotNull(entity);
    assertTrue(entity.isMediaEntity());
    assertNotNull(entity.getMediaContentSource());
    assertNotNull(entity.getMediaContentType());

    final ODataEntity written = getClient().getBinder().getODataEntity(getClient().getBinder().
            getEntry(entity, ResourceFactory.entryClassForFormat(format == ODataPubFormat.ATOM)));
    assertEquals(entity, written);
  }

  @Test
  public void mediaEntityFromAtom() {
    mediaEntity(ODataPubFormat.ATOM);
  }

  @Test
  public void mediaEntityFromJSON() {
    mediaEntity(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void issue128(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final InputStream input = getClass().getResourceAsStream("AllGeoTypesSet_-5." + getSuffix(format));
    final ODataEntity entity = getClient().getBinder().getODataEntity(
            getClient().getDeserializer().toEntry(input, format));
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
  public void issue128FromAtom() throws EdmPrimitiveTypeException {
    issue128(ODataPubFormat.ATOM);
  }

  @Test
  public void issue128FromJSON() throws EdmPrimitiveTypeException {
    issue128(ODataPubFormat.JSON_FULL_METADATA);
  }
}
