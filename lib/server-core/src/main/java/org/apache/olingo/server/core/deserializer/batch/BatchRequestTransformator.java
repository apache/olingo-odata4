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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;

public class BatchRequestTransformator {
  private final String baseUri;
  private final String rawServiceResolutionUri;

  public BatchRequestTransformator(final String baseUri, final String serviceResolutionUri) {
    this.baseUri = baseUri;
    rawServiceResolutionUri = serviceResolutionUri;
  }

  public List<BatchRequestPart> transform(final BatchBodyPart bodyPart) throws BatchDeserializerException {
    final List<ODataRequest> requests = new LinkedList<ODataRequest>();
    final List<BatchRequestPart> resultList = new ArrayList<BatchRequestPart>();

    validateHeaders(bodyPart.getHeaders(), bodyPart.isChangeSet());

    for (BatchQueryOperation queryOperation : bodyPart.getRequests()) {
      requests.add(processQueryOperation(bodyPart, baseUri, queryOperation));
    }

    resultList.add(new BatchRequestPart(bodyPart.isChangeSet(), requests));
    return resultList;
  }

  private ODataRequest processQueryOperation(final BatchBodyPart bodyPart, final String baseUri,
      final BatchQueryOperation queryOperation) throws BatchDeserializerException {
    if (bodyPart.isChangeSet()) {
      BatchQueryOperation encapsulatedQueryOperation = ((BatchChangeSetPart) queryOperation).getRequest();
      handleContentId(queryOperation, encapsulatedQueryOperation);
      validateHeaders(queryOperation.getHeaders(), false);

      return createRequest(encapsulatedQueryOperation, baseUri, bodyPart.isChangeSet());
    } else {
      return createRequest(queryOperation, baseUri, bodyPart.isChangeSet());
    }
  }

  private void handleContentId(final BatchQueryOperation changeRequestPart, final BatchQueryOperation request)
      throws BatchDeserializerException {
    final HeaderField contentIdChangeRequestPart = getContentId(changeRequestPart);
    final HeaderField contentIdRequest = getContentId(request);

    if (contentIdChangeRequestPart == null && contentIdRequest == null) {
      throw new BatchDeserializerException("Missing content id", MessageKeys.MISSING_CONTENT_ID,
          Integer.toString(changeRequestPart.getHeaders().getLineNumber()));
    } else if (contentIdChangeRequestPart != null) {
      request.getHeaders().replaceHeaderField(contentIdChangeRequestPart);
    }
  }

  private HeaderField getContentId(final BatchQueryOperation queryOperation) throws BatchDeserializerException {
    final HeaderField contentIdHeader = queryOperation.getHeaders().getHeaderField(HttpHeader.CONTENT_ID);

    if (contentIdHeader != null) {
      if (contentIdHeader.getValues().size() == 1) {
        return contentIdHeader;
      } else {
        throw new BatchDeserializerException("Invalid Content-ID header", MessageKeys.INVALID_CONTENT_ID,
            Integer.toString(contentIdHeader.getLineNumber()));
      }
    }

    return null;
  }

  private ODataRequest createRequest(final BatchQueryOperation operation, final String baseUri,
      final boolean isChangeSet) throws BatchDeserializerException {
    final HttpRequestStatusLine statusLine =
        new HttpRequestStatusLine(operation.getHttpStatusLine(), baseUri, rawServiceResolutionUri);
    statusLine.validateHttpMethod(isChangeSet);
    BatchTransformatorCommon.validateHost(operation.getHeaders(), baseUri);

    validateBody(statusLine, operation);
    Charset charset = getCharset(operation);
    InputStream bodyStream = getBodyStream(operation, statusLine, charset);

    validateForbiddenHeader(operation);

    final ODataRequest request = new ODataRequest();
    request.setBody(bodyStream);
    request.setMethod(statusLine.getMethod());
    request.setRawBaseUri(statusLine.getRawBaseUri());
    request.setRawODataPath(statusLine.getRawODataPath());
    request.setRawQueryPath(statusLine.getRawQueryPath());
    request.setRawRequestUri(statusLine.getRawRequestUri());
    request.setRawServiceResolutionUri(statusLine.getRawServiceResolutionUri());

    for (final HeaderField field : operation.getHeaders()) {
      request.addHeader(field.getFieldName(), field.getValues());
    }

    return request;
  }

  private Charset getCharset(final BatchQueryOperation operation) {
    final ContentType contentType = ContentType.parse(operation.getHeaders().getHeader(HttpHeader.CONTENT_TYPE));
    if (contentType != null) {
      final String charsetValue = contentType.getParameter(ContentType.PARAMETER_CHARSET);
      if (charsetValue == null) {
        if (contentType.isCompatible(ContentType.APPLICATION_JSON) || contentType.getSubtype().contains("xml")) {
          return Charset.forName("UTF-8");
        }
      } else {
        return Charset.forName(charsetValue);
      }
    }
    return Charset.forName("ISO-8859-1");
  }

  private void validateForbiddenHeader(final BatchQueryOperation operation) throws BatchDeserializerException {
    final Header header = operation.getHeaders();

    if (header.exists(HttpHeader.WWW_AUTHENTICATE) || header.exists(HttpHeader.AUTHORIZATION)
        || header.exists(HttpHeader.EXPECT) || header.exists(HttpHeader.FROM) || header.exists(HttpHeader.MAX_FORWARDS)
        || header.exists(HttpHeader.RANGE) || header.exists(HttpHeader.TE)) {
      throw new BatchDeserializerException("Forbidden header", MessageKeys.FORBIDDEN_HEADER,
          Integer.toString(header.getLineNumber()));
    }
  }

  private InputStream getBodyStream(final BatchQueryOperation operation, final HttpRequestStatusLine statusLine,
      final Charset charset) throws BatchDeserializerException {
    if (statusLine.getMethod().equals(HttpMethod.GET)) {
      return new ByteArrayInputStream(new byte[0]);
    } else {
      int contentLength = BatchTransformatorCommon.getContentLength(operation.getHeaders());

      if (contentLength == -1) {
        return BatchParserCommon.convertLineListToInputStream(operation.getBody(), charset);
      } else {
        return BatchParserCommon.convertLineListToInputStream(operation.getBody(), charset, contentLength);
      }
    }
  }

  private void validateBody(final HttpRequestStatusLine statusLine, final BatchQueryOperation operation)
      throws BatchDeserializerException {
    if (statusLine.getMethod().equals(HttpMethod.GET) && isInvalidGetRequestBody(operation)) {
      throw new BatchDeserializerException("Invalid request line", MessageKeys.INVALID_CONTENT,
          Integer.toString(statusLine.getLineNumber()));
    }
  }

  private boolean isInvalidGetRequestBody(final BatchQueryOperation operation) {
    return operation.getBody().size() > 1
        || operation.getBody().size() == 1 && !operation.getBody().get(0).toString().trim().isEmpty();
  }

  private void validateHeaders(final Header headers, final boolean isChangeSet) throws BatchDeserializerException {
    if (isChangeSet) {
      BatchTransformatorCommon.validateContentType(headers, ContentType.MULTIPART_MIXED);
    } else {
      BatchTransformatorCommon.validateContentTransferEncoding(headers);
      BatchTransformatorCommon.validateContentType(headers, ContentType.APPLICATION_HTTP);
    }
  }
}
