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
package org.apache.olingo.server.api.uri.queryoption.apply;

import org.apache.olingo.server.api.uri.queryoption.ApplyItem;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

/**
 * Represents a transformation with one of the pre-defined methods
 * <code>bottomcount</code>, <code>bottompercent</code>, <code>bottomsum</code>,
 * <code>topcount</code>, <code>toppercent</code>, <code>topsum</code>.
 */
public interface BottomTop extends ApplyItem {

  /** Pre-defined method for partial aggregration. */
  public enum Method { BOTTOM_COUNT, BOTTOM_PERCENT, BOTTOM_SUM, TOP_COUNT, TOP_PERCENT, TOP_SUM }

  /**
   * Gets the partial-aggregation method.
   * @return a {@link Method} (but never <code>null</code>)
   */
  Method getMethod();

  /**
   * Gets the expression that determines the number of items to aggregate.
   * @return an {@link Expression} (but never <code>null</code>)
   */
  Expression getNumber();

  /**
   * Gets the expression that determines the values to aggregate.
   * @return an {@link Expression} (but never <code>null</code>)
   */
  Expression getValue();
}
