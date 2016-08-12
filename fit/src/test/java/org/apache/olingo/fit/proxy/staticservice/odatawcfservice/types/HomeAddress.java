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
import org.apache.olingo.ext.proxy.api.Annotatable;

// CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "HomeAddress",
    isOpenType = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.ODataWCFService.Address")
public interface HomeAddress
    extends Address {

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Street",
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
  java.lang.String getStreet();

  @Override
  void setStreet(java.lang.String _street);

  @Override
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

  @Override
  void setCity(java.lang.String _city);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PostalCode",
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
  java.lang.String getPostalCode();

  @Override
  void setPostalCode(java.lang.String _postalCode);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "FamilyName",
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
  java.lang.String getFamilyName();

  void setFamilyName(java.lang.String _familyName);

  @Override
  Annotations annotations();

  interface Annotations
      extends
          Address.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Street",
        type = "Edm.String")
    Annotatable getStreetAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "City",
        type = "Edm.String")
    Annotatable getCityAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PostalCode",
        type = "Edm.String")
    Annotatable getPostalCodeAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "FamilyName",
        type = "Edm.String")
    Annotatable getFamilyNameAnnotations();

  }

  @Override
  Operations operations();

  interface Operations
      extends
          Address.Operations {
    // No additional methods needed for now.
  }
}
