/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.api.edm.provider;

import java.util.List;

public class EnumType {

  private String name;
  private boolean isFlags;
  // Underlying Type can only be primitve...
  private FullQualifiedName underlyingType;
  private List<EnumMember> members;

  // Facets
  // Annotations?

  public String getName() {
    return name;
  }

  public EnumType setName(final String name) {
    this.name = name;
    return this;
  }

  public boolean isFlags() {
    return isFlags;
  }

  public EnumType setFlags(final boolean isFlags) {
    this.isFlags = isFlags;
    return this;
  }

  public FullQualifiedName getUnderlyingType() {
    return underlyingType;
  }

  public EnumType setUnderlyingType(final FullQualifiedName underlyingType) {
    this.underlyingType = underlyingType;
    return this;
  }

  public List<EnumMember> getMembers() {
    return members;
  }

  public EnumType setMembers(final List<EnumMember> members) {
    this.members = members;
    return this;
  }
}
