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
import org.apache.olingo.ext.proxy.api.OperationType;
// CHECKSTYLE:ON (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Person",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface Person
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<Person>, org.apache.olingo.ext.proxy.api.StructuredQuery<Person> {

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

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Parent",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Person",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "People",
      containsTarget = false)
  Person getParent();

  void setParent(
      Person _parent);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetHomeAddress",
        type = OperationType.FUNCTION,
        isComposable = true,
        referenceType = HomeAddress.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.HomeAddress")
    HomeAddressComposableInvoker
        getHomeAddress(
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "ResetAddress",
        type = OperationType.ACTION,
        referenceType = Person.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
        org.apache.olingo.ext.proxy.api.StructuredInvoker<Person>
        resetAddress(
            @Parameter(name = "addresses", type = "Collection(Microsoft.Test.OData.Services.ODataWCFService.Address)",
                nullable = false) AddressCollection addresses,
            @Parameter(name = "index", type = "Edm.Int32", nullable = false) java.lang.Integer index
        );

  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PersonID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getPersonIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "FirstName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getFirstNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "LastName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getLastNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "MiddleName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getMiddleNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "HomeAddress",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
    org.apache.olingo.ext.proxy.api.Annotatable getHomeAddressAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Home",
        type = "Edm.GeographyPoint")
    org.apache.olingo.ext.proxy.api.Annotatable getHomeAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Numbers",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getNumbersAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Emails",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getEmailsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Parent",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Person")
    org.apache.olingo.ext.proxy.api.Annotatable getParentAnnotations();
  }

}
