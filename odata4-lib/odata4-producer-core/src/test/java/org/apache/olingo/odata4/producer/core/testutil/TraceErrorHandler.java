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

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.apache.olingo.producer.core.uri.antlr.UriLexer;

public class TraceErrorHandler<T> extends BaseErrorListener {
  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
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
}
