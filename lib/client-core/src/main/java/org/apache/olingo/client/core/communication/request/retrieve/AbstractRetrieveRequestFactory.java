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
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.request.retrieve.CommonRetrieveRequestFactory;
import org.apache.olingo.client.core.uri.URIUtils;

public abstract class AbstractRetrieveRequestFactory implements CommonRetrieveRequestFactory {

  private static final long serialVersionUID = -111683263158803362L;

  protected final CommonODataClient<?> client;

  protected AbstractRetrieveRequestFactory(final CommonODataClient<?> client) {
    this.client = client;
  }

  @Override
  public ODataValueRequest getValueRequest(final URI uri) {
    return new ODataValueRequestImpl(client, uri);
  }

  @Override
  public ODataValueRequest getPropertyValueRequest(final URI uri) {
    return getValueRequest(URIUtils.addValueSegment(uri));
  }

  @Override
  public ODataMediaRequest getMediaRequest(final URI uri) {
    return new ODataMediaRequestImpl(client, uri);
  }

  @Override
  public ODataMediaRequest getMediaEntityRequest(final URI uri) {
    return getMediaRequest(URIUtils.addValueSegment(uri));
  }

  @Override
  public ODataRawRequest getRawRequest(final URI uri) {
    return new ODataRawRequestImpl(client, uri);
  }

  @Override
  public EdmMetadataRequest getMetadataRequest(final String serviceRoot) {
    return new EdmMetadataRequestImpl(client, serviceRoot,
            client.newURIBuilder(serviceRoot).appendMetadataSegment().build());
  }

  @Override
  public ODataServiceDocumentRequest getServiceDocumentRequest(final String serviceRoot) {
    return new ODataServiceDocumentRequestImpl(client,
            StringUtils.isNotBlank(serviceRoot) && serviceRoot.endsWith("/")
            ? client.newURIBuilder(serviceRoot).build()
            : client.newURIBuilder(serviceRoot + "/").build());
  }
}
