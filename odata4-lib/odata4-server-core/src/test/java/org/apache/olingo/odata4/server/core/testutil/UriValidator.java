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
package org.apache.olingo.odata4.server.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.server.api.uri.UriInfoKind;
import org.apache.olingo.odata4.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.odata4.server.core.uri.UriParserException;
import org.apache.olingo.odata4.server.core.uri.UriParserSemanticException;
import org.apache.olingo.odata4.server.core.uri.UriParserSyntaxException;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriInfoImpl;
import org.apache.olingo.odata4.server.core.uri.parser.Parser;
import org.apache.olingo.odata4.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SelectOptionImpl;

public class UriValidator implements Validator {
  private Edm edm;

  private UriInfoImpl uriInfo;
  private Exception exception;

  // Setup
  public UriValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // Execution
  public UriValidator run(final String uri) {
    Parser parser = new Parser();
    uriInfo = null;
    try {
      // uriInfoTmp = new UriParserImpl(edm).ParseUri(uri);
      uriInfo = (UriInfoImpl) parser.parseUri(uri, edm);
    } catch (UriParserException e) {
      fail("Exception occured while parsing the URI: " + uri + "\n"
          + " Exception: " + e.getMessage());
    }

    return this;
  }

  public UriValidator runEx(final String uri) {
    Parser parser = new Parser();
    uriInfo = null;
    try {
      // uriInfoTmp = new UriParserImpl(edm).ParseUri(uri);
      uriInfo = (UriInfoImpl) parser.parseUri(uri, edm);

    } catch (UriParserException e) {
      exception = e;
    }

    return this;
  }

  public UriValidator log(final String uri) {
    ParserTest parserTest = new ParserTest();
    parserTest.setLogLevel(1);
    uriInfo = null;
    try {
      // uriInfoTmp = new UriParserImpl(edm).ParseUri(uri);
      uriInfo = (UriInfoImpl) parserTest.parseUri(uri, edm);
    } catch (UriParserException e) {
      fail("Exception occured while parsing the URI: " + uri + "\n"
          + " Exception: " + e.getMessage());
    }

    return this;
  }

  // Navigation
  public UriResourceValidator goPath() {
    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    return new UriResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoImplPath(uriInfo);
  }

  public FilterValidator goFilter() {
    FilterOptionImpl filter = (FilterOptionImpl) uriInfo.getFilterOption();
    if (filter == null) {
      fail("no filter found");
    }
    return new FilterValidator().setUriValidator(this).setFilter(filter);

  }

  public ExpandValidator goExpand() {
    ExpandOptionImpl expand = (ExpandOptionImpl) uriInfo.getExpandOption();
    if (expand == null) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    return new ExpandValidator().setGoUpValidator(this).setExpand(expand);
  }

  public UriResourceValidator goSelectItemPath(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    UriInfoImpl uriInfo1 = (UriInfoImpl) item.getResourceInfo();

    return new UriResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoImplPath(uriInfo1);

  }

  // Validation
  public UriValidator isKind(final UriInfoKind kind) {
    assertEquals(kind, uriInfo.getKind());
    return this;
  }

  public UriValidator isCustomParameter(final int index, final String name, final String value) {
    if (uriInfo == null) {
      fail("hasQueryParameter: uriInfo == null");
    }

    List<CustomQueryOption> list = uriInfo.getCustomQueryOptions();
    if (list.size() <= index) {
      fail("not enought queryParameters");
    }

    CustomQueryOptionImpl option = (CustomQueryOptionImpl) list.get(index);
    assertEquals(name, option.getName());
    assertEquals(value, option.getText());
    return this;
  }

  public void isCrossJoinEntityList(final List<String> entitySets) {
    if (uriInfo.getKind() != UriInfoKind.crossjoin) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    int i = 0;
    for (String entitySet : entitySets) {
      assertEquals(entitySet, uriInfo.getEntitySetNames().get(i));
      i++;
    }

  }

  public UriValidator isExSyntax(final long errorID) {
    assertEquals(UriParserSyntaxException.class, exception.getClass());
    return this;
  }

  public UriValidator isExSemantic(final long errorID) {
    assertEquals(UriParserSemanticException.class, exception.getClass());
    return this;
  }

  public UriValidator isIdText(final String text) {
    assertEquals(text, uriInfo.getIdOption().getText());
    return this;
  }

  public UriValidator isExpandText(final String text) {
    assertEquals(text, uriInfo.getExpandOption().getText());
    return this;
  }

  public UriValidator isSelectText(final String text) {
    assertEquals(text, uriInfo.getSelectOption().getText());
    return this;
  }

  public UriValidator isFormatText(final String text) {
    assertEquals(text, uriInfo.getFormatOption().getText());
    return this;
  }

  public UriValidator isFragmentText(final String text) {
    if (uriInfo.getKind() != UriInfoKind.metadata) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    assertEquals(text, uriInfo.getFragment());

    return this;
  }

  public UriValidator isEntityType(final FullQualifiedName fullName) {
    if (uriInfo.getKind() != UriInfoKind.entityId) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    assertEquals(fullName.toString(), fullName(uriInfo.getEntityTypeCast()));
    return this;
  }

  private String fullName(final EdmEntityType type) {
    return type.getNamespace() + "." + type.getName();
  }

  public UriValidator isSelectItemStar(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(true, item.isStar());
    return this;
  }

  public UriValidator isSelectItemAllOp(final int index, final FullQualifiedName fqn) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn.toString(), item.getAllOperationsInSchemaNameSpace().toString());
    return this;
  }

}
