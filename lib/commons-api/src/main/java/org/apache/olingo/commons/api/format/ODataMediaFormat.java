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
 * Available formats for media.
 */
public enum ODataMediaFormat implements Format{

  CHARSET_PARAMETER("charset"),
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

  private ODataMediaFormat(final String format) {
    this.format = format;
  }

  @Override
  public String toString() {
    return format;
  }

  public static ODataMediaFormat fromFormat(final String format) {
    final String _format = format.split(";")[0];

    ODataMediaFormat result = null;

    for (ODataMediaFormat value : values()) {
      if (_format.equals(value.toString())) {
        result = value;
      }
    }

    if (result == null) {
      throw new IllegalArgumentException("Unsupported format: " + format);
    }

    return result;
  }

  @Override
  public String toString(final ODataServiceVersion version) {
    return this.toString();
  }
}
