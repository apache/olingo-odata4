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
package org.apache.olingo.client.core.serialization;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Reads JSON string into an entity set.
 * <br/>
 * If metadata information is available, the corresponding entity fields and content will be populated.
 */
public class JsonEntitySetDeserializer extends JsonDeserializer {

  public JsonEntitySetDeserializer(final boolean serverMode) {
    super(serverMode);
  }

  protected ResWrap<EntityCollection> doDeserialize(final JsonParser parser) throws IOException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    if (!tree.has(Constants.VALUE)) {
      return null;
    }

    final EntityCollection entitySet = new EntityCollection();

    URI contextURL;
    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = URI.create(tree.get(Constants.JSON_CONTEXT).textValue());
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      contextURL = URI.create(tree.get(Constants.JSON_METADATA).textValue());
      tree.remove(Constants.JSON_METADATA);
    } else {
      contextURL = null;
    }
    if (contextURL != null) {
      entitySet.setBaseURI(URI.create(StringUtils.substringBefore(contextURL.toASCIIString(), Constants.METADATA)));
    }

    final String metadataETag;
    if (tree.hasNonNull(Constants.JSON_METADATA_ETAG)) {
      metadataETag = tree.get(Constants.JSON_METADATA_ETAG).textValue();
      tree.remove(Constants.JSON_METADATA_ETAG);
    } else {
      metadataETag = null;
    }

    if (tree.hasNonNull(Constants.JSON_COUNT)) {
      entitySet.setCount(tree.get(Constants.JSON_COUNT).asInt());
      tree.remove(Constants.JSON_COUNT);
    }
    if (tree.hasNonNull(Constants.JSON_NEXT_LINK)) {
      entitySet.setNext(URI.create(tree.get(Constants.JSON_NEXT_LINK).textValue()));
      tree.remove(Constants.JSON_NEXT_LINK);
    }
    if (tree.hasNonNull(Constants.JSON_DELTA_LINK)) {
      entitySet.setDeltaLink(URI.create(tree.get(Constants.JSON_DELTA_LINK).textValue()));
      tree.remove(Constants.JSON_DELTA_LINK);
    }

    if (tree.hasNonNull(Constants.VALUE)) {
      final JsonEntityDeserializer entityDeserializer = new JsonEntityDeserializer(serverMode);
      for (JsonNode jsonNode : tree.get(Constants.VALUE)) {
        entitySet.getEntities().add(
            entityDeserializer.doDeserialize(jsonNode.traverse(parser.getCodec())).getPayload());
      }
      tree.remove(Constants.VALUE);
    }
    final Set<String> toRemove = new HashSet<String>();
    // any remaining entry is supposed to be an annotation or is ignored
    for (final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();
      if (field.getKey().charAt(0) == '@') {
        final Annotation annotation = new Annotation();
        annotation.setTerm(field.getKey().substring(1));

        try {
          value(annotation, field.getValue(), parser.getCodec());
        } catch (final EdmPrimitiveTypeException e) {
          throw new IOException(e);
        }
        entitySet.getAnnotations().add(annotation);
      } else if (field.getKey().charAt(0) == '#') {
        final Operation operation = new Operation();
        operation.setMetadataAnchor(field.getKey());

        final ObjectNode opNode = (ObjectNode) tree.get(field.getKey());
        operation.setTitle(opNode.get(Constants.ATTR_TITLE).asText());
        operation.setTarget(URI.create(opNode.get(Constants.ATTR_TARGET).asText()));
        entitySet.getOperations().add(operation);
        toRemove.add(field.getKey());
      }
    }
    tree.remove(toRemove);
    return new ResWrap<EntityCollection>(contextURL, metadataETag, entitySet);
  }
}
