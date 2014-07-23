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
package org.apache.olingo.fit.proxy.v3.opentype;

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
    complexTypes.put("Microsoft.Test.OData.Services.OpenTypesServiceV3.ContactDetails", org.apache.olingo.fit.proxy.v3.opentype.microsoft.test.odata.services.opentypesservicev3.types.ContactDetails.class);
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
