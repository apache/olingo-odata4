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

  @Override
  public String visitBinaryOperator(BinaryOperatorKind operator, String left, String right)
      throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValue("nodeType", "binary").separator().namedStringValue("operator",
          operator.toString()).separator().namedStringValueRaw("type", getType(operator)).separator().name("left")
          .unquotedValue(left).separator().name("right").unquotedValue(right).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitUnaryOperator(UnaryOperatorKind operator, String operand) throws ExpressionVisitException,
      ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValue("nodeType", "unary").separator()
          .namedStringValueRaw("operator", operator.toString()).separator().namedStringValueRaw("type",
              getType(operator)).separator().name("operand").unquotedValue(operand).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitMethodCall(MethodKind methodCall, List<String> parameters) throws ExpressionVisitException,
      ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw("nodeType", "method").separator()
          .namedStringValueRaw("operator", methodCall.toString()).separator().namedStringValueRaw("type",
              getType(methodCall)).separator().name("parameters").beginArray();
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
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression)
      throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValue("nodeType", "lambdaFunction").separator()
          .namedStringValue("lambdaVariable", lambdaVariable).separator().name("expression");

      // Write expression string object
      String expressionJsonTree = expression.accept(this);
      jsonStreamWriter.unquotedValue(expressionJsonTree).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw("nodeType", "literal").separator().namedStringValueRaw("type",
          getTypeString(literal.getType())).separator().namedStringValue("value", literal.getText()).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException("IOException occoured");
    }
  }

  @Override
  public String visitMember(UriInfoResource member) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      List<UriResource> uriResourceParts = member.getUriResourceParts();
      jsonStreamWriter.beginObject().namedStringValue("nodeType", "member").separator()
          .namedStringValueRaw("type", getType(uriResourceParts)).separator();

      // write all member properties in an array
      jsonStreamWriter.name("resourceSegments").beginArray();
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
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw("nodeType", "alias").separator()
          .namedStringValue("alias", aliasName).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw("nodeType", "type").separator()
          .namedStringValueRaw("type", getTypeString(type)).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitLambdaReference(String variableName) throws ExpressionVisitException, ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw("nodeType", "lambdaReference").separator()
          .namedStringValueRaw("name", variableName).endObject();
      writer.flush();
      return writer.toString();
    } catch (final IOException e) {
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  @Override
  public String visitEnum(EdmEnumType type, List<String> enumValues) throws ExpressionVisitException,
      ODataApplicationException {
    try {
      StringWriter writer = new StringWriter();
      JsonStreamWriter jsonStreamWriter = new JsonStreamWriter(writer);
      jsonStreamWriter.beginObject().namedStringValueRaw("nodeType", "enum").separator()
          .namedStringValueRaw("type", getTypeString(type)).separator();
      jsonStreamWriter.name("values").beginArray();
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
      throw new ExpressionVisitException("IOException occoured", e);
    }
  }

  private String getType(UnaryOperatorKind operator) {
    switch (operator) {
    case MINUS:
      return "Number";
    case NOT:
      return "Boolean";
    default:
      return "unknown";
    }
  }

  private String getType(MethodKind methodCall) {
    switch (methodCall) {
    case STARTSWITH:
    case CONTAINS:
    case ENDSWITH:
    case ISOF:
      return "Boolean";
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
      return "Number";
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
      return "String";
    default:
      return "unkown";
    }
  }

  private void appendUriResourcePartObject(JsonStreamWriter jsonStreamWriter, UriResource segment) throws IOException,
      ExpressionVisitException, ODataApplicationException {
    if (segment instanceof UriResourceLambdaAll) {
      UriResourceLambdaAll all = (UriResourceLambdaAll) segment;
      String lambdaJsonObjectString = visitLambdaExpression("ALL", all.getLambdaVariable(), all.getExpression());
      jsonStreamWriter.unquotedValue(lambdaJsonObjectString);
      return;
    } else if (segment instanceof UriResourceLambdaAny) {
      UriResourceLambdaAny any = (UriResourceLambdaAny) segment;
      String lambdaJsonObjectString = visitLambdaExpression("ANY", any.getLambdaVariable(), any.getExpression());
      jsonStreamWriter.unquotedValue(lambdaJsonObjectString);
      return;
    } else if (segment instanceof UriResourcePartTyped) {
      String typeName =
          ((UriResourcePartTyped) segment).getType().getFullQualifiedName().getFullQualifiedNameAsString();
      jsonStreamWriter.beginObject().namedStringValue("nodeType", segment.getKind().toString()).separator()
          .namedStringValue("name", segment.toString()).separator().namedStringValueRaw("type", typeName).endObject();
    } else {
      jsonStreamWriter.beginObject().namedStringValue("nodeType", segment.getKind().toString()).separator()
          .namedStringValue("name", segment.toString()).separator().namedStringValueRaw("type", null).endObject();
    }
  }

  private String getType(BinaryOperatorKind operator) {
    switch (operator) {
    case MUL:
    case DIV:
    case MOD:
    case ADD:
    case SUB:
      return "Number";

    case HAS:
    case GT:
    case GE:
    case LT:
    case LE:
    case EQ:
    case NE:
    case AND:
    case OR:
      return "Boolean";

    default:
      return "unkown";
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
    return type == null ? "unknown" : type.getFullQualifiedName().getFullQualifiedNameAsString();
  }

}
