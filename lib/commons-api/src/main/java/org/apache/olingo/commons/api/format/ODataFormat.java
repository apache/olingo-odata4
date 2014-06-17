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
package org.apache.olingo.commons.api.format;

import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Available formats to be used in various contexts.
 */
public enum ODataFormat {

  /** JSON format with no metadata. */
  JSON_NO_METADATA,
  /** JSON format with minimal metadata (default). */
  JSON,
  /** JSON format with full metadata. */
  JSON_FULL_METADATA,

  /** XML format. */
  XML(ContentType.APPLICATION_XML),
  /** Atom format. */
  ATOM(ContentType.APPLICATION_ATOM_XML),

  // media formats
  APPLICATION_XML(ContentType.APPLICATION_XML),
  APPLICATION_ATOM_XML(ContentType.APPLICATION_ATOM_XML),
  APPLICATION_XHTML_XML(ContentType.APPLICATION_XHTML_XML),
  APPLICATION_SVG_XML(ContentType.APPLICATION_SVG_XML),
  APPLICATION_JSON(ContentType.APPLICATION_JSON),
  APPLICATION_FORM_URLENCODED(ContentType.APPLICATION_FORM_URLENCODED),
  MULTIPART_FORM_DATA(ContentType.MULTIPART_FORM_DATA),
  APPLICATION_OCTET_STREAM(ContentType.APPLICATION_OCTET_STREAM),
  TEXT_PLAIN(ContentType.TEXT_PLAIN),
  TEXT_XML(ContentType.TEXT_XML),
  TEXT_HTML(ContentType.TEXT_HTML);

  private static final String JSON_METADATA_PARAMETER_V3 = "odata";
  private static final String JSON_METADATA_PARAMETER_V4 = "odata.metadata";

  private static final Map<ODataServiceVersion, Map<ODataFormat, ContentType>> FORMAT_PER_VERSION = new
      HashMap<ODataServiceVersion, Map<ODataFormat, ContentType>>();

  static {
    final Map<ODataFormat, ContentType> v3 = new HashMap<ODataFormat, ContentType>();
    v3.put(ODataFormat.JSON_NO_METADATA, ContentType.create(
        ContentType.APPLICATION_JSON, JSON_METADATA_PARAMETER_V3, "nometadata"));
    v3.put(ODataFormat.JSON, ContentType.create(
        ContentType.APPLICATION_JSON, JSON_METADATA_PARAMETER_V3, "minimalmetadata"));
    v3.put(ODataFormat.JSON_FULL_METADATA, ContentType.create(
        ContentType.APPLICATION_JSON, JSON_METADATA_PARAMETER_V3, "fullmetadata"));
    FORMAT_PER_VERSION.put(ODataServiceVersion.V30, v3);

    final Map<ODataFormat, ContentType> v4 = new HashMap<ODataFormat, ContentType>();
    v4.put(ODataFormat.JSON_NO_METADATA, ContentType.create(
        ContentType.APPLICATION_JSON, JSON_METADATA_PARAMETER_V4, "none"));
    v4.put(ODataFormat.JSON, ContentType.create(
        ContentType.APPLICATION_JSON, JSON_METADATA_PARAMETER_V4, "minimal"));
    v4.put(ODataFormat.JSON_FULL_METADATA, ContentType.create(
        ContentType.APPLICATION_JSON, JSON_METADATA_PARAMETER_V4, "full"));
    FORMAT_PER_VERSION.put(ODataServiceVersion.V40, v4);
  }

  private final ContentType contentType;

  ODataFormat(final ContentType contentType) {
    this.contentType = contentType;
  }

  ODataFormat() {
    this.contentType = null;
  }

  /**
   * Gets format as {@link ContentType}.
   * @param version OData service version.
   * @return format as ContentType.
   */
  public ContentType getContentType(final ODataServiceVersion version) {
    if (version.ordinal() < ODataServiceVersion.V30.ordinal()) {
      throw new IllegalArgumentException("Unsupported version " + version);
    }

    return contentType == null ? FORMAT_PER_VERSION.get(version).get(this) : contentType;
  }

  @Override
  public String toString() {
    if (contentType == null) {
      throw new UnsupportedOperationException();
    } else {
      return contentType.toContentTypeString();
    }
  }

  /**
   * Gets OData format from a content type.
   *
   * @param contentType content type
   * @return OData format.
   */
  public static ODataFormat fromContentType(final ContentType contentType) {
    if (contentType == null) {
      return null;
    }
    if (contentType.hasWildcard()) {
      throw new IllegalArgumentException("Content Type must be fully specified!");
    }

    if (contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_SVC)) {
      return ATOM;
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)) {
      return XML;
    } else if (contentType.isCompatible(ContentType.APPLICATION_JSON)) {
      String jsonVariant = contentType.getParameters().get(JSON_METADATA_PARAMETER_V3);
      if (jsonVariant != null) {
        for (ODataFormat candidate : FORMAT_PER_VERSION.get(ODataServiceVersion.V30).keySet()) {
          if (FORMAT_PER_VERSION.get(ODataServiceVersion.V30).get(candidate).getParameters()
              .get(JSON_METADATA_PARAMETER_V3)
              .equals(jsonVariant)) {
            return candidate;
          }
        }
      }
      jsonVariant = contentType.getParameters().get(JSON_METADATA_PARAMETER_V4);
      if (jsonVariant != null) {
        for (ODataFormat candidate : FORMAT_PER_VERSION.get(ODataServiceVersion.V40).keySet()) {
          if (FORMAT_PER_VERSION.get(ODataServiceVersion.V40).get(candidate).getParameters()
              .get(JSON_METADATA_PARAMETER_V4)
              .equals(jsonVariant)) {
            return candidate;
          }
        }
      }
      return JSON;
    } else if (contentType.isCompatible(ContentType.APPLICATION_OCTET_STREAM)) {
      return APPLICATION_OCTET_STREAM;
    } else if (contentType.isCompatible(ContentType.TEXT_PLAIN)) {
      return TEXT_PLAIN;
    } else if (contentType.isCompatible(ContentType.APPLICATION_XHTML_XML)) {
      return APPLICATION_XHTML_XML;
    } else if (contentType.isCompatible(ContentType.APPLICATION_SVG_XML)) {
      return APPLICATION_SVG_XML;
    } else if (contentType.isCompatible(ContentType.APPLICATION_FORM_URLENCODED)) {
      return APPLICATION_FORM_URLENCODED;
    } else if (contentType.isCompatible(ContentType.MULTIPART_FORM_DATA)) {
      return MULTIPART_FORM_DATA;
    } else if (contentType.isCompatible(ContentType.TEXT_XML)) {
      return TEXT_XML;
    } else if (contentType.isCompatible(ContentType.TEXT_HTML)) {
      return TEXT_HTML;
    }

    throw new IllegalArgumentException("Unsupported content Type: " + contentType);
  }

  public static ODataFormat fromString(final String contentType) {
    return contentType == null ? null : fromContentType(ContentType.parse(contentType));
  }
}
