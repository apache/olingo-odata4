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
package org.apache.olingo.commons.api.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EnumType extends AbstractEdmItem implements Named, Annotatable {

  private static final long serialVersionUID = -718032622783883403L;

  private String name;

  private boolean isFlags;

  private FullQualifiedName underlyingType;

  private List<EnumMember> members = new ArrayList<EnumMember>();
  
  private final List<Annotation> annotations = new ArrayList<Annotation>();

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

  //TODO: Underlying type has a default
  public String getUnderlyingType() {
    if(underlyingType != null){
      return underlyingType.getFullQualifiedNameAsString();
    }
    return null;
  }

  public EnumType setUnderlyingType(final String underlyingType) {
    this.underlyingType = new FullQualifiedName(underlyingType);
    return this;
  }
  
  public EnumType setUnderlyingType(final FullQualifiedName underlyingType) {
    this.underlyingType = underlyingType;
    return this;
  }


  public List<EnumMember> getMembers() {
    return members;
  }

  public EnumMember getMember(final String name) {
    EnumMember result = null;
    if (getMembers() != null) {
      for (EnumMember member : getMembers()) {
        if (name.equals(member.getName())) {
          result = member;
        }
      }
    }
    return result;
  }

  public EnumMember getMember(final Integer value) {
    EnumMember result = null;
    if (getMembers() != null) {
      for (EnumMember member : getMembers()) {
        if (String.valueOf(value).equals(member.getValue())) {
          result = member;
        }
      }
    }
    return result;
  }

  public EnumType setMembers(final List<EnumMember> members) {
    this.members = members;
    return this;
  }
  
  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }
}
