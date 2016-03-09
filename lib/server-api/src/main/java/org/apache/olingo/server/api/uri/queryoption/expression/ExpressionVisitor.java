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

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;

/**
 * Generic interface to define expression visitors with arbitrary return types.
 *
 * @param <T> Return type
 */
public interface ExpressionVisitor<T> {

  /**
   * Called for each traversed {@link Binary} expression
   * @param operator Operator kind
   * @param left Application return value of left sub tree
   * @param right Application return value of right sub tree
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitBinaryOperator(BinaryOperatorKind operator, T left, T right)
      throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link Unary} expression
   * @param operator Operator kind
   * @param operand return value of sub tree
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitUnaryOperator(UnaryOperatorKind operator, T operand)
      throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link Method} expression
   * @param methodCall Method
   * @param parameters List of application return values created by visiting each method parameter
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occurred
   * @throws ODataApplicationException Thrown by the application
   */
  T visitMethodCall(MethodKind methodCall, List<T> parameters)
      throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed lambda expression
   * @param lambdaFunction "ALL" or "ANY"
   * @param lambdaVariable Variable name used lambda variable
   * @param expression Lambda expression
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression)
      throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link Literal} expression
   * @param literal Literal
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link Member} expression
   * @param member UriInfoResource object describing the whole path used to access an data value
   * (this includes for example the usage of $root and $it inside the URI)
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitMember(Member member) throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link Alias} expression
   * @param aliasName Name of the alias
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link TypeLiteral} expression
   * @param type EdmType
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link LambdaRef}
   * @param variableName Name of the used lambda variable
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitLambdaReference(String variableName) throws ExpressionVisitException, ODataApplicationException;

  /**
   * Called for each traversed {@link Enumeration} expression
   * @param type Type used in the URI before the enumeration values
   * @param enumValues List of enumeration values
   * @return Application return value of type T
   * @throws ExpressionVisitException Thrown if an exception while traversing occured
   * @throws ODataApplicationException Thrown by the application
   */
  T visitEnum(EdmEnumType type, List<String> enumValues) throws ExpressionVisitException, ODataApplicationException;

}
