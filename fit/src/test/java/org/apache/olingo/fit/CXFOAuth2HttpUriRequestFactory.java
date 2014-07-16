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
package org.apache.olingo.fit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.net.URI;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.olingo.client.core.http.AbstractOAuth2HttpUriRequestFactory;
import org.apache.olingo.client.core.http.OAuth2Exception;
import org.apache.olingo.fit.rest.OAuth2Provider;

public class CXFOAuth2HttpUriRequestFactory extends AbstractOAuth2HttpUriRequestFactory {

  private String code;

  public CXFOAuth2HttpUriRequestFactory(final URI redirectURI) {
    super(redirectURI);
  }

  @Override
  protected boolean isInited() {
    return code != null;
  }

  @Override
  protected void init() throws OAuth2Exception {
    // 1. Disable automatic redirects handling
    final HttpParams params = new BasicHttpParams();
    params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
    final DefaultHttpClient httpClient = new DefaultHttpClient(params);

    // 2. Try to access the redirect URI without any special header: get redirected to the OAuth2 service
    URI location = null;
    try {
      final HttpResponse response = httpClient.execute(new HttpGet(redirectURI));

      final Header locationHeader = response.getFirstHeader("Location");
      if (response.getStatusLine().getStatusCode() != 303 || locationHeader == null) {
        throw new IllegalStateException("OAuth flow is broken");
      }

      location = new URI(locationHeader.getValue());

      EntityUtils.consumeQuietly(response.getEntity());
    } catch (Exception e) {
      throw new OAuth2Exception(e);
    }

    JsonNode oAuthAuthorizationData = null;
    String authenticityCookie = null;
    try {
      // 3. Need to (basic) authenticate against the OAuth2 service
      final HttpGet method = new HttpGet(location);
      method.addHeader("Authorization", "Basic " + Base64.encodeBase64String("odatajclient:odatajclient".getBytes()));
      final HttpResponse response = httpClient.execute(method);

      // 4. Pull out OAuth2 authorization data and "authenticity" cookie (CXF specific)
      oAuthAuthorizationData = new XmlMapper().readTree(EntityUtils.toString(response.getEntity()));

      final Header setCookieHeader = response.getFirstHeader("Set-Cookie");
      if (setCookieHeader == null) {
        throw new IllegalStateException("OAuth flow is broken");
      }
      authenticityCookie = setCookieHeader.getValue();
    } catch (Exception e) {
      throw new OAuth2Exception(e);
    }

    try {
      // 5. Submit the HTTP form for allowing access to the application
      location = new URIBuilder(oAuthAuthorizationData.get("replyTo").asText()).
              addParameter("session_authenticity_token", oAuthAuthorizationData.get("authenticityToken").asText()).
              addParameter("client_id", oAuthAuthorizationData.get("clientId").asText()).
              addParameter("redirect_uri", oAuthAuthorizationData.get("redirectUri").asText()).
              addParameter("oauthDecision", "allow").
              build();
      final HttpGet method = new HttpGet(location);
      method.addHeader("Authorization", "Basic " + Base64.encodeBase64String("odatajclient:odatajclient".getBytes()));
      method.addHeader("Cookie", authenticityCookie);

      final HttpResponse response = httpClient.execute(method);

      final Header locationHeader = response.getFirstHeader("Location");
      if (response.getStatusLine().getStatusCode() != 303 || locationHeader == null) {
        throw new IllegalStateException("OAuth flow is broken");
      }

      // 6. Finally get the code value out of this last redirect
      code = StringUtils.substringAfterLast(locationHeader.getValue(), "=");

      EntityUtils.consumeQuietly(response.getEntity());
    } catch (Exception e) {
      throw new OAuth2Exception(e);
    }
  }

  @Override
  protected void sign(final HttpUriRequest request) {
    request.addHeader(OAuth2Provider.OAUTH2_CODE_HEADER, code);
  }

}
