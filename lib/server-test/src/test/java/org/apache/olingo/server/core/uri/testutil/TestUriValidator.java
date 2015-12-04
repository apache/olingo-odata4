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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class TestUriValidator implements TestValidator {
  private final OData odata = OData.newInstance();
  private Edm edm;

  private UriInfo uriInfo;
  private ODataLibraryException exception;

  // Setup
  public TestUriValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // Execution
  public TestUriValidator run(final String path) throws UriParserException, UriValidationException {
    return run(path, null);
  }

  public TestUriValidator run(final String path, final String query)
      throws UriParserException, UriValidationException {
    uriInfo = new Parser(edm, odata).parseUri(path, query, null);
    new UriValidator().validate(uriInfo, HttpMethod.GET);
    return this;
  }

  public TestUriValidator run(final String path, final String query, final String fragment)
      throws UriParserException, UriValidationException {
    uriInfo = new Parser(edm, odata).parseUri(path, query, fragment);
    new UriValidator().validate(uriInfo, HttpMethod.GET);
    return this;
  }

  public TestUriValidator runEx(final String path) {
    return runEx(path, null);
  }

  public TestUriValidator runEx(final String path, final String query) {
    uriInfo = null;
    try {
      uriInfo = new Parser(edm, odata).parseUri(path, query, null);
      new UriValidator().validate(uriInfo, HttpMethod.GET);
      fail("Exception expected");
    } catch (UriParserException e) {
      exception = e;
    } catch (UriValidationException e) {
      exception = e;
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
        .setUriInfoPath(uriInfo);
  }

  public FilterValidator goFilter() {
    final FilterOption filter = uriInfo.getFilterOption();
    if (filter == null) {
      fail("no filter found");
    }
    return new FilterValidator().setUriValidator(this).setFilter(filter);
  }

  public ExpandValidator goExpand() {
    final ExpandOption expand = uriInfo.getExpandOption();
    if (expand == null) {
      fail("invalid resource kind: " + uriInfo.getKind().toString());
    }

    return new ExpandValidator().setUpValidator(this).setExpand(expand);
  }

  public ResourceValidator goSelectItemPath(final int index) {
    final SelectOption select = uriInfo.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    return new ResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoPath(item.getResourcePath());
  }

  public TestUriValidator isSelectStartType(final int index, final FullQualifiedName fullName) {
    final SelectOption select = uriInfo.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    EdmType actualType = item.getStartTypeFilter();
    assertEquals(fullName, actualType.getFullQualifiedName());
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

    CustomQueryOption option = list.get(index);
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

  public TestUriValidator isExceptionMessage(final ODataLibraryException.MessageKey messageKey) {
    assertEquals(messageKey, exception.getMessageKey());
    return this;
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

    assertEquals(fullName, uriInfo.getEntityTypeCast().getFullQualifiedName());
    return this;
  }

  public TestUriValidator isSelectItemStar(final int index) {
    final SelectOption select = uriInfo.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    assertTrue(item.isStar());
    return this;
  }

  public TestUriValidator isSelectItemAllOp(final int index, final FullQualifiedName fqn) {
    final SelectOption select = uriInfo.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn, item.getAllOperationsInSchemaNameSpace());
    return this;
  }
}
