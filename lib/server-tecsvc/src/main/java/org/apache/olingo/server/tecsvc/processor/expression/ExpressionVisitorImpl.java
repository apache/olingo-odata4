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
package org.apache.olingo.server.tecsvc.processor.expression;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.apache.olingo.server.tecsvc.processor.expression.operand.TypedOperand;
import org.apache.olingo.server.tecsvc.processor.expression.operand.UntypedOperand;
import org.apache.olingo.server.tecsvc.processor.expression.operand.VisitorOperand;
import org.apache.olingo.server.tecsvc.processor.expression.operation.BinaryOperator;
import org.apache.olingo.server.tecsvc.processor.expression.operation.MethodCallOperator;
import org.apache.olingo.server.tecsvc.processor.expression.operation.UnaryOperator;

public class ExpressionVisitorImpl implements ExpressionVisitor<VisitorOperand> {

  final private Entity entity;
  final private EdmEntitySet edmEntitySet;

  public ExpressionVisitorImpl(Entity entity, EdmEntitySet edmEntitySet) {
    this.entity = entity;
    this.edmEntitySet = edmEntitySet;
  }

  @Override
  public VisitorOperand visitBinaryOperator(BinaryOperatorKind operator, VisitorOperand left, VisitorOperand right)
      throws ExpressionVisitException, ODataApplicationException {

    final BinaryOperator binaryOperator = new BinaryOperator(left, right);

    switch (operator) {
    case AND:
      return binaryOperator.andOperator();
    case OR:
      return binaryOperator.orOperator();
    case EQ:
      return binaryOperator.equalsOperator();
    case NE:
      return binaryOperator.notEqualsOperator();
    case GE:
      return binaryOperator.greaterEqualsOperator();
    case GT:
      return binaryOperator.greaterThanOperator();
    case LE:
      return binaryOperator.lessEqualsOperator();
    case LT:
      return binaryOperator.lessThanOperator();
    case ADD:
    case SUB:
    case MUL:
    case DIV:
    case MOD:
      return binaryOperator.arithmeticOperator(operator);
    default:
      return throwNotImplemented();
    }
  }

  @Override
  public VisitorOperand visitUnaryOperator(UnaryOperatorKind operator, VisitorOperand operand)
      throws ExpressionVisitException, ODataApplicationException {

    final UnaryOperator unaryOperator = new UnaryOperator(operand);

    switch (operator) {
    case MINUS:
      return unaryOperator.minusOperation();
    case NOT:
      return unaryOperator.notOperation();
    default:
      // Can`t happen
      return throwNotImplemented();
    }
  }

  @Override
  public VisitorOperand visitMethodCall(MethodKind methodCall, List<VisitorOperand> parameters)
      throws ExpressionVisitException, ODataApplicationException {

    final MethodCallOperator methodCallOperation = new MethodCallOperator(parameters);

    switch (methodCall) {
    case ENDSWITH:
      return methodCallOperation.endsWith();
    case INDEXOF:
      return methodCallOperation.indexOf();
    case STARTSWITH:
      return methodCallOperation.startsWith();
    case TOLOWER:
      return methodCallOperation.toLower();
    case TOUPPER:
      return methodCallOperation.toUpper();
    case TRIM:
      return methodCallOperation.trim();
    case SUBSTRING:
      return methodCallOperation.substring();
    case CONTAINS:
      return methodCallOperation.contains();
    case CONCAT:
      return methodCallOperation.concat();
    case LENGTH:
      return methodCallOperation.length();
    case YEAR:
      return methodCallOperation.year();
    case MONTH:
      return methodCallOperation.month();
    case DAY:
      return methodCallOperation.day();
    case HOUR:
      return methodCallOperation.hour();
    case MINUTE:
      return methodCallOperation.minute();
    case SECOND:
      return methodCallOperation.second();
    case FRACTIONALSECONDS:
      return methodCallOperation.fractionalseconds();
    case ROUND:
      return methodCallOperation.round();
    case FLOOR:
      return methodCallOperation.floor();
    case CEILING:
      return methodCallOperation.ceiling();

    default:
      return throwNotImplemented();
    }
  }

  @Override
  public VisitorOperand visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression)
      throws ExpressionVisitException, ODataApplicationException {

    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitLiteral(String literal) throws ExpressionVisitException, ODataApplicationException {

    return new UntypedOperand(literal);
  }

  @Override
  public VisitorOperand visitMember(UriInfoResource member) throws ExpressionVisitException,
      ODataApplicationException {

    final List<UriResource> uriResourceParts = member.getUriResourceParts();

    // UriResourceParts contains at least one UriResource
    Property currentProperty = entity.getProperty(uriResourceParts.get(0).toString());
    EdmType currentType = ((UriResourcePartTyped) uriResourceParts.get(0)).getType();

    EdmProperty currentEdmProperty = edmEntitySet.getEntityType()
        .getStructuralProperty(uriResourceParts.get(0).toString());

    for (int i = 1; i < uriResourceParts.size(); i++) {
      currentType = ((UriResourcePartTyped) uriResourceParts.get(i)).getType();

      if (currentProperty.isComplex() || currentProperty.isLinkedComplex()) {
        final List<Property> complex = currentProperty.isLinkedComplex() ?
            currentProperty.asLinkedComplex().getValue() : currentProperty.asComplex();

        for (final Property innerProperty : complex) {
          if (innerProperty.getName().equals(uriResourceParts.get(i).toString())) {
            EdmComplexType edmComplexType = (EdmComplexType) currentEdmProperty.getType();
            currentEdmProperty = edmComplexType.getStructuralProperty(uriResourceParts.get(i).toString());
            currentProperty = innerProperty;
            break;
          }
        }
      }
    }

    return new TypedOperand(((Property) currentProperty).getValue(), currentType, currentEdmProperty);
  }

  @Override
  public VisitorOperand visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException {
    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException {
    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitLambdaReference(String variableName) throws ExpressionVisitException,
      ODataApplicationException {
    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitEnum(EdmEnumType type, List<String> enumValues) throws ExpressionVisitException,
      ODataApplicationException {
    return throwNotImplemented();
  }

  private VisitorOperand throwNotImplemented() throws ODataApplicationException {
    throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
        Locale.ROOT);
  }
}
