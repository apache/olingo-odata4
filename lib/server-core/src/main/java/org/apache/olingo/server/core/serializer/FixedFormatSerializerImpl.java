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
package org.apache.olingo.server.core.serializer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.serializer.BatchSerializerException;
import org.apache.olingo.server.api.serializer.FixedFormatSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveValueSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;

public class FixedFormatSerializerImpl implements FixedFormatSerializer {

  @Override
  public InputStream binary(final byte[] binary) throws SerializerException {
    return new ByteArrayInputStream(binary);
  }

  @Override
  public InputStream count(final Integer count) throws SerializerException {
    try {
      return new ByteArrayInputStream(count.toString().getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new SerializerException("UTF-8 is nott supprted as an encoding", e,
          SerializerException.MessageKeys.UNSUPPORTED_ENCODING, "UTF-8");
    }
  }

  @Override
  public InputStream primitiveValue(final EdmPrimitiveType type, final Object value,
      final PrimitiveValueSerializerOptions options) throws SerializerException {
    try {
      final String result = type.valueToString(value,
          options.isNullable(), options.getMaxLength(),
          options.getPrecision(), options.getScale(), options.isUnicode());
      return new ByteArrayInputStream(result.getBytes("UTF-8"));
    } catch (final EdmPrimitiveTypeException e) {
      throw new SerializerException("Error in primitive-value formatting.", e,
          SerializerException.MessageKeys.WRONG_PRIMITIVE_VALUE,
          type.getFullQualifiedName().getFullQualifiedNameAsString(), value.toString());
    } catch (final UnsupportedEncodingException e) {
      throw new SerializerException("Encoding exception.", e, SerializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  @Override
  public InputStream asyncResponse(final ODataResponse odataResponse) throws SerializerException {
    AsyncResponseSerializer serializer = new AsyncResponseSerializer();
    return serializer.serialize(odataResponse);
  }

  // TODO: Signature refactoring for writeBatchResponse
  @Override
  public InputStream batchResponse(final List<ODataResponsePart> batchResponses, final String boundary)
      throws BatchSerializerException {
    final BatchResponseSerializer serializer = new BatchResponseSerializer();

    return serializer.serialize(batchResponses, boundary);
  }
}
