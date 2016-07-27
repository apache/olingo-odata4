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

import org.apache.olingo.commons.api.data.AbstractEntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.ServiceMetadata;

/** EDM-assisted serializer */
public interface EdmAssistedSerializer {

  /**
   * Writes entity-collection data into an InputStream.
   * Information from the EDM is used in addition to information from the data and preferred,
   * but the serializer works without any EDM information as well.
   * Linked data is always written as expanded items (so closed reference loops have to be avoided).
   * @param metadata             metadata for the service
   * @param referencedEntityType the {@link EdmEntityType} or <code>null</code> if not available
   * @param entityCollection     the data of the entity collection
   * @param options              options for the serializer
   */
  SerializerResult entityCollection(ServiceMetadata metadata, EdmEntityType referencedEntityType,
      AbstractEntityCollection entityCollection, EdmAssistedSerializerOptions options) throws SerializerException;
}
