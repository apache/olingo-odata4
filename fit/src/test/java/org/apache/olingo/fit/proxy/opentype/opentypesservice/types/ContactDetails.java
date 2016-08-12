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
package org.apache.olingo.fit.proxy.opentype.opentypesservice.types;

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.Annotatable;

// CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.OpenTypesServiceV4")
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "ContactDetails",
    isOpenType = false,
    isAbstract = false)
public interface ContactDetails
    extends org.apache.olingo.ext.proxy.api.ComplexType<ContactDetails>,
    org.apache.olingo.ext.proxy.api.StructuredQuery<ContactDetails> {

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "FirstContacted",
      type = "Edm.Binary",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  byte[] getFirstContacted();

  void setFirstContacted(byte[] _firstContacted);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "LastContacted",
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
  java.sql.Timestamp getLastContacted();

  void setLastContacted(java.sql.Timestamp _lastContacted);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Contacted",
      type = "Edm.Date",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.util.Calendar getContacted();

  void setContacted(java.util.Calendar _contacted);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GUID",
      type = "Edm.Guid",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.util.UUID getGUID();

  void setGUID(java.util.UUID _gUID);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PreferedContactTime",
      type = "Edm.TimeOfDay",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.util.Calendar getPreferedContactTime();

  void setPreferedContactTime(java.util.Calendar _preferedContactTime);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Byte",
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
  java.lang.Short getByte();

  void setByte(java.lang.Short _byte);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "SignedByte",
      type = "Edm.SByte",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Byte getSignedByte();

  void setSignedByte(java.lang.Byte _signedByte);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Double",
      type = "Edm.Double",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Double getDouble();

  void setDouble(java.lang.Double _double);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Single",
      type = "Edm.Single",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Float getSingle();

  void setSingle(java.lang.Float _single);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Short",
      type = "Edm.Int16",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Short getShort();

  void setShort(java.lang.Short _short);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Int",
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
  java.lang.Integer getInt();

  void setInt(java.lang.Integer _int);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Long",
      type = "Edm.Int64",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Long getLong();

  void setLong(java.lang.Long _long);

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "FirstContacted",
        type = "Edm.Binary")
    Annotatable getFirstContactedAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "LastContacted",
        type = "Edm.DateTimeOffset")
    Annotatable getLastContactedAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Contacted",
        type = "Edm.Date")
    Annotatable getContactedAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GUID",
        type = "Edm.Guid")
    Annotatable getGUIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PreferedContactTime",
        type = "Edm.TimeOfDay")
    Annotatable getPreferedContactTimeAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Byte",
        type = "Edm.Byte")
    Annotatable getByteAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "SignedByte",
        type = "Edm.SByte")
    Annotatable getSignedByteAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Double",
        type = "Edm.Double")
    Annotatable getDoubleAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Single",
        type = "Edm.Single")
    Annotatable getSingleAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Short",
        type = "Edm.Int16")
    Annotatable getShortAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Int",
        type = "Edm.Int32")
    Annotatable getIntAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Long",
        type = "Edm.Int64")
    Annotatable getLongAnnotations();

  }

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    // No additional methods needed for now.
  }
}
