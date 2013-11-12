package org.apache.olingo.commons.core.edm.primitivetype;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;

//TODO: Should we delete this typekind and use a facade?
public enum EdmPrimitiveTypeKind {
  Binary, Boolean, Byte, Date, DateTimeOffset, Decimal, Double, Duration, Guid,
  Int16, Int32, Int64, SByte, Single, String, TimeOfDay;

  /**
   * Returns the {@link FullQualifiedName} for this type kind.
   * @return {@link FullQualifiedName}
   */
  public FullQualifiedName getFullQualifiedName() {
    return new FullQualifiedName(EdmPrimitiveType.EDM_NAMESPACE, toString());
  }

  /**
   * Returns an instance for this {@link EdmPrimitiveTypeKind} in the form of {@link EdmPrimitiveType}.
   * @return {@link EdmPrimitiveType} instance
   */
  public EdmPrimitiveType getEdmPrimitiveTypeInstance() {
    switch (this) {
    case Binary:
      return EdmBinary.getInstance();
    case Boolean:
      return EdmBoolean.getInstance();
    case Byte:
      return EdmByte.getInstance();
    case Date:
      return EdmDate.getInstance();
    case DateTimeOffset:
      return EdmDateTimeOffset.getInstance();
    case Decimal:
      return EdmDecimal.getInstance();
    case Double:
      return EdmDouble.getInstance();
    case Duration:
      return EdmDuration.getInstance();
    case Guid:
      return EdmGuid.getInstance();
    case Int16:
      return EdmInt16.getInstance();
    case Int32:
      return EdmInt32.getInstance();
    case Int64:
      return EdmInt64.getInstance();
    case SByte:
      return EdmSByte.getInstance();
    case Single:
      return EdmSingle.getInstance();
    case String:
      return EdmString.getInstance();
    case TimeOfDay:
      return EdmTimeOfDay.getInstance();
    default:
      throw new RuntimeException("Wrong type:" + this);
    }
  }
}
