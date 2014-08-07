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
package org.apache.olingo.server.core.uri.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class TestUriValidator implements TestValidator {
  private Edm edm;

  private UriInfo uriInfo;
  private ODataTranslatedException exception;

  // Setup
  public TestUriValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // Execution
  public TestUriValidator run(final String uri) throws UriParserException, UriValidationException {
    Parser parser = new Parser();
    UriValidator validator = new UriValidator();

    uriInfo = parser.parseUri(uri, edm);
    validator.validate(uriInfo, HttpMethod.GET);
    return this;
  }

  public TestUriValidator runEx(final String uri) {
    Parser parser = new Parser();
    uriInfo = null;
    try {
      uriInfo = parser.parseUri(uri, edm);
      new UriValidator().validate(uriInfo, HttpMethod.GET);
      fail("Exception expected");
    } catch (UriParserException e) {
      exception = e;
    } catch (UriValidationException e) {
      exception = e;
    }

    return this;
  }

  public TestUriValidator log(final String uri) {
    ParserWithLogging parserTest = new ParserWithLogging();
    parserTest.setLogLevel(1);
    uriInfo = null;
    try {
      uriInfo = parserTest.parseUri(uri, edm);
    } catch (UriParserException e) {
      fail("Exception occured while parsing the URI: " + uri + "\n"
          + " Exception: " + e.getMessage());
    }

    return this;
  }

  // Navigation
  public ResourceValidator goPath() {
    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    return new ResourceValidator()
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

    return new ExpandValidator().setUpValidator(this).setExpand(expand);
  }

  public ResourceValidator goSelectItemPath(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    UriInfoImpl uriInfo1 = (UriInfoImpl) item.getResourcePath();

    return new ResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoImplPath(uriInfo1);

  }

  public TestUriValidator isSelectStartType(final int index, final FullQualifiedName fullName) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    EdmType actualType = item.getStartTypeFilter();

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());
    assertEquals(fullName, actualName);
    return this;

  }

  // Validation
  public TestUriValidator isKind(final UriInfoKind kind) {
    assertEquals(kind, uriInfo.getKind());
    return this;
  }

  public TestUriValidator isCustomParameter(final int index, final String name, final String value) {
    if (uriInfo == null) {
      fail("hasQueryParameter: uriInfo == null");
    }

    List<CustomQueryOption> list = uriInfo.getCustomQueryOptions();
    if (list.size() <= index) {
      fail("not enough queryParameters");
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

  public TestUriValidator isExSyntax(final UriParserSyntaxException.MessageKeys messageKey) {
    assertEquals(UriParserSyntaxException.class, exception.getClass());
    assertEquals(messageKey, exception.getMessageKey());
    return this;
  }

  public TestUriValidator isExSemantic(final UriParserSemanticException.MessageKeys messageKey) {
    assertEquals(UriParserSemanticException.class, exception.getClass());
    assertEquals(messageKey, exception.getMessageKey());
    return this;
  }

  public TestUriValidator isExValidation(final UriValidationException.MessageKeys messageKey) {
    assertEquals(UriValidationException.class, exception.getClass());
    assertEquals(messageKey, exception.getMessageKey());
    return this;
  }

  public TestUriValidator isIdText(final String text) {
    assertEquals(text, uriInfo.getIdOption().getText());
    return this;
  }

  public TestUriValidator isExpandText(final String text) {
    assertEquals(text, uriInfo.getExpandOption().getText());
    return this;
  }

  public TestUriValidator isSelectText(final String text) {
    assertEquals(text, uriInfo.getSelectOption().getText());
    return this;
  }

  public TestUriValidator isFormatText(final String text) {
    assertEquals(text, uriInfo.getFormatOption().getText());
    return this;
  }

  public TestUriValidator isFragmentText(final String text) {
    if (uriInfo.getKind() != UriInfoKind.metadata) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    assertEquals(text, uriInfo.getFragment());

    return this;
  }

  public TestUriValidator isEntityType(final FullQualifiedName fullName) {
    if (uriInfo.getKind() != UriInfoKind.entityId) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    assertEquals(fullName.toString(), fullName(uriInfo.getEntityTypeCast()));
    return this;
  }

  private String fullName(final EdmEntityType type) {
    return type.getNamespace() + "." + type.getName();
  }

  public TestUriValidator isSelectItemStar(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(true, item.isStar());
    return this;
  }

  public TestUriValidator isSelectItemAllOp(final int index, final FullQualifiedName fqn) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn.toString(), item.getAllOperationsInSchemaNameSpace().toString());
    return this;
  }
}
