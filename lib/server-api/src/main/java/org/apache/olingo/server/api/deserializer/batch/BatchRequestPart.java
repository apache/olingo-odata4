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
package org.apache.olingo.server.api.deserializer.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.server.api.ODataRequest;

/**
 * Represents a distinct MIME part of a Batch Request body - either a Change Set or a Query Operation
 */
public class BatchRequestPart {
  private List<ODataRequest> requests = new ArrayList<ODataRequest>();
  private boolean isChangeSet;

  /**
   * Creates a new instance of BachRequestPart.
   * @param isChangeSet True, if this instance represents a change set
   * @param requests A list of {@link ODataRequest}
   */
  public BatchRequestPart(final boolean isChangeSet, final List<ODataRequest> requests) {
    this.isChangeSet = isChangeSet;
    this.requests = requests;
  }

  /**
   * Creates a new instance of BachRequestPart.
   * @param isChangeSet True, if this instance represents a change set
   * @param request A single {@link ODataRequest}
   */
  public BatchRequestPart(final boolean isChangeSet, final ODataRequest request) {
    this.isChangeSet = isChangeSet;
    requests = new ArrayList<ODataRequest>();
    requests.add(request);
  }

  /**
   * Gets the info if a BatchPart is a ChangeSet.
   * @return true or false
   */
  public boolean isChangeSet() {
    return isChangeSet;
  }

  /**
   * Gets all requests of this part. If a BatchPart is a Query Operation, the list contains one request.
   * @return a list of {@link ODataRequest}
   */
  public List<ODataRequest> getRequests() {
    return Collections.unmodifiableList(requests);
  }
}