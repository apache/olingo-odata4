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
package org.apache.olingo.commons.core.edm.annotation;

import org.apache.olingo.commons.api.edm.annotation.EdmAnnotationExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmIf;

public class EdmIfImpl extends AbstractEdmAnnotatableDynamicAnnotationExpression implements EdmIf {

  private final EdmAnnotationExpression guard;

  private final EdmAnnotationExpression _then;

  private final EdmAnnotationExpression _else;

  public EdmIfImpl(final EdmAnnotationExpression guard,
      final EdmAnnotationExpression _then, final EdmAnnotationExpression _else) {

    this.guard = guard;
    this._then = _then;
    this._else = _else;
  }

  @Override
  public EdmAnnotationExpression getGuard() {
    return guard;
  }

  @Override
  public EdmAnnotationExpression getThen() {
    return _then;
  }

  @Override
  public EdmAnnotationExpression getElse() {
    return _else;
  }

}
