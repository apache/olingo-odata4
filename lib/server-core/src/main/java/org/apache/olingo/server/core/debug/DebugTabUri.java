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
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourceSingleton;
import org.apache.olingo.server.api.uri.queryoption.ApplyItem;
import org.apache.olingo.server.api.uri.queryoption.ApplyOption;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.apply.Aggregate;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression;
import org.apache.olingo.server.api.uri.queryoption.apply.BottomTop;
import org.apache.olingo.server.api.uri.queryoption.apply.Compute;
import org.apache.olingo.server.api.uri.queryoption.apply.ComputeExpression;
import org.apache.olingo.server.api.uri.queryoption.apply.Concat;
import org.apache.olingo.server.api.uri.queryoption.apply.CustomFunction;
import org.apache.olingo.server.api.uri.queryoption.apply.Expand;
import org.apache.olingo.server.api.uri.queryoption.apply.Filter;
import org.apache.olingo.server.api.uri.queryoption.apply.GroupBy;
import org.apache.olingo.server.api.uri.queryoption.apply.GroupByItem;
import org.apache.olingo.server.api.uri.queryoption.apply.Search;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * URI parser debug information.
 */
public class DebugTabUri implements DebugTab {

  private final UriInfo uriInfo;

  public DebugTabUri(final UriInfo uriInfo) {
    this.uriInfo = uriInfo;
  }

  @Override
  public String getName() {
    return "URI";
  }

  @Override
  public void appendJson(final JsonGenerator gen) throws IOException {
    gen.writeStartObject();

    gen.writeStringField("kind", uriInfo.getKind().name());
    if (uriInfo.getKind() == UriInfoKind.resource) {
      gen.writeFieldName("uriResourceParts");
      appendURIResourceParts(gen, uriInfo.getUriResourceParts());
    } else if (uriInfo.getKind() == UriInfoKind.crossjoin) {
      gen.writeFieldName("entitySetNames");
      gen.writeStartArray();
      for (final String name : uriInfo.asUriInfoCrossjoin().getEntitySetNames()) {
        gen.writeString(name);
      }
      gen.writeEndArray();
    } else if (uriInfo.getKind() == UriInfoKind.entityId) {
      appendType(gen, "typeCast", uriInfo.asUriInfoEntityId().getEntityTypeCast());
    }
    
    if (uriInfo.getDeltaTokenOption() != null) {
      gen.writeStringField("deltatoken", uriInfo.getDeltaTokenOption().getValue());
    }
    
    if (uriInfo.getFormatOption() != null) {
      gen.writeStringField("format", uriInfo.getFormatOption().getFormat());
    }

    if (uriInfo.getIdOption() != null) {
      gen.writeStringField("id", uriInfo.getIdOption().getValue());
    }

    if (uriInfo.getSkipTokenOption() != null) {
      gen.writeStringField("skiptoken", uriInfo.getSkipTokenOption().getValue());
    }

    appendCommonJsonObjects(gen, uriInfo.getCountOption(), uriInfo.getSkipOption(), uriInfo.getTopOption(),
        uriInfo.getFilterOption(), uriInfo.getOrderByOption(), uriInfo.getSelectOption(), uriInfo.getExpandOption(),
        uriInfo.getSearchOption(), uriInfo.getApplyOption());

    if (!uriInfo.getAliases().isEmpty()) {
      gen.writeFieldName("aliases");
      DebugResponseHelperImpl.appendJsonTable(gen, getQueryOptionsMap(uriInfo.getAliases()));
    }

    if (!uriInfo.getCustomQueryOptions().isEmpty()) {
      gen.writeFieldName("customQueryOptions");
      DebugResponseHelperImpl.appendJsonTable(gen, getQueryOptionsMap(uriInfo.getCustomQueryOptions()));
    }

    gen.writeEndObject();
  }

  private void appendCommonJsonObjects(JsonGenerator gen,
      final CountOption countOption, final SkipOption skipOption, final TopOption topOption,
      final FilterOption filterOption, final OrderByOption orderByOption,
      final SelectOption selectOption, final ExpandOption expandOption, final SearchOption searchOption,
      final ApplyOption applyOption)
      throws IOException {
    if (countOption != null) {
      gen.writeBooleanField("isCount", countOption.getValue());
    }

    if (skipOption != null) {
      gen.writeNumberField("skip", skipOption.getValue());
    }

    if (topOption != null) {
      gen.writeNumberField("top", topOption.getValue());
    }

    if (filterOption != null) {
      gen.writeFieldName("filter");
      appendExpressionJson(gen, filterOption.getExpression());
    }

    if (orderByOption != null && !orderByOption.getOrders().isEmpty()) {
      gen.writeFieldName("orderby");
      gen.writeStartObject();
      gen.writeStringField("nodeType", "orderCollection");
      gen.writeFieldName("orders");
      appendOrderByItemsJson(gen, orderByOption.getOrders());
      gen.writeEndObject();
    }

    if (selectOption != null && !selectOption.getSelectItems().isEmpty()) {
      gen.writeFieldName("select");
      appendSelectedPropertiesJson(gen, selectOption.getSelectItems());
    }

    if (expandOption != null && !expandOption.getExpandItems().isEmpty()) {
      gen.writeFieldName("expand");
      appendExpandedPropertiesJson(gen, expandOption.getExpandItems());
    }

    if (searchOption != null) {
      gen.writeFieldName("search");
      appendSearchJson(gen, searchOption.getSearchExpression());
    }

    if (applyOption != null) {
      gen.writeFieldName("apply");
      appendApplyItemsJson(gen, applyOption.getApplyItems());
    }
  }

  private void appendURIResourceParts(final JsonGenerator gen, final List<UriResource> uriResourceParts)
      throws IOException {
    gen.writeStartArray();
    for (UriResource resource : uriResourceParts) {
      gen.writeStartObject();
      gen.writeStringField("uriResourceKind", resource.getKind().toString());
      gen.writeStringField("segment", resource.toString());
      if (resource instanceof UriResourcePartTyped) {
        appendType(gen, "type", ((UriResourcePartTyped) resource).getType());
        gen.writeBooleanField("isCollection", ((UriResourcePartTyped) resource).isCollection());
      }
      if (resource instanceof UriResourceEntitySet) {
        appendParameters(gen, "keys", ((UriResourceEntitySet) resource).getKeyPredicates());
        appendType(gen, "typeFilterOnCollection", ((UriResourceEntitySet) resource).getTypeFilterOnCollection());
        appendType(gen, "typeFilterOnEntry", ((UriResourceEntitySet) resource).getTypeFilterOnEntry());
      } else if (resource instanceof UriResourceNavigation) {
        appendParameters(gen, "keys", ((UriResourceNavigation) resource).getKeyPredicates());
        appendType(gen, "typeFilterOnCollection", ((UriResourceNavigation) resource).getTypeFilterOnCollection());
        appendType(gen, "typeFilterOnEntry", ((UriResourceNavigation) resource).getTypeFilterOnEntry());
      } else if (resource instanceof UriResourceFunction) {
        appendParameters(gen, "parameters", ((UriResourceFunction) resource).getParameters());
        appendParameters(gen, "keys", ((UriResourceFunction) resource).getKeyPredicates());
        appendType(gen, "typeFilterOnCollection", ((UriResourceFunction) resource).getTypeFilterOnCollection());
        appendType(gen, "typeFilterOnEntry", ((UriResourceFunction) resource).getTypeFilterOnEntry());
      } else if (resource instanceof UriResourceSingleton) {
        appendType(gen, "typeFilter", ((UriResourceSingleton) resource).getEntityTypeFilter());
      } else if (resource instanceof UriResourceComplexProperty) {
        appendType(gen, "typeFilter", ((UriResourceComplexProperty) resource).getComplexTypeFilter());
      }
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

  private void appendType(JsonGenerator json, final String name, final EdmType type) throws IOException {
    if (type != null) {
      json.writeStringField(name, type.getFullQualifiedName().getFullQualifiedNameAsString());
    }
  }

  private void appendParameters(final JsonGenerator gen, final String name, final List<UriParameter> parameters)
      throws IOException {
    if (!parameters.isEmpty()) {
      Map<String, String> parameterMap = new LinkedHashMap<String, String>();
      for (final UriParameter parameter : parameters) {
        parameterMap.put(parameter.getName(),
            parameter.getText() == null ? parameter.getAlias() : parameter.getText());
      }
      gen.writeFieldName(name);
      DebugResponseHelperImpl.appendJsonTable(gen, parameterMap);
    }
  }

  private void appendOrderByItemsJson(final JsonGenerator gen, final List<OrderByItem> orders) throws IOException {
    gen.writeStartArray();
    for (final OrderByItem item : orders) {
      gen.writeStartObject();
      gen.writeStringField("nodeType", "order");
      gen.writeStringField("sortorder", item.isDescending() ? "desc" : "asc");
      gen.writeFieldName("expression");
      appendExpressionJson(gen, item.getExpression());
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

  private void appendExpandedPropertiesJson(final JsonGenerator gen, final List<ExpandItem> expandItems)
      throws IOException {
    gen.writeStartArray();
    for (ExpandItem item : expandItems) {
      appendExpandItemJson(gen, item);
    }
    gen.writeEndArray();
  }

  private void appendExpandItemJson(final JsonGenerator gen, final ExpandItem item) throws IOException {
    gen.writeStartObject();

    if (item.isStar()) {
      gen.writeBooleanField("star", item.isStar());
    } else if (item.getResourcePath() != null && !item.getResourcePath().getUriResourceParts().isEmpty()) {
      gen.writeFieldName("expandPath");
      appendURIResourceParts(gen, item.getResourcePath().getUriResourceParts());
    }

    if (item.isRef()) {
      gen.writeBooleanField("isRef", item.isRef());
    }

    if (item.getLevelsOption() != null) {
      gen.writeFieldName("levels");
      if (item.getLevelsOption().isMax()) {
        gen.writeString("max");
      } else {
        gen.writeNumber(item.getLevelsOption().getValue());
      }
    }

    appendCommonJsonObjects(gen, item.getCountOption(), item.getSkipOption(), item.getTopOption(),
        item.getFilterOption(), item.getOrderByOption(), item.getSelectOption(), item.getExpandOption(),
        item.getSearchOption(), item.getApplyOption());

    gen.writeEndObject();
  }

  private void appendExpressionJson(final JsonGenerator gen, final Expression expression) throws IOException {
    if (expression == null) {
      gen.writeNull();
    } else {
      try {
        final JsonNode tree = expression.accept(new ExpressionJsonVisitor());
        gen.writeTree(tree);
      } catch (final ODataException e) {
        gen.writeString("Exception in Debug Expression visitor occurred: " + e.getMessage());
      }
    }
  }

  private void appendSelectedPropertiesJson(final JsonGenerator gen, final List<SelectItem> selectItems)
      throws IOException {
    gen.writeStartArray();
    for (SelectItem selectItem : selectItems) {
      gen.writeString(getSelectString(selectItem));
    }
    gen.writeEndArray();
  }

  private String getSelectString(final SelectItem selectItem) {
    if (selectItem.isStar()) {
      if (selectItem.getAllOperationsInSchemaNameSpace() == null) {
        return "*";
      } else {
        return selectItem.getAllOperationsInSchemaNameSpace().getFullQualifiedNameAsString() + ".*";
      }
    } else {
      final StringBuilder tmp = new StringBuilder();
      for (UriResource resourcePart : selectItem.getResourcePath().getUriResourceParts()) {
        if (tmp.length() > 0) {
          tmp.append('/');
        }
        tmp.append(resourcePart.toString());
      }
      return tmp.toString();
    }
  }

  private void appendSearchJson(final JsonGenerator json, final SearchExpression searchExpression) throws IOException {
    json.writeStartObject();
    if (searchExpression.isSearchTerm()) {
      json.writeStringField("nodeType", "searchTerm");
      json.writeStringField("searchTerm", searchExpression.asSearchTerm().getSearchTerm());
    } else if (searchExpression.isSearchBinary()) {
      json.writeStringField("nodeType", "binary");
      json.writeStringField("operator", searchExpression.asSearchBinary().getOperator().toString());
      json.writeFieldName("left");
      appendSearchJson(json, searchExpression.asSearchBinary().getLeftOperand());
      json.writeFieldName("right");
      appendSearchJson(json, searchExpression.asSearchBinary().getRightOperand());
    } else if (searchExpression.isSearchUnary()) {
      json.writeStringField("nodeType", "unary");
      json.writeStringField("operator", searchExpression.asSearchUnary().getOperator().toString());
      json.writeFieldName("operand");
      appendSearchJson(json, searchExpression.asSearchUnary().getOperand());
    }
    json.writeEndObject();
  }

  private void appendApplyItemsJson(JsonGenerator json, final List<ApplyItem> applyItems) throws IOException {
    json.writeStartArray();
    for (final ApplyItem item : applyItems) {
      appendApplyItemJson(json, item);
    }
    json.writeEndArray();
  }

  private void appendApplyItemJson(JsonGenerator json, final ApplyItem item) throws IOException {
    json.writeStartObject();

    json.writeStringField("kind", item.getKind().name());
    switch (item.getKind()) {
    case AGGREGATE:
      appendAggregateJson(json, (Aggregate) item);
      break;
    case BOTTOM_TOP:
      json.writeStringField("method", ((BottomTop) item).getMethod().name());
      json.writeFieldName("number");
      appendExpressionJson(json, ((BottomTop) item).getNumber());
      json.writeFieldName("value");
      appendExpressionJson(json, ((BottomTop) item).getValue());
      break;
    case COMPUTE:
      json.writeFieldName("compute");
      json.writeStartArray();
      for (final ComputeExpression computeExpression : ((Compute) item).getExpressions()) {
        json.writeStartObject();
        json.writeFieldName("expression");
        appendExpressionJson(json, computeExpression.getExpression());
        json.writeStringField("as", computeExpression.getAlias());
        json.writeEndObject();
      }
      json.writeEndArray();
      break;
    case CONCAT:
      json.writeFieldName("concat");
      json.writeStartArray();
      for (final ApplyOption option : ((Concat) item).getApplyOptions()) {
        appendApplyItemsJson(json, option.getApplyItems());
      }
      json.writeEndArray();
      break;
    case CUSTOM_FUNCTION:
      json.writeStringField("name",
          ((CustomFunction) item).getFunction().getFullQualifiedName().getFullQualifiedNameAsString());
      appendParameters(json, "parameters", ((CustomFunction) item).getParameters());
      break;
    case EXPAND:
      appendCommonJsonObjects(json, null, null, null, null, null, null, ((Expand) item).getExpandOption(), null, null);
      break;
    case FILTER:
      appendCommonJsonObjects(json, null, null, null, ((Filter) item).getFilterOption(), null, null, null, null, null);
      break;
    case GROUP_BY:
      json.writeFieldName("groupBy");
      appendGroupByItemsJson(json, ((GroupBy) item).getGroupByItems());
      appendCommonJsonObjects(json, null, null, null, null, null, null, null, null, ((GroupBy) item).getApplyOption());
      break;
    case IDENTITY:
      break;
    case SEARCH:
      appendCommonJsonObjects(json, null, null, null, null, null, null, null, ((Search) item).getSearchOption(), null);
      break;
    }

    json.writeEndObject();
  }

  private void appendGroupByItemsJson(JsonGenerator json, final List<GroupByItem> groupByItems) throws IOException {
    json.writeStartArray();
    for (final GroupByItem groupByItem : groupByItems) {
      json.writeStartObject();
      if (!groupByItem.getPath().isEmpty()) {
        json.writeFieldName("path");
        appendURIResourceParts(json, groupByItem.getPath());
      }
      json.writeBooleanField("isRollupAll", groupByItem.isRollupAll());
      if (!groupByItem.getRollup().isEmpty()) {
        json.writeFieldName("rollup");
        appendGroupByItemsJson(json, groupByItem.getRollup());
      }
      json.writeEndObject();
    }
    json.writeEndArray();
  }

  private void appendAggregateJson(JsonGenerator json, final Aggregate aggregate) throws IOException {
    json.writeFieldName("aggregate");
    appendAggregateExpressionsJson(json, aggregate.getExpressions());
  }

  private void appendAggregateExpressionsJson(JsonGenerator json, final List<AggregateExpression> aggregateExpressions)
      throws IOException {
    json.writeStartArray();
    for (final AggregateExpression aggregateExpression : aggregateExpressions) {
      appendAggregateExpressionJson(json, aggregateExpression);
    }
    json.writeEndArray();
  }

  private void appendAggregateExpressionJson(JsonGenerator json, final AggregateExpression aggregateExpression)
      throws IOException {
    if (aggregateExpression == null) {
      json.writeNull();
    } else {
      json.writeStartObject();
      if (!aggregateExpression.getPath().isEmpty()) {
        json.writeFieldName("path");
        appendURIResourceParts(json, aggregateExpression.getPath());
      }
      if (aggregateExpression.getExpression() != null) {
        json.writeFieldName("expression");
        appendExpressionJson(json, aggregateExpression.getExpression());
      }
      if (aggregateExpression.getStandardMethod() != null) {
        json.writeStringField("standardMethod", aggregateExpression.getStandardMethod().name());
      }
      if (aggregateExpression.getCustomMethod() != null) {
        json.writeStringField("customMethod", aggregateExpression.getCustomMethod().getFullQualifiedNameAsString());
      }
      if (aggregateExpression.getAlias() != null) {
        json.writeStringField("as", aggregateExpression.getAlias());
      }
      if (aggregateExpression.getInlineAggregateExpression() != null) {
        json.writeFieldName("inlineAggregateExpression");
        appendAggregateExpressionJson(json, aggregateExpression.getInlineAggregateExpression());
      }
      if (!aggregateExpression.getFrom().isEmpty()) {
        json.writeFieldName("from");
        appendAggregateExpressionsJson(json, aggregateExpression.getFrom());
      }
      json.writeEndObject();
    }
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {
    // factory for JSON generators (the object mapper is necessary to write expression trees)
    final JsonFactory jsonFactory = new ObjectMapper().getFactory();
    JsonGenerator json;

    if (uriInfo.getKind() == UriInfoKind.resource) {
      writer.append("<h2>Resource Path</h2>\n")
          .append("<ul>\n<li class=\"json\">");
      json = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter();
      appendURIResourceParts(json, uriInfo.getUriResourceParts());
      json.close();
      writer.append("\n</li>\n</ul>\n");
    } else if (uriInfo.getKind() == UriInfoKind.crossjoin) {
      writer.append("<h2>Crossjoin EntitySet Names</h2>\n")
          .append("<ul>\n");
      for (final String name : uriInfo.asUriInfoCrossjoin().getEntitySetNames()) {
        writer.append("<li>").append(name).append("</li>\n");
      }
      writer.append("</ul>\n");
    } else {
      writer.append("<h2>Kind</h2>\n<p>").append(uriInfo.getKind().name()).append("</p>\n");
      if (uriInfo.getKind() == UriInfoKind.entityId && uriInfo.asUriInfoEntityId().getEntityTypeCast() != null) {
        writer.append("<h2>Type Cast</h2>\n<p>")
            .append(uriInfo.asUriInfoEntityId().getEntityTypeCast().getFullQualifiedName()
                .getFullQualifiedNameAsString())
            .append("</p>\n");
      }
    }

    if (uriInfo.getSearchOption() != null) {
      writer.append("<h2>Search Option</h2>\n")
          .append("<ul>\n<li class=\"json\">");
      json = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter();
      appendSearchJson(json, uriInfo.getSearchOption().getSearchExpression());
      json.close();
      writer.append("\n</li>\n</ul>\n");
    }

    if (uriInfo.getFilterOption() != null) {
      writer.append("<h2>Filter Option</h2>\n")
          .append("<ul>\n<li class=\"json\">");
      json = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter();
      appendExpressionJson(json, uriInfo.getFilterOption().getExpression());
      json.close();
      writer.append("\n</li>\n</ul>\n");
    }

    if (uriInfo.getOrderByOption() != null) {
      writer.append("<h2>OrderBy Option</h2>\n")
          .append("<ul>\n<li class=\"json\">");
      json = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter();
      appendOrderByItemsJson(json, uriInfo.getOrderByOption().getOrders());
      json.close();
      writer.append("\n</li>\n</ul>\n");
    }

    if (uriInfo.getExpandOption() != null) {
      writer.append("<h2>Expand Option</h2>\n")
          .append("<ul>\n<li class=\"json\">");
      json = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter();
      appendExpandedPropertiesJson(json, uriInfo.getExpandOption().getExpandItems());
      json.close();
      writer.append("\n</li>\n</ul>\n");
    }

    if (uriInfo.getSelectOption() != null) {
      writer.append("<h2>Selected Properties</h2>\n")
          .append("<ul>\n");
      for (final SelectItem selectItem : uriInfo.getSelectOption().getSelectItems()) {
        writer.append("<li>").append(getSelectString(selectItem)).append("</li>\n");
      }
      writer.append("</ul>\n");
    }

    if (uriInfo.getApplyOption() != null) {
      writer.append("<h2>Apply Option</h2>\n")
          .append("<ul>\n<li class=\"json\">");
      json = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter();
      appendApplyItemsJson(json, uriInfo.getApplyOption().getApplyItems());
      json.close();
      writer.append("\n</li>\n</ul>\n");
    }

    if (uriInfo.getCountOption() != null
        || uriInfo.getSkipOption() != null
        || uriInfo.getSkipTokenOption() != null
        || uriInfo.getTopOption() != null
        || uriInfo.getFormatOption() != null
        || uriInfo.getIdOption() != null) {
      writer.append("<h2>Unstructured System Query Options</h2>\n");
      DebugResponseHelperImpl.appendHtmlTable(writer, getQueryOptionsMap(Arrays.asList(
          uriInfo.getCountOption(),
          uriInfo.getSkipOption(),
          uriInfo.getSkipTokenOption(),
          uriInfo.getTopOption(),
          uriInfo.getFormatOption(),
          uriInfo.getIdOption())));
    }

    if (!uriInfo.getAliases().isEmpty()) {
      writer.append("<h2>Aliases</h2>\n");
      DebugResponseHelperImpl.appendHtmlTable(writer, getQueryOptionsMap(uriInfo.getAliases()));
    }

    if (!uriInfo.getCustomQueryOptions().isEmpty()) {
      writer.append("<h2>Custom Query Options</h2>\n");
      DebugResponseHelperImpl.appendHtmlTable(writer, getQueryOptionsMap(uriInfo.getCustomQueryOptions()));
    }
  }

  private Map<String, String> getQueryOptionsMap(final List<? extends QueryOption> queryOptions) {
    Map<String, String> options = new LinkedHashMap<String, String>();
    for (final QueryOption option : queryOptions) {
      if (option != null) {
        options.put(option.getName(), option.getText());
      }
    }
    return options;
  }
}
