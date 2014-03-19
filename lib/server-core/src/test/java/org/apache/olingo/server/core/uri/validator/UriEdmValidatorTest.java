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

import java.util.ArrayList;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.core.testutil.EdmTechProvider;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.junit.Ignore;
import org.junit.Test;

public class UriEdmValidatorTest {

  private static final String URI_ALL = "$all";
  private static final String URI_BATCH = "$batch";
  private static final String URI_CROSSJOIN = "$crossjoin(ESAllPrim)";
  private static final String URI_ENTITY_ID = "/$entity";
  private static final String URI_METADATA = "$metadata";
  private static final String URI_SERVICE = "";
  private static final String URI_ENTITY_SET = "/ESAllPrim";
  private static final String URI_ENTITY_SET_COUNT = "/ESAllPrim/$count";
  private static final String URI_ENTITY = "/ESAllPrim(1)";
  private static final String URI_MEDIA_STREAM = "/ESMedia(1)/$value";
  private static final String URI_REFERENCES = "/ESAllPrim/$ref";
  private static final String URI_REFERENECE = "/ESAllPrim(1)/$ref";
  private static final String URI_PROPERTY_COMPLEX = "/ESCompComp(1)/PropertyComplex";
  private static final String URI_PROPERTY_COMPLEX_COLLECTION =
      "/ESCompCollComp(1)/PropertyComplex/CollPropertyComplex";
  private static final String URI_PROPERTY_COMPLEX_COLLECTION_COUNT =
      "/ESCompCollComp(1)/PropertyComplex/CollPropertyComplex/$count";
  private static final String URI_PROPERTY_PRIMITIVE = "/ESAllPrim(1)/PropertyString";
  private static final String URI_PROPERTY_PRIMITIVE_COLLECTION = "/ESCollAllPrim/CollPropertyString";
  private static final String URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT =
      "/ESCollAllPrim/CollPropertyString/$count";
  private static final String URI_PROPERTY_PRIMITIVE_VALUE = "/ESAllPrim(1)/PropertyString/$value";

  private static final String QO_FILTER = "$filter='1' eq '1'";
  private static final String QO_FORMAT = "$format=bla";
  private static final String QO_EXPAND = "$expand=*";
  private static final String QO_ID = "$id=Products(0)";
  private static final String QO_COUNT = "$count";
//  private static final String QO_ORDERBY = "$orderby=bla asc";
//  private static final String QO_SEARCH = "$search='bla'";
  private static final String QO_SELECT = "$select=*";
  private static final String QO_SKIP = "$skip=3";
  private static final String QO_SKIPTOKEN = "$skiptoken=123";
  private static final String QO_LEVELS = "$expand=*($levels=1)";
  private static final String QO_TOP = "$top=1";

  private Edm edm = new EdmProviderImpl(new EdmTechProvider());

  private String[][] urisWithValidSystemQueryOptions = {
      { URI_ALL, QO_FILTER, }, { URI_ALL, QO_FORMAT }, { URI_ALL, QO_EXPAND }, { URI_ALL, QO_COUNT },
      /* { URI_ALL, QO_ORDERBY }, *//* { URI_ALL, QO_SEARCH }, */{ URI_ALL, QO_SELECT }, { URI_ALL, QO_SKIP },
      { URI_ALL, QO_SKIPTOKEN }, { URI_ALL, QO_LEVELS },

      { URI_CROSSJOIN, QO_FILTER, }, { URI_CROSSJOIN, QO_FORMAT },
      { URI_CROSSJOIN, QO_EXPAND }, { URI_CROSSJOIN, QO_COUNT }, /* { URI_CROSSJOIN, QO_ORDERBY }, */
      /* { URI_CROSSJOIN, QO_SEARCH }, */{ URI_CROSSJOIN, QO_SELECT }, { URI_CROSSJOIN, QO_SKIP },
      { URI_CROSSJOIN, QO_SKIPTOKEN }, { URI_CROSSJOIN, QO_LEVELS }, { URI_CROSSJOIN, QO_TOP },

      { URI_ENTITY_ID, QO_ID, QO_FORMAT }, { URI_ENTITY_ID, QO_ID, }, { URI_ENTITY_ID, QO_ID, QO_EXPAND },
      { URI_ENTITY_ID, QO_ID, QO_SELECT }, { URI_ENTITY_ID, QO_ID, QO_LEVELS },

      { URI_METADATA, QO_FORMAT },

      { URI_SERVICE, QO_FORMAT },

      { URI_ENTITY_SET, QO_FILTER, }, { URI_ENTITY_SET, QO_FORMAT }, { URI_ENTITY_SET, QO_EXPAND },
      { URI_ENTITY_SET, QO_COUNT }, /* { URI_ENTITY_SET, QO_ORDERBY }, *//* { URI_ENTITY_SET, QO_SEARCH }, */
      { URI_ENTITY_SET, QO_SELECT },
      { URI_ENTITY_SET, QO_SKIP }, { URI_ENTITY_SET, QO_SKIPTOKEN }, { URI_ENTITY_SET, QO_LEVELS },
      { URI_ENTITY_SET, QO_TOP },

  };

  private String[][] urisWithNonValidSystemQueryOptions = {
      { URI_ALL, QO_ID, }, { URI_ALL, QO_TOP },

      { URI_BATCH, QO_FILTER, }, { URI_BATCH, QO_FORMAT }, { URI_BATCH, QO_ID, }, { URI_BATCH, QO_EXPAND },
      { URI_BATCH, QO_COUNT }, /* { URI_BATCH, QO_ORDERBY }, *//* { URI_BATCH, QO_SEARCH }, */{ URI_BATCH, QO_SELECT },
      { URI_BATCH, QO_SKIP }, { URI_BATCH, QO_SKIPTOKEN }, { URI_BATCH, QO_LEVELS }, { URI_BATCH, QO_TOP },

      { URI_CROSSJOIN, QO_ID, },

      { URI_ENTITY_ID, QO_ID, QO_FILTER, },
      { URI_ENTITY_ID, QO_ID, QO_COUNT }, /* { URI_ENTITY_ID, QO_ORDERBY }, *//* { URI_ENTITY_ID, QO_SEARCH }, */

      { URI_ENTITY_ID, QO_ID, QO_SKIP }, { URI_ENTITY_ID, QO_ID, QO_SKIPTOKEN }, { URI_ENTITY_ID, QO_ID, QO_TOP },

      { URI_METADATA, QO_FILTER, }, { URI_METADATA, QO_ID, }, { URI_METADATA, QO_EXPAND },
      { URI_METADATA, QO_COUNT }, /* { URI_METADATA, QO_ORDERBY }, *//* { URI_METADATA, QO_SEARCH }, */
      { URI_METADATA, QO_SELECT }, { URI_METADATA, QO_SKIP }, { URI_METADATA, QO_SKIPTOKEN },
      { URI_METADATA, QO_LEVELS }, { URI_METADATA, QO_TOP },

      { URI_SERVICE, QO_FILTER }, { URI_SERVICE, QO_ID }, { URI_SERVICE, QO_EXPAND }, { URI_SERVICE, QO_COUNT },
      /* { URI_SERVICE, QO_ORDERBY }, *//* { URI_SERVICE, QO_SEARCH }, */{ URI_SERVICE, QO_SELECT },
      { URI_SERVICE, QO_SKIP }, { URI_SERVICE, QO_SKIPTOKEN }, { URI_SERVICE, QO_LEVELS }, { URI_SERVICE, QO_TOP },

      { URI_ENTITY_SET, QO_ID },

      { URI_ENTITY_SET_COUNT, QO_FILTER }, { URI_ENTITY_SET_COUNT, QO_FORMAT }, { URI_ENTITY_SET_COUNT, QO_ID },
      { URI_ENTITY_SET_COUNT, QO_EXPAND }, { URI_ENTITY_SET_COUNT, QO_COUNT },
      /* { URI_ENTITY_SET_COUNT, QO_ORDERBY }, *//* { URI_ENTITY_SET_COUNT, QO_SEARCH }, */
      { URI_ENTITY_SET_COUNT, QO_SELECT }, { URI_ENTITY_SET_COUNT, QO_SKIP }, { URI_ENTITY_SET_COUNT, QO_SKIPTOKEN },
      { URI_ENTITY_SET_COUNT, QO_LEVELS }, { URI_ENTITY_SET_COUNT, QO_TOP },
  };

  @Test
  public void bla() throws Exception {
    String[][] m = { { URI_ENTITY_SET_COUNT, QO_FILTER } };
    String[] uris = constructUri(m);
    System.out.println(uris[0]);

    parseAndValidate(uris[0]);
  }

  @Test
  public void checkValidSystemQueryOption() throws Exception {
    String[] uris = constructUri(urisWithValidSystemQueryOptions);

    for (String uri : uris) {
      try {
        parseAndValidate(uri);
      } catch (Exception e) {
        throw new Exception("Faild for uri: " + uri, e);
      }
    }
  }

  @Test
  public void checkNonValidSystemQueryOption() throws Exception {
    String[] uris = constructUri(urisWithNonValidSystemQueryOptions);

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
  @Ignore
  public void systemQueryOptionValid() throws Exception {
    String[] uris =
    {
        /* $filter */
        "/$all?$format=bla",
        // "/$batch?$format=bla",
        "/$crossjoin(ESAllPrim)?$format=bla",
        "/$entity?$id=Products(0)?$format=bla",
        "/$metadata?$format=bla",
        "?$format=bla",
        "/ESAllPrim?$format=bla",
        "/ESAllPrim/$count?$format=bla",
        "/ESAllPrim(1)?$format=bla",
        "/ESMedia(1)/$value?$format=bla",
        "/ESAllPrim/$ref?$format=bla",
        "/ESAllPrim(1)/$ref?$format=bla",
        "/ESCompComp(1)/PropertyComplex?$format=bla",
        "/ESCompCollComp(1)/PropertyComplex/CollPropertyComplex?$format=bla",
        "/ESCompCollComp(1)/PropertyComplex/CollPropertyComplex/$count?$format=bla",
        "/ESAllPrim(1)/PropertyString?$format=bla",
        "/ESCollAllPrim/CollPropertyString?$format=bla",
        "/ESCollAllPrim/CollPropertyString/$count?$format=bla",
        "/ESAllPrim(1)/PropertyString/$value?$format=bla"
    };

    for (String uri : uris) {
      try {
        parseAndValidate(uri);
      } catch (Exception e) {
        throw new Exception("Faild for uri: " + uri, e);
      }
    }

  }

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

  private String[] constructUri(String[][] uriParameterMatrix) {
    ArrayList<String> uris = new ArrayList<String>();
    for (String[] uriParameter : uriParameterMatrix) {
      String uri = uriParameter[0];
      if (uriParameter.length > 1) {
        uri += "?";
      }
      for (int i = 1; i < uriParameter.length; i++) {
        uri += uriParameter[i];
        if (i < (uriParameter.length - 1)) {
          uri += "&";
        }
      }
      uris.add(uri);
    }
    return uris.toArray(new String[0]);
  }

  @Test
  @Ignore
  public void systemQueryOptionInvalid() throws Exception {
    String[] uris =
    {
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

    System.out.print("URI: " + uri);
    validator.validate(uriInfo, edm);
  }
}
