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

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.server.api.ODataResponse;

/**
 * An ODataResponsePart represents a collection of ODataResponses.
 * A list of ODataResponseParts can be combined by the BatchSerializer to a single
 * OData batch response.
 */
public class ODataResponsePart {
  private final List<ODataResponse> responses;
  private final boolean isChangeSet;

  /**
   * Creates a new ODataResponsePart.
   *
   * An ODataResponsePart represents a collection of ODataResponses.
   * A list of ODataResponseParts can be combined by the BatchSerializer to a single
   * OData batch response.
   *
   * @param responses A list of {@link ODataResponse}
   * @param isChangeSet whether this ODataResponsePart represents a change set
   */
  public ODataResponsePart(final List<ODataResponse> responses, final boolean isChangeSet) {
    this.responses = responses;
    this.isChangeSet = isChangeSet;
  }

  /**
   * Creates a new ODataResponsePart.
   *
   * An ODataResponsePart represents a collection of ODataResponses.
   * A list of ODataResponseParts can be combined by the BatchSerializer to a single
   * OData batch response.
   *
   * @param response A single {@link ODataResponse}
   * @param isChangeSet whether this ODataResponsePart represents a change set
   */
  public ODataResponsePart(final ODataResponse response, final boolean isChangeSet) {
    responses = Arrays.asList(response);
    this.isChangeSet = isChangeSet;
  }

  /**
   * Returns a collection of ODataResponses.
   * Each collection contains at least one {@link ODataResponse}.
   * If this instance represents a change set, there may be many ODataResponses.
   * @return a list of {@link ODataResponse}
   */
  public List<ODataResponse> getResponses() {
    return responses;
  }

  /**
   * Returns true if the current instance represents a change set.
   * @return true or false
   */
  public boolean isChangeSet() {
    return isChangeSet;
  }
}
