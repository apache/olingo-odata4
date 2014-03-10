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
package org.apache.olingo.odata4.commons.core.edm.primitivetype;

import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;

//TODO: Should we delete this typekind and use a facade?
public enum EdmPrimitiveTypeKind {

  Binary, Boolean, Byte, Date, DateTimeOffset, Decimal, Double, Duration, Guid,
  Int16, Int32, Int64, SByte, Single, String, TimeOfDay;

  /**
   * Returns the {@link FullQualifiedName} for this type kind.
   *
   * @return {@link FullQualifiedName}
   */
  public FullQualifiedName getFullQualifiedName() {
    return new FullQualifiedName(EdmPrimitiveType.EDM_NAMESPACE, toString());
  }

  /**
   * Returns an instance for this {@link EdmPrimitiveTypeKind} in the form of {@link EdmPrimitiveType}.
   *
   * @return {@link EdmPrimitiveType} instance
   */
  public EdmPrimitiveType getEdmPrimitiveTypeInstance() {
    switch (this) {
      case Binary:
        return EdmBinary.getInstance();
      case Boolean:
        return EdmBoolean.getInstance();
      case Byte:
        return EdmByte.getInstance();
      case Date:
        return EdmDate.getInstance();
      case DateTimeOffset:
        return EdmDateTimeOffset.getInstance();
      case Decimal:
        return EdmDecimal.getInstance();
      case Double:
        return EdmDouble.getInstance();
      case Duration:
        return EdmDuration.getInstance();
      case Guid:
        return EdmGuid.getInstance();
      case Int16:
        return EdmInt16.getInstance();
      case Int32:
        return EdmInt32.getInstance();
      case Int64:
        return EdmInt64.getInstance();
      case SByte:
        return EdmSByte.getInstance();
      case Single:
        return EdmSingle.getInstance();
      case String:
        return EdmString.getInstance();
      case TimeOfDay:
        return EdmTimeOfDay.getInstance();
      default:
        throw new RuntimeException("Wrong type:" + this);
    }
  }

  /**
   * Gets <tt>EdmPrimitiveTypeKind</tt> from a full string (e.g. 'Edm.Int32').
   *
   * @param value string value type.
   * @return <tt>EdmPrimitiveTypeKind</tt> object.
   */
  public static EdmPrimitiveTypeKind fromString(final String value) {
    final String noNsValue = value.substring(4);
    for (EdmPrimitiveTypeKind edmSimpleType : EdmPrimitiveTypeKind.values()) {
      if (edmSimpleType.name().equals(noNsValue)) {
        return edmSimpleType;
      }
    }
    throw new IllegalArgumentException(value);
  }
}
