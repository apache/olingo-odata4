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
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;

// CHECKSTYLE:ON (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;

public interface CreditCardPIComposableInvoker
    extends org.apache.olingo.ext.proxy.api.StructuredComposableInvoker<CreditCardPI, CreditCardPI.Operations>
{

  @Override
  CreditCardPIComposableInvoker select(String... select);

  @Override
  CreditCardPIComposableInvoker expand(String... expand);

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

  void setPaymentInstrumentID(java.lang.Integer _paymentInstrumentID);

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

  void setFriendlyName(java.lang.String _friendlyName);

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

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "TheStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  StoredPI
      getTheStoredPI();

      void
      setTheStoredPI(
          StoredPI _theStoredPI);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "BackupStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  StoredPI
      getBackupStoredPI();

      void
      setBackupStoredPI(
          StoredPI _backupStoredPI);

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
