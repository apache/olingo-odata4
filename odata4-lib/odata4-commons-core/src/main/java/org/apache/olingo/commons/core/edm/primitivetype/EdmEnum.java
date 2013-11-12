package org.apache.olingo.commons.core.edm.primitivetype;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

/**
 * Implementation of the EDM enum type.
 * @see EdmEnumType
 */
final class EdmEnum extends AbstractPrimitiveType implements EdmEnumType {

  private final String namespace;
  private final String name;
  private final String fullName;
  private final EdmPrimitiveType underlyingType;
  private final List<EdmMember> members;
  private final Boolean isFlags;

  public EdmEnum(final String namespace, final String name,
      final EdmPrimitiveType underlyingType, final List<EdmMember> members, final Boolean isFlags) {
    this.namespace = namespace;
    this.name = name;
    fullName = namespace + '.' + name;
    uriPrefix = fullName + '\'';
    uriSuffix = "'";
    this.underlyingType = underlyingType;
    this.members = members;
    this.isFlags = isFlags;
  }

  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public EdmTypeKind getKind() {
    return EdmTypeKind.ENUM;
  }

  @Override
  public Class<?> getDefaultType() {
    return underlyingType.getDefaultType();
  }

  @Override
  public EdmMember getMember(final String name) {
    for (EdmMember member : members) {
      if (member.getName().equals(name)) {
        return member;
      }
    }
    return null;
  }

  @Override
  public List<String> getMemberNames() {
    List<String> names = new ArrayList<String>();
    for (final EdmMember member : members) {
      names.add(member.getName());
    }
    return names;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    return underlyingType;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
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

  protected Long parseEnumValue(final String value) throws EdmPrimitiveTypeException {
    Long result = null;
    for (final String memberValue : value.split(",", isFlags ? -1 : 1)) {
      Long memberValueLong = null;
      for (final EdmMember member : members) {
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
  protected String internalValueToString(final Object value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
      return constructEnumValue(((Number) value).longValue());
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }

  protected String constructEnumValue(final long value) throws EdmPrimitiveTypeException {
    long remaining = value;
    StringBuilder result = new StringBuilder();

    for (final EdmMember member : members) {
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
}
