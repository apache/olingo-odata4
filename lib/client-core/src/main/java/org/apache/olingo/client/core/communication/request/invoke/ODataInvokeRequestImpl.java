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
package org.apache.olingo.client.core.communication.request.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataInvokeResult;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This class implements an OData invoke operation request.
 */
public class ODataInvokeRequestImpl<T extends ODataInvokeResult>
        extends AbstractODataBasicRequest<ODataInvokeResponse<T>, ODataPubFormat>
        implements ODataInvokeRequest<T>, ODataBatchableRequest {

  private final Class<T> reference;

  /**
   * Function parameters.
   */
  private Map<String, ODataValue> parameters;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param reference reference class for invoke result
   * @param method HTTP method of the request.
   * @param uri URI that identifies the operation.
   */
  public ODataInvokeRequestImpl(
          final CommonODataClient odataClient,
          final Class<T> reference,
          final HttpMethod method,
          final URI uri) {

    super(odataClient, ODataPubFormat.class, method, uri);

    this.reference = reference;
    this.parameters = new LinkedHashMap<String, ODataValue>();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void setParameters(final Map<String, ODataValue> parameters) {
    this.parameters.clear();
    if (parameters != null && !parameters.isEmpty()) {
      this.parameters.putAll(parameters);
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void setFormat(final ODataPubFormat format) {
    final String _format = (reference.isAssignableFrom(ODataProperty.class) && format == ODataPubFormat.ATOM)
            ? ODataFormat.XML.toString()
            : format.toString(odataClient.getServiceVersion());
    setAccept(_format);
    setContentType(_format);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected InputStream getPayload() {
    if (!this.parameters.isEmpty() && this.method == HttpMethod.POST) {
      // Additional, non-binding parameters MUST be sent as JSON
      final ODataEntity tmp = odataClient.getObjectFactory().newEntity("");
      for (Map.Entry<String, ODataValue> param : parameters.entrySet()) {
        ODataProperty property = null;

        if (param.getValue().isPrimitive()) {
          property = odataClient.getObjectFactory().
                  newPrimitiveProperty(param.getKey(), param.getValue().asPrimitive());
        } else if (param.getValue().isComplex()) {
          property = odataClient.getObjectFactory().
                  newComplexProperty(param.getKey(), param.getValue().asComplex());
        } else if (param.getValue().isCollection()) {
          property = odataClient.getObjectFactory().
                  newCollectionProperty(param.getKey(), param.getValue().asCollection());
        }

        if (property != null) {
          tmp.getProperties().add(property);
        }
      }

      return odataClient.getWriter().writeEntity(tmp, ODataPubFormat.JSON, false);
    }

    return null;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataInvokeResponse<T> execute() {
    final InputStream input = getPayload();

    if (!this.parameters.isEmpty()) {
      if (this.method == HttpMethod.GET) {
        final URIBuilder uriBuilder = new URIBuilder(this.uri);
        for (Map.Entry<String, ODataValue> param : parameters.entrySet()) {
          if (!param.getValue().isPrimitive()) {
            throw new IllegalArgumentException("Only primitive values can be passed via GET");
          }

          uriBuilder.addParameter(param.getKey(), param.getValue().toString());
        }
        try {
          ((HttpRequestBase) this.request).setURI(uriBuilder.build());
        } catch (URISyntaxException e) {
          throw new IllegalArgumentException("While adding GET parameters", e);
        }
      } else if (this.method == HttpMethod.POST) {
        ((HttpPost) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

        setContentType(ODataPubFormat.JSON.toString(odataClient.getServiceVersion()));
      }
    }

    try {
      return new ODataInvokeResponseImpl(httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * Response class about an ODataInvokeRequest.
   */
  protected class ODataInvokeResponseImpl extends AbstractODataResponse implements ODataInvokeResponse<T> {

    private T invokeResult = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataInvokeResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataInvokeResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getBody() {
      if (invokeResult == null) {
        if (reference.isAssignableFrom(ODataNoContent.class)) {
          invokeResult = (T) new ODataNoContent();
        }

        try {
          if (reference.isAssignableFrom(ODataEntitySet.class)) {
            invokeResult = (T) odataClient.getReader().readEntitySet(res.getEntity().getContent(),
                    ODataPubFormat.fromString(getContentType()));
          }
          if (reference.isAssignableFrom(ODataEntity.class)) {
            invokeResult = (T) odataClient.getReader().readEntity(res.getEntity().getContent(),
                    ODataPubFormat.fromString(getContentType()));
          }
          if (reference.isAssignableFrom(ODataProperty.class)) {
            invokeResult = (T) odataClient.getReader().readProperty(res.getEntity().getContent(),
                    ODataFormat.fromString(getContentType()));
          }
        } catch (IOException e) {
          throw new HttpClientException(e);
        } finally {
          this.close();
        }
      }
      return invokeResult;
    }
  }
}
