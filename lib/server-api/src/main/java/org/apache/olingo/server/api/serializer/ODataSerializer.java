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

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;

/** OData serializer */
public interface ODataSerializer {

  /** The default character set is UTF-8. */
  public static final String DEFAULT_CHARSET = "UTF-8";

  /**
   * Writes the service document into an InputStream.
   * @param edm the Entity Data Model
   * @param serviceRoot the service-root URI of this OData service
   */
  SerializerResult serviceDocument(Edm edm, String serviceRoot) throws SerializerException;

  /**
   * Writes the metadata document into an InputStream.
   * @param serviceMetadata the metadata information for the service
   */
  SerializerResult metadataDocument(ServiceMetadata serviceMetadata) throws SerializerException;

  /**
   * Writes an ODataError into an InputStream.
   * @param error the main error
   * @return inputStream containing the OData-formatted error
   */
  SerializerResult error(ODataServerError error) throws SerializerException;

  /**
   * Writes entity-collection data into an InputStream.
   * @param metadata Metadata for the service
   * @param entityType the {@link EdmEntityType}
   * @param entitySet the data of the entity set
   * @param options options for the serializer
   */
  SerializerResult entityCollection(ServiceMetadata metadata, EdmEntityType entityType,
      EntityCollection entitySet, EntityCollectionSerializerOptions options) throws SerializerException;

  /**
   * Writes entity data into an InputStream.
   * @param metadata Metadata for the service
   * @param entityType the {@link EdmEntityType}
   * @param entity the data of the entity
   * @param options options for the serializer
   */
  SerializerResult entity(ServiceMetadata metadata, EdmEntityType entityType, Entity entity,
      EntitySerializerOptions options) throws SerializerException;

  /**
   * Writes primitive-type instance data into an InputStream.
   * @param type primitive type
   * @param property property value
   * @param options options for the serializer
   */
  SerializerResult primitive(EdmPrimitiveType type, Property property, PrimitiveSerializerOptions options)
      throws SerializerException;

  /**
   * Writes complex-type instance data into an InputStream.
   * @param metadata Metadata for the service
   * @param type complex type
   * @param property property value
   * @param options options for the serializer
   */
  SerializerResult complex(ServiceMetadata metadata, EdmComplexType type, Property property,
      ComplexSerializerOptions options) throws SerializerException;

  /**
   * Writes data of a collection of primitive-type instances into an InputStream.
   * @param type primitive type
   * @param property property value
   * @param options options for the serializer
   */
  SerializerResult primitiveCollection(EdmPrimitiveType type, Property property, PrimitiveSerializerOptions options)
      throws SerializerException;

  /**
   * Writes data of a collection of complex-type instances into an InputStream.
   * @param metadata Metadata for the service
   * @param type complex type
   * @param property property value
   * @param options options for the serializer
   */
  SerializerResult complexCollection(ServiceMetadata metadata, EdmComplexType type, Property property,
      ComplexSerializerOptions options) throws SerializerException;
  
  /**
   * Writes a single entity reference into an InputStream
   * 
   * @param metadata              Metadata for the service
   * @param edmEntitySet          {@link EdmEntitySet}
   * @param entity                data of the entity
   * @param contextUrl            {@link ContextURL}
   * @return Serialized           entity reference
   * @throws SerializerException
   */
  SerializerResult reference(ServiceMetadata metadata, EdmEntitySet edmEntitySet, Entity entity, 
      final ContextURL contextUrl) throws SerializerException;
  
  /**
   * Writes entity collection references into an InputStream
   * 
   * @param metadata              Metadata for the service
   * @param edmEntitySet          {@link EdmEntitySet}
   * @param entityCollection      data of the entity collection
   * @param contextURL 
   * @return Serialized           entity reference
   * @throws SerializerException
   */
  SerializerResult referenceCollection(ServiceMetadata metadata, EdmEntitySet edmEntitySet, 
      EntityCollection entityCollection, final ContextURL contextURL) throws SerializerException;
}
