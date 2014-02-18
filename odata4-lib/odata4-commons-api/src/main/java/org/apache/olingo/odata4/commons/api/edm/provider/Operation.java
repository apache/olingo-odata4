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
package org.apache.olingo.odata4.commons.api.edm.provider;

import java.util.List;

public abstract class Operation {

  protected String name;

  protected boolean isBound;

  // Do we need EntitySetPath as a class?
  protected EntitySetPath entitySetPath;

  protected List<Parameter> parameters;

  protected ReturnType returnType;

  // Annotations?
  public String getName() {
    return name;
  }

  public Operation setName(final String name) {
    this.name = name;
    return this;
  }

  public boolean isBound() {
    return isBound;
  }

  public Operation setBound(final boolean isBound) {
    this.isBound = isBound;
    return this;
  }

  public EntitySetPath getEntitySetPath() {
    return entitySetPath;
  }

  public Operation setEntitySetPath(final EntitySetPath entitySetPath) {
    this.entitySetPath = entitySetPath;
    return this;
  }

  public List<Parameter> getParameters() {
    return parameters;
  }

  public Operation setParameters(final List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public ReturnType getReturnType() {
    return returnType;
  }

  public Operation setReturnType(final ReturnType returnType) {
    this.returnType = returnType;
    return this;
  }
}
