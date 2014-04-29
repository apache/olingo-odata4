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

import org.apache.olingo.commons.core.data.v4.JSONDeltaImpl;
import org.apache.olingo.commons.core.domain.v4.ODataDeltaLinkImpl;
import org.apache.olingo.commons.core.domain.v4.ODataDeletedEntityImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ResWrap;

public class JSONDeltaDeserializer extends AbstractJsonDeserializer<JSONDeltaImpl> {

  @Override
  protected ResWrap<JSONDeltaImpl> doDeserialize(final JsonParser parser, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ObjectNode tree = parser.getCodec().readTree(parser);

    final JSONDeltaImpl delta = new JSONDeltaImpl();

    final URI contextURL;
    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = URI.create(tree.get(Constants.JSON_CONTEXT).textValue());
    } else {
      contextURL = null;
    }
    if (contextURL != null) {
      delta.setBaseURI(StringUtils.substringBefore(contextURL.toASCIIString(), Constants.METADATA));
    }

    if (tree.hasNonNull(jsonCount)) {
      delta.setCount(tree.get(jsonCount).asInt());
    }
    if (tree.hasNonNull(jsonNextLink)) {
      delta.setNext(URI.create(tree.get(jsonNextLink).textValue()));
    }
    if (tree.hasNonNull(jsonDeltaLink)) {
      delta.setDeltaLink(URI.create(tree.get(jsonDeltaLink).textValue()));
    }

    if (tree.hasNonNull(Constants.VALUE)) {
      for (final Iterator<JsonNode> itor = tree.get(Constants.VALUE).iterator(); itor.hasNext();) {
        final ObjectNode item = (ObjectNode) itor.next();
        final ContextURL itemContextURL = item.hasNonNull(Constants.JSON_CONTEXT)
                ? ContextURL.getInstance(URI.create(item.get(Constants.JSON_CONTEXT).textValue())) : null;
        item.remove(Constants.JSON_CONTEXT);

        if (itemContextURL == null || itemContextURL.isEntity()) {
          final ResWrap<JSONEntityImpl> entity = item.traverse(parser.getCodec()).
                  readValueAs(new TypeReference<JSONEntityImpl>() {
                  });
          delta.getEntities().add(entity.getPayload());
        } else if (itemContextURL.isDeltaDeletedEntity()) {
          delta.getDeletedEntities().
                  add(parser.getCodec().treeToValue(item, ODataDeletedEntityImpl.class));
        } else if (itemContextURL.isDeltaLink()) {
          delta.getAddedLinks().
                  add(parser.getCodec().treeToValue(item, ODataDeltaLinkImpl.class));
        } else if (itemContextURL.isDeltaDeletedLink()) {
          delta.getDeletedLinks().
                  add(parser.getCodec().treeToValue(item, ODataDeltaLinkImpl.class));
        }
      }
    }

    return new ResWrap<JSONDeltaImpl>(contextURL, null, delta);
  }

}
