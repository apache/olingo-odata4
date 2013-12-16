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
package org.apache.olingo.odata4.producer.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriParserParser.OdataRelativeUriEOFContext;

// TODO extend to test also exception which can occure while paring
public class ParserValidator {
  
  private List<Exception> exceptions = new ArrayList<Exception>();
  private ParserRuleContext root;

  private String input = null;
  private Exception curException = null;
  // private int exceptionOnStage = -1;
  // private Exception curWeakException = null;
  private boolean allowFullContext;
  private boolean allowContextSensitifity;
  private boolean allowAmbiguity;
  public int logLevel = 0;
  private int lexerLogLevel = 0;

  // private int lexerLogLevel = 0;

  public ParserValidator run(final String uri) {
    input = uri;
    // just run a short lexer step. E.g. to print the tokens
    if (lexerLogLevel > 0) {
      (new TokenValidator()).setLog(lexerLogLevel).run(input);
    }
    root = parseInput(uri);
    // LOG > 0 - Write serialized tree
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

    // exFirst();
    return this;
  }

  public ParserValidator log(final int logLevel) {
    this.logLevel = logLevel;
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

  public ParserValidator isText(final String expected) {

    assertEquals(null, curException);

    assertEquals(0, exceptions.size());

    String text = ParseTreeToText.getTreeAsText(root, new UriParserParser(null).getRuleNames());

    assertEquals(expected, text);
    return this;
  }

  public ParserValidator isExeptionType(final Class<?> exClass) {
    assertEquals(exClass, curException.getClass());
    return this;
  }

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
      lexer = new UriLexerWithTrace( new ANTLRInputStream(input), this.lexerLogLevel);
      parser = new UriParserParser(new CommonTokenStream(lexer));

      // write single tokens to System.out
      if (logLevel > 1) {
        // can not be used because the listener is called before the mode changes
        // TODO verify this
        parser.addParseListener(new TokenWriter());
      }
      // write always a error message in case of syntax errors
      // parser.addErrorListener(new TestErrorHandler<Object>());
      // check error message if whether they are allowed or not
      parser.addErrorListener(new ErrorCollector(this));

      // bail out of parser at first syntax error. --> proceed in catch block with step 2
      parser.setErrorHandler(new BailErrorStrategy());

      // user the faster LL parsing
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

      // parse
      if (logLevel > 1) {
        System.out.println("Step 1");
        System.out.println(" PrectictionMode: " + parser.getInterpreter().getPredictionMode() + ")");
      }
      ret = parser.odataRelativeUriEOF();

    } catch (Exception exception) {
      curException = exception;
      try {
        // clear status
        curException = null;
        exceptions.clear();

        // create parser
        lexer = new UriLexerWithTrace(new ANTLRInputStream(input), this.lexerLogLevel);
        parser = new UriParserParser(new CommonTokenStream(lexer));

        // write single tokens to System.out
        if (logLevel > 1) {
          parser.addParseListener(new TokenWriter());
        }

        // write always a error message in case of syntax errors
        parser.addErrorListener(new ErrorCollector(this));
        // check error message if whether they are allowed or not
        parser.addErrorListener(new ErrorCollector(this));

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

  private static class ErrorCollector implements ANTLRErrorListener {
    private ParserValidator tokenValidator;

    public ErrorCollector(final ParserValidator tokenValidator) {
      this.tokenValidator = tokenValidator;
    }

    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
        final int charPositionInLine,
        final String msg, final RecognitionException e) {

      // Collect the exception
      // TODO needs to be improved
      tokenValidator.exceptions.add(e);
      System.out.println("syntaxError");
      trace(recognizer, offendingSymbol, line, charPositionInLine, msg, e);

      fail("syntaxError");
    }

    @Override
    public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex,
        final boolean exact,
        final BitSet ambigAlts, final ATNConfigSet configs) {

      if (tokenValidator.logLevel > 0) {
        System.out.println("reportAmbiguity: ");
        System.out.println(" ambigAlts: " + ambigAlts);
        System.out.println(" configs: " + configs);
        System.out.println(" input: " + recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex)));
      }

      if (!tokenValidator.allowAmbiguity) {
        printStack(recognizer);
        fail("reportAmbiguity");
      }
    }

    @Override
    public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex,
        final int stopIndex,
        final BitSet conflictingAlts, final ATNConfigSet configs) {

      // The grammar should be written in order to avoid attempting a full context parse because its negative
      // impact on the performance, so trace and stop here
      if (tokenValidator.logLevel > 0) {
        System.out.println("allowed AttemptingFullContext");
      }

      if (!tokenValidator.allowFullContext) {
        printStack(recognizer);
        fail("reportAttemptingFullContext");
      }
    }

    @Override
    public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex,
        final int stopIndex, final int prediction,
        final ATNConfigSet configs) {

      if (tokenValidator.logLevel > 0) {
        System.out.println("allowed ContextSensitivity");
      }

      if (!tokenValidator.allowContextSensitifity) {
        printStack(recognizer);
        fail("reportContextSensitivity");
      }
    }

    /*
     * private void printStack(final Parser recognizer) {
     * List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
     * Collections.reverse(stack);
     * 
     * System.out.println(" Rule stack: " + stack);
     * }
     */
    private void printStack(final Recognizer<?, ?> recognizer) {
      List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
      Collections.reverse(stack);
      System.out.println(" rule stack: " + stack);
    }

    public void trace(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
        final int line, final int charPositionInLine, final String msg, final RecognitionException e) {

      System.err.println("-");
      // TODO check also http://stackoverflow.com/questions/14747952/ll-exact-ambig-detection-interpetation

      printStack(recognizer);

      if (e != null && e.getOffendingToken() != null) {

        //String lexerTokenName = TestSuiteLexer.tokenNames[e.getOffendingToken().getType()];
        String lexerTokenName = "";
        try {
          lexerTokenName = UriLexer.tokenNames[e.getOffendingToken().getType()];
        } catch (ArrayIndexOutOfBoundsException es) {
          lexerTokenName = "token error";
        }
        System.err.println(" line " + line + ":" + charPositionInLine + " at " +
            offendingSymbol + "/" + lexerTokenName + ": " + msg);
      } else {
        System.err.println(" line " + line + ":" + charPositionInLine + " at " + offendingSymbol + ": " + msg);
      }
    }

  }

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

  public ParserValidator lexerLog(final int i) {
    lexerLogLevel = i;
    return this;
  }

}
