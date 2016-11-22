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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourceLambdaAll;
import org.apache.olingo.server.api.uri.UriResourceLambdaAny;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.UriResourceSingleton;
import org.apache.olingo.server.core.uri.UriResourceTypedImpl;
import org.apache.olingo.server.core.uri.UriResourceWithKeysImpl;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.core.uri.validator.UriValidator;

public class ResourceValidator implements TestValidator {
  private final OData odata = OData.newInstance();
  private Edm edm;
  private TestValidator invokedBy;
  private UriInfoResource uriInfo = null;

  private UriResource uriPathInfo = null;
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

  public ResourceValidator setUriInfoPath(final UriInfoResource uriInfoPath) {
    uriInfo = uriInfoPath;
    first();
    return this;
  }

  // --- Execution ---

  public ResourceValidator run(final String path) {
    UriInfo uriInfoTmp = null;
    try {
      uriInfoTmp = new Parser(edm, odata).parseUri(path, null, null, null);
    } catch (final ODataLibraryException e) {
      fail("Exception occurred while parsing the URI: " + path + "\n"
          + " Message: " + e.getMessage());
    }

    try {
      new UriValidator().validate(uriInfoTmp, HttpMethod.GET);
    } catch (final UriValidationException e) {
      fail("Exception occurred while validating the URI: " + path + "\n"
          + " Message: " + e.getMessage());
    }

    assertEquals("Invalid UriInfoKind: " + uriInfoTmp.getKind().toString(),
        UriInfoKind.resource, uriInfoTmp.getKind());

    setUriInfoPath(uriInfoTmp.asUriInfoResource());
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
    isUriPathInfoKind(UriResourceKind.function);
    return new FilterValidator()
        .setEdm(edm)
        .setExpression(((UriResourceFunction) uriPathInfo).getParameters().get(index).getExpression())
        .setValidator(this);
  }

  public FilterValidator goLambdaExpression() {
    assertTrue("invalid resource kind: " + uriPathInfo.getKind().toString(),
        uriPathInfo.getKind() == UriResourceKind.lambdaAll
        || uriPathInfo.getKind() == UriResourceKind.lambdaAny);
    return new FilterValidator()
        .setEdm(edm)
        .setExpression(uriPathInfo.getKind() == UriResourceKind.lambdaAll ?
            ((UriResourceLambdaAll) uriPathInfo).getExpression() :
            ((UriResourceLambdaAny) uriPathInfo).getExpression())
        .setValidator(this);
  }

  public ResourceValidator first() {
    return at(0);
  }

  public ResourceValidator n() {
    return at(uriResourceIndex + 1);
  }

  public ResourceValidator at(final int index) {
    uriResourceIndex = index;
    assertTrue("not enough segments", index < uriInfo.getUriResourceParts().size());
    uriPathInfo = uriInfo.getUriResourceParts().get(index);
    return this;
  }

  // --- Validation ---

  public ResourceValidator isLambdaVar(final String var) {
    assertTrue("invalid resource kind: " + uriPathInfo.getKind().toString(),
        uriPathInfo.getKind() == UriResourceKind.lambdaAll
        || uriPathInfo.getKind() == UriResourceKind.lambdaAny);
    final String actualVar = uriPathInfo.getKind() == UriResourceKind.lambdaAll ?
        ((UriResourceLambdaAll) uriPathInfo).getLambdaVariable() :
        ((UriResourceLambdaAny) uriPathInfo).getLambdaVariable();
    assertEquals(var, actualVar);
    return this;
  }

  public ResourceValidator isTypeFilter(final FullQualifiedName expectedType) {
    assertTrue("invalid resource kind: " + uriPathInfo.getKind().toString(),
        uriPathInfo.getKind() == UriResourceKind.complexProperty
        || uriPathInfo.getKind() == UriResourceKind.singleton);
    final EdmType actualType = ((UriResourceTypedImpl) uriPathInfo).getTypeFilter();
    assertNotNull("type information not set", actualType);
    assertEquals(expectedType, actualType.getFullQualifiedName());
    return this;
  }

  public ResourceValidator isType(final FullQualifiedName type) {
    assertTrue("invalid resource kind: "
        + (uriPathInfo.getKind() == null ? "null" : uriPathInfo.getKind().toString()),
        uriPathInfo instanceof UriResourcePartTyped);
    final EdmType actualType = ((UriResourcePartTyped) uriPathInfo).getType();
    assertNotNull("type information not set", actualType);
    assertEquals(type, actualType.getFullQualifiedName());
    return this;
  }

  public ResourceValidator isType(final FullQualifiedName type, final boolean isFinallyACollection) {
    isType(type);
    assertEquals(isFinallyACollection, ((UriResourcePartTyped) uriPathInfo).isCollection());
    return this;
  }

  public ResourceValidator isTypeFilterOnEntry(final FullQualifiedName type) {
    return isTypeFilter(false, type);
  }

  public ResourceValidator isTypeFilterOnCollection(final FullQualifiedName type) {
    return isTypeFilter(true, type);
  }

  private ResourceValidator isTypeFilter(final boolean onCollection, final FullQualifiedName type) {
    assertTrue("invalid resource kind: " + uriPathInfo.getKind().toString(),
        uriPathInfo instanceof UriResourceWithKeysImpl);
    UriResourceWithKeysImpl uriPathInfoKeyPred = (UriResourceWithKeysImpl) uriPathInfo;

    // input parameter type may be null in order to assert that the type filter is not set
    final EdmType actualType = onCollection ?
        uriPathInfoKeyPred.getTypeFilterOnCollection() :
        uriPathInfoKeyPred.getTypeFilterOnEntry();
    assertTrue("Expected a type filter of type: " + (type == null ? null : type.getFullQualifiedNameAsString()),
        type == null || actualType != null);
    assertEquals(type, type == null || actualType == null ? actualType : actualType.getFullQualifiedName());

    return this;
  }

  // other functions
  public ResourceValidator isKeyPredicateRef(final int index, final String name, final String referencedProperty) {
    final UriParameter keyPredicate = getKeyPredicate(index);
    assertEquals(name, keyPredicate.getName());
    assertEquals(referencedProperty, keyPredicate.getReferencedProperty());
    return this;
  }

  public ResourceValidator isKeyPredicateAlias(final int index, final String name, final String alias) {
    final UriParameter keyPredicate = getKeyPredicate(index);
    assertEquals(name, keyPredicate.getName());
    assertEquals(alias, keyPredicate.getAlias());
    return this;
  }

  public ResourceValidator isKeyPredicate(final int index, final String name, final String text) {
    final UriParameter keyPredicate = getKeyPredicate(index);
    assertEquals(name, keyPredicate.getName());
    assertEquals(text, keyPredicate.getText());
    return this;
  }

  private UriParameter getKeyPredicate(final int index) {
    assertTrue("invalid resource kind: " + uriPathInfo.getKind().toString(),
        uriPathInfo instanceof UriResourceWithKeysImpl);
    return ((UriResourceWithKeysImpl) uriPathInfo).getKeyPredicates().get(index);
  }

  public ResourceValidator isParameter(final int index, final String name, final String text) {
    isUriPathInfoKind(UriResourceKind.function);
    final UriParameter parameter = ((UriResourceFunction) uriPathInfo).getParameters().get(index);
    assertEquals(name, parameter.getName());
    assertEquals(text, parameter.getText());
    return this;
  }

  public ResourceValidator isParameterAlias(final int index, final String name, final String alias) {
    isUriPathInfoKind(UriResourceKind.function);
    final UriParameter parameter = ((UriResourceFunction) uriPathInfo).getParameters().get(index);
    assertEquals(name, parameter.getName());
    assertEquals(alias, parameter.getAlias());
    return this;
  }

  public ResourceValidator isPrimitiveProperty(final String name, final FullQualifiedName type,
      final boolean isCollection) {
    return isProperty(UriResourceKind.primitiveProperty, name, type, isCollection);
  }

  public ResourceValidator isComplexProperty(final String name, final FullQualifiedName type,
      final boolean isCollection) {
    return isProperty(UriResourceKind.complexProperty, name, type, isCollection);
  }

  public ResourceValidator isNavProperty(final String name, final FullQualifiedName type, final boolean isCollection) {
    return isProperty(UriResourceKind.navigationProperty, name, type, isCollection);
  }

  private ResourceValidator isProperty(final UriResourceKind kind,
      final String name, final FullQualifiedName type, final boolean isCollection) {
    isUriPathInfoKind(kind);
    final EdmElement property = kind == UriResourceKind.navigationProperty ?
        ((UriResourceNavigation) uriPathInfo).getProperty() :
        ((UriResourceProperty) uriPathInfo).getProperty();
    assertEquals(name, property.getName());
    isType(type, isCollection);
    return this;
  }

  public ResourceValidator isUriPathInfoKind(final UriResourceKind infoType) {
    assertNotNull(uriPathInfo);
    assertEquals("invalid resource kind: " + uriPathInfo.getKind().toString(), infoType, uriPathInfo.getKind());
    return this;
  }

  public ResourceValidator isAction(final String name) {
    isUriPathInfoKind(UriResourceKind.action);
    assertEquals(name, ((UriResourceAction) uriPathInfo).getAction().getName());
    return this;
  }

  public ResourceValidator isActionImport(final String name) {
    isUriPathInfoKind(UriResourceKind.action);
    assertEquals(name, ((UriResourceAction) uriPathInfo).getActionImport().getName());
    return this;
  }

  public ResourceValidator isFunction(final String name) {
    isUriPathInfoKind(UriResourceKind.function);
    assertEquals(name, ((UriResourceFunction) uriPathInfo).getFunction().getName());
    return this;
  }

  public ResourceValidator isFunctionImport(final String name) {
    isUriPathInfoKind(UriResourceKind.function);
    assertEquals(name, ((UriResourceFunction) uriPathInfo).getFunctionImport().getName());
    return this;
  }

  public ResourceValidator isEntitySet(final String name) {
    isUriPathInfoKind(UriResourceKind.entitySet);
    assertEquals(name, ((UriResourceEntitySet) uriPathInfo).getEntitySet().getName());
    return this;
  }

  public ResourceValidator isSingleton(final String name) {
    isUriPathInfoKind(UriResourceKind.singleton);
    assertEquals(name, ((UriResourceSingleton) uriPathInfo).getSingleton().getName());
    return this;
  }

  public ResourceValidator isValue() {
    return isUriPathInfoKind(UriResourceKind.value);
  }

  public ResourceValidator isCount() {
    return isUriPathInfoKind(UriResourceKind.count);
  }

  public ResourceValidator isRef() {
    return isUriPathInfoKind(UriResourceKind.ref);
  }

  public ResourceValidator isIt() {
    return isUriPathInfoKind(UriResourceKind.it);
  }
}
