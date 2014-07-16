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
package org.apache.olingo.fit.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class OAuth2InInterceptor extends AbstractPhaseInterceptor<Message> {

  private static final OAuthClientUtils.Consumer OAUTH2_CONSUMER =
          new OAuthClientUtils.Consumer(OAuth2Provider.CLIENT_ID, OAuth2Provider.CLIENT_SECRET);

  public OAuth2InInterceptor() {
    super(Phase.PRE_INVOKE);
  }

  @Override
  public void handleMessage(final Message message) throws Fault {
    final String requestURL = (String) message.get(Message.REQUEST_URL);
    if (requestURL.contains("V40/OAuth2.svc")) {
      @SuppressWarnings("unchecked")
      final Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
      final List<String> oauth2CodeHeader = headers.get(OAuth2Provider.OAUTH2_CODE_HEADER);
      if (oauth2CodeHeader == null || oauth2CodeHeader.isEmpty()) {
        message.put(AbstractHTTPDestination.REQUEST_REDIRECTED, Boolean.TRUE);

        final HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
        try {
          final String authorizationServiceURI =
                  StringUtils.substringBefore(requestURL, "V40/OAuth2.svc") + "oauth/authorize";

          final URI authorizationURI = OAuthClientUtils.getAuthorizationURI(
                  authorizationServiceURI,
                  OAuth2Provider.CLIENT_ID,
                  OAuth2Provider.REDIRECT_URI,
                  null,
                  null);
          response.addHeader("Location", authorizationURI.toASCIIString());
          response.sendError(303);
        } catch (Exception e) {
          throw new Fault(e);
        }
      } else {
        try {
          final JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
          bean.setAddress(StringUtils.substringBefore(requestURL, "V40/OAuth2.svc") + "oauth/token");
          bean.setUsername("odatajclient");
          bean.setPassword("odatajclient");
          final WebClient accessTokenService = bean.createWebClient().
                  type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).
                  accept(MediaType.APPLICATION_JSON_TYPE);

          final AuthorizationCodeGrant codeGrant = new AuthorizationCodeGrant(oauth2CodeHeader.get(0));
          final ClientAccessToken accessToken =
                  OAuthClientUtils.getAccessToken(accessTokenService, OAUTH2_CONSUMER, codeGrant);
          if (accessToken == null) {
            throw new WebApplicationException("No OAuth2 access token");
          }
        } catch (OAuthServiceException e) {
          throw new Fault(e);
        }
      }
    }
  }
}
