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
package org.apache.olingo.client.api.communication.request.retrieve;

import java.net.URI;

import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientSingleton;

public interface RetrieveRequestFactory {

  /**
   * Gets a metadata request instance.
   * <br/>
   * Compared to {@link #getMetadataRequest(java.lang.String)}, this method returns a request instance for fetching
   * low-level metadata representation.
   *
   * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the data
   * service.
   * @return new {@link XMLMetadataRequest} instance.
   */
  XMLMetadataRequest getXMLMetadataRequest(String serviceRoot);

  /**
   * Gets a metadata request instance.
   *
   * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the data
   * service.
   * @return new {@link EdmMetadataRequest} instance.
   */
  EdmMetadataRequest getMetadataRequest(String serviceRoot);

  /**
   * Gets a service document request instance.
   *
   * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the data
   * service.
   * @return new {@link ODataServiceDocumentRequest} instance.
   */
  ODataServiceDocumentRequest getServiceDocumentRequest(String serviceRoot);

  /**
   * Gets a uri request returning a set of one or more OData entities.
   *
   * @param uri request URI.
   * @return new {@link ODataEntitySetRequest} instance.
   */
  ODataEntitySetRequest<ClientEntitySet> getEntitySetRequest(URI uri);

  /**
   * Gets a uri request returning a set of one or more OData entities.
   * <br/>
   * Returned request gives the possibility to consume entities iterating on them without parsing and loading in memory
   * the entire entity set.
   *
   * @param uri request URI.
   * @return new {@link ODataEntitySetIteratorRequest} instance.
   */
  ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> getEntitySetIteratorRequest(URI uri);

  /**
   * Gets a uri request returning a single OData entity.
   *
   * @param uri request URI.
   * @return new {@link ODataEntityRequest} instance.
   */
  ODataEntityRequest<ClientEntity> getEntityRequest(URI uri);

  /**
   * Gets a uri request returning a single OData entity property.
   *
   * @param uri request URI.
   * @return new {@link ODataPropertyRequest} instance.
   */
  ODataPropertyRequest<ClientProperty> getPropertyRequest(URI uri);
  
  /**
   * Gets a uri request returning a single OData entity property value.
   *
   * @param uri request URI.
   * @return new {@link ODataValueRequest} instance.
   */
  ODataValueRequest getPropertyValueRequest(URI uri);

  /**
   * Gets a uri request returning a single OData entity property value.
   *
   * @param uri request URI.
   * @return new {@link ODataValueRequest} instance.
   */
  ODataValueRequest getValueRequest(URI uri);

  /**
   * Gets a uri request returning a media stream.
   *
   * @param uri request URI.
   * @return new {@link ODataMediaRequest} instance.
   */
  ODataMediaRequest getMediaRequest(URI uri);
  
  /**
   * Gets a uri request returning a media entity.
   *
   * @param uri request URI.
   * @return new {@link ODataMediaRequest} instance.
   */
  ODataMediaRequest getMediaEntityRequest(URI uri);

  /**
   * Implements a raw request request without specifying any return type.
   *
   * @param uri request URI.
   * @return new {@link ODataRawRequest} instance.
   */
  ODataRawRequest getRawRequest(URI uri);

  ODataEntityRequest<ClientSingleton> getSingletonRequest(URI uri);

  ODataDeltaRequest getDeltaRequest(URI uri);
}
