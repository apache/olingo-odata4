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
package org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types;

import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.*;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.*;

import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import java.io.Serializable;
import java.util.Collection;
import java.util.Calendar;
import javax.xml.datatype.Duration;

@KeyRef(ProductReviewKey.class)
@Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@EntityType(name = "ProductReview",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface ProductReview 
  extends Serializable {

                
    @Key
    @Property(name = "ProductID", 
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
    Integer getProductID();

    void setProductID(Integer _productID);    
    
    @Key
    @Property(name = "ProductDetailID", 
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
    Integer getProductDetailID();

    void setProductDetailID(Integer _productDetailID);    
    
    @Key
    @Property(name = "ReviewTitle", 
                type = "Edm.String", 
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
    String getReviewTitle();

    void setReviewTitle(String _reviewTitle);    
    
    @Key
    @Property(name = "RevisionID", 
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
    Integer getRevisionID();

    void setRevisionID(Integer _revisionID);    
    
    
    @Property(name = "Comment", 
                type = "Edm.String", 
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
    String getComment();

    void setComment(String _comment);    
    
    
    @Property(name = "Author", 
                type = "Edm.String", 
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
    String getAuthor();

    void setAuthor(String _author);    
    
    



}
