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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceCount;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.UriResourceRef;
import org.apache.olingo.server.api.uri.UriResourceValue;
import org.apache.olingo.server.api.uri.queryoption.AliasQueryOption;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceStartingTypeFilterImpl;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.parser.search.SearchParser;
import org.apache.olingo.server.core.uri.queryoption.AliasQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.CountOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SkipOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SkipTokenOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.TopOptionImpl;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class Parser {
  private static final String ATOM = "atom";
  private static final String JSON = "json";
  private static final String XML = "xml";
  private static final String AT = "@";
  private static final String NULL = "null";

  private final Edm edm;
  private final OData odata;

  public Parser(final Edm edm, final OData odata) {
    this.edm = edm;
    this.odata = odata;
  }

  public UriInfo parseUri(final String path, final String query, final String fragment)
      throws UriParserException, UriValidationException {

    UriInfoImpl contextUriInfo = new UriInfoImpl();
    Deque<EdmType> contextTypes = new ArrayDeque<EdmType>();
    boolean contextIsCollection = false;

    final List<String> pathSegmentsDecoded = UriDecoder.splitAndDecodePath(path);
    final int numberOfSegments = pathSegmentsDecoded.size();

    // first, read the decoded path segments
    final String firstSegment = numberOfSegments == 0 ? "" : pathSegmentsDecoded.get(0);

    if (firstSegment.isEmpty()) {
      ensureLastSegment(firstSegment, 0, numberOfSegments);
      contextUriInfo.setKind(UriInfoKind.service);

    } else if (firstSegment.equals("$batch")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      contextUriInfo.setKind(UriInfoKind.batch);

    } else if (firstSegment.equals("$metadata")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      contextUriInfo.setKind(UriInfoKind.metadata);
      contextUriInfo.setFragment(fragment);

    } else if (firstSegment.equals("$all")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      contextUriInfo.setKind(UriInfoKind.all);
      // This loads nearly the whole schema, but sooner or later '$all' needs all entity sets anyway.
      for (final EdmEntitySet entitySet : edm.getEntityContainer().getEntitySets()) {
        contextTypes.push(entitySet.getEntityType());
      }
      contextIsCollection = true;

    } else if (firstSegment.equals("$entity")) {
      if (numberOfSegments > 1) {
        final String typeCastSegment = pathSegmentsDecoded.get(1);
        ensureLastSegment(typeCastSegment, 2, numberOfSegments);
        contextUriInfo = new ResourcePathParser(edm).parseDollarEntityTypeCast(typeCastSegment);
        contextTypes.push(contextUriInfo.getEntityTypeCast());
      } else {
        contextUriInfo.setKind(UriInfoKind.entityId);
        // The type of the entity is not known until the $id query option has been parsed.
        // TODO: Set the type (needed for the evaluation of system query options).
      }
      contextIsCollection = false;

    } else if (firstSegment.startsWith("$crossjoin")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      contextUriInfo = new ResourcePathParser(edm).parseCrossjoinSegment(firstSegment);
      contextIsCollection = true;

    } else {
      contextUriInfo.setKind(UriInfoKind.resource);
      final ResourcePathParser resourcePathParser = new ResourcePathParser(edm);
      int count = 0;
      UriResource lastSegment = null;
      for (final String pathSegment : pathSegmentsDecoded) {
        count++;
        final UriResource segment = resourcePathParser.parsePathSegment(pathSegment, lastSegment);
        if (segment != null) {
          if (segment instanceof UriResourceCount
              || segment instanceof UriResourceRef
              || segment instanceof UriResourceValue) {
            ensureLastSegment(pathSegment, count, numberOfSegments);
          } else if (segment instanceof UriResourceAction
              || segment instanceof UriResourceFunction
              && !((UriResourceFunction) segment).getFunction().isComposable()) {
            if (count < numberOfSegments) {
              throw new UriValidationException(
                  "The segment of an action or of a non-composable function must be the last resource-path segment.",
                  UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH,
                  pathSegmentsDecoded.get(count));
            }
            lastSegment = segment;
          } else if (segment instanceof UriResourceStartingTypeFilterImpl) {
            throw new UriParserSemanticException("First resource-path segment must not be namespace-qualified.",
                UriParserSemanticException.MessageKeys.NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT);
          } else {
            lastSegment = segment;
          }
          contextUriInfo.addResourcePart(segment);
        }
      }

      if (lastSegment instanceof UriResourcePartTyped) {
        final UriResourcePartTyped typed = (UriResourcePartTyped) lastSegment;
        final EdmType type = ParserHelper.getTypeInformation(typed);
        if (type != null) { // could be null for, e.g., actions without return type
          contextTypes.push(type);
        }
        contextIsCollection = typed.isCollection();
      }
    }

    // second, read the system query options and the custom query options
    final List<QueryOption> options = UriDecoder.splitAndDecodeOptions(query);
    for (final QueryOption option : options) {
      final String optionName = option.getName();
      final String optionValue = option.getText();
      if (optionName.startsWith("$")) {
        SystemQueryOption systemOption = null;
        if (optionName.equals(SystemQueryOptionKind.FILTER.toString())) {
          UriTokenizer filterTokenizer = new UriTokenizer(optionValue);
          // The referring type could be a primitive type or a structured type.
          systemOption = new FilterParser(edm, odata).parse(filterTokenizer,
              contextTypes.peek(),
              contextUriInfo.getEntitySetNames());
          checkOptionEOF(filterTokenizer, optionName, optionValue);

        } else if (optionName.equals(SystemQueryOptionKind.FORMAT.toString())) {
          FormatOptionImpl formatOption = new FormatOptionImpl();
          formatOption.setText(optionValue);
          if (optionValue.equalsIgnoreCase(JSON)
              || optionValue.equalsIgnoreCase(XML)
              || optionValue.equalsIgnoreCase(ATOM)
              || isFormatSyntaxValid(optionValue)) {
            formatOption.setFormat(optionValue);
          } else {
            throw new UriParserSyntaxException("Illegal value of $format option!",
                UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT, optionValue);
          }
          systemOption = formatOption;

        } else if (optionName.equals(SystemQueryOptionKind.EXPAND.toString())) {
          if (contextTypes.peek() instanceof EdmStructuredType
              || !contextUriInfo.getEntitySetNames().isEmpty()
              || contextUriInfo.getKind() == UriInfoKind.entityId) { // TODO: Remove once the type has been set above.
            UriTokenizer expandTokenizer = new UriTokenizer(optionValue);
            systemOption = new ExpandParser(edm, odata).parse(expandTokenizer,
                contextTypes.peek() instanceof EdmStructuredType ? (EdmStructuredType) contextTypes.peek() : null);
            checkOptionEOF(expandTokenizer, optionName, optionValue);
          } else {
            throw new UriValidationException("Expand is only allowed on structured types!",
                UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED, optionName);
          }

        } else if (optionName.equals(SystemQueryOptionKind.ID.toString())) {
          IdOptionImpl idOption = new IdOptionImpl();
          idOption.setText(optionValue);
          if (optionValue == null || optionValue.isEmpty()) {
            throw new UriParserSyntaxException("Illegal value of $id option!",
                UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                optionName, optionValue);
          }
          idOption.setValue(optionValue);
          systemOption = idOption;

        } else if (optionName.equals(SystemQueryOptionKind.LEVELS.toString())) {
          throw new UriParserSyntaxException("System query option '$levels' is allowed only inside '$expand'!",
              UriParserSyntaxException.MessageKeys.SYSTEM_QUERY_OPTION_LEVELS_NOT_ALLOWED_HERE);

        } else if (optionName.equals(SystemQueryOptionKind.ORDERBY.toString())) {
          UriTokenizer orderByTokenizer = new UriTokenizer(optionValue);
          systemOption = new OrderByParser(edm, odata).parse(orderByTokenizer,
              contextTypes.peek() instanceof EdmStructuredType ? (EdmStructuredType) contextTypes.peek() : null,
              contextUriInfo.getEntitySetNames());
          checkOptionEOF(orderByTokenizer, optionName, optionValue);

        } else if (optionName.equals(SystemQueryOptionKind.SEARCH.toString())) {
          systemOption = new SearchParser().parse(optionValue);

        } else if (optionName.equals(SystemQueryOptionKind.SELECT.toString())) {
          UriTokenizer selectTokenizer = new UriTokenizer(optionValue);
          systemOption = new SelectParser(edm).parse(selectTokenizer,
              contextTypes.peek() instanceof EdmStructuredType ? (EdmStructuredType) contextTypes.peek() : null,
              contextIsCollection);
          checkOptionEOF(selectTokenizer, optionName, optionValue);

        } else if (optionName.equals(SystemQueryOptionKind.SKIP.toString())) {
          SkipOptionImpl skipOption = new SkipOptionImpl();
          skipOption.setText(optionValue);
          skipOption.setValue(ParserHelper.parseNonNegativeInteger(optionName, optionValue, true));
          systemOption = skipOption;

        } else if (optionName.equals(SystemQueryOptionKind.SKIPTOKEN.toString())) {
          SkipTokenOptionImpl skipTokenOption = new SkipTokenOptionImpl();
          skipTokenOption.setText(optionValue);
          if (optionValue == null || optionValue.isEmpty()) {
            throw new UriParserSyntaxException("Illegal value of $skiptoken option!",
                UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                optionName, optionValue);
          }
          skipTokenOption.setValue(optionValue);
          systemOption = skipTokenOption;

        } else if (optionName.equals(SystemQueryOptionKind.TOP.toString())) {
          TopOptionImpl topOption = new TopOptionImpl();
          topOption.setText(optionValue);
          topOption.setValue(ParserHelper.parseNonNegativeInteger(optionName, optionValue, true));
          systemOption = topOption;

        } else if (optionName.equals(SystemQueryOptionKind.COUNT.toString())) {
          CountOptionImpl inlineCountOption = new CountOptionImpl();
          inlineCountOption.setText(optionValue);
          if (optionValue.equals("true") || optionValue.equals("false")) {
            inlineCountOption.setValue(Boolean.parseBoolean(optionValue));
          } else {
            throw new UriParserSyntaxException("Illegal value of $count option!",
                UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                optionName, optionValue);
          }
          systemOption = inlineCountOption;

        } else {
          throw new UriParserSyntaxException("Unknown system query option!",
              UriParserSyntaxException.MessageKeys.UNKNOWN_SYSTEM_QUERY_OPTION, optionName);
        }
        try {
          contextUriInfo.setSystemQueryOption(systemOption);
        } catch (final ODataRuntimeException e) {
          throw new UriParserSyntaxException("Double system query option!", e,
              UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION, optionName);
        }

      } else if (optionName.startsWith(AT)) {
        if (contextUriInfo.getAlias(optionName) == null) {
          // TODO: Create a proper alias-value parser that can parse also common expressions.
          Expression expression = null;
          UriTokenizer aliasTokenizer = new UriTokenizer(optionValue);
          if (aliasTokenizer.next(TokenKind.jsonArrayOrObject)) {
            if (!aliasTokenizer.next(TokenKind.EOF)) {
              throw new UriParserSyntaxException("Illegal value for alias '" + optionName + "'.",
                  UriParserSyntaxException.MessageKeys.SYNTAX);
            }
          } else {
            UriTokenizer aliasValueTokenizer = new UriTokenizer(optionValue);
            expression = new ExpressionParser(edm, odata).parse(aliasValueTokenizer, null,
                contextUriInfo.getEntitySetNames());
            if (!aliasValueTokenizer.next(TokenKind.EOF)) {
              throw new UriParserSyntaxException("Illegal value for alias '" + optionName + "'.",
                  UriParserSyntaxException.MessageKeys.SYNTAX);
            }
          }
          contextUriInfo.addAlias((AliasQueryOption) new AliasQueryOptionImpl()
              .setAliasValue(expression)
              .setName(optionName)
              .setText(NULL.equals(optionValue) ? null : optionValue));
        } else {
          throw new UriParserSyntaxException("Alias already specified! Name: " + optionName,
              UriParserSyntaxException.MessageKeys.DUPLICATED_ALIAS, optionName);
        }

      } else {
        contextUriInfo.addCustomQueryOption((CustomQueryOption) option);
      }
    }

    return contextUriInfo;
  }

  private void ensureLastSegment(final String segment, final int pos, final int size)
      throws UriParserSyntaxException {
    if (pos < size) {
      throw new UriParserSyntaxException(segment + " must be the last segment.",
          UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT, segment);
    }
  }

  private boolean isFormatSyntaxValid(final String value) {
    final int index = value.indexOf('/');
    return index > 0 && index < value.length() - 1 && index == value.lastIndexOf('/');
  }

  private void checkOptionEOF(UriTokenizer tokenizer, final String optionName, final String optionValue)
      throws UriParserException {
    if (!tokenizer.next(TokenKind.EOF)) {
      throw new UriParserSyntaxException("Illegal value of '" + optionName + "' option!",
          UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
          optionName, optionValue);
    }
  }
}
