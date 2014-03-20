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
package org.apache.olingo.client.core.uri;

import org.apache.olingo.client.api.uri.FilterArg;
import org.apache.olingo.client.api.uri.URIFilter;

public class FilterLambda implements FilterArg {

  private final FilterArg collection;

  private final String operator;

  private final URIFilter expression;

  public FilterLambda(final FilterArg collection, final String operator, final URIFilter expression) {
    this.collection = collection;
    this.operator = operator;
    this.expression = expression;
  }

  @Override
  public String build() {
    return new StringBuilder(collection.build()).
            append('/').
            append(operator).
            append(expression.build()).
            toString();
  }
}
