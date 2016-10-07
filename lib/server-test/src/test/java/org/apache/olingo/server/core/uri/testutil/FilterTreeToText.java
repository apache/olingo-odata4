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
package org.apache.olingo.server.core.uri.testutil;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

public class FilterTreeToText implements ExpressionVisitor<String> {

  public static String Serialize(final FilterOption filter)
      throws ExpressionVisitException, ODataApplicationException {

    Expression expression = filter.getExpression();
    return expression.accept(new FilterTreeToText());
  }

  public static String Serialize(final Expression expression)
      throws ExpressionVisitException, ODataApplicationException {

    return expression.accept(new FilterTreeToText());
  }

  @Override
  public String visitBinaryOperator(final BinaryOperatorKind operator, final String left, final String right)
      throws ExpressionVisitException {

    return "<" + left + " " + operator.toString() + " " + right + ">";
  }

  @Override
  public String visitUnaryOperator(final UnaryOperatorKind operator, final String operand)
      throws ExpressionVisitException {

    return "<" + operator + " " + operand + ">";
  }

  @Override
  public String visitMethodCall(final MethodKind methodCall, final List<String> parameters)
      throws ExpressionVisitException {

    String text = "<" + methodCall + "(";
    boolean first = true;
    for (final String parameter : parameters) {
      if (!first) {
        text += ",";
      }
      text += parameter;
      first = false;
    }
    return text + ")>";
  }

  @Override
  public String visitLiteral(final Literal literal) throws ExpressionVisitException {
    return "<" + literal.getText() + ">";
  }

  @Override
  public String visitMember(final Member member) throws ExpressionVisitException, ODataApplicationException {
    String ret = "";

    for (UriResource item : member.getResourcePath().getUriResourceParts()) {
      String tmp = "";
      if (item instanceof UriResourceLambdaAll) {
        UriResourceLambdaAll all = (UriResourceLambdaAll) item;
        tmp = visitLambdaExpression("ALL", all.getLambdaVariable(), all.getExpression());
      } else if (item instanceof UriResourceLambdaAny) {
        UriResourceLambdaAny any = (UriResourceLambdaAny) item;
        tmp = visitLambdaExpression("ANY", any.getLambdaVariable(), any.getExpression());
      } else if (item instanceof UriResourcePartTyped) {
        UriResourcePartTyped typed = (UriResourcePartTyped) item;
        tmp = typed.toString(true);
      }

      if (ret.length() > 0) {
        ret += "/";
      }
      ret += tmp;

    }
    return "<" + ret + ">";
  }

  @Override
  public String visitAlias(final String referenceName) throws ExpressionVisitException {
    return "<" + referenceName + ">";
  }

  @Override
  public String visitLambdaExpression(final String functionText, final String string, final Expression expression)
      throws ExpressionVisitException, ODataApplicationException {

    return "<" + functionText + ";" + ((expression == null) ? "" : expression.accept(this)) + ">";
  }

  @Override
  public String visitTypeLiteral(final EdmType type) {
    return "<" + type.getFullQualifiedName().getFullQualifiedNameAsString() + ">";
  }

  @Override
  public String visitLambdaReference(final String variableText) {
    return "<" + variableText + ">";
  }

  @Override
  public String visitEnum(final EdmEnumType type, final List<String> enumValues)
      throws ExpressionVisitException, ODataApplicationException {
    String tmp = "";

    for (String item : enumValues) {
      if (tmp.length() > 0) {
        tmp += ",";
      }
      tmp += item;
    }

    return "<" + type.getFullQualifiedName().getFullQualifiedNameAsString() + "<" + tmp + ">>";
  }

}
