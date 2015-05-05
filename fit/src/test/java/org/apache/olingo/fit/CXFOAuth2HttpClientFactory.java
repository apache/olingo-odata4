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
package org.apache.olingo.fit;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.olingo.client.core.http.AbstractOAuth2HttpClientFactory;
import org.apache.olingo.client.core.http.OAuth2Exception;
import org.apache.olingo.fit.rest.OAuth2Provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class CXFOAuth2HttpClientFactory extends AbstractOAuth2HttpClientFactory {

  private static final OAuthClientUtils.Consumer OAUTH2_CONSUMER =
      new OAuthClientUtils.Consumer(OAuth2Provider.CLIENT_ID, OAuth2Provider.CLIENT_SECRET);

  private ClientAccessToken accessToken;

  public CXFOAuth2HttpClientFactory(final URI oauth2GrantServiceURI, final URI oauth2TokenServiceURI) {
    super(oauth2GrantServiceURI, oauth2TokenServiceURI);
  }

  private WebClient getAccessTokenService() {
    final JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
    bean.setAddress(oauth2TokenServiceURI.toASCIIString());
    bean.setUsername("odatajclient");
    bean.setPassword("odatajclient");
    return bean.createWebClient().
        type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE);
  }

  @Override
  protected boolean isInited() throws OAuth2Exception {
    return accessToken != null;
  }

  @Override
  protected void init() throws OAuth2Exception {
    final URI authURI = OAuthClientUtils.getAuthorizationURI(
        oauth2GrantServiceURI.toASCIIString(),
        OAuth2Provider.CLIENT_ID,
        OAuth2Provider.REDIRECT_URI,
        null,
        null);

    // Disable automatic redirects handling
    final HttpParams params = new BasicHttpParams();
    params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
    final DefaultHttpClient httpClient = new DefaultHttpClient(params);

    JsonNode oAuthAuthorizationData = null;
    String authenticityCookie = null;
    try {
      // 1. Need to (basic) authenticate against the OAuth2 service
      final HttpGet method = new HttpGet(authURI);
      method.addHeader("Authorization", "Basic " + Base64.encodeBase64String("odatajclient:odatajclient".getBytes()));
      final HttpResponse response = httpClient.execute(method);

      // 2. Pull out OAuth2 authorization data and "authenticity" cookie (CXF specific)
      oAuthAuthorizationData = new XmlMapper().readTree(EntityUtils.toString(response.getEntity()));

      final Header setCookieHeader = response.getFirstHeader("Set-Cookie");
      if (setCookieHeader == null) {
        throw new IllegalStateException("OAuth flow is broken");
      }
      authenticityCookie = setCookieHeader.getValue();
    } catch (Exception e) {
      throw new OAuth2Exception(e);
    }

    String code = null;
    try {
      // 3. Submit the HTTP form for allowing access to the application
      final URI location = new URIBuilder(oAuthAuthorizationData.get("replyTo").asText()).
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

      // 4. Get the authorization code value out of this last redirect
      code = StringUtils.substringAfterLast(locationHeader.getValue(), "=");

      EntityUtils.consumeQuietly(response.getEntity());
    } catch (Exception e) {
      throw new OAuth2Exception(e);
    }

    // 5. Obtain the access token
    try {
      accessToken = OAuthClientUtils.getAccessToken(
          getAccessTokenService(), OAUTH2_CONSUMER, new AuthorizationCodeGrant(code));
    } catch (OAuthServiceException e) {
      throw new OAuth2Exception(e);
    }

    if (accessToken == null) {
      throw new OAuth2Exception("No OAuth2 access token");
    }
  }

  @Override
  protected void accessToken(final DefaultHttpClient client) throws OAuth2Exception {
    client.addRequestInterceptor(new HttpRequestInterceptor() {

      @Override
      public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        request.removeHeaders(HttpHeaders.AUTHORIZATION);
        request.addHeader(HttpHeaders.AUTHORIZATION, OAuthClientUtils.createAuthorizationHeader(accessToken));
      }
    });
  }

  @Override
  protected void refreshToken(final DefaultHttpClient client) throws OAuth2Exception {
    final String refreshToken = accessToken.getRefreshToken();
    if (refreshToken == null) {
      throw new OAuth2Exception("No OAuth2 refresh token");
    }

    // refresh the token
    try {
      accessToken = OAuthClientUtils.getAccessToken(
          getAccessTokenService(), OAUTH2_CONSUMER, new RefreshTokenGrant(refreshToken));
    } catch (OAuthServiceException e) {
      throw new OAuth2Exception(e);
    }
  }

}
