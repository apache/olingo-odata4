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
package org.apache.olingo.client.core.communication.response.v4;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.communication.response.v4.AsyncResponse;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * Abstract representation of an OData response.
 */
public class AsyncResponseImpl extends AbstractODataResponse implements AsyncResponse {

  /**
   * Constructor.
   * <p>
   * Just to create response templates to be initialized from batch.
   */
  public AsyncResponseImpl() {
    super();
  }

  /**
   * Constructor.
   *
   * @param client HTTP client.
   * @param res HTTP response.
   */
  public AsyncResponseImpl(final HttpClient client, final HttpResponse res) {
    super(client, res);
  }
}
