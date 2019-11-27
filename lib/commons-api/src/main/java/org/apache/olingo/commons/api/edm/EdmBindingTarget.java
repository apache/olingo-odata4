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
 * Entity Sets or Singletons can be bound to each other using a navigation property binding so an
 * {@link EdmBindingTarget} can either be an {@link EdmEntitySet} or an {@link EdmSingleton}.
 */
public interface EdmBindingTarget extends EdmNamed, EdmAnnotatable, EdmMappable {

  /**
   * Returns a human readable title or null if not set.
   * @return a human readable title or null
   */
  String getTitle();
  
  /**
   * Returns the target for a given path.
   *
   * @param path path for which the target is returned
   * @return {@link EdmBindingTarget}
   */
  EdmBindingTarget getRelatedBindingTarget(String path);

  /**
   * @return all navigation property bindings
   */
  List<EdmNavigationPropertyBinding> getNavigationPropertyBindings();

  /**
   * Returns the entity container this target is contained in.
   *
   * @return {@link EdmEntityContainer}
   */
  EdmEntityContainer getEntityContainer();

  /**
   * Get the entity type.
   *
   * @return {@link EdmEntityType}
   */
  EdmEntityType getEntityType();
  
  /**
   * Get the entity type with annotations defined in external file.
   *
   * @return {@link EdmEntityType}
   */
  EdmEntityType getEntityTypeWithAnnotations();
}
