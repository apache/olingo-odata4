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

import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.AND;
import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.CLOSE;
import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.NOT;
import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.OPEN;
import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.OR;
import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.PHRASE;
import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.WORD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SearchTokenizerTest {

  @Test
  public void parseBasics() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;
    
    //
    result = tokenizer.tokenize("abc");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());

    result = tokenizer.tokenize("NOT abc");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(NOT, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());

    result = tokenizer.tokenize("(abc)");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(OPEN, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());
    Assert.assertEquals(CLOSE, result.get(2).getToken());

    result = tokenizer.tokenize("((abc))");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(OPEN, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());
    Assert.assertEquals(CLOSE, result.get(4).getToken());
  }

  @Test
  public void parseWords() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    //
    result = tokenizer.tokenize("abc");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());

    //
    result = tokenizer.tokenize("anotherWord\u1234");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
  }

  @Test
  public void parsePhrase() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    SearchValidator.init("abc AND \"x-y_z\" AND olingo").validate();

    //
    result = tokenizer.tokenize("\"abc\"");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(PHRASE, result.get(0).getToken());

    //
    result = tokenizer.tokenize("\"9988  abs\"");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(PHRASE, result.get(0).getToken());
    Assert.assertEquals("\"9988  abs\"", result.get(0).getLiteral());

    //
    result = tokenizer.tokenize("\"99_88.\"");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(PHRASE, result.get(0).getToken());
    Assert.assertEquals("\"99_88.\"", result.get(0).getLiteral());

    SearchValidator.init("abc or \"xyz\"").addExpected(WORD, WORD, PHRASE).validate();
  }

  /**
   * https://tools.oasis-open.org/version-control/browse/wsvn/odata/trunk/spec/ABNF/odata-abnf-testcases.xml
   * @throws Exception
   */
  @Test
  @Ignore("Test must be moved to SearchParserTest and SearchParserAndTokenizerTest")
  public void parsePhraseAbnfTestcases() throws Exception {
    //    <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    SearchValidator.init("\"blue%20green\"").validate();
    //    <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    SearchValidator.init("\"blue%20green%22").validate();
    //    <TestCase Name="5.1.7 Search - phrase with escaped double-quote" Rule="queryOptions">
    //    <Input>$search="blue\"green"</Input>
    SearchValidator.init("\"blue\\\"green\"").validate();

    //    <TestCase Name="5.1.7 Search - phrase with escaped backslash" Rule="queryOptions">
    //    <Input>$search="blue\\green"</Input>
    SearchValidator.init("\"blue\\\\green\"").validate();

    //    <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="14">
    SearchValidator.init("\"blue\"green\"").validate();

    //    <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="16">
    SearchValidator.init("\"blue%22green\"").validate();

//    <TestCase Name="5.1.7 Search - implicit AND" Rule="queryOptions">
//    <Input>$search=blue green</Input>
//    SearchValidator.init("\"blue%20green\"").validate();
    //    <TestCase Name="5.1.7 Search - implicit AND, encoced" Rule="queryOptions">
//    SearchValidator.init("blue%20green").validate();
  }


    @Test
  public void parseNot() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("NOT abc");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(NOT, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());

    SearchValidator.init("not abc").addExpected(WORD, WORD).validate();
    SearchValidator.init("NOT    abc").addExpected(NOT, WORD).validate();
    SearchValidator.init("NOT    \"abc\"").addExpected(NOT, PHRASE).validate();
    SearchValidator.init("NOT (sdf)").validate(SearchTokenizerException.class);
  }

  @Test
  public void parseOr() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("abc OR xyz");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(OR, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());

    result = tokenizer.tokenize("abc OR xyz OR olingo");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(OR, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());
    Assert.assertEquals(OR, result.get(3).getToken());
    Assert.assertEquals(WORD, result.get(4).getToken());

    SearchValidator.init("abc or xyz").addExpected(WORD, WORD, WORD).validate();
  }

  @Test
  public void parseImplicitAnd() throws SearchTokenizerException {
    SearchValidator.init("a b").addExpected(WORD, WORD).validate();
    SearchValidator.init("a b OR c").addExpected(WORD, WORD, OR, WORD).validate();
    SearchValidator.init("a bc OR c").addExpected(WORD, WORD, OR, WORD).validate();
    SearchValidator.init("a bc c").addExpected(WORD, WORD, WORD).validate();
    SearchValidator.init("(a OR x) bc c").addExpected(OPEN, WORD, OR, WORD, CLOSE, WORD, WORD).validate();
  }

  @Test
  public void parseAnd() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("abc AND xyz");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());

    // no lower case allowed for AND
    result = tokenizer.tokenize("abc and xyz");
    Assert.assertNotNull(result);
    Assert.assertEquals(3, result.size());
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());

    // implicit AND
    result = tokenizer.tokenize("abc xyz");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());

    result = tokenizer.tokenize("abc AND xyz AND olingo");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());
    Assert.assertEquals(AND, result.get(3).getToken());
    Assert.assertEquals(WORD, result.get(4).getToken());

    result = tokenizer.tokenize("abc AND \"x-y_z\" AND olingo");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(PHRASE, result.get(2).getToken());
    Assert.assertEquals("\"x-y_z\"", result.get(2).getLiteral());
    Assert.assertEquals(AND, result.get(3).getToken());
    Assert.assertEquals(WORD, result.get(4).getToken());
  }

  @Test
  public void parseAndOr() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("abc AND xyz OR olingo");
    Assert.assertNotNull(result);
    
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());
    Assert.assertEquals(OR, result.get(3).getToken());
    Assert.assertEquals(WORD, result.get(4).getToken());

    SearchValidator.init("abc AND ANDsomething")
        .addExpected(WORD, AND, WORD).validate();
  }


  @Test
  public void parseCombinations() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("abc AND NOT xyz OR olingo");
    Assert.assertNotNull(result);
    
    Iterator<SearchQueryToken> it = result.iterator();
    Assert.assertEquals(WORD, it.next().getToken());
    Assert.assertEquals(AND, it.next().getToken());
    Assert.assertEquals(NOT, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());
    Assert.assertEquals(OR, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());

    SearchValidator.init("foo AND bar OR foo AND baz OR that AND bar OR that AND baz")
        .addExpected(WORD, "foo").addExpected(AND)
        .addExpected(WORD, "bar").addExpected(OR)
        .addExpected(WORD, "foo").addExpected(AND)
        .addExpected(WORD, "baz").addExpected(OR)
        .addExpected(WORD, "that").addExpected(AND)
        .addExpected(WORD, "bar").addExpected(OR)
        .addExpected(WORD, "that").addExpected(AND)
        .addExpected(WORD, "baz")
        .validate();


    SearchValidator.init("(foo OR that) AND (bar OR baz)")
        .addExpected(OPEN)
        .addExpected(WORD, "foo").addExpected(OR).addExpected(WORD, "that")
        .addExpected(CLOSE).addExpected(AND).addExpected(OPEN)
        .addExpected(WORD, "bar").addExpected(OR).addExpected(WORD, "baz")
        .addExpected(CLOSE)
        .validate();
  }


  @Test
  public void parseSpecial() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;
    Iterator<SearchQueryToken> it;

    result = tokenizer.tokenize("NOT abc AND nothing");
    
    it = result.iterator();
    Assert.assertEquals(NOT, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());
    Assert.assertEquals(AND, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());

    result = tokenizer.tokenize("abc AND andsomething");
    
    it = result.iterator();
    Assert.assertEquals(WORD, it.next().getToken());
    Assert.assertEquals(AND, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());

    SearchValidator.init("abc AND ANDsomething")
        .addExpected(WORD, AND, WORD).validate();

    SearchValidator.init("abc ANDsomething")
        .addExpected(WORD, WORD).validate();

    SearchValidator.init("abc ORsomething")
        .addExpected(WORD, WORD).validate();

    SearchValidator.init("abc OR orsomething")
        .addExpected(WORD, OR, WORD).validate();

    SearchValidator.init("abc OR ORsomething")
        .addExpected(WORD, OR, WORD).validate();
  }

  @Ignore
  @Test
  public void unicodeInWords() throws Exception {
    // Ll, Lm, Lo, Lt, Lu, Nl
    SearchValidator.init("abc OR Ll\u01E3Lm\u02B5Lo\u1BE4Lt\u01F2Lu\u03D3Nl\u216F")
        .addExpected(WORD, OR, WORD).validate();
  }

  /**
   * unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
   * other-delims   = "!" /                   "(" / ")" / "*" / "+" / "," / ";"
   * qchar-unescaped       = unreserved / pct-encoded-unescaped / other-delims / ":" / "@" / "/" / "?" / "$" / "'" / "="
   * pct-encoded-unescaped = "%" ( "0" / "1" /   "3" / "4" /   "6" / "7" / "8" / "9" / A-to-F ) HEXDIG
   *   / "%" "2" ( "0" / "1" /   "3" / "4" / "5" / "6" / "7" / "8" / "9" / A-to-F )
   *   / "%" "5" ( DIGIT / "A" / "B" /   "D" / "E" / "F" )
   *
   * qchar-no-AMP-DQUOTE   = qchar-unescaped  / escape ( escape / quotation-mark )
   *
   * escape = "\" / "%5C"     ; reverse solidus U+005C
   * quotation-mark  = DQUOTE / "%22"
   * ALPHA  = %x41-5A / %x61-7A
   * DIGIT  = %x30-39
   * DQUOTE = %x22
   *
   * @throws Exception
   */
  @Test
  public void characterInPhrase() throws Exception {
    SearchValidator.init("\"123\" OR \"ALPHA-._~\"")
        .addExpected(PHRASE, OR, PHRASE).validate();
  }

  @Test
  public void moreMixedTests() throws SearchTokenizerException {
    validate("abc");
    validate("NOT abc");

    validate("abc AND def");
    validate("abc  OR def");
    validate("abc     def", WORD, WORD);

    validate("abc AND def AND ghi", WORD, AND, WORD, AND, WORD);
    validate("abc AND def  OR ghi");
    validate("abc AND def     ghi");

    validate("abc  OR def AND ghi", WORD, OR, WORD, AND, WORD);
    validate("abc  OR def  OR ghi", WORD, OR, WORD, OR, WORD);
    validate("abc  OR def     ghi", WORD, OR, WORD, WORD);

    validate("abc     def AND ghi");
    validate("abc     def  OR ghi");
    validate("abc     def     ghi");

    // mixed not
    SearchValidator.init("    abc         def AND     ghi").validate(WORD, WORD, AND, WORD);
    validate("NOT abc  NOT    def  OR NOT ghi", NOT, WORD, NOT, WORD, OR, NOT, WORD);
    validate("    abc         def     NOT ghi", WORD, WORD, NOT, WORD);

    // parenthesis
    validate("(abc)", OPEN, WORD, CLOSE);
    validate("(abc AND  def)", OPEN, WORD, AND, WORD, CLOSE);
    validate("(abc AND  def)   OR  ghi", OPEN, WORD, AND, WORD, CLOSE, OR, WORD);
    validate("(abc AND  def)       ghi", OPEN, WORD, AND, WORD, CLOSE, WORD);
    validate("abc AND (def    OR  ghi)", WORD, AND, OPEN, WORD, OR, WORD, CLOSE);
    validate("abc AND (def        ghi)", WORD, AND, OPEN, WORD, WORD, CLOSE);
  }

  @Test
  public void parseInvalid() throws SearchTokenizerException {
    SearchValidator.init("abc AND OR something").validate();
    SearchValidator.init("abc AND \"something\" )").validate();
    //
    SearchValidator.init("(  abc AND) OR something").validate(SearchTokenizerException.class);
  }

  public void validate(String query) throws SearchTokenizerException {
    new SearchValidator(query).validate();
  }

  public void validate(String query, SearchQueryToken.Token ... tokens) throws SearchTokenizerException {
    SearchValidator sv = new SearchValidator(query);
    for (SearchQueryToken.Token token : tokens) {
      sv.addExpected(token);
    }
    sv.validate();
  }

  private static class SearchValidator {
    private List<Tuple> validations = new ArrayList<Tuple>();
    private boolean log;
    private final String searchQuery;

    public void validate(SearchQueryToken.Token... tokens) throws SearchTokenizerException {
      addExpected(tokens);
      validate();
    }

    private class Tuple {
      final SearchQueryToken.Token token;
      final String literal;
      public Tuple(SearchQueryToken.Token token, String literal) {
        this.token = token;
        this.literal = literal;
      }
      public Tuple(SearchQueryToken.Token token) {
        this(token, null);
      }
    }

    private SearchValidator(String searchQuery) {
      this.searchQuery = searchQuery;
    }

    private static SearchValidator init(String searchQuery) {
      return new SearchValidator(searchQuery);
    }
    
    @SuppressWarnings("unused")
    private SearchValidator enableLogging() {
      log = true;
      return this;
    }
    private SearchValidator addExpected(SearchQueryToken.Token token, String literal) {
      validations.add(new Tuple(token, literal));
      return this;
    }
    private SearchValidator addExpected(SearchQueryToken.Token ... token) {
      for (SearchQueryToken.Token t : token) {
        validations.add(new Tuple(t));
      }
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

    private void validate() throws SearchTokenizerException {
      SearchTokenizer tokenizer = new SearchTokenizer();
      List<SearchQueryToken> result = tokenizer.tokenize(searchQuery);
      Assert.assertNotNull(result);
      if(log) {
        System.out.println(result);
      }
      if(validations.size() != 0) {
        Assert.assertEquals(validations.size(), result.size());

        Iterator<Tuple> validationIt = validations.iterator();
        for (SearchQueryToken iToken : result) {
          Tuple validation = validationIt.next();
          Assert.assertEquals(validation.token, iToken.getToken());
          if(validation.literal != null) {
            Assert.assertEquals(validation.literal, iToken.getLiteral());
          }
        }
      }
    }
  }
}