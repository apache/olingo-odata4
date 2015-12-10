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
 * because this tokenizer should behave like a classical token-consuming tokenizer.</p>
 */
public class UriTokenizer {

  public enum TokenKind {
    EOF, // signals the end of the string to be parsed

    // constant-value tokens (convention: uppercase)
    REF,
    VALUE,
    COUNT,
    CROSSJOIN,
    OPEN,
    CLOSE,
    COMMA,
    SEMI,
    DOT,
    SLASH,
    EQ,
    STAR,
    PLUS,
    NULL,

    // variable-value tokens (convention: mixed case)
    ODataIdentifier,
    QualifiedName,
    ParameterAliasName,

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

    jsonArrayOrObject
  }

  private final String parseString;

  private int startIndex = 0;
  private int index = 0;

  public UriTokenizer(final String parseString) {
    this.parseString = parseString == null ? "" : parseString;
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
    case EOF:
      found = index >= parseString.length();
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
    case PrimitiveBooleanValue:
      found = nextBooleanValue();
      break;
    case PrimitiveStringValue:
      found = nextStringValue();
      break;
    case PrimitiveIntegerValue:
      found = nextIntegerValue(true);
      break;
    case PrimitiveGuidValue:
      found = nextGuidValue();
      break;
    case PrimitiveDateValue:
      found = nextDateValue();
      break;
    case PrimitiveDateTimeOffsetValue:
      found = nextDateTimeOffsetValue();
      break;
    case PrimitiveTimeOfDayValue:
      found = nextTimeOfDayValue();
      break;
    case PrimitiveDecimalValue:
      found = nextDecimalValue();
      break;
    case PrimitiveDoubleValue:
      found = nextDoubleValue();
      break;
    case PrimitiveDurationValue:
      found = nextDurationValue();
      break;
    case PrimitiveBinaryValue:
      found = nextBinaryValue();
      break;
    case PrimitiveEnumValue:
      found = nextEnumValue();
      break;

    // Primitive Values
    case jsonArrayOrObject:
      found = nextJsonArrayOrObject();
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
        if (nextIntegerValue(false)) {
          if (!(nextCharacter('D') || nextCharacter('d'))) {
            return false;
          }
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
            if (nextCharacter('.')) {
              if (!nextIntegerValue(false)) {
                return false;
              }
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

  private boolean nextJsonMember() {
    return nextJsonString() && nextCharacter(':') && nextJsonValue();
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
}
