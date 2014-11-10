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
package org.apache.olingo.server.api.batch;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.server.api.ODataResponse;

public class ODataResponsePart {
  private List<ODataResponse> responses;
  private boolean isChangeSet;
  
  /**
   * Creates a new ODataResponsePart.
   * 
   * An ODataResponsePart represents a collections of ODataResponses.
   * A list of ODataResponseParts can be combined by the BatchSerializer to a single
   * OData batch response.
   *  
   * @param responses     A list of {@link ODataResponse}
   * @param isChangeSet   True this ODataResponsePart represents a change set, otherwise false
   */
  public ODataResponsePart(List<ODataResponse> responses, boolean isChangeSet) {
    this.responses = responses;
    this.isChangeSet = isChangeSet;
  }
  
  /**
   * Creates a new ODataResponsePart.
   * 
   * An ODataResponsePart represents a collections of ODataResponses.
   * A list of ODataResponseParts can be combined by the BatchSerializer to a single
   * OData batch response.
   *  
   * @param responses     A single {@link ODataResponse}
   * @param isChangeSet   True this ODataResponsePart represents a change set, otherwise false
   */
  public ODataResponsePart(ODataResponse response, boolean isChangeSet) {
    this.responses = Arrays.asList(new ODataResponse[] { response });
    this.isChangeSet = isChangeSet;
  }
  
  /**
   * Returns true if the current instance represents a change set.
   * 
   * @return true or false
   */
  public List<ODataResponse> getResponses() {
    return responses;
  }
  
  /**
   * Returns a collection of ODataResponses.
   * Each collections contains at least one {@link ODataResponse}.
   * 
   * If this instance represents a change set, there are may many ODataResponses
   *  
   * @return a list of {@link ODataResponse}
   */
  public boolean isChangeSet() {
    return isChangeSet;
  }
}
