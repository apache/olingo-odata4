/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.api.serialization;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

public interface ClientODataDeserializer extends ODataDeserializer {

  XMLMetadata toMetadata(InputStream input);
  
  /**
   * Gets all the terms defined in the given input stream
   * @param input
   * @return
   */
  List<CsdlSchema> fetchTermDefinitionSchema(List<InputStream> input);

  /**
   * Gets the ServiceDocument object represented by the given InputStream.
   *
   * @param input stream to be de-serialized.
   * @return <tt>ServiceDocument</tt> object.
   * @throws ODataDeserializerException
   */
  ResWrap<ServiceDocument> toServiceDocument(InputStream input) throws ODataDeserializerException;
  
  /**
   * Gets a delta object from the given InputStream.
   *
   * @param input stream to be de-serialized.
   * @return {@link Delta} instance.
   * @throws ODataDeserializerException
   */
  ResWrap<Delta> toDelta(InputStream input) throws ODataDeserializerException;
}
