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
package org.apache.olingo.fit.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Commons {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(Commons.class);

  protected static Pattern multiKeyPattern = Pattern.compile("(.*=.*,?)+");

  protected final static Map<String, Integer> sequence = new HashMap<String, Integer>();

  protected final static Map<String, String> mediaContent = new HashMap<String, String>();

  protected final static Map<ODataVersion, MetadataLinkInfo> linkInfo =
          new EnumMap<ODataVersion, MetadataLinkInfo>(ODataVersion.class);

  static {
    sequence.put("Customer", 1000);
    sequence.put("CustomerInfo", 1000);
    sequence.put("Car", 1000);
    sequence.put("Message", 1000);
    sequence.put("Order", 1000);
    sequence.put("ComputerDetail", 1000);
    sequence.put("AllGeoTypesSet", 1000);

    mediaContent.put("CustomerInfo", "CustomerinfoId");
    mediaContent.put("Car", "VIN");
    mediaContent.put("Car/Photo", null);
  }

  public static Map<ODataVersion, MetadataLinkInfo> getLinkInfo() {
    return linkInfo;
  }

  public static String getEntityURI(final String entitySetName, final String entityKey) {
    // expected singleton in case of null key
    return entitySetName + (StringUtils.isNotBlank(entityKey) ? "(" + entityKey + ")" : "");
  }

  public static String getEntityBasePath(final String entitySetName, final String entityKey) {
    // expected singleton in case of null key
    return entitySetName + File.separatorChar
            + (StringUtils.isNotBlank(entityKey) ? getEntityKey(entityKey) + File.separatorChar : "");
  }

  public static String getLinksURI(
          final ODataVersion version,
          final String entitySetName,
          final String entityId,
          final String linkName) throws IOException {
    return getEntityURI(entitySetName, entityId) + "/" + linkName;
  }

  public static String getLinksPath(
          final ODataVersion version,
          final String entitySetName,
          final String entityId,
          final String linkName,
          final Accept accept) throws IOException {
    return getLinksPath(ODataVersion.v3, getEntityBasePath(entitySetName, entityId), linkName, accept);

  }

  public static String getLinksPath(
          final ODataVersion version, final String basePath, final String linkName, final Accept accept)
          throws IOException {
    try {
      return FSManager.instance(version)
              .getAbsolutePath(basePath + Constants.get(version, ConstantKey.LINKS_FILE_PATH)
              + File.separatorChar + linkName, accept);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  public static String getEntityKey(final String entityId) {
    if (multiKeyPattern.matcher(entityId).matches()) {
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

  public static InputStream getLinksAsATOM(final Map.Entry<String, Collection<String>> link)
          throws IOException {
    final StringBuilder builder = new StringBuilder();
    builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    builder.append("<links xmlns=\"http://schemas.microsoft.com/ado/2007/08/dataservices\">");

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

    return IOUtils.toInputStream(builder.toString());
  }

  public static InputStream getLinksAsJSON(
          final String entitySetName, final Map.Entry<String, Collection<String>> link)
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

    return IOUtils.toInputStream(links.toString());
  }

  public static InputStream changeFormat(final InputStream is, final Accept target) {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      IOUtils.copy(is, bos);
      IOUtils.closeQuietly(is);

      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode node =
              changeFormat((ObjectNode) mapper.readTree(new ByteArrayInputStream(bos.toByteArray())), target);

      return IOUtils.toInputStream(node.toString());
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
    final Map<String, JsonNode> toBeReplaced = new HashMap<String, JsonNode>();

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
            toBeReplaced.put(field.getKey(), changeFormat((ObjectNode) field.getValue(), target));
          }
        }
      case JSON_FULLMETA:
        //ignore: no changes
        break;

      default:
        throw new UnsupportedOperationException(target.name());
    }

    for (String field : toBeRemoved) {
      node.remove(field);
    }

    for (Map.Entry<String, JsonNode> field : toBeReplaced.entrySet()) {
      node.replace(field.getKey(), field.getValue());
    }

    return node;
  }

  public static String getETag(final String basePath, final ODataVersion version) throws Exception {
    try {
      final InputStream is = FSManager.instance(version).readFile(basePath + "etag", Accept.TEXT);
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
