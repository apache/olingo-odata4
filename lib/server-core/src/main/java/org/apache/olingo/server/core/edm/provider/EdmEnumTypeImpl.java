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
package org.apache.olingo.server.core.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmEnumType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.edm.provider.EnumMember;
import org.apache.olingo.server.api.edm.provider.EnumType;

public class EdmEnumTypeImpl extends AbstractEdmEnumType implements EdmEnumType {

  private final EdmPrimitiveType underlyingType;

  private final EnumType enumType;

  private List<EdmMember> members;

  public EdmEnumTypeImpl(final Edm edm, final FullQualifiedName enumName, final EnumType enumType) {
    super(edm, enumName, enumType.isFlags());

    if (enumType.getUnderlyingType() == null) {
      underlyingType = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32);
    } else {
      underlyingType = EdmPrimitiveTypeFactory.getInstance(
              EdmPrimitiveTypeKind.valueOf(enumType.getUnderlyingType().getName()));
      // TODO: Should we validate that the underlying type is of byte, sbyte, in16, int32 or int64?
    }

    this.enumType = enumType;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    return underlyingType;
  }

  @Override
  protected List<? extends EdmMember> getMembers() {
    if (members == null) {
      members = new ArrayList<EdmMember>(enumType.getMembers().size());
      for (EnumMember member : enumType.getMembers()) {
        members.add(new EdmMemberImpl(edm, getFullQualifiedName(), member.getName(), member.getValue()));
      }
    }
    return members;
  }

}
