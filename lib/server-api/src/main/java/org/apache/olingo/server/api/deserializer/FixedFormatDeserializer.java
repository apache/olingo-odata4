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
package org.apache.olingo.server.api.deserializer;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;

public interface FixedFormatDeserializer {

  /**
   * Reads binary data from an InputStream.
   * @param content the binary data as input stream
   * @return the binary data
   */
  public byte[] binary(InputStream content) throws DeserializerException;

  /**
   * Reads primitive-type data from an InputStream.
   * @param content the textual value as input stream
   * @param property EDM property
   */
  public Object primitiveValue(InputStream content, EdmProperty property) throws DeserializerException;

  /**
   * Reads parameter data (in URI syntax) from a String.
   * @param content the textual value as String
   * @param parameter EDM parameter
   */
  public Parameter parameter(String content, EdmParameter parameter) throws DeserializerException;

  /**
   * Reads batch data from an InputStream.
   * @param content the data as multipart input stream
   * @param boundary the boundary between the parts
   * @param options options for the deserializer
   * @return a list of batch-request parts
   */
  public List<BatchRequestPart> parseBatchRequest(InputStream content, String boundary, BatchOptions options)
      throws BatchDeserializerException;
}
