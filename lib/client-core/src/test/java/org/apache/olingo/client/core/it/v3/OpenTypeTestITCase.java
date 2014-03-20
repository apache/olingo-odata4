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
package org.apache.olingo.client.core.it.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.domain.ODataComplexValue;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.domain.ODataJClientEdmPrimitiveType;
import org.apache.olingo.client.api.domain.geospatial.Geospatial;
import org.apache.olingo.client.api.domain.geospatial.GeospatialCollection;
import org.apache.olingo.client.api.domain.geospatial.LineString;
import org.apache.olingo.client.api.domain.geospatial.MultiLineString;
import org.apache.olingo.client.api.domain.geospatial.MultiPoint;
import org.apache.olingo.client.api.domain.geospatial.MultiPolygon;
import org.apache.olingo.client.api.domain.geospatial.Point;
import org.apache.olingo.client.api.domain.geospatial.Polygon;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.junit.Ignore;
import org.junit.Test;

public class OpenTypeTestITCase extends AbstractTestITCase {

  @Test
  public void checkOpenTypeEntityTypesExist() {
    final Edm metadata = client.getRetrieveRequestFactory().
            getMetadataRequest(testStaticServiceRootURL).execute().getBody();

    final EdmSchema schema = metadata.getSchemas().get(0);

    // TODO: https://issues.apache.org/jira/browse/OLINGO-209
//        assertTrue(metadata.getEntityType(new FullQualifiedName(schema.getNamespace(), "Row")).isOpenType());
//        assertTrue(metadata.getEntityType(new FullQualifiedName(schema.getNamespace(), "IndexedRow")).isOpenType());
//        assertTrue(metadata.getEntityType(new FullQualifiedName(schema.getNamespace(), "RowIndex")).isOpenType());
  }

  private ODataEntity readRow(final ODataPubFormat format, final String uuid) {
    final CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Row").appendKeySegment(UUID.fromString(uuid));
    return read(format, builder.build());
  }

  private void read(final ODataPubFormat format) {
    ODataEntity row = readRow(format, "71f7d0dc-ede4-45eb-b421-555a2aa1e58f");
    assertEquals(
            ODataJClientEdmPrimitiveType.Double.toString(),
            row.getProperty("Double").getPrimitiveValue().getTypeName());
    assertEquals(
            ODataJClientEdmPrimitiveType.Guid.toString(),
            row.getProperty("Id").getPrimitiveValue().getTypeName());

    row = readRow(format, "672b8250-1e6e-4785-80cf-b94b572e42b3");
    assertEquals(
            ODataJClientEdmPrimitiveType.Decimal.toString(),
            row.getProperty("Decimal").getPrimitiveValue().getTypeName());
  }

  @Test
  @Ignore
  public void readAsAtom() {
    read(ODataPubFormat.ATOM);
  }

  @Test
  @Ignore
  public void readAsJSON() {
    read(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void cud(final ODataPubFormat format) {
    final UUID guid = UUID.randomUUID();

    ODataEntity row = client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.OpenTypesService.Row");
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Id",
            client.getPrimitiveValueBuilder().setType(ODataJClientEdmPrimitiveType.Guid).setValue(guid).
            build()));
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aString",
            client.getPrimitiveValueBuilder().setType(ODataJClientEdmPrimitiveType.String).setValue("string").
            build()));
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aBoolean",
            client.getPrimitiveValueBuilder().setType(ODataJClientEdmPrimitiveType.Boolean).setValue(true).
            build()));
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aLong",
            client.getPrimitiveValueBuilder().setType(ODataJClientEdmPrimitiveType.Int64).setValue(15L).
            build()));
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aDouble",
            client.getPrimitiveValueBuilder().setType(ODataJClientEdmPrimitiveType.Double).setValue(1.5D).
            build()));
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aByte",
            client.getPrimitiveValueBuilder().setType(ODataJClientEdmPrimitiveType.SByte).setValue(Byte.MAX_VALUE).
            build()));
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aDate",
            client.getPrimitiveValueBuilder().setType(ODataJClientEdmPrimitiveType.DateTime).setValue(new Date()).
            build()));

    final Point point = new Point(Geospatial.Dimension.GEOGRAPHY, null);
    point.setX(1.2);
    point.setY(2.1);
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aPoint",
            client.getGeospatialValueBuilder().setType(ODataJClientEdmPrimitiveType.GeographyPoint).
            setValue(point).build()));
    final List<Point> points = new ArrayList<Point>();
    points.add(point);
    points.add(point);
    final MultiPoint multipoint = new MultiPoint(Geospatial.Dimension.GEOMETRY, null, points);
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aMultiPoint",
            client.getGeospatialValueBuilder().setType(ODataJClientEdmPrimitiveType.GeometryMultiPoint).
            setValue(multipoint).build()));
    final LineString lineString = new LineString(Geospatial.Dimension.GEOMETRY, null, points);
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aLineString",
            client.getGeospatialValueBuilder().setType(ODataJClientEdmPrimitiveType.GeometryLineString).
            setValue(lineString).build()));
    final List<LineString> lineStrings = new ArrayList<LineString>();
    lineStrings.add(lineString);
    lineStrings.add(lineString);
    final MultiLineString multiLineString = new MultiLineString(Geospatial.Dimension.GEOGRAPHY, null, lineStrings);
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aMultiLineString",
            client.getGeospatialValueBuilder().setType(ODataJClientEdmPrimitiveType.GeometryMultiLineString).
            setValue(multiLineString).build()));
    final Point otherPoint = new Point(Geospatial.Dimension.GEOGRAPHY, null);
    otherPoint.setX(3.4);
    otherPoint.setY(4.3);
    points.set(1, otherPoint);
    points.add(otherPoint);
    points.add(point);
    final Polygon polygon = new Polygon(Geospatial.Dimension.GEOGRAPHY, null, points, points);
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aPolygon",
            client.getGeospatialValueBuilder().setType(ODataJClientEdmPrimitiveType.GeographyPolygon).
            setValue(polygon).build()));
    final List<Polygon> polygons = new ArrayList<Polygon>();
    polygons.add(polygon);
    polygons.add(polygon);
    final MultiPolygon multiPolygon = new MultiPolygon(Geospatial.Dimension.GEOGRAPHY, null, polygons);
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aMultiPolygon",
            client.getGeospatialValueBuilder().setType(ODataJClientEdmPrimitiveType.GeographyMultiPolygon).
            setValue(multiPolygon).build()));
    final List<Geospatial> geospatials = new ArrayList<Geospatial>();
    geospatials.add(otherPoint);
    geospatials.add(polygon);
    geospatials.add(multiLineString);
    geospatials.add(multiPolygon);
    final GeospatialCollection geoColl = new GeospatialCollection(Geospatial.Dimension.GEOGRAPHY, null, geospatials);
    row.getProperties().add(client.getObjectFactory().newPrimitiveProperty("aCollection",
            client.getGeospatialValueBuilder().setType(ODataJClientEdmPrimitiveType.GeographyCollection).
            setValue(geoColl).build()));

    final ODataComplexValue contactDetails =
            new ODataComplexValue("Microsoft.Test.OData.Services.OpenTypesService.ContactDetails");
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("FirstContacted",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Binary).setValue("text".getBytes()).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("LastContacted",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.DateTimeOffset).setText("2001-04-05T05:05:05.001+00:01").build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("Contacted",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.DateTime).setText("2001-04-05T05:05:04.001").build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("GUID",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Guid).setValue(UUID.randomUUID()).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("PreferedContactTime",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Time).setText("-P9DT51M10.5063807S").build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("Byte",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Byte).setValue(Integer.valueOf(241)).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("SignedByte",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.SByte).setValue(Byte.MAX_VALUE).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("Double",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Double).setValue(Double.MAX_VALUE).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("Single",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Single).setValue(Float.MAX_VALUE).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("Short",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Int16).setValue(Short.MAX_VALUE).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("Int",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Int32).setValue(Integer.MAX_VALUE).build()));
    contactDetails.add(client.getObjectFactory().newPrimitiveProperty("Long",
            client.getPrimitiveValueBuilder().
            setType(ODataJClientEdmPrimitiveType.Int64).setValue(Long.MAX_VALUE).build()));
    row.getProperties().add(client.getObjectFactory().newComplexProperty("aContact", contactDetails));

    final ODataEntityCreateRequest createReq = client.getCUDRequestFactory().
            getEntityCreateRequest(client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Row").build(), row);
    createReq.setFormat(format);
    final ODataEntityCreateResponse createRes = createReq.execute();
    assertEquals(201, createRes.getStatusCode());

    row = readRow(format, guid.toString());
    assertNotNull(row);
    assertEquals(ODataJClientEdmPrimitiveType.Guid.toString(),
            row.getProperty("Id").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.String.toString(),
            row.getProperty("aString").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.Boolean.toString(),
            row.getProperty("aBoolean").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.Int64.toString(),
            row.getProperty("aLong").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.Double.toString(),
            row.getProperty("aDouble").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.SByte.toString(),
            row.getProperty("aByte").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.DateTime.toString(),
            row.getProperty("aDate").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.GeographyPoint.toString(),
            row.getProperty("aPoint").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.GeometryMultiPoint.toString(),
            row.getProperty("aMultiPoint").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.GeometryLineString.toString(),
            row.getProperty("aLineString").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.GeometryMultiLineString.toString(),
            row.getProperty("aMultiLineString").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.GeographyPolygon.toString(),
            row.getProperty("aPolygon").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.GeographyMultiPolygon.toString(),
            row.getProperty("aMultiPolygon").getPrimitiveValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.GeographyCollection.toString(),
            row.getProperty("aCollection").getPrimitiveValue().getTypeName());
    assertEquals("Microsoft.Test.OData.Services.OpenTypesService.ContactDetails",
            row.getProperty("aContact").getComplexValue().getTypeName());
    assertEquals(ODataJClientEdmPrimitiveType.SByte.toString(),
            row.getProperty("aContact").getComplexValue().get("SignedByte").getPrimitiveValue().getTypeName());

    final ODataDeleteResponse deleteRes = client.getCUDRequestFactory().getDeleteRequest(row.getEditLink()).
            execute();
    assertEquals(204, deleteRes.getStatusCode());
  }

  @Test
  @Ignore
  public void cudAsAtom() {
    cud(ODataPubFormat.ATOM);
  }

  @Test
  @Ignore
  public void cudAsJSON() {
    cud(ODataPubFormat.JSON_FULL_METADATA);
  }
}
