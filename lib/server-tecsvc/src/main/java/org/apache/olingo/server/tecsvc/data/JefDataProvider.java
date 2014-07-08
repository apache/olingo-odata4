/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.tecsvc.data;

import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.LinkedComplexValueImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.serialization.JsonDeserializer;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class JefDataProvider implements DataProvider {

  private final Edm edm;
  private final Map<String, EntitySetContainer> esName2esContainer = new HashMap<String, EntitySetContainer>();

  public JefDataProvider(Edm edm) {
    this.edm = edm;
  }

  @Override
  public void reset() throws DataProviderException {
    initialize("ESAllPrim");
    initialize("ESCompAllPrim");
    initialize("ESCollAllPrim");
    initialize("ESCollAllPrim");
  }

  @Override
  public Entity read(String entitySetName, Object ... keys) throws DataProviderException {
    EntitySetContainer container = esName2esContainer.get(entitySetName);
    if(container == null) {
      container = initialize(entitySetName);
    }
    return container.getEntity(keys);
  }

  @Override
  public EntitySet readAll(String entitySetName) throws DataProviderException {
    try {
      EdmEntitySet edmEntitySet = edm.getEntityContainer(ContainerProvider.nameContainer).getEntitySet(entitySetName);
      return createEntitySetContainer(edmEntitySet).getEntitySet();
    } catch (ODataDeserializerException e) {
      throw new DataProviderException("Failure during reset/createEntitySetContainer", e);
    }
  }

  private EntitySetContainer initialize(String entitySetName) throws DataProviderException {
    try {
      EdmEntityContainer edmEntityContainer = edm.getEntityContainer(ContainerProvider.nameContainer);
      EntitySetContainer entitySetContainer = createEntitySetContainer(edmEntityContainer.getEntitySet(entitySetName));
      esName2esContainer.put(entitySetContainer.getEntitySetName(), entitySetContainer);
      return entitySetContainer;
    } catch (ODataDeserializerException e) {
      throw new DataProviderException("Failure during reset/createEntitySetContainer", e);
    }
  }

  private EntitySetContainer createEntitySetContainer(EdmEntitySet edmEntitySet)
      throws ODataDeserializerException, DataProviderException {
    String name = edmEntitySet.getName();
    InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(name + ".json");
    if(fis == null) {
      throw new DataProviderException("Failure during createEntitySetContainer, unable to load entity set [" +
              name + "] from file.");
    }

    ResWrap<EntitySet> wrapper = new JsonDeserializer(ODataServiceVersion.V40, true).toEntitySet(fis);
    EntitySet es = wrapper.getPayload();

    EntitySetContainer container = new EntitySetContainer(edmEntitySet);
    for (Entity entity : es.getEntities()) {
      Entity norm = normalize(entity, edmEntitySet.getEntityType());
      container.addEntity(norm);
    }

    return container;
  }

  private Entity normalize(Entity entity, EdmEntityType entityType) throws DataProviderException {
    EntityImpl ei = new EntityImpl();

    List<Property> propertyList = entity.getProperties();

    try {
      List<Property> normalized = normalizeProperties(propertyList, entityType);
      ei.getProperties().addAll(normalized);
      return ei;
    } catch (Exception e) {
      throw new DataProviderException("Failure during normalization of entity properties (for entity -> '" +
              entity + "')", e);
    }
  }

  private List<Property> normalizeProperties(Collection<Property> propertyList, EdmStructuredType entityType)
      throws IllegalAccessException, DataProviderException, EdmPrimitiveTypeException {
    List<Property> normalized = new ArrayList<Property>();

    for (Property prop: propertyList) {
      EdmElement element = entityType.getProperty(prop.getName());
      // FIXME
      if(element == null && prop.getName().contains("PropertyComp")) {
        String fixedName = prop.getName().replace("PropertyComp", "PropertyComplex");
        element = entityType.getProperty(fixedName);
      }
      //
      Property result = normalizeProperty(prop, element);
      normalized.add(result);
    }

    return normalized;
  }

  private Property normalizeProperty(Property prop, EdmElement element)
      throws EdmPrimitiveTypeException, IllegalAccessException, DataProviderException {
    return normalizeValue(prop.getValue(), element);
  }

  private Property normalizeValue(Object value, EdmElement element)
      throws EdmPrimitiveTypeException, IllegalAccessException, DataProviderException {
    if(element instanceof EdmProperty) {
      EdmProperty property = (EdmProperty) element;
      if(property.isPrimitive()) {
        return PropBuilder.build(property).value(value);
      } else {
        // recursion
        if(property.isCollection()) {
          return normalizeComplexCollection((Collection<?>) value, property);
        }

        return normalizeComplexValue(value, property);
      }
    }
    throw new DataProviderException("Unable to convert value [" + value + "] for property [" + element + "].");
  }

  private Property normalizeComplexCollection(Collection<?> values, EdmProperty property)
      throws IllegalAccessException, DataProviderException, EdmPrimitiveTypeException {

    List<Object> norm = new ArrayList<Object>();
    boolean linkedComplex = false;
    for (Object value : values) {
      if(value instanceof Collection) {
        Property r = normalizeComplexValue(value, property);
        norm.add(r.getValue());
      } else if(value instanceof LinkedComplexValue) {
        LinkedComplexValue lcv = (LinkedComplexValue) value;
        Property r = normalizeComplexValue(lcv, property);
        linkedComplex = r.isLinkedComplex();
        norm.add(r.getValue());
      }
    }

    if(linkedComplex) {
      return PropBuilder.build(property).linkedComplexCollection(norm);
    }
    return PropBuilder.build(property).complexCollection(norm);
  }

  private Property normalizeComplexValue(Object value, EdmProperty property)
      throws IllegalAccessException, DataProviderException, EdmPrimitiveTypeException {
    EdmComplexType complex = edm.getComplexType(property.getType().getFullQualifiedName());
    if(value instanceof Collection) {
      Collection<Property> norm = normalizeProperties((Collection<Property>) value, complex);
      return PropBuilder.build(property).complex(norm);
    } else if(value instanceof LinkedComplexValue) {
      LinkedComplexValue lcv = (LinkedComplexValue) value;
      Collection<Property> norm = normalizeProperties(lcv.getValue(), complex);
      LinkedComplexValueImpl no = new LinkedComplexValueImpl();
      no.getValue().addAll(norm);
      return PropBuilder.build(property).linkedComplex(no);
    }
    throw new DataProviderException("Unable to convert value [" + value + "] for property [" + property + "].");
  }

  private static class EntitySetContainer {
    final EdmEntitySet entitySet;
    final Map<String, Entity> key2Entity;

    public EntitySetContainer(EdmEntitySet entitySet) {
      this.entitySet = entitySet;
      key2Entity = new HashMap<String, Entity>();
    }

    public void addEntity(Entity entity) {
      String key = createKey(entity, entitySet);
      key2Entity.put(key, entity);
    }

    public EntitySet getEntitySet() {
      EntitySetImpl resultEntitySet = new EntitySetImpl();
      resultEntitySet.getEntities().addAll(key2Entity.values());
      return resultEntitySet;
    }

    public String getEntitySetName() {
      return entitySet.getName();
    }

    public Entity getEntity(Object ... keyValues) {
      List<String> keyProperties = new ArrayList<String>();
      Collections.sort(keyProperties);
      for (Object keyValue : keyValues) {
        keyProperties.add(String.valueOf(keyValue));
      }
      String key = keyProperties.toString();
      return key2Entity.get(key);
    }

    private static String createKey(Entity entity, EdmEntitySet entitySet) {
      List<String> keyPropertyValues = new ArrayList<String>();
      List<EdmKeyPropertyRef> keyRefs = entitySet.getEntityType().getKeyPropertyRefs();
      for (EdmKeyPropertyRef keyRef : keyRefs) {
        Property keyProperty = entity.getProperty(keyRef.getKeyPropertyName());
        keyPropertyValues.add(String.valueOf(keyProperty.getValue()));
      }
      Collections.sort(keyPropertyValues);
      return keyPropertyValues.toString();
    }
  }


  private static class PropBuilder {
    final PropertyImpl property;
    final EdmProperty edmProperty;

    private PropBuilder(EdmProperty edmProperty) {
      this.edmProperty = edmProperty;
      property = new PropertyImpl(edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString(),
          edmProperty.getName());
    }

    public static PropBuilder build(EdmProperty edmProperty) {
      return new PropBuilder(edmProperty);
    }

    public PropertyImpl primitive(Object ... value) {
      if(edmProperty.isCollection()) {
        property.setValue(ValueType.COLLECTION_PRIMITIVE, Arrays.asList(value));
      } else {
        if(value == null || value.length == 0) {
          property.setValue(ValueType.PRIMITIVE, null);
        } else {
          property.setValue(ValueType.PRIMITIVE, value[0]);
        }
      }
      return property;
    }

    public PropertyImpl complexCollection(Object value) {
      if(edmProperty.isCollection()) {
        property.setValue(ValueType.COLLECTION_COMPLEX, value);
      } else {
        throw new IllegalArgumentException("Property is no collection.");
      }
      return property;
    }

    public PropertyImpl complex(Object value) {
      property.setValue(ValueType.COMPLEX, value);
      return property;
    }

    public PropertyImpl linkedComplexCollection(Object value) {
      if(edmProperty.isCollection()) {
        property.setValue(ValueType.COLLECTION_LINKED_COMPLEX, value);
      } else {
        throw new IllegalArgumentException("Property is no collection.");
      }
      return property;
    }

    public PropertyImpl linkedComplex(Object value) {
      property.setValue(ValueType.LINKED_COMPLEX, value);
      return property;
    }

    public Property value(Object value) throws EdmPrimitiveTypeException {
      FullQualifiedName fqn = edmProperty.getType().getFullQualifiedName();

      try {
        EdmPrimitiveTypeKind kind = EdmPrimitiveTypeKind.valueOfFQN(ODataServiceVersion.V40, fqn);
        EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);

        if(edmProperty.isCollection()) {
          final Collection<?> values;
          if(value.getClass().isArray()) {
            values = Arrays.asList(value);
          } else if(value instanceof Collection) {
            values = (Collection<?>) value;
          } else {
            throw new EdmPrimitiveTypeException("Invalid collection value object class: " + value.getClass());
          }
          Collection<Object> result = new ArrayList<Object>(values.size());
          for (Object v : values) {
            String valueStr = String.valueOf(v);
            result.add(instance.valueOfString(valueStr,
                    edmProperty.isNullable(), edmProperty.getMaxLength(),
                    edmProperty.getPrecision(), edmProperty.getScale(),
                    true, instance.getDefaultType()));
          }
          property.setValue(ValueType.COLLECTION_PRIMITIVE, result);
        } else {
          String valueStr = String.valueOf(value);
          Object result = instance.valueOfString(valueStr,
                  edmProperty.isNullable(), edmProperty.getMaxLength(),
                  edmProperty.getPrecision(), edmProperty.getScale(),
                  true, instance.getDefaultType());
          property.setValue(ValueType.PRIMITIVE, result);
        }
      } catch (IllegalArgumentException e) {
        complex(value);
      }
      return property;
    }
  }
}
