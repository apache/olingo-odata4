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
import org.apache.olingo.odata4.client.api.edm.v3.ReferentialConstraint;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmItem;

public class ReferentialConstraintImpl extends AbstractEdmItem implements ReferentialConstraint {

  private static final long serialVersionUID = 9067893732765127269L;

  @JsonProperty(value = "Principal", required = true)
  private ReferentialConstraintRole principal;

  @JsonProperty(value = "Dependent", required = true)
  private ReferentialConstraintRole dependent;

  public ReferentialConstraintRole getPrincipal() {
    return principal;
  }

  public void setPrincipal(final ReferentialConstraintRole principal) {
    this.principal = principal;
  }

  public ReferentialConstraintRole getDependent() {
    return dependent;
  }

  public void setDependent(final ReferentialConstraintRole dependent) {
    this.dependent = dependent;
  }
}
