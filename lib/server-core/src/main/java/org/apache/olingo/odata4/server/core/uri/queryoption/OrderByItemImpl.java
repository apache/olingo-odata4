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
package org.apache.olingo.odata4.server.core.uri.queryoption;

import org.apache.olingo.odata4.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.odata4.server.core.uri.queryoption.expression.ExpressionImpl;

public class OrderByItemImpl implements OrderByItem {

  private ExpressionImpl expression;
  private boolean descending = false; // default sort order is ascending

  @Override
  public boolean isDescending() {
    return descending;
  }

  public OrderByItem setDescending(final boolean descending) {
    this.descending = descending;
    return this;
  }

  @Override
  public ExpressionImpl getExpression() {
    return expression;
  }

  public OrderByItem setExpression(final ExpressionImpl expression) {
    this.expression = expression;
    return this;
  }

}
