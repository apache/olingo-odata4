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
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
//CHECKSTYLE:ON (Maven checkstyle)

public interface LoginComposableInvoker 
  extends org.apache.olingo.ext.proxy.api.StructuredComposableInvoker<Login, Login.Operations>
   {

  @Override
  LoginComposableInvoker select(String... select);

  @Override
  LoginComposableInvoker expand(String... expand);

    

    @Key
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Username", 
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
    java.lang.String getUsername();

    void setUsername(java.lang.String _username);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CustomerId", 
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
    java.lang.Integer getCustomerId();

    void setCustomerId(java.lang.Integer _customerId);
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Customer", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Customer",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Customer getCustomer();

    void setCustomer(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Customer _customer);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "LastLogin", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.LastLogin", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "LastLogin",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LastLogin getLastLogin();

    void setLastLogin(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LastLogin _lastLogin);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "SentMessages", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Message", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Message",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.MessageCollection getSentMessages();

    void setSentMessages(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.MessageCollection _sentMessages);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ReceivedMessages", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Message", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Message",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.MessageCollection getReceivedMessages();

    void setReceivedMessages(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.MessageCollection _receivedMessages);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Orders", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Order", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Order",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection getOrders();

    void setOrders(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection _orders);
    

}
