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

import java.util.regex.Pattern;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.fit.UnsupportedMediaTypeException;

public enum Accept {

  TEXT(ContentType.TEXT_PLAIN, ".txt"),
  XML(ContentType.APPLICATION_XML, ".xml"),
  ATOM(ContentType.APPLICATION_ATOM_XML, ".xml"),
  JSON(ContentType.JSON, ".full.json"),
  JSON_NOMETA(ContentType.JSON_NO_METADATA, ".full.json"),
  JSON_FULLMETA(ContentType.JSON_FULL_METADATA, ".full.json");

  private static Pattern allTypesPattern = Pattern.compile("(.*,)?\\*/\\*([,;].*)?");

  private final ContentType contentType;
  private final String fileExtension;

  Accept(final ContentType contentType, final String fileExtension) {
    this.contentType = contentType;
    this.fileExtension = fileExtension;
  }

  @Override
  public String toString() {
    return contentType.toContentTypeString();
  }

  public String getExtension() {
    return fileExtension;
  }

  public static Accept parse(final String contentType) {
    return parse(contentType, JSON_NOMETA);
  }

  public static Accept parse(final String contentType, final Accept def) {
    if (contentType == null || contentType.isEmpty() || allTypesPattern.matcher(contentType).matches()) {
      return def;
    } else if (contentType.startsWith(JSON_NOMETA.toString())) {
      return JSON_NOMETA;
    } else if (contentType.startsWith(JSON_FULLMETA.toString())) {
      return JSON_FULLMETA;
    } else if (contentType.startsWith(JSON.toString())
        || contentType.startsWith(ContentType.APPLICATION_JSON.toContentTypeString())) {
      return JSON;
    } else if (contentType.startsWith(XML.toString())) {
      return XML;
    } else if (contentType.startsWith(ATOM.toString())) {
      return ATOM;
    } else if (contentType.startsWith(TEXT.toString())) {
      return TEXT;
    } else {
      throw new UnsupportedMediaTypeException(contentType);
    }
  }
}
