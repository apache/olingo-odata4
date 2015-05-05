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

public enum EdmPrimitiveTypeKind {

  Binary,
  Boolean,
  Byte,
  SByte,
  Date,
  DateTimeOffset,
  TimeOfDay,
  Duration,
  Decimal,
  Single,
  Double,
  Guid,
  Int16,
  Int32,
  Int64,
  String,
  Stream,
  Geography,
  GeographyPoint,
  GeographyLineString,
  GeographyPolygon,
  GeographyMultiPoint,
  GeographyMultiLineString,
  GeographyMultiPolygon,
  GeographyCollection,
  Geometry,
  GeometryPoint,
  GeometryLineString,
  GeometryPolygon,
  GeometryMultiPoint,
  GeometryMultiLineString,
  GeometryMultiPolygon,
  GeometryCollection;

  /**
   * Checks if is a geospatial type.
   *
   * @return <tt>true</tt> if is geospatial type; <tt>false</tt> otherwise.
   */
  public boolean isGeospatial() {
    return name().startsWith("Geo");
  }

  /**
   * Returns the {@link FullQualifiedName} for this type kind.
   *
   * @return {@link FullQualifiedName}
   */
  public FullQualifiedName getFullQualifiedName() {
    return new FullQualifiedName(EdmPrimitiveType.EDM_NAMESPACE, toString());
  }

  // public static EdmPrimitiveTypeKind valueOf(final ODataServiceVersion version, final String name) {
  // return valueOf(name);
  // }

  /**
   * Gets <tt>EdmPrimitiveTypeKind</tt> from a full-qualified type name, for the given OData protocol version.
   *
   * @param fqn full-qualified type name.
   * @return <tt>EdmPrimitiveTypeKind</tt> object.
   */
  public static EdmPrimitiveTypeKind valueOfFQN(final FullQualifiedName fqn) {
    return valueOfFQN(fqn.toString());
  }

  /**
   * Gets <tt>EdmPrimitiveTypeKind</tt> from a full type expression (as <tt>Edm.Int32</tt>), for the given OData
   * protocol version.
   *
   * @param fqn string value type.
   * @return <tt>EdmPrimitiveTypeKind</tt> object.
   */
  public static EdmPrimitiveTypeKind valueOfFQN(final String fqn) {
    if (!fqn.startsWith(EdmPrimitiveType.EDM_NAMESPACE + ".")) {
      throw new IllegalArgumentException(fqn + " does not look like an Edm primitive type");
    }

    return valueOf(fqn.substring(4));
  }

}
