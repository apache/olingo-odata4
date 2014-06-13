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

import org.apache.olingo.commons.api.format.ODataFormat;


public final class ODataClientFactory {

  public static org.apache.olingo.client.api.v3.ODataClient getV3() {
    return new org.apache.olingo.client.core.v3.ODataClientImpl();
  }

  public static org.apache.olingo.client.api.v3.EdmEnabledODataClient getEdmEnabledV3(final String serviceRoot) {
    final org.apache.olingo.client.api.v3.EdmEnabledODataClient instance =
            new org.apache.olingo.client.core.v3.EdmEnabledODataClientImpl(serviceRoot);
    instance.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return instance;
  }

  public static org.apache.olingo.client.api.v4.ODataClient getV4() {
    return new org.apache.olingo.client.core.v4.ODataClientImpl();
  }

  public static org.apache.olingo.client.api.v4.EdmEnabledODataClient getEdmEnabledV4(final String serviceRoot) {
    final org.apache.olingo.client.api.v4.EdmEnabledODataClient instance =
            new org.apache.olingo.client.core.v4.EdmEnabledODataClientImpl(serviceRoot);
    instance.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return instance;
  }

  private ODataClientFactory() {
    // empty constructory for static utility class
  }
}
