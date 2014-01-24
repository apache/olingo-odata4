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
package org.apache.olingo.odata4.producer.core.testutil;

import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfoResource;
import org.apache.olingo.odata4.producer.api.uri.UriResourcePart;
import org.apache.olingo.odata4.producer.api.uri.UriResourceIt;
import org.apache.olingo.odata4.producer.api.uri.UriResourceCount;
import org.apache.olingo.odata4.producer.api.uri.UriResourceAction;
import org.apache.olingo.odata4.producer.api.uri.UriResourceAll;
import org.apache.olingo.odata4.producer.api.uri.UriResourceAny;
import org.apache.olingo.odata4.producer.api.uri.UriResourceEntitySet;
import org.apache.olingo.odata4.producer.api.uri.UriResourceFunction;
import org.apache.olingo.odata4.producer.api.uri.UriResourceNavigation;
import org.apache.olingo.odata4.producer.api.uri.UriResourceProperty;
import org.apache.olingo.odata4.producer.api.uri.UriResourceSingleton;
import org.apache.olingo.odata4.producer.api.uri.UriResourceRef;
import org.apache.olingo.odata4.producer.api.uri.UriResourceRoot;
import org.apache.olingo.odata4.producer.api.uri.UriResourceValue;

import org.apache.olingo.odata4.producer.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.Expression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedMethodCalls;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedUnaryOperators;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.VisitableExression;
import org.apache.olingo.odata4.producer.core.uri.UriResourceActionImpl;

public class FilterTreeToText implements ExpressionVisitor<String> {

  public static String Serialize(FilterOption filter) throws ExceptionVisitExpression, ODataApplicationException {
    Expression expression = filter.getExpression();
    return expression.accept(new FilterTreeToText());
  }

  @Override
  public String visitBinaryOperator(SupportedBinaryOperators operator, String left, String right)
      throws ExceptionVisitExpression {
    return "<" + left + " " + operator.toString() + " " + right + ">";
  }

  @Override
  public String visitUnaryOperator(SupportedUnaryOperators operator, String operand) throws ExceptionVisitExpression {
    return "<" + operator + " " + operand.toString() + ">";
  }

  @Override
  public String visitMethodCall(SupportedMethodCalls methodCall, List<String> parameters)
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
  public String visitLiteral(String literal) throws ExceptionVisitExpression {
    return literal;
  }

  @Override
  public String visitMember(UriInfoResource resource) throws ExceptionVisitExpression, ODataApplicationException {
    String ret = "";

    UriInfoResource path = resource;

    for (UriResourcePart item : path.getUriResourceParts()) {
      String tmp = "";
      if (item instanceof UriResourceIt) {
        if (((UriResourceIt) item).isExplicitIt()) {
          tmp = "$it";
        }
      } else if ( item instanceof UriResourceAll) {
        UriResourceAll all = (UriResourceAll) item;
        tmp = visitLambdaExpression(all.getLamdaVariable(), all.getExpression());
      } else if ( item instanceof UriResourceAny) {
        UriResourceAny any = (UriResourceAny) item;
        tmp = visitLambdaExpression(any.getLamdaVariable(), any.getExpression());
      } else {
        tmp = item.toString();
      }
      
             
      
      if (ret.length() != 0) {
        ret += "/";
      }
      ret += tmp;

    }
    return ret;
  }

  @Override
  public String visitAlias(String referenceName) throws ExceptionVisitExpression {
    return "<" + referenceName + ">";
  }

  @Override
  public String visitLambdaExpression(String variableText, Expression expression) 
      throws ExceptionVisitExpression, ODataApplicationException {
    return "<" + variableText + ";" + expression.accept(this) + ">";
  }

  @Override
  public String visitTypeLiteral(EdmType type) {
    return type.toString();
  }

  @Override
  public String visitLambdaReference(String variableText) {
    // TODO Auto-generated method stub
    return null;
  }

}
