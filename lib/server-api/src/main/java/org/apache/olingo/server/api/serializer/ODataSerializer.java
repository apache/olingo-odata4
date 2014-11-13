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
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriParameter;
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
   * @param serviceMetadata the metadata information for the service
   */
  InputStream metadataDocument(ServiceMetadata serviceMetadata) throws SerializerException;

  /**
   * Writes an ODataError into an InputStream.
   * @param error the main error
   * @return inputStream containing the OData-formatted error
   */
  InputStream error(ODataServerError error) throws SerializerException;

  /**
   * Writes entity-collection data into an InputStream.
   * @param entityType the {@link EdmEntityType}
   * @param entitySet  the data of the entity set
   * @param options    options for the serializer
   */
  InputStream entityCollection(EdmEntityType entityType, EntitySet entitySet,
      EntityCollectionSerializerOptions options) throws SerializerException;

  /**
   * Writes entity data into an InputStream.
   * @param entityType the {@link EdmEntityType}
   * @param entity     the data of the entity
   * @param options    options for the serializer
   */
  InputStream entity(EdmEntityType entityType, Entity entity, EntitySerializerOptions options)
      throws SerializerException;

  /**
   * Writes primitive-type instance data into an InputStream.
   * @param type     primitive type
   * @param property property value
   * @param options options for the serializer
   */
  InputStream primitive(EdmPrimitiveType type, Property property, PrimitiveSerializerOptions options)
      throws SerializerException;

  /**
   * Writes complex-type instance data into an InputStream.
   * @param type     complex type
   * @param property property value
   * @param options options for the serializer
   */
  InputStream complex(EdmComplexType type, Property property, ComplexSerializerOptions options)
      throws SerializerException;

  /**
   * Writes data of a collection of primitive-type instances into an InputStream.
   * @param type     primitive type
   * @param property property value
   * @param options options for the serializer
   */
  InputStream primitiveCollection(EdmPrimitiveType type, Property property, PrimitiveSerializerOptions options)
      throws SerializerException;

  /**
   * Writes data of a collection of complex-type instances into an InputStream.
   * @param type     complex type
   * @param property property value
   * @param options options for the serializer
   */
  InputStream complexCollection(EdmComplexType type, Property property, ComplexSerializerOptions options)
      throws SerializerException;

  /**
   * Builds the select-list part of a {@link org.apache.olingo.commons.api.data.ContextURL ContextURL}.
   * @param type   the {@link EdmStructuredType}
   * @param expand the $expand option
   * @param select the $select option
   * @return a String with the select list
   */
  String buildContextURLSelectList(EdmStructuredType type, ExpandOption expand, SelectOption select)
      throws SerializerException;

  /**
   * Builds the key-predicate part of a {@link org.apache.olingo.commons.api.data.ContextURL ContextURL}.
   * @param keys the keys as a list of {@link UriParameter} instances
   * @return a String with the key predicate
   */
  String buildContextURLKeyPredicate(List<UriParameter> keys) throws SerializerException;
}
