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
package org.apache.olingo.client.core.http;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpClientFactory implements HttpClientFactory {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractHttpClientFactory.class);

  protected static final String USER_AGENT;

  static {
    final StringBuilder userAgent = new StringBuilder("Apache-Olingo");

    final InputStream input = AbstractHttpClientFactory.class.getResourceAsStream("/client.properties");
    try {
      final Properties prop = new Properties();
      prop.load(input);
      userAgent.append('/').append(prop.getProperty("version"));
    } catch (Exception e) {
      LOG.warn("Could not get Apache Olingo version", e);
    } finally {
      IOUtils.closeQuietly(input);
    }

    USER_AGENT = userAgent.toString();
  }
}
