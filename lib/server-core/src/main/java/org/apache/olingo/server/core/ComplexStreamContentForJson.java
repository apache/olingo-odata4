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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.olingo.commons.api.data.ComplexIterator;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializer;

class ComplexStreamContentForJson extends ComplexStreamContent {
  private final ODataJsonSerializer jsonSerializer;

  protected ComplexStreamContentForJson(ComplexIterator iterator, EdmComplexType edmComplexType,
          ODataJsonSerializer jsonSerializer, ServiceMetadata serviceMetadata,
          ComplexSerializerOptions options) {
    super(iterator, serviceMetadata, edmComplexType, options);

    this.jsonSerializer = jsonSerializer;
  }

  @Override
  protected void writeComplex(OutputStream outputStream) throws SerializerException {
    try {
      jsonSerializer.complexCollectionIntoStream(metadata, complexType, iterator, options, outputStream);
      outputStream.flush();
    } catch (final IOException e) {
      throw new ODataRuntimeException("Failed complex serialization", e);
    }
  }
}