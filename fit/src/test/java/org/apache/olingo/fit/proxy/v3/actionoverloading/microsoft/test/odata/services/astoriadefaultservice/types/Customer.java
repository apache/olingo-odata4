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

package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types;

import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Customer",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface Customer
    extends Annotatable, java.io.Serializable {

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Thumbnail",
      type = "Edm.Stream",
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
  java.io.InputStream getThumbnail();

  void setThumbnail(java.io.InputStream _thumbnail);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Video",
      type = "Edm.Stream",
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
  java.io.InputStream getVideo();

  void setVideo(java.io.InputStream _video);

  @Key
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

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Name",
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
  java.lang.String getName();

  void setName(java.lang.String _name);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PrimaryContactInfo",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails",
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
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails
      getPrimaryContactInfo();

      void
      setPrimaryContactInfo(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails _primaryContactInfo);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BackupContactInfo",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails",
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
      java.util.Collection<org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails>
      getBackupContactInfo();

      void
      setBackupContactInfo(
          java.util.Collection<org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails> _backupContactInfo);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Auditing",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo",
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
  org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo
      getAuditing();

      void
      setAuditing(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo _auditing);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Orders",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Order",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Order",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection
      getOrders();

      void
      setOrders(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.OrderCollection _orders);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Logins",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Login",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LoginCollection
      getLogins();

      void
      setLogins(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.LoginCollection _logins);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Husband",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Customer",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Customer
      getHusband();

      void
      setHusband(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Customer _husband);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Wife",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Customer",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Customer
      getWife();

      void
      setWife(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Customer _wife);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Info",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.CustomerInfo",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "CustomerInfo",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.CustomerInfo
      getInfo();

      void
      setInfo(
          org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.CustomerInfo _info);

  ComplexFactory factory();

  interface ComplexFactory {
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PrimaryContactInfo",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails")
        org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails
        newPrimaryContactInfo();

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "BackupContactInfo",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails")
        org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails
        newBackupContactInfo();

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Auditing",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo")
        org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo
        newAuditing();

  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Thumbnail",
        type = "Edm.Stream")
    Annotatable getThumbnailAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Video",
        type = "Edm.Stream")
    Annotatable getVideoAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CustomerId",
        type = "Edm.Int32")
    Annotatable getCustomerIdAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    Annotatable getNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PrimaryContactInfo",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails")
    Annotatable getPrimaryContactInfoAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "BackupContactInfo",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails")
    Annotatable getBackupContactInfoAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Auditing",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo")
    Annotatable getAuditingAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Orders",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Order")
    Annotatable getOrdersAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Logins",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login")
    Annotatable getLoginsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Husband",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer")
    Annotatable getHusbandAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Wife",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer")
    Annotatable getWifeAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Info",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.CustomerInfo")
    Annotatable getInfoAnnotations();
  }

}
