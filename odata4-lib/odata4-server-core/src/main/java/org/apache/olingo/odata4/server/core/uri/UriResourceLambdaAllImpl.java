/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.server.core.uri;

import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.odata4.server.api.uri.UriResourceKind;
import org.apache.olingo.odata4.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.odata4.server.core.uri.queryoption.expression.ExpressionImpl;

public class UriResourceLambdaAllImpl extends UriResourceTypedImpl implements UriResourceLambdaAll {
  protected EdmProperty property;
  private String lambdaVariable;
  private ExpressionImpl expression;

  public UriResourceLambdaAllImpl() {
    super(UriResourceKind.lambdaAll);
  }

  @Override
  public EdmType getType() {
    return EdmPrimitiveTypeKind.Boolean.getEdmPrimitiveTypeInstance();
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public String getLambdaVariable() {
    return lambdaVariable;
  }

  public UriResourceLambdaAllImpl setLamdaVariable(final String lambdaVariable) {
    this.lambdaVariable = lambdaVariable;
    return this;
  };

  @Override
  public Expression getExpression() {
    return expression;
  }

  public UriResourceLambdaAllImpl setExpression(final ExpressionImpl expression) {
    this.expression = expression;
    return this;
  };

  @Override
  public String toString() {
    return "all";
  }

}
