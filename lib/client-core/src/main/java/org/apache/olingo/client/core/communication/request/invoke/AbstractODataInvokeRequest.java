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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataInvokeResult;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.serialization.ODataSerializerException;

/**
 * This class implements an OData invoke operation request.
 */
public abstract class AbstractODataInvokeRequest<T extends ODataInvokeResult>
        extends AbstractODataBasicRequest<ODataInvokeResponse<T>, ODataPubFormat>
        implements ODataInvokeRequest<T>, ODataBatchableRequest {

  private final Class<T> reference;

  /**
   * Function parameters.
   */
  protected Map<String, ODataValue> parameters;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param reference reference class for invoke result
   * @param method HTTP method of the request.
   * @param uri URI that identifies the operation.
   */
  public AbstractODataInvokeRequest(
          final CommonODataClient<?> odataClient,
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

  private String getActualFormat(final ODataPubFormat format) {
    return (CommonODataProperty.class.isAssignableFrom(reference) && format == ODataPubFormat.ATOM)
            ? ODataFormat.XML.toString(odataClient.getServiceVersion())
            : format.toString(odataClient.getServiceVersion());
  }

  @Override
  public void setFormat(final ODataPubFormat format) {
    final String _format = getActualFormat(format);
    setAccept(_format);
    setContentType(_format);
  }

  protected abstract ODataPubFormat getPOSTParameterFormat();

  @Override
  protected InputStream getPayload() {
    if (!this.parameters.isEmpty() && this.method == HttpMethod.POST) {
      // Additional, non-binding parameters MUST be sent as JSON
      final CommonODataEntity tmp = odataClient.getObjectFactory().newEntity(null);
      for (Map.Entry<String, ODataValue> param : parameters.entrySet()) {
        CommonODataProperty property = null;

        if (param.getValue().isPrimitive()) {
          property = odataClient.getObjectFactory().
                  newPrimitiveProperty(param.getKey(), param.getValue().asPrimitive());
        } else if (param.getValue().isComplex()) {
          property = odataClient.getObjectFactory().
                  newComplexProperty(param.getKey(), param.getValue().asComplex());
        } else if (param.getValue().isCollection()) {
          property = odataClient.getObjectFactory().
                  newCollectionProperty(param.getKey(), param.getValue().asCollection());
        } else if (param.getValue() instanceof org.apache.olingo.commons.api.domain.v4.ODataValue
                && ((org.apache.olingo.commons.api.domain.v4.ODataValue) param.getValue()).isEnum()) {

          property = ((ODataClient) odataClient).getObjectFactory().
                  newEnumProperty(param.getKey(),
                          ((org.apache.olingo.commons.api.domain.v4.ODataValue) param.getValue()).asEnum());
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

  protected abstract URI buildGETURI();

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataInvokeResponse<T> execute() {
    final InputStream input = getPayload();

    if (!this.parameters.isEmpty()) {
      if (this.method == HttpMethod.GET) {
        ((HttpRequestBase) this.request).setURI(buildGETURI());
      } else if (this.method == HttpMethod.POST) {
        ((HttpPost) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

        setContentType(getActualFormat(getPOSTParameterFormat()));
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
      super();
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
    public T getBody() {
      if (invokeResult == null) {
        try {
          if (ODataNoContent.class.isAssignableFrom(reference)) {
            invokeResult = reference.cast(new ODataNoContent());
          } else {
             // avoid getContent() twice:IllegalStateException: Content has been consumed
             InputStream responseStream = this.payload == null ? res.getEntity().getContent() : this.payload;
             if (CommonODataEntitySet.class.isAssignableFrom(reference)) {
	        invokeResult = reference.cast(odataClient.getReader().readEntitySet(responseStream,
	            ODataPubFormat.fromString(getContentType())));
	      } else if (CommonODataEntity.class.isAssignableFrom(reference)) {
	        invokeResult = reference.cast(odataClient.getReader().readEntity(responseStream,
	            ODataPubFormat.fromString(getContentType())));
	      } else if (CommonODataProperty.class.isAssignableFrom(reference)) {
	        invokeResult = reference.cast(odataClient.getReader().readProperty(responseStream,
	            ODataFormat.fromString(getContentType())));
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
