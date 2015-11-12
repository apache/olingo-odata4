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

import java.util.ArrayList;

import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.apache.olingo.server.api.uri.queryoption.search.SearchTerm;
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

public class SearchParser {

  protected ArrayList<SearchQueryToken> tokens;
  private int size;
  private int currentPosition = -1;

  public SearchOption parse(String path, String value) throws SearchTokenizerException, SearchParserException {
    SearchTokenizer tokenizer = new SearchTokenizer();
    tokens = tokenizer.tokenize(value);
    SearchExpression root = processTokens();
    final SearchOptionImpl searchOption = new SearchOptionImpl();
    searchOption.setSearchExpression(root);
    return searchOption;
  }

  protected SearchExpression processTokens() throws SearchParserException {
    size = tokens.size();
    SearchExpression root = null;
    SearchQueryToken nextToken = next();
    switch (nextToken.getToken()) {
    case WORD:
    case PHRASE:
      root = processWord(null, nextToken.getLiteral());
      break;
    case NOT:
      root = processNot();
    case OPEN:
      // TODO: implement
    default:
      break;
    }

    if (hasNext()) {
      throw new SearchParserException();
    }

    return root;
  }

  private SearchExpression processNot() throws SearchParserException {
    SearchUnaryImpl not = new SearchUnaryImpl();
    SearchQueryToken nextToken = next();
    switch (nextToken.getToken()) {
    case WORD:
    case PHRASE:
      processWord(not, nextToken.getLiteral());
    case OPEN:
    default:
      break;
    }
    return not;
  }

  private SearchExpression processWord(SearchUnaryImpl not, String literal) throws SearchParserException {
    SearchExpression exp = new SearchTermImpl(literal);
    if (not != null) {
      not.setOperand(exp);
      exp = not;
    }
    if (hasNext()) {
      SearchQueryToken nextToken = next();
      switch (nextToken.getToken()) {
      case WORD:
      case PHRASE:
        exp = processImplicitAnd(exp, nextToken);
        break;
      case AND:
        exp = processAnd(exp);
        break;
      case OR:
        exp = processOr(exp);
        break;
      default:
        break;
      }
    }
    return exp;
  }

  private SearchExpression processOr(SearchExpression left) throws SearchParserException {
    SearchBinaryImpl or = new SearchBinaryImpl(SearchBinaryOperatorKind.OR);
    or.setLeft(left);
    SearchQueryToken nextToken = next();
    switch (nextToken.getToken()) {
    case WORD:
    case PHRASE:
      or.setRight(processWord(null, nextToken.getLiteral()));
      break;
    default:
      break;
    }
    return or;
  }

  private SearchExpression processAnd(SearchExpression left) throws SearchParserException {
    SearchBinaryImpl and = new SearchBinaryImpl(SearchBinaryOperatorKind.AND);
    and.setLeft(left);
    SearchQueryToken nextToken = next();
    switch (nextToken.getToken()) {
    case WORD:
    case PHRASE:
      and.setRight(processWord(null, nextToken.getLiteral()));
      break;
    default:
      break;
    }
    return and;
  }

  private SearchExpression processImplicitAnd(SearchExpression left, SearchQueryToken nextToken)
      throws SearchParserException {
    SearchBinaryImpl and = new SearchBinaryImpl(SearchBinaryOperatorKind.AND);
    and.setLeft(left);
    and.setRight(new SearchTermImpl(nextToken.getLiteral()));
    return and;
  }

  private SearchQueryToken next() throws SearchParserException {
    currentPosition++;
    if (currentPosition < size) {
      return tokens.get(currentPosition);
    } else {
      throw new SearchParserException();
    }
  }

  private boolean hasNext() {
    return currentPosition + 1 < size;
  }
}
