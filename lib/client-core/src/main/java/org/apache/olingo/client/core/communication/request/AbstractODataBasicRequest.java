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
package org.apache.olingo.client.core.communication.request;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataBasicRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Basic request abstract implementation.
 *
 * @param <T> OData response type corresponding to the request implementation.
 */
public abstract class AbstractODataBasicRequest<T extends ODataResponse>
    extends AbstractODataRequest implements ODataBasicRequest<T> {

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  private static final byte[] CRLF = {13, 10};

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param uri OData request URI.
   */
  public AbstractODataBasicRequest(final ODataClient odataClient, final HttpMethod method, final URI uri) {
    super(odataClient, method, uri);
  }

  @Override
  public void setFormat(final ContentType contentType) {
    if (contentType != null) {
      final String formatString = contentType.toContentTypeString();
      setAccept(formatString);
      setContentType(formatString);
    }
  }

  @Override
  public final Future<T> asyncExecute() {
    return odataClient.getConfiguration().getExecutor().submit(new Callable<T>() {
      @Override
      public T call() throws Exception { //NOSONAR
        return execute();
      }
    });
  }

  /**
   * Gets payload as an InputStream.
   *
   * @return InputStream for entire payload.
   */
  public abstract InputStream getPayload();

  /**
   * Serializes the full request into the given batch request.
   *
   * @param req destination batch request.
   */
  public void batch(final ODataBatchRequest req) {
    batch(req, null);
  }

  /**
   * Serializes the full request into the given batch request.
   * <p>
   * This method have to be used to serialize a changeset item with the specified contentId.
   *
   * @param req destination batch request.
   * @param contentId contentId of the changeset item.
   */
  public void batch(final ODataBatchRequest req, final String contentId) {
    try {
      req.rawAppend(toByteArray());
      if (StringUtils.isNotBlank(contentId)) {
        req.rawAppend((ODataBatchConstants.CHANGESET_CONTENT_ID_NAME + ": " + contentId).getBytes(DEFAULT_CHARSET));
        req.rawAppend(CRLF);
      }
      req.rawAppend(CRLF);

      final InputStream payload = getPayload();
      if (payload != null) {
        req.rawAppend(IOUtils.toByteArray(getPayload()));
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
