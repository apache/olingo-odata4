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

/**
 * The different types of representations that form the body of either the
 * OData request or the OData response, primarily used for content negotiation.
 */
public enum RepresentationType {
  /** service document */
  SERVICE,
  /** metadata document */
  METADATA,
  /** batch request or response */
  BATCH,
  /** error document */
  ERROR,
  /** single entity */
  ENTITY,
  /** collection of entities (entity set) */
  COLLECTION_ENTITY,
  /** single primitive-type instance */
  PRIMITIVE,
  /** collection of primitive-type instances */
  COLLECTION_PRIMITIVE,
  /** single complex-type instance */
  COMPLEX,
  /** collection of complex-type instances */
  COLLECTION_COMPLEX,
  /** differences */
  DIFFERENCES,
  /** media entity */
  MEDIA,
  /** binary-type instance */
  BINARY,
  /** single reference */
  REFERENCE,
  /** collection of references */
  COLLECTION_REFERENCE,
  /** textual raw value of a primitive-type instance (except binary) */
  VALUE,
  /** count of instances */
  COUNT,
  /** parameters of an action */
  ACTION_PARAMETERS
}
