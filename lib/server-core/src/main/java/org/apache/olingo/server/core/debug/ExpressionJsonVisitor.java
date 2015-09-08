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
package org.apache.olingo.server.core.debug;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

/**
 * A custom expression visitor which writes down the tree from top to bottom
 */
public class ExpressionJsonVisitor implements ExpressionVisitor<String> {

  private static final String ANY_NAME = "ANY";
  private static final String ALL_NAME = "ALL";
  private static final String STRING_NAME = "String";
  private static final String UNKNOWN_NAME = "unknown";
  private static final String BOOLEAN_NAME = "Boolean";
  private static final String NUMBER_NAME = "Number";
  private static final String ENUM_NAME = "enum";
  private static final String VALUES_NAME = "values";
  private static final String NAME_NAME = "name";
  private static final String LAMBDA_REFERENCE_NAME = "lambdaReference";
  private static final String ALIAS_NAME = "alias";
  private static final String RESOURCE_SEGMENTS_NAME = "resourceSegments";
  private static final String MEMBER_NAME = "member";
  private static final String VALUE_NAME = "value";
  private static final String LITERAL_NAME = "literal";
  private static final String EXPRESSION_NAME = "expression";
  private static final String LAMBDA_VARIABLE_NAME = "lambdaVariable";
  private static final String LAMBDA_FUNCTION_NAME = "lambdaFunction";
  private static final String UNARY_NAME = "unary";
  private static final String BINARY_NAME = "binary";
  private static final String LEFT_NODE_NAME = "left";
  private static final String RIGHT_NODE_NAME = "right";
  private static final String IO_EXCEPTION_OCCURRED_MESSAGE = "IOException occurred";
  private static final String PARAMETERS_NAME = "parameters";
  private static final String METHOD_NAME = "method";
  private static final String OPERAND_NAME = "operand";
  private static final String TYPE_NAME = "type";
  private static final String OPERATOR_NAME = "operator";
  private static final String NODE_TYPE_NAME = "nodeType";

  @Override
  public String visitBinaryOperator(BinaryOperatorKind operator, String left, String right)
      throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValue(NODE_TYPE_NAME, BINARY_NAME).separator().namedStringValue(
          OPERATOR_NAME, operator.toString()).separator().namedStringValueRaw(TYPE_NAME, getType(operator)).separator()
          .name(LEFT_NODE_NAME).unquotedValue(left).separator().name(RIGHT_NODE_NAME).unquotedValue(right).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitUnaryOperator(UnaryOperatorKind operator, String operand) throws ExpressionVisitException,
      ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValue(NODE_TYPE_NAME, UNARY_NAME).separator()
          .namedStringValueRaw(OPERATOR_NAME, operator.toString()).separator().namedStringValueRaw(TYPE_NAME,
              getType(operator)).separator().name(OPERAND_NAME).unquotedValue(operand).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitMethodCall(MethodKind methodCall, List<String> parameters) throws ExpressionVisitException,
      ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw(NODE_TYPE_NAME, METHOD_NAME).separator()
          .namedStringValueRaw(OPERATOR_NAME, methodCall.toString()).separator().namedStringValueRaw(TYPE_NAME,
              getType(methodCall)).separator().name(PARAMETERS_NAME).beginArray();
      boolean first = true;
      for (String parameter : parameters) {
        if (first) {
          first = false;
        } else {
          jsonStreamWriter.separator();
        }
        jsonStreamWriter.unquotedValue(parameter);
      }
      jsonStreamWriter.endArray().endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression)
      throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValue(NODE_TYPE_NAME, LAMBDA_FUNCTION_NAME).separator()
          .namedStringValue(LAMBDA_VARIABLE_NAME, lambdaVariable).separator().name(EXPRESSION_NAME);

      // Write expression string object
      String expressionJsonTree = expression.accept(this);
      jsonStreamWriter.unquotedValue(expressionJsonTree).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw(NODE_TYPE_NAME, LITERAL_NAME).separator().namedStringValueRaw(
          TYPE_NAME, getTypeString(literal.getType())).separator().namedStringValue(VALUE_NAME, literal.getText())
          .endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE);
    }
  }

  @Override
  public String visitMember(UriInfoResource member) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      List<UriResource> uriResourceParts = member.getUriResourceParts();
      jsonStreamWriter.beginObject().namedStringValue(NODE_TYPE_NAME, MEMBER_NAME).separator()
          .namedStringValueRaw(TYPE_NAME, getType(uriResourceParts)).separator();

      // write all member properties in an array
      jsonStreamWriter.name(RESOURCE_SEGMENTS_NAME).beginArray();
      if (uriResourceParts != null) {
        boolean first = true;
        for (UriResource segment : uriResourceParts) {
          if (first) {
            first = false;
          } else {
            jsonStreamWriter.separator();
          }
          appendUriResourcePartObject(jsonStreamWriter, segment);
        }
      }
      jsonStreamWriter.endArray();

      jsonStreamWriter.endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw(NODE_TYPE_NAME, ALIAS_NAME).separator()
          .namedStringValue(ALIAS_NAME, aliasName).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw(NODE_TYPE_NAME, TYPE_NAME).separator()
          .namedStringValueRaw(TYPE_NAME, getTypeString(type)).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitLambdaReference(String variableName) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw(NODE_TYPE_NAME, LAMBDA_REFERENCE_NAME).separator()
          .namedStringValueRaw(NAME_NAME, variableName).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  @Override
  public String visitEnum(EdmEnumType type, List<String> enumValues) throws ExpressionVisitException,
      ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw(NODE_TYPE_NAME, ENUM_NAME).separator()
          .namedStringValueRaw(TYPE_NAME, getTypeString(type)).separator();
      jsonStreamWriter.name(VALUES_NAME).beginArray();
      if (enumValues != null) {
        boolean first = true;
        for (String value : enumValues) {
          if (first) {
            first = false;
          } else {
            jsonStreamWriter.separator();
          }
          jsonStreamWriter.stringValue(value);
        }
      }
      jsonStreamWriter.endArray();

      jsonStreamWriter.endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException(IO_EXCEPTION_OCCURRED_MESSAGE, e);
    }
  }

  private String getType(UnaryOperatorKind operator) {
    switch (operator) {
    case MINUS:
      return NUMBER_NAME;
    case NOT:
      return BOOLEAN_NAME;
    default:
      return UNKNOWN_NAME;
    }
  }

  private String getType(MethodKind methodCall) {
    switch (methodCall) {
    case STARTSWITH:
    case CONTAINS:
    case ENDSWITH:
    case ISOF:
      return BOOLEAN_NAME;
    case INDEXOF:
    case LENGTH:
    case ROUND:
    case FLOOR:
    case CEILING:
    case DAY:
    case HOUR:
    case MINUTE:
    case MONTH:
    case SECOND:
    case FRACTIONALSECONDS:
      return NUMBER_NAME;
    case CAST:
    case CONCAT:
    case DATE:
    case GEODISTANCE:
    case GEOINTERSECTS:
    case GEOLENGTH:
    case MAXDATETIME:
    case MINDATETIME:
    case NOW:
    case SUBSTRING:
    case TIME:
    case TOLOWER:
    case TOTALOFFSETMINUTES:
    case TOTALSECONDS:
    case TOUPPER:
    case TRIM:
    case YEAR:
      return STRING_NAME;
    default:
      return UNKNOWN_NAME;
    }
  }

  private void appendUriResourcePartObject(JsonStreamWriter jsonStreamWriter, UriResource segment) throws IOException,
      ExpressionVisitException, ODataApplicationException {
    if (segment instanceof UriResourceLambdaAll) {
      UriResourceLambdaAll all = (UriResourceLambdaAll) segment;
      String lambdaJsonObjectString = visitLambdaExpression(ALL_NAME, all.getLambdaVariable(), all.getExpression());
      jsonStreamWriter.unquotedValue(lambdaJsonObjectString);
      return;
    } else if (segment instanceof UriResourceLambdaAny) {
      UriResourceLambdaAny any = (UriResourceLambdaAny) segment;
      String lambdaJsonObjectString = visitLambdaExpression(ANY_NAME, any.getLambdaVariable(), any.getExpression());
      jsonStreamWriter.unquotedValue(lambdaJsonObjectString);
      return;
    } else if (segment instanceof UriResourcePartTyped) {
      String typeName =
          ((UriResourcePartTyped) segment).getType().getFullQualifiedName().getFullQualifiedNameAsString();
      jsonStreamWriter.beginObject().namedStringValue(NODE_TYPE_NAME, segment.getKind().toString()).separator()
          .namedStringValue(NAME_NAME, segment.toString()).separator().namedStringValueRaw(TYPE_NAME, typeName)
          .endObject();
    } else {
      jsonStreamWriter.beginObject().namedStringValue(NODE_TYPE_NAME, segment.getKind().toString()).separator()
          .namedStringValue(NAME_NAME, segment.toString()).separator().namedStringValueRaw(TYPE_NAME, null).endObject();
    }
  }

  private String getType(BinaryOperatorKind operator) {
    switch (operator) {
    case MUL:
    case DIV:
    case MOD:
    case ADD:
    case SUB:
      return NUMBER_NAME;

    case HAS:
    case GT:
    case GE:
    case LT:
    case LE:
    case EQ:
    case NE:
    case AND:
    case OR:
      return BOOLEAN_NAME;

    default:
      return UNKNOWN_NAME;
    }
  }

  private String getTypeString(EdmType type) {
    if (type == null) {
      return null;
    }
    return type.getFullQualifiedName().getFullQualifiedNameAsString();
  }

  private String getType(List<UriResource> uriResourceParts) {
    if (uriResourceParts == null || uriResourceParts.isEmpty()) {
      return null;
    }
    UriResource lastSegment = uriResourceParts.get(uriResourceParts.size() - 1);
    EdmType type = null;
    if (lastSegment instanceof UriResourcePartTyped) {
      type = ((UriResourcePartTyped) lastSegment).getType();
    }
    return type == null ? UNKNOWN_NAME : type.getFullQualifiedName().getFullQualifiedNameAsString();
  }

}
