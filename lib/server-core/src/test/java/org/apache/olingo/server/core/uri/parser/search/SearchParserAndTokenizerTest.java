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

import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.junit.Assert;
import org.junit.Test;

public class SearchParserAndTokenizerTest {

  @Test
  public void basicParsing() throws Exception {
    assertQuery("\"99\"").resultsIn("'99'");
    assertQuery("a").resultsIn("'a'");
    assertQuery("a AND b").resultsIn("{'a' AND 'b'}");
    assertQuery("a AND b AND c").resultsIn("{{'a' AND 'b'} AND 'c'}");
    assertQuery("a OR b").resultsIn("{'a' OR 'b'}");
    assertQuery("a OR b OR c").resultsIn("{'a' OR {'b' OR 'c'}}");
  }

  @Test
  public void mixedParsing() throws Exception {
    assertQuery("a AND b OR c").resultsIn("{{'a' AND 'b'} OR 'c'}");
    assertQuery("a OR b AND c").resultsIn("{'a' OR {'b' AND 'c'}}");
  }

  @Test
  public void notParsing() throws Exception {
    assertQuery("NOT a AND b OR c").resultsIn("{{{NOT 'a'} AND 'b'} OR 'c'}");
    assertQuery("a OR b AND NOT c").resultsIn("{'a' OR {'b' AND {NOT 'c'}}}");
  }

  @Test
  public void parenthesesParsing() throws Exception {
    assertQuery("a AND (b OR c)").resultsIn("{'a' AND {'b' OR 'c'}}");
    assertQuery("(a OR b) AND NOT c").resultsIn("{{'a' OR 'b'} AND {NOT 'c'}}");
  }

  @Test
  public void parseImplicitAnd() throws Exception {
    assertQuery("a b").resultsIn("{'a' AND 'b'}");
    assertQuery("a b c").resultsIn("{'a' AND {'b' AND 'c'}}");
    assertQuery("a and b").resultsIn("{'a' AND {'and' AND 'b'}}");
    assertQuery("a b OR c").resultsIn("{{'a' AND 'b'} OR 'c'}");
    assertQuery("a \"bc123\" OR c").resultsIn("{{'a' AND 'bc123'} OR 'c'}");
    assertQuery("(a OR x) bc c").resultsIn("{{'a' OR 'x'} AND {'bc' AND 'c'}}");
    assertQuery("one ((a OR x) bc c)").resultsIn("{'one' AND {{'a' OR 'x'} AND {'bc' AND 'c'}}}");
  }

  @Test
  public void invalidSearchQuery() throws Exception {
    assertQuery("99").resultsIn(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    assertQuery("NOT").resultsIn(SearchParserException.MessageKeys.INVALID_NOT_OPERAND);
    assertQuery("AND").resultsIn(SearchParserException.MessageKeys.INVALID_BINARY_OPERATOR_POSITION);
    assertQuery("OR").resultsIn(SearchParserException.MessageKeys.INVALID_BINARY_OPERATOR_POSITION);
  }

  private static Validator assertQuery(String searchQuery) {
    return Validator.init(searchQuery);
  }

  private static class Validator {
    private boolean log;
    private final String searchQuery;

    private Validator(String searchQuery) {
      this.searchQuery = searchQuery;
    }

    private static Validator init(String searchQuery) {
      return new Validator(searchQuery);
    }

    @SuppressWarnings("unused")
    private Validator withLogging() {
      log = true;
      return this;
    }

    private void resultsIn(ODataLibraryException.MessageKey key)
            throws SearchTokenizerException {
      try {
        resultsIn(searchQuery);
      } catch (ODataLibraryException e) {
        Assert.assertEquals(SearchParserException.class, e.getClass());
        Assert.assertEquals(key, e.getMessageKey());
        return;
      }
      Assert.fail("SearchParserException with message key " + key.getKey() + " was not thrown.");
    }

    private void resultsIn(String expectedSearchExpression) throws SearchTokenizerException, SearchParserException {
      final SearchExpression searchExpression = getSearchExpression();
      Assert.assertEquals(expectedSearchExpression, searchExpression.toString());
    }

    private SearchExpression getSearchExpression() throws SearchParserException, SearchTokenizerException {
      SearchParser tokenizer = new SearchParser();
      SearchOption result = tokenizer.parse(searchQuery);
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
