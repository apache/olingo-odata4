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
package org.apache.olingo.server.core.uri.parser.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.apache.olingo.server.core.uri.parser.search.SearchParserException.MessageKeys;
import org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token;
import org.junit.Test;

public class SearchParserTest extends SearchParser {

  @Test
  public void simple() throws Exception {
    SearchExpression se = run(Token.WORD);
    assertEquals("'word1'", se.toString());
    assertTrue(se.isSearchTerm());
    assertEquals("word1", se.asSearchTerm().getSearchTerm());

    se = run(Token.PHRASE);
    assertEquals("'phrase1'", se.toString());
    assertTrue(se.isSearchTerm());
    assertEquals("phrase1", se.asSearchTerm().getSearchTerm());
  }

  @Test
  public void simpleAnd() throws Exception {
    SearchExpression se = run(Token.WORD, Token.AND, Token.WORD);
    assertEquals("{'word1' AND 'word2'}", se.toString());
    assertTrue(se.isSearchBinary());
    assertEquals(SearchBinaryOperatorKind.AND, se.asSearchBinary().getOperator());
    assertEquals("word1", se.asSearchBinary().getLeftOperand().asSearchTerm().getSearchTerm());
    assertEquals("word2", se.asSearchBinary().getRightOperand().asSearchTerm().getSearchTerm());

    se = run(Token.PHRASE, Token.AND, Token.PHRASE);
    assertEquals("{'phrase1' AND 'phrase2'}", se.toString());
    assertTrue(se.isSearchBinary());
    assertEquals(SearchBinaryOperatorKind.AND, se.asSearchBinary().getOperator());
    assertEquals("phrase1", se.asSearchBinary().getLeftOperand().asSearchTerm().getSearchTerm());
    assertEquals("phrase2", se.asSearchBinary().getRightOperand().asSearchTerm().getSearchTerm());
  }

  @Test
  public void simpleOr() throws Exception {
    SearchExpression se = run(Token.WORD, Token.OR, Token.WORD);
    assertEquals("{'word1' OR 'word2'}", se.toString());
    assertTrue(se.isSearchBinary());
    assertEquals(SearchBinaryOperatorKind.OR, se.asSearchBinary().getOperator());
    assertEquals("word1", se.asSearchBinary().getLeftOperand().asSearchTerm().getSearchTerm());
    assertEquals("word2", se.asSearchBinary().getRightOperand().asSearchTerm().getSearchTerm());

    se = run(Token.PHRASE, Token.OR, Token.PHRASE);
    assertEquals("{'phrase1' OR 'phrase2'}", se.toString());
    assertTrue(se.isSearchBinary());
    assertEquals(SearchBinaryOperatorKind.OR, se.asSearchBinary().getOperator());
    assertEquals("phrase1", se.asSearchBinary().getLeftOperand().asSearchTerm().getSearchTerm());
    assertEquals("phrase2", se.asSearchBinary().getRightOperand().asSearchTerm().getSearchTerm());
  }

  @Test
  public void simpleImplicitAnd() throws Exception {
    SearchExpression se = run(Token.WORD, Token.WORD);
    assertEquals("{'word1' AND 'word2'}", se.toString());
    assertTrue(se.isSearchBinary());
    assertEquals(SearchBinaryOperatorKind.AND, se.asSearchBinary().getOperator());
    assertEquals("word1", se.asSearchBinary().getLeftOperand().asSearchTerm().getSearchTerm());
    assertEquals("word2", se.asSearchBinary().getRightOperand().asSearchTerm().getSearchTerm());

    se = run(Token.PHRASE, Token.PHRASE);
    assertEquals("{'phrase1' AND 'phrase2'}", se.toString());
    assertTrue(se.isSearchBinary());
    assertEquals(SearchBinaryOperatorKind.AND, se.asSearchBinary().getOperator());
    assertEquals("phrase1", se.asSearchBinary().getLeftOperand().asSearchTerm().getSearchTerm());
    assertEquals("phrase2", se.asSearchBinary().getRightOperand().asSearchTerm().getSearchTerm());
  }

  @Test
  public void simpleBrackets() throws Exception {
    SearchExpression se = run(Token.OPEN, Token.WORD, Token.CLOSE);
    assertEquals("'word1'", se.toString());
    assertTrue(se.isSearchTerm());
    assertEquals("word1", se.asSearchTerm().getSearchTerm());

    se = run(Token.OPEN, Token.PHRASE, Token.CLOSE);
    assertEquals("'phrase1'", se.toString());
    assertTrue(se.isSearchTerm());
    assertEquals("phrase1", se.asSearchTerm().getSearchTerm());
  }

  @Test
  public void simpleNot() throws Exception {
    SearchExpression se = run(Token.NOT, Token.WORD);
    assertEquals("{NOT 'word1'}", se.toString());
    assertTrue(se.isSearchUnary());
    assertEquals("word1", se.asSearchUnary().getOperand().asSearchTerm().getSearchTerm());

    se = run(Token.NOT, Token.PHRASE);
    assertEquals("{NOT 'phrase1'}", se.toString());
    assertTrue(se.isSearchUnary());
    assertEquals("phrase1", se.asSearchUnary().getOperand().asSearchTerm().getSearchTerm());
  }

  @Test
  public void precedenceLast() throws Exception {
    // word1 AND (word2 AND word3)
    SearchExpression se = run(Token.WORD, Token.AND, Token.OPEN, Token.WORD, Token.AND, Token.WORD, Token.CLOSE);
    assertEquals("{'word1' AND {'word2' AND 'word3'}}", se.toString());
  }

  @Test
  public void precedenceFirst() throws Exception {
    // (word1 AND word2) AND word3
    SearchExpression se = run(Token.OPEN, Token.WORD, Token.AND, Token.WORD, Token.CLOSE, Token.AND, Token.WORD);
    assertEquals("{{'word1' AND 'word2'} AND 'word3'}", se.toString());
  }

  @Test
  public void combinationAndOr() throws Exception {
    // word1 AND word2 OR word3
    SearchExpression se = run(Token.WORD, Token.AND, Token.WORD, Token.OR, Token.WORD);
    assertEquals("{{'word1' AND 'word2'} OR 'word3'}", se.toString());
    // word1 OR word2 AND word3
    se = run(Token.WORD, Token.OR, Token.WORD, Token.AND, Token.WORD);
    assertEquals("{'word1' OR {'word2' AND 'word3'}}", se.toString());
  }

  @Test
  public void unnecessaryBrackets() throws Exception {
    // (word1) (word2)
    SearchExpression se = run(Token.OPEN, Token.WORD, Token.CLOSE, Token.OPEN, Token.WORD, Token.CLOSE);
    assertEquals("{'word1' AND 'word2'}", se.toString());
  }

  @Test
  public void complex() throws Exception {
    // ((word1 word2) word3) OR word4
    SearchExpression se =
        run(Token.OPEN, Token.OPEN, Token.WORD, Token.WORD, Token.CLOSE, Token.WORD, Token.CLOSE, Token.OR, Token.WORD);
    assertEquals("{{{'word1' AND 'word2'} AND 'word3'} OR 'word4'}", se.toString());
  }

  @Test
  public void doubleNot() throws Exception {
    SearchExpression se = run(Token.NOT, Token.WORD, Token.AND, Token.NOT, Token.PHRASE);
    assertEquals("{{NOT 'word1'} AND {NOT 'phrase1'}}", se.toString());
  }

  @Test
  public void notAnd() throws Exception {
    runEx(SearchParserException.MessageKeys.INVALID_NOT_OPERAND, Token.NOT, Token.AND);
  }

  @Test
  public void notNotWord() throws Exception {
    runEx(SearchParserException.MessageKeys.INVALID_NOT_OPERAND, Token.NOT, Token.NOT, Token.WORD);
  }

  @Test
  public void doubleAnd() throws Exception {
    runEx(SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.WORD, Token.AND, Token.AND, Token.WORD);
  }

  @Test
  public void invalidQueryEnds() {
    runEx(MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.WORD, Token.AND);
    runEx(MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.WORD, Token.OR);
    runEx(MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.NOT, Token.WORD, Token.OR);
    runEx(MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.NOT, Token.WORD, Token.AND);
    runEx(MessageKeys.INVALID_END_OF_QUERY, Token.WORD, Token.AND, Token.WORD, Token.CLOSE);
  }

  @Test
  public void invalidQueryStarts() throws Exception {
    run(Token.WORD, Token.AND, Token.WORD, Token.AND, Token.WORD);
  }

  @Test
  public void singleAnd() {
    runEx(SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.AND);
  }

  @Test
  public void singleOpenBracket() {
    runEx(SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.OPEN);
  }

  @Test
  public void emptyBrackets() {
    runEx(SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN, Token.OPEN, Token.CLOSE);
  }

  @Test
  public void empty() {
    Token[] emptyArray = new Token[0];
    runEx(SearchParserException.MessageKeys.NO_EXPRESSION_FOUND, emptyArray);
  }

  private void runEx(final MessageKeys key, final Token... tokenArray) {
    try {
      run(tokenArray);
      fail("Expected SearchParserException with key " + key);
    } catch (SearchParserException e) {
      assertEquals(key, e.getMessageKey());
    }
  }

  private SearchExpression run(final Token... tokenArray) throws SearchParserException {
    List<SearchQueryToken> tokenList = prepareTokens(tokenArray);
    SearchExpression se = parse(tokenList);
    assertNotNull(se);
    return se;
  }

  public List<SearchQueryToken> prepareTokens(final Token... tokenArray) {
    ArrayList<SearchQueryToken> tokenList = new ArrayList<SearchQueryToken>();
    int wordNumber = 1;
    int phraseNumber = 1;
    for (Token aToken : tokenArray) {
      SearchQueryToken token = mock(SearchQueryToken.class);
      when(token.getToken()).thenReturn(aToken);
      if (aToken == Token.WORD) {
        when(token.getLiteral()).thenReturn("word" + wordNumber);
        wordNumber++;
      } else if (aToken == Token.PHRASE) {
        when(token.getLiteral()).thenReturn("\"phrase" + phraseNumber + "\"");
        phraseNumber++;
      }
      when(token.toString()).thenReturn("" + aToken);
      tokenList.add(token);
    }
    return tokenList;
  }

}