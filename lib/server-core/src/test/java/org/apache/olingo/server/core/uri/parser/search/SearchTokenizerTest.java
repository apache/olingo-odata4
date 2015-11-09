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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.olingo.server.core.uri.parser.search.SearchQueryToken.Token.*;

public class SearchTokenizerTest {

  private boolean logEnabled = false;

  @Test
  public void parseBasics() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    //
    result = tokenizer.tokenize("abc");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());

    result = tokenizer.tokenize("NOT abc");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(NOT, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());

    result = tokenizer.tokenize("(abc)");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(OPEN, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());
    Assert.assertEquals(CLOSE, result.get(2).getToken());

    result = tokenizer.tokenize("((abc))");
    Assert.assertNotNull(result);
    log(result.toString());
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
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());

    //
    result = tokenizer.tokenize("9988abs");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());
  }

  @Test
  public void parsePhrase() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    SearchValidator.init("abc AND \"x-y_z\" AND 123").validate();

    //
    result = tokenizer.tokenize("\"abc\"");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(PHRASE, result.get(0).getToken());

    //
    result = tokenizer.tokenize("\"9988  abs\"");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(PHRASE, result.get(0).getToken());
    Assert.assertEquals("\"9988  abs\"", result.get(0).getLiteral());

    //
    result = tokenizer.tokenize("\"99_88.\"");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(PHRASE, result.get(0).getToken());
    Assert.assertEquals("\"99_88.\"", result.get(0).getLiteral());

    SearchValidator.init("abc or \"xyz\"").addExpected(WORD, AND, WORD, AND, PHRASE).validate();
  }

  @Test
  public void parseNot() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("NOT abc");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(NOT, result.get(0).getToken());
    Assert.assertEquals(WORD, result.get(1).getToken());

    SearchValidator.init("not abc").addExpected(WORD, AND, WORD).validate();
  }

  @Test
  public void parseOr() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("abc OR xyz");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(OR, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());

    result = tokenizer.tokenize("abc OR xyz OR 123");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(OR, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());
    Assert.assertEquals(OR, result.get(3).getToken());
    Assert.assertEquals(WORD, result.get(4).getToken());

    SearchValidator.init("abc or xyz").addExpected(WORD, AND, WORD, AND, WORD).validate();
  }

  @Test
  public void parseImplicitAnd() {
    SearchValidator.init("a b").addExpected(WORD, AND, WORD).validate();
    SearchValidator.init("a b OR c").addExpected(WORD, AND, WORD, OR, WORD).validate();
    SearchValidator.init("a bc OR c").addExpected(WORD, AND, WORD, OR, WORD).validate();
    SearchValidator.init("a bc c").addExpected(WORD, AND, WORD, AND, WORD).validate();
    SearchValidator.init("(a OR x) bc c").addExpected(OPEN, WORD, OR, WORD, CLOSE, AND, WORD, AND, WORD).validate();
  }

  @Test
  public void parseAnd() throws Exception {
    SearchTokenizer tokenizer = new SearchTokenizer();
    List<SearchQueryToken> result;

    result = tokenizer.tokenize("abc AND xyz");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());

    // no lower case allowed for AND
    result = tokenizer.tokenize("abc and xyz");
    Assert.assertNotNull(result);
    Assert.assertEquals(5, result.size());
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());
    Assert.assertEquals(AND, result.get(3).getToken());
    Assert.assertEquals(WORD, result.get(4).getToken());

    // implicit AND
    result = tokenizer.tokenize("abc xyz");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());

    result = tokenizer.tokenize("abc AND xyz AND 123");
    Assert.assertNotNull(result);
    log(result.toString());
    Assert.assertEquals(WORD, result.get(0).getToken());
    Assert.assertEquals(AND, result.get(1).getToken());
    Assert.assertEquals(WORD, result.get(2).getToken());
    Assert.assertEquals(AND, result.get(3).getToken());
    Assert.assertEquals(WORD, result.get(4).getToken());

    result = tokenizer.tokenize("abc AND \"x-y_z\" AND 123");
    Assert.assertNotNull(result);
    log(result.toString());
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

    result = tokenizer.tokenize("abc AND xyz OR 123");
    Assert.assertNotNull(result);
    log(result.toString());
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

    result = tokenizer.tokenize("abc AND NOT xyz OR 123");
    Assert.assertNotNull(result);
    log(result.toString());
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
    log(result.toString());
    it = result.iterator();
    Assert.assertEquals(NOT, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());
    Assert.assertEquals(AND, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());

    result = tokenizer.tokenize("abc AND andsomething");
    log(result.toString());
    it = result.iterator();
    Assert.assertEquals(WORD, it.next().getToken());
    Assert.assertEquals(AND, it.next().getToken());
    Assert.assertEquals(WORD, it.next().getToken());

    SearchValidator.init("abc AND ANDsomething")
        .addExpected(WORD, AND, WORD).validate();

    // FIXME (mibo): issue with implicit and
//    SearchValidator.init("abc ANDsomething").enableLogging()
//        .addExpected(WORD, AND, WORD).validate();

//    SearchValidator.init("abc ORsomething")
//        .addExpected(WORD, AND, WORD).validate();

    SearchValidator.init("abc OR orsomething")
        .addExpected(WORD, OR, WORD).validate();

    SearchValidator.init("abc OR ORsomething")
        .addExpected(WORD, OR, WORD).validate();

  }

  @Test
  public void moreMixedTests() {
    validate("abc");
    validate("NOT abc");

    validate("abc AND def");
    validate("abc  OR def");
    validate("abc     def");

    validate("abc AND def AND ghi", WORD, AND, WORD, AND, WORD);
    validate("abc AND def  OR ghi");
    validate("abc AND def     ghi");

    validate("abc  OR def AND ghi");
    validate("abc  OR def  OR ghi");
    validate("abc  OR def     ghi");

    validate("abc     def AND ghi");
    validate("abc     def  OR ghi");
    validate("abc     def     ghi");

    // mixed not
    validate("    abc         def AND     ghi");
    validate("NOT abc  NOT    def  OR NOT ghi");
    validate("    abc         def     NOT ghi");

    // parenthesis
    validate("(abc)");
    validate("(abc AND  def)");
    validate("(abc AND  def)   OR  ghi");
    validate("(abc AND  def)       ghi");
    validate("abc AND (def    OR  ghi)");
    validate("abc AND (def        ghi)");
  }

  @Test
  public void parseInvalid() {
    SearchValidator.init("abc AND OR something").validate();
  }

  public boolean validate(String query) {
    return new SearchValidator(query).validate();
  }

  public boolean validate(String query, SearchQueryToken.Token ... tokens) {
    SearchValidator sv = new SearchValidator(query);
    for (SearchQueryToken.Token token : tokens) {
      sv.addExpected(token);
    }
    return sv.validate();
  }

  private static class SearchValidator {
    private List<Tuple> validations = new ArrayList<Tuple>();
    private boolean log;
    private final String searchQuery;
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
    private boolean validate() {
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

      return true;
    }
  }



  private void log(Object ... toString) {
    if(logEnabled) {
      System.out.println("------------");
      if(toString == null || toString.length <= 1) {
        System.out.println(toString == null? "NULL": (toString.length == 0? "EMPTY ARRAY": toString[0]));
      } else {
        int count = 1;
        for (Object o : toString) {
          System.out.println(count++ + ": " + o);
        }
      }
    }
  }
}