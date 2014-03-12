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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.*;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.GeospatialCollection;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.LineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiLineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPoint;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPolygon;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Polygon;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import org.junit.Ignore;

public class GeoSpatialTestITCase extends AbstractTestITCase {
    // test with json full metadata

    @Test
    public void withJSON() {
        ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        String contentType = "application/json;odata=fullmetadata";
        String prefer = "return-content";
        int id = 11155;
        geoSpacialTest(format, contentType, prefer, id);
    }
    // test with atom

    @Test
    public void withATOM() {
        ODataPubFormat format = ODataPubFormat.ATOM;
        String contentType = "application/atom+xml";
        String prefer = "return-content";
        int id = 12091;
        geoSpacialTest(format, contentType, prefer, id);
    }
    // test with no metadata

    @Test
    public void withJSONMinimalMetadata() {
        ODataPubFormat format = ODataPubFormat.JSON;
        String contentType = "application/json";
        String prefer = "return-content";
        int id = 111166;
        geoSpacialTest(format, contentType, prefer, id);
    }
    // test with json and atom as accept and content-type header respectively

    @Test
    @Ignore
    public void withJSONAndATOMReturn() {
        ODataPubFormat format = ODataPubFormat.ATOM;
        String contentType = "application/json;odata=fullmetadata";
        String prefer = "return-content";
        int id = 124;
        geoSpacialTest(format, contentType, prefer, id);
    }
    // test with atom and json as content Type and accept header respectively

    @Test
    @Ignore
    public void withATOMAndJSONReturn() {
        ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        String contentType = "application/atom+xml";
        String prefer = "return-content";
        int id = 11135;
        geoSpacialTest(format, contentType, prefer, id);
    }
    // geo spacial entity create test

    public void geoSpacialTest(
            final ODataPubFormat format, final String contentType, final String prefer, final int id) {
        try {
            final ODataEntity entity =client.getObjectFactory().newEntity(
                    "Microsoft.Test.OData.Services.AstoriaDefaultService.AllSpatialTypes");
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Id",
                    client.getPrimitiveValueBuilder().setText(String.valueOf(id)).setType(EdmSimpleType.Int32).
                    build()));

            final Point point1 = new Point(Geospatial.Dimension.GEOGRAPHY);
            point1.setX(6.2);
            point1.setY(1.1);
            final Point point2 = new Point(Geospatial.Dimension.GEOGRAPHY);
            point2.setX(33.33);
            point2.setY(-2.5);

            // create a point
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogPoint",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyPoint).
                    setValue(point1).build()));

            // create  multiple point
            final List<Point> points = new ArrayList<Point>();
            points.add(point1);
            points.add(point2);
            final MultiPoint multipoint = new MultiPoint(Geospatial.Dimension.GEOGRAPHY, points);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogMultiPoint",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyMultiPoint).
                    setValue(multipoint).build()));

            // create a line
            final List<Point> linePoints = new ArrayList<Point>();
            linePoints.add(point1);
            linePoints.add(point2);
            final LineString lineString = new LineString(Geospatial.Dimension.GEOGRAPHY, linePoints);

            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogLine",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyLineString).
                    setValue(lineString).build()));

            // create a polygon
            linePoints.set(1, point2);
            linePoints.add(point2);
            linePoints.add(point1);
            final Polygon polygon = new Polygon(Geospatial.Dimension.GEOGRAPHY, linePoints, linePoints);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogPolygon",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyPolygon).
                    setValue(polygon).build()));

            // create a multi line string
            final List<LineString> multipleLines = new ArrayList<LineString>();
            multipleLines.add(lineString);
            multipleLines.add(lineString);
            final MultiLineString multiLine = new MultiLineString(Geospatial.Dimension.GEOGRAPHY, multipleLines);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogMultiLine",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyMultiLineString).
                    setValue(multiLine).build()));

            // create a multi polygon        
            final List<Polygon> polygons = new ArrayList<Polygon>();
            polygons.add(polygon);
            polygons.add(polygon);
            final MultiPolygon multiPolygon = new MultiPolygon(Geospatial.Dimension.GEOGRAPHY, polygons);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogMultiPolygon",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyMultiPolygon).
                    setValue(multiPolygon).build()));

            // create  acolletion of various shapes
            final List<Geospatial> geospatialCollection = new ArrayList<Geospatial>();
            geospatialCollection.add(point1);
            geospatialCollection.add(lineString);
            geospatialCollection.add(polygon);
            final GeospatialCollection collection = new GeospatialCollection(Geospatial.Dimension.GEOGRAPHY,
                    geospatialCollection);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogCollection",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyCollection).
                    setValue(collection).build()));

            // with geometry test
            final Point goemPoint1 = new Point(Geospatial.Dimension.GEOMETRY);
            goemPoint1.setX(6.2);
            goemPoint1.setY(1.1);
            final Point goemPoint2 = new Point(Geospatial.Dimension.GEOMETRY);
            goemPoint2.setX(33.33);
            goemPoint2.setY(-2.5);

            // create a point
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeomPoint",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeometryPoint).
                    setValue(goemPoint2).build()));

            // create  multiple point
            final List<Point> geomPoints = new ArrayList<Point>();
            geomPoints.add(point1);
            geomPoints.add(point2);
            final MultiPoint geomMultipoint = new MultiPoint(Geospatial.Dimension.GEOMETRY, geomPoints);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeomMultiPoint",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeometryMultiPoint).
                    setValue(geomMultipoint).build()));

            // create a line
            final List<Point> geomLinePoints = new ArrayList<Point>();
            geomLinePoints.add(goemPoint1);
            geomLinePoints.add(goemPoint2);
            final LineString geomLineString = new LineString(Geospatial.Dimension.GEOMETRY, geomLinePoints);

            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeomLine",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeometryLineString).
                    setValue(geomLineString).build()));

            // create a polygon
            geomLinePoints.set(1, goemPoint2);
            geomLinePoints.add(goemPoint2);
            geomLinePoints.add(goemPoint1);
            final Polygon geomPolygon = new Polygon(Geospatial.Dimension.GEOMETRY, geomLinePoints, geomLinePoints);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeomPolygon",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeometryPolygon).
                    setValue(geomPolygon).build()));

            // create a multi line string
            final List<LineString> geomMultipleLines = new ArrayList<LineString>();
            geomMultipleLines.add(geomLineString);
            geomMultipleLines.add(geomLineString);
            final MultiLineString geomMultiLine = new MultiLineString(Geospatial.Dimension.GEOMETRY, geomMultipleLines);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeomMultiLine",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeometryMultiLineString).
                    setValue(geomMultiLine).build()));

            // create a multi polygon        
            final List<Polygon> geomPolygons = new ArrayList<Polygon>();
            geomPolygons.add(geomPolygon);
            geomPolygons.add(geomPolygon);
            final MultiPolygon geomMultiPolygon = new MultiPolygon(Geospatial.Dimension.GEOMETRY, geomPolygons);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeomMultiPolygon",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyMultiPolygon).
                    setValue(geomMultiPolygon).build()));

            // create  a collection of various shapes
            final List<Geospatial> geomspatialCollection = new ArrayList<Geospatial>();
            geomspatialCollection.add(goemPoint1);
            geomspatialCollection.add(geomLineString);
            final GeospatialCollection geomCollection = new GeospatialCollection(Geospatial.Dimension.GEOMETRY,
                    geomspatialCollection);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("GeomCollection",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeometryCollection).
                    setValue(geomCollection).build()));

            // create request
            final ODataEntityCreateRequest createReq = client.getCUDRequestFactory().
                    getEntityCreateRequest(client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment("AllGeoTypesSet").build(), entity);
            createReq.setFormat(format);
            createReq.setContentType(contentType);
            createReq.setPrefer(prefer);
            final ODataEntityCreateResponse createRes = createReq.execute();
            final ODataEntity entityAfterCreate = createRes.getBody();
            final ODataProperty geogCollection = entityAfterCreate.getProperty("GeogCollection");
            if (format.equals(ODataPubFormat.JSON) || format.equals(ODataPubFormat.JSON_NO_METADATA)) {
                assertTrue(geogCollection.hasComplexValue());
            } else {
                assertEquals(EdmSimpleType.GeographyCollection.toString(), geogCollection.getPrimitiveValue().
                        getTypeName());

                final ODataProperty geometryCollection = entityAfterCreate.getProperty("GeomCollection");
                assertEquals(EdmSimpleType.GeographyCollection.toString(),
                        geogCollection.getPrimitiveValue().getTypeName());

                int count = 0;
                for (Geospatial g : geogCollection.getPrimitiveValue().<GeospatialCollection>toCastValue()) {
                    assertNotNull(g);
                    count++;
                }
                assertEquals(3, count);
                count = 0;
                for (Geospatial g : geometryCollection.getPrimitiveValue().<GeospatialCollection>toCastValue()) {
                    assertNotNull(g);
                    count++;
                }
                assertEquals(2, count);
            }
            // update geog points 
            final Point updatePoint1 = new Point(Geospatial.Dimension.GEOGRAPHY);
            updatePoint1.setX(21.2);
            updatePoint1.setY(31.1);
            final Point updatePoint2 = new Point(Geospatial.Dimension.GEOGRAPHY);
            updatePoint2.setX(99.99);
            updatePoint2.setY(-3.24);
            ODataProperty property = entityAfterCreate.getProperty("GeogPoint");
            entityAfterCreate.removeProperty(property);
            entityAfterCreate.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogPoint",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyPoint).
                    setValue(updatePoint1).build()));
            updateGeog(format, contentType, prefer, entityAfterCreate, UpdateType.REPLACE, entityAfterCreate.getETag());

            // update geography line  
            final List<Point> updateLinePoints = new ArrayList<Point>();
            updateLinePoints.add(updatePoint1);
            updateLinePoints.add(updatePoint2);
            final LineString updateLineString = new LineString(Geospatial.Dimension.GEOGRAPHY, updateLinePoints);
            ODataProperty lineProperty = entityAfterCreate.getProperty("GeogLine");
            entityAfterCreate.removeProperty(lineProperty);
            entityAfterCreate.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogLine",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyLineString).
                    setValue(updateLineString).build()));
            //updateGeog(format,contentType, prefer, entityAfterCreate, UpdateType.REPLACE,entityAfterCreate.getETag());

            // update a geography polygon
            updateLinePoints.set(1, updatePoint2);
            updateLinePoints.add(updatePoint2);
            updateLinePoints.add(updatePoint1);
            final Polygon updatePolygon =
                    new Polygon(Geospatial.Dimension.GEOGRAPHY, updateLinePoints, updateLinePoints);
            ODataProperty polygonProperty = entityAfterCreate.getProperty("GeogPolygon");
            entityAfterCreate.removeProperty(polygonProperty);
            entityAfterCreate.addProperty(client.getObjectFactory().newPrimitiveProperty("GeogPolygon",
                    client.getGeospatialValueBuilder().setType(EdmSimpleType.GeographyPolygon).
                    setValue(updatePolygon).build()));
            //updateGeog(format,contentType, prefer, entityAfterCreate, UpdateType.REPLACE,entityAfterCreate.getETag());

            // delete the entity
            URIBuilder deleteUriBuilder = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment("AllGeoTypesSet(" + id + ")");
            ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(deleteUriBuilder.build());
            deleteReq.setFormat(format);
            deleteReq.setContentType(contentType);
            assertEquals(204, deleteReq.execute().getStatusCode());
        } catch (Exception e) {
            LOG.error("", e);
            if (!format.equals(ODataPubFormat.JSON) && !format.equals(ODataPubFormat.JSON_NO_METADATA)) {
                fail(e.getMessage());
            }
        } catch (AssertionError e) {
            LOG.error("", e);
            fail(e.getMessage());
        }
    }
    // update operation of geo spacial properties

    private void updateGeog(final ODataPubFormat format, final String contentType,
            final String prefer, final ODataEntity entityAfterCreate, final UpdateType type,
            final String tag) {
        final ODataEntityUpdateRequest req = client.getCUDRequestFactory().getEntityUpdateRequest(type,
                entityAfterCreate);
        if (client.getConfiguration().isUseXHTTPMethod()) {
            assertEquals(HttpMethod.POST, req.getMethod());
        } else {
            assertEquals(type.getMethod(), req.getMethod());
        }
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);
        if (StringUtils.isNotEmpty(tag)) {
            req.setIfMatch(tag);
        }
        final ODataEntityUpdateResponse res = req.execute();

        if (prefer.equals("return-content")) {
            assertEquals(200, res.getStatusCode());
            ODataEntity entityAfterUpdate = res.getBody();
            assertNotNull(entityAfterUpdate);
        } else {
            assertEquals(204, res.getStatusCode());
        }
    }
}
