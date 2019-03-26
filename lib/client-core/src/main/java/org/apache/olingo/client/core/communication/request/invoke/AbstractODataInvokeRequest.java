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
package org.apache.olingo.client.core.communication.request.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.invoke.ClientNoContent;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.api.serialization.ODataSerializerException;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * This class implements an OData invoke operation request.
 */
public abstract class AbstractODataInvokeRequest<T extends ClientInvokeResult>
    extends AbstractODataBasicRequest<ODataInvokeResponse<T>>
    implements ODataInvokeRequest<T>, ODataBatchableRequest {

  protected final Class<T> reference;

  /**
   * Function parameters.
   */
  protected Map<String, ClientValue> parameters;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param reference reference class for invoke result
   * @param method HTTP method of the request.
   * @param uri URI that identifies the operation.
   */
  public AbstractODataInvokeRequest(
      final ODataClient odataClient,
      final Class<T> reference,
      final HttpMethod method,
      final URI uri) {

    super(odataClient, method, uri);

    this.reference = reference;
    this.parameters = new LinkedHashMap<String, ClientValue>();
  }

  @Override
  public void setParameters(final Map<String, ClientValue> parameters) {
    this.parameters.clear();
    if (parameters != null && !parameters.isEmpty()) {
      this.parameters.putAll(parameters);
    }
  }

  @Override
  public ContentType getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  private String getActualFormat(final ContentType contentType) {
    return (ClientProperty.class.isAssignableFrom(reference)
        && (contentType.isCompatible(ContentType.APPLICATION_ATOM_SVC)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)) ?
        ContentType.APPLICATION_XML : contentType).toContentTypeString();
  }

  @Override
  public void setFormat(final ContentType contentType) {
    final String _contentType = getActualFormat(contentType);
    setAccept(_contentType);
    setContentType(_contentType);
  }

  protected abstract ContentType getPOSTParameterFormat();

  @Override
  public InputStream getPayload() {
    if (!this.parameters.isEmpty() && this.method == HttpMethod.POST) {
      // Additional, non-binding parameters MUST be sent as JSON
      final ClientEntity tmp = odataClient.getObjectFactory().newEntity(null);
      for (Map.Entry<String, ClientValue> param : parameters.entrySet()) {
        ClientProperty property = null;

        if (param.getValue().isPrimitive()) {
          property = odataClient.getObjectFactory().
              newPrimitiveProperty(param.getKey(), param.getValue().asPrimitive());
        } else if (param.getValue().isComplex()) {
          property = odataClient.getObjectFactory().
              newComplexProperty(param.getKey(), param.getValue().asComplex());
        } else if (param.getValue().isCollection()) {
          property = odataClient.getObjectFactory().
              newCollectionProperty(param.getKey(), param.getValue().asCollection());
        } else if (param.getValue().isEnum()) {
          property = odataClient.getObjectFactory().newEnumProperty(param.getKey(), param.getValue().asEnum());
        }

        if (property != null) {
          odataClient.getBinder().add(tmp, property);
        }
      }

      try {
        return odataClient.getWriter().writeEntity(tmp, getPOSTParameterFormat());
      } catch (final ODataSerializerException e) {
        throw new IllegalArgumentException(e);
      }
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
        ((HttpRequestBase) this.request).setURI(
            URIUtils.buildFunctionInvokeURI(this.uri, parameters));
      } else if (this.method == HttpMethod.POST) {
        ((HttpPost) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

        setContentType(getActualFormat(getPOSTParameterFormat()));
      }
    }

    try {
      return new ODataInvokeResponseImpl(odataClient, httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * Response class about an ODataInvokeRequest.
   */
  protected class ODataInvokeResponseImpl extends AbstractODataResponse implements ODataInvokeResponse<T> {

    private T invokeResult = null;

    private ODataInvokeResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
        final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getBody() {
      if (invokeResult == null) {
        try {
          if (ClientNoContent.class.isAssignableFrom(reference)) {
            invokeResult = reference.cast(new ClientNoContent());
          } else {
            // avoid getContent() twice:IllegalStateException: Content has been consumed
            final InputStream responseStream = this.payload == null ? res.getEntity().getContent() : this.payload;
            if (ClientEntitySet.class.isAssignableFrom(reference)) {
              invokeResult = reference.cast(odataClient.getReader().readEntitySet(responseStream,
                  ContentType.parse(getContentType())));
            } else if (ClientEntity.class.isAssignableFrom(reference)) {
              invokeResult = reference.cast(odataClient.getReader().readEntity(responseStream,
                  ContentType.parse(getContentType())));
            } else if (ClientProperty.class.isAssignableFrom(reference)) {
              invokeResult = reference.cast(odataClient.getReader().readProperty(responseStream,
                  ContentType.parse(getContentType())));
            }
          }
        } catch (IOException e) {
          throw new HttpClientException(e);
        } catch (final ODataDeserializerException e) {
          throw new IllegalArgumentException(e);
        } finally {
          this.close();
        }
      }
      return invokeResult;
    }
  }
}
