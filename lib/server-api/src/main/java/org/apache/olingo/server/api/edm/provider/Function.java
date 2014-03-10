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
package org.apache.olingo.server.api.edm.provider;

import java.util.List;

public class Function extends Operation {

  private boolean isComposable;

  public boolean isComposable() {
    return isComposable;
  }

  public Function setComposable(final boolean isComposable) {
    this.isComposable = isComposable;
    return this;
  }

  @Override
  public Function setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public Function setBound(final boolean isBound) {
    this.isBound = isBound;
    return this;
  }

  @Override
  public Function setEntitySetPath(final EntitySetPath entitySetPath) {
    this.entitySetPath = entitySetPath;
    return this;
  }

  @Override
  public Function setParameters(final List<Parameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  @Override
  public Function setReturnType(final ReturnType returnType) {
    this.returnType = returnType;
    return this;
  }
}
