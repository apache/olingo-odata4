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
package org.apache.olingo.odata4.server.core.uri.queryoption.expression;

import org.apache.olingo.odata4.commons.api.ODataApplicationException;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.Constant;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedConstants;

public class ConstantImpl extends ExpressionImpl implements Constant {

  EdmType type;
  SupportedConstants kind;

  @Override
  public <T> T accept(final ExpressionVisitor<T> visitor) throws ExceptionVisitExpression, ODataApplicationException {
    return visitor.visitConstant(kind);
  }

  @Override
  public boolean isNull() {
    return kind == SupportedConstants.NULL;
  }

  @Override
  public boolean isTrue() {
    return kind == SupportedConstants.TRUE;
  }

  @Override
  public boolean isFalse() {
    return kind == SupportedConstants.FALSE;
  }

  @Override
  public EdmType getType() {
    return type;
  }

  public ConstantImpl setType(final EdmType type) {
    this.type = type;
    return this;
  }

  @Override
  public SupportedConstants getKind() {
    return kind;
  }

  public ConstantImpl setKind(final SupportedConstants kind) {
    this.kind = kind;
    return this;
  }

}
