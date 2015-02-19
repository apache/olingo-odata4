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
package org.apache.olingo.server.tecsvc.processor.expression.operand;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmByte;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDecimal;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDouble;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt16;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSByte;
import org.apache.olingo.commons.core.edm.primitivetype.EdmSingle;
import org.apache.olingo.server.api.ODataApplicationException;

public abstract class VisitorOperand {
  final static private HashMap<EdmType, Class<?>> defaultTypeMapping = new HashMap<EdmType, Class<?>>();
  protected Object value;

  static {
    defaultTypeMapping.put(EdmByte.getInstance(), BigInteger.class);
    defaultTypeMapping.put(EdmSByte.getInstance(), BigInteger.class);
    defaultTypeMapping.put(EdmInt16.getInstance(), BigInteger.class);
    defaultTypeMapping.put(EdmInt32.getInstance(), BigInteger.class);
    defaultTypeMapping.put(EdmInt64.getInstance(), BigInteger.class);

    defaultTypeMapping.put(EdmSingle.getInstance(), BigDecimal.class);
    defaultTypeMapping.put(EdmDouble.getInstance(), BigDecimal.class);
    defaultTypeMapping.put(EdmDecimal.getInstance(), BigDecimal.class);
  }

  public VisitorOperand(Object value) {
    this.value = value;
  }

  public abstract TypedOperand asTypedOperand() throws ODataApplicationException;

  public abstract TypedOperand asTypedOperand(EdmPrimitiveType... types) throws ODataApplicationException;

  public abstract EdmProperty getEdmProperty();

  public Object getValue() {
    return value;
  }

  protected Object castTo(final String value, EdmPrimitiveType type) throws EdmPrimitiveTypeException {
    final EdmProperty edmProperty = getEdmProperty();

    if (edmProperty != null) {
      return type.valueOfString(value, edmProperty.isNullable(), edmProperty.getMaxLength(),
          edmProperty.getPrecision(), edmProperty.getScale(),
          edmProperty.isUnicode(), getDefaultType(type));
    } else {
      return type.valueOfString(value, null, null, null, null, null, getDefaultType(type));
    }
  }

  protected Class<?> getDefaultType(EdmPrimitiveType type) {
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
