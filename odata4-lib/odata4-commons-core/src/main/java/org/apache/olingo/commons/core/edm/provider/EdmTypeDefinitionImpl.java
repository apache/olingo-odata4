package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.TypeDefinition;

//TODO: Test
public class EdmTypeDefinitionImpl extends EdmNamedImpl implements EdmTypeDefinition {

  private final FullQualifiedName typeDefinitionName;

  public EdmTypeDefinitionImpl(final FullQualifiedName typeDefinitionName, final TypeDefinition typeDefinition) {
    super(typeDefinitionName.getName());
    this.typeDefinitionName = typeDefinitionName;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return false;
  }

  @Override
  public Class<?> getDefaultType() {
    return null;
  }

  @Override
  public boolean validate(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) {
    return false;
  }

  @Override
  public <T> T valueOfString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  public String valueToString(final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  public String toUriLiteral(final String literal) {
    return null;
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  public String getNamespace() {
    return typeDefinitionName.getNamespace();
  }

  @Override
  public EdmTypeKind getKind() {
    return null;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
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
}
