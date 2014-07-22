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
package org.apache.olingo.client.core.communication.request.retrieve.v3;

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.v3.ODataLinkCollectionRequest;
import org.apache.olingo.client.api.communication.request.retrieve.v3.RetrieveRequestFactory;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.communication.request.retrieve.AbstractRetrieveRequestFactory;
import org.apache.olingo.client.core.communication.request.retrieve.ODataEntityRequestImpl;
import org.apache.olingo.client.core.communication.request.retrieve.ODataEntitySetIteratorRequestImpl;
import org.apache.olingo.client.core.communication.request.retrieve.ODataEntitySetRequestImpl;
import org.apache.olingo.client.core.communication.request.retrieve.ODataPropertyRequestImpl;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;

import java.net.URI;

@SuppressWarnings("unchecked")
public class RetrieveRequestFactoryImpl extends AbstractRetrieveRequestFactory implements RetrieveRequestFactory {

  public RetrieveRequestFactoryImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public XMLMetadataRequest getXMLMetadataRequest(final String serviceRoot) {
    return new XMLMetadataRequestImpl(((ODataClient) client),
            client.newURIBuilder(serviceRoot).appendMetadataSegment().build());
  }

  @Override
  public ODataLinkCollectionRequest getLinkCollectionRequest(final URI targetURI, final String linkName) {
    return new ODataLinkCollectionRequestImpl((ODataClient) client, targetURI, linkName);
  }

  @Override
  public ODataEntitySetRequest<ODataEntitySet> getEntitySetRequest(final URI uri) {
    return new ODataEntitySetRequestImpl<ODataEntitySet>(client, uri);
  }

  @Override
  public ODataEntitySetIteratorRequest<ODataEntitySet, ODataEntity> getEntitySetIteratorRequest(final URI uri) {
    return new ODataEntitySetIteratorRequestImpl<ODataEntitySet, ODataEntity>(client, uri);
  }

  @Override
  public ODataEntityRequest<ODataEntity> getEntityRequest(final URI uri) {
    return new ODataEntityRequestImpl<ODataEntity>(client, uri);
  }

  @Override
  public ODataPropertyRequest<ODataProperty> getPropertyRequest(final URI uri) {
    return new ODataPropertyRequestImpl<ODataProperty>(client, uri);
  }
}
