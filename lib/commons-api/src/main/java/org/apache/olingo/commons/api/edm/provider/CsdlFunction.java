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
package org.apache.olingo.commons.api.edm.provider;

import java.util.List;

/**
 * The type Csdl function.
 */
public class CsdlFunction extends CsdlOperation {

  private boolean isComposable = false;

  /**
   * Is composable.
   *
   * @return the boolean
   */
  public boolean isComposable() {
    return isComposable;
  }

  /**
   * Sets composable.
   *
   * @param isComposable the is composable
   * @return the composable
   */
  public CsdlFunction setComposable(final boolean isComposable) {
    this.isComposable = isComposable;
    return this;
  }

  @Override
  public CsdlFunction setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public CsdlFunction setBound(final boolean isBound) {
    this.isBound = isBound;
    return this;
  }

  @Override
  public CsdlFunction setEntitySetPath(final String entitySetPath) {
    this.entitySetPath = entitySetPath;
    return this;
  }

  @Override
  public CsdlFunction setParameters(final List<CsdlParameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  @Override
  public CsdlFunction setReturnType(final CsdlReturnType returnType) {
    this.returnType = returnType;
    return this;
  }

  @Override
  public CsdlFunction setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
