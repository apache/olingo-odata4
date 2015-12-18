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

import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

public class UriParameterImpl implements UriParameter {
  private String name;
  private String text;
  private String alias;
  private Expression expression;
  private String referencedProperty;

  @Override
  public String getName() {
    return name;
  }

  public UriParameterImpl setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public UriParameterImpl setAlias(final String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getText() {
    return text;
  }

  public UriParameterImpl setText(final String text) {
    this.text = text;
    return this;
  }

  @Override
  public Expression getExpression() {
    return expression;
  }

  public UriParameterImpl setExpression(final Expression expression) {
    this.expression = expression;
    return this;
  }

  @Override
  public String getReferencedProperty() {
    return referencedProperty;
  }

  public UriParameterImpl setReferencedProperty(final String referencedProperty) {
    this.referencedProperty = referencedProperty;
    return this;
  }

}
