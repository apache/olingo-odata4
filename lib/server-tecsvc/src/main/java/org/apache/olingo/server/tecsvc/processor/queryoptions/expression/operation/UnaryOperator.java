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
package org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.TypedOperand;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.VisitorOperand;

public class UnaryOperator {

  protected static final OData oData;
  protected static final EdmPrimitiveType primBoolean;
  protected static final EdmPrimitiveType primDuration;

  static {
    oData = OData.newInstance();
    primBoolean = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean);
    primDuration = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration);
  }

  final private TypedOperand operand;

  public UnaryOperator(final VisitorOperand operand) throws ODataApplicationException {
    this.operand = operand.asTypedOperand();
  }

  public VisitorOperand minusOperation() throws ODataApplicationException {
    if (operand.isNull()) {
      return operand;
    } else if (operand.isIntegerType()) {
      return new TypedOperand(operand.getTypedValue(BigInteger.class).negate(), operand.getType());
    } else if (operand.isDecimalType() || operand.is(primDuration)) {
      return new TypedOperand(operand.getTypedValue(BigDecimal.class).negate(), operand.getType());
    } else {
      throw new ODataApplicationException("Unsupported type", HttpStatusCode.BAD_REQUEST.getStatusCode(),
          Locale.ROOT);
    }
  }

  public VisitorOperand notOperation() throws ODataApplicationException {
    if (operand.isNull()) {
      return operand;
    } else if (operand.is(primBoolean)) {
      return new TypedOperand(!operand.getTypedValue(Boolean.class), operand.getType());
    } else {
      throw new ODataApplicationException("Unsupported type", HttpStatusCode.BAD_REQUEST.getStatusCode(),
          Locale.ROOT);
    }
  }
}
