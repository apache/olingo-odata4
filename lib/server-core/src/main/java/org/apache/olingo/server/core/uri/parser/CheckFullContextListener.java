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
package org.apache.olingo.server.core.uri.parser;

import java.util.BitSet;

import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

class CheckFullContextListener extends DiagnosticErrorListener {

  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
      final int charPositionInLine,
      final String msg, final RecognitionException e) {
    // System.err.println("syntaxError detected");
  }

  @Override
  public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex, final int stopIndex,
      final boolean exact,
      final BitSet ambigAlts, final ATNConfigSet configs) {
    // System.err.println("reportAmbiguity detected");
  }

  @Override
  public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex,
      final BitSet conflictingAlts, final ATNConfigSet configs) {
    // System.err.println("reportAttemptingFullContext detected");
  }

  @Override
  public void reportContextSensitivity(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex, final int prediction,
      final ATNConfigSet configs) {
    // System.err.println("reportContextSensitivity detected");
  }

}