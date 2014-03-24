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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.ODataJacksonDeserializer;

public class JSONServiceDocumentDeserializer extends ODataJacksonDeserializer<AbstractServiceDocument> {

  @Override
  protected AbstractServiceDocument doDeserialize(final JsonParser parser, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    final AbstractServiceDocument serviceDocument = ODataServiceVersion.V30 == version
            ? new org.apache.olingo.client.core.data.v3.JSONServiceDocumentImpl()
            : new org.apache.olingo.client.core.data.v4.JSONServiceDocumentImpl();

    if (tree.hasNonNull(Constants.JSON_METADATA)
            && serviceDocument instanceof org.apache.olingo.client.core.data.v3.JSONServiceDocumentImpl) {

      ((org.apache.olingo.client.core.data.v3.JSONServiceDocumentImpl) serviceDocument).
              setMetadata(tree.get(Constants.JSON_METADATA).textValue());
    }
    if (tree.hasNonNull(Constants.JSON_CONTEXT)
            && serviceDocument instanceof org.apache.olingo.client.core.data.v4.JSONServiceDocumentImpl) {

      ((org.apache.olingo.client.core.data.v4.JSONServiceDocumentImpl) serviceDocument).
              setMetadataContext(tree.get(Constants.JSON_CONTEXT).textValue());
    }

    for (final Iterator<JsonNode> itor = tree.get(Constants.JSON_VALUE).elements(); itor.hasNext();) {
      final JsonNode node = itor.next();

      final ServiceDocumentItemImpl item = new ServiceDocumentItemImpl();
      item.setName(node.get("name").asText());
      if (node.has("title")) {
        item.setTitle(node.get("title").asText());
      }
      item.setHref(node.get("url").asText());

      final String kind = node.has("kind") ? node.get("kind").asText() : null;
      if (StringUtils.isBlank(kind) || "EntitySet".equals(kind)) {
        serviceDocument.getEntitySets().add(item);
      } else if ("Singleton".equals(kind)) {
        serviceDocument.getSingletons().add(item);
      } else if ("FunctionImport".equals(kind)) {
        serviceDocument.getFunctionImports().add(item);
      } else if ("ServiceDocument".equals(kind)) {
        serviceDocument.getRelatedServiceDocuments().add(item);
      }
    }

    return serviceDocument;
  }

}
