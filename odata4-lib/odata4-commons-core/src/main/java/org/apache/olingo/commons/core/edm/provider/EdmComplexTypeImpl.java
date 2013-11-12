package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.ComplexType;

//TODO: Test
public class EdmComplexTypeImpl extends EdmStructuralTypeImpl implements EdmComplexType {

  public EdmComplexTypeImpl(final EdmProviderImpl edm, final FullQualifiedName name, final ComplexType complexType) {
    super(edm, name, complexType, EdmTypeKind.COMPLEX);
  }

  @Override
  public EdmComplexType getBaseType() {
    return (EdmComplexType) baseType;
  }
}
