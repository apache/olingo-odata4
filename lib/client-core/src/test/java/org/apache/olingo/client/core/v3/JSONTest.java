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
package org.apache.olingo.client.core.v3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JSONTest extends AtomTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  protected ODataFormat getODataPubFormat() {
    return ODataFormat.JSON;
  }

  @Override
  protected ODataFormat getODataFormat() {
    return ODataFormat.JSON;
  }

  private void cleanup(final ObjectNode node) {
    final ODataServiceVersion version = getClient().getServiceVersion();
    if (node.has(Constants.JSON_METADATA)) {
      node.remove(Constants.JSON_METADATA);
    }
    if (node.has(version.getJsonName(ODataServiceVersion.JsonKey.TYPE))) {
      node.remove(version.getJsonName(ODataServiceVersion.JsonKey.TYPE));
    }
    if (node.has(version.getJsonName(ODataServiceVersion.JsonKey.EDIT_LINK))) {
      node.remove(version.getJsonName(ODataServiceVersion.JsonKey.EDIT_LINK));
    }
    if (node.has(version.getJsonName(ODataServiceVersion.JsonKey.READ_LINK))) {
      node.remove(version.getJsonName(ODataServiceVersion.JsonKey.READ_LINK));
    }
    if (node.has(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_EDIT_LINK))) {
      node.remove(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_EDIT_LINK));
    }
    if (node.has(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_READ_LINK))) {
      node.remove(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_READ_LINK));
    }
    if (node.has(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_CONTENT_TYPE))) {
      node.remove(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_CONTENT_TYPE));
    }
    if (node.has(version.getJsonName(ODataServiceVersion.JsonKey.NEXT_LINK))) {
      node.remove(version.getJsonName(ODataServiceVersion.JsonKey.NEXT_LINK));
    }
    final List<String> toRemove = new ArrayList<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      final String key = field.getKey();
      if (key.charAt(0) == '#'
              || key.endsWith(version.getJsonName(ODataServiceVersion.JsonKey.TYPE))
              || key.endsWith(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_EDIT_LINK))
              || key.endsWith(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_CONTENT_TYPE))
              || key.endsWith(version.getJsonName(ODataServiceVersion.JsonKey.ASSOCIATION_LINK))
              || key.endsWith(version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_ETAG))) {

        toRemove.add(key);
      } else if (field.getValue().isObject()) {
        cleanup((ObjectNode) field.getValue());
      } else if (field.getValue().isArray()) {
        for (final Iterator<JsonNode> arrayItems = field.getValue().elements(); arrayItems.hasNext();) {
          final JsonNode arrayItem = arrayItems.next();
          if (arrayItem.isObject()) {
            cleanup((ObjectNode) arrayItem);
          }
        }
      }
    }
    node.remove(toRemove);
  }

  @Override
  protected void assertSimilar(final String filename, final String actual) throws Exception {
    final JsonNode expected = OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream(filename))
        .replace("Categories"
            + getClient().getServiceVersion().getJsonName(ODataServiceVersion.JsonKey.NAVIGATION_LINK),
            "Categories" + Constants.JSON_BIND_LINK_SUFFIX)
            .replace("\"Products(0)/Categories\"", "[\"Products(0)/Categories\"]")
            .replace(getClient().getServiceVersion().getJsonName(ODataServiceVersion.JsonKey.NAVIGATION_LINK),
                    Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected);
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    cleanup(actualNode);
    assertEquals(expected, actualNode);
  }

}
