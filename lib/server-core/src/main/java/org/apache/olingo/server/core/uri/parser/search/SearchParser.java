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
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

import java.util.Iterator;

public class SearchParser {

  private Iterator<SearchQueryToken> tokens;
  private SearchExpression root;
//  private SearchQueryToken currentToken;

  public SearchOption parse(String path, String value) {
    SearchTokenizer tokenizer = new SearchTokenizer();
    try {
      tokens = tokenizer.tokenize(value).iterator();
//      currentToken = tokens.next();
      root = processSearchExpression();
    } catch (SearchTokenizerException e) {
      return null;
    }
    final SearchOptionImpl searchOption = new SearchOptionImpl();
    searchOption.setSearchExpression(root);
    return searchOption;
  }

  private SearchExpression processSearchExpression() {
    SearchQueryToken token = nextToken();
    if(token.getToken() == SearchQueryToken.Token.OPEN) {
      throw illegalState();
    } else if(token.getToken() == SearchQueryToken.Token.NOT) {
      return processNot();
    } else if(token.getToken() == SearchQueryToken.Token.PHRASE ||
        token.getToken() == SearchQueryToken.Token.WORD) {
      return processTerm(token);
//    } else if(token.getToken() == SearchQueryToken.Token.AND) {
//      return processAnd();
    } else {
      throw illegalState();
    }
  }

  private SearchExpression processAnd(SearchExpression se) {
    SearchQueryToken token = nextToken();
    if(token.getToken() == SearchQueryToken.Token.PHRASE ||
        token.getToken() == SearchQueryToken.Token.WORD) {
//      SearchExpression t = processTerm(token);
      return new SearchBinaryImpl(se, SearchBinaryOperatorKind.AND, processTerm(token));
    }
    throw illegalState();
  }

  private SearchExpression processOr(SearchExpression se) {
    SearchQueryToken token = nextToken();
    if(token.getToken() == SearchQueryToken.Token.PHRASE ||
        token.getToken() == SearchQueryToken.Token.WORD) {
      return new SearchBinaryImpl(se, SearchBinaryOperatorKind.OR, processTerm(token));
    }
    throw illegalState();
  }

  private RuntimeException illegalState() {
    return new RuntimeException();
  }

  private SearchUnaryImpl processNot() {
    SearchQueryToken token = nextToken();
    if(token.getToken() == SearchQueryToken.Token.PHRASE ||
        token.getToken() == SearchQueryToken.Token.WORD) {
      throw illegalState();
//      return new SearchUnaryImpl(processTerm(token));
    }
    throw illegalState();
  }

  private SearchQueryToken nextToken() {
//    if(tokens.hasNext()) {
    return tokens.next();
//    }
//    return null;
  }

  private SearchExpression processTerm(SearchQueryToken token) {
    SearchTerm searchTerm = new SearchTermImpl(token.getLiteral());
    if(isEof()) {
      return searchTerm;
    }

    SearchQueryToken next = nextToken();
    if(next.getToken() == SearchQueryToken.Token.AND) {
      return processAnd(searchTerm);
    } else if(next.getToken() == SearchQueryToken.Token.OR) {
      return processOr(searchTerm);
    }

    throw illegalState();
  }

  private boolean isEof() {
    return !tokens.hasNext();
  }
}
