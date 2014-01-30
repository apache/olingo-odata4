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
package org.apache.olingo.odata4.producer.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandItem;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.QueryOptionImpl;

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
    UriInfoImpl uriInfo = (UriInfoImpl) expandItem.getPath();

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("goPath() can only be used on UriInfoKind.resource");
    }

    return new UriResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoImplPath(uriInfo);

  }

  public ExpandValidator goExpand() {
    ExpandValidator val = new ExpandValidator();
    val.setExpand((ExpandOptionImpl) expandItem.getExpand());
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
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getLevels();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSkipText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getSkip();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isTopText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getTop();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isInlineCountText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getInlineCount();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSelectText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getSelect();
    assertEquals(text, option.getText());
    return this;
  }
  
  public ExpandValidator isFilterText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getFilter();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isFilterSerialized(final String serialized) {
    FilterOptionImpl filter = (FilterOptionImpl) expandItem.getFilter();

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
