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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerResult;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.core.deserializer.batch.BufferedReaderIncludingLineEndings.Line;

public class BatchParser {

  private String contentTypeMime;
  private String rawServiceResolutionUri;
  private boolean isStrict;

  @SuppressWarnings("unchecked")
  public List<BatchRequestPart> parseBatchRequest(final InputStream in, final String contentType, final String baseUri,
      final String serviceResolutionUri, final boolean isStrict) throws BatchException {

    contentTypeMime = contentType;
    this.isStrict = isStrict;
    this.rawServiceResolutionUri = serviceResolutionUri;

    return (List<BatchRequestPart>) parse(in, new BatchRequestTransformator(baseUri, rawServiceResolutionUri));
  }

  private List<? extends BatchDeserializerResult> parse(final InputStream in,
      final BatchRequestTransformator transformator)
      throws BatchException {
    try {
      return parseBatch(in, transformator);
    } catch (IOException e) {
      throw new ODataRuntimeException(e);
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        throw new ODataRuntimeException(e);
      }
    }
  }

  private List<BatchDeserializerResult> parseBatch(final InputStream in, final BatchRequestTransformator transformator)
      throws IOException, BatchException {
    final String boundary = BatchParserCommon.getBoundary(contentTypeMime, 1);
    final List<BatchDeserializerResult> resultList = new LinkedList<BatchDeserializerResult>();
    final List<List<Line>> bodyPartStrings = splitBodyParts(in, boundary);

    for (List<Line> bodyPartString : bodyPartStrings) {
      BatchBodyPart bodyPart = new BatchBodyPart(bodyPartString, boundary, isStrict).parse();
      resultList.addAll(transformator.transform(bodyPart));
    }

    return resultList;
  }

  private List<List<Line>> splitBodyParts(final InputStream in, final String boundary) throws IOException,
      BatchException {
    final BufferedReaderIncludingLineEndings reader = new BufferedReaderIncludingLineEndings(new InputStreamReader(in));
    final List<Line> message = reader.toLineList();
    reader.close();

    return BatchParserCommon.splitMessageByBoundary(message, boundary);
  }
}
