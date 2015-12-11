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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.Method;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.apache.olingo.server.core.uri.queryoption.expression.AliasImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MethodImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.UnaryImpl;

public class ExpressionParser {
  private Tokenizer tokenizer;

  private static final Map<TokenKind, BinaryOperatorKind> tokenToBinaryOperator;
  static {
    Map<TokenKind, BinaryOperatorKind> temp = new HashMap<ExpressionParser.TokenKind, BinaryOperatorKind>();
    temp.put(TokenKind.OR_OP, BinaryOperatorKind.OR);
    temp.put(TokenKind.AND_OP, BinaryOperatorKind.AND);

    temp.put(TokenKind.EQ_OP, BinaryOperatorKind.EQ);
    temp.put(TokenKind.NE_OP, BinaryOperatorKind.NE);

    temp.put(TokenKind.GT_OP, BinaryOperatorKind.GT);
    temp.put(TokenKind.GE_OP, BinaryOperatorKind.GE);
    temp.put(TokenKind.LT_OP, BinaryOperatorKind.LT);
    temp.put(TokenKind.LE_OP, BinaryOperatorKind.LE);

    temp.put(TokenKind.ADD_OP, BinaryOperatorKind.ADD);
    temp.put(TokenKind.SUB_OP, BinaryOperatorKind.SUB);

    temp.put(TokenKind.MUL_OP, BinaryOperatorKind.MUL);
    temp.put(TokenKind.DIV_OP, BinaryOperatorKind.DIV);
    temp.put(TokenKind.MOD_OP, BinaryOperatorKind.MOD);

    tokenToBinaryOperator = Collections.unmodifiableMap(temp);
  }

  private static final Map<TokenKind, UnaryOperatorKind> tokenToUnaryOperator;
  static {
    Map<TokenKind, UnaryOperatorKind> temp = new HashMap<ExpressionParser.TokenKind, UnaryOperatorKind>();
    temp.put(TokenKind.MINUS, UnaryOperatorKind.MINUS);
    temp.put(TokenKind.NOT, UnaryOperatorKind.NOT);
    tokenToUnaryOperator = Collections.unmodifiableMap(temp);
  }

  private static final Map<TokenKind, MethodKind> tokenToMethod;
  static {
    Map<TokenKind, MethodKind> temp = new HashMap<ExpressionParser.TokenKind, MethodKind>();
    temp.put(TokenKind.Cast, MethodKind.CAST);
    temp.put(TokenKind.Ceiling, MethodKind.CEILING);
    temp.put(TokenKind.Concat, MethodKind.CONCAT);
    temp.put(TokenKind.Contains, MethodKind.CONTAINS);
    temp.put(TokenKind.Date, MethodKind.DATE);
    temp.put(TokenKind.Day, MethodKind.DAY);
    temp.put(TokenKind.Endswith, MethodKind.ENDSWITH);
    temp.put(TokenKind.Floor, MethodKind.FLOOR);
    temp.put(TokenKind.Fractionalseconds, MethodKind.FRACTIONALSECONDS);
    temp.put(TokenKind.GeoDistance, MethodKind.GEODISTANCE);
    temp.put(TokenKind.GeoIntersects, MethodKind.GEOINTERSECTS);
    temp.put(TokenKind.GeoLength, MethodKind.GEOLENGTH);
    temp.put(TokenKind.Hour, MethodKind.HOUR);
    temp.put(TokenKind.Indexof, MethodKind.INDEXOF);
    temp.put(TokenKind.Isof, MethodKind.ISOF);
    temp.put(TokenKind.Length, MethodKind.LENGTH);
    temp.put(TokenKind.Maxdatetime, MethodKind.MAXDATETIME);
    temp.put(TokenKind.Mindatetime, MethodKind.MINDATETIME);
    temp.put(TokenKind.Minute, MethodKind.MINUTE);
    temp.put(TokenKind.Month, MethodKind.MONTH);
    temp.put(TokenKind.Now, MethodKind.NOW);
    temp.put(TokenKind.Round, MethodKind.ROUND);
    temp.put(TokenKind.Second, MethodKind.SECOND);
    temp.put(TokenKind.Startswith, MethodKind.STARTSWITH);
    temp.put(TokenKind.Substring, MethodKind.SUBSTRING);
    temp.put(TokenKind.Time, MethodKind.TIME);
    temp.put(TokenKind.Tolower, MethodKind.TOLOWER);
    temp.put(TokenKind.Totaloffsetminutes, MethodKind.TOTALOFFSETMINUTES);
    temp.put(TokenKind.Totalseconds, MethodKind.TOTALSECONDS);
    temp.put(TokenKind.Toupper, MethodKind.TOUPPER);
    temp.put(TokenKind.Trim, MethodKind.TRIM);
    temp.put(TokenKind.Year, MethodKind.YEAR);

    tokenToMethod = Collections.unmodifiableMap(temp);
  }

  private static final Map<TokenKind, EdmPrimitiveTypeKind> tokenToPrimitiveType;
  static {
    /* Enum and null are not present in the map. These have to be handled differently */
    Map<TokenKind, EdmPrimitiveTypeKind> temp = new HashMap<ExpressionParser.TokenKind, EdmPrimitiveTypeKind>();
    temp.put(TokenKind.PrimitiveBooleanValue, EdmPrimitiveTypeKind.Boolean);
    temp.put(TokenKind.PrimitiveStringValue, EdmPrimitiveTypeKind.String);
    // TODO:Check if int64 is correct here or if it has to be single instead
    temp.put(TokenKind.PrimitiveIntegerValue, EdmPrimitiveTypeKind.Int64);
    temp.put(TokenKind.PrimitiveGuidValue, EdmPrimitiveTypeKind.Guid);
    temp.put(TokenKind.PrimitiveDateValue, EdmPrimitiveTypeKind.Date);
    temp.put(TokenKind.PrimitiveDateTimeOffsetValue, EdmPrimitiveTypeKind.DateTimeOffset);
    temp.put(TokenKind.PrimitiveTimeOfDayValue, EdmPrimitiveTypeKind.TimeOfDay);
    temp.put(TokenKind.PrimitiveDecimalValue, EdmPrimitiveTypeKind.Decimal);
    temp.put(TokenKind.PrimitiveDoubleValue, EdmPrimitiveTypeKind.Double);
    temp.put(TokenKind.PrimitiveDurationValue, EdmPrimitiveTypeKind.Duration);
    temp.put(TokenKind.PrimitiveBinaryValue, EdmPrimitiveTypeKind.Binary);

    tokenToPrimitiveType = Collections.unmodifiableMap(temp);
  }

  public Expression parse(Tokenizer tokenizer) throws UriParserException {
    // Initialize tokenizer.
    this.tokenizer = tokenizer;

    return parseExpression();
  }

  private Expression parseExpression() throws UriParserException {
    Expression left = parseAnd();

    while (is(TokenKind.OR_OP) != null) {
      tokenizer.getText();

      Expression right = parseAnd();
      left = new BinaryImpl(left, BinaryOperatorKind.OR, right);
    }

    return left;
  }

  private Expression parseAnd() throws UriParserException {
    Expression left = parseExprEquality();
    while (is(TokenKind.AND_OP) != null) {
      tokenizer.getText();

      Expression right = parseExprEquality();
      left = new BinaryImpl(left, BinaryOperatorKind.AND, right);
    }
    return left;
  }

  private Expression parseExprEquality() throws UriParserException {
    Expression left = parseExprRel();

    TokenKind nextTokenKind = is(TokenKind.EQ_OP, TokenKind.NE_OP);
    // Null for everything other than EQ or NE
    while (nextTokenKind != null) {
      tokenizer.getText();

      Expression right = parseExprEquality();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(nextTokenKind), right);
      nextTokenKind = is(TokenKind.EQ_OP, TokenKind.NE_OP);
    }

    return left;
  }

  private Expression parseExprRel() throws UriParserException {
    Expression left = parseExprAdd();

    TokenKind nextTokenKind = is(TokenKind.GT_OP, TokenKind.GE_OP, TokenKind.LT_OP, TokenKind.LE_OP);
    // Null for everything other than GT or GE or LT or LE
    while (nextTokenKind != null) {
      tokenizer.getText();

      Expression right = parseExprAdd();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(nextTokenKind), right);
      nextTokenKind = is(TokenKind.GT_OP, TokenKind.GE_OP, TokenKind.LT_OP, TokenKind.LE_OP);
    }

    return left;
  }

  private Expression parseExprAdd() throws UriParserException {
    Expression left = parseExprMul();

    TokenKind nextTokenKind = is(TokenKind.ADD_OP, TokenKind.SUB_OP);
    // Null for everything other than ADD or SUB
    while (nextTokenKind != null) {
      tokenizer.getText();

      Expression right = parseExprMul();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(nextTokenKind), right);
      nextTokenKind = is(TokenKind.ADD_OP, TokenKind.SUB_OP);
    }

    return left;
  }

  private Expression parseExprMul() throws UriParserException {
    Expression left = parseExprUnary();

    TokenKind nextTokenKind = is(TokenKind.MUL_OP, TokenKind.DIV_OP, TokenKind.MOD_OP);
    // Null for everything other than MUL or DIV or MOD
    while (nextTokenKind != null) {
      tokenizer.getText();

      Expression right = parseExprUnary();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(nextTokenKind), right);
      nextTokenKind = is(TokenKind.MUL_OP, TokenKind.DIV_OP, TokenKind.MOD_OP);
    }

    return left;
  }

  private Expression parseExprUnary() throws UriParserException {
    Expression left = null;
    TokenKind nextTokenKind = is(TokenKind.MINUS, TokenKind.NOT);
    // Null for everything other than - or NOT
    while (nextTokenKind != null) {
      tokenizer.getText();

      Expression exp = parseExprValue();
      left = new UnaryImpl(tokenToUnaryOperator.get(nextTokenKind), exp);
      nextTokenKind = is(TokenKind.MINUS, TokenKind.NOT);
    }

    if (left == null) {
      left = parseExprValue();
    }

    return left;
  }

  private Expression parseExprValue() throws UriParserException {
    if (is(TokenKind.OPEN) != null) {
      tokenizer.getText();
      Expression exp = parseExpression();
      require(TokenKind.CLOSE);
      return exp;
    }

    if (is(TokenKind.ParameterAlias) != null) {
      return new AliasImpl(tokenizer.getText());
    }

    if (is(TokenKind.RootExpr) != null) {
      tokenizer.getText();
      // TODO: Consume $root Expression.
    }

    TokenKind nextPrimitive = isPrimitive();
    if (nextPrimitive != null) {
      EdmPrimitiveTypeKind primitiveTypeKind = tokenToPrimitiveType.get(nextPrimitive);
      EdmPrimitiveType type;
      if (primitiveTypeKind == null) {
        if (nextPrimitive == TokenKind.PrimitiveEnumValue) {
          // TODO: Get enum type.
          type = null;
        } else {
          // Null handling
          type = null;
        }
      } else {
        type = EdmPrimitiveTypeFactory.getInstance(primitiveTypeKind);
      }
      return new LiteralImpl(tokenizer.getText(), type);
    }

    TokenKind nextMethod = isMethod();
    if (nextMethod != null) {
      MethodKind methodKind = tokenToMethod.get(nextMethod);
      List<Expression> parameters = new ArrayList<Expression>();
      // Consume Method name.
      tokenizer.getText();
      if (is(TokenKind.CLOSE) != null) {
        // Consume closing parenthesis.
        tokenizer.getText();
      } else {
        parameters.add(parseExpression());
        while (is(TokenKind.COMMA) != null) {
          tokenizer.getText();
          parameters.add(parseExpression());
        }
        require(TokenKind.CLOSE);
      }

      MethodImpl methodImpl = new MethodImpl(methodKind, parameters);
      validateMethodParameters(methodImpl);

      return methodImpl;
    }

    throw new UriParserSyntaxException("Unexpected token", UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  private void validateMethodParameters(final Method method) throws UriParserException {
    // We might validate parameter types in the future.
    int size = method.getParameters().size();
    switch (method.getMethod()) {
    // Must have two Parameters.
    case CONTAINS:
    case ENDSWITH:
    case STARTSWITH:
    case INDEXOF:
    case CONCAT:
    case GEODISTANCE:
    case GEOINTERSECTS:
      if (size != 2) {
        throw new UriParserSemanticException(
            "The method " + method.getMethod() + " needs exactly two parameters.",
            null); // TODO: message key
      }
      break;
    // Must have one parameter.
    case LENGTH:
    case TOLOWER:
    case TOUPPER:
    case TRIM:
    case YEAR:
    case MONTH:
    case DAY:
    case HOUR:
    case MINUTE:
    case SECOND:
    case FRACTIONALSECONDS:
    case DATE:
    case TIME:
    case TOTALOFFSETMINUTES:
    case TOTALSECONDS:
    case ROUND:
    case FLOOR:
    case CEILING:
    case GEOLENGTH:
      if (size != 1) {
        throw new UriParserSemanticException(
            "The method '" + method.getMethod() + "' needs exactly one parameter.",
            null); // TODO: message key
      }
      break;
    // Must have no parameter.
    case NOW:
    case MAXDATETIME:
    case MINDATETIME:
      if (size != 0) {
        throw new UriParserSemanticException("The method '" + method.getMethod() + "' must have no parameters.",
            null); // TODO: message key
      }
      break;
    // Variable parameter number
    case CAST:
    case ISOF:
      if (size < 1 || size > 2) {
        throw new UriParserSemanticException(
            "The method '" + method.getMethod() + "' must have one or two parameters.",
            null); // TODO: message key
      }
      break;
    case SUBSTRING:
      if (size < 2 || size > 3) {
        throw new UriParserSemanticException(
            "The method '" + method.getMethod() + "' must have two or three parameters.",
            null); // TODO: message key
      }
      break;
    default:
      throw new UriParserSemanticException(
          "Unkown method '" + method.getMethod() + "'",
          null); // TODO: message key
    }
  }

  private String require(TokenKind required) throws UriParserException {
    if (is(required) == null) {
      throw new UriParserSyntaxException("Required token: " + required,
          UriParserSyntaxException.MessageKeys.SYNTAX);
    }
    return tokenizer.getText();
  }

  private TokenKind is(TokenKind... kind) {
    for (int i = 0; i < kind.length; i++) {
      if (tokenizer.next(kind[i])) {
        return kind[i];
      }
    }
    return null;
  }

  private TokenKind isMethod() {
    return is(TokenKind.Cast,
        TokenKind.Ceiling,
        TokenKind.Concat,
        TokenKind.Contains,
        TokenKind.Date,
        TokenKind.Day,
        TokenKind.Endswith,
        TokenKind.Floor,
        TokenKind.Fractionalseconds,
        TokenKind.GeoDistance,
        TokenKind.GeoIntersects,
        TokenKind.GeoLength,
        TokenKind.Hour,
        TokenKind.Indexof,
        TokenKind.Isof,
        TokenKind.Length,
        TokenKind.Maxdatetime,
        TokenKind.Mindatetime,
        TokenKind.Minute,
        TokenKind.Month,
        TokenKind.Now,
        TokenKind.Round,
        TokenKind.Second,
        TokenKind.Startswith,
        TokenKind.Substring,
        TokenKind.Time,
        TokenKind.Tolower,
        TokenKind.Totaloffsetminutes,
        TokenKind.Totalseconds,
        TokenKind.Toupper,
        TokenKind.Trim,
        TokenKind.Year);
  }

  private TokenKind isPrimitive() {
    return is(TokenKind.PrimitiveNullValue,
        TokenKind.PrimitiveBooleanValue,
        TokenKind.PrimitiveStringValue,

        // The order of the next seven expressions is important in order to avoid
        // finding partly parsed tokens (counter-intuitive as it may be, even a GUID may start with digits ...).
        TokenKind.PrimitiveDoubleValue,
        TokenKind.PrimitiveDecimalValue,
        TokenKind.PrimitiveGuidValue,
        TokenKind.PrimitiveDateTimeOffsetValue,
        TokenKind.PrimitiveDateValue,
        TokenKind.PrimitiveTimeOfDayValue,
        TokenKind.PrimitiveIntegerValue,
        TokenKind.PrimitiveDurationValue,
        TokenKind.PrimitiveBinaryValue,
        TokenKind.PrimitiveEnumValue);
  }

  public enum TokenKind {
    // BINARY
    OR_OP,
    AND_OP,

    EQ_OP,
    NE_OP,

    GT_OP,
    GE_OP,
    LT_OP,
    LE_OP,

    ADD_OP,
    SUB_OP,

    MUL_OP,
    DIV_OP,
    MOD_OP,

    MINUS,
    NOT,

    // Grouping
    OPEN,
    CLOSE,

    // PrimitiveValues
    PrimitiveNullValue,
    PrimitiveBooleanValue,

    PrimitiveStringValue,
    PrimitiveIntegerValue,
    PrimitiveGuidValue,
    PrimitiveDateValue,
    PrimitiveDateTimeOffsetValue,
    PrimitiveTimeOfDayValue,
    PrimitiveDecimalValue,
    PrimitiveDoubleValue,
    PrimitiveDurationValue,
    PrimitiveBinaryValue,
    PrimitiveEnumValue,

    // ExpressionValues
    ParameterAlias,
    ArrayOrObject,
    RootExpr,
    IT,

    // BuiltInMethods
    Cast,
    Ceiling,
    Concat,
    Contains,
    Date,
    Day,
    Endswith,
    Floor,
    Fractionalseconds,
    GeoDistance,
    GeoIntersects,
    GeoLength,
    Hour,
    Indexof,
    Isof,
    Length,
    Maxdatetime,
    Mindatetime,
    Minute,
    Month,
    Now,
    Round,
    Second,
    Startswith,
    Substring,
    Time,
    Tolower,
    Totaloffsetminutes,
    Totalseconds,
    Toupper,
    Trim,
    Year,
    COMMA
  }

  public static class Token {
    TokenKind kind;
    String text;

    public Token(TokenKind kind, String text) {
      this.kind = kind;
      this.text = text;
    }
  }

  public static class Tokenizer {
    private List<Token> tokens;
    int counter = 0;

    public Tokenizer(List<Token> tokens) {
      this.tokens = tokens;
    }

    public boolean next(TokenKind expectedKind) {
      if (counter < tokens.size() && expectedKind == tokens.get(counter).kind) {
        return true;
      }
      return false;
    }

    public String getText() {
      String text = tokens.get(counter).text;
      counter++;
      return text;
    }
  }

}
