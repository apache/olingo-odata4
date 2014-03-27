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

import java.net.URI;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.v3.ODataLinkCollectionRequest;
import org.apache.olingo.client.api.communication.request.retrieve.v3.RetrieveRequestFactory;
import org.apache.olingo.client.core.communication.request.retrieve.AbstractRetrieveRequestFactory;

public class RetrieveRequestFactoryImpl extends AbstractRetrieveRequestFactory
        implements RetrieveRequestFactory {

  private static final long serialVersionUID = 6602745001042802479L;

  public RetrieveRequestFactoryImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public XMLMetadataRequest getXMLMetadataRequest(final String serviceRoot) {
    return new XMLMetadataRequestImpl(((ODataClient) client),
            client.getURIBuilder(serviceRoot).appendMetadataSegment().build());
  }

  @Override
  public ODataLinkCollectionRequest getLinkCollectionRequest(final URI targetURI, final String linkName) {
    return new ODataLinkCollectionRequestImpl((ODataClient) client, targetURI, linkName);
  }
}
