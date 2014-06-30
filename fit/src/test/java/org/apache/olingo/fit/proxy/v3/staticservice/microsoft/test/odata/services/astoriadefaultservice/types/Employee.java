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
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Employee",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.AstoriaDefaultService.Person")
public interface Employee
    extends Annotatable,
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Person {

  @Override
  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PersonId",
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
  java.lang.Integer getPersonId();

  @Override
  void setPersonId(java.lang.Integer _personId);

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

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ManagersPersonId",
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
  java.lang.Integer getManagersPersonId();

  void setManagersPersonId(java.lang.Integer _managersPersonId);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Salary",
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
  java.lang.Integer getSalary();

  void setSalary(java.lang.Integer _salary);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Title",
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
  java.lang.String getTitle();

  void setTitle(java.lang.String _title);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "PersonMetadata",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.PersonMetadata",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "PersonMetadata",
      containsTarget = false)
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.PersonMetadataCollection
      getPersonMetadata();

  @Override
      void
      setPersonMetadata(
          org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.PersonMetadataCollection _personMetadata);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Manager",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Person",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee
      getManager();

      void
      setManager(
          org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee _manager);

  Operations operations();

  interface Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "Sack",
        type = OperationType.ACTION)
    void sack(
        );

  }

  @Override
  ComplexFactory factory();

  interface ComplexFactory
      extends
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Person.ComplexFactory {}

  @Override
  Annotations annotations();

  interface Annotations
      extends
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Person.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PersonId",
        type = "Edm.Int32")
    Annotatable getPersonIdAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    Annotatable getNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManagersPersonId",
        type = "Edm.Int32")
    Annotatable getManagersPersonIdAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Salary",
        type = "Edm.Int32")
    Annotatable getSalaryAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Title",
        type = "Edm.String")
    Annotatable getTitleAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "PersonMetadata",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.PersonMetadata")
    Annotatable getPersonMetadataAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Manager",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee")
    Annotatable getManagerAnnotations();
  }

}
