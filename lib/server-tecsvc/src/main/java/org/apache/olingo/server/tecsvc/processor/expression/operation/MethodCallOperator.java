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
package org.apache.olingo.server.tecsvc.processor.expression.operation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmBoolean;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDate;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.commons.core.edm.primitivetype.EdmTimeOfDay;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.tecsvc.processor.expression.operand.TypedOperand;
import org.apache.olingo.server.tecsvc.processor.expression.operand.VisitorOperand;

public class MethodCallOperator {

  final private List<VisitorOperand> parameters;

  public MethodCallOperator(List<VisitorOperand> parameters) {
    this.parameters = parameters;
  }

  public VisitorOperand endsWith() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).endsWith(params.get(1));
      }
    }, EdmBoolean.getInstance());
  }

  public VisitorOperand indexOf() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).indexOf(params.get(1));
      }
    }, EdmInt32.getInstance());
  }

  public VisitorOperand startsWith() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).startsWith(params.get(1));
      }
    }, EdmBoolean.getInstance());
  }

  public VisitorOperand toLower() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).toLowerCase();
      }
    }, EdmString.getInstance());
  }

  public VisitorOperand toUpper() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).toUpperCase();
      }
    }, EdmString.getInstance());
  }

  public VisitorOperand trim() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).trim();
      }
    }, EdmString.getInstance());
  }

  public VisitorOperand substring() throws ODataApplicationException {
    final TypedOperand valueOperand = parameters.get(0).asTypedOperand();
    final TypedOperand startOperand = parameters.get(1).asTypedOperand();

    if (valueOperand.isNull() || startOperand.isNull()) {
      return new TypedOperand(null, EdmString.getInstance());
    } else if (valueOperand.is(EdmString.getInstance()) && startOperand.isIntegerType()) {
      final String value = valueOperand.getTypedValue(String.class);
      final BigInteger start = startOperand.getTypedValue(BigInteger.class);
      int end = value.length();

      if (parameters.size() == 3) {
        final TypedOperand lengthOperand = parameters.get(2).asTypedOperand();

        if (lengthOperand.isNull()) {
          return new TypedOperand(null, EdmString.getInstance());
        } else if (lengthOperand.isIntegerType()) {
          end = Math.min(start.add(lengthOperand.getTypedValue(BigInteger.class)).intValue(), value.length());
        } else {
          throw new ODataApplicationException("Third substring parameter should be Edm.Int32",
              HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
        }
      }

      return new TypedOperand(value.substring(Math.min(start.intValue(), value.length()), end),
          EdmString.getInstance());
    } else {
      throw new ODataApplicationException("Substring has invalid parameters. First parameter should be Edm.String,"
          + " second parameter should be Edm.Int32", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  public VisitorOperand contains() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).contains(params.get(1));
      }
    }, EdmBoolean.getInstance());
  }

  public VisitorOperand concat() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0) + params.get(1);
      }
    }, EdmString.getInstance());
  }

  public VisitorOperand length() throws ODataApplicationException {
    return stringFunction(new StringFunction() {
      @Override
      public Object perform(List<String> params) {
        return params.get(0).length();
      }
    }, EdmInt32.getInstance());
  }

  public VisitorOperand year() throws ODataApplicationException {
    return dateFunction(new DateFunction() {
      @Override
      public Object perform(Calendar calendar, TypedOperand operand) {
        return calendar.get(Calendar.YEAR);
      }
    }, EdmInt32.getInstance(), EdmDateTimeOffset.getInstance(), EdmDate.getInstance());
  }

  public VisitorOperand month() throws ODataApplicationException {
    return dateFunction(new DateFunction() {
      @Override
      public Object perform(Calendar calendar, TypedOperand operand) {
        // Month is 0-based!
        return calendar.get(Calendar.MONTH) + 1;
      }
    }, EdmInt32.getInstance(), EdmDateTimeOffset.getInstance(), EdmDate.getInstance());
  }

  public VisitorOperand day() throws ODataApplicationException {
    return dateFunction(new DateFunction() {
      @Override
      public Object perform(Calendar calendar, TypedOperand operand) {
        return calendar.get(Calendar.DAY_OF_MONTH);
      }
    }, EdmInt32.getInstance(), EdmDateTimeOffset.getInstance(), EdmDate.getInstance());
  }

  public VisitorOperand hour() throws ODataApplicationException {
    return dateFunction(new DateFunction() {
      @Override
      public Object perform(Calendar calendar, TypedOperand operand) {
        return calendar.get(Calendar.HOUR_OF_DAY);
      }
    }, EdmInt32.getInstance(), EdmDateTimeOffset.getInstance(), EdmTimeOfDay.getInstance());
  }

  public VisitorOperand minute() throws ODataApplicationException {
    return dateFunction(new DateFunction() {
      @Override
      public Object perform(Calendar calendar, TypedOperand operand) {
        return calendar.get(Calendar.MINUTE);
      }
    }, EdmInt32.getInstance(), EdmDateTimeOffset.getInstance(), EdmTimeOfDay.getInstance());
  }

  public VisitorOperand second() throws ODataApplicationException {
    return dateFunction(new DateFunction() {
      @Override
      public Object perform(Calendar calendar, TypedOperand operand) {
        return calendar.get(Calendar.SECOND);
      }
    }, EdmInt32.getInstance(), EdmDateTimeOffset.getInstance(), EdmTimeOfDay.getInstance());
  }

  public VisitorOperand fractionalseconds() throws ODataApplicationException {
    return dateFunction(new DateFunction() {
      @Override
      public Object perform(Calendar calendar, TypedOperand operand) {
        if (operand.getValue() instanceof Timestamp) {
          return new BigDecimal(operand.getTypedValue(Timestamp.class).getNanos()).divide(BigDecimal
              .valueOf(1000 * 1000 * 1000));
        } else {
          return new BigDecimal(calendar.get(Calendar.MILLISECOND)).divide(BigDecimal.valueOf(1000));
        }
      }
    }, EdmDecimal.getInstance(), EdmDateTimeOffset.getInstance(), EdmTimeOfDay.getInstance());
  }

  public VisitorOperand round() throws ODataApplicationException {
    final TypedOperand operand = parameters.get(0).asTypedOperand();
    if (operand.isNull()) {
      return operand;
    } else if (operand.isDecimalType()) {
      return new TypedOperand(operand.getTypedValue(BigDecimal.class).round(new MathContext(1, RoundingMode.HALF_UP)),
          operand.getType());
    } else {
      throw new ODataApplicationException("Invalid type", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  public VisitorOperand floor() throws ODataApplicationException {
    final TypedOperand operand = parameters.get(0).asTypedOperand();
    if (operand.isNull()) {
      return operand;
    } else if (operand.isDecimalType()) {
      return new TypedOperand(operand.getTypedValue(BigDecimal.class).round(new MathContext(1, RoundingMode.FLOOR)),
          operand.getType());
    } else {
      throw new ODataApplicationException("Invalid type", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  public VisitorOperand ceiling() throws ODataApplicationException {
    final TypedOperand operand = parameters.get(0).asTypedOperand();
    if (operand.isNull()) {
      return operand;
    } else if (operand.isDecimalType()) {
      return new TypedOperand(operand.getTypedValue(BigDecimal.class).round(new MathContext(1, RoundingMode.CEILING)),
          operand.getType());
    } else {
      throw new ODataApplicationException("Invalid type", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  private interface StringFunction {
    Object perform(List<String> params);
  }

  private interface DateFunction {
    Object perform(Calendar calendar, TypedOperand operand);
  }

  private VisitorOperand dateFunction(DateFunction f, EdmType returnType, EdmPrimitiveType... expectedTypes)
      throws ODataApplicationException {
    final TypedOperand operand = parameters.get(0).asTypedOperand();

    if (operand.is(expectedTypes)) {
      if (!operand.isNull()) {
        Calendar calendar = null;
        if (operand.is(EdmDate.getInstance())) {
          calendar = operand.getTypedValue(Calendar.class);
        } else if (operand.is(EdmDateTimeOffset.getInstance())) {
          final Timestamp timestamp = operand.getTypedValue(Timestamp.class);
          calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
          calendar.setTimeInMillis(timestamp.getTime());
        } else if (operand.is(EdmTimeOfDay.getInstance())) {
          calendar = operand.getTypedValue(Calendar.class);
        } else {
          throw new ODataApplicationException("Invalid type", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
        }

        return new TypedOperand(f.perform(calendar, operand), returnType);
      } else {
        return new TypedOperand(null, returnType);
      }
    } else {
      throw new ODataApplicationException("Invalid type", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  private VisitorOperand stringFunction(StringFunction f, EdmType returnValue) throws ODataApplicationException {
    List<String> stringParameters = getParametersAsString();
    if (stringParameters.contains(null)) {
      return new TypedOperand(null, returnValue);
    } else {
      return new TypedOperand(f.perform(stringParameters), returnValue);
    }
  }

  private List<String> getParametersAsString() throws ODataApplicationException {
    List<String> result = new ArrayList<String>();

    for (VisitorOperand param : parameters) {
      TypedOperand operand = param.asTypedOperand();
      if (operand.isNull()) {
        result.add(null);
      } else if (operand.is(EdmString.getInstance())) {
        result.add(operand.getTypedValue(String.class));
      } else {
        throw new ODataApplicationException("Invalid parameter. Expected Edm.String", HttpStatusCode.BAD_REQUEST
            .getStatusCode(), Locale.ROOT);
      }
    }

    return result;
  }
}
