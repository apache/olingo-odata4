package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Action;

//TODO: Test
public class EdmActionImpl extends EdmOperationImpl implements EdmAction {

  public EdmActionImpl(final FullQualifiedName name, final Action action) {
    super(name, action, EdmTypeKind.ACTION);
  }
}
