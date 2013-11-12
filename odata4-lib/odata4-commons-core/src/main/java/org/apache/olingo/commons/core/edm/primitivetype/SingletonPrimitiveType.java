package org.apache.olingo.commons.core.edm.primitivetype;

import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

/**
 * Abstract singleton implementation of the EDM primitive-type interface.
 */
abstract class SingletonPrimitiveType extends AbstractPrimitiveType {

  @Override
  public boolean equals(final Object obj) {
    return this == obj || obj != null && getClass() == obj.getClass();
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String getNamespace() {
    return EDM_NAMESPACE;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName().substring(3);
  }

  @Override
  public EdmTypeKind getKind() {
    return EdmTypeKind.PRIMITIVE;
  }
}
