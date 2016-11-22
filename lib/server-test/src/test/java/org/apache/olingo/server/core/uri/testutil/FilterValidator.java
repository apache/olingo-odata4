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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Alias;
import org.apache.olingo.server.api.uri.queryoption.expression.Binary;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Enumeration;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.Method;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.TypeLiteral;
import org.apache.olingo.server.api.uri.queryoption.expression.Unary;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MethodImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.UnaryImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class FilterValidator implements TestValidator {
  private final OData odata = OData.newInstance();
  private Edm edm;

  private TestValidator invokedByValidator;
  private FilterOption filter;
  private OrderByOption orderBy;

  private Expression curExpression;
  private Expression rootExpression;

  // --- Setup ---
  public FilterValidator setValidator(final TestValidator uriValidator) {
    invokedByValidator = uriValidator;
    return this;
  }

  public FilterValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  public FilterValidator setFilter(final FilterOption filter) {
    this.filter = filter;
    assertNotNull("FilterValidator: no filter found", filter.getExpression());
    setExpression(filter.getExpression());
    return this;
  }

  public FilterValidator setExpression(final Expression expression) {
    rootExpression = curExpression = expression;
    return this;
  }

  // --- Execution ---

  public FilterValidator runOrderByOnETAllPrim(final String orderBy)
      throws UriParserException, UriValidationException {
    return runUriOrderBy("ESAllPrim", "$orderby=" + orderBy);
  }

  public FilterValidator runOrderByOnETTwoKeyNav(final String orderBy)
      throws UriParserException, UriValidationException {
    return runUriOrderBy("ESTwoKeyNav", "$orderby=" + orderBy);
  }

  public FilterValidator runOrderByOnETMixEnumDefCollComp(final String orderBy)
      throws UriParserException, UriValidationException {
    return runUriOrderBy("ESMixEnumDefCollComp", "$orderby=" + orderBy);
  }

  public TestUriValidator runOrderByOnETTwoKeyNavEx(final String orderBy) {
    return runUriEx("ESTwoKeyNav", "$orderby=" + orderBy);
  }

  public FilterValidator runOnETTwoKeyNav(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESTwoKeyNav", "$filter=" + filter);
  }

  public FilterValidator runOnETMixEnumDefCollComp(final String filter)
      throws UriParserException, UriValidationException {
    return runUri("ESMixEnumDefCollComp", "$filter=" + filter);
  }

  public FilterValidator runOnETTwoKeyNavSingle(final String filter)
      throws UriParserException, UriValidationException {
    return runUri("SINav", "$filter=" + filter);
  }

  public TestUriValidator runOnETTwoKeyNavEx(final String filter) {
    return runUriEx("ESTwoKeyNav", "$filter=" + filter);
  }

  public FilterValidator runOnETAllPrim(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESAllPrim(1)", "$filter=" + filter);
  }

  public FilterValidator runOnETKeyNav(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESKeyNav(1)", "$filter=" + filter);
  }

  public TestUriValidator runOnETKeyNavEx(final String filter) {
    return runUriEx("ESKeyNav(1)", "$filter=" + filter);
  }

  public FilterValidator runOnCTTwoPrim(final String filter) throws UriParserException, UriValidationException {
    return runUri("SINav/PropertyCompTwoPrim", "$filter=" + filter);
  }

  public FilterValidator runOnString(final String filter) throws UriParserException, UriValidationException {
    return runUri("SINav/PropertyString", "$filter=" + filter);
  }

  public FilterValidator runOnInt32(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyInt32", "$filter=" + filter);
  }

  public FilterValidator runOnDateTimeOffset(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyDateTimeOffset", "$filter=" + filter);
  }

  public FilterValidator runOnDuration(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyDuration", "$filter=" + filter);
  }

  public FilterValidator runOnTimeOfDay(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyTimeOfDay", "$filter=" + filter);
  }

  public FilterValidator runUri(final String path, final String query)
      throws UriParserException, UriValidationException {
    final UriInfo uriInfo = new Parser(edm, odata).parseUri(path, query, null, null);
    assertTrue("Filtervalidator can only be used on resourcePaths", uriInfo.getKind() == UriInfoKind.resource);
    setFilter(uriInfo.getFilterOption());
    curExpression = filter.getExpression();
    return this;
  }

  public TestUriValidator runUriEx(final String path, final String query) {
    return new TestUriValidator().setEdm(edm).runEx(path, query);
  }

  public FilterValidator runUriOrderBy(final String path, final String query)
      throws UriParserException, UriValidationException {
    final UriInfo uriInfo = new Parser(edm, odata).parseUri(path, query, null, null);
    assertTrue("Filtervalidator can only be used on resourcePaths", uriInfo.getKind() == UriInfoKind.resource);
    orderBy = uriInfo.getOrderByOption();
    return this;
  }

  // --- Navigation ---

  public ResourceValidator goUpToResourceValidator() {
    return (ResourceValidator) invokedByValidator;
  }

  public TestUriValidator goUpToUriValidator() {
    return (TestUriValidator) invokedByValidator;
  }

  public ResourceValidator goPath() {
    isMember();
    Member member = (Member) curExpression;

    return new ResourceValidator()
        .setEdm(edm)
        .setUriInfoPath(member.getResourcePath())
        .setUpValidator(this);
  }

  public FilterValidator goParameter(final int parameterIndex) {
    assertTrue("Current expression not a methodCall", curExpression instanceof Method);
    Method methodCall = (Method) curExpression;
    curExpression = methodCall.getParameters().get(parameterIndex);
    return this;
  }

  // --- Validation ---

  /**
   * Validates the serialized filterTree against a given filterString.
   * The given expected filterString is compressed before to allow better readable code in the unit tests.
   * @param toBeCompr
   * @return {@link FilterValidator}
   */
  public FilterValidator isCompr(final String toBeCompr) {
    return is(compress(toBeCompr));
  }

  public FilterValidator is(final String expectedFilterAsString) {
    try {
      assertEquals(expectedFilterAsString, FilterTreeToText.Serialize(filter));
    } catch (final ODataException e) {
      fail("Exception occurred while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    }
    return this;
  }

  // --- Helper ---

  private String compress(final String expected) {
    return expected.replaceAll("\\s+", " ")
        .replaceAll("< ", "<")
        .replaceAll(" >", ">");
  }

  public FilterValidator isType(final FullQualifiedName fullName) {
    EdmType actualType = null;

    if (curExpression instanceof Member) {
      actualType = ((Member) curExpression).getType();
    } else if (curExpression instanceof TypeLiteral) {
      actualType = ((TypeLiteral) curExpression).getType();
    } else if (curExpression instanceof Literal) {
      actualType = ((Literal) curExpression).getType();
    } else if (curExpression instanceof Enumeration) {
      actualType = ((Enumeration) curExpression).getType();
    } else if (curExpression instanceof Unary) {
      actualType = ((UnaryImpl) curExpression).getType();
    } else if (curExpression instanceof Binary) {
      actualType = ((BinaryImpl) curExpression).getType();
    } else if (curExpression instanceof Method) {
      actualType = ((MethodImpl) curExpression).getType();
    }

    assertNotNull("Current expression not typed", actualType);
    assertEquals(fullName, actualType.getFullQualifiedName());
    return this;
  }

  public FilterValidator root() {
    curExpression = filter == null ? rootExpression : filter.getExpression();
    return this;
  }

  public FilterValidator left() {
    assertTrue("Current expression not a binary operator", curExpression instanceof Binary);
    curExpression = ((Binary) curExpression).getLeftOperand();
    return this;
  }

  public FilterValidator right() {
    assertTrue("Current expression not a binary operator", curExpression instanceof Binary);
    curExpression = ((Binary) curExpression).getRightOperand();
    return this;
  }

  public FilterValidator isLiteral(final String literalText) {
    assertTrue("Current expression is not a literal", curExpression instanceof Literal);
    String actualLiteralText = ((Literal) curExpression).getText();
    assertEquals(literalText, actualLiteralText);
    return this;
  }

  public FilterValidator isLiteralType(final EdmType edmType) {
    assertTrue("Current expression is not a literal", curExpression instanceof Literal);
    final EdmType type = ((Literal) curExpression).getType();
    assertEquals(edmType, type);
    return this;
  }

  public FilterValidator isMethod(final MethodKind methodKind, final int parameterCount) {
    assertTrue("Current expression is not a methodCall", curExpression instanceof Method);
    Method methodCall = (Method) curExpression;
    assertEquals(methodKind, methodCall.getMethod());
    assertEquals(parameterCount, methodCall.getParameters().size());
    return this;
  }

  public FilterValidator isParameterText(final int parameterIndex, final String parameterText)
      throws ExpressionVisitException, ODataApplicationException {

    if (curExpression instanceof MethodImpl) {
      MethodImpl methodCall = (MethodImpl) curExpression;

      Expression parameter = methodCall.getParameters().get(parameterIndex);
      String actualParameterText = FilterTreeToText.Serialize(parameter);
      assertEquals(parameterText, actualParameterText);
    } else if (curExpression instanceof MemberImpl) {
      final MemberImpl member = (MemberImpl) curExpression;
      final List<UriResource> uriResourceParts = member.getResourcePath().getUriResourceParts();

      if (!uriResourceParts.isEmpty() && uriResourceParts.get(0) instanceof UriResourceFunctionImpl) {
        assertEquals(parameterText, ((UriResourceFunctionImpl) uriResourceParts.get(0)).getParameters()
            .get(parameterIndex).getText());
      } else {
        fail("Current expression is not a method or function");
      }
    } else {
      fail("Current expression is not a method or function");
    }

    return this;
  }

  public FilterValidator isBinary(final BinaryOperatorKind binaryOperator) {
    assertTrue("Current expression not a binary operator", curExpression instanceof Binary);
    Binary binary = (Binary) curExpression;
    assertEquals(binaryOperator, binary.getOperator());
    return this;
  }

  public FilterValidator isTypedLiteral(final FullQualifiedName fullName) {
    assertTrue("Current expression not a typeLiteral", curExpression instanceof TypeLiteral);
    isType(fullName);
    return this;
  }

  public FilterValidator isMember() {
    assertTrue("Current expression not a member", curExpression instanceof Member);
    return this;
  }

  public FilterValidator isMemberStartType(final FullQualifiedName fullName) {
    isMember();
    Member member = (Member) curExpression;
    EdmType actualType = member.getStartTypeFilter();
    assertEquals(fullName, actualType.getFullQualifiedName());
    return this;
  }

  public FilterValidator isEnum(final FullQualifiedName name, final List<String> enumValues) {
    assertTrue("Current expression not an enumeration", curExpression instanceof Enumeration);
    Enumeration enumeration = (Enumeration) curExpression;

    // check name
    assertEquals(name, enumeration.getType().getFullQualifiedName());

    // check values
    assertEquals(enumValues, enumeration.getValues());

    return this;
  }

  public FilterValidator isAlias(final String name) {
    assertTrue("Current expression not an alias", curExpression instanceof Alias);
    final Alias alias = (Alias) curExpression;
    assertEquals(name, alias.getParameterName());
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
}
