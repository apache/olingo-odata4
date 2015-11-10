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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class EdmEnumTypeImpl extends EdmTypeImpl implements EdmEnumType {

  private final EdmPrimitiveType underlyingType;
  private final CsdlEnumType enumType;
  private final FullQualifiedName enumName;
  private List<String> memberNames;
  private Map<String, EdmMember> membersMap;

  public EdmEnumTypeImpl(final Edm edm, final FullQualifiedName enumName, final CsdlEnumType enumType) {
    super(edm, enumName, EdmTypeKind.ENUM, enumType);

    if (enumType.getUnderlyingType() == null) {
      underlyingType = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32);
    } else {
      final EdmPrimitiveTypeKind underlyingTypeKind = EdmPrimitiveTypeKind.valueOfFQN(enumType.getUnderlyingType());
      if (underlyingTypeKind == EdmPrimitiveTypeKind.Byte
          || underlyingTypeKind == EdmPrimitiveTypeKind.SByte
          || underlyingTypeKind == EdmPrimitiveTypeKind.Int16
          || underlyingTypeKind == EdmPrimitiveTypeKind.Int32
          || underlyingTypeKind == EdmPrimitiveTypeKind.Int64) {
        underlyingType = EdmPrimitiveTypeFactory.getInstance(underlyingTypeKind);
      } else {
        throw new EdmException("Not allowed as underlying type: " + underlyingTypeKind);
      }
    }

    this.enumType = enumType;
    this.enumName = enumName;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    return underlyingType;
  }

  @Override
  public EdmMember getMember(final String name) {
    if (membersMap == null) {
      createEdmMembers();
    }
    return membersMap.get(name);
  }

  @Override
  public List<String> getMemberNames() {
    if (memberNames == null) {
      createEdmMembers();
    }
    return Collections.unmodifiableList(memberNames);
  }

  /**
   * Creates the map from member names to member objects,
   * preserving the order for the case of implicit value assignments.
   */
  private void createEdmMembers() {
    final Map<String, EdmMember> membersMapLocal = new LinkedHashMap<String, EdmMember>();
    final List<String> memberNamesLocal = new ArrayList<String>();
    if (enumType.getMembers() != null) {
      for (final CsdlEnumMember member : enumType.getMembers()) {
        membersMapLocal.put(member.getName(), new EdmMemberImpl(edm, member));
        memberNamesLocal.add(member.getName());
      }

      membersMap = membersMapLocal;
      memberNames = memberNamesLocal;
    }
  }

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
    for (final String memberValue : value.split(",", isFlags() ? -1 : 1)) {
      Long memberValueLong = null;
      long count = 0;
      for (final EdmMember member : getMembers()) {
        count++;
        if (memberValue.equals(member.getName()) || memberValue.equals(member.getValue())) {
          memberValueLong = member.getValue() == null ? count - 1 : Long.decode(member.getValue());
        }
      }
      if (memberValueLong == null) {
        throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
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
        throw new EdmPrimitiveTypeException("The literal 'null' is not allowed.");
      }
      return null;
    }

    try {
      return EdmInt64.convertNumber(parseEnumValue(value), returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException("The literal '" + value
          + "' cannot be converted to value type " + returnType + ".", e);
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException("The value type " + returnType + " is not supported.", e);
    }
  }

  private String constructEnumValue(final long value) throws EdmPrimitiveTypeException {
    long remaining = value;
    StringBuilder result = new StringBuilder();

    final boolean flags = isFlags();
    long memberValue = -1;
    for (final EdmMember member : getMembers()) {
      memberValue = member.getValue() == null ? memberValue + 1 : Long.parseLong(member.getValue());
      if (flags) {
        if ((memberValue & remaining) == memberValue) {
          if (result.length() > 0) {
            result.append(',');
          }
          result.append(member.getName());
          remaining ^= memberValue;
        }
      } else {
        if (value == memberValue) {
          return member.getName();
        }
      }
    }

    if (remaining != 0) {
      throw new EdmPrimitiveTypeException("The value '" + value + "' is not valid.");
    }
    return result.toString();
  }

  private Collection<EdmMember> getMembers() {
    if (membersMap == null) {
      createEdmMembers();
    }
    return membersMap.values();
  }

  @Override
  public String valueToString(final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (value == null) {
      if (isNullable != null && !isNullable) {
        throw new EdmPrimitiveTypeException("The value NULL is not allowed.");
      }
      return null;
    }
    if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
      return constructEnumValue(((Number) value).longValue());
    } else {
      throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
    }
  }

  @Override
  public String toUriLiteral(final String literal) {
    return literal == null ? null : enumName.getFullQualifiedNameAsString() + "'" + literal + "'";
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    if (literal == null) {
      return null;
    } else {
      String uriPrefix = enumName.getFullQualifiedNameAsString() + "'";
      String uriSuffix = "'";
      if (literal.length() >= uriPrefix.length() + uriSuffix.length()
          && literal.startsWith(uriPrefix) && literal.endsWith(uriSuffix)) {
        // This is the positive case where the literal is prefixed with the full qualified name of the enum type
        return literal.substring(uriPrefix.length(), literal.length() - uriSuffix.length());
      } else {
        // This case will be called if the prefix might be an alias
        if (literal.endsWith(uriSuffix)) {
          int indexSingleQuote = literal.indexOf('\'');
          String fqn = literal.substring(0, indexSingleQuote);
          FullQualifiedName typeFqn = null;
          try {
            typeFqn = new FullQualifiedName(fqn);
          } catch (IllegalArgumentException e) {
            throw new EdmPrimitiveTypeException("The literal '" + literal + "' has illegal content.", e);
          }
          /*
           * Get itself. This will also resolve a possible alias. If we had an easier way to query the edm for an alias
           * we could use this here. But since there is no such method we try to get the enum type based on a possible
           * alias qualified name. This way the edm will resolve the alias for us. Also in a positive case the type is
           * already cached so the EdmProvider should not be called.
           */
          EdmEnumType prospect = edm.getEnumType(typeFqn);
          if (prospect != null && enumName.equals(prospect.getFullQualifiedName())
              && literal.length() >= fqn.length() + 2) {
            return literal.substring(fqn.length() + 1, literal.length() - 1);
          }
        }
      }
    }
    throw new EdmPrimitiveTypeException("The literal '" + literal + "' has illegal content.");
  }

  @Override
  public boolean isFlags() {
    return enumType.isFlags();
  }

  @Override
  public int hashCode() {
    return getFullQualifiedName().getFullQualifiedNameAsString().hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return obj != null
        && (obj == this
        || obj instanceof EdmEnumType
            && getFullQualifiedName().equals(((EdmEnumType) obj).getFullQualifiedName()));
  }
}
