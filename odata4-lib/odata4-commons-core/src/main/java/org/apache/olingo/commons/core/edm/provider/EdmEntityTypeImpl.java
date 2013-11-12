package org.apache.olingo.commons.core.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.EntityType;

//TODO: Test
public class EdmEntityTypeImpl extends EdmStructuralTypeImpl implements EdmEntityType {

  public EdmEntityTypeImpl(final EdmProviderImpl edm, final FullQualifiedName name, final EntityType entityType) {
    super(edm, name, entityType, EdmTypeKind.ENTITY);
  }

  @Override
  public boolean hasStream() {
    return false;
  }

  @Override
  public EdmEntityType getBaseType() {
    return null;
  }

  @Override
  public List<String> getKeyPredicateNames() {
    return null;
  }

  @Override
  public List<EdmKeyPropertyRef> getKeyPropertyRefs() {
    return null;
  }

  @Override
  public EdmKeyPropertyRef getKeyPropertyRef(final String keyPredicateName) {
    return null;
  }

}
