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
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

/**
 * Writes out JSON string from an entry.
 */
public class JSONEntrySerializer extends AbstractJsonSerializer<JSONEntryImpl> {

  @Override
  protected void doSerialize(final JSONEntryImpl entry, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {

    jgen.writeStartObject();

    if (entry.getId() != null) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_ID), entry.getId());
    }

    for (Property property : entry.getProperties()) {
      property(jgen, property, property.getName());
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
