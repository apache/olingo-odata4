/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.olingo.fit.proxy.v3.primitivekeys;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.AbstractService;

public class Service<C extends CommonEdmEnabledODataClient<?>> extends AbstractService<C> {

  private static final Map<String, Service<?>> SERVICES = new ConcurrentHashMap<String, Service<?>>();

  @SuppressWarnings("unchecked")
  private static <C extends CommonEdmEnabledODataClient<?>> Service<C> getInstance(
          final C client, final boolean transactional) {

    if (!SERVICES.containsKey(client.getServiceRoot())) {
      client.getConfiguration().setDefaultPubFormat(ODataFormat.JSON_FULL_METADATA);
      final Service<C> instance = new Service<C>(client, transactional);
      SERVICES.put(client.getServiceRoot(), instance);
    }

    return (Service<C>) SERVICES.get(client.getServiceRoot());
  }

  /**
   * Gives an OData 3.0 instance for given service root, operating in transactions (with batch requests).
   *
   * @param serviceRoot OData service root
   * @return OData 3.0 instance for given service root, operating in transactions (with batch requests)
   */
  public static Service<org.apache.olingo.client.api.v3.EdmEnabledODataClient> getV3(
          final String serviceRoot) {

    return getV3(serviceRoot, true);
  }

  /**
   * Gives an OData 3.0 instance for given service root.
   *
   * @param serviceRoot OData service root
   * @param transactional whether operating in transactions (with batch requests) or not
   * @return OData 3.0 instance for given service root
   */
  public static Service<org.apache.olingo.client.api.v3.EdmEnabledODataClient> getV3(
          final String serviceRoot, final boolean transactional) {

    return getInstance(ODataClientFactory.getEdmEnabledV3(serviceRoot), transactional);
  }

  /**
   * Gives an OData 4.0 instance for given service root, operating in transactions (with batch requests).
   *
   * @param serviceRoot OData service root
   * @return OData 4.0 instance for given service root, operating in transactions (with batch requests)
   */
  public static Service<org.apache.olingo.client.api.v4.EdmEnabledODataClient> getV4(
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
  public static Service<org.apache.olingo.client.api.v4.EdmEnabledODataClient> getV4(
          final String serviceRoot, final boolean transactional) {

    return getInstance(ODataClientFactory.getEdmEnabledV4(serviceRoot), transactional);
  }
  private final Map<String, Class<?>> complexTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<?>> enumTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<? extends AbstractTerm>> terms = new HashMap<String, Class<? extends AbstractTerm>>();

  public Service(final CommonEdmEnabledODataClient<?> client, final boolean transactional) {
    super(client, transactional);

    //CHECKSTYLE:OFF (Maven checkstyle)
    //CHECKSTYLE:ON (Maven checkstyle)
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
