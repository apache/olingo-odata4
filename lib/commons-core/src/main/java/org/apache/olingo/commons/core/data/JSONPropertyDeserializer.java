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
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

/**
 * Parse JSON string into <tt>JSONPropertyImpl</tt>.
 *
 * @see JSONPropertyImpl
 */
public class JSONPropertyDeserializer extends AbstractJsonDeserializer<JSONPropertyImpl> {

  @Override
  protected Container<JSONPropertyImpl> doDeserialize(final JsonParser parser, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    final String metadataETag;
    final URI contextURL;
    final JSONPropertyImpl property = new JSONPropertyImpl();

    if (tree.hasNonNull(Constants.JSON_METADATA_ETAG)) {
      metadataETag = tree.get(Constants.JSON_METADATA_ETAG).textValue();
      tree.remove(Constants.JSON_METADATA_ETAG);
    } else {
      metadataETag = null;
    }

    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = URI.create(tree.get(Constants.JSON_CONTEXT).textValue());
      property.setName(StringUtils.substringAfterLast(contextURL.toASCIIString(), "/"));
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      contextURL = URI.create(tree.get(Constants.JSON_METADATA).textValue());
      property.setType(new EdmTypeInfo.Builder().
              setTypeExpression(StringUtils.substringAfterLast(contextURL.toASCIIString(), "#")).build().internal());
      tree.remove(Constants.JSON_METADATA);
    } else {
      contextURL = null;
    }

    if (tree.has(jsonType)) {
      property.setType(new EdmTypeInfo.Builder().setTypeExpression(tree.get(jsonType).textValue()).build().internal());
    }

    if (tree.has(Constants.JSON_NULL) && tree.get(Constants.JSON_NULL).asBoolean()) {
      property.setValue(new NullValueImpl());
    }

    if (property.getValue() == null) {
      value(property, tree.has(Constants.VALUE) ? tree.get(Constants.VALUE) : tree, parser.getCodec());
    }

    return new Container<JSONPropertyImpl>(contextURL, metadataETag, property);
  }
}
