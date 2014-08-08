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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.antlr.UriLexer;
import org.apache.olingo.server.core.uri.antlr.UriParserParser;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.AllEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.BatchEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.CrossjoinEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.EntityEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.ExpandItemsEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.FilterExpressionEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.MetadataEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.OrderByEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.PathSegmentEOFContext;
import org.apache.olingo.server.core.uri.antlr.UriParserParser.SelectEOFContext;
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

import java.util.ArrayList;
import java.util.List;

public class Parser {
  int logLevel = 0;

  private enum ParserEntryRules {
    All, Batch, CrossJoin, Entity, ExpandItems, FilterExpression, Metadata, PathSegment, Orderby, Select
  };

  public Parser setLogLevel(final int logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  public UriInfo parseUri(final String input, final Edm edm) throws UriParserException {

    boolean readQueryParameter = false;
    boolean readFragment = false;

    UriContext context = new UriContext();
    UriParseTreeVisitor uriParseTreeVisitor = new UriParseTreeVisitor(edm, context);

    try {
      RawUri uri = UriDecoder.decodeUri(input, 0); // -> 0 segments are before the service url

      // first, read the decoded path segments
      String firstSegment = "";
      if (uri.pathSegmentListDecoded.size() > 0) {
        firstSegment = uri.pathSegmentListDecoded.get(0);
      }

      if (firstSegment.length() == 0) {
        readQueryParameter = true;
        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.service);
      } else if (firstSegment.startsWith("$batch")) {
        BatchEOFContext ctxBatchEOF =
            (BatchEOFContext) parseRule(uri.pathSegmentListDecoded.get(0), ParserEntryRules.Batch);

        uriParseTreeVisitor.visitBatchEOF(ctxBatchEOF);
        readQueryParameter = true;
      } else if (firstSegment.startsWith("$metadata")) {
        MetadataEOFContext ctxMetadataEOF =
            (MetadataEOFContext) parseRule(uri.pathSegmentListDecoded.get(0), ParserEntryRules.Metadata);

        uriParseTreeVisitor.visitMetadataEOF(ctxMetadataEOF);
        readQueryParameter = true;
        readFragment = true;
      } else if (firstSegment.startsWith("$entity")) {

        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.entityId);
        if (uri.pathSegmentListDecoded.size() > 1) {
          EntityEOFContext ctxEntityEOF =
              (EntityEOFContext) parseRule(uri.pathSegmentListDecoded.get(1), ParserEntryRules.Entity);
          uriParseTreeVisitor.visitEntityEOF(ctxEntityEOF);

        }
        readQueryParameter = true;

      } else if (firstSegment.startsWith("$all")) {
        AllEOFContext ctxResourcePathEOF =
            (AllEOFContext) parseRule(uri.pathSegmentListDecoded.get(0), ParserEntryRules.All);

        uriParseTreeVisitor.visitAllEOF(ctxResourcePathEOF);
        readQueryParameter = true;
      } else if (firstSegment.startsWith("$crossjoin")) {
        CrossjoinEOFContext ctxResourcePathEOF =
            (CrossjoinEOFContext) parseRule(uri.pathSegmentListDecoded.get(0), ParserEntryRules.CrossJoin);

        uriParseTreeVisitor.visitCrossjoinEOF(ctxResourcePathEOF);
        readQueryParameter = true;
      } else {
        List<PathSegmentEOFContext> ctxPathSegments = new ArrayList<PathSegmentEOFContext>();
        for (String pathSegment : uri.pathSegmentListDecoded) {
          PathSegmentEOFContext ctxPathSegment =
              (PathSegmentEOFContext) parseRule(pathSegment, ParserEntryRules.PathSegment);
          ctxPathSegments.add(ctxPathSegment);
        }

        context.contextUriInfo = new UriInfoImpl().setKind(UriInfoKind.resource);

        for (PathSegmentEOFContext ctxPathSegment : ctxPathSegments) {
          // add checks for batcvh entity metadata, all crossjsoin
          uriParseTreeVisitor.visitPathSegmentEOF(ctxPathSegment);
        }

        UriResource lastSegment = context.contextUriInfo.getLastResourcePart();
        if (lastSegment instanceof UriResourcePartTyped) {
          UriResourcePartTyped typed = (UriResourcePartTyped) lastSegment;

          UriParseTreeVisitor.TypeInformation myType = uriParseTreeVisitor.getTypeInformation(typed);
          UriParseTreeVisitor.TypeInformation typeInfo =
              uriParseTreeVisitor.new TypeInformation(myType.type, typed.isCollection());
          context.contextTypes.push(typeInfo);

        }

        readQueryParameter = true;

      }

      if (readQueryParameter) {
        // second, read the simple systemQueryOptions and the Custom QueryOptions
        for (RawUri.QueryOption option : uri.queryOptionListDecoded) {
          if (!option.name.startsWith("$")) {
            CustomQueryOptionImpl customOption = new CustomQueryOptionImpl();
            customOption.setName(option.name);
            customOption.setText(option.value);
            context.contextUriInfo.addCustomQueryOption(customOption);
          } else if (option.name.equals(SystemQueryOptionKind.FILTER.toString())) {
            FilterExpressionEOFContext ctxFilterExpression =
                (FilterExpressionEOFContext) parseRule(option.value, ParserEntryRules.FilterExpression);

            FilterOptionImpl filterOption =
                (FilterOptionImpl) uriParseTreeVisitor.visitFilterExpressionEOF(ctxFilterExpression);

            context.contextUriInfo.setSystemQueryOption(filterOption);

          } else if (option.name.equals(SystemQueryOptionKind.FORMAT.toString())) {
            FormatOptionImpl formatOption = new FormatOptionImpl();
            formatOption.setName(option.name);
            formatOption.setText(option.value);
            if (option.value.equalsIgnoreCase(ODataFormat.JSON.name())
                || option.value.equalsIgnoreCase(ODataFormat.XML.name())
                || option.value.equalsIgnoreCase(ODataFormat.ATOM.name())
                || isFormatSyntaxValid(option)) {
              formatOption.setFormat(option.value);
            } else {
              throw new UriParserSemanticException("Illegal value of $format option!",
                  UriParserSemanticException.MessageKeys.TEST);
            }
            context.contextUriInfo.setSystemQueryOption(formatOption);

          } else if (option.name.equals(SystemQueryOptionKind.EXPAND.toString())) {
            ExpandItemsEOFContext ctxExpandItems =
                (ExpandItemsEOFContext) parseRule(option.value, ParserEntryRules.ExpandItems);

            ExpandOptionImpl expandOption =
                (ExpandOptionImpl) uriParseTreeVisitor.visitExpandItemsEOF(ctxExpandItems);

            context.contextUriInfo.setSystemQueryOption(expandOption);

          } else if (option.name.equals(SystemQueryOptionKind.ID.toString())) {
            IdOptionImpl idOption = new IdOptionImpl();
            idOption.setName(option.name);
            idOption.setText(option.value);
            idOption.setValue(option.value);
            context.contextUriInfo.setSystemQueryOption(idOption);
          } else if (option.name.equals(SystemQueryOptionKind.LEVELS.toString())) {
            throw new UriParserSyntaxException("System query option '$levels' is allowed only inside '$expand'!",
                UriParserSyntaxException.MessageKeys.TEST);
          } else if (option.name.equals(SystemQueryOptionKind.ORDERBY.toString())) {
            OrderByEOFContext ctxFilterExpression =
                (OrderByEOFContext) parseRule(option.value, ParserEntryRules.Orderby);

            OrderByOptionImpl orderByOption =
                (OrderByOptionImpl) uriParseTreeVisitor.visitOrderByEOF(ctxFilterExpression);

            context.contextUriInfo.setSystemQueryOption(orderByOption);
          } else if (option.name.equals(SystemQueryOptionKind.SEARCH.toString())) {
            throw new RuntimeException("System query option '$search' not implemented!");
          } else if (option.name.equals(SystemQueryOptionKind.SELECT.toString())) {
            SelectEOFContext ctxSelectEOF =
                (SelectEOFContext) parseRule(option.value, ParserEntryRules.Select);

            SelectOptionImpl selectOption =
                (SelectOptionImpl) uriParseTreeVisitor.visitSelectEOF(ctxSelectEOF);

            context.contextUriInfo.setSystemQueryOption(selectOption);
          } else if (option.name.equals(SystemQueryOptionKind.SKIP.toString())) {
            SkipOptionImpl skipOption = new SkipOptionImpl();
            skipOption.setName(option.name);
            skipOption.setText(option.value);
            try {
              skipOption.setValue(Integer.parseInt(option.value));
            } catch (final NumberFormatException e) {
              throw new UriParserSemanticException("Illegal value of $skip option!", e,
                  UriParserSemanticException.MessageKeys.TEST);
            }
            context.contextUriInfo.setSystemQueryOption(skipOption);
          } else if (option.name.equals(SystemQueryOptionKind.SKIPTOKEN.toString())) {
            SkipTokenOptionImpl skipTokenOption = new SkipTokenOptionImpl();
            skipTokenOption.setName(option.name);
            skipTokenOption.setText(option.value);
            skipTokenOption.setValue(option.value);
            context.contextUriInfo.setSystemQueryOption(skipTokenOption);
          } else if (option.name.equals(SystemQueryOptionKind.TOP.toString())) {
            TopOptionImpl topOption = new TopOptionImpl();
            topOption.setName(option.name);
            topOption.setText(option.value);
            try {
              topOption.setValue(Integer.parseInt(option.value));
            } catch (final NumberFormatException e) {
              throw new UriParserSemanticException("Illegal value of $top option!", e,
                  UriParserSemanticException.MessageKeys.TEST);
            }
            context.contextUriInfo.setSystemQueryOption(topOption);
          } else if (option.name.equals(SystemQueryOptionKind.COUNT.toString())) {
            CountOptionImpl inlineCountOption = new CountOptionImpl();
            inlineCountOption.setName(option.name);
            inlineCountOption.setText(option.value);
            if (option.value.equals("true") || option.value.equals("false")) {
              inlineCountOption.setValue(Boolean.parseBoolean(option.value));
            } else {
              throw new UriParserSemanticException("Illegal value of $count option!",
                  UriParserSemanticException.MessageKeys.TEST);
            }
            context.contextUriInfo.setSystemQueryOption(inlineCountOption);
          } else {
            throw new UriParserSyntaxException("Unknown system query option!",
                UriParserSyntaxException.MessageKeys.TEST);
          }
        }
      }

      if (readFragment) {
        context.contextUriInfo.setFragment(uri.fragment);
      }

      return context.contextUriInfo;
    } catch (ParseCancellationException e) {
      Throwable cause = e.getCause();
      if (cause instanceof UriParserException) {
        throw (UriParserException) cause;
      }
    }
    return null;
  }

  private boolean isFormatSyntaxValid(RawUri.QueryOption option) {
    final int index = option.value.indexOf('/');
    return index > 0 && index < option.value.length() - 1 && index == option.value.lastIndexOf('/');
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
      case All:
        ret = parser.allEOF();
        break;
      case Batch:
        ret = parser.batchEOF();
        break;
      case CrossJoin:
        ret = parser.crossjoinEOF();
        break;
      case Metadata:
        ret = parser.metadataEOF();
        break;
      case PathSegment:
        ret = parser.pathSegmentEOF();
        break;
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
      case Entity:
        ret = parser.entityEOF();
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
        case All:
          ret = parser.allEOF();
          break;
        case Batch:
          ret = parser.batchEOF();
          break;
        case CrossJoin:
          ret = parser.crossjoinEOF();
          break;
        case Metadata:
          ret = parser.metadataEOF();
          break;
        case PathSegment:
          ret = parser.pathSegmentEOF();
          break;
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
        case Entity:
          ret = parser.entityEOF();
          break;
        case Select:
          ret = parser.selectEOF();
          break;
        default:
          break;
        }

      } catch (Exception weakException) {
        throw new UriParserSyntaxException("Error in syntax", weakException, UriParserSyntaxException.MessageKeys.TEST);

        // exceptionOnStage = 2;
      }
    } catch (Exception hardException) {
      throw new UriParserSyntaxException("Error in syntax", hardException, UriParserSyntaxException.MessageKeys.TEST);
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
    String out = "[" + nL;
    for (Token token : list) {
      if (!first) {
        out += ",";
        first = false;
      }
      int index = token.getType();
      if (index != -1) {
        out += "\"" + token.getText() + "\"" + "     " + UriLexer.tokenNames[index] + nL;
      } else {
        out += "\"" + token.getText() + "\"" + "     " + index + nL;
      }
    }
    out += ']';
    System.out.println("tokens: " + out);
    return;
  }

}
