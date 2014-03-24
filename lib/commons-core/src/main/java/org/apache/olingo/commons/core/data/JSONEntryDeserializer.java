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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

/**
 * Reads JSON string into an entry.
 * <br/>
 * If metadata information is available, the corresponding entry fields and content will be populated.
 */
public class JSONEntryDeserializer extends AbstractJsonDeserializer<JSONEntryImpl> {

  private String getTitle(final Map.Entry<String, JsonNode> entry) {
    return entry.getKey().substring(0, entry.getKey().indexOf('@'));
  }

  private String setInline(final String name, final String suffix, final ObjectNode tree,
          final ObjectCodec codec, final LinkImpl link) throws IOException {

    final String entryNamePrefix = name.substring(0, name.indexOf(suffix));
    if (tree.has(entryNamePrefix)) {
      final JsonNode inline = tree.path(entryNamePrefix);

      if (inline instanceof ObjectNode) {
        link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
        link.setInlineEntry(inline.traverse(codec).readValuesAs(JSONEntryImpl.class).next());
      }

      if (inline instanceof ArrayNode) {
        link.setType(ODataLinkType.ENTITY_SET_NAVIGATION.toString());

        final JSONFeedImpl feed = new JSONFeedImpl();
        final Iterator<JsonNode> entries = ((ArrayNode) inline).elements();
        while (entries.hasNext()) {
          feed.getEntries().add(entries.next().traverse(codec).readValuesAs(JSONEntryImpl.class).next());
        }

        link.setInlineFeed(feed);
      }
    }
    return entryNamePrefix;
  }

  @Override
  protected JSONEntryImpl doDeserialize(final JsonParser parser, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    if (tree.has(Constants.JSON_VALUE) && tree.get(Constants.JSON_VALUE).isArray()) {
      throw new JsonParseException("Expected OData Entity, found EntitySet", parser.getCurrentLocation());
    }

    final JSONEntryImpl entry = new JSONEntryImpl();

    if (tree.hasNonNull(Constants.JSON_METADATA)) {
      entry.setMetadata(URI.create(tree.get(Constants.JSON_METADATA).textValue()));
      tree.remove(Constants.JSON_METADATA);
    }

    if (tree.hasNonNull(Constants.JSON_MEDIA_ETAG)) {
      entry.setMediaETag(tree.get(Constants.JSON_MEDIA_ETAG).textValue());
      tree.remove(Constants.JSON_MEDIA_ETAG);
    }

    if (tree.hasNonNull(Constants.JSON_ETAG)) {
      entry.setETag(tree.get(Constants.JSON_ETAG).textValue());
      tree.remove(Constants.JSON_ETAG);
    }

    if (tree.hasNonNull(Constants.JSON_TYPE)) {
      entry.setType(tree.get(Constants.JSON_TYPE).textValue());
      tree.remove(Constants.JSON_TYPE);
    }

    if (tree.hasNonNull(Constants.JSON_ID)) {
      try {
        entry.setId(tree.get(Constants.JSON_ID).textValue());
      } catch (ParseException e) {
        throw new JsonMappingException("While parsing Atom entry or feed common elements", e);
      }
      tree.remove(Constants.JSON_ID);
    }

    if (tree.hasNonNull(Constants.JSON_READ_LINK)) {
      final LinkImpl link = new LinkImpl();
      link.setRel(Constants.SELF_LINK_REL);
      link.setHref(tree.get(Constants.JSON_READ_LINK).textValue());
      entry.setSelfLink(link);

      tree.remove(Constants.JSON_READ_LINK);
    }

    if (tree.hasNonNull(Constants.JSON_EDIT_LINK)) {
      final LinkImpl link = new LinkImpl();
      link.setRel(Constants.EDIT_LINK_REL);
      link.setHref(tree.get(Constants.JSON_EDIT_LINK).textValue());
      entry.setEditLink(link);

      tree.remove(Constants.JSON_EDIT_LINK);
    }

    if (tree.hasNonNull(Constants.JSON_MEDIAREAD_LINK)) {
      entry.setMediaContentSource(tree.get(Constants.JSON_MEDIAREAD_LINK).textValue());
      tree.remove(Constants.JSON_MEDIAREAD_LINK);
    }
    if (tree.hasNonNull(Constants.JSON_MEDIAEDIT_LINK)) {
      tree.remove(Constants.JSON_MEDIAEDIT_LINK);
    }
    if (tree.hasNonNull(Constants.JSON_MEDIA_CONTENT_TYPE)) {
      entry.setMediaContentType(tree.get(Constants.JSON_MEDIA_CONTENT_TYPE).textValue());
      tree.remove(Constants.JSON_MEDIA_CONTENT_TYPE);
    }

    final Set<String> toRemove = new HashSet<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      if (field.getKey().endsWith(Constants.JSON_NAVIGATION_LINK_SUFFIX)) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(version.getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL) + getTitle(field));

        if (field.getValue().isValueNode()) {
          link.setHref(field.getValue().textValue());
          link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
        }
        // NOTE: this should be expected to happen, but it isn't - at least up to OData 4.0
                /* if (field.getValue().isArray()) {
         * link.setHref(field.getValue().asText());
         * link.setType(ODataLinkType.ENTITY_SET_NAVIGATION.toString());
         * } */

        entry.getNavigationLinks().add(link);

        toRemove.add(field.getKey());
        toRemove.add(setInline(field.getKey(), Constants.JSON_NAVIGATION_LINK_SUFFIX, tree, parser.getCodec(), link));
      } else if (field.getKey().endsWith(Constants.JSON_ASSOCIATION_LINK_SUFFIX)) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(version.getNamespaceMap().get(ODataServiceVersion.ASSOCIATION_LINK_REL) + getTitle(field));
        link.setHref(field.getValue().textValue());
        link.setType(ODataLinkType.ASSOCIATION.toString());
        entry.getAssociationLinks().add(link);

        toRemove.add(field.getKey());
      } else if (field.getKey().endsWith(Constants.JSON_MEDIAEDIT_LINK_SUFFIX)) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(version.getNamespaceMap().get(ODataServiceVersion.MEDIA_EDIT_LINK_REL) + getTitle(field));
        link.setHref(field.getValue().textValue());
        link.setType(ODataLinkType.MEDIA_EDIT.toString());
        entry.getMediaEditLinks().add(link);

        if (tree.has(link.getTitle() + Constants.JSON_MEDIA_ETAG_SUFFIX)) {
          link.setMediaETag(tree.get(link.getTitle() + Constants.JSON_MEDIA_ETAG_SUFFIX).asText());
          toRemove.add(link.getTitle() + Constants.JSON_MEDIA_ETAG_SUFFIX);
        }

        toRemove.add(field.getKey());
        toRemove.add(setInline(field.getKey(), Constants.JSON_MEDIAEDIT_LINK_SUFFIX, tree, parser.getCodec(), link));
      } else if (field.getKey().endsWith(Constants.JSON_MEDIA_CONTENT_TYPE)) {
        final String linkTitle = getTitle(field);
        for (Link link : entry.getMediaEditLinks()) {
          if (linkTitle.equals(link.getTitle())) {
            ((LinkImpl) link).setType(field.getValue().asText());
          }
        }
        toRemove.add(field.getKey());
      } else if (field.getKey().charAt(0) == '#') {
        final ODataOperation operation = new ODataOperation();
        operation.setMetadataAnchor(field.getKey());

        final ObjectNode opNode = (ObjectNode) tree.get(field.getKey());
        operation.setTitle(opNode.get(Constants.ATTR_TITLE).asText());
        operation.setTarget(URI.create(opNode.get(Constants.ATTR_TARGET).asText()));

        entry.getOperations().add(operation);

        toRemove.add(field.getKey());
      }
    }
    tree.remove(toRemove);

    String type = null;
    for (final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      if (type == null && field.getKey().endsWith(Constants.JSON_TYPE_SUFFIX)) {
        type = field.getValue().asText();
      } else {
        final JSONPropertyImpl property = new JSONPropertyImpl();
        property.setName(field.getKey());
        property.setType(type);
        type = null;

        value(property, field.getValue());
        entry.getProperties().add(property);
      }
    }

    return entry;
  }
}
