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

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.jose.jwt.JoseJwtConsumer;
import org.apache.cxf.rs.security.oauth2.filters.OAuthRequestFilter;

@Provider
public class OAuth2RequestFilter extends OAuthRequestFilter {

  @Inject
  public OAuth2RequestFilter(JoseJwtConsumer joseJwtConsumer, OAuth2Provider oAuth2Provider,
                             MessageContext messageContext) {
    super.setJwtTokenConsumer(joseJwtConsumer);
    super.setDataProvider(oAuth2Provider);
    this.setMessageContext(messageContext);

  }
  @Override
  public void filter(final ContainerRequestContext context) {
    final String svcName =
        StringUtils.substringBefore(StringUtils.substringAfter(context.getUriInfo().getPath(), "/"), "/");
    if ("OAuth2.svc".equals(svcName)) {
      super.filter(context);
    }
  }
}
