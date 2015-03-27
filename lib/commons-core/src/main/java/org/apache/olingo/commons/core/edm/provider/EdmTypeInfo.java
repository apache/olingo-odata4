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
package org.apache.olingo.commons.core.edm.provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class EdmTypeInfo {

  public static class Builder {

    private String typeExpression;
    private String defaultNamespace;
    private Edm edm;

    public Builder setTypeExpression(final String typeExpression) {
      this.typeExpression = typeExpression;
      return this;
    }

    public Builder setDefaultNamespace(final String defaultNamespace) {
      this.defaultNamespace = defaultNamespace;
      return this;
    }

    public Builder setEdm(final Edm edm) {
      this.edm = edm;
      return this;
    }

    public EdmTypeInfo build() {
      return new EdmTypeInfo(edm, typeExpression.indexOf('.') == -1 && StringUtils.isNotBlank(defaultNamespace)
          ? defaultNamespace + "." + typeExpression
          : typeExpression);
    }
  }

  private final boolean collection;
  private final FullQualifiedName fullQualifiedName;
  private EdmPrimitiveTypeKind primitiveType;
  private EdmTypeDefinition typeDefinition;
  private EdmEnumType enumType;
  private EdmComplexType complexType;
  private EdmEntityType entityType;

  private EdmTypeInfo(final Edm edm, final String typeExpression) {
    String baseType;
    final int collStartIdx = typeExpression.indexOf("Collection(");
    final int collEndIdx = typeExpression.lastIndexOf(')');
    if (collStartIdx == -1) {
      baseType = typeExpression;
      collection = false;
    } else {
      if (collEndIdx == -1) {
        throw new IllegalArgumentException("Malformed type: " + typeExpression);
      }

      collection = true;
      baseType = typeExpression.substring(collStartIdx + 11, collEndIdx);
    }

    baseType = baseType.replaceAll("^#", "");

    final String typeName;
    final String namespace;

    final int lastDotIdx = baseType.lastIndexOf('.');
    if (lastDotIdx == -1) {
      namespace = EdmPrimitiveType.EDM_NAMESPACE;
      typeName = baseType;
    } else {
      namespace = baseType.substring(0, lastDotIdx);
      typeName = baseType.substring(lastDotIdx + 1);
    }

    if (StringUtils.isBlank(typeName)) {
      throw new IllegalArgumentException("Null or empty type name in " + typeExpression);
    }

    fullQualifiedName = new FullQualifiedName(namespace, typeName);

    try {
      primitiveType = EdmPrimitiveTypeKind.valueOf(fullQualifiedName.getName());
    } catch (final IllegalArgumentException e) {
      primitiveType = null;
    }
    if (primitiveType == null && edm != null) {
      typeDefinition = edm.getTypeDefinition(fullQualifiedName);
      if (typeDefinition == null) {
        enumType = edm.getEnumType(fullQualifiedName);
        if (enumType == null) {
          complexType = edm.getComplexType(fullQualifiedName);
          if (complexType == null) {
            entityType = edm.getEntityType(fullQualifiedName);
          }
        }
      }
    }
  }

  public String internal() {
    final StringBuilder deserialize = new StringBuilder();

    if (isCollection()) {
      deserialize.append("Collection(");
    }

    deserialize.append(getFullQualifiedName().toString());

    if (isCollection()) {
      deserialize.append(")");
    }

    return deserialize.toString();
  }

  public String external() {
    final StringBuilder serialize = new StringBuilder();

    if (isCollection()) {
      serialize.append('#');
      serialize.append("Collection(");
    }

    if (isPrimitiveType()) {
      serialize.append(getFullQualifiedName().getName());
    }else{
      serialize.append(getFullQualifiedName().toString());
    }

    if (isCollection()) {
      serialize.append(")");
    }

    if (!isPrimitiveType() && !isCollection()) {
      serialize.insert(0, '#');
    }

    return serialize.toString();
  }

  public boolean isCollection() {
    return collection;
  }

  public FullQualifiedName getFullQualifiedName() {
    return fullQualifiedName;
  }

  public boolean isPrimitiveType() {
    return primitiveType != null;
  }

  public EdmPrimitiveTypeKind getPrimitiveTypeKind() {
    return primitiveType;
  }

  public boolean isTypeDefinition() {
    return typeDefinition != null;
  }

  public EdmTypeDefinition getTypeDefinition() {
    return typeDefinition;
  }

  public boolean isEnumType() {
    return enumType != null;
  }

  public EdmEnumType getEnumType() {
    return enumType;
  }

  public boolean isComplexType() {
    return complexType != null;
  }

  public EdmComplexType getComplexType() {
    return complexType;
  }

  public boolean isEntityType() {
    return entityType != null;
  }

  public EdmEntityType getEntityType() {
    return entityType;
  }

  public EdmType getType() {
    return isPrimitiveType()
        ? EdmPrimitiveTypeFactory.getInstance(getPrimitiveTypeKind())
        : isTypeDefinition()
            ? getTypeDefinition()
            : isEnumType()
                ? getEnumType()
                : isComplexType()
                    ? getComplexType()
                    : isEntityType()
                        ? getEntityType()
                        : null;
  }
}
