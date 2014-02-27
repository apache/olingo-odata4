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
import org.apache.olingo.odata4.client.api.edm.xml.OnDelete;
import org.apache.olingo.odata4.client.api.edm.xml.v3.AssociationEnd;
import org.apache.olingo.odata4.client.core.edm.xml.AbstractEdmItem;

public class AssociationEndImpl extends AbstractEdmItem implements AssociationEnd {

  private static final long serialVersionUID = 3305394053564979376L;

  @JsonProperty(value = "Type", required = true)
  private String type;

  @JsonProperty(value = "Role")
  private String role;

  @JsonProperty(value = "Multiplicity")
  private String multiplicity;

  @JsonProperty(value = "OnDelete")
  private OnDelete onDelete;

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(final String role) {
    this.role = role;
  }

  @Override
  public String getMultiplicity() {
    return multiplicity;
  }

  @Override
  public void setMultiplicity(final String multiplicity) {
    this.multiplicity = multiplicity;
  }

  @Override
  public OnDelete getOnDelete() {
    return onDelete;
  }

  @Override
  public void setOnDelete(final OnDelete onDelete) {
    this.onDelete = onDelete;
  }
}
