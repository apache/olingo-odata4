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
package org.apache.olingo.client.core;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;

public final class ODataClientFactory {

  public static ODataClient getClient() {
    return new ODataClientImpl();
  }

  public static EdmEnabledODataClient getEdmEnabledClient(final String serviceRoot) {
    return getEdmEnabledClient(serviceRoot, null, null, ContentType.JSON);
  }

  public static EdmEnabledODataClient getEdmEnabledClient(final String serviceRoot, ContentType contentType) {
    return getEdmEnabledClient(serviceRoot, null, null, contentType);
  }

  public static EdmEnabledODataClient getEdmEnabledClient(
          final String serviceRoot, final Edm edm, final String metadataETag) {
    return getEdmEnabledClient(serviceRoot, edm, metadataETag, ContentType.JSON);
  }

  
  public static EdmEnabledODataClient getEdmEnabledClient(
      final String serviceRoot, final Edm edm, final String metadataETag, ContentType contentType) {

    final EdmEnabledODataClient instance =
        new EdmEnabledODataClientImpl(serviceRoot, edm, metadataETag);
    instance.getConfiguration().setDefaultPubFormat(contentType);
    return instance;
  }  
  private ODataClientFactory() {
    // empty constructory for static utility class
  }
}
