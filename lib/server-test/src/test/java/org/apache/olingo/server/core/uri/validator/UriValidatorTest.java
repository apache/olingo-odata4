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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class UriValidatorTest {

  private static final String URI_ACTION_NO_RETURN = "AIRT";
  private static final String URI_ACTION_PRIM = "AIRTString";
  private static final String URI_ACTION_COLL_PRIM = "AIRTCollStringTwoParam";
  private static final String URI_ACTION_CT = "AIRTCTTwoPrimParam";
  private static final String URI_ACTION_COLL_CT = "AIRTCollCTTwoPrimParam";
  private static final String URI_ACTION_ENTITY = "AIRTESAllPrimParam";
  private static final String URI_ACTION_ES = "AIRTCollESAllPrimParam";
  // TODO: bound actions
  @SuppressWarnings("unused")
  private static final String URI_BOUND_ACTION_ENTITY_SET = "ESTwoKeyNav/Namespace1_Alias.BAESTwoKeyNavRTESTwoKeyNav";
  @SuppressWarnings("unused")
  private static final String URI_BOUND_ACTION_ENTITY = "ESAllPrim/Namespace1_Alias.BAESAllPrimRTETAllPrim";
  private static final String URI_ALL = "$all";
  private static final String URI_BATCH = "$batch";
  private static final String URI_CROSSJOIN = "$crossjoin(ESAllPrim)";
  private static final String URI_ENTITY_ID = "$entity/Namespace1_Alias.ETBase";
  private static final String URI_METADATA = "$metadata";
  private static final String URI_SERVICE = "";
  private static final String URI_ENTITY_SET = "ESAllPrim";
  private static final String URI_ENTITY_SET_COUNT = "ESAllPrim/$count";
  private static final String URI_ENTITY = "ESAllPrim(1)";
  private static final String URI_MEDIA_STREAM = "ESMedia(1)/$value";
  private static final String URI_REFERENCES = "ESAllPrim/$ref";
  private static final String URI_REFERENCE = "ESAllPrim(1)/$ref";
  private static final String URI_PROPERTY_COMPLEX = "ESCompComp(1)/PropertyComp";
  private static final String URI_PROPERTY_COMPLEX_COLLECTION =
      "ESCompCollComp(1)/PropertyComp/CollPropertyComp";
  private static final String URI_PROPERTY_COMPLEX_COLLECTION_COUNT =
      "ESCompCollComp(1)/PropertyComp/CollPropertyComp/$count";
  private static final String URI_PROPERTY_PRIMITIVE = "ESAllPrim(1)/PropertyString";
  private static final String URI_PROPERTY_PRIMITIVE_COLLECTION = "ESCollAllPrim(1)/CollPropertyString";
  private static final String URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT =
      "ESCollAllPrim(1)/CollPropertyString/$count";
  private static final String URI_PROPERTY_PRIMITIVE_VALUE = "ESAllPrim(1)/PropertyString/$value";
  private static final String URI_SINGLETON = "SI";
  private static final String URI_NAV_ENTITY = "ESKeyNav(1)/NavPropertyETKeyNavOne";
  private static final String URI_NAV_ENTITY_SET = "ESKeyNav(1)/NavPropertyETKeyNavMany";
  private static final String URI_FI_ENTITY_SET = "FICRTCollESMedia()";
  private static final String URI_FI_ENTITY = "FICRTETTwoKeyNavParam(ParameterInt16=1)";
  private static final String URI_FI_ENTITY_SET_KEY = "FICRTCollESMedia()(3)";

  private static final String QO_FILTER = "$filter='1' eq '1'";
  private static final String QO_FORMAT = "$format=bla/bla";
  private static final String QO_EXPAND = "$expand=*";
  private static final String QO_ID = "$id=ESAllPrim(1)";
  private static final String QO_COUNT = "$count=true";
  private static final String QO_ORDERBY = "$orderby=true";
  private static final String QO_SEARCH = "$search=bla";
  private static final String QO_SELECT = "$select=*";
  private static final String QO_SKIP = "$skip=3";
  private static final String QO_SKIPTOKEN = "$skiptoken=123";
  private static final String QO_TOP = "$top=1";
  private static final String QO_APPLY = "$apply=identity";

  private final String[][] urisWithValidSystemQueryOptions = {
      { URI_ALL, QO_FILTER }, { URI_ALL, QO_FORMAT }, { URI_ALL, QO_EXPAND }, { URI_ALL, QO_COUNT },
      { URI_ALL, QO_ORDERBY }, { URI_ALL, QO_SEARCH }, { URI_ALL, QO_SELECT }, { URI_ALL, QO_SKIP },
      { URI_ALL, QO_SKIPTOKEN }, { URI_ALL, QO_TOP }, { URI_ALL, QO_APPLY },

      { URI_CROSSJOIN, QO_FILTER }, { URI_CROSSJOIN, QO_FORMAT },
      { URI_CROSSJOIN, QO_EXPAND }, { URI_CROSSJOIN, QO_COUNT }, { URI_CROSSJOIN, QO_ORDERBY },
      { URI_CROSSJOIN, QO_SEARCH }, { URI_CROSSJOIN, QO_SELECT }, { URI_CROSSJOIN, QO_SKIP },
      { URI_CROSSJOIN, QO_SKIPTOKEN }, { URI_CROSSJOIN, QO_TOP }, { URI_CROSSJOIN, QO_APPLY },

      { URI_ENTITY_ID, QO_ID, QO_FORMAT }, { URI_ENTITY_ID, QO_ID }, { URI_ENTITY_ID, QO_ID, QO_EXPAND },
      { URI_ENTITY_ID, QO_ID, QO_SELECT },

      { URI_METADATA, QO_FORMAT },

      { URI_SERVICE, QO_FORMAT },

      { URI_ENTITY_SET, QO_FILTER }, { URI_ENTITY_SET, QO_FORMAT }, { URI_ENTITY_SET, QO_EXPAND },
      { URI_ENTITY_SET, QO_COUNT }, { URI_ENTITY_SET, QO_ORDERBY }, { URI_ENTITY_SET, QO_SEARCH },
      { URI_ENTITY_SET, QO_SELECT }, { URI_ENTITY_SET, QO_SKIP }, { URI_ENTITY_SET, QO_SKIPTOKEN },
      { URI_ENTITY_SET, QO_TOP }, { URI_ENTITY_SET, QO_APPLY },

      { URI_ENTITY_SET_COUNT, QO_FILTER }, { URI_ENTITY_SET_COUNT, QO_SEARCH }, { URI_ENTITY_SET_COUNT, QO_APPLY },

      { URI_ENTITY, QO_FORMAT }, { URI_ENTITY, QO_EXPAND }, { URI_ENTITY, QO_SELECT },

      { URI_REFERENCES, QO_FILTER }, { URI_REFERENCES, QO_FORMAT }, { URI_REFERENCES, QO_ORDERBY },
      { URI_REFERENCES, QO_SEARCH }, { URI_REFERENCES, QO_SKIP }, { URI_REFERENCES, QO_SKIPTOKEN },
      { URI_REFERENCES, QO_TOP }, { URI_REFERENCES, QO_COUNT },

      { URI_REFERENCE, QO_FORMAT },

      { URI_PROPERTY_COMPLEX, QO_FORMAT }, { URI_PROPERTY_COMPLEX, QO_SELECT }, { URI_PROPERTY_COMPLEX, QO_EXPAND },

      { URI_PROPERTY_COMPLEX_COLLECTION, QO_FILTER }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_FORMAT },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_EXPAND }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_COUNT },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_ORDERBY }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_SELECT },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_SKIP }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_SKIPTOKEN },
      { URI_PROPERTY_COMPLEX_COLLECTION, QO_TOP }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_APPLY },

      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_FILTER }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_APPLY },

      { URI_PROPERTY_PRIMITIVE, QO_FORMAT },

      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_FILTER }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_FORMAT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_COUNT }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_ORDERBY },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SKIP }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SKIPTOKEN },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_TOP },

      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_FILTER },

      { URI_PROPERTY_PRIMITIVE_VALUE, QO_FORMAT },

      { URI_SINGLETON, QO_FORMAT }, { URI_SINGLETON, QO_EXPAND }, { URI_SINGLETON, QO_SELECT },

      { URI_NAV_ENTITY, QO_FORMAT }, { URI_NAV_ENTITY, QO_EXPAND }, { URI_NAV_ENTITY, QO_SELECT },

      { URI_NAV_ENTITY_SET, QO_FILTER }, { URI_NAV_ENTITY_SET, QO_FORMAT }, { URI_NAV_ENTITY_SET, QO_EXPAND },
      { URI_NAV_ENTITY_SET, QO_COUNT }, { URI_NAV_ENTITY_SET, QO_ORDERBY },
      { URI_NAV_ENTITY_SET, QO_SEARCH }, { URI_NAV_ENTITY_SET, QO_SELECT }, { URI_NAV_ENTITY_SET, QO_SKIP },
      { URI_NAV_ENTITY_SET, QO_SKIPTOKEN }, { URI_NAV_ENTITY_SET, QO_TOP }, { URI_NAV_ENTITY_SET, QO_APPLY },

      { URI_FI_ENTITY_SET, QO_FILTER }, { URI_FI_ENTITY_SET, QO_FORMAT }, { URI_FI_ENTITY_SET, QO_EXPAND },
      { URI_FI_ENTITY_SET, QO_COUNT }, { URI_FI_ENTITY_SET, QO_ORDERBY }, { URI_FI_ENTITY_SET, QO_SEARCH },
      { URI_FI_ENTITY_SET, QO_SELECT }, { URI_FI_ENTITY_SET, QO_SKIP }, { URI_FI_ENTITY_SET, QO_SKIPTOKEN },
      { URI_FI_ENTITY_SET, QO_TOP }, { URI_FI_ENTITY_SET, QO_APPLY },

      { URI_FI_ENTITY, QO_FORMAT }, { URI_FI_ENTITY, QO_EXPAND }, { URI_FI_ENTITY, QO_SELECT },
      { URI_FI_ENTITY_SET_KEY, QO_FORMAT }, { URI_FI_ENTITY_SET_KEY, QO_EXPAND }, { URI_FI_ENTITY_SET_KEY, QO_SELECT },

      { "FINRTInt16()", QO_FORMAT },
      { "FICRTCollString()", QO_FORMAT },
      { "FICRTCTTwoPrim()", QO_FORMAT },
      { "FICRTCollCTTwoPrim()", QO_FORMAT }
  };

  private final String[][] urisWithNonValidSystemQueryOptions = {
      { URI_ALL, QO_ID },

      { URI_BATCH, QO_FILTER }, { URI_BATCH, QO_FORMAT }, { URI_BATCH, QO_ID }, { URI_BATCH, QO_EXPAND },
      { URI_BATCH, QO_COUNT }, { URI_BATCH, QO_ORDERBY }, { URI_BATCH, QO_SEARCH }, { URI_BATCH, QO_SELECT },
      { URI_BATCH, QO_SKIP }, { URI_BATCH, QO_SKIPTOKEN }, { URI_BATCH, QO_TOP }, { URI_BATCH, QO_APPLY },

      { URI_CROSSJOIN, QO_ID },

      { URI_ENTITY_ID, QO_ID, QO_FILTER },
      { URI_ENTITY_ID, QO_ID, QO_COUNT }, { URI_ENTITY_ID, QO_ID, QO_ORDERBY }, { URI_ENTITY_ID, QO_ID, QO_SEARCH },
      { URI_ENTITY_ID, QO_ID, QO_SKIP }, { URI_ENTITY_ID, QO_ID, QO_SKIPTOKEN }, { URI_ENTITY_ID, QO_ID, QO_TOP },
      { URI_ENTITY_ID, QO_ID, QO_APPLY },

      { URI_METADATA, QO_FILTER }, { URI_METADATA, QO_ID }, { URI_METADATA, QO_EXPAND },
      { URI_METADATA, QO_COUNT }, { URI_METADATA, QO_ORDERBY }, { URI_METADATA, QO_SEARCH },
      { URI_METADATA, QO_SELECT }, { URI_METADATA, QO_SKIP }, { URI_METADATA, QO_SKIPTOKEN },
      { URI_METADATA, QO_TOP }, { URI_METADATA, QO_APPLY },

      { URI_SERVICE, QO_FILTER }, { URI_SERVICE, QO_ID }, { URI_SERVICE, QO_EXPAND }, { URI_SERVICE, QO_COUNT },
      { URI_SERVICE, QO_ORDERBY }, { URI_SERVICE, QO_SEARCH }, { URI_SERVICE, QO_SELECT },
      { URI_SERVICE, QO_SKIP }, { URI_SERVICE, QO_SKIPTOKEN }, { URI_SERVICE, QO_TOP }, { URI_SERVICE, QO_APPLY },

      { URI_ENTITY_SET, QO_ID },

      { URI_ENTITY_SET_COUNT, QO_FORMAT }, { URI_ENTITY_SET_COUNT, QO_ID },
      { URI_ENTITY_SET_COUNT, QO_EXPAND }, { URI_ENTITY_SET_COUNT, QO_COUNT },
      { URI_ENTITY_SET_COUNT, QO_ORDERBY },
      { URI_ENTITY_SET_COUNT, QO_SELECT }, { URI_ENTITY_SET_COUNT, QO_SKIP }, { URI_ENTITY_SET_COUNT, QO_SKIPTOKEN },
      { URI_ENTITY_SET_COUNT, QO_TOP },

      { URI_ENTITY, QO_FILTER }, { URI_ENTITY, QO_ID }, { URI_ENTITY, QO_COUNT }, { URI_ENTITY, QO_ORDERBY },
      { URI_ENTITY, QO_SEARCH }, { URI_ENTITY, QO_SKIP }, { URI_ENTITY, QO_SKIPTOKEN }, { URI_ENTITY, QO_TOP },
      { URI_ENTITY, QO_APPLY },

      { URI_MEDIA_STREAM, QO_FILTER }, { URI_MEDIA_STREAM, QO_FORMAT }, { URI_MEDIA_STREAM, QO_ID },
      { URI_MEDIA_STREAM, QO_EXPAND }, { URI_MEDIA_STREAM, QO_COUNT }, { URI_MEDIA_STREAM, QO_ORDERBY },
      { URI_MEDIA_STREAM, QO_SEARCH }, { URI_MEDIA_STREAM, QO_SELECT }, { URI_MEDIA_STREAM, QO_SKIP },
      { URI_MEDIA_STREAM, QO_SKIPTOKEN }, { URI_MEDIA_STREAM, QO_TOP }, { URI_MEDIA_STREAM, QO_APPLY },

      { URI_REFERENCES, QO_ID }, { URI_REFERENCES, QO_EXPAND }, { URI_REFERENCES, QO_SELECT },
      { URI_REFERENCES, QO_APPLY },

      { URI_REFERENCE, QO_FILTER }, { URI_REFERENCE, QO_ID }, { URI_REFERENCE, QO_EXPAND },
      { URI_REFERENCE, QO_COUNT }, { URI_REFERENCE, QO_ORDERBY }, { URI_REFERENCE, QO_SEARCH },
      { URI_REFERENCE, QO_SELECT }, { URI_REFERENCE, QO_SKIP }, { URI_REFERENCE, QO_SKIPTOKEN },
      { URI_REFERENCE, QO_TOP }, { URI_REFERENCE, QO_APPLY },

      { URI_PROPERTY_COMPLEX, QO_FILTER }, { URI_PROPERTY_COMPLEX, QO_ID }, { URI_PROPERTY_COMPLEX, QO_COUNT },
      { URI_PROPERTY_COMPLEX, QO_ORDERBY }, { URI_PROPERTY_COMPLEX, QO_SEARCH },
      { URI_PROPERTY_COMPLEX, QO_SKIP }, { URI_PROPERTY_COMPLEX, QO_SKIPTOKEN }, { URI_PROPERTY_COMPLEX, QO_TOP },
      { URI_PROPERTY_COMPLEX, QO_APPLY },

      { URI_PROPERTY_COMPLEX_COLLECTION, QO_ID }, { URI_PROPERTY_COMPLEX_COLLECTION, QO_SEARCH },

      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_FORMAT },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_ID }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_EXPAND },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_COUNT }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_ORDERBY },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SEARCH }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SELECT },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SKIP }, { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_SKIPTOKEN },
      { URI_PROPERTY_COMPLEX_COLLECTION_COUNT, QO_TOP },

      { URI_PROPERTY_PRIMITIVE, QO_FILTER }, { URI_PROPERTY_PRIMITIVE, QO_ID }, { URI_PROPERTY_PRIMITIVE, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE, QO_COUNT }, { URI_PROPERTY_PRIMITIVE, QO_ORDERBY },
      { URI_PROPERTY_PRIMITIVE, QO_SEARCH }, { URI_PROPERTY_PRIMITIVE, QO_SELECT },
      { URI_PROPERTY_PRIMITIVE, QO_SKIP }, { URI_PROPERTY_PRIMITIVE, QO_SKIPTOKEN },
      { URI_PROPERTY_PRIMITIVE, QO_TOP }, { URI_PROPERTY_PRIMITIVE, QO_APPLY },

      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_ID }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SEARCH }, { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_SELECT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION, QO_APPLY },

      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_FORMAT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_ID }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_EXPAND },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_COUNT },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_ORDERBY }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SEARCH },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_SKIPTOKEN }, { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_TOP },
      { URI_PROPERTY_PRIMITIVE_COLLECTION_COUNT, QO_APPLY },

      { URI_PROPERTY_PRIMITIVE_VALUE, QO_FILTER }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_ID },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_EXPAND }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_COUNT },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_ORDERBY }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_SEARCH },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_SELECT }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_SKIP },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_SKIPTOKEN }, { URI_PROPERTY_PRIMITIVE_VALUE, QO_TOP },
      { URI_PROPERTY_PRIMITIVE_VALUE, QO_APPLY },

      { URI_SINGLETON, QO_FILTER }, { URI_SINGLETON, QO_ID }, { URI_SINGLETON, QO_COUNT },
      { URI_SINGLETON, QO_ORDERBY }, { URI_SINGLETON, QO_SEARCH }, { URI_SINGLETON, QO_SKIP },
      { URI_SINGLETON, QO_SKIPTOKEN }, { URI_SINGLETON, QO_TOP }, { URI_SINGLETON, QO_APPLY },

      { URI_NAV_ENTITY, QO_FILTER }, { URI_NAV_ENTITY, QO_ID }, { URI_NAV_ENTITY, QO_COUNT },
      { URI_NAV_ENTITY, QO_ORDERBY }, { URI_NAV_ENTITY, QO_SEARCH }, { URI_NAV_ENTITY, QO_SKIP },
      { URI_NAV_ENTITY, QO_SKIPTOKEN }, { URI_NAV_ENTITY, QO_TOP }, { URI_NAV_ENTITY, QO_APPLY },

      { URI_NAV_ENTITY_SET, QO_ID },

      { URI_FI_ENTITY_SET, QO_ID },

      { URI_FI_ENTITY, QO_FILTER }, { URI_FI_ENTITY, QO_ID }, { URI_FI_ENTITY, QO_COUNT },
      { URI_FI_ENTITY, QO_ORDERBY }, { URI_FI_ENTITY, QO_SEARCH }, { URI_FI_ENTITY, QO_SKIP },
      { URI_FI_ENTITY, QO_SKIPTOKEN }, { URI_FI_ENTITY, QO_TOP },
      { URI_FI_ENTITY_SET_KEY, QO_FILTER }, { URI_FI_ENTITY_SET_KEY, QO_ID }, { URI_FI_ENTITY_SET_KEY, QO_COUNT },
      { URI_FI_ENTITY_SET_KEY, QO_ORDERBY }, { URI_FI_ENTITY_SET_KEY, QO_SEARCH },
      { URI_FI_ENTITY_SET_KEY, QO_SKIP }, { URI_FI_ENTITY_SET_KEY, QO_SKIPTOKEN }, { URI_FI_ENTITY_SET_KEY, QO_TOP },
      { URI_FI_ENTITY_SET_KEY, QO_APPLY }
  };

  private final String[][] actionWithValidSystemQueryOptions = {
      // NoReturnType
      { URI_ACTION_NO_RETURN, QO_FORMAT },
      // PrimReturnType
      { URI_ACTION_PRIM, QO_FORMAT },
      // PrimCollectionReturnType
      { URI_ACTION_COLL_PRIM, QO_FILTER }, { URI_ACTION_COLL_PRIM, QO_FORMAT },
      { URI_ACTION_COLL_PRIM, QO_COUNT }, { URI_ACTION_COLL_PRIM, QO_ORDERBY },
      { URI_ACTION_COLL_PRIM, QO_SKIP }, { URI_ACTION_COLL_PRIM, QO_SKIPTOKEN },
      { URI_ACTION_COLL_PRIM, QO_TOP },
      // ComplexReturnType
      { URI_ACTION_CT, QO_FORMAT }, { URI_ACTION_CT, QO_SELECT }, { URI_ACTION_CT, QO_EXPAND },
      // ComplexCollectionReturnType
      { URI_ACTION_COLL_CT, QO_FILTER }, { URI_ACTION_COLL_CT, QO_FORMAT },
      { URI_ACTION_COLL_CT, QO_EXPAND }, { URI_ACTION_COLL_CT, QO_COUNT },
      { URI_ACTION_COLL_CT, QO_ORDERBY }, { URI_ACTION_COLL_CT, QO_SELECT },
      { URI_ACTION_COLL_CT, QO_SKIP }, { URI_ACTION_COLL_CT, QO_SKIPTOKEN },
      { URI_ACTION_COLL_CT, QO_TOP }, { URI_ACTION_COLL_CT, QO_APPLY },
      // EntityReturnType
      { URI_ACTION_ENTITY, QO_FORMAT }, { URI_ACTION_ENTITY, QO_EXPAND }, { URI_ACTION_ENTITY, QO_SELECT },
      // EntityCollectionReturnType
      { URI_ACTION_ES, QO_FORMAT }, { URI_ACTION_ES, QO_FILTER },
      { URI_ACTION_ES, QO_COUNT }, { URI_ACTION_ES, QO_ORDERBY }, { URI_ACTION_ES, QO_SEARCH },
      { URI_ACTION_ES, QO_SELECT }, { URI_ACTION_ES, QO_SKIP }, { URI_ACTION_ES, QO_SKIPTOKEN },
      { URI_ACTION_ES, QO_TOP }, { URI_ACTION_ES, QO_APPLY }
  };

  private final String[][] actionsWithNotValidSystemQueryOptions = {
      // NoReturnType
      { URI_ACTION_NO_RETURN, QO_FILTER }, { URI_ACTION_NO_RETURN, QO_ID },
      { URI_ACTION_NO_RETURN, QO_EXPAND }, { URI_ACTION_NO_RETURN, QO_COUNT },
      { URI_ACTION_NO_RETURN, QO_ORDERBY }, { URI_ACTION_NO_RETURN, QO_SEARCH },
      { URI_ACTION_NO_RETURN, QO_SELECT }, { URI_ACTION_NO_RETURN, QO_SKIP },
      { URI_ACTION_NO_RETURN, QO_SKIPTOKEN }, { URI_ACTION_NO_RETURN, QO_TOP }, { URI_ACTION_NO_RETURN, QO_APPLY },
      // PrimReturnType
      { URI_ACTION_PRIM, QO_FILTER }, { URI_ACTION_PRIM, QO_ID },
      { URI_ACTION_PRIM, QO_EXPAND }, { URI_ACTION_PRIM, QO_COUNT },
      { URI_ACTION_PRIM, QO_ORDERBY }, { URI_ACTION_PRIM, QO_SEARCH },
      { URI_ACTION_PRIM, QO_SELECT }, { URI_ACTION_PRIM, QO_SKIP },
      { URI_ACTION_PRIM, QO_SKIPTOKEN }, { URI_ACTION_PRIM, QO_TOP }, { URI_ACTION_PRIM, QO_APPLY },
      // PrimCollectionReturnType
      { URI_ACTION_COLL_PRIM, QO_ID }, { URI_ACTION_COLL_PRIM, QO_EXPAND },
      { URI_ACTION_COLL_PRIM, QO_SEARCH }, { URI_ACTION_COLL_PRIM, QO_SELECT }, { URI_ACTION_COLL_PRIM, QO_APPLY },
      // ComplexReturnType
      { URI_ACTION_CT, QO_FILTER }, { URI_ACTION_CT, QO_ID }, { URI_ACTION_CT, QO_COUNT },
      { URI_ACTION_CT, QO_ORDERBY }, { URI_ACTION_CT, QO_SEARCH },
      { URI_ACTION_CT, QO_SKIP }, { URI_ACTION_CT, QO_SKIPTOKEN }, { URI_ACTION_CT, QO_TOP },
      { URI_ACTION_CT, QO_APPLY },
      // ComplexCollectionReturnType
      { URI_ACTION_COLL_CT, QO_ID }, { URI_ACTION_COLL_CT, QO_SEARCH },
      // EntityReturnType
      { URI_ACTION_ENTITY, QO_FILTER }, { URI_ACTION_ENTITY, QO_ID }, { URI_ACTION_ENTITY, QO_COUNT },
      { URI_ACTION_ENTITY, QO_ORDERBY }, { URI_ACTION_ENTITY, QO_SEARCH },
      { URI_ACTION_ENTITY, QO_SKIP }, { URI_ACTION_ENTITY, QO_SKIPTOKEN }, { URI_ACTION_ENTITY, QO_TOP },
      { URI_ACTION_ENTITY, QO_APPLY },
      // EntityCollectionReturnType
      { URI_ACTION_ES, QO_ID }
  };

  private static final OData odata = OData.newInstance();
  private static final Edm edm = odata.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  @Test
  public void serviceDocumentMustNotFailForAllHttpMethods() throws Exception {
    // We must not fail with a nullpointer here as the HTTP method to resource validation happens in the dispatcher.
    for (final HttpMethod method : HttpMethod.values()) {
      validate(URI_SERVICE, null, method);
    }
  }

  @Test
  public void validateForHttpMethods() throws Exception {
    for (final HttpMethod method : HttpMethod.values()) {
      validate(URI_ENTITY, null, method);
    }
  }

  @Test
  public void systemQueryOptionsNotAllowedForHttpPostPutPatchDelete() throws Exception {
    final String[] queryOptions =
        { QO_FILTER, QO_FORMAT, QO_EXPAND, QO_COUNT, QO_ORDERBY, QO_SEARCH, QO_SELECT, QO_SKIP, QO_TOP, QO_SKIPTOKEN };
    for (int i = 0; i < queryOptions.length; i++) {
      validateWrong(URI_ENTITY, queryOptions[i], HttpMethod.POST,
          UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);
      validateWrong(URI_ENTITY, queryOptions[i], HttpMethod.PUT,
          UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);
      validateWrong(URI_ENTITY, queryOptions[i], HttpMethod.PATCH,
          UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);
      validateWrong(URI_ENTITY, queryOptions[i], HttpMethod.DELETE,
          UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);
    }
  }

  @Test
  public void systemQueryOptionIDAllowedForDELETEReferencesOnly() throws Exception {
    validate(URI_REFERENCES, QO_ID, HttpMethod.DELETE);

    validateWrong(URI_REFERENCES, QO_ID, HttpMethod.POST,
        UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);
    validateWrong(URI_REFERENCES, QO_ID, HttpMethod.PUT,
        UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);
    validateWrong(URI_REFERENCES, QO_ID, HttpMethod.PATCH,
        UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);

    validateWrong(URI_ENTITY, QO_ID, HttpMethod.DELETE,
        UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED_FOR_HTTP_METHOD);
  }

  @Test
  public void checkKeys() throws Exception {
    final TestUriValidator testUri = new TestUriValidator().setEdm(edm);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='abc')");

    testUri.runEx("ESTwoKeyNav(xxx=1,yyy='abc')")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESCollAllPrim(null)").isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESAllPrim(PropertyInt16='1')")
        .isExSemantic(UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESAllPrim(12345678901234567890)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString=1)")
        .isExSemantic(UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyInt16=1)")
        .isExValidation(UriValidationException.MessageKeys.DOUBLE_KEY_PROPERTY);

    testUri.runEx("ESAllPrim(0)/NavPropertyETTwoPrimMany(xxx=42)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
    testUri.runEx("ESAllPrim(0)/NavPropertyETTwoPrimMany(PropertyInt16='1')")
        .isExSemantic(UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE);
  }

  @Test
  public void checkValidSystemQueryOption() throws Exception {
    for (final String[] uriArray : urisWithValidSystemQueryOptions) {
      final String[] uri = constructUri(uriArray);
      validate(uri[0], uri[1], HttpMethod.GET);
    }
  }

  @Test
  public void checkNonValidSystemQueryOption() throws Exception {
    for (final String[] uriArray : urisWithNonValidSystemQueryOptions) {
      final String[] uri = constructUri(uriArray);
      validateWrong(uri[0], uri[1], HttpMethod.GET,
          UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED);
    }
  }

  @Test
  public void validatePostOnActionSystemQueryOptions() throws Exception {
    for (final String[] uriArray : actionWithValidSystemQueryOptions) {
      final String[] uri = constructUri(uriArray);
      validate(uri[0], uri[1], HttpMethod.POST);
    }
  }

  @Test
  public void checkPostOnActionSystemQueryOptionsNotValid() throws Exception {
    for (final String[] uriArray : actionsWithNotValidSystemQueryOptions) {
      final String[] uri = constructUri(uriArray);
      validateWrong(uri[0], uri[1], HttpMethod.POST,
          UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED);
    }
  }

  @Test
  public void propertyOperations() throws Exception {
    validateWrong(URI_PROPERTY_PRIMITIVE_COLLECTION, null, HttpMethod.PATCH,
        UriValidationException.MessageKeys.UNSUPPORTED_HTTP_METHOD);
    validateWrong(URI_PROPERTY_COMPLEX_COLLECTION, null, HttpMethod.PATCH,
        UriValidationException.MessageKeys.UNSUPPORTED_HTTP_METHOD);
    validateWrong("ESKeyNav(1)/PropertyString", null, HttpMethod.DELETE,
        UriValidationException.MessageKeys.UNSUPPORTED_HTTP_METHOD);
    validateWrong("ESKeyNav(1)/PropertyString/$value", null, HttpMethod.DELETE,
        UriValidationException.MessageKeys.UNSUPPORTED_HTTP_METHOD);
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

  private void validate(final String path, final String query, final HttpMethod method) {
    try {
      new UriValidator().validate(new Parser(edm, odata).parseUri(path, query, null, null), method);
    } catch (final UriParserException e) {
      fail("Failed for " + method + " on URI: " + path + '?' + query);
    } catch (final UriValidationException e) {
      fail("Failed for " + method + " on URI: " + path + '?' + query);
    }
  }

  private void validateWrong(final String path, final String query, final HttpMethod method,
      final UriValidationException.MessageKeys expectedMessageKey) {
    try {
      new UriValidator().validate(new Parser(edm, odata).parseUri(path, query, null, null), method);
      fail("Validation Exception not thrown: " + method + ' ' + path + '?' + query);
    } catch (final UriParserException e) {
      fail("Wrong Exception thrown: " + method + ' ' + path + '?' + query);
    } catch (final UriValidationException e) {
      assertEquals(expectedMessageKey, e.getMessageKey());
    }
  }
}
