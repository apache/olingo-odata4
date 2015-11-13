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

import static org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind.AND;
import static org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind.OR;

import java.lang.reflect.Field;

import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.apache.olingo.server.api.uri.queryoption.search.SearchUnary;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SearchParserAndTokenizerTest {

  @Test
  public void basicParsing() throws SearchTokenizerException {
    SearchExpressionValidator.init("a")
        .validate(with("a"));
    SearchExpressionValidator.init("a AND b")
        .validate(with("a", and("b")));
    SearchExpressionValidator.init("a AND b AND c")
        .validate("{{'a' AND 'b'} AND 'c'}");
    SearchExpressionValidator.init("a OR b")
        .validate(with("a", or("b")));
    SearchExpressionValidator.init("a OR b OR c")
        .validate(with("a", or("b", or("c"))));
  }

  @Test
  public void mixedParsing() throws Exception {
    SearchExpressionValidator.init("a AND b OR c")
        .validate("{{'a' AND 'b'} OR 'c'}");
    SearchExpressionValidator.init("a OR b AND c")
        .validate("{'a' OR {'b' AND 'c'}}");
  }

  @Test
  public void notParsing() throws Exception {
    SearchExpressionValidator.init("NOT a AND b OR c")
        .validate("{{{NOT 'a'} AND 'b'} OR 'c'}");
    SearchExpressionValidator.init("a OR b AND NOT c")
        .validate("{'a' OR {'b' AND {NOT 'c'}}}");
  }

  @Test
  public void parenthesesParsing() throws Exception {
    SearchExpressionValidator.init("a AND (b OR c)")
        .validate("{'a' AND {'b' OR 'c'}}");
    SearchExpressionValidator.init("(a OR b) AND NOT c")
        .validate("{{'a' OR 'b'} AND {NOT 'c'}}");
  }

  @Ignore
  @Test
  public void sebuilder() {
    System.out.println(with("c", or("a", and("b"))).toString());
    System.out.println(with("a", and("b", and("c"))).toString());
    System.out.println(with("a").toString());
    System.out.println(with(not("a")).toString());
    System.out.println(with("a", and("b")).toString());
    System.out.println(with("a", or("b")).toString());
    System.out.println(with("a", and(not("b"))).toString());
  }

  private static SearchExpression with(String term) {
    return new SearchTermImpl(term);
  }

  private static SearchExpression with(String left, SearchExpression right) {
    setLeftField(left, right);
    return right;
  }

  private static SearchUnary with(SearchUnary unary) {
    return unary;
  }

  private static SearchExpression or(String left, SearchExpression right) {
    SearchExpression or = or(right);
    setLeftField(left, right);
    return or;
  }

  private static SearchExpression and(String left, SearchExpression right) {
    SearchExpression and = and(right);
    setLeftField(left, right);
    return and;
  }

  private static SearchExpression or(SearchExpression right) {
    return new SearchBinaryImpl(null, OR, right);
  }

  private static SearchExpression and(SearchExpression right) {
    return new SearchBinaryImpl(null, AND, right);
  }

  private static SearchExpression and(String right) {
    return and(new SearchTermImpl(right));
  }

  private static SearchExpression or(String right) {
    return or(new SearchTermImpl(right));
  }

  private static SearchUnary not(String term) {
    return new SearchUnaryImpl(new SearchTermImpl(term));
  }

  private static void setLeftField(String left, SearchExpression se) {
    try {
      Field field = null;
      if (se instanceof SearchUnaryImpl) {
        field = SearchBinaryImpl.class.getDeclaredField("operand");
      } else if (se instanceof SearchBinaryImpl) {
        field = SearchBinaryImpl.class.getDeclaredField("left");
      } else {
        Assert.fail("Unexpected exception: " + se.getClass());
      }
      field.setAccessible(true);
      field.set(se, new SearchTermImpl(left));
    } catch (Exception e) {
      Assert.fail("Unexpected exception: " + e.getClass());
    }
  }

  private static class SearchExpressionValidator {
    private boolean log;
    private final String searchQuery;

    private SearchExpressionValidator(String searchQuery) {
      this.searchQuery = searchQuery;
    }

    private static SearchExpressionValidator init(String searchQuery) {
      return new SearchExpressionValidator(searchQuery);
    }

    @SuppressWarnings("unused")
    private SearchExpressionValidator enableLogging() {
      log = true;
      return this;
    }

    private void validate(Class<? extends Exception> exception) throws SearchTokenizerException {
      try {
        new SearchTokenizer().tokenize(searchQuery);
      } catch (Exception e) {
        Assert.assertEquals(exception, e.getClass());
        return;
      }
      Assert.fail("Expected exception " + exception.getClass().getSimpleName() + " was not thrown.");
    }

    private void validate(SearchExpression expectedSearchExpression) throws SearchTokenizerException {
      final SearchExpression searchExpression = getSearchExpression();
      Assert.assertEquals(expectedSearchExpression.toString(), searchExpression.toString());
    }

    private void validate(String expectedSearchExpression) throws SearchTokenizerException {
      final SearchExpression searchExpression = getSearchExpression();
      Assert.assertEquals(expectedSearchExpression, searchExpression.toString());
    }

    private SearchExpression getSearchExpression() {
      SearchParser tokenizer = new SearchParser();
      SearchOption result = tokenizer.parse(null, searchQuery);
      Assert.assertNotNull(result);
      final SearchExpression searchExpression = result.getSearchExpression();
      Assert.assertNotNull(searchExpression);
      if (log) {
        System.out.println(searchExpression);
      }
      return searchExpression;
    }
  }

  
}
