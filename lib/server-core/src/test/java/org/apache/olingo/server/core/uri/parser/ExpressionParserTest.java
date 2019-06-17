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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.queryoption.AliasQueryOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.queryoption.AliasQueryOptionImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.junit.Test;
import org.mockito.Mockito;

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
  public void testIntegerTypes() throws Exception {
    Expression expression = parseExpression("5 ne 545678979");
    assertEquals("{5 NE 545678979}", expression.toString());
    
    expression = parseExpression("5456 eq 5456");
    assertEquals("{5456 EQ 5456}", expression.toString());
    
    expression = parseExpression("null ne 54561234567");
    assertEquals("{null NE 54561234567}", expression.toString());
    
    expression = parseExpression("null ne 255");
    assertEquals("{null NE 255}", expression.toString());
    
    expression = parseExpression("123 le 2551234567890000999999");
    assertEquals("{123 LE 2551234567890000999999}", expression.toString());
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
    
    expression = parseExpression("-( 5 add 5\t)");
    assertEquals("{MINUS {5 ADD 5}}", expression.toString());
    
  }

  @Test
  public void precedence() throws Exception {
    assertEquals("{-1 ADD {2 DIV 3}}", parseExpression("-1 add 2 div 3").toString());
    assertEquals("{true OR {{NOT false} AND true}}", parseExpression("true or not false and true").toString());
  }

  @Test
  public void noParameterMethods() throws Exception {
    parseMethod(TokenKind.NowMethod);
    parseMethod(TokenKind.MaxdatetimeMethod);
    parseMethod(TokenKind.MindatetimeMethod);

    wrongExpression("now(1)");
  }

  @Test
  public void oneParameterMethods() throws Exception {
    final String stringValue = "'abc'";
    final String dateValue = "1234-12-25";
    final String dateTimeOffsetValue = "1234-12-25T11:12:13.456Z";

    parseMethod(TokenKind.LengthMethod, stringValue);
    parseMethod(TokenKind.TolowerMethod, stringValue);
    parseMethod(TokenKind.ToupperMethod, stringValue);
    parseMethod(TokenKind.TrimMethod, stringValue);
    parseMethod(TokenKind.YearMethod, dateValue);
    parseMethod(TokenKind.MonthMethod, dateValue);
    parseMethod(TokenKind.DayMethod, dateValue);
    parseMethod(TokenKind.HourMethod, dateTimeOffsetValue);
    parseMethod(TokenKind.MinuteMethod, dateTimeOffsetValue);
    parseMethod(TokenKind.SecondMethod, dateTimeOffsetValue);
    parseMethod(TokenKind.DateMethod, dateTimeOffsetValue);
    parseMethod(TokenKind.TotalsecondsMethod, "duration'PT1H'");
    parseMethod(TokenKind.RoundMethod, "3.141592653589793");
    parseMethod(TokenKind.GeoLengthMethod, "geometry'SRID=0;LineString(0 0,4 0,4 4,0 4,0 0)'");
    parseMethod(TokenKind.HourMethod, new String[] { null });

    wrongExpression("trim()");
    wrongExpression("trim(1)");
    wrongExpression("ceiling('1.2')");
    
    assertEquals("{trim ['abc']}", parseExpression("trim( 'abc' )").toString());
  }

  @Test
  public void twoParameterMethods() throws Exception {
    parseMethod(TokenKind.ContainsMethod, "'a'", "'b'");
    parseMethod(TokenKind.EndswithMethod, "'a'", "'b'");
    parseMethod(TokenKind.StartswithMethod, "'a'", "'b'");
    parseMethod(TokenKind.IndexofMethod, "'a'", "'b'");
    parseMethod(TokenKind.ConcatMethod, "'a'", "'b'");
    parseMethod(TokenKind.GeoDistanceMethod, "geography'SRID=0;Point(1.2 3.4)'", "geography'SRID=0;Point(5.6 7.8)'");
    parseMethod(TokenKind.GeoIntersectsMethod,
        "geometry'SRID=0;Point(1.2 3.4)'",
        "geometry'SRID=0;Polygon((0 0,4 0,4 4,0 4,0 0),(1 1,2 1,2 2,1 2,1 1))'");
    parseMethod(TokenKind.StartswithMethod, null, "'b'");
    parseMethod(TokenKind.IndexofMethod, "'a'", null);

    wrongExpression("concat('a')");
    wrongExpression("endswith('a',1)");
    
    assertEquals("{concat ['a', 'b']}", parseExpression("concat( 'a' ,\t'b' )").toString());
    
    parseMethod(TokenKind.SubstringofMethod, "'a'", "'b'");
    parseMethod(TokenKind.SubstringofMethod, "' '", "'b'");
    parseMethod(TokenKind.SubstringofMethod, "' '", "' '");
    parseMethod(TokenKind.SubstringofMethod, null, "'a'");
    wrongExpression("substringof('a',1)");
}

  @Test
  public void variableParameterNumberMethods() throws Exception {
    parseMethod(TokenKind.SubstringMethod, "'abc'", "1", "2");
    parseMethod(TokenKind.SubstringMethod, "'abc'", "1");

    parseMethod(TokenKind.CastMethod, "Edm.SByte");
    parseMethod(TokenKind.CastMethod, "42", "Edm.SByte");

    parseMethod(TokenKind.IsofMethod, "Edm.SByte");
    parseMethod(TokenKind.IsofMethod, "42", "Edm.SByte");

    wrongExpression("substring('abc')");
    wrongExpression("substring('abc',1,2,3)");
    wrongExpression("substring(1,2)");
    wrongExpression("cast(1,2)");
    wrongExpression("isof(Edm.Int16,2)");
    
    assertEquals("{cast [42, Edm.SByte]}", parseExpression("cast( 42\t, Edm.SByte        )").toString());
  }

  @Test
  public void twoParameterAliasMethods() throws Exception {
    parseMethodWithParametersWithAlias(TokenKind.SubstringofMethod, "'a'", "'b'");
    parseMethodWithParametersWithoutAlias(TokenKind.SubstringofMethod, "'a'", "'b'");
  }
  
  private void parseMethodWithParametersWithoutAlias(TokenKind kind, String... parameters) 
      throws UriParserException, UriValidationException {
    final String methodName = kind.name().substring(0, kind.name().indexOf("Method")).toLowerCase(Locale.ROOT)
        .replace("geo", "geo.");
    String expressionString = methodName + '(';
    expressionString += "@word1";
    expressionString += ',';
    expressionString += parameters[1];
    expressionString += ')';
    expressionString += "&@word1=" + parameters[0];

    Map<String, AliasQueryOption> alias = new HashMap<String, AliasQueryOption>();
    AliasQueryOptionImpl aliasQueryOption = new AliasQueryOptionImpl();
    aliasQueryOption.setName("@word");
    aliasQueryOption.setText("\'a\'");
    alias.put("@word", aliasQueryOption);
    UriTokenizer tokenizer = new UriTokenizer(expressionString);
    final Expression expression = new ExpressionParser(mock(Edm.class), odata).parse(tokenizer, null, null, alias);
    assertNotNull(expression);
    
    assertEquals('{' + methodName + ' ' + "[@word1, " + parameters[1] + ']' + '}',
        expression.toString());
    
  }

  private void parseMethodWithParametersWithAlias(TokenKind kind, 
      String... parameters) throws UriParserException, UriValidationException {
    final String methodName = kind.name().substring(0, kind.name().indexOf("Method")).toLowerCase(Locale.ROOT)
        .replace("geo", "geo.");
    String expressionString = methodName + '(';
    expressionString += "@word";
    expressionString += ',';
    expressionString += parameters[1];
    expressionString += ')';
    expressionString += "&@word=" + parameters[0];

    Map<String, AliasQueryOption> alias = new HashMap<String, AliasQueryOption>();
    AliasQueryOptionImpl aliasQueryOption = new AliasQueryOptionImpl();
    aliasQueryOption.setName("@word");
    aliasQueryOption.setText("\'a\'");
    alias.put("@word", aliasQueryOption);
    UriTokenizer tokenizer = new UriTokenizer(expressionString);
    final Expression expression = new ExpressionParser(mock(Edm.class), odata).parse(tokenizer, null, null, alias);
    assertNotNull(expression);
    
    assertEquals('{' + methodName + ' ' + "[@word, " + parameters[1] + ']' + '}',
        expression.toString());
    
  }

  private void parseMethod(TokenKind kind, String... parameters) throws UriParserException, UriValidationException {
    final String methodName = kind.name().substring(0, kind.name().indexOf("Method")).toLowerCase(Locale.ROOT)
        .replace("geo", "geo.");
    String expressionString = methodName + '(';
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

    final Expression expression = parseExpression(expressionString);
    assertEquals('{' + methodName + ' ' + Arrays.toString(parameters) + '}',
        expression.toString());
  }

  private Expression parseExpression(final String expressionString)
      throws UriParserException, UriValidationException {
    UriTokenizer tokenizer = new UriTokenizer(expressionString);
    final Expression expression = new ExpressionParser(mock(Edm.class), odata).parse(tokenizer, null, null, null);
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
  
  @Test
  public void testPropertyPathExp() throws Exception {
    final String entitySetName = "ESName";
    final String keyPropertyName = "a";
    EdmProperty keyProperty = mockProperty(keyPropertyName,
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    EdmKeyPropertyRef keyPropertyRef = mockKeyPropertyRef(keyPropertyName, keyProperty);
    EdmEntityType entityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(entityType.getPropertyNames()).thenReturn(Collections.singletonList(keyPropertyName));
    Mockito.when(entityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    EdmEntitySet entitySet = mockEntitySet(entitySetName, entityType);
    EdmEntityContainer container = mockContainer(entitySetName, entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    
    UriTokenizer tokenizer = new UriTokenizer("a eq \'abc\'");
    Expression expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        entityType, null, null);
    assertNotNull(expression);
    assertEquals("{[a] EQ \'abc\'}", expression.toString());
    
    tokenizer = new UriTokenizer("a in (\'abc\', \'xyz\')");
    expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        entityType, null, null);
    assertNotNull(expression);
    assertEquals("{[a] IN [\'abc\', \'xyz\']}", expression.toString());
    try {
      tokenizer = new UriTokenizer("a in (\'abc\', 10)");
      expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
          entityType, null, null);
    } catch (UriParserSemanticException e) {
      assertEquals("Incompatible types.", e.getMessage());
    }
  }

  /**
   * @param keyPropertyName
   * @param keyPropertyRef
   * @return
   */
  private EdmEntityType mockEntityType(final String keyPropertyName, EdmKeyPropertyRef keyPropertyRef) {
    EdmEntityType entityType = Mockito.mock(EdmEntityType.class);
    Mockito.when(entityType.getKeyPredicateNames()).thenReturn(Collections.singletonList(keyPropertyName));
    Mockito.when(entityType.getKeyPropertyRefs()).thenReturn(Collections.singletonList(keyPropertyRef));
    return entityType;
  }

  /**
   * @param keyPropertyName
   * @param keyProperty
   * @return
   */
  private EdmKeyPropertyRef mockKeyPropertyRef(final String keyPropertyName, EdmProperty keyProperty) {
    EdmKeyPropertyRef keyPropertyRef = Mockito.mock(EdmKeyPropertyRef.class);
    Mockito.when(keyPropertyRef.getName()).thenReturn(keyPropertyName);
    Mockito.when(keyPropertyRef.getProperty()).thenReturn(keyProperty);
    return keyPropertyRef;
  }

  /**
   * @param propertyName
   * @return
   */
  private EdmProperty mockProperty(final String propertyName, final EdmType type) {
    EdmProperty keyProperty = Mockito.mock(EdmProperty.class);
    Mockito.when(keyProperty.getType()).thenReturn(type);
    Mockito.when(keyProperty.getDefaultValue()).thenReturn("");
    Mockito.when(keyProperty.getName()).thenReturn(propertyName);
    return keyProperty;
  }
  
  @Test(expected = UriParserSemanticException.class)
  public void testPropertyPathExpWithoutType() throws Exception {
    final String entitySetName = "ESName";
    final String keyPropertyName = "a";
    EdmProperty keyProperty = mockProperty(keyPropertyName,
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    EdmKeyPropertyRef keyPropertyRef = mockKeyPropertyRef(keyPropertyName, keyProperty);
    EdmEntityType entityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(entityType.getPropertyNames()).thenReturn(Collections.singletonList(keyPropertyName));
    Mockito.when(entityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    EdmEntitySet entitySet = mockEntitySet(entitySetName, entityType);
    EdmEntityContainer container = mockContainer(entitySetName, entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    
    UriTokenizer tokenizer = new UriTokenizer("a eq \'abc\'");
    new ExpressionParser(mockedEdm, odata).parse(tokenizer, null, null, null);
  }
  
  @Test(expected = UriParserSemanticException.class)
  public void testPropertyPathExpWithoutProperty() throws Exception {
    final String entitySetName = "ESName";
    final String keyPropertyName = "a";
    EdmProperty keyProperty = mockProperty(keyPropertyName, 
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    EdmKeyPropertyRef keyPropertyRef = mockKeyPropertyRef(keyPropertyName, keyProperty);
    EdmEntityType entityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("test.ETName"));
    EdmEntitySet entitySet = mockEntitySet(entitySetName, entityType);
    EdmEntityContainer container = mockContainer(entitySetName, entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    
    UriTokenizer tokenizer = new UriTokenizer("a eq \'abc\'");
    new ExpressionParser(mockedEdm, odata).parse(tokenizer, entityType, null, null);
  }

  /**
   * @param entitySetName
   * @param entitySet
   * @return
   */
  private EdmEntityContainer mockContainer(final String entitySetName, EdmEntitySet entitySet) {
    EdmEntityContainer container = Mockito.mock(EdmEntityContainer.class);
    Mockito.when(container.getEntitySet(entitySetName)).thenReturn(entitySet);
    return container;
  }

  /**
   * @param entitySetName
   * @param entityType
   * @return
   */
  private EdmEntitySet mockEntitySet(final String entitySetName, EdmEntityType entityType) {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn(entitySetName);
    Mockito.when(entitySet.getEntityType()).thenReturn(entityType);
    return entitySet;
  }
  
  @Test
  public void testComplexPropertyPathExp() throws Exception {
    final String entitySetName = "ESName";
    final String keyPropertyName = "a";
    final String complexPropertyName = "comp";
    final String propertyName = "prop";
    EdmProperty keyProperty = mockProperty(keyPropertyName, 
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    EdmKeyPropertyRef keyPropertyRef = mockKeyPropertyRef(keyPropertyName, keyProperty);
    EdmProperty property = mockProperty(propertyName, 
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    
    EdmComplexType complexType = mockComplexType(propertyName, property);
    EdmProperty complexProperty = mockProperty(complexPropertyName, complexType);
    
    EdmEntityType entityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(entityType.getPropertyNames()).thenReturn(Arrays.asList(keyPropertyName, complexPropertyName));
    Mockito.when(entityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    Mockito.when(entityType.getProperty(complexPropertyName)).thenReturn(complexProperty);
    EdmEntitySet entitySet = mockEntitySet(entitySetName, entityType);
    EdmEntityContainer container = mockContainer(entitySetName, entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    
    UriTokenizer tokenizer = new UriTokenizer("comp/prop eq \'abc\'");
    Expression expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        entityType, null, null);
    assertNotNull(expression);
    assertEquals("{[comp, prop] EQ \'abc\'}", expression.toString());
    
    tokenizer = new UriTokenizer("comp/prop in (\'abc\','xyz')");
    expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        entityType, null, null);
    assertNotNull(expression);
    assertEquals("{[comp, prop] IN [\'abc\', \'xyz\']}", expression.toString());
  }

  /**
   * @param propertyName
   * @param property
   * @return
   */
  private EdmComplexType mockComplexType(final String propertyName, EdmProperty property) {
    EdmComplexType complexType = Mockito.mock(EdmComplexType.class);
    Mockito.when(complexType.getPropertyNames()).thenReturn(Collections.singletonList(propertyName));
    Mockito.when(complexType.getProperty(propertyName)).thenReturn(property);
    return complexType;
  }
  
  /**
   * @param propertyName
   * @param property
   * @return
   */
  private EdmComplexType mockComplexType(final String propertyName, EdmNavigationProperty property) {
    EdmComplexType complexType = Mockito.mock(EdmComplexType.class);
    Mockito.when(complexType.getPropertyNames()).thenReturn(Collections.singletonList(propertyName));
    Mockito.when(complexType.getProperty(propertyName)).thenReturn(property);
    return complexType;
  }
  
  @Test
  public void testLambdaPropertyPathExp() throws Exception {
    final String entitySetName = "ESName";
    final String keyPropertyName = "a";
    final String complexPropertyName = "comp";
    final String propertyName = "prop";
    EdmProperty keyProperty = mockProperty(keyPropertyName, 
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    EdmKeyPropertyRef keyPropertyRef = mockKeyPropertyRef(keyPropertyName, keyProperty);
    EdmProperty property = mockProperty(propertyName, 
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    
    EdmComplexType complexType = mockComplexType(propertyName, property);
    EdmProperty complexProperty = mockProperty(complexPropertyName, complexType);
    Mockito.when(complexProperty.isCollection()).thenReturn(true);
    
    EdmEntityType entityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(entityType.getPropertyNames()).thenReturn(Arrays.asList(keyPropertyName, complexPropertyName));
    Mockito.when(entityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    Mockito.when(entityType.getProperty(complexPropertyName)).thenReturn(complexProperty);
    Mockito.when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("test.ET"));
    EdmEntitySet entitySet = mockEntitySet(entitySetName, entityType);
    EdmEntityContainer container = mockContainer(entitySetName, entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    
    UriTokenizer tokenizer = new UriTokenizer("comp/any(d:d/prop eq \'abc\')");
    Expression expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        entityType, null, null);
    assertNotNull(expression);
    assertEquals("[comp, any]", expression.toString());
    
    tokenizer = new UriTokenizer("comp/all(d:d/prop eq \'abc\')");
    expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        entityType, null, null);
    assertNotNull(expression);
    assertEquals("[comp, all]", expression.toString());
  }
  
  @Test
  public void testNavigationPropertyPathExp() throws Exception {
    final String entitySetName = "ESName";
    final String keyPropertyName = "a";
    final String complexPropertyName = "comp";
    final String propertyName = "navProp";
    EdmProperty keyProperty = mockProperty(keyPropertyName, 
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    EdmKeyPropertyRef keyPropertyRef = mockKeyPropertyRef(keyPropertyName, keyProperty);
    
    EdmEntityType targetEntityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(targetEntityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("test.TargetET"));
    EdmNavigationProperty navProperty = mockNavigationProperty(propertyName, targetEntityType);
    
    EdmComplexType complexType = mockComplexType(propertyName, navProperty);
    EdmProperty complexProperty = mockProperty(complexPropertyName, complexType);
    
    EdmEntityType startEntityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(startEntityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("test.StartET"));
    Mockito.when(startEntityType.getPropertyNames()).thenReturn(
        Arrays.asList(keyPropertyName, complexPropertyName));
    Mockito.when(startEntityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    Mockito.when(startEntityType.getProperty(complexPropertyName)).thenReturn(complexProperty);
    EdmEntitySet entitySet = mockEntitySet(entitySetName, startEntityType);
    EdmEntityContainer container = mockContainer(entitySetName, entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    
    UriTokenizer tokenizer = new UriTokenizer("comp/navProp");
    final Expression expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        startEntityType, null, null);
    assertNotNull(expression);
    assertEquals("[comp, navProp]", expression.toString());
  }

  /**
   * @param propertyName
   * @param entityType
   * @return
   */
  private EdmNavigationProperty mockNavigationProperty(final String propertyName, EdmEntityType entityType) {
    EdmNavigationProperty navProperty = Mockito.mock(EdmNavigationProperty.class);
    Mockito.when(navProperty.getName()).thenReturn(propertyName);
    Mockito.when(navProperty.getType()).thenReturn(entityType);
    return navProperty;
  }
  
  @Test
  public void testDerivedPathExp() throws Exception {
    final String derivedEntitySetName = "ESName";
    final String keyPropertyName = "a";
    final String propertyName = "navProp";
    EdmProperty keyProperty = mockProperty(keyPropertyName, 
        OData.newInstance().createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    EdmKeyPropertyRef keyPropertyRef = mockKeyPropertyRef(keyPropertyName, keyProperty);
    
    EdmEntityType navEntityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(navEntityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("test.navET"));
    Mockito.when(navEntityType.getNamespace()).thenReturn("test");
    Mockito.when(navEntityType.getPropertyNames()).thenReturn(
        Arrays.asList(keyPropertyName));
    Mockito.when(navEntityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    
    EdmEntityType baseEntityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(baseEntityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("test.baseET"));
    Mockito.when(baseEntityType.getNamespace()).thenReturn("test");
    Mockito.when(baseEntityType.getPropertyNames()).thenReturn(
        Arrays.asList(keyPropertyName));
    Mockito.when(baseEntityType.getProperty(keyPropertyName)).thenReturn(keyProperty);
    
    Mockito.when(navEntityType.getBaseType()).thenReturn(baseEntityType);
    Mockito.when(baseEntityType.compatibleTo(navEntityType)).thenReturn(true);
    
    EdmEntityType entityType = mockEntityType(keyPropertyName, keyPropertyRef);
    Mockito.when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("test.derivedET"));
    Mockito.when(entityType.getNamespace()).thenReturn("test");
    Mockito.when(entityType.getPropertyNames()).thenReturn(Arrays.asList(keyPropertyName, propertyName));
    EdmNavigationProperty navProperty = mockNavigationProperty(propertyName, navEntityType);
    Mockito.when(entityType.getProperty(propertyName)).thenReturn(navProperty);
    
    EdmEntitySet entitySet = mockEntitySet(derivedEntitySetName, entityType);
    EdmEntityContainer container = mockContainer(derivedEntitySetName, entitySet);
    Edm mockedEdm = Mockito.mock(Edm.class);
    Mockito.when(mockedEdm.getEntityContainer()).thenReturn(container);
    Mockito.when(mockedEdm.getEntityType(new FullQualifiedName("test.baseET"))).thenReturn(baseEntityType);
    
    UriTokenizer tokenizer = new UriTokenizer("navProp/test.baseET");
    Expression expression = new ExpressionParser(mockedEdm, odata).parse(tokenizer, 
        entityType, null, null);
    assertNotNull(expression);
    assertEquals("[navProp]", expression.toString());
  }
}
