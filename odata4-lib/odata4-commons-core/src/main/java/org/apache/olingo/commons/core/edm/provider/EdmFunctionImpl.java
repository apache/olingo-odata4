package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Function;

//TODO: Test
public class EdmFunctionImpl extends EdmOperationImpl implements EdmFunction {

  private Function function;

  public EdmFunctionImpl(final FullQualifiedName name, final Function function) {
    super(name, function, EdmTypeKind.FUNCTION);
    this.function = function;
  }

  @Override
  public boolean isComposable() {
    return function.isComposable();
  }

}
