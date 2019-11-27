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

import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.format.AcceptCharset;
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
  private static final String APPLICATION_JSON = "application/json";
  private static final String XML = "xml";
  private static final String METADATA = "METADATA";
  private static final String COLON = ":";
  private static final Pattern CHARSET_PATTERN = Pattern.compile("([^,][\\w!#$%&'*+-._`|~;^]*)");

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
      return Collections.unmodifiableList(Arrays.asList(ContentType.APPLICATION_XML,
          ContentType.APPLICATION_JSON));
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
    String acceptCharset = request.getHeader(HttpHeader.ACCEPT_CHARSET);
    List<AcceptCharset> charsets = null;
      
    ContentType result = null;

    if (formatOption != null && formatOption.getFormat() != null) {
      final String formatString = formatOption.getFormat().trim();
      final ContentType contentType = mapContentType(formatString, representationType);
      boolean isCharsetInFormat = false;
      List<AcceptType> formatTypes = null;
      try {
      formatTypes = AcceptType.fromContentType(contentType == null ?
          ContentType.create(formatOption.getFormat()) : contentType);
      } catch (final IllegalArgumentException e) {
        throw new AcceptHeaderContentNegotiatorException(
            "Unsupported $format=" + formatString, e,
            AcceptHeaderContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatString);
      }
      Map<String, String> formatParameters = formatTypes.get(0).getParameters();
      if (!formatParameters.isEmpty() && null != formatParameters.get(ContentType.PARAMETER_CHARSET)) {
        isCharsetInFormat = true;
      } else {
        isCharsetInFormat = false;
        charsets = getAcceptCharset(acceptCharset);
      }
      try {
        if (isCharsetInFormat) {
          charsets = getAcceptCharset(formatParameters.get(ContentType.PARAMETER_CHARSET));
        }
        result = getAcceptedType(formatTypes, supportedContentTypes, charsets);
      } catch (final IllegalArgumentException e) {
        throw new AcceptHeaderContentNegotiatorException(
            "Unsupported $format=" + formatString, e,
            AcceptHeaderContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatString);
      } catch (final AcceptHeaderContentNegotiatorException e) {
        throw new AcceptHeaderContentNegotiatorException (
            "Unsupported $format=" + formatString, e,
            AcceptHeaderContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatString);
      } catch (final ContentNegotiatorException e) {
        throw new ContentNegotiatorException (
            "Unsupported $format=" + formatString, e, 
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatString);
      }
      if (result == null) {
        throw new ContentNegotiatorException("Unsupported $format = " + formatString,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatString);
      }
    } else if (acceptHeaderValue != null) {
      charsets = getAcceptCharset(acceptCharset);
      try {
        result = getAcceptedType(AcceptType.create(acceptHeaderValue), 
            supportedContentTypes, charsets);
      } catch (final IllegalArgumentException e) {
        throw new AcceptHeaderContentNegotiatorException(e.getMessage(), e,
            AcceptHeaderContentNegotiatorException.MessageKeys.UNSUPPORTED_ACCEPT_TYPES, 
            e.getMessage().substring(e.getMessage().lastIndexOf(COLON) + 1));
      } 
      if (result == null) {
        List<AcceptType> types = AcceptType.create(acceptHeaderValue);
        throw new ContentNegotiatorException(
            "The combination of type and subtype " + types.get(0) +
            " != " + supportedContentTypes,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_ACCEPT_TYPES, acceptHeaderValue);
      }
    } else {
      charsets = getAcceptCharset(acceptCharset);
      final ContentType requestedContentType = getDefaultSupportedContentTypes(representationType).get(0);
      result = getAcceptedType(AcceptType.fromContentType(requestedContentType), 
          supportedContentTypes, charsets);
      
      if (result == null) {
        throw new ContentNegotiatorException(
            "unsupported accept content type: " + requestedContentType + " != " + supportedContentTypes,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPE,
            requestedContentType.toContentTypeString());
      }
    }
    return result;
  }
  
  /**
   * @param acceptCharset
   * @return
   * @throws ContentNegotiatorException
   * @throws AcceptHeaderContentNegotiatorException
   */
  private static List<AcceptCharset> getAcceptCharset(String acceptCharset)
      throws ContentNegotiatorException, AcceptHeaderContentNegotiatorException {
    List<AcceptCharset> charsets = null;
    if (acceptCharset != null) {
      try {
        charsets = AcceptCharset.create(acceptCharset); 
      } catch (UnsupportedCharsetException e) {
        throw new ContentNegotiatorException(e.getMessage(), e,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_ACCEPT_CHARSET, 
            e.getMessage().substring(e.getMessage().lastIndexOf(COLON) + 1));
      } catch (IllegalArgumentException e) {
        throw new AcceptHeaderContentNegotiatorException(e.getMessage(), e, 
            AcceptHeaderContentNegotiatorException.MessageKeys.UNSUPPORTED_ACCEPT_CHARSET_HEADER_OPTIONS, 
            e.getMessage().substring(e.getMessage().lastIndexOf(COLON) + 1));
      }
    }
    return charsets;
  }

  private static ContentType mapContentType(final String formatString, 
      RepresentationType representationType) {
    if (representationType.name().equals(METADATA)) {
      return JSON.equalsIgnoreCase(formatString) ||
          APPLICATION_JSON.equalsIgnoreCase(formatString) ? ContentType.APPLICATION_JSON :
        XML.equalsIgnoreCase(formatString) ? ContentType.APPLICATION_XML :
          ATOM.equalsIgnoreCase(formatString) ? ContentType.APPLICATION_ATOM_XML : null;
    } else {
      return JSON.equalsIgnoreCase(formatString) ? ContentType.JSON :
          XML.equalsIgnoreCase(formatString) ? ContentType.APPLICATION_XML :
              ATOM.equalsIgnoreCase(formatString) ? ContentType.APPLICATION_ATOM_XML : 
                APPLICATION_JSON.equalsIgnoreCase(formatString)? ContentType.APPLICATION_JSON: null;
    }
  }

  private static ContentType getAcceptedType(final List<AcceptType> acceptedContentTypes,
      final List<ContentType> supportedContentTypes, List<AcceptCharset> charsets) throws ContentNegotiatorException {
    if (charsets != null) {
      for (AcceptCharset charset : charsets) {
        return getContentType(acceptedContentTypes, supportedContentTypes, charset);
      }
    } else {
      return getContentType(acceptedContentTypes, supportedContentTypes, null);
    }
    return null;
  }

  private static ContentType getContentType(List<AcceptType> acceptedContentTypes,
      List<ContentType> supportedContentTypes, AcceptCharset charset) throws ContentNegotiatorException {
    for (final AcceptType acceptedType : acceptedContentTypes) {
      for (final ContentType supportedContentType : supportedContentTypes) {
        ContentType contentType = supportedContentType;
        final String charSetValue = acceptedType.getParameter(ContentType.PARAMETER_CHARSET);
        if (charset != null) {
          if ("*".equals(charset.toString())) {
            contentType = ContentType.create(contentType, ContentType.PARAMETER_CHARSET, "utf-8");
          } else {
            contentType = ContentType.create(contentType, ContentType.PARAMETER_CHARSET, charset.toString());
          }
        } else if (charSetValue != null) {
          if ("utf8".equalsIgnoreCase(charSetValue) || "utf-8".equalsIgnoreCase(charSetValue)) {
            contentType = ContentType.create(contentType, ContentType.PARAMETER_CHARSET, "utf-8");
          } else {
            if (CHARSET_PATTERN.matcher(charSetValue).matches()) {
              throw new ContentNegotiatorException("Unsupported accept-header-charset = " + charSetValue,
                  ContentNegotiatorException.MessageKeys.UNSUPPORTED_ACCEPT_HEADER_CHARSET, acceptedType.toString());
            } else {
              throw new AcceptHeaderContentNegotiatorException(
                  "Illegal charset in Accept header: " + charSetValue,
                  AcceptHeaderContentNegotiatorException.MessageKeys.UNSUPPORTED_ACCEPT_HEADER_CHARSET, 
                  acceptedType.toString());
            }
          }
        }

        final String ieee754compatibleValue = acceptedType.getParameter(ContentType.PARAMETER_IEEE754_COMPATIBLE);
        if ("true".equalsIgnoreCase(ieee754compatibleValue)) {
          contentType = ContentType.create(contentType, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true");
        } else if ("false".equalsIgnoreCase(ieee754compatibleValue)) {
          contentType = ContentType.create(contentType, ContentType.PARAMETER_IEEE754_COMPATIBLE, "false");
        } else if (ieee754compatibleValue != null) {
          throw new IllegalArgumentException("Invalid IEEE754Compatible value in accept header:" + 
              acceptedType.toString());
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
