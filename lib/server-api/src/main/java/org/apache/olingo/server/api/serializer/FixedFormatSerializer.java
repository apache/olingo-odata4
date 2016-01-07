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
package org.apache.olingo.server.api.serializer;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;

/** OData serializer for fixed output formats. */
public interface FixedFormatSerializer {

  /**
   * Writes binary output into an InputStream.
   * @param binary the binary data
   */
  InputStream binary(byte[] binary) throws SerializerException;

  /**
   * Writes a count into an InputStream as plain text.
   * @param count the count
   */
  InputStream count(Integer count) throws SerializerException;

  /**
   * Writes the raw value of a primitive-type instance into an InputStream.
   * @param type the primitive type
   * @param value the value
   * @param options options for the serializer
   */
  InputStream primitiveValue(EdmPrimitiveType type, Object value, PrimitiveValueSerializerOptions options)
      throws SerializerException;

  /**
   * Serializes a batch response.
   * @param batchResponses the response parts
   * @param boundary the boundary between the parts
   * @return response as an input stream
   */
  InputStream batchResponse(List<ODataResponsePart> batchResponses, String boundary) throws BatchSerializerException;

  /**
   * Serializes a ODataResponse into an async response.
   * @param odataResponse the response parts
   * @return response as an input stream
   */
  InputStream asyncResponse(ODataResponse odataResponse) throws SerializerException;
}
