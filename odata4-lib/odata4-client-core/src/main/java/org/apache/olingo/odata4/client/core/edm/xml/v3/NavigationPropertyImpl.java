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
package org.apache.olingo.odata4.client.core.edm.xml.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.odata4.client.api.edm.xml.v3.NavigationProperty;
import org.apache.olingo.odata4.client.core.edm.xml.AbstractNavigationProperty;

public class NavigationPropertyImpl extends AbstractNavigationProperty implements NavigationProperty {

  private static final long serialVersionUID = -2889417442815563307L;

  @JsonProperty(value = "Relationship", required = true)
  private String relationship;

  @JsonProperty(value = "ToRole", required = true)
  private String toRole;

  @JsonProperty(value = "FromRole", required = true)
  private String fromRole;

  @Override
  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(final String relationship) {
    this.relationship = relationship;
  }

  @Override
  public String getToRole() {
    return toRole;
  }

  public void setToRole(final String toRole) {
    this.toRole = toRole;
  }

  @Override
  public String getFromRole() {
    return fromRole;
  }

  public void setFromRole(final String fromRole) {
    this.fromRole = fromRole;
  }

}
