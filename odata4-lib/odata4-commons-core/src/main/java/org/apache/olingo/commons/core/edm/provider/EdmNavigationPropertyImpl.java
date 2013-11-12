package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.provider.NavigationProperty;

//TODO: Test
public class EdmNavigationPropertyImpl extends EdmElementImpl implements EdmNavigationProperty {

  public EdmNavigationPropertyImpl(final NavigationProperty navigationProperty) {
    super(navigationProperty.getName());
  }

  @Override
  public EdmType getType() {
    return null;
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public Boolean isNullable() {
    return null;
  }

}
