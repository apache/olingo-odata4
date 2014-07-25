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
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
//CHECKSTYLE:ON (Maven checkstyle)


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "ProductPageView",
        openType = false,
        hasStream = false,
        isAbstract = false,
        baseType = "Microsoft.Test.OData.Services.AstoriaDefaultService.PageView")
public interface ProductPageView 
  extends org.apache.olingo.ext.proxy.api.Annotatable,
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.PageView   {

  @Override
  ProductPageView load();

    

    @Key
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PageViewId", 
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
    java.lang.Integer getPageViewId();

    void setPageViewId(java.lang.Integer _pageViewId);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Username", 
                type = "Edm.String", 
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
    java.lang.String getUsername();

    void setUsername(java.lang.String _username);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Viewed", 
                type = "Edm.DateTimeOffset", 
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
    java.sql.Timestamp getViewed();

    void setViewed(java.sql.Timestamp _viewed);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "TimeSpentOnPage", 
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
    java.math.BigDecimal getTimeSpentOnPage();

    void setTimeSpentOnPage(java.math.BigDecimal _timeSpentOnPage);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PageUrl", 
                type = "Edm.String", 
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
    java.lang.String getPageUrl();

    void setPageUrl(java.lang.String _pageUrl);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ProductId", 
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
    java.lang.Integer getProductId();

    void setProductId(java.lang.Integer _productId);
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ConcurrencyToken", 
                type = "Edm.String", 
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
    java.lang.String getConcurrencyToken();

    void setConcurrencyToken(java.lang.String _concurrencyToken);
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Login", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Login",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Login getLogin();

    void setLogin(org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Login _login);
    


    Annotations annotations();

    interface Annotations            extends org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.PageView.Annotations{

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PageViewId",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getPageViewIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Username",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getUsernameAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Viewed",
                   type = "Edm.DateTimeOffset")
        org.apache.olingo.ext.proxy.api.Annotatable getViewedAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "TimeSpentOnPage",
                   type = "Edm.Time")
        org.apache.olingo.ext.proxy.api.Annotatable getTimeSpentOnPageAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PageUrl",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getPageUrlAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductId",
                   type = "Edm.Int32")
        org.apache.olingo.ext.proxy.api.Annotatable getProductIdAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ConcurrencyToken",
                   type = "Edm.String")
        org.apache.olingo.ext.proxy.api.Annotatable getConcurrencyTokenAnnotations();



        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Login", 
                  type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login")
        org.apache.olingo.ext.proxy.api.Annotatable getLoginAnnotations();
    }

}
