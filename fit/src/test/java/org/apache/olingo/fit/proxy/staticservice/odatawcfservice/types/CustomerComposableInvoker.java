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
import org.apache.olingo.ext.proxy.api.annotations.Key;

// CHECKSTYLE:ON (Maven checkstyle)

public interface CustomerComposableInvoker
    extends org.apache.olingo.ext.proxy.api.StructuredComposableInvoker<Customer, Customer.Operations>
{

  @Override
  CustomerComposableInvoker select(String... select);

  @Override
  CustomerComposableInvoker expand(String... expand);

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

  void setPersonID(java.lang.Integer _personID);

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

  void setFirstName(java.lang.String _firstName);

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

  void setLastName(java.lang.String _lastName);

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

  void setMiddleName(java.lang.String _middleName);

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

      void
      setHomeAddress(
          Address _homeAddress);

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

  void setHome(org.apache.olingo.commons.api.edm.geo.Point _home);

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

  void setNumbers(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> _numbers);

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

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Parent",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Person",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "People",
      containsTarget = false)
  Person getParent();

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

}
