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
package org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.primitive.EdmNull;

public class TypedOperand extends VisitorOperand {

  final private EdmType type;
  final private EdmProperty edmProperty;

  public TypedOperand(Object value, EdmType type) {
    super(value);
    this.type = type;
    this.edmProperty = null;
  }

  public TypedOperand(Object value, EdmType type, EdmProperty edmProperty) {
    super(value);
    this.type = type;
    this.edmProperty = edmProperty;
  }

  @Override
  public TypedOperand asTypedOperand() throws ODataApplicationException {
    if (!isNull() && value.getClass() != getDefaultType((EdmPrimitiveType) type)) {
      return asTypedOperand((EdmPrimitiveType) type);
    }
    return this;
  }

  @Override
  public TypedOperand asTypedOperand(EdmPrimitiveType... asTypes) throws ODataApplicationException {
    if (type.equals(EdmNull.getInstance())) {
      return this;
    } else if (isNull()) {
      return new TypedOperand(null, asTypes[0]);
    }

    Object newValue = null;
    for (EdmPrimitiveType asType : asTypes) {
      // Use BigDecimal for unlimited precision
      if (asType.equals(primDouble) || asType.equals(primSingle) || asType.equals(primDecimal)) {

        try {
          newValue = new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
          // Nothing to do
        }
      } else {
        // Use type conversion of EdmPrimitive types
        try {
          final String literal = getLiteral(value);
          newValue = tryCast(literal, (EdmPrimitiveType) type);
        } catch (EdmPrimitiveTypeException e) {
          // Nothing to do
        }
      }

      if (newValue != null) {
        return new TypedOperand(newValue, asType);
      }
    }

    throw new ODataApplicationException("Cast failed ", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
  }

  public TypedOperand castToCommonType(VisitorOperand otherOperand) throws ODataApplicationException {
    final TypedOperand other = otherOperand.asTypedOperand();
    final EdmType oType = other.getType();

    // Make sure that the EDM type is equals, check also the java type.
    // So it is possible, that there is an conversation even if the same
    // EdmType is provided.
    // For example consider an Edm16 (internal Integer) and Edm16(internal
    // Short)
    // shortInstance.equals(intInstance) will always be false!
    if (type == oType && value != null && other.getValue() != null
        && value.getClass() == other.getValue().getClass()) {
      return this;
    } else if (isNullLiteral() || other.isNullLiteral()) {
      return this;
    }

    if (type.equals(primDouble) || oType.equals(primDouble)) {
      return asTypedOperand(primDouble);
    } else if (type.equals(primSingle) || oType.equals(primSingle)) {
      return asTypedOperand(primSingle);
    } else if (type.equals(primDecimal) || oType.equals(primDecimal)) {
      return asTypedOperand(primDecimal);
    } else if (type.equals(primInt64) || oType.equals(primInt64)) {
      return asTypedOperand(primInt64);
    } else if (type.equals(primInt32) || oType.equals(primInt32)) {
      return asTypedOperand(primInt32);
    } else if (type.equals(primInt16) || oType.equals(primInt16)) {
      return asTypedOperand(primInt16);
    } else {
      return asTypedOperand((EdmPrimitiveType) type);
    }
  }

  public EdmType getType() {
    return type;
  }

  public <T> T getTypedValue(Class<T> clazz) {
    return clazz.cast(value);
  }

  public boolean isNullLiteral() {
    return type.equals(EdmNull.getInstance());
  }

  public boolean isNull() {
    return isNullLiteral() || value == null;
  }

  public boolean isIntegerType() {
    return is(
        primByte,
        primSByte,
        primInt16,
        primInt32,
        primInt64);
  }

  public boolean isDecimalType() {
    return is(
        primSingle,
        primDouble,
        primDecimal);
  }

  public boolean is(EdmPrimitiveType... types) {
    if (isNullLiteral()) {
      return true;
    }

    for (EdmPrimitiveType type : types) {
      if (type.equals(this.type)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public EdmProperty getEdmProperty() {
    return edmProperty;
  }

  private String getLiteral(Object value) throws EdmPrimitiveTypeException {
    final EdmProperty edmProperty = getEdmProperty();
    String uriLiteral = null;

    if (edmProperty != null) {
      uriLiteral = ((EdmPrimitiveType) type).valueToString(value, edmProperty.isNullable(), edmProperty.getMaxLength(),
          edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode());
    } else {
      uriLiteral = ((EdmPrimitiveType) type).valueToString(value, null, null, null, null, null);
    }

    return ((EdmPrimitiveType) type).toUriLiteral(uriLiteral);
  }
}
