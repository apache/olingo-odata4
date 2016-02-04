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
package org.apache.olingo.fit.proxy.demo.odatademo.types;

import java.util.concurrent.Future;

import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("ODataDemo")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Customer",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "ODataDemo.Person")
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

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ID",
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
  @Override
  java.lang.Integer getID();

  @Override
  void setID(java.lang.Integer _iD);

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
  @Override
  java.lang.String getName();

  @Override
  void setName(java.lang.String _name);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "TotalExpense",
      type = "Edm.Decimal",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.math.BigDecimal getTotalExpense();

  void setTotalExpense(java.math.BigDecimal _totalExpense);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "PersonDetail",
      type = "ODataDemo.PersonDetail",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "PersonDetails",
      containsTarget = false)
  PersonDetail getPersonDetail();

  @Override
  void setPersonDetail(PersonDetail _personDetail);

  @Override
  Operations operations();

  interface Operations extends Person.Operations {
    // No additional methods needed for now.
  }

  @Override
  Annotations annotations();

  interface Annotations extends Person.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Name",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "TotalExpense",
        type = "Edm.Decimal")
    org.apache.olingo.ext.proxy.api.Annotatable getTotalExpenseAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "PersonDetail",
        type = "ODataDemo.PersonDetail")
    org.apache.olingo.ext.proxy.api.Annotatable getPersonDetailAnnotations();
  }

}
