/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.v4.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.edm.xml.v4.annotation.ConstantAnnotationExpression;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.annotation.EdmConstantAnnotationExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.domain.v4.ODataCollectionValueImpl;
import org.apache.olingo.commons.core.domain.v4.ODataEnumValueImpl;
import org.apache.olingo.commons.core.domain.v4.ODataPrimitiveValueImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class EdmConstantAnnotationExpressionImpl implements EdmConstantAnnotationExpression {

  private final ODataValue value;

  public EdmConstantAnnotationExpressionImpl(final ConstantAnnotationExpression constExprConstruct) {
    if (constExprConstruct.getType() == ConstantAnnotationExpression.Type.EnumMember) {
      final List<ODataEnumValue> enumValues = new ArrayList<ODataEnumValue>();
      String enumTypeName = null;
      for (String split : StringUtils.split(constExprConstruct.getValue(), ' ')) {
        final String[] enumSplit = StringUtils.split(split, '/');
        enumTypeName = enumSplit[0];
        enumValues.add(new ODataEnumValueImpl(enumSplit[0], enumSplit[1]));
      }
      if (enumValues.size() == 1) {
        value = enumValues.get(0);
      } else {
        final ODataCollectionValueImpl collValue = new ODataCollectionValueImpl(enumTypeName);
        for (ODataValue enumValue : enumValues) {
          collValue.add(enumValue);
        }
        value = collValue;
      }
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
      final ODataPrimitiveValueImpl.BuilderImpl primitiveValueBuilder =
          new ODataPrimitiveValueImpl.BuilderImpl(ODataServiceVersion.V40);
      primitiveValueBuilder.setType(kind);
      try {
        final EdmPrimitiveType primitiveType = EdmPrimitiveTypeFactory.getInstance(kind);
        primitiveValueBuilder.setValue(
            primitiveType.valueOfString(constExprConstruct.getValue(),
                null, null, Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null,
                primitiveType.getDefaultType()));
      } catch (final EdmPrimitiveTypeException e) {
        throw new IllegalArgumentException(e);
      }

      value = primitiveValueBuilder.build();
    }
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public EdmConstantAnnotationExpression asConstant() {
    return this;
  }

  @Override
  public boolean isDynamic() {
    return false;
  }

  @Override
  public EdmDynamicAnnotationExpression asDynamic() {
    return null;
  }

  @Override
  public ODataValue getValue() {
    return value;
  }

}
