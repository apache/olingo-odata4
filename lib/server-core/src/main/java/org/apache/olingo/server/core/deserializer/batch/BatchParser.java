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
import java.util.LinkedList;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;

public class BatchParser {

  private BatchOptions options;

  public List<BatchRequestPart> parseBatchRequest(final InputStream content, final String boundary,
      final BatchOptions options)
      throws BatchDeserializerException {
    this.options = options;

    BatchRequestTransformator transformator = new BatchRequestTransformator(options.getRawBaseUri(),
        options.getRawServiceResolutionUri());
    return parse(content, boundary, transformator);
  }

  private List<BatchRequestPart> parse(final InputStream in, final String boundary,
      final BatchRequestTransformator transformator)
      throws BatchDeserializerException {
    try {
      return parseBatch(in, boundary, transformator);
    } catch (IOException e) {
      throw new ODataRuntimeException(e);
    }
  }

  private List<BatchRequestPart> parseBatch(final InputStream in, final String boundary,
      final BatchRequestTransformator transformator) throws IOException, BatchDeserializerException {
    final List<BatchRequestPart> resultList = new LinkedList<BatchRequestPart>();
    final List<List<Line>> bodyPartStrings = splitBodyParts(in, boundary);

    for (List<Line> bodyPartString : bodyPartStrings) {
      BatchBodyPart bodyPart = new BatchBodyPart(bodyPartString, boundary, options.isStrict()).parse();
      resultList.addAll(transformator.transform(bodyPart));
    }

    return resultList;
  }

  private List<List<Line>> splitBodyParts(final InputStream in, final String boundary) throws IOException,
      BatchDeserializerException {
    final BatchLineReader reader = new BatchLineReader(in);
    final List<Line> message = reader.toLineList();
    reader.close();

    return BatchParserCommon.splitMessageByBoundary(message, boundary);
  }
}
