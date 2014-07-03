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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;

public abstract class AbstractEdmEnumType extends EdmTypeImpl implements EdmEnumType {

  private final boolean isFlags;

  private final String uriPrefix;

  private final String uriSuffix;

  private List<String> memberNames;

  private Map<String, EdmMember> members;

  public AbstractEdmEnumType(final Edm edm, final FullQualifiedName fqn, final boolean isFlags) {
    super(edm, fqn, EdmTypeKind.ENUM);

    this.isFlags = isFlags;
    uriPrefix = fqn.getFullQualifiedNameAsString() + '\'';
    uriSuffix = "'";
  }

  protected abstract Collection<? extends EdmMember> getMembers();

  @Override
  public EdmMember getMember(final String name) {
    if (members == null) {
      members = new LinkedHashMap<String, EdmMember>();
      for (final EdmMember member : getMembers()) {
        members.put(member.getName(), member);
      }
    }
    return members.get(name);
  }

  @Override
  public List<String> getMemberNames() {
    if (memberNames == null) {
      memberNames = new ArrayList<String>();
      for (final EdmMember member : getMembers()) {
        memberNames.add(member.getName());
      }
    }
    return memberNames;
  }

  @Override
  public abstract EdmPrimitiveType getUnderlyingType();

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return equals(primitiveType);
  }

  @Override
  public Class<?> getDefaultType() {
    return getUnderlyingType().getDefaultType();
  }

  @Override
  public boolean validate(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) {

    try {
      valueOfString(value, isNullable, maxLength, precision, scale, isUnicode, getDefaultType());
      return true;
    } catch (final EdmPrimitiveTypeException e) {
      return false;
    }
  }

  private Long parseEnumValue(final String value) throws EdmPrimitiveTypeException {
    Long result = null;
    for (final String memberValue : value.split(",", isFlags ? -1 : 1)) {
      Long memberValueLong = null;
      for (final EdmMember member : getMembers()) {
        if (member.getName().equals(memberValue) || member.getValue().equals(memberValue)) {
          memberValueLong = Long.decode(member.getValue());
        }
      }
      if (memberValueLong == null) {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
      }
      result = result == null ? memberValueLong : result | memberValueLong;
    }
    return result;
  }

  @Override
  public <T> T valueOfString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode, final Class<T> returnType)
      throws EdmPrimitiveTypeException {

    if (value == null) {
      if (isNullable != null && !isNullable) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_NULL_NOT_ALLOWED");
      }
      return null;
    }

    try {
      return EdmInt64.convertNumber(parseEnumValue(value), returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType), e");
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
    }
  }

  protected String constructEnumValue(final long value) throws EdmPrimitiveTypeException {
    long remaining = value;
    StringBuilder result = new StringBuilder();

    for (final EdmMember member : getMembers()) {
      final long memberValue = Long.parseLong(member.getValue());
      if ((memberValue & remaining) == memberValue) {
        if (result.length() > 0) {
          result.append(',');
        }
        result.append(member.getName());
        remaining ^= memberValue;
      }
    }

    if (remaining != 0) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
    }
    return result.toString();
  }

  @Override
  public String valueToString(final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (value == null) {
      if (isNullable != null && !isNullable) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.VALUE_NULL_NOT_ALLOWED");
      }
      return null;
    }
    if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
      return constructEnumValue(((Number) value).longValue());
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }

  @Override
  public String toUriLiteral(final String literal) {
    return literal == null ? null
        : uriPrefix.isEmpty() && uriSuffix.isEmpty() ? literal : uriPrefix + literal + uriSuffix;
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    if (literal == null) {
      return null;
    } else if (uriPrefix.isEmpty() && uriSuffix.isEmpty()) {
      return literal;
    } else if (literal.length() >= uriPrefix.length() + uriSuffix.length()
        && literal.startsWith(uriPrefix) && literal.endsWith(uriSuffix)) {
      return literal.substring(uriPrefix.length(), literal.length() - uriSuffix.length());
    } else {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(literal)");
    }
  }

  @Override
  public boolean isFlags() {
    return isFlags;
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.EnumType;
  }

  @Override
  public String getAnnotationsTargetPath() {
    return null;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return getFullQualifiedName();
  }

}
