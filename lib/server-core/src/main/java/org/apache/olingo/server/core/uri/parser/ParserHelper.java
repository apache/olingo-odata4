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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.core.ODataImpl;
import org.apache.olingo.server.core.uri.UriParameterImpl;
import org.apache.olingo.server.core.uri.UriResourceTypedImpl;
import org.apache.olingo.server.core.uri.UriResourceWithKeysImpl;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class ParserHelper {

  private static final OData odata = new ODataImpl();

  protected static final Map<TokenKind, EdmPrimitiveTypeKind> tokenToPrimitiveType;
  static {
    /* Enum and null are not present in the map. These have to be handled differently. */
    Map<TokenKind, EdmPrimitiveTypeKind> temp = new HashMap<TokenKind, EdmPrimitiveTypeKind>();
    temp.put(TokenKind.BooleanValue, EdmPrimitiveTypeKind.Boolean);
    temp.put(TokenKind.StringValue, EdmPrimitiveTypeKind.String);
    // Very large integer values are of type Edm.Decimal but this is handled elsewhere.
    temp.put(TokenKind.IntegerValue, EdmPrimitiveTypeKind.Int64);
    temp.put(TokenKind.GuidValue, EdmPrimitiveTypeKind.Guid);
    temp.put(TokenKind.DateValue, EdmPrimitiveTypeKind.Date);
    temp.put(TokenKind.DateTimeOffsetValue, EdmPrimitiveTypeKind.DateTimeOffset);
    temp.put(TokenKind.TimeOfDayValue, EdmPrimitiveTypeKind.TimeOfDay);
    temp.put(TokenKind.DecimalValue, EdmPrimitiveTypeKind.Decimal);
    temp.put(TokenKind.DoubleValue, EdmPrimitiveTypeKind.Double);
    temp.put(TokenKind.DurationValue, EdmPrimitiveTypeKind.Duration);
    temp.put(TokenKind.BinaryValue, EdmPrimitiveTypeKind.Binary);

    temp.put(TokenKind.GeographyPoint, EdmPrimitiveTypeKind.GeographyPoint);
    temp.put(TokenKind.GeometryPoint, EdmPrimitiveTypeKind.GeometryPoint);
    temp.put(TokenKind.GeographyLineString, EdmPrimitiveTypeKind.GeographyLineString);
    temp.put(TokenKind.GeometryLineString, EdmPrimitiveTypeKind.GeometryLineString);
    temp.put(TokenKind.GeographyPolygon, EdmPrimitiveTypeKind.GeographyPolygon);
    temp.put(TokenKind.GeometryPolygon, EdmPrimitiveTypeKind.GeometryPolygon);
    temp.put(TokenKind.GeographyMultiPoint, EdmPrimitiveTypeKind.GeographyMultiPoint);
    temp.put(TokenKind.GeometryMultiPoint, EdmPrimitiveTypeKind.GeometryMultiPoint);
    temp.put(TokenKind.GeographyMultiLineString, EdmPrimitiveTypeKind.GeographyMultiLineString);
    temp.put(TokenKind.GeometryMultiLineString, EdmPrimitiveTypeKind.GeometryMultiLineString);
    temp.put(TokenKind.GeographyMultiPolygon, EdmPrimitiveTypeKind.GeographyMultiPolygon);
    temp.put(TokenKind.GeometryMultiPolygon, EdmPrimitiveTypeKind.GeometryMultiPolygon);
    temp.put(TokenKind.GeographyCollection, EdmPrimitiveTypeKind.GeographyCollection);
    temp.put(TokenKind.GeometryCollection, EdmPrimitiveTypeKind.GeometryCollection);

    tokenToPrimitiveType = Collections.unmodifiableMap(temp);
  }

  public static void requireNext(UriTokenizer tokenizer, final TokenKind required) throws UriParserException {
    if (!tokenizer.next(required)) {
      throw new UriParserSyntaxException("Expected token '" + required.toString() + "' not found.",
          UriParserSyntaxException.MessageKeys.SYNTAX);
    }
  }

  public static void requireTokenEnd(UriTokenizer tokenizer) throws UriParserException {
    requireNext(tokenizer, TokenKind.EOF);
  }

  public static TokenKind next(UriTokenizer tokenizer, final TokenKind... kinds) {
    for (final TokenKind kind : kinds) {
      if (tokenizer.next(kind)) {
        return kind;
      }
    }
    return null;
  }

  public static TokenKind nextPrimitiveValue(UriTokenizer tokenizer) {
    return next(tokenizer,
        TokenKind.NULL,
        TokenKind.BooleanValue,
        TokenKind.StringValue,

        // The order of the next seven expressions is important in order to avoid
        // finding partly parsed tokens (counter-intuitive as it may be, even a GUID may start with digits ...).
        TokenKind.DoubleValue,
        TokenKind.DecimalValue,
        TokenKind.GuidValue,
        TokenKind.DateTimeOffsetValue,
        TokenKind.DateValue,
        TokenKind.TimeOfDayValue,
        TokenKind.IntegerValue,

        TokenKind.DurationValue,
        TokenKind.BinaryValue,
        TokenKind.EnumValue,

        // Geography and geometry literals are defined to be primitive,
        // although they contain several parts with their own meaning.
        TokenKind.GeographyPoint,
        TokenKind.GeometryPoint,
        TokenKind.GeographyLineString,
        TokenKind.GeometryLineString,
        TokenKind.GeographyPolygon,
        TokenKind.GeometryPolygon,
        TokenKind.GeographyMultiPoint,
        TokenKind.GeometryMultiPoint,
        TokenKind.GeographyMultiLineString,
        TokenKind.GeometryMultiLineString,
        TokenKind.GeographyMultiPolygon,
        TokenKind.GeometryMultiPolygon,
        TokenKind.GeographyCollection,
        TokenKind.GeometryCollection);
  }

  protected static List<UriParameter> parseFunctionParameters(UriTokenizer tokenizer,
      final Edm edm, final EdmType referringType, final boolean withComplex)
      throws UriParserException, UriValidationException {
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
      } else if (tokenizer.next(TokenKind.jsonArrayOrObject)) {
        if (withComplex) {
          parameters.add(new UriParameterImpl().setName(name).setText(tokenizer.getText()));
        } else {
          throw new UriParserSemanticException("A JSON array or object is not allowed as parameter value.",
              UriParserSemanticException.MessageKeys.COMPLEX_PARAMETER_IN_RESOURCE_PATH, tokenizer.getText());
        }
      } else if (withComplex) {
        final Expression expression = new ExpressionParser(edm, odata).parse(tokenizer, referringType, null);
        parameters.add(new UriParameterImpl().setName(name)
            .setText(expression instanceof Literal ?
                "null".equals(((Literal) expression).getText()) ? null : ((Literal) expression).getText() :
                null)
            .setExpression(expression instanceof Literal ? null : expression));
      } else if (nextPrimitiveValue(tokenizer) == null) {
        throw new UriParserSemanticException("Wrong parameter value.",
            UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE, "");
      } else {
        final String literalValue = tokenizer.getText();
        parameters.add(new UriParameterImpl().setName(name)
            .setText("null".equals(literalValue) ? null : literalValue));
      }
    } while (tokenizer.next(TokenKind.COMMA));
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);
    return parameters;
  }

  protected static List<UriParameter> parseNavigationKeyPredicate(UriTokenizer tokenizer,
      final EdmNavigationProperty navigationProperty) throws UriParserException, UriValidationException {
    if (tokenizer.next(TokenKind.OPEN)) {
      if (navigationProperty.isCollection()) {
        return parseKeyPredicate(tokenizer, navigationProperty.getType(), navigationProperty.getPartner());
      } else {
        throw new UriParserSemanticException("A key is not allowed on non-collection navigation properties.",
            UriParserSemanticException.MessageKeys.KEY_NOT_ALLOWED);
      }
    }
    return null;
  }

  protected static List<UriParameter> parseKeyPredicate(UriTokenizer tokenizer, final EdmEntityType edmEntityType,
      final EdmNavigationProperty partner) throws UriParserException, UriValidationException {
    final List<EdmKeyPropertyRef> keyPropertyRefs = edmEntityType.getKeyPropertyRefs();
    if (tokenizer.next(TokenKind.CLOSE)) {
      throw new UriParserSemanticException(
          "Expected " + keyPropertyRefs.size() + " key predicates but got none.",
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
      keys.addAll(compoundKey(tokenizer, edmEntityType));
    } else if (keyPropertyRefs.size() - referencedNames.size() == 1) {
      for (final EdmKeyPropertyRef candidate : keyPropertyRefs) {
        if (referencedNames.get(candidate.getName()) == null) {
          keys.add(simpleKey(tokenizer, candidate));
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

  private static UriParameter simpleKey(UriTokenizer tokenizer, final EdmKeyPropertyRef edmKeyPropertyRef)
      throws UriParserException, UriValidationException {
    final EdmProperty edmProperty = edmKeyPropertyRef == null ? null : edmKeyPropertyRef.getProperty();
    if (nextPrimitiveTypeValue(tokenizer,
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

  private static List<UriParameter> compoundKey(UriTokenizer tokenizer, final EdmEntityType edmEntityType)
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
      parameters.add(keyValuePair(tokenizer, keyPredicateName, edmEntityType));
      parameterNames.add(keyPredicateName);
      hasComma = tokenizer.next(TokenKind.COMMA);
      if (hasComma) {
        ParserHelper.requireNext(tokenizer, TokenKind.ODataIdentifier);
      }
    } while (hasComma);
    ParserHelper.requireNext(tokenizer, TokenKind.CLOSE);

    return parameters;
  }

  protected static UriParameter keyValuePair(UriTokenizer tokenizer,
      final String keyPredicateName, final EdmEntityType edmEntityType)
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
    if (nextPrimitiveTypeValue(tokenizer, (EdmPrimitiveType) edmProperty.getType(), edmProperty.isNullable())) {
      return createUriParameter(edmProperty, keyPredicateName, tokenizer.getText());
    } else {
      throw new UriParserSemanticException(keyPredicateName + " has not a valid  key value.",
          UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE, keyPredicateName);
    }
  }

  private static UriParameter createUriParameter(final EdmProperty edmProperty, final String parameterName,
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

  private static boolean nextPrimitiveTypeValue(UriTokenizer tokenizer,
      final EdmPrimitiveType primitiveType, final boolean nullable) {
    final EdmPrimitiveType type = primitiveType instanceof EdmTypeDefinition ?
        ((EdmTypeDefinition) primitiveType).getUnderlyingType() :
        primitiveType;
    if (tokenizer.next(TokenKind.ParameterAliasName)) {
      return true;
    } else if (nullable && tokenizer.next(TokenKind.NULL)) {
      return true;

    // Special handling for frequently-used types and types with more than one token kind.
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
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal).equals(type)) {
      // The order is important.
      // A decimal value should not be parsed as integer and let the tokenizer stop at the decimal point.
      return tokenizer.next(TokenKind.DecimalValue)
          || tokenizer.next(TokenKind.IntegerValue);
    } else if (odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double).equals(type)
        || odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Single).equals(type)) {
      // The order is important.
      // A floating-point value should not be parsed as decimal and let the tokenizer stop at 'E'.
      // A decimal value should not be parsed as integer and let the tokenizer stop at the decimal point.
      return tokenizer.next(TokenKind.DoubleValue)
          || tokenizer.next(TokenKind.DecimalValue)
          || tokenizer.next(TokenKind.IntegerValue);
    } else if (type.getKind() == EdmTypeKind.ENUM) {
      return tokenizer.next(TokenKind.EnumValue);
    } else {
      // Check the types that have not been checked already above.
      for (final Entry<TokenKind, EdmPrimitiveTypeKind> entry : tokenToPrimitiveType.entrySet()) {
        final EdmPrimitiveTypeKind kind = entry.getValue();
        if ((kind == EdmPrimitiveTypeKind.Date || kind == EdmPrimitiveTypeKind.DateTimeOffset
            || kind == EdmPrimitiveTypeKind.TimeOfDay || kind == EdmPrimitiveTypeKind.Duration
            || kind == EdmPrimitiveTypeKind.Binary
            || kind.isGeospatial())
            && odata.createPrimitiveTypeInstance(kind).equals(type)) {
          return tokenizer.next(entry.getKey());
        }
      }
      return false;
    }
  }

  protected static List<String> getParameterNames(final List<UriParameter> parameters) {
    List<String> names = new ArrayList<String>();
    for (final UriParameter parameter : parameters) {
      names.add(parameter.getName());
    }
    return names;
  }

  protected static EdmType getTypeInformation(final UriResourcePartTyped resourcePart) {
    EdmType type = null;
    if (resourcePart instanceof UriResourceWithKeysImpl) {
      final UriResourceWithKeysImpl lastPartWithKeys = (UriResourceWithKeysImpl) resourcePart;
      if (lastPartWithKeys.getTypeFilterOnEntry() != null) {
        type = lastPartWithKeys.getTypeFilterOnEntry();
      } else if (lastPartWithKeys.getTypeFilterOnCollection() != null) {
        type = lastPartWithKeys.getTypeFilterOnCollection();
      } else {
        type = lastPartWithKeys.getType();
      }

    } else if (resourcePart instanceof UriResourceTypedImpl) {
      final UriResourceTypedImpl lastPartTyped = (UriResourceTypedImpl) resourcePart;
      type = lastPartTyped.getTypeFilter() == null ?
          lastPartTyped.getType() :
          lastPartTyped.getTypeFilter();
    } else {
      type = resourcePart.getType();
    }

    return type;
  }

  protected static int parseNonNegativeInteger(final String optionName, final String optionValue,
      final boolean zeroAllowed) throws UriParserException {
    int value;
    try {
      value = Integer.parseInt(optionValue);
    } catch (final NumberFormatException e) {
      throw new UriParserSyntaxException("Illegal value of '" + optionName + "' option!", e,
          UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
          optionName, optionValue);
    }
    if (value > 0 || value == 0 && zeroAllowed) {
      return value;
    } else {
      throw new UriParserSyntaxException("Illegal value of '" + optionName + "' option!",
          UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
          optionName, optionValue);
    }
  }
}
