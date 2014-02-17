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
package org.apache.olingo.odata4.producer.core.uri;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfo;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriEOFContext;

public class Parser {
  public UriInfo parseUri(final String input, final UriParseTreeVisitor uriParseTreeVisitor)
      throws UriParserException {

    try {
      OdataRelativeUriEOFContext parseTree = parseInput(input, true);

      // reset visitor
      uriParseTreeVisitor.init();
      parseTree.accept(uriParseTreeVisitor);
      UriInfoImpl uriInput = uriParseTreeVisitor.getUriInfo();

      return uriInput;

    } catch (ParseCancellationException e) {
      Throwable cause = e.getCause();
      if (cause instanceof UriParserException) {
        throw (UriParserException) cause;
      }
    }
    throw new UriParserSyntaxException("unknown syntax error");
  }

  private OdataRelativeUriEOFContext parseInput(final String input, boolean onResource)
      throws UriParserSyntaxException {
    UriParserParser parser = null;
    UriLexer lexer = null;
    OdataRelativeUriEOFContext ret = null;

    // Use 2 stage approach to improve performance
    // see https://github.com/antlr/antlr4/issues/192

    // stage = 1
    try {
      // create parser
      lexer = new UriLexer(new ANTLRInputStream(input));
      parser = new UriParserParser(new CommonTokenStream(lexer));

      // Set error strategy
      addStage1ErrorStategy(parser);

      // Set error collector
      addStage1ErrorListener(parser);

      // user the faster LL parsing
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

      // parse
      ret = parser.odataRelativeUriEOF();

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
        ret = parser.odataRelativeUriEOF();

      } catch (Exception weakException) {
        throw new UriParserSyntaxException("Error in syntax", weakException);

        // exceptionOnStage = 2;
      }
    } catch (Exception hardException) {
      throw new UriParserSyntaxException("Error in syntax", hardException);
    }

    return ret;
  }

  protected void addStage1ErrorStategy(UriParserParser parser) {
    // Throw exception at first syntax error
    parser.setErrorHandler(new BailErrorStrategy());

  }

  protected void addStage2ErrorStategy(UriParserParser parser) {
    // Throw exception at first syntax error
    parser.setErrorHandler(new BailErrorStrategy());
  }

  protected void addStage1ErrorListener(UriParserParser parser) {
    // No error logging to System.out or System.err, only exceptions used (depending on ErrorStrategy)
    parser.removeErrorListeners();
  }

  protected void addStage2ErrorListener(UriParserParser parser) {
    // No error logging to System.out or System.err, only exceptions used (depending on ErrorStrategy)
    parser.removeErrorListeners();
  }

}
