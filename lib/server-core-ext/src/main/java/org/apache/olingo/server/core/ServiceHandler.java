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
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.core.requests.ActionRequest;
import org.apache.olingo.server.core.requests.DataRequest;
import org.apache.olingo.server.core.requests.FunctionRequest;
import org.apache.olingo.server.core.requests.MediaRequest;
import org.apache.olingo.server.core.requests.MetadataRequest;
import org.apache.olingo.server.core.requests.ServiceDocumentRequest;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.ErrorResponse;
import org.apache.olingo.server.core.responses.MetadataResponse;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;
import org.apache.olingo.server.core.responses.ServiceDocumentResponse;
import org.apache.olingo.server.core.responses.ServiceResponse;
import org.apache.olingo.server.core.responses.StreamResponse;

public interface ServiceHandler extends Processor {

  /**
   * Read CSDL document of the Service
   * @param request
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void readMetadata(MetadataRequest request, MetadataResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Read ServiceDocument of the service
   * @param request
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void readServiceDocument(ServiceDocumentRequest request, ServiceDocumentResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Read operation for EntitySets, Entities, Properties, Media etc. Based on the type of request
   * the response object is different. Even the navigation based queries are handled by this method.
   * @param request
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  <T extends ServiceResponse> void read(DataRequest request, T response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Create new entity in the service based on the entity object provided
   * @param request
   * @param entity
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void createEntity(DataRequest request, Entity entity, EntityResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Update the entity object.
   * @param request
   * @param entity
   * @param merge - true if merge operation, false it needs to be replaced
   * @param entityETag - previous entity tag if provided by the user. "*" means allow.
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void updateEntity(DataRequest request, Entity entity, boolean merge, String entityETag,
      EntityResponse response) throws ODataLibraryException, ODataApplicationException;

  /**
   * Update or create the entity object. If based on passed in entity object's key value,  if 
   * entity exists update the entity, else create a new entity
   * @param request
   * @param entity - Entity to create or update
   * @param merge - in the case of update, true to do merge operation with current entity, 
   * false the entity needs to be replaced
   * @param entityETag - previous entity tag if provided by the user. "*" means allow.
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void upsertEntity(DataRequest request, Entity entity, boolean merge, String entityETag,
      EntityResponse response) throws ODataLibraryException, ODataApplicationException;

  /**
   * Delete the Entity
   * @param request
   * @param entityETag - entity tag to match, if provided by the user. "*" means allow
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void deleteEntity(DataRequest request, String entityETag, EntityResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Update a non-media/stream property.if the value of property NULL, it should be treated as
   * DeleteProperty 11.4.9.2
   * @param request
   * @param property - Updated property.
   * @param rawValue - $value based call, where property value provided is in byte[] format. 
   *                   user must convert the value to correct datatype format before update. 
   *                   The semantics of conversion are not defined. 
   * @param merge - if the property is complex, true here means merge, false is replace
   * @param entityETag - entity tag to match before update operation, "*" allows all.
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void updateProperty(DataRequest request, Property property, boolean rawValue,
      boolean merge, String entityETag, PropertyResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Update Stream property, if StreamContent is null, it should treated as delete request
   * @param request
   * @param entityETag - entity tag to match before update operation, "*" allows all.
   * @param streamContent - updated stream content
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void upsertStreamProperty(DataRequest request, String entityETag, InputStream streamContent,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException;

  /**
   * Invocation of a Function. The response object will be based on metadata defined for service
   * @param request
   * @param method
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  <T extends ServiceResponse> void invoke(FunctionRequest request, HttpMethod method, T response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Invocation of a Function. The response object will be based on metadata defined for service
   * @param request
   * @param eTag
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  <T extends ServiceResponse> void invoke(ActionRequest request, String eTag, T response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Read media stream content of a Entity
   * @param request
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void readMediaStream(MediaRequest request, StreamResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Update of Media Stream Content of a Entity. If the mediaContent is null it should be treated
   * as delete request.
   * @param request
   * @param entityETag - entity etag to match before update operation, "*" allows all.
   * @param mediaContent - if null, must be treated as delete request
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void upsertMediaStream(MediaRequest request, String entityETag, InputStream mediaContent,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException;

  /**
   * Any Unsupported one will be directed here.
   * @param request
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void anyUnsupported(ODataRequest request, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Add references (relationships) to Entity. This is always on collection valued navigation property
   * @param request
   * @param entityETag - entity etag to match before add operation, "*" allows all.
   * @param referenceId - references to add
   * @param response - return always should be 204
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void addReference(DataRequest request, String entityETag, URI referenceId, NoContentResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Update references (relationships) in an Entity; This is always against single valued navigation property
   * @param request
   * @param entityETag
   * @param referenceId
   * @param response - always should be 204
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void updateReference(DataRequest request, String entityETag, URI referenceId, NoContentResponse response)
      throws ODataLibraryException, ODataApplicationException;

  /**
   * Delete references (relationships) in an Entity
   * @param request
   * @param deleteId for collection valued navigation this will be non-null value;
   *                 for single valued navigation property, this will be null
   * @param entityETag
   * @param response - always should be 204
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void deleteReference(DataRequest request, URI deleteId, String entityETag, NoContentResponse response)
      throws ODataLibraryException, ODataApplicationException;


  /**
   * During a batch operation, this method starts the transaction (if any) before any operation is handled
   * by the service. No nested transactions.
   * @return must return a unique transaction id that references a atomic operation.
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  String startTransaction() throws ODataLibraryException, ODataApplicationException;;

  /**
   * When a batch operation is complete and all the intermediate service requests are successful, then
   * commit is called with transaction id returned in the startTransaction method.
   * @param txnId
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void commit(String txnId) throws ODataLibraryException, ODataApplicationException;;
  /**
   * When a batch operation is in-complete due to an error in the middle of changeset, then rollback is
   * called with transaction id, that returned from startTransaction method.
   * @param txnId
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void rollback(String txnId) throws ODataLibraryException, ODataApplicationException;;

  /**
   * This is not complete, more URL parsing changes required. Cross join between two entities.
   * @param dataRequest
   * @param entitySetNames
   * @param response
   * @throws ODataLibraryException
   * @throws ODataApplicationException
   */
  void crossJoin(DataRequest dataRequest, List<String> entitySetNames, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException;
  
  /**
   * Snapshot isolation guarantees that all data returned for a request, including multiple requests within 
   * a batch or results retrieved across multiple pages, will be consistent as of a single point in time. 
   * Only data modifications made within the request (for example, by a data modification request 
   * within the same batch) are visible. The effect is as if the request generates a "snapshot" of 
   * the committed data as it existed at the start of the request. for more info see OData V4, Part1 8.2.6
   * 
   * The contract for this interface is if it returns true, whenever the service deals with $skiptoken based
   * results, they MUST be from same snapshot of the original request. false, the framework will automatically
   * returns a 412.
   * @return
   */
  boolean supportsDataIsolation();
  
  
  /**
   * Handle errors generated by the framework as well as the service specific errors. This can be used
   * one place handle error logging, error modification etc.   
   * @param error 
   * @param response
   */
  void processError(ODataServerError error, ErrorResponse response);
}
