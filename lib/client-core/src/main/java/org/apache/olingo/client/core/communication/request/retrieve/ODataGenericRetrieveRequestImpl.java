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
package org.apache.olingo.client.core.communication.request.retrieve;

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataGenericRetrieveRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ObjectWrapper;
import org.apache.olingo.client.core.communication.response.ODataResponseImpl;

/**
 * This class implements a generic OData retrieve query request.
 */
public class ODataGenericRetrieveRequestImpl extends ODataRawRequestImpl implements ODataGenericRetrieveRequest {

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri query request.
   */
  public ODataGenericRetrieveRequestImpl(final ODataClient odataClient, final URI uri) {
    super(odataClient, uri);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void setFormat(final String format) {
    setAccept(format);
    setContentType(format);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataRetrieveResponse<ObjectWrapper> execute() {
    return new ODataGenericResponseImpl(httpClient, doExecute());
  }

  /**
   * Query response implementation about a generic query request.
   */
  protected class ODataGenericResponseImpl extends ODataResponseImpl implements ODataRetrieveResponse<ObjectWrapper> {

    /**
     * Retrieved object wrapper.
     */
    private ObjectWrapper obj = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataGenericResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataGenericResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ObjectWrapper getBody() {
      if (obj == null) {
        try {
          obj = new ObjectWrapper(odataClient.getReader(), getRawResponse(), getContentType());
        } finally {
          this.close();
        }
      }
      return obj;
    }
  }
}
