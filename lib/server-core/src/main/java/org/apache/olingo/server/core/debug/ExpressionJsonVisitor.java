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

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceIt;
import org.apache.olingo.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourceSingleton;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A custom expression visitor which converts the tree into a {@link JsonNode} tree.
 */
public class ExpressionJsonVisitor implements ExpressionVisitor<JsonNode> {

  private static final String ANY_NAME = "ANY";
  private static final String ALL_NAME = "ALL";
  private static final String UNKNOWN_NAME = "unknown";
  private static final String STRING_NAME = "String";
  private static final String BOOLEAN_NAME = "Boolean";
  private static final String NUMBER_NAME = "Number";
  private static final String DATE_NAME = "Date";
  private static final String TIME_NAME = "TimeOfDay";
  private static final String DATETIMEOFFSET_NAME = "DateTimeOffset";
  private static final String ENUM_NAME = "enum";
  private static final String VALUES_NAME = "values";
  private static final String NAME_NAME = "name";
  private static final String ALIAS_NAME = "alias";
  private static final String RESOURCE_SEGMENTS_NAME = "resourceSegments";
  private static final String MEMBER_NAME = "member";
  private static final String VALUE_NAME = "value";
  private static final String LITERAL_NAME = "literal";
  private static final String EXPRESSION_NAME = "expression";
  private static final String LAMBDA_VARIABLE_NAME = "lambdaVariable";
  private static final String LAMBDA_FUNCTION_NAME = "lambdaFunction";
  private static final String LAMBDA_REFERENCE_NAME = "lambdaReference";
  private static final String UNARY_NAME = "unary";
  private static final String BINARY_NAME = "binary";
  private static final String LEFT_NODE_NAME = "left";
  private static final String RIGHT_NODE_NAME = "right";
  private static final String PARAMETERS_NAME = "parameters";
  private static final String METHOD_NAME = "method";
  private static final String OPERAND_NAME = "operand";
  private static final String TYPE_NAME = "type";
  private static final String OPERATOR_NAME = "operator";
  private static final String NODE_TYPE_NAME = "nodeType";
  private static final String KEYS_NAME = "keys";
  private static final String TYPE_FILTER_NAME = "typeFilter";
  private static final String TYPE_FILTER_ON_COLLECTION_NAME = "typeFilterOnCollection";
  private static final String TYPE_FILTER_ON_ENTRY_NAME = "typeFilterOnEntry";

  private final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

  @Override
  public JsonNode visitBinaryOperator(final BinaryOperatorKind operator, final JsonNode left, final JsonNode right)
      throws ExpressionVisitException, ODataApplicationException {
    ObjectNode result = nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, BINARY_NAME)
        .put(OPERATOR_NAME, operator.toString())
        .put(TYPE_NAME, getType(operator));
    result.set(LEFT_NODE_NAME, left);
    result.set(RIGHT_NODE_NAME, right);
    return result;
  }

  @Override
  public JsonNode visitUnaryOperator(final UnaryOperatorKind operator, final JsonNode operand)
      throws ExpressionVisitException, ODataApplicationException {
    return nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, UNARY_NAME)
        .put(OPERATOR_NAME, operator.toString())
        .put(TYPE_NAME, getType(operator))
        .set(OPERAND_NAME, operand);
  }

  @Override
  public JsonNode visitMethodCall(final MethodKind methodCall, final List<JsonNode> parameters)
      throws ExpressionVisitException, ODataApplicationException {
    ObjectNode result = nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, METHOD_NAME)
        .put(OPERATOR_NAME, methodCall.toString())
        .put(TYPE_NAME, getType(methodCall));
    ArrayNode jsonParameters = result.putArray(PARAMETERS_NAME);
    for (final JsonNode parameter : parameters) {
      jsonParameters.add(parameter);
    }
    return result;
  }

  @Override
  public JsonNode visitLambdaExpression(final String lambdaFunction, final String lambdaVariable,
      final Expression expression) throws ExpressionVisitException, ODataApplicationException {
    return nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, LAMBDA_FUNCTION_NAME)
        .put(LAMBDA_VARIABLE_NAME, lambdaVariable)
        .set(EXPRESSION_NAME, expression.accept(this));
  }

  @Override
  public JsonNode visitLiteral(final Literal literal) throws ExpressionVisitException, ODataApplicationException {
    return nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, LITERAL_NAME)
        .put(TYPE_NAME, getTypeString(literal.getType()))
        .put(VALUE_NAME, literal.getText());
  }

  @Override
  public JsonNode visitMember(final Member member)
      throws ExpressionVisitException, ODataApplicationException {
    final List<UriResource> uriResourceParts = member.getResourcePath().getUriResourceParts();
    final UriResource lastSegment = uriResourceParts.get(uriResourceParts.size() - 1);
    ObjectNode result = nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, MEMBER_NAME)
        .put(TYPE_NAME, getType(lastSegment));
    putType(result, TYPE_FILTER_NAME, member.getStartTypeFilter());
    ArrayNode segments = result.putArray(RESOURCE_SEGMENTS_NAME);
    for (final UriResource segment : uriResourceParts) {
      if (segment instanceof UriResourceLambdaAll) {
        final UriResourceLambdaAll all = (UriResourceLambdaAll) segment;
        segments.add(visitLambdaExpression(ALL_NAME, all.getLambdaVariable(), all.getExpression()));
      } else if (segment instanceof UriResourceLambdaAny) {
        final UriResourceLambdaAny any = (UriResourceLambdaAny) segment;
        segments.add(visitLambdaExpression(ANY_NAME, any.getLambdaVariable(), any.getExpression()));
      } else if (segment instanceof UriResourcePartTyped) {
        ObjectNode node = nodeFactory.objectNode()
            .put(NODE_TYPE_NAME, segment.getKind().toString())
            .put(NAME_NAME, segment.toString())
            .put(TYPE_NAME, getType(segment));
        if (segment instanceof UriResourceEntitySet) {
          putParameters(node, KEYS_NAME, ((UriResourceEntitySet) segment).getKeyPredicates());
          putType(node, TYPE_FILTER_ON_COLLECTION_NAME, ((UriResourceEntitySet) segment).getTypeFilterOnCollection());
          putType(node, TYPE_FILTER_ON_ENTRY_NAME, ((UriResourceEntitySet) segment).getTypeFilterOnEntry());
        } else if (segment instanceof UriResourceNavigation) {
          putParameters(node, KEYS_NAME, ((UriResourceNavigation) segment).getKeyPredicates());
          putType(node, TYPE_FILTER_ON_COLLECTION_NAME, ((UriResourceNavigation) segment).getTypeFilterOnCollection());
          putType(node, TYPE_FILTER_ON_ENTRY_NAME, ((UriResourceNavigation) segment).getTypeFilterOnEntry());
        } else if (segment instanceof UriResourceFunction) {
          putParameters(node, PARAMETERS_NAME, ((UriResourceFunction) segment).getParameters());
          putParameters(node, KEYS_NAME, ((UriResourceFunction) segment).getKeyPredicates());
          putType(node, TYPE_FILTER_ON_COLLECTION_NAME, ((UriResourceFunction) segment).getTypeFilterOnCollection());
          putType(node, TYPE_FILTER_ON_ENTRY_NAME, ((UriResourceFunction) segment).getTypeFilterOnEntry());
        } else if (segment instanceof UriResourceIt) {
          putType(node, TYPE_FILTER_ON_COLLECTION_NAME, ((UriResourceIt) segment).getTypeFilterOnCollection());
          putType(node, TYPE_FILTER_ON_ENTRY_NAME, ((UriResourceIt) segment).getTypeFilterOnEntry());
        } else if (segment instanceof UriResourceSingleton) {
          putType(node, TYPE_FILTER_NAME, ((UriResourceSingleton) segment).getEntityTypeFilter());
        } else if (segment instanceof UriResourceComplexProperty) {
          putType(node, TYPE_FILTER_NAME, ((UriResourceComplexProperty) segment).getComplexTypeFilter());
        }
        segments.add(node);
      } else {
        segments.add(nodeFactory.objectNode()
            .put(NODE_TYPE_NAME, segment.getKind().toString())
            .put(NAME_NAME, segment.toString())
            .putNull(TYPE_NAME));
      }
    }
    return result;
  }

  private void putType(ObjectNode node, final String name, final EdmType type) {
    if (type != null) {
      node.put(name, type.getFullQualifiedName().getFullQualifiedNameAsString());
    }
  }

  private void putParameters(ObjectNode node, final String name, final List<UriParameter> parameters) {
    if (!parameters.isEmpty()) {
      ObjectNode parametersNode = node.putObject(name);
      for (final UriParameter parameter : parameters) {
        parametersNode.put(parameter.getName(),
            parameter.getText() == null ? parameter.getAlias() : parameter.getText());
      }
    }
  }

  @Override
  public JsonNode visitAlias(final String aliasName) throws ExpressionVisitException, ODataApplicationException {
    return nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, ALIAS_NAME)
        .put(ALIAS_NAME, aliasName);
  }

  @Override
  public JsonNode visitTypeLiteral(final EdmType type) throws ExpressionVisitException, ODataApplicationException {
    return nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, TYPE_NAME)
        .put(TYPE_NAME, getTypeString(type));
  }

  @Override
  public JsonNode visitLambdaReference(final String variableName)
      throws ExpressionVisitException, ODataApplicationException {
    return nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, LAMBDA_REFERENCE_NAME)
        .put(NAME_NAME, variableName);
  }

  @Override
  public JsonNode visitEnum(final EdmEnumType type, final List<String> enumValues)
      throws ExpressionVisitException, ODataApplicationException {
    ObjectNode result = nodeFactory.objectNode()
        .put(NODE_TYPE_NAME, ENUM_NAME)
        .put(TYPE_NAME, getTypeString(type));
    ArrayNode values = result.putArray(VALUES_NAME);
    if (enumValues != null) {
      for (final String enumValue : enumValues) {
        values.add(enumValue);
      }
    }
    return result;
  }

  private String getType(final UnaryOperatorKind operator) {
    switch (operator) {
    case MINUS:
      return NUMBER_NAME;
    case NOT:
      return BOOLEAN_NAME;
    }
    return UNKNOWN_NAME;
  }

  private String getType(final MethodKind methodCall) {
    switch (methodCall) {
    case STARTSWITH:
    case CONTAINS:
    case ENDSWITH:
    case ISOF:
    case GEOINTERSECTS:
      return BOOLEAN_NAME;

    case INDEXOF:
    case LENGTH:
    case ROUND:
    case FLOOR:
    case CEILING:
    case YEAR:
    case MONTH:
    case DAY:
    case HOUR:
    case MINUTE:
    case SECOND:
    case FRACTIONALSECONDS:
    case TOTALOFFSETMINUTES:
    case TOTALSECONDS:
    case GEODISTANCE:
    case GEOLENGTH:
      return NUMBER_NAME;

    case CONCAT:
    case SUBSTRING:
    case TOLOWER:
    case TOUPPER:
    case TRIM:
      return STRING_NAME;

    case DATE:
      return DATE_NAME;

    case TIME:
      return TIME_NAME;

    case MAXDATETIME:
    case MINDATETIME:
    case NOW:
      return DATETIMEOFFSET_NAME;

    case CAST:
      return UNKNOWN_NAME;
    }
    return UNKNOWN_NAME;
  }

  private String getType(final BinaryOperatorKind operator) {
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
    }
    return UNKNOWN_NAME;
  }

  private String getTypeString(final EdmType type) {
    return type == null ? null : type.getFullQualifiedName().getFullQualifiedNameAsString();
  }

  private String getType(final UriResource segment) {
    final EdmType type = segment instanceof UriResourcePartTyped ? ((UriResourcePartTyped) segment).getType() : null;
    return type == null ? UNKNOWN_NAME : type.getFullQualifiedName().getFullQualifiedNameAsString();
  }
}
