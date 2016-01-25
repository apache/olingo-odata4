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
import static org.junit.Assert.assertTrue;

import org.apache.olingo.server.core.uri.parser.UriTokenizer;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.junit.Test;

/**
 * Tests originally written for the ANTLR lexer.
 */
public class LexerTest {

  private TokenValidator test = new TokenValidator();

  @Test
  public void unary() {
    test.run("-a eq a").has(TokenKind.MinusOperator, TokenKind.ODataIdentifier, TokenKind.EqualsOperator,
        TokenKind.ODataIdentifier).isInput();
  }

  @Test
  public void uriTokens() {
//    test.run("#").isType(TokenKind.FRAGMENT).isInput();
    test.run("$count").has(TokenKind.COUNT).isInput();
    test.run("$ref").has(TokenKind.REF).isInput();
    test.run("$value").has(TokenKind.VALUE).isInput();
  }

  @Test
  public void queryOptionsTokens() {
    test.run("$skip=1").has(TokenKind.SKIP, TokenKind.EQ, TokenKind.IntegerValue).isInput();
    test.run("$skip=2").has(TokenKind.SKIP, TokenKind.EQ, TokenKind.IntegerValue).isInput();
    test.run("$skip=123").has(TokenKind.SKIP, TokenKind.EQ, TokenKind.IntegerValue).isInput();

    test.run("$top=1").has(TokenKind.TOP, TokenKind.EQ, TokenKind.IntegerValue).isInput();
    test.run("$top=2").has(TokenKind.TOP, TokenKind.EQ, TokenKind.IntegerValue).isInput();
    test.run("$top=123").has(TokenKind.TOP, TokenKind.EQ, TokenKind.IntegerValue).isInput();

    test.run("$levels=1").has(TokenKind.LEVELS, TokenKind.EQ, TokenKind.IntegerValue).isInput();
    test.run("$levels=2").has(TokenKind.LEVELS, TokenKind.EQ, TokenKind.IntegerValue).isInput();
    test.run("$levels=123").has(TokenKind.LEVELS, TokenKind.EQ, TokenKind.IntegerValue).isInput();
    test.run("$levels=max").has(TokenKind.LEVELS, TokenKind.EQ, TokenKind.MAX).isInput();

//    test.run("$format=atom").has(TokenKind.FORMAT, TokenKind.EQ, TokenKind.ODataIdentifier).isInput();
//    test.run("$format=json").has(TokenKind.FORMAT, TokenKind.EQ, TokenKind.ODataIdentifier).isInput();
//    test.run("$format=xml").has(TokenKind.FORMAT,, TokenKind.EQ, TokenKind.ODataIdentifier).isInput();
//    test.run("$format=abc/def").has(TokenKind.FORMAT, TokenKind.EQ,
//        TokenKind.ODataIdentifier, TokenKind.SLASH, TokenKind.ODataIdentifier).isInput();

//    test.run("$id=123").has(TokenKind.ID, TokenKind.EQ, TokenKind.IntegerValue).isInput();
//    test.run("$id=ABC").has(TokenKind.ID, TokenKind.EQ, TokenKind.ODataIdentifier).isInput();

//    test.run("$skiptoken=ABC").has(TokenKind.SKIPTOKEN, TokenKind.EQ, TokenKind.ODataIdentifier).isInput();
//    test.run("$skiptoken=ABC").has(TokenKind.SKIPTOKEN, TokenKind.EQ, TokenKind.ODataIdentifier).isInput();

    test.run("$search=\"ABC\"").has(TokenKind.SEARCH, TokenKind.EQ, TokenKind.Phrase).isInput();
    test.run("$search=ABC").has(TokenKind.SEARCH, TokenKind.EQ, TokenKind.Word).isInput();
    test.run("$search=\"A%20B%20C\"").has(TokenKind.SEARCH, TokenKind.EQ, TokenKind.Phrase).isInput();
    test.run("$search=Test Test").has(TokenKind.SEARCH, TokenKind.EQ, TokenKind.Word,
        TokenKind.AndOperatorSearch, TokenKind.Word).isInput();
    test.run("$search=Test&$filter=ABC eq 1").has(TokenKind.SEARCH, TokenKind.EQ, TokenKind.Word);
  }

  @Test
  public void queryOptionsDefaultMode() {
    test.run("$expand=ABC($skip=1)").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.SKIP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE).isInput();
    test.run("$expand=ABC($skip=123)").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.SKIP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE).isInput();
    test.run("$expand=ABC($search=abc)").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.SEARCH, TokenKind.EQ, TokenKind.Word, TokenKind.CLOSE).isInput();
    test.run("$expand=ABC($search=\"123\")").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.SEARCH, TokenKind.EQ, TokenKind.Phrase, TokenKind.CLOSE).isInput();
    test.run("$expand=ABC($top=1)").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.TOP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE).isInput();
    test.run("$expand=ABC($top=123)").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.TOP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE).isInput();

    test.run("$expand=ABC($expand=DEF($skip=1))").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.SKIP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE, TokenKind.CLOSE)
        .isInput();
    test.run("$expand=ABC($expand=DEF($skip=123))").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.SKIP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE, TokenKind.CLOSE)
        .isInput();

    test.run("$expand=ABC($expand=DEF($top=1))").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.TOP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE, TokenKind.CLOSE)
        .isInput();
    test.run("$expand=ABC($expand=DEF($top=123))").has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
        TokenKind.OPEN, TokenKind.TOP, TokenKind.EQ, TokenKind.IntegerValue, TokenKind.CLOSE, TokenKind.CLOSE)
        .isInput();

    test.run("$expand=ABC($expand=DEF($search=Test Test))")
        .has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
            TokenKind.OPEN, TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
            TokenKind.OPEN, TokenKind.SEARCH, TokenKind.EQ, TokenKind.Word,
            TokenKind.AndOperatorSearch, TokenKind.Word, TokenKind.CLOSE, TokenKind.CLOSE)
        .isInput();
    test.run("$expand=ABC($expand=DEF($search=\"Test\" \"Test\"))")
        .has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
            TokenKind.OPEN, TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
            TokenKind.OPEN, TokenKind.SEARCH, TokenKind.EQ, TokenKind.Phrase,
            TokenKind.AndOperatorSearch, TokenKind.Phrase, TokenKind.CLOSE, TokenKind.CLOSE)
        .isInput();
    test.run("$expand=ABC($expand=DEF($search=\"Test\" \"Test\";$filter=PropertyInt16 eq 0;$orderby=PropertyInt16))")
        .has(TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
            TokenKind.OPEN, TokenKind.EXPAND, TokenKind.EQ, TokenKind.ODataIdentifier,
            TokenKind.OPEN, TokenKind.SEARCH, TokenKind.EQ, TokenKind.Phrase,
            TokenKind.AndOperatorSearch, TokenKind.Phrase, TokenKind.SEMI,
            TokenKind.FILTER, TokenKind.EQ, TokenKind.ODataIdentifier, TokenKind.EqualsOperator,
            TokenKind.IntegerValue, TokenKind.SEMI,
            TokenKind.ORDERBY, TokenKind.EQ, TokenKind.ODataIdentifier, TokenKind.CLOSE, TokenKind.CLOSE)
        .isInput();
  }

  @Test
  public void queryExpressions() {
    test.run("$it").has(TokenKind.IT).isText("$it");

    test.run("$filter=contains(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.ContainsMethod).isText("contains(");

    test.run("$filter=containsabc").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.ODataIdentifier)
        .isText("containsabc");

    test.run("$filter=startswith(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.StartswithMethod)
        .isText("startswith(");
    test.run("$filter=endswith(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.EndswithMethod).isText("endswith(");
    test.run("$filter=length(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.LengthMethod).isText("length(");
    test.run("$filter=indexof(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.IndexofMethod).isText("indexof(");
    test.run("$filter=substring(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.SubstringMethod).isText("substring(");
    test.run("$filter=tolower(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.TolowerMethod).isText("tolower(");
    test.run("$filter=toupper(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.ToupperMethod).isText("toupper(");
    test.run("$filter=trim(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.TrimMethod).isText("trim(");
    test.run("$filter=concat(").has(TokenKind.FILTER, TokenKind.EQ, TokenKind.ConcatMethod).isText("concat(");
  }

  @Test
  public void literalDataValues() {
    // null
    test.run("null").has(TokenKind.NULL).isInput();

    // binary
    test.run("binary'ABCD'").has(TokenKind.BinaryValue).isInput();
    test.run("BiNaRy'ABCD'").has(TokenKind.BinaryValue).isInput();

    // boolean
    test.run("true").has(TokenKind.BooleanValue).isInput();
    test.run("false").has(TokenKind.BooleanValue).isInput();
    test.run("TrUe").has(TokenKind.BooleanValue).isInput();
    test.run("FaLsE").has(TokenKind.BooleanValue).isInput();

    // Lexer rule INT
    test.run("123").has(TokenKind.IntegerValue).isInput();
    test.run("123456789").has(TokenKind.IntegerValue).isInput();
    test.run("+123").has(TokenKind.IntegerValue).isInput();
    test.run("+123456789").has(TokenKind.IntegerValue).isInput();
    test.run("-123").has(TokenKind.IntegerValue).isInput();
    test.run("-123456789").has(TokenKind.IntegerValue).isInput();

    // Lexer rule DECIMAL
    test.run("0.1").has(TokenKind.DecimalValue).isInput();
    test.run("1.1").has(TokenKind.DecimalValue).isInput();
    test.run("+0.1").has(TokenKind.DecimalValue).isInput();
    test.run("+1.1").has(TokenKind.DecimalValue).isInput();
    test.run("-0.1").has(TokenKind.DecimalValue).isInput();
    test.run("-1.1").has(TokenKind.DecimalValue).isInput();

    // Lexer rule EXP
    test.run("1.1e+1").has(TokenKind.DoubleValue).isInput();
    test.run("1.1e-1").has(TokenKind.DoubleValue).isInput();

    test.run("NaN").has(TokenKind.DoubleValue).isInput();
    test.run("-INF").has(TokenKind.DoubleValue).isInput();
    test.run("INF").has(TokenKind.DoubleValue).isInput();

    // Lexer rule GUID
    test.run("1234ABCD-12AB-23CD-45EF-123456780ABC").has(TokenKind.GuidValue).isInput();
    test.run("1234ABCD-12AB-23CD-45EF-123456780ABC").has(TokenKind.GuidValue).isInput();

    // Lexer rule DATE
    test.run("2013-11-15").has(TokenKind.DateValue).isInput();

    // Lexer rule DATETIMEOFFSET
    test.run("2013-11-15T13:35Z").has(TokenKind.DateTimeOffsetValue).isInput();
    test.run("2013-11-15T13:35:10Z").has(TokenKind.DateTimeOffsetValue).isInput();
    test.run("2013-11-15T13:35:10.1234Z").has(TokenKind.DateTimeOffsetValue).isInput();

    test.run("2013-11-15T13:35:10.1234+01:30").has(TokenKind.DateTimeOffsetValue).isInput();
    test.run("2013-11-15T13:35:10.1234-01:12").has(TokenKind.DateTimeOffsetValue).isInput();

    test.run("2013-11-15T13:35Z").has(TokenKind.DateTimeOffsetValue).isInput();

    // Lexer rule DURATION
    test.run("duration'PT67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT67.89S'").has(TokenKind.DurationValue).isInput();

    test.run("duration'PT5M'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT5M67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT5M67.89S'").has(TokenKind.DurationValue).isInput();

    test.run("duration'PT4H'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT4H67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT4H67.89S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT4H5M'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT4H5M67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'PT4H5M67.89S'").has(TokenKind.DurationValue).isInput();

    test.run("duration'P3D'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT67.89S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT5M'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT5M67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT5M67.89S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT4H'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT4H67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT4H67.89S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT4H5M'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT4H5M67S'").has(TokenKind.DurationValue).isInput();
    test.run("duration'P3DT4H5M67.89S'").has(TokenKind.DurationValue).isInput();

    test.run("DuRaTiOn'P3DT4H5M67.89S'").has(TokenKind.DurationValue).isInput();
    test.run("DuRaTiOn'-P3DT4H5M67.89S'").has(TokenKind.DurationValue).isInput();

    test.run("20:00").has(TokenKind.TimeOfDayValue).isInput();
    test.run("20:15:01").has(TokenKind.TimeOfDayValue).isInput();
    test.run("20:15:01.02").has(TokenKind.TimeOfDayValue).isInput();

    test.run("20:15:01.02").has(TokenKind.TimeOfDayValue).isInput();

    // String
    test.run("'ABC'").has(TokenKind.StringValue).isInput();
    test.run("'A%20C'").has(TokenKind.StringValue).isInput();
    test.run("'%20%20%20ABC'").has(TokenKind.StringValue).isInput();
  }

  @Test
  public void delims() {
    // The last two chars are not in cPCT_ENCODED_UNESCAPED.
//    final String cPCT_ENCODED = "%45%46%47" + "%22" + "%5C";
//    final String cUNRESERVED = "ABCabc123-._~";
//    final String cOTHER_DELIMS = "!()*+,;";
//    final String cSUB_DELIMS = "$&'=" + cOTHER_DELIMS;

//    private static final String cPCHAR = cUNRESERVED + cPCT_ENCODED + cSUB_DELIMS + ":@";
//    final String reserved = "/";
    // Test lexer rule UNRESERVED
//    test.run("$format=A/" + cUNRESERVED).has(TokenKind.FORMAT).isInput();
//    test.run("$format=A/" + cUNRESERVED + reserved).has(TokenKind.FORMAT).isText(cUNRESERVED);
    // Test lexer rule PCT_ENCODED
//    test.run("$format=A/" + cPCT_ENCODED).has(TokenKind.FORMAT).isInput();
//    test.run("$format=A/" + cPCT_ENCODED + reserved).has(TokenKind.FORMAT).isText(cPCT_ENCODED);
    // Test lexer rule SUB_DELIMS
//    test.run("$format=A/" + cSUB_DELIMS).has(TokenKind.FORMAT).isInput();
//    test.run("$format=A/" + cSUB_DELIMS + reserved).has(TokenKind.FORMAT).isText("$");
    // Test lexer rule PCHAR rest
//    test.run("$format=A/:@").has(TokenKind.FORMAT).isInput();
//    test.run("$format=A/:@" + reserved).has(TokenKind.FORMAT).isText(":@");
    // Test lexer rule PCHAR all
//    test.run("$format=" + cPCHAR + "/" + cPCHAR).has(TokenKind.FORMAT).isInput();
  }

  public class TokenValidator {

    private String input = null;
    private UriTokenizer tokenizer = null;
    private String curText = null;

    public TokenValidator run(final String uri) {
      input = uri;
      tokenizer = new UriTokenizer(uri);
      curText = "";
      return this;
    }

    public TokenValidator has(final TokenKind... expected) {
      for (final TokenKind kind : expected) {
        assertTrue(tokenizer.next(kind));
        curText += tokenizer.getText();
      }
      return this;
    }

    public TokenValidator isText(final String expected) {
      assertEquals(expected, tokenizer.getText());
      return this;
    }

    public TokenValidator isInput() {
      assertEquals(input, curText);
      assertTrue(tokenizer.next(TokenKind.EOF));
      return this;
    }
  }
}
