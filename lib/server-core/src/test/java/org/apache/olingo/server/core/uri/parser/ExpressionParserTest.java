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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.junit.Test;

public class ExpressionParserTest {

  private final OData odata = OData.newInstance();

  @Test
  public void equality() throws Exception {
    Expression expression = parseExpression("5 eq 5");
    assertEquals("{5 EQ 5}", expression.toString());

    expression = parseExpression("5 ne 5");
    assertEquals("{5 NE 5}", expression.toString());

    assertEquals("{1 EQ null}", parseExpression("1 eq null").toString());
    assertEquals("{null NE 2}", parseExpression("null ne 2").toString());
    assertEquals("{null EQ null}", parseExpression("null eq null").toString());

    wrongExpression("5 eq '5'");
  }

  @Test
  public void relational() throws Exception {
    Expression expression = parseExpression("5 gt 5");
    assertEquals("{5 GT 5}", expression.toString());

    expression = parseExpression("5 ge 5");
    assertEquals("{5 GE 5}", expression.toString());

    expression = parseExpression("5 lt 5");
    assertEquals("{5 LT 5}", expression.toString());

    expression = parseExpression("5 le 5");
    assertEquals("{5 LE 5}", expression.toString());

    assertEquals("{5 LE 5.1}", parseExpression("5 le 5.1").toString());

    assertEquals("{1 GT null}", parseExpression("1 gt null").toString());
    assertEquals("{null GE 2}", parseExpression("null ge 2").toString());
    assertEquals("{null LE null}", parseExpression("null le null").toString());

    wrongExpression("5 gt duration'PT5H'");
  }

  @Test
  public void additive() throws Exception {
    Expression expression = parseExpression("5 add 5");
    assertEquals("{5 ADD 5}", expression.toString());

    expression = parseExpression("5 sub 5.1");
    assertEquals("{5 SUB 5.1}", expression.toString());

    expression = parseExpression("2000-02-29 sub 2016-02-29");
    assertEquals("{2000-02-29 SUB 2016-02-29}", expression.toString());

    expression = parseExpression("2000-02-29T00:00:00Z sub 2016-02-29T01:02:03Z");
    assertEquals("{2000-02-29T00:00:00Z SUB 2016-02-29T01:02:03Z}", expression.toString());

    expression = parseExpression("duration'PT1H' add duration'PT1M'");
    assertEquals("{duration'PT1H' ADD duration'PT1M'}", expression.toString());

    expression = parseExpression("2016-01-01 add duration'P60D'");
    assertEquals("{2016-01-01 ADD duration'P60D'}", expression.toString());

    expression = parseExpression("2000-02-29T00:00:00Z add duration'PT12H'");
    assertEquals("{2000-02-29T00:00:00Z ADD duration'PT12H'}", expression.toString());

    assertEquals("{1 ADD null}", parseExpression("1 add null").toString());
    assertEquals("{null ADD 2}", parseExpression("null add 2").toString());
    assertEquals("{null SUB null}", parseExpression("null sub null").toString());

    wrongExpression("1 add '2'");
    wrongExpression("'1' add 2");
    wrongExpression("1 add 2000-02-29");
    wrongExpression("11:12:13 sub 2000-02-29T11:12:13Z");
    wrongExpression("2000-02-29 add 2016-02-29");
    wrongExpression("2000-02-29T00:00:00Z add 2016-02-29T01:02:03Z");
    wrongExpression("2000-02-29T00:00:00Z add 1");
    wrongExpression("2000-02-29 sub 1");
    wrongExpression("duration'P7D' add 2000-02-29");
  }

  @Test
  public void multiplicative() throws Exception {
    Expression expression = parseExpression("5 mul 5");
    assertEquals("{5 MUL 5}", expression.toString());

    expression = parseExpression("5 div 5");
    assertEquals("{5 DIV 5}", expression.toString());

    expression = parseExpression("5 mod 5");
    assertEquals("{5 MOD 5}", expression.toString());

    wrongExpression("1 mod '2'");
  }

  @Test
  public void unary() throws Exception {
    Expression expression = parseExpression("-5");
    assertEquals("-5", expression.toString());

    assertEquals("{MINUS -1}", parseExpression("--1").toString());
    assertEquals("{MINUS duration'PT1M'}", parseExpression("-duration'PT1M'").toString());

    expression = parseExpression("not false");
    assertEquals("{NOT false}", expression.toString());

    wrongExpression("not 11:12:13");
  }

  @Test
  public void grouping() throws Exception {
    Expression expression = parseExpression("-5 add 5");
    assertEquals("{-5 ADD 5}", expression.toString());

    expression = parseExpression("-(5 add 5)");
    assertEquals("{MINUS {5 ADD 5}}", expression.toString());
  }

  @Test
  public void precedence() throws Exception {
    assertEquals("{-1 ADD {2 DIV 3}}", parseExpression("-1 add 2 div 3").toString());
    assertEquals("{true OR {{NOT false} AND true}}", parseExpression("true or not false and true").toString());
  }

  @Test
  public void noParameterMethods() throws Exception {
    Expression expression = parseMethod(TokenKind.NowMethod);
    assertEquals("{now []}", expression.toString());

    expression = parseMethod(TokenKind.MaxdatetimeMethod);
    assertEquals("{maxdatetime []}", expression.toString());

    expression = parseMethod(TokenKind.MindatetimeMethod);
    assertEquals("{mindatetime []}", expression.toString());

    wrongExpression("now(1)");
  }

  @Test
  public void oneParameterMethods() throws Exception {
    final String stringValue = "'abc'";
    final String dateValue = "1234-12-25";
    final String dateTimeOffsetValue = "1234-12-25T11:12:13.456Z";

    Expression expression = parseMethod(TokenKind.LengthMethod, stringValue);
    assertEquals("{length [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.TolowerMethod, stringValue);
    assertEquals("{tolower [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.ToupperMethod, stringValue);
    assertEquals("{toupper [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.TrimMethod, stringValue);
    assertEquals("{trim [" + stringValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.YearMethod, dateValue);
    assertEquals("{year [" + dateValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.MonthMethod, dateValue);
    assertEquals("{month [" + dateValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.DayMethod, dateValue);
    assertEquals("{day [" + dateValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.HourMethod, dateTimeOffsetValue);
    assertEquals("{hour [" + dateTimeOffsetValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.MinuteMethod, dateTimeOffsetValue);
    assertEquals("{minute [" + dateTimeOffsetValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.SecondMethod, dateTimeOffsetValue);
    assertEquals("{second [" + dateTimeOffsetValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.DateMethod, dateTimeOffsetValue);
    assertEquals("{date [" + dateTimeOffsetValue + "]}", expression.toString());

    expression = parseMethod(TokenKind.TotalsecondsMethod, "duration'PT1H'");
    assertEquals("{totalseconds [duration'PT1H']}", expression.toString());

    expression = parseMethod(TokenKind.RoundMethod, "3.141592653589793");
    assertEquals("{round [3.141592653589793]}", expression.toString());

    assertEquals("{hour [null]}", parseMethod(TokenKind.HourMethod, new String[] { null }).toString());

    wrongExpression("trim()");
    wrongExpression("trim(1)");
    wrongExpression("ceiling('1.2')");
  }

  @Test
  public void twoParameterMethods() throws Exception {
    Expression expression = parseMethod(TokenKind.ContainsMethod, "'a'", "'b'");
    assertEquals("{contains ['a', 'b']}", expression.toString());

    expression = parseMethod(TokenKind.EndswithMethod, "'a'", "'b'");
    assertEquals("{endswith ['a', 'b']}", expression.toString());

    expression = parseMethod(TokenKind.StartswithMethod, "'a'", "'b'");
    assertEquals("{startswith ['a', 'b']}", expression.toString());

    expression = parseMethod(TokenKind.IndexofMethod, "'a'", "'b'");
    assertEquals("{indexof ['a', 'b']}", expression.toString());

    expression = parseMethod(TokenKind.ConcatMethod, "'a'", "'b'");
    assertEquals("{concat ['a', 'b']}", expression.toString());

    expression = parseMethod(TokenKind.GeoDistanceMethod,
        "geography'SRID=0;Point(1.2 3.4)'", "geography'SRID=0;Point(5.6 7.8)'");
    assertEquals("{geo.distance [geography'SRID=0;Point(1.2 3.4)', geography'SRID=0;Point(5.6 7.8)']}",
        expression.toString());

    expression = parseMethod(TokenKind.GeoIntersectsMethod,
        "geometry'SRID=0;Point(1.2 3.4)'",
        "geometry'SRID=0;Polygon((0 0,4 0,4 4,0 4,0 0),(1 1,2 1,2 2,1 2,1 1))'");
    assertEquals("{geo.intersects [geometry'SRID=0;Point(1.2 3.4)', "
        + "geometry'SRID=0;Polygon((0 0,4 0,4 4,0 4,0 0),(1 1,2 1,2 2,1 2,1 1))']}",
        expression.toString());

    assertEquals("{startswith [null, 'b']}", parseMethod(TokenKind.StartswithMethod, null, "'b'").toString());
    assertEquals("{indexof ['a', null]}", parseMethod(TokenKind.IndexofMethod, "'a'", null).toString());

    wrongExpression("concat('a')");
    wrongExpression("endswith('a',1)");
}

  @Test
  public void variableParameterNumberMethods() throws Exception {
    Expression expression = parseMethod(TokenKind.SubstringMethod, "'abc'", "1", "2");
    assertEquals("{substring ['abc', 1, 2]}", expression.toString());
    expression = parseMethod(TokenKind.SubstringMethod, "'abc'", "1");
    assertEquals("{substring ['abc', 1]}", expression.toString());

    assertEquals("{cast [Edm.SByte]}", parseMethod(TokenKind.CastMethod, "Edm.SByte").toString());
    assertEquals("{cast [42, Edm.SByte]}", parseMethod(TokenKind.CastMethod, "42", "Edm.SByte").toString());

    assertEquals("{isof [Edm.SByte]}", parseMethod(TokenKind.IsofMethod, "Edm.SByte").toString());
    assertEquals("{isof [42, Edm.SByte]}", parseMethod(TokenKind.IsofMethod, "42", "Edm.SByte").toString());

    wrongExpression("substring('abc')");
    wrongExpression("substring('abc',1,2,3)");
    wrongExpression("substring(1,2)");
    wrongExpression("cast(1,2)");
    wrongExpression("isof(Edm.Int16,2)");
  }

  private Expression parseMethod(TokenKind kind, String... parameters)
      throws UriParserException, UriValidationException {
    String expressionString = kind.name().substring(0, kind.name().indexOf("Method"))
        .toLowerCase(Locale.ROOT).replace("geo", "geo.") + '(';
    boolean first = true;
    for (final String parameter : parameters) {
      if (first) {
        first = false;
      } else {
        expressionString += ',';
      }
      expressionString += parameter;
    }
    expressionString += ')';

    return parseExpression(expressionString);
  }

  private Expression parseExpression(final String expressionString)
      throws UriParserException, UriValidationException {
    UriTokenizer tokenizer = new UriTokenizer(expressionString);
    final Expression expression = new ExpressionParser(mock(Edm.class), odata).parse(tokenizer, null, null);
    assertNotNull(expression);
    assertTrue(tokenizer.next(TokenKind.EOF));
    return expression;
  }

  private void wrongExpression(final String expressionString) {
    try {
      parseExpression(expressionString);
      fail("Expected exception not thrown.");
    } catch (final UriParserException e) {
      assertNotNull(e);
    } catch (final UriValidationException e) {
      assertNotNull(e);
    }
  }
}
