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

import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.processor.Processor;

public interface ServiceHandler extends Processor {

  void readMetadata(MetadataRequest request, MetadataResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  void readServiceDocument(ServiceDocumentRequest request, ServiceDocumentResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  <T extends ServiceResponse> void read(DataRequest request, T response)
      throws ODataTranslatedException, ODataApplicationException;

  void createEntity(DataRequest request, Entity entity, EntityResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  void updateEntity(DataRequest request, Entity entity, boolean merge, String entityETag,
      EntityResponse response) throws ODataTranslatedException, ODataApplicationException;

  void deleteEntity(DataRequest request, String entityETag, EntityResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  // delete property means update setting the value to null for nullable
  // property,
  // otherwise send bad request error. 11.4.9.2
  // response can be PropertyResponse
  void updateProperty(DataRequest request, Property property, boolean merge, String entityETag,
      PropertyResponse response) throws ODataTranslatedException, ODataApplicationException;

  void updateStreamProperty(DataRequest request, InputStream property, String entityETag,
      StreamResponse response) throws ODataTranslatedException, ODataApplicationException;

  <T extends ServiceResponse> void invoke(FunctionRequest request, HttpMethod method, T response)
      throws ODataTranslatedException, ODataApplicationException;

  <T extends ServiceResponse> void invoke(ActionRequest request, String eTag, T response)
      throws ODataTranslatedException, ODataApplicationException;

  // creating media entity is same as entity, with additional stream property
  // but there is no property to reference the stream in $metadata, the response
  // should include the links for media
  // do we really need this method?? createEntity can be used
  void createMediaEntity(DataRequest request, Entity entity, InputStream media,
      EntityResponse response) throws ODataTranslatedException, ODataApplicationException;

  // read/update/delete media stream [read|edit] url are either user or framework defined.
  // for olingo, this being defined by framework, so that the below methods can be wired
  // accordingly
  void readMediaStream(MediaRequest request, StreamResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  void updateMediaStream(MediaRequest request, String entityETag,
      StreamResponse response) throws ODataTranslatedException, ODataApplicationException;

  void deleteMediaStream(MediaRequest request, String entityETag, NoContentResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  void anyUnsupported(ODataRequest request, ODataResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  // Reference Support, return always should be 204
  void addReference(DataRequest request, String entityETag, URI uri, NoContentResponse response)
      throws ODataTranslatedException, ODataApplicationException;
  // Successful return always should be 204
  void updateReference(DataRequest request, String entityETag, URI uri, NoContentResponse response)
      throws ODataTranslatedException, ODataApplicationException;
  // Successful return always should be 204
  void deleteReference(DataRequest request, String entityETag, NoContentResponse response)
      throws ODataTranslatedException, ODataApplicationException;

  // to support atomic processing of the change sets in the batch processing.
  String startTransaction();
  void commit(String txnId);
  void rollback(String txnId);
}
