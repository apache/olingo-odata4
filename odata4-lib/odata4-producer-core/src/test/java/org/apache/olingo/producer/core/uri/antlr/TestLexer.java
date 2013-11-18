/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.producer.core.uri.antlr;

import org.antlr.v4.runtime.LexerNoViableAltException;
import org.apache.olingo.producer.core.testutil.TokenValidator;
import org.junit.Test;

public class TestLexer {

  private TokenValidator test = null;

  private static final String cPCT_ENCODED = "%45%46%47" + "%22" + "%5C";// last two chars are not in
                                                                         // cPCT_ENCODED_UNESCAPED
  private static final String cPCT_ENCODED_UNESCAPED = "%45%46%47";
  private static final String cUNRESERVED = "ABCabc123-._~";
  private static final String cOTHER_DELIMS = "!()*+,;";
  private static final String cSUB_DELIMS = "$&'=" + cOTHER_DELIMS;

  private static final String cQCHAR_NO_AMP = cUNRESERVED + cPCT_ENCODED + cOTHER_DELIMS + ":@/?$'=";
  // private static final String cQCHAR_NO_AMP_EQ = cUNRESERVED + cPCT_ENCODED + cOTHER_DELIMS + ":@/?$'";
  // private static final String cQCHAR_NO_AMP_EQ_AT_DOLLAR = cUNRESERVED + cPCT_ENCODED + cOTHER_DELIMS + ":/?'";

  private static final String cPCTENCODEDnoSQUOTE = "%65%66%67";
  //private static final String cPCHARnoSQUOTE = cUNRESERVED + cPCTENCODEDnoSQUOTE + cOTHER_DELIMS + "$&=:@";

  private static final String cPCHAR = cUNRESERVED + cPCT_ENCODED + cSUB_DELIMS + ":@";

  private static final String cQCHAR_UNESCAPED = cUNRESERVED + cPCT_ENCODED_UNESCAPED + cOTHER_DELIMS + ":@/?$'=";

  // QCHAR_NO_AMP_DQUOTE : QCHAR_UNESCAPED | ESCAPE ( ESCAPE | QUOTATION_MARK );
  private static final String cQCHAR_NO_AMP_DQUOTE = cQCHAR_UNESCAPED + "\\\\\\\"";

  private static final String cQCHAR_JSON_SPECIAL = " :{}[]";

  private static final String cESCAPE = "\\";
  private static final String cQUOTATION_MARK = "\"";

  public TestLexer() {
    test = new TokenValidator();
  }

  // ;------------------------------------------------------------------------------
  // ; 0. URI
  // ;------------------------------------------------------------------------------
  @Test
  public void testUriTokens() {
    test.run("#").isText("#").isType(UriLexer.FRAGMENT);
    test.run("$count").isText("$count").isType(UriLexer.COUNT);
    test.run("$ref").isText("$ref").isType(UriLexer.REF);
    test.run("$value").isText("$value").isType(UriLexer.VALUE);
  }

  // ;------------------------------------------------------------------------------
  // ; 2. Query Options
  // ;------------------------------------------------------------------------------
  @Test
  public void testQueryOptionsTokens() {
    test.run("$skip=1").isText("$skip=1").isType(UriLexer.SKIP);
    test.run("$skip=2").isText("$skip=2").isType(UriLexer.SKIP);
    test.run("$skip=123").isText("$skip=123").isType(UriLexer.SKIP);
    test.run("$skip=A").isExType(LexerNoViableAltException.class);

    test.run("$top=1").isText("$top=1").isType(UriLexer.TOP);
    test.run("$top=2").isText("$top=2").isType(UriLexer.TOP);
    test.run("$top=123").isText("$top=123").isType(UriLexer.TOP);
    test.run("$top=A").isExType(LexerNoViableAltException.class);

    test.run("$levels=1").isText("$levels=1").isType(UriLexer.LEVELS);
    test.run("$levels=2").isText("$levels=2").isType(UriLexer.LEVELS);
    test.run("$levels=123").isText("$levels=123").isType(UriLexer.LEVELS);
    test.run("$levels=max").isText("$levels=max").isType(UriLexer.LEVELS);
    test.run("$levels=A").isExType(LexerNoViableAltException.class);

    test.run("$format=atom").isText("$format=atom").isType(UriLexer.FORMAT);
    test.run("$format=json").isText("$format=json").isType(UriLexer.FORMAT);
    test.run("$format=xml").isText("$format=xml").isType(UriLexer.FORMAT);
    test.run("$format=abc/def").isText("$format=abc/def").isType(UriLexer.FORMAT);
    test.run("$format=abc").isExType(LexerNoViableAltException.class);

    test.run("$id=123").isText("$id=123").isType(UriLexer.ID);
    test.run("$id=ABC").isText("$id=ABC").isType(UriLexer.ID);

    test.run("$skiptoken=ABC").isText("$skiptoken=ABC").isType(UriLexer.SKIPTOKEN);
    test.run("$skiptoken=ABC").isText("$skiptoken=ABC").isType(UriLexer.SKIPTOKEN);

    test.run("\"ABC\"", true).isText("\"ABC\"").isType(UriLexer.SEARCHPHRASE);

    test.run("$id=" + cQCHAR_NO_AMP + "", true).isInput().isType(UriLexer.ID);

  }

  // ;------------------------------------------------------------------------------
  // ; 4. Expressions
  // ;------------------------------------------------------------------------------
  @Test
  public void testQueryExpressions() {
    // assertEquals("expected","actual");
    test.run("$it").isText("$it").isType(UriLexer.IMPLICIT_VARIABLE_EXPR);
    test.run("$itabc").isText("$it").isType(UriLexer.IMPLICIT_VARIABLE_EXPR);

    test.run("contains").isText("contains").isType(UriLexer.CONTAINS);

    test.run("containsabc").isText("containsabc").isType(UriLexer.ODATAIDENTIFIER); // test that this is a ODI

    test.run("startswith").isText("startswith").isType(UriLexer.STARTSWITH);
    test.run("endswith").isText("endswith").isType(UriLexer.ENDSWITH);
    test.run("length").isText("length").isType(UriLexer.LENGTH);
    test.run("indexof").isText("indexof").isType(UriLexer.INDEXOF);
    test.run("substring").isText("substring").isType(UriLexer.SUBSTRING);
    test.run("tolower").isText("tolower").isType(UriLexer.TOLOWER);
    test.run("toupper").isText("toupper").isType(UriLexer.TOUPPER);
    test.run("trim").isText("trim").isType(UriLexer.TRIM);
    test.run("concat").isText("concat").isType(UriLexer.CONCAT);

  }

  // ;------------------------------------------------------------------------------
  // ; 5. JSON format for function parameters
  // ;------------------------------------------------------------------------------
  @Test
  public void testQueryJSON_and_otheres() {
    // QUOTATION_MARK
    test.run("\"").isText("\"").isType(UriLexer.QUOTATION_MARK);
    test.run("%22").isText("%22").isType(UriLexer.QUOTATION_MARK);

    // Lexer rule QCHAR_UNESCAPED
    test.run("\"abc\"").isText("\"abc\"").isType(UriLexer.STRING_IN_JSON);

    // Lexer rule QCHAR_JSON_SPECIAL
    test.run("\"" + cQCHAR_JSON_SPECIAL + "\"").isInput().isType(UriLexer.STRING_IN_JSON);

    // Lexer rule CHAR_IN_JSON
    test.run("\"" + cQCHAR_UNESCAPED + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cQCHAR_JSON_SPECIAL + "\"").isInput().isType(UriLexer.STRING_IN_JSON);

    // Lexer rule ESCAPE CHAR_IN_JSON
    test.run("\"" + cESCAPE + cQUOTATION_MARK + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + cESCAPE + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "/" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "%2F" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "b" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "f" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "n" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "r" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "t" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
    test.run("\"" + cESCAPE + "u12AB" + "\"").isInput().isType(UriLexer.STRING_IN_JSON);
  }

  // ;------------------------------------------------------------------------------
  // ; 6. Names and identifiers
  // ;------------------------------------------------------------------------------
  @Test
  public void testNamesAndIdentifiers() {

    test.run("Binary").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Boolean").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Byte").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Date").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("DateTimeOffset").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Decimal").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Double").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Duration").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Guid").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Int16").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Int32").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Int64").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("SByte").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Single").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Stream").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("String").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("TimeOfDay").isInput().isType(UriLexer.PRIMITIVETYPENAME);

    test.run("Geography").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run("Geometry").isInput().isType(UriLexer.PRIMITIVETYPENAME);

    String g = "Geography";
    test.run(g + "Collection").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "LineString").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "MultiLineString").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "MultiPoint").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "MultiPolygon").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "Point").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "LineString").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "Polygon").isInput().isType(UriLexer.PRIMITIVETYPENAME);

    g = "Geometry";
    test.run(g + "Collection").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "LineString").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "MultiLineString").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "MultiPoint").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "MultiPolygon").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "Point").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "LineString").isInput().isType(UriLexer.PRIMITIVETYPENAME);
    test.run(g + "Polygon").isInput().isType(UriLexer.PRIMITIVETYPENAME);
  }

  // ;------------------------------------------------------------------------------
  // ; 7. Literal Data Values
  // ;------------------------------------------------------------------------------
  @Test
  public void testLiteralDataValues() {
    // null
    test.run("null").isInput().isType(UriLexer.NULLVALUE);

    // binary
    test.run("X'ABCD'").isInput().isType(UriLexer.BINARY);
    test.run("X'ABCD'").isInput().isType(UriLexer.BINARY);
    test.run("binary'ABCD'").isInput().isType(UriLexer.BINARY);
    test.run("BiNaRy'ABCD'").isInput().isType(UriLexer.BINARY);

    // not a binary
    test.run("x'ABCDA'")
        .at(0).isText("x").isType(UriLexer.ODATAIDENTIFIER)
        .at(1).isText("'ABCDA'").isType(UriLexer.STRING);
    test.run("BiNaRy'ABCDA'")
        .at(0).isText("BiNaRy").isType(UriLexer.ODATAIDENTIFIER)
        .at(1).isText("'ABCDA'").isType(UriLexer.STRING);

    // boolean
    test.run("true").isInput().isType(UriLexer.TRUE);
    test.run("false").isInput().isType(UriLexer.FALSE);
    test.run("TrUe").isInput().isType(UriLexer.BOOLEAN);
    test.run("FaLsE").isInput().isType(UriLexer.BOOLEAN);

    // Lexer rule INT
    test.run("123").isInput().isType(UriLexer.INT);
    test.run("123456789").isInput().isType(UriLexer.INT);
    test.run("+123").isInput().isType(UriLexer.INT);
    test.run("+123456789").isInput().isType(UriLexer.INT);
    test.run("-123").isInput().isType(UriLexer.INT);
    test.run("-123456789").isInput().isType(UriLexer.INT);
    // Lexer rule DECIMAL
    test.run("0.1").isInput().isType(UriLexer.DECIMAL);
    test.run("1.1").isInput().isType(UriLexer.DECIMAL);
    test.run("+0.1").isInput().isType(UriLexer.DECIMAL);
    test.run("+1.1").isInput().isType(UriLexer.DECIMAL);
    test.run("-0.1").isInput().isType(UriLexer.DECIMAL);
    test.run("-1.1").isInput().isType(UriLexer.DECIMAL);
    // Lexer rule EXP
    test.run("1.1e+1").isInput().isType(UriLexer.DECIMAL);
    test.run("1.1e-1").isInput().isType(UriLexer.DECIMAL);

    test.run("NaN").isInput().isType(UriLexer.NANINFINITY);
    test.run("-INF").isInput().isType(UriLexer.NANINFINITY);
    test.run("INF").isInput().isType(UriLexer.NANINFINITY);

    // Lexer rule DATE
    test.run("date'2013-11-15'").isInput().isType(UriLexer.DATE);
    test.run("DaTe'2013-11-15'").isInput().isType(UriLexer.DATE);

    // Lexer rule DATETIMEOFFSET
    test.run("datetimeoffset'2013-11-15T13:35Z'").isInput().isType(UriLexer.DATETIMEOFFSET);
    test.run("datetimeoffset'2013-11-15T13:35:10Z'").isInput().isType(UriLexer.DATETIMEOFFSET);
    test.run("datetimeoffset'2013-11-15T13:35:10.1234Z'").isInput().isType(UriLexer.DATETIMEOFFSET);

    test.run("datetimeoffset'2013-11-15T13:35:10.1234+01:30'").isInput().isType(UriLexer.DATETIMEOFFSET);
    test.run("datetimeoffset'2013-11-15T13:35:10.1234-01:12'").isInput().isType(UriLexer.DATETIMEOFFSET);

    test.run("DaTeTiMeOfFsEt'2013-11-15T13:35Z'").isInput().isType(UriLexer.DATETIMEOFFSET);

    // Lexer rule DURATION
    test.run("duration'PT67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT67.89S'").isInput().isType(UriLexer.DURATION);

    test.run("duration'PT5M'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT5M67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT5M67.89S'").isInput().isType(UriLexer.DURATION);

    test.run("duration'PT4H'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT4H67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT4H67.89S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT4H5M'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT4H5M67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'PT4H5M67.89S'").isInput().isType(UriLexer.DURATION);

    test.run("duration'P3D'");
    test.run("duration'P3DT67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT67.89S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT5M'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT5M67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT5M67.89S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT4H'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT4H67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT4H67.89S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT4H5M'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT4H5M67S'").isInput().isType(UriLexer.DURATION);
    test.run("duration'P3DT4H5M67.89S'").isInput().isType(UriLexer.DURATION);

    test.run("DuRaTiOn'P3DT4H5M67.89S'").isInput().isType(UriLexer.DURATION);
    test.run("DuRaTiOn'-P3DT4H5M67.89S'").isInput().isType(UriLexer.DURATION);

    test.run("timeofday'20:00'").isInput().isType(UriLexer.TIMEOFDAY);
    test.run("timeofday'20:15:01'").isInput().isType(UriLexer.TIMEOFDAY);
    test.run("timeofday'20:15:01.02'").isInput().isType(UriLexer.TIMEOFDAY);
  }

  // ;------------------------------------------------------------------------------
  // ; 0. misc
  // ;------------------------------------------------------------------------------

  @Test
  public void testCriticalOrder() {
    // Test lexer rule STRING
    test.run("'abc'").isInput().isType(UriLexer.STRING);

    // Test lexer rule SEARCHWORD
    test.run("abc", true).isInput().isType(UriLexer.SEARCHWORD);

    // Test lexer rule SEARCHPHRASE
    test.run("\"abc\"", true).isInput().isType(UriLexer.SEARCHPHRASE);

    // Test lexer rule ODATAIDENTIFIER
    test.run("abc").isInput().isType(UriLexer.ODATAIDENTIFIER);
    test.run("@abc").isInput().isType(UriLexer.AT_ODATAIDENTIFIER);

    test.run("\"abc\"").isInput().isType(UriLexer.STRING_IN_JSON);
  }

  @Test
  public void testDelims() {
    String reserved = "/";

    // Test lexer rule UNRESERVED
    test.run("$format=A/" + cUNRESERVED).isInput().isType(UriLexer.FORMAT);
    test.run("$format=A/" + cUNRESERVED + reserved).isText("$format=A/" + cUNRESERVED).isType(UriLexer.FORMAT);
    // Test lexer rule PCT_ENCODED
    test.run("$format=A/" + cPCT_ENCODED).isInput().isType(UriLexer.FORMAT);
    test.run("$format=A/" + cPCT_ENCODED + reserved).isText("$format=A/" + cPCT_ENCODED).isType(UriLexer.FORMAT);
    // Test lexer rule SUB_DELIMS
    test.run("$format=A/" + cSUB_DELIMS).isInput().isType(UriLexer.FORMAT);
    test.run("$format=A/" + cSUB_DELIMS + reserved).isText("$format=A/" + cSUB_DELIMS).isType(UriLexer.FORMAT);
    // Test lexer rule PCHAR rest
    test.run("$format=A/:@").isText("$format=A/:@").isType(UriLexer.FORMAT);
    test.run("$format=A/:@" + reserved).isText("$format=A/:@").isType(UriLexer.FORMAT);
    // Test lexer rule PCHAR all
    test.run("$format=" + cPCHAR + "/" + cPCHAR).isInput().isType(UriLexer.FORMAT);
    test.run("$format=" + cPCHAR + "/" + cPCHAR + reserved)
        .isText("$format=" + cPCHAR + "/" + cPCHAR)
        .isType(UriLexer.FORMAT);

    // Test lexer rule QCHAR_NO_AMP
    String amp = "&";
    // Test lexer rule UNRESERVED
    test.run("$id=" + cUNRESERVED).isInput().isType(UriLexer.ID);
    test.run("$id=" + cUNRESERVED + amp).isText("$id=" + cUNRESERVED).isType(UriLexer.ID);
    // Test lexer rule PCT_ENCODED
    test.run("$id=" + cPCT_ENCODED).isInput().isType(UriLexer.ID);
    test.run("$id=" + cPCT_ENCODED + amp).isText("$id=" + cPCT_ENCODED).isType(UriLexer.ID);
    // Test lexer rule OTHER_DELIMS
    test.run("$id=" + cOTHER_DELIMS).isInput().isType(UriLexer.ID);
    test.run("$id=" + cOTHER_DELIMS + amp).isText("$id=" + cOTHER_DELIMS).isType(UriLexer.ID);
    // Lexer rule QCHAR_NO_AMP rest
    test.run("$id=:@/?$'=").isText("$id=:@/?$'=").isType(UriLexer.ID);
    test.run("$id=:@/?$'=" + amp).isText("$id=:@/?$'=").isType(UriLexer.ID);

    // Test lexer rule QCHAR_NO_AMP_DQUOTE
    test.run("\"" + cQCHAR_NO_AMP_DQUOTE + "\"", true).isInput().isType(UriLexer.SEARCHPHRASE);

  }

}
