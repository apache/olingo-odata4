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
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.apache.olingo.producer.core.uri.antlr.UriLexer;

public class TokenValidator {
  private List<? extends Token> tokens = null;
  private List<Exception> exceptions = new ArrayList<Exception>();
  private Token curToken = null;
  private Exception curException = null;
  private String input = null;
  private int logLevel = 0;

  private int mode;

  public TokenValidator run(String uri) {
    input = uri;
    exceptions.clear();
    tokens = parseInput(uri);
    if (logLevel > 0) {
      showTokens();
    }

    first();
    exFirst();
    return this;
  }

  public TokenValidator showTokens() {
    boolean first = true;
    System.out.println("input: " + input);
    String nL = "\n";
    String out = "[" + nL;
    for (Token token : tokens) {
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
    return this;
  }

  public TokenValidator log(int logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  public TokenValidator isText(String expected) {
    assertEquals(expected, curToken.getText());
    return this;
  }

  public TokenValidator isAllText(String expected) {
    String tmp = "";

    for (Token curToken : tokens) {
      tmp += curToken.getText();
    }
    assertEquals(expected, tmp);
    return this;
  }

  public TokenValidator isAllInput() {
    String tmp = "";

    for (Token curToken : tokens) {
      tmp += curToken.getText();
    }
    assertEquals(input, tmp);
    return this;
  }

  public TokenValidator isInput() {
    assertEquals(input, curToken.getText());
    return this;
  }

  public TokenValidator isType(int expected) {
    // assertEquals(UriLexer.tokenNames[expected], UriLexer.tokenNames[curToken.getType()]);
    assertEquals(UriLexer.tokenNames[expected], UriLexer.tokenNames[curToken.getType()]);
    return this;
  }

  public TokenValidator isExType(Class<?> exClass) {
    assertEquals(exClass, curException.getClass());
    return this;
  }

  private List<? extends Token> parseInput(final String input) {
    ANTLRInputStream inputStream = new ANTLRInputStream(input);

    UriLexer lexer = new TestUriLexer(this,inputStream, mode);
    // lexer.setInSearch(searchMode);
    // lexer.removeErrorListeners();
    lexer.addErrorListener(new ErrorCollector(this));
    return lexer.getAllTokens();
  }

  public TokenValidator first() {
    try {
      curToken = tokens.get(0);
    } catch (IndexOutOfBoundsException ex) {
      curToken = null;
    }
    return this;
  }

  public TokenValidator exFirst() {
    try {
      curException = exceptions.get(0);
    } catch (IndexOutOfBoundsException ex) {
      curException = null;
    }
    return this;

  }

  public TokenValidator last() {
    curToken = tokens.get(tokens.size() - 1);
    return this;
  }

  public TokenValidator exLast() {
    curException = exceptions.get(exceptions.size() - 1);
    return this;
  }

  public TokenValidator at(int index) {
    try {
      curToken = tokens.get(index);
    } catch (IndexOutOfBoundsException ex) {
      curToken = null;
    }
    return this;
  }

  public TokenValidator exAt(int index) {
    try {
      curException = exceptions.get(index);
    } catch (IndexOutOfBoundsException ex) {
      curException = null;
    }
    return this;
  }

  
  private static class TestUriLexer extends UriLexer {
    private TokenValidator validator;

    public TestUriLexer(TokenValidator validator, CharStream input, int mode) {
      super(input);
      super.mode(mode);
      this.validator = validator;
    }

    @Override
    public void pushMode(int m) {
      if (validator.logLevel > 0) {
        System.out.println("OnMode" + ": " + UriLexer.modeNames[m]);
      }
      super.pushMode(m);
      
    }

    @Override
    public int popMode() {
      int m =  super.popMode();
      if (validator.logLevel > 0) {
        System.out.println("OnMode" + ": " + UriLexer.modeNames[m]);
      }
      
      return m;
    }

  }

  private static class ErrorCollector implements ANTLRErrorListener {
    TokenValidator tokenValidator;

    public ErrorCollector(TokenValidator tokenValidator) {
      this.tokenValidator = tokenValidator;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
        String msg, RecognitionException e) {
      tokenValidator.exceptions.add(e);
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
        BitSet ambigAlts, ATNConfigSet configs) {
      fail("reportAmbiguity");
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
        BitSet conflictingAlts, ATNConfigSet configs) {
      fail("reportAttemptingFullContext");
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
        ATNConfigSet configs) {
      fail("reportContextSensitivity");
    }

  }

  public void globalMode(int mode) {
    this.mode = mode;
  }

}
