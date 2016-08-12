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
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "GiftCard",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface GiftCard
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<GiftCard>, org.apache.olingo.ext.proxy.api.StructuredQuery<GiftCard> {

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GiftCardID",
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
  java.lang.Integer getGiftCardID();

  void setGiftCardID(java.lang.Integer _giftCardID);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "GiftCardNO",
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
  java.lang.String getGiftCardNO();

  void setGiftCardNO(java.lang.String _giftCardNO);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Amount",
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
  java.lang.Double getAmount();

  void setAmount(java.lang.Double _amount);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ExperationDate",
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
  java.sql.Timestamp getExperationDate();

  void setExperationDate(java.sql.Timestamp _experationDate);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "OwnerName",
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
  java.lang.String getOwnerName();

  void setOwnerName(java.lang.String _ownerName);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetActualAmount",
        type = OperationType.FUNCTION,
        isComposable = false,
        referenceType = java.lang.Double.class, returnType = "Edm.Double")
    org.apache.olingo.ext.proxy.api.Invoker<java.lang.Double> getActualAmount(
        @Parameter(name = "bonusRate", type = "Edm.Double", nullable = true) java.lang.Double bonusRate
        );

  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GiftCardID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getGiftCardIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "GiftCardNO",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getGiftCardNOAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Amount",
        type = "Edm.Double")
    org.apache.olingo.ext.proxy.api.Annotatable getAmountAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ExperationDate",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getExperationDateAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OwnerName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getOwnerNameAnnotations();

  }

}
