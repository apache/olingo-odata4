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
package org.apache.olingo.commons.api.data;

import java.net.URI;

/**
 * A deleted entity contains the reason for deletion and the id.
 */
public class DeletedEntity extends Entity{
  
  /**
   * Reason of the removal from the list
   */
  public enum Reason {
    /** The entity was deleted. */
    deleted,
    /** The data of the entity has changed and is not any longer part of the response. */
    changed
  }

  private URI id;
  private Reason reason;

  /**
   * Get id.
   * @return id
   */
  public URI getId() {
    return id;
  }

  /**
   * Set id.
   * @param id id
   */
  public void setId(final URI id) {
    this.id = id;
  }

  /**
   * Get reason for deletion.
   * @return reason for deletion
   */
  public Reason getReason() {
    return reason;
  }

  /**
   * Set reason for deletion.
   * @param reason for deletion
   */
  public void setReason(final Reason reason) {
    this.reason = reason;
  }
}
