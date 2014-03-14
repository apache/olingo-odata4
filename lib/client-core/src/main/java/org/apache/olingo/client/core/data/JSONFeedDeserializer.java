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
import java.net.URI;
import java.util.Iterator;
import org.apache.olingo.client.api.Constants;

/**
 * Reads JSON string into a feed.
 * <br/>
 * If metadata information is available, the corresponding entry fields and content will be populated.
 */
public class JSONFeedDeserializer extends ODataJacksonDeserializer<JSONFeedImpl> {

  @Override
  protected JSONFeedImpl doDeserialize(final JsonParser parser, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    if (!tree.has(Constants.JSON_VALUE)) {
      return null;
    }

    final JSONFeedImpl feed = new JSONFeedImpl();

    if (tree.hasNonNull(Constants.JSON_METADATA)) {
      feed.setMetadata(URI.create(tree.get(Constants.JSON_METADATA).textValue()));
    }
    if (tree.hasNonNull("odata.count")) {
      feed.setCount(tree.get("odata.count").asInt());
    }
    if (tree.hasNonNull("odata.nextLink")) {
      feed.setNext(URI.create(tree.get("odata.nextLink").textValue()));
    }

    if (tree.hasNonNull(Constants.JSON_VALUE)) {
      for (final Iterator<JsonNode> itor = tree.get(Constants.JSON_VALUE).iterator(); itor.hasNext();) {
        feed.getEntries().add(itor.next().traverse(parser.getCodec()).readValueAs(JSONEntryImpl.class));
      }
    }

    return feed;
  }
}
