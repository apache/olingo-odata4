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
package org.apache.olingo.server.core.batch.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.batch.BatchParserResult;
import org.apache.olingo.server.api.batch.BatchRequestPart;
import org.apache.olingo.server.core.batch.BatchException;
import org.apache.olingo.server.core.batch.parser.BufferedReaderIncludingLineEndings.Line;
import org.apache.olingo.server.core.batch.transformator.BatchRequestTransformator;

public class BatchParser {

  private final String contentTypeMime;
  private final String baseUri;
  private final String rawServiceResolutionUri;
  private final boolean isStrict;
  
  public BatchParser(final String contentType, final String baseUri, final String serviceResolutionUri, 
      final boolean isStrict) {
    contentTypeMime = contentType;
    this.baseUri = BatchParserCommon.removeEndingSlash(baseUri);
    this.isStrict = isStrict;
    this.rawServiceResolutionUri = serviceResolutionUri;
  }

  @SuppressWarnings("unchecked")
  public List<BatchRequestPart> parseBatchRequest(final InputStream in) throws BatchException {
    return (List<BatchRequestPart>) parse(in, new BatchRequestTransformator(baseUri, rawServiceResolutionUri));
  }

  private List<? extends BatchParserResult> parse(final InputStream in, final BatchRequestTransformator transformator)
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

  private List<BatchParserResult> parseBatch(final InputStream in, final BatchRequestTransformator transformator)
      throws IOException, BatchException {
    final String boundary = BatchParserCommon.getBoundary(contentTypeMime, 1);
    final List<BatchParserResult> resultList = new LinkedList<BatchParserResult>();
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
