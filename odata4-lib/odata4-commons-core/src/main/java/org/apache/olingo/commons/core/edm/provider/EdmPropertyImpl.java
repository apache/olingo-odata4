/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.provider.Property;

public class EdmPropertyImpl extends EdmElementImpl implements EdmProperty {

  public EdmPropertyImpl(final Property property) {
    super(property.getName());
  }

  @Override
  public EdmType getType() {
    return null;
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public EdmMapping getMapping() {
    return null;
  }

  @Override
  public String getMimeType() {
    return null;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public Boolean isNullable() {
    return null;
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
  public Boolean isUnicode() {
    return null;
  }

  @Override
  public String getDefaultValue() {
    return null;
  }

}
