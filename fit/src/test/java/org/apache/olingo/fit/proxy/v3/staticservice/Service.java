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
package org.apache.olingo.fit.proxy.v3.staticservice;

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
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.ComplexToCategory", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ComplexToCategory.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.ComplexWithAllPrimitiveTypes", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ComplexWithAllPrimitiveTypes.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases.class);
    complexTypes.put("Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone.class);
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
