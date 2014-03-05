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
package org.apache.olingo.odata4.client.core.edm;

import org.apache.olingo.odata4.client.api.edm.xml.v4.ReturnType;
import org.apache.olingo.odata4.client.api.utils.EdmTypeInfo;
import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.core.edm.AbstractEdmReturnType;

public class EdmReturnTypeImpl extends AbstractEdmReturnType {

  private final ReturnType returnType;

  private final EdmTypeInfo returnTypeInfo;

  public static EdmReturnTypeImpl getInstance(final Edm edm, final ReturnType returnType) {
    final EdmTypeInfo returnTypeInfo = new EdmTypeInfo(returnType.getType());
    return new EdmReturnTypeImpl(edm, returnType, returnTypeInfo);
  }

  private EdmReturnTypeImpl(final Edm edm, final ReturnType returnType, final EdmTypeInfo returnTypeInfo) {
    super(edm, returnTypeInfo.getFullQualifiedName());
    this.returnType = returnType;
    this.returnTypeInfo = returnTypeInfo;
  }

  @Override
  public Boolean isNullable() {
    return returnType.isNullable();
  }

  @Override
  public Integer getMaxLength() {
    return returnType.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return returnType.getPrecision();
  }

  @Override
  public Integer getScale() {
    return returnType.getScale();
  }

  @Override
  public boolean isCollection() {
    return returnTypeInfo.isCollection();
  }

}
