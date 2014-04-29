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
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.commons.api.ODataApplicationException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceActionImpl;
import org.apache.olingo.server.core.uri.UriResourceComplexPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceEntitySetImpl;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.UriResourceImpl;
import org.apache.olingo.server.core.uri.UriResourceLambdaAllImpl;
import org.apache.olingo.server.core.uri.UriResourceLambdaAnyImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourcePrimitivePropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceSingletonImpl;
import org.apache.olingo.server.core.uri.UriResourceWithKeysImpl;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.ExpressionImpl;

public class ResourceValidator implements TestValidator {
  private Edm edm;
  private TestValidator invokedBy;
  private UriInfo uriInfo = null;

  private UriResourceImpl uriPathInfo = null;
  private int uriResourceIndex;

  // --- Setup ---

  public ResourceValidator setUpValidator(final TestValidator uriValidator) {
    invokedBy = uriValidator;
    return this;
  }

  public ResourceValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  public ResourceValidator setUriInfoImplPath(final UriInfoImpl uriInfoPath) {
    uriInfo = uriInfoPath;
    last();
    return this;
  }

  // --- Execution ---

  public ResourceValidator run(final String uri) {
    ParserWithLogging testParser = new ParserWithLogging();

    UriInfoImpl uriInfoTmp = null;
    uriPathInfo = null;
    try {
      uriInfoTmp = (UriInfoImpl) testParser.parseUri(uri, edm);
    } catch (UriParserException e) {
      fail("Exception occured while parsing the URI: " + uri + "\n"
          + " Message: " + e.getMessage());
    }

    if (uriInfoTmp.getKind() != UriInfoKind.resource) {
      fail("Invalid UriInfoKind: " + uriInfoTmp.getKind().toString());
    }
    uriInfo = uriInfoTmp;

    first();
    return this;
  }

  // --- Navigation ---

  public TestUriValidator goUpUriValidator() {
    return (TestUriValidator) invokedBy;
  }

  public ExpandValidator goUpExpandValidator() {
    return (ExpandValidator) invokedBy;
  }

  public FilterValidator goUpFilterValidator() {
    return (FilterValidator) invokedBy;
  }

  public FilterValidator goParameter(final int index) {
    assertEquals(UriResourceKind.function, uriPathInfo.getKind());
    UriResourceFunctionImpl function = (UriResourceFunctionImpl) uriPathInfo;

    return new FilterValidator()
        .setEdm(edm)
        .setExpression(function.getParameters().get(index).getExression())
        .setValidator(this);
  }

  public FilterValidator goLambdaExpression() {
    if (uriPathInfo.getKind() == UriResourceKind.lambdaAll) {
      return new FilterValidator()
          .setEdm(edm)
          .setExpression(((UriResourceLambdaAllImpl) uriPathInfo).getExpression());

    } else if (uriPathInfo.getKind() == UriResourceKind.lambdaAny) {
      return new FilterValidator()
          .setEdm(edm)
          .setExpression(((UriResourceLambdaAnyImpl) uriPathInfo).getExpression());
    } else {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }
    return null;
  }

  public ResourceValidator goSelectItem(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    UriInfoImpl uriInfo1 = (UriInfoImpl) item.getResourcePath();

    return new ResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoImplPath(uriInfo1);

  }

  public ExpandValidator goExpand() {
    ExpandOptionImpl expand = (ExpandOptionImpl) uriInfo.getExpandOption();
    if (expand == null) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    return new ExpandValidator().setUpValidator(this).setExpand(expand);
  }

  public ResourceValidator first() {
    uriResourceIndex = 0;
    uriPathInfo = (UriResourceImpl) uriInfo.getUriResourceParts().get(0);
    return this;
  }

  public ResourceValidator last() {
    uriResourceIndex = 0;

    try {
      uriPathInfo = (UriResourceImpl) uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1);
      uriResourceIndex = uriInfo.getUriResourceParts().size() - 1;
    } catch (IndexOutOfBoundsException ex) {
      fail("not enough segments");
    }

    return this;
  }

  public ResourceValidator n() {
    uriResourceIndex++;

    try {
      uriPathInfo = (UriResourceImpl) uriInfo.getUriResourceParts().get(uriResourceIndex);
    } catch (IndexOutOfBoundsException ex) {
      fail("not enough segments");
    }

    return this;
  }

  public ResourceValidator at(final int index) {
    uriResourceIndex = index;
    try {
      uriPathInfo = (UriResourceImpl) uriInfo.getUriResourceParts().get(index);
    } catch (IndexOutOfBoundsException ex) {
      fail("not enough segments");
    }
    return this;
  }

  // --- Validation ---

  public ResourceValidator isLambdaVar(final String var) {
    String actualVar = null;
    if (uriPathInfo.getKind() == UriResourceKind.lambdaAll) {
      actualVar = ((UriResourceLambdaAllImpl) uriPathInfo).getLambdaVariable();
    } else if (uriPathInfo.getKind() == UriResourceKind.lambdaAny) {
      actualVar = ((UriResourceLambdaAnyImpl) uriPathInfo).getLamdaVariable();
    } else {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    assertEquals(var, actualVar);
    return this;
  }

  public ResourceValidator isTypeFilter(final FullQualifiedName expectedType) {

    if (uriPathInfo.getKind() != UriResourceKind.complexProperty &&
        uriPathInfo.getKind() != UriResourceKind.singleton) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
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

  public ResourceValidator isType(final FullQualifiedName type) {
    if (!(uriPathInfo instanceof UriResourcePartTyped)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }
    UriResourcePartTyped uriPathInfoTyped = (UriResourcePartTyped) uriPathInfo;

    EdmType actualType = uriPathInfoTyped.getType();
    if (actualType == null) {
      fail("type information not set");
    }

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());

    assertEquals(type.toString(), actualName.toString());

    return this;
  }

  public ResourceValidator isType(final FullQualifiedName type, final boolean isFinallyACollection) {
    isType(type);
    assertEquals(isFinallyACollection, ((UriResourcePartTyped) uriPathInfo).isCollection());
    return this;
  }

  public ResourceValidator isTypeFilterOnEntry(final FullQualifiedName type) {
    if (!(uriPathInfo instanceof UriResourceWithKeysImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceWithKeysImpl uriPathInfoKeyPred = (UriResourceWithKeysImpl) uriPathInfo;

    // input parameter type may be null in order to assert that the singleTypeFilter is not set
    EdmType actualType = uriPathInfoKeyPred.getTypeFilterOnEntry();
    if (type == null) {
      assertEquals(type, actualType);
    } else {
      assertEquals(type.toString(), new FullQualifiedName(actualType.getNamespace(), actualType.getName()).toString());
    }

    return this;
  }

  public ResourceValidator isTypeFilterOnCollection(final FullQualifiedName expectedType) {
    if (!(uriPathInfo instanceof UriResourceWithKeysImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }
    UriResourceWithKeysImpl uriPathInfoKeyPred = (UriResourceWithKeysImpl) uriPathInfo;

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
  public ResourceValidator checkCustomParameter(final int index, final String name, final String value) {
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

  // TODO remove
  /*
   * public ResourceValidator isCollection(final boolean isCollection) {
   * if (!(uriPathInfo instanceof UriResourcePartTyped)) {
   * fail("invalid resource kind: " + uriPathInfo.getKind().toString());
   * }
   * UriResourcePartTyped uriPathInfoTyped = (UriResourcePartTyped) uriPathInfo;
   * 
   * EdmType type = uriPathInfoTyped.getType();
   * if (type == null) {
   * fail("isCollection: type == null");
   * }
   * assertEquals(isCollection, uriPathInfoTyped.isCollection());
   * return this;
   * }
   */

  public ResourceValidator isFilterString(final String expectedFilterTreeAsString) {

    ExpressionImpl filterTree = (ExpressionImpl) uriInfo.getFilterOption().getExpression();
    try {
      String filterTreeAsString = filterTree.accept(new FilterTreeToText());
      assertEquals(expectedFilterTreeAsString, filterTreeAsString);
    } catch (ExpressionVisitException e) {
      fail("isFilterString: Exception " + e.getMessage() + " occured");
    } catch (ODataApplicationException e) {
      fail("isFilterString: Exception " + e.getMessage() + " occured");
    }

    return this;
  }

  public ResourceValidator isKeyPredicateRef(final int index, final String name, final String refencedProperty) {
    if (!(uriPathInfo instanceof UriResourceWithKeysImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceWithKeysImpl info = (UriResourceWithKeysImpl) uriPathInfo;
    List<UriParameter> keyPredicates = info.getKeyPredicates();
    assertEquals(name, keyPredicates.get(index).getName());
    assertEquals(refencedProperty, keyPredicates.get(index).getRefencedProperty());
    return this;

  }

  public ResourceValidator isKeyPredicateAlias(final int index, final String name, final String alias) {
    if (!(uriPathInfo instanceof UriResourceWithKeysImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceWithKeysImpl info = (UriResourceWithKeysImpl) uriPathInfo;
    List<UriParameter> keyPredicates = info.getKeyPredicates();
    assertEquals(name, keyPredicates.get(index).getName());
    assertEquals(alias, keyPredicates.get(index).getAlias());
    return this;

  }

  public ResourceValidator isKeyPredicate(final int index, final String name, final String text) {
    if (!(uriPathInfo instanceof UriResourceWithKeysImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceWithKeysImpl info = (UriResourceWithKeysImpl) uriPathInfo;
    List<UriParameter> keyPredicates = info.getKeyPredicates();
    assertEquals(name, keyPredicates.get(index).getName());
    assertEquals(text, keyPredicates.get(index).getText());
    return this;

  }

  public ResourceValidator isParameter(final int index, final String name, final String text) {
    if (!(uriPathInfo instanceof UriResourceFunctionImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceFunctionImpl info = (UriResourceFunctionImpl) uriPathInfo;
    List<UriParameter> keyPredicates = info.getParameters();
    assertEquals(name, keyPredicates.get(index).getName());
    assertEquals(text, keyPredicates.get(index).getText());
    return this;

  }

  public ResourceValidator isParameterAlias(final int index, final String name, final String alias) {
    if (!(uriPathInfo instanceof UriResourceFunctionImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceFunctionImpl info = (UriResourceFunctionImpl) uriPathInfo;
    List<UriParameter> keyPredicates = info.getParameters();
    assertEquals(name, keyPredicates.get(index).getName());
    assertEquals(alias, keyPredicates.get(index).getAlias());
    return this;

  }

  public ResourceValidator isKind(final UriInfoKind kind) {
    assertEquals(kind, uriInfo.getKind());
    return this;
  }

  public ResourceValidator isPrimitiveProperty(final String name,
      final FullQualifiedName type, final boolean isCollection) {
    if (!(uriPathInfo instanceof UriResourcePrimitivePropertyImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourcePrimitivePropertyImpl uriPathInfoProp = (UriResourcePrimitivePropertyImpl) uriPathInfo;

    EdmElement property = uriPathInfoProp.getProperty();

    assertEquals(name, property.getName());
    assertEquals(type, new FullQualifiedName(property.getType().getNamespace(), property.getType().getName()));
    assertEquals(isCollection, property.isCollection());
    return this;
  }

  public ResourceValidator
      isComplexProperty(final String name, final FullQualifiedName type, final boolean isCollection) {
    if (!(uriPathInfo instanceof UriResourceComplexPropertyImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceComplexPropertyImpl uriPathInfoProp = (UriResourceComplexPropertyImpl) uriPathInfo;

    EdmElement property = uriPathInfoProp.getProperty();

    assertEquals(name, property.getName());
    assertEquals(type, new FullQualifiedName(property.getType().getNamespace(), property.getType().getName()));
    assertEquals(isCollection, property.isCollection());
    return this;
  }

  public ResourceValidator isNavProperty(final String name, final FullQualifiedName type, final boolean isCollection) {
    if (!(uriPathInfo instanceof UriResourceNavigationPropertyImpl)) {
      fail("invalid resource kind: " + uriPathInfo.getKind().toString());
    }

    UriResourceNavigationPropertyImpl uriPathInfoProp = (UriResourceNavigationPropertyImpl) uriPathInfo;

    EdmElement property = uriPathInfoProp.getProperty();

    assertEquals(name, property.getName());
    assertEquals(type, new FullQualifiedName(property.getType().getNamespace(), property.getType().getName()));
    assertEquals(isCollection, uriPathInfoProp.isCollection());
    return this;
  }

  public ResourceValidator isUriPathInfoKind(final UriResourceKind infoType) {
    assertNotNull(uriPathInfo);
    assertEquals(infoType, uriPathInfo.getKind());
    return this;
  }

  public ResourceValidator isAction(final String name) {
    assertEquals(UriResourceKind.action, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceActionImpl) uriPathInfo).getAction().getName());
    return this;
  }

  public ResourceValidator isFunction(final String name) {
    assertEquals(UriResourceKind.function, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceFunctionImpl) uriPathInfo).getFunction().getName());
    return this;
  }

  public ResourceValidator isFunctionImport(final String name) {
    assertEquals(UriResourceKind.function, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceFunctionImpl) uriPathInfo).getFunctionImport().getName());
    return this;
  }

  public ResourceValidator isEntitySet(final String name) {
    assertEquals(UriResourceKind.entitySet, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceEntitySetImpl) uriPathInfo).getEntitySet().getName());
    return this;
  }

  public ResourceValidator isComplex(final String name) {
    assertEquals(UriResourceKind.complexProperty, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceComplexPropertyImpl) uriPathInfo).getProperty().getName());
    return this;
  }

  public ResourceValidator isSingleton(final String name) {
    assertEquals(UriResourceKind.singleton, uriPathInfo.getKind());
    assertEquals(name, ((UriResourceSingletonImpl) uriPathInfo).getSingleton().getName());
    return this;
  }

  public ResourceValidator isValue() {
    assertEquals(UriResourceKind.value, uriPathInfo.getKind());
    return this;
  }

  public ResourceValidator isCount() {
    assertEquals(UriResourceKind.count, uriPathInfo.getKind());
    return this;
  }

  public ResourceValidator isRef() {
    assertEquals(UriResourceKind.ref, uriPathInfo.getKind());
    return this;
  }

  public ResourceValidator isActionImport(final String actionName) {
    assertEquals(UriResourceKind.action, uriPathInfo.getKind());
    assertEquals(actionName, ((UriResourceActionImpl) uriPathInfo).getActionImport().getName());
    return this;
  }

  public ResourceValidator isIt() {
    assertEquals(UriResourceKind.it, uriPathInfo.getKind());
    return this;
  }

  public ResourceValidator isTopText(final String topText) {
    assertEquals(topText, uriInfo.getTopOption().getText());
    return this;
  }

  public ResourceValidator isFormatText(final String formatText) {
    assertEquals(formatText, uriInfo.getFormatOption().getText());
    return this;
  }

  public ResourceValidator isInlineCountText(final String inlineCountText) {
    assertEquals(inlineCountText, uriInfo.getCountOption().getText());
    return this;
  }

  public ResourceValidator isSkipText(final String skipText) {
    assertEquals(skipText, uriInfo.getSkipOption().getText());
    return this;
  }

  public ResourceValidator isSkipTokenText(final String skipTokenText) {
    assertEquals(skipTokenText, uriInfo.getSkipTokenOption().getText());
    return this;
  }

  public ResourceValidator isSelectItemStar(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(true, item.isStar());
    return this;
  }

  public ResourceValidator isSelectItemAllOp(final int index, final FullQualifiedName fqn) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn.toString(), item.getAllOperationsInSchemaNameSpace().toString());
    return this;
  }

  public ResourceValidator isSelectStartType(final int index, final FullQualifiedName fullName) {
    SelectOptionImpl select = (SelectOptionImpl) uriInfo.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);

    EdmType actualType = item.getStartTypeFilter();

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());
    assertEquals(fullName, actualName);
    return this;
  }

}
