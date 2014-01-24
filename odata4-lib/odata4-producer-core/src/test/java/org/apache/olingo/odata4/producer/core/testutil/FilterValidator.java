/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.exception.ODataApplicationException;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.core.uri.ParserAdapter;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriParserException;
import org.apache.olingo.odata4.producer.core.uri.UriParseTreeVisitor;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FilterOptionImpl;

public class FilterValidator implements Validator {
  private Edm edm;

  private Validator invokedBy;
  private FilterOptionImpl filter;

  private int logLevel;

  // --- Setup ---
  public FilterValidator SetUriResourcePathValidator(UriResourceValidator uriResourcePathValidator) {
    this.invokedBy = uriResourcePathValidator;
    return this;
  }

  public FilterValidator setUriValidator(UriValidator uriValidator) {
    this.invokedBy = uriValidator;
    return this;
  }

  public FilterValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  public FilterValidator setFilter(FilterOptionImpl filter) {
    this.filter = filter;

    if (filter.getExpression() == null) {

      fail("FilterValidator: no filter found");
    }
    return this;
  }
  
  public FilterValidator log(final int logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  // --- Execution ---
  public FilterValidator runOnETTwoKeyNav(String filter) {
    String uri = "SINav?$filter=" + filter.trim();
    return runUri(uri);
  }

  public FilterValidator runOnETAllPrim(String filter) {
    String uri = "ESAllPrim(1)?$filter=" + filter.trim(); 
    return runUri(uri);
  }

  public FilterValidator runOnETKeyNav(String filter) {
    String uri = "ESKeyNav(1)?$filter=" + filter.trim(); 
    return runUri(uri);
  }

  
  public FilterValidator runOnCTTwoPrim(String filter) {
    String uri = "SINav/PropertyComplexTwoPrim?$filter=" + filter.trim(); 
    return runUri(uri);
  }
  
  public FilterValidator runOnString(String filter) {
    String uri = "SINav/PropertyString?$filter=" + filter.trim(); 
    return runUri(uri);
  }
  
  public FilterValidator runOnInt32(String filter) {
    String uri = "ESCollAllPrim(1)/CollPropertyInt32?$filter=" + filter.trim(); 
    return runUri(uri);
  }
  
  
  public FilterValidator runOnDateTimeOffset(String filter) {
    String uri = "ESCollAllPrim(1)/CollPropertyDateTimeOffset?$filter=" + filter.trim(); 
    return runUri(uri);
  }
  
  public FilterValidator runOnDuration(String filter) {
    String uri = "ESCollAllPrim(1)/CollPropertyDuration?$filter=" + filter.trim(); 
    return runUri(uri);
  }
  
  public FilterValidator runOnTimeOfDay(String filter) {
    String uri = "ESCollAllPrim(1)/CollPropertyTimeOfDay?$filter=" + filter.trim(); 
    return runUri(uri);
  }
  
  
  public FilterValidator runESabc(String filter) {
    String uri = "ESabc?$filter=" + filter.trim(); 
    return runUri(uri);
  }
  
  public FilterValidator runUri(String uri) { 
  
    UriInfoImpl uriInfo = null;
    try {

      uriInfo = ParserAdapter.parseUri(uri, new UriParseTreeVisitor(edm));
    } catch (UriParserException e) {
      fail("Exception occured while parsing the URI: " + uri + "\n"
          + " Exception: " + e.getMessage());
    }

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("Filtervalidator can only be used on resourcePaths");
    }

    setFilter((FilterOptionImpl) uriInfo.getFilterOption());

    return this;
  }

  // --- Navigation ---
  public Validator goUp() {
    return invokedBy;
  }

  // --- Validation ---

  /**
   * Validates the serialized filterTree against a given filterString
   * The given filterString is compressed before to allow better readable code in the unit tests
   * @param toBeCompr
   * @return
   */
  public FilterValidator isCompr(String toBeCompr) {
    return is(compress(toBeCompr));
  }

  public FilterValidator is(String expectedFilterAsString) {
    try {
      String actualFilterAsText = FilterTreeToText.Serialize((FilterOptionImpl) filter);
      assertEquals(expectedFilterAsString, actualFilterAsText);
    } catch (ExceptionVisitExpression e) {
      fail("Exception occured while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    } catch (ODataApplicationException e) {
      fail("Exception occured while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    }

    return this;
  }

  // --- Helper ---

  private String compress(String expected) {
    String ret = expected.replaceAll("\\s+", " ");
    ret = ret.replaceAll("< ", "<");
    ret = ret.replaceAll(" >", ">");
    return ret;
  }

}
