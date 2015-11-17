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

import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;

public class UntypedOperand extends VisitorOperand {

  public UntypedOperand(final String literal) {
    super(literal);
  }

  @Override
  public TypedOperand asTypedOperand() throws ODataApplicationException {
    return determineType();
  }

  @Override
  public TypedOperand asTypedOperand(final EdmPrimitiveType type) throws ODataApplicationException {
    final String literal = (String) value;
    Object newValue = null;

    // First try the null literal.
    if ((newValue = tryCast(literal, primNull)) != null) {
      return new TypedOperand(newValue, primNull);
    }

    // Then try the given type.
    if ((newValue = tryCast(literal, type)) != null) {
      return new TypedOperand(newValue, type);
    }

    throw new ODataApplicationException("Cast failed", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
        Locale.ROOT);
  }

  public TypedOperand determineType() throws ODataApplicationException {
    final String literal = (String) value;
    Object newValue = null;

    // Null literal
    if (primNull.validate(literal, null, null, null, null, null)) {
      return new TypedOperand(newValue, primNull);
    }

    // String
    if ((newValue = tryCast(literal, primString)) != null) {
      return new TypedOperand(newValue, primString);
    }

    // Boolean
    if ((newValue = tryCast(literal, primBoolean)) != null) {
      return new TypedOperand(newValue, primBoolean);
    }

    // Date
    if ((newValue = tryCast(literal, primDateTimeOffset)) != null) {
      return new TypedOperand(newValue, primDateTimeOffset);
    }

    if ((newValue = tryCast(literal, primDate)) != null) {
      return new TypedOperand(newValue, primDate);
    }

    if ((newValue = tryCast(literal, primTimeOfDay)) != null) {
      return new TypedOperand(newValue, primTimeOfDay);
    }

    if ((newValue = tryCast(literal, primDuration)) != null) {
      return new TypedOperand(newValue, primDuration);
    }

    // Integer
    if ((newValue = tryCast(literal, primSByte)) != null) {
      return new TypedOperand(newValue, primSByte);
    }

    if ((newValue = tryCast(literal, primByte)) != null) {
      return new TypedOperand(newValue, primByte);
    }

    if ((newValue = tryCast(literal, primInt16)) != null) {
      return new TypedOperand(newValue, primInt16);
    }

    if ((newValue = tryCast(literal, primInt32)) != null) {
      return new TypedOperand(newValue, primInt32);
    }

    if ((newValue = tryCast(literal, primInt64)) != null) {
      return new TypedOperand(newValue, primInt64);
    }

    // Decimal
    if ((newValue = tryCast(literal, primDecimal)) != null) {
      return new TypedOperand(newValue, primDecimal);
    }

    // Float
    if ((newValue = tryCast(literal, primSingle)) != null) {
      return new TypedOperand(newValue, primSingle);
    }

    if ((newValue = tryCast(literal, primDouble)) != null) {
      return new TypedOperand(newValue, primDouble);
    }

    throw new ODataApplicationException("Could not determine type for literal " + literal,
        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
  }

  @Override
  public EdmProperty getEdmProperty() {
    return null;
  }
}
