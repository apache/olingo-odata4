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
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "PersonMetadata",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface PersonMetadata
    extends Annotatable, java.io.Serializable {

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PersonMetadataId",
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
  java.lang.Integer getPersonMetadataId();

  void setPersonMetadataId(java.lang.Integer _personMetadataId);

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

  void setPersonId(java.lang.Integer _personId);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PropertyName",
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
  java.lang.String getPropertyName();

  void setPropertyName(java.lang.String _propertyName);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PropertyValue",
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
  java.lang.String getPropertyValue();

  void setPropertyValue(java.lang.String _propertyValue);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Person",
      type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Person",
      targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService",
      targetContainer = "DefaultContainer",
      targetEntitySet = "Person",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Person
      getPerson();

      void
      setPerson(
          org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Person _person);

  ComplexFactory factory();

  interface ComplexFactory {}

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PersonMetadataId",
        type = "Edm.Int32")
    Annotatable getPersonMetadataIdAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PersonId",
        type = "Edm.Int32")
    Annotatable getPersonIdAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PropertyName",
        type = "Edm.String")
    Annotatable getPropertyNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PropertyValue",
        type = "Edm.String")
    Annotatable getPropertyValueAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Person",
        type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Person")
    Annotatable getPersonAnnotations();
  }

}
