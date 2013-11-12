package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;

public abstract class EdmTypeImpl extends EdmNamedImpl implements EdmType {

  private final EdmTypeKind kind;
  private final String namespace;

  public EdmTypeImpl(final FullQualifiedName name, final EdmTypeKind kind) {
    super(name.getName());
    namespace = name.getNamespace();
    this.kind = kind;
  }

  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public EdmTypeKind getKind() {
    return kind;
  }

}
