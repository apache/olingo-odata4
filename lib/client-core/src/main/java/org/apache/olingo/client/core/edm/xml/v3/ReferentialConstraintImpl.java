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
package org.apache.olingo.client.core.edm.xml.v3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.client.api.edm.xml.v3.ReferentialConstraint;
import org.apache.olingo.client.api.edm.xml.v3.ReferentialConstraintRole;
import org.apache.olingo.client.core.edm.xml.AbstractEdmItem;

public class ReferentialConstraintImpl extends AbstractEdmItem implements ReferentialConstraint {

  private static final long serialVersionUID = 9067893732765127269L;

  @JsonProperty(value = "Principal", required = true)
  private ReferentialConstraintRoleImpl principal;

  @JsonProperty(value = "Dependent", required = true)
  private ReferentialConstraintRoleImpl dependent;

  @Override
  public ReferentialConstraintRoleImpl getPrincipal() {
    return principal;
  }

  @JsonIgnore
  public void setPrincipal(final ReferentialConstraintRole principal) {
    this.principal = (ReferentialConstraintRoleImpl) principal;
  }

  @Override
  public ReferentialConstraintRoleImpl getDependent() {
    return dependent;
  }

  @JsonIgnore
  public void setDependent(final ReferentialConstraintRole dependent) {
    this.dependent = (ReferentialConstraintRoleImpl) dependent;
  }
}
