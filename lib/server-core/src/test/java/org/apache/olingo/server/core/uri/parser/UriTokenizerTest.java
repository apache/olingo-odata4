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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.junit.Test;

public class UriTokenizerTest {

  @Test
  public void nullOK() {
    assertFalse(new UriTokenizer(null).next(null));
    assertTrue(new UriTokenizer(null).next(TokenKind.EOF));
  }

  @Test
  public void constants() {
    final UriTokenizer tokenizer = new UriTokenizer("$ref");
    assertTrue(tokenizer.next(TokenKind.REF));
    assertEquals("$ref", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.EOF));
    assertTrue(tokenizer.next(TokenKind.EOF));

    assertTrue(new UriTokenizer("$value").next(TokenKind.VALUE));
    assertTrue(new UriTokenizer("$count").next(TokenKind.COUNT));
    assertTrue(new UriTokenizer("$crossjoin").next(TokenKind.CROSSJOIN));
    assertTrue(new UriTokenizer("$root").next(TokenKind.ROOT));
    assertTrue(new UriTokenizer("$it").next(TokenKind.IT));
    assertTrue(new UriTokenizer("null").next(TokenKind.NULL));

    wrongToken(TokenKind.REF, "$ref", 'x');
  }

  @Test
  public void sequence() {
    UriTokenizer tokenizer = new UriTokenizer("(A=1,B=2);.*/+-");
    assertTrue(tokenizer.next(TokenKind.OPEN));
    assertFalse(tokenizer.next(TokenKind.OPEN));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals("A", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertEquals("1", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.COMMA));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals("B", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertEquals("2", tokenizer.getText());
    assertFalse(tokenizer.next(TokenKind.EOF));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.SEMI));
    assertTrue(tokenizer.next(TokenKind.DOT));
    assertTrue(tokenizer.next(TokenKind.STAR));
    assertTrue(tokenizer.next(TokenKind.SLASH));
    assertTrue(tokenizer.next(TokenKind.PLUS));
    assertTrue(tokenizer.next(TokenKind.MinusOperator));
    assertTrue(tokenizer.next(TokenKind.EOF));

    tokenizer = new UriTokenizer("any(a:true) or all(b:false)");
    assertTrue(tokenizer.next(TokenKind.ANY));
    assertTrue(tokenizer.next(TokenKind.OPEN));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.COLON));
    assertTrue(tokenizer.next(TokenKind.BooleanValue));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.OrOperator));
    assertTrue(tokenizer.next(TokenKind.ALL));
    assertTrue(tokenizer.next(TokenKind.OPEN));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.COLON));
    assertTrue(tokenizer.next(TokenKind.BooleanValue));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.EOF));
  }

  @Test
  public void saveState() {
    UriTokenizer tokenizer = new UriTokenizer("a*");
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    tokenizer.saveState();
    assertTrue(tokenizer.next(TokenKind.STAR));
    assertTrue(tokenizer.next(TokenKind.EOF));
    tokenizer.returnToSavedState();
    assertTrue(tokenizer.next(TokenKind.STAR));
    assertTrue(tokenizer.next(TokenKind.EOF));
  }

  @Test
  public void systemQueryOptions() {
    UriTokenizer tokenizer = new UriTokenizer("$expand=*;$filter=true;$levels=max;$orderby=false");
    assertTrue(tokenizer.next(TokenKind.EXPAND));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.STAR));
    assertTrue(tokenizer.next(TokenKind.SEMI));
    assertTrue(tokenizer.next(TokenKind.FILTER));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.BooleanValue));
    assertTrue(tokenizer.next(TokenKind.SEMI));
    assertTrue(tokenizer.next(TokenKind.LEVELS));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.MAX));
    assertTrue(tokenizer.next(TokenKind.SEMI));
    assertTrue(tokenizer.next(TokenKind.ORDERBY));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.BooleanValue));
    assertTrue(tokenizer.next(TokenKind.EOF));

    tokenizer = new UriTokenizer("$search=A;$select=*;$skip=1;$top=2");
    assertTrue(tokenizer.next(TokenKind.SEARCH));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.SEMI));
    assertTrue(tokenizer.next(TokenKind.SELECT));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.STAR));
    assertTrue(tokenizer.next(TokenKind.SEMI));
    assertTrue(tokenizer.next(TokenKind.SKIP));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.SEMI));
    assertTrue(tokenizer.next(TokenKind.TOP));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.EOF));
  }

  @Test
  public void identifier() {
    assertTrue(new UriTokenizer("name").next(TokenKind.ODataIdentifier));
    assertTrue(new UriTokenizer("_name").next(TokenKind.ODataIdentifier));
    assertFalse(new UriTokenizer("1name").next(TokenKind.ODataIdentifier));
    assertFalse(new UriTokenizer("").next(TokenKind.ODataIdentifier));

    final String outsideBmpLetter = String.valueOf(Character.toChars(0x10330));
    UriTokenizer tokenizer = new UriTokenizer(
        outsideBmpLetter + "name1\u0300a\u0600b\uFE4F" + outsideBmpLetter + "end\b");
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals(outsideBmpLetter + "name1\u0300a\u0600b\uFE4F" + outsideBmpLetter + "end", tokenizer.getText());

    // Identifiers consist of up to 128 characters.  Check that the identifier does not have more characters.
    final String name = "Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch"; // Do you know this village?
    tokenizer = new UriTokenizer(name + '_' + name + "_0123456789X");
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals(name + '_' + name + "_0123456789", tokenizer.getText());
    tokenizer.next(TokenKind.ODataIdentifier);
    assertEquals("X", tokenizer.getText());

    wrongToken(TokenKind.ODataIdentifier, "_", '.');
    wrongToken(TokenKind.ODataIdentifier, "_", ',');
  }

  @Test
  public void qualifiedName() {
    assertTrue(new UriTokenizer("namespace.name").next(TokenKind.QualifiedName));

    final UriTokenizer tokenizer = new UriTokenizer("multi.part.namespace.name.1");
    assertTrue(tokenizer.next(TokenKind.QualifiedName));
    assertTrue(tokenizer.next(TokenKind.DOT));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.EOF));

    assertFalse(new UriTokenizer("name").next(TokenKind.QualifiedName));
    assertFalse(new UriTokenizer("namespace..name").next(TokenKind.QualifiedName));
    assertFalse(new UriTokenizer("").next(TokenKind.QualifiedName));
    wrongToken(TokenKind.QualifiedName, "namespace._", ',');
  }

  @Test
  public void alias() {
    assertTrue(new UriTokenizer("@name").next(TokenKind.ParameterAliasName));
    assertTrue(new UriTokenizer("@_name").next(TokenKind.ParameterAliasName));
    assertFalse(new UriTokenizer("name").next(TokenKind.ParameterAliasName));
    assertFalse(new UriTokenizer("@").next(TokenKind.ParameterAliasName));
    assertFalse(new UriTokenizer("@1").next(TokenKind.ParameterAliasName));
  }

  @Test
  public void booleanValue() {
    assertTrue(new UriTokenizer("true").next(TokenKind.BooleanValue));
    assertTrue(new UriTokenizer("tRuE").next(TokenKind.BooleanValue));
    assertTrue(new UriTokenizer("false").next(TokenKind.BooleanValue));
    assertTrue(new UriTokenizer("False").next(TokenKind.BooleanValue));

    wrongToken(TokenKind.BooleanValue, "true", 'x');
  }

  @Test
  public void string() {
    assertTrue(new UriTokenizer("'ABC'").next(TokenKind.StringValue));
    assertTrue(new UriTokenizer("'€\uFDFC'").next(TokenKind.StringValue));
    assertTrue(new UriTokenizer('\'' + String.valueOf(Character.toChars(0x1F603)) + '\'')
        .next(TokenKind.StringValue));

    final UriTokenizer tokenizer = new UriTokenizer("'AB''''C'''D");
    assertTrue(tokenizer.next(TokenKind.StringValue));
    assertEquals("'AB''''C'''", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals("D", tokenizer.getText());

    assertFalse(new UriTokenizer("A").next(TokenKind.StringValue));
    assertFalse(new UriTokenizer("'A").next(TokenKind.StringValue));
  }

  @Test
  public void integer() {
    assertTrue(new UriTokenizer("1").next(TokenKind.IntegerValue));
    assertTrue(new UriTokenizer("1.").next(TokenKind.IntegerValue));
    assertFalse(new UriTokenizer(".1").next(TokenKind.IntegerValue));
    assertTrue(new UriTokenizer("-1").next(TokenKind.IntegerValue));
    assertTrue(new UriTokenizer("1234567890").next(TokenKind.IntegerValue));
  }

  @Test
  public void guid() {
    assertTrue(new UriTokenizer("12345678-abcd-ef12-1234-567890ABCDEF").next(TokenKind.GuidValue));
    wrongToken(TokenKind.GuidValue, "12345678-1234-1234-1234-123456789ABC", 'G');
  }

  @Test
  public void date() {
    assertTrue(new UriTokenizer("12345-12-25").next(TokenKind.DateValue));
    assertTrue(new UriTokenizer("-0001-12-24").next(TokenKind.DateValue));
    assertFalse(new UriTokenizer("1234-13-01").next(TokenKind.DateValue));
    assertFalse(new UriTokenizer("1234-12-32").next(TokenKind.DateValue));
    assertFalse(new UriTokenizer("123-01-01").next(TokenKind.DateValue));
    assertFalse(new UriTokenizer("1234-00-01").next(TokenKind.DateValue));
    assertFalse(new UriTokenizer("1234-01-00").next(TokenKind.DateValue));
    wrongToken(TokenKind.DateValue, "2000-12-29", 'A');
    wrongToken(TokenKind.DateValue, "0001-01-01", 'A');
    wrongToken(TokenKind.DateValue, "-12345-01-31", 'A');
  }

  @Test
  public void dateTimeOffset() {
    assertTrue(new UriTokenizer("1234-12-25T11:12:13.456Z").next(TokenKind.DateTimeOffsetValue));
    assertTrue(new UriTokenizer("-1234-12-25t01:12z").next(TokenKind.DateTimeOffsetValue));
    assertTrue(new UriTokenizer("-1234-12-25T21:22:23+01:00").next(TokenKind.DateTimeOffsetValue));
    assertTrue(new UriTokenizer("1234-12-25T11:12:13-00:30").next(TokenKind.DateTimeOffsetValue));
    assertFalse(new UriTokenizer("1234-10-01").next(TokenKind.DateTimeOffsetValue));
    wrongToken(TokenKind.DateTimeOffsetValue, "-1234-12-25T11:12:13.456+01:00", 'P');
  }

  @Test
  public void timeOfDay() {
    assertTrue(new UriTokenizer("11:12:13").next(TokenKind.TimeOfDayValue));
    assertTrue(new UriTokenizer("11:12:13.456").next(TokenKind.TimeOfDayValue));
    assertFalse(new UriTokenizer("24:00:00").next(TokenKind.TimeOfDayValue));
    assertFalse(new UriTokenizer("01:60:00").next(TokenKind.TimeOfDayValue));
    assertFalse(new UriTokenizer("01:00:60").next(TokenKind.TimeOfDayValue));
    assertFalse(new UriTokenizer("01:00:00.").next(TokenKind.TimeOfDayValue));
    assertFalse(new UriTokenizer("0:02:03").next(TokenKind.TimeOfDayValue));
    assertFalse(new UriTokenizer("01:0:03").next(TokenKind.TimeOfDayValue));
    assertFalse(new UriTokenizer("01:02:0").next(TokenKind.TimeOfDayValue));
    wrongToken(TokenKind.TimeOfDayValue, "11:12", '-');
  }

  @Test
  public void decimal() {
    assertTrue(new UriTokenizer("1.2").next(TokenKind.DecimalValue));
    assertFalse(new UriTokenizer(".1").next(TokenKind.DecimalValue));
    assertTrue(new UriTokenizer("-12.34").next(TokenKind.DecimalValue));
    assertTrue(new UriTokenizer("1234567890.0123456789").next(TokenKind.DecimalValue));
    assertFalse(new UriTokenizer("0,1").next(TokenKind.DecimalValue));
    assertFalse(new UriTokenizer("0..1").next(TokenKind.DecimalValue));
  }

  @Test
  public void doubleValue() {
    assertTrue(new UriTokenizer("NaN").next(TokenKind.DoubleValue));
    assertTrue(new UriTokenizer("-INF").next(TokenKind.DoubleValue));
    assertTrue(new UriTokenizer("INF").next(TokenKind.DoubleValue));
    assertFalse(new UriTokenizer("inf").next(TokenKind.DoubleValue));
    assertTrue(new UriTokenizer("1.2E3").next(TokenKind.DoubleValue));
    assertTrue(new UriTokenizer("-12.34e-05").next(TokenKind.DoubleValue));
    assertTrue(new UriTokenizer("1E2").next(TokenKind.DoubleValue));
    assertFalse(new UriTokenizer("1.E2").next(TokenKind.DoubleValue));
    wrongToken(TokenKind.DoubleValue, "-12.34E+5", 'i');
  }

  @Test
  public void duration() {
    assertTrue(new UriTokenizer("duration'P'").next(TokenKind.DurationValue));
    assertTrue(new UriTokenizer("DURATION'P1D'").next(TokenKind.DurationValue));
    assertTrue(new UriTokenizer("duration'PT'").next(TokenKind.DurationValue));
    assertTrue(new UriTokenizer("duration'PT1H'").next(TokenKind.DurationValue));
    assertTrue(new UriTokenizer("duration'pt1M'").next(TokenKind.DurationValue));
    assertTrue(new UriTokenizer("duration'PT1S'").next(TokenKind.DurationValue));
    assertTrue(new UriTokenizer("duration'PT1.2s'").next(TokenKind.DurationValue));
    assertTrue(new UriTokenizer("duration'-p1dt2h3m4.5s'").next(TokenKind.DurationValue));
    assertFalse(new UriTokenizer("-p1dt2h3m4.5s").next(TokenKind.DurationValue));
    assertFalse(new UriTokenizer("duration'-p1dt2h3m4.5s").next(TokenKind.DurationValue));
    assertFalse(new UriTokenizer("duration'2h3m4s'").next(TokenKind.DurationValue));
    wrongToken(TokenKind.DurationValue, "duration'P1DT2H3M4.5S'", ':');
  }

  @Test
  public void binary() {
    assertTrue(new UriTokenizer("binary''").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("Binary'bm93'").next(TokenKind.BinaryValue));

    // all cases with three base64 characters (and one fill character) at the end
    assertTrue(new UriTokenizer("binary'QUA='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUE='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUI='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUM='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUQ='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUU='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUY='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUc='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUg='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUk='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUo='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUs='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QUw='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QU0='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QU4='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'QU8='").next(TokenKind.BinaryValue));
    assertFalse(new UriTokenizer("binary'QUB='").next(TokenKind.BinaryValue));

    // all cases with two base64 characters (and two fill characters) at the end
    assertTrue(new UriTokenizer("BINARY'VGVzdA=='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'U-RnZQ=='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'Yg=='").next(TokenKind.BinaryValue));
    assertTrue(new UriTokenizer("binary'Yw=='").next(TokenKind.BinaryValue));

    // without optional fill character
    assertTrue(new UriTokenizer("binary'T0RhdGE'").next(TokenKind.BinaryValue));

    // special character '_' (the other, '-', already has been used above)
    assertTrue(new UriTokenizer("binary'V_ZydGVy'").next(TokenKind.BinaryValue));

    wrongToken(TokenKind.BinaryValue, "binary'VGVzdA=='", '+');
  }

  @Test
  public void enumValue() {
    assertTrue(new UriTokenizer("namespace.name'value'").next(TokenKind.EnumValue));
    assertTrue(new UriTokenizer("namespace.name'flag1,flag2,-3'").next(TokenKind.EnumValue));
    assertFalse(new UriTokenizer("namespace.name'1flag'").next(TokenKind.EnumValue));
    assertFalse(new UriTokenizer("namespace.name'flag1,,flag2'").next(TokenKind.EnumValue));
    assertFalse(new UriTokenizer("namespace.name',value'").next(TokenKind.EnumValue));
    assertFalse(new UriTokenizer("namespace.name'value,'").next(TokenKind.EnumValue));
    assertFalse(new UriTokenizer("namespace.name''").next(TokenKind.EnumValue));
    assertFalse(new UriTokenizer("'1'").next(TokenKind.EnumValue));
    assertFalse(new UriTokenizer("1").next(TokenKind.EnumValue));
    wrongToken(TokenKind.EnumValue, "namespace.name'_1,_2,3'", ';');
  }

  @Test
  public void json() {
    // Empty string or JSON values are not allowed.
    assertFalse(new UriTokenizer("").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("1").next(TokenKind.jsonArrayOrObject));

    // object with values
    assertTrue(new UriTokenizer("{}").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("{\"name\":0}").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("{\"name\":true}").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("{\"name\":false}").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("{\"name\":null}").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("{\"name\":\"value\"}").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("{\"name\":\"value\",\"name2\":null}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{\"name\"}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{\"name\":}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{0}").next(TokenKind.jsonArrayOrObject));

    // array with values
    assertTrue(new UriTokenizer("[]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[1]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[true]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[false]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[null]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[\"value\"]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[\"\"]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[\"\\b\\t\\f\\r\\nn\\/\\\\x\\uFE4Fu\\\"\"]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[1,2.0,3.4E5]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("[\"value\",null]").next(TokenKind.jsonArrayOrObject));

    // nesting
    assertTrue(new UriTokenizer("[{\"name\":\"value\"},{\"name\":\"value2\"}]").next(TokenKind.jsonArrayOrObject));
    assertTrue(new UriTokenizer("{\"name\":{\"name2\":\"value\"}}").next(TokenKind.jsonArrayOrObject));

    // unbalanced opening and closing
    assertFalse(new UriTokenizer("{").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{]").next(TokenKind.jsonArrayOrObject));

    // missing values
    assertFalse(new UriTokenizer("[1,]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[,1]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[1,,2]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[1,x]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[+\"x\"]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{\"name\":1,}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{,\"name\":1}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{\"name\":1,,\"name2\":2}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{\"name\":1,x}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{\"name\":1,\"name2\"}").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("{\"name\":1,\"name2\":}").next(TokenKind.jsonArrayOrObject));

    // wrong JSON strings
    assertFalse(new UriTokenizer("[\"a").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[\"a]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[\"a\"\"]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[\"\\x\"]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[\"\\ux\"]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[\"\\u1\"]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[\"\\u12x\"]").next(TokenKind.jsonArrayOrObject));
    assertFalse(new UriTokenizer("[\"\\u123x\"]").next(TokenKind.jsonArrayOrObject));
    wrongToken(TokenKind.jsonArrayOrObject, "[{\"name\":+123.456},null]", '\\');
  }

  @Test
  public void operators() {
    UriTokenizer tokenizer = new UriTokenizer("1 ne 2");
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertFalse(tokenizer.next(TokenKind.EqualsOperator));
    assertTrue(tokenizer.next(TokenKind.NotEqualsOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.EOF));

    tokenizer = new UriTokenizer("-1ne 2");
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertFalse(tokenizer.next(TokenKind.NotEqualsOperator));

    tokenizer = new UriTokenizer("1 ne-2");
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertFalse(tokenizer.next(TokenKind.NotEqualsOperator));

    tokenizer = new UriTokenizer("1    \tle\t\t\t2");
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.LessThanOrEqualsOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.EOF));

    assertTrue(new UriTokenizer("-x").next(TokenKind.MinusOperator));
    assertFalse(new UriTokenizer("-1").next(TokenKind.MinusOperator));
    assertFalse(new UriTokenizer("-INF").next(TokenKind.MinusOperator));
    assertFalse(new UriTokenizer("+").next(TokenKind.MinusOperator));

    assertFalse(new UriTokenizer("nottrue").next(TokenKind.NotOperator));
    assertFalse(new UriTokenizer("no true").next(TokenKind.NotOperator));

    tokenizer = new UriTokenizer("true or not false and 1 eq 2 add 3 sub 4 mul 5 div 6 mod 7");
    assertTrue(tokenizer.next(TokenKind.BooleanValue));
    assertTrue(tokenizer.next(TokenKind.OrOperator));
    assertTrue(tokenizer.next(TokenKind.NotOperator));
    assertTrue(tokenizer.next(TokenKind.BooleanValue));
    assertTrue(tokenizer.next(TokenKind.AndOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.EqualsOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.AddOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.SubOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.MulOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.DivOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.ModOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.EOF));

    tokenizer = new UriTokenizer("1 gt 2 or 3 ge 4 or 5 lt 6 or 7 has namespace.name'flag1,flag2'");
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.GreaterThanOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.OrOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.GreaterThanOrEqualsOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.OrOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.LessThanOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.OrOperator));
    assertTrue(tokenizer.next(TokenKind.IntegerValue));
    assertTrue(tokenizer.next(TokenKind.HasOperator));
    assertTrue(tokenizer.next(TokenKind.EnumValue));
    assertTrue(tokenizer.next(TokenKind.EOF));
  }

  @Test
  public void methods() {
    UriTokenizer tokenizer = new UriTokenizer("now()");
    assertTrue(tokenizer.next(TokenKind.NowMethod));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.EOF));

    assertFalse(new UriTokenizer("no w()").next(TokenKind.NowMethod));
    assertFalse(new UriTokenizer("now ()").next(TokenKind.NowMethod));

    assertTrue(new UriTokenizer("maxdatetime()").next(TokenKind.MaxdatetimeMethod));
    assertTrue(new UriTokenizer("mindatetime()").next(TokenKind.MindatetimeMethod));

    for (final TokenKind tokenKind : TokenKind.values()) {
      if (tokenKind.name().endsWith("Method")) {
        assertTrue(tokenKind.name(),
            new UriTokenizer(
                tokenKind.name().substring(0, tokenKind.name().indexOf("Method"))
                    .toLowerCase(Locale.ROOT).replace("geo", "geo.") + '(')
                .next(tokenKind));
      }
    }
  }

  @Test
  public void suffixes() {
    UriTokenizer tokenizer = new UriTokenizer("p1 asc,p2 desc");
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.AscSuffix));
    assertTrue(tokenizer.next(TokenKind.COMMA));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.DescSuffix));
    assertTrue(tokenizer.next(TokenKind.EOF));

    wrongToken(TokenKind.DescSuffix, " desc", 'D');
  }

  @Test
  public void search() {
    UriTokenizer tokenizer = new UriTokenizer("a AND b OR NOT \"c\" d");
    assertTrue(tokenizer.next(TokenKind.Word));
    assertTrue(tokenizer.next(TokenKind.AndOperatorSearch));
    assertTrue(tokenizer.next(TokenKind.Word));
    assertFalse(tokenizer.next(TokenKind.AndOperatorSearch));
    assertTrue(tokenizer.next(TokenKind.OrOperatorSearch));
    assertTrue(tokenizer.next(TokenKind.NotOperatorSearch));
    assertTrue(tokenizer.next(TokenKind.Phrase));
    assertTrue(tokenizer.next(TokenKind.AndOperatorSearch));
    assertTrue(tokenizer.next(TokenKind.Word));
    assertFalse(tokenizer.next(TokenKind.AndOperatorSearch));
    assertFalse(tokenizer.next(TokenKind.Word));
    assertFalse(tokenizer.next(TokenKind.Phrase));
    assertTrue(tokenizer.next(TokenKind.EOF));

    assertTrue(new UriTokenizer("\"a\\\\x\\\"\"").next(TokenKind.Phrase));
    assertFalse(new UriTokenizer("\"a\\\"").next(TokenKind.Phrase));
    assertFalse(new UriTokenizer("\"a\\x\"").next(TokenKind.Phrase));
    wrongToken(TokenKind.Phrase, "\"a\"", '\\');

    final String outsideBmpLetter = String.valueOf(Character.toChars(0x10330));
    assertTrue(new UriTokenizer("\"" + outsideBmpLetter + "\"").next(TokenKind.Phrase));

    assertTrue(new UriTokenizer(outsideBmpLetter).next(TokenKind.Word));
    assertFalse(new UriTokenizer("1").next(TokenKind.Word));
    assertFalse(new UriTokenizer("AND").next(TokenKind.Word));
    assertFalse(new UriTokenizer("OR").next(TokenKind.Word));
    assertFalse(new UriTokenizer("NOT").next(TokenKind.Word));
  }

  @Test
  public void geoPoint() {
    assertTrue(new UriTokenizer("geography'SRID=4326;Point(1.23 4.56)'").next(TokenKind.GeographyPoint));
    assertTrue(new UriTokenizer("GeOgRaPhY'SrId=4326;pOiNt(1 2)'").next(TokenKind.GeographyPoint));
    assertTrue(new UriTokenizer("geography'srid=4326;point(1.2E3 4.5E-6)'").next(TokenKind.GeographyPoint));
    wrongToken(TokenKind.GeographyPoint, "geography'SRID=4326;Point(1.23 4.56)'", 'x');

    assertTrue(new UriTokenizer("geometry'SRID=0;Point(1 2)'").next(TokenKind.GeometryPoint));
    assertFalse(new UriTokenizer("geometry'SRID=123456;Point(1 2)'").next(TokenKind.GeometryPoint));
    assertFalse(new UriTokenizer("geometry'SRID=123456;Point(1)'").next(TokenKind.GeometryPoint));
    wrongToken(TokenKind.GeometryPoint, "geometry'SRID=0;Point(1.23 4.56)'", ',');
  }

  @Test
  public void geoLineString() {
    assertTrue(new UriTokenizer("geography'SRID=4326;LineString(1.23 4.56,7 8)'")
        .next(TokenKind.GeographyLineString));
    wrongToken(TokenKind.GeographyLineString, "geography'SRID=4326;LineString(1.23 4.56,7 8)'", '{');

    assertTrue(new UriTokenizer("geometry'SRID=0;LineString(1 2,3 4,5 6,7 8)'")
        .next(TokenKind.GeometryLineString));
    wrongToken(TokenKind.GeometryLineString, "geometry'SRID=0;LineString(1 2,3 4,5 6,7 8)'", '.');
  }

  @Test
  public void geoPolygon() {
    assertTrue(new UriTokenizer("geography'SRID=4326;Polygon((0 0,1 0,0 1,0 0))'").next(TokenKind.GeographyPolygon));
    assertTrue(new UriTokenizer("geometry'SRID=0;Polygon((0 0,1 0,0 1,0 0))'").next(TokenKind.GeometryPolygon));
    assertTrue(new UriTokenizer("geometry'SRID=0;Polygon((1 1,2 1,2 2,1 2,1 1),(0 0,4 0,4 4,0 4,0 0))'")
    	.next(TokenKind.GeometryPolygon));
    assertTrue(new UriTokenizer(
    	"geometry'SRID=0;Polygon((0 0,1 1,2 2,0 0),(1 1,2 1,2 2,1 2,1 1),(0 0,4 0,4 4,0 4,0 0))'")
    	.next(TokenKind.GeometryPolygon));
    wrongToken(TokenKind.GeometryPolygon,
        "geometry'SRID=0;Polygon((0 0,4 0,4 4,0 4,0 0),(1 1,2 1,2 2,1 2,1 1))'",
        'x');
  }

  @Test
  public void geoMultiPoint() {
    assertTrue(new UriTokenizer("geography'SRID=4326;MultiPoint()'").next(TokenKind.GeographyMultiPoint));
    assertTrue(new UriTokenizer("geography'SRID=4326;MultiPoint((0 0))'").next(TokenKind.GeographyMultiPoint));
    assertTrue(new UriTokenizer("geometry'SRID=0;MultiPoint((0 0),(1 1))'").next(TokenKind.GeometryMultiPoint));
    wrongToken(TokenKind.GeometryMultiPoint, "geometry'SRID=0;MultiPoint((0 0),(1 1),(2.3 4.5))'", 'x');
  }

  @Test
  public void geoMultiLineString() {
    assertTrue(new UriTokenizer("geography'SRID=4326;MultiLineString()'").next(TokenKind.GeographyMultiLineString));
    assertTrue(new UriTokenizer("geography'SRID=4326;MultiLineString((1 2,3 4))'")
        .next(TokenKind.GeographyMultiLineString));

    wrongToken(TokenKind.GeometryMultiLineString,
        "geometry'SRID=0;MultiLineString((1.23 4.56,7 8),(0 0,1 1),(2 2,3 3))'",
        '}');
  }

  @Test
  public void geoMultiPolygon() {
    assertTrue(new UriTokenizer("geography'SRID=4326;MultiPolygon()'").next(TokenKind.GeographyMultiPolygon));
    assertTrue(new UriTokenizer("geography'SRID=4326;MultiPolygon(((0 0,1 0,0 1,0 0)))'")
        .next(TokenKind.GeographyMultiPolygon));

    wrongToken(TokenKind.GeometryMultiPolygon,
        "geometry'SRID=0;MultiPolygon(((0 0,4 0,4 4,0 4,0 0),(1 1,2 1,2 2,1 2,1 1)),"
            + "((0 0,40 0,40 40,0 40,0 0),(10 10,20 10,20 20,10 20,10 10)))'",
        'x');
  }

  @Test
  public void geoCollection() {
    assertTrue(new UriTokenizer("geography'SRID=4326;Collection(Point(1 2))'").next(TokenKind.GeographyCollection));
    assertTrue(new UriTokenizer("geography'SRID=4326;Collection(Collection(Point(1 2),Point(3 4)))'")
        .next(TokenKind.GeographyCollection));
    assertTrue(new UriTokenizer("geography'SRID=4326;Collection(LineString(1 2,3 4))'")
        .next(TokenKind.GeographyCollection));
    assertTrue(new UriTokenizer("geography'SRID=4326;Collection(Polygon((0 0,1 0,0 1,0 0)))'")
        .next(TokenKind.GeographyCollection));
    assertTrue(new UriTokenizer("geography'SRID=4326;Collection(MultiPoint(),MultiLineString(),MultiPolygon())'")
        .next(TokenKind.GeographyCollection));

    wrongToken(TokenKind.GeometryCollection, "geometry'SRID=0;Collection(Point(1 2),Point(3 4))'", 'x');
  }

  @Test
  public void aggregation() {
    UriTokenizer tokenizer = new UriTokenizer("$apply=aggregate(a with sum as s from x with average)");
    assertTrue(tokenizer.next(TokenKind.APPLY));
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.AggregateTrafo));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.WithOperator));
    assertTrue(tokenizer.next(TokenKind.SUM));
    assertTrue(tokenizer.next(TokenKind.AsOperator));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.FromOperator));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.WithOperator));
    assertTrue(tokenizer.next(TokenKind.AVERAGE));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.EOF));

    tokenizer = new UriTokenizer("a with min as m");
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.WithOperator));
    assertTrue(tokenizer.next(TokenKind.MIN));

    tokenizer = new UriTokenizer("a with countdistinct as c");
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.WithOperator));
    assertTrue(tokenizer.next(TokenKind.COUNTDISTINCT));

    assertTrue(new UriTokenizer("identity").next(TokenKind.IDENTITY));
    assertTrue(new UriTokenizer("bottomcount(1,x)").next(TokenKind.BottomCountTrafo));
    assertTrue(new UriTokenizer("bottompercent(1,x)").next(TokenKind.BottomPercentTrafo));
    assertTrue(new UriTokenizer("bottomsum(1,x)").next(TokenKind.BottomSumTrafo));
    assertTrue(new UriTokenizer("topcount(1,x)").next(TokenKind.TopCountTrafo));
    assertTrue(new UriTokenizer("toppercent(1,x)").next(TokenKind.TopPercentTrafo));
    assertTrue(new UriTokenizer("topsum(1,x)").next(TokenKind.TopSumTrafo));
    assertTrue(new UriTokenizer("compute(a mul b as m)").next(TokenKind.ComputeTrafo));

    assertTrue(new UriTokenizer("search(a)").next(TokenKind.SearchTrafo));
    assertTrue(new UriTokenizer("expand(a)").next(TokenKind.ExpandTrafo));
    assertTrue(new UriTokenizer("filter(true)").next(TokenKind.FilterTrafo));

    tokenizer = new UriTokenizer("groupby((rollup($all,x,y)))");
    assertTrue(tokenizer.next(TokenKind.GroupByTrafo));
    assertTrue(tokenizer.next(TokenKind.OPEN));
    assertTrue(tokenizer.next(TokenKind.RollUpSpec));
    assertTrue(tokenizer.next(TokenKind.ROLLUP_ALL));
    assertTrue(tokenizer.next(TokenKind.COMMA));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.COMMA));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.EOF));

    assertTrue(new UriTokenizer("isdefined(x)").next(TokenKind.IsDefinedMethod));
  }

  private void wrongToken(final TokenKind kind, final String value, final char disturbCharacter) {
    assertFalse(new UriTokenizer(disturbCharacter + value).next(kind));

    final UriTokenizer tokenizer = new UriTokenizer(value + disturbCharacter);
    assertTrue(tokenizer.next(kind));
    assertEquals(value, tokenizer.getText());
    assertFalse(tokenizer.next(TokenKind.EOF));

    // Place the disturbing character at every position in the value string
    // and check that this leads to a failed token recognition.
    for (int index = 0; index < value.length(); index++) {
      assertFalse("Error at index " + index,
          new UriTokenizer(value.substring(0, index) + disturbCharacter + value.substring(index + 1)).next(kind));
    }
  }
}
