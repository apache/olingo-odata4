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
package org.apache.olingo.server.api.serializer;

import java.io.InputStream;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

/** OData serializer */
public interface ODataSerializer {

  /** The default character set is UTF-8. */
  public static final String DEFAULT_CHARSET = "UTF-8";

  /**
   * Writes the service document into an InputStream.
   * @param edm         the Entity Data Model
   * @param serviceRoot the service-root URI of this OData service 
   */
  InputStream serviceDocument(Edm edm, String serviceRoot) throws SerializerException;

  /**
   * Writes the metadata document into an InputStream.
   * @param edm the Entity Data Model
   */
  InputStream metadataDocument(Edm edm) throws SerializerException;

  /**
   * Writes entity data into an InputStream.
   * @param edmEntitySet the {@link EdmEntitySet}
   * @param entity       the data of the entity
   * @param options      options for the serializer
   */
  InputStream entity(EdmEntitySet edmEntitySet, Entity entity, ODataSerializerOptions options)
      throws SerializerException;

  /**
   * Writes entity-set data into an InputStream.
   * @param edmEntitySet the {@link EdmEntitySet}
   * @param entitySet    the data of the entity set
   * @param options      options for the serializer
   */
  InputStream entitySet(EdmEntitySet edmEntitySet, EntitySet entitySet, ODataSerializerOptions options)
      throws SerializerException;

  /**
   * Writes an ODataError into an InputStream.
   * @param error the main error
   * @return inputStream containing the OData-formatted error
   */
  InputStream error(ODataServerError error) throws SerializerException;

  /**
   * Builds the select-list part of a {@link org.apache.olingo.commons.api.data.ContextURL ContextURL}.
   * @param edmEntitySet the Entity Set
   * @param expand       the $expand option
   * @param select       the $select option
   * @return a String with the select list
   */
  String buildContextURLSelectList(EdmEntitySet edmEntitySet, ExpandOption expand, SelectOption select)
      throws SerializerException;
}
