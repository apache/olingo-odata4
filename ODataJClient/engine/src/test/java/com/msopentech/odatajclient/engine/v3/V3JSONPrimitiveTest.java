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
package com.msopentech.odatajclient.engine;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial.Dimension;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class V3JSONPrimitiveTest extends AbstractPrimitiveTest {

    @Override
    protected ODataFormat getFormat() {
        return ODataFormat.JSON;
    }

    @Override
    protected ODataClient getClient() {
        return v3Client;
    }

    @Test
    public void readInt32() {
        int32("Customer(-10)", "CustomerId", -10);
    }

    @Test
    public void readString() {
        string("Product(-9)", "Description", "kdcuklu");
    }

    @Test
    public void readDecimal() {
        decimal("Product(-10)", "Dimensions/Width", new BigDecimal("-79228162514264337593543950335"));
    }

    @Test
    public void readDatetime() {
        datetime("Product(-10)", "ComplexConcurrency/QueriedDateTime", "2013-01-10T06:27:51.1667673");
    }

    @Test
    public void readGuid() {
        guid("MessageAttachment(guid'1126a28b-a4af-4bbd-bf0a-2b2c22635565')", "AttachmentId",
                "1126a28b-a4af-4bbd-bf0a-2b2c22635565");
    }

    @Test
    public void readBinary() {
        binary("MessageAttachment(guid'1126a28b-a4af-4bbd-bf0a-2b2c22635565')", "Attachment");
    }

    @Test
    public void readPoint() {
        final Point expectedValues = new Point(Geospatial.Dimension.GEOGRAPHY);
        expectedValues.setX(52.8606);
        expectedValues.setY(173.334);

        point("AllGeoTypesSet(-10)", "GeogPoint", expectedValues, EdmSimpleType.GeographyPoint, Dimension.GEOGRAPHY);
    }

    @Test
    public void readLineString() {
        final List<Point> expectedValues = new ArrayList<Point>();
        Point point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(40.5);
        point.setY(40.5);
        expectedValues.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(30.5);
        point.setY(30.5);
        expectedValues.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(20.5);
        point.setY(40.5);
        expectedValues.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(10.5);
        point.setY(30.5);
        expectedValues.add(point);

        lineString(
                "AllGeoTypesSet(-10)",
                "GeogLine",
                expectedValues,
                EdmSimpleType.GeographyLineString,
                Dimension.GEOGRAPHY);
    }

    @Test
    public void readMultiPoint() {
        final List<Point> expectedValues = new ArrayList<Point>();
        Point point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(0);
        point.setY(0);
        expectedValues.add(point);

        multiPoint(
                "AllGeoTypesSet(-7)",
                "GeomMultiPoint",
                expectedValues,
                EdmSimpleType.GeometryMultiPoint,
                Dimension.GEOMETRY);
    }

    @Test
    public void readMultiLine() {
        final List<List<Point>> expectedValues = new ArrayList<List<Point>>();

        // line one ...
        List<Point> line = new ArrayList<Point>();
        expectedValues.add(line);

        Point point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(10);
        point.setY(10);
        line.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(20);
        point.setY(20);
        line.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(10);
        point.setY(40);
        line.add(point);

        // line two ...
        line = new ArrayList<Point>();
        expectedValues.add(line);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(40);
        point.setY(40);
        line.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(30);
        point.setY(30);
        line.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(40);
        point.setY(20);
        line.add(point);

        point = new Point(Geospatial.Dimension.GEOMETRY);
        point.setX(30);
        point.setY(10);
        line.add(point);

        multiLine(
                "AllGeoTypesSet(-6)",
                "GeomMultiLine",
                expectedValues,
                EdmSimpleType.GeometryMultiLineString,
                Dimension.GEOMETRY);
    }

    @Test
    public void readPolygon() {
        final List<Point> expectedInteriorValues = new ArrayList<Point>();
        final List<Point> expectedExteriorValues = new ArrayList<Point>();

        Point point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(5);
        point.setY(15);
        expectedExteriorValues.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(10);
        point.setY(40);
        expectedExteriorValues.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(20);
        point.setY(10);
        expectedExteriorValues.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(10);
        point.setY(5);
        expectedExteriorValues.add(point);

        point = new Point(Geospatial.Dimension.GEOGRAPHY);
        point.setX(5);
        point.setY(15);
        expectedExteriorValues.add(point);

        polygon(
                "AllGeoTypesSet(-5)",
                "GeogPolygon",
                expectedInteriorValues,
                expectedExteriorValues,
                EdmSimpleType.GeographyPolygon,
                Dimension.GEOGRAPHY);
    }

    @Test
    public void readMultiPolygon() {
        final List<List<Point>> expectedInteriorValues = new ArrayList<List<Point>>();
        final List<List<Point>> expectedExteriorValues = new ArrayList<List<Point>>();

        // interior one ...
        expectedInteriorValues.add(Collections.<Point>emptyList());

        // exterior one ...
        List<Point> exterior = new ArrayList<Point>();
        expectedExteriorValues.add(exterior);

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

        // interior two ...
        List<Point> interior = new ArrayList<Point>();
        expectedInteriorValues.add(interior);

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
        exterior = new ArrayList<Point>();
        expectedExteriorValues.add(exterior);

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

        multiPolygon(
                "AllGeoTypesSet(-3)",
                "GeomMultiPolygon",
                expectedInteriorValues,
                expectedExteriorValues,
                EdmSimpleType.GeometryMultiPolygon,
                Dimension.GEOMETRY);
    }

    @Test
    public void readGeomCollection() {
        geomCollection("AllGeoTypesSet(-8)", "GeomCollection", EdmSimpleType.GeometryCollection, Dimension.GEOMETRY);
    }

    @Test
    public void readGeogCollection() {
        geogCollection("AllGeoTypesSet(-5)", "GeogCollection", EdmSimpleType.GeographyCollection, Dimension.GEOGRAPHY);
    }
}
