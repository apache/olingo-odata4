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

/**
 * <p>Simple OData URI tokenizer that works on a given string by keeping an index.</p>
 * <p>As far as feasible, it tries to work on character basis, assuming this to be faster than string operations.
 * Since only the index is "moved", backing out while parsing a token is easy and used throughout.
 * There is intentionally no method to push back tokens (although it would be easy to add such a method)
 * because this tokenizer should behave like a classical token-consuming tokenizer.
 * There is, however, the possibility to save the current state and return to it later.</p>
 * <p>Whitespace is not an extra token but consumed with the tokens that require whitespace.
 * Optional whitespace is not supported.</p>
 */
public class UriTokenizer {

  public enum TokenKind {
    EOF, // signals the end of the string to be parsed

    // constant-value tokens (convention: uppercase)
    REF,
    VALUE,
    COUNT,
    CROSSJOIN,
    ROOT,
    IT,

    APPLY, // for the aggregation extension
    EXPAND,
    FILTER,
    LEVELS,
    ORDERBY,
    SEARCH,
    SELECT,
    SKIP,
    TOP,

    ANY,
    ALL,

    OPEN,
    CLOSE,
    COMMA,
    SEMI,
    COLON,
    DOT,
    SLASH,
    EQ,
    STAR,
    PLUS,

    NULL,
    MAX,

    AVERAGE, // for the aggregation extension
    COUNTDISTINCT, // for the aggregation extension
    IDENTITY, // for the aggregation extension
    MIN, // for the aggregation extension
    SUM, // for the aggregation extension
    ROLLUP_ALL, // for the aggregation extension

    // variable-value tokens (convention: mixed case)
    ODataIdentifier,
    QualifiedName,
    ParameterAliasName,

    BooleanValue,
    StringValue,
    IntegerValue,
    GuidValue,
    DateValue,
    DateTimeOffsetValue,
    TimeOfDayValue,
    DecimalValue,
    DoubleValue,
    DurationValue,
    BinaryValue,
    EnumValue,

    GeographyPoint,
    GeometryPoint,
    GeographyLineString,
    GeometryLineString,
    GeographyPolygon,
    GeometryPolygon,
    GeographyMultiPoint,
    GeometryMultiPoint,
    GeographyMultiLineString,
    GeometryMultiLineString,
    GeographyMultiPolygon,
    GeometryMultiPolygon,
    GeographyCollection,
    GeometryCollection,

    jsonArrayOrObject,

    Word,
    Phrase,

    OrOperatorSearch,
    AndOperatorSearch,
    NotOperatorSearch,

    OrOperator,
    AndOperator,
    EqualsOperator,
    NotEqualsOperator,
    GreaterThanOperator,
    GreaterThanOrEqualsOperator,
    LessThanOperator,
    LessThanOrEqualsOperator,
    HasOperator,
    AddOperator,
    SubOperator,
    MulOperator,
    DivOperator,
    ModOperator,
    MinusOperator,
    NotOperator,

    AsOperator, // for the aggregation extension
    FromOperator, // for the aggregation extension
    WithOperator, // for the aggregation extension

    CastMethod,
    CeilingMethod,
    ConcatMethod,
    ContainsMethod,
    DateMethod,
    DayMethod,
    EndswithMethod,
    FloorMethod,
    FractionalsecondsMethod,
    GeoDistanceMethod,
    GeoIntersectsMethod,
    GeoLengthMethod,
    HourMethod,
    IndexofMethod,
    IsofMethod,
    LengthMethod,
    MaxdatetimeMethod,
    MindatetimeMethod,
    MinuteMethod,
    MonthMethod,
    NowMethod,
    RoundMethod,
    SecondMethod,
    StartswithMethod,
    SubstringMethod,
    TimeMethod,
    TolowerMethod,
    TotaloffsetminutesMethod,
    TotalsecondsMethod,
    ToupperMethod,
    TrimMethod,
    YearMethod,

    IsDefinedMethod, // for the aggregation extension

    AggregateTrafo, // for the aggregation extension
    BottomCountTrafo, // for the aggregation extension
    BottomPercentTrafo, // for the aggregation extension
    BottomSumTrafo, // for the aggregation extension
    ComputeTrafo, // for the aggregation extension
    ExpandTrafo, // for the aggregation extension
    FilterTrafo, // for the aggregation extension
    GroupByTrafo, // for the aggregation extension
    SearchTrafo, // for the aggregation extension
    TopCountTrafo, // for the aggregation extension
    TopPercentTrafo, // for the aggregation extension
    TopSumTrafo, // for the aggregation extension

    RollUpSpec, // for the aggregation extension

    AscSuffix,
    DescSuffix
  }

  private final String parseString;

  private int startIndex = 0;
  private int index = 0;

  private int savedStartIndex;
  private int savedIndex;

  public UriTokenizer(final String parseString) {
    this.parseString = parseString == null ? "" : parseString;
  }

  /**
   * Save the current state.
   * @see #returnToSavedState()
   */
  public void saveState() {
    savedStartIndex = startIndex;
    savedIndex = index;
  }

  /**
   * Return to the previously saved state.
   * @see #saveState()
   */
  public void returnToSavedState() {
    startIndex = savedStartIndex;
    index = savedIndex;
  }

  /** Returns the string value corresponding to the last successful {@link #next(TokenKind)} call. */
  public String getText() {
    return parseString.substring(startIndex, index);
  }

  /**
   * Tries to find a token of the given token kind at the current index.
   * The order in which this method is called with different token kinds is important,
   * not only for performance reasons but also if tokens can start with the same characters
   * (e.g., a qualified name starts with an OData identifier).
   * The index is advanced to the end of this token if the token is found.
   * @param allowedTokenKind the kind of token to expect
   * @return <code>true</code> if the token is found; <code>false</code> otherwise
   * @see #getText()
   */
  public boolean next(final TokenKind allowedTokenKind) {
    if (allowedTokenKind == null) {
      return false;
    }

    boolean found = false;
    final int previousIndex = index;
    switch (allowedTokenKind) {
    case EOF:
      found = index >= parseString.length();
      break;

    // Constants
    case REF:
      found = nextConstant("$ref");
      break;
    case VALUE:
      found = nextConstant("$value");
      break;
    case COUNT:
      found = nextConstant("$count");
      break;
    case CROSSJOIN:
      found = nextConstant("$crossjoin");
      break;
    case ROOT:
      found = nextConstant("$root");
      break;
    case IT:
      found = nextConstant("$it");
      break;

    case APPLY:
      found = nextConstant("$apply");
      break;
    case EXPAND:
      found = nextConstant("$expand");
      break;
    case FILTER:
      found = nextConstant("$filter");
      break;
    case LEVELS:
      found = nextConstant("$levels");
      break;
    case ORDERBY:
      found = nextConstant("$orderby");
      break;
    case SEARCH:
      found = nextConstant("$search");
      break;
    case SELECT:
      found = nextConstant("$select");
      break;
    case SKIP:
      found = nextConstant("$skip");
      break;
    case TOP:
      found = nextConstant("$top");
      break;

    case ANY:
      found = nextConstant("any");
      break;
    case ALL:
      found = nextConstant("all");
      break;

    case OPEN:
      found = nextCharacter('(');
      break;
    case CLOSE:
      found = nextCharacter(')');
      break;
    case COMMA:
      found = nextCharacter(',');
      break;
    case SEMI:
      found = nextCharacter(';');
      break;
    case COLON:
      found = nextCharacter(':');
      break;
    case DOT:
      found = nextCharacter('.');
      break;
    case SLASH:
      found = nextCharacter('/');
      break;
    case EQ:
      found = nextCharacter('=');
      break;
    case STAR:
      found = nextCharacter('*');
      break;
    case PLUS:
      found = nextCharacter('+');
      break;

    case NULL:
      found = nextConstant("null");
      break;
    case MAX:
      found = nextConstant("max");
      break;

    case AVERAGE:
      found = nextConstant("average");
      break;
    case COUNTDISTINCT:
      found = nextConstant("countdistinct");
      break;
    case IDENTITY:
      found = nextConstant("identity");
      break;
    case MIN:
      found = nextConstant("min");
      break;
    case SUM:
      found = nextConstant("sum");
      break;

    case ROLLUP_ALL:
      found = nextConstant("$all");
      break;

    // Identifiers
    case ODataIdentifier:
      found = nextODataIdentifier();
      break;
    case QualifiedName:
      found = nextQualifiedName();
      break;
    case ParameterAliasName:
      found = nextParameterAliasName();
      break;

    // Primitive Values
    case BooleanValue:
      found = nextBooleanValue();
      break;
    case StringValue:
      found = nextStringValue();
      break;
    case IntegerValue:
      found = nextIntegerValue(true);
      break;
    case GuidValue:
      found = nextGuidValue();
      break;
    case DateValue:
      found = nextDateValue();
      break;
    case DateTimeOffsetValue:
      found = nextDateTimeOffsetValue();
      break;
    case TimeOfDayValue:
      found = nextTimeOfDayValue();
      break;
    case DecimalValue:
      found = nextDecimalValue();
      break;
    case DoubleValue:
      found = nextDoubleValue();
      break;
    case DurationValue:
      found = nextDurationValue();
      break;
    case BinaryValue:
      found = nextBinaryValue();
      break;
    case EnumValue:
      found = nextEnumValue();
      break;

    // Geo Values
    case GeographyPoint:
      found = nextGeoPoint(true);
      break;
    case GeometryPoint:
      found = nextGeoPoint(false);
      break;
    case GeographyLineString:
      found = nextGeoLineString(true);
      break;
    case GeometryLineString:
      found = nextGeoLineString(false);
      break;
    case GeographyPolygon:
      found = nextGeoPolygon(true);
      break;
    case GeometryPolygon:
      found = nextGeoPolygon(false);
      break;
    case GeographyMultiPoint:
      found = nextGeoMultiPoint(true);
      break;
    case GeometryMultiPoint:
      found = nextGeoMultiPoint(false);
      break;
    case GeographyMultiLineString:
      found = nextGeoMultiLineString(true);
      break;
    case GeometryMultiLineString:
      found = nextGeoMultiLineString(false);
      break;
    case GeographyMultiPolygon:
      found = nextGeoMultiPolygon(true);
      break;
    case GeometryMultiPolygon:
      found = nextGeoMultiPolygon(false);
      break;
    case GeographyCollection:
      found = nextGeoCollection(true);
      break;
    case GeometryCollection:
      found = nextGeoCollection(false);
      break;

    // Complex or Collection Value
    case jsonArrayOrObject:
      found = nextJsonArrayOrObject();
      break;

    // Search
    case Word:
      found = nextWord();
      break;
    case Phrase:
      found = nextPhrase();
      break;

    // Operators in Search Expressions
    case OrOperatorSearch:
      found = nextBinaryOperator("OR");
      break;
    case AndOperatorSearch:
      found = nextAndOperatorSearch();
      break;
    case NotOperatorSearch:
      found = nextUnaryOperator("NOT");
      break;

    // Operators
    case OrOperator:
      found = nextBinaryOperator("or");
      break;
    case AndOperator:
      found = nextBinaryOperator("and");
      break;
    case EqualsOperator:
      found = nextBinaryOperator("eq");
      break;
    case NotEqualsOperator:
      found = nextBinaryOperator("ne");
      break;
    case GreaterThanOperator:
      found = nextBinaryOperator("gt");
      break;
    case GreaterThanOrEqualsOperator:
      found = nextBinaryOperator("ge");
      break;
    case LessThanOperator:
      found = nextBinaryOperator("lt");
      break;
    case LessThanOrEqualsOperator:
      found = nextBinaryOperator("le");
      break;
    case HasOperator:
      found = nextBinaryOperator("has");
      break;
    case AddOperator:
      found = nextBinaryOperator("add");
      break;
    case SubOperator:
      found = nextBinaryOperator("sub");
      break;
    case MulOperator:
      found = nextBinaryOperator("mul");
      break;
    case DivOperator:
      found = nextBinaryOperator("div");
      break;
    case ModOperator:
      found = nextBinaryOperator("mod");
      break;
    case MinusOperator:
      // To avoid unnecessary minus operators for negative numbers, we have to check what follows the minus sign.
      found = nextCharacter('-') && !nextDigit() && !nextConstant("INF");
      break;
    case NotOperator:
      found = nextUnaryOperator("not");
      break;

    // Operators for the aggregation extension
    case AsOperator:
      found = nextBinaryOperator("as");
      break;
    case FromOperator:
      found = nextBinaryOperator("from");
      break;
    case WithOperator:
      found = nextBinaryOperator("with");
      break;

    // Methods
    case CastMethod:
      found = nextMethod("cast");
      break;
    case CeilingMethod:
      found = nextMethod("ceiling");
      break;
    case ConcatMethod:
      found = nextMethod("concat");
      break;
    case ContainsMethod:
      found = nextMethod("contains");
      break;
    case DateMethod:
      found = nextMethod("date");
      break;
    case DayMethod:
      found = nextMethod("day");
      break;
    case EndswithMethod:
      found = nextMethod("endswith");
      break;
    case FloorMethod:
      found = nextMethod("floor");
      break;
    case FractionalsecondsMethod:
      found = nextMethod("fractionalseconds");
      break;
    case GeoDistanceMethod:
      found = nextMethod("geo.distance");
      break;
    case GeoIntersectsMethod:
      found = nextMethod("geo.intersects");
      break;
    case GeoLengthMethod:
      found = nextMethod("geo.length");
      break;
    case HourMethod:
      found = nextMethod("hour");
      break;
    case IndexofMethod:
      found = nextMethod("indexof");
      break;
    case IsofMethod:
      found = nextMethod("isof");
      break;
    case LengthMethod:
      found = nextMethod("length");
      break;
    case MaxdatetimeMethod:
      found = nextMethod("maxdatetime");
      break;
    case MindatetimeMethod:
      found = nextMethod("mindatetime");
      break;
    case MinuteMethod:
      found = nextMethod("minute");
      break;
    case MonthMethod:
      found = nextMethod("month");
      break;
    case NowMethod:
      found = nextMethod("now");
      break;
    case RoundMethod:
      found = nextMethod("round");
      break;
    case SecondMethod:
      found = nextMethod("second");
      break;
    case StartswithMethod:
      found = nextMethod("startswith");
      break;
    case SubstringMethod:
      found = nextMethod("substring");
      break;
    case TimeMethod:
      found = nextMethod("time");
      break;
    case TolowerMethod:
      found = nextMethod("tolower");
      break;
    case TotaloffsetminutesMethod:
      found = nextMethod("totaloffsetminutes");
      break;
    case TotalsecondsMethod:
      found = nextMethod("totalseconds");
      break;
    case ToupperMethod:
      found = nextMethod("toupper");
      break;
    case TrimMethod:
      found = nextMethod("trim");
      break;
    case YearMethod:
      found = nextMethod("year");
      break;

    // Method for the aggregation extension
    case IsDefinedMethod:
      found = nextMethod("isdefined");
      break;

    // Transformations for the aggregation extension
    case AggregateTrafo:
      found = nextMethod("aggregate");
      break;
    case BottomCountTrafo:
      found = nextMethod("bottomcount");
      break;
    case BottomPercentTrafo:
      found = nextMethod("bottompercent");
      break;
    case BottomSumTrafo:
      found = nextMethod("bottomsum");
      break;
    case ComputeTrafo:
      found = nextMethod("compute");
      break;
    case ExpandTrafo:
      found = nextMethod("expand");
      break;
    case FilterTrafo:
      found = nextMethod("filter");
      break;
    case GroupByTrafo:
      found = nextMethod("groupby");
      break;
    case SearchTrafo:
      found = nextMethod("search");
      break;
    case TopCountTrafo:
      found = nextMethod("topcount");
      break;
    case TopPercentTrafo:
      found = nextMethod("toppercent");
      break;
    case TopSumTrafo:
      found = nextMethod("topsum");
      break;

    // Roll-up specification for the aggregation extension
    case RollUpSpec:
      found = nextMethod("rollup");
      break;

    // Suffixes
    case AscSuffix:
      found = nextSuffix("asc");
      break;
    case DescSuffix:
      found = nextSuffix("desc");
      break;
    }

    if (found) {
      startIndex = previousIndex;
    } else {
      index = previousIndex;
    }
    return found;
  }

  /**
   * Moves past the given string constant if found; otherwise leaves the index unchanged.
   * @return whether the constant has been found at the current index
   */
  private boolean nextConstant(final String constant) {
    if (parseString.startsWith(constant, index)) {
      index += constant.length();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Moves past the given string constant, ignoring case, if found; otherwise leaves the index unchanged.
   * @return whether the constant has been found at the current index
   */
  private boolean nextConstantIgnoreCase(final String constant) {
    final int length = constant.length();
    if (index + length <= parseString.length()
        && constant.equalsIgnoreCase(parseString.substring(index, index + length))) {
      index += length;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Moves past the given character if found; otherwise leaves the index unchanged.
   * @return whether the given character has been found at the current index
   */
  private boolean nextCharacter(final char character) {
    if (index < parseString.length() && parseString.charAt(index) == character) {
      index++;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Moves past the next character if it is in the given character range;
   * otherwise leaves the index unchanged.
   * @return whether the given character has been found at the current index
   */
  private boolean nextCharacterRange(final char from, final char to) {
    if (index < parseString.length()) {
      final char code = parseString.charAt(index);
      if (code >= from && code <= to) {
        index++;
        return true;
      }
    }
    return false;
  }

  /**
   * Moves past a digit character ('0' to '9') if found; otherwise leaves the index unchanged.
   * @return whether a digit character has been found at the current index
   */
  private boolean nextDigit() {
    return nextCharacterRange('0', '9');
  }

  /**
   * Moves past a hexadecimal digit character ('0' to '9', 'A' to 'F', or 'a' to 'f') if found;
   * otherwise leaves the index unchanged.
   * @return whether a hexadecimal digit character has been found at the current index
   */
  private boolean nextHexDigit() {
    return nextCharacterRange('0', '9') || nextCharacterRange('A', 'F') || nextCharacterRange('a', 'f');
  }

  /**
   * Moves past a base64 character ('0' to '9', 'A' to 'Z', 'a' to 'z', '-', or '_') if found;
   * otherwise leaves the index unchanged.
   * @return whether a base64 character has been found at the current index
   */
  private boolean nextBase64() {
    return nextCharacterRange('0', '9') || nextCharacterRange('A', 'Z') || nextCharacterRange('a', 'z')
        || nextCharacter('-') || nextCharacter('_');
  }

  /**
   * Moves past a sign character ('+' or '-') if found; otherwise leaves the index unchanged.
   * @return whether a sign character has been found at the current index
   */
  private boolean nextSign() {
    return nextCharacter('+') || nextCharacter('-');
  }

  /**
   * Moves past whitespace (space or horizontal tabulator) characters if found;
   * otherwise leaves the index unchanged.
   * @return whether whitespace characters have been found at the current index
   */
  boolean nextWhitespace() {
    int count = 0;
    while (nextCharacter(' ') || nextCharacter('\t')) {
      count++;
    }
    return count > 0;
  }
  /**
   * Moves past an OData identifier if found; otherwise leaves the index unchanged.
   * @return whether an OData identifier has been found at the current index
   */
  private boolean nextODataIdentifier() {
    int count = 0;
    if (index < parseString.length()) {
      int code = parseString.codePointAt(index);
      if (Character.isUnicodeIdentifierStart(code) || code == '_') {
        count++;
        // Unicode characters outside of the Basic Multilingual Plane are represented as two Java characters.
        index += Character.isSupplementaryCodePoint(code) ? 2 : 1;
        while (index < parseString.length() && count < 128) {
          code = parseString.codePointAt(index);
          if (Character.isUnicodeIdentifierPart(code) && !Character.isISOControl(code)) {
            count++;
            // Unicode characters outside of the Basic Multilingual Plane are represented as two Java characters.
            index += Character.isSupplementaryCodePoint(code) ? 2 : 1;
          } else {
            break;
          }
        }
      }
    }
    return count > 0;
  }

  /**
   * Moves past a qualified name if found; otherwise leaves the index unchanged.
   * @return whether a qualified name has been found at the current index
   */
  private boolean nextQualifiedName() {
    final int lastGoodIndex = index;
    if (!nextODataIdentifier()) {
      return false;
    }
    int count = 1;
    while (nextCharacter('.')) {
      if (nextODataIdentifier()) {
        count++;
      } else {
        index--;
        break;
      }
    }
    if (count >= 2) {
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  /**
   * Moves past the given whitespace-surrounded operator constant if found.
   * @return whether the operator has been found at the current index
   */
  private boolean nextBinaryOperator(final String operator) {
    return nextWhitespace() && nextConstant(operator) && nextWhitespace();
  }

  /**
   * Moves past the given whitespace-suffixed operator constant if found.
   * @return whether the operator has been found at the current index
   */
  private boolean nextUnaryOperator(final String operator) {
    return nextConstant(operator) && nextWhitespace();
  }

  /**
   * Moves past the given method name and its immediately following opening parenthesis if found.
   * @return whether the method has been found at the current index
   */
  private boolean nextMethod(final String methodName) {
    return nextConstant(methodName) && nextCharacter('(');
  }

  /**
   * Moves past (required) whitespace and the given suffix name if found.
   * @return whether the suffix has been found at the current index
   */
  private boolean nextSuffix(final String suffixName) {
    return nextWhitespace() && nextConstant(suffixName);
  }

  private boolean nextParameterAliasName() {
    return nextCharacter('@') && nextODataIdentifier();
  }

  private boolean nextBooleanValue() {
    return nextConstantIgnoreCase("true") || nextConstantIgnoreCase("false");
  }

  private boolean nextStringValue() {
    if (!nextCharacter('\'')) {
      return false;
    }
    while (index < parseString.length()) {
      if (parseString.charAt(index) == '\'') {
        // If a single quote is followed by another single quote,
        // it represents one single quote within the string literal,
        // otherwise it marks the end of the string literal.
        if (index + 1 < parseString.length() && parseString.charAt(index + 1) == '\'') {
          index++;
        } else {
          break;
        }
      }
      index++;
    }
    return nextCharacter('\'');
  }

  /**
   * Moves past an integer value if found; otherwise leaves the index unchanged.
   * @param signed whether a sign character ('+' or '-') at the beginning is allowed
   * @return whether an integer value has been found at the current index
   */
  private boolean nextIntegerValue(final boolean signed) {
    final int lastGoodIndex = index;
    if (signed) {
      nextSign();
    }
    boolean hasDigits = false;
    while (nextDigit()) {
      hasDigits = true;
    }
    if (hasDigits) {
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  /**
   * Moves past a decimal value with a fractional part if found; otherwise leaves the index unchanged.
   * Whole numbers must be found with {@link #nextIntegerValue()}.
   */
  private boolean nextDecimalValue() {
    final int lastGoodIndex = index;
    if (nextIntegerValue(true) && nextCharacter('.') && nextIntegerValue(false)) {
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  /**
   * Moves past a floating-point-number value with an exponential part
   * or one of the special constants "NaN", "-INF", and "INF"
   * if found; otherwise leaves the index unchanged.
   * Whole numbers must be found with {@link #nextIntegerValue()}.
   * Decimal numbers must be found with {@link #nextDecimalValue()}.
   */
  private boolean nextDoubleValue() {
    if (nextConstant("NaN") || nextConstant("-INF") || nextConstant("INF")) {
      return true;
    } else {
      final int lastGoodIndex = index;
      if (!nextIntegerValue(true)) {
        return false;
      }
      if (nextCharacter('.') && !nextIntegerValue(false)) {
        index = lastGoodIndex;
        return false;
      }
      if ((nextCharacter('E') || nextCharacter('e')) && nextIntegerValue(true)) {
        return true;
      } else {
        index = lastGoodIndex;
        return false;
      }
    }
  }

  private boolean nextGuidValue() {
    return nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit()
        && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit()
        && nextCharacter('-')
        && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit()
        && nextCharacter('-')
        && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit()
        && nextCharacter('-')
        && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit()
        && nextCharacter('-')
        && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit()
        && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit()
        && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit();
  }

  private boolean nextYear() {
    nextCharacter('-');
    if (nextCharacter('0')) {
      return nextDigit() && nextDigit() && nextDigit();
    } else if (nextCharacterRange('1', '9')) {
      int count = 0;
      while (nextDigit()) {
        count++;
      }
      return count >= 3;
    } else {
      return false;
    }
  }

  private boolean nextDateValue() {
    return nextYear()
        && nextCharacter('-')
        && (nextCharacter('0') && nextCharacterRange('1', '9')
        || nextCharacter('1') && nextCharacterRange('0', '2'))
        && nextCharacter('-')
        && (nextCharacter('0') && nextCharacterRange('1', '9')
            || nextCharacterRange('1', '2') && nextDigit()
            || nextCharacter('3') && nextCharacterRange('0', '1'));
  }

  private boolean nextHours() {
    return nextCharacterRange('0', '1') && nextDigit()
        || nextCharacter('2') && nextCharacterRange('0', '3');
  }

  private boolean nextMinutesOrSeconds() {
    return nextCharacterRange('0', '5') && nextDigit();
  }

  private boolean nextDateTimeOffsetValue() {
    return nextDateValue()
        && (nextCharacter('T') || nextCharacter('t'))
        && nextTimeOfDayValue()
        && (nextCharacter('Z')
            || nextCharacter('z')
            || nextSign() && nextHours() && nextCharacter(':') && nextMinutesOrSeconds());
  }

  private boolean nextTimeOfDayValue() {
    if (nextHours() && nextCharacter(':') && nextMinutesOrSeconds()) {
      if (nextCharacter(':')) {
        if (nextMinutesOrSeconds()) {
          if (nextCharacter('.') && !nextIntegerValue(false)) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  private boolean nextDurationValue() {
    if (nextConstantIgnoreCase("duration") && nextCharacter('\'')) {
      nextSign();
      if (nextCharacter('P') || nextCharacter('p')) {
        if (nextIntegerValue(false) && (!(nextCharacter('D') || nextCharacter('d')))) {
          return false;
        }
        if (nextCharacter('T') || nextCharacter('t')) {
          boolean hasNumber = false;
          if (nextIntegerValue(false)) {
            hasNumber = true;
            if (nextCharacter('H') || nextCharacter('h')) {
              hasNumber = false;
            }
          }
          if (hasNumber || nextIntegerValue(false)) {
            hasNumber = true;
            if (nextCharacter('M') || nextCharacter('m')) {
              hasNumber = false;
            }
          }
          if (hasNumber || nextIntegerValue(false)) {
            if (nextCharacter('.') && !nextIntegerValue(false)) {
              return false;
            }
            if (!(nextCharacter('S') || nextCharacter('s'))) {
              return false;
            }
          }
        }
        return nextCharacter('\'');
      }
    }
    return false;
  }

  private boolean nextBinaryValue() {
    if (nextConstantIgnoreCase("binary") && nextCharacter('\'')) {
      int lastGoodIndex = index;
      while (nextBase64() && nextBase64() && nextBase64() && nextBase64()) {
        lastGoodIndex += 4;
      }
      index = lastGoodIndex;
      if (nextBase64() && nextBase64()
          && (nextCharacter('A') || nextCharacter('E') || nextCharacter('I') || nextCharacter('M')
              || nextCharacter('Q') || nextCharacter('U') || nextCharacter('Y') || nextCharacter('c')
              || nextCharacter('g') || nextCharacter('k') || nextCharacter('o') || nextCharacter('s')
              || nextCharacter('w') || nextCharacter('0') || nextCharacter('4') || nextCharacter('8'))) {
        nextCharacter('=');
      } else {
        index = lastGoodIndex;
        if (nextBase64()) {
          if (nextCharacter('A') || nextCharacter('Q') || nextCharacter('g') || nextCharacter('w')) {
            nextConstant("==");
          } else {
            return false;
          }
        }
      }
      return nextCharacter('\'');
    }
    return false;
  }

  private boolean nextEnumValue() {
    if (nextQualifiedName() && nextCharacter('\'')) {
      do {
        if (!(nextODataIdentifier() || nextIntegerValue(true))) {
          return false;
        }
      } while (nextCharacter(','));
      return nextCharacter('\'');
    }
    return false;
  }

  /**
   * Moves past a geo prefix if found; otherwise leaves the index unchanged.
   * @return whether a geo prefix has been found at the current index
   */
  private boolean nextGeoPrefix(final boolean isGeography) {
    return nextConstantIgnoreCase(isGeography ? "geography" : "geometry");
  }

  /**
   * Moves past an SRID if found; otherwise leaves the index unchanged.
   * @return whether an SRID has been found at the current index
   */
  private boolean nextSrid() {
    final int lastGoodIndex = index;
    if (nextConstantIgnoreCase("SRID") && nextCharacter('=') && nextDigit()) {
      // The digit checked above is mandatory, four more digits are optional.
      nextDigit();
      nextDigit();
      nextDigit();
      nextDigit();
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  /**
   * Moves past a geo position if found; otherwise leaves the index unchanged.
   * @return whether a geo position has been found at the current index
   */
  private boolean nextPosition() {
    final int lastGoodIndex = index;
    if ((nextDoubleValue() || nextDecimalValue() || nextIntegerValue(true))
        && nextCharacter(' ')
        && (nextDoubleValue() || nextDecimalValue() || nextIntegerValue(true))) {
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  /**
   * Moves past a geo-point data instance if found; otherwise leaves the index unchanged.
   * @return whether a geo-point data instance has been found at the current index
   */
  private boolean nextPointData() {
    final int lastGoodIndex = index;
    if (nextCharacter('(') && nextPosition() && nextCharacter(')')) {
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  private boolean nextPoint() {
    return nextConstantIgnoreCase("Point") && nextPointData();
  }

  private boolean nextGeoPoint(final boolean isGeography) {
    return nextGeoPrefix(isGeography) && nextCharacter('\'')
        && nextSrid() && nextCharacter(';') && nextPoint()
        && nextCharacter('\'');
  }

  /**
   * Moves past geo LineString data if found; otherwise leaves the index unchanged.
   * @param isRing whether the line is a closed ring (in that case it must have at least four positions,
   *               and the last position must have the same coordinates as the first position)
   * @return whether geo LineString data has been found at the current index
   */
  private boolean nextLineStringData(final boolean isRing) {
    final int lastGoodIndex = index;
    if (nextCharacter('(') && nextPosition()) {
      int count = 1;
      final String firstPosition = isRing ? parseString.substring(lastGoodIndex + 1, index) : null;
      int positionStart = -1;
      while (nextCharacter(',')) {
        positionStart = index;
        if (nextPosition()) {
          count++;
        } else {
          index = lastGoodIndex;
          return false;
        }
      }
      if (count < (isRing ? 4 : 2)) {
        index = lastGoodIndex;
        return false;
      }
      if (isRing) {
        final String lastPosition = parseString.substring(positionStart, index);
        if (!lastPosition.equals(firstPosition)) {
          index = lastGoodIndex;
          return false;
        }
      }
      if (!nextCharacter(')')) {
        index = lastGoodIndex;
        return false;
      }
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  private boolean nextLineString() {
    return nextConstantIgnoreCase("LineString") && nextLineStringData(false);
  }

  private boolean nextGeoLineString(final boolean isGeography) {
    return nextGeoPrefix(isGeography) && nextCharacter('\'')
        && nextSrid() && nextCharacter(';') && nextLineString() 
        && nextCharacter('\'');
  }

  /**
   * Moves past geo polygon data if found; otherwise leaves the index unchanged.
   * @return whether geo polygon data have been found at the current index
   */
  private boolean nextPolygonData() {
    final int lastGoodIndex = index;
    if (nextCharacter('(') && nextLineStringData(true)) {
      // The polygon can have holes, described by further rings.
      while (nextCharacter(',')) {
        if (!nextLineStringData(true)) {
          index = lastGoodIndex;
          return false;
        }
      }
      if (!nextCharacter(')')) {
        index = lastGoodIndex;
        return false;
      }
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  private boolean nextPolygon() {
    return nextConstantIgnoreCase("Polygon") && nextPolygonData();
  }

  private boolean nextGeoPolygon(final boolean isGeography) {
    return nextGeoPrefix(isGeography) && nextCharacter('\'')
        && nextSrid() && nextCharacter(';') && nextPolygon() 
        && nextCharacter('\'');
  }

  private boolean nextMultiPoint() {
    if (nextConstantIgnoreCase("MultiPoint") && nextCharacter('(') && nextPointData()) {
      while (nextCharacter(',')) {
        if (!nextPointData()) {
          return false;
        }
      }
    }
    return nextCharacter(')');
  }

  private boolean nextGeoMultiPoint(final boolean isGeography) {
    return nextGeoPrefix(isGeography) && nextCharacter('\'')
        && nextSrid() && nextCharacter(';') && nextMultiPoint()
        && nextCharacter('\'');
  }

  private boolean nextMultiLineString() {
    if (nextConstantIgnoreCase("MultiLineString") && nextCharacter('(') && nextLineStringData(false)) {
      while (nextCharacter(',')) {
        if (!nextLineStringData(false)) {
          return false;
        }
      }
    }
    return nextCharacter(')');
  }

  private boolean nextGeoMultiLineString(final boolean isGeography) {
    return nextGeoPrefix(isGeography) && nextCharacter('\'')
        && nextSrid() && nextCharacter(';') && nextMultiLineString()
        && nextCharacter('\'');
  }

  private boolean nextMultiPolygon() {
    if (nextConstantIgnoreCase("MultiPolygon") && nextCharacter('(') && nextPolygonData()) {
      while (nextCharacter(',')) {
        if (!nextPolygonData()) {
          return false;
        }
      }
    }
    return nextCharacter(')');
  }

  private boolean nextGeoMultiPolygon(final boolean isGeography) {
    return nextGeoPrefix(isGeography) && nextCharacter('\'')
        && nextSrid() && nextCharacter(';') && nextMultiPolygon()
        && nextCharacter('\'');
  }

  /**
   * Moves past geo data if found; otherwise leaves the index unchanged.
   * @return whether geo data has been found at the current index
   */
  private boolean nextGeo() {
    final int lastGoodIndex = index;
    if (nextPoint()) {
      return true;
    } else {
      index = lastGoodIndex;
    }
    if (nextLineString()) {
      return true;
    } else {
      index = lastGoodIndex;
    }
    if (nextPolygon()) {
      return true;
    } else {
      index = lastGoodIndex;
    }
    if (nextMultiPoint()) {
      return true;
    } else {
      index = lastGoodIndex;
    }
    if (nextMultiLineString()) {
      return true;
    } else {
      index = lastGoodIndex;
    }
    if (nextMultiPolygon()) {
      return true;
    } else {
      index = lastGoodIndex;
    }
    if (nextCollection()) {
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  private boolean nextCollection() {
    if (nextConstantIgnoreCase("Collection") && nextCharacter('(') && nextGeo()) {
      while (nextCharacter(',')) {
        if (!nextGeo()) {
          return false;
        }
      }
    }
    return nextCharacter(')');
  }

  private boolean nextGeoCollection(final boolean isGeography) {
    return nextGeoPrefix(isGeography) && nextCharacter('\'')
        && nextSrid() && nextCharacter(';') && nextCollection()
        && nextCharacter('\'');
  }

  /**
   * Moves past a JSON string if found; otherwise leaves the index unchanged.
   * @return whether a JSON string has been found at the current index
   */
  private boolean nextJsonString() {
    final int lastGoodIndex = index;
    if (nextCharacter('"')) {
      do {
        if (nextCharacter('\\')) {
          if (!(nextCharacter('b') || nextCharacter('t')
              || nextCharacter('n') || nextCharacter('f') || nextCharacter('r')
              || nextCharacter('"') || nextCharacter('/') || nextCharacter('\\')
              || nextCharacter('u') && nextHexDigit() && nextHexDigit() && nextHexDigit() && nextHexDigit())) {
            index = lastGoodIndex;
            return false;
          }
        } else if (nextCharacter('"')) {
          return true;
        } else {
          index++;
        }
      } while (index < parseString.length());
      index = lastGoodIndex;
      return false;
    }
    index = lastGoodIndex;
    return false;
  }

  private boolean nextJsonValue() {
    return nextConstant("null") || nextConstant("true") || nextConstant("false")
        || nextDoubleValue() || nextDecimalValue() || nextIntegerValue(true)
        || nextJsonString()
        || nextJsonArrayOrObject();
  }

  /**
   * Moves past a JSON object member if found; otherwise leaves the index unchanged.
   * @return whether a JSON object member has been found at the current index
   */
  private boolean nextJsonMember() {
    final int lastGoodIndex = index;
    if (nextJsonString() && nextCharacter(':') && nextJsonValue()) {
      return true;
    } else {
      index = lastGoodIndex;
      return false;
    }
  }

  /**
   * Moves past a JSON array or object if found; otherwise leaves the index unchanged.
   * @return whether a JSON array or object has been found at the current index
   */
  private boolean nextJsonArrayOrObject() {
    final int lastGoodIndex = index;
    if (nextCharacter('[')) {
      if (nextJsonValue()) {
        while (nextCharacter(',')) {
          if (!nextJsonValue()) {
            index = lastGoodIndex;
            return false;
          }
        }
      }
      if (nextCharacter(']')) {
        return true;
      } else {
        index = lastGoodIndex;
        return false;
      }
    } else if (nextCharacter('{')) {
      if (nextJsonMember()) {
        while (nextCharacter(',')) {
          if (!nextJsonMember()) {
            index = lastGoodIndex;
            return false;
          }
        }
      }
      if (nextCharacter('}')) {
        return true;
      } else {
        index = lastGoodIndex;
        return false;
      }
    } else {
      return false;
    }
  }

  private boolean nextAndOperatorSearch() {
    if (nextWhitespace()) {
      final int lastGoodIndex = index;
      if (nextUnaryOperator("OR")) {
        return false;
      } else if (!(nextUnaryOperator("AND"))) {
        index = lastGoodIndex;
      }
      return true;
    } else {
      return false;
    }
  }

  private boolean nextWord() {
    int count = 0;
    while (index < parseString.length()) {
      final int code = parseString.codePointAt(index);
      if (Character.isUnicodeIdentifierStart(code)) {
        count++;
        // Unicode characters outside of the Basic Multilingual Plane are represented as two Java characters.
        index += Character.isSupplementaryCodePoint(code) ? 2 : 1;
      } else {
        break;
      }
    }
    final String word = parseString.substring(index - count, index);
    return count > 0 && !("OR".equals(word) || "AND".equals(word) || "NOT".equals(word));
  }

  private boolean nextPhrase() {
    if (nextCharacter('"')) {
      do {
        if (nextCharacter('\\')) {
          if (!(nextCharacter('\\') || nextCharacter('"'))) {
            return false;
          }
        } else if (nextCharacter('"')) {
          return true;
        } else {
          index++;
        }
      } while (index < parseString.length());
      return false;
    }
    return false;
  }
}
