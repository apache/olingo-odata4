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
package org.apache.olingo.server.core.uri.antlr;

import org.antlr.v4.runtime.Lexer;
import org.apache.olingo.server.core.uri.testutil.TokenValidator;
import org.junit.Test;

public class TestLexer {

  private TokenValidator test = null;

  private static final String cPCT_ENCODED = "%45%46%47" + "%22" + "%5C";// last two chars are not in
  // cPCT_ENCODED_UNESCAPED
  private static final String cUNRESERVED = "ABCabc123-._~";
  private static final String cOTHER_DELIMS = "!()*+,;";
  private static final String cSUB_DELIMS = "$&'=" + cOTHER_DELIMS;

  private static final String cPCHAR = cUNRESERVED + cPCT_ENCODED + cSUB_DELIMS + ":@";

  public TestLexer() {
    test = new TokenValidator();
  }

  @Test
  public void test() {

    // test.log(1).run("ESAllPrim?$orderby=PropertyDouble eq 3.5E+38");
  }

  // ;------------------------------------------------------------------------------
  // ; 0. URI
  // ;------------------------------------------------------------------------------

  @Test
  public void testUnary() {
    test.run("- a eq a").isAllInput();
  }

  @Test
  public void testUriTokens() {
    test.globalMode(UriLexer.MODE_QUERY);
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

    test.globalMode(UriLexer.MODE_QUERY);
    test.run("$skip=1").isAllText("$skip=1").isType(UriLexer.SKIP);
    test.run("$skip=2").isAllText("$skip=2").isType(UriLexer.SKIP);
    test.run("$skip=123").isAllText("$skip=123").isType(UriLexer.SKIP);

    test.run("$top=1").isAllText("$top=1").isType(UriLexer.TOP);
    test.run("$top=2").isAllText("$top=2").isType(UriLexer.TOP);
    test.run("$top=123").isAllText("$top=123").isType(UriLexer.TOP);

    test.run("$levels=1").isAllText("$levels=1").isType(UriLexer.LEVELS);
    test.run("$levels=2").isAllText("$levels=2").isType(UriLexer.LEVELS);
    test.run("$levels=123").isAllText("$levels=123").isType(UriLexer.LEVELS);
    test.run("$levels=max").isAllText("$levels=max").isType(UriLexer.LEVELS);

    test.run("$format=atom").isAllText("$format=atom").isType(UriLexer.FORMAT);
    test.run("$format=json").isAllText("$format=json").isType(UriLexer.FORMAT);
    test.run("$format=xml").isAllText("$format=xml").isType(UriLexer.FORMAT);
    test.run("$format=abc/def").isAllText("$format=abc/def").isType(UriLexer.FORMAT);

    test.run("$id=123").isAllText("$id=123").isType(UriLexer.ID);
    test.run("$id=ABC").isAllText("$id=ABC").isType(UriLexer.ID);

    test.run("$skiptoken=ABC").isAllText("$skiptoken=ABC").isType(UriLexer.SKIPTOKEN);
    test.run("$skiptoken=ABC").isAllText("$skiptoken=ABC").isType(UriLexer.SKIPTOKEN);

    test.run("$search=\"ABC\"").isAllText("$search=\"ABC\"").isType(UriLexer.SEARCH);
    test.run("$search=ABC").isAllText("$search=ABC").isType(UriLexer.SEARCH);
    test.run("$search=\"A%20B%20C\"").isAllText("$search=\"A%20B%20C\"").isType(UriLexer.SEARCH);
  }

  // ;------------------------------------------------------------------------------
  // ; 4. Expressions
  // ;------------------------------------------------------------------------------
  @Test
  public void testQueryExpressions() {
    test.globalMode(Lexer.DEFAULT_MODE);

    test.run("$it").isText("$it").isType(UriLexer.IT);

    test.run("$filter=contains(").at(2).isText("contains(").isType(UriLexer.CONTAINS_WORD);

    test.run("$filter=containsabc").at(2).isText("containsabc")
    .isType(UriLexer.ODATAIDENTIFIER); // test that this is a ODI

    test.run("$filter=startswith(").at(2).isText("startswith(").isType(UriLexer.STARTSWITH_WORD);
    test.run("$filter=endswith(").at(2).isText("endswith(").isType(UriLexer.ENDSWITH_WORD);
    test.run("$filter=length(").at(2).isText("length(").isType(UriLexer.LENGTH_WORD);
    test.run("$filter=indexof(").at(2).isText("indexof(").isType(UriLexer.INDEXOF_WORD);
    test.run("$filter=substring(").at(2).isText("substring(").isType(UriLexer.SUBSTRING_WORD);
    test.run("$filter=tolower(").at(2).isText("tolower(").isType(UriLexer.TOLOWER_WORD);
    test.run("$filter=toupper(").at(2).isText("toupper(").isType(UriLexer.TOUPPER_WORD);
    test.run("$filter=trim(").at(2).isText("trim(").isType(UriLexer.TRIM_WORD);
    test.run("$filter=concat(").at(2).isText("concat(").isType(UriLexer.CONCAT_WORD);

  }

  // ;------------------------------------------------------------------------------
  // ; 7. Literal Data Values
  // ;------------------------------------------------------------------------------

  @Test
  public void testLiteralDataValues() {
    test.globalMode(Lexer.DEFAULT_MODE);
    // null
    test.run("null").isInput().isType(UriLexer.NULLVALUE);

    // binary
    test.run("binary'ABCD'").isInput().isType(UriLexer.BINARY);
    test.run("BiNaRy'ABCD'").isInput().isType(UriLexer.BINARY);

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

    // Lexer rule GUID
    test.run("1234ABCD-12AB-23CD-45EF-123456780ABC").isInput().isType(UriLexer.GUID);
    test.run("1234ABCD-12AB-23CD-45EF-123456780ABC").isInput().isType(UriLexer.GUID);

    // Lexer rule DATE
    test.run("2013-11-15").isInput().isType(UriLexer.DATE);

    // Lexer rule DATETIMEOFFSET
    test.run("2013-11-15T13:35Z").isInput().isType(UriLexer.DATETIMEOFFSET);
    test.run("2013-11-15T13:35:10Z").isInput().isType(UriLexer.DATETIMEOFFSET);
    test.run("2013-11-15T13:35:10.1234Z").isInput().isType(UriLexer.DATETIMEOFFSET);

    test.run("2013-11-15T13:35:10.1234+01:30").isInput().isType(UriLexer.DATETIMEOFFSET);
    test.run("2013-11-15T13:35:10.1234-01:12").isInput().isType(UriLexer.DATETIMEOFFSET);

    test.run("2013-11-15T13:35Z").isInput().isType(UriLexer.DATETIMEOFFSET);

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

    test.run("20:00").isInput().isType(UriLexer.TIMEOFDAY);
    test.run("20:15:01").isInput().isType(UriLexer.TIMEOFDAY);
    test.run("20:15:01.02").isInput().isType(UriLexer.TIMEOFDAY);

    test.run("20:15:01.02").isInput().isType(UriLexer.TIMEOFDAY);

    // String
    test.run("'ABC'").isText("'ABC'").isType(UriLexer.STRING);
    test.run("'A%20C'").isInput().isType(UriLexer.STRING);
    test.run("'%20%20%20ABC'").isInput().isType(UriLexer.STRING);

  }

  @Test
  public void testDelims() {
    String reserved = "/";
    test.globalMode(UriLexer.MODE_QUERY);
    // Test lexer rule UNRESERVED
    test.run("$format=A/" + cUNRESERVED).isAllInput().isType(UriLexer.FORMAT);
    test.run("$format=A/" + cUNRESERVED + reserved).isType(UriLexer.FORMAT).at(4).isText(cUNRESERVED);
    // Test lexer rule PCT_ENCODED
    test.run("$format=A/" + cPCT_ENCODED).isAllInput().isType(UriLexer.FORMAT);
    test.run("$format=A/" + cPCT_ENCODED + reserved).isType(UriLexer.FORMAT).at(4).isText(cPCT_ENCODED);
    // Test lexer rule SUB_DELIMS
    test.run("$format=A/" + cSUB_DELIMS).isAllInput().isType(UriLexer.FORMAT);
    test.run("$format=A/" + cSUB_DELIMS + reserved).isType(UriLexer.FORMAT).at(4).isText("$");
    // Test lexer rule PCHAR rest
    test.run("$format=A/:@").isAllText("$format=A/:@").isType(UriLexer.FORMAT);
    test.run("$format=A/:@" + reserved).isType(UriLexer.FORMAT).at(4).isText(":@");
    // Test lexer rule PCHAR all
    test.run("$format=" + cPCHAR + "/" + cPCHAR).isAllInput().isType(UriLexer.FORMAT);

  }

}
