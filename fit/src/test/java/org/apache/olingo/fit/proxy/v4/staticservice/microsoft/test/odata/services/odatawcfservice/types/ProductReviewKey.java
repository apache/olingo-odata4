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

import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKey;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.AbstractEntityKey;
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

@org.apache.olingo.ext.proxy.api.annotations.CompoundKey
public class ProductReviewKey extends AbstractEntityKey {

    private java.lang.Integer _productID;

    @CompoundKeyElement(name = "ProductID", position = 0)
    public java.lang.Integer getProductID() {
        return _productID;
    }

    public void setProductID(java.lang.Integer _productID) {
        this._productID = _productID;
    }

    private java.lang.Integer _productDetailID;

    @CompoundKeyElement(name = "ProductDetailID", position = 1)
    public java.lang.Integer getProductDetailID() {
        return _productDetailID;
    }

    public void setProductDetailID(java.lang.Integer _productDetailID) {
        this._productDetailID = _productDetailID;
    }

    private java.lang.String _reviewTitle;

    @CompoundKeyElement(name = "ReviewTitle", position = 2)
    public java.lang.String getReviewTitle() {
        return _reviewTitle;
    }

    public void setReviewTitle(java.lang.String _reviewTitle) {
        this._reviewTitle = _reviewTitle;
    }

    private java.lang.Integer _revisionID;

    @CompoundKeyElement(name = "RevisionID", position = 3)
    public java.lang.Integer getRevisionID() {
        return _revisionID;
    }

    public void setRevisionID(java.lang.Integer _revisionID) {
        this._revisionID = _revisionID;
    }
}
