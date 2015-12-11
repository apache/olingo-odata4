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
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.queryoption.expression.AliasImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MethodImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.UnaryImpl;

public class ExpressionParser {
  private static final Map<TokenKind, BinaryOperatorKind> tokenToBinaryOperator;
  static {
    Map<TokenKind, BinaryOperatorKind> temp = new HashMap<TokenKind, BinaryOperatorKind>();
    temp.put(TokenKind.OrOperator, BinaryOperatorKind.OR);
    temp.put(TokenKind.AndOperator, BinaryOperatorKind.AND);

    temp.put(TokenKind.EqualsOperator, BinaryOperatorKind.EQ);
    temp.put(TokenKind.NotEqualsOperator, BinaryOperatorKind.NE);

    temp.put(TokenKind.GreaterThanOperator, BinaryOperatorKind.GT);
    temp.put(TokenKind.GreaterThanOrEqualsOperator, BinaryOperatorKind.GE);
    temp.put(TokenKind.LessThanOperator, BinaryOperatorKind.LT);
    temp.put(TokenKind.LessThanOrEqualsOperator, BinaryOperatorKind.LE);

    temp.put(TokenKind.AddOperator, BinaryOperatorKind.ADD);
    temp.put(TokenKind.SubOperator, BinaryOperatorKind.SUB);

    temp.put(TokenKind.MulOperator, BinaryOperatorKind.MUL);
    temp.put(TokenKind.DivOperator, BinaryOperatorKind.DIV);
    temp.put(TokenKind.ModOperator, BinaryOperatorKind.MOD);

    tokenToBinaryOperator = Collections.unmodifiableMap(temp);
  }

  private static final Map<TokenKind, UnaryOperatorKind> tokenToUnaryOperator;
  static {
    Map<TokenKind, UnaryOperatorKind> temp = new HashMap<TokenKind, UnaryOperatorKind>();
    temp.put(TokenKind.MINUS, UnaryOperatorKind.MINUS);
    temp.put(TokenKind.NotOperator, UnaryOperatorKind.NOT);
    tokenToUnaryOperator = Collections.unmodifiableMap(temp);
  }

  private static final Map<TokenKind, MethodKind> tokenToMethod;
  static {
    Map<TokenKind, MethodKind> temp = new HashMap<TokenKind, MethodKind>();
    temp.put(TokenKind.CastMethod, MethodKind.CAST);
    temp.put(TokenKind.CeilingMethod, MethodKind.CEILING);
    temp.put(TokenKind.ConcatMethod, MethodKind.CONCAT);
    temp.put(TokenKind.ContainsMethod, MethodKind.CONTAINS);
    temp.put(TokenKind.DateMethod, MethodKind.DATE);
    temp.put(TokenKind.DayMethod, MethodKind.DAY);
    temp.put(TokenKind.EndswithMethod, MethodKind.ENDSWITH);
    temp.put(TokenKind.FloorMethod, MethodKind.FLOOR);
    temp.put(TokenKind.FractionalsecondsMethod, MethodKind.FRACTIONALSECONDS);
    temp.put(TokenKind.GeoDistanceMethod, MethodKind.GEODISTANCE);
    temp.put(TokenKind.GeoIntersectsMethod, MethodKind.GEOINTERSECTS);
    temp.put(TokenKind.GeoLengthMethod, MethodKind.GEOLENGTH);
    temp.put(TokenKind.HourMethod, MethodKind.HOUR);
    temp.put(TokenKind.IndexofMethod, MethodKind.INDEXOF);
    temp.put(TokenKind.IsofMethod, MethodKind.ISOF);
    temp.put(TokenKind.LengthMethod, MethodKind.LENGTH);
    temp.put(TokenKind.MaxdatetimeMethod, MethodKind.MAXDATETIME);
    temp.put(TokenKind.MindatetimeMethod, MethodKind.MINDATETIME);
    temp.put(TokenKind.MinuteMethod, MethodKind.MINUTE);
    temp.put(TokenKind.MonthMethod, MethodKind.MONTH);
    temp.put(TokenKind.NowMethod, MethodKind.NOW);
    temp.put(TokenKind.RoundMethod, MethodKind.ROUND);
    temp.put(TokenKind.SecondMethod, MethodKind.SECOND);
    temp.put(TokenKind.StartswithMethod, MethodKind.STARTSWITH);
    temp.put(TokenKind.SubstringMethod, MethodKind.SUBSTRING);
    temp.put(TokenKind.TimeMethod, MethodKind.TIME);
    temp.put(TokenKind.TolowerMethod, MethodKind.TOLOWER);
    temp.put(TokenKind.TotaloffsetminutesMethod, MethodKind.TOTALOFFSETMINUTES);
    temp.put(TokenKind.TotalsecondsMethod, MethodKind.TOTALSECONDS);
    temp.put(TokenKind.ToupperMethod, MethodKind.TOUPPER);
    temp.put(TokenKind.TrimMethod, MethodKind.TRIM);
    temp.put(TokenKind.YearMethod, MethodKind.YEAR);

    tokenToMethod = Collections.unmodifiableMap(temp);
  }

  private static final Map<TokenKind, EdmPrimitiveTypeKind> tokenToPrimitiveType;
  static {
    /* Enum and null are not present in the map. These have to be handled differently */
    Map<TokenKind, EdmPrimitiveTypeKind> temp = new HashMap<TokenKind, EdmPrimitiveTypeKind>();
    temp.put(TokenKind.BooleanValue, EdmPrimitiveTypeKind.Boolean);
    temp.put(TokenKind.StringValue, EdmPrimitiveTypeKind.String);
    // TODO:Check if int64 is correct here or if it has to be single instead
    temp.put(TokenKind.IntegerValue, EdmPrimitiveTypeKind.Int64);
    temp.put(TokenKind.GuidValue, EdmPrimitiveTypeKind.Guid);
    temp.put(TokenKind.DateValue, EdmPrimitiveTypeKind.Date);
    temp.put(TokenKind.DateTimeOffsetValue, EdmPrimitiveTypeKind.DateTimeOffset);
    temp.put(TokenKind.TimeOfDayValue, EdmPrimitiveTypeKind.TimeOfDay);
    temp.put(TokenKind.DecimalValue, EdmPrimitiveTypeKind.Decimal);
    temp.put(TokenKind.DoubleValue, EdmPrimitiveTypeKind.Double);
    temp.put(TokenKind.DurationValue, EdmPrimitiveTypeKind.Duration);
    temp.put(TokenKind.BinaryValue, EdmPrimitiveTypeKind.Binary);

    tokenToPrimitiveType = Collections.unmodifiableMap(temp);
  }

  private UriTokenizer tokenizer;

  public Expression parse(UriTokenizer tokenizer) throws UriParserException {
    // Initialize tokenizer.
    this.tokenizer = tokenizer;

    return parseExpression();
  }

  private Expression parseExpression() throws UriParserException {
    Expression left = parseAnd();
    while (tokenizer.next(TokenKind.OrOperator)) {
      final Expression right = parseAnd();
      left = new BinaryImpl(left, BinaryOperatorKind.OR, right);
    }
    return left;
  }

  private Expression parseAnd() throws UriParserException {
    Expression left = parseExprEquality();
    while (tokenizer.next(TokenKind.AndOperator)) {
      final Expression right = parseExprEquality();
      left = new BinaryImpl(left, BinaryOperatorKind.AND, right);
    }
    return left;
  }

  private Expression parseExprEquality() throws UriParserException {
    Expression left = parseExprRel();
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.EqualsOperator, TokenKind.NotEqualsOperator);
    // Null for everything other than EQ or NE
    while (operatorTokenKind != null) {
      final Expression right = parseExprEquality();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right);
      operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.EqualsOperator, TokenKind.NotEqualsOperator);
    }
    return left;
  }

  private Expression parseExprRel() throws UriParserException {
    Expression left = parseExprAdd();
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer,
        TokenKind.GreaterThanOperator, TokenKind.GreaterThanOrEqualsOperator,
        TokenKind.LessThanOperator, TokenKind.LessThanOrEqualsOperator);
    // Null for everything other than GT or GE or LT or LE
    while (operatorTokenKind != null) {
      final Expression right = parseExprAdd();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right);
      operatorTokenKind = ParserHelper.next(tokenizer,
          TokenKind.GreaterThanOperator, TokenKind.GreaterThanOrEqualsOperator,
          TokenKind.LessThanOperator, TokenKind.LessThanOrEqualsOperator);
    }
    return left;
  }

  private Expression parseExprAdd() throws UriParserException {
    Expression left = parseExprMul();
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.AddOperator, TokenKind.SubOperator);
    // Null for everything other than ADD or SUB
    while (operatorTokenKind != null) {
      final Expression right = parseExprMul();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right);
      operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.AddOperator, TokenKind.SubOperator);
    }
    return left;
  }

  private Expression parseExprMul() throws UriParserException {
    Expression left = parseExprUnary();
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer,
        TokenKind.MulOperator, TokenKind.DivOperator, TokenKind.ModOperator);
    // Null for everything other than MUL or DIV or MOD
    while (operatorTokenKind != null) {
      final Expression right = parseExprUnary();
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right);
      operatorTokenKind = ParserHelper.next(tokenizer,
          TokenKind.MulOperator, TokenKind.DivOperator, TokenKind.ModOperator);
    }
    return left;
  }

  private Expression parseExprUnary() throws UriParserException {
    Expression left = null;
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.MINUS, TokenKind.NotOperator);
    // Null for everything other than - or NOT
    while (operatorTokenKind != null) {
      final Expression expression = parseExprValue();
      left = new UnaryImpl(tokenToUnaryOperator.get(operatorTokenKind), expression);
      operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.MINUS, TokenKind.NotOperator);
    }
    if (left == null) {
      left = parseExprValue();
    }
    return left;
  }

  private Expression parseExprValue() throws UriParserException {
    if (tokenizer.next(TokenKind.OPEN)) {
      final Expression expression = parseExpression();
      ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
      return expression;
    }

    if (tokenizer.next(TokenKind.ParameterAliasName)) {
      return new AliasImpl(tokenizer.getText());
    }

    if (tokenizer.next(TokenKind.jsonArrayOrObject)) {
      // TODO: Can the type be determined?
      return new LiteralImpl(tokenizer.getText(), null);
    }

    if (tokenizer.next(TokenKind.ROOT)) {
      // TODO: Consume $root expression.
    }

    if (tokenizer.next(TokenKind.IT)) {
      // TODO: Consume $it expression.
    }

    if (tokenizer.next(TokenKind.QualifiedName)) {
      // TODO: Consume typecast or bound-function expression.
    }

    TokenKind nextPrimitive = ParserHelper.nextPrimitive(tokenizer);
    if (nextPrimitive != null) {
      final EdmPrimitiveTypeKind primitiveTypeKind = tokenToPrimitiveType.get(nextPrimitive);
      EdmPrimitiveType type;
      if (primitiveTypeKind == null) {
        if (nextPrimitive == TokenKind.EnumValue) {
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

    TokenKind nextMethod = nextMethod();
    if (nextMethod != null) {
      MethodKind methodKind = tokenToMethod.get(nextMethod);
      List<Expression> parameters = new ArrayList<Expression>();
      // The method token text includes the opening parenthesis!
      if (!tokenizer.next(TokenKind.CLOSE)) {
        do {
          parameters.add(parseExpression());
        } while (tokenizer.next(TokenKind.COMMA));
        ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
      }

      MethodImpl methodImpl = new MethodImpl(methodKind, parameters);
      validateMethodParameters(methodImpl);

      return methodImpl;
    }

    if (tokenizer.next(TokenKind.ODataIdentifier)) {
      // TODO: Consume property-path or lambda-variable expression.
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
      if (size > 0) {
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

  private TokenKind nextMethod() {
    return ParserHelper.next(tokenizer,
        TokenKind.CastMethod,
        TokenKind.CeilingMethod,
        TokenKind.ConcatMethod,
        TokenKind.ContainsMethod,
        TokenKind.DateMethod,
        TokenKind.DayMethod,
        TokenKind.EndswithMethod,
        TokenKind.FloorMethod,
        TokenKind.FractionalsecondsMethod,
        TokenKind.GeoDistanceMethod,
        TokenKind.GeoIntersectsMethod,
        TokenKind.GeoLengthMethod,
        TokenKind.HourMethod,
        TokenKind.IndexofMethod,
        TokenKind.IsofMethod,
        TokenKind.LengthMethod,
        TokenKind.MaxdatetimeMethod,
        TokenKind.MindatetimeMethod,
        TokenKind.MinuteMethod,
        TokenKind.MonthMethod,
        TokenKind.NowMethod,
        TokenKind.RoundMethod,
        TokenKind.SecondMethod,
        TokenKind.StartswithMethod,
        TokenKind.SubstringMethod,
        TokenKind.TimeMethod,
        TokenKind.TolowerMethod,
        TokenKind.TotaloffsetminutesMethod,
        TokenKind.TotalsecondsMethod,
        TokenKind.ToupperMethod,
        TokenKind.TrimMethod,
        TokenKind.YearMethod);
  }
}
