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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UriValidatorTest {

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
  private static final String URI_PROPERTY_COMPLEX = "/ESCompComp(1)/PropertyComp";
  private static final String URI_PROPERTY_COMPLEX_COLLECTION =
      "/ESCompCollComp(1)/PropertyComp/CollPropertyComp";
  private static final String URI_PROPERTY_COMPLEX_COLLECTION_COUNT =
      "/ESCompCollComp(1)/PropertyComp/CollPropertyComp/$count";
  private static final String URI_PROPERTY_PRIMITIVE = "/ESAllPrim(1)/PropertyString";
  private static final String URI_PROPERTY_PRIMITIVE_COLLECTION = "/ESCollAllPrim(1)/CollPropertyString";
  private static final String URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT =
      "/ESCollAllPrim(1)/CollPropertyString/$count";
  private static final String URI_PROPERTY_PRIMITIVE_VALUE = "/ESAllPrim(1)/PropertyString/$value";
  private static final String URI_SINGLETON = "/SI";
  private static final String URI_NAV_ENTITY = "/ESKeyNav(1)/NavPropertyETKeyNavOne";
  private static final String URI_NAV_ENTITY_SET = "/ESKeyNav(1)/NavPropertyETKeyNavMany";

  private static final String QO_FILTER = "$filter='1' eq '1'";
  private static final String QO_FORMAT = "$format=bla/bla";
  private static final String QO_EXPAND = "$expand=*";
  private static final String QO_ID = "$id=Products(0)";
  private static final String QO_COUNT = "$count=true";
  private static final String QO_ORDERBY = "$orderby=true";
  //  private static final String QO_SEARCH = "$search='bla'";
  private static final String QO_SELECT = "$select=*";
  private static final String QO_SKIP = "$skip=3";
  private static final String QO_SKIPTOKEN = "$skiptoken=123";
  private static final String QO_LEVELS = "$expand=*($levels=1)";
  private static final String QO_TOP = "$top=1";

  private String[][] urisWithValidSystemQueryOptions = {
      { URI_ALL, QO_FILTER }, { URI_ALL, QO_FORMAT }, { URI_ALL, QO_EXPAND }, { URI_ALL, QO_COUNT },
      { URI_ALL, QO_ORDERBY }, /* { URI_ALL, QO_SEARCH }, */{ URI_ALL, QO_SELECT }, { URI_ALL, QO_SKIP },
      { URI_ALL, QO_SKIPTOKEN }, { URI_ALL, QO_LEVELS },

      { URI_CROSSJOIN, QO_FILTER }, { URI_CROSSJOIN, QO_FORMAT },
      { URI_CROSSJOIN, QO_EXPAND }, { URI_CROSSJOIN, QO_COUNT }, { URI_CROSSJOIN, QO_ORDERBY },
      /* { URI_CROSSJOIN, QO_SEARCH }, */{ URI_CROSSJOIN, QO_SELECT }, { URI_CROSSJOIN, QO_SKIP },
      { URI_CROSSJOIN, QO_SKIPTOKEN }, { URI_CROSSJOIN, QO_LEVELS }, { URI_CROSSJOIN, QO_TOP },

      { URI_ENTITY_ID, QO_ID, QO_FORMAT }, { URI_ENTITY_ID, QO_ID }, { URI_ENTITY_ID, QO_ID, QO_EXPAND },
      { URI_ENTITY_ID, QO_ID, QO_SELECT }, { URI_ENTITY_ID, QO_ID, QO_LEVELS },

      { URI_METADATA, QO_FORMAT },

      { URI_SERVICE, QO_FORMAT },

      { URI_ENTITY_SET, QO_FILTER }, { URI_ENTITY_SET, QO_FORMAT }, { URI_ENTITY_SET, QO_EXPAND },
      { URI_ENTITY_SET, QO_COUNT }, { URI_ENTITY_SET, QO_ORDERBY }, /* { URI_ENTITY_SET, QO_SEARCH }, */
      { URI_ENTITY_SET, QO_SELECT },
      { URI_ENTITY_SET, QO_SKIP }, { URI_ENTITY_SET, QO_SKIPTOKEN }, { URI_ENTITY_SET, QO_LEVELS },
      { URI_ENTITY_SET, QO_TOP },

      { URI_ENTITY_SET_COUNT, QO_FILTER }, /* { URI_ENTITY_SET_COUNT, QO_SEARCH }, */

      { URI_ENTITY, QO_FORMAT }, { URI_ENTITY, QO_EXPAND }, { URI_ENTITY, QO_SELECT }, { URI_ENTITY, QO_LEVELS },

      { URI_MEDIA_STREAM, QO_FORMAT },

      { URI_REFERENCES, QO_FILTER }, { URI_REFERENCES, QO_FORMAT }, { URI_REFERENCES, QO_ORDERBY },
      /* { URI_REFERENCES, QO_SEARCH }, */{ URI_REFERENCES, QO_SKIP }, { URI_REFERENCES, QO_SKIPTOKEN },
      { URI_REFERENCES, QO_TOP },

      { URI_REFERENCE, QO_FORMAT },

      { URI_PROPERTY_COMPLEX, QO_FORMAT }, { URI_PROPERTY_COMPLEX, QO_SELECT }, { URI_PROPERTY_COMPLEX, QO_EXPAND },
      { URI_PROPERTY_COMPLEX, QO_LEVELS },

      { URI_PROPERTY_COMPLEX_COLLECTION, QO_FILTER }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_FORMAT },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_EXPAND }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_COUNT },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_SKIP }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_SKIPTOKEN },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_LEVELS }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_TOP },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_ORDERBY },

      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_FILTER }, /* { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SEARCH }, */

      { URI_PROPERTY_PRIMITIVE, QO_FORMAT },

      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_FILTER }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_FORMAT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_ORDERBY }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SKIPTOKEN }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_TOP },

      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_FILTER },
      /* { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SEARCH }, */

      { URI_PROPERTY_PRIMITIVE_VALUE, QO_FORMAT },

      { URI_SINGLETON, QO_FORMAT }, { URI_SINGLETON, QO_EXPAND }, { URI_SINGLETON, QO_SELECT },
      { URI_SINGLETON, QO_LEVELS },

      { URI_NAV_ENTITY, QO_FORMAT }, { URI_NAV_ENTITY, QO_EXPAND }, { URI_NAV_ENTITY, QO_SELECT },
      { URI_NAV_ENTITY, QO_LEVELS },

      { URI_NAV_ENTITY_SET, QO_FILTER }, { URI_NAV_ENTITY_SET, QO_FORMAT }, { URI_NAV_ENTITY_SET, QO_EXPAND },
      { URI_NAV_ENTITY_SET, QO_COUNT }, { URI_NAV_ENTITY_SET, QO_ORDERBY },
      /* { URI_NAV_ENTITY_SET, QO_SEARCH }, */{ URI_NAV_ENTITY_SET, QO_SELECT }, { URI_NAV_ENTITY_SET, QO_SKIP },
      { URI_NAV_ENTITY_SET, QO_SKIPTOKEN }, { URI_NAV_ENTITY_SET, QO_LEVELS }, { URI_NAV_ENTITY_SET, QO_TOP },

      { "FINRTInt16()" },
      { "FICRTETKeyNav()" },
      { "FICRTESTwoKeyNavParam(ParameterInt16=1)" },
      { "FICRTCollString()" },
      { "FICRTCTTwoPrim()" },
      { "FICRTCollCTTwoPrim()" },
      { "FICRTETMedia()" },

      { "ESTwoKeyNav/olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav" },
      { "ESAllPrim/olingo.odata.test1.BAESAllPrimRTETAllPrim" },
      { "AIRTPrimCollParam" },
      { "AIRTETParam" },
      { "AIRTPrimParam" }
  };

  private String[][] urisWithNonValidSystemQueryOptions = {
      { URI_ALL, QO_ID }, { URI_ALL, QO_TOP },

      { URI_BATCH, QO_FILTER }, { URI_BATCH, QO_FORMAT }, { URI_BATCH, QO_ID }, { URI_BATCH, QO_EXPAND },
      { URI_BATCH, QO_COUNT }, { URI_BATCH, QO_ORDERBY }, /* { URI_BATCH, QO_SEARCH }, */{ URI_BATCH, QO_SELECT },
      { URI_BATCH, QO_SKIP }, { URI_BATCH, QO_SKIPTOKEN }, { URI_BATCH, QO_LEVELS }, { URI_BATCH, QO_TOP },

      { URI_CROSSJOIN, QO_ID },

      { URI_ENTITY_ID, QO_ID, QO_FILTER },
      { URI_ENTITY_ID, QO_ID, QO_COUNT }, { URI_ENTITY_ID, QO_ORDERBY }, /* { URI_ENTITY_ID, QO_SEARCH }, */

      { URI_ENTITY_ID, QO_ID, QO_SKIP }, { URI_ENTITY_ID, QO_ID, QO_SKIPTOKEN }, { URI_ENTITY_ID, QO_ID, QO_TOP },

      { URI_METADATA, QO_FILTER }, { URI_METADATA, QO_ID }, { URI_METADATA, QO_EXPAND },
      { URI_METADATA, QO_COUNT }, { URI_METADATA, QO_ORDERBY }, /* { URI_METADATA, QO_SEARCH }, */
      { URI_METADATA, QO_SELECT }, { URI_METADATA, QO_SKIP }, { URI_METADATA, QO_SKIPTOKEN },
      { URI_METADATA, QO_LEVELS }, { URI_METADATA, QO_TOP },

      { URI_SERVICE, QO_FILTER }, { URI_SERVICE, QO_ID }, { URI_SERVICE, QO_EXPAND }, { URI_SERVICE, QO_COUNT },
      { URI_SERVICE, QO_ORDERBY }, /* { URI_SERVICE, QO_SEARCH }, */{ URI_SERVICE, QO_SELECT },
      { URI_SERVICE, QO_SKIP }, { URI_SERVICE, QO_SKIPTOKEN }, { URI_SERVICE, QO_LEVELS }, { URI_SERVICE, QO_TOP },

      { URI_ENTITY_SET, QO_ID },

      { URI_ENTITY_SET_COUNT, QO_FORMAT }, { URI_ENTITY_SET_COUNT, QO_ID },
      { URI_ENTITY_SET_COUNT, QO_EXPAND }, { URI_ENTITY_SET_COUNT, QO_COUNT },
      { URI_ENTITY_SET_COUNT, QO_ORDERBY },
      { URI_ENTITY_SET_COUNT, QO_SELECT }, { URI_ENTITY_SET_COUNT, QO_SKIP }, { URI_ENTITY_SET_COUNT, QO_SKIPTOKEN },
      { URI_ENTITY_SET_COUNT, QO_LEVELS }, { URI_ENTITY_SET_COUNT, QO_TOP },

      { URI_ENTITY, QO_FILTER }, { URI_ENTITY, QO_ID }, { URI_ENTITY, QO_COUNT }, /* { URI_ENTITY, QO_ORDERBY }, */
      /* { URI_ENTITY, QO_SEARCH }, */{ URI_ENTITY, QO_SKIP }, { URI_ENTITY, QO_SKIPTOKEN }, { URI_ENTITY, QO_TOP },

      { URI_MEDIA_STREAM, QO_FILTER }, { URI_MEDIA_STREAM, QO_ID }, { URI_MEDIA_STREAM, QO_EXPAND },
      { URI_MEDIA_STREAM, QO_COUNT }, { URI_MEDIA_STREAM, QO_ORDERBY }, /* { URI_MEDIA_STREAM, QO_SEARCH }, */
      { URI_MEDIA_STREAM, QO_SELECT }, { URI_MEDIA_STREAM, QO_SKIP }, { URI_MEDIA_STREAM, QO_SKIPTOKEN },
      { URI_MEDIA_STREAM, QO_LEVELS }, { URI_MEDIA_STREAM, QO_TOP },

      { URI_REFERENCES, QO_ID }, { URI_REFERENCES, QO_EXPAND }, { URI_REFERENCES, QO_COUNT },
      { URI_REFERENCES, QO_SELECT }, { URI_REFERENCES, QO_LEVELS },

      { URI_REFERENCE, QO_FILTER }, { URI_REFERENCE, QO_ID }, { URI_REFERENCE, QO_EXPAND },
      { URI_REFERENCE, QO_COUNT }, { URI_REFERENCE, QO_ORDERBY }, /* { URI_REFERENCE, QO_SEARCH }, */
      { URI_REFERENCE, QO_SELECT }, { URI_REFERENCE, QO_SKIP }, { URI_REFERENCE, QO_SKIPTOKEN },
      { URI_REFERENCE, QO_LEVELS }, { URI_REFERENCE, QO_TOP },

      { URI_PROPERTY_COMPLEX, QO_FILTER }, { URI_PROPERTY_COMPLEX, QO_ID }, { URI_PROPERTY_COMPLEX, QO_COUNT },
      { URI_PROPERTY_COMPLEX, QO_ORDERBY }, /* { URI_PROPERTY_COMPLEX, QO_SEARCH }, */
      { URI_PROPERTY_COMPLEX, QO_SKIP }, { URI_PROPERTY_COMPLEX, QO_SKIPTOKEN }, { URI_PROPERTY_COMPLEX, QO_TOP },

      { URI_PROPERTY_COMPLEX_COLLECTION, QO_ID },
      /* { URI_PROPERTY_COMPLEX_COLLECTION, QO_SEARCH }, */{ URI_PROPERTY_COMPLEX_COLLECTION, QO_SELECT },

      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_FORMAT },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_ID }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_EXPAND },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_COUNT }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_ORDERBY },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SELECT },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SKIP }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SKIPTOKEN },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_LEVELS }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_TOP },

      { URI_PROPERTY_PRIMITIVE, QO_FILTER }, { URI_PROPERTY_PRIMITIVE, QO_ID }, { URI_PROPERTY_PRIMITIVE, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE, QO_COUNT }, { URI_PROPERTY_PRIMITIVE, QO_ORDERBY },
      /* { URI_PROPERTY_PRIMITIVE, QO_SEARCH }, */{ URI_PROPERTY_PRIMITIVE, QO_SELECT },
      { URI_PROPERTY_PRIMITIVE, QO_SKIP }, { URI_PROPERTY_PRIMITIVE, QO_SKIPTOKEN },
      { URI_PROPERTY_PRIMITIVE, QO_LEVELS }, { URI_PROPERTY_PRIMITIVE, QO_TOP },

      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_ID }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_COUNT }, /* { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SEARCH }, */
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_LEVELS },

      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_FORMAT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_ID }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_COUNT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_ORDERBY },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SKIPTOKEN },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_LEVELS }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_TOP },

      { URI_PROPERTY_PRIMITIVE_VALUE, QO_FILTER }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_ID },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_EXPAND }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_COUNT },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_ORDERBY },/* { URI_PROPERTY_PRIMITIVE_VALUE, QO_SEARCH }, */
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_SKIPTOKEN }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_LEVELS },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_TOP },

      { URI_SINGLETON, QO_FILTER }, { URI_SINGLETON, QO_ID }, { URI_SINGLETON, QO_COUNT },
      { URI_SINGLETON, QO_ORDERBY }, /* { URI_SINGLETON, QO_SEARCH }, */{ URI_SINGLETON, QO_SKIP },
      { URI_SINGLETON, QO_SKIPTOKEN }, { URI_SINGLETON, QO_TOP },

      { URI_NAV_ENTITY, QO_FILTER }, { URI_NAV_ENTITY, QO_ID }, { URI_NAV_ENTITY, QO_COUNT },
      { URI_NAV_ENTITY, QO_ORDERBY }, /* { URI_NAV_ENTITY, QO_SEARCH }, */{ URI_NAV_ENTITY, QO_SKIP },
      { URI_NAV_ENTITY, QO_SKIPTOKEN }, { URI_SINGLETON, QO_TOP },

      { URI_NAV_ENTITY_SET, QO_ID }
  };

  private static final Edm edm = new EdmProviderImpl(new EdmTechProvider());

  @Test
  public void validateForHttpMethods() throws Exception {
    final UriInfo uri = new Parser().parseUri(URI_ENTITY, null, null, edm);
    final UriValidator validator = new UriValidator();

    validator.validate(uri, HttpMethod.GET);
    validator.validate(uri, HttpMethod.POST);
    validator.validate(uri, HttpMethod.PUT);
    validator.validate(uri, HttpMethod.DELETE);
    validator.validate(uri, HttpMethod.PATCH);
    validator.validate(uri, HttpMethod.MERGE);
  }

  @Test
  public void validateSelect() throws Exception {
    new TestUriValidator().setEdm(edm).run(URI_ENTITY, "$select=PropertyString");
  }

  @Test
  public void validateOrderBy() throws Exception {
    final TestUriValidator testUri = new TestUriValidator().setEdm(edm);

    testUri.run(URI_ENTITY_SET, "$orderby=PropertyString");

    testUri.runEx(URI_ENTITY, "$orderby=XXXX")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void validateCountInvalid() throws Exception {
    new TestUriValidator().setEdm(edm).runEx(URI_ENTITY_SET, "$count=foo")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void validateTopInvalid() throws Exception {
    new TestUriValidator().setEdm(edm).runEx(URI_ENTITY_SET, "$top=foo")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void validateSkipInvalid() throws Exception {
    new TestUriValidator().setEdm(edm).runEx(URI_ENTITY_SET, "$skip=foo")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void validateDoubleSystemOptions() throws Exception {
    new TestUriValidator().setEdm(edm).runEx(URI_ENTITY_SET, "$skip=1&$skip=2")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void checkKeys() throws Exception {
    final TestUriValidator testUri = new TestUriValidator().setEdm(edm);

    testUri.run("ESTwoKeyNav(PropertyInt16=1, PropertyString='abc')");

    testUri.runEx("ESTwoKeyNav(xxx=1, yyy='abc')")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESCollAllPrim(null)").isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESAllPrim(PropertyInt16='1')")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESAllPrim(12345678901234567890)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString=1)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyInt16=1)")
        .isExValidation(UriValidationException.MessageKeys.DOUBLE_KEY_PROPERTY);
  }

  @Test
  public void checkValidSystemQueryOption() throws Exception {
    for (final String[] uriArray : urisWithValidSystemQueryOptions) {
      final String[] uri = constructUri(uriArray);
      try {
        new UriValidator().validate(
            new Parser().parseUri(uri[0], uri[1], null, edm),
            HttpMethod.GET);
      } catch (final UriParserException e) {
        fail("Failed for uri: " + uri[0] + '?' + uri[1]);
      } catch (final UriValidationException e) {
        fail("Failed for uri: " + uri[0] + '?' + uri[1]);
      }
    }
  }

  @Test
  public void checkNonValidSystemQueryOption() throws Exception {
    for (final String[] uriArray : urisWithNonValidSystemQueryOptions) {
      final String[] uri = constructUri(uriArray);
      try {
        new UriValidator().validate(
            new Parser().parseUri(uri[0], uri[1], null, edm),
            HttpMethod.GET);
        fail("Validation Exception not thrown: " + uri[0] + '?' + uri[1]);
      } catch (final UriParserException e) {
        fail("Wrong Exception thrown: " + uri[0] + '?' + uri[1]);
      } catch (final UriValidationException e) {
        assertEquals(UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED,
            e.getMessageKey());
      }
    }
  }

  private String[] constructUri(final String[] uriParameterArray) {
    final String path = uriParameterArray[0];
    String query = "";
    for (int i = 1; i < uriParameterArray.length; i++) {
      if (i > 1) {
        query += '&';
      }
      query += uriParameterArray[i];
    }
    return new String[] { path, query };
  }
}
