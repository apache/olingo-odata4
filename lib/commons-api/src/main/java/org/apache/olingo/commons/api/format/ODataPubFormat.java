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
 * Available formats for AtomPub exchange.
 */
public enum ODataPubFormat implements Format {

  /**
   * JSON format with no metadata.
   */
  JSON_NO_METADATA,
  /**
   * JSON format with minimal metadata (default).
   */
  JSON,
  /**
   * JSON format with no metadata.
   */
  JSON_FULL_METADATA,
  /**
   * Atom format.
   */
  ATOM;

  /**
   * Gets format as a string.
   *
   * @param version OData service version.
   * @return format as a string.
   */
  @Override
  public String toString(final ODataServiceVersion version) {
    if (version.ordinal() < ODataServiceVersion.V30.ordinal()) {
      throw new IllegalArgumentException("Unsupported version " + version);
    }

    return ContentType.formatPerVersion.get(version).get(this.name());
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException();
  }

  /**
   * Gets OData format from its string representation.
   *
   * @param format string representation of the format.
   * @return OData format.
   */
  public static ODataPubFormat fromString(final String format) {
    ODataPubFormat result = null;

    final StringBuffer _format = new StringBuffer();

    final String[] parts = format.split(";");
    _format.append(parts[0].trim());
    if (ContentType.APPLICATION_JSON.equals(parts[0].trim())) {
      if (parts.length > 1 && parts[1].startsWith("odata")) {
        _format.append(';').append(parts[1].trim());
      } else {
        result = ODataPubFormat.JSON;
      }
    }

    if (result == null) {
      final String candidate = _format.toString();
      for (ODataPubFormat value : values()) {
        if (candidate.equals(value.toString(ODataServiceVersion.V30))
                || candidate.equals(value.toString(ODataServiceVersion.V40))) {
          result = value;
        }
      }
    }

    if (result == null) {
      throw new IllegalArgumentException("Unsupported format: " + format);
    }

    return result;
  }
}
