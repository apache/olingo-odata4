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
    assertTrue(new UriTokenizer("null").next(TokenKind.NULL));

    wrongToken(TokenKind.REF, "$ref", 'x');
  }

  @Test
  public void sequence() {
    final UriTokenizer tokenizer = new UriTokenizer("(A=1,B=2);");
    assertTrue(tokenizer.next(TokenKind.OPEN));
    assertFalse(tokenizer.next(TokenKind.OPEN));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals("A", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.PrimitiveIntegerValue));
    assertEquals("1", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.COMMA));
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals("B", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.EQ));
    assertTrue(tokenizer.next(TokenKind.PrimitiveIntegerValue));
    assertEquals("2", tokenizer.getText());
    assertFalse(tokenizer.next(TokenKind.EOF));
    assertTrue(tokenizer.next(TokenKind.CLOSE));
    assertTrue(tokenizer.next(TokenKind.SEMI));
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

    final UriTokenizer tokenizer = new UriTokenizer("multi.part.namespace.name");
    assertTrue(tokenizer.next(TokenKind.QualifiedName));
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
    assertTrue(new UriTokenizer("true").next(TokenKind.PrimitiveBooleanValue));
    assertTrue(new UriTokenizer("tRuE").next(TokenKind.PrimitiveBooleanValue));
    assertTrue(new UriTokenizer("false").next(TokenKind.PrimitiveBooleanValue));
    assertTrue(new UriTokenizer("False").next(TokenKind.PrimitiveBooleanValue));

    wrongToken(TokenKind.PrimitiveBooleanValue, "true", 'x');
  }

  @Test
  public void string() {
    assertTrue(new UriTokenizer("'ABC'").next(TokenKind.PrimitiveStringValue));
    assertTrue(new UriTokenizer("'€\uFDFC'").next(TokenKind.PrimitiveStringValue));
    assertTrue(new UriTokenizer('\'' + String.valueOf(Character.toChars(0x1F603)) + '\'')
        .next(TokenKind.PrimitiveStringValue));

    final UriTokenizer tokenizer = new UriTokenizer("'AB''''C'''D");
    assertTrue(tokenizer.next(TokenKind.PrimitiveStringValue));
    assertEquals("'AB''''C'''", tokenizer.getText());
    assertTrue(tokenizer.next(TokenKind.ODataIdentifier));
    assertEquals("D", tokenizer.getText());

    assertFalse(new UriTokenizer("A").next(TokenKind.PrimitiveStringValue));
    assertFalse(new UriTokenizer("'A").next(TokenKind.PrimitiveStringValue));
  }

  @Test
  public void integer() {
    assertTrue(new UriTokenizer("1").next(TokenKind.PrimitiveIntegerValue));
    assertTrue(new UriTokenizer("1.").next(TokenKind.PrimitiveIntegerValue));
    assertFalse(new UriTokenizer(".1").next(TokenKind.PrimitiveIntegerValue));
    assertTrue(new UriTokenizer("-1").next(TokenKind.PrimitiveIntegerValue));
    assertTrue(new UriTokenizer("1234567890").next(TokenKind.PrimitiveIntegerValue));
  }

  @Test
  public void guid() {
    assertTrue(new UriTokenizer("12345678-abcd-ef12-1234-567890ABCDEF").next(TokenKind.PrimitiveGuidValue));
    wrongToken(TokenKind.PrimitiveGuidValue, "12345678-1234-1234-1234-123456789ABC", 'G');
  }

  @Test
  public void date() {
    assertTrue(new UriTokenizer("12345-12-25").next(TokenKind.PrimitiveDateValue));
    assertTrue(new UriTokenizer("-0001-12-24").next(TokenKind.PrimitiveDateValue));
    assertFalse(new UriTokenizer("1234-13-01").next(TokenKind.PrimitiveDateValue));
    assertFalse(new UriTokenizer("1234-12-32").next(TokenKind.PrimitiveDateValue));
    assertFalse(new UriTokenizer("123-01-01").next(TokenKind.PrimitiveDateValue));
    assertFalse(new UriTokenizer("1234-00-01").next(TokenKind.PrimitiveDateValue));
    assertFalse(new UriTokenizer("1234-01-00").next(TokenKind.PrimitiveDateValue));
    wrongToken(TokenKind.PrimitiveDateValue, "2000-12-29", 'A');
    wrongToken(TokenKind.PrimitiveDateValue, "0001-01-01", 'A');
    wrongToken(TokenKind.PrimitiveDateValue, "-12345-01-31", 'A');
  }

  @Test
  public void dateTimeOffset() {
    assertTrue(new UriTokenizer("1234-12-25T11:12:13.456Z").next(TokenKind.PrimitiveDateTimeOffsetValue));
    assertTrue(new UriTokenizer("-1234-12-25t01:12z").next(TokenKind.PrimitiveDateTimeOffsetValue));
    assertTrue(new UriTokenizer("-1234-12-25T21:22:23+01:00").next(TokenKind.PrimitiveDateTimeOffsetValue));
    assertTrue(new UriTokenizer("1234-12-25T11:12:13-00:30").next(TokenKind.PrimitiveDateTimeOffsetValue));
    assertFalse(new UriTokenizer("1234-10-01").next(TokenKind.PrimitiveDateTimeOffsetValue));
    wrongToken(TokenKind.PrimitiveDateTimeOffsetValue, "-1234-12-25T11:12:13.456+01:00", 'P');
  }

  @Test
  public void timeOfDay() {
    assertTrue(new UriTokenizer("11:12:13").next(TokenKind.PrimitiveTimeOfDayValue));
    assertTrue(new UriTokenizer("11:12:13.456").next(TokenKind.PrimitiveTimeOfDayValue));
    assertFalse(new UriTokenizer("24:00:00").next(TokenKind.PrimitiveTimeOfDayValue));
    assertFalse(new UriTokenizer("01:60:00").next(TokenKind.PrimitiveTimeOfDayValue));
    assertFalse(new UriTokenizer("01:00:60").next(TokenKind.PrimitiveTimeOfDayValue));
    assertFalse(new UriTokenizer("01:00:00.").next(TokenKind.PrimitiveTimeOfDayValue));
    assertFalse(new UriTokenizer("0:02:03").next(TokenKind.PrimitiveTimeOfDayValue));
    assertFalse(new UriTokenizer("01:0:03").next(TokenKind.PrimitiveTimeOfDayValue));
    assertFalse(new UriTokenizer("01:02:0").next(TokenKind.PrimitiveTimeOfDayValue));
    wrongToken(TokenKind.PrimitiveTimeOfDayValue, "11:12", '-');
  }

  @Test
  public void decimal() {
    assertTrue(new UriTokenizer("1.2").next(TokenKind.PrimitiveDecimalValue));
    assertFalse(new UriTokenizer(".1").next(TokenKind.PrimitiveDecimalValue));
    assertTrue(new UriTokenizer("-12.34").next(TokenKind.PrimitiveDecimalValue));
    assertTrue(new UriTokenizer("1234567890.0123456789").next(TokenKind.PrimitiveDecimalValue));
    assertFalse(new UriTokenizer("0,1").next(TokenKind.PrimitiveDecimalValue));
    assertFalse(new UriTokenizer("0..1").next(TokenKind.PrimitiveDecimalValue));
  }

  @Test
  public void doubleValue() {
    assertTrue(new UriTokenizer("NaN").next(TokenKind.PrimitiveDoubleValue));
    assertTrue(new UriTokenizer("-INF").next(TokenKind.PrimitiveDoubleValue));
    assertTrue(new UriTokenizer("INF").next(TokenKind.PrimitiveDoubleValue));
    assertFalse(new UriTokenizer("inf").next(TokenKind.PrimitiveDoubleValue));
    assertTrue(new UriTokenizer("1.2E3").next(TokenKind.PrimitiveDoubleValue));
    assertTrue(new UriTokenizer("-12.34e-05").next(TokenKind.PrimitiveDoubleValue));
    assertTrue(new UriTokenizer("1E2").next(TokenKind.PrimitiveDoubleValue));
    assertFalse(new UriTokenizer("1.E2").next(TokenKind.PrimitiveDoubleValue));
    wrongToken(TokenKind.PrimitiveDoubleValue, "-12.34E+5", 'i');
  }

  @Test
  public void duration() {
    assertTrue(new UriTokenizer("duration'P'").next(TokenKind.PrimitiveDurationValue));
    assertTrue(new UriTokenizer("DURATION'P1D'").next(TokenKind.PrimitiveDurationValue));
    assertTrue(new UriTokenizer("duration'PT'").next(TokenKind.PrimitiveDurationValue));
    assertTrue(new UriTokenizer("duration'PT1H'").next(TokenKind.PrimitiveDurationValue));
    assertTrue(new UriTokenizer("duration'pt1M'").next(TokenKind.PrimitiveDurationValue));
    assertTrue(new UriTokenizer("duration'PT1S'").next(TokenKind.PrimitiveDurationValue));
    assertTrue(new UriTokenizer("duration'PT1.2s'").next(TokenKind.PrimitiveDurationValue));
    assertTrue(new UriTokenizer("duration'-p1dt2h3m4.5s'").next(TokenKind.PrimitiveDurationValue));
    assertFalse(new UriTokenizer("-p1dt2h3m4.5s").next(TokenKind.PrimitiveDurationValue));
    assertFalse(new UriTokenizer("duration'-p1dt2h3m4.5s").next(TokenKind.PrimitiveDurationValue));
    assertFalse(new UriTokenizer("duration'2h3m4s'").next(TokenKind.PrimitiveDurationValue));
    wrongToken(TokenKind.PrimitiveDurationValue, "duration'P1DT2H3M4.5S'", ':');
  }

  @Test
  public void binary() {
    assertTrue(new UriTokenizer("binary''").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("Binary'bm93'").next(TokenKind.PrimitiveBinaryValue));

    // all cases with three base64 characters (and one fill character) at the end
    assertTrue(new UriTokenizer("binary'QUA='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUE='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUI='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUM='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUQ='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUU='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUY='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUc='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUg='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUk='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUo='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUs='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QUw='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QU0='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QU4='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'QU8='").next(TokenKind.PrimitiveBinaryValue));
    assertFalse(new UriTokenizer("binary'QUB='").next(TokenKind.PrimitiveBinaryValue));

    // all cases with two base64 characters (and two fill characters) at the end
    assertTrue(new UriTokenizer("BINARY'VGVzdA=='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'U-RnZQ=='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'Yg=='").next(TokenKind.PrimitiveBinaryValue));
    assertTrue(new UriTokenizer("binary'Yw=='").next(TokenKind.PrimitiveBinaryValue));

    // without optional fill character
    assertTrue(new UriTokenizer("binary'T0RhdGE'").next(TokenKind.PrimitiveBinaryValue));

    // special character '_' (the other, '-', already has been used above)
    assertTrue(new UriTokenizer("binary'V_ZydGVy'").next(TokenKind.PrimitiveBinaryValue));

    wrongToken(TokenKind.PrimitiveBinaryValue, "binary'VGVzdA=='", '+');
  }

  @Test
  public void enumValue() {
    assertTrue(new UriTokenizer("namespace.name'value'").next(TokenKind.PrimitiveEnumValue));
    assertTrue(new UriTokenizer("namespace.name'flag1,flag2,-3'").next(TokenKind.PrimitiveEnumValue));
    assertFalse(new UriTokenizer("namespace.name'1flag'").next(TokenKind.PrimitiveEnumValue));
    assertFalse(new UriTokenizer("namespace.name'flag1,,flag2'").next(TokenKind.PrimitiveEnumValue));
    assertFalse(new UriTokenizer("namespace.name',value'").next(TokenKind.PrimitiveEnumValue));
    assertFalse(new UriTokenizer("namespace.name'value,'").next(TokenKind.PrimitiveEnumValue));
    assertFalse(new UriTokenizer("namespace.name''").next(TokenKind.PrimitiveEnumValue));
    assertFalse(new UriTokenizer("'1'").next(TokenKind.PrimitiveEnumValue));
    assertFalse(new UriTokenizer("1").next(TokenKind.PrimitiveEnumValue));
    wrongToken(TokenKind.PrimitiveEnumValue, "namespace.name'_1,_2,3'", ';');
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
  }

  private void wrongToken(final TokenKind kind, final String value, final char disturbCharacter) {
    assertFalse(new UriTokenizer(disturbCharacter + value).next(kind));

    final UriTokenizer tokenizer = new UriTokenizer(value + disturbCharacter);
    assertTrue(tokenizer.next(kind));
    assertEquals(value, tokenizer.getText());

    // Place the disturbing character at every position in the value string
    // and check that this leads to a failed token recognition.
    for (int index = 0; index < value.length(); index++) {
      assertFalse("Error at index " + index,
          new UriTokenizer(value.substring(0, index) + disturbCharacter + value.substring(index + 1)).next(kind));
    }
  }
}
