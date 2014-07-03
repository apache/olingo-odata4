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
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "SpecialEmployee",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee")
public interface SpecialEmployee
    extends Annotatable,
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee {

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

  @Override
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

  @Override
  void setManagersPersonId(java.lang.Integer _managersPersonId);

  @Override
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

  @Override
  void setSalary(java.lang.Integer _salary);

  @Override
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

  @Override
  void setTitle(java.lang.String _title);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CarsVIN",
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
  java.lang.Integer getCarsVIN();

  void setCarsVIN(java.lang.Integer _carsVIN);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Bonus",
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
  java.lang.Integer getBonus();

  void setBonus(java.lang.Integer _bonus);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "IsFullyVested",
      type = "Edm.Boolean",
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
  java.lang.Boolean getIsFullyVested();

  void setIsFullyVested(java.lang.Boolean _isFullyVested);

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

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Manager",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Person",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee
      getManager();

  @Override
      void
      setManager(
          org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee _manager);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Car",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Car",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Car",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Car getCar();

  void setCar(
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Car _car);

  @Override
  Operations operations();

  interface Operations
      extends
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee.Operations {

  }

  @Override
  ComplexFactory factory();

  interface ComplexFactory
      extends
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee.ComplexFactory {}

  @Override
  Annotations annotations();

  interface Annotations
      extends
      org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PersonId",
        type = "Edm.Int32")
    Annotatable getPersonIdAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    Annotatable getNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ManagersPersonId",
        type = "Edm.Int32")
    Annotatable getManagersPersonIdAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Salary",
        type = "Edm.Int32")
    Annotatable getSalaryAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Title",
        type = "Edm.String")
    Annotatable getTitleAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CarsVIN",
        type = "Edm.Int32")
    Annotatable getCarsVINAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Bonus",
        type = "Edm.Int32")
    Annotatable getBonusAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "IsFullyVested",
        type = "Edm.Boolean")
    Annotatable getIsFullyVestedAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "PersonMetadata",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.PersonMetadata")
    Annotatable getPersonMetadataAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Manager",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee")
    Annotatable getManagerAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Car",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Car")
    Annotatable getCarAnnotations();
  }

}
