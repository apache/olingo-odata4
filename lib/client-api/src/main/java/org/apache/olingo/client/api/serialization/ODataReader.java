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
package org.apache.olingo.client.api.serialization;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.format.ContentType;


/**
 * OData reader.
 * <br/>
 * Use this class to de-serialize an OData response body.
 * <br/>
 * This class provides method helpers to de-serialize an entire entity set, a set of entities or a single entity.
 */
public interface ODataReader {
  /**
   * Parses a stream into metadata representation.
   *
   * @param input stream to de-serialize.
   * @return metadata representation.
   */
  Edm readMetadata(InputStream input);
  
  /**
   * Parses a stream into metadata representation. 
   * Also parses a term definition stream into Term representation.
   * @param input
   * @param termDefinitions
   * @return
   */
  Edm readMetadata(InputStream input, List<InputStream> termDefinitions);

  /**
   * Fetches schema from metadata document and parses the document which includes term definitions
   * @param metadata
   * @param termDefinitions
   * @return
   */
  Edm readMetadata(XMLMetadata metadata, List<InputStream> termDefinitions);

  /**
   * Parses a stream into metadata representation, including referenced metadata documents.
   *
   * @param xmlSchemas XML representation of the requested metadata document + any other referenced (via
   * <tt>&lt;edmx:Reference/&gt;</tt>) metadata document
   * @return metadata representation.
   */
  Edm readMetadata(Map<String, CsdlSchema> xmlSchemas);
  
  /**
   * Parses metadata document along with the document which includes term definitions
   * @param xmlSchemas
   * @param termDefinitionSchema
   * @return
   */
  Edm readMetadata(Map<String, CsdlSchema> xmlSchemas, List<CsdlSchema> termDefinitionSchema);

  /**
   * Parses an OData service document.
   *
   * @param input stream to de-serialize.
   * @param contentType de-serialize as XML or JSON
   * @return List of URIs.
   * @throws ODataDeserializerException
   */
  ClientServiceDocument readServiceDocument(InputStream input, ContentType contentType) 
      throws ODataDeserializerException;

  /**
   * De-Serializes a stream into an OData entity set.
   *
   * @param input stream to de-serialize.
   * @param contentType de-serialize format
   * @return de-serialized entity set.
   * @throws ODataDeserializerException
   */
  ClientEntitySet readEntitySet(InputStream input, ContentType contentType) throws ODataDeserializerException;

  /**
   * Parses a stream taking care to de-serializes the first OData entity found.
   *
   * @param input stream to de-serialize.
   * @param contentType de-serialize format
   * @return entity de-serialized.
   * @throws ODataDeserializerException
   */
  ClientEntity readEntity(InputStream input, ContentType contentType) throws ODataDeserializerException;

  /**
   * Parses a stream taking care to de-serialize the first OData entity property found.
   *
   * @param input stream to de-serialize.
   * @param contentType de-serialize as XML or JSON
   * @return OData entity property de-serialized.
   * @throws ODataDeserializerException
   */
  ClientProperty readProperty(InputStream input, ContentType contentType) throws ODataDeserializerException;

  /**
   * Parses a stream into an OData error.
   *
   * @param inputStream stream to de-serialize.
   * @param contentType format
   * @return OData error.
   * @throws ODataDeserializerException
   */
  ODataError readError(InputStream inputStream, ContentType contentType) throws ODataDeserializerException;

  /**
   * Parses a stream into the object type specified by the given reference.
   *
   * @param <T> expected object type.
   * @param src input stream.
   * @param format format
   * @param reference reference.
   * @return read object.
   * @throws ODataDeserializerException
   */
  <T> ResWrap<T> read(InputStream src, String format, Class<T> reference) throws ODataDeserializerException;
}
