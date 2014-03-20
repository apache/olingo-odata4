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
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import org.apache.olingo.client.api.Constants;

/**
 * Parse JSON string into <tt>JSONPropertyImpl</tt>.
 *
 * @see JSONPropertyImpl
 */
public class JSONPropertyDeserializer extends AbstractJsonDeserializer<JSONPropertyImpl> {

  @Override
  protected JSONPropertyImpl doDeserialize(final JsonParser parser, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    final JSONPropertyImpl property = new JSONPropertyImpl();

    if (tree.hasNonNull(Constants.JSON_METADATA)) {
      property.setMetadata(URI.create(tree.get(Constants.JSON_METADATA).textValue()));
      tree.remove(Constants.JSON_METADATA);
    }

    if (property.getMetadata() != null) {
      final String metadataURI = property.getMetadata().toASCIIString();
      final int dashIdx = metadataURI.lastIndexOf('#');
      if (dashIdx != -1) {
        property.setType(metadataURI.substring(dashIdx + 1));
      }
    }

    if (tree.has(Constants.JSON_TYPE) && property.getType() == null) {
      property.setType(tree.get(Constants.JSON_TYPE).asText());
    }

    if (tree.has(Constants.JSON_NULL) && tree.get(Constants.JSON_NULL).asBoolean()) {
      property.setValue(new NullValueImpl());
    }

    if (property.getValue() == null) {
      value(property, tree.has(Constants.JSON_VALUE) ? tree.get(Constants.JSON_VALUE) : tree);
    }

    return property;
  }
}
