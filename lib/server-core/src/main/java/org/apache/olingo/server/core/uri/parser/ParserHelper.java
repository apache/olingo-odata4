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

import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;

public class ParserHelper {

  public static void requireNext(UriTokenizer tokenizer, final TokenKind required) throws UriParserException {
    if (!tokenizer.next(required)) {
      throw new UriParserSyntaxException("Expected token '" + required.toString() + "' not found.",
          UriParserSyntaxException.MessageKeys.SYNTAX);
    }
  }

  public static void requireTokenEnd(UriTokenizer tokenizer) throws UriParserException {
    requireNext(tokenizer, TokenKind.EOF);
  }

  public static TokenKind next(UriTokenizer tokenizer, final TokenKind... kind) {
    for (int i = 0; i < kind.length; i++) {
      if (tokenizer.next(kind[i])) {
        return kind[i];
      }
    }
    return null;
  }

  public static TokenKind nextPrimitive(UriTokenizer tokenizer) {
    return next(tokenizer,
        TokenKind.NULL,
        TokenKind.BooleanValue,
        TokenKind.StringValue,

        // The order of the next seven expressions is important in order to avoid
        // finding partly parsed tokens (counter-intuitive as it may be, even a GUID may start with digits ...).
        TokenKind.DoubleValue,
        TokenKind.DecimalValue,
        TokenKind.GuidValue,
        TokenKind.DateTimeOffsetValue,
        TokenKind.DateValue,
        TokenKind.TimeOfDayValue,
        TokenKind.IntegerValue,

        TokenKind.DurationValue,
        TokenKind.BinaryValue,
        TokenKind.EnumValue);
  }
}
