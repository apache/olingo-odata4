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
package org.apache.olingo.server.core.uri.queryoption.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceActionImpl;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.testutil.EdmTechTestProvider;
import org.apache.olingo.server.core.uri.testutil.FilterTreeToText;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EnumTypeProvider;
import org.apache.olingo.server.tecsvc.provider.FunctionProvider;
import org.junit.Test;

public class ExpressionTest {
  Edm edm = new EdmProviderImpl(new EdmTechTestProvider());

  @Test
  public void testSupportedOperators() {
    assertEquals(UnaryOperatorKind.MINUS, UnaryOperatorKind.get("-"));
    assertEquals(null, UnaryOperatorKind.get("XXX"));

    assertEquals(BinaryOperatorKind.MOD, BinaryOperatorKind.get("mod"));
    assertEquals(null, BinaryOperatorKind.get("XXX"));

    assertEquals(MethodKind.CONCAT, MethodKind.get("concat"));
    assertEquals(null, MethodKind.get("XXX"));
  }

  @Test
  public void testAliasExpression() throws ExpressionVisitException, ODataApplicationException {
    AliasImpl expression = new AliasImpl();

    expression.setParameter("Test");

    assertEquals("Test", expression.getParameterName());

    String output = expression.accept(new FilterTreeToText());
    assertEquals("<Test>", output);

  }

  @Test
  public void testBinaryExpression() throws ExpressionVisitException, ODataApplicationException {
    BinaryImpl expression = new BinaryImpl();

    ExpressionImpl expressionLeft = new LiteralImpl().setText("A");
    ExpressionImpl expressionRight = new LiteralImpl().setText("B");

    expression.setLeftOperand(expressionLeft);
    expression.setRightOperand(expressionRight);
    expression.setOperator(BinaryOperatorKind.SUB);

    assertEquals(expressionLeft, expression.getLeftOperand());
    assertEquals(expressionRight, expression.getRightOperand());
    assertEquals(BinaryOperatorKind.SUB, expression.getOperator());

    String output = expression.accept(new FilterTreeToText());
    assertEquals("<<A> sub <B>>", output);
  }

  @Test
  public void testEnumerationExpression() throws ExpressionVisitException, ODataApplicationException {
    EnumerationImpl expression = new EnumerationImpl();
    EdmEnumType type = (EdmEnumType) edm.getEnumType(EnumTypeProvider.nameENString);
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
  public void testLambdaRefExpression() throws ExpressionVisitException, ODataApplicationException {
    LambdaRefImpl expression = new LambdaRefImpl();
    expression.setVariableText("A");
    assertEquals("A", expression.getVariableName());

    assertEquals("<A>", expression.accept(new FilterTreeToText()));

  }

  @Test
  public void testLiteralExpresion() throws ExpressionVisitException, ODataApplicationException {
    LiteralImpl expression = new LiteralImpl();
    expression.setText("A");
    assertEquals("A", expression.getText());

    assertEquals("<A>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void testMemberExpression() throws ExpressionVisitException, ODataApplicationException {
    MemberImpl expression = new MemberImpl();
    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETKeyNav);

    // UriResourceImplTyped
    EdmAction action = edm.getUnboundAction(ActionProvider.nameUARTPrimParam);
    UriInfoResource uriInfo = new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceActionImpl().setAction(action)).asUriInfoResource();
    expression.setResourcePath(uriInfo);
    assertEquals(action.getReturnType().getType(), expression.getType());

    // check accept and path
    assertEquals(uriInfo, expression.getResourcePath());
    assertEquals("<UARTPrimParam>", expression.accept(new FilterTreeToText()));

    // UriResourceImplTyped check collection = false case
    assertEquals(false, expression.isCollection());

    // UriResourceImplTyped check collection = true case
    action = edm.getUnboundAction(ActionProvider.nameUARTPrimCollParam);
    expression.setResourcePath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceActionImpl().setAction(action))
        .asUriInfoResource());
    assertEquals(true, expression.isCollection());

    // UriResourceImplTyped with filter
    action = edm.getUnboundAction(ActionProvider.nameUARTPrimParam);
    expression.setResourcePath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceActionImpl().setAction(action).setTypeFilter(entityType))
        .asUriInfoResource());
    assertEquals(entityType, expression.getType());

    // UriResourceImplKeyPred
    EdmFunction function = edm.getUnboundFunction(FunctionProvider.nameUFCRTETKeyNav, null);
    expression.setResourcePath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl().setFunction(function))
        .asUriInfoResource());
    assertEquals(function.getReturnType().getType(), expression.getType());

    // UriResourceImplKeyPred typeFilter on entry
    EdmEntityType entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    function = edm.getUnboundFunction(FunctionProvider.nameUFCRTESTwoKeyNavParam, Arrays.asList("ParameterInt16"));
    expression.setResourcePath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl().setFunction(function).setEntryTypeFilter(entityBaseType))
        .asUriInfoResource());
    assertEquals(entityBaseType, expression.getType());

    // UriResourceImplKeyPred typeFilter on entry
    entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    function = edm.getUnboundFunction(FunctionProvider.nameUFCRTESTwoKeyNavParam, Arrays.asList("ParameterInt16"));
    expression.setResourcePath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl().setFunction(function).setCollectionTypeFilter(entityBaseType))
        .asUriInfoResource());
    assertEquals(entityBaseType, expression.getType());

    // no typed
    entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    function = edm.getUnboundFunction(FunctionProvider.nameUFCRTESTwoKeyNavParam, Arrays.asList("ParameterInt16"));
    expression.setResourcePath(new UriInfoImpl().setKind(UriInfoKind.all));
    assertEquals(null, expression.getType());

    // no typed collection else case
    assertEquals(false, expression.isCollection());
  }

  @Test
  public void testMethodCallExpression() throws ExpressionVisitException, ODataApplicationException {
    MethodImpl expression = new MethodImpl();
    expression.setMethod(MethodKind.CONCAT);

    ExpressionImpl p0 = new LiteralImpl().setText("A");
    ExpressionImpl p1 = new LiteralImpl().setText("B");
    expression.addParameter(p0);
    expression.addParameter(p1);

    assertEquals(MethodKind.CONCAT, expression.getMethod());
    assertEquals("<concat(<A>,<B>)>", expression.accept(new FilterTreeToText()));

    assertEquals(p0, expression.getParameters().get(0));
    assertEquals(p1, expression.getParameters().get(1));
  }

  @Test
  public void testTypeLiteralExpression() throws ExpressionVisitException, ODataApplicationException {
    TypeLiteralImpl expression = new TypeLiteralImpl();
    EdmEntityType entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    expression.setType(entityBaseType);

    assertEquals(entityBaseType, expression.getType());
    assertEquals("<com.sap.odata.test1.ETBaseTwoKeyNav>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void testUnaryExpression() throws ExpressionVisitException, ODataApplicationException {
    UnaryImpl expression = new UnaryImpl();
    expression.setOperator(UnaryOperatorKind.MINUS);

    ExpressionImpl operand = new LiteralImpl().setText("A");
    expression.setOperand(operand);

    assertEquals(UnaryOperatorKind.MINUS, expression.getOperator());
    assertEquals(operand, expression.getOperand());

    assertEquals("<- <A>>", expression.accept(new FilterTreeToText()));
  }

}
