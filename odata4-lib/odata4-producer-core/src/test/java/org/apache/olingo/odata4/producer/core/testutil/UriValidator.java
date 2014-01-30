/*******************************************************************************
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

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.odata4.producer.core.uri.ParserAdapter;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriParseTreeVisitor;
import org.apache.olingo.odata4.producer.core.uri.UriParserException;
import org.apache.olingo.odata4.producer.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FilterOptionImpl;

public class UriValidator implements Validator {
  private Edm edm;

  private UriInfoImpl uriInfo;

  // Setup
  public UriValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // Execution
  public UriValidator run(final String uri) {
    uriInfo = null;
    try {
      // uriInfoTmp = new UriParserImpl(edm).ParseUri(uri);
      uriInfo = (UriInfoImpl) ParserAdapter.parseUri(uri, new UriParseTreeVisitor(edm));
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

}
