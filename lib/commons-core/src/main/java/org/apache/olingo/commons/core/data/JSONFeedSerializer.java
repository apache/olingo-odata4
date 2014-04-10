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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class JSONFeedSerializer extends AbstractJsonSerializer<JSONFeedImpl> {

  @Override
  protected void doSerialize(
          final JSONFeedImpl feed, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {
    doContainerSerialize(new Container<JSONFeedImpl>(null, null, feed), jgen, provider);
  }

  @Override
  protected void doContainerSerialize(
          final Container<JSONFeedImpl> container, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {

    final JSONFeedImpl feed = container.getObject();

    jgen.writeStartObject();

    if (serverMode) {
      if (container.getContextURL() != null) {
        jgen.writeStringField(version.compareTo(ODataServiceVersion.V40) >= 0
                ? Constants.JSON_CONTEXT : Constants.JSON_METADATA,
                container.getContextURL().toASCIIString());
      }

      if (version.compareTo(ODataServiceVersion.V40) >= 0 && StringUtils.isNotBlank(container.getMetadataETag())) {
        jgen.writeStringField(
                Constants.JSON_METADATA_ETAG,
                container.getMetadataETag());
      }
    }

    if (feed.getId() != null) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_ID), feed.getId());
    }
    if (feed.getCount() != null) {
      jgen.writeNumberField(Constants.JSON_COUNT, feed.getCount());
    }
    if (feed.getNext() != null) {
      jgen.writeStringField(Constants.JSON_NEXT_LINK, feed.getNext().toASCIIString());
    }

    jgen.writeArrayFieldStart(Constants.VALUE);
    for (Entry entry : feed.getEntries()) {
      jgen.writeObject(entry);
    }

    jgen.writeEndArray();
  }
}
