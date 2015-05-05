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

import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;

public class BatchBodyPart implements BatchPart {
  final private String boundary;
  final private boolean isStrict;
  final List<Line> remainingMessage = new LinkedList<Line>();

  private Header headers;
  private boolean isChangeSet;
  private List<BatchQueryOperation> requests;

  public BatchBodyPart(final List<Line> message, final String boundary, final boolean isStrict) {
    this.boundary = boundary;
    this.isStrict = isStrict;
    remainingMessage.addAll(message);
  }

  public BatchBodyPart parse() throws BatchDeserializerException {
    headers = BatchParserCommon.consumeHeaders(remainingMessage);
    BatchParserCommon.consumeBlankLine(remainingMessage, isStrict);
    isChangeSet = isChangeSet(headers);
    requests = consumeRequest(remainingMessage);

    return this;
  }

  private boolean isChangeSet(final Header header) throws BatchDeserializerException {
    final List<String> contentTypes = headers.getHeaders(HttpHeader.CONTENT_TYPE);
    boolean isChangeSet = false;

    if (contentTypes.size() == 0) {
      throw new BatchDeserializerException("Missing content type",
          BatchDeserializerException.MessageKeys.MISSING_CONTENT_TYPE, ""
              + headers.getLineNumber());
    }

    for (String contentType : contentTypes) {
      if (isContentTypeMultiPartMixed(contentType)) {
        isChangeSet = true;
      }
    }

    return isChangeSet;
  }

  private List<BatchQueryOperation> consumeRequest(final List<Line> remainingMessage)
      throws BatchDeserializerException {
    if (isChangeSet) {
      return consumeChangeSet(remainingMessage);
    } else {
      return consumeQueryOperation(remainingMessage);
    }
  }

  private List<BatchQueryOperation> consumeChangeSet(final List<Line> remainingMessage2)
      throws BatchDeserializerException {
    final List<List<Line>> changeRequests = splitChangeSet(remainingMessage);
    final List<BatchQueryOperation> requestList = new LinkedList<BatchQueryOperation>();

    for (List<Line> changeRequest : changeRequests) {
      requestList.add(new BatchChangeSetPart(changeRequest, isStrict).parse());
    }

    return requestList;
  }

  private List<List<Line>> splitChangeSet(final List<Line> remainingMessage2) throws BatchDeserializerException {

    final HeaderField contentTypeField = headers.getHeaderField(HttpHeader.CONTENT_TYPE);
    final String changeSetBoundary = BatchParserCommon.getBoundary(contentTypeField.getValueNotNull(),
        contentTypeField.getLineNumber());
    validateChangeSetBoundary(changeSetBoundary, headers);

    return BatchParserCommon.splitMessageByBoundary(remainingMessage, changeSetBoundary);
  }

  private void validateChangeSetBoundary(final String changeSetBoundary, final Header header)
      throws BatchDeserializerException {
    if (changeSetBoundary.equals(boundary)) {
      throw new BatchDeserializerException("Change set boundary is equals to batch request boundary",
          BatchDeserializerException.MessageKeys.INVALID_BOUNDARY,
          "" + header.getHeaderField(HttpHeader.CONTENT_TYPE).getLineNumber());
    }
  }

  private List<BatchQueryOperation> consumeQueryOperation(final List<Line> remainingMessage)
      throws BatchDeserializerException {
    final List<BatchQueryOperation> requestList = new LinkedList<BatchQueryOperation>();
    requestList.add(new BatchQueryOperation(remainingMessage, isStrict).parse());

    return requestList;
  }

  private boolean isContentTypeMultiPartMixed(final String contentType) {
    return BatchParserCommon.PATTERN_MULTIPART_BOUNDARY.matcher(contentType).matches();
  }

  @Override
  public Header getHeaders() {
    return headers;
  }

  @Override
  public boolean isStrict() {
    return isStrict;
  }

  public boolean isChangeSet() {
    return isChangeSet;
  }

  public List<BatchQueryOperation> getRequests() {
    return requests;
  }
}
