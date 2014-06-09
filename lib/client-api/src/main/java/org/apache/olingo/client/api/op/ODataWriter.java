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
package org.apache.olingo.client.api.op;

import java.io.InputStream;
import java.util.Collection;

import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.api.op.ODataSerializerException;

/**
 * OData writer.
 * <br/>
 * Use this interface to serialize an OData request body.
 * <br/>
 * This interface provides method helpers to serialize a set of entities and a single entity as well.
 */
public interface ODataWriter {

  /**
   * Writes a collection of OData entities.
   *
   * @param entities entities to be serialized.
   * @param format serialization format.
   * @return stream of serialized objects.
   * @throws ODataSerializerException 
   */
  InputStream writeEntities(Collection<CommonODataEntity> entities, ODataPubFormat format)
      throws ODataSerializerException;

  /**
   * Serializes a single OData entity.
   *
   * @param entity entity to be serialized.
   * @param format serialization format.
   * @return stream of serialized object.
   * @throws ODataSerializerException 
   */
  InputStream writeEntity(CommonODataEntity entity, ODataPubFormat format)
      throws ODataSerializerException;

  /**
   * Writes a single OData entity property.
   *
   * @param property entity property to be serialized.
   * @param format serialization format.
   * @return stream of serialized object.
   * @throws ODataSerializerException 
   */
  InputStream writeProperty(CommonODataProperty property, ODataFormat format)
      throws ODataSerializerException;

  /**
   * Writes an OData link.
   *
   * @param link link to be serialized.
   * @param format serialization format.
   * @return stream of serialized object.
   * @throws ODataSerializerException 
   */
  InputStream writeLink(ODataLink link, ODataFormat format)
      throws ODataSerializerException;
}
