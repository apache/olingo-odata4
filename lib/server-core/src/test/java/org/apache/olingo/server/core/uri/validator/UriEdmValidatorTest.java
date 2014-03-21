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
import org.junit.Before;
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
  private static final String URI_REFERENCE = "/ESAllPrim(1)/$ref";
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
//  private static final String QO_ORDERBY = "$orderby=true";
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

      { URI_ENTITY, QO_FORMAT }, { URI_ENTITY, QO_EXPAND }, { URI_ENTITY, QO_SELECT }, { URI_ENTITY, QO_LEVELS },

      { URI_MEDIA_STREAM, QO_FORMAT },

      { URI_REFERENCES, QO_FILTER }, { URI_REFERENCES, QO_FORMAT }, /* { URI_REFERENCES, QO_ORDERBY }, */
      /* { URI_REFERENCES, QO_SEARCH }, */{ URI_REFERENCES, QO_SKIP }, { URI_REFERENCES, QO_SKIPTOKEN },
      { URI_REFERENCES, QO_TOP },

      { URI_REFERENCE, QO_FORMAT },

      { URI_PROPERTY_COMPLEX, QO_FORMAT }, { URI_PROPERTY_COMPLEX, QO_SELECT }, { URI_PROPERTY_COMPLEX, QO_EXPAND },
      { URI_PROPERTY_COMPLEX, QO_LEVELS },

      { URI_PROPERTY_COMPLEX_COLLECTION, QO_FILTER }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_FORMAT },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_EXPAND }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_COUNT },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_SKIP }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_SKIPTOKEN },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_LEVELS }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_TOP },

      { URI_PROPERTY_PRIMITIVE, QO_FORMAT },

      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_FILTER }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_FORMAT },
      /* { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_ORDERBY }, */{ URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SKIPTOKEN }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_TOP },

      { URI_PROPERTY_PRIMITIVE_VALUE, QO_FORMAT },
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

      { URI_ENTITY, QO_FILTER }, { URI_ENTITY, QO_ID }, { URI_ENTITY, QO_COUNT }, /* { URI_ENTITY, QO_ORDERBY }, */
      /* { URI_ENTITY, QO_SEARCH }, */{ URI_ENTITY, QO_SKIP }, { URI_ENTITY, QO_SKIPTOKEN }, { URI_ENTITY, QO_TOP },

      { URI_MEDIA_STREAM, QO_FILTER }, { URI_MEDIA_STREAM, QO_ID, }, { URI_MEDIA_STREAM, QO_EXPAND },
      { URI_MEDIA_STREAM, QO_COUNT }, /* { URI_MEDIA_STREAM, QO_ORDERBY }, *//* { URI_MEDIA_STREAM, QO_SEARCH }, */
      { URI_MEDIA_STREAM, QO_SELECT }, { URI_MEDIA_STREAM, QO_SKIP }, { URI_MEDIA_STREAM, QO_SKIPTOKEN },
      { URI_MEDIA_STREAM, QO_LEVELS }, { URI_MEDIA_STREAM, QO_TOP },

      { URI_REFERENCES, QO_ID, }, { URI_REFERENCES, QO_EXPAND }, { URI_REFERENCES, QO_COUNT },
      { URI_REFERENCES, QO_SELECT }, { URI_REFERENCES, QO_LEVELS },

      { URI_REFERENCE, QO_FILTER }, { URI_REFERENCE, QO_ID, }, { URI_REFERENCE, QO_EXPAND },
      { URI_REFERENCE, QO_COUNT }, /* { URI_REFERENCE, QO_ORDERBY }, *//* { URI_REFERENCE, QO_SEARCH }, */
      { URI_REFERENCE, QO_SELECT }, { URI_REFERENCE, QO_SKIP }, { URI_REFERENCE, QO_SKIPTOKEN },
      { URI_REFERENCE, QO_LEVELS }, { URI_REFERENCE, QO_TOP },

      { URI_PROPERTY_COMPLEX, QO_FILTER }, { URI_PROPERTY_COMPLEX, QO_ID, }, { URI_PROPERTY_COMPLEX, QO_COUNT },
      /* { URI_PROPERTY_COMPLEX, QO_ORDERBY }, *//* { URI_PROPERTY_COMPLEX, QO_SEARCH }, */
      { URI_PROPERTY_COMPLEX, QO_SKIP }, { URI_PROPERTY_COMPLEX, QO_SKIPTOKEN }, { URI_PROPERTY_COMPLEX, QO_TOP },

      { URI_PROPERTY_COMPLEX_COLLECTION, QO_ID, }, /* { URI_PROPERTY_COMPLEX_COLLECTION, QO_ORDERBY }, */
      /* { URI_PROPERTY_COMPLEX_COLLECTION, QO_SEARCH }, */{ URI_PROPERTY_COMPLEX_COLLECTION, QO_SELECT },

      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_FILTER }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_FORMAT },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_ID, }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_EXPAND },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_COUNT }, /* { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_ORDERBY }, */
      /* { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SEARCH }, */{ URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SELECT },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SKIP }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SKIPTOKEN },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_LEVELS }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_TOP },

      { URI_PROPERTY_PRIMITIVE, QO_FILTER }, { URI_PROPERTY_PRIMITIVE, QO_ID, }, { URI_PROPERTY_PRIMITIVE, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE, QO_COUNT }, /* { URI_PROPERTY_PRIMITIVE, QO_ORDERBY }, */
      /* { URI_PROPERTY_PRIMITIVE, QO_SEARCH }, */{ URI_PROPERTY_PRIMITIVE, QO_SELECT },
      { URI_PROPERTY_PRIMITIVE, QO_SKIP }, { URI_PROPERTY_PRIMITIVE, QO_SKIPTOKEN },
      { URI_PROPERTY_PRIMITIVE, QO_LEVELS }, { URI_PROPERTY_PRIMITIVE, QO_TOP },

      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_ID, }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_COUNT }, /* { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SEARCH }, */
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_LEVELS },

      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_FILTER }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_FORMAT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_ID, }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_COUNT },
      /* { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_ORDERBY }, */
      /* { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SEARCH }, */
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SKIPTOKEN },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_LEVELS }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_TOP },

      { URI_PROPERTY_PRIMITIVE_VALUE, QO_FILTER }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_ID, },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_EXPAND }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_COUNT },
      /* { URI_PROPERTY_PRIMITIVE_VALUE, QO_ORDERBY }, *//* { URI_PROPERTY_PRIMITIVE_VALUE, QO_SEARCH }, */
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_SKIPTOKEN }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_LEVELS },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_TOP },

  };
  private Parser parser;

  @Before
  public void before() {
    parser = new Parser();
  }

  @Test
  @Ignore
  public void bla() throws Exception {
    String[][] m = { { URI_PROPERTY_PRIMITIVE_VALUE, QO_SELECT } };
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

  private void parseAndValidate(String uri) throws UriParserException, UriValidationException {
    UriInfo uriInfo = parser.parseUri(uri.trim(), edm);
    SystemQueryValidator validator = new SystemQueryValidator();

    System.out.print("URI: " + uri);
    validator.validate(uriInfo, edm);
  }
}
