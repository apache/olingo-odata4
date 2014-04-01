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
package org.apache.olingo.commons.core.edm;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdmTypeInfo {

  private static final Logger LOG = LoggerFactory.getLogger(EdmTypeInfo.class);

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
  private final Edm edm;

  private final String typeExpression;

  private final boolean collection;

  private final FullQualifiedName fullQualifiedName;

  private EdmPrimitiveTypeKind primitiveType;

  private EdmEnumType enumType;

  private EdmComplexType complexType;

  private EdmEntityType entityType;

  private EdmTypeInfo(final Edm edm, final String typeExpression) {
    this.edm = edm;

    String baseType;
    final int collStartIdx = typeExpression.indexOf("Collection(");
    final int collEndIdx = typeExpression.lastIndexOf(')');
    if (collStartIdx == -1) {
      baseType = typeExpression;
      this.collection = false;
    } else {
      if (collEndIdx == -1) {
        throw new IllegalArgumentException("Malformed type: " + typeExpression);
      }

      this.collection = true;
      baseType = typeExpression.substring(collStartIdx + 11, collEndIdx);
    }


    baseType = baseType.replaceAll("^#", "");

    final String typeName;
    final String namespace;

    final int lastDotIdx = baseType.lastIndexOf('.');
    if (lastDotIdx == -1) {
      namespace = EdmPrimitiveType.EDM_NAMESPACE;
      typeName = baseType;
      baseType = new FullQualifiedName(EdmPrimitiveType.EDM_NAMESPACE, baseType).toString();
    } else {
      namespace = baseType.substring(0, lastDotIdx);
      typeName = baseType.substring(lastDotIdx + 1);
    }

    if (StringUtils.isBlank(typeName)) {
      throw new IllegalArgumentException("Null or empty type name in " + typeExpression);
    }

    final StringBuilder exp = new StringBuilder();
    exp.append(baseType);

    this.typeExpression = (this.collection ? exp.insert(0, "Collection(").append(")") : exp).toString();
    this.fullQualifiedName = new FullQualifiedName(namespace, typeName);

    try {
      this.primitiveType = EdmPrimitiveTypeKind.valueOf(this.fullQualifiedName.getName());
    } catch (IllegalArgumentException e) {
      LOG.debug("{} does not appear to refer to an Edm primitive type", this.fullQualifiedName);
    }
    if (this.primitiveType == null && this.edm != null) {
      this.enumType = this.edm.getEnumType(this.fullQualifiedName);
      if (this.enumType == null) {
        this.complexType = this.edm.getComplexType(this.fullQualifiedName);
        if (this.complexType == null) {
          this.entityType = this.edm.getEntityType(this.fullQualifiedName);
        }
      }
    }
  }

  public String getTypeExpression() {
    return typeExpression;
  }

  public boolean isCollection() {
    return collection;
  }

  public FullQualifiedName getFullQualifiedName() {
    return fullQualifiedName;
  }

  public boolean isPrimitiveType() {
    return this.primitiveType != null;
  }

  public EdmPrimitiveTypeKind getPrimitiveTypeKind() {
    return primitiveType;
  }

  public boolean isEnumType() {
    return this.enumType != null;
  }

  public EdmEnumType getEnumType() {
    return enumType;
  }

  public boolean isComplexType() {
    return this.complexType != null;
  }

  public EdmComplexType getComplexType() {
    return complexType;
  }

  public boolean isEntityType() {
    return this.entityType != null;
  }

  public EdmEntityType getEntityType() {
    return entityType;
  }
}
