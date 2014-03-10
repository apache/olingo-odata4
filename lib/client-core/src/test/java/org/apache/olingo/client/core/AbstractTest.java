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

import java.util.Locale;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.ODataV3Client;
import org.apache.olingo.client.api.ODataV4Client;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.core.ODataClientFactory;
import org.junit.BeforeClass;

public abstract class AbstractTest {

  protected static ODataV3Client v3Client;

  protected static ODataV4Client v4Client;

  protected abstract ODataClient getClient();

  /**
   * This is needed for correct number handling (Double, for example).
   */
  @BeforeClass
  public static void setEnglishLocale() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeClass
  public static void setClientInstances() {
    v3Client = ODataClientFactory.getV3();
    v4Client = ODataClientFactory.getV4();
  }

  protected String getSuffix(final ODataPubFormat format) {
    return format == ODataPubFormat.ATOM ? "xml" : "json";
  }

  protected String getSuffix(final ODataFormat format) {
    return format == ODataFormat.XML ? "xml" : "json";
  }
}
