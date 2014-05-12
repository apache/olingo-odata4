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

import java.util.List;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.annotation.EdmPropertyValue;
import org.apache.olingo.commons.api.edm.annotation.EdmRecord;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.annotation.AbstractEdmAnnotatableDynamicAnnotationExpression;

public class EdmRecordImpl extends AbstractEdmAnnotatableDynamicAnnotationExpression implements EdmRecord {

  private final Edm edm;

  private final List<EdmPropertyValue> propertyValues;

  private EdmStructuredType type;

  public EdmRecordImpl(final Edm edm, final String type, final List<EdmPropertyValue> propertyValues) {
    this.edm = edm;
    this.propertyValues = propertyValues;

    if (type != null) {
      final EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(type).build();
      this.type = typeInfo.getEntityType() == null ? typeInfo.getComplexType() : typeInfo.getEntityType();
    }
  }

  @Override
  public List<EdmPropertyValue> getPropertyValues() {
    return propertyValues;
  }

  @Override
  public EdmStructuredType getType() {
    return type;
  }

}
