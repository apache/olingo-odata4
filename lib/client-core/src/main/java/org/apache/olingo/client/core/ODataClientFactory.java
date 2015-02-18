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

import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.v4.EdmEnabledODataClientImpl;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ODataFormat;

public final class ODataClientFactory {

  public static ODataClient getV4() {
    return new org.apache.olingo.client.core.v4.ODataClientImpl();
  }

  public static EdmEnabledODataClient getEdmEnabledV4(final String serviceRoot) {
    return getEdmEnabledV4(serviceRoot, null, null);
  }

  public static EdmEnabledODataClient getEdmEnabledV4(
          final String serviceRoot, final Edm edm, final String metadataETag) {

    final EdmEnabledODataClient instance =
            new EdmEnabledODataClientImpl(serviceRoot, edm, metadataETag);
    instance.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return instance;
  }

  private ODataClientFactory() {
    // empty constructory for static utility class
  }
}
