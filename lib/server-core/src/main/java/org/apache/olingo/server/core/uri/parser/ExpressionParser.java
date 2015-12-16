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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.queryoption.expression.Alias;
import org.apache.olingo.server.api.uri.queryoption.expression.Binary;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Enumeration;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.LambdaRef;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.Method;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.TypeLiteral;
import org.apache.olingo.server.api.uri.queryoption.expression.Unary;
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

  // 'cast' and 'isof' are handled specially.
  private static final Map<TokenKind, MethodKind> tokenToMethod;
  static {
    Map<TokenKind, MethodKind> temp = new HashMap<TokenKind, MethodKind>();
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

  private final Edm edm;
  private final OData odata;

  private UriTokenizer tokenizer;

  public ExpressionParser(final Edm edm, final OData odata) {
    this.edm = edm;
    this.odata = odata;
  }

  public Expression parse(UriTokenizer tokenizer) throws UriParserException {
    // Initialize tokenizer.
    this.tokenizer = tokenizer;

    return parseExpression();
  }

  private Expression parseExpression() throws UriParserException {
    Expression left = parseAnd();
    while (tokenizer.next(TokenKind.OrOperator)) {
      final Expression right = parseAnd();
      checkType(left, EdmPrimitiveTypeKind.Boolean);
      checkType(right, EdmPrimitiveTypeKind.Boolean);
      left = new BinaryImpl(left, BinaryOperatorKind.OR, right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean));
    }
    return left;
  }

  private Expression parseAnd() throws UriParserException {
    Expression left = parseExprEquality();
    while (tokenizer.next(TokenKind.AndOperator)) {
      final Expression right = parseExprEquality();
      checkType(left, EdmPrimitiveTypeKind.Boolean);
      checkType(right, EdmPrimitiveTypeKind.Boolean);
      left = new BinaryImpl(left, BinaryOperatorKind.AND, right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean));
    }
    return left;
  }

  private Expression parseExprEquality() throws UriParserException {
    Expression left = parseExprRel();
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.EqualsOperator, TokenKind.NotEqualsOperator);
    // Null for everything other than EQ or NE
    while (operatorTokenKind != null) {
      final Expression right = parseExprEquality();
      checkEqualityTypes(left, right);
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean));
      operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.EqualsOperator, TokenKind.NotEqualsOperator);
    }
    return left;
  }

  // TODO: The 'isof' method has relational precedence and should appear here.
  private Expression parseExprRel() throws UriParserException {
    Expression left = parseExprAdd();
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer,
        TokenKind.GreaterThanOperator, TokenKind.GreaterThanOrEqualsOperator,
        TokenKind.LessThanOperator, TokenKind.LessThanOrEqualsOperator);
    // Null for everything other than GT or GE or LT or LE
    while (operatorTokenKind != null) {
      final Expression right = parseExprAdd();
      checkRelationTypes(left, right);
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean));
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
      checkAddSubTypes(left, right, operatorTokenKind == TokenKind.AddOperator);
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double));
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
      checkType(left,
          EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
          EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
          EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double);
      checkType(right,
          EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
          EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
          EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double);
      left = new BinaryImpl(left, tokenToBinaryOperator.get(operatorTokenKind), right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double));
      operatorTokenKind = ParserHelper.next(tokenizer,
          TokenKind.MulOperator, TokenKind.DivOperator, TokenKind.ModOperator);
    }
    return left;
  }

  // TODO: The 'cast' method has unary precedence and should appear here.
  private Expression parseExprUnary() throws UriParserException {
    Expression left = null;
    TokenKind operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.MINUS, TokenKind.NotOperator);
    // Null for everything other than - or NOT
    while (operatorTokenKind != null) {
      final Expression expression = parseExprPrimary();
      if (operatorTokenKind == TokenKind.NotOperator) {
        checkType(expression, EdmPrimitiveTypeKind.Boolean);
      } else {
        checkType(expression,
            EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
            EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
            EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double,
            EdmPrimitiveTypeKind.Duration);
      }
      left = new UnaryImpl(tokenToUnaryOperator.get(operatorTokenKind), expression, getType(expression));
      operatorTokenKind = ParserHelper.next(tokenizer, TokenKind.MINUS, TokenKind.NotOperator);
    }
    if (left == null) {
      left = parseExprPrimary();
    }
    return left;
  }

  private Expression parseExprPrimary() throws UriParserException {
    final Expression left = parseExprValue();
    if (isEnumType(left) && tokenizer.next(TokenKind.HasOperator)) {
      ParserHelper.requireNext(tokenizer, TokenKind.EnumValue);
      final String primitiveValueLiteral = tokenizer.getText();
      final Expression right = new LiteralImpl(primitiveValueLiteral, getEnumType(primitiveValueLiteral));
      checkEnumLiteral(right);
      return new BinaryImpl(left, BinaryOperatorKind.HAS, right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean));
    } else {
      return left;
    }
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

    TokenKind nextPrimitive = ParserHelper.nextPrimitive(tokenizer);
    if (nextPrimitive != null) {
      final String primitiveValueLiteral = tokenizer.getText();
      final EdmPrimitiveTypeKind primitiveTypeKind = tokenToPrimitiveType.get(nextPrimitive);
      EdmPrimitiveType type;
      if (primitiveTypeKind == null) {
        if (nextPrimitive == TokenKind.EnumValue) {
          type = getEnumType(primitiveValueLiteral);
        } else {
          // Null handling
          type = null;
        }
      } else {
        type = odata.createPrimitiveTypeInstance(primitiveTypeKind);
      }
      return new LiteralImpl(primitiveValueLiteral, type);
    }

    // The method token text includes the opening parenthesis so that method calls can be recognized unambiguously.
    // OData identifiers have to be considered after that.
    TokenKind nextMethod = nextMethod();
    if (nextMethod != null) {
      MethodKind methodKind = tokenToMethod.get(nextMethod);
      return new MethodImpl(methodKind, parseMethodParameters(methodKind));
    }

    if (tokenizer.next(TokenKind.QualifiedName)) {
      // TODO: Consume typecast or bound-function expression.
    }

    if (tokenizer.next(TokenKind.ODataIdentifier)) {
      // TODO: Consume property-path or lambda-variable expression.
    }

    throw new UriParserSyntaxException("Unexpected token", UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  private List<Expression> parseMethodParameters(final MethodKind methodKind) throws UriParserException {
    List<Expression> parameters = new ArrayList<Expression>();
    switch (methodKind) {
    // Must have no parameter.
    case NOW:
    case MAXDATETIME:
    case MINDATETIME:
      break;

    // Must have one parameter.
    case LENGTH:
    case TOLOWER:
    case TOUPPER:
    case TRIM:
      final Expression stringParameter = parseExpression();
      checkType(stringParameter, EdmPrimitiveTypeKind.String);
      parameters.add(stringParameter);
      break;
    case YEAR:
    case MONTH:
    case DAY:
      final Expression dateParameter = parseExpression();
      checkType(dateParameter, EdmPrimitiveTypeKind.Date, EdmPrimitiveTypeKind.DateTimeOffset);
      parameters.add(dateParameter);
      break;
    case HOUR:
    case MINUTE:
    case SECOND:
    case FRACTIONALSECONDS:
      final Expression timeParameter = parseExpression();
      checkType(timeParameter, EdmPrimitiveTypeKind.TimeOfDay, EdmPrimitiveTypeKind.DateTimeOffset);
      parameters.add(timeParameter);
      break;
    case DATE:
    case TIME:
    case TOTALOFFSETMINUTES:
      final Expression dateTimeParameter = parseExpression();
      checkType(dateTimeParameter, EdmPrimitiveTypeKind.DateTimeOffset);
      parameters.add(dateTimeParameter);
      break;
    case TOTALSECONDS:
      final Expression durationParameter = parseExpression();
      checkType(durationParameter, EdmPrimitiveTypeKind.Duration);
      parameters.add(durationParameter);
      break;
    case ROUND:
    case FLOOR:
    case CEILING:
      final Expression decimalParameter = parseExpression();
      checkType(decimalParameter,
          EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double);
      parameters.add(decimalParameter);
      break;
    case GEOLENGTH:
      final Expression geoParameter = parseExpression();
      checkType(geoParameter,
          EdmPrimitiveTypeKind.GeographyLineString, EdmPrimitiveTypeKind.GeometryLineString);
      parameters.add(geoParameter);
      break;

    // Must have two parameters.
    case CONTAINS:
    case ENDSWITH:
    case STARTSWITH:
    case INDEXOF:
    case CONCAT:
      final Expression stringParameter1 = parseExpression();
      checkType(stringParameter1, EdmPrimitiveTypeKind.String);
      parameters.add(stringParameter1);
      ParserHelper.requireNext(tokenizer, TokenKind.COMMA);
      final Expression stringParameter2 = parseExpression();
      checkType(stringParameter2, EdmPrimitiveTypeKind.String);
      parameters.add(stringParameter2);
      break;
    case GEODISTANCE:
      final Expression geoParameter1 = parseExpression();
      checkType(geoParameter1, EdmPrimitiveTypeKind.GeographyPoint, EdmPrimitiveTypeKind.GeometryPoint);
      parameters.add(geoParameter1);
      ParserHelper.requireNext(tokenizer, TokenKind.COMMA);
      final Expression geoParameter2 = parseExpression();
      checkType(geoParameter2, EdmPrimitiveTypeKind.GeographyPoint, EdmPrimitiveTypeKind.GeometryPoint);
      parameters.add(geoParameter2);
      break;
    case GEOINTERSECTS:
      final Expression geoPointParameter = parseExpression();
      checkType(geoPointParameter,
          EdmPrimitiveTypeKind.GeographyPoint, EdmPrimitiveTypeKind.GeometryPoint);
      parameters.add(geoPointParameter);
      ParserHelper.requireNext(tokenizer, TokenKind.COMMA);
      final Expression geoPolygonParameter = parseExpression();
      checkType(geoPolygonParameter,
          EdmPrimitiveTypeKind.GeographyPolygon, EdmPrimitiveTypeKind.GeometryPolygon);
      parameters.add(geoPolygonParameter);
      break;

    // Can have two or three parameters.
    case SUBSTRING:
      final Expression parameterFirst = parseExpression();
      checkType(parameterFirst, EdmPrimitiveTypeKind.String);
      parameters.add(parameterFirst);
      ParserHelper.requireNext(tokenizer, TokenKind.COMMA);
      final Expression parameterSecond = parseExpression();
      checkType(parameterSecond,
          EdmPrimitiveTypeKind.Int64, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int16,
          EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte);
      parameters.add(parameterSecond);
      if (tokenizer.next(TokenKind.COMMA)) {
        final Expression parameterThird = parseExpression();
        checkType(parameterThird,
            EdmPrimitiveTypeKind.Int64, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int16,
            EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte);
        parameters.add(parameterThird);
      }
      break;

    default:
      throw new UriParserSemanticException("Unkown method '" + methodKind.name() + "'",
          UriParserSemanticException.MessageKeys.NOT_IMPLEMENTED, methodKind.name()); // TODO: better message
    }
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return parameters;
  }

  private TokenKind nextMethod() {
    return ParserHelper.next(tokenizer,
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

  private EdmType getType(final Expression expression) throws UriParserException {
    EdmType type;
    if (expression instanceof Literal) {
      type = ((Literal) expression).getType();
    } else if (expression instanceof TypeLiteral) {
      type = ((TypeLiteral) expression).getType();
    } else if (expression instanceof Enumeration) {
      type = ((Enumeration) expression).getType();
    } else if (expression instanceof Member) {
      type = ((Member) expression).getType();
    } else if (expression instanceof Unary) {
      type = ((UnaryImpl) expression).getType();
    } else if (expression instanceof Binary) {
      type = ((BinaryImpl) expression).getType();
    } else if (expression instanceof Method) {
      type = ((MethodImpl) expression).getType();
    } else if (expression instanceof LambdaRef) {
      throw new UriParserSemanticException("Type determination not implemented.",
          UriParserSemanticException.MessageKeys.NOT_IMPLEMENTED, expression.toString());
    } else if (expression instanceof Alias) {
      type = null; // The alias would have to be available already parsed.
    } else {
      throw new UriParserSemanticException("Unknown expression type.",
          UriParserSemanticException.MessageKeys.NOT_IMPLEMENTED, expression.toString());
    }
    if (type != null && type.getKind() == EdmTypeKind.DEFINITION) {
      type = ((EdmTypeDefinition) type).getUnderlyingType();
    }
    return type;
  }

  private boolean isType(final Expression expression, final EdmPrimitiveTypeKind... kinds) throws UriParserException {
    final EdmType expressionType = getType(expression);
    if (expressionType == null) {
      return true;
    }
    for (final EdmPrimitiveTypeKind kind : kinds) {
      if (expressionType.equals(odata.createPrimitiveTypeInstance(kind))) {
        return true;
      }
    }
    return false;
  }

  private void checkType(final Expression expression, final EdmPrimitiveTypeKind... kinds) throws UriParserException {
    if (!isType(expression, kinds)) {
      throw new UriParserSemanticException("Incompatible type.",
          UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, // TODO: better message
          getType(expression) == null ?
              "" :
              getType(expression).getFullQualifiedName().getFullQualifiedNameAsString());
    }
  }

  private void checkEqualityTypes(final Expression left, final Expression right) throws UriParserException {
    final EdmType leftType = getType(left);
    final EdmType rightType = getType(right);
    if (leftType == null || rightType == null || leftType.equals(rightType)) {
      return;
    }
    if (leftType.getKind() != EdmTypeKind.PRIMITIVE
        || rightType.getKind() != EdmTypeKind.PRIMITIVE
        || !(((EdmPrimitiveType) leftType).isCompatible((EdmPrimitiveType) rightType)
        || ((EdmPrimitiveType) rightType).isCompatible((EdmPrimitiveType) leftType))) {
      throw new UriParserSemanticException("Incompatible types.",
          UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, ""); // TODO: better message
    }
  }

  private EdmPrimitiveType getEnumType(final String primitiveValueLiteral) throws UriParserException {
    final String enumTypeName = primitiveValueLiteral.substring(0, primitiveValueLiteral.indexOf('\''));
    final EdmPrimitiveType type = edm.getEnumType(new FullQualifiedName(enumTypeName));
    if (type == null) {
      throw new UriParserSemanticException("Unknown Enum type '" + enumTypeName + "'.",
          UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, enumTypeName);
    }
    return type;
  }

  private boolean isEnumType(final Expression expression) throws UriParserException {
    final EdmType expressionType = getType(expression);
    return expressionType == null
        || expressionType.getKind() == EdmTypeKind.ENUM
        || isType(expression,
            EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
            EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte);
  }

  private void checkEnumLiteral(final Expression expression) throws UriParserException {
    if (expression == null
        || !(expression instanceof Literal)
        || ((Literal) expression).getType() == null
        || ((Literal) expression).getType().getKind() != EdmTypeKind.ENUM) {
      throw new UriParserSemanticException("Enum literal expected.",
          UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, ""); // TODO: better message
    }
  }

  private void checkRelationTypes(final Expression left, final Expression right) throws UriParserException {
    final EdmType leftType = getType(left);
    final EdmType rightType = getType(right);
    if (leftType == null || rightType == null) {
      return;
    }
    checkType(left,
        EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
        EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
        EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double,
        EdmPrimitiveTypeKind.Boolean, EdmPrimitiveTypeKind.Guid, EdmPrimitiveTypeKind.String,
        EdmPrimitiveTypeKind.Date, EdmPrimitiveTypeKind.TimeOfDay,
        EdmPrimitiveTypeKind.DateTimeOffset, EdmPrimitiveTypeKind.Duration);
    checkType(right,
        EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
        EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
        EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double,
        EdmPrimitiveTypeKind.Boolean, EdmPrimitiveTypeKind.Guid, EdmPrimitiveTypeKind.String,
        EdmPrimitiveTypeKind.Date, EdmPrimitiveTypeKind.TimeOfDay,
        EdmPrimitiveTypeKind.DateTimeOffset, EdmPrimitiveTypeKind.Duration);
    if (!(((EdmPrimitiveType) leftType).isCompatible((EdmPrimitiveType) rightType)
    || ((EdmPrimitiveType) rightType).isCompatible((EdmPrimitiveType) leftType))) {
      throw new UriParserSemanticException("Incompatible types.",
          UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, ""); // TODO: better message
    }
  }

  private void checkAddSubTypes(final Expression left, final Expression right, final boolean isAdd)
      throws UriParserException {
    final EdmType leftType = getType(left);
    final EdmType rightType = getType(right);
    if (leftType == null || rightType == null
        || isType(left,
            EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
            EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
            EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double)
        && isType(right,
            EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
            EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
            EdmPrimitiveTypeKind.Decimal, EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double)) {
      return;
    }
    if (isType(left, EdmPrimitiveTypeKind.DateTimeOffset)
        && (isType(right, EdmPrimitiveTypeKind.Duration)
        || isType(right, EdmPrimitiveTypeKind.DateTimeOffset) && !isAdd)) {
      return;
    }
    if (isType(left, EdmPrimitiveTypeKind.Duration) && isType(right, EdmPrimitiveTypeKind.Duration)
        || isType(left, EdmPrimitiveTypeKind.Date)
        && (isType(right, EdmPrimitiveTypeKind.Duration) || isType(right, EdmPrimitiveTypeKind.Date) && !isAdd)) {
      return;
    }
    throw new UriParserSemanticException("Incompatible types.",
        UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, ""); // TODO: better message
  }
}
