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
package org.apache.olingo.server.core.uri.testutil;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.apache.olingo.server.core.uri.antlr.UriLexer;

class TestErrorLogger implements ANTLRErrorListener {

  private String prefix;
  private int logLevel = 0;

  public TestErrorLogger(final String prefix, final int logLevel) {
    this.prefix = prefix;
    this.logLevel = logLevel;
  }

  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
      final int charPositionInLine,
      final String msg, final RecognitionException e) {

    if (logLevel > 0) {
      System.out.println("\n" + prefix + " -- SyntaxError");
      trace(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    }

  }

  @Override
  public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex,
      final boolean exact,
      final BitSet ambigAlts, final ATNConfigSet configs) {

  }

  @Override
  public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex,
      final BitSet conflictingAlts, final ATNConfigSet configs) {

  }

  @Override
  public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex, final int prediction,
      final ATNConfigSet configs) {

  }

  private void printStack(final Recognizer<?, ?> recognizer) {
    List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
    Collections.reverse(stack);
    System.out.println(" rule stack: " + stack);
  }

  public void trace(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
      final int line, final int charPositionInLine, final String msg, final RecognitionException e) {

    System.out.println("Error message: " + msg);

    printStack(recognizer);

    System.out.println(" line/char :" + line + " / " + charPositionInLine);
    System.out.println(" sym       :" + offendingSymbol);
    if (e != null && e.getOffendingToken() != null) {

      String lexerTokenName = "";
      try {
        lexerTokenName = UriLexer.tokenNames[e.getOffendingToken().getType()];
      } catch (ArrayIndexOutOfBoundsException es) {
        lexerTokenName = "token error";
      }

      System.out.println(" tokenname:" + lexerTokenName);
    }

  }

}