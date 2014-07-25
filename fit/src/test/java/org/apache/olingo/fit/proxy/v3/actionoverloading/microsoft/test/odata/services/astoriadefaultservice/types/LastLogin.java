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


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "LastLogin",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface LastLogin 
  extends org.apache.olingo.ext.proxy.api.Annotatable,
  org.apache.olingo.ext.proxy.api.EntityType<LastLogin>, org.apache.olingo.ext.proxy.api.StructuredQuery<LastLogin>   {


    

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
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "LoggedIn", 
                type = "Edm.DateTime", 
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
    java.sql.Timestamp getLoggedIn();

    void setLoggedIn(java.sql.Timestamp _loggedIn);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "LoggedOut", 
                type = "Edm.DateTime", 
                nullable = true,
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
    java.sql.Timestamp getLoggedOut();

    void setLoggedOut(java.sql.Timestamp _loggedOut);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Duration", 
                type = "Edm.Time", 
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
    java.math.BigDecimal getDuration();

    void setDuration(java.math.BigDecimal _duration);
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Login", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Login",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Login getLogin();

    void setLogin(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Login _login);
    


        Operations operations();

    interface Operations extends org.apache.olingo.ext.proxy.api.Operations{
    
        }
    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Username",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getUsernameAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "LoggedIn",
                   type = "Edm.DateTime")
        org.apache.olingo.ext.proxy.api.Annotatable getLoggedInAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "LoggedOut",
                   type = "Edm.DateTime")
        org.apache.olingo.ext.proxy.api.Annotatable getLoggedOutAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Duration",
                   type = "Edm.Time")
        org.apache.olingo.ext.proxy.api.Annotatable getDurationAnnotations();



        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Login", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login")
        org.apache.olingo.ext.proxy.api.Annotatable getLoginAnnotations();
    }

}
