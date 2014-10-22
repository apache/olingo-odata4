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
package org.apache.olingo.server.core;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.AcceptType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;

public class ContentNegotiator {

  private ContentNegotiator() {}

  private static List<ContentType> getDefaultSupportedContentTypes(final RepresentationType type) {
    switch (type) {
    case METADATA:
      return Arrays.asList(ContentType.APPLICATION_XML);
    case MEDIA:
    case BINARY:
      return Arrays.asList(ContentType.APPLICATION_OCTET_STREAM);
    case VALUE:
    case COUNT:
      return Arrays.asList(ContentType.TEXT_PLAIN);
    case BATCH:
      return Arrays.asList(ContentType.MULTIPART_MIXED);
    default:
      return Arrays.asList(
          ODataFormat.JSON.getContentType(ODataServiceVersion.V40),
          ODataFormat.JSON_NO_METADATA.getContentType(ODataServiceVersion.V40));
    }
  }

  private static List<ContentType> getSupportedContentTypes(
      final CustomContentTypeSupport customContentTypeSupport, final RepresentationType representationType)
          throws ContentNegotiatorException {
    final List<ContentType> defaultSupportedContentTypes = getDefaultSupportedContentTypes(representationType);
    final List<ContentType> result = customContentTypeSupport == null ? defaultSupportedContentTypes :
        customContentTypeSupport.modifySupportedContentTypes(defaultSupportedContentTypes, representationType);
    if (result == null || result.isEmpty()) {
      throw new ContentNegotiatorException("No content type has been specified as supported.",
          ContentNegotiatorException.MessageKeys.NO_CONTENT_TYPE_SUPPORTED);
    } else {
      return result;
    }
  }

  public static ContentType doContentNegotiation(final FormatOption formatOption, final ODataRequest request,
      final CustomContentTypeSupport customContentTypeSupport, final RepresentationType representationType)
          throws ContentNegotiatorException {
    final List<ContentType> supportedContentTypes =
        getSupportedContentTypes(customContentTypeSupport, representationType);
    final String acceptHeaderValue = request.getHeader(HttpHeader.ACCEPT);
    ContentType result = null;

    if (formatOption != null && formatOption.getFormat() != null) {
      final String formatString = formatOption.getFormat().trim();
      final ODataFormat format =
          ODataFormat.JSON.name().equalsIgnoreCase(formatString) ? ODataFormat.JSON :
          ODataFormat.XML.name().equalsIgnoreCase(formatString) ? ODataFormat.XML :
          ODataFormat.ATOM.name().equalsIgnoreCase(formatString) ? ODataFormat.ATOM : null;
      try {
        result = getAcceptedType(
            AcceptType.fromContentType(format == null ?
                ContentType.create(formatOption.getFormat()) : format.getContentType(ODataServiceVersion.V40)),
            supportedContentTypes);
      } catch (final IllegalArgumentException e) {}
      if (result == null) {
        throw new ContentNegotiatorException("Unsupported $format = " + formatString,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatString);
      }
    } else if (acceptHeaderValue != null) {
      final List<AcceptType> acceptedContentTypes = AcceptType.create(acceptHeaderValue);
      try {
        result = getAcceptedType(acceptedContentTypes, supportedContentTypes);
      } catch (final IllegalArgumentException e) {
        throw new ContentNegotiatorException("charset in accept header not supported: " + acceptHeaderValue, e,
            ContentNegotiatorException.MessageKeys.WRONG_CHARSET_IN_HEADER, HttpHeader.ACCEPT, acceptHeaderValue);
      }
      if (result == null) {
        throw new ContentNegotiatorException(
            "unsupported accept content type: " + acceptedContentTypes + " != " + supportedContentTypes,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPES, acceptedContentTypes.toString());
      }
    } else {
      final ContentType requestedContentType = getDefaultSupportedContentTypes(representationType).get(0);
      result = getAcceptedType(AcceptType.fromContentType(requestedContentType), supportedContentTypes);
      if (result == null) {
        throw new ContentNegotiatorException(
            "unsupported accept content type: " + requestedContentType + " != " + supportedContentTypes,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPE,
            requestedContentType.toContentTypeString());
      }
    }
    return result;
  }

  private static ContentType getAcceptedType(final List<AcceptType> acceptedContentTypes,
      final List<ContentType> supportedContentTypes) {
    for (final AcceptType acceptedType : acceptedContentTypes) {
      for (final ContentType supportedContentType : supportedContentTypes) {
        ContentType contentType = supportedContentType;
        if (acceptedType.getParameters().containsKey("charset")) {
          final String value = acceptedType.getParameters().get("charset");
          if ("utf8".equalsIgnoreCase(value) || "utf-8".equalsIgnoreCase(value)) {
            contentType = ContentType.create(contentType, ContentType.PARAMETER_CHARSET_UTF8);
          } else {
            throw new IllegalArgumentException("charset not supported: " + acceptedType);
          }
        }
        if (acceptedType.matches(contentType)) {
          return contentType;
        }
      }
    }
    return null;
  }
}
