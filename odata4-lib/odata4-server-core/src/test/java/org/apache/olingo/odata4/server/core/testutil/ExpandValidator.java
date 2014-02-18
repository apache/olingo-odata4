/*******************************************************************************
 * 
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
 ******************************************************************************/
package org.apache.olingo.odata4.server.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.olingo.odata4.commons.api.ODataApplicationException;
import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.server.api.uri.UriInfoKind;
import org.apache.olingo.odata4.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.odata4.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriInfoImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SelectOptionImpl;

public class ExpandValidator implements Validator {
  private Edm edm;
  private Validator invokedByValidator;

  private int expandItemIndex;
  private ExpandOptionImpl expandOption;
  private ExpandItem expandItem;

  // --- Setup ---

  public ExpandValidator setGoUpValidator(final Validator parentValidator) {
    invokedByValidator = parentValidator;
    return this;
  }

  public ExpandValidator setExpand(final ExpandOptionImpl expand) {
    expandOption = expand;
    first();
    return this;
  }

  public ExpandValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // --- Navigation ---

  public UriResourceValidator goPath() {
    UriInfoImpl uriInfo = (UriInfoImpl) expandItem.getResourceInfo();

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("goPath() can only be used on UriInfoKind.resource");
    }

    return new UriResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoImplPath(uriInfo);

  }

  public UriResourceValidator goSelectItemPath(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) expandItem.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    UriInfoImpl uriInfo = (UriInfoImpl) item.getResourceInfo();

    return new UriResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoImplPath(uriInfo);

  }

  public ExpandValidator goExpand() {
    ExpandValidator val = new ExpandValidator();
    val.setExpand((ExpandOptionImpl) expandItem.getExpandOption());
    val.setGoUpValidator(this);
    return val;
  }

  public ExpandValidator goUpToExpandValidator() {
    return (ExpandValidator) invokedByValidator;
  }

  public UriResourceValidator goUpToUriResourceValidator() {
    return (UriResourceValidator) invokedByValidator;
  }

  public ExpandValidator first() {
    expandItemIndex = 0;
    expandItem = expandOption.getExpandItems().get(expandItemIndex);
    return this;
  }

  public ExpandValidator next() {
    expandItemIndex++;

    try {
      expandItem = expandOption.getExpandItems().get(expandItemIndex);
    } catch (IndexOutOfBoundsException ex) {
      fail("not enought segments");
    }
    return this;

  }

  public ExpandValidator isSegmentStar(final int index) {
    assertEquals(true, expandItem.isStar());
    return this;
  }

  public ExpandValidator isSegmentRef(final int index) {
    assertEquals(true, expandItem.isRef());
    return this;
  }

  public ExpandValidator isLevelText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getLevelsOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSkipText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getSkipOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isTopText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getTopOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isInlineCountText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getInlineCountOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSelectText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getSelectOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSelectItemStar(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) expandItem.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(true, item.isStar());
    return this;
  }

  public ExpandValidator isSelectItemAllOp(final int index, final FullQualifiedName fqn) {
    SelectOptionImpl select = (SelectOptionImpl) expandItem.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn.toString(), item.getAllOperationsInSchemaNameSpace().toString());
    return this;
  }

  public ExpandValidator isFilterText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getFilterOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isFilterSerialized(final String serialized) {
    FilterOptionImpl filter = (FilterOptionImpl) expandItem.getFilterOption();

    try {
      String tmp = FilterTreeToText.Serialize(filter);
      assertEquals(serialized, tmp);
    } catch (ExceptionVisitExpression e) {
      fail("Exception occured while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    } catch (ODataApplicationException e) {
      fail("Exception occured while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    }

    return this;
  }

}
