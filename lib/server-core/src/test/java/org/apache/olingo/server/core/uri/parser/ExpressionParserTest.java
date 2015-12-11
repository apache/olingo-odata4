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

import java.util.ArrayList;

import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.ExpressionParser.Token;
import org.apache.olingo.server.core.uri.parser.ExpressionParser.TokenKind;
import org.apache.olingo.server.core.uri.parser.ExpressionParser.Tokenizer;
import org.junit.Test;

public class ExpressionParserTest {

  @Test
  public void equality() throws Exception {
    Expression expression = parseExpression(TokenKind.EQ_OP);
    assertEquals("{5 EQ 5}", expression.toString());

    expression = parseExpression(TokenKind.NE_OP);
    assertEquals("{5 NE 5}", expression.toString());
  }

  @Test
  public void relational() throws Exception {
    Expression expression = parseExpression(TokenKind.GT_OP);
    assertEquals("{5 GT 5}", expression.toString());

    expression = parseExpression(TokenKind.GE_OP);
    assertEquals("{5 GE 5}", expression.toString());

    expression = parseExpression(TokenKind.LT_OP);
    assertEquals("{5 LT 5}", expression.toString());

    expression = parseExpression(TokenKind.LE_OP);
    assertEquals("{5 LE 5}", expression.toString());
  }

  @Test
  public void additive() throws Exception {
    Expression expression = parseExpression(TokenKind.ADD_OP);
    assertEquals("{5 ADD 5}", expression.toString());

    expression = parseExpression(TokenKind.SUB_OP);
    assertEquals("{5 SUB 5}", expression.toString());
  }

  @Test
  public void multiplicative() throws Exception {
    Expression expression = parseExpression(TokenKind.MUL_OP);
    assertEquals("{5 MUL 5}", expression.toString());

    expression = parseExpression(TokenKind.DIV_OP);
    assertEquals("{5 DIV 5}", expression.toString());

    expression = parseExpression(TokenKind.MOD_OP);
    assertEquals("{5 MOD 5}", expression.toString());
  }

  @Test
  public void unary() throws Exception {
    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(new Token(TokenKind.MINUS, ""));
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    Tokenizer tokenizer = new Tokenizer(tokens);
    Expression expression = new ExpressionParser().parse(tokenizer);
    assertEquals("{- 5}", expression.toString());

    tokens = new ArrayList<Token>();
    tokens.add(new Token(TokenKind.NOT, ""));
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    tokenizer = new Tokenizer(tokens);
    expression = new ExpressionParser().parse(tokenizer);
    assertEquals("{not 5}", expression.toString());
  }

  @Test
  public void grouping() throws Exception {
    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(new Token(TokenKind.MINUS, ""));
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    tokens.add(new Token(TokenKind.ADD_OP, ""));
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    Tokenizer tokenizer = new Tokenizer(tokens);
    Expression expression = new ExpressionParser().parse(tokenizer);
    assertEquals("{{- 5} ADD 5}", expression.toString());

    tokens = new ArrayList<Token>();
    tokens.add(new Token(TokenKind.MINUS, ""));
    tokens.add(new Token(TokenKind.OPEN, ""));
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    tokens.add(new Token(TokenKind.ADD_OP, ""));
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    tokens.add(new Token(TokenKind.CLOSE, ""));
    tokenizer = new Tokenizer(tokens);
    expression = new ExpressionParser().parse(tokenizer);
    assertEquals("{- {5 ADD 5}}", expression.toString());
  }

  @Test
  public void noParameterMethods() throws Exception {
    Expression expression = parseMethod(TokenKind.Now);
    assertEquals("{now []}", expression.toString());

    expression = parseMethod(TokenKind.Maxdatetime);
    assertEquals("{maxdatetime []}", expression.toString());

    expression = parseMethod(TokenKind.Mindatetime);
    assertEquals("{mindatetime []}", expression.toString());
  }

  @Test
  public void oneParameterMethods() throws Exception {
    Expression expression = parseMethod(TokenKind.Length, TokenKind.PrimitiveStringValue);
    assertEquals("{length [String1]}", expression.toString());

    expression = parseMethod(TokenKind.Tolower, TokenKind.PrimitiveStringValue);
    assertEquals("{tolower [String1]}", expression.toString());

    expression = parseMethod(TokenKind.Toupper, TokenKind.PrimitiveStringValue);
    assertEquals("{toupper [String1]}", expression.toString());

    expression = parseMethod(TokenKind.Trim, TokenKind.PrimitiveStringValue);
    assertEquals("{trim [String1]}", expression.toString());

    expression = parseMethod(TokenKind.Year, TokenKind.PrimitiveDateValue);
    assertEquals("{year [Date1]}", expression.toString());

    expression = parseMethod(TokenKind.Month, TokenKind.PrimitiveDateValue);
    assertEquals("{month [Date1]}", expression.toString());

    expression = parseMethod(TokenKind.Day, TokenKind.PrimitiveDateValue);
    assertEquals("{day [Date1]}", expression.toString());

    expression = parseMethod(TokenKind.Hour, TokenKind.PrimitiveDateTimeOffsetValue);
    assertEquals("{hour [DateTimeOffset1]}", expression.toString());

    expression = parseMethod(TokenKind.Minute, TokenKind.PrimitiveDateTimeOffsetValue);
    assertEquals("{minute [DateTimeOffset1]}", expression.toString());

    expression = parseMethod(TokenKind.Second, TokenKind.PrimitiveDateTimeOffsetValue);
    assertEquals("{second [DateTimeOffset1]}", expression.toString());
  }

  @Test
  public void twoParameterMethods() {

  }

  private Expression parseMethod(TokenKind... kind) throws UriParserException {
    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(new Token(kind[0], ""));

    for (int i = 1; i < kind.length; i++) {
      String text = null;
      switch (kind[i]) {
      case PrimitiveStringValue:
        text = "String" + i;
        break;
      case PrimitiveDateValue:
        text = "Date" + i;
        break;
      case PrimitiveDateTimeOffsetValue:
        text = "DateTimeOffset" + i;
        break;
      default:
        text = "" + i;
        break;
      }
      tokens.add(new Token(kind[i], text));
    }

    tokens.add(new Token(TokenKind.CLOSE, ""));
    Tokenizer tokenizer = new Tokenizer(tokens);
    Expression expression = new ExpressionParser().parse(tokenizer);
    assertNotNull(expression);
    return expression;
  }

  private Expression parseExpression(TokenKind operator) throws UriParserException {
    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    tokens.add(new Token(operator, ""));
    tokens.add(new Token(TokenKind.PrimitiveIntegerValue, "5"));
    Tokenizer tokenizer = new Tokenizer(tokens);

    Expression expression = new ExpressionParser().parse(tokenizer);
    assertNotNull(expression);
    return expression;
  }
}
