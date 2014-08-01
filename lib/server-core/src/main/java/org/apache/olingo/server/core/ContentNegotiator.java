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

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.AcceptType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.processor.CustomContentTypeSupportProcessor;
import org.apache.olingo.server.api.processor.FormatContentTypeMapping;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;

public class ContentNegotiator {

  private ContentNegotiator() {}

  private static List<FormatContentTypeMapping>
      getDefaultSupportedContentTypes(final Class<? extends Processor> processorClass) {
    List<FormatContentTypeMapping> defaults = new ArrayList<FormatContentTypeMapping>();

    if (processorClass == MetadataProcessor.class) {
      defaults.add(new FormatContentTypeMapping("xml", ContentType.APPLICATION_XML.toContentTypeString()));
    } else {
      defaults.add(new FormatContentTypeMapping("json",
          ODataFormat.JSON.getContentType(ODataServiceVersion.V40).toContentTypeString()));
    }

    return defaults;
  }

  private static List<FormatContentTypeMapping> getSupportedContentTypes(final Processor processor,
      final Class<? extends Processor> processorClass) {

    List<FormatContentTypeMapping> supportedContentTypes = getDefaultSupportedContentTypes(processorClass);

    if (processor instanceof CustomContentTypeSupportProcessor) {
      supportedContentTypes =
          ((CustomContentTypeSupportProcessor) processor).modifySupportedContentTypes(supportedContentTypes,
              processorClass);
    }

    return supportedContentTypes;
  }

  public static ContentType doContentNegotiation(final FormatOption formatOption, final ODataRequest request,
      final Processor processor, final Class<? extends Processor> processorClass) throws ContentNegotiatorException {
    ContentType requestedContentType = null;

    List<FormatContentTypeMapping> supportedContentTypes = getSupportedContentTypes(processor, processorClass);

    String acceptHeaderValue = request.getHeader(HttpHeader.ACCEPT);

    boolean supported = false;

    if (formatOption != null) {
      if ("json".equalsIgnoreCase(formatOption.getText().trim())) {
        requestedContentType = ODataFormat.JSON.getContentType(ODataServiceVersion.V40);
        for (FormatContentTypeMapping entry : supportedContentTypes) {
          if (requestedContentType.isCompatible(ContentType.create(entry.getContentType().trim()))) {
            supported = true;
            break;
          }
        }
      } else if ("xml".equalsIgnoreCase(formatOption.getText().trim())) {
        requestedContentType = ContentType.APPLICATION_XML;
        for (FormatContentTypeMapping entry : supportedContentTypes) {
          if (requestedContentType.isCompatible(ContentType.create(entry.getContentType().trim()))) {
            supported = true;
            break;
          }
        }
      } else {
        for (FormatContentTypeMapping entry : supportedContentTypes) {
          if (formatOption.getText().equalsIgnoreCase(entry.getFormatAlias().trim())) {
            requestedContentType = ContentType.create(entry.getContentType().trim());
            supported = true;
            break;
          }
        }
      }
      if (!supported) {
        throw new ContentNegotiatorException("Unsupported $format = " + formatOption.getText(),
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_FORMAT_OPTION, formatOption.getText());
      }
    } else if (acceptHeaderValue != null) {
      List<AcceptType> acceptedContentTypes = AcceptType.create(acceptHeaderValue);

      for (AcceptType acceptedType : acceptedContentTypes) {
        for (FormatContentTypeMapping supportedType : supportedContentTypes) {

          ContentType ct = ContentType.create(supportedType.getContentType());
          if (acceptedType.getParameters().containsKey("charset")) {
            String value = acceptedType.getParameters().get("charset");
            if ("utf8".equalsIgnoreCase(value) || "utf-8".equalsIgnoreCase(value)) {
              ct = ContentType.create(ct, ContentType.PARAMETER_CHARSET_UTF8);
            } else {
              throw new ContentNegotiatorException("charset in accept header not supported: " + acceptHeaderValue,
                  ContentNegotiatorException.MessageKeys.WRONG_CHARSET_IN_HEADER, HttpHeader.ACCEPT, acceptHeaderValue);
            }
          }

          if (acceptedType.matches(ct)) {
            requestedContentType = ct;
            supported = true;
            break;
          }
        }
        if (supported) {
          break;
        }
      }

      if (!supported) {
        throw new ContentNegotiatorException(
            "unsupported accept content type: " + acceptedContentTypes + " != " + supportedContentTypes,
            ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPES, acceptedContentTypes.toString());
      }
    } else {
      if (processorClass == MetadataProcessor.class) {
        requestedContentType = ContentType.APPLICATION_XML;
      } else {
        requestedContentType = ODataFormat.JSON.getContentType(ODataServiceVersion.V40);
      }

      for (FormatContentTypeMapping entry : supportedContentTypes) {
        if (requestedContentType.isCompatible(ContentType.create(entry.getContentType().trim()))) {
          supported = true;
          break;
        }
      }
    }

    if (!supported) {
      throw new ContentNegotiatorException(
          "unsupported accept content type: " + requestedContentType + " != " + supportedContentTypes,
          ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPE, requestedContentType.toContentTypeString());
    }

    return requestedContentType;
  }
}
