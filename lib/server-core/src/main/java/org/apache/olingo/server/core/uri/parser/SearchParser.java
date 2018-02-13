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
package org.apache.olingo.server.core.uri.parser;

import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.apache.olingo.server.api.uri.queryoption.search.SearchTerm;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.apache.olingo.server.core.uri.parser.search.SearchBinaryImpl;
import org.apache.olingo.server.core.uri.parser.search.SearchParserException;
import org.apache.olingo.server.core.uri.parser.search.SearchTermImpl;
import org.apache.olingo.server.core.uri.parser.search.SearchUnaryImpl;
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

/**
 * Parses search expressions according to the following (rewritten) grammar:
 * <pre>
 * SearchExpr  ::= ExprOR
 * ExprOR      ::= ExprAnd ('OR' ExprAnd)*
 * ExprAnd     ::= Term ('AND'? Term)*
 * Term        ::= ('NOT'? (Word | Phrase)) | ('(' SearchExpr ')')
 * </pre> 
 */
public class SearchParser {

  public SearchOption parse(UriTokenizer tokenizer) throws SearchParserException {
    SearchOptionImpl searchOption = new SearchOptionImpl();
    searchOption.setSearchExpression(processExprOr(tokenizer));
    return searchOption;
  }

  private SearchExpression processExprOr(UriTokenizer tokenizer) throws SearchParserException {
    SearchExpression left = processExprAnd(tokenizer);

    while (tokenizer.next(TokenKind.OrOperatorSearch)) {
      final SearchExpression right = processExprAnd(tokenizer);
      left = new SearchBinaryImpl(left, SearchBinaryOperatorKind.OR, right);
    }

    return left;
  }

  private SearchExpression processExprAnd(UriTokenizer tokenizer) throws SearchParserException {
    SearchExpression left = processTerm(tokenizer);

    while (tokenizer.next(TokenKind.AndOperatorSearch)) { 
      // Could be whitespace or whitespace-surrounded 'AND'.
      final SearchExpression right = processTerm(tokenizer);
      left = new SearchBinaryImpl(left, SearchBinaryOperatorKind.AND, right);
    }

    return left;
  }

  private SearchExpression processTerm(UriTokenizer tokenizer) throws SearchParserException {
    if (tokenizer.next(TokenKind.OPEN)) {
      ParserHelper.bws(tokenizer);
      final SearchExpression expr = processExprOr(tokenizer);
      ParserHelper.bws(tokenizer);
      if (!tokenizer.next(TokenKind.CLOSE)) {
        throw new SearchParserException("Missing close parenthesis after open parenthesis.",
            SearchParserException.MessageKeys.MISSING_CLOSE);
      }
      return expr;
    } else if (tokenizer.next(TokenKind.NotOperatorSearch)) {
      return processNot(tokenizer);
    } else if (tokenizer.next(TokenKind.Word)) {
      return new SearchTermImpl(tokenizer.getText());
    } else if (tokenizer.next(TokenKind.Phrase)) {
      return processPhrase(tokenizer);
    } else {
      throw new SearchParserException("Expected PHRASE or WORD not found.",
          SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN, "PHRASE, WORD", "");
    }
  }

  private SearchExpression processNot(UriTokenizer tokenizer) throws SearchParserException {
    if (tokenizer.next(TokenKind.Word)) {
      return new SearchUnaryImpl(new SearchTermImpl(tokenizer.getText()));
    } else if (tokenizer.next(TokenKind.Phrase)) {
      return new SearchUnaryImpl(processPhrase(tokenizer));
    } else {
      throw new SearchParserException("NOT must be followed by a term.",
          SearchParserException.MessageKeys.INVALID_NOT_OPERAND, "");
    }
  }

  private SearchTerm processPhrase(UriTokenizer tokenizer) {
    final String literal = tokenizer.getText();
    return new SearchTermImpl(literal.substring(1, literal.length() - 1)
        .replace("\\\"", "\"")
        .replace("\\\\", "\\"));
  }
}
