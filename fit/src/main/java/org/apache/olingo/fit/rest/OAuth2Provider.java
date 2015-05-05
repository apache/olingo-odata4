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

import java.util.Collections;
import java.util.List;

import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;

public class OAuth2Provider implements AuthorizationCodeDataProvider {

  public static final String CLIENT_ID = "odataOAuth2SVC";

  public static final String CLIENT_SECRET = "1234567890";

  public static final String REDIRECT_URI = "/stub/StaticService/V40/OAuth2.svc/";

  private Client client;

  private ServerAuthorizationCodeGrant grant;

  private ServerAccessToken token;

  @Override
  public Client getClient(final String string) throws OAuthServiceException {
    if (client == null) {
      client = new Client(CLIENT_ID, CLIENT_SECRET, true);
      client.getRedirectUris().add(REDIRECT_URI);
    }
    return client;
  }

  @Override
  public ServerAccessToken getPreauthorizedToken(
      final Client client, final List<String> list, final UserSubject us, final String string)
      throws OAuthServiceException {

    return null;
  }

  @Override
  public List<OAuthPermission> convertScopeToPermissions(final Client client, final List<String> list) {
    return Collections.singletonList(new OAuthPermission());
  }

  @Override
  public ServerAuthorizationCodeGrant createCodeGrant(final AuthorizationCodeRegistration acr)
      throws OAuthServiceException {

    grant = new ServerAuthorizationCodeGrant(client, 3600L);
    grant.setRedirectUri(acr.getRedirectUri());
    grant.setSubject(acr.getSubject());
    final List<String> scope = acr.getApprovedScope().isEmpty()
        ? acr.getRequestedScope()
        : acr.getApprovedScope();
        grant.setApprovedScopes(scope);

        return grant;
  }

  @Override
  public ServerAuthorizationCodeGrant removeCodeGrant(final String code) throws OAuthServiceException {
    return grant == null || !grant.getCode().equals(code)
        ? null
        : grant;
  }

  @Override
  public ServerAccessToken createAccessToken(final AccessTokenRegistration atr) throws OAuthServiceException {
    token = new BearerAccessToken(atr.getClient(), 3600L);

    final List<String> scope = atr.getApprovedScope().isEmpty()
        ? atr.getRequestedScope()
        : atr.getApprovedScope();
        token.setScopes(convertScopeToPermissions(atr.getClient(), scope));
        token.setSubject(atr.getSubject());
        token.setGrantType(atr.getGrantType());

        return token;
  }

  @Override
  public ServerAccessToken getAccessToken(final String tokenId) throws OAuthServiceException {
    return token == null || token.getTokenKey().equals(tokenId) ? token : null;
  }

  @Override
  public ServerAccessToken refreshAccessToken(
      final Client client, final String string, final List<String> list)
      throws OAuthServiceException {

    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void removeAccessToken(final ServerAccessToken sat) throws OAuthServiceException {
    if (token != null && token.getTokenKey().equals(sat.getTokenKey())) {
      token = null;
    }
  }

  @Override
  public void revokeToken(final Client client, final String string, final String string1) throws OAuthServiceException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
