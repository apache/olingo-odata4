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
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

/**
 * Writes out JSON string from an entry.
 */
public class JSONEntrySerializer extends AbstractJsonSerializer<JSONEntryImpl> {

  @Override
  protected void doSerialize(final JSONEntryImpl entry, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {
    doContainerSerialize(new Container<JSONEntryImpl>(null, null, entry), jgen, provider);
  }

  @Override
  protected void doContainerSerialize(
          final Container<JSONEntryImpl> container, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {

    final Entry entry = container.getObject();

    jgen.writeStartObject();

    if (StringUtils.isNotBlank(entry.getType())) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_TYPE),
              new EdmTypeInfo.Builder().setTypeExpression(entry.getType()).build().external(version));
    }

    if (serverMode) {
      if (version.compareTo(ODataServiceVersion.V40) >= 0 && StringUtils.isNotBlank(container.getMetadataETag())) {
        jgen.writeStringField(
                Constants.JSON_METADATA_ETAG,
                container.getMetadataETag());
      }

      if (StringUtils.isNotBlank(entry.getETag())) {
        jgen.writeStringField(
                version.getJSONMap().get(ODataServiceVersion.JSON_ETAG),
                entry.getETag());
      }
    }

    if (entry.getId() != null) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_ID), entry.getId());
    }

    for (Property property : entry.getProperties()) {
      property(jgen, property, property.getName());
    }

    if (serverMode && entry.getEditLink() != null && StringUtils.isNotBlank(entry.getEditLink().getHref())) {
      final URI link = URI.create(entry.getEditLink().getHref());
      final String editLink = link.isAbsolute() ? link.toASCIIString()
              : URI.create(entry.getBaseURI() + "/" + link.toASCIIString()).normalize().toASCIIString();

      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_EDIT_LINK), editLink);
    }

    links(entry, jgen);

    for (Link link : entry.getMediaEditLinks()) {
      if (link.getTitle() == null) {
        jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_MEDIAEDIT_LINK), link.getHref());
      }

      if (link.getInlineEntry() != null) {
        jgen.writeObjectField(link.getTitle(), link.getInlineEntry());
      }
      if (link.getInlineFeed() != null) {
        jgen.writeArrayFieldStart(link.getTitle());
        for (Entry subEntry : link.getInlineFeed().getEntries()) {
          jgen.writeObject(subEntry);
        }
        jgen.writeEndArray();
      }
    }

    jgen.writeEndObject();
  }
}
