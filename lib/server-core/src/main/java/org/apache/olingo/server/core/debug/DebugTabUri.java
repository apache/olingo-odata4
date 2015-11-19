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

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
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

  public DebugTabUri(UriInfo uriInfo) {
    this.uriInfo = uriInfo;
  }

  @Override
  public String getName() {
    return "URI";
  }

  @Override
  public void appendJson(JsonGenerator gen) throws IOException {
    gen.writeStartObject();

    if (!uriInfo.getUriResourceParts().isEmpty()) {
      gen.writeFieldName("uriResourceParts");
      appendURIResourceParts(gen, uriInfo.getUriResourceParts());
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
        uriInfo.getSearchOption());

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

  private void appendCommonJsonObjects(JsonGenerator gen, CountOption countOption, SkipOption skipOption,
      TopOption topOption, FilterOption filterOption, OrderByOption orderByOption, SelectOption selectOption,
      ExpandOption expandOption, SearchOption searchOption)
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
  }

  private void appendURIResourceParts(JsonGenerator gen, List<UriResource> uriResourceParts) throws IOException {
    gen.writeStartArray();
    for (UriResource resource : uriResourceParts) {
      gen.writeStartObject();
      gen.writeStringField("uriResourceKind", resource.getKind().toString());
      gen.writeStringField("segment", resource.toString());
      if (resource instanceof UriResourceEntitySet) {
        appendParameters(gen, "keys", ((UriResourceEntitySet) resource).getKeyPredicates());
      } else if (resource instanceof UriResourceNavigation) {
        appendParameters(gen, "keys", ((UriResourceNavigation) resource).getKeyPredicates());
      } else if (resource instanceof UriResourceFunction) {
        appendParameters(gen, "parameters", ((UriResourceFunction) resource).getParameters());
        appendParameters(gen, "keys", ((UriResourceFunction) resource).getKeyPredicates());
      }
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

  private void appendParameters(JsonGenerator gen, final String name, final List<UriParameter> parameters)
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

  private void appendOrderByItemsJson(JsonGenerator gen, final List<OrderByItem> orders) throws IOException {
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

  private void appendExpandedPropertiesJson(JsonGenerator gen, List<ExpandItem> expandItems) throws IOException {
    gen.writeStartArray();
    for (ExpandItem item : expandItems) {
      appendExpandItemJson(gen, item);
    }
    gen.writeEndArray();
  }

  private void appendExpandItemJson(JsonGenerator gen, ExpandItem item) throws IOException {
    gen.writeStartObject();

    if (item.isStar()) {
      gen.writeBooleanField("star", item.isStar());
    } else if (item.getResourcePath() != null && !item.getResourcePath().getUriResourceParts().isEmpty()) {
      gen.writeFieldName("expandPath");
      gen.writeStartArray();
      for (UriResource resource : item.getResourcePath().getUriResourceParts()) {
        gen.writeStartObject();
        gen.writeStringField("propertyKind", resource.getKind().toString());
        gen.writeStringField("propertyName", resource.toString());
        gen.writeEndObject();
      }
      gen.writeEndArray();
    }

    if (item.isRef()) {
      gen.writeBooleanField("isRef", item.isRef());
    }

    if (item.getLevelsOption() != null) {
      gen.writeNumberField("levels", item.getLevelsOption().getValue());
    }

    appendCommonJsonObjects(gen, item.getCountOption(), item.getSkipOption(), item.getTopOption(),
        item.getFilterOption(), item.getOrderByOption(), item.getSelectOption(), item.getExpandOption(),
        item.getSearchOption());

    gen.writeEndObject();
  }

  private void appendExpressionJson(JsonGenerator gen, final Expression expression) throws IOException {
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

  private void appendSelectedPropertiesJson(JsonGenerator gen, List<SelectItem> selectItems) throws IOException {
    gen.writeStartArray();
    for (SelectItem selectItem : selectItems) {
      gen.writeString(getSelectString(selectItem));
    }
    gen.writeEndArray();
  }

  private String getSelectString(final SelectItem selectItem) {
    String selectedProperty = "";
    if (selectItem.isStar()) {
      if (selectItem.getAllOperationsInSchemaNameSpace() == null) {
        selectedProperty = "*";
      } else {
        selectedProperty = selectItem.getAllOperationsInSchemaNameSpace().getFullQualifiedNameAsString() + ".*";
      }
    } else {
      boolean first = true;
      for (UriResource resourcePart : selectItem.getResourcePath().getUriResourceParts()) {
        if (!first) {
          selectedProperty += "/";
        }
        selectedProperty = resourcePart.toString();
        first = false;
      }
    }
    return selectedProperty;
  }

  private void appendSearchJson(JsonGenerator json, final SearchExpression searchExpression) throws IOException {
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

  @Override
  public void appendHtml(final Writer writer) throws IOException {
    // factory for JSON generators (the object mapper is necessary to write expression trees)
    final JsonFactory jsonFactory = new ObjectMapper().getFactory();

    writer.append("<h2>Resource Path</h2>\n")
        .append("<ul>\n<li class=\"json\">");
    JsonGenerator json = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter();
    appendURIResourceParts(json, uriInfo.getUriResourceParts());
    json.close();
    writer.append("\n</li>\n</ul>\n");

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
