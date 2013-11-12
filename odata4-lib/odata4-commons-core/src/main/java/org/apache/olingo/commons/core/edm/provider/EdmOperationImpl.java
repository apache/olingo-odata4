package org.apache.olingo.commons.core.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Operation;

//TODO: Test
public class EdmOperationImpl extends EdmTypeImpl implements EdmOperation {

  public EdmOperationImpl(final FullQualifiedName name, final Operation operation, final EdmTypeKind kind) {
    super(name, kind);
  }

  @Override
  public EdmParameter getParameter(final String name) {
    return null;
  }

  @Override
  public List<String> getParameterNames() {
    return null;
  }

  @Override
  public EdmEntitySet getReturnedEntitySet(final EdmEntitySet bindingParameterEntitySet, final String path) {
    return null;
  }

  @Override
  public EdmReturnType getReturnType() {
    return null;
  }

  @Override
  public boolean isBound() {
    return false;
  }

}
