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
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.format.AcceptType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;

public final class ContentNegotiator {

  private static final String ATOM = "atom";
  private static final String JSON = "json";
  private static final String XML = "xml";

  private static final List<ContentType> DEFAULT_SUPPORTED_CONTENT_TYPES =
      Collections.unmodifiableList(Arrays.asList(
          ContentType.JSON,
          ContentType.JSON_NO_METADATA,
          ContentType.APPLICATION_JSON,
          ContentType.JSON_FULL_METADATA,
          ContentType.APPLICATION_ATOM_XML,
          ContentType.APPLICATION_XML));

  private ContentNegotiator() {}

  private static List<ContentType> getDefaultSupportedContentTypes(final RepresentationType type) {
    switch (type) {
    case METADATA:
      return Collections.singletonList(ContentType.APPLICATION_XML);
    case MEDIA:
    case BINARY:
      return Collections.singletonList(ContentType.APPLICATION_OCTET_STREAM);
    case VALUE:
    case COUNT:
      return Collections.singletonList(ContentType.TEXT_PLAIN);
    case BATCH:
      return Collections.singletonList(ContentType.MULTIPART_MIXED);
    default:
      return DEFAULT_SUPPORTED_CONTENT_TYPES;
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
      final ContentType contentType = mapContentType(formatString);

      try {
        result = getAcceptedType(
            AcceptType.fromContentType(contentType == null ?
                ContentType.create(formatOption.getFormat()) : contentType),
                supportedContentTypes);
      } catch (final IllegalArgumentException e) {
        // Exception results in result = null for next check.
      }
      if (result == null) {
        throw new ContentNegotiatorException("Unsupported $format = " + formatString,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatString);
      }
    } else if (acceptHeaderValue != null) {
      try {
        result = getAcceptedType(AcceptType.create(acceptHeaderValue), supportedContentTypes);
      } catch (final IllegalArgumentException e) {
        result = null;
      }
      if (result == null) {
        throw new ContentNegotiatorException(
            "Unsupported or illegal Accept header value: " + acceptHeaderValue + " != " + supportedContentTypes,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_ACCEPT_TYPES, acceptHeaderValue);
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

  private static ContentType mapContentType(final String formatString) {
    return JSON.equalsIgnoreCase(formatString) ? ContentType.JSON :
        XML.equalsIgnoreCase(formatString) ? ContentType.APPLICATION_XML :
            ATOM.equalsIgnoreCase(formatString) ? ContentType.APPLICATION_ATOM_XML : null;
  }

  private static ContentType getAcceptedType(final List<AcceptType> acceptedContentTypes,
      final List<ContentType> supportedContentTypes) {
    for (final AcceptType acceptedType : acceptedContentTypes) {
      for (final ContentType supportedContentType : supportedContentTypes) {
        ContentType contentType = supportedContentType;
        final String charSetValue = acceptedType.getParameter(ContentType.PARAMETER_CHARSET);
        if (charSetValue != null) {
          if ("utf8".equalsIgnoreCase(charSetValue) || "utf-8".equalsIgnoreCase(charSetValue)) {
            contentType = ContentType.create(contentType, ContentType.PARAMETER_CHARSET, "utf-8");
          } else {
            throw new IllegalArgumentException("charset not supported: " + acceptedType);
          }
        }

        final String ieee754compatibleValue = acceptedType.getParameter(ContentType.PARAMETER_IEEE754_COMPATIBLE);
        if ("true".equalsIgnoreCase(ieee754compatibleValue)) {
          contentType = ContentType.create(contentType, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true");
        } else if ("false".equalsIgnoreCase(ieee754compatibleValue)) {
          contentType = ContentType.create(contentType, ContentType.PARAMETER_IEEE754_COMPATIBLE, "false");
        } else if (ieee754compatibleValue != null) {
          throw new IllegalArgumentException("Invalid IEEE754Compatible value " + ieee754compatibleValue);
        }

        if (acceptedType.matches(contentType)) {
          return contentType;
        }
      }
    }
    return null;
  }

  public static void checkSupport(final ContentType contentType,
      final CustomContentTypeSupport customContentTypeSupport, final RepresentationType representationType)
          throws ContentNegotiatorException {
    for (ContentType supportedContentType : getSupportedContentTypes(customContentTypeSupport, representationType)) {
      if (AcceptType.fromContentType(supportedContentType).get(0).matches(contentType)) {
        return;
      }
    }
    throw new ContentNegotiatorException("unsupported content type: " + contentType,
        ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPE, contentType.toContentTypeString());
  }

  public static boolean isSupported(final ContentType contentType,
      final CustomContentTypeSupport customContentTypeSupport,
      final RepresentationType representationType) throws ContentNegotiatorException {

    for (ContentType supportedContentType : getSupportedContentTypes(customContentTypeSupport, representationType)) {
      if (AcceptType.fromContentType(supportedContentType).get(0).matches(contentType)) {
        return true;
      }
    }
    return false;
  }
}
