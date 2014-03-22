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

import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.olingo.client.api.domain.AbstractODataValue;
import org.apache.olingo.client.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class ODataPrimitiveValueImpl extends AbstractODataValue implements ODataPrimitiveValue {

  private static final long serialVersionUID = 8889282662298376036L;

  public static class BuilderImpl implements Builder {

    private final ODataServiceVersion version;

    private final ODataPrimitiveValueImpl instance;

    public BuilderImpl(final ODataServiceVersion version) {
      this.version = version;
      this.instance = new ODataPrimitiveValueImpl();
    }

    @Override
    public BuilderImpl setType(final EdmPrimitiveTypeKind type) {
      if (type != null && !type.getSupportedVersions().contains(version)) {
        throw new IllegalArgumentException(String.format(
                "Type %s not supported by OData version %s", type.toString(), version));
      }
      if (type == EdmPrimitiveTypeKind.Stream) {
        throw new IllegalArgumentException(String.format(
                "Cannot build a primitive value for %s", EdmPrimitiveTypeKind.Stream.toString()));
      }
      if (type != null && type.isGeospatial()) {
        throw new IllegalArgumentException("Don't use this for geospatial types");
      }

      this.instance.typeKind = type == null ? EdmPrimitiveTypeKind.String : type;
      this.instance.type = EdmPrimitiveTypeFactory.getNonGeoInstance(this.instance.typeKind);

      return this;
    }

    @Override
    public BuilderImpl setText(final String text) {
      this.instance.text = text;
      return this;
    }

    @Override
    public BuilderImpl setValue(final Object value) {
      this.instance.value = value;
      return this;
    }

    @Override
    public ODataPrimitiveValueImpl build() {
      if (this.instance.text == null && this.instance.value == null) {
        throw new IllegalArgumentException("Must provide either text or value");
      }
      if (this.instance.text != null && this.instance.value != null) {
        throw new IllegalArgumentException("Cannot provide both text and value");
      }

      if (this.instance.type == null) {
        setType(EdmPrimitiveTypeKind.String);
      }

      if (this.instance.text != null) {
        final Class<?> returnType = this.instance.type.getDefaultType().isAssignableFrom(Calendar.class)
                ? Timestamp.class : this.instance.type.getDefaultType();
        try {
          // TODO: when Edm is available, set facets when calling this method
          this.instance.value = this.instance.type.valueOfString(
                  this.instance.text, null, null, 40, 25, null, returnType);
        } catch (EdmPrimitiveTypeException e) {
          throw new IllegalArgumentException(e);
        }
      }
      if (this.instance.value != null) {
        try {
          // TODO: when Edm is available, set facets when calling this method
          this.instance.text = this.instance.type.valueToString(
                  this.instance.value, null, null, 40, 25, null);
        } catch (EdmPrimitiveTypeException e) {
          throw new IllegalArgumentException(e);
        }
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
  private EdmPrimitiveType type;

  /**
   * Text value.
   */
  private String text;

  /**
   * Actual value.
   */
  private Object value;

  @Override
  public EdmPrimitiveTypeKind getTypeKind() {
    return typeKind;
  }

  @Override
  public EdmPrimitiveType getType() {
    return type;
  }

  @Override
  public Object toValue() {
    return this.value;
  }

  @Override
  public <T> T toCastValue(final Class<T> reference) throws EdmPrimitiveTypeException {
    // TODO: when Edm is available, set facets when calling this method
    return type.valueOfString(this.text, null, null, 40, 25, null, reference);
  }

  @Override
  public String toString() {
    return this.text;
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
