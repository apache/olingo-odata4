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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public abstract class AbstractEdmBindingTarget extends EdmNamedImpl implements EdmBindingTarget {

  protected final EdmEntityContainer container;

  private final FullQualifiedName type;

  public AbstractEdmBindingTarget(final Edm edm, final EdmEntityContainer container,
          final String name, final FullQualifiedName type) {

    super(edm, name);
    this.container = container;
    this.type = type;
  }

  @Override
  public EdmEntityContainer getEntityContainer() {
    return container;
  }

  @Override
  public EdmEntityType getEntityType() {
    final EdmEntityType entityType = edm.getEntityType(this.type);
    if (entityType == null) {
      throw new EdmException("Can´t find entity type: " + type + " for entity set or singleton: " + getName());
    }
    return entityType;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return container.getFullQualifiedName();
  }

  @Override
  public String getAnnotationsTargetPath() {
    return getName();
  }

}
