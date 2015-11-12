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
  private SearchExpression root;
  private SearchQueryToken token;

  public SearchOption parse(String path, String value) {
    SearchTokenizer tokenizer = new SearchTokenizer();
    try {
      tokens = tokenizer.tokenize(value).iterator();
      nextToken();
      root = processSearchExpression(null);
    } catch (SearchTokenizerException e) {
      return null;
    }
    final SearchOptionImpl searchOption = new SearchOptionImpl();
    searchOption.setSearchExpression(root);
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

    if(token.getToken() == SearchQueryToken.Token.OPEN) {
      processOpen();
      throw illegalState();
    } else if(token.getToken() == SearchQueryToken.Token.CLOSE) {
        processClose();
        throw illegalState();
    } else if(token.getToken() == SearchQueryToken.Token.NOT) {
      processNot();
    } else if(token.getToken() == SearchQueryToken.Token.PHRASE ||
        token.getToken() == SearchQueryToken.Token.WORD) {
      return processSearchExpression(processTerm());
    } else if(token.getToken() == SearchQueryToken.Token.AND) {
        SearchExpression se = processAnd(left);
        return processSearchExpression(se);
    } else if(token.getToken() == SearchQueryToken.Token.OR) {
        return processOr(left);
    } else {
      throw illegalState();
    }
    throw illegalState();
  }

  private void processClose() {
    nextToken();
  }

  private void processOpen() {
    nextToken();
  }

  private SearchExpression processAnd(SearchExpression left) {
    nextToken();
    SearchExpression se = processTerm();
    return new SearchBinaryImpl(left, SearchBinaryOperatorKind.AND, se);
  }

  public SearchExpression processOr(SearchExpression left) {
    nextToken();
    SearchExpression se = processSearchExpression(left);
    return new SearchBinaryImpl(left, SearchBinaryOperatorKind.OR, se);
  }

  private RuntimeException illegalState() {
    return new RuntimeException();
  }

  private void processNot() {
    nextToken();
  }

  private void nextToken() {
    if(tokens.hasNext()) {
     token = tokens.next();
    } else {
      token = null;
    }
//    return null;
  }

  private SearchExpression processTerm() {
    if(token.getToken() == SearchQueryToken.Token.NOT) {
      return new SearchUnaryImpl(processPhrase());
    }
    if(token.getToken() == SearchQueryToken.Token.PHRASE) {
      return processPhrase();
    }
    if(token.getToken() == SearchQueryToken.Token.WORD) {
      return processWord();
    }
    return null;
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
