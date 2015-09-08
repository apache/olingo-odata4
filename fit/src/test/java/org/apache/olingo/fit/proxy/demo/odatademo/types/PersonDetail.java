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

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;

// CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.Namespace("ODataDemo")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "PersonDetail",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface PersonDetail
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<PersonDetail>,
    org.apache.olingo.ext.proxy.api.StructuredQuery<PersonDetail> {

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

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Age",
      type = "Edm.Byte",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Short getAge();

  void setAge(java.lang.Short _age);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Gender",
      type = "Edm.Boolean",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Boolean getGender();

  void setGender(java.lang.Boolean _gender);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Phone",
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
  java.lang.String getPhone();

  void setPhone(java.lang.String _phone);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Address",
      type = "ODataDemo.Address",
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

  void setAddress(Address _address);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Photo",
      type = "Edm.Stream",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  org.apache.olingo.ext.proxy.api.EdmStreamValue getPhoto();

  void setPhoto(org.apache.olingo.ext.proxy.api.EdmStreamValue _photo);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "Person",
      type = "ODataDemo.Person",
      targetSchema = "ODataDemo",
      targetContainer = "DemoService",
      targetEntitySet = "Persons",
      containsTarget = false)
  Person getPerson();

  void setPerson(Person _person);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    // No additional methods needed for now.
  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PersonID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getPersonIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Age",
        type = "Edm.Byte")
    org.apache.olingo.ext.proxy.api.Annotatable getAgeAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Gender",
        type = "Edm.Boolean")
    org.apache.olingo.ext.proxy.api.Annotatable getGenderAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Phone",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getPhoneAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Address",
        type = "ODataDemo.Address")
    org.apache.olingo.ext.proxy.api.Annotatable getAddressAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Photo",
        type = "Edm.Stream")
    org.apache.olingo.ext.proxy.api.Annotatable getPhotoAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "Person",
        type = "ODataDemo.Person")
    org.apache.olingo.ext.proxy.api.Annotatable getPersonAnnotations();
  }

}
