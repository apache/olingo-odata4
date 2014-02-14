/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.proxy;

import static com.msopentech.odatajclient.proxy.AbstractTest.testOpenTypeServiceRootURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.data.ODataDuration;
import com.msopentech.odatajclient.engine.data.ODataTimestamp;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.GeospatialCollection;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.LineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiLineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPoint;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPolygon;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Polygon;
import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import com.msopentech.odatajclient.proxy.api.annotations.EntityType;
import com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.DefaultContainer;
import com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.types.ContactDetails;
import com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.types.IndexedRow;
import com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.types.Row;
import com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.types.RowIndex;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

/**
 * This is the unit test class to check basic feed operations.
 */
public class OpenTypeTestITCase extends AbstractTest {

    @Test
    public void checkOpenTypeEntityTypesExist() {
        assertTrue(Row.class.getAnnotation(EntityType.class).openType());
        assertTrue(IndexedRow.class.getAnnotation(EntityType.class).openType());
        assertTrue(RowIndex.class.getAnnotation(EntityType.class).openType());
    }

    @Test
    public void readRow() {
        final DefaultContainer otcontainer = EntityContainerFactory.getV3Instance(testOpenTypeServiceRootURL).
                getEntityContainer(DefaultContainer.class);

        Row row = otcontainer.getRow().get(UUID.fromString("71f7d0dc-ede4-45eb-b421-555a2aa1e58f"));
        Object value = row.getAdditionalProperty("Double");
        assertEquals(1.2626D, ((Double) value).doubleValue(), 1.0E-100D);
    }

    @Test
    public void cud() {
        final DefaultContainer otcontainer = EntityContainerFactory.getV3Instance(testOpenTypeServiceRootURL).
                getEntityContainer(DefaultContainer.class);

        Row row = otcontainer.getRow().newRow();

        final UUID guid = UUID.randomUUID();
        row.setId(guid);

        row.addAdditionalProperty("aString", "string");
        row.addAdditionalProperty("aBoolean", true);
        row.addAdditionalProperty("aLong", 15L);
        row.addAdditionalProperty("aDouble", 1.5D);
        row.addAdditionalProperty("aByte", Byte.MAX_VALUE);
        row.addAdditionalProperty("aDate",
                ODataTimestamp.getInstance(EdmSimpleType.DateTime, new Timestamp(new Date().getTime())));

        final Point point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(1.2);
        point.setY(2.1);
        row.addAdditionalProperty("aPoint", point);

        final List<Point> points = new ArrayList<Point>();
        points.add(point);
        points.add(point);
        final MultiPoint multipoint = new MultiPoint(Geospatial.Dimension.GEOMETRY, points);
        row.addAdditionalProperty("aMultiPoint", multipoint);

        final LineString lineString = new LineString(Geospatial.Dimension.GEOMETRY, points);
        row.addAdditionalProperty("aLineString", lineString);

        final List<LineString> lineStrings = new ArrayList<LineString>();
        lineStrings.add(lineString);
        lineStrings.add(lineString);
        final MultiLineString multiLineString = new MultiLineString(Geospatial.Dimension.GEOGRAPHY, lineStrings);
        row.addAdditionalProperty("aMultiLineString", multiLineString);

        final Point otherPoint = new Point(Geospatial.Dimension.GEOGRAPHY);
        otherPoint.setX(3.4);
        otherPoint.setY(4.3);
        points.set(1, otherPoint);
        points.add(otherPoint);
        points.add(point);
        final Polygon polygon = new Polygon(Geospatial.Dimension.GEOGRAPHY, points, points);
        row.addAdditionalProperty("aPolygon", polygon);

        final List<Polygon> polygons = new ArrayList<Polygon>();
        polygons.add(polygon);
        polygons.add(polygon);
        final MultiPolygon multiPolygon = new MultiPolygon(Geospatial.Dimension.GEOGRAPHY, polygons);
        row.addAdditionalProperty("aMultiPolygon", multiPolygon);

        final List<Geospatial> geospatials = new ArrayList<Geospatial>();
        geospatials.add(otherPoint);
        geospatials.add(polygon);
        geospatials.add(multiLineString);
        geospatials.add(multiPolygon);
        final GeospatialCollection geoColl = new GeospatialCollection(Geospatial.Dimension.GEOGRAPHY, geospatials);
        row.addAdditionalProperty("aCollection", geoColl);

        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setFirstContacted("text".getBytes());
        contactDetails.setLastContacted(
                ODataTimestamp.parse(EdmSimpleType.DateTimeOffset, "2001-04-05T05:05:05.001+00:01"));
        contactDetails.setContacted(
                ODataTimestamp.parse(EdmSimpleType.DateTime, "2001-04-05T05:05:04.001"));
        contactDetails.setGUID(UUID.randomUUID());
        contactDetails.setPreferedContactTime(new ODataDuration("-P9DT51M10.5063807S"));
        contactDetails.setByte(241);
        contactDetails.setSignedByte(Byte.MAX_VALUE);
        contactDetails.setDouble(Double.MAX_VALUE);
        contactDetails.setSingle(Float.MAX_VALUE);
        contactDetails.setShort(Short.MAX_VALUE);
        contactDetails.setInt(Integer.MAX_VALUE);
        contactDetails.setLong(Long.MAX_VALUE);
        row.addAdditionalProperty("aContact", contactDetails);

        otcontainer.flush();

        row = otcontainer.getRow().get(guid);
        assertNotNull(row);

        assertEquals(1.5D, ((Double) row.getAdditionalProperty("aDouble")).doubleValue(), 0.1E-100);
        assertEquals(1.2D, ((Point) row.getAdditionalProperty("aPoint")).getX(), 0.1E-100);
        assertEquals(2.1D, ((Point) row.getAdditionalProperty("aPoint")).getY(), 0.1E-100);

        GeospatialCollection aCollection = (GeospatialCollection) row.getAdditionalProperty("aCollection");
        assertEquals(Geospatial.Dimension.GEOGRAPHY, aCollection.getDimension());
        int count = 0;
        for (Geospatial geospatial : aCollection) {
            count++;
        }
        assertEquals(4, count);

        ContactDetails aContact = (ContactDetails) row.getAdditionalProperty("aContact");
        assertEquals("text", new String(aContact.getFirstContacted()));
        assertEquals(Short.MAX_VALUE, aContact.getShort().shortValue());
        assertEquals(241, aContact.getByte().intValue());
        assertEquals(ODataTimestamp.parse(EdmSimpleType.DateTime, "2001-04-05T05:05:04.001").toString(),
                aContact.getContacted().toString());

        otcontainer.getRow().delete(guid);
        otcontainer.flush();

        assertNull(otcontainer.getRow().get(guid));
    }
}
