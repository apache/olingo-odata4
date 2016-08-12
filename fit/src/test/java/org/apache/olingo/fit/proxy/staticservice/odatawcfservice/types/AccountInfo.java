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
import org.apache.olingo.ext.proxy.api.Annotatable;

// CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "AccountInfo",
    isOpenType = true,
    isAbstract = false)
public interface AccountInfo
    extends org.apache.olingo.ext.proxy.api.ComplexType<AccountInfo>,
    org.apache.olingo.ext.proxy.api.StructuredQuery<AccountInfo>, AbstractOpenType {

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

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "FirstName",
        type = "Edm.String")
    Annotatable getFirstNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "LastName",
        type = "Edm.String")
    Annotatable getLastNameAnnotations();

  }

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    //
  }
}
