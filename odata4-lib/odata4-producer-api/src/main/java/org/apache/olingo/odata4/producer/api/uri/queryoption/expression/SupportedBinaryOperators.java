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
package org.apache.olingo.odata4.producer.api.uri.queryoption.expression;

public enum SupportedBinaryOperators {
  // multiplicative
  MUL("mul"), DIV("div"), MOD("mod"),
  // additive
  ADD("add"), SUB("sub"),
  // comparism
  GT("gt"), GE("ge"), LT("lt"), LE("le"),
  // isof
  ISOF("isof"),
  // equality
  EQ("eq"), NE("ne"),
  // and/or
  AND("and"), OR("or");

  private String syntax;

  private SupportedBinaryOperators(final String syntax) {
    this.syntax = syntax;
  }

  public static SupportedBinaryOperators get(final String operator) {
    for (SupportedBinaryOperators op : SupportedBinaryOperators.values()) {
      if (op.toString().equals(operator)) {
        return op;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return syntax;
  }

}
