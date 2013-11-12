package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmNamed;

public abstract class EdmNamedImpl implements EdmNamed {

  private String name;

  // TODO: ValidateName?
  public EdmNamedImpl(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

}
