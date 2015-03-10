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
import org.apache.olingo.commons.core.edm.primitivetype.EdmByte;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDouble;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt16;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSByte;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSingle;
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
      if (asType.equals(EdmDouble.getInstance())
          || asType.equals(EdmSingle.getInstance())
          || asType.equals(EdmDecimal.getInstance())) {
        
        try {
          newValue = new BigDecimal(value.toString());
        } catch(NumberFormatException e) {
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

    if (type.equals(EdmDouble.getInstance()) || oType.equals(EdmDouble.getInstance())) {
      return asTypedOperand(EdmDouble.getInstance());
    } else if (type.equals(EdmSingle.getInstance()) || oType.equals(EdmSingle.getInstance())) {
      return asTypedOperand(EdmSingle.getInstance());
    } else if (type.equals(EdmDecimal.getInstance()) || oType.equals(EdmDecimal.getInstance())) {
      return asTypedOperand(EdmDecimal.getInstance());
    } else if (type.equals(EdmInt64.getInstance()) || oType.equals(EdmInt64.getInstance())) {
      return asTypedOperand(EdmInt64.getInstance());
    } else if (type.equals(EdmInt32.getInstance()) || oType.equals(EdmInt32.getInstance())) {
      return asTypedOperand(EdmInt32.getInstance());
    } else if (type.equals(EdmInt16.getInstance()) || oType.equals(EdmInt16.getInstance())) {
      return asTypedOperand(EdmInt16.getInstance());
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
    return is(EdmByte.getInstance(),
        EdmSByte.getInstance(),
        EdmInt16.getInstance(),
        EdmInt32.getInstance(),
        EdmInt64.getInstance());
  }

  public boolean isDecimalType() {
    return is(EdmSingle.getInstance(),
        EdmDouble.getInstance(),
        EdmDecimal.getInstance());
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
