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

/**
 * EdmPrimitiveType is a primitive type as defined in the Entity Data Model (EDM).
 * <br/>
 * There are methods to convert EDM primitive types from and to Java objects, respectively. The following Java types are
 * supported:
 * <table frame="hsides" rules="groups">
 * <thead>
 * <tr><th>EDM primitive type</th><th>Java types</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>Binary</td><td>byte[], {@link Byte}[]</td></tr>
 * <tr><td>Boolean</td><td>{@link Boolean}</td></tr>
 * <tr><td>Byte</td><td>{@link Short}, {@link Byte}, {@link Integer}, {@link Long}, {@link java.math.BigInteger}
 * </td></tr>
 * <tr><td>Date</td><td>{@link java.util.Calendar}, {@link java.util.Date}, {@link java.sql.Timestamp},
 * {@link java.sql.Time}, {@link Long}</td></tr>
 * <tr><td>DateTimeOffset</td><td>{@link java.sql.Timestamp}, {@link java.util.Calendar}, {@link java.util.Date},
 * {@link java.sql.Time}, {@link Long}</td></tr>
 * <tr><td>Decimal</td><td>{@link java.math.BigDecimal}, {@link java.math.BigInteger}, {@link Double}, {@link Float},
 * {@link Byte}, {@link Short}, {@link Integer}, {@link Long}</td></tr>
 * <tr><td>Double</td><td>{@link Double}, {@link Float}, {@link java.math.BigDecimal}, {@link Byte}, {@link Short},
 * {@link Integer}, {@link Long}</td></tr>
 * <tr><td>Duration</td><td>{@link java.math.BigDecimal}, {@link java.math.BigInteger}, {@link Double}, {@link Float},
 * {@link Byte}, {@link Short}, {@link Integer}, {@link Long}</td></tr>
 * <tr><td>Guid</td><td>{@link java.util.UUID}</td></tr>
 * <tr><td>Int16</td><td>{@link Short}, {@link Byte}, {@link Integer}, {@link Long}, {@link java.math.BigInteger}
 * </td></tr>
 * <tr><td>Int32</td><td>{@link Integer}, {@link Byte}, {@link Short}, {@link Long}, {@link java.math.BigInteger}
 * </td></tr>
 * <tr><td>Int64</td><td>{@link Long}, {@link Byte}, {@link Short}, {@link Integer}, {@link java.math.BigInteger}
 * </td></tr>
 * <tr><td>SByte</td><td>{@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link java.math.BigInteger}
 * </td></tr>
 * <tr><td>Single</td><td>{@link Float}, {@link Double}, {@link java.math.BigDecimal}, {@link Byte}, {@link Short},
 * {@link Integer}, {@link Long}</td></tr>
 * <tr><td>String</td><td>{@link String}</td></tr>
 * <tr><td>TimeOfDay</td><td>{@link java.util.Calendar}, {@link java.util.Date}, {@link java.sql.Timestamp},
 * {@link java.sql.Time}, {@link Long}</td></tr>
 * </tbody>
 * </table>
 * <p>
 * The first Java type is the default type for the respective EDM primitive type.
 * </p>
 * <p>
 * For all EDM primitive types, the <code>Nullable</code> facet is taken into account. For <code>Binary</code> and
 * <code>String</code>, <code>MaxLength</code> is also applicable. For <code>String</code>, the facet
 * <code>Unicode</code> is considered additionally. The EDM primitive types <code>DateTimeOffset</code>,
 * <code>Decimal</code>, <code>Duration</code>, and <code>TimeOfDay</code> can have a <code>Precision</code> facet.
 * Additionally, <code>Decimal</code> can have the facet <code>Scale</code>.
 * </p>
 */
public interface EdmPrimitiveType extends EdmType {

  String EDM_NAMESPACE = "Edm";

  /**
   * Checks type compatibility.
   *
   * @param primitiveType the {@link EdmPrimitiveType} to be tested for compatibility
   * @return <code>true</code> if the provided type is compatible to this type
   */
  boolean isCompatible(EdmPrimitiveType primitiveType);

  /**
   * Returns the default Java type for this EDM primitive type as described in the documentation of
   * {@link EdmPrimitiveType}.
   *
   * @return the default Java type
   */
  Class<?> getDefaultType();

  /**
   * Validates literal value.
   *
   * @param value the literal value
   * @param isNullable whether the <code>null</code> value is allowed
   * @param maxLength the maximum length
   * @param precision the precision
   * @param scale the scale
   * @param isUnicode whether non-ASCII characters are allowed (relevant only for Edm.String)
   * @return <code>true</code> if the validation is successful
   */
  boolean validate(String value,
      Boolean isNullable, Integer maxLength, Integer precision, Integer scale, Boolean isUnicode);

  /**
   * Converts literal representation of value to system data type.
   *
   * @param value the literal representation of value
   * @param isNullable whether the <code>null</code> value is allowed
   * @param maxLength the maximum length
   * @param precision the precision
   * @param scale the scale
   * @param isUnicode whether non-ASCII characters are allowed (relevant only for Edm.String)
   * @param returnType the class of the returned value; it must be one of the list in the documentation of
   * {@link EdmPrimitiveType}
   * @throws EdmPrimitiveTypeException
   * @return the value as an instance of the class the parameter <code>returnType</code> indicates
   */
  <T> T valueOfString(String value,
      Boolean isNullable, Integer maxLength, Integer precision, Integer scale, Boolean isUnicode,
      Class<T> returnType) throws EdmPrimitiveTypeException;

  /**
   * Converts system data type to literal representation of value.
   * <p>
   * Returns <code>null</code> if value is <code>null</code> and <code>null</code> is an allowed value.
   * </p>
   *
   * @param value the Java value as Object; its type must be one of the list in the documentation of
   * {@link EdmPrimitiveType}
   * @param isNullable whether the <code>null</code> value is allowed
   * @param maxLength the maximum length
   * @param precision the precision
   * @param scale the scale
   * @param isUnicode whether non-ASCII characters are allowed (relevant only for Edm.String)
   * @throws EdmPrimitiveTypeException
   * @return literal representation as String
   */
  String valueToString(Object value,
      Boolean isNullable, Integer maxLength, Integer precision, Integer scale, Boolean isUnicode)
          throws EdmPrimitiveTypeException;

  /**
   * Converts default literal representation to URI literal representation.
   * <p>
   * Returns <code>null</code> if the literal is <code>null</code>. Does not perform any validation.
   * </p>
   *
   * @param literal the literal in default representation
   * @return URI literal representation as String
   */
  String toUriLiteral(String literal);

  /**
   * Converts URI literal representation to default literal representation.
   * <p>
   * Returns <code>null</code> if the literal is <code>null</code>. Checks the presence of a required prefix and of
   * required surrounding quotation marks but does not perform any further validation.
   * </p>
   *
   * @param literal the literal in URI representation
   * @return default literal representation as String
   * @throws EdmPrimitiveTypeException if a required prefix or required surrounding quotation marks are missing
   */
  String fromUriLiteral(String literal) throws EdmPrimitiveTypeException;
}
