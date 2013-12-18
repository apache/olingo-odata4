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

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.apache.olingo.odata4.producer.core.uri.antlr.UriLexer;

//TODO extend to test also exception which can occure while paring
public class TokenValidator {

  private String input = null;

  private List<? extends Token> tokens = null;
  private Token curToken = null;
  private Exception curException = null;

  private int startMode;
  private int logLevel = 0;

  // --- Setup ---

  public TokenValidator log(final int logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  // --- Execution ---

  public TokenValidator run(final String uri) {
    input = uri;

    tokens = parseInput(uri);
    if (logLevel > 0) {
      showTokens();
    }

    first();
    exFirst();
    logLevel = 0;

    return this;
  }

  // --- Navigation ---

  // navigate within the tokenlist
  public TokenValidator first() {
    try {
      curToken = tokens.get(0);
    } catch (IndexOutOfBoundsException ex) {
      curToken = null;
    }
    return this;
  }

  public TokenValidator last() {
    curToken = tokens.get(tokens.size() - 1);
    return this;
  }

  public TokenValidator at(final int index) {
    try {
      curToken = tokens.get(index);
    } catch (IndexOutOfBoundsException ex) {
      curToken = null;
    }
    return this;
  }

  public TokenValidator exLast() {
    //curException = exceptions.get(exceptions.size() - 1);
    return this;
  }

  // navigate within the exception list
  public TokenValidator exFirst() {
    try {
      //curException = exceptions.get(0);
    } catch (IndexOutOfBoundsException ex) {
      curException = null;
    }
    return this;

  }

  public TokenValidator exAt(final int index) {
    try {
      //curException = exceptions.get(index);
    } catch (IndexOutOfBoundsException ex) {
      curException = null;
    }
    return this;
  }

  // --- Validation ---
  
  public TokenValidator isText(final String expected) {
    assertEquals(expected, curToken.getText());
    return this;
  }

  public TokenValidator isAllText(final String expected) {
    String actual = "";

    for (Token curToken : tokens) {
      actual += curToken.getText();
    }
    assertEquals(expected, actual);
    return this;
  }

  public TokenValidator isAllInput() {
    String actual = "";

    for (Token curToken : tokens) {
      actual += curToken.getText();
    }
    assertEquals(input, actual);
    return this;
  }

  public TokenValidator isInput() {
    assertEquals(input, curToken.getText());
    return this;
  }

  public TokenValidator isType(final int expected) {
    assertEquals(UriLexer.tokenNames[expected], UriLexer.tokenNames[curToken.getType()]);
    return this;
  }

  public TokenValidator isExType(final Class<?> exClass) {
    assertEquals(exClass, curException.getClass());
    return this;
  }

  public void globalMode(final int mode) {
    this.startMode = mode;
  }

  // --- Helper ---

  private List<? extends Token> parseInput(final String input) {
    ANTLRInputStream inputStream = new ANTLRInputStream(input);

    UriLexer lexer = new UriLexerWithTrace(inputStream, logLevel, startMode);
    //lexer.addErrorListener(new ErrorCollector(this));
    return lexer.getAllTokens();
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

}
