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
package org.apache.olingo.server.core.deserializer.batch;

import java.net.URI;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException.MessageKeys;

public class BatchTransformatorCommon {

  private BatchTransformatorCommon() {
    // Private Utility Constructor
  }

  public static void validateContentType(final Header headers, final ContentType expected)
      throws BatchDeserializerException {
    final List<String> contentTypes = headers.getHeaders(HttpHeader.CONTENT_TYPE);

    if (contentTypes.isEmpty()) {
      throw new BatchDeserializerException("Missing content type", MessageKeys.MISSING_CONTENT_TYPE,
          Integer.toString(headers.getLineNumber()));
    }
    BatchParserCommon.parseContentType(contentTypes.get(0), expected, headers.getLineNumber());
  }

  public static void validateContentTransferEncoding(final Header headers) throws BatchDeserializerException {
    final HeaderField contentTransferField = headers.getHeaderField(BatchParserCommon.CONTENT_TRANSFER_ENCODING);

    if (contentTransferField != null) {
      final List<String> contentTransferValues = contentTransferField.getValues();
      if (contentTransferValues.size() > 1
          || !BatchParserCommon.BINARY_ENCODING.equalsIgnoreCase(contentTransferValues.get(0))) {
        throw new BatchDeserializerException("Invalid Content-Transfer-Encoding header",
            MessageKeys.INVALID_CONTENT_TRANSFER_ENCODING, Integer.toString(headers.getLineNumber()));
      }
    }
  }

  public static int getContentLength(final Header headers) throws BatchDeserializerException {
    final HeaderField contentLengthField = headers.getHeaderField(HttpHeader.CONTENT_LENGTH);

    if (contentLengthField != null && contentLengthField.getValues().size() == 1) {
      try {
        final int contentLength = Integer.parseInt(contentLengthField.getValues().get(0));

        if (contentLength < 0) {
          throw new BatchDeserializerException("Invalid content length", MessageKeys.INVALID_CONTENT_LENGTH,
              Integer.toString(contentLengthField.getLineNumber()));
        }

        return contentLength;
      } catch (NumberFormatException e) {
        throw new BatchDeserializerException("Invalid content length", e, MessageKeys.INVALID_CONTENT_LENGTH,
            Integer.toString(contentLengthField.getLineNumber()));
      }
    }

    return -1;
  }

  public static void validateHost(final Header headers, final String baseUri) throws BatchDeserializerException {
    final HeaderField hostField = headers.getHeaderField(HttpHeader.HOST);

    if (hostField != null &&
        (hostField.getValues().size() > 1
            || !URI.create(baseUri).getAuthority().equalsIgnoreCase(hostField.getValues().get(0).trim()))) {
      throw new BatchDeserializerException("Invalid Host header",
          MessageKeys.INVALID_HOST, Integer.toString(headers.getLineNumber()));
    }
  }
}
