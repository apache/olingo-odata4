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
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Account",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface Account
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<Account>, org.apache.olingo.ext.proxy.api.StructuredQuery<Account> {

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

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetDefaultPI",
        type = OperationType.FUNCTION,
        isComposable = false,
        referenceType = PaymentInstrument.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument")
        org.apache.olingo.ext.proxy.api.StructuredInvoker<PaymentInstrument>
        getDefaultPI(
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetAccountInfo",
        type = OperationType.FUNCTION,
        isComposable = true,
        referenceType = AccountInfo.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.AccountInfo")
    AccountInfoComposableInvoker
        getAccountInfo(
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "RefreshDefaultPI",
        type = OperationType.ACTION,
        referenceType = PaymentInstrument.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument")
        org.apache.olingo.ext.proxy.api.StructuredInvoker<PaymentInstrument>
        refreshDefaultPI(
            @Parameter(name = "newDate", type = "Edm.DateTimeOffset", nullable = true) java.sql.Timestamp newDate
        );

  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "AccountID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getAccountIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Country",
        type = "Edm.String")
    org.apache.olingo.ext.proxy.api.Annotatable getCountryAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "AccountInfo",
        type = "Microsoft.Test.OData.Services.ODataWCFService.AccountInfo")
    org.apache.olingo.ext.proxy.api.Annotatable getAccountInfoAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "MyGiftCard",
        type = "Microsoft.Test.OData.Services.ODataWCFService.GiftCard")
    org.apache.olingo.ext.proxy.api.Annotatable getMyGiftCardAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "MyPaymentInstruments",
        type = "Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument")
    org.apache.olingo.ext.proxy.api.Annotatable getMyPaymentInstrumentsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "ActiveSubscriptions",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Subscription")
    org.apache.olingo.ext.proxy.api.Annotatable getActiveSubscriptionsAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(
        name = "AvailableSubscriptionTemplatess",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Subscription")
    org.apache.olingo.ext.proxy.api.Annotatable getAvailableSubscriptionTemplatessAnnotations();
  }

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
