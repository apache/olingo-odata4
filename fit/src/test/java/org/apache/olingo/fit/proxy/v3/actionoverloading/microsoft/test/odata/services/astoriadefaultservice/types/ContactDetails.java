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
package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types;

//CHECKSTYLE:OFF (Maven checkstyle)


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "ContactDetails")
public interface ContactDetails 
    extends org.apache.olingo.ext.proxy.api.ComplexType<ContactDetails>, org.apache.olingo.ext.proxy.api.StructuredQuery<ContactDetails> {




    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "EmailBag", 
                type = "Edm.String", 
                nullable = false)
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> getEmailBag();

    void setEmailBag(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> _emailBag);

    


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "AlternativeNames", 
                type = "Edm.String", 
                nullable = false)
    org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> getAlternativeNames();

    void setAlternativeNames(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> _alternativeNames);

    


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "ContactAlias", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases", 
                nullable = true)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Aliases getContactAlias();

    void setContactAlias(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Aliases _contactAlias);

        


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "HomePhone", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", 
                nullable = true)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone getHomePhone();

    void setHomePhone(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone _homePhone);

        


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "WorkPhone", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", 
                nullable = true)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone getWorkPhone();

    void setWorkPhone(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone _workPhone);

        


    @org.apache.olingo.ext.proxy.api.annotations.Property(
                name = "MobilePhoneBag", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", 
                nullable = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.PhoneCollection getMobilePhoneBag();

    void setMobilePhoneBag(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.PhoneCollection _mobilePhoneBag);

        


        Operations operations();

    interface Operations extends org.apache.olingo.ext.proxy.api.Operations{
    
        }
}
