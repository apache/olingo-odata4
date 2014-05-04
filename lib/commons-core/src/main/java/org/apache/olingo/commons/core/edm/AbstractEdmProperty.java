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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;

public abstract class AbstractEdmProperty extends EdmElementImpl implements EdmProperty {

  private EdmType propertyType;

  public AbstractEdmProperty(final Edm edm, final String name) {
    super(edm, name);
  }

  protected abstract EdmTypeInfo getTypeInfo();

  @Override
  public boolean isPrimitive() {
    return getTypeInfo().isPrimitiveType();
  }

  @Override
  public EdmType getType() {
    if (propertyType == null) {
      propertyType = getTypeInfo().getType();
      if (propertyType == null) {
        throw new EdmException("Cannot find type with name: " + getTypeInfo().getFullQualifiedName());
      }
    }

    return propertyType;
  }

  @Override
  public boolean isCollection() {
    return getTypeInfo().isCollection();
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.Property;
  }

  @Override
  public String getAnnotationsTargetPath() {
    return getName();
  }

}
