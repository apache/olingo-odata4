/**
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
package com.msopentech.odatajclient.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.msopentech.odatajclient.engine.client.ODataClientFactory;
import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.data.ODataDuration;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataTimestamp;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.GeospatialCollection;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.LineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiLineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPoint;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPolygon;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Polygon;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class PrimitiveValueTest extends AbstractTest {

    @Override
    protected ODataV3Client getClient() {
        return v3Client;
    }

    @Test
    public void manageInt32() {
        final int primitive = -10;
        ODataValue value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Int32).setValue(primitive).
                build();
        assertEquals(EdmSimpleType.Int32.toString(), value.asPrimitive().getTypeName());
        assertEquals(Integer.valueOf(primitive), value.asPrimitive().<Integer>toCastValue());

        value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Int32).setText("9").build();
        assertEquals("9", value.asPrimitive().<Integer>toCastValue().toString());
    }

    @Test
    public void manageString() {
        final String primitive = UUID.randomUUID().toString();
        ODataValue value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.String).setText(primitive).
                build();
        assertEquals(EdmSimpleType.String.toString(), value.asPrimitive().getTypeName());
        assertEquals(primitive, value.toString());

        value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.String).
                setText("1126a28b-a4af-4bbd-bf0a-2b2c22635565").build();
        assertEquals("1126a28b-a4af-4bbd-bf0a-2b2c22635565", value.asPrimitive().<String>toCastValue().toString());
    }

    @Test
    public void manageDecimal() {
        final BigDecimal primitive = new BigDecimal("-79228162514264337593543950335");
        ODataValue value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Decimal).setValue(primitive).
                build();
        assertEquals(EdmSimpleType.Decimal.toString(), value.asPrimitive().getTypeName());
        assertEquals(primitive, value.asPrimitive().<BigDecimal>toCastValue());

        value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Decimal).
                setText("-79228162514264337593543950335").build();
        assertEquals("-79228162514264337593543950335", value.asPrimitive().<BigDecimal>toCastValue().toString());
    }

    @Test
    public void manageDateTime() {
        // OData V3 only
        final String primitive = "2013-01-10T06:27:51.1667673";
        try {
            new ODataPrimitiveValue.Builder(ODataClientFactory.getV4()).
                    setType(EdmSimpleType.DateTime).setText(primitive).build();
            fail();
        } catch (IllegalArgumentException iae) {
            // ignore
        }
        final ODataValue value =
                getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.DateTime).setText(primitive).build();
        assertEquals(EdmSimpleType.DateTime.toString(), value.asPrimitive().getTypeName());
        // performed cast to improve the check
        assertEquals(primitive, value.asPrimitive().<ODataTimestamp>toCastValue().toString());
    }

    @Test
    public void manageTime() {
        // OData V3 only
        final String primitive = "-P9DT51M10.5063807S";
        try {
            new ODataPrimitiveValue.Builder(ODataClientFactory.getV4()).
                    setType(EdmSimpleType.Time).setText(primitive).build();
            fail();
        } catch (IllegalArgumentException iae) {
            // ignore
        }

        final ODataValue value =
                getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Time).setText(primitive).build();
        assertEquals(EdmSimpleType.Time.toString(), value.asPrimitive().getTypeName());
        // performed cast to improve the check
        assertEquals(primitive, value.asPrimitive().<ODataDuration>toCastValue().toString());
    }

    @Test
    public void manageTimeOfDay() {
        // OData V4 only
        final String primitive = "-P9DT51M12.5063807S";
        try {
            getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.TimeOfDay).setText(primitive).build();
            fail();
        } catch (IllegalArgumentException iae) {
            // ignore
        }

        final ODataValue value = new ODataPrimitiveValue.Builder(ODataClientFactory.getV4()).
                setType(EdmSimpleType.TimeOfDay).setText(primitive).build();
        assertEquals(EdmSimpleType.TimeOfDay.toString(), value.asPrimitive().getTypeName());
        // performed cast to improve the check
        assertEquals(primitive, value.asPrimitive().<ODataDuration>toCastValue().toString());
    }

    @Test
    public void manageDate() {
        // OData V4 only
        final String primitive = "2013-01-10";
        try {
            getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Date).setText(primitive).build();
            fail();
        } catch (IllegalArgumentException iae) {
            // ignore
        }

        final ODataValue value = new ODataPrimitiveValue.Builder(ODataClientFactory.getV4()).
                setType(EdmSimpleType.Date).setText(primitive).build();
        assertEquals(EdmSimpleType.Date.toString(), value.asPrimitive().getTypeName());
        // performed cast to improve the check
        assertEquals(primitive, value.asPrimitive().<ODataTimestamp>toCastValue().toString());
    }

    @Test
    public void manageDateTimeOffset() {
        final String primitive = "2013-01-10T02:00:00";
        final ODataValue value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.DateTimeOffset).
                setText(primitive).build();
        assertEquals(EdmSimpleType.DateTimeOffset.toString(), value.asPrimitive().getTypeName());
        // performed cast to improve the check
        assertEquals(primitive, value.asPrimitive().<ODataTimestamp>toCastValue().toString());
    }

    @Test
    public void manageGuid() {
        final UUID primitive = UUID.fromString("1126a28b-a4af-4bbd-bf0a-2b2c22635565");
        ODataValue value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Guid).setValue(primitive).
                build();
        assertEquals(EdmSimpleType.Guid.toString(), value.asPrimitive().getTypeName());
        assertEquals(primitive, value.asPrimitive().<UUID>toCastValue());

        value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Guid).
                setText("1126a28b-a4af-4bbd-bf0a-2b2c22635565").build();
        assertEquals("1126a28b-a4af-4bbd-bf0a-2b2c22635565", value.asPrimitive().<UUID>toCastValue().toString());
    }

    @Test
    public void manageBinary() {
        final byte[] primitive = UUID.randomUUID().toString().getBytes();
        ODataValue value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Binary).setValue(primitive).
                build();
        assertEquals(EdmSimpleType.Binary.toString(), value.asPrimitive().getTypeName());
        assertEquals(
                Base64.encodeBase64String(primitive),
                Base64.encodeBase64String(value.asPrimitive().<byte[]>toCastValue()));

        value = getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.Binary).
                setText(Base64.encodeBase64String("primitive".getBytes())).build();
        assertEquals("primitive", new String(value.asPrimitive().<byte[]>toCastValue()));
    }

    @Test
    public void managePoint() {
        final Point primitive = new Point(Geospatial.Dimension.GEOGRAPHY);
        primitive.setX(52.8606);
        primitive.setY(173.334);

        try {
            getClient().getPrimitiveValueBuilder().setType(EdmSimpleType.GeographyPoint).setValue(primitive).build();
            fail();
        } catch (IllegalArgumentException iae) {
            // nothing top do
        }

        final ODataValue value =
                getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeographyPoint).setValue(primitive).
                build();
        assertEquals(EdmSimpleType.GeographyPoint.toString(), value.asPrimitive().getTypeName());
        assertEquals(Double.valueOf(primitive.getX()), Double.valueOf(value.asPrimitive().<Point>toCastValue().getX()));
        assertEquals(Double.valueOf(primitive.getY()), Double.valueOf(value.asPrimitive().<Point>toCastValue().getY()));
    }

    @Test
    public void manageLineString() {
        final List<Point> points = new ArrayList<Point>();
        Point point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(40.5);
        point.setY(40.5);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(30.5);
        point.setY(30.5);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(20.5);
        point.setY(40.5);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(10.5);
        point.setY(30.5);
        points.add(point);

        final LineString primitive = new LineString(Geospatial.Dimension.GEOGRAPHY, points);

        final ODataValue value = getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeographyLineString).
                setValue(primitive).build();
        assertEquals(EdmSimpleType.GeographyLineString.toString(), value.asPrimitive().getTypeName());

        final Iterator<Point> iter = value.asPrimitive().<LineString>toCastValue().iterator();

        // take the third one and check the point value ...
        iter.next();
        iter.next();
        point = iter.next();

        assertEquals(Double.valueOf(points.get(2).getX()), Double.valueOf(point.getX()));
        assertEquals(Double.valueOf(points.get(2).getY()), Double.valueOf(point.getY()));
    }

    @Test
    public void manageMultiPoint() {
        final List<Point> points = new ArrayList<Point>();
        Point point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(0);
        point.setY(0);
        points.add(point);

        final MultiPoint primitive = new MultiPoint(Geospatial.Dimension.GEOMETRY, points);

        final ODataValue value = getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeometryMultiPoint).
                setValue(primitive).build();
        assertEquals(EdmSimpleType.GeometryMultiPoint.toString(), value.asPrimitive().getTypeName());

        final Iterator<Point> iter = value.asPrimitive().<MultiPoint>toCastValue().iterator();
        point = iter.next();

        assertEquals(Double.valueOf(points.get(0).getX()), Double.valueOf(point.getX()));
        assertEquals(Double.valueOf(points.get(0).getY()), Double.valueOf(point.getY()));
    }

    @Test
    public void manageMultiLine() {
        final List<LineString> lines = new ArrayList<LineString>();

        // line one ...
        List<Point> points = new ArrayList<Point>();
        Point point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(10);
        point.setY(10);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(20);
        point.setY(20);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(10);
        point.setY(40);
        points.add(point);

        lines.add(new LineString(Geospatial.Dimension.GEOMETRY, points));

        // line two ...
        points = new ArrayList<Point>();

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(40);
        point.setY(40);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(30);
        point.setY(30);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(40);
        point.setY(20);
        points.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(30);
        point.setY(10);
        points.add(point);
        lines.add(new LineString(Geospatial.Dimension.GEOMETRY, points));

        final MultiLineString primitive = new MultiLineString(Geospatial.Dimension.GEOMETRY, lines);

        final ODataValue value =
                getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeometryMultiLineString).
                setValue(primitive).build();
        assertEquals(EdmSimpleType.GeometryMultiLineString.toString(), value.asPrimitive().getTypeName());

        final Iterator<LineString> lineIter = value.asPrimitive().<MultiLineString>toCastValue().iterator();

        // take the second line and check the third point value ...
        lineIter.next();
        final LineString line = lineIter.next();

        final Iterator<Point> pointIter = line.iterator();
        pointIter.next();
        pointIter.next();
        point = pointIter.next();

        assertEquals(Double.valueOf(points.get(2).getX()), Double.valueOf(point.getX()));
        assertEquals(Double.valueOf(points.get(2).getY()), Double.valueOf(point.getY()));
    }

    @Test
    public void managePolygon() {

        final List<Point> interior = new ArrayList<Point>();
        final List<Point> exterior = new ArrayList<Point>();

        Point point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(5);
        point.setY(15);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(10);
        point.setY(40);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(20);
        point.setY(10);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(10);
        point.setY(5);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(5);
        point.setY(15);
        exterior.add(point);

        final Polygon primitive = new Polygon(Geospatial.Dimension.GEOGRAPHY, interior, exterior);

        final ODataValue value =
                getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeographyPolygon).
                setValue(primitive).build();
        assertEquals(EdmSimpleType.GeographyPolygon.toString(), value.asPrimitive().getTypeName());

        assertTrue(value.asPrimitive().<Polygon>toCastValue().getInterior().isEmpty());
        final Iterator<Point> iter = value.asPrimitive().<Polygon>toCastValue().getExterior().iterator();

        // take the third one ...
        iter.next();
        iter.next();
        point = iter.next();

        assertEquals(Double.valueOf(exterior.get(2).getX()), Double.valueOf(point.getX()));
        assertEquals(Double.valueOf(exterior.get(2).getY()), Double.valueOf(point.getY()));
    }

    @Test
    public void manageMultiPolygon() {
        final List<Polygon> polygons = new ArrayList<Polygon>();

        List<Point> interior = new ArrayList<Point>();
        List<Point> exterior = new ArrayList<Point>();

        // exterior one ...
        Point point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(40);
        point.setY(40);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(20);
        point.setY(45);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(45);
        point.setY(30);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(40);
        point.setY(40);
        exterior.add(point);

        polygons.add(new Polygon(Geospatial.Dimension.GEOMETRY, interior, exterior));

        // interior two ...
        interior = new ArrayList<Point>();
        exterior = new ArrayList<Point>();

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(30);
        point.setY(20);
        interior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(20);
        point.setY(25);
        interior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(20);
        point.setY(15);
        interior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(30);
        point.setY(20);
        interior.add(point);

        // exterior two ...
        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(20);
        point.setY(35);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(45);
        point.setY(20);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(30);
        point.setY(5);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(10);
        point.setY(10);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(10);
        point.setY(30);
        exterior.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(20);
        point.setY(35);
        exterior.add(point);

        polygons.add(new Polygon(Geospatial.Dimension.GEOMETRY, interior, exterior));

        final MultiPolygon primitive = new MultiPolygon(Geospatial.Dimension.GEOMETRY, polygons);

        final ODataValue value =
                getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeometryMultiPolygon).
                setValue(primitive).build();
        assertEquals(EdmSimpleType.GeometryMultiPolygon.toString(), value.asPrimitive().getTypeName());

        final Iterator<Polygon> iter = value.asPrimitive().<MultiPolygon>toCastValue().iterator();

        // second one polygon
        iter.next();
        final Polygon polygon = iter.next();
        Iterator<Point> pointIter = polygon.getInterior().iterator();
        pointIter.next();
        point = pointIter.next();

        // check the second point from interior
        assertEquals(Double.valueOf(interior.get(1).getX()), Double.valueOf(point.getX()));
        assertEquals(Double.valueOf(interior.get(1).getY()), Double.valueOf(point.getY()));

        pointIter = polygon.getExterior().iterator();
        pointIter.next();
        pointIter.next();
        point = pointIter.next();

        // check the third point from exterior
        assertEquals(Double.valueOf(exterior.get(2).getX()), Double.valueOf(point.getX()));
        assertEquals(Double.valueOf(exterior.get(2).getY()), Double.valueOf(point.getY()));
    }

    @Test
    public void manageGeomCollection() {
        final List<Geospatial> collection = new ArrayList<Geospatial>();

        Point point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(1);
        point.setY(2);
        point.setZ(3);
        collection.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(4);
        point.setY(5);
        point.setZ(6);
        collection.add(point);

        final GeospatialCollection primitive = new GeospatialCollection(Geospatial.Dimension.GEOMETRY, collection);

        final ODataValue value =
                getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeometryCollection).
                setValue(primitive).build();
        assertEquals(EdmSimpleType.GeometryCollection.toString(), value.asPrimitive().getTypeName());

        final Iterator<Geospatial> iter = value.asPrimitive().<GeospatialCollection>toCastValue().iterator();
        iter.next();
        final Point collectedPoint = (Point) iter.next();

        assertTrue(point.getX() == collectedPoint.getX()
                && point.getY() == collectedPoint.getY()
                && point.getZ() == collectedPoint.getZ());
    }

    @Test
    public void manageGeogCollection() {
        final List<Geospatial> collection = new ArrayList<Geospatial>();

        Point point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(1);
        point.setY(2);
        point.setZ(3);
        collection.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(4);
        point.setY(5);
        point.setZ(6);
        collection.add(point);

        final GeospatialCollection primitive = new GeospatialCollection(Geospatial.Dimension.GEOGRAPHY, collection);

        final ODataValue value =
                getClient().getGeospatialValueBuilder().setType(EdmSimpleType.GeographyCollection).
                setValue(primitive).build();
        assertEquals(EdmSimpleType.GeographyCollection.toString(), value.asPrimitive().getTypeName());

        final Iterator<Geospatial> iter = value.asPrimitive().<GeospatialCollection>toCastValue().iterator();
        iter.next();
        final Point collectedPoint = (Point) iter.next();

        assertTrue(point.getX() == collectedPoint.getX()
                && point.getY() == collectedPoint.getY()
                && point.getZ() == collectedPoint.getZ());
    }
}
