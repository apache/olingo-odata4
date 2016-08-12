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

import java.util.concurrent.Future;

import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Customer",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
public interface Customer extends Person {

  @Override
  Customer load();

  @Override
  Future<? extends Customer> loadAsync();

  @Override
  Customer refs();

  @Override
  Customer expand(String... expand);

  @Override
  Customer select(String... select);

  @Override
  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PersonID",
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
  java.lang.Integer getPersonID();

  @Override
  void setPersonID(java.lang.Integer _personID);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "FirstName",
      type = "Edm.String",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.String getFirstName();

  @Override
  void setFirstName(java.lang.String _firstName);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "LastName",
      type = "Edm.String",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.String getLastName();

  @Override
  void setLastName(java.lang.String _lastName);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "MiddleName",
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
  java.lang.String getMiddleName();

  @Override
  void setMiddleName(java.lang.String _middleName);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "HomeAddress",
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
  Address
      getHomeAddress();

  @Override
      void
      setHomeAddress(
          Address _homeAddress);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Home",
      type = "Edm.GeographyPoint",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  org.apache.olingo.commons.api.edm.geo.Point getHome();

  @Override
  void setHome(org.apache.olingo.commons.api.edm.geo.Point _home);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Numbers",
      type = "Edm.String",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> getNumbers();

  @Override
  void setNumbers(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> _numbers);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Emails",
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
  org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> getEmails();

  @Override
  void setEmails(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> _emails);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "City",
      type = "Edm.String",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.String getCity();

  void setCity(java.lang.String _city);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Birthday",
      type = "Edm.DateTimeOffset",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.sql.Timestamp getBirthday();

  void setBirthday(java.sql.Timestamp _birthday);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "TimeBetweenLastTwoOrders",
      type = "Edm.Duration",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.math.BigDecimal getTimeBetweenLastTwoOrders();

  void setTimeBetweenLastTwoOrders(java.math.BigDecimal _timeBetweenLastTwoOrders);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Parent",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Person",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "People",
      containsTarget = false)
  Person getParent();

  @Override
  void setParent(
      Person _parent);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Orders",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Order",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Orders",
      containsTarget = false)
  OrderCollection
      getOrders();

      void
      setOrders(
          OrderCollection _orders);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Company",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Company",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Company",
      containsTarget = false)
  Company getCompany();

      void
      setCompany(
          Company _company);

  @Override
  Operations operations();

  interface Operations
      extends
      Person.Operations {
    // No additional methods needed for now.
  }

  @Override
  Annotations annotations();

  interface Annotations
      extends
      Person.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PersonID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getPersonIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "FirstName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getFirstNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "LastName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getLastNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "MiddleName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getMiddleNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "HomeAddress",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
    org.apache.olingo.ext.proxy.api.Annotatable getHomeAddressAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Home",
        type = "Edm.GeographyPoint")
    org.apache.olingo.ext.proxy.api.Annotatable getHomeAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Numbers",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getNumbersAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Emails",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getEmailsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "City",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getCityAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Birthday",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getBirthdayAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "TimeBetweenLastTwoOrders",
        type = "Edm.Duration")
    org.apache.olingo.ext.proxy.api.Annotatable getTimeBetweenLastTwoOrdersAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Parent",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Person")
    org.apache.olingo.ext.proxy.api.Annotatable getParentAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Orders",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Order")
    org.apache.olingo.ext.proxy.api.Annotatable getOrdersAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Company",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Company")
    org.apache.olingo.ext.proxy.api.Annotatable getCompanyAnnotations();
  }

}
