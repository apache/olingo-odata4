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
package org.apache.olingo.fit.proxy.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Address;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Advertisement;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Category;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Customer;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Employee;
import org.apache.olingo.fit.proxy.demo.odatademo.types.FeaturedProduct;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Person;
import org.apache.olingo.fit.proxy.demo.odatademo.types.PersonDetail;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Product;
import org.apache.olingo.fit.proxy.demo.odatademo.types.ProductDetail;
import org.apache.olingo.fit.proxy.demo.odatademo.types.Supplier;

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
  public static Service<org.apache.olingo.client.api.EdmEnabledODataClient> getV4(
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
  public static Service<org.apache.olingo.client.api.EdmEnabledODataClient> getV4(
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
    entityTypes.put("ODataDemo.Customer", Customer.class);
    entityTypes.put("ODataDemo.PersonDetail", PersonDetail.class);
    entityTypes.put("ODataDemo.ProductDetail", ProductDetail.class);
    entityTypes.put("ODataDemo.Employee", Employee.class);
    entityTypes.put("ODataDemo.Product", Product.class);
    entityTypes.put("ODataDemo.Advertisement", Advertisement.class);
    entityTypes.put("ODataDemo.Category", Category.class);
    entityTypes.put("ODataDemo.Person", Person.class);
    entityTypes.put("ODataDemo.Supplier", Supplier.class);
    entityTypes.put("ODataDemo.FeaturedProduct",
        FeaturedProduct.class);
    complexTypes.put("ODataDemo.Address", Address.class);
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
