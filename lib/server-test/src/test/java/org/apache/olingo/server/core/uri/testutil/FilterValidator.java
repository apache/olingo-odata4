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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
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
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
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

  private ODataLibraryException exception;

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

  public FilterValidator setFilter(final FilterOption filter) {
    this.filter = filter;

    if (filter.getExpression() == null) {
      fail("FilterValidator: no filter found");
    }
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
    return runUriOrderBy("ESAllPrim", "$orderby=" + orderBy.trim());
  }

  public FilterValidator runOrderByOnETTwoKeyNav(final String orderBy)
      throws UriParserException, UriValidationException {
    return runUriOrderBy("ESTwoKeyNav", "$orderby=" + orderBy.trim());
  }

  public FilterValidator runOrderByOnETMixEnumDefCollComp(final String orderBy)
      throws UriParserException, UriValidationException {
    return runUriOrderBy("ESMixEnumDefCollComp", "$orderby=" + orderBy.trim());
  }

  public FilterValidator runOrderByOnETTwoKeyNavEx(final String orderBy) throws UriParserException {
    return runUriEx("ESTwoKeyNav", "$orderby=" + orderBy.trim());
  }

  public FilterValidator runOnETTwoKeyNav(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESTwoKeyNav", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETMixEnumDefCollComp(final String filter)
      throws UriParserException, UriValidationException {
    return runUri("ESMixEnumDefCollComp", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETTwoKeyNavSingle(final String filter)
      throws UriParserException, UriValidationException {
    return runUri("SINav", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETTwoKeyNavEx(final String filter) throws UriParserException {
    return runUriEx("ESTwoKeyNav", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETAllPrim(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESAllPrim(1)", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETKeyNav(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESKeyNav(1)", "$filter=" + filter.trim());
  }

  public FilterValidator runOnETKeyNavEx(final String filter) throws UriParserException {
    return runUriEx("ESKeyNav(1)", "$filter=" + filter.trim());
  }

  public FilterValidator runOnCTTwoPrim(final String filter) throws UriParserException, UriValidationException {
    return runUri("SINav/PropertyCompTwoPrim", "$filter=" + filter.trim());
  }

  public FilterValidator runOnString(final String filter) throws UriParserException, UriValidationException {
    return runUri("SINav/PropertyString", "$filter=" + filter.trim());
  }

  public FilterValidator runOnInt32(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyInt32", "$filter=" + filter.trim());
  }

  public FilterValidator runOnDateTimeOffset(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyDateTimeOffset", "$filter=" + filter.trim());
  }

  public FilterValidator runOnDuration(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyDuration", "$filter=" + filter.trim());
  }

  public FilterValidator runOnTimeOfDay(final String filter) throws UriParserException, UriValidationException {
    return runUri("ESCollAllPrim(1)/CollPropertyTimeOfDay", "$filter=" + filter.trim());
  }

  public FilterValidator runUri(final String path, final String query)
      throws UriParserException, UriValidationException {
    final UriInfo uriInfo = new Parser(edm, odata).parseUri(path, query, null);

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setFilter(uriInfo.getFilterOption());
    curExpression = filter.getExpression();
    return this;
  }

  public FilterValidator runUriEx(final String path, final String query) {
    exception = null;
    try {
      new Parser(edm, odata).parseUri(path, query, null);
      fail("Expected exception not thrown.");
    } catch (final UriParserException e) {
      exception = e;
    } catch (final UriValidationException e) {
      exception = e;
    }
    return this;
  }

  public FilterValidator runUriOrderBy(final String path, final String query)
      throws UriParserException, UriValidationException {
    final UriInfo uriInfo = new Parser(edm, odata).parseUri(path, query, null);

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    orderBy = uriInfo.getOrderByOption();
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
    if (!(curExpression instanceof Member)) {
      fail("Current expression not a member");
    }

    Member member = (Member) curExpression;

    return new ResourceValidator()
        .setEdm(edm)
        .setUriInfoPath(member.getResourcePath())
        .setUpValidator(this);
  }

  public FilterValidator goParameter(final int parameterIndex) {
    if (curExpression instanceof Method) {
      Method methodCall = (Method) curExpression;
      curExpression = methodCall.getParameters().get(parameterIndex);
    } else {
      fail("Current expression not a methodCall");
    }
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
      String actualFilterAsText = FilterTreeToText.Serialize(filter);
      assertEquals(expectedFilterAsString, actualFilterAsText);
    } catch (ExpressionVisitException e) {
      fail("Exception occurred while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    } catch (ODataApplicationException e) {
      fail("Exception occurred while converting the filterTree into text" + "\n"
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

    if (actualType == null) {
      fail("Current expression not typed");
    }

    assertEquals(fullName, actualType.getFullQualifiedName());
    return this;
  }

  public FilterValidator root() {
    curExpression = filter == null ? rootExpression : filter.getExpression();
    return this;
  }

  public FilterValidator left() {
    if (!(curExpression instanceof Binary)) {
      fail("Current expression not a binary operator");
    }

    curExpression = ((Binary) curExpression).getLeftOperand();
    return this;
  }

  public FilterValidator right() {
    if (!(curExpression instanceof Binary)) {
      fail("Current expression is not a binary operator");
    }

    curExpression = ((Binary) curExpression).getRightOperand();
    return this;
  }

  public FilterValidator isLiteral(final String literalText) {
    if (!(curExpression instanceof Literal)) {
      fail("Current expression is not a literal");
    }

    String actualLiteralText = ((Literal) curExpression).getText();
    assertEquals(literalText, actualLiteralText);
    return this;
  }

  public FilterValidator isLiteralType(final EdmType edmType) {
    if (!(curExpression instanceof Literal)) {
      fail("Current expression is not a literal");
    }

    final EdmType type = ((Literal) curExpression).getType();
    assertNotNull(type);
    assertEquals(edmType, type);
    return this;
  }

  public FilterValidator isNullLiteralType() {
    if (!(curExpression instanceof Literal)) {
      fail("Current expression is not a literal");
    }

    final EdmType type = ((Literal) curExpression).getType();
    assertNull(type);
    return this;
  }

  public FilterValidator isMethod(final MethodKind methodKind, final int parameterCount) {
    if (!(curExpression instanceof Method)) {
      fail("Current expression is not a methodCall");
    }

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
    if (!(curExpression instanceof Binary)) {
      fail("Current expression is not a binary operator");
    }

    Binary binary = (Binary) curExpression;
    assertEquals(binaryOperator, binary.getOperator());
    return this;
  }

  public FilterValidator isTypedLiteral(final FullQualifiedName fullName) {
    if (!(curExpression instanceof TypeLiteral)) {
      fail("Current expression not a typeLiteral");
    }

    isType(fullName);
    return this;
  }

  public FilterValidator isMember() {
    if (!(curExpression instanceof Member)) {
      fail("Current expression not a member");
    }
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
    if (!(curExpression instanceof Enumeration)) {
      fail("Current expression not a enumeration");
    }

    Enumeration enumeration = (Enumeration) curExpression;

    // check name
    assertEquals(name, enumeration.getType().getFullQualifiedName());

    // check values
    assertEquals(enumValues, enumeration.getValues());

    return this;
  }

  public FilterValidator isAlias(final String name) {
    if (curExpression instanceof Alias) {
      final Alias alias = (Alias) curExpression;
      assertEquals(name, alias.getParameterName());
    } else {
      fail("Current expression is not an alias.");
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

  public FilterValidator isExValidation(final UriValidationException.MessageKeys messageKey) {
    assertEquals(UriValidationException.class, exception.getClass());
    assertEquals(messageKey, exception.getMessageKey());
    return this;
  }
}
