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

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfoResource;

public interface ExpressionVisitor<T> {

  T visitBinaryOperator(SupportedBinaryOperators operator, T left, T right)
      throws ExceptionVisitExpression, ODataApplicationException;

  T visitUnaryOperator(SupportedUnaryOperators operator, T operand)
      throws ExceptionVisitExpression, ODataApplicationException;

  T visitMethodCall(SupportedMethodCalls methodCall, List<T> parameters)
      throws ExceptionVisitExpression, ODataApplicationException;
  
  T visitLambdaExpression(String functionText,String variableText, Expression expression)
      throws ExceptionVisitExpression, ODataApplicationException;

  T visitLiteral(String literal) throws ExceptionVisitExpression, ODataApplicationException;

  T visitMember(UriInfoResource member) throws ExceptionVisitExpression, ODataApplicationException;

  T visitAlias(String referenceName) throws ExceptionVisitExpression, ODataApplicationException;

  T visitTypeLiteral(EdmType type) throws ExceptionVisitExpression, ODataApplicationException;

  T visitLambdaReference(String variableText) throws ExceptionVisitExpression, ODataApplicationException;

  T visitEnum(EdmEnumType type, List<String> enumValues) throws ExceptionVisitExpression, ODataApplicationException;
  
  T visitConstant(SupportedConstants kind) throws ExceptionVisitExpression, ODataApplicationException;
  
}
