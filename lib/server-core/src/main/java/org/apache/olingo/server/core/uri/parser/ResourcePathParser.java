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
package org.apache.olingo.server.core.uri.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriParameterImpl;
import org.apache.olingo.server.core.uri.UriResourceActionImpl;
import org.apache.olingo.server.core.uri.UriResourceComplexPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceCountImpl;
import org.apache.olingo.server.core.uri.UriResourceEntitySetImpl;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourcePrimitivePropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceRefImpl;
import org.apache.olingo.server.core.uri.UriResourceSingletonImpl;
import org.apache.olingo.server.core.uri.UriResourceTypedImpl;
import org.apache.olingo.server.core.uri.UriResourceValueImpl;
import org.apache.olingo.server.core.uri.UriResourceWithKeysImpl;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class ResourcePathParser {

  private final Edm edm;
  private final EdmEntityContainer edmEntityContainer;
  private final OData odata;
  private UriTokenizer tokenizer;

  public ResourcePathParser(final Edm edm, final OData odata) {
    this.edm = edm;
    edmEntityContainer = edm.getEntityContainer();
    this.odata = odata;
  }

  public UriResource parsePathSegment(final String pathSegment, UriResource previous)
      throws UriParserException, UriValidationException {
    tokenizer = new UriTokenizer(pathSegment);

    // The order is important.
    // A qualified name should not be parsed as identifier and let the tokenizer halt at '.'.

    if (previous == null) {
      if (tokenizer.next(TokenKind.QualifiedName)) {
        throw new UriParserSemanticException("The initial segment must not be namespace-qualified.",
            UriParserSemanticException.MessageKeys.NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT,
            new FullQualifiedName(tokenizer.getText()).getNamespace());
      } else if (tokenizer.next(TokenKind.ODataIdentifier)) {
        return leadingResourcePathSegment();
      }

    } else {
      if (tokenizer.next(TokenKind.REF)) {
        return ref(previous);
      } else if (tokenizer.next(TokenKind.VALUE)) {
        return value(previous);
      } else if (tokenizer.next(TokenKind.COUNT)) {
        return count(previous);
      } else if (tokenizer.next(TokenKind.QualifiedName)) {
        return boundOperationOrTypeCast(previous);
      } else if (tokenizer.next(TokenKind.ODataIdentifier)) {
        return navigationOrProperty(previous);
      }
    }

    throw new UriParserSyntaxException("Unexpected start of resource-path segment.",
        UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  public UriInfoImpl parseDollarEntityTypeCast(final String pathSegment) throws UriParserException {
    UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.entityId);
    tokenizer = new UriTokenizer(pathSegment);
    ParserHelper.requireNext(tokenizer, TokenKind.QualifiedName);
    final String name = tokenizer.getText();
    ParserHelper.requireTokenEnd(tokenizer);
    final EdmEntityType type = edm.getEntityType(new FullQualifiedName(name));
    if (type == null) {
      throw new UriParserSemanticException("Type '" + name + "' not found.",
          UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, name);
    } else {
      uriInfo.setEntityTypeCast(type);
    }
    return uriInfo;
  }

  public UriInfoImpl parseCrossjoinSegment(final String pathSegment) throws UriParserException {
    UriInfoImpl uriInfo = new UriInfoImpl().setKind(UriInfoKind.crossjoin);
    tokenizer = new UriTokenizer(pathSegment);
    ParserHelper.requireNext(tokenizer, TokenKind.CROSSJOIN);
    ParserHelper.requireNext(tokenizer, TokenKind.OPEN);
    // At least one entity-set name is mandatory.  Try to fetch all.
    do {
      ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
      final String name = tokenizer.getText();
      final EdmEntitySet edmEntitySet = edmEntityContainer.getEntitySet(name);
      if (edmEntitySet == null) {
        throw new UriParserSemanticException("Expected Entity Set Name.",
            UriParserSemanticException.MessageKeys.UNKNOWN_PART, name);
      } else {
        uriInfo.addEntitySetName(name);
      }
    } while (tokenizer.next(TokenKind.COMMA));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    ParserHelper.requireTokenEnd(tokenizer);
    return uriInfo;
  }

  private UriResource ref(final UriResource previous) throws UriParserException {
    ParserHelper.requireTokenEnd(tokenizer);
    requireTyped(previous, "$ref");
    if (((UriResourcePartTyped) previous).getType() instanceof EdmEntityType) {
      return new UriResourceRefImpl();
    } else {
      throw new UriParserSemanticException("$ref is only allowed on entity types.",
          UriParserSemanticException.MessageKeys.ONLY_FOR_ENTITY_TYPES, "$ref");
    }
  }

  private UriResource value(final UriResource previous) throws UriParserException {
    ParserHelper.requireTokenEnd(tokenizer);
    requireTyped(previous, "$value");
    if (!((UriResourcePartTyped) previous).isCollection()) {
      return new UriResourceValueImpl();
    } else {
      throw new UriParserSemanticException("$value is only allowed on typed path segments.",
          UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS, "$value");
    }
  }

  private UriResource count(final UriResource previous) throws UriParserException {
    ParserHelper.requireTokenEnd(tokenizer);
    requireTyped(previous, "$count");
    if (((UriResourcePartTyped) previous).isCollection()) {
      return new UriResourceCountImpl();
    } else {
      throw new UriParserSemanticException("$count is only allowed on collections.",
          UriParserSemanticException.MessageKeys.ONLY_FOR_COLLECTIONS, "$count");
    }
  }

  private UriResource leadingResourcePathSegment() throws UriParserException, UriValidationException {
    final String oDataIdentifier = tokenizer.getText();

    final EdmEntitySet edmEntitySet = edmEntityContainer.getEntitySet(oDataIdentifier);
    if (edmEntitySet != null) {
      final UriResourceEntitySetImpl entitySetResource = new UriResourceEntitySetImpl().setEntitSet(edmEntitySet);

      if (tokenizer.next(TokenKind.OPEN)) {
        final List<UriParameter> keyPredicates = keyPredicate(entitySetResource.getEntityType(), null);
        entitySetResource.setKeyPredicates(keyPredicates);
      }

      ParserHelper.requireTokenEnd(tokenizer);
      return entitySetResource;
    }

    final EdmSingleton edmSingleton = edmEntityContainer.getSingleton(oDataIdentifier);
    if (edmSingleton != null) {
      ParserHelper.requireTokenEnd(tokenizer);
      return new UriResourceSingletonImpl().setSingleton(edmSingleton);
    }

    final EdmActionImport edmActionImport = edmEntityContainer.getActionImport(oDataIdentifier);
    if (edmActionImport != null) {
      ParserHelper.requireTokenEnd(tokenizer);
      return new UriResourceActionImpl().setActionImport(edmActionImport);
    }

    final EdmFunctionImport edmFunctionImport = edmEntityContainer.getFunctionImport(oDataIdentifier);
    if (edmFunctionImport != null) {
      return functionCall(edmFunctionImport, null, null, false);
    }

    if (tokenizer.next(TokenKind.OPEN) || tokenizer.next(TokenKind.EOF)) {
      throw new UriParserSemanticException("Unexpected start of resource-path segment.",
          UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND, oDataIdentifier);
    } else {
      throw new UriParserSyntaxException("Unexpected start of resource-path segment.",
          UriParserSyntaxException.MessageKeys.SYNTAX);
    }
  }

  private UriResource navigationOrProperty(final UriResource previous)
      throws UriParserException, UriValidationException {
    final String name = tokenizer.getText();

    UriResourcePartTyped previousTyped = null;
    EdmStructuredType structType = null;
    requireTyped(previous, name);
    if (((UriResourcePartTyped) previous).getType() instanceof EdmStructuredType) {
      previousTyped = (UriResourcePartTyped) previous;
      final EdmType previousTypeFilter = getPreviousTypeFilter(previousTyped);
      structType = (EdmStructuredType) (previousTypeFilter == null ? previousTyped.getType() : previousTypeFilter);
    } else {
      throw new UriParserSemanticException(
          "Cannot parse '" + name + "'; previous path segment is not a structural type.",
          UriParserSemanticException.MessageKeys.RESOURCE_PART_MUST_BE_PRECEDED_BY_STRUCTURAL_TYPE, name);
    }

    if (previousTyped.isCollection()) {
      throw new UriParserSemanticException("Property '" + name + "' is not allowed after collection.",
          UriParserSemanticException.MessageKeys.PROPERTY_AFTER_COLLECTION, name);
    }

    final EdmProperty property = structType.getStructuralProperty(name);
    if (property != null) {
      return property.isPrimitive()
          || property.getType().getKind() == EdmTypeKind.ENUM
          || property.getType().getKind() == EdmTypeKind.DEFINITION ?
          new UriResourcePrimitivePropertyImpl().setProperty(property) :
          new UriResourceComplexPropertyImpl().setProperty(property);
    }
    final EdmNavigationProperty navigationProperty = structType.getNavigationProperty(name);
    if (navigationProperty == null) {
      throw new UriParserSemanticException("Property '" + name + "' not found in type '"
          + structType.getFullQualifiedName().getFullQualifiedNameAsString() + "'",
          UriParserSemanticException.MessageKeys.PROPERTY_NOT_IN_TYPE,
          structType.getFullQualifiedName().getFullQualifiedNameAsString(), name);
    }
    List<UriParameter> keyPredicate = null;
    if (tokenizer.next(TokenKind.OPEN)) {
      if (navigationProperty.isCollection()) {
        keyPredicate = keyPredicate(navigationProperty.getType(), navigationProperty.getPartner());
      } else {
        throw new UriParserSemanticException("A key is not allowed on non-collection navigation properties.",
            UriParserSemanticException.MessageKeys.KEY_NOT_ALLOWED);
      }
    }
    ParserHelper.requireTokenEnd(tokenizer);
    return new UriResourceNavigationPropertyImpl()
        .setNavigationProperty(navigationProperty)
        .setKeyPredicates(keyPredicate);
  }

  private UriResource boundOperationOrTypeCast(UriResource previous)
      throws UriParserException, UriValidationException {
    final FullQualifiedName name = new FullQualifiedName(tokenizer.getText());
    requireTyped(previous, name.getFullQualifiedNameAsString());
      final UriResourcePartTyped previousTyped = (UriResourcePartTyped) previous;
      final EdmType previousTypeFilter = getPreviousTypeFilter(previousTyped);
      final EdmType previousType = previousTypeFilter == null ? previousTyped.getType() : previousTypeFilter;
      final EdmAction boundAction = edm.getBoundAction(name,
          previousType.getFullQualifiedName(),
          previousTyped.isCollection());
      if (boundAction != null) {
        ParserHelper.requireTokenEnd(tokenizer);
        return new UriResourceActionImpl().setAction(boundAction);
      }
      EdmStructuredType type = edm.getEntityType(name);
      if (type == null) {
        type = edm.getComplexType(name);
      }
      if (type != null) {
        return typeCast(name, type, previousTyped);
      }
      if (tokenizer.next(TokenKind.EOF)) {
        throw new UriParserSemanticException("Type '" + name.getFullQualifiedNameAsString() + "' not found.",
            UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, name.getFullQualifiedNameAsString());
      }
      return functionCall(null, name,
          previousType.getFullQualifiedName(),
          previousTyped.isCollection());
  }

  private void requireTyped(final UriResource previous, final String forWhat) throws UriParserException {
    if (previous == null || !(previous instanceof UriResourcePartTyped)) {
      throw new UriParserSemanticException("Path segment before '" + forWhat + "' is not typed.",
          UriParserSemanticException.MessageKeys.PREVIOUS_PART_NOT_TYPED, forWhat);
    }
  }

  private List<UriParameter> keyPredicate(final EdmEntityType edmEntityType, final EdmNavigationProperty partner)
      throws UriParserException, UriValidationException {
    final List<EdmKeyPropertyRef> keyPropertyRefs = edmEntityType.getKeyPropertyRefs();
    if (tokenizer.next(TokenKind.CLOSE)) {
      throw new UriParserSemanticException(
          "Expected " + keyPropertyRefs.size() + " key predicates but none.",
          UriParserSemanticException.MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES,
          Integer.toString(keyPropertyRefs.size()), "0");
    }
    List<UriParameter> keys = new ArrayList<UriParameter>();
    Map<String, String> referencedNames = new HashMap<String, String>();

    if (partner != null) {
      // Prepare list of potentially missing keys to be filled from referential constraints.
      for (final String name : edmEntityType.getKeyPredicateNames()) {
        final String referencedName = partner.getReferencingPropertyName(name);
        if (referencedName != null) {
          referencedNames.put(name, referencedName);
        }
      }
    }

    if (tokenizer.next(TokenKind.ODataIdentifier)) {
      keys.addAll(compoundKey(edmEntityType));
    } else if (keyPropertyRefs.size() - referencedNames.size() == 1) {
      for (final EdmKeyPropertyRef candidate : keyPropertyRefs) {
        if (referencedNames.get(candidate.getName()) == null) {
          keys.add(simpleKey(candidate));
          break;
        }
      }
    } else {
      throw new UriParserSemanticException(
          "Expected " + (keyPropertyRefs.size() -referencedNames.size()) + " key predicates but found one.",
          UriParserSemanticException.MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES,
          Integer.toString(keyPropertyRefs.size() - referencedNames.size()), "1");
    }

    if (keys.size() < keyPropertyRefs.size() && partner != null) {
      // Fill missing keys from referential constraints.
      for (final String name : edmEntityType.getKeyPredicateNames()) {
        boolean found = false;
        for (final UriParameter key : keys) {
          if (name.equals(key.getName())) {
            found = true;
            break;
          }
        }
        if (!found && referencedNames.get(name) != null) {
          keys.add(0, new UriParameterImpl().setName(name).setReferencedProperty(referencedNames.get(name)));
        }
      }
    }

    // Check that all key predicates are filled from the URI.
    if (keys.size() < keyPropertyRefs.size()) {
      throw new UriParserSemanticException(
          "Expected " + keyPropertyRefs.size() + " key predicates but found " + keys.size() + ".",
          UriParserSemanticException.MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES,
          Integer.toString(keyPropertyRefs.size()), Integer.toString(keys.size()));
    } else {
      return keys;
    }
  }

  private UriParameter simpleKey(final EdmKeyPropertyRef edmKeyPropertyRef)
      throws UriParserException, UriValidationException {
    final EdmProperty edmProperty = edmKeyPropertyRef == null ? null : edmKeyPropertyRef.getProperty();
    if (nextPrimitiveTypeValue(
        edmProperty == null ? null : (EdmPrimitiveType) edmProperty.getType(),
        edmProperty == null ? false : edmProperty.isNullable())) {
      final String literalValue = tokenizer.getText();
      ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
      return createUriParameter(edmProperty, edmKeyPropertyRef.getName(), literalValue);
    } else {
      throw new UriParserSemanticException("The key value is not valid.",
          UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE, edmKeyPropertyRef.getName());
    }
  }

  private List<UriParameter> compoundKey(final EdmEntityType edmEntityType)
      throws UriParserException, UriValidationException {

    List<UriParameter> parameters = new ArrayList<UriParameter>();
    List<String> parameterNames = new ArrayList<String>();

    // To validate that each key predicate is exactly specified once, we use a list to pick from.
    List<String> remainingKeyNames = new ArrayList<String>(edmEntityType.getKeyPredicateNames());

    // At least one key predicate is mandatory.  Try to fetch all.
    boolean hasComma = false;
    do {
      final String keyPredicateName = tokenizer.getText();
      if (parameterNames.contains(keyPredicateName)) {
        throw new UriValidationException("Duplicated key property " + keyPredicateName,
            UriValidationException.MessageKeys.DOUBLE_KEY_PROPERTY, keyPredicateName);
      }
      if (remainingKeyNames.isEmpty()) {
        throw new UriParserSemanticException("Too many key properties.",
            UriParserSemanticException.MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES,
            Integer.toString(parameters.size()), Integer.toString(parameters.size() + 1));
      }
      if (!remainingKeyNames.remove(keyPredicateName)) {
        throw new UriValidationException("Unknown key property " + keyPredicateName,
            UriValidationException.MessageKeys.INVALID_KEY_PROPERTY, keyPredicateName);
      }
      parameters.add(keyValuePair(keyPredicateName, edmEntityType));
      parameterNames.add(keyPredicateName);
      hasComma = tokenizer.next(TokenKind.COMMA);
      if (hasComma) {
        ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
      }
    } while (hasComma);
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);

    return parameters;
  }

  private UriParameter keyValuePair(final String keyPredicateName, final EdmEntityType edmEntityType)
      throws UriParserException, UriValidationException {
    final EdmKeyPropertyRef keyPropertyRef = edmEntityType.getKeyPropertyRef(keyPredicateName);
    final EdmProperty edmProperty = keyPropertyRef == null ? null : keyPropertyRef.getProperty();
    if (edmProperty == null) {
      throw new UriValidationException(keyPredicateName + " is not a valid key property name.",
          UriValidationException.MessageKeys.INVALID_KEY_PROPERTY, keyPredicateName);
    }
    ParserHelper.requireNext(tokenizer, TokenKind.EQ);
    if (tokenizer.next(TokenKind.COMMA) || tokenizer.next(TokenKind.CLOSE) || tokenizer.next(TokenKind.EOF)) {
      throw new UriParserSyntaxException("Key value expected.", UriParserSyntaxException.MessageKeys.SYNTAX);
    }
    if (nextPrimitiveTypeValue((EdmPrimitiveType) edmProperty.getType(), edmProperty.isNullable())) {
      return createUriParameter(edmProperty, keyPredicateName, tokenizer.getText());
    } else {
      throw new UriParserSemanticException(keyPredicateName + " has not a valid  key value.",
          UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE, keyPredicateName);
    }
  }

  private UriParameter createUriParameter(final EdmProperty edmProperty, final String parameterName,
      final String literalValue) throws UriParserException, UriValidationException {
    if (literalValue.startsWith("@")) {
      return new UriParameterImpl()
          .setName(parameterName)
          .setAlias(literalValue);
    }

    final EdmPrimitiveType primitiveType = (EdmPrimitiveType) edmProperty.getType();
    try {
      if (!(primitiveType.validate(primitiveType.fromUriLiteral(literalValue), edmProperty.isNullable(),
          edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode()))) {
        throw new UriValidationException("Invalid key property",
            UriValidationException.MessageKeys.INVALID_KEY_PROPERTY, parameterName);
      }
    } catch (final EdmPrimitiveTypeException e) {
      throw new UriValidationException("Invalid key property",
          UriValidationException.MessageKeys.INVALID_KEY_PROPERTY, parameterName);
    }

    return new UriParameterImpl()
        .setName(parameterName)
        .setText("null".equals(literalValue) ? null : literalValue);
  }

  private UriResource typeCast(final FullQualifiedName name, final EdmStructuredType type,
      final UriResourcePartTyped previousTyped) throws UriParserException, UriValidationException {
    if (type.compatibleTo(previousTyped.getType())) {
      EdmType previousTypeFilter = null;
      if (previousTyped instanceof UriResourceWithKeysImpl) {
        if (previousTyped.isCollection()) {
          previousTypeFilter = ((UriResourceWithKeysImpl) previousTyped).getTypeFilterOnCollection();
          if (previousTypeFilter != null) {
            throw new UriParserSemanticException("Type filters are not chainable.",
                UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE,
                previousTypeFilter.getName(), type.getName());
          }
          ((UriResourceWithKeysImpl) previousTyped).setCollectionTypeFilter(type);
        } else {
          previousTypeFilter = ((UriResourceWithKeysImpl) previousTyped).getTypeFilterOnEntry();
          if (previousTypeFilter != null) {
            throw new UriParserSemanticException("Type filters are not chainable.",
                UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE,
                previousTypeFilter.getName(), type.getName());
          }
          ((UriResourceWithKeysImpl) previousTyped).setEntryTypeFilter(type);
        }
        if (tokenizer.next(TokenKind.OPEN)) {
          ((UriResourceWithKeysImpl) previousTyped).setKeyPredicates(
              keyPredicate((EdmEntityType) type, null));
        }
      } else {
        previousTypeFilter = ((UriResourceTypedImpl) previousTyped).getTypeFilter();
        if (previousTypeFilter != null) {
          throw new UriParserSemanticException("Type filters are not chainable.",
              UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE,
              previousTypeFilter.getName(), type.getName());
        }
        ((UriResourceTypedImpl) previousTyped).setTypeFilter(type);
      }
      ParserHelper.requireTokenEnd(tokenizer);
      return null;
    } else {
      throw new UriParserSemanticException(
          "Type filter not compatible to previous path segment: " + name.getFullQualifiedNameAsString(),
          UriParserSemanticException.MessageKeys.INCOMPATIBLE_TYPE_FILTER, name.getFullQualifiedNameAsString());
    }
  }

  private EdmType getPreviousTypeFilter(final UriResourcePartTyped previousTyped) {
    if (previousTyped instanceof UriResourceWithKeysImpl) {
      return ((UriResourceWithKeysImpl) previousTyped).getTypeFilterOnEntry() == null ?
          ((UriResourceWithKeysImpl) previousTyped).getTypeFilterOnCollection() :
          ((UriResourceWithKeysImpl) previousTyped).getTypeFilterOnEntry();
    } else {
      return ((UriResourceTypedImpl) previousTyped).getTypeFilter();
    }
  }

  private UriResource functionCall(final EdmFunctionImport edmFunctionImport,
      final FullQualifiedName boundFunctionName, final FullQualifiedName bindingParameterTypeName,
      final boolean isBindingParameterCollection) throws UriParserException, UriValidationException {
    final List<UriParameter> parameters = functionParameters();
    List<String> names = new ArrayList<String>();
    for (final UriParameter parameter : parameters) {
      names.add(parameter.getName());
    }
    EdmFunction function = null;
    if (edmFunctionImport != null) {
      function = edmFunctionImport.getUnboundFunction(names);
      if (function == null) {
        throw new UriParserSemanticException(
            "Function of function import '" + edmFunctionImport.getName() + "' "
                + "with parameters " + names.toString() + " not found.",
            UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND, edmFunctionImport.getName(), names.toString());
      }
    } else {
      function = edm.getBoundFunction(boundFunctionName,
          bindingParameterTypeName, isBindingParameterCollection, names);
      if (function == null) {
        throw new UriParserSemanticException(
            "Function " + boundFunctionName + " not found.",
            UriParserSemanticException.MessageKeys.UNKNOWN_PART, boundFunctionName.getFullQualifiedNameAsString());
      }
    }
    UriResourceFunctionImpl resource = new UriResourceFunctionImpl()
        .setFunctionImport(edmFunctionImport, null)
        .setFunction(function)
        .setParameters(parameters);
    if (tokenizer.next(TokenKind.OPEN)) {
      if (function.getReturnType() != null
          && function.getReturnType().getType().getKind() == EdmTypeKind.ENTITY
          && function.getReturnType().isCollection()) {
        resource.setKeyPredicates(
            keyPredicate((EdmEntityType) function.getReturnType().getType(), null));
      } else {
        throw new UriParserSemanticException("A key is not allowed.",
            UriParserSemanticException.MessageKeys.KEY_NOT_ALLOWED);
      }
    }
    ParserHelper.requireTokenEnd(tokenizer);
    return resource;
  }

  private List<UriParameter> functionParameters() throws UriParserException {
    List<UriParameter> parameters = new ArrayList<UriParameter>();
    ParserHelper.requireNext(tokenizer, TokenKind.OPEN);
    if (tokenizer.next(TokenKind.CLOSE)) {
      return parameters;
    }
    do {
      ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
      final String name = tokenizer.getText();
      if (parameters.contains(name)) {
        throw new UriParserSemanticException("Duplicated function parameter " + name,
            UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE, name);
      }
      ParserHelper.requireNext(tokenizer, TokenKind.EQ);
      if (tokenizer.next(TokenKind.COMMA) || tokenizer.next(TokenKind.CLOSE) || tokenizer.next(TokenKind.EOF)) {
        throw new UriParserSyntaxException("Parameter value expected.", UriParserSyntaxException.MessageKeys.SYNTAX);
      }
      if (tokenizer.next(TokenKind.ParameterAliasName)) {
        parameters.add(new UriParameterImpl().setName(name).setAlias(tokenizer.getText()));
      } else if (nextPrimitiveValue()) {
        final String literalValue = tokenizer.getText();
        parameters.add(new UriParameterImpl().setName(name)
            .setText("null".equals(literalValue) ? null : literalValue));
      } else {
        throw new UriParserSemanticException("Wrong parameter value.",
            UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE, "");
      }
    } while (tokenizer.next(TokenKind.COMMA));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return parameters;
  }

  private boolean nextPrimitiveTypeValue(final EdmPrimitiveType primitiveType, final boolean nullable) {
    final EdmPrimitiveType type = primitiveType instanceof EdmTypeDefinition ?
        ((EdmTypeDefinition) primitiveType).getUnderlyingType() :
        primitiveType;
    if (tokenizer.next(TokenKind.ParameterAliasName)) {
      return true;
    } else if (nullable && tokenizer.next(TokenKind.NULL)) {
      return true;

    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean).equals(type)) {
      return tokenizer.next(TokenKind.BooleanValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String).equals(type)) {
      return tokenizer.next(TokenKind.StringValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte).equals(type)
        || odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte).equals(type)
        || odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16).equals(type)
        || odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32).equals(type)
        || odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int64).equals(type)) {
      return tokenizer.next(TokenKind.IntegerValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Guid).equals(type)) {
      return tokenizer.next(TokenKind.GuidValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date).equals(type)) {
      return tokenizer.next(TokenKind.DateValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.DateTimeOffset).equals(type)) {
      return tokenizer.next(TokenKind.DateTimeOffsetValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.TimeOfDay).equals(type)) {
      return tokenizer.next(TokenKind.TimeOfDayValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal).equals(type)) {
      // The order is important.
      // A decimal value should not be parsed as integer and let the tokenizer stop at the decimal point.
      return tokenizer.next(TokenKind.DecimalValue)
          || tokenizer.next(TokenKind.IntegerValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double).equals(type)) {
      // The order is important.
      // A floating-point value should not be parsed as decimal and let the tokenizer stop at 'E'.
      // A decimal value should not be parsed as integer and let the tokenizer stop at the decimal point.
      return tokenizer.next(TokenKind.DoubleValue)
          || tokenizer.next(TokenKind.DecimalValue)
          || tokenizer.next(TokenKind.IntegerValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration).equals(type)) {
      return tokenizer.next(TokenKind.DurationValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Binary).equals(type)) {
      return tokenizer.next(TokenKind.BinaryValue);
    } else if (type.getKind() == EdmTypeKind.ENUM) {
      return tokenizer.next(TokenKind.EnumValue);
    } else {
      return false;
    }
  }

  private boolean nextPrimitiveValue() {
    return tokenizer.next(TokenKind.NULL)
        || tokenizer.next(TokenKind.BooleanValue)
        || tokenizer.next(TokenKind.StringValue)

        // The order of the next seven expressions is important in order to avoid
        // finding partly parsed tokens (counter-intuitive as it may be, even a GUID may start with digits ...).
        || tokenizer.next(TokenKind.DoubleValue)
        || tokenizer.next(TokenKind.DecimalValue)
        || tokenizer.next(TokenKind.GuidValue)
        || tokenizer.next(TokenKind.DateTimeOffsetValue)
        || tokenizer.next(TokenKind.DateValue)
        || tokenizer.next(TokenKind.TimeOfDayValue)
        || tokenizer.next(TokenKind.IntegerValue)

        || tokenizer.next(TokenKind.DurationValue)
        || tokenizer.next(TokenKind.BinaryValue)
        || tokenizer.next(TokenKind.EnumValue);
  }
}
