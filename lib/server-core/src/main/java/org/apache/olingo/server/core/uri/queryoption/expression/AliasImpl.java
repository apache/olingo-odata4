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
package org.apache.olingo.server.core.uri.queryoption.expression;

import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.AliasQueryOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Alias;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;

public class AliasImpl implements Alias {

  private final String parameterName;
  private final AliasQueryOption alias;

  public AliasImpl(final String parameterName, final AliasQueryOption alias) {
    this.parameterName = parameterName;
    this.alias = alias;
  }

  @Override
  public String getParameterName() {
    return parameterName;
  }

  public AliasQueryOption getAlias() {
    return alias;
  }

  @Override
  public <T> T accept(final ExpressionVisitor<T> visitor) throws ExpressionVisitException, ODataApplicationException {
    return visitor.visitAlias(parameterName);
  }

  @Override
  public String toString() {
    return parameterName;
  }
}
