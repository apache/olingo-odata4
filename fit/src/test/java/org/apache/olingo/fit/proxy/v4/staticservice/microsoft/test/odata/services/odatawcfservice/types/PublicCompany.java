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

// CHECKSTYLE:OFF (Maven checkstyle)
import java.util.concurrent.Future;
// CHECKSTYLE:ON (Maven checkstyle)

import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "PublicCompany",
    openType = true,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.ODataWCFService.Company")
public interface PublicCompany
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company,
    AbstractOpenType {

  @Override
  PublicCompany load();

  @Override
  Future<? extends PublicCompany> loadAsync();

  @Override
  PublicCompany refs();

  @Override
  PublicCompany expand(String... expand);

  @Override
  PublicCompany select(String... select);

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
      srid = "")
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
      srid = "")
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
      srid = "")
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
      srid = "")
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
      srid = "")
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
      srid = "")
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
    // No additional methods needed for now.
  }

  @Override
  Annotations annotations();

  interface Annotations
      extends
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Company.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CompanyID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getCompanyIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CompanyCategory",
        type = "Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory")
    org.apache.olingo.ext.proxy.api.Annotatable getCompanyCategoryAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Revenue",
        type = "Edm.Int64")
    org.apache.olingo.ext.proxy.api.Annotatable getRevenueAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Address",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
    org.apache.olingo.ext.proxy.api.Annotatable getAddressAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "StockExchange",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getStockExchangeAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Employees",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Employee")
    org.apache.olingo.ext.proxy.api.Annotatable getEmployeesAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "VipCustomer",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Customer")
    org.apache.olingo.ext.proxy.api.Annotatable getVipCustomerAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Departments",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Department")
    org.apache.olingo.ext.proxy.api.Annotatable getDepartmentsAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "CoreDepartment",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Department")
    org.apache.olingo.ext.proxy.api.Annotatable getCoreDepartmentAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Assets",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Asset")
    org.apache.olingo.ext.proxy.api.Annotatable getAssetsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Club",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Club")
    org.apache.olingo.ext.proxy.api.Annotatable getClubAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "LabourUnion",
        type = "Microsoft.Test.OData.Services.ODataWCFService.LabourUnion")
    org.apache.olingo.ext.proxy.api.Annotatable getLabourUnionAnnotations();
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
      org.apache.olingo.ext.proxy.api.EntitySet<org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Asset, org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AssetCollection>,
      org.apache.olingo.ext.proxy.api.StructuredCollectionQuery<Assets>,
  AbstractEntitySet<org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Asset, java.lang.Integer, org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AssetCollection> {
    // No additional methods needed for now.
  }

}
