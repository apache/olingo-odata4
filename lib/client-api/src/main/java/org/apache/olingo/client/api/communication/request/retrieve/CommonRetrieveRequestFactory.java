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

import java.io.Serializable;
import java.net.URI;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;

/**
 * OData request factory class.
 */
public interface CommonRetrieveRequestFactory extends Serializable {

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
   * @param <T> concrete ODataEntitySet implementation.
   * @param uri request URI.
   * @return new {@link ODataEntitySetRequest} instance.
   */
  <T extends CommonODataEntitySet> ODataEntitySetRequest<T> getEntitySetRequest(URI uri);

  /**
   * Gets a uri request returning a set of one or more OData entities.
   * <br/>
   * Returned request gives the possibility to consume entities iterating on them without parsing and loading in memory
   * the entire entity set.
   *
   * @param <ES> concreate ODataEntitySet implementation.
   * @param <E> concrete ODataEntity implementation.
   * @param uri request URI.
   * @return new {@link ODataEntitySetIteratorRequest} instance.
   */
  <ES extends CommonODataEntitySet, E extends CommonODataEntity>
          ODataEntitySetIteratorRequest<ES, E> getEntitySetIteratorRequest(URI uri);

  /**
   * Gets a uri request returning a single OData entity.
   *
   * @param <T> concrete ODataEntity implementation.
   * @param uri request URI.
   * @return new {@link ODataEntityRequest} instance.
   */
  <T extends CommonODataEntity> ODataEntityRequest<T> getEntityRequest(URI uri);

  /**
   * Gets a uri request returning a single OData entity property.
   *
   * @param <T> concrete ODataProperty implementation.
   * @param uri request URI.
   * @return new {@link ODataPropertyRequest} instance.
   */
  <T extends CommonODataProperty> ODataPropertyRequest<T> getPropertyRequest(URI uri);

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
   * Implements a raw request request without specifying any return type.
   *
   * @param uri request URI.
   * @return new {@link ODataRawRequest} instance.
   */
  ODataRawRequest getRawRequest(URI uri);
}
