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
package org.apache.olingo.client.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.olingo.client.api.domain.AbstractODataValue;
import org.apache.olingo.client.api.domain.ODataGeospatialValue;
import org.apache.olingo.commons.api.edm.EdmGeospatialType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class ODataGeospatialValueImpl extends AbstractODataValue implements ODataGeospatialValue {

  private static final long serialVersionUID = 8277433906326348952L;

  public static class BuilderImpl implements Builder {

    private final ODataGeospatialValueImpl instance;

    public BuilderImpl() {
      this.instance = new ODataGeospatialValueImpl();
    }

    @Override
    public BuilderImpl setType(final EdmPrimitiveTypeKind type) {
      if (type != null && !type.isGeospatial()) {
        throw new IllegalArgumentException("Don't use this for non-geospatial types");
      }
      if (type == EdmPrimitiveTypeKind.Geography || type == EdmPrimitiveTypeKind.Geometry) {
        throw new IllegalArgumentException(
                type + "is not an instantiable type. "
                + "An entity can declare a property to be of type Geometry. "
                + "An instance of an entity MUST NOT have a value of type Geometry. "
                + "Each value MUST be of some subtype.");
      }

      if (type != null) {
        this.instance.typeKind = type;
        this.instance.type = EdmPrimitiveTypeFactory.getGeoInstance(type);
      }

      return this;
    }

    @Override
    public BuilderImpl setValue(final Geospatial value) {
      this.instance.value = value;
      if (value != null) {
        setType(value.getEdmPrimitiveTypeKind());
      }
      return this;
    }

    @Override
    public ODataGeospatialValueImpl build() {
      if (this.instance.type == null) {
        throw new IllegalArgumentException("Must provide geospatial type");
      }
      if (this.instance.value == null) {
        throw new IllegalArgumentException("Must provide geospatial value");
      }

      return this.instance;
    }
  }

  /**
   * Type kind.
   */
  private EdmPrimitiveTypeKind typeKind;

  /**
   * Type.
   */
  private EdmGeospatialType type;

  /**
   * Value.
   */
  private Geospatial value;

  @Override
  public EdmPrimitiveTypeKind getTypeKind() {
    return typeKind;
  }

  @Override
  public EdmGeospatialType getType() {
    return type;
  }

  @Override
  public Geospatial toValue() {
    return value;
  }

  @Override
  public <T extends Geospatial> T toCastValue(final Class<T> reference) {
    return reference.cast(this.value);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

}
