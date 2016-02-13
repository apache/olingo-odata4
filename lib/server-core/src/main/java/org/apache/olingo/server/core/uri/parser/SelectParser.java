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
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceActionImpl;
import org.apache.olingo.server.core.uri.UriResourceComplexPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourcePrimitivePropertyImpl;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.queryoption.SelectItemImpl;
import org.apache.olingo.server.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class SelectParser {

  private final Edm edm;

  public SelectParser(final Edm edm) {
    this.edm = edm;
  }

  public SelectOption parse(UriTokenizer tokenizer, final EdmStructuredType referencedType,
      final boolean referencedIsCollection) throws UriParserException, UriValidationException {
    List<SelectItem> selectItems = new ArrayList<SelectItem>();
    SelectItem item;
    do {
      item = parseItem(tokenizer, referencedType, referencedIsCollection);
      selectItems.add(item);
    } while (tokenizer.next(TokenKind.COMMA));

    return new SelectOptionImpl().setSelectItems(selectItems);
  }

  private SelectItem parseItem(UriTokenizer tokenizer,
      final EdmStructuredType referencedType, final boolean referencedIsCollection) throws UriParserException {
    SelectItemImpl item = new SelectItemImpl();
    if (tokenizer.next(TokenKind.STAR)) {
      item.setStar(true);

    } else if (tokenizer.next(TokenKind.QualifiedName)) {
      // The namespace or its alias could consist of dot-separated OData identifiers.
      final FullQualifiedName allOperationsInSchema = parseAllOperationsInSchema(tokenizer);
      if (allOperationsInSchema != null) {
        item.addAllOperationsInSchema(allOperationsInSchema);

      } else {
        ensureReferencedTypeNotNull(referencedType);
        final FullQualifiedName qualifiedName = new FullQualifiedName(tokenizer.getText());
        EdmStructuredType type = edm.getEntityType(qualifiedName);
        if (type == null) {
          type = edm.getComplexType(qualifiedName);
        }
        if (type == null) {
          item.setResourcePath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
              parseBoundOperation(tokenizer, qualifiedName, referencedType, referencedIsCollection)));

        } else {
          if (type.compatibleTo(referencedType)) {
            item.setTypeFilter(type);
            if (tokenizer.next(TokenKind.SLASH)) {
              ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
              UriInfoImpl resource = new UriInfoImpl().setKind(UriInfoKind.resource);
              addSelectPath(tokenizer, type, resource);
              item.setResourcePath(resource);
            }
          } else {
            throw new UriParserSemanticException("The type cast is not compatible.",
                UriParserSemanticException.MessageKeys.INCOMPATIBLE_TYPE_FILTER, type.getName());
          }
        }
      }

    } else {
      ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
      // The namespace or its alias could be a single OData identifier.
      final FullQualifiedName allOperationsInSchema = parseAllOperationsInSchema(tokenizer);
      if (allOperationsInSchema != null) {
        item.addAllOperationsInSchema(allOperationsInSchema);

      } else {
        ensureReferencedTypeNotNull(referencedType);
        UriInfoImpl resource = new UriInfoImpl().setKind(UriInfoKind.resource);
        addSelectPath(tokenizer, referencedType, resource);
        item.setResourcePath(resource);
      }
    }

    return item;
  }

  private FullQualifiedName parseAllOperationsInSchema(UriTokenizer tokenizer) throws UriParserException {
    final String namespace = tokenizer.getText();
    if (tokenizer.next(TokenKind.DOT)) {
      if (tokenizer.next(TokenKind.STAR)) {
        // Validate the namespace.  Currently a namespace from a non-default schema is not supported.
        // There is no direct access to the namespace without loading the whole schema;
        // however, the default entity container should always be there, so its access methods can be used.
        if (edm.getEntityContainer(new FullQualifiedName(namespace, edm.getEntityContainer().getName())) == null) {
          throw new UriParserSemanticException("Wrong namespace '" + namespace + "'.",
              UriParserSemanticException.MessageKeys.UNKNOWN_PART, namespace);
        }
        return new FullQualifiedName(namespace, tokenizer.getText());
      } else {
        throw new UriParserSemanticException("Expected star after dot.",
            UriParserSemanticException.MessageKeys.UNKNOWN_PART, "");
      }
    }
    return null;
  }

  private void ensureReferencedTypeNotNull(final EdmStructuredType referencedType) throws UriParserException {
    if (referencedType == null) {
      throw new UriParserSemanticException("The referenced part is not typed.",
          UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS, "select");
    }
  }

  private UriResourcePartTyped parseBoundOperation(UriTokenizer tokenizer, final FullQualifiedName qualifiedName,
      final EdmStructuredType referencedType, final boolean referencedIsCollection) throws UriParserException {
    final EdmAction boundAction = edm.getBoundAction(qualifiedName,
        referencedType.getFullQualifiedName(),
        referencedIsCollection);
    if (boundAction == null) {
      final List<String> parameterNames = parseFunctionParameterNames(tokenizer);
      final EdmFunction boundFunction = edm.getBoundFunction(qualifiedName,
          referencedType.getFullQualifiedName(), referencedIsCollection, parameterNames);
      if (boundFunction == null) {
        throw new UriParserSemanticException("Function not found.",
            UriParserSemanticException.MessageKeys.UNKNOWN_PART, qualifiedName.getFullQualifiedNameAsString());
      } else {
        return new UriResourceFunctionImpl(null, boundFunction, null);
      }
    } else {
      return new UriResourceActionImpl(boundAction);
    }
  }

  private List<String> parseFunctionParameterNames(UriTokenizer tokenizer) throws UriParserException {
    List<String> names = new ArrayList<String>();
    if (tokenizer.next(TokenKind.OPEN)) {
      do {
        ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
        names.add(tokenizer.getText());
      } while (tokenizer.next(TokenKind.COMMA));
      ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    }
    return names;
  }

  private void addSelectPath(UriTokenizer tokenizer, final EdmStructuredType referencedType, UriInfoImpl resource)
      throws UriParserException {
    final String name = tokenizer.getText();
    final EdmProperty property = referencedType.getStructuralProperty(name);

    if (property == null) {
      final EdmNavigationProperty navigationProperty = referencedType.getNavigationProperty(name);
      if (navigationProperty == null) {
        throw new UriParserSemanticException("Selected property not found.",
            UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE,
            referencedType.getName(), name);
      } else {
        resource.addResourcePart(new UriResourceNavigationPropertyImpl(navigationProperty));
      }

    } else if (property.isPrimitive()
        || property.getType().getKind() == EdmTypeKind.ENUM
        || property.getType().getKind() == EdmTypeKind.DEFINITION) {
      resource.addResourcePart(new UriResourcePrimitivePropertyImpl(property));

    } else {
      UriResourceComplexPropertyImpl complexPart = new UriResourceComplexPropertyImpl(property);
      resource.addResourcePart(complexPart);
      if (tokenizer.next(TokenKind.SLASH)) {
        if (tokenizer.next(TokenKind.QualifiedName)) {
          final FullQualifiedName qualifiedName = new FullQualifiedName(tokenizer.getText());
          final EdmComplexType type = edm.getComplexType(qualifiedName);
          if (type == null) {
            throw new UriParserSemanticException("Type not found.",
                UriParserSemanticException.MessageKeys.UNKNOWN_TYPE, qualifiedName.getFullQualifiedNameAsString());
          } else if (type.compatibleTo(property.getType())) {
            complexPart.setTypeFilter(type);
            if (tokenizer.next(TokenKind.SLASH)) {
              if (tokenizer.next(TokenKind.ODataIdentifier)) {
                addSelectPath(tokenizer, type, resource);
              } else {
                throw new UriParserSemanticException("Unknown part after '/'.",
                    UriParserSemanticException.MessageKeys.UNKNOWN_PART, "");
              }
            }
          } else {
            throw new UriParserSemanticException("The type cast is not compatible.",
                UriParserSemanticException.MessageKeys.INCOMPATIBLE_TYPE_FILTER, type.getName());
          }
        } else if (tokenizer.next(TokenKind.ODataIdentifier)) {
          addSelectPath(tokenizer, (EdmStructuredType) property.getType(), resource);
        } else if (tokenizer.next(TokenKind.SLASH)) {
          throw new UriParserSyntaxException("Illegal $select expression.",
              UriParserSyntaxException.MessageKeys.SYNTAX);
        } else {
          throw new UriParserSemanticException("Unknown part after '/'.",
              UriParserSemanticException.MessageKeys.UNKNOWN_PART, "");
        }
      }
    }
  }
}
