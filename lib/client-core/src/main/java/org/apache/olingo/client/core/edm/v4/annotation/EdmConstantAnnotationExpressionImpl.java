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
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.annotation.EdmConstantAnnotationExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicAnnotationExpression;
import org.apache.olingo.commons.core.domain.v4.ODataCollectionValueImpl;
import org.apache.olingo.commons.core.domain.v4.ODataEnumValueImpl;
import org.apache.olingo.commons.core.domain.v4.ODataPrimitiveValueImpl;

public class EdmConstantAnnotationExpressionImpl implements EdmConstantAnnotationExpression {

  private final ODataValue value;

  public EdmConstantAnnotationExpressionImpl(final Edm edm, final ConstantAnnotationExpression constExprConstruct) {
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
      final ODataPrimitiveValueImpl.BuilderImpl primitiveValueBuilder =
              new ODataPrimitiveValueImpl.BuilderImpl(edm.getServiceMetadata().getDataServiceVersion());
      primitiveValueBuilder.setText(constExprConstruct.getValue());

      switch (constExprConstruct.getType()) {
        case Binary:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Binary);
          break;

        case Bool:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Boolean);
          break;

        case Date:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Date);
          break;

        case DateTimeOffset:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.DateTimeOffset);
          break;

        case Decimal:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Decimal);
          break;

        case Duration:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Duration);
          break;

        case Float:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Single);
          break;

        case Guid:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Guid);
          break;

        case Int:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.Int32);
          break;

        case TimeOfDay:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.TimeOfDay);
          break;

        case String:
        default:
          primitiveValueBuilder.setType(EdmPrimitiveTypeKind.String);
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
