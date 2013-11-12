package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.provider.Property;

public class EdmPropertyImpl extends EdmElementImpl implements EdmProperty {

  public EdmPropertyImpl(final Property property) {
    super(property.getName());
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
  public EdmMapping getMapping() {
    return null;
  }

  @Override
  public String getMimeType() {
    return null;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public Boolean isNullable() {
    return null;
  }

  @Override
  public Integer getMaxLength() {
    return null;
  }

  @Override
  public Integer getPrecision() {
    return null;
  }

  @Override
  public Integer getScale() {
    return null;
  }

  @Override
  public Boolean isUnicode() {
    return null;
  }

  @Override
  public String getDefaultValue() {
    return null;
  }

}
