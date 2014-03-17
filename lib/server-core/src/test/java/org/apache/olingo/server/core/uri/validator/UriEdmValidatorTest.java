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
package org.apache.olingo.server.core.uri.validator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.core.testutil.EdmTechProvider;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.junit.Ignore;
import org.junit.Test;

public class UriEdmValidatorTest {

  private Edm edm = new EdmProviderImpl(new EdmTechProvider());

  String[] tmpUri = {
      "$crossjoin(ESKeyNav, ESTwoKeyNav)/invalid                                                                    ",
      "$crossjoin(invalidEntitySet)                                                                                 ",
      "$entity                                                                                                      ",
      "$entity?$idfalse=ESKeyNav(1)                                                                                 ",
      "ESAllPrim(PropertyInt16='1')                                                                                 ",
      "ESCollAllPrim(null)                                                                                          ",
      "ESTwoPrim(1)/com.sap.odata.test1.ETBase(1)                                                                   ",
      "ESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase(1)                                     ",
      "ESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase(1)                                     ",
      "FICRTCollCTTwoPrimParam(ParameterInt16='1',ParameterString='2')                                              ",
      "FICRTESTwoKeyNavParam(ParameterInt16=@invalidAlias)?@validAlias=1                                            ",
      "FINRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/PropertyComplex                         ",
      "FINRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/$count                                  ",
      // "ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$ref                                                              ",
//      "ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$count                                                            ",
//      "ESKeyNav?$top=-3                                                                                             ",
//      "ESAllPrim?$count=foo                                                                                         ",
//      "ESAllPrim?$skip=-3                                                                                           "
  };

  @Test
  @Ignore("key predicate validation not implemented")
  public void keyPredicateValidTypes() throws Exception {
    String[] uris = {};

    for (String uri : uris) {
      parseAndValidate(uri);
    }

  }

  @Test
  @Ignore("key predicate validation not implemented")
  public void keyPredicateInvalidTypes() throws UriParserException {
    String[] uris = {};

    for (String uri : uris) {

      try {
        parseAndValidate(uri);
        fail("Validation Exception not thrown: " + uri);
      } catch (UriValidationException e) {
        assertTrue(e instanceof UriValidationException);
      }
    }
  }

  @Test
  public void systemQueryOptionValid() throws Exception {
    String[] uris =
    {
        /* service document */
        "",
        /* metadata */
        "/$metadata",
        "/$metadata?$format=atom",
    };

    for (String uri : uris) {
      try {
        parseAndValidate(uri);
      } catch (Exception e) {
        throw new Exception("Faild for uri: " + uri, e);
      }
    }

  }

  @Test
  public void systemQueryOptionInvalid() throws Exception {
    String[] uris =
        {
            /* service document */
            /* metadata */
            "/$metadata?$format=json&$top=3&$skip=5&$skiptoken=123",

            /* misc */
            "ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$ref                                                              ",
            "ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$count                                                            ",
            "ESKeyNav?$top=-3                                                                                             ",
            "ESAllPrim?$count=foo                                                                                         ",
            "ESAllPrim?$skip=-3                                                                                           "
        };

    for (String uri : uris) {

      try {
        parseAndValidate(uri);
        fail("Validation Exception not thrown: " + uri);
      } catch (UriValidationException e) {
        assertTrue(e instanceof UriValidationException);
      }
    }
  }

  private void parseAndValidate(String uri) throws UriParserException, UriValidationException {
    UriInfo uriInfo = new Parser().parseUri(uri.trim(), edm);
    SystemQueryValidator validator = new SystemQueryValidator();
    validator.validate(uriInfo, edm);
  }
}
