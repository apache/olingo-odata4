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

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
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
  public DefaultHttpClient create(final HttpMethod method, final URI uri) {
    final DefaultHttpClient httpclient = super.create(method, uri);

    final CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(AuthScope.ANY,
            new NTCredentials(username, password, workstation, domain));

    httpclient.setCredentialsProvider(credsProvider);

    return httpclient;
  }
}
