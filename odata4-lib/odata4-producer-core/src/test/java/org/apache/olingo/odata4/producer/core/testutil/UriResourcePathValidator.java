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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImplPath;
import org.apache.olingo.odata4.producer.core.uri.UriKeyPredicateList;
import org.apache.olingo.odata4.producer.core.uri.UriParserException;
import org.apache.olingo.odata4.producer.core.uri.UriParserImpl;
import org.apache.olingo.odata4.producer.core.uri.UriPathInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriPathInfoEntitySetImpl;
import org.apache.olingo.odata4.producer.core.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.core.uri.expression.Expression;

public class UriResourcePathValidator {
  private Edm edm;
  // this validator can only be used on resourcePaths
  public UriInfoImplPath uriInfo = null;
  private UriPathInfoImpl uriPathInfo = null;

  public UriResourcePathValidator run(String uri) {
    UriInfoImpl uriInfoTmp = null;
    uriPathInfo = null;
    try {
      uriInfoTmp = new UriParserImpl(edm).ParseUri(uri);
    } catch (UriParserException e) {
      fail("Exception occured while parsing the URI: " + uri + "\n"
          + " Exception: " + e.getMessage());
    }

    if (!(uriInfoTmp instanceof UriInfoImplPath)) {
      fail("Validator can only be used on resourcePaths");
    }
    this.uriInfo = (UriInfoImplPath) uriInfoTmp;

    last();
    return this;
  }
  
  
  // short cut which avoid adding the ESabc?$filter when testing filter to each URI
  public FilterValidator runFilter(String filter) {
    String uri = "ESabc?$filter=" + filter.trim(); // TODO check what to do with trailing spaces in the URI
    this.run(uri);
    return new FilterValidator(this);
  }
  
  public UriResourcePathValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // navigation for uriPathInfo
  public UriResourcePathValidator at(int index) {
    try {
      uriPathInfo = uriInfo.getUriPathInfo(index);
    } catch (IndexOutOfBoundsException ex) {
      uriPathInfo = null;
    }

    return this;
  }

  public UriResourcePathValidator first() {
    try {
      uriPathInfo = ((UriInfoImplPath) uriInfo).getUriPathInfo(0);
    } catch (IndexOutOfBoundsException ex) {
      uriPathInfo = null;
    }
    return this;
  }

  public UriResourcePathValidator last() {
    if (uriInfo instanceof UriInfoImplPath) {
      uriPathInfo = ((UriInfoImplPath) uriInfo).getLastUriPathInfo();
    } else {
      fail("UriInfo not instanceof UriInfoImplPath");
    }

    return this;
  }

  // check types

  public UriResourcePathValidator isInitialType(FullQualifiedName expectedType) {
    EdmType actualType = uriPathInfo.getInitialType();
    if (actualType == null) {
      fail("isInitialType: actualType == null");
    }

    FullQualifiedName actualTypeName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());

    assertEquals(expectedType.toString(), actualTypeName.toString());
    return this;
  }

  public UriResourcePathValidator isType(FullQualifiedName type) {
    EdmType actualType = uriPathInfo.getType();
    if (actualType == null) {
      fail("type information not set");
    }

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());

    assertEquals(type.toString(), actualName.toString());
    return this;
  }

  public UriResourcePathValidator isSingleTypeFilter(FullQualifiedName type) {
    // input parameter type may be null in order to assert that the singleTypeFilter is not set
    EdmType actualType = uriPathInfo.getSingleTypeFilter();
    if (type == null) {
      assertEquals(type, actualType);
    } else {
      assertEquals(type.toString(), new FullQualifiedName(actualType.getNamespace(), actualType.getName()).toString());
    }

    return this;
  }

  public UriResourcePathValidator isCollectionTypeFilter(FullQualifiedName expectedType) {
    // input parameter type may be null in order to assert that the collectionTypeFilter is not set
    EdmType actualType = uriPathInfo.getCollectionTypeFilter();
    if (expectedType == null) {
      assertEquals(expectedType, actualType);
    } else {
      FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());
      assertEquals(expectedType.toString(), actualName.toString());
    }

    return this;
  }

  // other functions
  public UriResourcePathValidator hasQueryParameter(String parameter, int count) {
    if (uriInfo == null) {
      fail("hasQueryParameter: uriInfo == null");
    }

    int actualCount = uriInfo.getQueryParameters(parameter).size();
    assertEquals(count, actualCount);
    return this;
  }

  public UriResourcePathValidator isCollection(boolean isCollection) {
    EdmType type = uriPathInfo.getType();
    if (type == null) {
      fail("isCollection: type == null");
    }
    assertEquals(isCollection, uriPathInfo.isCollection());
    return this;
  }

  public UriResourcePathValidator isFilterString(String expectedFilterTreeAsString) {
    Expression filterTree = this.uriInfo.getFilter();
    try {
      String filterTreeAsString = filterTree.accept(new FilterTreeToText());
      assertEquals(expectedFilterTreeAsString, filterTreeAsString);
    } catch (ExceptionVisitExpression e) {
      fail("isFilterString: Exception " + e.getMessage() + " occured");
    }

    return this;
  }

  public UriResourcePathValidator isKeyPredicate(int index, String name, String value) {
    if (!(uriPathInfo instanceof UriPathInfoEntitySetImpl)) {
      //TODO add and "or" for FunctionImports 
      fail("isKeyPredicate: uriPathInfo is not instanceof UriPathInfoEntitySetImpl");
    }

    UriPathInfoEntitySetImpl info = (UriPathInfoEntitySetImpl) uriPathInfo;
    UriKeyPredicateList keyPredicates = info.getKeyPredicates();
    assertEquals(name, keyPredicates.getName(index));
    assertEquals(value, keyPredicates.getValue(index));
    return this;

  }

  public UriResourcePathValidator isKind(UriInfoKind kind) {
    assertEquals(kind, uriInfo.getKind());
    return this;
  }

  public UriResourcePathValidator isProperties(List<String> asList) {
    assertNotNull(uriPathInfo);

    int index = 0;
    while (index < asList.size()) {
      String propertyName = uriPathInfo.getProperty(index).getName();

      assertEquals(asList.get(index), propertyName);

      index++;
    }

    return this;
  }

  public UriResourcePathValidator isProperty(int index, String name, FullQualifiedName type) {
    if (index >= uriPathInfo.getPropertyCount()) {
      fail("isProperty: invalid index");
    }

    EdmElement property = uriPathInfo.getProperty(index);

    assertEquals(name, property.getName());
    assertEquals(type, new FullQualifiedName(property.getType().getNamespace(), property.getType().getName()));
    return this;
  }

  public UriResourcePathValidator isUriPathInfoKind(UriPathInfoKind infoType) {
    assertNotNull(uriPathInfo);
    assertEquals(infoType, uriPathInfo.getKind());
    return this;
  }

  

}
