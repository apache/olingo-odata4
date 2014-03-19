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
package org.apache.olingo.client.api.domain;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.geospatial.Geospatial;

public class ODataGeospatialValue extends ODataPrimitiveValue {

  private static final long serialVersionUID = -3984105137562291082L;

  /**
   * Geospatial value builder.
   */
  public static class Builder extends AbstractBuilder {

    private final ODataGeospatialValue ogv;

    /**
     * Constructor.
     */
    public Builder(final ODataClient client) {
      super(client);
      this.ogv = new ODataGeospatialValue(client);
    }

    /**
     * Sets the actual object value.
     *
     * @param value value.
     * @return the current builder.
     */
    public <T extends Geospatial> Builder setValue(final T value) {
      this.ogv.value = value;
      return this;
    }

    /**
     * Sets actual value type.
     *
     * @param type type.
     * @return the current builder.
     */
    public Builder setType(final ODataJClientEdmPrimitiveType type) {
      isSupported(type);

      if (type != null && !type.isGeospatial()) {
        throw new IllegalArgumentException(
                "Use " + ODataPrimitiveValue.class.getSimpleName() + " for non-geospatial types");
      }

      if (type == ODataJClientEdmPrimitiveType.Geography || type == ODataJClientEdmPrimitiveType.Geometry) {
        throw new IllegalArgumentException(
                type + " is not an instantiable type. "
                + "An entity can declare a property to be of type Geometry. "
                + "An instance of an entity MUST NOT have a value of type Geometry. "
                + "Each value MUST be of some subtype.");
      }
      this.ogv.type = type;
      return this;
    }

    /**
     * Builds the geospatial value.
     *
     * @return <tt>ODataGeospatialValue</tt> object.
     */
    public ODataGeospatialValue build() {
      if (this.ogv.value == null) {
        throw new IllegalArgumentException("No Geospatial value provided");
      }
      if (this.ogv.type == null) {
        this.ogv.type = ((Geospatial) this.ogv.value).getEdmSimpleType();
      }

      return this.ogv;
    }
  }

  /**
   * Protected constructor, need to use the builder to instantiate this class.
   *
   * @see Builder
   */
  protected ODataGeospatialValue(final ODataClient client) {
    super(client);
  }

  public Geospatial getGeospatial() {
    return (Geospatial) this.value;
  }
}
