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
package org.apache.olingo.commons.core.edm.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.annotation.EdmConstantExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlConstantExpression;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class EdmConstantExpressionImpl extends AbstractEdmExpression implements EdmConstantExpression {

  private Valuable value;
  private EdmPrimitiveType type;
  private final CsdlConstantExpression constExprConstruct;

  public EdmConstantExpressionImpl(Edm edm, final CsdlConstantExpression constExprConstruct) {
    super(edm, constExprConstruct.getType().toString());
    this.constExprConstruct = constExprConstruct;
  }

  @Override
  public Valuable getValue() {
    if(value == null){
      build();
    }
    return value;
  }

  @Override
  public String getValueAsString() {
    return constExprConstruct.getValue();
    
//    if (value == null) {
//      build();
//    }
//    if (value == null) {
//      return "";
//    } else if (value.isEnum()) {
//      return value.getValue().toString();
//    } else if (value.isGeospatial()) {
//      return value.toString();
//    } else {
//      // TODO: check after copied from ClientPrimitiveValueImpl
//      try {
//        return type.valueToString(value.getValue(), null, null,
//            Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null);
//      } catch (EdmPrimitiveTypeException e) {
//        throw new IllegalArgumentException(e);
//      }
//    }
  }

  private void build() {
    if (constExprConstruct.getType() == CsdlConstantExpression.ConstantExpressionType.EnumMember) {
      // TODO: Delete ProeprtyValue here
      final List<Property> enumValues = new ArrayList<Property>();
      String enumTypeName = null;
      for (String split : StringUtils.split(constExprConstruct.getValue(), ' ')) {
        final String[] enumSplit = StringUtils.split(split, '/');
        enumTypeName = enumSplit[0];
        enumValues.add(new Property(enumSplit[0], null, ValueType.ENUM, enumSplit[1]));
      }
      if (enumValues.size() == 1) {
        value = enumValues.get(0);
      } else {
        final List<Property> collValue = new ArrayList<Property>();
        for (Property enumValue : enumValues) {
          collValue.add(enumValue);
        }
        value = new Property(enumTypeName, null, ValueType.COLLECTION_ENUM, collValue);
      }
      type = null;
    } else {
      EdmPrimitiveTypeKind kind;
      switch (constExprConstruct.getType()) {
      case Binary:
        kind = EdmPrimitiveTypeKind.Binary;
        break;
      case Bool:
        kind = EdmPrimitiveTypeKind.Boolean;
        break;
      case Date:
        kind = EdmPrimitiveTypeKind.Date;
        break;
      case DateTimeOffset:
        kind = EdmPrimitiveTypeKind.DateTimeOffset;
        break;
      case Decimal:
        kind = EdmPrimitiveTypeKind.Decimal;
        break;
      case Duration:
        kind = EdmPrimitiveTypeKind.Duration;
        break;
      case Float:
        kind = EdmPrimitiveTypeKind.Single;
        break;
      case Guid:
        kind = EdmPrimitiveTypeKind.Guid;
        break;
      case Int:
        kind = EdmPrimitiveTypeKind.Int32;
        break;
      case TimeOfDay:
        kind = EdmPrimitiveTypeKind.TimeOfDay;
        break;
      case String:
      default:
        kind = EdmPrimitiveTypeKind.String;
      }
      type = EdmPrimitiveTypeFactory.getInstance(kind);
      try {
        final Object valueOfString = type.valueOfString(constExprConstruct.getValue(),
            null, null, Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null,
            type.getDefaultType());
        value = new Property(kind.getFullQualifiedName().getName(), null, ValueType.PRIMITIVE, valueOfString);
      } catch (EdmPrimitiveTypeException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
}
