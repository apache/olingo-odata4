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
package org.apache.olingo.client.core.v4;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JSONTest extends AbstractTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  protected ODataPubFormat getODataPubFormat() {
    return ODataPubFormat.JSON;
  }

  protected ODataFormat getODataFormat() {
    return ODataFormat.JSON;
  }

  private void cleanup(final ObjectNode node) {
    if (node.has(Constants.JSON_CONTEXT)) {
      node.remove(Constants.JSON_CONTEXT);
    }
    if (node.has(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_ETAG))) {
      node.remove(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_ETAG));
    }
    if (node.has(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_TYPE))) {
      node.remove(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_TYPE));
    }
    if (node.has(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_EDIT_LINK))) {
      node.remove(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_EDIT_LINK));
    }
    if (node.has(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_READ_LINK))) {
      node.remove(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_READ_LINK));
    }
    if (node.has(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIAEDIT_LINK))) {
      node.remove(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIAEDIT_LINK));
    }
    if (node.has(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIAREAD_LINK))) {
      node.remove(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIAREAD_LINK));
    }
    if (node.has(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIA_CONTENT_TYPE))) {
      node.remove(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIA_CONTENT_TYPE));
    }
    final List<String> toRemove = new ArrayList<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      if (field.getKey().charAt(0) == '#'
              || field.getKey().endsWith(
                      getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_TYPE))
              || field.getKey().endsWith(
                      getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIAEDIT_LINK))
              || field.getKey().endsWith(
                      getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIA_CONTENT_TYPE))
              || field.getKey().endsWith(
                      getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_ASSOCIATION_LINK))
              || field.getKey().endsWith(
                      getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_MEDIA_ETAG))) {

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

  protected void assertSimilar(final String filename, final String actual) throws Exception {
    final JsonNode expected = OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream(filename)).
            replace(getClient().getServiceVersion().getJSONMap().get(ODataServiceVersion.JSON_NAVIGATION_LINK),
                    Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected);
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    cleanup(actualNode);
    assertEquals(expected, actualNode);
  }

  protected void feed(final String filename, final ODataPubFormat format) throws Exception {
    final StringWriter writer = new StringWriter();
    getClient().getSerializer().feed(getClient().getDeserializer().toFeed(
            getClass().getResourceAsStream(filename + "." + getSuffix(format)), format).getObject(), writer);

    assertSimilar(filename + "." + getSuffix(format), writer.toString());
  }

  @Test
  public void feeds() throws Exception {
    feed("Customers", getODataPubFormat());
    feed("collectionOfEntityReferences", getODataPubFormat());
  }

  protected void entry(final String filename, final ODataPubFormat format) throws Exception {
    final StringWriter writer = new StringWriter();
    getClient().getSerializer().entry(getClient().getDeserializer().toEntry(
            getClass().getResourceAsStream(filename + "." + getSuffix(format)), format).getObject(), writer);

    assertSimilar(filename + "." + getSuffix(format), writer.toString());
  }

  @Test
  public void additionalEntries() throws Exception {
    entry("entity.minimal", getODataPubFormat());
    entry("entity.primitive", getODataPubFormat());
    entry("entity.complex", getODataPubFormat());
    entry("entity.collection.primitive", getODataPubFormat());
    entry("entity.collection.complex", getODataPubFormat());
  }

  @Test
  public void entries() throws Exception {
    entry("Products_5", getODataPubFormat());
    entry("VipCustomer", getODataPubFormat());
    entry("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7", getODataPubFormat());
    entry("entityReference", getODataPubFormat());
    entry("entity.withcomplexnavigation", getODataPubFormat());
  }

  protected void property(final String filename, final ODataFormat format) throws Exception {
    final StringWriter writer = new StringWriter();
    getClient().getSerializer().property(getClient().getDeserializer().
            toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(format)), format).getObject(), writer);

    assertSimilar(filename + "." + getSuffix(format), writer.toString());
  }

  @Test
  public void properties() throws Exception {
    property("Products_5_SkinColor", getODataFormat());
    property("Products_5_CoverColors", getODataFormat());
    property("Employees_3_HomeAddress", getODataFormat());
    property("Employees_3_HomeAddress", getODataFormat());
  }
}
