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
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriEOFContext;

public class ParserAdapter {
  static public OdataRelativeUriEOFContext parseInput(final String input) throws UriParserException {
    UriParserParser parser = null;
    UriLexer lexer = null;
    OdataRelativeUriEOFContext ret = null;

    // Use 2 stage approach to improve performance
    // see https://github.com/antlr/antlr4/issues/192

    // stage= 1
    try {
      // create parser
      lexer = new UriLexer(new ANTLRInputStream(input));
      parser = new UriParserParser(new CommonTokenStream(lexer));

      // Bail out of parser at first syntax error. --> proceeds in catch block with step 2
      parser.setErrorHandler(new BailErrorStrategy());

      // User the faster LL parsing
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
      ret = parser.odataRelativeUriEOF();

    } catch (ParseCancellationException hardException) {

      try {
        // create parser
        lexer = new UriLexer(new ANTLRInputStream(input));
        parser = new UriParserParser(new CommonTokenStream(lexer));
        

        // Used default error strategy
        parser.setErrorHandler(new DefaultErrorStrategy());

        // User the slower SLL parsing
        parser.getInterpreter().setPredictionMode(PredictionMode.LL);

        ret = parser.odataRelativeUriEOF();

      } catch (Exception weakException) {
        throw new UriParserException("Error in Parser", weakException);

        // exceptionOnStage = 2;
      }
    } catch (Exception hardException) {
      throw new UriParserException("Error in Parser", hardException);
    }

    return ret;
  }

}
