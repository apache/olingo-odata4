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
package org.apache.olingo.server.api.uri.queryoption.expression;

import java.util.List;

import org.apache.olingo.commons.api.ODataApplicationException;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriInfoResource;

public interface ExpressionVisitor<T> {

  T visitBinaryOperator(BinaryOperatorKind operator, T left, T right)
      throws ExpressionVisitException, ODataApplicationException;

  T visitUnaryOperator(UnaryOperatorKind operator, T operand)
      throws ExpressionVisitException, ODataApplicationException;


  T visitMethodCall(MethodCallKind methodCall, List<T> parameters)
      throws ExpressionVisitException, ODataApplicationException;


  T visitLambdaExpression(String functionText, String variableText, Expression expression)
      throws ExpressionVisitException, ODataApplicationException;

  T visitLiteral(String literal) throws ExpressionVisitException, ODataApplicationException;

  T visitMember(UriInfoResource member) throws ExpressionVisitException, ODataApplicationException;

  T visitAlias(String referenceName) throws ExpressionVisitException, ODataApplicationException;

  T visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException;

  T visitLambdaReference(String variableText) throws ExpressionVisitException, ODataApplicationException;

  T visitEnum(EdmEnumType type, List<String> enumValues) throws ExpressionVisitException, ODataApplicationException;


}
