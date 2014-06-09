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

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Reads JSON string into an entity set.
 * <br/>
 * If metadata information is available, the corresponding entity fields and content will be populated.
 */
public class JSONEntitySetDeserializer extends JsonDeserializer {

  public JSONEntitySetDeserializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version, serverMode);
  }

  protected ResWrap<EntitySet> doDeserialize(final JsonParser parser) throws IOException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    if (!tree.has(Constants.VALUE)) {
      return null;
    }

    final EntitySetImpl entitySet = new EntitySetImpl();

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
      entitySet.setBaseURI(StringUtils.substringBefore(contextURL.toASCIIString(), Constants.METADATA));
    }

    final String metadataETag;
    if (tree.hasNonNull(Constants.JSON_METADATA_ETAG)) {
      metadataETag = tree.get(Constants.JSON_METADATA_ETAG).textValue();
      tree.remove(Constants.JSON_METADATA_ETAG);
    } else {
      metadataETag = null;
    }

    if (tree.hasNonNull(jsonCount)) {
      entitySet.setCount(tree.get(jsonCount).asInt());
      tree.remove(jsonCount);
    }
    if (tree.hasNonNull(jsonNextLink)) {
      entitySet.setNext(URI.create(tree.get(jsonNextLink).textValue()));
      tree.remove(jsonNextLink);
    }
    if (tree.hasNonNull(jsonDeltaLink)) {
      entitySet.setDeltaLink(URI.create(tree.get(jsonDeltaLink).textValue()));
      tree.remove(jsonDeltaLink);
    }

    if (tree.hasNonNull(Constants.VALUE)) {
      final JSONEntityDeserializer entityDeserializer = new JSONEntityDeserializer(version, serverMode);
      for (final Iterator<JsonNode> itor = tree.get(Constants.VALUE).iterator(); itor.hasNext();) {
        entitySet.getEntities().add(
            entityDeserializer.doDeserialize(itor.next().traverse(parser.getCodec())).getPayload());
      }
      tree.remove(Constants.VALUE);
    }

    // any remaining entry is supposed to be an annotation or is ignored
    for (final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();
      if (field.getKey().charAt(0) == '@') {
        final Annotation annotation = new AnnotationImpl();
        annotation.setTerm(field.getKey().substring(1));

        value(annotation, field.getValue(), parser.getCodec());
        entitySet.getAnnotations().add(annotation);
      }
    }

    return new ResWrap<EntitySet>(contextURL, metadataETag, entitySet);
  }
}
