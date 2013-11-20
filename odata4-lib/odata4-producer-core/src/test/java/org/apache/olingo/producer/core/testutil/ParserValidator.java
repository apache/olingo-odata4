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
package org.apache.olingo.producer.core.testutil;

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
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;
import org.apache.olingo.producer.core.uri.antlr.UriLexer;
import org.apache.olingo.producer.core.uri.antlr.UriParser;
import org.apache.olingo.producer.core.uri.antlr.UriParser.OdataRelativeUriEOFContext;
import org.apache.olingo.producer.core.uri.antlr.UriParser.TestContext;

public class ParserValidator {
  private List<Exception> exceptions = new ArrayList<Exception>();
  private ParserRuleContext root;

  private String input = null;
  private int exceptionOnStage = -1;
  private Exception curException = null;
  private Exception curWeakException = null;
  private boolean allowFullContext;
  private boolean allowContextSensitifity;

  public ParserValidator run(String uri) {
    return run(uri, false);
  }

  public ParserValidator runTest(String uri) {
    return runTest(uri, false);
  }

  public ParserValidator run(String uri, boolean searchMode) {
    input = uri;
    root = parseInput(uri, searchMode);
    allowFullContext = false;
    allowContextSensitifity = false;

    exFirst();
    return this;
  }

  public ParserValidator runTest(String uri, boolean searchMode) {
    input = uri;
    root = parseInputTest(uri, searchMode);
    allowFullContext = false;
    allowContextSensitifity = false;

    exFirst();
    return this;
  }

  public ParserValidator aFC() {
    allowFullContext = true;
    return this;
  }

  public ParserValidator aCS() {
    allowContextSensitifity = true;
    return this;
  }

  public ParserValidator isText(String expected) {
    assertEquals(null, curException);
    assertEquals(0, exceptions.size());

    String text = ParseTreeSerializer.getTreeAsText(root, new UriParser(null).getRuleNames());
    assertEquals(expected, text);
    return this;
  }

  public ParserValidator isExType(Class<?> exClass) {
    assertEquals(exClass, curException.getClass());
    return this;
  }

  private OdataRelativeUriEOFContext parseInput(final String input, boolean searchMode) {
    UriParser parser = null;
    OdataRelativeUriEOFContext ret = null;

    // Use 2 stage approach to improve performance
    // see https://github.com/antlr/antlr4/issues/192
    // TODO verify this

    // stage= 1
    try {
      curException = null;
      exceptions.clear();
      parser = getNewParser(input, searchMode);
      parser.setErrorHandler(new BailErrorStrategy());
      parser.getInterpreter().setPredictionMode(PredictionMode.LL);
      ret = parser.odataRelativeUriEOF();
    } catch (Exception ex) {
      curException = ex;
      exceptionOnStage = 1;
      // stage= 2
      try {
        curException = null;
        exceptions.clear();
        parser = getNewParser(input, searchMode);
        parser.setErrorHandler(new DefaultErrorStrategy());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        ret = parser.odataRelativeUriEOF();
      } catch (Exception ex1) {
        curException = ex1;
        exceptionOnStage = 2;
      }
    }

    return ret;
  }

  private TestContext parseInputTest(final String input, boolean searchMode) {
    UriParser parser = null;
    TestContext ret = null;

    // Use 2 stage approach to improve performance
    // see https://github.com/antlr/antlr4/issues/192
    // TODO verify this

    // stage= 1
    try {
      curException = null;
      exceptions.clear();
      parser = getNewParser(input, searchMode);
      parser.setErrorHandler(new BailErrorStrategy());
      parser.getInterpreter().setPredictionMode(PredictionMode.LL);
      ret = parser.test();
    } catch (Exception ex) {
      curException = ex;
      exceptionOnStage = 1;
      // stage= 2
      try {
        curException = null;
        exceptions.clear();
        parser = getNewParser(input, searchMode);
        parser.setErrorHandler(new DefaultErrorStrategy());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        ret = parser.test();
      } catch (Exception ex1) {
        curException = ex1;
        exceptionOnStage = 2;
      }
    }

    return ret;
  }

  private UriParser getNewParser(final String input, boolean searchMode) {
    ANTLRInputStream inputStream = new ANTLRInputStream(input);

    // UriLexer lexer = new UriLexer(inputStream);
    UriLexer lexer = new UriLexer(inputStream);
    lexer.setInSearch(searchMode);
    // lexer.removeErrorListeners();
    lexer.addErrorListener(new ErrorCollector(this));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    UriParser parser = new UriParser(tokens);
    parser.addErrorListener(new TraceErrorHandler());
    parser.addErrorListener(new ErrorCollector(this));

    return parser;
  }

  private static class ErrorCollector implements ANTLRErrorListener {
    ParserValidator tokenValidator;

    public ErrorCollector(ParserValidator tokenValidator) {
      this.tokenValidator = tokenValidator;
    }

    public void trace(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
        final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
      System.err.println("-");
      // check also http://stackoverflow.com/questions/14747952/ll-exact-ambig-detection-interpetation
      List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
      Collections.reverse(stack);
      System.err.println("rule stack: " + stack);
      if (e != null && e.getOffendingToken() != null) {

        // String lexerTokenName =TestSuiteLexer.tokenNames[e.getOffendingToken().getType()];
        String lexerTokenName = "";
        try {
          lexerTokenName = UriLexer.tokenNames[e.getOffendingToken().getType()];
        } catch (ArrayIndexOutOfBoundsException es) {
          lexerTokenName = "token error";
        }
        System.err.println("line " + line + ":" + charPositionInLine + " at " +
            offendingSymbol + "/" + lexerTokenName + ": " + msg);
      } else {
        System.err.println("line " + line + ":" + charPositionInLine + " at " + offendingSymbol + ": " + msg);
      }
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
        String msg, RecognitionException e) {
      tokenValidator.exceptions.add(e);
      trace(recognizer, offendingSymbol, line, charPositionInLine, msg, e);

      fail("syntaxError"); // don't fail here we want to the error message at the caller
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
        BitSet ambigAlts, ATNConfigSet configs) {
      printStack(recognizer);

      fail("reportAmbiguity");

    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
        BitSet conflictingAlts, ATNConfigSet configs) {
      // The grammar should be written in order to avoid attempting a full context parse because its negative
      // impact on the performance, so trace and stop here

      if (!tokenValidator.allowFullContext) {
        printStack(recognizer);
        fail("reportAttemptingFullContext");
      }
    }

    private void printStack(Parser recognizer) {
      List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
      Collections.reverse(stack);

      System.err.println("rule stack: " + stack);
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
        ATNConfigSet configs) {
      if (!tokenValidator.allowContextSensitifity) {
        printStack(recognizer);
        fail("reportContextSensitivity");
      }
    }

  }

  public ParserValidator exFirst() {
    try {
      curWeakException = exceptions.get(0);
    } catch (IndexOutOfBoundsException ex) {
      curWeakException = null;
    }
    return this;

  }

  public ParserValidator exLast() {
    curWeakException = exceptions.get(exceptions.size() - 1);
    return this;
  }

  public ParserValidator exAt(int index) {
    try {
      curWeakException = exceptions.get(index);
    } catch (IndexOutOfBoundsException ex) {
      curWeakException = null;
    }
    return this;
  }

}
