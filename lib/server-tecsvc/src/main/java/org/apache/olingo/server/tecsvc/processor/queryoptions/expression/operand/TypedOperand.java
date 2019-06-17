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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;

public class TypedOperand extends VisitorOperand {

  final private EdmType type;
  final private EdmProperty edmProperty;

  public TypedOperand(final Object value, final EdmType type) {
    super(value);
    this.type = type;
    edmProperty = null;
  }

  public TypedOperand(final Object value, final EdmType type, final EdmProperty edmProperty) {
    super(value);
    this.type = type;
    this.edmProperty = edmProperty;
  }

  @Override
  public TypedOperand asTypedOperand() throws ODataApplicationException {
    if (isNull()) {
      return this;
    } else if (type instanceof EdmPrimitiveType && !(value instanceof Collection)) {
      return value.getClass() == getDefaultType((EdmPrimitiveType) type) ?
          this :
          asTypedOperand((EdmPrimitiveType) type);
    } else if (type instanceof EdmPrimitiveType && value instanceof Collection) {
      return value.getClass() == getDefaultType((EdmPrimitiveType) type) ?
          this :
          asTypedOperandForCollection((EdmPrimitiveType) type);
    } else {
      throw new ODataApplicationException("A single primitive-type instance is expected.",
          HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public TypedOperand asTypedOperandForCollection(final EdmPrimitiveType asType) throws ODataApplicationException {
    if (is(primNull)) {
      return this;
    } else if (isNull()) {
      return new TypedOperand(null, asType);
    } 
    List<Object> newValue = new ArrayList<Object>();
    List<Object> list = (List<Object>) value;
    for (Object val : list) {
   // Use BigInteger for arbitrarily large whole numbers.
      if (asType.equals(primSByte) || asType.equals(primByte)
          || asType.equals(primInt16) || asType.equals(primInt32) || asType.equals(primInt64)) {
        if (val instanceof BigInteger) {
          newValue.add(val);
        } else if (val instanceof Byte || val instanceof Short
            || val instanceof Integer || val instanceof Long) {
          newValue.add(BigInteger.valueOf(((Number) val).longValue()));
        }
      // Use BigDecimal for unlimited precision.
      } else if (asType.equals(primDouble) || asType.equals(primSingle) || asType.equals(primDecimal)) {
        try {
          newValue.add(new BigDecimal(val.toString()));
        } catch (NumberFormatException e) {
          throw new ODataApplicationException("Format exception", 
              HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT, e.getCause());
        }
      } else {
        // Use type conversion of EdmPrimitive types
        try {
          final String literal = getLiteral(val);
          newValue.add(tryCast(literal, asType));
        } catch (EdmPrimitiveTypeException e) {
          throw new ODataApplicationException("Cast Failed", 
              HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT, e.getCause());
        }
      }
    }
    if (!newValue.isEmpty()) {
      return new TypedOperand(newValue, asType);
    }

    throw new ODataApplicationException("Cast failed", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    
  }

  @Override
  public TypedOperand asTypedOperand(final EdmPrimitiveType asType) throws ODataApplicationException {
    if (is(primNull)) {
      return this;
    } else if (isNull()) {
      return new TypedOperand(null, asType);
    } 

    Object newValue = null;
    // Use BigInteger for arbitrarily large whole numbers.
    if (asType.equals(primSByte) || asType.equals(primByte)
        || asType.equals(primInt16) || asType.equals(primInt32) || asType.equals(primInt64)) {
      if (value instanceof BigInteger) {
        newValue = value;
      } else if (value instanceof Byte || value instanceof Short
          || value instanceof Integer || value instanceof Long) {
        newValue = BigInteger.valueOf(((Number) value).longValue());
      }
    // Use BigDecimal for unlimited precision.
    } else if (asType.equals(primDouble) || asType.equals(primSingle) || asType.equals(primDecimal)) {
      try {
        newValue = new BigDecimal(value.toString());
      } catch (NumberFormatException e) {
        // Nothing to do
      }
    } else {
      // Use type conversion of EdmPrimitive types
      try {
        final String literal = getLiteral(value);
        newValue = tryCast(literal, asType);
      } catch (EdmPrimitiveTypeException e) {
        // Nothing to do
      }
    }

    if (newValue != null) {
      return new TypedOperand(newValue, asType);
    }

    throw new ODataApplicationException("Cast failed", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
  }

  public TypedOperand castToCommonType(final VisitorOperand otherOperand) throws ODataApplicationException {
    final TypedOperand other = otherOperand.asTypedOperand();
    final EdmType oType = other.getType();
    
    // In case of numberic values make sure that the EDM type is equal, check also the java type.
    // It is possible that there is an conversion even if the same EdmType is provided.
    // For example consider an Edm.Int32 (internal Integer) and an Edm.Int16 (internal Short) value:
    // shortInstance.equals(intInstance) will always be false!
    if (type == oType && value != null && other.getValue() != null
        && value.getClass() == other.getValue().getClass()) {
      return this;
    } else if (is(primNull) || other.is(primNull)) {
      return this;
    }

    if (type.equals(primDouble) || oType.equals(primDouble)) {
      return (value instanceof ArrayList) ? asTypedOperandForCollection(primDouble) : asTypedOperand(primDouble);
    } else if (type.equals(primSingle) || oType.equals(primSingle)) {
      return (value instanceof ArrayList) ? asTypedOperandForCollection(primSingle) : asTypedOperand(primSingle);
    } else if (type.equals(primDecimal) || oType.equals(primDecimal)) {
      return (value instanceof ArrayList) ? asTypedOperandForCollection(primDecimal) : asTypedOperand(primDecimal);
    } else if (type.equals(primInt64) || oType.equals(primInt64)) {
      return (value instanceof ArrayList) ? asTypedOperandForCollection(primInt64) : asTypedOperand(primInt64);
    } else if (type.equals(primInt32) || oType.equals(primInt32)) {
      return (value instanceof ArrayList) ? asTypedOperandForCollection(primInt32) : asTypedOperand(primInt32);
    } else if (type.equals(primInt16) || oType.equals(primInt16)) {
      return (value instanceof ArrayList) ? asTypedOperandForCollection(primInt16) : asTypedOperand(primInt16);
    } else {
      return (value instanceof ArrayList) ? asTypedOperandForCollection((EdmPrimitiveType) type) : 
        asTypedOperand((EdmPrimitiveType) type);
    }
  }

  public EdmType getType() {
    return type;
  }

  public <T> T getTypedValue(final Class<T> clazz) {
    return clazz.cast(value);
  }
  
  public <T> List<T> getTypedValueList(final Class<T> clazz) {
    List<Object> list = (List<Object>) value;
    List<Object> newList = new ArrayList<Object>();
    for (Object obj : list) {
      newList.add(clazz.cast(obj));
    }
    return (List<T>) newList;
  }
  

  public boolean isNull() {
    return is(primNull) || value == null;
  }

  public boolean isIntegerType() {
    return is(primNull,
        primByte,
        primSByte,
        primInt16,
        primInt32,
        primInt64);
  }

  public boolean isDecimalType() {
    return is(primNull,
        primSingle,
        primDouble,
        primDecimal);
  }

  public boolean is(final EdmPrimitiveType... types) {
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

  private String getLiteral(final Object value) throws EdmPrimitiveTypeException {
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
