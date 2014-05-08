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

import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
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
import java.sql.Timestamp;
import javax.xml.datatype.Duration;


@Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@ComplexType(name = "ComplexWithAllPrimitiveTypes")
public interface ComplexWithAllPrimitiveTypes extends Serializable {


    @Property(name = "Binary", type = "Edm.Binary", nullable = true)
    byte[] getBinary();

    void setBinary(final byte[] _binary);

    

    @Property(name = "Boolean", type = "Edm.Boolean", nullable = false)
    Boolean getBoolean();

    void setBoolean(final Boolean _boolean);

    

    @Property(name = "Byte", type = "Edm.Byte", nullable = false)
    Short getByte();

    void setByte(final Short _byte);

    

    @Property(name = "DateTime", type = "Edm.DateTime", nullable = false)
    Timestamp getDateTime();

    void setDateTime(final Timestamp _dateTime);

    

    @Property(name = "Decimal", type = "Edm.Decimal", nullable = false)
    BigDecimal getDecimal();

    void setDecimal(final BigDecimal _decimal);

    

    @Property(name = "Double", type = "Edm.Double", nullable = false)
    Double getDouble();

    void setDouble(final Double _double);

    

    @Property(name = "Int16", type = "Edm.Int16", nullable = false)
    Short getInt16();

    void setInt16(final Short _int16);

    

    @Property(name = "Int32", type = "Edm.Int32", nullable = false)
    Integer getInt32();

    void setInt32(final Integer _int32);

    

    @Property(name = "Int64", type = "Edm.Int64", nullable = false)
    Long getInt64();

    void setInt64(final Long _int64);

    

    @Property(name = "SByte", type = "Edm.SByte", nullable = false)
    Byte getSByte();

    void setSByte(final Byte _sByte);

    

    @Property(name = "String", type = "Edm.String", nullable = true)
    String getString();

    void setString(final String _string);

    

    @Property(name = "Single", type = "Edm.Single", nullable = false)
    Float getSingle();

    void setSingle(final Float _single);

    

    @Property(name = "GeographyPoint", type = "Edm.GeographyPoint", nullable = true)
    Point getGeographyPoint();

    void setGeographyPoint(final Point _geographyPoint);

    

    @Property(name = "GeometryPoint", type = "Edm.GeometryPoint", nullable = true)
    Point getGeometryPoint();

    void setGeometryPoint(final Point _geometryPoint);

    
}
