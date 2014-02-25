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
package org.apache.olingo.odata4.client.core.edm.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.odata4.client.api.edm.v3.AssociationSetEnd;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmItem;

public class AssociationSetEndImpl extends AbstractEdmItem implements AssociationSetEnd {

  private static final long serialVersionUID = -6238344152962217446L;

  @JsonProperty("Role")
  private String role;

  @JsonProperty(value = "EntitySet", required = true)
  private String entitySet;

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(final String role) {
    this.role = role;
  }

  @Override
  public String getEntitySet() {
    return entitySet;
  }

  @Override
  public void setEntitySet(final String entitySet) {
    this.entitySet = entitySet;
  }
}
