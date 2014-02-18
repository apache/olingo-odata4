/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.server.core.uri.queryoption.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.server.api.uri.UriInfoKind;
import org.apache.olingo.odata4.server.api.uri.UriInfoResource;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedBinaryOperators;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedConstants;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedMethodCalls;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.SupportedUnaryOperators;
import org.apache.olingo.odata4.server.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.server.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.server.core.testutil.FilterTreeToText;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriInfoImpl;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriResourceActionImpl;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriResourceFunctionImpl;
import org.junit.Test;

public class ExpressionTest {
  Edm edm = new EdmProviderImpl(new EdmTechTestProvider());
  
  @Test
  public void testSupportedOperators() {
    assertEquals(SupportedUnaryOperators.MINUS, SupportedUnaryOperators.get("-") );
    assertEquals(null, SupportedUnaryOperators.get("XXX") );
    
    assertEquals(SupportedBinaryOperators.MOD, SupportedBinaryOperators.get("mod") );
    assertEquals(null, SupportedBinaryOperators.get("XXX") );
    
    assertEquals(SupportedMethodCalls.CONCAT, SupportedMethodCalls.get("concat") );
    assertEquals(null, SupportedMethodCalls.get("XXX") );
    
    assertEquals(SupportedConstants.TRUE, SupportedConstants.get("true") );
    assertEquals(null, SupportedConstants.get("XXX") );
  }

  @Test
  public void testAliasExpression() throws ExceptionVisitExpression, ODataApplicationException {
    AliasImpl expression = new AliasImpl();

    expression.setParameter("Test");

    assertEquals("Test", expression.getParameterName());

    String output = expression.accept(new FilterTreeToText());
    assertEquals("<Test>", output);

  }

  @Test
  public void testBinaryExpression() throws ExceptionVisitExpression, ODataApplicationException {
    BinaryImpl expression = new BinaryImpl();

    ExpressionImpl expressionLeft = new LiteralImpl().setText("A");
    ExpressionImpl expressionRight = new LiteralImpl().setText("B");

    expression.setLeftOperand(expressionLeft);
    expression.setRightOperand(expressionRight);
    expression.setOperator(SupportedBinaryOperators.SUB);

    assertEquals(expressionLeft, expression.getLeftOperand());
    assertEquals(expressionRight, expression.getRightOperand());
    assertEquals(SupportedBinaryOperators.SUB, expression.getOperator());

    String output = expression.accept(new FilterTreeToText());
    assertEquals("<<A> sub <B>>", output);
  }

  @Test
  public void testConstantExpression() throws ExceptionVisitExpression, ODataApplicationException {
    ConstantImpl expression = new ConstantImpl();
    EdmType type = edm.getEntityType(EdmTechProvider.nameETKeyNav);
    assertNotNull(type);
    expression.setType(type);
    assertEquals(type, expression.getType());

    expression.setKind(SupportedConstants.FALSE);
    assertEquals(SupportedConstants.FALSE, expression.getKind());
    assertEquals(true, expression.isFalse());
    assertEquals(false, expression.isTrue());
    assertEquals(false, expression.isNull());
    assertEquals("<false>", expression.accept(new FilterTreeToText()));

    expression.setKind(SupportedConstants.TRUE);
    assertEquals(SupportedConstants.TRUE, expression.getKind());
    assertEquals(false, expression.isFalse());
    assertEquals(true, expression.isTrue());
    assertEquals(false, expression.isNull());
    assertEquals("<true>", expression.accept(new FilterTreeToText()));

    expression.setKind(SupportedConstants.NULL);
    assertEquals(SupportedConstants.NULL, expression.getKind());
    assertEquals(false, expression.isFalse());
    assertEquals(false, expression.isTrue());
    assertEquals(true, expression.isNull());
    assertEquals("<null>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void testEnumerationExpression() throws ExceptionVisitExpression, ODataApplicationException {
    EnumerationImpl expression = new EnumerationImpl();
    EdmEnumType type = (EdmEnumType) edm.getEnumType(EdmTechProvider.nameENString);
    assertNotNull(type);
    expression.setType(type);

    assertEquals(type, expression.getType());

    expression.addValue("A");
    expression.addValue("B");
    assertEquals("A", expression.getValues().get(0));
    assertEquals("B", expression.getValues().get(1));
    assertEquals("<com.sap.odata.test1.ENString<A,B>>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void testLambdaRefExpression() throws ExceptionVisitExpression, ODataApplicationException {
    LambdaRefImpl expression = new LambdaRefImpl();
    expression.setVariableText("A");
    assertEquals("A", expression.getVariableName());

    assertEquals("<A>", expression.accept(new FilterTreeToText()));

  }

  @Test
  public void testLiteralExpresion() throws ExceptionVisitExpression, ODataApplicationException {
    LiteralImpl expression = new LiteralImpl();
    expression.setText("A");
    assertEquals("A", expression.getText());

    assertEquals("<A>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void testMemberExpression() throws ExceptionVisitExpression, ODataApplicationException {
    MemberImpl expression = new MemberImpl();
    EdmEntityType entityType = edm.getEntityType(EdmTechProvider.nameETKeyNav);

    // UriResourceImplTyped
    UriInfoImpl resource = new UriInfoImpl().setKind(UriInfoKind.resource);
    EdmAction action = edm.getAction(EdmTechProvider.nameUARTPrimParam, null, null);
    UriInfoResource uriInfo = new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceActionImpl().setAction(action)).asUriInfoResource();
    expression.setPath(uriInfo);
    assertEquals(action.getReturnType().getType(), expression.getType());

    // check accept and path
    assertEquals(uriInfo, expression.getPath());
    assertEquals("<UARTPrimParam>", expression.accept(new FilterTreeToText()));

    // UriResourceImplTyped check collection = false case
    assertEquals(false, expression.isCollection());

    // UriResourceImplTyped check collection = true case
    resource = new UriInfoImpl().setKind(UriInfoKind.resource);
    action = edm.getAction(EdmTechProvider.nameUARTPrimCollParam, null, null);
    expression.setPath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceActionImpl().setAction(action))
        .asUriInfoResource());
    assertEquals(true, expression.isCollection());

    // UriResourceImplTyped with filter
    resource = new UriInfoImpl().setKind(UriInfoKind.resource);
    action = edm.getAction(EdmTechProvider.nameUARTPrimParam, null, null);
    expression.setPath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceActionImpl().setAction(action).setTypeFilter(entityType))
        .asUriInfoResource());
    assertEquals(entityType, expression.getType());

    // UriResourceImplKeyPred
    resource = new UriInfoImpl().setKind(UriInfoKind.resource);
    EdmFunction function = edm.getFunction(EdmTechProvider.nameUFCRTETKeyNav, null, null, null);
    expression.setPath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl().setFunction(function))
        .asUriInfoResource());
    assertEquals(function.getReturnType().getType(), expression.getType());

    // UriResourceImplKeyPred typeFilter on entry
    resource = new UriInfoImpl().setKind(UriInfoKind.resource);
    EdmEntityType entityBaseType = edm.getEntityType(EdmTechProvider.nameETBaseTwoKeyNav);
    function = edm.getFunction(EdmTechProvider.nameUFCRTESTwoKeyNavParam, null, null,
        Arrays.asList(("ParameterInt16")));
    expression.setPath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl().setFunction(function).setEntryTypeFilter(entityBaseType))
        .asUriInfoResource());
    assertEquals(entityBaseType, expression.getType());

    // UriResourceImplKeyPred typeFilter on entry
    resource = new UriInfoImpl().setKind(UriInfoKind.resource);
    entityBaseType = edm.getEntityType(EdmTechProvider.nameETBaseTwoKeyNav);
    function = edm.getFunction(EdmTechProvider.nameUFCRTESTwoKeyNavParam, null, null,
        Arrays.asList(("ParameterInt16")));
    expression.setPath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl().setFunction(function).setCollectionTypeFilter(entityBaseType))
        .asUriInfoResource());
    assertEquals(entityBaseType, expression.getType());

    // no typed
    resource = new UriInfoImpl().setKind(UriInfoKind.resource);
    entityBaseType = edm.getEntityType(EdmTechProvider.nameETBaseTwoKeyNav);
    function = edm.getFunction(EdmTechProvider.nameUFCRTESTwoKeyNavParam, null, null,
        Arrays.asList(("ParameterInt16")));
    expression.setPath(new UriInfoImpl().setKind(UriInfoKind.all));
    assertEquals(null, expression.getType());

    // no typed collection else case
    assertEquals(false, expression.isCollection());
  }

  @Test
  public void testMethodCallExpression() throws ExceptionVisitExpression, ODataApplicationException {
    MethodCallImpl expression = new MethodCallImpl();
    expression.setMethod(SupportedMethodCalls.CONCAT);

    ExpressionImpl p0 = new LiteralImpl().setText("A");
    ExpressionImpl p1 = new LiteralImpl().setText("B");
    expression.addParameter(p0);
    expression.addParameter(p1);

    assertEquals(SupportedMethodCalls.CONCAT, expression.getMethod());
    assertEquals("<concat(<A>,<B>)>", expression.accept(new FilterTreeToText()));

    assertEquals(p0, expression.getParameters().get(0));
    assertEquals(p1, expression.getParameters().get(1));
  }
  
  @Test
  public void testTypeLiteralExpression() throws ExceptionVisitExpression, ODataApplicationException {
    TypeLiteralImpl expression = new TypeLiteralImpl();
    EdmEntityType entityBaseType = edm.getEntityType(EdmTechProvider.nameETBaseTwoKeyNav);
    expression.setType(entityBaseType);
        
    assertEquals(entityBaseType, expression.getType());
    assertEquals("<com.sap.odata.test1.ETBaseTwoKeyNav>", expression.accept(new FilterTreeToText()));
  }
  
  @Test
  public void testUnaryExpression() throws ExceptionVisitExpression, ODataApplicationException {
    UnaryImpl expression = new UnaryImpl();
    expression.setOperator(SupportedUnaryOperators.MINUS);
    
    ExpressionImpl operand = new LiteralImpl().setText("A");
    expression.setOperand(operand);
        
    assertEquals(SupportedUnaryOperators.MINUS, expression.getOperator());
    assertEquals(operand, expression.getOperand());
    
    assertEquals("<- <A>>", expression.accept(new FilterTreeToText()));
  }


}
