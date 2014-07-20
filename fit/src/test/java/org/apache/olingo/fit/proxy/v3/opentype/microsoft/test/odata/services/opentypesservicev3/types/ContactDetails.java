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

package org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.types;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty;
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.*;

import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
//CHECKSTYLE:ON (Maven checkstyle)


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.OpenTypesServiceV3")
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "ContactDetails")
public interface ContactDetails 
    extends org.apache.olingo.ext.proxy.api.ComplexType,org.apache.olingo.ext.proxy.api.SingleQuery<ContactDetails> {



    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "FirstContacted", type = "Edm.Binary", nullable = true)
    byte[] getFirstContacted();

    void setFirstContacted(byte[] _firstContacted);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "LastContacted", type = "Edm.DateTimeOffset", nullable = false)
    java.sql.Timestamp getLastContacted();

    void setLastContacted(java.sql.Timestamp _lastContacted);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Contacted", type = "Edm.DateTime", nullable = false)
    java.sql.Timestamp getContacted();

    void setContacted(java.sql.Timestamp _contacted);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GUID", type = "Edm.Guid", nullable = false)
    java.util.UUID getGUID();

    void setGUID(java.util.UUID _gUID);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PreferedContactTime", type = "Edm.Time", nullable = false)
    java.math.BigDecimal getPreferedContactTime();

    void setPreferedContactTime(java.math.BigDecimal _preferedContactTime);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Byte", type = "Edm.Byte", nullable = false)
    java.lang.Short getByte();

    void setByte(java.lang.Short _byte);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "SignedByte", type = "Edm.SByte", nullable = false)
    java.lang.Byte getSignedByte();

    void setSignedByte(java.lang.Byte _signedByte);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Double", type = "Edm.Double", nullable = false)
    java.lang.Double getDouble();

    void setDouble(java.lang.Double _double);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Single", type = "Edm.Single", nullable = false)
    java.lang.Float getSingle();

    void setSingle(java.lang.Float _single);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Short", type = "Edm.Int16", nullable = false)
    java.lang.Short getShort();

    void setShort(java.lang.Short _short);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Int", type = "Edm.Int32", nullable = false)
    java.lang.Integer getInt();

    void setInt(java.lang.Integer _int);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Long", type = "Edm.Int64", nullable = false)
    java.lang.Long getLong();

    void setLong(java.lang.Long _long);

    
}
