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
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;
import org.junit.Assert;
import org.junit.Test;

public class SearchParserAndTokenizerTest {

  private static final String EOF = "<EOF>";

  @Test
  public void basicParsing() throws Exception {
    assertQuery("\"99\"").resultsIn("'99'");
    assertQuery("a").resultsIn("'a'");
    assertQuery("a AND b").resultsIn("{'a' AND 'b'}");
    assertQuery("a AND b AND c").resultsIn("{{'a' AND 'b'} AND 'c'}");
    assertQuery("a OR b").resultsIn("{'a' OR 'b'}");
    assertQuery("a OR b OR c").resultsIn("{{'a' OR 'b'} OR 'c'}");

    assertQuery("NOT a NOT b").resultsIn("{{NOT 'a'} AND {NOT 'b'}}");
    assertQuery("NOT a AND NOT b").resultsIn("{{NOT 'a'} AND {NOT 'b'}}");
    assertQuery("NOT a OR NOT b").resultsIn("{{NOT 'a'} OR {NOT 'b'}}");
    assertQuery("NOT a OR NOT b NOT C").resultsIn("{{NOT 'a'} OR {{NOT 'b'} AND {NOT 'C'}}}");
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
    assertQuery("(a OR B) AND (c OR d AND NOT e OR (f))")
        .resultsIn("{{'a' OR 'B'} AND {{'c' OR {'d' AND {NOT 'e'}}} OR 'f'}}");
    assertQuery("(a OR B) (c OR d NOT e OR (f))")
        .resultsIn("{{'a' OR 'B'} AND {{'c' OR {'d' AND {NOT 'e'}}} OR 'f'}}");
    assertQuery("((((a))))").resultsIn("'a'");
    assertQuery("((((a)))) ((((a))))").resultsIn("{'a' AND 'a'}");
    assertQuery("((((a)))) OR ((((a))))").resultsIn("{'a' OR 'a'}");
    assertQuery("((((((a)))) ((((c))) OR (((C)))) ((((a))))))").resultsIn("{{'a' AND {'c' OR 'C'}} AND 'a'}");
    assertQuery("((((\"a\")))) OR ((((\"a\"))))").resultsIn("{'a' OR 'a'}");
  }

  @Test
  public void parseImplicitAnd() throws Exception {
    assertQuery("a b").resultsIn("{'a' AND 'b'}");
    assertQuery("a b c").resultsIn("{{'a' AND 'b'} AND 'c'}");
    assertQuery("a and b").resultsIn("{{'a' AND 'and'} AND 'b'}");
    assertQuery("hey ANDy warhol").resultsIn("{{'hey' AND 'ANDy'} AND 'warhol'}");
    assertQuery("a b OR c").resultsIn("{{'a' AND 'b'} OR 'c'}");
    assertQuery("a \"bc123\" OR c").resultsIn("{{'a' AND 'bc123'} OR 'c'}");
    assertQuery("(a OR x) bc c").resultsIn("{{{'a' OR 'x'} AND 'bc'} AND 'c'}");
    assertQuery("one ((a OR x) bc c)").resultsIn("{'one' AND {{{'a' OR 'x'} AND 'bc'} AND 'c'}}");
  }

  @Test
  public void invalidSearchQuery() throws Exception {
    assertQuery("99").resultsIn(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    assertQuery("NOT").resultsIn(SearchParserException.MessageKeys.INVALID_NOT_OPERAND);
    assertQuery("AND").resultsInExpectedTerm(SearchQueryToken.Token.AND.name());
    assertQuery("OR").resultsInExpectedTerm(SearchQueryToken.Token.OR.name());

    assertQuery("NOT a AND").resultsInExpectedTerm(EOF);
    assertQuery("NOT a OR").resultsInExpectedTerm(EOF);
    assertQuery("a AND").resultsInExpectedTerm(EOF);
    assertQuery("a OR").resultsInExpectedTerm(EOF);

    assertQuery("a OR b)").resultsIn(SearchParserException.MessageKeys.INVALID_END_OF_QUERY);
    assertQuery("a NOT b)").resultsIn(SearchParserException.MessageKeys.INVALID_END_OF_QUERY);
    assertQuery("a AND b)").resultsIn(SearchParserException.MessageKeys.INVALID_END_OF_QUERY);

    assertQuery("(a OR b").resultsIn(SearchParserException.MessageKeys.MISSING_CLOSE);
    assertQuery("(a NOT b").resultsIn(SearchParserException.MessageKeys.MISSING_CLOSE);
    assertQuery("((a AND b)").resultsIn(SearchParserException.MessageKeys.MISSING_CLOSE);
    assertQuery("((a AND b OR c)").resultsIn(SearchParserException.MessageKeys.MISSING_CLOSE);
    assertQuery("a AND (b OR c").resultsIn(SearchParserException.MessageKeys.MISSING_CLOSE);
    assertQuery("(a AND ((b OR c)").resultsIn(SearchParserException.MessageKeys.MISSING_CLOSE);

    assertQuery("NOT NOT a").resultsIn(SearchParserException.MessageKeys.INVALID_NOT_OPERAND);
    assertQuery("NOT (a)").resultsIn(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
  }

  /**
   * Test all test cases from:
   * https://tools.oasis-open.org/version-control/browse/wsvn/odata/trunk/spec/ABNF/odata-abnf-testcases.xml
   *
   * However the parser prerequisites that the search query is already percent decoded.
   * Hence all "%xx" values are replaced by their decoded value.
   *
   * @throws Exception
   */
  @Test
  public void searchQueryPhraseAbnfTestcases() throws Exception {
    // <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    assertQuery("\"blue green\"").resultsIn("'blue green'");
    // <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    assertQuery("\"blue green\"").resultsIn("'blue green'");
    // <TestCase Name="5.1.7 Search - phrase with escaped double-quote" Rule="queryOptions">
    // <Input>$search="blue\"green"</Input>
    assertQuery("\"blue\\\"green\"").resultsIn("'blue\"green'");

    // <TestCase Name="5.1.7 Search - phrase with escaped backslash" Rule="queryOptions">
    // <Input>$search="blue\\green"</Input>
    assertQuery("\"blue\\\\green\"").resultsIn("'blue\\green'");
    // <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="14">
    assertQuery("\"blue\"green\"").resultsIn(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    // <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="16">
    assertQuery("\"blue\"green\"").resultsIn(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);

    // <TestCase Name="5.1.7 Search - implicit AND" Rule="queryOptions">
    // <Input>$search=blue green</Input>
    assertQuery("blue green").resultsIn("{'blue' AND 'green'}");
    // <TestCase Name="5.1.7 Search - implicit AND, encoced" Rule="queryOptions">
    assertQuery("blue green").resultsIn("{'blue' AND 'green'}");

    // <TestCase Name="5.1.7 Search - AND" Rule="queryOptions">
    // <Input>$search=blue AND green</Input>
    assertQuery("blue AND green").resultsIn("{'blue' AND 'green'}");

    // <TestCase Name="5.1.7 Search - OR" Rule="queryOptions">
    // <Input>$search=blue OR green</Input>
    assertQuery("blue OR green").resultsIn("{'blue' OR 'green'}");

    // <TestCase Name="5.1.7 Search - NOT" Rule="queryOptions">
    // <Input>$search=blue NOT green</Input>
    assertQuery("blue NOT green").resultsIn("{'blue' AND {NOT 'green'}}");

    // <TestCase Name="5.1.7 Search - only NOT" Rule="queryOptions">
    // <Input>$search=NOT blue</Input>
    assertQuery("NOT blue").resultsIn("{NOT 'blue'}");

    // <TestCase Name="5.1.7 Search - multiple" Rule="queryOptions">
    // <Input>$search=foo AND bar OR foo AND baz OR that AND bar OR that AND baz</Input>
    assertQuery("foo AND bar OR foo AND baz OR that AND bar OR that AND baz")
        .resultsIn("{{{{'foo' AND 'bar'} OR {'foo' AND 'baz'}} OR {'that' AND 'bar'}} OR {'that' AND 'baz'}}");

    // <TestCase Name="5.1.7 Search - multiple" Rule="queryOptions">
    // <Input>$search=(foo OR that) AND (bar OR baz)</Input>
    assertQuery("(foo OR that) AND (bar OR baz)").resultsIn("{{'foo' OR 'that'} AND {'bar' OR 'baz'}}");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=foo AND (bar OR baz)</Input>
    assertQuery("foo AND (bar OR baz)").resultsIn("{'foo' AND {'bar' OR 'baz'}}");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(foo AND bar) OR baz</Input>
    assertQuery("(foo AND bar) OR baz").resultsIn("{{'foo' AND 'bar'} OR 'baz'}");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(NOT foo) OR baz</Input>
    assertQuery("(NOT foo) OR baz").resultsIn("{{NOT 'foo'} OR 'baz'}");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(NOT foo)</Input>
    assertQuery("(NOT foo)").resultsIn("{NOT 'foo'}");

    // <TestCase Name="5.1.7 Search - on entity set" Rule="odataUri">
    // <Input>http://serviceRoot/Products?$search=blue</Input>
    assertQuery("blue").resultsIn("'blue'");

    // below cases can not be tested here
    // <TestCase Name="5.1.7 Search - on entity container" Rule="odataUri">
    // <Input>http://serviceRoot/Model.Container/$all?$search=blue</Input>
    // <TestCase Name="5.1.7 Search - on service" Rule="odataUri">
    // <Input>http://serviceRoot/$all?$search=blue</Input>
  }

  private static Validator assertQuery(final String searchQuery) {
    return new Validator(searchQuery);
  }

  private static class Validator {
    private final String searchQuery;

    private Validator(final String searchQuery) {
      this.searchQuery = searchQuery;
    }

    private void resultsIn(final SearchParserException.MessageKey key) throws SearchTokenizerException {
      try {
        resultsIn(searchQuery);
      } catch (final SearchParserException e) {
        Assert.assertEquals("SearchParserException with unexpected message '" + e.getMessage() + "' was thrown.",
            key, e.getMessageKey());
        return;
      }
      Assert.fail("SearchParserException with message key " + key.getKey() + " was not thrown.");
    }

    public void resultsInExpectedTerm(final String actualToken) throws SearchTokenizerException {
      try {
        resultsIn(searchQuery);
      } catch (final SearchParserException e) {
        Assert.assertEquals(SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN, e.getMessageKey());
        Assert.assertEquals("Expected PHRASE||WORD found: " + actualToken, e.getMessage());
        return;
      }
      Assert.fail("SearchParserException with message key "
          + SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN + " was not thrown.");
    }

    private void resultsIn(final String expectedSearchExpression)
        throws SearchTokenizerException, SearchParserException {
      final SearchOption result = new SearchParser().parse(searchQuery);
      Assert.assertNotNull(result);
      final SearchExpression searchExpression = result.getSearchExpression();
      Assert.assertNotNull(searchExpression);
      Assert.assertEquals(expectedSearchExpression, searchExpression.toString());
    }
  }
}
