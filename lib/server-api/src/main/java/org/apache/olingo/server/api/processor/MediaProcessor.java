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
package org.apache.olingo.server.api.processor;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;

/**
 * Processor interface for handling Entity Media.
 */
public interface MediaProcessor extends Processor {

  /**
   * Reads entity media data from persistence and puts content and status into the response.
   * @param request  OData request object containing raw HTTP information
   * @param response OData response object for collecting response data
   * @param uriInfo  information of a parsed OData URI
   * @param format   requested content type after content negotiation
   * @throws ODataApplicationException if the service implementation encounters a failure
   * @throws SerializerException       if serialization failed
   */
  void readMedia(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType format)
      throws ODataApplicationException, SerializerException;

  /**
   * Creates entity media data in the persistence and puts content, status, and Location into the response.
   * @param request  OData request object containing raw HTTP information
   * @param response OData response object for collecting response data
   * @param uriInfo  information of a parsed OData URI
   * @param format   requested content type after content negotiation
   * @throws ODataApplicationException if the service implementation encounters a failure
   * @throws DeserializerException     if deserialization failed
   * @throws SerializerException       if serialization failed
   */
  void createMedia(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType format)
      throws ODataApplicationException, DeserializerException, SerializerException;

  /**
   * Updates entity media data in the persistence and puts content and status into the response.
   * @param request  OData request object containing raw HTTP information
   * @param response OData response object for collecting response data
   * @param uriInfo  information of a parsed OData URI
   * @param format   requested content type after content negotiation
   * @throws ODataApplicationException if the service implementation encounters a failure
   * @throws DeserializerException     if deserialization failed
   * @throws SerializerException       if serialization failed
   */
  void updateMedia(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType format)
      throws ODataApplicationException, DeserializerException, SerializerException;

  /**
   * Deletes entity media data from persistence and puts the status into the response.
   * @param request  OData request object containing raw HTTP information
   * @param response OData response object for collecting response data
   * @param uriInfo  information of a parsed OData URI
   * @throws ODataApplicationException if the service implementation encounters a failure
   */
  void deleteMedia(ODataRequest request, ODataResponse response, UriInfo uriInfo) throws ODataApplicationException;
}
