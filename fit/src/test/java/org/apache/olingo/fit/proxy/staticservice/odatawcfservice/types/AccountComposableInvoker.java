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

public interface AccountComposableInvoker
    extends org.apache.olingo.ext.proxy.api.StructuredComposableInvoker<Account, Account.Operations>
{

  @Override
  AccountComposableInvoker select(String... select);

  @Override
  AccountComposableInvoker expand(String... expand);

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "AccountID",
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
  java.lang.Integer getAccountID();

  void setAccountID(java.lang.Integer _accountID);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Country",
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
  java.lang.String getCountry();

  void setCountry(java.lang.String _country);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "AccountInfo",
      type = "Microsoft.Test.OData.Services.ODataWCFService.AccountInfo",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  AccountInfo
      getAccountInfo();

      void
      setAccountInfo(
          AccountInfo _accountInfo);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "MyGiftCard",
      type = "Microsoft.Test.OData.Services.ODataWCFService.GiftCard",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "",
      targetEntitySet = "",
      containsTarget = true)
  GiftCard
      getMyGiftCard();

      void
      setMyGiftCard(
          GiftCard _myGiftCard);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "AvailableSubscriptionTemplatess",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Subscription",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "SubscriptionTemplates",
      containsTarget = false)
  SubscriptionCollection
      getAvailableSubscriptionTemplatess();

      void
      setAvailableSubscriptionTemplatess(
          SubscriptionCollection _availableSubscriptionTemplatess);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "MyPaymentInstruments",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Subscription",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "SubscriptionTemplates",
      containsTarget = true)
      Account.MyPaymentInstruments
      getMyPaymentInstruments();

      void
      setMyPaymentInstruments(
          Account.MyPaymentInstruments _myPaymentInstruments);

  @org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "MyPaymentInstruments", contained = true)
  interface MyPaymentInstruments
      extends
      org.apache.olingo.ext.proxy.api.EntitySet<PaymentInstrument, PaymentInstrumentCollection>,
      org.apache.olingo.ext.proxy.api.StructuredCollectionQuery<MyPaymentInstruments>,
  AbstractEntitySet<PaymentInstrument, java.lang.Integer, PaymentInstrumentCollection> {
    // No additional methods needed for now.
  }

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ActiveSubscriptions",
      type = "java.lang.Integer",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "SubscriptionTemplates",
      containsTarget = true)
      Account.ActiveSubscriptions
      getActiveSubscriptions();

      void
      setActiveSubscriptions(
          Account.ActiveSubscriptions _activeSubscriptions);

  @org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "ActiveSubscriptions", contained = true)
  interface ActiveSubscriptions
      extends
      org.apache.olingo.ext.proxy.api.EntitySet<Subscription, SubscriptionCollection>,
      org.apache.olingo.ext.proxy.api.StructuredCollectionQuery<ActiveSubscriptions>,
  AbstractEntitySet<Subscription, java.lang.Integer, SubscriptionCollection> {
    // No additional methods needed for now.
  }

}
