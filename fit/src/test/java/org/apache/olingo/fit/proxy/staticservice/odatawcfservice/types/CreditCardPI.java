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

import java.util.concurrent.Future;

import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "CreditCardPI",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument")
public interface CreditCardPI extends PaymentInstrument {

  @Override
  CreditCardPI load();

  @Override
  Future<? extends CreditCardPI> loadAsync();

  @Override
  CreditCardPI refs();

  @Override
  CreditCardPI expand(String... expand);

  @Override
  CreditCardPI select(String... select);

  @Override
  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PaymentInstrumentID",
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
  java.lang.Integer getPaymentInstrumentID();

  @Override
  void setPaymentInstrumentID(java.lang.Integer _paymentInstrumentID);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "FriendlyName",
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
  java.lang.String getFriendlyName();

  @Override
  void setFriendlyName(java.lang.String _friendlyName);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CreatedDate",
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
  java.sql.Timestamp getCreatedDate();

  @Override
  void setCreatedDate(java.sql.Timestamp _createdDate);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CardNumber",
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
  java.lang.String getCardNumber();

  void setCardNumber(java.lang.String _cardNumber);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CVV",
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
  java.lang.String getCVV();

  void setCVV(java.lang.String _cVV);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "HolderName",
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
  java.lang.String getHolderName();

  void setHolderName(java.lang.String _holderName);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Balance",
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
  java.lang.Double getBalance();

  void setBalance(java.lang.Double _balance);

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

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "TheStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  StoredPI
      getTheStoredPI();

  @Override
      void
      setTheStoredPI(
          StoredPI _theStoredPI);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "BackupStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  StoredPI
      getBackupStoredPI();

  @Override
      void
      setBackupStoredPI(
          StoredPI _backupStoredPI);

  @Override
  Operations operations();

  interface Operations
      extends
          PaymentInstrument.Operations {
    // No additional methods needed for now.
  }

  @Override
  Annotations annotations();

  interface Annotations
      extends
          PaymentInstrument.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PaymentInstrumentID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getPaymentInstrumentIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "FriendlyName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getFriendlyNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CreatedDate",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getCreatedDateAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CardNumber",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getCardNumberAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CVV",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getCVVAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "HolderName",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getHolderNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Balance",
        type = "Edm.Double")
    org.apache.olingo.ext.proxy.api.Annotatable getBalanceAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ExperationDate",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getExperationDateAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "TheStoredPI",
        type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI")
    org.apache.olingo.ext.proxy.api.Annotatable getTheStoredPIAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "BillingStatements",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Statement")
    org.apache.olingo.ext.proxy.api.Annotatable getBillingStatementsAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "BackupStoredPI",
        type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI")
    org.apache.olingo.ext.proxy.api.Annotatable getBackupStoredPIAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "CreditRecords",
        type = "Microsoft.Test.OData.Services.ODataWCFService.CreditRecord")
    org.apache.olingo.ext.proxy.api.Annotatable getCreditRecordsAnnotations();
  }

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "CreditRecords",
      type = "Microsoft.Test.OData.Services.ODataWCFService.CreditRecord",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "",
      targetEntitySet = "",
      containsTarget = true)
      CreditCardPI.CreditRecords
      getCreditRecords();

      void
      setCreditRecords(
          CreditCardPI.CreditRecords _creditRecords);

  @org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "CreditRecords", contained = true)
  interface CreditRecords
      extends
      org.apache.olingo.ext.proxy.api.EntitySet<CreditRecord, CreditRecordCollection>,
      org.apache.olingo.ext.proxy.api.StructuredCollectionQuery<CreditRecords>,
  AbstractEntitySet<CreditRecord, java.lang.Integer, CreditRecordCollection> {
    // No additional methods needed for now.
  }

}
