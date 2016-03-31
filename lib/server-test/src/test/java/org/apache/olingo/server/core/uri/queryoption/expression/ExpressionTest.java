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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceActionImpl;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.testutil.FilterTreeToText;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EnumTypeProvider;
import org.apache.olingo.server.tecsvc.provider.FunctionProvider;
import org.junit.Test;

public class ExpressionTest {
  private static final OData odata = OData.newInstance();
  private static final Edm edm = odata.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  @Test
  public void supportedOperators() {
    assertEquals(UnaryOperatorKind.MINUS, UnaryOperatorKind.get("-"));
    assertEquals(null, UnaryOperatorKind.get("XXX"));

    assertEquals(BinaryOperatorKind.MOD, BinaryOperatorKind.get("mod"));
    assertEquals(null, BinaryOperatorKind.get("XXX"));

    assertEquals(MethodKind.CONCAT, MethodKind.get("concat"));
    assertEquals(null, MethodKind.get("XXX"));
  }

  @Test
  public void aliasExpression() throws Exception {
    AliasImpl expression = new AliasImpl("@Test", null);

    assertEquals("@Test", expression.getParameterName());

    String output = expression.accept(new FilterTreeToText());
    assertEquals("<@Test>", output);
  }

  @Test
  public void binaryExpression() throws Exception {
    Expression expressionLeft = new LiteralImpl("2", odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte));
    Expression expressionRight = new LiteralImpl("-1", odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte));

    BinaryImpl expression = new BinaryImpl(expressionLeft, BinaryOperatorKind.SUB, expressionRight,
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte));

    assertEquals(expressionLeft, expression.getLeftOperand());
    assertEquals(expressionRight, expression.getRightOperand());
    assertEquals(BinaryOperatorKind.SUB, expression.getOperator());
    assertEquals(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte), expression.getType());

    String output = expression.accept(new FilterTreeToText());
    assertEquals("<<2> sub <-1>>", output);
  }

  @Test
  public void enumerationExpression() throws Exception {
    EdmEnumType type = edm.getEnumType(EnumTypeProvider.nameENString);
    assertNotNull(type);
    EnumerationImpl expression = new EnumerationImpl(type, Arrays.asList("String1", "String2"));
    assertEquals(type, expression.getType());
    assertEquals("String1", expression.getValues().get(0));
    assertEquals("String2", expression.getValues().get(1));
    assertEquals("<olingo.odata.test1.ENString<String1,String2>>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void lambdaRefExpression() throws Exception {
    LambdaRefImpl expression = new LambdaRefImpl("A");
    assertEquals("A", expression.getVariableName());
    assertEquals("<A>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void literalExpression() throws Exception {
    LiteralImpl expression = new LiteralImpl("'A'", odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    assertEquals("'A'", expression.getText());
    assertEquals("<'A'>", expression.accept(new FilterTreeToText()));
    assertEquals(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String), expression.getType());
  }

  @Test
  public void memberExpression() throws Exception {
    EdmEntityType entityType = edm.getEntityType(EntityTypeProvider.nameETKeyNav);

    // UriResourceImpl
    EdmAction action = edm.getUnboundAction(ActionProvider.nameUARTString);
    UriInfoResource uriInfo = new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceActionImpl(action)).asUriInfoResource();
    MemberImpl expression = new MemberImpl(uriInfo, null);
    assertEquals(action.getReturnType().getType(), expression.getType());

    // check accept and path
    assertEquals(uriInfo, expression.getResourcePath());
    assertEquals("<UARTString>", expression.accept(new FilterTreeToText()));

    // UriResourceImplTyped check collection = false case
    assertFalse(expression.isCollection());

    // UriResourceImplTyped check collection = true case
    action = edm.getUnboundAction(ActionProvider.nameUARTCollStringTwoParam);
    expression = new MemberImpl(new UriInfoImpl().setKind(UriInfoKind.resource)
        .addResourcePart(new UriResourceActionImpl(action))
        .asUriInfoResource(),
        null);
    assertTrue(expression.isCollection());

    // UriResourceImplTyped with filter
    EdmFunction function = edm.getUnboundFunction(FunctionProvider.nameUFCRTETKeyNav, null);
    expression = new MemberImpl(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl(null, function, null).setEntryTypeFilter(entityType))
        .asUriInfoResource(),
        null);
    assertEquals(entityType, expression.getType());

    // UriResourceImplKeyPred
    function = edm.getUnboundFunction(FunctionProvider.nameUFCRTETKeyNav, null);
    expression = new MemberImpl(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl(null, function, null))
        .asUriInfoResource(),
        null);
    assertEquals(function.getReturnType().getType(), expression.getType());

    // UriResourceImplKeyPred typeFilter on entry
    EdmEntityType entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    function = edm.getUnboundFunction(FunctionProvider.nameUFCRTCollETTwoKeyNavParam, Arrays.asList("ParameterInt16"));
    expression = new MemberImpl(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl(null, function, null).setEntryTypeFilter(entityBaseType))
        .asUriInfoResource(),
        null);
    assertEquals(entityBaseType, expression.getType());

    // UriResourceImplKeyPred typeFilter on entry
    entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    function = edm.getUnboundFunction(FunctionProvider.nameUFCRTCollETTwoKeyNavParam, Arrays.asList("ParameterInt16"));
    expression = new MemberImpl(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
        new UriResourceFunctionImpl(null, function, null).setCollectionTypeFilter(entityBaseType))
        .asUriInfoResource(),
        null);
    assertEquals(entityBaseType, expression.getType());

    // no typed
    entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    function = edm.getUnboundFunction(FunctionProvider.nameUFCRTCollETTwoKeyNavParam, Arrays.asList("ParameterInt16"));
    expression = new MemberImpl(new UriInfoImpl().setKind(UriInfoKind.all), null);
    assertEquals(null, expression.getType());

    // no typed collection else case
    assertFalse(expression.isCollection());
  }

  @Test
  public void methodCallExpression() throws Exception {
    Expression p0 = new LiteralImpl("'A'", odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    Expression p1 = new LiteralImpl("'B'", odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    MethodImpl expression = new MethodImpl(MethodKind.CONCAT, Arrays.asList(p0, p1));

    assertEquals(MethodKind.CONCAT, expression.getMethod());
    assertEquals("<concat(<'A'>,<'B'>)>", expression.accept(new FilterTreeToText()));
    assertEquals(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String), expression.getType());

    assertEquals(p0, expression.getParameters().get(0));
    assertEquals(p1, expression.getParameters().get(1));
  }

  @Test
  public void typeLiteralExpression() throws Exception {
    EdmEntityType entityBaseType = edm.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav);
    TypeLiteralImpl expression = new TypeLiteralImpl(entityBaseType);

    assertEquals(entityBaseType, expression.getType());
    assertEquals("<olingo.odata.test1.ETBaseTwoKeyNav>", expression.accept(new FilterTreeToText()));
  }

  @Test
  public void unaryExpression() throws Exception {
    Expression operand = new LiteralImpl("1.2", odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal));
    UnaryImpl expression = new UnaryImpl(UnaryOperatorKind.MINUS, operand,
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal));

    assertEquals(UnaryOperatorKind.MINUS, expression.getOperator());
    assertEquals(operand, expression.getOperand());
    assertEquals(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal), expression.getType());

    assertEquals("<- <1.2>>", expression.accept(new FilterTreeToText()));
  }
}
