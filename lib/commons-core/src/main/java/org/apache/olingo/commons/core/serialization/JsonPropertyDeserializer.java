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
package org.apache.olingo.commons.core.serialization;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.AnnotationImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Parse JSON string into <tt>Property</tt>.
 */
public class JsonPropertyDeserializer extends JsonDeserializer {

  public JsonPropertyDeserializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version, serverMode);
  }

  protected ResWrap<Property> doDeserialize(final JsonParser parser) throws IOException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    final String metadataETag;
    final URI contextURL;
    final PropertyImpl property = new PropertyImpl();

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
      tree.remove(jsonType);
    }

    if (tree.has(Constants.JSON_NULL) && tree.get(Constants.JSON_NULL).asBoolean()) {
      property.setValue(ValueType.PRIMITIVE, null);
      tree.remove(Constants.JSON_NULL);
    }

    if (property.getValue() == null) {
      try {
        value(property, tree.has(Constants.VALUE) ? tree.get(Constants.VALUE) : tree, parser.getCodec());
      } catch (final EdmPrimitiveTypeException e) {
        throw new IOException(e);
      }
      tree.remove(Constants.VALUE);
    }

    // any remaining entry is supposed to be an annotation or is ignored
    for (final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();
      if (field.getKey().charAt(0) == '@') {
        final Annotation annotation = new AnnotationImpl();
        annotation.setTerm(field.getKey().substring(1));

        try {
          value(annotation, field.getValue(), parser.getCodec());
        } catch (final EdmPrimitiveTypeException e) {
          throw new IOException(e);
        }
        property.getAnnotations().add(annotation);
      }
    }

    return new ResWrap<Property>(contextURL, metadataETag, property);
  }
}
