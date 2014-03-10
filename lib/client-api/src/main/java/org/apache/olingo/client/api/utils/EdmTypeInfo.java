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
package org.apache.olingo.client.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EdmTypeInfo {

  private final String typeExpression;

  private final boolean collection;

  private final FullQualifiedName fullQualifiedName;

  public EdmTypeInfo(final String typeExpression, final String defaultNamespace) {
    this(typeExpression.indexOf('.') == -1
            ? defaultNamespace + "." + typeExpression
            : typeExpression);
  }

  public EdmTypeInfo(final String typeExpression) {
    this.typeExpression = typeExpression;

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

    final int lastDotIdx = baseType.lastIndexOf('.');
    if (lastDotIdx == -1) {
      throw new IllegalArgumentException("Cannot find namespace or alias in " + typeExpression);
    }
    final String namespace = baseType.substring(0, lastDotIdx);
    final String typeName = baseType.substring(lastDotIdx + 1);
    if (StringUtils.isBlank(typeName)) {
      throw new IllegalArgumentException("Null or empty type name in " + typeExpression);
    }

    this.fullQualifiedName = new FullQualifiedName(namespace, typeName);
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

}
