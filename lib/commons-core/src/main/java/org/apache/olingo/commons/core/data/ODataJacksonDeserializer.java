/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.core.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public abstract class ODataJacksonDeserializer<T> extends JsonDeserializer<T> {

  protected ODataServiceVersion version;

  protected String jsonType;

  protected String jsonId;

  protected String jsonETag;

  protected String jsonReadLink;

  protected String jsonEditLink;

  protected String jsonMediaEditLink;

  protected String jsonMediaReadLink;

  protected String jsonMediaContentType;

  protected String jsonMediaETag;

  protected String jsonAssociationLink;

  protected String jsonNavigationLink;

  protected abstract T doDeserialize(JsonParser jp, DeserializationContext ctxt)
          throws IOException, JsonProcessingException;

  protected String getJSONAnnotation(final String string) {
    return string.startsWith("@") ? string : "@" + string;
  }

  @Override
  public T deserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    version = (ODataServiceVersion) ctxt.findInjectableValue(ODataServiceVersion.class.getName(), null, null);

    jsonType = version.getJSONMap().get(ODataServiceVersion.JSON_TYPE);
    jsonId = version.getJSONMap().get(ODataServiceVersion.JSON_ID);
    jsonETag = version.getJSONMap().get(ODataServiceVersion.JSON_ETAG);
    jsonReadLink = version.getJSONMap().get(ODataServiceVersion.JSON_READ_LINK);
    jsonEditLink = version.getJSONMap().get(ODataServiceVersion.JSON_EDIT_LINK);
    jsonMediaReadLink = version.getJSONMap().get(ODataServiceVersion.JSON_MEDIAREAD_LINK);
    jsonMediaEditLink = version.getJSONMap().get(ODataServiceVersion.JSON_MEDIAEDIT_LINK);
    jsonMediaContentType = version.getJSONMap().get(ODataServiceVersion.JSON_MEDIA_CONTENT_TYPE);
    jsonMediaETag = version.getJSONMap().get(ODataServiceVersion.JSON_MEDIA_ETAG);
    jsonAssociationLink = version.getJSONMap().get(ODataServiceVersion.JSON_ASSOCIATION_LINK);
    jsonNavigationLink = version.getJSONMap().get(ODataServiceVersion.JSON_NAVIGATION_LINK);

    return doDeserialize(jp, ctxt);
  }

}
