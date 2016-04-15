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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

/**
 * Represents an aggregate expression.
 */
public class AggregateExpressionImpl implements AggregateExpression {

  private UriInfo path;
  private Expression expression;
  private StandardMethod standardMethod;
  private FullQualifiedName customMethod;
  private String alias;
  private AggregateExpression inlineAggregateExpression;
  private List<AggregateExpression> from = new ArrayList<AggregateExpression>();

  @Override
  public List<UriResource> getPath() {
    return path == null ? Collections.<UriResource> emptyList() : path.getUriResourceParts();
  }

  public AggregateExpressionImpl setPath(final UriInfo uriInfo) {
    path = uriInfo;
    return this;
  }

  @Override
  public Expression getExpression() {
    return expression;
  }

  public AggregateExpressionImpl setExpression(final Expression expression) {
    this.expression = expression;
    return this;
  }

  @Override
  public StandardMethod getStandardMethod() {
    return standardMethod;
  }

  public AggregateExpressionImpl setStandardMethod(final StandardMethod standardMethod) {
    this.standardMethod = standardMethod;
    return this;
  }

  @Override
  public FullQualifiedName getCustomMethod() {
    return customMethod;
  }

  public AggregateExpressionImpl setCustomMethod(final FullQualifiedName customMethod) {
    this.customMethod = customMethod;
    return this;
  }

  @Override
  public AggregateExpression getInlineAggregateExpression() {
    return inlineAggregateExpression;
  }

  public AggregateExpressionImpl setInlineAggregateExpression(final AggregateExpression aggregateExpression) {
    inlineAggregateExpression = aggregateExpression;
    return this;
  }

  @Override
  public List<AggregateExpression> getFrom() {
    return Collections.unmodifiableList(from);
  }

  public AggregateExpressionImpl addFrom(final AggregateExpression from) {
    this.from.add(from);
    return this;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public AggregateExpressionImpl setAlias(final String alias) {
    this.alias = alias;
    return this;
  }
}
