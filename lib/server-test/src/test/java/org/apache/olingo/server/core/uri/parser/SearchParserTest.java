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

import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.core.uri.parser.search.SearchParserException;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

/** Tests of the parts of the URI parser that parse the sytem query option $search. */
public class SearchParserTest {

  private static final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  private final TestUriValidator testUri = new TestUriValidator().setEdm(edm);

  @Test
  public void search() throws Exception {
    testUri.run("ESTwoKeyNav", "$search=abc");
    testUri.run("ESTwoKeyNav", "$search=NOT abc");

    testUri.run("ESTwoKeyNav", "$search=abc AND def");
    testUri.run("ESTwoKeyNav", "$search=abc  OR def");
    testUri.run("ESTwoKeyNav", "$search=abc     def");

    testUri.run("ESTwoKeyNav", "$search=abc AND def AND ghi");
    testUri.run("ESTwoKeyNav", "$search=abc AND def  OR ghi");
    testUri.run("ESTwoKeyNav", "$search=abc AND def     ghi");

    testUri.run("ESTwoKeyNav", "$search=abc  OR def AND ghi");
    testUri.run("ESTwoKeyNav", "$search=abc  OR def  OR ghi");
    testUri.run("ESTwoKeyNav", "$search=abc  OR def     ghi");

    testUri.run("ESTwoKeyNav", "$search=abc     def AND ghi");
    testUri.run("ESTwoKeyNav", "$search=abc     def  OR ghi");
    testUri.run("ESTwoKeyNav", "$search=abc     def     ghi");

    // mixed not
    testUri.run("ESTwoKeyNav", "$search=    abc         def AND     ghi");
    testUri.run("ESTwoKeyNav", "$search=NOT abc  NOT    def  OR NOT ghi");
    testUri.run("ESTwoKeyNav", "$search=    abc         def     NOT ghi");

    // parenthesis
    testUri.run("ESTwoKeyNav", "$search=(abc)");
    testUri.run("ESTwoKeyNav", "$search=(abc AND  def)");
    testUri.run("ESTwoKeyNav", "$search=(abc AND  def)   OR  ghi ");
    testUri.run("ESTwoKeyNav", "$search=(abc AND  def)       ghi ");
    testUri.run("ESTwoKeyNav", "$search=abc AND (def    OR  ghi)");
    testUri.run("ESTwoKeyNav", "$search=abc AND (def        ghi)");

    // search in function-import return value
    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)", "$search=test");

    // percent encoded characters
    testUri.run("ESTwoKeyNav", "$search=%41%42%43");
    testUri.run("ESTwoKeyNav", "$search=\"100%25\"");

    // escaped characters
    testUri.run("ESTwoKeyNav", "$search=\"abc\"");
    testUri.run("ESTwoKeyNav", "$search=\"a\\\"bc\"");
    testUri.run("ESTwoKeyNav", "$search=%22abc%22");
    testUri.run("ESTwoKeyNav", "$search=%22a%5C%22bc%22");
    testUri.run("ESTwoKeyNav", "$search=%22a%5C%5Cbc%22");

    // wrong escaped characters
    testUri.runEx("ESTwoKeyNav", "$search=%22a%22bc%22")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    testUri.runEx("ESTwoKeyNav", "$search=%22a%5Cbc%22")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    testUri.runEx("ESTwoKeyNav", "$search=not%27allowed")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
  }

  @Test
  public void searchTree() throws Exception {
    testUri.run("ESTwoKeyNav", "$expand=NavPropertyETKeyNavMany($search=(abc AND def) OR NOT ghi)")
        .goExpand().isSearchSerialized("{{'abc' AND 'def'} OR {NOT 'ghi'}}");
  }

  /**
   * See <a href=
   * "https://tools.oasis-open.org/version-control/browse/wsvn/odata/trunk/spec/ABNF/odata-abnf-testcases.xml">test
   * cases at OASIS</a>.
   */
  @Test
  public void searchQueryPhraseAbnfTestcases() throws Exception {
    // <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    testUri.run("ESTwoKeyNav", "$search=\"blue%20green\"");
    // <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    testUri.run("ESTwoKeyNav", "$search=\"blue%20green%22");
    // <TestCase Name="5.1.7 Search - phrase with escaped double-quote" Rule="queryOptions">
    // <Input>$search="blue\"green"</Input>
    testUri.run("ESTwoKeyNav", "$search=\"blue\\\"green\"");

    // <TestCase Name="5.1.7 Search - phrase with escaped backslash" Rule="queryOptions">
    // <Input>$search="blue\\green"</Input>
    testUri.run("ESTwoKeyNav", "$search=\"blue\\\\green\"");
    // <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="14">
    testUri.runEx("ESTwoKeyNav", "$search=\"blue\"green\"")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    // <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="16">
    testUri.runEx("ESTwoKeyNav", "$search=\"blue%22green\"")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);

    // <TestCase Name="5.1.7 Search - implicit AND" Rule="queryOptions">
    // <Input>$search=blue green</Input>
    // SearchassertQuery("\"blue%20green\"").resultsIn();
    testUri.run("ESTwoKeyNav", "$search=blue green");
    // <TestCase Name="5.1.7 Search - implicit AND, encoced" Rule="queryOptions">
    // SearchassertQuery("blue%20green").resultsIn();
    testUri.run("ESTwoKeyNav", "$search=blue%20green");

    // <TestCase Name="5.1.7 Search - AND" Rule="queryOptions">
    // <Input>$search=blue AND green</Input>
    testUri.run("ESTwoKeyNav", "$search=blue AND green");

    // <TestCase Name="5.1.7 Search - OR" Rule="queryOptions">
    // <Input>$search=blue OR green</Input>
    testUri.run("ESTwoKeyNav", "$search=blue OR green");

    // <TestCase Name="5.1.7 Search - NOT" Rule="queryOptions">
    // <Input>$search=blue NOT green</Input>
    testUri.run("ESTwoKeyNav", "$search=blue NOT green");

    // <TestCase Name="5.1.7 Search - only NOT" Rule="queryOptions">
    // <Input>$search=NOT blue</Input>
    testUri.run("ESTwoKeyNav", "$search=NOT blue");

    // <TestCase Name="5.1.7 Search - multiple" Rule="queryOptions">
    // <Input>$search=foo AND bar OR foo AND baz OR that AND bar OR that AND baz</Input>
    testUri.run("ESTwoKeyNav", "$search=foo AND bar OR foo AND baz OR that AND bar OR that AND baz");

    // <TestCase Name="5.1.7 Search - multiple" Rule="queryOptions">
    // <Input>$search=(foo OR that) AND (bar OR baz)</Input>
    testUri.run("ESTwoKeyNav", "$search=(foo OR that) AND (bar OR baz)");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=foo AND (bar OR baz)</Input>
    testUri.run("ESTwoKeyNav", "$search=foo AND (bar OR baz)");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(foo AND bar) OR baz</Input>
    testUri.run("ESTwoKeyNav", "$search=(foo AND bar) OR baz");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(NOT foo) OR baz</Input>
    testUri.run("ESTwoKeyNav", "$search=(NOT foo) OR baz");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(NOT foo)</Input>
    testUri.run("ESTwoKeyNav", "$search=(NOT foo)");

    // <TestCase Name="5.1.7 Search - on entity set" Rule="odataUri">
    // <Input>http://serviceRoot/Products?$search=blue</Input>
    testUri.run("ESTwoKeyNav", "$search=blue");

    // <TestCase Name="5.1.7 Search - on entity container" Rule="odataUri">
    // <Input>http://serviceRoot/Model.Container/$all?$search=blue</Input>
    testUri.run("$all", "$search=blue");

    // <TestCase Name="5.1.7 Search - on service" Rule="odataUri">
    // <Input>http://serviceRoot/$all?$search=blue</Input>
    testUri.run("$all", "$search=blue");
  }
}
