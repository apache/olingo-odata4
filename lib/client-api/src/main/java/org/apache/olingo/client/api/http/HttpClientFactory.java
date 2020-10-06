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
package org.apache.olingo.client.api.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpMethod;

import java.io.IOException;
import java.net.URI;

/**
 * Interface used by ODataRequest implementations to instantiate HttpClient.
 */
public interface HttpClientFactory {

    HttpClient create(HttpMethod method, URI uri);

    default void close(HttpResponse httpResponse) {
        if (httpResponse instanceof CloseableHttpResponse) {
            try {
                ((CloseableHttpResponse) httpResponse).close();
            } catch (IOException e) {
                throw new ODataRuntimeException("Unable to close response: " + httpResponse, e);
            }
        }
    }

    void close(HttpClient httpClient);
}
