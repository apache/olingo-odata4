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
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfo;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriParameter;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;
import org.apache.olingo.odata4.producer.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.core.uri.ParserAdapter;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriParseTreeVisitor;
import org.apache.olingo.odata4.producer.core.uri.UriParserException;
import org.apache.olingo.odata4.producer.core.uri.UriResourceActionImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceComplexPropertyImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceEntitySetImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplKeyPred;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplTyped;
import org.apache.olingo.odata4.producer.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourcePartImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourcePropertyImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceSimplePropertyImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceSingletonImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.expression.ExpressionImpl;

public class UriResourceValidator implements Validator {
  private Edm edm;
  private Validator invokedBy;
  private UriInfo uriInfo = null;

  private UriResourcePartImpl uriPathInfo = null;
  private int uriResourceIndex;

  // --- Setup ---

  public UriResourceValidator setUpValidator(Validator uriValidator) {
    invokedBy = uriValidator;
    return this;
  }

  public UriResourceValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  public UriResourceValidator setUriInfoImplPath(UriInfoImpl uriInfoPath) {
    this.uriInfo = uriInfoPath;
    last();
    return this;
  }

  // --- Execution ---

  public UriResourceValidator run(String uri) {
    UriInfoImpl uriInfoTmp = null;
    uriPathInfo = null;
    try {
      uriInfoTmp = ParserAdapter.parseUri(uri, new UriParseTreeVisitor(edm));
    } catch (UriParserException e) {
      fail("Exception occured while parsing the URI: " + uri + "\n"
          + " Exception: " + e.getMessage());
    }

    if (uriInfoTmp.getKind() != UriInfoKind.resource) {
      fail("Validator can only be used on resourcePaths");
    }
    this.uriInfo = uriInfoTmp;

    first();
    return this;
  }

  // --- Navigation ---

  public UriValidator goUpUriValidator() {
    return (UriValidator) invokedBy;
  }

  public ExpandValidator goUpExpandValidator() {
    return (ExpandValidator) invokedBy;
  }

  public ExpandValidator goExpand() {

    ExpandOptionImpl expand = (ExpandOptionImpl) uriInfo.getExpandOption();
    if (expand == null) {
      fail("goExpand can only be used if there is an expand option");
    }

    return new ExpandValidator().setUpValidator(this).setExpand(expand);
  }

  public UriResourceValidator first() {
    uriResourceIndex = 0;
    uriPathInfo = (UriResourcePartImpl) uriInfo.getUriResourceParts().get(0);

    return this;
  }

  public UriResourceValidator last() {
    try {
      uriResourceIndex = 0;
      uriPathInfo = (UriResourcePartImpl) uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1);
      uriResourceIndex = uriInfo.getUriResourceParts().size() - 1;
    } catch (IndexOutOfBoundsException ex) {
      fail("not enought segemnts");
    }

    return this;
  }

  public UriResourceValidator n() {
    uriResourceIndex++;

    try {
      uriPathInfo = (UriResourcePartImpl) uriInfo.getUriResourceParts().get(uriResourceIndex);
    } catch (IndexOutOfBoundsException ex) {
      fail("not enought segemnts");
    }

    return this;
  }

  public UriResourceValidator at(int index) {
    uriResourceIndex = index;
    try {
      uriPathInfo = (UriResourcePartImpl) uriInfo.getUriResourceParts().get(index);
    } catch (IndexOutOfBoundsException ex) {
      fail("not enought segemnts");
    }
    return this;
  }

  // --- Validation ---

  public UriResourceValidator isTypeFilter(FullQualifiedName expectedType) {

    if (uriPathInfo.getKind() != UriResourceKind.complexProperty &&
        uriPathInfo.getKind() != UriResourceKind.singleton) {
      fail("type wrong ujriResourceKind ( you may also check isTypeFilterOnEntry or isTypeFilterOnCollection");
    }

    EdmType actualType = null;
    if (uriPathInfo instanceof UriResourceComplexPropertyImpl) {
      actualType = ((UriResourceComplexPropertyImpl) uriPathInfo).getComplexTypeFilter();
    } else if (uriPathInfo instanceof UriResourceSingletonImpl) {
      actualType = ((UriResourceSingletonImpl) uriPathInfo).getEntityTypeFilter();
    }

    if (actualType == null) {
      fail("type information not set");
    }

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());

    assertEquals(expectedType.toString(), actualName.toString());
    return this;
  }

  public UriResourceValidator isType(FullQualifiedName type) {
    if (!(uriPathInfo instanceof UriResourceImplTyped)) {
      fail("not typed");
    }
    UriResourceImplTyped uriPathInfoTyped = (UriResourceImplTyped) uriPathInfo;

    EdmType actualType = uriPathInfoTyped.getType();
    if (actualType == null) {
      fail("type information not set");
    }

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());

    assertEquals(type.toString(), actualName.toString());

    return this;
  }

  public UriResourceValidator isType(FullQualifiedName type, boolean isFinallyACollection) {
    isType(type);
    assertEquals(isFinallyACollection, ((UriResourceImplTyped) uriPathInfo).isCollection());
    return this;
  }

  public UriResourceValidator isTypeFilterOnEntry(FullQualifiedName type) {
    if (!(uriPathInfo instanceof UriResourceImplKeyPred)) {
      fail("not keypred");
    }
    UriResourceImplKeyPred uriPathInfoKeyPred = (UriResourceImplKeyPred) uriPathInfo;

    // input parameter type may be null in order to assert that the singleTypeFilter is not set
    EdmType actualType = uriPathInfoKeyPred.getTypeFilterOnEntry();
    if (type == null) {
      assertEquals(type, actualType);
    } else {
      assertEquals(type.toString(), new FullQualifiedName(actualType.getNamespace(), actualType.getName()).toString());
    }

    return this;
  }

  public UriResourceValidator isTypeFilterOnCollection(FullQualifiedName expectedType) {
    if (!(uriPathInfo instanceof UriResourceImplKeyPred)) {
      fail("not keypred");
    }
    UriResourceImplKeyPred uriPathInfoKeyPred = (UriResourceImplKeyPred) uriPathInfo;

    // input parameter type may be null in order to assert that the collectionTypeFilter is not set
    EdmType actualType = uriPathInfoKeyPred.getTypeFilterOnCollection();
    if (expectedType == null) {
      assertEquals(expectedType, actualType);
    } else {
      FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());
      assertEquals(expectedType.toString(), actualName.toString());
    }

    return this;
  }

  // other functions
  public UriResourceValidator checkCustomParameter(int index, String name, String value) {
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

  // TODO remove
  public UriResourceValidator isCollection(boolean isCollection) {
    if (!(uriPathInfo instanceof UriResourceImplTyped)) {
      fail("not typed");
    }
    UriResourceImplTyped uriPathInfoTyped = (UriResourceImplTyped) uriPathInfo;

    EdmType type = uriPathInfoTyped.getType();
    if (type == null) {
      fail("isCollection: type == null");
    }
    assertEquals(isCollection, uriPathInfoTyped.isCollection());
    return this;
  }

  public UriResourceValidator isFilterString(String expectedFilterTreeAsString) {

    ExpressionImpl filterTree = (ExpressionImpl) this.uriInfo.getFilterOption().getExpression();
    try {
      String filterTreeAsString = filterTree.accept(new FilterTreeToText());
      assertEquals(expectedFilterTreeAsString, filterTreeAsString);
    } catch (ExceptionVisitExpression e) {
      fail("isFilterString: Exception " + e.getMessage() + " occured");
    } catch (ODataApplicationException e) {
      fail("isFilterString: Exception " + e.getMessage() + " occured");
    }

    return this;
  }

  public UriResourceValidator isKeyPredicate(int index, String name, String text) {
    if (!(uriPathInfo instanceof UriResourceImplKeyPred)) {
      // TODO add and "or" for FunctionImports
      fail("isKeyPredicate: uriPathInfo is not instanceof UriPathInfoEntitySetImpl");
    }

    UriResourceImplKeyPred info = (UriResourceImplKeyPred) uriPathInfo;
    List<UriParameter> keyPredicates = info.getKeyPredicates();
    assertEquals(name, keyPredicates.get(index).getName());
    assertEquals(text, keyPredicates.get(index).getText());
    return this;

  }

  public UriResourceValidator isParameter(int index, String name, String text) {
    if (!(uriPathInfo instanceof UriResourceFunctionImpl)) {
      // TODO add and "or" for FunctionImports
      fail("isKeyPredicate: uriPathInfo is not instanceof UriResourceFunctionImpl");
    }

    UriResourceFunctionImpl info = (UriResourceFunctionImpl) uriPathInfo;
    List<UriParameter> keyPredicates = info.getParameters();
    assertEquals(name, keyPredicates.get(index).getName());
    assertEquals(text, keyPredicates.get(index).getText());
    return this;

  }

  public UriResourceValidator isKind(UriInfoKind kind) {
    assertEquals(kind, uriInfo.getKind());
    return this;
  }

  public UriResourceValidator isProperty(String name, FullQualifiedName type) {
    if (!(uriPathInfo instanceof UriResourcePropertyImpl)) {
      // TODO add and "or" for FunctionImports
      fail("not a property");
    }

    UriResourcePropertyImpl uriPathInfoProp = (UriResourcePropertyImpl) uriPathInfo;

    EdmElement property = uriPathInfoProp.getProperty();

    assertEquals(name, property.getName());
    assertEquals(type, new FullQualifiedName(property.getType().getNamespace(), property.getType().getName()));
    return this;
  }

  public UriResourceValidator isComplexProperty(int index, String name, FullQualifiedName type) {
    if (!(uriPathInfo instanceof UriResourceComplexPropertyImpl)) {
      // TODO add and "or" for FunctionImports
      fail("not a property");
    }

    UriResourceComplexPropertyImpl uriPathInfoProp = (UriResourceComplexPropertyImpl) uriPathInfo;

    EdmElement property = uriPathInfoProp.getProperty();

    assertEquals(name, property.getName());
    assertEquals(type, new FullQualifiedName(property.getType().getNamespace(), property.getType().getName()));
    return this;
  }

  public UriResourceValidator isUriPathInfoKind(UriResourceKind infoType) {
    assertNotNull(uriPathInfo);
    assertEquals(infoType, uriPathInfo.getKind());
    return this;
  }

  public UriResourceValidator isAction(String name) {
    assertEquals(UriResourceKind.action, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceActionImpl) uriPathInfo).getAction().getName());
    return this;
  }

  public UriResourceValidator isFunction(String name) {
    assertEquals(UriResourceKind.function, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceFunctionImpl) uriPathInfo).getFunction().getName());
    return this;
  }

  public UriResourceValidator isFunctionImport(String name) {
    assertEquals(UriResourceKind.function, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceFunctionImpl) uriPathInfo).getFunctionImport().getName());
    return this;
  }

  public UriResourceValidator isEntitySet(String name) {
    assertEquals(UriResourceKind.entitySet, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceEntitySetImpl) uriPathInfo).getEntitySet().getName());
    return this;
  }

  public UriResourceValidator isComplex(String name) {
    assertEquals(UriResourceKind.complexProperty, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceComplexPropertyImpl) uriPathInfo).getProperty().getName());
    return this;
  }

  public UriResourceValidator isSimple(String name) {
    assertEquals(UriResourceKind.simpleProperty, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceSimplePropertyImpl) uriPathInfo).getProperty().getName());
    return this;
  }

  public UriResourceValidator isSingleton(String name) {
    assertEquals(UriResourceKind.singleton, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceSingletonImpl) uriPathInfo).getSingleton().getName());
    return this;
  }

  public UriResourceValidator isValue() {
    assertEquals(UriResourceKind.value, uriPathInfo.getKind());
    return this;
  }

  public UriResourceValidator isCount() {
    assertEquals(UriResourceKind.count, uriPathInfo.getKind());
    return this;
  }

  public UriResourceValidator isRef() {
    assertEquals(UriResourceKind.ref, uriPathInfo.getKind());
    return this;
  }

  public UriResourceValidator isActionImport(String actionName) {
    assertEquals(UriResourceKind.action, uriPathInfo.getKind());
    assertEquals(actionName, ((UriResourceActionImpl) uriPathInfo).getActionImport().getName());
    return this;
  }

  public UriResourceValidator isNav(String name) {
    assertEquals(UriResourceKind.navigationProperty, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceNavigationPropertyImpl) uriPathInfo).getNavigationProperty().getName());
    return this;
  }

  public UriResourceValidator isIt() {
    assertEquals(UriResourceKind.it, uriPathInfo.getKind());
    return this;
  }

  public UriResourceValidator isTopText(String topText) {
    assertEquals(topText,uriInfo.getTopOption().getText());
    return this;
  }

  public UriResourceValidator isFormatText(String formatText) {
    assertEquals(formatText,uriInfo.getFormatOption().getText());
    return this;
  }

  public UriResourceValidator isInlineCountText(String inlineCountText) {
    assertEquals(inlineCountText,uriInfo.getInlineCountOption().getText());
    return this;
  }

  public UriResourceValidator isSkipText(String skipText) {
    assertEquals(skipText,uriInfo.getSkipOption().getText());
    return this;
  }

  public UriResourceValidator isSkipTokenText(String skipTokenText) {
    assertEquals(skipTokenText,uriInfo.getSkipTokenOption().getText());
    return this;
  }
}
