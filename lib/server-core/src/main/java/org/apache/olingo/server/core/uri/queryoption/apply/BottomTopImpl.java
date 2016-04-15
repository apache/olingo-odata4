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
package org.apache.olingo.server.core.uri.queryoption.apply;

import org.apache.olingo.server.api.uri.queryoption.apply.BottomTop;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

/**
 * Represents a transformation with one of the pre-defined methods
 * <code>bottomcount</code>, <code>bottompercent</code>, <code>bottomsum</code>,
 * <code>topcount</code>, <code>toppercent</code>, <code>topsum</code>.
 */
public class BottomTopImpl implements BottomTop {

  private Method method;
  private Expression number;
  private Expression value;

  @Override
  public Kind getKind() {
    return Kind.BOTTOM_TOP;
  }

  @Override
  public Method getMethod() {
    return method;
  }

  public BottomTopImpl setMethod(final Method method) {
    this.method = method;
    return this;
  }

  @Override
  public Expression getNumber() {
    return number;
  }

  public BottomTopImpl setNumber(final Expression number) {
    this.number = number;
    return this;
  }

  @Override
  public Expression getValue() {
    return value;
  }

  public BottomTopImpl setValue(final Expression value) {
    this.value = value;
    return this;
  }
}
