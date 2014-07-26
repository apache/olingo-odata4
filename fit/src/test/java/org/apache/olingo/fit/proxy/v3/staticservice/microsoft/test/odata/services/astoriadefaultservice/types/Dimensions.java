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
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "Dimensions")
public interface Dimensions 
    extends org.apache.olingo.ext.proxy.api.ComplexType<Dimensions>, org.apache.olingo.ext.proxy.api.StructuredQuery<Dimensions> {




    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "Width", 
                type = "Edm.Decimal", 
                nullable = false)
    java.math.BigDecimal getWidth();

    void setWidth(java.math.BigDecimal _width);

    


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "Height", 
                type = "Edm.Decimal", 
                nullable = false)
    java.math.BigDecimal getHeight();

    void setHeight(java.math.BigDecimal _height);

    


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "Depth", 
                type = "Edm.Decimal", 
                nullable = false)
    java.math.BigDecimal getDepth();

    void setDepth(java.math.BigDecimal _depth);

    


        Operations operations();

    interface Operations extends org.apache.olingo.ext.proxy.api.Operations{
    
        }
}
