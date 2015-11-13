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
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

import java.util.Iterator;
import java.util.List;

public class SearchParser {

  private Iterator<SearchQueryToken> tokens;
  private SearchQueryToken token;

  public SearchOption parse(String path, String value) {
    SearchTokenizer tokenizer = new SearchTokenizer();
    SearchExpression searchExpression;
    try {
      tokens = tokenizer.tokenize(value).iterator();
      nextToken();
      searchExpression = processSearchExpression(null);
    } catch (SearchTokenizerException e) {
      return null;
    }
    final SearchOptionImpl searchOption = new SearchOptionImpl();
    searchOption.setSearchExpression(searchExpression);
    return searchOption;
  }

  protected SearchExpression parseInternal(List<SearchQueryToken> tokens) {
    this.tokens = tokens.iterator();
    nextToken();
    return processSearchExpression(null);
  }

  private SearchExpression processSearchExpression(SearchExpression left) {
    if(token == null) {
      return left;
    }

    SearchExpression expression = left;
    if(isToken(SearchQueryToken.Token.OPEN)) {
      processOpen();
      expression = processSearchExpression(left);
      validateToken(SearchQueryToken.Token.CLOSE);
      processClose();
    } else if(isTerm()) {
      expression = processTerm();
    }

    if(isToken(SearchQueryToken.Token.AND) || isTerm()) {
        expression = processAnd(expression);
    } else if(isToken(SearchQueryToken.Token.OR)) {
        expression = processOr(expression);
    } else if(isEof()) {
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
    if(token == null) {
      return false;
    }
    return token.getToken() == toCheckToken;
  }

  private void validateToken(SearchQueryToken.Token toValidateToken) {
    if(!isToken(toValidateToken)) {
      throw illegalState();
    }
  }

  private void processClose() {
    nextToken();
  }

  private void processOpen() {
    nextToken();
  }

  private SearchExpression processAnd(SearchExpression left) {
    if(isToken(SearchQueryToken.Token.AND)) {
      nextToken();
    }
    SearchExpression se = left;
    if(isTerm()) {
      se = processTerm();
      se = new SearchBinaryImpl(left, SearchBinaryOperatorKind.AND, se);
      return processSearchExpression(se);
    } else {
      se = processSearchExpression(se);
      return new SearchBinaryImpl(left, SearchBinaryOperatorKind.AND, se);
    }
  }

  public SearchExpression processOr(SearchExpression left) {
    if(isToken(SearchQueryToken.Token.OR)) {
      nextToken();
    }
    SearchExpression se = processSearchExpression(left);
    return new SearchBinaryImpl(left, SearchBinaryOperatorKind.OR, se);
  }

  private RuntimeException illegalState() {
    return new RuntimeException();
  }

  private SearchExpression processNot() {
    nextToken();
    SearchExpression searchExpression = processTerm();
    if(searchExpression.isSearchTerm()) {
      return new SearchUnaryImpl(searchExpression.asSearchTerm());
    }
    throw illegalState();
  }

  private void nextToken() {
    if(tokens.hasNext()) {
     token = tokens.next();
    } else {
      token = null;
    }
  }

  private SearchExpression processTerm() {
    if(isToken(SearchQueryToken.Token.NOT)) {
      return processNot();
    }
    if(isToken(SearchQueryToken.Token.PHRASE)) {
      return processPhrase();
    } else if(isToken(SearchQueryToken.Token.WORD)) {
      return processWord();
    }
    throw illegalState();
  }

  private SearchTermImpl processWord() {
    String literal = token.getLiteral();
    nextToken();
    return new SearchTermImpl(literal);
  }

  private SearchTermImpl processPhrase() {
    String literal = token.getLiteral();
    nextToken();
    return new SearchTermImpl(literal);
  }
}
