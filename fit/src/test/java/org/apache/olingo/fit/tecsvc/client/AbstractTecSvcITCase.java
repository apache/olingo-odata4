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
package org.apache.olingo.fit.tecsvc.client;

import java.util.Collection;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;

public abstract class AbstractTecSvcITCase extends AbstractBaseTestITCase {

  protected static final String SERVICE_URI = TecSvcConst.BASE_URI + '/';
  protected static final String AUTH_URI = TecSvcConst.AUTH_URI + '/';
  protected static final String SERVICE_NAMESPACE = "olingo.odata.test1";
  protected static final String USERNAME = "odatajclient";
  protected static final String PASSWORD = "odatajclient";

  // Read-only tests can re-use the server session via the session cookie.
  // JUnit constructs a fresh instance for each test method, so we have to
  // use static storage; a thread-local variable seems safer as a simple static one. 
  private static ThreadLocal<String> cookieHeader = new ThreadLocal<String>();

  protected void saveCookieHeader(final ODataResponse response) {
    if (cookieHeader.get() == null) {
      final Collection<String> header = response.getHeader(HttpHeader.SET_COOKIE);
      if (header != null && !header.isEmpty()) {
        cookieHeader.set(header.iterator().next());
      }
    }
  }

  protected void setCookieHeader(ODataRequest request) {
    if (cookieHeader.get() != null) {
      request.addCustomHeader(HttpHeader.COOKIE, cookieHeader.get());
    }
  }

  protected abstract ContentType getContentType();

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(getContentType());
    return odata;
  }

  protected ODataClient getBasicAuthClient(String username, String password) {
    ODataClient odata = getClient();
    odata.getConfiguration()
            .setHttpClientFactory(new BasicAuthHttpClientFactory(username, password));
    odata.getConfiguration().setDefaultPubFormat(getContentType());
    return odata;
  }

  protected EdmEnabledODataClient getEdmEnabledClient() {
    return ODataClientFactory.getEdmEnabledClient(SERVICE_URI, getContentType());
  }

  protected ClientObjectFactory getFactory() {
    return getClient().getObjectFactory();
  }
}
