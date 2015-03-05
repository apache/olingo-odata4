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
import org.apache.olingo.commons.core.edm.primitivetype.EdmBoolean;
import org.apache.olingo.commons.core.edm.primitivetype.EdmByte;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDate;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDouble;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDuration;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt16;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSByte;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSingle;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.commons.core.edm.primitivetype.EdmTimeOfDay;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.primitive.EdmNull;

public class UntypedOperand extends VisitorOperand {

  public UntypedOperand(final String literal) {
    super(literal);
  }

  @Override
  public TypedOperand asTypedOperand() throws ODataApplicationException {
    return determineType();
  }

  @Override
  public TypedOperand asTypedOperand(final EdmPrimitiveType... types) throws ODataApplicationException {
    final String literal = (String) value;
    Object newValue = null;

    // First try the null literal
    if ((newValue = tryCast(literal, EdmNull.getInstance())) != null) {
      return new TypedOperand(newValue, EdmNull.getInstance());
    }

    // Than try the given types
    for (EdmPrimitiveType type : types) {
      newValue = tryCast(literal, type);

      if (newValue != null) {
        return new TypedOperand(newValue, type);
      }
    }

    throw new ODataApplicationException("Cast failed", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
        Locale.ROOT);
  }

  public TypedOperand determineType() throws ODataApplicationException {
    final String literal = (String) value;
    Object newValue = null;

    // Null literal
    if ((newValue = tryCast(literal, EdmNull.getInstance())) != null) {
      return new TypedOperand(newValue, EdmNull.getInstance());
    }

    // String
    if ((newValue = tryCast(literal, EdmString.getInstance())) != null) {
      return new TypedOperand(newValue, EdmString.getInstance());
    }

    // Boolean
    if ((newValue = tryCast(literal, EdmBoolean.getInstance())) != null) {
      return new TypedOperand(newValue, EdmBoolean.getInstance());
    }

    // Date
    if ((newValue = tryCast(literal, EdmDateTimeOffset.getInstance())) != null) {
      return new TypedOperand(newValue, EdmDateTimeOffset.getInstance());
    }

    if ((newValue = tryCast(literal, EdmDate.getInstance())) != null) {
      return new TypedOperand(newValue, EdmDate.getInstance());
    }

    if ((newValue = tryCast(literal, EdmTimeOfDay.getInstance())) != null) {
      return new TypedOperand(newValue, EdmTimeOfDay.getInstance());
    }

    if ((newValue = tryCast(literal, EdmDuration.getInstance())) != null) {
      return new TypedOperand(newValue, EdmDuration.getInstance());
    }

    // Integer
    if ((newValue = tryCast(literal, EdmSByte.getInstance())) != null) {
      return new TypedOperand(newValue, EdmSByte.getInstance());
    }

    if ((newValue = tryCast(literal, EdmByte.getInstance())) != null) {
      return new TypedOperand(newValue, EdmByte.getInstance());
    }

    if ((newValue = tryCast(literal, EdmInt16.getInstance())) != null) {
      return new TypedOperand(newValue, EdmInt16.getInstance());
    }

    if ((newValue = tryCast(literal, EdmInt32.getInstance())) != null) {
      return new TypedOperand(newValue, EdmInt32.getInstance());
    }

    if ((newValue = tryCast(literal, EdmInt64.getInstance())) != null) {
      return new TypedOperand(newValue, EdmInt64.getInstance());
    }

    // Decimal
    if ((newValue = tryCast(literal, EdmDecimal.getInstance())) != null) {
      return new TypedOperand(newValue, EdmDecimal.getInstance());
    }

    // Float
    if ((newValue = tryCast(literal, EdmSingle.getInstance())) != null) {
      return new TypedOperand(newValue, EdmSingle.getInstance());
    }

    if ((newValue = tryCast(literal, EdmDouble.getInstance())) != null) {
      return new TypedOperand(newValue, EdmDouble.getInstance());
    }

    throw new ODataApplicationException("Could not determine type for literal " + literal,
        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
  }

  @Override
  public EdmProperty getEdmProperty() {
    return null;
  }
}
