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
package org.apache.olingo.fit.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.fit.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Commons {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(Commons.class);

  protected static final Pattern MULTIKEY_PATTERN = Pattern.compile("(.*=.*,?)+");

  protected static final Map<String, Integer> SEQUENCE = new HashMap<String, Integer>();

  protected static final Map<String, Pair<String, EdmPrimitiveTypeKind>> MEDIA_CONTENT =
      new HashMap<String, Pair<String, EdmPrimitiveTypeKind>>();

  static {
    SEQUENCE.put("Customer", 1000);
    SEQUENCE.put("CustomerInfo", 1000);
    SEQUENCE.put("Car", 1000);
    SEQUENCE.put("Message", 1000);
    SEQUENCE.put("OrderDetails", 1000);
    SEQUENCE.put("Order", 1000);
    SEQUENCE.put("Product", 1000);
    SEQUENCE.put("ComputerDetail", 1000);
    SEQUENCE.put("AllGeoTypesSet", 1000);
    SEQUENCE.put("Orders", 1000);
    SEQUENCE.put("Customers", 1000);
    SEQUENCE.put("Person", 1000);
    SEQUENCE.put("RowIndex", 1000);
    SEQUENCE.put("Products", 1000);
    SEQUENCE.put("ProductDetails", 1000);
    SEQUENCE.put("PersonDetails", 1000);
    SEQUENCE.put("PaymentInstrument", 10192);
    SEQUENCE.put("People", 1000);

    MEDIA_CONTENT.put("CustomerInfo",
        new ImmutablePair<String, EdmPrimitiveTypeKind>("CustomerInfoId", EdmPrimitiveTypeKind.Int32));
    MEDIA_CONTENT.put("Car",
        new ImmutablePair<String, EdmPrimitiveTypeKind>("VIN", EdmPrimitiveTypeKind.Int32));
    MEDIA_CONTENT.put("Car/Photo", null);
    MEDIA_CONTENT.put("PersonDetails/Photo", null);
    MEDIA_CONTENT.put("Advertisements",
        new ImmutablePair<String, EdmPrimitiveTypeKind>("ID", EdmPrimitiveTypeKind.Guid));
  }

  private static final Metadata METADATA =
      new Metadata(Commons.class.getResourceAsStream("/" + ODataServiceVersion.V40.name() + "/metadata.xml"));

  public static Metadata getMetadata() {
    return METADATA;
  }

  public static Map<String, Pair<String, EdmPrimitiveTypeKind>> getMediaContent() {
    return MEDIA_CONTENT;
  }

  public static String getEntityURI(final String entitySetName, final String entityKey) {
    // expected singleton in case of null key
    return entitySetName + (entityKey == null || entityKey.isEmpty() ? "" : "(" + entityKey + ")");
  }

  public static String getEntityBasePath(final String entitySetName, final String entityKey) {
    // expected singleton in case of null key
    return entitySetName + File.separatorChar
        + (entityKey == null || entityKey.isEmpty() ? "" : getEntityKey(entityKey) + File.separatorChar);
  }

  public static String getLinksURI(final String entitySetName, final String entityId, final String linkName)
      throws IOException {
    return getEntityURI(entitySetName, entityId) + "/" + linkName;
  }

  public static String getLinksPath(final String entitySetName, final String entityId,
      final String linkName, final Accept accept) throws IOException {
    return getLinksPath(getEntityBasePath(entitySetName, entityId), linkName, accept);

  }

  public static String getLinksPath(final String basePath, final String linkName, final Accept accept)
      throws IOException {
    try {
      return FSManager.instance()
          .getAbsolutePath(basePath + Constants.get(ConstantKey.LINKS_FILE_PATH)
              + File.separatorChar + linkName, accept);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  public static String getEntityKey(final String entityId) {
    if (MULTIKEY_PATTERN.matcher(entityId).matches()) {
      // assume correct multi-key
      final String[] keys = entityId.split(",");
      final StringBuilder keyBuilder = new StringBuilder();
      for (String part : keys) {
        if (keyBuilder.length() > 0) {
          keyBuilder.append(" ");
        }
        keyBuilder.append(part.split("=")[1].replaceAll("'", "").trim());
      }
      return keyBuilder.toString();
    } else {
      return entityId.trim();
    }
  }

  public static InputStream getLinksAsATOM(final Map.Entry<String, Collection<String>> link) throws IOException {

    final StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    builder.append("<links xmlns=\"").append(Constants.get(ConstantKey.DATASERVICES_NS)).append("\">");

    for (String uri : link.getValue()) {
      builder.append("<uri>");
      if (URI.create(uri).isAbsolute()) {
        builder.append(uri);
      } else {
        builder.append(Constants.get(ConstantKey.DEFAULT_SERVICE_URL)).append(uri);
      }
      builder.append("</uri>");
    }

    builder.append("</links>");

    return IOUtils.toInputStream(builder.toString(), Constants.ENCODING);
  }

  public static InputStream
  getLinksAsJSON(final String entitySetName, final Map.Entry<String, Collection<String>> link)
      throws IOException {

    final ObjectNode links = new ObjectNode(JsonNodeFactory.instance);
    links.put(
        Constants.get(ConstantKey.JSON_ODATAMETADATA_NAME),
        Constants.get(ConstantKey.ODATA_METADATA_PREFIX) + entitySetName + "/$links/" + link.getKey());

    final ArrayNode uris = new ArrayNode(JsonNodeFactory.instance);

    for (String uri : link.getValue()) {
      final String absoluteURI;
      if (URI.create(uri).isAbsolute()) {
        absoluteURI = uri;
      } else {
        absoluteURI = Constants.get(ConstantKey.DEFAULT_SERVICE_URL) + uri;
      }
      uris.add(new ObjectNode(JsonNodeFactory.instance).put("url", absoluteURI));
    }

    if (uris.size() == 1) {
      links.setAll((ObjectNode) uris.get(0));
    } else {
      links.set("value", uris);
    }

    return IOUtils.toInputStream(links.toString(), Constants.ENCODING);
  }

  public static InputStream changeFormat(final InputStream is, final Accept target) {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      IOUtils.copy(is, bos);
      IOUtils.closeQuietly(is);

      final ObjectMapper mapper = new ObjectMapper(
          new JsonFactory().configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true));
      final JsonNode node =
          changeFormat((ObjectNode) mapper.readTree(new ByteArrayInputStream(bos.toByteArray())), target);

      return IOUtils.toInputStream(node.toString(), Constants.ENCODING);
    } catch (Exception e) {
      LOG.error("Error changing format", e);
      return new ByteArrayInputStream(bos.toByteArray());
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  @SuppressWarnings("fallthrough")
  public static JsonNode changeFormat(final ObjectNode node, final Accept target) {
    final List<String> toBeRemoved = new ArrayList<String>();
    switch (target) {
    case JSON_NOMETA:
      // nometa + minimal
      toBeRemoved.add(Constants.get(ConstantKey.JSON_ODATAMETADATA_NAME));

    case JSON:
      // minimal
      toBeRemoved.add(Constants.get(ConstantKey.JSON_EDITLINK_NAME));
      toBeRemoved.add(Constants.get(ConstantKey.JSON_ID_NAME));
      toBeRemoved.add(Constants.get(ConstantKey.JSON_TYPE_NAME));

      final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
      while (fields.hasNext()) {
        final Map.Entry<String, JsonNode> field = fields.next();
        if (field.getKey().endsWith(Constants.get(ConstantKey.JSON_MEDIA_SUFFIX))
            || field.getKey().endsWith(Constants.get(ConstantKey.JSON_NAVIGATION_SUFFIX))
            || field.getKey().endsWith(Constants.get(ConstantKey.JSON_TYPE_SUFFIX))) {
          toBeRemoved.add(field.getKey());
        } else if (field.getValue().isObject()) {
          changeFormat((ObjectNode) field.getValue(), target);
        } else if (field.getValue().isArray()) {
          for (final Iterator<JsonNode> subItor = field.getValue().elements(); subItor.hasNext();) {
            final JsonNode subNode = subItor.next();
            if (subNode.isObject()) {
              changeFormat((ObjectNode) subNode, target);
            }
          }
        }
      }
    case JSON_FULLMETA:
      // ignore: no changes
      break;

    default:
      throw new UnsupportedOperationException(target.name());
    }
    node.remove(toBeRemoved);

    return node;
  }

  public static String getETag(final String basePath) throws Exception {
    try {
      final InputStream is = FSManager.instance().readFile(basePath + "etag", Accept.TEXT);
      if (is.available() <= 0) {
        return null;
      } else {
        final String etag = IOUtils.toString(is);
        IOUtils.closeQuietly(is);
        return etag;
      }
    } catch (Exception e) {
      return null;
    }
  }

  public static Map.Entry<String, String> parseEntityURI(final String uri) {
    final String relPath = uri.substring(uri.lastIndexOf("/"));
    final int branchIndex = relPath.indexOf('(');

    final String es;
    final String eid;

    if (branchIndex > -1) {
      es = relPath.substring(0, branchIndex);
      eid = relPath.substring(branchIndex + 1, relPath.indexOf(')'));
    } else {
      es = relPath;
      eid = null;
    }

    return new SimpleEntry<String, String>(es, eid);
  }
}
