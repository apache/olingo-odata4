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
package org.apache.olingo.server.core.uri;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

public class UriResourceLambdaAnyImpl extends UriResourceTypedImpl implements UriResourceLambdaAny {

  private final String lambdaVariable;
  private final Expression expression;

  public UriResourceLambdaAnyImpl(final String lambdaVariable, final Expression expression) {
    super(UriResourceKind.lambdaAny);
    this.lambdaVariable = lambdaVariable;
    this.expression = expression;
  }

  @Override
  public EdmType getType() {
    return EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean);
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public String getLambdaVariable() {
    return lambdaVariable;
  }

  @Override
  public Expression getExpression() {
    return expression;
  }

  @Override
  public String getSegmentValue() {
    return "any";
  }
}
