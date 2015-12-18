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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceLambdaVariable;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
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
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceComplexPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceCountImpl;
import org.apache.olingo.server.core.uri.UriResourceEntitySetImpl;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.UriResourceItImpl;
import org.apache.olingo.server.core.uri.UriResourceLambdaAllImpl;
import org.apache.olingo.server.core.uri.UriResourceLambdaAnyImpl;
import org.apache.olingo.server.core.uri.UriResourceLambdaVarImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourcePrimitivePropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceRootImpl;
import org.apache.olingo.server.core.uri.UriResourceSingletonImpl;
import org.apache.olingo.server.core.uri.UriResourceStartingTypeFilterImpl;
import org.apache.olingo.server.core.uri.UriResourceTypedImpl;
import org.apache.olingo.server.core.uri.UriResourceWithKeysImpl;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.queryoption.expression.AliasImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.EnumerationImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MethodImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.TypeLiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.UnaryImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

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
    // TODO: Check if int64 is correct here or if it has to be decimal or single or double instead.
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
  private Deque<UriResourceLambdaVariable> lambdaVariables = new ArrayDeque<UriResourceLambdaVariable>();
  private EdmType referringType;
  private Collection<String> crossjoinEntitySetNames;

  public ExpressionParser(final Edm edm, final OData odata) {
    this.edm = edm;
    this.odata = odata;
  }

  public Expression parse(UriTokenizer tokenizer, final EdmType referringType,
      final Collection<String> crossjoinEntitySetNames)
      throws UriParserException, UriValidationException {
    // Initialize tokenizer.
    this.tokenizer = tokenizer;
    this.referringType = referringType;
    this.crossjoinEntitySetNames = crossjoinEntitySetNames;

    return parseExpression();
  }

  private Expression parseExpression() throws UriParserException, UriValidationException {
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

  private Expression parseAnd() throws UriParserException, UriValidationException {
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

  private Expression parseExprEquality() throws UriParserException, UriValidationException {
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
  private Expression parseExprRel() throws UriParserException, UriValidationException {
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

  private Expression parseExprAdd() throws UriParserException, UriValidationException {
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

  private Expression parseExprMul() throws UriParserException, UriValidationException {
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
  private Expression parseExprUnary() throws UriParserException, UriValidationException {
    // Negative numbers start with a minus indistinguishable from an unary minus operator.
    // So we read numbers (and primitive values starting with numbers) right here.
    // TODO: Find a better idea how to solve this problem.
    final TokenKind numberTokenKind = ParserHelper.next(tokenizer,
        TokenKind.DoubleValue, TokenKind.DecimalValue, TokenKind.GuidValue,
        TokenKind.DateTimeOffsetValue, TokenKind.DateValue, TokenKind.TimeOfDayValue,
        TokenKind.IntegerValue);
    if (numberTokenKind != null) {
      final EdmPrimitiveTypeKind primitiveTypeKind = tokenToPrimitiveType.get(numberTokenKind);
      final EdmPrimitiveType type = primitiveTypeKind == null ?
          // Null handling
          null :
          odata.createPrimitiveTypeInstance(primitiveTypeKind);
      return new LiteralImpl(tokenizer.getText(), type);
    }
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

  private Expression parseExprPrimary() throws UriParserException, UriValidationException {
    final Expression left = parseExprValue();
    if (isEnumType(left) && tokenizer.next(TokenKind.HasOperator)) {
      ParserHelper.requireNext(tokenizer, TokenKind.EnumValue);
      final Expression right = createEnumExpression(tokenizer.getText());
      return new BinaryImpl(left, BinaryOperatorKind.HAS, right,
          odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean));
    } else {
      return left;
    }
  }

  private Expression parseExprValue() throws UriParserException, UriValidationException {
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
      return parseFirstMemberExpr(TokenKind.ROOT);
    }

    if (tokenizer.next(TokenKind.IT)) {
      return parseFirstMemberExpr(TokenKind.IT);
    }

    final TokenKind nextPrimitive = ParserHelper.nextPrimitiveValue(tokenizer);
    if (nextPrimitive != null) {
      final String primitiveValueLiteral = tokenizer.getText();
      if (nextPrimitive == TokenKind.EnumValue) {
        return createEnumExpression(primitiveValueLiteral);
      } else {
        final EdmPrimitiveTypeKind primitiveTypeKind = tokenToPrimitiveType.get(nextPrimitive);
        final EdmPrimitiveType type = primitiveTypeKind == null ?
            // Null handling
            null :
            odata.createPrimitiveTypeInstance(primitiveTypeKind);
        return new LiteralImpl(primitiveValueLiteral, type);
      }
    }

    // The method token text includes the opening parenthesis so that method calls can be recognized unambiguously.
    // OData identifiers have to be considered after that.
    final TokenKind nextMethod = nextMethod();
    if (nextMethod != null) {
      MethodKind methodKind = tokenToMethod.get(nextMethod);
      return new MethodImpl(methodKind, parseMethodParameters(methodKind));
    }

    if (tokenizer.next(TokenKind.QualifiedName)) {
      return parseFirstMemberExpr(TokenKind.QualifiedName);
    }

    if (tokenizer.next(TokenKind.ODataIdentifier)) {
      return parseFirstMemberExpr(TokenKind.ODataIdentifier);
    }

    throw new UriParserSyntaxException("Unexpected token.", UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  private List<Expression> parseMethodParameters(final MethodKind methodKind)
      throws UriParserException, UriValidationException {
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

  private Expression parseFirstMemberExpr(final TokenKind lastTokenKind)
      throws UriParserException, UriValidationException {

    final UriInfoImpl uriInfo = new UriInfoImpl();
    EdmType startTypeFilter = null;

    if (lastTokenKind == TokenKind.ROOT) {
      parseDollarRoot(uriInfo);
    } else if (lastTokenKind == TokenKind.IT) {
      parseDollarIt(uriInfo);
    } else if (lastTokenKind == TokenKind.ODataIdentifier) {
      parseFirstMemberODataIdentifier(uriInfo);
    } else if (lastTokenKind == TokenKind.QualifiedName) {
      // Special handling for leading type casts and type literals
      final FullQualifiedName fullQualifiedName = new FullQualifiedName(tokenizer.getText());
      EdmStructuredType structuredType = edm.getEntityType(fullQualifiedName);
      if (structuredType == null) {
        structuredType = edm.getComplexType(fullQualifiedName);
      }

      if (structuredType != null) {
        if (tokenizer.next(TokenKind.SLASH)) {
          // Leading type cast
          checkStructuredTypeFilter(referringType, structuredType);
          startTypeFilter = structuredType;

          final TokenKind tokenKind = ParserHelper.next(tokenizer, TokenKind.QualifiedName, TokenKind.ODataIdentifier);
          parseMemberExpression(tokenKind, uriInfo, new UriResourceStartingTypeFilterImpl(structuredType, false),
              false);
        } else {
          // Type literal
          checkStructuredTypeFilter(referringType, structuredType);
          return new TypeLiteralImpl(structuredType);
        }
      } else {
        // Must be bound or unbound function. // TODO: Is unbound function allowed?
        parseFunction(fullQualifiedName, uriInfo, referringType, true);
      }
    }

    return new MemberImpl(uriInfo, startTypeFilter);
  }

  private void parseDollarRoot(UriInfoImpl uriInfo) throws UriParserException, UriValidationException {
    UriResourceRootImpl rootResource = new UriResourceRootImpl(referringType, true);
    uriInfo.addResourcePart(rootResource);
    ParserHelper.requireNext(tokenizer, TokenKind.SLASH);
    ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
    final String name = tokenizer.getText();
    UriResourcePartTyped resource = null;
    final EdmEntitySet entitySet = edm.getEntityContainer().getEntitySet(name);
    if (entitySet == null) {
      final EdmSingleton singleton = edm.getEntityContainer().getSingleton(name);
      if (singleton == null) {
        throw new UriParserSemanticException("EntitySet or singleton expected.",
            UriParserSemanticException.MessageKeys.UNKNOWN_PART, name);
      } else {
        resource = new UriResourceSingletonImpl(singleton);
      }
    } else {
      ParserHelper.requireNext(tokenizer, TokenKind.OPEN);
      final List<UriParameter> keyPredicates =
          ParserHelper.parseKeyPredicate(tokenizer, entitySet.getEntityType(), null);
      resource = new UriResourceEntitySetImpl(entitySet).setKeyPredicates(keyPredicates);
    }
    uriInfo.addResourcePart(resource);
    parseSingleNavigationExpr(uriInfo, resource);
  }

  private void parseDollarIt(UriInfoImpl uriInfo) throws UriParserException, UriValidationException {
    UriResourceItImpl itResource = new UriResourceItImpl(referringType,
        referringType instanceof EdmEntityType); // TODO: Determine isCollection.
    uriInfo.addResourcePart(itResource);
    if (tokenizer.next(TokenKind.SLASH)) {
      final TokenKind tokenKind = ParserHelper.next(tokenizer, TokenKind.QualifiedName, TokenKind.ODataIdentifier);
      parseMemberExpression(tokenKind, uriInfo, itResource, true);
    }
  }

  private void parseFirstMemberODataIdentifier(UriInfoImpl uriInfo) throws UriParserException, UriValidationException {
    final String name = tokenizer.getText();

    // For a crossjoin, the identifier must be an entity-set name.
    if (crossjoinEntitySetNames != null && !crossjoinEntitySetNames.isEmpty()) {
      if (crossjoinEntitySetNames.contains(name)) {
        final UriResourceEntitySetImpl resource =
            new UriResourceEntitySetImpl(edm.getEntityContainer().getEntitySet(name));
        uriInfo.addResourcePart(resource);
        if (tokenizer.next(TokenKind.SLASH)) {
          final TokenKind tokenKind = ParserHelper.next(tokenizer, TokenKind.QualifiedName, TokenKind.ODataIdentifier);
          parseMemberExpression(tokenKind, uriInfo, resource, true);
        }
        return;
      } else {
        throw new UriParserSemanticException("Unknown crossjoin entity set.",
            UriParserSemanticException.MessageKeys.UNKNOWN_PART, name);
      }
    }

    // Check if the OData identifier is a lambda variable, otherwise it must be a property.
    UriResourceLambdaVariable lambdaVariable = null;
    for (final UriResourceLambdaVariable variable : lambdaVariables) {
      if (variable.getVariableName().equals(name)) {
        lambdaVariable = variable;
        break;
      }
    }
    if (lambdaVariable != null) {
      // Copy lambda variable into new resource, just in case ...
      final UriResourceLambdaVariable lambdaResource =
          new UriResourceLambdaVarImpl(lambdaVariable.getVariableName(), lambdaVariable.getType());
      uriInfo.addResourcePart(lambdaResource);
      if (tokenizer.next(TokenKind.SLASH)) {
        final TokenKind tokenKind = ParserHelper.next(tokenizer, TokenKind.QualifiedName, TokenKind.ODataIdentifier);
        parseMemberExpression(tokenKind, uriInfo, lambdaResource, true);
      }
    } else {
      // Must be a property.
      parseMemberExpression(TokenKind.ODataIdentifier, uriInfo, null, true); // TODO: Find last resource.
    }
  }

  private void parseMemberExpression(final TokenKind lastTokenKind, UriInfoImpl uriInfo,
      final UriResourcePartTyped lastResource, final boolean allowTypeFilter)
          throws UriParserException, UriValidationException {

    if (lastTokenKind == TokenKind.QualifiedName) {
      // Type cast or bound function
      final FullQualifiedName fullQualifiedName = new FullQualifiedName(tokenizer.getText());
      final EdmEntityType edmEntityType = edm.getEntityType(fullQualifiedName);

      if (edmEntityType != null) {
        if (allowTypeFilter) {
          setTypeFilter(lastResource, edmEntityType);
        } else {
          throw new UriParserSemanticException("Type filters are not chainable.",
              UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE,
              lastResource.getType().getFullQualifiedName().getFullQualifiedNameAsString(),
              fullQualifiedName.getFullQualifiedNameAsString());
        }
      } else {
        parseBoundFunction(fullQualifiedName, uriInfo, lastResource);
      }
    } else if (lastTokenKind == TokenKind.ODataIdentifier) {
      parsePropertyPathExpr(uriInfo, lastResource);
    } else {
      throw new UriParserSyntaxException("Unexpected token.", UriParserSyntaxException.MessageKeys.SYNTAX);
    }
  }

  private void setTypeFilter(UriResourcePartTyped lastResource, final EdmEntityType entityTypeFilter)
      throws UriParserException {
    checkStructuredTypeFilter(lastResource.getType(), entityTypeFilter);
    if (lastResource instanceof UriResourceTypedImpl) {
      ((UriResourceTypedImpl) lastResource).setTypeFilter(entityTypeFilter);
    } else if (lastResource instanceof UriResourceWithKeysImpl) {
      ((UriResourceWithKeysImpl) lastResource).setEntryTypeFilter(entityTypeFilter);
    }
  }

  private void parsePropertyPathExpr(UriInfoImpl uriInfo, final UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {

    final String oDataIdentifier = tokenizer.getText();

    final EdmType lastType = lastResource == null ? referringType : ParserHelper.getTypeInformation(lastResource);
    if (!(lastType instanceof EdmStructuredType)) {
      throw new UriParserSemanticException("Property paths must follow a structured type.",
          UriParserSemanticException.MessageKeys.ONLY_FOR_STRUCTURAL_TYPES, oDataIdentifier);
    }

    final EdmStructuredType structuredType = (EdmStructuredType) lastType;
    final EdmElement property = structuredType.getProperty(oDataIdentifier);

    if (property == null) {
      throw new UriParserSemanticException("Unknown property.",
          UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE, oDataIdentifier);
    }

    if (property.getType() instanceof EdmComplexType) {
      final UriResourceComplexPropertyImpl complexResource =
          new UriResourceComplexPropertyImpl((EdmProperty) property);
      uriInfo.addResourcePart(complexResource);

      if (property.isCollection()) {
        parseCollectionPathExpr(uriInfo, complexResource);
      } else {
        parseComplexPathExpr(uriInfo, complexResource);
      }
    } else if (property instanceof EdmNavigationProperty) {
      // Nav. property; maybe a collection
      final UriResourceNavigationPropertyImpl navigationResource =
          new UriResourceNavigationPropertyImpl((EdmNavigationProperty) property);
      navigationResource.setKeyPredicates(
          ParserHelper.parseNavigationKeyPredicate(tokenizer, (EdmNavigationProperty) property));
      uriInfo.addResourcePart(navigationResource);

      if (navigationResource.isCollection()) {
        parseCollectionNavigationExpr(uriInfo, navigationResource);
      } else {
        parseSingleNavigationExpr(uriInfo, navigationResource);
      }
    } else {
      // Primitive type or Enum type
      final UriResourcePrimitivePropertyImpl primitiveResource =
          new UriResourcePrimitivePropertyImpl((EdmProperty) property);
      uriInfo.addResourcePart(primitiveResource);

      if (property.isCollection()) {
        parseCollectionPathExpr(uriInfo, primitiveResource);
      } else {
        parseSinglePathExpr(uriInfo, primitiveResource);
      }
    }
  }

  private void parseSingleNavigationExpr(UriInfoImpl uriInfo, final UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {
    // TODO: Is that correct?
    if (tokenizer.next(TokenKind.SLASH)) {
      final TokenKind tokenKind = ParserHelper.next(tokenizer, TokenKind.QualifiedName, TokenKind.ODataIdentifier);
      parseMemberExpression(tokenKind, uriInfo, lastResource, true);
    }
  }

  private void parseCollectionNavigationExpr(UriInfoImpl uriInfo, UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {
    // TODO: Is type cast missing?
    if (tokenizer.next(TokenKind.OPEN)) {
      if (lastResource instanceof UriResourceNavigation) {
        ((UriResourceNavigationPropertyImpl) lastResource).setKeyPredicates(
              ParserHelper.parseNavigationKeyPredicate(tokenizer,
                  ((UriResourceNavigationPropertyImpl) lastResource).getProperty()));
      } else if (lastResource instanceof UriResourceFunction
          && ((UriResourceFunction) lastResource).getType() instanceof EdmEntityType) {
        ((UriResourceFunctionImpl) lastResource).setKeyPredicates(
            ParserHelper.parseKeyPredicate(tokenizer,
                (EdmEntityType) ((UriResourceFunction) lastResource).getType(),
                null));
      } else {
        throw new UriParserSemanticException("Unknown or wrong resource type.",
            UriParserSemanticException.MessageKeys.NOT_IMPLEMENTED, lastResource.toString());
      }
      parseSingleNavigationExpr(uriInfo, lastResource);
    }
    parseCollectionPathExpr(uriInfo, lastResource);
  }

  private void parseSinglePathExpr(UriInfoImpl uriInfo, final UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {
    if (tokenizer.next(TokenKind.SLASH)) {
      ParserHelper.requireNext(tokenizer, TokenKind.QualifiedName);
      parseBoundFunction(new FullQualifiedName(tokenizer.getText()), uriInfo, lastResource);
    }
  }

  private void parseComplexPathExpr(UriInfoImpl uriInfo, final UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {

    if (tokenizer.next(TokenKind.SLASH)) {
      if (tokenizer.next(TokenKind.QualifiedName)) {
        final FullQualifiedName fullQualifiedName = new FullQualifiedName(tokenizer.getText());
        final EdmEntityType edmEntityType = edm.getEntityType(fullQualifiedName);

        if (edmEntityType != null) {
          setTypeFilter(lastResource, edmEntityType);
          if (tokenizer.next(TokenKind.SLASH)) {
            parseComplexPathRestExpr(uriInfo, lastResource);
          }
        } else {
          // Must be a bound function.
          parseBoundFunction(fullQualifiedName, uriInfo, lastResource);
        }
      } else {
        parseComplexPathRestExpr(uriInfo, lastResource);
      }
    }
  }

  private void parseComplexPathRestExpr(UriInfoImpl uriInfo, final UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {
    if (tokenizer.next(TokenKind.QualifiedName)) {
      final FullQualifiedName fullQualifiedName = new FullQualifiedName(tokenizer.getText());
      // Must be a bound function.
      parseBoundFunction(fullQualifiedName, uriInfo, lastResource);
    } else if (tokenizer.next(TokenKind.ODataIdentifier)) {
      parsePropertyPathExpr(uriInfo, lastResource);
    } else {
      throw new UriParserSyntaxException("Unexpected token.", UriParserSyntaxException.MessageKeys.SYNTAX);
    }
  }

  private void parseCollectionPathExpr(UriInfoImpl uriInfo, final UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {

    if (tokenizer.next(TokenKind.SLASH)) {
      if (tokenizer.next(TokenKind.COUNT)) {
        uriInfo.addResourcePart(new UriResourceCountImpl());
      } else if (tokenizer.next(TokenKind.ANY)) {
        uriInfo.addResourcePart(parseLambdaRest(TokenKind.ANY, lastResource));
      } else if (tokenizer.next(TokenKind.ALL)) {
        uriInfo.addResourcePart(parseLambdaRest(TokenKind.ALL, lastResource));
      } else if (tokenizer.next(TokenKind.QualifiedName)) {
        final FullQualifiedName fullQualifiedName = new FullQualifiedName(tokenizer.getText());
        parseBoundFunction(fullQualifiedName, uriInfo, lastResource);
      }
    }
  }

  private void parseFunction(final FullQualifiedName fullQualifiedName, UriInfoImpl uriInfo,
      final EdmType lastType, final boolean lastIsCollection) throws UriParserException, UriValidationException {

    final List<UriParameter> parameters = ParserHelper.parseFunctionParameters(tokenizer, true);
    final List<String> parameterNames = ParserHelper.getParameterNames(parameters);
    final EdmFunction boundFunction = edm.getBoundFunction(fullQualifiedName,
        lastType.getFullQualifiedName(), lastIsCollection, parameterNames);

    if (boundFunction != null) {
      parseFunctionRest(uriInfo, boundFunction, parameters);
      return;
    }

    final EdmFunction unboundFunction = edm.getUnboundFunction(fullQualifiedName, parameterNames);
    if (unboundFunction != null) {
      parseFunctionRest(uriInfo, unboundFunction, parameters);
      return;
    }

    throw new UriParserSemanticException("No function found.",
        UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND, fullQualifiedName.getFullQualifiedNameAsString());
  }

  private void parseBoundFunction(final FullQualifiedName fullQualifiedName, UriInfoImpl uriInfo,
      final UriResourcePartTyped lastResource) throws UriParserException, UriValidationException {
    final List<UriParameter> parameters = ParserHelper.parseFunctionParameters(tokenizer, true);
    final List<String> parameterNames = ParserHelper.getParameterNames(parameters);
    final EdmFunction boundFunction = edm.getBoundFunction(fullQualifiedName,
        lastResource.getType().getFullQualifiedName(), lastResource.isCollection(), parameterNames);
    if (boundFunction == null) {
      throw new UriParserSemanticException("Bound function not found.",
          UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND, fullQualifiedName.getFullQualifiedNameAsString());
    }
    parseFunctionRest(uriInfo, boundFunction, parameters);
  }

  private void parseFunctionRest(UriInfoImpl uriInfo, final EdmFunction function,
      final List<UriParameter> parameters) throws UriParserException, UriValidationException {
    final UriResourceFunction functionResource = new UriResourceFunctionImpl(null, function, parameters);
    uriInfo.addResourcePart(functionResource);

    final EdmReturnType edmReturnType = function.getReturnType();
    final EdmType edmType = edmReturnType.getType();
    final boolean isCollection = edmReturnType.isCollection();

    if (function.isComposable()) {
      if (edmType instanceof EdmEntityType ) {
        if (isCollection) {
          parseCollectionNavigationExpr(uriInfo, null); // TODO: Get navigation property.
        } else {
          parseSingleNavigationExpr(uriInfo, null); // TODO: Get navigation property.
        }
      } else if (edmType instanceof EdmComplexType) {
        if (isCollection) {
          parseCollectionPathExpr(uriInfo, functionResource);
        } else {
          parseComplexPathExpr(uriInfo, functionResource);
        }
      } else if (edmType instanceof EdmPrimitiveType) {
        if (isCollection) {
          parseCollectionPathExpr(uriInfo, functionResource);
        } else {
          parseSinglePathExpr(uriInfo, functionResource);
        }
      }
    } else if (tokenizer.next(TokenKind.SLASH)) {
      throw new UriValidationException("Function is not composable.",
          UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH, "");
    }
  }

  private UriResourcePartTyped parseLambdaRest(final TokenKind lastTokenKind, final UriResourcePartTyped lastResource)
      throws UriParserException, UriValidationException {

    ParserHelper.requireNext(tokenizer, TokenKind.OPEN);
    if (lastTokenKind == TokenKind.ANY && tokenizer.next(TokenKind.CLOSE)) {
      return new UriResourceLambdaAnyImpl(null, null);
    }
    ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
    final String lambbdaVariable = tokenizer.getText();
    ParserHelper.requireNext(tokenizer, TokenKind.COLON);
    lambdaVariables.addFirst(new UriResourceLambdaVarImpl(lambbdaVariable,
        lastResource == null ? referringType : lastResource.getType()));
    final Expression lambdaPredicateExpr = parseExpression();
    lambdaVariables.removeFirst();
    // TODO: The ABNF suggests that the "lambaPredicateExpr" must contain at least one lambdaVariable.
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    if (lastTokenKind == TokenKind.ALL) {
      return new UriResourceLambdaAllImpl(lambbdaVariable, lambdaPredicateExpr);
    } else if (lastTokenKind == TokenKind.ANY) {
      return new UriResourceLambdaAnyImpl(lambbdaVariable, lambdaPredicateExpr);
    } else {
      throw new UriParserSyntaxException("Unexpected token.", UriParserSyntaxException.MessageKeys.SYNTAX);
    }
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

  private EdmEnumType getEnumType(final String primitiveValueLiteral) throws UriParserException {
    final String enumTypeName = primitiveValueLiteral.substring(0, primitiveValueLiteral.indexOf('\''));
    final EdmEnumType type = edm.getEnumType(new FullQualifiedName(enumTypeName));
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

  private Enumeration createEnumExpression(final String primitiveValueLiteral) throws UriParserException {
    final EdmEnumType enumType = getEnumType(primitiveValueLiteral);
    // TODO: Can the Enumeration interface be changed to handle the value as a whole?
    try {
      return new EnumerationImpl(enumType,
          Arrays.asList(enumType.fromUriLiteral(primitiveValueLiteral).split(",")));
    } catch (final EdmPrimitiveTypeException e) {
      // TODO: Better error message.
      throw new UriParserSemanticException("Wrong enumeration value.", e,
          UriParserSemanticException.MessageKeys.UNKNOWN_PART, primitiveValueLiteral);
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

  private void checkStructuredTypeFilter(final EdmType type, final EdmStructuredType filterType)
      throws UriParserException {
    if (!filterType.compatibleTo(type)) {
      throw new UriParserSemanticException("Incompatible type filter.",
          UriParserSemanticException.MessageKeys.INCOMPATIBLE_TYPE_FILTER,
          filterType.getFullQualifiedName().getFullQualifiedNameAsString());
    }
  }
}
