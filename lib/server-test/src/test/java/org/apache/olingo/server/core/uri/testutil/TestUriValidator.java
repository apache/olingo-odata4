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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
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
    return run(path, null, null, null);
  }

  public TestUriValidator run(final String path, final String query)
      throws UriParserException, UriValidationException {
    return run(path, query, null, null);
  }
  public TestUriValidator run(final String path, final String query, final String fragment, final String baseUri)
      throws UriParserException, UriValidationException {
    try {
      uriInfo = new Parser(edm, odata).parseUri(path, query, fragment, baseUri);
      new UriValidator().validate(uriInfo, HttpMethod.GET);
      return this;
    } catch (UriParserException e) {
      exception = e;
      throw e;
    } 
  }
  
  public TestUriValidator run(final String path, final String query, final String fragment)
      throws UriParserException, UriValidationException {
    uriInfo = new Parser(edm, odata).parseUri(path, query, fragment, null);
    new UriValidator().validate(uriInfo, HttpMethod.GET);
    return this;
  }

  public TestUriValidator runEx(final String path) {
    return runEx(path, null);
  }

  public TestUriValidator runEx(final String path, final String query) {
    uriInfo = null;
    try {
      run(path, query, null, null);
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
    assertNotNull(uriInfo);
    assertNotNull(uriInfo.getKind());
    assertTrue("invalid resource kind: " + uriInfo.getKind().toString(),
        uriInfo.getKind() == UriInfoKind.resource
        || uriInfo.getKind() == UriInfoKind.all
        || uriInfo.getKind() == UriInfoKind.crossjoin);
    return new ResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoPath(uriInfo);
  }

  public FilterValidator goFilter() {
    final FilterOption filter = uriInfo.getFilterOption();
    assertNotNull("no filter found", filter);
    return new FilterValidator().setValidator(this).setFilter(filter);
  }

  public ExpandValidator goExpand() {
    final ExpandOption expand = uriInfo.getExpandOption();
    assertNotNull("no expand found", expand);
    return new ExpandValidator().setUpValidator(this).setExpand(expand);
  }

  public ResourceValidator goSelectItemPath(final int index) {
    final SelectOption select = uriInfo.getSelectOption();
    assertNotNull("no select found", select);
    final SelectItem item = select.getSelectItems().get(index);
    return new ResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoPath(item.getResourcePath());
  }

  public TestUriValidator isSelectStartType(final int index, final FullQualifiedName fullName) {
    final SelectOption select = uriInfo.getSelectOption();
    assertNotNull("no select found", select);
    final SelectItem item = select.getSelectItems().get(index);
    assertEquals(fullName, item.getStartTypeFilter().getFullQualifiedName());
    return this;
  }

  public TestUriValidator isSelectItemStar(final int index) {
    final SelectOption select = uriInfo.getSelectOption();
    assertNotNull("no select found", select);
    final SelectItem item = select.getSelectItems().get(index);
    assertTrue(item.isStar());
    return this;
  }

  public TestUriValidator isSelectItemAllOp(final int index, final FullQualifiedName fqn) {
    final SelectOption select = uriInfo.getSelectOption();
    assertNotNull("no select found", select);
    final SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn, item.getAllOperationsInSchemaNameSpace());
    return this;
  }

  // Validation
  public TestUriValidator isKind(final UriInfoKind kind) {
    assertNotNull(uriInfo);
    assertNotNull(uriInfo.getKind());
    assertEquals("invalid resource kind: " + uriInfo.getKind().toString(), kind, uriInfo.getKind());
    return this;
  }

  public TestUriValidator isFormatText(final String text) {
    assertEquals(text, uriInfo.getFormatOption().getFormat());
    return this;
  }

  public TestUriValidator isSkip(final int skip) {
    assertEquals(skip, uriInfo.getSkipOption().getValue());
    return this;
  }

  public TestUriValidator isTop(final int top) {
    assertEquals(top, uriInfo.getTopOption().getValue());
    return this;
  }

  public TestUriValidator isInlineCount(final boolean value) {
    assertEquals(value, uriInfo.getCountOption().getValue());
    return this;
  }

  public TestUriValidator isSkipTokenText(final String skipTokenText) {
    assertEquals(skipTokenText, uriInfo.getSkipTokenOption().getValue());
    return this;
  }

  public TestUriValidator isSearchSerialized(final String serialized) {
    assertNotNull("no search found", uriInfo.getSearchOption());
    assertEquals(serialized, uriInfo.getSearchOption().getSearchExpression().toString());
    return this;
  }

  public TestUriValidator isInAliasToValueMap(final String alias, final String value) {
    assertEquals(value, uriInfo.getValueForAlias(alias));
    return this;
  }

  public TestUriValidator isCustomParameter(final int index, final String name, final String value) {
    assertNotNull(uriInfo);

    final List<CustomQueryOption> list = uriInfo.getCustomQueryOptions();
    assertTrue("not enough queryParameters", list.size() > index);

    CustomQueryOption option = list.get(index);
    assertEquals(name, option.getName());
    assertEquals(value, option.getText());
    return this;
  }

  public TestUriValidator isCrossJoinEntityList(final List<String> entitySets) {
    isKind(UriInfoKind.crossjoin);
    assertEquals(entitySets, uriInfo.getEntitySetNames());
    return this;
  }

  public TestUriValidator isEntityType(final FullQualifiedName fullName) {
    isKind(UriInfoKind.entityId);
    assertEquals(fullName, uriInfo.getEntityTypeCast().getFullQualifiedName());
    return this;
  }

  public TestUriValidator isIdText(final String text) {
    assertEquals(text, uriInfo.getIdOption().getText());
    return this;
  }

  public TestUriValidator isFragmentText(final String text) {
    isKind(UriInfoKind.metadata);
    assertEquals(text, uriInfo.getFragment());
    return this;
  }

  public TestUriValidator isExceptionMessage(final ODataLibraryException.MessageKey messageKey) {
    assertEquals(messageKey, exception.getMessageKey());
    return this;
  }

  public TestUriValidator isExSyntax(final UriParserSyntaxException.MessageKeys messageKey) {
    assertEquals(UriParserSyntaxException.class, exception.getClass());
    return isExceptionMessage(messageKey);
  }

  public TestUriValidator isExSemantic(final UriParserSemanticException.MessageKeys messageKey) {
    assertEquals(UriParserSemanticException.class, exception.getClass());
    return isExceptionMessage(messageKey);
  }

  public TestUriValidator isExValidation(final UriValidationException.MessageKeys messageKey) {
    assertEquals(UriValidationException.class, exception.getClass());
    return isExceptionMessage(messageKey);
  }
}
