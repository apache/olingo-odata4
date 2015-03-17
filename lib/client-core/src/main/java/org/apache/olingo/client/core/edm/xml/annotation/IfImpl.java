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
package org.apache.olingo.client.core.edm.xml.annotation;

import org.apache.olingo.commons.api.edm.provider.annotation.AnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.If;

public class IfImpl extends AbstractAnnotatableDynamicAnnotationExpression implements If {

  private static final long serialVersionUID = -8571383625077590656L;

  private AnnotationExpression guard;

  private AnnotationExpression _then;

  private AnnotationExpression _else;

  @Override
  public AnnotationExpression getGuard() {
    return guard;
  }

  public void setGuard(final AnnotationExpression guard) {
    this.guard = guard;
  }

  @Override
  public AnnotationExpression getThen() {
    return _then;
  }

  public void setThen(final AnnotationExpression _then) {
    this._then = _then;
  }

  @Override
  public AnnotationExpression getElse() {
    return _else;
  }

  public void setElse(final AnnotationExpression _else) {
    this._else = _else;
  }

}
