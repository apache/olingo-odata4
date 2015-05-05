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
package org.apache.olingo.commons.api.edm;

import java.util.List;

/**
 * A csdl schema element
 */
public interface EdmSchema extends EdmAnnotatable {

  /**
   * @return the namespace for this schema
   */
  String getNamespace();

  /**
   * @return the alias for this schema. May be null.
   */
  String getAlias();

  /**
   * @return all enum types for this schema
   */
  List<EdmEnumType> getEnumTypes();

  /**
   * @return all entity types for this schema
   */
  List<EdmEntityType> getEntityTypes();

  /**
   * @return all complex types for this schema
   */
  List<EdmComplexType> getComplexTypes();

  /**
   * @return all actions for this schema
   */
  List<EdmAction> getActions();

  /**
   * @return all functions for this schema
   */
  List<EdmFunction> getFunctions();

  /**
   * @return all {@link EdmTypeDefinition} for this schema.
   */
  List<EdmTypeDefinition> getTypeDefinitions();

  /**
   * @return all {@link EdmTerm} for this schema.
   */
  List<EdmTerm> getTerms();

  /**
   * @return all {@link EdmAnnotations} for this schema.
   */
  List<EdmAnnotations> getAnnotationGroups();

  /**
   * @return the entity container for this schema. May be null.
   */
  EdmEntityContainer getEntityContainer();

}
