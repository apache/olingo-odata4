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

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.commons.api.format.ContentType;

public abstract class AbstractMetadataRequestImpl<V> extends AbstractODataRetrieveRequest<V> {

  public AbstractMetadataRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
    super.setAccept(ContentType.APPLICATION_XML.toContentTypeString());
    super.setContentType(ContentType.APPLICATION_XML.toContentTypeString());
  }

  @Override
  public ContentType getDefaultFormat() {
    return ContentType.APPLICATION_XML;
  }

  @Override
  public ODataRequest setAccept(final String value) {
    // do nothing: Accept is application/xml
    return this;
  }

  @Override
  public ODataRequest setContentType(final String value) {
    // do nothing: Content-Type is application/xml
    return this;
  }

}
