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
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "ConcurrencyInfo")
public interface ConcurrencyInfo 
    extends org.apache.olingo.ext.proxy.api.ComplexType,org.apache.olingo.ext.proxy.api.SingleQuery<ConcurrencyInfo> {




    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "Token", 
                type = "Edm.String", 
                nullable = true)
    java.lang.String getToken();

    void setToken(java.lang.String _token);

    


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "QueriedDateTime", 
                type = "Edm.DateTime", 
                nullable = true)
    java.sql.Timestamp getQueriedDateTime();

    void setQueriedDateTime(java.sql.Timestamp _queriedDateTime);

    
}
