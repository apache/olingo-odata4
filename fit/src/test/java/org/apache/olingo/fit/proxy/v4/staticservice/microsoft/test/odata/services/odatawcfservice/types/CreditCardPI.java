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

package org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types;

import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.annotations.Key;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "CreditCardPI",
    openType = false,
    hasStream = false,
    isAbstract = false,
    baseType = "Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument")
public interface CreditCardPI
    extends Annotatable,
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PaymentInstrument {

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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.util.Calendar getCreatedDate();

  @Override
  void setCreatedDate(java.util.Calendar _createdDate);

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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
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
      srid = "",
      concurrencyMode = ConcurrencyMode.None,
      fcSourcePath = "",
      fcTargetPath = "",
      fcContentKind = EdmContentKind.text,
      fcNSPrefix = "",
      fcNSURI = "",
      fcKeepInContent = false)
  java.util.Calendar getExperationDate();

  void setExperationDate(java.util.Calendar _experationDate);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "TheStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.StoredPI
      getTheStoredPI();

  @Override
      void
      setTheStoredPI(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.StoredPI _theStoredPI);

  @Override
  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "BackupStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.StoredPI
      getBackupStoredPI();

  @Override
      void
      setBackupStoredPI(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.StoredPI _backupStoredPI);

  @Override
  ComplexFactory factory();

  interface ComplexFactory
      extends
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PaymentInstrument.ComplexFactory {}

  @Override
  Annotations annotations();

  interface Annotations
      extends
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.PaymentInstrument.Annotations {

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "PaymentInstrumentID",
        type = "Edm.Int32")
    Annotatable getPaymentInstrumentIDAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "FriendlyName",
        type = "Edm.String")
    Annotatable getFriendlyNameAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CreatedDate",
        type = "Edm.DateTimeOffset")
    Annotatable getCreatedDateAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CardNumber",
        type = "Edm.String")
    Annotatable getCardNumberAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "CVV",
        type = "Edm.String")
    Annotatable getCVVAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "HolderName",
        type = "Edm.String")
    Annotatable getHolderNameAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Balance",
        type = "Edm.Double")
    Annotatable getBalanceAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ExperationDate",
        type = "Edm.DateTimeOffset")
    Annotatable getExperationDateAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "TheStoredPI",
        type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI")
    Annotatable getTheStoredPIAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "BillingStatements",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Statement")
    Annotatable getBillingStatementsAnnotations();

    @Override
    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "BackupStoredPI",
        type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI")
    Annotatable getBackupStoredPIAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "CreditRecords",
        type = "Microsoft.Test.OData.Services.ODataWCFService.CreditRecord")
    Annotatable getCreditRecordsAnnotations();
  }

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "CreditRecords",
      type = "Microsoft.Test.OData.Services.ODataWCFService.CreditRecord",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "",
      targetEntitySet = "",
      containsTarget = true)
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditCardPI.CreditRecords
      getCreditRecords();

      void
      setCreditRecords(
          org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditCardPI.CreditRecords _creditRecords);

  @org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "CreditRecords", contained = true)
  interface CreditRecords
      extends
      AbstractEntitySet<org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditRecord, java.lang.Integer, org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditRecordCollection> {

    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditRecord
        newCreditRecord();

        org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CreditRecordCollection
        newCreditRecordCollection();
  }

}
