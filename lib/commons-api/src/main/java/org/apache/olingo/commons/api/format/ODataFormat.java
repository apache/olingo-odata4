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
package org.apache.olingo.commons.api.format;


/**
 * Available formats to be used in various contexts.
 */
public enum ODataFormat {

  /** JSON format with no metadata. */
  JSON_NO_METADATA(ContentType.create(ContentType.APPLICATION_JSON, "odata.metadata=none")),
  /** JSON format with minimal metadata (default). */
  JSON(ContentType.create(ContentType.APPLICATION_JSON, "odata.metadata=minimal")),
  /** JSON format with full metadata. */
  JSON_FULL_METADATA(ContentType.create(ContentType.APPLICATION_JSON, "odata.metadata=full")),

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

  private final ContentType contentType;

  ODataFormat(final ContentType contentType) {
    this.contentType = contentType;
  }

  /**
   * Gets format as {@link ContentType}.
   * @return format as ContentType.
   */
  public ContentType getContentType() {
    return contentType;
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

    if (contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_SVC)) {
      return ATOM;
    } else if (contentType.isCompatible(ContentType.APPLICATION_XML)) {
      return XML;
    } else if (contentType.isCompatible(ContentType.APPLICATION_JSON)) {
      String jsonVariant = contentType.getParameters().get("odata.metadata");
      if (jsonVariant != null) {
        if("none".equals(jsonVariant)){
          return JSON_NO_METADATA;
        }else if("minimal".equals(jsonVariant)){
          return ODataFormat.JSON;
        }else if("full".equals(jsonVariant)){
          return ODataFormat.JSON_FULL_METADATA;
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
