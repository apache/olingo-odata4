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

package com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types;

import com.msopentech.odatajclient.proxy.api.annotations.Namespace;
import com.msopentech.odatajclient.proxy.api.annotations.EntityType;
import com.msopentech.odatajclient.proxy.api.annotations.Key;
import com.msopentech.odatajclient.proxy.api.annotations.KeyRef;
import com.msopentech.odatajclient.proxy.api.annotations.NavigationProperty;
import com.msopentech.odatajclient.proxy.api.annotations.Property;
import com.msopentech.odatajclient.proxy.api.annotations.Operation;
import com.msopentech.odatajclient.proxy.api.annotations.Parameter;
import com.msopentech.odatajclient.engine.metadata.edm.v3.ParameterMode;
import com.msopentech.odatajclient.proxy.api.AbstractComplexType;
import com.msopentech.odatajclient.proxy.api.AbstractOpenType;
import com.msopentech.odatajclient.engine.metadata.EdmContentKind;
import com.msopentech.odatajclient.engine.metadata.edm.ConcurrencyMode;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.*;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.*;

// EdmSimpleType property imports
import com.msopentech.odatajclient.engine.data.ODataDuration;
import com.msopentech.odatajclient.engine.data.ODataTimestamp;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.GeospatialCollection;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.LineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiLineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPoint;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPolygon;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Polygon;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import java.io.Serializable;
import java.util.Collection;


@Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@EntityType(name = "Product",
        openType = false,
        hasStream = false,
        isAbstract = false,
        baseType = "")
public interface Product extends Serializable {

    
    
    @Property(name = "Picture", 
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
                fcKeepInContent = true)
    public java.io.InputStream getPicture();

    public void setPicture(final java.io.InputStream _picture);

    @Key
    @Property(name = "ProductId", 
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
                fcKeepInContent = true)
    public Integer getProductId();

    public void setProductId(final Integer _productId);

    
    @Property(name = "Description", 
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
                fcKeepInContent = true)
    public String getDescription();

    public void setDescription(final String _description);

    
    @Property(name = "Dimensions", 
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
                fcKeepInContent = true)
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions getDimensions();

    public void setDimensions(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions _dimensions);

    
    @Property(name = "BaseConcurrency", 
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
                concurrencyMode = ConcurrencyMode.Fixed,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = true)
    public String getBaseConcurrency();

    public void setBaseConcurrency(final String _baseConcurrency);

    
    @Property(name = "ComplexConcurrency", 
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
                fcKeepInContent = true)
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo getComplexConcurrency();

    public void setComplexConcurrency(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo _complexConcurrency);

    
    @Property(name = "NestedComplexConcurrency", 
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
                fcKeepInContent = true)
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo getNestedComplexConcurrency();

    public void setNestedComplexConcurrency(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo _nestedComplexConcurrency);

    


    @NavigationProperty(name = "RelatedProducts", 
                type = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Product)", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Product")
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection getRelatedProducts();

    public void setRelatedProducts(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductCollection _relatedProducts);


    @NavigationProperty(name = "Detail", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ProductDetail", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "ProductDetail")
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductDetail getDetail();

    public void setDetail(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductDetail _detail);


    @NavigationProperty(name = "Reviews", 
                type = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ProductReview)", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "ProductReview")
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductReviewCollection getReviews();

    public void setReviews(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductReviewCollection _reviews);


    @NavigationProperty(name = "Photos", 
                type = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ProductPhoto)", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "ProductPhoto")
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductPhotoCollection getPhotos();

    public void setPhotos(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ProductPhotoCollection _photos);


    @Operation(name = "RetrieveProduct"      ,
                    returnType = "Edm.Int32")
    Integer retrieveProduct(
        );

}
