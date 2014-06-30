/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types;

import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "PageView",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface PageView
    extends Annotatable, java.io.Serializable {

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
  java.util.Calendar getViewed();

  void setViewed(java.util.Calendar _viewed);

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

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Login",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Login",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Login
      getLogin();

      void
      setLogin(
          org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Login _login);

  ComplexFactory factory();

  interface ComplexFactory {}

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PageViewId",
        type = "Edm.Int32")
    Annotatable getPageViewIdAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Username",
        type = "Edm.String")
    Annotatable getUsernameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Viewed",
        type = "Edm.DateTimeOffset")
    Annotatable getViewedAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "TimeSpentOnPage",
        type = "Edm.Time")
    Annotatable getTimeSpentOnPageAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PageUrl",
        type = "Edm.String")
    Annotatable getPageUrlAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Login",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login")
    Annotatable getLoginAnnotations();
  }

}
