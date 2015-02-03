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

import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataTranslatedException;

@SuppressWarnings("unused")
public class ServiceResponseVisior {

  void visit(CountResponse response) throws ODataTranslatedException,
    ODataApplicationException {
    response.writeServerError(true);
  }

  void visit(EntityResponse response) throws ODataTranslatedException,
    ODataApplicationException {
    response.writeServerError(true);
  }

  void visit(MetadataResponse response) throws ODataTranslatedException,
    ODataApplicationException {
    response.writeServerError(true);
  }

  void visit(NoContentResponse response) throws ODataTranslatedException,
    ODataApplicationException {
    response.writeServerError(true);
  }

  void visit(PrimitiveValueResponse response) throws ODataTranslatedException,
      ODataApplicationException {
    response.writeServerError(true);
  }

  void visit(PropertyResponse response) throws ODataTranslatedException,
    ODataApplicationException {
    response.writeServerError(true);
  }

  void visit(ServiceDocumentResponse response) throws ODataTranslatedException,
      ODataApplicationException {
    response.writeServerError(true);
  }

  public void visit(StreamResponse response) throws ODataTranslatedException,
      ODataApplicationException {
    response.writeServerError(true);
  }

  public void visit(EntitySetResponse response) throws ODataTranslatedException,
      ODataApplicationException {
    response.writeServerError(true);
  }
}
