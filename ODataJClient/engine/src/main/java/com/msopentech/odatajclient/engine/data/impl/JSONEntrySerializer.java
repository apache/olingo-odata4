/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.data.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.msopentech.odatajclient.engine.data.ODataLinkType;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONLink;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONEntry;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Writes out JSON string from <tt>JSONV3Entry</tt> and <tt>JSONV4Entry</tt>.
 *
 * @see JSONEntry
 * @see JSONV4Entry
 */
public class JSONEntrySerializer extends ODataJacksonSerializer<AbstractJSONEntry> {

    @Override
    protected void doSerialize(
            final AbstractJSONEntry entry, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        if (entry.getMetadata() != null) {
            jgen.writeStringField(ODataConstants.JSON_METADATA, entry.getMetadata().toASCIIString());
        }
        if (StringUtils.isNotBlank(entry.getType())) {
            jgen.writeStringField(ODataConstants.JSON_TYPE, entry.getType());
        }
        if (entry.getId() != null) {
            jgen.writeStringField(ODataConstants.JSON_ID, entry.getId());
        }

        if (entry.getSelfLink() != null) {
            jgen.writeStringField(ODataConstants.JSON_READ_LINK, entry.getSelfLink().getHref());
        }

        if (entry.getEditLink() != null) {
            jgen.writeStringField(ODataConstants.JSON_EDIT_LINK, entry.getEditLink().getHref());
        }

        if (entry.getMediaContentSource() != null) {
            jgen.writeStringField(ODataConstants.JSON_MEDIAREAD_LINK, entry.getMediaContentSource());
        }
        if (entry.getMediaContentType() != null) {
            jgen.writeStringField(ODataConstants.JSON_MEDIA_CONTENT_TYPE, entry.getMediaContentType());
        }

        final Map<String, List<String>> entitySetLinks = new HashMap<String, List<String>>();

        for (JSONLink link : entry.getNavigationLinks()) {
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
                    jgen.writeStringField(link.getTitle() + ODataConstants.JSON_BIND_LINK_SUFFIX, link.getHref());
                }
            }
        }
        for (Map.Entry<String, List<String>> entitySetLink : entitySetLinks.entrySet()) {
            jgen.writeArrayFieldStart(entitySetLink.getKey() + ODataConstants.JSON_BIND_LINK_SUFFIX);
            for (String uri : entitySetLink.getValue()) {
                jgen.writeString(uri);
            }
            jgen.writeEndArray();
        }

        for (JSONLink link : entry.getMediaEditLinks()) {
            if (link.getTitle() == null) {
                jgen.writeStringField(ODataConstants.JSON_MEDIAEDIT_LINK, link.getHref());
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
