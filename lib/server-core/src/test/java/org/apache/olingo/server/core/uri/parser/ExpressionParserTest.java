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
package org.apache.olingo.server.core.uri.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.junit.Test;

public class ExpressionParserTest {

  @Test
  public void equality() throws Exception {
    Expression expression = parseExpression("5 eq 5");
    assertEquals("{5 EQ 5}", expression.toString());

    expression = parseExpression("5 ne 5");
    assertEquals("{5 NE 5}", expression.toString());
  }

  @Test
  public void relational() throws Exception {
    Expression expression = parseExpression("5 gt 5");
    assertEquals("{5 GT 5}", expression.toString());

    expression = parseExpression("5 ge 5");
    assertEquals("{5 GE 5}", expression.toString());

    expression = parseExpression("5 lt 5");
    assertEquals("{5 LT 5}", expression.toString());

    expression = parseExpression("5 le 5");
    assertEquals("{5 LE 5}", expression.toString());
  }

  @Test
  public void additive() throws Exception {
    Expression expression = parseExpression("5 add 5");
    assertEquals("{5 ADD 5}", expression.toString());

    expression = parseExpression("5 sub 5");
    assertEquals("{5 SUB 5}", expression.toString());
  }

  @Test
  public void multiplicative() throws Exception {
    Expression expression = parseExpression("5 mul 5");
    assertEquals("{5 MUL 5}", expression.toString());

    expression = parseExpression("5 div 5");
    assertEquals("{5 DIV 5}", expression.toString());

    expression = parseExpression("5 mod 5");
    assertEquals("{5 MOD 5}", expression.toString());
  }

  @Test
  public void unary() throws Exception {
    Expression expression = parseExpression("-5");
    assertEquals("{MINUS 5}", expression.toString());

    assertEquals("{MINUS -1}", parseExpression("--1").toString());

    expression = parseExpression("not 5");
    assertEquals("{NOT 5}", expression.toString());
  }

  @Test
  public void grouping() throws Exception {
    Expression expression = parseExpression("-5 add 5");
    assertEquals("{{MINUS 5} ADD 5}", expression.toString());

    expression = parseExpression("-(5 add 5)");
    assertEquals("{MINUS {5 ADD 5}}", expression.toString());
  }

  @Test
  public void precedence() throws Exception {
    assertEquals("{{MINUS 1} ADD {2 DIV 3}}", parseExpression("-1 add 2 div 3").toString());
    assertEquals("{true OR {{NOT false} AND true}}", parseExpression("true or not false and true").toString());
  }

  @Test
  public void noParameterMethods() throws Exception {
    Expression expression = parseMethod(TokenKind.NowMethod);
    assertEquals("{now []}", expression.toString());

    expression = parseMethod(TokenKind.MaxdatetimeMethod);
    assertEquals("{maxdatetime []}", expression.toString());

    expression = parseMethod(TokenKind.MindatetimeMethod);
    assertEquals("{mindatetime []}", expression.toString());
  }

  @Test
  public void oneParameterMethods() throws Exception {
    final String stringValue = "'abc'";
    final String dateValue = "1234-12-25";
    final String dateTimeOffsetValue = "1234-12-25T11:12:13.456Z";

    Expression expression = parseMethod(TokenKind.LengthMethod, stringValue);
    assertEquals("{length [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.TolowerMethod, stringValue);
    assertEquals("{tolower [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.ToupperMethod, stringValue);
    assertEquals("{toupper [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.TrimMethod, stringValue);
    assertEquals("{trim [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.YearMethod, dateValue);
    assertEquals("{year [" + dateValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.MonthMethod, dateValue);
    assertEquals("{month [" + dateValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.DayMethod, dateValue);
    assertEquals("{day [" + dateValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.HourMethod, dateTimeOffsetValue);
    assertEquals("{hour [" + dateTimeOffsetValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.MinuteMethod, dateTimeOffsetValue);
    assertEquals("{minute [" + dateTimeOffsetValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.SecondMethod, dateTimeOffsetValue);
    assertEquals("{second [" + dateTimeOffsetValue + "]}", expression.toString());
  }

  private Expression parseMethod(TokenKind kind, String... parameters) throws UriParserException {
    String expressionString = kind.name().substring(0, kind.name().indexOf("Method"))
        .toLowerCase(Locale.ROOT).replace("geo", "geo.") + '(';
    for (int i = 0; i < parameters.length; i++) {
      if (i > 0) {
        expressionString += ',';
      }
      expressionString += parameters[i];
    }
    expressionString += ')';

    Expression expression = parseExpression(expressionString);
    assertNotNull(expression);
    return expression;
  }

  private Expression parseExpression(final String expressionString) throws UriParserException {
    UriTokenizer tokenizer = new UriTokenizer(expressionString);
    Expression expression = new ExpressionParser().parse(tokenizer);
    assertNotNull(expression);
    assertTrue(tokenizer.next(TokenKind.EOF));
    return expression;
  }
}
