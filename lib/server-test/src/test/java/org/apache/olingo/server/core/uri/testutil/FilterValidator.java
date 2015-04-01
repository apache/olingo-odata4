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
package org.apache.olingo.server.core.uri.testutil;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.EnumerationImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MethodImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.TypeLiteralImpl;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FilterValidator implements TestValidator {
  private Edm edm;

  private TestValidator invokedByValidator;
  private FilterOptionImpl filter;

  private Expression curExpression;
  private Expression rootExpression;

  private OrderByOptionImpl orderBy;

  private UriParserException exception;

  // --- Setup ---
  public FilterValidator setUriResourcePathValidator(final ResourceValidator uriResourcePathValidator) {
    invokedByValidator = uriResourcePathValidator;
    return this;
  }

  public FilterValidator setUriValidator(final TestUriValidator uriValidator) {
    invokedByValidator = uriValidator;
    return this;
  }

  public FilterValidator setValidator(final TestValidator uriValidator) {
    invokedByValidator = uriValidator;
    return this;
  }

  public FilterValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  public FilterValidator setFilter(final FilterOptionImpl filter) {
    this.filter = filter;

    if (filter.getExpression() == null) {
      fail("FilterValidator: no filter found");
    }
    setExpression(filter.getExpression());
    return this;
  }

  public FilterValidator setOrderBy(final OrderByOptionImpl orderBy) {
    this.orderBy = orderBy;

    return this;
  }

  public FilterValidator setExpression(final Expression expression) {
    rootExpression = curExpression = expression;
    return this;
  }

  // --- Execution ---

  public FilterValidator runOrderByOnETAllPrim(final String orderBy) throws UriParserException {
    return runUriOrderBy("ESAllPrim", "$orderby=" + orderBy.trim());
  }

  public FilterValidator runOrderByOnETTwoKeyNav(final String orderBy) throws UriParserException {
    return runUriOrderBy("ESTwoKeyNav", "$orderby=" + orderBy.trim());
  }
  
  public FilterValidator runOrderByOnETMixEnumDefCollComp(final String orderBy) throws UriParserException {
    return runUriOrderBy("ESMixEnumDefCollComp", "$orderby=" + orderBy.trim());
  }

  public FilterValidator runOrderByOnETTwoKeyNavEx(final String orderBy) throws UriParserException {
    return runUriOrderByEx("ESTwoKeyNav", "$orderby=" + orderBy.trim());
  }

  public FilterValidator runOnETTwoKeyNav(final String filter) throws UriParserException {
    return runUri("ESTwoKeyNav", "$filter=" + filter.trim());
  }
  
  public FilterValidator runOnETMixEnumDefCollComp(final String filter) throws UriParserException {
    return runUri("ESMixEnumDefCollComp", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETTwoKeyNavSingle(final String filter) throws UriParserException {
    return runUri("SINav", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETTwoKeyNavEx(final String filter) throws UriParserException {
    return runUriEx("ESTwoKeyNav", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETAllPrim(final String filter) throws UriParserException {
    return runUri("ESAllPrim(1)", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETKeyNav(final String filter) throws UriParserException {
    return runUri("ESKeyNav(1)", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETKeyNavEx(final String filter) throws UriParserException {
    return runUriEx("ESKeyNav(1)", "$filter=" + filter.trim());
  }

  public FilterValidator runOnCTTwoPrim(final String filter) throws UriParserException {
    return runUri("SINav/PropertyCompTwoPrim", "$filter=" + filter.trim());
  }

  public FilterValidator runOnString(final String filter) throws UriParserException {
    return runUri("SINav/PropertyString", "$filter=" + filter.trim());
  }

  public FilterValidator runOnInt32(final String filter) throws UriParserException {
    return runUri("ESCollAllPrim(1)/CollPropertyInt32", "$filter=" + filter.trim());
  }

  public FilterValidator runOnDateTimeOffset(final String filter) throws UriParserException {
    return runUri("ESCollAllPrim(1)/CollPropertyDateTimeOffset", "$filter=" + filter.trim());
  }

  public FilterValidator runOnDuration(final String filter) throws UriParserException {
    return runUri("ESCollAllPrim(1)/CollPropertyDuration", "$filter=" + filter.trim());
  }

  public FilterValidator runOnTimeOfDay(final String filter) throws UriParserException {
    return runUri("ESCollAllPrim(1)/CollPropertyTimeOfDay", "$filter=" + filter.trim());
  }

  public FilterValidator runESabc(final String filter) throws UriParserException {
    return runUri("ESabc", "$filter=" + filter.trim());
  }

  public FilterValidator runUri(final String path, final String query) throws UriParserException {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    uriInfo = parser.parseUri(path, query, null, edm);

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setFilter((FilterOptionImpl) uriInfo.getFilterOption());
    curExpression = filter.getExpression();
    return this;
  }

  public FilterValidator runUriEx(final String path, final String query) {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    try {
      uriInfo = parser.parseUri(path, query, null, edm);
    } catch (UriParserException e) {
      exception = e;
      return this;
    }

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setFilter((FilterOptionImpl) uriInfo.getFilterOption());
    curExpression = filter.getExpression();
    return this;
  }

  public FilterValidator runUriOrderBy(final String path, final String query) throws UriParserException {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    uriInfo = parser.parseUri(path, query, null, edm);

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setOrderBy((OrderByOptionImpl) uriInfo.getOrderByOption());
    return this;
  }

  public FilterValidator runUriOrderByEx(final String path, final String query) {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    try {
      uriInfo = parser.parseUri(path, query, null, edm);
      fail("Expected exception not thrown.");
    } catch (UriParserException e) {
      exception = e;
      return this;
    }

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setOrderBy((OrderByOptionImpl) uriInfo.getOrderByOption());
    return this;
  }

  // --- Navigation ---

  public ExpandValidator goUpToExpandValidator() {
    return (ExpandValidator) invokedByValidator;
  }

  public ResourceValidator goUpToResourceValidator() {
    return (ResourceValidator) invokedByValidator;
  }

  public ResourceValidator goPath() {
    if (!(curExpression instanceof MemberImpl)) {
      fail("Current expression not a member");
    }

    MemberImpl member = (MemberImpl) curExpression;

    return new ResourceValidator()
        .setEdm(edm)
        .setUriInfoImplPath((UriInfoImpl) member.getResourcePath())
        .setUpValidator(this);

  }

  public FilterValidator goParameter(final int parameterIndex) {
    if (curExpression instanceof MethodImpl) {
      MethodImpl methodCall = (MethodImpl) curExpression;
      curExpression = methodCall.getParameters().get(parameterIndex);
    } else {
      fail("Current expression not a methodCall");
    }
    return this;
  }

  // --- Validation ---

  /**
   * Validates the serialized filterTree against a given filterString
   * The given expected filterString is compressed before to allow better readable code in the unit tests
   * @param toBeCompr
   * @return {@link FilterValidator}
   */
  public FilterValidator isCompr(final String toBeCompr) {
    return is(compress(toBeCompr));
  }

  public FilterValidator is(final String expectedFilterAsString) {
    try {
      String actualFilterAsText = FilterTreeToText.Serialize(filter);
      assertEquals(expectedFilterAsString, actualFilterAsText);
    } catch (ExpressionVisitException e) {
      fail("Exception occured while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    } catch (ODataApplicationException e) {
      fail("Exception occured while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    }

    return this;
  }

  // --- Helper ---

  private String compress(final String expected) {
    String ret = expected.replaceAll("\\s+", " ");
    ret = ret.replaceAll("< ", "<");
    ret = ret.replaceAll(" >", ">");
    return ret;
  }

  public FilterValidator isType(final FullQualifiedName fullName) {
    EdmType actualType = null;

    if (curExpression instanceof MemberImpl) {
      Member member = (Member) curExpression;
      actualType = member.getType();
    } else if (curExpression instanceof TypeLiteralImpl) {
      TypeLiteralImpl typeLiteral = (TypeLiteralImpl) curExpression;
      actualType = typeLiteral.getType();
    } else if (curExpression instanceof LiteralImpl) {
      LiteralImpl typeLiteral = (LiteralImpl) curExpression;
      actualType = typeLiteral.getType();
    }

    if (actualType == null) {
      fail("Current expression not typed");
    }

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());
    assertEquals(fullName, actualName);
    return this;
  }

  public FilterValidator left() {
    if (!(curExpression instanceof BinaryImpl)) {
      fail("Current expression not a binary operator");
    }

    curExpression = ((BinaryImpl) curExpression).getLeftOperand();

    return this;
  }

  public FilterValidator root() {
    if (filter != null) {
      curExpression = filter.getExpression();
    } else {
      curExpression = rootExpression;
    }

    return this;
  }

  public FilterValidator right() {
    if (!(curExpression instanceof BinaryImpl)) {
      fail("Current expression is not a binary operator");
    }

    curExpression = ((BinaryImpl) curExpression).getRightOperand();

    return this;

  }

  public FilterValidator isLiteral(final String literalText) {
    if (!(curExpression instanceof LiteralImpl)) {
      fail("Current expression is not a literal");
    }

    String actualLiteralText = ((LiteralImpl) curExpression).getText();
    assertEquals(literalText, actualLiteralText);

    return this;
  }

  public FilterValidator isMethod(final MethodKind methodKind, final int parameterCount) {
    if (!(curExpression instanceof MethodImpl)) {
      fail("Current expression is not a methodCall");
    }

    MethodImpl methodCall = (MethodImpl) curExpression;
    assertEquals(methodKind, methodCall.getMethod());
    assertEquals(parameterCount, methodCall.getParameters().size());

    return this;
  }

  public FilterValidator isParameterText(final int parameterIndex, final String parameterText)
      throws ExpressionVisitException, ODataApplicationException {

    if (!(curExpression instanceof MethodImpl)) {
      fail("Current expression is not a method");
    }

    MethodImpl methodCall = (MethodImpl) curExpression;

    Expression parameter = methodCall.getParameters().get(parameterIndex);
    String actualParameterText = FilterTreeToText.Serialize(parameter);
    assertEquals(parameterText, actualParameterText);

    return this;
  }

  public FilterValidator isBinary(final BinaryOperatorKind binaryOperator) {
    if (!(curExpression instanceof BinaryImpl)) {
      fail("Current expression is not a binary operator");
    }

    BinaryImpl binary = (BinaryImpl) curExpression;
    assertEquals(binaryOperator, binary.getOperator());

    return this;
  }

  public FilterValidator isTypedLiteral(final FullQualifiedName fullName) {
    if (!(curExpression instanceof TypeLiteralImpl)) {
      fail("Current expression not a typeLiteral");
    }

    isType(fullName);

    return this;
  }

  public FilterValidator isMember() {
    if (!(curExpression instanceof MemberImpl)) {
      fail("Current expression not a member");
    }

    return this;
  }

  public FilterValidator isMemberStartType(final FullQualifiedName fullName) {
    if (!(curExpression instanceof MemberImpl)) {
      fail("Current expression not a member");
    }

    MemberImpl member = (MemberImpl) curExpression;
    EdmType actualType = member.getStartTypeFilter();

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());
    assertEquals(fullName, actualName);
    return this;
  }

  public FilterValidator isEnum(final FullQualifiedName nameenstring, final List<String> enumValues) {
    if (!(curExpression instanceof EnumerationImpl)) {
      fail("Current expression not a enumeration");
    }

    EnumerationImpl enumeration = (EnumerationImpl) curExpression;

    FullQualifiedName actualName =
        new FullQualifiedName(enumeration.getType().getNamespace(), enumeration.getType().getName());

    // check name
    assertEquals(nameenstring.toString(), actualName.toString());

    // check values
    int i = 0;
    for (String item : enumValues) {
      assertEquals(item, enumeration.getValues().get(i));
      i++;
    }

    return this;
  }

  public FilterValidator isSortOrder(final int index, final boolean descending) {
    assertEquals(descending, orderBy.getOrders().get(index).isDescending());
    return this;
  }

  public FilterValidator goOrder(final int index) {
    curExpression = orderBy.getOrders().get(index).getExpression();
    return this;
  }

  public FilterValidator isExSyntax(final UriParserSyntaxException.MessageKeys messageKey) {
    assertEquals(UriParserSyntaxException.class, exception.getClass());
    assertEquals(messageKey, exception.getMessageKey());
    return this;
  }

  public FilterValidator isExSemantic(final UriParserSemanticException.MessageKeys messageKey) {
    assertEquals(UriParserSemanticException.class, exception.getClass());
    assertEquals(messageKey, exception.getMessageKey());
    return this;
  }

  public FilterValidator isNull() {
    if (!(curExpression instanceof LiteralImpl)) {
      fail("Current expression is not a literal");
    }

    String actualLiteralText = ((LiteralImpl) curExpression).getText();
    assertEquals("null", actualLiteralText);
    return this;
  }

  public FilterValidator isTrue() {
    if (!(curExpression instanceof LiteralImpl)) {
      fail("Current expression is not a literal");
    }

    String actualLiteralText = ((LiteralImpl) curExpression).getText();
    assertEquals("true", actualLiteralText);
    return this;
  }

  public FilterValidator isFalse() {
    if (!(curExpression instanceof LiteralImpl)) {
      fail("Current expression is not a literal");
    }

    String actualLiteralText = ((LiteralImpl) curExpression).getText();
    assertEquals("false", actualLiteralText);
    return this;
  }

}
