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
package org.apache.olingo.commons.core.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.v3.LinkCollection;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.core.data.v3.LinkCollectionImpl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonLinkCollectionDeserializer extends JsonDeserializer {

  public JsonLinkCollectionDeserializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version, serverMode);
  }

  protected ResWrap<LinkCollection> doDeserialize(final JsonParser parser) throws IOException {

    final ObjectNode tree = parser.getCodec().readTree(parser);

    final LinkCollectionImpl links = new LinkCollectionImpl();

    if (tree.hasNonNull("odata.metadata")) {
      links.setMetadata(URI.create(tree.get("odata.metadata").textValue()));
    }

    if (tree.hasNonNull(Constants.JSON_URL)) {
      links.getLinks().add(URI.create(tree.get(Constants.JSON_URL).textValue()));
    }

    if (tree.hasNonNull(Constants.VALUE)) {
      for (final JsonNode item : tree.get(Constants.VALUE)) {
        final URI uri = URI.create(item.get(Constants.JSON_URL).textValue());
        links.getLinks().add(uri);
      }
    }

    if (tree.hasNonNull(jsonNextLink)) {
      links.setNext(URI.create(tree.get(jsonNextLink).textValue()));
    }

    return new ResWrap<LinkCollection>((URI) null, null, links);
  }

  public ResWrap<LinkCollection> toLinkCollection(InputStream input) throws ODataDeserializerException {
    try {
      JsonParser parser = new JsonFactory(new ObjectMapper()).createParser(input);
      return doDeserialize(parser);
    } catch (final IOException e) {
      throw new ODataDeserializerException(e);
    }
  }
}
