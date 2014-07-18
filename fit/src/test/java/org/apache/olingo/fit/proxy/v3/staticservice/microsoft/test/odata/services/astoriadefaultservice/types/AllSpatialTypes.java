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

package org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types;
//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
//CHECKSTYLE:ON (Maven checkstyle)


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "AllSpatialTypes",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface AllSpatialTypes 
  extends org.apache.olingo.ext.proxy.api.EntityType,org.apache.olingo.ext.proxy.api.Annotatable,org.apache.olingo.ext.proxy.api.SingleQuery<AllSpatialTypes> {


    
    @Key
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Id", 
                type = "Edm.Int32", 
                nullable = false,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    java.lang.Integer getId();

    void setId(java.lang.Integer _id);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Geog", 
                type = "Edm.Geography", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.Geospatial getGeog();

    void setGeog(org.apache.olingo.commons.api.edm.geo.Geospatial _geog);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeogPoint", 
                type = "Edm.GeographyPoint", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.Point getGeogPoint();

    void setGeogPoint(org.apache.olingo.commons.api.edm.geo.Point _geogPoint);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeogLine", 
                type = "Edm.GeographyLineString", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.LineString getGeogLine();

    void setGeogLine(org.apache.olingo.commons.api.edm.geo.LineString _geogLine);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeogPolygon", 
                type = "Edm.GeographyPolygon", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.Polygon getGeogPolygon();

    void setGeogPolygon(org.apache.olingo.commons.api.edm.geo.Polygon _geogPolygon);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeogCollection", 
                type = "Edm.GeographyCollection", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.GeospatialCollection getGeogCollection();

    void setGeogCollection(org.apache.olingo.commons.api.edm.geo.GeospatialCollection _geogCollection);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeogMultiPoint", 
                type = "Edm.GeographyMultiPoint", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.MultiPoint getGeogMultiPoint();

    void setGeogMultiPoint(org.apache.olingo.commons.api.edm.geo.MultiPoint _geogMultiPoint);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeogMultiLine", 
                type = "Edm.GeographyMultiLineString", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.MultiLineString getGeogMultiLine();

    void setGeogMultiLine(org.apache.olingo.commons.api.edm.geo.MultiLineString _geogMultiLine);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeogMultiPolygon", 
                type = "Edm.GeographyMultiPolygon", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.MultiPolygon getGeogMultiPolygon();

    void setGeogMultiPolygon(org.apache.olingo.commons.api.edm.geo.MultiPolygon _geogMultiPolygon);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Geom", 
                type = "Edm.Geometry", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.Geospatial getGeom();

    void setGeom(org.apache.olingo.commons.api.edm.geo.Geospatial _geom);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeomPoint", 
                type = "Edm.GeometryPoint", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.Point getGeomPoint();

    void setGeomPoint(org.apache.olingo.commons.api.edm.geo.Point _geomPoint);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeomLine", 
                type = "Edm.GeometryLineString", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.LineString getGeomLine();

    void setGeomLine(org.apache.olingo.commons.api.edm.geo.LineString _geomLine);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeomPolygon", 
                type = "Edm.GeometryPolygon", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.Polygon getGeomPolygon();

    void setGeomPolygon(org.apache.olingo.commons.api.edm.geo.Polygon _geomPolygon);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeomCollection", 
                type = "Edm.GeometryCollection", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.GeospatialCollection getGeomCollection();

    void setGeomCollection(org.apache.olingo.commons.api.edm.geo.GeospatialCollection _geomCollection);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeomMultiPoint", 
                type = "Edm.GeometryMultiPoint", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.MultiPoint getGeomMultiPoint();

    void setGeomMultiPoint(org.apache.olingo.commons.api.edm.geo.MultiPoint _geomMultiPoint);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeomMultiLine", 
                type = "Edm.GeometryMultiLineString", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.MultiLineString getGeomMultiLine();

    void setGeomMultiLine(org.apache.olingo.commons.api.edm.geo.MultiLineString _geomMultiLine);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeomMultiPolygon", 
                type = "Edm.GeometryMultiPolygon", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = false)
    org.apache.olingo.commons.api.edm.geo.MultiPolygon getGeomMultiPolygon();

    void setGeomMultiPolygon(org.apache.olingo.commons.api.edm.geo.MultiPolygon _geomMultiPolygon);    
    
    



    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Id",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Geog",
                   type = "Edm.Geography")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeogPoint",
                   type = "Edm.GeographyPoint")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogPointAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeogLine",
                   type = "Edm.GeographyLineString")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogLineAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeogPolygon",
                   type = "Edm.GeographyPolygon")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogPolygonAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeogCollection",
                   type = "Edm.GeographyCollection")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogCollectionAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeogMultiPoint",
                   type = "Edm.GeographyMultiPoint")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogMultiPointAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeogMultiLine",
                   type = "Edm.GeographyMultiLineString")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogMultiLineAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeogMultiPolygon",
                   type = "Edm.GeographyMultiPolygon")
        org.apache.olingo.ext.proxy.api.Annotatable getGeogMultiPolygonAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Geom",
                   type = "Edm.Geometry")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeomPoint",
                   type = "Edm.GeometryPoint")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomPointAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeomLine",
                   type = "Edm.GeometryLineString")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomLineAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeomPolygon",
                   type = "Edm.GeometryPolygon")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomPolygonAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeomCollection",
                   type = "Edm.GeometryCollection")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomCollectionAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeomMultiPoint",
                   type = "Edm.GeometryMultiPoint")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomMultiPointAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeomMultiLine",
                   type = "Edm.GeometryMultiLineString")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomMultiLineAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GeomMultiPolygon",
                   type = "Edm.GeometryMultiPolygon")
        org.apache.olingo.ext.proxy.api.Annotatable getGeomMultiPolygonAnnotations();


    }

}
