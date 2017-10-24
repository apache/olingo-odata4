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

import java.util.List;

import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;

public class BatchQueryOperation implements BatchPart {

  protected final boolean isStrict;
  protected Line httpStatusLine;
  protected Header headers;
  protected List<Line> body;
  protected int bodySize;
  protected List<Line> message;

  public BatchQueryOperation(final List<Line> message, final boolean isStrict) {
    this.isStrict = isStrict;
    this.message = message;
  }

  public BatchQueryOperation parse() throws BatchDeserializerException {
    httpStatusLine = consumeHttpStatusLine(message);
    headers = BatchParserCommon.consumeHeaders(message);
    BatchParserCommon.consumeBlankLine(message, isStrict);
    body = message;

    return this;
  }

  protected Line consumeHttpStatusLine(final List<Line> message) throws BatchDeserializerException {
    if (!message.isEmpty() && !"".equals(message.get(0).toString().trim())) {
      final Line method = message.get(0);
      message.remove(0);

      return method;
    } else {
      final int line = (!message.isEmpty()) ? message.get(0).getLineNumber() : 0;
      throw new BatchDeserializerException("Missing http request line",
          BatchDeserializerException.MessageKeys.INVALID_STATUS_LINE, "" + line);
    }
  }

  public Line getHttpStatusLine() {
    return httpStatusLine;
  }

  public List<Line> getBody() {
    return body;
  }

  public int getBodySize() {
    return bodySize;
  }

  @Override
  public Header getHeaders() {
    return headers;
  }

  @Override
  public boolean isStrict() {
    return isStrict;
  }
}
