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

import java.net.URI;

import jakarta.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.grants.refresh.RefreshTokenGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.olingo.client.core.http.AbstractOAuth2HttpClientFactory;
import org.apache.olingo.client.core.http.OAuth2Exception;
import org.apache.olingo.fit.rest.OAuth2Provider;
import org.apache.cxf.rs.security.oauth2.client.Consumer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Test helper providing OAuth2 flow using CXF client utils and HttpClient5.
 */
public class CXFOAuth2HttpClientFactory extends AbstractOAuth2HttpClientFactory {

    private static final Consumer OAUTH2_CONSUMER =
            new Consumer(OAuth2Provider.CLIENT_ID, OAuth2Provider.CLIENT_SECRET);

    private ClientAccessToken accessToken;
    private String accessTokenHeader;

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

    protected boolean isInited() throws OAuth2Exception {
        return accessToken != null;
    }

    protected void init() throws OAuth2Exception {
        final URI authURI = OAuthClientUtils.getAuthorizationURI(
                oauth2GrantServiceURI.toASCIIString(),
                OAuth2Provider.CLIENT_ID,
                OAuth2Provider.REDIRECT_URI,
                null,
                "foo bar");

        JsonNode oAuthAuthorizationData = null;
        String authenticityCookie = null;

        // Step 1: fetch login/authorization info
        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet method = new HttpGet(authURI);
            method.addHeader("Authorization", "Basic "
                    + Base64.encodeBase64String("odatajclient:odatajclient".getBytes()));
            final ClassicHttpResponse response = httpClient.execute(method);

            // 2. Pull out OAuth2 authorization data and "authenticity" cookie (CXF specific)
            oAuthAuthorizationData = new XmlMapper().readTree(EntityUtils.toString(response.getEntity()));

            final Header setCookieHeader = response.getFirstHeader("Set-Cookie");
            if (setCookieHeader == null) {
                // Some test/deployment environments may not return a Set-Cookie header.
                // Instead of failing the whole OAuth flow early, continue without the cookie.
                authenticityCookie = null;
            } else {
                authenticityCookie = setCookieHeader.getValue();
            }
        } catch (Exception e) {
            throw new OAuth2Exception(e);
        }

        String code = null;
        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final URI location = new URIBuilder(oAuthAuthorizationData.get("replyTo").asText())
                    .addParameter("session_authenticity_token", oAuthAuthorizationData.get("authenticityToken").
                            asText())
                    .addParameter("client_id", oAuthAuthorizationData.get("clientId").asText())
                    .addParameter("redirect_uri", oAuthAuthorizationData.get("redirectUri").asText())
                    .addParameter("oauthDecision", "allow").addParameter("scope", "foo bar").build();

            final HttpGet method = new HttpGet(location);
            method.addHeader("Authorization",
                    "Basic " + Base64.encodeBase64String("odatajclient:odatajclient".getBytes()));
            if (authenticityCookie != null) {
                method.addHeader("Cookie", authenticityCookie);
            }

            final ClassicHttpResponse response = httpClient.execute(method);

            final Header locationHeader = response.getFirstHeader("Location");
            if (response.getCode() != 303 || locationHeader == null) {
                throw new IllegalStateException("OAuth flow is broken");
            }

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

    protected void accessToken(final CloseableHttpClient client) throws OAuth2Exception {
        // compute and store Authorization header value for use by the base class interceptor
        this.accessTokenHeader = OAuthClientUtils.createAuthorizationHeader(accessToken);
    }

    protected void refreshToken(final CloseableHttpClient client) throws OAuth2Exception {
        final String refreshToken = accessToken.getRefreshToken();
        if (refreshToken == null) {
            throw new OAuth2Exception("No OAuth2 refresh token");
        }

        // refresh the token
        try {
            accessToken = OAuthClientUtils.getAccessToken(getAccessTokenService(), OAUTH2_CONSUMER,
                    new RefreshTokenGrant(refreshToken));
            this.accessTokenHeader = OAuthClientUtils.createAuthorizationHeader(accessToken);
        } catch (OAuthServiceException e) {
            throw new OAuth2Exception(e);
        }
    }

    protected String getAuthorizationHeader() {
        return accessTokenHeader;
    }
}
