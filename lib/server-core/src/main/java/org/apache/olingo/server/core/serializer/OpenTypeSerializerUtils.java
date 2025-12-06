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
package org.apache.olingo.server.core.serializer;

import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ServiceMetadata;

import java.util.Collections;
import java.util.List;

public class OpenTypeSerializerUtils {
  public static EdmProperty generateDynamicEdmProperty(final ServiceMetadata metadata, final Property property) {
    return new EdmProperty() {
      @Override
      public String getMimeType() {
        return null;
      }

      @Override
      public boolean isPrimitive() {
        return property.isPrimitive();
      }

      @Override
      public boolean isNullable() {
        return true;
      }

      @Override
      public Integer getMaxLength() {
        return null;
      }

      @Override
      public Integer getPrecision() {
        return null;
      }

      @Override
      public Integer getScale() {
        return null;
      }

      @Override
      public String getScaleAsString() {
        return null;
      }

      @Override
      public SRID getSrid() {
        return null;
      }

      @Override
      public boolean isUnicode() {
        return true;
      }

      @Override
      public String getDefaultValue() {
        return null;
      }

      @Override
      public EdmType getTypeWithAnnotations() {
        return getType();
      }

      @Override
      public EdmAnnotation getAnnotation(EdmTerm term, String qualifier) {
        return null;
      }

      @Override
      public List<EdmAnnotation> getAnnotations() {
        return Collections.emptyList();
      }

      @Override
      public EdmMapping getMapping() {
        return null;
      }

      @Override
      public String getName() {
        return property.getName();
      }

      @Override
      public EdmType getType() {
        ValueType valueType = property.getValueType();
        if (ValueType.PRIMITIVE.equals(valueType) || ValueType.COLLECTION_PRIMITIVE.equals(valueType)
            || ValueType.GEOSPATIAL.equals(valueType) || ValueType.COLLECTION_GEOSPATIAL.equals(valueType)) {
          return EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.valueOfFQN(property.getType()));
        } else if (ValueType.ENUM.equals(valueType) || ValueType.COLLECTION_ENUM.equals(valueType)) {
          return metadata.getEdm().getEnumType(new FullQualifiedName(property.getType()));
        } else if (ValueType.COMPLEX.equals(valueType) || ValueType.COLLECTION_COMPLEX.equals(valueType)) {
          return metadata.getEdm().getComplexType(new FullQualifiedName(property.getType()));
        } else if (ValueType.ENTITY.equals(valueType) || ValueType.COLLECTION_ENTITY.equals(valueType)) {
          return metadata.getEdm().getEntityType(new FullQualifiedName(property.getType()));
        }
        return null;
      }

      @Override
      public boolean isCollection() {
        return property.isCollection();
      }
    };
  }

  private OpenTypeSerializerUtils() {
  }
}
