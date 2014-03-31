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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;

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

    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      final String contextURL = tree.get(Constants.JSON_CONTEXT).textValue();
      property.setName(StringUtils.substringAfterLast(contextURL, "/"));
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      final String metadata = tree.get(Constants.JSON_METADATA).textValue();
      final int dashIdx = metadata.lastIndexOf('#');
      if (dashIdx != -1) {
        property.setType(metadata.substring(dashIdx + 1));
      }
      tree.remove(Constants.JSON_METADATA);
    }

    if (tree.has(jsonType) && property.getType() == null) {
      property.setType(tree.get(jsonType).asText());
    }

    if (tree.has(Constants.JSON_NULL) && tree.get(Constants.JSON_NULL).asBoolean()) {
      property.setValue(new NullValueImpl());
    }

    if (property.getValue() == null) {
      value(property, tree.has(Constants.VALUE) ? tree.get(Constants.VALUE) : tree);
    }

    return property;
  }
}
