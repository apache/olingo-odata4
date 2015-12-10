/*
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
 */
package org.apache.olingo.server.core.uri.testutil;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.apache.olingo.server.core.uri.antlr.UriLexer;

public class TokenValidator {

  private String input = null;
  private List<? extends Token> tokens = null;
  private Token curToken = null;
  private Exception curException = null;

  private int startMode;

  // --- Execution ---

  public TokenValidator run(final String uri) {
    input = uri;
    tokens = parseInput(uri);
    first();
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
    assertEquals(UriLexer.VOCABULARY.getDisplayName(expected), UriLexer.VOCABULARY.getDisplayName(curToken.getType()));
    return this;
  }

  public TokenValidator isExType(final Class<?> exClass) {
    assertEquals(exClass, curException.getClass());
    return this;
  }

  public void globalMode(final int mode) {
    startMode = mode;
  }

  // --- Helper ---

  private List<? extends Token> parseInput(final String input) {
    ANTLRInputStream inputStream = new ANTLRInputStream(input);
    UriLexer lexer = new UriLexer(inputStream);
    lexer.mode(startMode);
    return lexer.getAllTokens();
  }
}
