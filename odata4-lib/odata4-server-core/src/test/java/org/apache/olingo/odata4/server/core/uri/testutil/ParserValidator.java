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
package org.apache.olingo.odata4.server.core.uri.testutil;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.olingo.odata4.server.core.uri.antlr.UriParserParser;

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

    /**/// root = parseInput(uri);

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

}