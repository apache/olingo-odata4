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
package org.apache.olingo.fit.proxy.staticservice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Account;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccountInfo;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Asset;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Club;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Color;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Company;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.CompanyAddress;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.CompanyCategory;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.CreditCardPI;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.CreditRecord;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Department;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Employee;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.GiftCard;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.HomeAddress;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.IsBoss;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.LabourUnion;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Order;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.OrderDetail;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PaymentInstrument;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Product;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductDetail;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductReview;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PublicCompany;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Statement;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.StoredPI;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Subscription;

public class Service<C extends EdmEnabledODataClient> extends AbstractService<C> {

  // CHECKSTYLE:OFF (Maven checkstyle)
  private static final String COMPRESSED_METADATA =
      "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";
  private static final String METADATA_ETAG = null;
  // CHECKSTYLE:ON (Maven checkstyle)

  private static final Map<String, Service<?>> SERVICES = new ConcurrentHashMap<String, Service<?>>();

  @SuppressWarnings("unchecked")
  private static <C extends EdmEnabledODataClient> Service<C> getInstance(
      final ODataServiceVersion version, final String serviceRoot, final boolean transactional) {

    if (!SERVICES.containsKey(serviceRoot)) {
      final Service<C> instance = new Service<C>(COMPRESSED_METADATA, METADATA_ETAG,
          version, serviceRoot, transactional);
      SERVICES.put(serviceRoot, instance);
    }

    return (Service<C>) SERVICES.get(serviceRoot);
  }

  /**
   * Gives an OData 4.0 instance for given service root, operating in transactions (with batch requests).
   *
   * @param serviceRoot OData service root
   * @return OData 4.0 instance for given service root, operating in transactions (with batch requests)
   */
  public static Service<EdmEnabledODataClient> getV4(
      final String serviceRoot) {

    return getV4(serviceRoot, true);
  }

  /**
   * Gives an OData 4.0 instance for given service root.
   *
   * @param serviceRoot OData service root
   * @param transactional whether operating in transactions (with batch requests) or not
   * @return OData 4.0 instance for given service root
   */
  public static Service<EdmEnabledODataClient> getV4(
      final String serviceRoot, final boolean transactional) {

    return getInstance(ODataServiceVersion.V40, serviceRoot, transactional);
  }

  private final Map<String, Class<?>> entityTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<?>> complexTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<?>> enumTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<? extends AbstractTerm>> terms = new HashMap<String, Class<? extends AbstractTerm>>();

  public Service(final String compressedMetadata, final String metadataETag,
      final ODataServiceVersion version, final String serviceRoot, final boolean transactional) {

    super(compressedMetadata, metadataETag, version, serviceRoot, transactional);

    // CHECKSTYLE:OFF (Maven checkstyle)
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.CreditCardPI",
        CreditCardPI.class);
    entityTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Account",
        Account.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.ProductDetail",
        ProductDetail.class);
    entityTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Order",
        Order.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.Statement",
        Statement.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.Subscription",
        Subscription.class);
    entityTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Person",
        Person.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.GiftCard",
        GiftCard.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.OrderDetail",
        OrderDetail.class);
    entityTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Product",
        Product.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
        PaymentInstrument.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.Customer",
        Customer.class);
    entityTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Club",
        Club.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.ProductReview",
        ProductReview.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.Department",
        Department.class);
    entityTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Asset",
        Asset.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.Employee",
        Employee.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
        StoredPI.class);
    entityTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Company",
        Company.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.CreditRecord",
        CreditRecord.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.LabourUnion",
        LabourUnion.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.PublicCompany",
        PublicCompany.class);
    complexTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Address",
        Address.class);
    complexTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.CompanyAddress",
        CompanyAddress.class);
    complexTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.AccountInfo",
        AccountInfo.class);
    complexTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.HomeAddress",
        HomeAddress.class);
    enumTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.AccessLevel",
        AccessLevel.class);
    enumTypes.put("Microsoft.Test.OData.Services.ODataWCFService.Color",
        Color.class);
    enumTypes
    .put(
        "Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory",
        CompanyCategory.class);
    terms.put("Microsoft.Test.OData.Services.ODataWCFService.IsBoss",
        IsBoss.class);
    // CHECKSTYLE:ON (Maven checkstyle)
  }

  @Override
  public Class<?> getEntityTypeClass(final String name) {
    return entityTypes.get(name);
  }

  @Override
  public Class<?> getComplexTypeClass(final String name) {
    return complexTypes.get(name);
  }

  @Override
  public Class<?> getEnumTypeClass(final String name) {
    return enumTypes.get(name);
  }

  @Override
  public Class<? extends AbstractTerm> getTermClass(final String name) {
    return terms.get(name);
  }

}
