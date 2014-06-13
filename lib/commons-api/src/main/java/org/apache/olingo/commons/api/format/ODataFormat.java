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
  MEDIA_TYPE_WILDCARD("*"),
  WILDCARD(ContentType.WILDCARD),
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

  private final String format;

  ODataFormat() {
    this.format = null;
  }

  ODataFormat(final String format) {
    this.format = format;
  }

  /**
   * Gets format as a string.
   *
   * @param version OData service version.
   * @return format as a string.
   */
  public String toString(final ODataServiceVersion version) {
    if (version.ordinal() < ODataServiceVersion.V30.ordinal()) {
      throw new IllegalArgumentException("Unsupported version " + version);
    }

    return format == null ? ContentType.formatPerVersion.get(version).get(this.name()) : format;
  }

  @Override
  public String toString() {
    if (format == null) {
      throw new UnsupportedOperationException();
    } else {
      return format;
    }
  }

  /**
   * Gets OData format from its string representation.
   *
   * @param format string representation of the format.
   * @return OData format.
   */
  public static ODataFormat fromString(final String format) {
    ODataFormat result = null;

    final StringBuffer _format = new StringBuffer();

    final String[] parts = format.split(";");
    _format.append(parts[0].trim());
    if (ContentType.APPLICATION_JSON.equals(parts[0].trim())) {
      if (parts.length > 1) {
        if (parts[1].trim().equalsIgnoreCase("charset=UTF-8")) {
          result = ODataFormat.JSON;
        } else {
          _format.append(';').append(parts[1].trim());
        }
      } else {
        result = ODataFormat.JSON;
      }
    }

    if (result == null) {
      final String candidate = _format.toString();
      for (ODataFormat value : values()) {
        if (candidate.equals(value.toString(ODataServiceVersion.V30))
                || candidate.equals(value.toString(ODataServiceVersion.V40))) {
          result = value;
          break;
        }
      }
    }

    if (result == null) {
      throw new IllegalArgumentException("Unsupported format: " + format);
    }

    return result;
  }
}
