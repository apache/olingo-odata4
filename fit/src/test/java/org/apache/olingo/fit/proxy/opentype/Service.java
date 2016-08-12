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
package org.apache.olingo.fit.proxy.opentype;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.fit.proxy.opentype.opentypesservice.types.AccountInfo;
import org.apache.olingo.fit.proxy.opentype.opentypesservice.types.Color;
import org.apache.olingo.fit.proxy.opentype.opentypesservice.types.ContactDetails;
import org.apache.olingo.fit.proxy.opentype.opentypesservice.types.IndexedRow;
import org.apache.olingo.fit.proxy.opentype.opentypesservice.types.Row;
import org.apache.olingo.fit.proxy.opentype.opentypesservice.types.RowIndex;

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
    entityTypes.put("Microsoft.Test.OData.Services.OpenTypesServiceV4.Row",
        Row.class);
    entityTypes
    .put(
        "Microsoft.Test.OData.Services.OpenTypesServiceV4.IndexedRow",
        IndexedRow.class);
    entityTypes.put("Microsoft.Test.OData.Services.OpenTypesServiceV4.RowIndex",
        RowIndex.class);
    complexTypes
    .put(
        "Microsoft.Test.OData.Services.OpenTypesServiceV4.AccountInfo",
        AccountInfo.class);
    complexTypes
    .put(
        "Microsoft.Test.OData.Services.OpenTypesServiceV4.ContactDetails",
        ContactDetails.class);
    enumTypes.put("Microsoft.Test.OData.Services.OpenTypesServiceV4.Color",
        Color.class);
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
