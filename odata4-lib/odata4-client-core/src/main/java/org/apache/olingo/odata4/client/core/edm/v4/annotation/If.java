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
package org.apache.olingo.odata4.client.core.edm.v4.annotation;

import org.apache.olingo.odata4.client.api.edm.v4.annotation.ExprConstruct;

public class If extends AnnotatedDynExprConstruct {

  private static final long serialVersionUID = 6752952406406218936L;

  private ExprConstruct guard;

  private ExprConstruct _then;

  private ExprConstruct _else;

  public ExprConstruct getGuard() {
    return guard;
  }

  public void setGuard(final ExprConstruct guard) {
    this.guard = guard;
  }

  public ExprConstruct getThen() {
    return _then;
  }

  public void setThen(final ExprConstruct _then) {
    this._then = _then;
  }

  public ExprConstruct getElse() {
    return _else;
  }

  public void setElse(final ExprConstruct _else) {
    this._else = _else;
  }

}
