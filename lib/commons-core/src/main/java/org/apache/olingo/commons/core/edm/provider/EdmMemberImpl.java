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
package org.apache.olingo.commons.core.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.EnumMember;

public class EdmMemberImpl extends EdmNamedImpl implements EdmMember {

  private final EdmAnnotationHelper helper;
  private final FullQualifiedName enumFQN;
  private final EnumMember member;

  public EdmMemberImpl(final Edm edm, final FullQualifiedName enumFQN, final EnumMember member) {
    super(edm, member.getName());
    this.enumFQN = enumFQN;
    this.member = member;
    this.helper = new EdmAnnotationHelperImpl(edm, member);
  }
  
  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.Member;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return enumFQN;
  }

  @Override
  public String getAnnotationsTargetPath() {
    return getName();
  }

  @Override
  public String getValue() {
    return member.getValue();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper.getAnnotations();
  }

}
