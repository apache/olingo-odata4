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


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "ComplexWithAllPrimitiveTypes")
public interface ComplexWithAllPrimitiveTypes 
    extends org.apache.olingo.ext.proxy.api.ComplexType,org.apache.olingo.ext.proxy.api.SingleQuery<ComplexWithAllPrimitiveTypes> {



    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Binary", type = "Edm.Binary", nullable = true)
    byte[] getBinary();

    void setBinary(byte[] _binary);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Boolean", type = "Edm.Boolean", nullable = false)
    java.lang.Boolean getBoolean();

    void setBoolean(java.lang.Boolean _boolean);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Byte", type = "Edm.Byte", nullable = false)
    java.lang.Short getByte();

    void setByte(java.lang.Short _byte);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "DateTime", type = "Edm.DateTime", nullable = false)
    java.sql.Timestamp getDateTime();

    void setDateTime(java.sql.Timestamp _dateTime);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Decimal", type = "Edm.Decimal", nullable = false)
    java.math.BigDecimal getDecimal();

    void setDecimal(java.math.BigDecimal _decimal);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Double", type = "Edm.Double", nullable = false)
    java.lang.Double getDouble();

    void setDouble(java.lang.Double _double);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Int16", type = "Edm.Int16", nullable = false)
    java.lang.Short getInt16();

    void setInt16(java.lang.Short _int16);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Int32", type = "Edm.Int32", nullable = false)
    java.lang.Integer getInt32();

    void setInt32(java.lang.Integer _int32);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Int64", type = "Edm.Int64", nullable = false)
    java.lang.Long getInt64();

    void setInt64(java.lang.Long _int64);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "SByte", type = "Edm.SByte", nullable = false)
    java.lang.Byte getSByte();

    void setSByte(java.lang.Byte _sByte);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "String", type = "Edm.String", nullable = true)
    java.lang.String getString();

    void setString(java.lang.String _string);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Single", type = "Edm.Single", nullable = false)
    java.lang.Float getSingle();

    void setSingle(java.lang.Float _single);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeographyPoint", type = "Edm.GeographyPoint", nullable = true)
    org.apache.olingo.commons.api.edm.geo.Point getGeographyPoint();

    void setGeographyPoint(org.apache.olingo.commons.api.edm.geo.Point _geographyPoint);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GeometryPoint", type = "Edm.GeometryPoint", nullable = true)
    org.apache.olingo.commons.api.edm.geo.Point getGeometryPoint();

    void setGeometryPoint(org.apache.olingo.commons.api.edm.geo.Point _geometryPoint);

    
}
