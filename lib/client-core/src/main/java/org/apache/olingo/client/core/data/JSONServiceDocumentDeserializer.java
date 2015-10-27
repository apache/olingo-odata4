/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.serialization.JsonDeserializer;
import org.apache.olingo.commons.api.Constants;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONServiceDocumentDeserializer extends JsonDeserializer {

  public JSONServiceDocumentDeserializer(final boolean serverMode) {
    super(serverMode);
  }

  protected ResWrap<ServiceDocument> doDeserialize(final JsonParser parser) throws IOException {

    final ObjectNode tree = parser.getCodec().readTree(parser);

    ServiceDocumentImpl serviceDocument = new ServiceDocumentImpl();

    final String metadataETag;
    if (tree.hasNonNull(Constants.JSON_METADATA_ETAG)) {
      metadataETag = tree.get(Constants.JSON_METADATA_ETAG).textValue();
      tree.remove(Constants.JSON_METADATA_ETAG);
    } else {
      metadataETag = null;
    }

    final URI contextURL;
    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = URI.create(tree.get(Constants.JSON_CONTEXT).textValue());
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      contextURL = URI.create(tree.get(Constants.JSON_METADATA).textValue());
      tree.remove(Constants.JSON_METADATA);
    } else {
      contextURL = null;
    }

    serviceDocument.setMetadata(contextURL == null ? null : contextURL.toASCIIString());

    for (final Iterator<JsonNode> itor = tree.get(Constants.VALUE).elements(); itor.hasNext();) {
      final JsonNode node = itor.next();

      final ServiceDocumentItemImpl item = new ServiceDocumentItemImpl();
      item.setName(node.get("name").asText());
      JsonNode titleNode = node.get("title");
      if (titleNode != null) {
        item.setTitle(titleNode.asText());
      }
      item.setUrl(node.get("url").asText());

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

    return new ResWrap<ServiceDocument>(contextURL, metadataETag, serviceDocument);
  }

  public ResWrap<ServiceDocument> toServiceDocument(final InputStream input) throws ODataDeserializerException {
    try {
      JsonParser parser = new JsonFactory(new ObjectMapper()).createParser(input);
      return doDeserialize(parser);
    } catch (final IOException e) {
      throw new ODataDeserializerException(e);
    }
  }
}
