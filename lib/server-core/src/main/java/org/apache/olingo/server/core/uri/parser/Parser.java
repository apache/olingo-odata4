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

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
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
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceStartingTypeFilterImpl;
import org.apache.olingo.server.core.uri.UriResourceTypedImpl;
import org.apache.olingo.server.core.uri.UriResourceWithKeysImpl;
import org.apache.olingo.server.core.uri.antlr.UriLexer;
import org.apache.olingo.server.core.uri.antlr.UriParserParser;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.ExpandItemsEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.FilterExpressionEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.OrderByEOFContext;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.parser.search.SearchParser;
import org.apache.olingo.server.core.uri.queryoption.AliasQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.CountOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.OrderByOptionImpl;
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

  private enum ParserEntryRules {
    ExpandItems, FilterExpression, Orderby
  }

  public Parser(final Edm edm, final OData odata) {
    this.edm = edm;
    this.odata = odata;
  }

  public UriInfo parseUri(final String path, final String query, final String fragment)
      throws UriParserException, UriValidationException {

    UriContext context = new UriContext();
    UriParseTreeVisitor uriParseTreeVisitor = new UriParseTreeVisitor(edm, context);

    final List<String> pathSegmentsDecoded = UriDecoder.splitAndDecodePath(path);
    final int numberOfSegments = pathSegmentsDecoded.size();

    // first, read the decoded path segments
    final String firstSegment = numberOfSegments == 0 ? "" : pathSegmentsDecoded.get(0);

    if (firstSegment.isEmpty()) {
      ensureLastSegment(firstSegment, 0, numberOfSegments);
      context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.service);

    } else if (firstSegment.equals("$batch")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.batch);

    } else if (firstSegment.equals("$metadata")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.metadata);
      context.contextUriInfo.setFragment(fragment);

    } else if (firstSegment.equals("$all")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.all);
      // This loads nearly the whole schema, but sooner or later '$all' needs all entity sets anyway.
      for (final EdmEntitySet entitySet : edm.getEntityContainer().getEntitySets()) {
        context.contextTypes.push(entitySet.getEntityType());
      }
      context.isCollection = true;

    } else if (firstSegment.equals("$entity")) {
      if (numberOfSegments > 1) {
        final String typeCastSegment = pathSegmentsDecoded.get(1);
        ensureLastSegment(typeCastSegment, 2, numberOfSegments);
        context.contextUriInfo = new ResourcePathParser(edm, odata).parseDollarEntityTypeCast(typeCastSegment);
        context.contextTypes.push(context.contextUriInfo.getEntityTypeCast());
      } else {
        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.entityId);
        // The type of the entity is not known until the $id query option has been parsed.
      }
      context.isCollection = false;

    } else if (firstSegment.startsWith("$crossjoin")) {
      ensureLastSegment(firstSegment, 1, numberOfSegments);
      context.contextUriInfo = new ResourcePathParser(edm, odata).parseCrossjoinSegment(firstSegment);
      final EdmEntityContainer container = edm.getEntityContainer();
      for (final String name : context.contextUriInfo.getEntitySetNames()) {
        context.contextTypes.push(container.getEntitySet(name).getEntityType());
      }
      context.isCollection = true;

    } else {
      context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);
      final ResourcePathParser resourcePathParser = new ResourcePathParser(edm, odata);
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
          context.contextUriInfo.addResourcePart(segment);
        }
      }

      if (lastSegment instanceof UriResourcePartTyped) {
        final UriResourcePartTyped typed = (UriResourcePartTyped) lastSegment;
        final EdmType type = getTypeInformation(typed);
        if (type != null) { // could be null for, e.g., actions without return type
          context.contextTypes.push(type);
        }
        context.isCollection = typed.isCollection();
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
          try {
            FilterExpressionEOFContext ctxFilterExpression =
                (FilterExpressionEOFContext) parseRule(optionValue, ParserEntryRules.FilterExpression);
            systemOption = (FilterOptionImpl) uriParseTreeVisitor.visitFilterExpressionEOF(ctxFilterExpression);
          } catch (final ParseCancellationException e) {
            throw e.getCause() instanceof UriParserException ?
                (UriParserException) e.getCause() :
                new UriParserSyntaxException("Syntax error", e, UriParserSyntaxException.MessageKeys.SYNTAX);
          }

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
          try {
            ExpandItemsEOFContext ctxExpandItems =
                (ExpandItemsEOFContext) parseRule(optionValue, ParserEntryRules.ExpandItems);
            systemOption = (ExpandOptionImpl) uriParseTreeVisitor.visitExpandItemsEOF(ctxExpandItems);
          } catch (final ParseCancellationException e) {
            throw e.getCause() instanceof UriParserException ?
                (UriParserException) e.getCause() :
                new UriParserSyntaxException("Syntax error", e, UriParserSyntaxException.MessageKeys.SYNTAX);
          }

        } else if (optionName.equals(SystemQueryOptionKind.ID.toString())) {
          IdOptionImpl idOption = new IdOptionImpl();
          idOption.setText(optionValue);
          idOption.setValue(optionValue);
          systemOption = idOption;

        } else if (optionName.equals(SystemQueryOptionKind.LEVELS.toString())) {
          throw new UriParserSyntaxException("System query option '$levels' is allowed only inside '$expand'!",
              UriParserSyntaxException.MessageKeys.SYSTEM_QUERY_OPTION_LEVELS_NOT_ALLOWED_HERE);

        } else if (optionName.equals(SystemQueryOptionKind.ORDERBY.toString())) {
          try {
            OrderByEOFContext ctxOrderByExpression =
                (OrderByEOFContext) parseRule(optionValue, ParserEntryRules.Orderby);
            systemOption = (OrderByOptionImpl) uriParseTreeVisitor.visitOrderByEOF(ctxOrderByExpression);
          } catch (final ParseCancellationException e) {
            throw e.getCause() instanceof UriParserException ?
                (UriParserException) e.getCause() :
                new UriParserSyntaxException("Syntax error", e, UriParserSyntaxException.MessageKeys.SYNTAX);
          }

        } else if (optionName.equals(SystemQueryOptionKind.SEARCH.toString())) {
          systemOption = new SearchParser().parse(optionValue);

        } else if (optionName.equals(SystemQueryOptionKind.SELECT.toString())) {
          UriTokenizer selectTokenizer = new UriTokenizer(optionValue);
          systemOption = new SelectParser(edm).parse(selectTokenizer,
              context.contextTypes.peek() instanceof EdmStructuredType ?
                  (EdmStructuredType) context.contextTypes.peek() :
                  null,
              context.isCollection);
          if (!selectTokenizer.next(TokenKind.EOF)) {
            throw new UriParserSyntaxException("Illegal value of $select option!",
                UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                optionName, optionValue);
          }

        } else if (optionName.equals(SystemQueryOptionKind.SKIP.toString())) {
          SkipOptionImpl skipOption = new SkipOptionImpl();
          skipOption.setText(optionValue);
          try {
            skipOption.setValue(Integer.parseInt(optionValue));
          } catch (final NumberFormatException e) {
            throw new UriParserSyntaxException("Illegal value of $skip option!", e,
                UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                optionName, optionValue);
          }
          systemOption = skipOption;

        } else if (optionName.equals(SystemQueryOptionKind.SKIPTOKEN.toString())) {
          SkipTokenOptionImpl skipTokenOption = new SkipTokenOptionImpl();
          skipTokenOption.setText(optionValue);
          skipTokenOption.setValue(optionValue);
          systemOption = skipTokenOption;

        } else if (optionName.equals(SystemQueryOptionKind.TOP.toString())) {
          TopOptionImpl topOption = new TopOptionImpl();
          topOption.setText(optionValue);
          try {
            topOption.setValue(Integer.parseInt(optionValue));
          } catch (final NumberFormatException e) {
            throw new UriParserSyntaxException("Illegal value of $top option!", e,
                UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                optionName, optionValue);
          }
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
          context.contextUriInfo.setSystemQueryOption(systemOption);
        } catch (final ODataRuntimeException e) {
          throw new UriParserSyntaxException("Double system query option!", e,
              UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION, optionName);
        }

      } else if (optionName.startsWith(AT)) {
        if (context.contextUriInfo.getAlias(optionName) == null) {
          // TODO: Create a proper alias-value parser that can parse also common expressions.
          Expression expression = null;
          UriTokenizer aliasTokenizer = new UriTokenizer(optionValue);
          if (aliasTokenizer.next(TokenKind.jsonArrayOrObject)) {
            if (!aliasTokenizer.next(TokenKind.EOF)) {
              throw new UriParserSyntaxException("Illegal value for alias '" + optionName + "'.",
                  UriParserSyntaxException.MessageKeys.SYNTAX);
            }
          } else {
            try {
              final FilterExpressionEOFContext filterExpCtx =
                  (FilterExpressionEOFContext) parseRule(optionValue, ParserEntryRules.FilterExpression);
              expression = ((FilterOption) uriParseTreeVisitor.visitFilterExpressionEOF(filterExpCtx))
                  .getExpression();
            } catch (final ParseCancellationException e) {
              throw e.getCause() instanceof UriParserException ?
                  (UriParserException) e.getCause() :
                  new UriParserSyntaxException("Syntax error", e, UriParserSyntaxException.MessageKeys.SYNTAX);
            }
          }
          context.contextUriInfo.addAlias((AliasQueryOption) new AliasQueryOptionImpl()
              .setAliasValue(expression)
              .setName(optionName)
              .setText(NULL.equals(optionValue) ? null : optionValue));
        } else {
          throw new UriParserSyntaxException("Alias already specified! Name: " + optionName,
              UriParserSyntaxException.MessageKeys.DUPLICATED_ALIAS, optionName);
        }

      } else {
        context.contextUriInfo.addCustomQueryOption((CustomQueryOption) option);
      }
    }

    return context.contextUriInfo;
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

  private ParserRuleContext parseRule(final String input, final ParserEntryRules entryPoint)
      throws UriParserSyntaxException {
    UriParserParser parser = null;
    UriLexer lexer = null;
    ParserRuleContext ret = null;

    // Use 2 stage approach to improve performance
    // see https://github.com/antlr/antlr4/issues/192

    // stage = 1
    try {

      // create parser
      lexer = new UriLexer(new ANTLRInputStream(input));
      parser = new UriParserParser(new CommonTokenStream(lexer));

      // Set error strategy
      addStage1ErrorStrategy(parser);

      // Set error collector
      addStage1ErrorListener(parser);

      // user the faster LL parsing
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

      // parse
      switch (entryPoint) {
      case FilterExpression:
        lexer.mode(Lexer.DEFAULT_MODE);
        ret = parser.filterExpressionEOF();
        break;
      case Orderby:
        lexer.mode(Lexer.DEFAULT_MODE);
        ret = parser.orderByEOF();
        break;
      case ExpandItems:
        lexer.mode(Lexer.DEFAULT_MODE);
        ret = parser.expandItemsEOF();
        break;
      default:
        break;

      }

    } catch (ParseCancellationException hardException) {
      // stage = 2
      try {

        // create parser
        lexer = new UriLexer(new ANTLRInputStream(input));
        parser = new UriParserParser(new CommonTokenStream(lexer));

        // Set error strategy
        addStage2ErrorStrategy(parser);

        // Set error collector
        addStage2ErrorListener(parser);

        // Use the slower SLL parsing
        parser.getInterpreter().setPredictionMode(PredictionMode.LL);

        // parse
        switch (entryPoint) {
        case FilterExpression:
          lexer.mode(Lexer.DEFAULT_MODE);
          ret = parser.filterExpressionEOF();
          break;
        case Orderby:
          lexer.mode(Lexer.DEFAULT_MODE);
          ret = parser.orderByEOF();
          break;
        case ExpandItems:
          lexer.mode(Lexer.DEFAULT_MODE);
          ret = parser.expandItemsEOF();
          break;
        default:
          break;
        }

      } catch (final RecognitionException weakException) {
        throw new UriParserSyntaxException("Error in syntax", weakException,
            UriParserSyntaxException.MessageKeys.SYNTAX);

        // exceptionOnStage = 2;
      }
    } catch (final RecognitionException hardException) {
      throw new UriParserSyntaxException("Error in syntax", hardException,
          UriParserSyntaxException.MessageKeys.SYNTAX);
    }

    return ret;
  }

  protected void addStage1ErrorStrategy(final UriParserParser parser) {
    // Throw exception at first syntax error
    parser.setErrorHandler(new BailErrorStrategy());

  }

  protected void addStage2ErrorStrategy(final UriParserParser parser) {
    // Throw exception at first syntax error
    parser.setErrorHandler(new BailErrorStrategy());
  }

  protected void addStage1ErrorListener(final UriParserParser parser) {
    // No error logging to System.out or System.err, only exceptions used (depending on ErrorStrategy)
    parser.removeErrorListeners();
  }

  protected void addStage2ErrorListener(final UriParserParser parser) {
    // No error logging to System.out or System.err, only exceptions used (depending on ErrorStrategy)
    parser.removeErrorListeners();
  }
}
