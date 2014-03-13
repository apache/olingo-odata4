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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.client.api.domain.ODataJClientEdmPrimitiveType;
import org.apache.olingo.client.api.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Parse JSON string into <tt>JSONProperty</tt>.
 *
 * @see JSONProperty
 */
public class JSONPropertyDeserializer extends ODataJacksonDeserializer<JSONPropertyImpl> {

  @Override
  protected JSONPropertyImpl doDeserialize(final JsonParser parser, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    final JSONPropertyImpl property = new JSONPropertyImpl();

    if (tree.hasNonNull(Constants.JSON_METADATA)) {
      property.setMetadata(URI.create(tree.get(Constants.JSON_METADATA).textValue()));
      tree.remove(Constants.JSON_METADATA);
    }

    try {
      final DocumentBuilder builder = XMLUtils.DOC_BUILDER_FACTORY.newDocumentBuilder();
      final Document document = builder.newDocument();

      Element content = document.createElement(Constants.ELEM_PROPERTY);

      if (property.getMetadata() != null) {
        final String metadataURI = property.getMetadata().toASCIIString();
        final int dashIdx = metadataURI.lastIndexOf('#');
        if (dashIdx != -1) {
          content.setAttribute(Constants.ATTR_M_TYPE, metadataURI.substring(dashIdx + 1));
        }
      }

      JsonNode subtree = null;
      if (tree.has(Constants.JSON_VALUE)) {
        if (tree.has(Constants.JSON_TYPE)
                && StringUtils.isBlank(content.getAttribute(Constants.ATTR_M_TYPE))) {

          content.setAttribute(Constants.ATTR_M_TYPE, tree.get(Constants.JSON_TYPE).asText());
        }

        final JsonNode value = tree.get(Constants.JSON_VALUE);
        if (value.isValueNode()) {
          content.appendChild(document.createTextNode(value.asText()));
        } else if (ODataJClientEdmPrimitiveType.isGeospatial(content.getAttribute(Constants.ATTR_M_TYPE))) {
          subtree = tree.objectNode();
          ((ObjectNode) subtree).put(Constants.JSON_VALUE, tree.get(Constants.JSON_VALUE));
          if (StringUtils.isNotBlank(content.getAttribute(Constants.ATTR_M_TYPE))) {
            ((ObjectNode) subtree).put(
                    Constants.JSON_VALUE + "@" + Constants.JSON_TYPE,
                    content.getAttribute(Constants.ATTR_M_TYPE));
          }
        } else {
          subtree = tree.get(Constants.JSON_VALUE);
        }
      } else {
        subtree = tree;
      }

      if (subtree != null) {
        JSONDOMTreeUtils.buildSubtree(client, content, subtree);
      }

      final List<Node> children = XMLUtils.getChildNodes(content, Node.ELEMENT_NODE);
      if (children.size() == 1) {
        final Element value = (Element) children.iterator().next();
        if (Constants.JSON_VALUE.equals(XMLUtils.getSimpleName(value))) {
          if (StringUtils.isNotBlank(content.getAttribute(Constants.ATTR_M_TYPE))) {
            value.setAttribute(Constants.ATTR_M_TYPE, content.getAttribute(Constants.ATTR_M_TYPE));
          }
          content = value;
        }
      }

      property.setContent(content);
    } catch (ParserConfigurationException e) {
      throw new JsonParseException("Cannot build property", parser.getCurrentLocation(), e);
    }

    return property;
  }
}
