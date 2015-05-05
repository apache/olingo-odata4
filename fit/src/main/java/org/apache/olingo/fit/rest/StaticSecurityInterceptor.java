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

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.security.SecurityContext;
import org.apache.geronimo.mail.util.Base64;

public class StaticSecurityInterceptor extends AbstractPhaseInterceptor<Message> {

  private static final String AUTHORIZATION_PROPERTY = "Authorization";

  private static final String AUTHENTICATION_SCHEME = "Basic";

  public StaticSecurityInterceptor() {
    super(Phase.PRE_PROTOCOL);
  }

  @Override
  public void handleMessage(final Message message) throws Fault {
    final SecurityContext sc = message.get(SecurityContext.class);
    if (sc == null || sc.getUserPrincipal() == null) {
      @SuppressWarnings("unchecked")
      final Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

      final List<String> auth = headers.get(AUTHORIZATION_PROPERTY);
      if (auth == null || auth.isEmpty()) {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      final String encodedUserPassword = auth.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
      final String usernameAndPassword = new String(Base64.decode(encodedUserPassword));

      // Split username and password tokens
      final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
      final String username = tokenizer.nextToken();
      final String password = tokenizer.nextToken();

      if (!"odatajclient".equals(username) || !"odatajclient".equals(password)) {
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      final SecurityContext newSc = new SecurityContext() {

        @Override
        public Principal getUserPrincipal() {
          return new SimplePrincipal("odatajclient");
        }

        @Override
        public boolean isUserInRole(final String role) {
          return false;
        }
      };
      message.put(SecurityContext.class, newSc);
    }
  }

}
