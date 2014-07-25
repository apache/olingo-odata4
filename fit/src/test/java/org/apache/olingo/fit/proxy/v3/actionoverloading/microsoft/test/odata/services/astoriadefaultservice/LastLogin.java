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
package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
//CHECKSTYLE:ON (Maven checkstyle)



@org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "LastLogin", container = "Microsoft.Test.OData.Services.AstoriaDefaultService.DefaultContainer")
public interface LastLogin 
  extends org.apache.olingo.ext.proxy.api.EntitySet<org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LastLogin, org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LastLoginCollection>, 
  org.apache.olingo.ext.proxy.api.StructuredCollectionQuery<LastLogin>,
  AbstractEntitySet<org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LastLogin, java.lang.String, org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LastLoginCollection> {


        Operations operations();

    interface Operations extends org.apache.olingo.ext.proxy.api.Operations{
    
        }}
