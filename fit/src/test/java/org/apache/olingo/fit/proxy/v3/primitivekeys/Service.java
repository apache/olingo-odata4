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
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.AbstractService;

public class Service<C extends CommonEdmEnabledODataClient<?>> extends AbstractService<C> {

  //CHECKSTYLE:OFF (Maven checkstyle)
  private static final String COMPRESSED_METADATA = "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";
  //CHECKSTYLE:ON (Maven checkstyle)

  private static final Map<String, Service<?>> SERVICES = new ConcurrentHashMap<String, Service<?>>();

  @SuppressWarnings("unchecked")
  private static <C extends CommonEdmEnabledODataClient<?>> Service<C> getInstance(
          final ODataServiceVersion version, final String serviceRoot, final boolean transactional) {

    if (!SERVICES.containsKey(serviceRoot)) {
      final Service<C> instance = new Service<C>(COMPRESSED_METADATA, version, serviceRoot, transactional);
      SERVICES.put(serviceRoot, instance);
    }

    return (Service<C>) SERVICES.get(serviceRoot);
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

    return getInstance(ODataServiceVersion.V30, serviceRoot, transactional);
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

    return getInstance(ODataServiceVersion.V40, serviceRoot, transactional);
  }

  private final Map<String, Class<?>> complexTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<?>> enumTypes = new HashMap<String, Class<?>>();

  private final Map<String, Class<? extends AbstractTerm>> terms = new HashMap<String, Class<? extends AbstractTerm>>();

  public Service(final String compressedMetadata,
          final ODataServiceVersion version, final String serviceRoot, final boolean transactional) {

    super(compressedMetadata, version, serviceRoot, transactional);

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
