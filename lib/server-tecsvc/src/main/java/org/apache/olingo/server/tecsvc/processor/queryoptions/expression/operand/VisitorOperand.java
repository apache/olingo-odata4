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
import java.util.HashMap;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.primitive.EdmNull;

public abstract class VisitorOperand {
  final static private HashMap<EdmType, Class<?>> defaultTypeMapping = new HashMap<EdmType, Class<?>>();
  protected Object value;
  protected static final OData oData;
  protected static final EdmPrimitiveType primNull = EdmNull.getInstance();
  protected static final EdmPrimitiveType primString;
  protected static final EdmPrimitiveType primBoolean;
  protected static final EdmPrimitiveType primDateTimeOffset;
  protected static final EdmPrimitiveType primDate;
  protected static final EdmPrimitiveType primTimeOfDay;
  protected static final EdmPrimitiveType primDuration;
  protected static final EdmPrimitiveType primSByte;
  protected static final EdmPrimitiveType primByte;
  protected static final EdmPrimitiveType primInt16;
  protected static final EdmPrimitiveType primInt32;
  protected static final EdmPrimitiveType primInt64;
  protected static final EdmPrimitiveType primDecimal;
  protected static final EdmPrimitiveType primSingle;
  protected static final EdmPrimitiveType primDouble;

  static {
    oData = OData.newInstance();
    primString = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String);
    primBoolean = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean);
    primDateTimeOffset = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.DateTimeOffset);
    primDate = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date);
    primTimeOfDay = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.TimeOfDay);
    primDuration = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration);
    primSByte = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte);
    primByte = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte);
    primInt16 = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16);
    primInt32 = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32);
    primInt64 = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int64);
    primDecimal = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal);
    primSingle = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Single);
    primDouble = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double);

    defaultTypeMapping.put(primByte, BigInteger.class);
    defaultTypeMapping.put(primSByte, BigInteger.class);
    defaultTypeMapping.put(primInt16, BigInteger.class);
    defaultTypeMapping.put(primInt32, BigInteger.class);
    defaultTypeMapping.put(primInt64, BigInteger.class);

    defaultTypeMapping.put(primSingle, BigDecimal.class);
    defaultTypeMapping.put(primDouble, BigDecimal.class);
    defaultTypeMapping.put(primDecimal, BigDecimal.class);
  }

  public VisitorOperand(final Object value) {
    this.value = value;
  }

  public abstract TypedOperand asTypedOperand() throws ODataApplicationException;

  public abstract TypedOperand asTypedOperand(EdmPrimitiveType type) throws ODataApplicationException;

  public abstract EdmProperty getEdmProperty();

  public Object getValue() {
    return value;
  }

  protected Object castTo(final String value, final EdmPrimitiveType type) throws EdmPrimitiveTypeException {
    final EdmProperty edmProperty = getEdmProperty();

    if (edmProperty != null) {
      return type.valueOfString(value, edmProperty.isNullable(), edmProperty.getMaxLength(),
          edmProperty.getPrecision(), edmProperty.getScale(),
          edmProperty.isUnicode(), getDefaultType(type));
    } else {
      return type.valueOfString(value, null, null, null, null, null, getDefaultType(type));
    }
  }

  protected Class<?> getDefaultType(final EdmPrimitiveType type) {
    return defaultTypeMapping.get(type) != null ? defaultTypeMapping.get(type) : type.getDefaultType();
  }

  protected Object tryCast(final String literal, final EdmPrimitiveType type) {
    try {
      return castTo(type.fromUriLiteral(literal), type);
    } catch (EdmPrimitiveTypeException e) {
      return null;
    }
  }

}
