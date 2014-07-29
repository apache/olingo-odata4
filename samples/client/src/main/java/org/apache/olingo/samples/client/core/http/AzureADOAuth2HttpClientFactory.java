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
package org.apache.olingo.samples.client.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.olingo.client.core.http.AbstractOAuth2HttpClientFactory;
import org.apache.olingo.client.core.http.OAuth2Exception;

/**
 * Shows how to work with OAuth 2.0 native applications protected by Azure Active Directory.
 * <a href="http://msdn.microsoft.com/en-us/library/azure/dn645542.aspx">More information</a>.
 */
public class AzureADOAuth2HttpClientFactory extends AbstractOAuth2HttpClientFactory {

  private final String clientId;

  private final String redirectURI;

  private final String resourceURI;

  private final UsernamePasswordCredentials creds;

  private ObjectNode token;

  public AzureADOAuth2HttpClientFactory(final String authority, final String clientId,
          final String redirectURI, final String resourceURI, final UsernamePasswordCredentials creds) {

    super(URI.create(authority + "/oauth2/authorize"), URI.create(authority + "/oauth2/token"));
    this.clientId = clientId;
    this.redirectURI = redirectURI;
    this.resourceURI = resourceURI;
    this.creds = creds;
  }

  @Override
  protected boolean isInited() throws OAuth2Exception {
    return token != null;
  }

  private void fetchAccessToken(final DefaultHttpClient httpClient, final List<BasicNameValuePair> data) {
    token = null;

    InputStream tokenResponse = null;
    try {
      final HttpPost post = new HttpPost(oauth2TokenServiceURI);
      post.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));

      final HttpResponse response = httpClient.execute(post);

      tokenResponse = response.getEntity().getContent();
      token = (ObjectNode) new ObjectMapper().readTree(tokenResponse);
    } catch (Exception e) {
      throw new OAuth2Exception(e);
    } finally {
      IOUtils.closeQuietly(tokenResponse);
    }
  }

  @Override
  protected void init() throws OAuth2Exception {
    final DefaultHttpClient httpClient = wrapped.create(null, null);

    // 1. access the OAuth2 grant service (with authentication)
    String code = null;
    try {
      final URIBuilder builder = new URIBuilder(oauth2GrantServiceURI).
              addParameter("response_type", "code").
              addParameter("client_id", clientId).
              addParameter("redirect_uri", redirectURI);

      HttpResponse response = httpClient.execute(new HttpGet(builder.build()));

      final String loginPage = EntityUtils.toString(response.getEntity());

      String postURL = StringUtils.substringBefore(
              StringUtils.substringAfter(loginPage, "<form id=\"credentials\" method=\"post\" action=\""),
              "\">");
      final String ppsx = StringUtils.substringBefore(
              StringUtils.substringAfter(loginPage, "<input type=\"hidden\" id=\"PPSX\" name=\"PPSX\" value=\""),
              "\"/>");
      final String ppft = StringUtils.substringBefore(
              StringUtils.substringAfter(loginPage, "<input type=\"hidden\" name=\"PPFT\" id=\"i0327\" value=\""),
              "\"/>");

      List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
      data.add(new BasicNameValuePair("login", creds.getUserName()));
      data.add(new BasicNameValuePair("passwd", creds.getPassword()));
      data.add(new BasicNameValuePair("PPSX", ppsx));
      data.add(new BasicNameValuePair("PPFT", ppft));

      HttpPost post = new HttpPost(postURL);
      post.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));

      response = httpClient.execute(post);

      final String samlPage = EntityUtils.toString(response.getEntity());

      postURL = StringUtils.substringBefore(
              StringUtils.substringAfter(samlPage, "<form name=\"fmHF\" id=\"fmHF\" action=\""),
              "\" method=\"post\" target=\"_top\">");
      final String wctx = StringUtils.substringBefore(
              StringUtils.substringAfter(samlPage, "<input type=\"hidden\" name=\"wctx\" id=\"wctx\" value=\""),
              "\">");
      final String wresult = StringUtils.substringBefore(StringUtils.substringAfter(samlPage,
              "<input type=\"hidden\" name=\"wresult\" id=\"wresult\" value=\""), "\">");
      final String wa = StringUtils.substringBefore(
              StringUtils.substringAfter(samlPage, "<input type=\"hidden\" name=\"wa\" id=\"wa\" value=\""),
              "\">");

      data = new ArrayList<BasicNameValuePair>();
      data.add(new BasicNameValuePair("wctx", wctx));
      data.add(new BasicNameValuePair("wresult", wresult.replace("&quot;", "\"")));
      data.add(new BasicNameValuePair("wa", wa));

      post = new HttpPost(postURL);
      post.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));

      response = httpClient.execute(post);

      final Header locationHeader = response.getFirstHeader("Location");
      if (response.getStatusLine().getStatusCode() != 302 || locationHeader == null) {
        throw new OAuth2Exception("Unexpected response from server");
      }

      final String[] oauth2Info = StringUtils.split(
              StringUtils.substringAfter(locationHeader.getValue(), "?"), '&');
      code = StringUtils.substringAfter(oauth2Info[0], "=");

      EntityUtils.consume(response.getEntity());
    } catch (Exception e) {
      throw new OAuth2Exception(e);
    }

    if (code == null) {
      throw new OAuth2Exception("No OAuth2 grant");
    }

    // 2. ask the OAuth2 token service
    final List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
    data.add(new BasicNameValuePair("grant_type", "authorization_code"));
    data.add(new BasicNameValuePair("code", code));
    data.add(new BasicNameValuePair("client_id", clientId));
    data.add(new BasicNameValuePair("redirect_uri", redirectURI));
    data.add(new BasicNameValuePair("resource", resourceURI));

    fetchAccessToken(httpClient, data);

    if (token == null) {
      throw new OAuth2Exception("No OAuth2 access token");
    }
  }

  @Override
  protected void accessToken(final DefaultHttpClient client) throws OAuth2Exception {
    client.addRequestInterceptor(new HttpRequestInterceptor() {

      @Override
      public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        request.removeHeaders(HttpHeaders.AUTHORIZATION);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.get("access_token").asText());
      }
    });
  }

  @Override
  protected void refreshToken(final DefaultHttpClient client) throws OAuth2Exception {
    final List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
    data.add(new BasicNameValuePair("grant_type", "refresh_token"));
    data.add(new BasicNameValuePair("refresh_token", token.get("refresh_token").asText()));

    fetchAccessToken(wrapped.create(null, null), data);

    if (token == null) {
      throw new OAuth2Exception("No OAuth2 refresh token");
    }
  }

}
