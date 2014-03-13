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
package org.apache.olingo.client.core.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.client.api.data.Link;
import org.apache.olingo.client.api.domain.ODataLinkType;

/**
 * Writes out JSON string from an entry.
 */
public class JSONEntrySerializer extends ODataJacksonSerializer<JSONEntryImpl> {

  @Override
  protected void doSerialize(final JSONEntryImpl entry, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {

    jgen.writeStartObject();

    if (entry.getMetadata() != null) {
      jgen.writeStringField(Constants.JSON_METADATA, entry.getMetadata().toASCIIString());
    }
    if (StringUtils.isNotBlank(entry.getType())) {
      jgen.writeStringField(Constants.JSON_TYPE, entry.getType());
    }
    if (entry.getId() != null) {
      jgen.writeStringField(Constants.JSON_ID, entry.getId());
    }

    if (entry.getSelfLink() != null) {
      jgen.writeStringField(Constants.JSON_READ_LINK, entry.getSelfLink().getHref());
    }

    if (entry.getEditLink() != null) {
      jgen.writeStringField(Constants.JSON_EDIT_LINK, entry.getEditLink().getHref());
    }

    if (entry.getMediaContentSource() != null) {
      jgen.writeStringField(Constants.JSON_MEDIAREAD_LINK, entry.getMediaContentSource());
    }
    if (entry.getMediaContentType() != null) {
      jgen.writeStringField(Constants.JSON_MEDIA_CONTENT_TYPE, entry.getMediaContentType());
    }

    final Map<String, List<String>> entitySetLinks = new HashMap<String, List<String>>();

    for (Link link : entry.getNavigationLinks()) {
      if (link.getInlineEntry() != null) {
        jgen.writeObjectField(link.getTitle(), link.getInlineEntry());
      } else if (link.getInlineFeed() != null) {
        jgen.writeObjectField(link.getTitle(), link.getInlineFeed());
      } else {
        ODataLinkType type = null;
        try {
          type = ODataLinkType.fromString(client, link.getRel(), link.getType());
        } catch (IllegalArgumentException e) {
          // ignore   
        }

        if (type == ODataLinkType.ENTITY_SET_NAVIGATION) {
          final List<String> uris;
          if (entitySetLinks.containsKey(link.getTitle())) {
            uris = entitySetLinks.get(link.getTitle());
          } else {
            uris = new ArrayList<String>();
            entitySetLinks.put(link.getTitle(), uris);
          }
          uris.add(link.getHref());
        } else {
          jgen.writeStringField(link.getTitle() + Constants.JSON_BIND_LINK_SUFFIX, link.getHref());
        }
      }
    }
    for (Map.Entry<String, List<String>> entitySetLink : entitySetLinks.entrySet()) {
      jgen.writeArrayFieldStart(entitySetLink.getKey() + Constants.JSON_BIND_LINK_SUFFIX);
      for (String uri : entitySetLink.getValue()) {
        jgen.writeString(uri);
      }
      jgen.writeEndArray();
    }

    for (Link link : entry.getMediaEditLinks()) {
      if (link.getTitle() == null) {
        jgen.writeStringField(Constants.JSON_MEDIAEDIT_LINK, link.getHref());
      }

      if (link.getInlineEntry() != null) {
        jgen.writeObjectField(link.getTitle(), link.getInlineEntry());
      }
      if (link.getInlineFeed() != null) {
        jgen.writeObjectField(link.getTitle(), link.getInlineFeed());
      }
    }

    if (entry.getMediaEntryProperties() == null) {
      JSONDOMTreeUtils.writeSubtree(client, jgen, entry.getContent());
    } else {
      JSONDOMTreeUtils.writeSubtree(client, jgen, entry.getMediaEntryProperties());
    }

    jgen.writeEndObject();
  }
}
