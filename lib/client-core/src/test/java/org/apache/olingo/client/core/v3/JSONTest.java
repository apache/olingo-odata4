package org.apache.olingo.client.core.v3;

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
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;

public class JSONTest extends AtomTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  protected ODataPubFormat getODataPubFormat() {
    return ODataPubFormat.JSON;
  }

  @Override
  protected ODataFormat getODataFormat() {
    return ODataFormat.JSON;
  }

  private void cleanup(final ObjectNode node) {
    if (node.has(Constants.JSON_TYPE)) {
      node.remove(Constants.JSON_TYPE);
    }
    if (node.has(Constants.JSON_EDIT_LINK)) {
      node.remove(Constants.JSON_EDIT_LINK);
    }
    if (node.has(Constants.JSON_READ_LINK)) {
      node.remove(Constants.JSON_READ_LINK);
    }
    if (node.has(Constants.JSON_MEDIAEDIT_LINK)) {
      node.remove(Constants.JSON_MEDIAEDIT_LINK);
    }
    if (node.has(Constants.JSON_MEDIAREAD_LINK)) {
      node.remove(Constants.JSON_MEDIAREAD_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_CONTENT_TYPE)) {
      node.remove(Constants.JSON_MEDIA_CONTENT_TYPE);
    }
    final List<String> toRemove = new ArrayList<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      if (field.getKey().charAt(0) == '#'
              || field.getKey().endsWith(Constants.JSON_TYPE_SUFFIX)
              || field.getKey().endsWith(Constants.JSON_MEDIAEDIT_LINK_SUFFIX)
              || field.getKey().endsWith(Constants.JSON_MEDIA_CONTENT_TYPE_SUFFIX)
              || field.getKey().endsWith(Constants.JSON_ASSOCIATION_LINK_SUFFIX)
              || field.getKey().endsWith(Constants.JSON_MEDIA_ETAG_SUFFIX)) {

        toRemove.add(field.getKey());
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
    final JsonNode orig = OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream(filename)).
            replace("Categories" + Constants.JSON_NAVIGATION_LINK_SUFFIX,
                    "Categories" + Constants.JSON_BIND_LINK_SUFFIX).
            replace("\"Products(0)/Categories\"", "[\"Products(0)/Categories\"]").
            replace(Constants.JSON_NAVIGATION_LINK_SUFFIX, Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) orig);
    assertEquals(orig, OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes())));
  }

}
