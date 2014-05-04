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
package org.apache.olingo.client.core.edm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.olingo.client.api.edm.xml.EnumType;
import org.apache.olingo.client.api.edm.xml.Member;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmEnumType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class EdmEnumTypeImpl extends AbstractEdmEnumType implements EdmEnumType {

  private static final EdmPrimitiveTypeKind[] VALID_UNDERLYING_TYPES = new EdmPrimitiveTypeKind[] {
    EdmPrimitiveTypeKind.Byte,
    EdmPrimitiveTypeKind.SByte,
    EdmPrimitiveTypeKind.Int16,
    EdmPrimitiveTypeKind.Int32,
    EdmPrimitiveTypeKind.Int64
  };

  private final EdmPrimitiveType underlyingType;

  private final List<String> memberNames;

  private final Map<String, EdmMember> members;

  public EdmEnumTypeImpl(final ODataServiceVersion version, final Edm edm, final FullQualifiedName fqn,
          final EnumType xmlEnumType) {

    super(edm, fqn, xmlEnumType.isFlags());

    if (xmlEnumType.getUnderlyingType() == null) {
      this.underlyingType = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32);
    } else {
      this.underlyingType = EdmPrimitiveTypeFactory.getInstance(
              EdmPrimitiveTypeKind.valueOfFQN(version, xmlEnumType.getUnderlyingType()));
      if (!ArrayUtils.contains(VALID_UNDERLYING_TYPES, this.underlyingType.getKind())) {
        throw new EdmException("Not allowed as underlying type: " + this.underlyingType.getKind());
      }
    }

    final List<? extends Member> xmlMembers = xmlEnumType.getMembers();
    final List<String> _memberNames = new ArrayList<String>();
    final Map<String, EdmMember> _members = new LinkedHashMap<String, EdmMember>(xmlMembers.size());
    for (Member xmlMember : xmlMembers) {
      _memberNames.add(xmlMember.getName());
      _members.put(xmlMember.getName(), new EdmMemberImpl(edm, fqn, xmlMember));
    }
    this.memberNames = Collections.unmodifiableList(_memberNames);
    this.members = Collections.unmodifiableMap(_members);
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    return underlyingType;
  }

  @Override
  public List<String> getMemberNames() {
    return memberNames;
  }

  @Override
  protected Collection<? extends EdmMember> getMembers() {
    return members.values();
  }

}
