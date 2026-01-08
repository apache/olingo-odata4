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
package org.apache.olingo.client.core.android.http;

import java.net.URI;
import java.io.IOException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.olingo.client.core.http.AbstractHttpClientFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Android-specific HTTP client factory. On non-Android platforms this factory is not usable;
 * create() will throw UnsupportedOperationException to make this explicit at runtime.
 */
public class AndroidHttpClientFactory extends AbstractHttpClientFactory {

  @Override
  public HttpClient create(final HttpMethod method, final URI uri) {
    // This factory is only meant for Android runtime where a compatible HTTP client exists.
    throw new UnsupportedOperationException("AndroidHttpClientFactory is only available on Android runtime");
  }

  @Override
  public void close(final CloseableHttpClient httpClient) throws IOException {
    if (httpClient != null) {
      httpClient.close();
    }
  }
}
