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

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.MethodCall;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedMethodCalls;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.VisitableExression;

public class MethodCallImpl extends ExpressionImpl implements MethodCall, VisitableExression {

  private SupportedMethodCalls method;
  private List<ExpressionImpl> parameters = new ArrayList<ExpressionImpl>();

  @Override
  public SupportedMethodCalls getMethod() {
    return method;
  }

  public MethodCallImpl setMethod(final SupportedMethodCalls methodCalls) {
    method = methodCalls;
    return this;
  }

  @Override
  public List<Expression> getParameters() {
    List<Expression> list = new ArrayList<Expression>();
    for (ExpressionImpl item : parameters) {
      list.add(item);
    }
    return list;
  }

  public MethodCallImpl addParameter(final ExpressionImpl readCommonExpression) {
    parameters.add(readCommonExpression);
    return this;
  }

  @Override
  public <T> T accept(final ExpressionVisitor<T> visitor) throws ExceptionVisitExpression, ODataApplicationException {
    List<T> userParameters = new ArrayList<T>();
    for (ExpressionImpl parameter : parameters) {
      userParameters.add(parameter.accept(visitor));
    }
    return visitor.visitMethodCall(method, userParameters);
  }

}
