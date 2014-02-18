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
package org.apache.olingo.odata4.server.core.testutil;

import java.util.List;

import org.apache.olingo.odata4.commons.api.ODataApplicationException;
import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.server.api.uri.UriInfoResource;
import org.apache.olingo.odata4.server.api.uri.UriResource;
import org.apache.olingo.odata4.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.odata4.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.odata4.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.odata4.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedConstants;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedMethodCalls;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedUnaryOperators;

public class FilterTreeToText implements ExpressionVisitor<String> {

  public static String Serialize(final FilterOption filter)
      throws ExceptionVisitExpression, ODataApplicationException {

    Expression expression = filter.getExpression();
    return expression.accept(new FilterTreeToText());
  }

  public static String Serialize(final Expression expression)
      throws ExceptionVisitExpression, ODataApplicationException {

    return expression.accept(new FilterTreeToText());
  }

  @Override
  public String visitBinaryOperator(final SupportedBinaryOperators operator, final String left, final String right)
      throws ExceptionVisitExpression {

    return "<" + left + " " + operator.toString() + " " + right + ">";
  }

  @Override
  public String visitUnaryOperator(final SupportedUnaryOperators operator, final String operand)
      throws ExceptionVisitExpression {

    return "<" + operator + " " + operand.toString() + ">";
  }

  @Override
  public String visitMethodCall(final SupportedMethodCalls methodCall, final List<String> parameters)
      throws ExceptionVisitExpression {

    String text = "<" + methodCall + "(";
    int i = 0;
    while (i < parameters.size()) {
      if (i > 0) {
        text += ",";
      }
      text += parameters.get(i);
      i++;
    }
    return text + ")>";
  }

  @Override
  public String visitLiteral(final String literal) throws ExceptionVisitExpression {
    return "<" + literal + ">";
  }

  @Override
  public String visitMember(final UriInfoResource resource) throws ExceptionVisitExpression, ODataApplicationException {
    String ret = "";

    UriInfoResource path = resource;

    for (UriResource item : path.getUriResourceParts()) {
      String tmp = "";
      if (item instanceof UriResourceLambdaAll) {
        UriResourceLambdaAll all = (UriResourceLambdaAll) item;
        tmp = visitLambdaExpression("ALL", all.getLamdaVariable(), all.getExpression());
      } else if (item instanceof UriResourceLambdaAny) {
        UriResourceLambdaAny any = (UriResourceLambdaAny) item;
        // TODO create enum
        tmp = visitLambdaExpression("ANY", any.getLamdaVariable(), any.getExpression());
      } else if (item instanceof UriResourcePartTyped) {
        UriResourcePartTyped typed = (UriResourcePartTyped) item;
        tmp = typed.toString(true);
      }

      if (ret.length() != 0) {
        ret += "/";
      }
      ret += tmp;

    }
    return "<" + ret + ">";
  }

  @Override
  public String visitAlias(final String referenceName) throws ExceptionVisitExpression {
    return "<" + referenceName + ">";
  }

  @Override
  public String visitLambdaExpression(final String functionText, final String string, final Expression expression)
      throws ExceptionVisitExpression, ODataApplicationException {

    return "<" + functionText + ";" + ((expression == null) ? "" : expression.accept(this)) + ">";
  }

  @Override
  public String visitTypeLiteral(final EdmType type) {
    return "<" + type.getNamespace() + "." + type.getName() + ">";
  }

  @Override
  public String visitLambdaReference(final String variableText) {
    return "<" + variableText + ">";
  }

  @Override
  public String visitEnum(final EdmEnumType type, final List<String> enumValues)
      throws ExceptionVisitExpression, ODataApplicationException {
    String tmp = "";

    for (String item : enumValues) {
      if (tmp.length() > 0) {
        tmp += ",";
      }
      tmp += item;
    }

    return "<" + type.getNamespace() + "." + type.getName() + "<" + tmp + ">>";
  }

  @Override
  public String visitConstant(final SupportedConstants kind)
      throws ExceptionVisitExpression, ODataApplicationException {
    // TODO Auto-generated method stub
    return "<" + kind.toString() + ">";
  }
}
