/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.server.core.testutil;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.olingo.odata4.server.core.uri.antlr.UriParserParser;

// TODO extend to test also exception which can occure while paring
public class ParserValidator {

  private String input = null;
  private ParserRuleContext root;

  int logLevel = 0;
  private int lexerLogLevel = 0;

  boolean allowFullContext;
  boolean allowContextSensitifity;
  boolean allowAmbiguity;

  List<Exception> exceptions = new ArrayList<Exception>();
  private Exception curException = null;

  // --- Setup ---

  public ParserValidator log(final int logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  public ParserValidator lexerLog(final int logLevel) {
    lexerLogLevel = logLevel;
    return this;
  }

  /**
   * TODO verify
   * Used in fast LL Parsing:
   * Don't stop the parsing process when the slower full context parsing (with prediction mode SLL) is
   * required
   * @return
   */
  public ParserValidator aFC() {
    allowFullContext = true;
    return this;
  }

  /**
   * 
   * TODO verify
   * Used in fast LL Parsing:
   * Allows ContextSensitifity Errors which occur often when using the slower full context parsing
   * and indicate that there is a context sensitivity ( which may not be an error).
   * @return
   */
  public ParserValidator aCS() {
    allowContextSensitifity = true;
    return this;
  }

  /**
   * TODO verify
   * Used in fast LL Parsing:
   * Allows ambiguities
   * @return
   */
  public ParserValidator aAM() {
    allowAmbiguity = true;
    return this;
  }

  // --- Execution ---

  public ParserValidator run(final String uri) {
    input = uri;

    // just run a short lexer step. E.g. to print the tokens
    if (lexerLogLevel > 0) {
      (new TokenValidator()).log(lexerLogLevel).run(input);
    }

    /**///root = parseInput(uri);

    // if LOG > 0 - Write serialized tree
    if (logLevel > 0) {
      if (root != null) {
        System.out.println(ParseTreeToText.getTreeAsText(root, new UriParserParser(null).getRuleNames()));
      } else {
        System.out.println("root == null");
      }
    }

    // reset for next test
    allowFullContext = false;
    allowContextSensitifity = false;
    allowAmbiguity = false;
    logLevel = 0;

    return this;
  }

  // --- Navigation ---

  public ParserValidator exFirst() {
    try {
      // curWeakException = exceptions.get(0);
    } catch (IndexOutOfBoundsException ex) {
      // curWeakException = null;
    }
    return this;

  }

  public ParserValidator exLast() {
    // curWeakException = exceptions.get(exceptions.size() - 1);
    return this;
  }

  public ParserValidator exAt(final int index) {
    try {
      // curWeakException = exceptions.get(index);
    } catch (IndexOutOfBoundsException ex) {
      // curWeakException = null;
    }
    return this;
  }

  // --- Validation ---

  public ParserValidator isText(final String expected) {

    assertEquals(null, curException);
    assertEquals(0, exceptions.size());

    String actualTreeAsText = ParseTreeToText.getTreeAsText(root, new UriParserParser(null).getRuleNames());

    assertEquals(expected, actualTreeAsText);
    return this;
  }

  public ParserValidator isExeptionType(final Class<?> exClass) {
    assertEquals(exClass, curException.getClass());
    return this;
  }

  // --- Helper ---
/*
  private OdataRelativeUriEOFContext parseInput(final String input) {
    UriParserParser parser = null;
    UriLexerWithTrace lexer = null;
    OdataRelativeUriEOFContext ret = null;

    // Use 2 stage approach to improve performance
    // see https://github.com/antlr/antlr4/issues/192
    // TODO verify this

    // stage= 1
    try {
      curException = null;
      exceptions.clear();

      // create parser
      lexer = new UriLexerWithTrace(new ANTLRInputStream(input), lexerLogLevel);
      parser = new UriParserParser(new CommonTokenStream(lexer));

      // write always a error message in case of syntax errors
      // parser.addErrorListener(new TestErrorHandler<Object>());
      // check error message if whether they are allowed or not
      // parser.addErrorListener(new ErrorCollector());

      // bail out of parser at first syntax error. --> proceed in catch block with step 2
      parser.setErrorHandler(new BailErrorStrategy());

      // user the faster SLL parsing
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

      // parse
      if (logLevel > 0) {
        System.out.println("Step 1");
        System.out.println(" PrectictionMode: " + parser.getInterpreter().getPredictionMode());
      }
      ret = parser.odataRelativeUriEOF();

    } catch (Exception exception) {
      curException = exception;
      try {
        // clear status
        curException = null;
        exceptions.clear();

        // create parser
        lexer = new UriLexerWithTrace(new ANTLRInputStream(input), lexerLogLevel);
        parser = new UriParserParser(new CommonTokenStream(lexer));

        // write always a error message in case of syntax errors
        // parser.addErrorListener(new ErrorCollector(this));
        // check error message if whether they are allowed or not
        // parser.addErrorListener(new ErrorCollector(this));

        // Used default error strategy
        parser.setErrorHandler(new DefaultErrorStrategy());

        // User the slower SLL parsing
        parser.getInterpreter().setPredictionMode(PredictionMode.LL);

        // parse
        if (logLevel > 1) {
          System.out.println("Step 2");
          System.out.println(" PrectictionMode: " + parser.getInterpreter().getPredictionMode() + ")");
        }
        ret = parser.odataRelativeUriEOF();

      } catch (Exception exception1) {
        curException = exception1;
        // exceptionOnStage = 2;
      }
    }

    return ret;
  }
*/
}