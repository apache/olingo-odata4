package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmElement;

public abstract class EdmElementImpl extends EdmNamedImpl implements EdmElement {

  public EdmElementImpl(final String name) {
    super(name);
  }
}
