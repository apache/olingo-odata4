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

import java.net.URI;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Implementation for working with NTLM Authentication via embedded HttpClient features.
 * <br/>
 * External NTLM engine such as <a href="http://jcifs.samba.org/">JCIFS</a> library developed by the
 * <a href="http://www.samba.org/">Samba</a> project as a part of their Windows interoperability suite of programs.
 * <br/>
 * See also http://hc.apache.org/httpcomponents-client-ga/ntlm.html#Using_Samba_JCIFS_as_an_alternative_NTLM_engine.
 * @see NTCredentials
 */
public class NTLMAuthHttpClientFactory extends DefaultHttpClientFactory {

  private final String username;

  private final String password;

  private final String workstation;

  private final String domain;

  public NTLMAuthHttpClientFactory(final String username, final String password,
          final String workstation, final String domain) {

    this.username = username;
    this.password = password;
    this.workstation = workstation;
    this.domain = domain;
  }

  @Override
  public CloseableHttpClient create(final HttpMethod method, final URI uri) {
      BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(
              new AuthScope(uri.getHost(), uri.getPort()),
              new UsernamePasswordCredentials(username, password.toCharArray())  // 5.x requires char[]
      );

      return HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
  }
}
