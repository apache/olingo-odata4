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
package org.apache.olingo.commons.core.edm.primitivetype;

import org.apache.olingo.commons.api.edm.EdmGeospatialType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Dimension;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Type;

public abstract class AbstractEdmGeospatialType<T extends Geospatial> implements EdmGeospatialType {

  private final Class<T> reference;

  protected final Dimension dimension;

  protected final Type type;

  protected AbstractEdmGeospatialType(final Class<T> reference, final Dimension dimension, final Type type) {
    this.reference = reference;
    this.dimension = dimension;
    this.type = type;
  }

  @Override
  public Class<? extends Geospatial> getJavaType() {
    return reference;
  }

  @Override
  public String getNamespace() {
    return EDM_NAMESPACE;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName().substring(3);
  }

  @Override
  public EdmTypeKind getKind() {
    return EdmTypeKind.PRIMITIVE;
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj || obj != null && getClass() == obj.getClass();
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return new FullQualifiedName(getNamespace(), getName()).getFullQualifiedNameAsString();
  }
}
