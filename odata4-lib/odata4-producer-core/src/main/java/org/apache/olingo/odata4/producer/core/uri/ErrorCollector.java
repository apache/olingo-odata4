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


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

class ErrorCollector implements ANTLRErrorListener {
  
  private List<Exception> exceptions = new ArrayList<Exception>();
  //private ParserValidator tokenValidator;

 

  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
      final int charPositionInLine,
      final String msg, final RecognitionException e) {

    // Collect the exception
    // TODO needs to be improved
    exceptions.add(e);
    System.out.println("syntaxError");
    trace(recognizer, offendingSymbol, line, charPositionInLine, msg, e);

    //fail("syntaxError");
  }

  @Override
  public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex,
      final boolean exact,
      final BitSet ambigAlts, final ATNConfigSet configs) {

    /*
    if (tokenValidator.logLevel > 0) {
      System.out.println("reportAmbiguity: ");
      System.out.println(" ambigAlts: " + ambigAlts);
      System.out.println(" configs: " + configs);
      System.out.println(" input: " + recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex)));
    }*/
/*
    if (!tokenValidator.allowAmbiguity) {
      printStack(recognizer);
      fail("reportAmbiguity");
    }*/
  }

  @Override
  public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex,
      final BitSet conflictingAlts, final ATNConfigSet configs) {
/*
    // The grammar should be written in order to avoid attempting a full context parse because its negative
    // impact on the performance, so trace and stop here
    if (tokenValidator.logLevel > 0) {
      System.out.println("allowed AttemptingFullContext");
    }

    if (!tokenValidator.allowFullContext) {
      printStack(recognizer);
      fail("reportAttemptingFullContext");
    }*/
  }

  @Override
  public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex, final int prediction,
      final ATNConfigSet configs) {
/*
    if (tokenValidator.logLevel > 0) {
      System.out.println("allowed ContextSensitivity");
    }

    if (!tokenValidator.allowContextSensitifity) {
      printStack(recognizer);
      fail("reportContextSensitivity");
    }*/
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

      // String lexerTokenName = TestSuiteLexer.tokenNames[e.getOffendingToken().getType()];
      String lexerTokenName = "";
      try {
        //TODO check how the Lexer is accessed in the new package structure
        //lexerTokenName = UriLexer.tokenNames[e.getOffendingToken().getType()];
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