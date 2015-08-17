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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

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
    if (uriInfo == null) {
      gen.writeNull();
      return;
    }

    gen.writeStartObject();

    if (uriInfo.getFormatOption() != null) {
      gen.writeStringField("format", uriInfo.getFormatOption().getFormat());
    }

    if (uriInfo.getIdOption() != null) {
      gen.writeStringField("id", uriInfo.getIdOption().getValue());
    }

    if (uriInfo.getSkipTokenOption() != null) {
      gen.writeStringField("id", uriInfo.getSkipTokenOption().getValue());
    }

    appendCommonJsonObjects(gen, uriInfo.getCountOption(), uriInfo.getSkipOption(), uriInfo.getTopOption(), uriInfo
        .getFilterOption(), uriInfo.getOrderByOption(), uriInfo.getSelectOption(), uriInfo.getExpandOption());

    if (uriInfo.getUriResourceParts() != null) {
      appendURIResourceParts(gen, uriInfo.getUriResourceParts());
    }

    gen.writeEndObject();
  }

  private void appendCommonJsonObjects(JsonGenerator gen, CountOption countOption, SkipOption skipOption,
      TopOption topOption, FilterOption filterOption, OrderByOption orderByOption, SelectOption selectOption,
      ExpandOption expandOption)
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
      appendJsonExpressionString(gen, filterOption.getExpression());
    }

    if (orderByOption != null && orderByOption.getOrders() != null) {
      gen.writeFieldName("orderby");
      gen.writeStartObject();
      gen.writeStringField("nodeType", "orderCollection");
      gen.writeFieldName("orders");
      gen.writeStartArray();
      for (OrderByItem item : orderByOption.getOrders()) {
        gen.writeStartObject();
        gen.writeStringField("nodeType", "order");
        gen.writeStringField("sortorder", item.isDescending() ? "desc" : "asc");
        gen.writeFieldName("expression");
        appendJsonExpressionString(gen, item.getExpression());
        gen.writeEndObject();
      }
      gen.writeEndArray();
      gen.writeEndObject();
    }

    if (selectOption != null && !selectOption.getSelectItems().isEmpty()) {
      appendSelectedPropertiesJson(gen, selectOption.getSelectItems());
    }

    if (expandOption != null && !expandOption.getExpandItems().isEmpty()) {
      appendExpandedPropertiesJson(gen, expandOption.getExpandItems());
    }
  }

  private void appendURIResourceParts(JsonGenerator gen, List<UriResource> uriResourceParts) throws IOException {
    gen.writeFieldName("uriResourceParts");

    gen.writeStartArray();
    for (UriResource resource : uriResourceParts) {
      gen.writeStartObject();
      gen.writeStringField("uriResourceKind", resource.getKind().toString());
      gen.writeStringField("segment", resource.toString());
      gen.writeEndObject();
    }
    gen.writeEndArray();
  }

  private void appendExpandedPropertiesJson(JsonGenerator gen, List<ExpandItem> expandItems) throws IOException {
    gen.writeFieldName("expand");

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
    } else if (item.getResourcePath() != null && item.getResourcePath().getUriResourceParts() != null) {
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

    appendCommonJsonObjects(gen, item.getCountOption(), item.getSkipOption(), item.getTopOption(), item
        .getFilterOption(), item.getOrderByOption(), item.getSelectOption(), item.getExpandOption());

    gen.writeEndObject();
  }

  private void appendJsonExpressionString(JsonGenerator gen, Expression expression) throws IOException {
    if (expression == null) {
      gen.writeNull();
      return;
    }
    String expressionJsonString;
    try {
      expressionJsonString = expression.accept(new ExpressionJsonVisitor());
    } catch (Exception e) {
      expressionJsonString = "Exception in Debug Filter visitor occoured: " + e.getMessage();
    }

    gen.writeRawValue(expressionJsonString);
  }

  private void appendSelectedPropertiesJson(JsonGenerator gen, List<SelectItem> selectItems) throws IOException {
    gen.writeFieldName("select");

    gen.writeStartArray();
    for (SelectItem selectItem : selectItems) {
      appendSelectItemJson(gen, selectItem);
    }
    gen.writeEndArray();
  }

  private void appendSelectItemJson(JsonGenerator gen, SelectItem selectItem) throws IOException {
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
          selectedProperty = selectedProperty + "/";
        }
        selectedProperty = resourcePart.toString();
        first = false;
      }
    }

    gen.writeString(selectedProperty);
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {
    if (uriInfo == null) {
      return;
    }

    writer.append("<h2>Uri Information</h2>\n")
        .append("<ul class=\"jsonCode\"><li>");
    writer.append(getJsonString());
    writer.append("</li></ul>\n");
  }

  private String getJsonString() throws IOException {
    CircleStreamBuffer csb = new CircleStreamBuffer();
    IOException cachedException = null;
    OutputStream outputStream = csb.getOutputStream();
    try {
      JsonGenerator json =
          new JsonFactory().createGenerator(outputStream, JsonEncoding.UTF8)
              .setPrettyPrinter(new DefaultPrettyPrinter());
      appendJson(json);
      json.close();
      outputStream.close();
    } catch (IOException e) {
      throw e;
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          if (cachedException != null) {
            throw cachedException;
          } else {
            throw e;
          }
        }
      }
    }

    InputStream inputStream = csb.getInputStream();
    try {
      String jsonString = IOUtils.toString(inputStream);
      inputStream.close();
      return jsonString;
    } catch (IOException e) {
      throw e;
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          if (cachedException != null) {
            throw cachedException;
          } else {
            throw e;
          }
        }
      }
    }
  }
}
