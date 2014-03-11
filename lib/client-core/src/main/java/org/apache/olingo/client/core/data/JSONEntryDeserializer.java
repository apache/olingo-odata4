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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.olingo.client.api.ODataConstants;
import org.apache.olingo.client.api.domain.ODataLinkType;
import org.apache.olingo.client.api.domain.ODataOperation;
import org.apache.olingo.client.api.utils.XMLUtils;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads JSON string into an entry.
 * <br/>
 * If metadata information is available, the corresponding entry fields and content will be populated.
 */
public class JSONEntryDeserializer extends ODataJacksonDeserializer<JSONEntryImpl> {

  private String getTitle(final Map.Entry<String, JsonNode> entry) {
    return entry.getKey().substring(0, entry.getKey().indexOf('@'));
  }

  private String setInline(final String name, final String suffix, final ObjectNode tree,
          final ObjectCodec codec, final LinkImpl link) throws IOException {

    final String entryNamePrefix = name.substring(0, name.indexOf(suffix));
    if (tree.has(entryNamePrefix)) {
      final JsonNode inline = tree.path(entryNamePrefix);

      if (inline instanceof ObjectNode) {
        final JsonParser inlineParser = inline.traverse();
        inlineParser.setCodec(codec);
        link.setInlineEntry(inlineParser.readValuesAs(JSONEntryImpl.class).next());
      }

      if (inline instanceof ArrayNode) {
        final JSONFeedImpl feed = new JSONFeedImpl();
        final Iterator<JsonNode> entries = ((ArrayNode) inline).elements();
        while (entries.hasNext()) {
          final JsonParser inlineParser = entries.next().traverse();
          inlineParser.setCodec(codec);
          feed.getEntries().add(inlineParser.readValuesAs(JSONEntryImpl.class).next());
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

    if (tree.has(ODataConstants.JSON_VALUE) && tree.get(ODataConstants.JSON_VALUE).isArray()) {
      throw new JsonParseException("Expected OData Entity, found EntitySet", parser.getCurrentLocation());
    }

    final boolean isMediaEntry =
            tree.hasNonNull(ODataConstants.JSON_MEDIAREAD_LINK)
            && tree.hasNonNull(ODataConstants.JSON_MEDIA_CONTENT_TYPE);

    final JSONEntryImpl entry = new JSONEntryImpl();

    if (tree.hasNonNull(ODataConstants.JSON_METADATA)) {
      entry.setMetadata(URI.create(tree.get(ODataConstants.JSON_METADATA).textValue()));
      tree.remove(ODataConstants.JSON_METADATA);
    }

    if (tree.hasNonNull(ODataConstants.JSON_MEDIA_ETAG)) {
      entry.setMediaETag(tree.get(ODataConstants.JSON_MEDIA_ETAG).textValue());
      tree.remove(ODataConstants.JSON_MEDIA_ETAG);
    }

    if (tree.hasNonNull(ODataConstants.JSON_ETAG)) {
      entry.setETag(tree.get(ODataConstants.JSON_ETAG).textValue());
      tree.remove(ODataConstants.JSON_ETAG);
    }

    if (tree.hasNonNull(ODataConstants.JSON_TYPE)) {
      entry.setType(tree.get(ODataConstants.JSON_TYPE).textValue());
      tree.remove(ODataConstants.JSON_TYPE);
    }

    if (tree.hasNonNull(ODataConstants.JSON_ID)) {
      entry.setId(tree.get(ODataConstants.JSON_ID).textValue());
      tree.remove(ODataConstants.JSON_ID);
    }

    if (tree.hasNonNull(ODataConstants.JSON_READ_LINK)) {
      final LinkImpl link = new LinkImpl();
      link.setRel(ODataConstants.SELF_LINK_REL);
      link.setHref(tree.get(ODataConstants.JSON_READ_LINK).textValue());
      entry.setSelfLink(link);

      tree.remove(ODataConstants.JSON_READ_LINK);
    }

    if (tree.hasNonNull(ODataConstants.JSON_EDIT_LINK)) {
      final LinkImpl link = new LinkImpl();
      link.setRel(ODataConstants.EDIT_LINK_REL);
      link.setHref(tree.get(ODataConstants.JSON_EDIT_LINK).textValue());
      entry.setEditLink(link);

      tree.remove(ODataConstants.JSON_EDIT_LINK);
    }

    if (tree.hasNonNull(ODataConstants.JSON_MEDIAREAD_LINK)) {
      entry.setMediaContentSource(tree.get(ODataConstants.JSON_MEDIAREAD_LINK).textValue());
      tree.remove(ODataConstants.JSON_MEDIAREAD_LINK);
    }
    if (tree.hasNonNull(ODataConstants.JSON_MEDIAEDIT_LINK)) {
      /*final LinkImpl link = new LinkImpl();
      link.setHref(tree.get(ODataConstants.JSON_MEDIAEDIT_LINK).textValue());
      entry.getMediaEditLinks().add(link);*/

      tree.remove(ODataConstants.JSON_MEDIAEDIT_LINK);
    }
    if (tree.hasNonNull(ODataConstants.JSON_MEDIA_CONTENT_TYPE)) {
      entry.setMediaContentType(tree.get(ODataConstants.JSON_MEDIA_CONTENT_TYPE).textValue());
      tree.remove(ODataConstants.JSON_MEDIA_CONTENT_TYPE);
    }

    final Set<String> toRemove = new HashSet<String>();
    final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields();
    while (itor.hasNext()) {
      final Map.Entry<String, JsonNode> field = itor.next();

      if (field.getKey().endsWith(ODataConstants.JSON_NAVIGATION_LINK_SUFFIX)) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(client.getServiceVersion().getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL)
                + getTitle(field));
        if (field.getValue().isValueNode()) {
          link.setHref(field.getValue().textValue());
          link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
        }
        // NOTE: this should be expected to happen, but it isn't - at least up to OData 4.0
                /* if (field.getValue().isArray()) {
         * link.setHref(field.getValue().asText());
         * link.setType(LinkType.ENTITY_SET_NAVIGATION.toString());
         * } */
        entry.getNavigationLinks().add(link);

        toRemove.add(field.getKey());
        toRemove.add(setInline(field.getKey(),
                ODataConstants.JSON_NAVIGATION_LINK_SUFFIX, tree, parser.getCodec(), link));
      } else if (field.getKey().endsWith(ODataConstants.JSON_ASSOCIATION_LINK_SUFFIX)) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(client.getServiceVersion().getNamespaceMap().get(ODataServiceVersion.ASSOCIATION_LINK_REL)
                + getTitle(field));
        link.setHref(field.getValue().textValue());
        link.setType(ODataLinkType.ASSOCIATION.toString());
        entry.getAssociationLinks().add(link);

        toRemove.add(field.getKey());
      } else if (field.getKey().endsWith(ODataConstants.JSON_MEDIAEDIT_LINK_SUFFIX)) {
        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(client.getServiceVersion().getNamespaceMap().get(ODataServiceVersion.MEDIA_EDIT_LINK_REL)
                + getTitle(field));
        link.setHref(field.getValue().textValue());
        link.setType(ODataLinkType.MEDIA_EDIT.toString());
        entry.getMediaEditLinks().add(link);

        toRemove.add(field.getKey());
        toRemove.add(setInline(field.getKey(),
                ODataConstants.JSON_MEDIAEDIT_LINK_SUFFIX, tree, parser.getCodec(), link));
      } else if (field.getKey().charAt(0) == '#') {
        final ODataOperation operation = new ODataOperation();
        operation.setMetadataAnchor(field.getKey());

        final ObjectNode opNode = (ObjectNode) tree.get(field.getKey());
        operation.setTitle(opNode.get(ODataConstants.ATTR_TITLE).asText());
        operation.setTarget(URI.create(opNode.get(ODataConstants.ATTR_TARGET).asText()));

        entry.getOperations().add(operation);

        toRemove.add(field.getKey());
      }
    }
    tree.remove(toRemove);

    try {
      final DocumentBuilder builder = XMLUtils.DOC_BUILDER_FACTORY.newDocumentBuilder();
      final Document document = builder.newDocument();

      final Element properties = document.createElementNS(
              client.getServiceVersion().getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              ODataConstants.ELEM_PROPERTIES);

      JSONDOMTreeUtils.buildSubtree(client, properties, tree);

      if (isMediaEntry) {
        entry.setMediaEntryProperties(properties);
      } else {
        entry.setContent(properties);
      }
    } catch (ParserConfigurationException e) {
      throw new JsonParseException("Cannot build entry content", parser.getCurrentLocation(), e);
    }

    return entry;
  }
}
