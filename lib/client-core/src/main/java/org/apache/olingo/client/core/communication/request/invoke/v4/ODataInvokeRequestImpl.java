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
package org.apache.olingo.client.core.communication.request.invoke.v4;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.invoke.AbstractODataInvokeRequest;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.domain.ODataInvokeResult;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataFormat;

public class ODataInvokeRequestImpl<T extends ODataInvokeResult> extends AbstractODataInvokeRequest<T> {

  private ODataFormat format;

  public ODataInvokeRequestImpl(final CommonODataClient<?> odataClient, final Class<T> reference,
          final HttpMethod method, final URI uri) {

    super(odataClient, reference, method, uri);
  }

  @Override
  public void setFormat(final ODataFormat format) {
    super.setFormat(format);
    this.format = format;
  }

  @Override
  protected ODataFormat getPOSTParameterFormat() {
    return format == null ? getDefaultFormat() : format;
  }

  @Override
  protected URI buildGETURI() {
    String baseURI = this.uri.toASCIIString();
    if (baseURI.endsWith("()")) {
      baseURI = baseURI.substring(0, baseURI.length() - 2);
    }

    final StringBuilder inlineParams = new StringBuilder();
    for (Map.Entry<String, ODataValue> param : parameters.entrySet()) {
      inlineParams.append(param.getKey()).append("=");

      Object value = null;
      if (param.getValue().isPrimitive()) {
        value = param.getValue().asPrimitive().toValue();
      } else if (param.getValue().isComplex()) {
        value = param.getValue().asComplex().asJavaMap();
      } else if (param.getValue().isCollection()) {
        value = param.getValue().asCollection().asJavaCollection();
      } else if (param.getValue() instanceof org.apache.olingo.commons.api.domain.v4.ODataValue
              && ((org.apache.olingo.commons.api.domain.v4.ODataValue) param.getValue()).isEnum()) {

        value = ((org.apache.olingo.commons.api.domain.v4.ODataValue) param.getValue()).asEnum().toString();
      }

      inlineParams.append(URIUtils.escape(odataClient.getServiceVersion(), value)).append(',');
    }
    inlineParams.deleteCharAt(inlineParams.length() - 1);

    try {
      return URI.create(baseURI + "(" + URLEncoder.encode(inlineParams.toString(), Constants.UTF8) + ")");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException("While adding GET parameters", e);
    }
  }
}
