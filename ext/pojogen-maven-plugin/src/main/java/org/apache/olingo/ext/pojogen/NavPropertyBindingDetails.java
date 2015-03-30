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
package org.apache.olingo.ext.pojogen;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuredType;

public class NavPropertyBindingDetails {

  protected Edm edm;

  protected EdmSchema schema;

  protected EdmEntityContainer container;

  protected EdmBindingTarget entitySet;

  protected EdmStructuredType type;

  protected NavPropertyBindingDetails() {}

  public NavPropertyBindingDetails(final Edm edm, final EdmStructuredType type) {
    this.edm = edm;
    this.type = type;
    this.entitySet = getNavigationBindingDetails(type);
    this.container = this.entitySet.getEntityContainer();
    this.schema = edm.getSchema(container.getNamespace());
  }

  public NavPropertyBindingDetails(
      final Edm edm, final EdmStructuredType sourceType, final EdmNavigationProperty property) {
    this.edm = edm;
    this.entitySet = getNavigationBindingDetails(sourceType, property);
    this.container = this.entitySet.getEntityContainer();
    this.schema = edm.getSchema(container.getNamespace());
    this.type = entitySet.getEntityType();
  }

  private EdmBindingTarget getNavigationBindingDetails(final EdmStructuredType type) {
    EdmEntityContainer c = edm.getEntityContainer();
    if (c != null) {
      for (EdmEntitySet es : c.getEntitySets()) {
        if (es.getEntityType().getFullQualifiedName().equals(type.getFullQualifiedName())) {
          return es;
        }
      }

      for (EdmSingleton s : c.getSingletons()) {
        if (s.getEntityType().getFullQualifiedName().equals(type.getFullQualifiedName())) {
          return s;
        }
      }
    }

    throw new IllegalStateException("EntitySet for '" + type.getName() + "' not found");
  }

  private EdmBindingTarget getNavigationBindingDetails(
      final EdmStructuredType sourceType, final EdmNavigationProperty property) {

    EdmEntityContainer c = edm.getEntityContainer();
    if (c != null) {
      for (EdmEntitySet es : c.getEntitySets()) {
        if (es.getEntityType().getFullQualifiedName().equals(sourceType.getFullQualifiedName())) {
          for (EdmNavigationPropertyBinding binding : es.getNavigationPropertyBindings()) {
            if (binding.getPath().equals(property.getName())
                || binding.getPath().endsWith("/" + property.getName())) {
              return es.getRelatedBindingTarget(binding.getPath());
            }
          }
        }
      }

      for (EdmSingleton s : c.getSingletons()) {
        if (s.getEntityType().getFullQualifiedName().equals(sourceType.getFullQualifiedName())) {
          for (EdmNavigationPropertyBinding binding : s.getNavigationPropertyBindings()) {
            if (binding.getPath().equals(property.getName())
                || binding.getPath().endsWith("/" + property.getName())) {
              return s.getRelatedBindingTarget(binding.getPath());
            }
          }
        }
      }
    }

    throw new IllegalStateException(
        "Navigation property '" + sourceType.getName() + "." + property.getName() + "' not valid");
  }

  public EdmSchema getSchema() {
    return schema;
  }

  public EdmEntityContainer getContainer() {
    return container;
  }

  public EdmBindingTarget getEntitySet() {
    return entitySet;
  }

  public EdmStructuredType getType() {
    return type;
  }
}
