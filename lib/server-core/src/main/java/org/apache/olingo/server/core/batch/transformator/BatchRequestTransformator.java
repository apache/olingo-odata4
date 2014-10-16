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
package org.apache.olingo.server.core.batch.transformator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.batch.BatchParserResult;
import org.apache.olingo.server.core.batch.BatchException;
import org.apache.olingo.server.core.batch.BatchException.MessageKeys;
import org.apache.olingo.server.core.batch.parser.BatchBodyPart;
import org.apache.olingo.server.core.batch.parser.BatchChangeSetPart;
import org.apache.olingo.server.core.batch.parser.BatchParserCommon;
import org.apache.olingo.server.core.batch.parser.BatchPart;
import org.apache.olingo.server.core.batch.parser.BatchQueryOperation;
import org.apache.olingo.server.core.batch.parser.BatchRequestPartImpl;
import org.apache.olingo.server.core.batch.parser.Header;
import org.apache.olingo.server.core.batch.parser.HeaderField;
import org.apache.olingo.server.core.batch.transformator.BatchTransformatorCommon.HttpRequestStatusLine;

public class BatchRequestTransformator implements BatchTransformator {
  private final String baseUri;
  private final String rawServiceResolutionUri;

  public BatchRequestTransformator(final String baseUri, final String serviceResolutionUri) {
    this.baseUri = baseUri;
    this.rawServiceResolutionUri = serviceResolutionUri;
  }

  @Override
  public List<BatchParserResult> transform(final BatchBodyPart bodyPart) throws BatchException {
    final List<ODataRequest> requests = new LinkedList<ODataRequest>();
    final List<BatchParserResult> resultList = new ArrayList<BatchParserResult>();

    validateBodyPartHeader(bodyPart);

    for (BatchQueryOperation queryOperation : bodyPart.getRequests()) {
      requests.add(processQueryOperation(bodyPart, baseUri, queryOperation));
    }

    resultList.add(new BatchRequestPartImpl(bodyPart.isChangeSet(), requests));
    return resultList;
  }

  private ODataRequest
      processQueryOperation(BatchBodyPart bodyPart, String baseUri, BatchQueryOperation queryOperation)
          throws BatchException {
    if (bodyPart.isChangeSet()) {
      BatchQueryOperation encapsulatedQueryOperation = ((BatchChangeSetPart) queryOperation).getRequest();
      handleContentId(queryOperation, encapsulatedQueryOperation);
      validateHeader(queryOperation, true);

      return createRequest(encapsulatedQueryOperation, baseUri, bodyPart.isChangeSet());
    } else {
      return createRequest(queryOperation, baseUri, bodyPart.isChangeSet());
    }
  }

  private void handleContentId(BatchQueryOperation changeRequestPart, BatchQueryOperation request)
      throws BatchException {
    final HeaderField contentIdChangeRequestPart = getContentId(changeRequestPart);
    final HeaderField contentIdRequest = getContentId(request);

    if (contentIdChangeRequestPart == null && contentIdRequest == null) {
      throw new BatchException("Missing content type", MessageKeys.MISSING_CONTENT_ID, changeRequestPart.getHeaders()
          .getLineNumber());
    } else if (contentIdChangeRequestPart != null) {
      request.getHeaders().replaceHeaderField(contentIdChangeRequestPart);
    }
  }

  private HeaderField getContentId(final BatchQueryOperation queryOperation) throws BatchException {
    final HeaderField contentTypeHeader = queryOperation.getHeaders().getHeaderField(BatchParserCommon.HTTP_CONTENT_ID);

    if (contentTypeHeader != null) {
      if (contentTypeHeader.getValues().size() == 1) {
        return contentTypeHeader;
      } else {
        throw new BatchException("Invalid header", MessageKeys.INVALID_HEADER, contentTypeHeader.getLineNumber());
      }
    }

    return null;
  }

  private ODataRequest createRequest(BatchQueryOperation operation, String baseUri, boolean isChangeSet)
      throws BatchException {
    final HttpRequestStatusLine statusLine =
        new HttpRequestStatusLine(operation.getHttpStatusLine(), baseUri, rawServiceResolutionUri, operation
            .getHeaders());
    statusLine.validateHttpMethod(isChangeSet);

    validateBody(statusLine, operation);
    InputStream bodyStrean = getBodyStream(operation, statusLine);

    validateForbiddenHeader(operation);

    final ODataRequest request = new ODataRequest();
    request.setBody(bodyStrean);
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

  private void validateForbiddenHeader(BatchQueryOperation operation) throws BatchException {
    final Header header = operation.getHeaders();

    if (header.exists(HttpHeader.AUTHORIZATION) || header.exists(BatchParserCommon.HTTP_EXPECT)
        || header.exists(BatchParserCommon.HTTP_FROM) || header.exists(BatchParserCommon.HTTP_MAX_FORWARDS)
        || header.exists(BatchParserCommon.HTTP_RANGE) || header.exists(BatchParserCommon.HTTP_TE)) {
      throw new BatchException("Forbidden header", MessageKeys.FORBIDDEN_HEADER, header.getLineNumber());
    }
  }

  private InputStream getBodyStream(BatchQueryOperation operation, HttpRequestStatusLine statusLine)
      throws BatchException {
    if (statusLine.getMethod().equals(HttpMethod.GET)) {
      return new ByteArrayInputStream(new byte[0]);
    } else {
      int contentLength = BatchTransformatorCommon.getContentLength(operation.getHeaders());

      if (contentLength == -1) {
        return BatchParserCommon.convertLineListToInputStream(operation.getBody());
      } else {
        return BatchParserCommon.convertLineListToInputStream(operation.getBody(), contentLength);
      }
    }
  }

  private void validateBody(HttpRequestStatusLine statusLine, BatchQueryOperation operation) throws BatchException {
    if (statusLine.getMethod().equals(HttpMethod.GET) && isUnvalidGetRequestBody(operation)) {
      throw new BatchException("Invalid request line", MessageKeys.INVALID_CONTENT, statusLine.getLineNumber());
    }
  }

  private boolean isUnvalidGetRequestBody(final BatchQueryOperation operation) {
    return (operation.getBody().size() > 1)
        || (operation.getBody().size() == 1 && !"".equals(operation.getBody().get(0).toString().trim()));
  }

  private void validateHeader(BatchPart bodyPart, boolean isChangeSet) throws BatchException {
    final Header headers = bodyPart.getHeaders();

    BatchTransformatorCommon.validateContentType(headers, BatchParserCommon.PATTERN_CONTENT_TYPE_APPLICATION_HTTP);
    if (isChangeSet) {
      BatchTransformatorCommon.validateContentTransferEncoding(headers);
    }
  }

  private void validateBodyPartHeader(BatchBodyPart bodyPart) throws BatchException {
    final Header header = bodyPart.getHeaders();

    if (bodyPart.isChangeSet()) {
      BatchTransformatorCommon.validateContentType(header, BatchParserCommon.PATTERN_MULTIPART_BOUNDARY);
    } else {
      BatchTransformatorCommon.validateContentTransferEncoding(header);
      BatchTransformatorCommon.validateContentType(header, BatchParserCommon.PATTERN_CONTENT_TYPE_APPLICATION_HTTP);
    }
  }
}
