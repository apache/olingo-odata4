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
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
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
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceStartingTypeFilterImpl;
import org.apache.olingo.server.core.uri.antlr.UriLexer;
import org.apache.olingo.server.core.uri.antlr.UriParserParser;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.ExpandItemsEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.FilterExpressionEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.OrderByEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.SelectEOFContext;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.parser.search.SearchParser;
import org.apache.olingo.server.core.uri.queryoption.AliasQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.CountOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SelectOptionImpl;
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

  int logLevel = 0;
  private final Edm edm;
  private final OData odata;

  private enum ParserEntryRules {
    ExpandItems, FilterExpression, Orderby, Select
  }

  public Parser(final Edm edm, final OData odata) {
    this.edm = edm;
    this.odata = odata;
  }

  public UriInfo parseUri(final String path, final String query, final String fragment)
      throws UriParserException, UriValidationException {

    UriContext context = new UriContext();
    UriParseTreeVisitor uriParseTreeVisitor = new UriParseTreeVisitor(edm, context);

    try {
      final RawUri uri = UriDecoder.decodeUri(path, query, fragment, 0); // -> 0 segments are before the service url

      // first, read the decoded path segments
      final String firstSegment = uri.pathSegmentListDecoded.isEmpty() ? "" : uri.pathSegmentListDecoded.get(0);

      if (firstSegment.isEmpty()) {
        ensureLastSegment(firstSegment, 0, uri.pathSegmentListDecoded.size());
        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.service);

      } else if (firstSegment.equals("$batch")) {
        ensureLastSegment(firstSegment, 1, uri.pathSegmentListDecoded.size());
        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.batch);

      } else if (firstSegment.equals("$metadata")) {
        ensureLastSegment(firstSegment, 1, uri.pathSegmentListDecoded.size());
        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.metadata);
        context.contextUriInfo.setFragment(uri.fragment);

      } else if (firstSegment.equals("$all")) {
        ensureLastSegment(firstSegment, 1, uri.pathSegmentListDecoded.size());
        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.all);

      } else if (firstSegment.equals("$entity")) {
        if (uri.pathSegmentListDecoded.size() > 1) {
          final String typeCastSegment = uri.pathSegmentListDecoded.get(1);
          ensureLastSegment(typeCastSegment, 2, uri.pathSegmentListDecoded.size());
          context.contextUriInfo = new ResourcePathParser(edm, odata).parseDollarEntityTypeCast(typeCastSegment);
          context.contextTypes.push(
              uriParseTreeVisitor.new TypeInformation(context.contextUriInfo.getEntityTypeCast(), false));
        } else {
          context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.entityId);
        }

      } else if (firstSegment.startsWith("$crossjoin")) {
        ensureLastSegment(firstSegment, 1, uri.pathSegmentListDecoded.size());
        context.contextUriInfo = new ResourcePathParser(edm, odata)
            .parseCrossjoinSegment(uri.pathSegmentListDecoded.get(0));
        final EdmEntityContainer container = edm.getEntityContainer();
        for (final String name : context.contextUriInfo.getEntitySetNames()) {
          context.contextTypes.push(
              uriParseTreeVisitor.new TypeInformation(container.getEntitySet(name).getEntityType(), true));
        }

      } else {
        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);
        final ResourcePathParser resourcePathParser = new ResourcePathParser(edm, odata);
        int count = 0;
        UriResource lastSegment = null;
        for (final String pathSegment : uri.pathSegmentListDecoded) {
          count++;
          final UriResource segment = resourcePathParser.parsePathSegment(pathSegment, lastSegment);
          if (segment != null) {
            if (segment instanceof UriResourceCount
                || segment instanceof UriResourceRef
                || segment instanceof UriResourceValue) {
              ensureLastSegment(pathSegment, count, uri.pathSegmentListDecoded.size());
            } else if (segment instanceof UriResourceAction
                || segment instanceof UriResourceFunction
                && !((UriResourceFunction) segment).getFunction().isComposable()) {
              if (count < uri.pathSegmentListDecoded.size()) {
                throw new UriValidationException(
                    "The segment of an action or of a non-composable function must be the last resource-path segment.",
                    UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH,
                    uri.pathSegmentListDecoded.get(count));
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
          UriResourcePartTyped typed = (UriResourcePartTyped) lastSegment;

          UriParseTreeVisitor.TypeInformation myType = uriParseTreeVisitor.getTypeInformation(typed);
          UriParseTreeVisitor.TypeInformation typeInfo =
              uriParseTreeVisitor.new TypeInformation(myType.type, typed.isCollection());
          context.contextTypes.push(typeInfo);
        }
      }

      // second, read the system query options and the custom query options
      for (final RawUri.QueryOption option : uri.queryOptionListDecoded) {
        if (option.name.startsWith("$")) {
          SystemQueryOption systemOption = null;
          if (option.name.equals(SystemQueryOptionKind.FILTER.toString())) {
            FilterExpressionEOFContext ctxFilterExpression =
                (FilterExpressionEOFContext) parseRule(option.value, ParserEntryRules.FilterExpression);
            systemOption = (FilterOptionImpl) uriParseTreeVisitor.visitFilterExpressionEOF(ctxFilterExpression);

          } else if (option.name.equals(SystemQueryOptionKind.FORMAT.toString())) {
            FormatOptionImpl formatOption = new FormatOptionImpl();
            formatOption.setName(option.name);
            formatOption.setText(option.value);
            if (option.value.equalsIgnoreCase(JSON)
                || option.value.equalsIgnoreCase(XML)
                || option.value.equalsIgnoreCase(ATOM)
                || isFormatSyntaxValid(option.value)) {
              formatOption.setFormat(option.value);
            } else {
              throw new UriParserSyntaxException("Illegal value of $format option!",
                  UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT, option.value);
            }
            systemOption = formatOption;

          } else if (option.name.equals(SystemQueryOptionKind.EXPAND.toString())) {
            ExpandItemsEOFContext ctxExpandItems =
                (ExpandItemsEOFContext) parseRule(option.value, ParserEntryRules.ExpandItems);
            systemOption = (ExpandOptionImpl) uriParseTreeVisitor.visitExpandItemsEOF(ctxExpandItems);

          } else if (option.name.equals(SystemQueryOptionKind.ID.toString())) {
            IdOptionImpl idOption = new IdOptionImpl();
            idOption.setName(option.name);
            idOption.setText(option.value);
            idOption.setValue(option.value);
            systemOption = idOption;

          } else if (option.name.equals(SystemQueryOptionKind.LEVELS.toString())) {
            throw new UriParserSyntaxException("System query option '$levels' is allowed only inside '$expand'!",
                UriParserSyntaxException.MessageKeys.SYSTEM_QUERY_OPTION_LEVELS_NOT_ALLOWED_HERE);

          } else if (option.name.equals(SystemQueryOptionKind.ORDERBY.toString())) {
            OrderByEOFContext ctxOrderByExpression =
                (OrderByEOFContext) parseRule(option.value, ParserEntryRules.Orderby);
            systemOption = (OrderByOptionImpl) uriParseTreeVisitor.visitOrderByEOF(ctxOrderByExpression);

          } else if (option.name.equals(SystemQueryOptionKind.SEARCH.toString())) {
            systemOption = new SearchParser().parse(option.value);

          } else if (option.name.equals(SystemQueryOptionKind.SELECT.toString())) {
            SelectEOFContext ctxSelectEOF =
                (SelectEOFContext) parseRule(option.value, ParserEntryRules.Select);
            systemOption = (SelectOptionImpl) uriParseTreeVisitor.visitSelectEOF(ctxSelectEOF);

          } else if (option.name.equals(SystemQueryOptionKind.SKIP.toString())) {
            SkipOptionImpl skipOption = new SkipOptionImpl();
            skipOption.setName(option.name);
            skipOption.setText(option.value);
            try {
              skipOption.setValue(Integer.parseInt(option.value));
            } catch (final NumberFormatException e) {
              throw new UriParserSyntaxException("Illegal value of $skip option!", e,
                  UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                  option.name, option.value);
            }
            systemOption = skipOption;

          } else if (option.name.equals(SystemQueryOptionKind.SKIPTOKEN.toString())) {
            SkipTokenOptionImpl skipTokenOption = new SkipTokenOptionImpl();
            skipTokenOption.setName(option.name);
            skipTokenOption.setText(option.value);
            skipTokenOption.setValue(option.value);
            systemOption = skipTokenOption;

          } else if (option.name.equals(SystemQueryOptionKind.TOP.toString())) {
            TopOptionImpl topOption = new TopOptionImpl();
            topOption.setName(option.name);
            topOption.setText(option.value);
            try {
              topOption.setValue(Integer.parseInt(option.value));
            } catch (final NumberFormatException e) {
              throw new UriParserSyntaxException("Illegal value of $top option!", e,
                  UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                  option.name, option.value);
            }
            systemOption = topOption;

          } else if (option.name.equals(SystemQueryOptionKind.COUNT.toString())) {
            CountOptionImpl inlineCountOption = new CountOptionImpl();
            inlineCountOption.setName(option.name);
            inlineCountOption.setText(option.value);
            if (option.value.equals("true") || option.value.equals("false")) {
              inlineCountOption.setValue(Boolean.parseBoolean(option.value));
            } else {
              throw new UriParserSyntaxException("Illegal value of $count option!",
                  UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
                  option.name, option.value);
            }
            systemOption = inlineCountOption;

          } else {
            throw new UriParserSyntaxException("Unknown system query option!",
                UriParserSyntaxException.MessageKeys.UNKNOWN_SYSTEM_QUERY_OPTION, option.name);
          }
          try {
            context.contextUriInfo.setSystemQueryOption(systemOption);
          } catch (final ODataRuntimeException e) {
            throw new UriParserSyntaxException("Double system query option!", e,
                UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION, option.name);
          }

        } else if (option.name.startsWith(AT)) {
          if (context.contextUriInfo.getAlias(option.name) == null) {
            // TODO: Create a proper alias-value parser that can parse also common expressions.
            Expression expression = null;
            if (!option.value.isEmpty() && (option.value.charAt(0) == '[' || option.value.charAt(0) == '{')) {
              UriTokenizer tokenizer = new UriTokenizer(option.value);
              if (!(tokenizer.next(TokenKind.jsonArrayOrObject) && tokenizer.next(TokenKind.EOF))) {
                throw new UriParserSyntaxException("Illegal value for alias '" + option.name + "'.",
                    UriParserSyntaxException.MessageKeys.SYNTAX);
              }
            } else {
              final FilterExpressionEOFContext filterExpCtx =
                  (FilterExpressionEOFContext) parseRule(option.value, ParserEntryRules.FilterExpression);
              expression = ((FilterOption) uriParseTreeVisitor.visitFilterExpressionEOF(filterExpCtx))
                  .getExpression();
            }
            context.contextUriInfo.addAlias((AliasQueryOption) new AliasQueryOptionImpl()
                .setAliasValue(expression)
                .setName(option.name)
                .setText(NULL.equals(option.value) ? null : option.value));
          } else {
            throw new UriParserSyntaxException("Alias already specified! Name: " + option.name,
                UriParserSyntaxException.MessageKeys.DUPLICATED_ALIAS, option.name);
          }

        } else {
          context.contextUriInfo.addCustomQueryOption((CustomQueryOption)
              new CustomQueryOptionImpl()
                  .setName(option.name)
                  .setText(option.value));
        }
      }

      return context.contextUriInfo;
    } catch (ParseCancellationException e) {
      throw e.getCause() instanceof UriParserException ?
          (UriParserException) e.getCause() :
          new UriParserSyntaxException("Syntax error", e, UriParserSyntaxException.MessageKeys.SYNTAX);
    }
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
      if (logLevel > 0) {
        //TODO: Discuss if we should keep this code
        lexer = new UriLexer(new ANTLRInputStream(input));
        showTokens(input, lexer.getAllTokens());
      }

      lexer = new UriLexer(new ANTLRInputStream(input));
      parser = new UriParserParser(new CommonTokenStream(lexer));

      // Set error strategy
      addStage1ErrorStategy(parser);

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
      case Select:
        ret = parser.selectEOF();
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
        addStage2ErrorStategy(parser);

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
        case Select:
          ret = parser.selectEOF();
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

  protected void addStage1ErrorStategy(final UriParserParser parser) {
    // Throw exception at first syntax error
    parser.setErrorHandler(new BailErrorStrategy());

  }

  protected void addStage2ErrorStategy(final UriParserParser parser) {
    // Throw exception at first syntax error
    parser.setErrorHandler(new BailErrorStrategy());
  }

  protected void addStage1ErrorListener(final UriParserParser parser) {
    // No error logging to System.out or System.err, only exceptions used (depending on ErrorStrategy)
    parser.removeErrorListeners();
    parser.addErrorListener(new CheckFullContextListener());

  }

  protected void addStage2ErrorListener(final UriParserParser parser) {
    // No error logging to System.out or System.err, only exceptions used (depending on ErrorStrategy)
    parser.removeErrorListeners();
  }

  public void showTokens(final String input, final List<? extends Token> list) {
    boolean first = true;
    System.out.println("input: " + input);
    String nL = "\n";
    StringBuilder out = new StringBuilder("[").append(nL);
    for (Token token : list) {
      if (!first) {
        out.append(",");
        first = false;
      }
      int index = token.getType();
      out.append("\"").append(token.getText()).append("\"").append("     ");
      if (index != -1) {
        out.append(UriLexer.VOCABULARY.getDisplayName(index));
      } else {
        out.append(index);
      }
      out.append(nL);
    }
    out.append(']');
    System.out.println("tokens: " + out.toString());
  }

}
