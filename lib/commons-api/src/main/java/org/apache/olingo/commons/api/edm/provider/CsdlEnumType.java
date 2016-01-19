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

/**
 * The type Csdl enum type.
 */
public class CsdlEnumType extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  private String name;

  private boolean isFlags;

  private FullQualifiedName underlyingType;

  private List<CsdlEnumMember> members = new ArrayList<CsdlEnumMember>();

  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   * @return the name
   */
  public CsdlEnumType setName(final String name) {
    this.name = name;
    return this;
  }

  /**
   * Is flags.
   *
   * @return the boolean
   */
  public boolean isFlags() {
    return isFlags;
  }

  /**
   * Sets flags.
   *
   * @param isFlags the is flags
   * @return the flags
   */
  public CsdlEnumType setFlags(final boolean isFlags) {
    this.isFlags = isFlags;
    return this;
  }

  /**
   * Gets underlying type.
   *
   * @return the underlying type
   */
  public String getUnderlyingType() {
    if (underlyingType != null) {
      return underlyingType.getFullQualifiedNameAsString();
    }
    return null;
  }

  /**
   * Sets underlying type.
   *
   * @param underlyingType the underlying type
   * @return the underlying type
   */
  public CsdlEnumType setUnderlyingType(final String underlyingType) {
    this.underlyingType = new FullQualifiedName(underlyingType);
    return this;
  }

  /**
   * Sets underlying type.
   *
   * @param underlyingType the underlying type
   * @return the underlying type
   */
  public CsdlEnumType setUnderlyingType(final FullQualifiedName underlyingType) {
    this.underlyingType = underlyingType;
    return this;
  }

  /**
   * Gets members.
   *
   * @return the members
   */
  public List<CsdlEnumMember> getMembers() {
    return members;
  }

  /**
   * Gets member.
   *
   * @param name the name
   * @return the member
   */
  public CsdlEnumMember getMember(final String name) {
    CsdlEnumMember result = null;
    if (getMembers() != null) {
      for (CsdlEnumMember member : getMembers()) {
        if (name.equals(member.getName())) {
          result = member;
        }
      }
    }
    return result;
  }

  /**
   * Gets member.
   *
   * @param value the value
   * @return the member
   */
  public CsdlEnumMember getMember(final Integer value) {
    CsdlEnumMember result = null;
    if (getMembers() != null) {
      for (CsdlEnumMember member : getMembers()) {
        if (String.valueOf(value).equals(member.getValue())) {
          result = member;
        }
      }
    }
    return result;
  }

  /**
   * Sets members.
   *
   * @param members the members
   * @return the members
   */
  public CsdlEnumType setMembers(final List<CsdlEnumMember> members) {
    this.members = members;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
  
  /**
   * Sets a list of annotations
   * @param annotations list of annotations
   * @return this instance
   */
  public CsdlEnumType setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
