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
package org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types;

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.OperationType;
// CHECKSTYLE:ON (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Company",
    openType = true,
    hasStream = false,
    isAbstract = false)
public interface Company
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<Company>, org.apache.olingo.ext.proxy.api.StructuredQuery<Company>,
    AbstractOpenType {

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

  void setCompanyID(java.lang.Integer _companyID);

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
  CompanyCategory
      getCompanyCategory();

      void
      setCompanyCategory(
          CompanyCategory _companyCategory);

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

  void setRevenue(java.lang.Long _revenue);

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

  void setName(java.lang.String _name);

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
  Address getAddress();

      void
      setAddress(
          Address _address);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Employees",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Employee",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Employees",
      containsTarget = false)
  EmployeeCollection
      getEmployees();

      void
      setEmployees(
          EmployeeCollection _employees);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "VipCustomer",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Customer",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "VipCustomer",
      containsTarget = false)
  Customer
      getVipCustomer();

      void
      setVipCustomer(
          Customer _vipCustomer);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Departments",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Department",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Departments",
      containsTarget = false)
  DepartmentCollection
      getDepartments();

      void
      setDepartments(
          DepartmentCollection _departments);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "CoreDepartment",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Department",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Departments",
      containsTarget = false)
  Department
      getCoreDepartment();

      void
      setCoreDepartment(
          Department _coreDepartment);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetEmployeesCount",
        type = OperationType.FUNCTION,
        isComposable = false,
        referenceType = java.lang.Integer.class, returnType = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Invoker<java.lang.Integer> getEmployeesCount(
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "IncreaseRevenue",
        type = OperationType.ACTION,
        referenceType = java.lang.Long.class, returnType = "Edm.Int64")
    org.apache.olingo.ext.proxy.api.Invoker<java.lang.Long> increaseRevenue(
        @Parameter(name = "IncreaseValue", type = "Edm.Int64", nullable = true) java.lang.Long increaseValue
        );

  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CompanyID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getCompanyIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CompanyCategory",
        type = "Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory")
    org.apache.olingo.ext.proxy.api.Annotatable getCompanyCategoryAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Revenue",
        type = "Edm.Int64")
    org.apache.olingo.ext.proxy.api.Annotatable getRevenueAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Address",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
    org.apache.olingo.ext.proxy.api.Annotatable getAddressAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Employees",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Employee")
    org.apache.olingo.ext.proxy.api.Annotatable getEmployeesAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "VipCustomer",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Customer")
    org.apache.olingo.ext.proxy.api.Annotatable getVipCustomerAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Departments",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Department")
    org.apache.olingo.ext.proxy.api.Annotatable getDepartmentsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "CoreDepartment",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Department")
    org.apache.olingo.ext.proxy.api.Annotatable getCoreDepartmentAnnotations();
  }

}
