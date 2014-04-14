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

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.fit.UnsupportedMediaTypeException;

public enum Accept {

  TEXT(ContentType.TEXT_PLAIN.getMimeType(), ".txt"),
  XML(ContentType.APPLICATION_XML.getMimeType(), ".xml"),
  ATOM(ContentType.APPLICATION_ATOM_XML.getMimeType(), ".xml"),
  JSON(ContentType.APPLICATION_JSON.getMimeType() + ";odata=minimalmetadata",
          ContentType.APPLICATION_JSON.getMimeType() + ";odata.metadata=minimal", ".full.json"),
  JSON_NOMETA(ContentType.APPLICATION_JSON.getMimeType() + ";odata=nometadata",
          ContentType.APPLICATION_JSON.getMimeType() + ";odata.metadata=none", ".full.json"),
  JSON_FULLMETA(ContentType.APPLICATION_JSON.getMimeType() + ";odata=fullmetadata",
          ContentType.APPLICATION_JSON.getMimeType() + ";odata.metadata=full", ".full.json");

  private final String contentTypeV3;

  private final String contentTypeV4;

  private final String fileExtension;

  private static Pattern allTypesPattern = Pattern.compile("(.*,)?\\*/\\*([,;].*)?");

  Accept(final String contentTypeV3, final String fileExtension) {
    this.contentTypeV3 = contentTypeV3;
    this.contentTypeV4 = contentTypeV3;
    this.fileExtension = fileExtension;
  }

  Accept(final String contentTypeV3, final String contentTypeV4, final String fileExtension) {
    this.contentTypeV3 = contentTypeV3;
    this.contentTypeV4 = contentTypeV4;
    this.fileExtension = fileExtension;
  }

  public String toString(final ODataServiceVersion version) {
    return version.compareTo(ODataServiceVersion.V40) >= 0 ? contentTypeV4 : contentTypeV3;
  }

  public String getExtension() {
    return fileExtension;
  }

  public static Accept parse(final String contentType, final ODataServiceVersion version) {
    final Accept def;
    if (version.compareTo(ODataServiceVersion.V30) <= 0) {
      def = ATOM;
    } else {
      def = JSON_NOMETA;
    }

    return parse(contentType, version, def);
  }

  public static Accept parse(final String contentType, final ODataServiceVersion version, final Accept def) {
    if (StringUtils.isBlank(contentType) || allTypesPattern.matcher(contentType).matches()) {
      return def;
    } else if (contentType.startsWith(JSON_NOMETA.toString(version))) {
      return JSON_NOMETA;
    } else if (contentType.startsWith(JSON_FULLMETA.toString(version))) {
      return JSON_FULLMETA;
    } else if (contentType.startsWith(JSON.toString(version))
            || contentType.startsWith(ContentType.APPLICATION_JSON.getMimeType())) {

      return JSON;
    } else if (contentType.startsWith(XML.toString(version))) {
      return XML;
    } else if (contentType.startsWith(ATOM.toString(version))) {
      return ATOM;
    } else if (contentType.startsWith(TEXT.toString(version))) {
      return TEXT;
    } else {
      throw new UnsupportedMediaTypeException("Unsupported media type");
    }
  }
}
