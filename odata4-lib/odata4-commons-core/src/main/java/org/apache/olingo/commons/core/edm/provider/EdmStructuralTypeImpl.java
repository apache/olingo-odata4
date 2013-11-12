package org.apache.olingo.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmStructuralType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.commons.api.edm.provider.Property;
import org.apache.olingo.commons.api.edm.provider.StructuralType;

public abstract class EdmStructuralTypeImpl extends EdmTypeImpl implements EdmStructuralType {

  private final Map<String, EdmElement> properties = new HashMap<String, EdmElement>();
  private final ArrayList<String> navigationPropertyNames = new ArrayList<String>();
  private final ArrayList<String> propertyNames = new ArrayList<String>();
  protected final EdmStructuralType baseType;

  public EdmStructuralTypeImpl(final EdmProviderImpl edm, final FullQualifiedName name,
      final StructuralType structuralType,
      final EdmTypeKind kind) {
    super(name, kind);
    baseType = buildBaseType(edm, structuralType.getBaseType(), kind);
    buildProperties(structuralType.getProperties());
    buildNavigationProperties(structuralType.getNavigationProperties());
  }

  private EdmStructuralType buildBaseType(final Edm edm, final FullQualifiedName baseType, final EdmTypeKind kind) {
    if (baseType != null) {
      if (EdmTypeKind.COMPLEX.equals(kind)) {
        EdmComplexType complexType = edm.getComplexType(baseType);
        if (complexType != null) {
          propertyNames.addAll(complexType.getPropertyNames());
          navigationPropertyNames.addAll(complexType.getNavigationPropertyNames());
        } else {
          throw new EdmException("Missing ComplexType for FQN: " + baseType);
        }
        return complexType;
      } else if (EdmTypeKind.ENTITY.equals(kind)) {
        EdmEntityType entityType = edm.getEntityType(baseType);
        if (entityType != null) {
          propertyNames.addAll(entityType.getPropertyNames());
          navigationPropertyNames.addAll(entityType.getNavigationPropertyNames());
        } else {
          throw new EdmException("Missing EntityType for FQN: " + baseType);
        }
        return entityType;
      } else {
        throw new EdmException("Unkonwn Type Kind");
      }
    } else {
      return null;
    }

  }

  private void buildNavigationProperties(final List<NavigationProperty> providerNavigationProperties) {
    if (providerNavigationProperties != null) {
      for (NavigationProperty navigationProperty : providerNavigationProperties) {
        navigationPropertyNames.add(navigationProperty.getName());
        properties.put(navigationProperty.getName(), new EdmNavigationPropertyImpl(navigationProperty));
      }
    }

  }

  private void buildProperties(final List<Property> providerProperties) {
    if (providerProperties != null) {
      for (Property property : providerProperties) {
        propertyNames.add(property.getName());
        properties.put(property.getName(), new EdmPropertyImpl(property));
      }
    }

  }

  @Override
  public EdmElement getProperty(final String name) {
    EdmElement property = null;
    if (baseType != null) {
      property = baseType.getProperty(name);
    }
    if (property == null) {
      property = properties.get(name);
    }
    return property;
  }

  @Override
  public List<String> getPropertyNames() {
    return propertyNames;
  }

  @Override
  public List<String> getNavigationPropertyNames() {
    return navigationPropertyNames;
  }

  @Override
  public EdmStructuralType getBaseType() {
    return baseType;
  }
}
