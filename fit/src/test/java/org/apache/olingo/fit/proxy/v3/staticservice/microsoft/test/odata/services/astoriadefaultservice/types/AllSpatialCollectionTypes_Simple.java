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
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "AllSpatialCollectionTypes_Simple",
        openType = false,
        hasStream = false,
        isAbstract = false,
        baseType = "Microsoft.Test.OData.Services.AstoriaDefaultService.AllSpatialCollectionTypes")
public interface AllSpatialCollectionTypes_Simple 
  extends org.apache.olingo.ext.proxy.api.EntityType,org.apache.olingo.ext.proxy.api.Annotatable,org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.AllSpatialCollectionTypes {

  @Override
  AllSpatialCollectionTypes_Simple load();

    

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
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ManyGeogPoint", 
                type = "Edm.GeographyPoint", 
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
    java.util.Collection<org.apache.olingo.commons.api.edm.geo.Point> getManyGeogPoint();

    void setManyGeogPoint(java.util.Collection<org.apache.olingo.commons.api.edm.geo.Point> _manyGeogPoint);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ManyGeogLine", 
                type = "Edm.GeographyLineString", 
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
    java.util.Collection<org.apache.olingo.commons.api.edm.geo.LineString> getManyGeogLine();

    void setManyGeogLine(java.util.Collection<org.apache.olingo.commons.api.edm.geo.LineString> _manyGeogLine);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ManyGeogPolygon", 
                type = "Edm.GeographyPolygon", 
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
    java.util.Collection<org.apache.olingo.commons.api.edm.geo.Polygon> getManyGeogPolygon();

    void setManyGeogPolygon(java.util.Collection<org.apache.olingo.commons.api.edm.geo.Polygon> _manyGeogPolygon);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ManyGeomPoint", 
                type = "Edm.GeometryPoint", 
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
    java.util.Collection<org.apache.olingo.commons.api.edm.geo.Point> getManyGeomPoint();

    void setManyGeomPoint(java.util.Collection<org.apache.olingo.commons.api.edm.geo.Point> _manyGeomPoint);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ManyGeomLine", 
                type = "Edm.GeometryLineString", 
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
    java.util.Collection<org.apache.olingo.commons.api.edm.geo.LineString> getManyGeomLine();

    void setManyGeomLine(java.util.Collection<org.apache.olingo.commons.api.edm.geo.LineString> _manyGeomLine);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ManyGeomPolygon", 
                type = "Edm.GeometryPolygon", 
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
    java.util.Collection<org.apache.olingo.commons.api.edm.geo.Polygon> getManyGeomPolygon();

    void setManyGeomPolygon(java.util.Collection<org.apache.olingo.commons.api.edm.geo.Polygon> _manyGeomPolygon);
    



    Annotations annotations();

    interface Annotations            extends org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.AllSpatialCollectionTypes.Annotations{

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Id",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManyGeogPoint",
                   type = "Edm.GeographyPoint")
        org.apache.olingo.ext.proxy.api.Annotatable getManyGeogPointAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManyGeogLine",
                   type = "Edm.GeographyLineString")
        org.apache.olingo.ext.proxy.api.Annotatable getManyGeogLineAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManyGeogPolygon",
                   type = "Edm.GeographyPolygon")
        org.apache.olingo.ext.proxy.api.Annotatable getManyGeogPolygonAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManyGeomPoint",
                   type = "Edm.GeometryPoint")
        org.apache.olingo.ext.proxy.api.Annotatable getManyGeomPointAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManyGeomLine",
                   type = "Edm.GeometryLineString")
        org.apache.olingo.ext.proxy.api.Annotatable getManyGeomLineAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManyGeomPolygon",
                   type = "Edm.GeometryPolygon")
        org.apache.olingo.ext.proxy.api.Annotatable getManyGeomPolygonAnnotations();


    }

}
