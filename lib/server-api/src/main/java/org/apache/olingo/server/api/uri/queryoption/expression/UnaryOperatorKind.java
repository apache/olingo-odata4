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
package org.apache.olingo.server.api.uri.queryoption.expression;

/**
 * Enumeration of supported unary operators<br>
 * For the semantic of these operators please see the ODATA specification for URL conventions
 */
public enum UnaryOperatorKind {

  /**
   * Minus operator
   */
  MINUS("-"),

  /**
   * not operator
   */
  NOT("not");

  private String syntax;

  /**
   * Constructor for enumeration value
   * @param Syntax used in the URI
   */
  private UnaryOperatorKind(final String syntax) {
    this.syntax = syntax;
  }

  /**
   * @return URI syntax for that operator kind
   */
  @Override
  public String toString() {
    return syntax;
  }

  /**
   * URI syntax to enumeration value
   * @param operator Operator in the syntax used in the URI
   * @return Operator kind which represents the given syntax
   */
  public static UnaryOperatorKind get(final String operator) {
    for (UnaryOperatorKind op : UnaryOperatorKind.values()) {
      if (op.toString().equals(operator)) {
        return op;
      }
    }
    return null;
  }

}
