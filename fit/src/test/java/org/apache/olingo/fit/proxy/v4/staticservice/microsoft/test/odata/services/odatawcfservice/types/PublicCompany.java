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

package org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types;

import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "PublicCompany",
    openType = true,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.ODataWCFService.Company")
public interface PublicCompany
    extends Annotatable,
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company {

  @Override
  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CompanyID",
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
  java.lang.Integer getCompanyID();

  @Override
  void setCompanyID(java.lang.Integer _companyID);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CompanyCategory",
      type = "Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory",
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
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CompanyCategory
      getCompanyCategory();

  @Override
      void
      setCompanyCategory(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CompanyCategory _companyCategory);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Revenue",
      type = "Edm.Int64",
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
  java.lang.Long getRevenue();

  @Override
  void setRevenue(java.lang.Long _revenue);

  @Override
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

  @Override
  void setName(java.lang.String _name);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Address",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Address",
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
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address getAddress();

  @Override
      void
      setAddress(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address _address);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "StockExchange",
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
  java.lang.String getStockExchange();

  void setStockExchange(java.lang.String _stockExchange);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Employees",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Employee",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Employees",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.EmployeeCollection
      getEmployees();

  @Override
      void
      setEmployees(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.EmployeeCollection _employees);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "VipCustomer",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Customer",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "VipCustomer",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer
      getVipCustomer();

  @Override
      void
      setVipCustomer(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer _vipCustomer);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Departments",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Department",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Departments",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.DepartmentCollection
      getDepartments();

  @Override
      void
      setDepartments(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.DepartmentCollection _departments);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "CoreDepartment",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Department",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Departments",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Department
      getCoreDepartment();

  @Override
      void
      setCoreDepartment(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Department _coreDepartment);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Club",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Club",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "",
      targetEntitySet = "",
      containsTarget = true)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Club getClub();

  void setClub(
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Club _club);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "LabourUnion",
      type = "Microsoft.Test.OData.Services.ODataWCFService.LabourUnion",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "LabourUnion",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.LabourUnion
      getLabourUnion();

      void
      setLabourUnion(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.LabourUnion _labourUnion);

  @Override
  Operations operations();

  interface Operations
      extends
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company.Operations {

  }

  @Override
  ComplexFactory factory();

  interface ComplexFactory
      extends
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company.ComplexFactory {
    @Override
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Address",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address
        newAddress();

  }

  @Override
  Annotations annotations();

  interface Annotations
      extends
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CompanyID",
        type = "Edm.Int32")
    Annotatable getCompanyIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CompanyCategory",
        type = "Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory")
    Annotatable getCompanyCategoryAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Revenue",
        type = "Edm.Int64")
    Annotatable getRevenueAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    Annotatable getNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Address",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
    Annotatable getAddressAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "StockExchange",
        type = "Edm.String")
    Annotatable getStockExchangeAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Employees",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Employee")
    Annotatable getEmployeesAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "VipCustomer",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Customer")
    Annotatable getVipCustomerAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Departments",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Department")
    Annotatable getDepartmentsAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "CoreDepartment",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Department")
    Annotatable getCoreDepartmentAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Assets",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Asset")
    Annotatable getAssetsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Club",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Club")
    Annotatable getClubAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "LabourUnion",
        type = "Microsoft.Test.OData.Services.ODataWCFService.LabourUnion")
    Annotatable getLabourUnionAnnotations();
  }

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Assets",
      type = "Microsoft.Test.OData.Services.ODataWCFService.LabourUnion",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "LabourUnion",
      containsTarget = true)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PublicCompany.Assets
      getAssets();

      void
      setAssets(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PublicCompany.Assets _assets);

  @org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "Assets", contained = true)
  interface Assets
      extends
      AbstractEntitySet<org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Asset, java.lang.Integer, org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AssetCollection> {

    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Asset newAsset();

    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AssetCollection
        newAssetCollection();
  }

}
