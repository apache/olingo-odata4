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

import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKey;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.AbstractEntityKey;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.*;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.*;

// EdmSimpleType property imports
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

@CompoundKey
public class ProductReviewKey extends AbstractEntityKey {

    private Integer _productId;

    @CompoundKeyElement(name = "ProductId", position = 0)
    public Integer getProductId() {
        return _productId;
    }

    public void setProductId(final Integer _productId) {
        this._productId = _productId;
    }

    private Integer _reviewId;

    @CompoundKeyElement(name = "ReviewId", position = 1)
    public Integer getReviewId() {
        return _reviewId;
    }

    public void setReviewId(final Integer _reviewId) {
        this._reviewId = _reviewId;
    }

    private String _revisionId;

    @CompoundKeyElement(name = "RevisionId", position = 2)
    public String getRevisionId() {
        return _revisionId;
    }

    public void setRevisionId(final String _revisionId) {
        this._revisionId = _revisionId;
    }
}
