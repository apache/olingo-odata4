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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token;
import org.junit.Ignore;
import org.junit.Test;

public class SearchParserTest extends SearchParser {

  @Test
  public void simple() {
    SearchExpression se = run(Token.WORD);
    assertEquals("'word1'", se.toString());
    assertTrue(se.isSearchTerm());
    assertEquals("word1", se.asSearchTerm().getSearchTerm());
    
    se = run(Token.PHRASE);
    assertEquals("'phrase1'", se.toString());
    assertTrue(se.isSearchTerm());
    //TODO: Check if quotation marks should be part of the string we deliver
    assertEquals("phrase1", se.asSearchTerm().getSearchTerm());
  }

  @Test
  public void simpleAnd() {
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
  public void simpleOr() {
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
  public void simpleImplicitAnd() {
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
  public void simpleBrackets() {
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
  public void simpleNot() {
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
  public void precedenceLast() {
    //word1 AND (word2 AND word3) 
    SearchExpression se = run(Token.WORD, Token.AND, Token.OPEN, Token.WORD, Token.AND, Token.WORD, Token.CLOSE);
    assertEquals("{'word1' AND {'word2' AND 'word3'}}", se.toString());
  }
  
  @Test
  public void precedenceFirst() {
    //(word1 AND word2) AND word3 
    SearchExpression se = run(Token.OPEN, Token.WORD, Token.AND, Token.WORD, Token.CLOSE, Token.AND, Token.WORD);
    assertEquals("{{'word1' AND 'word2'} AND 'word3'}", se.toString());
  }

  @Test
  public void combinationAndOr() {
    //word1 AND word2 OR word3
    SearchExpression se = run(Token.WORD, Token.AND, Token.WORD, Token.OR, Token.WORD);
    assertEquals("{{'word1' AND 'word2'} OR 'word3'}", se.toString());
    //word1 OR word2 AND word3
    se = run(Token.WORD, Token.OR, Token.WORD, Token.AND, Token.WORD);
    assertEquals("{'word1' OR {'word2' AND 'word3'}}", se.toString());
  }


  private SearchExpression run(SearchQueryToken.Token... tokenArray) {
    List<SearchQueryToken> tokenList = prepareTokens(tokenArray);
    SearchExpression se = parseInternal(tokenList);
    assertNotNull(se);
    return se;
  }

  public List<SearchQueryToken> prepareTokens(SearchQueryToken.Token... tokenArray) {
    ArrayList<SearchQueryToken> tokenList = new ArrayList<SearchQueryToken>();
    int wordNumber = 1;
    int phraseNumber = 1;
    for (int i = 0; i < tokenArray.length; i++) {
      SearchQueryToken token = mock(SearchQueryToken.class);
      when(token.getToken()).thenReturn(tokenArray[i]);
      if (tokenArray[i] == Token.WORD) {
        when(token.getLiteral()).thenReturn("word" + wordNumber);
        wordNumber++;
      } else if (tokenArray[i] == Token.PHRASE) {
        when(token.getLiteral()).thenReturn("phrase" + phraseNumber);
        phraseNumber++;
      }
      tokenList.add(token);
    }
    return tokenList;
  }

}