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
package org.apache.olingo.odata4.producer.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfo;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.Expression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.Member;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedConstants;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedMethodCalls;
import org.apache.olingo.odata4.producer.core.uri.Parser;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriParseTreeVisitor;
import org.apache.olingo.odata4.producer.core.uri.UriParserException;
import org.apache.olingo.odata4.producer.core.uri.UriParserSemanticException;
import org.apache.olingo.odata4.producer.core.uri.UriParserSyntaxException;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.ConstantImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.EnumerationImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.MethodCallImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.TypeLiteralImpl;

public class FilterValidator implements Validator {
  private Edm edm;

  private Validator invokedByValidator;
  private FilterOptionImpl filter;

  private Expression curExpression;
  private Expression rootExpression;

  private OrderByOptionImpl orderBy;

  private UriParserException exception;

  // --- Setup ---
  public FilterValidator setUriResourcePathValidator(final UriResourceValidator uriResourcePathValidator) {
    invokedByValidator = uriResourcePathValidator;
    return this;
  }

  public FilterValidator setUriValidator(final UriValidator uriValidator) {
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
    String uri = "ESAllPrim?$orderby=" + orderBy.trim();
    return runUriOrderBy(uri);
  }

  public FilterValidator runOrderByOnETTwoKeyNav(final String orderBy) throws UriParserException {
    String uri = "ESTwoKeyNav?$orderby=" + orderBy.trim();
    return runUriOrderBy(uri);
  }
  
  public FilterValidator runOrderByOnETTwoKeyNavEx(final String orderBy) throws UriParserException {
    String uri = "ESTwoKeyNav?$orderby=" + orderBy.trim();
    return runUriOrderByEx(uri);
  }

  public FilterValidator runOnETTwoKeyNav(final String filter) throws UriParserException {
    // TODO change to ESTwoKeyNav
    String uri = "SINav?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnETTwoKeyNavEx(final String filter) throws UriParserException {
    // TODO change to ESTwoKeyNav
    String uri = "SINav?$filter=" + filter.trim();
    return runUriEx(uri);
  }

  public FilterValidator runOnETAllPrim(final String filter) throws UriParserException {
    String uri = "ESAllPrim(1)?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnETKeyNav(final String filter) throws UriParserException {
    String uri = "ESKeyNav(1)?$filter=" + filter.trim();
    return runUri(uri);
  }
  
  public FilterValidator runOnETKeyNavEx(final String filter) throws UriParserException {
    String uri = "ESKeyNav(1)?$filter=" + filter.trim();
    return runUriEx(uri);
  }

  public FilterValidator runOnCTTwoPrim(final String filter) throws UriParserException {
    String uri = "SINav/PropertyComplexTwoPrim?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnString(final String filter) throws UriParserException {
    String uri = "SINav/PropertyString?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnInt32(final String filter) throws UriParserException {
    String uri = "ESCollAllPrim(1)/CollPropertyInt32?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnDateTimeOffset(final String filter) throws UriParserException {
    String uri = "ESCollAllPrim(1)/CollPropertyDateTimeOffset?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnDuration(final String filter) throws UriParserException {
    String uri = "ESCollAllPrim(1)/CollPropertyDuration?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnTimeOfDay(final String filter) throws UriParserException {
    String uri = "ESCollAllPrim(1)/CollPropertyTimeOfDay?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runESabc(final String filter) throws UriParserException {
    String uri = "ESabc?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runUri(final String uri) throws UriParserException {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    uriInfo = parser.parseUri(uri, new UriParseTreeVisitor(edm));

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setFilter((FilterOptionImpl) uriInfo.getFilterOption());
    curExpression = filter.getExpression();
    return this;
  }

  public FilterValidator runUriEx(final String uri) {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    try {
      uriInfo = parser.parseUri(uri, new UriParseTreeVisitor(edm));
    } catch (UriParserException e) {
      this.exception = e;
      return this;
    }

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setFilter((FilterOptionImpl) uriInfo.getFilterOption());
    curExpression = filter.getExpression();
    return this;
  }

  public FilterValidator runUriOrderBy(final String uri) throws UriParserException {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    uriInfo = parser.parseUri(uri, new UriParseTreeVisitor(edm));

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setOrderBy((OrderByOptionImpl) uriInfo.getOrderByOption());
    return this;
  }
  
  public FilterValidator runUriOrderByEx(final String uri) {
    Parser parser = new Parser();
    UriInfo uriInfo = null;

    try {
      uriInfo = parser.parseUri(uri, new UriParseTreeVisitor(edm));
    } catch (UriParserException e) {
      this.exception = e;
      return this;
    }

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setOrderBy((OrderByOptionImpl) uriInfo.getOrderByOption());
    return this;
  }


  // --- Navigation ---

  public Validator goUp() {
    return invokedByValidator;
  }

  public UriResourceValidator goPath() {
    if (!(curExpression instanceof MemberImpl)) {
      fail("Current expression not a member");
    }

    MemberImpl member = (MemberImpl) curExpression;
    UriResourceValidator uriValidator = new UriResourceValidator();
    uriValidator.setEdm(edm);
    uriValidator.setUriInfoImplPath((UriInfoImpl) member.getPath());
    uriValidator.setUpValidator(this);
    return uriValidator;
  }

  public FilterValidator goParameter(final int parameterIndex) {
    if (curExpression instanceof MethodCallImpl) {
      MethodCallImpl methodCall = (MethodCallImpl) curExpression;

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
   * @return
   */
  public FilterValidator isCompr(final String toBeCompr) {
    return is(compress(toBeCompr));
  }

  public FilterValidator is(final String expectedFilterAsString) {
    try {
      String actualFilterAsText = FilterTreeToText.Serialize((FilterOptionImpl) filter);
      assertEquals(expectedFilterAsString, actualFilterAsText);
    } catch (ExceptionVisitExpression e) {
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

  public FilterValidator isMethod(final SupportedMethodCalls methodKind, final int parameterCount) {
    if (!(curExpression instanceof MethodCallImpl)) {
      fail("Current expression is not a methodCall");
    }

    MethodCallImpl methodCall = (MethodCallImpl) curExpression;
    assertEquals(methodKind, methodCall.getMethod());
    assertEquals(parameterCount, methodCall.getParameters().size());

    return this;
  }

  
  public FilterValidator isParameterText(final int parameterIndex, final String parameterText)
      throws ExceptionVisitExpression, ODataApplicationException {

    if (!(curExpression instanceof MethodCallImpl)) {
      fail("Current expression is not a method");
    }

    MethodCallImpl methodCall = (MethodCallImpl) curExpression;

    Expression parameter = methodCall.getParameters().get(parameterIndex);
    String actualParameterText = FilterTreeToText.Serialize(parameter);
    assertEquals(parameterText, actualParameterText);

    return this;
  }

  public FilterValidator isBinary(final SupportedBinaryOperators binaryOperator) {
    if (!(curExpression instanceof BinaryImpl)) {
      fail("Current expression is not a binary operator");
    }

    BinaryImpl binary = (BinaryImpl) curExpression;
    assertEquals(binaryOperator, binary.getOperator());

    return this;
  }

  public FilterValidator isTypedLiteral(FullQualifiedName fullName) {
    if (!(curExpression instanceof TypeLiteralImpl)) {
      fail("Current expression not a typeLiteral");
    }

    this.isType(fullName);

    return this;
  }

  public FilterValidator isMember() {
    if (!(curExpression instanceof MemberImpl)) {
      fail("Current expression not a member");
    }

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

  public FilterValidator isConstant(SupportedConstants kind) {
    if (!(curExpression instanceof ConstantImpl)) {
      fail("Current expression not a constant");
    }

    assertEquals(kind, ((ConstantImpl) curExpression).getKind());

    return this;
  }

  public FilterValidator isSortOrder(int index, boolean descending) {
    assertEquals(descending, orderBy.getOrders().get(index).isDescending());
    return this;
  }

  public FilterValidator goOrder(int index) {
    curExpression = orderBy.getOrders().get(index).getExpression();
    return this;
  }

  public FilterValidator isExSyntax(long errorID) {
    assertEquals(UriParserSyntaxException.class, exception.getClass());
    return this;
  }

  public FilterValidator isExSemantic(long errorID) {
    assertEquals(UriParserSemanticException.class, exception.getClass());
    return this;
  }

}
