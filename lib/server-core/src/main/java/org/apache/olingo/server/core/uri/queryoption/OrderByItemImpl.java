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
package org.apache.olingo.server.core.uri.queryoption;

import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

public class OrderByItemImpl implements OrderByItem {

  private Expression expression;
  // default sort order is ascending
  private boolean descending = false;

  @Override
  public boolean isDescending() {
    return descending;
  }

  public OrderByItemImpl setDescending(final boolean descending) {
    this.descending = descending;
    return this;
  }

  @Override
  public Expression getExpression() {
    return expression;
  }

  public OrderByItemImpl setExpression(final Expression expression) {
    this.expression = expression;
    return this;
  }

}
