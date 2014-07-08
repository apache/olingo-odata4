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

import org.apache.olingo.commons.api.format.AcceptType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.processor.CustomContentTypeSupportProcessor;
import org.apache.olingo.server.api.processor.FormatContentTypeMapping;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.util.TypeUtil;

public class ContentNegotiator {

  private final static Logger LOG = LoggerFactory.getLogger(ContentNegotiator.class);

  private ContentNegotiator() {}

  private static List<FormatContentTypeMapping>
      getDefaultSupportedContentTypes(final Class<? extends Processor> processorClass) {
    List<FormatContentTypeMapping> defaults = new ArrayList<FormatContentTypeMapping>();

    if (processorClass == MetadataProcessor.class) {
      defaults.add(new FormatContentTypeMapping("xml", ContentType.APPLICATION_XML.toContentTypeString()));
    } else {
//      defaults.add(new FormatContentTypeMapping("json", ContentType.APPLICATION_JSON.toContentTypeString()));
      defaults.add(new FormatContentTypeMapping("json", ContentType.APPLICATION_JSON.toContentTypeString()
          + ";odata.metadata=minimal"));
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
      final Processor processor, final Class<? extends Processor> processorClass) {
    ContentType requestedContentType = null;

    List<FormatContentTypeMapping> supportedContentTypes = getSupportedContentTypes(processor, processorClass);

    List<String> acceptHeaderValues = request.getHeader(HttpHeader.ACCEPT);

    boolean supported = false;

    if (formatOption != null) {

      if ("json".equalsIgnoreCase(formatOption.getText().trim())) {
        requestedContentType = ContentType.APPLICATION_JSON_MIN;
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
    } else if (acceptHeaderValues != null) {
      List<AcceptType> acceptedContentTypes = new ArrayList<AcceptType>();

      for (String acceptHeaderValue : acceptHeaderValues) {
        acceptedContentTypes.addAll((AcceptType.create(acceptHeaderValue)));
      }
      AcceptType.sort(acceptedContentTypes);

      for (AcceptType acceptedType : acceptedContentTypes) {
        for (FormatContentTypeMapping supportedType : supportedContentTypes) {

          ContentType ct = ContentType.create(supportedType.getContentType());
          if (acceptedType.getParameters().containsKey("charset")) {
            String value = acceptedType.getParameters().get("charset");
            if ("utf8".equalsIgnoreCase(value) || "utf-8".equalsIgnoreCase(value)) {
              ct = ContentType.create(ct, "charset", "UTF-8");
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

      if (requestedContentType == null) {
        throw new RuntimeException("unsupported accept content type: " + acceptedContentTypes + " != "
            + supportedContentTypes);
      }
    } else {

      if (processorClass == MetadataProcessor.class) {
        requestedContentType = ContentType.APPLICATION_XML;
      } else {
        requestedContentType = ContentType.APPLICATION_JSON_MIN;
      }

      for (FormatContentTypeMapping entry : supportedContentTypes) {
        if (requestedContentType.isCompatible(ContentType.create(entry.getContentType().trim()))) {
          supported = true;
          break;
        }
      }
    }

    if (!supported) {
      throw new RuntimeException("unsupported accept content type: " + requestedContentType + " != "
          + supportedContentTypes);
    }

    LOG.debug("requested content type: " + requestedContentType);

    return requestedContentType;
  }
}
