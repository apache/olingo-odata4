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

import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.apache.olingo.server.api.uri.queryoption.search.SearchTerm;
import org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token;
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

import java.util.Iterator;
import java.util.List;

public class SearchParser {

  private Iterator<SearchQueryToken> tokens;
  private SearchQueryToken token;

  public SearchOption parse(String searchQuery) throws SearchParserException, SearchTokenizerException {
    SearchTokenizer tokenizer = new SearchTokenizer();
    SearchExpression searchExpression;
    try {
      searchExpression = parse(tokenizer.tokenize(searchQuery));
    } catch (SearchTokenizerException e) {
      String message = e.getMessage();
      throw new SearchParserException("Tokenizer exception with message: " + message,
              SearchParserException.MessageKeys.TOKENIZER_EXCEPTION, message);
    }
    final SearchOptionImpl searchOption = new SearchOptionImpl();
    searchOption.setSearchExpression(searchExpression);
    return searchOption;
  }

  protected SearchExpression parse(List<SearchQueryToken> tokens) throws SearchParserException {
    this.tokens = tokens.iterator();
    nextToken();
    if (token == null) {
      throw new SearchParserException("No search String", SearchParserException.MessageKeys.NO_EXPRESSION_FOUND);
    }
    SearchExpression se = processSearchExpression(null);
    if(!isEof()) {
      throw new SearchParserException("Token left after end of search query parsing.",
          SearchParserException.MessageKeys.INVALID_END_OF_QUERY_TOKEN_LEFT, token.getToken().name());
    }
    return se;
  }

  private SearchExpression processSearchExpression(SearchExpression left) throws SearchParserException {
    if (isEof()) {
      return left;
    }

    if (left == null && (isToken(SearchQueryToken.Token.AND) || isToken(SearchQueryToken.Token.OR))) {
      throw new SearchParserException(token.getToken() + " needs a left operand.",
          SearchParserException.MessageKeys.INVALID_BINARY_OPERATOR_POSITION, token.getToken().toString());
    }

    SearchExpression expression = left;
    if (isToken(SearchQueryToken.Token.OPEN)) {
      processOpen();
      expression = processSearchExpression(left);
      if (expression == null) {
        throw new SearchParserException("Brackets must contain an expression.",
            SearchParserException.MessageKeys.NO_EXPRESSION_FOUND);
      }
      processClose();
    } else if (isTerm()) {
      expression = processTerm();
    }


    if (isToken(SearchQueryToken.Token.AND) || isToken(SearchQueryToken.Token.OPEN) || isTerm()) {
      expression = processAnd(expression);
    } else if (isToken(SearchQueryToken.Token.OR)) {
      expression = processOr(expression);
    } else if (isEof()) {
      return expression;
    }
    return expression;
  }

  private boolean isTerm() {
    return isToken(SearchQueryToken.Token.NOT)
        || isToken(SearchQueryToken.Token.PHRASE)
        || isToken(SearchQueryToken.Token.WORD);
  }

  private boolean isEof() {
    return token == null;
  }

  private boolean isToken(SearchQueryToken.Token toCheckToken) {
    return token != null && token.getToken() == toCheckToken;
  }

  private void processClose() throws SearchParserException {
    if (isToken(Token.CLOSE)) {
      nextToken();
    } else {
      throw new SearchParserException("Missing close bracket after open bracket.",
          SearchParserException.MessageKeys.MISSING_CLOSE);
    }
  }

  private void processOpen() {
    nextToken();
  }

  private SearchExpression processAnd(SearchExpression left) throws SearchParserException {
    if (isToken(SearchQueryToken.Token.AND)) {
      nextToken();
    }
    SearchExpression se = left;
    if (isTerm()) {
      se = processTerm();
      if(isTerm()) {
        se = processAnd(se);
      }
      se = new SearchBinaryImpl(left, SearchBinaryOperatorKind.AND, se);
      return processSearchExpression(se);
    } else {
      if (isToken(SearchQueryToken.Token.AND) || isToken(SearchQueryToken.Token.OR)) {
        throw new SearchParserException("Operators must not be followed by an AND or an OR",
            SearchParserException.MessageKeys.INVALID_OPERATOR_AFTER_AND, token.getToken().name());
      } else if(isEof()) {
        throw new SearchParserException("Missing search expression after AND (found end of search query)",
            SearchParserException.MessageKeys.INVALID_END_OF_QUERY, Token.AND.name());
      }
      se = processSearchExpression(se);
      return new SearchBinaryImpl(left, SearchBinaryOperatorKind.AND, se);
    }
  }

  public SearchExpression processOr(SearchExpression left) throws SearchParserException {
    if (isToken(SearchQueryToken.Token.OR)) {
      nextToken();
    }
    if(isEof()) {
      throw new SearchParserException("Missing search expression after OR (found end of search query)",
          SearchParserException.MessageKeys.INVALID_END_OF_QUERY, Token.OR.name());
    }
    SearchExpression se = processSearchExpression(left);
    return new SearchBinaryImpl(left, SearchBinaryOperatorKind.OR, se);
  }

  private SearchExpression processNot() throws SearchParserException {
    nextToken();
    if (isToken(Token.WORD) || isToken(Token.PHRASE)) {
      return new SearchUnaryImpl(processWordOrPhrase());
    }
    if(isEof()) {
      throw new SearchParserException("NOT must be followed by a term.",
          SearchParserException.MessageKeys.INVALID_NOT_OPERAND, "EOF");
    }
    throw new SearchParserException("NOT must be followed by a term not a " + token.getToken(),
        SearchParserException.MessageKeys.INVALID_NOT_OPERAND, token.getToken().toString());
  }

  private void nextToken() {
    if (tokens.hasNext()) {
      token = tokens.next();
    } else {
      token = null;
    }
  }

  private SearchExpression processTerm() throws SearchParserException {
    if (isToken(SearchQueryToken.Token.NOT)) {
      return processNot();
    }
    return processWordOrPhrase();
  }

  private SearchTerm processWordOrPhrase() throws SearchParserException {
    if (isToken(Token.PHRASE)) {
      return processPhrase();
    } else if (isToken(Token.WORD)) {
      return processWord();
    }
    throw new SearchParserException("Expected PHRASE||WORD found: " + token.getToken(),
        SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN,
        Token.PHRASE.name() + "" + Token.WORD.name(), token.getToken().toString());
  }

  private SearchTerm processWord() {
    String literal = token.getLiteral();
    nextToken();
    return new SearchTermImpl(literal);
  }

  private SearchTerm processPhrase() {
    String literal = token.getLiteral();
    nextToken();
    return new SearchTermImpl(literal.substring(1,literal.length()-1));
  }
}
