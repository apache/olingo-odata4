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

package org.apache.olingo.server.core.deserializer;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

import java.util.List;

public class OpenTypeDeserializerUtils {
  public static EdmProperty generateDynamicEdmProperty(final String propertyName, final EdmType edmType,
      final boolean isCollection) {
    return new EdmProperty() {

      @Override
      public EdmType getType() {
        return edmType;
      }

      @Override
      public boolean isCollection() {
        return isCollection;
      }

      @Override
      public String getName() {
        return propertyName;
      }

      @Override
      public EdmMapping getMapping() {
        return null;
      }

      @Override
      public EdmAnnotation getAnnotation(EdmTerm term, String qualifier) {
        return null;
      }

      @Override
      public List<EdmAnnotation> getAnnotations() {
        return null;
      }

      @Override
      public String getMimeType() {
        return null;
      }

      @Override
      public boolean isPrimitive() {
        return edmType instanceof EdmPrimitiveType;
      }

      @Override
      public boolean isNullable() {
        return false;
      }

      @Override
      public Integer getMaxLength() {
        return null;
      }

      @Override
      public Integer getPrecision() {
        if (EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.DateTimeOffset).equals(edmType)) {
          return 3;
        }
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
    };
  }

  private OpenTypeDeserializerUtils() {
  }
}
