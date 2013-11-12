package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.helper.EntityContainerInfo;

//TODO: Test
public class EdmEntityContainerImpl extends EdmNamedImpl implements EdmEntityContainer {

  private EntityContainerInfo entityContainerInfo;

  public EdmEntityContainerImpl(final EntityContainerInfo entityContainerInfo) {
    super(entityContainerInfo.getContainerName().getName());
    this.entityContainerInfo = entityContainerInfo;
  }

  @Override
  public String getNamespace() {
    return entityContainerInfo.getContainerName().getNamespace();
  }

  @Override
  public EdmSingleton getSingleton(final String name) {
    return null;
  }

  @Override
  public EdmEntitySet getEntitySet(final String name) {
    return null;
  }

  @Override
  public EdmActionImport getActionImport(final String name) {
    return null;
  }

  @Override
  public EdmFunctionImport getFunctionImport(final String name) {
    return null;
  }

}
