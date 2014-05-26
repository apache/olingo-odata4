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

import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty;
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.*;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.*;

import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Product",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface Product 
  extends Annotatable,java.io.Serializable {

    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Picture", 
                type = "Edm.Stream", 
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
    java.io.InputStream getPicture();

    void setPicture(java.io.InputStream _picture);    
    
    @Key
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ProductId", 
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
    java.lang.Integer getProductId();

    void setProductId(java.lang.Integer _productId);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Description", 
                type = "Edm.String", 
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
    java.lang.String getDescription();

    void setDescription(java.lang.String _description);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Dimensions", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions", 
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
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions getDimensions();

    void setDimensions(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions _dimensions);    
        
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BaseConcurrency", 
                type = "Edm.String", 
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
    java.lang.String getBaseConcurrency();

    void setBaseConcurrency(java.lang.String _baseConcurrency);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComplexConcurrency", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo", 
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
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo getComplexConcurrency();

    void setComplexConcurrency(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo _complexConcurrency);    
        
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "NestedComplexConcurrency", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo", 
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
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo getNestedComplexConcurrency();

    void setNestedComplexConcurrency(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo _nestedComplexConcurrency);    
        
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "RelatedProducts", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Product",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection getRelatedProducts();

    void setRelatedProducts(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection _relatedProducts);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Detail", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductDetail", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "ProductDetail",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductDetail getDetail();

    void setDetail(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductDetail _detail);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Reviews", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductReview", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "ProductReview",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductReviewCollection getReviews();

    void setReviews(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductReviewCollection _reviews);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Photos", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductPhoto", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "ProductPhoto",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductPhotoCollection getPhotos();

    void setPhotos(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductPhotoCollection _photos);
    

        Operations operations();

    interface Operations {
    
          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "ChangeProductDimensions",
                    type = OperationType.ACTION)
      void changeProductDimensions(
                @Parameter(name = "dimensions", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions", nullable = true) org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions dimensions
            );

        }

    ComplexFactory factory();

    interface ComplexFactory {
         @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Dimensions",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions")
         org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions newDimensions();

         @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComplexConcurrency",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo")
         org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo newComplexConcurrency();

         @org.apache.olingo.ext.proxy.api.annotations.Property(name = "NestedComplexConcurrency",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo")
         org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo newNestedComplexConcurrency();

    }

    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Picture",
                   type = "Edm.Stream")
        Annotatable getPictureAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductId",
                   type = "Edm.Int32")
        Annotatable getProductIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Description",
                   type = "Edm.String")
        Annotatable getDescriptionAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Dimensions",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions")
        Annotatable getDimensionsAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BaseConcurrency",
                   type = "Edm.String")
        Annotatable getBaseConcurrencyAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ComplexConcurrency",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo")
        Annotatable getComplexConcurrencyAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "NestedComplexConcurrency",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo")
        Annotatable getNestedComplexConcurrencyAnnotations();



        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "RelatedProducts", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product")
        Annotatable getRelatedProductsAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Detail", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductDetail")
        Annotatable getDetailAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Reviews", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductReview")
        Annotatable getReviewsAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Photos", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductPhoto")
        Annotatable getPhotosAnnotations();
    }

}
