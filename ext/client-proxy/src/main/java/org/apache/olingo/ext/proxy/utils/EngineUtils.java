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
package org.apache.olingo.ext.proxy.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.api.v3.UnsupportedInV3Exception;
import org.apache.olingo.client.core.edm.xml.AbstractComplexType;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EngineUtils {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EngineUtils.class);

  private EngineUtils() {
    // Empty private constructor for static utility classes
  }

  public static ODataLink getNavigationLink(final String name, final CommonODataEntity entity) {
    ODataLink res = null;
    final List<ODataLink> links = entity.getNavigationLinks();

    for (int i = 0; i < links.size() && res == null; i++) {
      if (links.get(i).getName().equalsIgnoreCase(name)) {
        res = links.get(i);
      }
    }
    return res;
  }

  public static ODataValue getODataValue(
          final CommonEdmEnabledODataClient<?> client, final EdmTypeInfo type, final Object obj) {

    final ODataValue value;

    if (type.isCollection()) {
      value = client.getObjectFactory().newCollectionValue(type.getFullQualifiedName().toString());

      final EdmTypeInfo intType = new EdmTypeInfo.Builder().
              setEdm(client.getCachedEdm()).setTypeExpression(type.getFullQualifiedName().toString()).build();

      for (Object collectionItem : (Collection) obj) {
        if (intType.isPrimitiveType()) {
          value.asCollection().add(getODataValue(client, intType, collectionItem).asPrimitive());
        } else if (intType.isComplexType()) {
          value.asCollection().add(getODataValue(client, intType, collectionItem).asComplex());
        } else if (intType.isEnumType()) {
          if (client.getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
            throw new UnsupportedInV3Exception();
          } else {
            value.asCollection().add(((org.apache.olingo.commons.api.domain.v4.ODataValue) getODataValue(
                    client, intType, collectionItem)).asEnum());
          }

        } else {
          throw new UnsupportedOperationException("Usupported object type " + intType.getFullQualifiedName());
        }
      }
    } else if (type.isComplexType()) {
      value = client.getObjectFactory().newComplexValue(type.getFullQualifiedName().toString());

      if (obj.getClass().isAnnotationPresent(ComplexType.class)) {
        for (Method method : obj.getClass().getMethods()) {
          final Property complexPropertyAnn = method.getAnnotation(Property.class);
          try {
            if (complexPropertyAnn != null) {
              value.asComplex().add(getODataComplexProperty(
                      client, type.getFullQualifiedName(), complexPropertyAnn.name(), method.invoke(obj)));
            }
          } catch (Exception ignore) {
            // ignore value
            LOG.warn("Error attaching complex field '{}'", complexPropertyAnn.name(), ignore);
          }
        }
      } else {
        throw new IllegalArgumentException(
                "Object '" + obj.getClass().getSimpleName() + "' is not a complex value");
      }
    } else if (type.isEnumType()) {
      if (client.getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
        throw new UnsupportedInV3Exception();
      } else {
        value = ((org.apache.olingo.commons.api.domain.v4.ODataObjectFactory) client.getObjectFactory()).
                newEnumValue(type.getFullQualifiedName().toString(), ((Enum) obj).name());
      }
    } else {
      value = client.getObjectFactory().newPrimitiveValueBuilder().setType(type.getPrimitiveTypeKind()).setValue(obj).
              build();
    }

    return value;
  }

  private static CommonODataProperty getODataEntityProperty(
          final CommonEdmEnabledODataClient<?> client,
          final FullQualifiedName entity,
          final String property,
          final Object obj) {
    final EdmType edmType = client.getCachedEdm().getEntityType(entity).getProperty(property).getType();
    final EdmTypeInfo type = new EdmTypeInfo.Builder().
            setEdm(client.getCachedEdm()).setTypeExpression(edmType.getFullQualifiedName().toString()).build();

    return getODataProperty(client, property, type, obj);
  }

  private static CommonODataProperty getODataComplexProperty(
          final CommonEdmEnabledODataClient<?> client,
          final FullQualifiedName complex,
          final String property,
          final Object obj) {
    final EdmType edmType = client.getCachedEdm().getComplexType(complex).getProperty(property).getType();
    final EdmTypeInfo type = new EdmTypeInfo.Builder().
            setEdm(client.getCachedEdm()).setTypeExpression(edmType.getFullQualifiedName().toString()).build();

    return getODataProperty(client, property, type, obj);
  }

  private static CommonODataProperty getODataProperty(
          final CommonEdmEnabledODataClient<?> client, final String name, final EdmTypeInfo type, final Object obj) {
    final CommonODataProperty oprop;

    try {
      if (type == null || obj == null) {
        oprop = client.getObjectFactory().newPrimitiveProperty(name, null);
      } else if (type.isCollection()) {
        // create collection property
        oprop = client.getObjectFactory().newCollectionProperty(name, getODataValue(client, type, obj).asCollection());
      } else if (type.isPrimitiveType()) {
        // create a primitive property
        oprop = client.getObjectFactory().newPrimitiveProperty(name, getODataValue(client, type, obj).asPrimitive());
      } else if (type.isComplexType()) {
        // create a complex property
        oprop = client.getObjectFactory().newComplexProperty(name, getODataValue(client, type, obj).asComplex());
      } else if (type.isEnumType()) {
        if (client.getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
          throw new UnsupportedInV3Exception();
        } else {
          oprop = ((org.apache.olingo.commons.api.domain.v4.ODataObjectFactory) client.getObjectFactory()).
                  newEnumProperty(name,
                  ((org.apache.olingo.commons.api.domain.v4.ODataValue) getODataValue(client, type, obj)).asEnum());
        }
      } else {
        throw new UnsupportedOperationException("Usupported object type " + type.getFullQualifiedName());
      }

      return oprop;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static void addProperties(
          final CommonEdmEnabledODataClient<?> client,
          final Map<String, Object> changes,
          final CommonODataEntity entity) {

    for (Map.Entry<String, Object> property : changes.entrySet()) {
      // if the getter exists and it is annotated as expected then get value/value and add a new property
      final CommonODataProperty odataProperty = entity.getProperty(property.getKey());
      if (odataProperty != null) {
        entity.getProperties().remove(odataProperty);
      }

      ((List<CommonODataProperty>) entity.getProperties()).add(
              getODataEntityProperty(client, entity.getTypeName(), property.getKey(), property.getValue()));
    }
  }

  @SuppressWarnings("unchecked")
  private static void setPropertyValue(final Object bean, final Method getter, final Object value)
          throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    // Assumption: setter is always prefixed by 'set' word
    final String setterName = getter.getName().replaceFirst("get", "set");
    bean.getClass().getMethod(setterName, getter.getReturnType()).invoke(bean, value);
  }

  public static Object getKey(
          final Edm metadata, final Class<?> entityTypeRef, final CommonODataEntity entity) {
    final Object res;

    if (entity.getProperties().isEmpty()) {
      res = null;
    } else {
      final Class<?> keyRef = ClassUtils.getCompoundKeyRef(entityTypeRef);
      if (keyRef == null) {
        final CommonODataProperty property = entity.getProperty(firstValidEntityKey(entityTypeRef));
        res = property == null || !property.hasPrimitiveValue()
                ? null
                : property.getPrimitiveValue().toValue();

      } else {
        try {
          res = keyRef.newInstance();
          populate(metadata, res, CompoundKeyElement.class, entity.getProperties().iterator());
        } catch (Exception e) {
          LOG.error("Error population compound key {}", keyRef.getSimpleName(), e);
          throw new IllegalArgumentException("Cannot populate compound key");
        }
      }
    }

    return res;
  }

  @SuppressWarnings({"unchecked"})
  public static void populate(
          final Edm metadata,
          final Object bean,
          final Class<? extends Annotation> getterAnn,
          final Iterator<? extends CommonODataProperty> propItor) {

    if (bean != null) {
      while (propItor.hasNext()) {
        final CommonODataProperty property = propItor.next();

        final Method getter =
                ClassUtils.findGetterByAnnotatedName(bean.getClass(), getterAnn, property.getName());

        if (getter == null) {
          LOG.warn("Could not find any property annotated as {} in {}",
                  property.getName(), bean.getClass().getName());
        } else {
          try {
            if (property.hasNullValue()) {
              setPropertyValue(bean, getter, null);
            }
            if (property.hasPrimitiveValue()) {
              setPropertyValue(bean, getter, property.getPrimitiveValue().toValue());
            }
            if (property.hasComplexValue()) {
              final Object complex = getter.getReturnType().newInstance();
              populate(metadata, complex, Property.class, property.getValue().asComplex().iterator());
              setPropertyValue(bean, getter, complex);
            }
            if (property.hasCollectionValue()) {
              final ParameterizedType collType = (ParameterizedType) getter.getGenericReturnType();
              final Class<?> collItemClass = (Class<?>) collType.getActualTypeArguments()[0];

              Collection<Object> collection = (Collection<Object>) getter.invoke(bean);
              if (collection == null) {
                collection = new ArrayList<Object>();
                setPropertyValue(bean, getter, collection);
              }

              final Iterator<ODataValue> collPropItor = property.getValue().asCollection().iterator();
              while (collPropItor.hasNext()) {
                final ODataValue value = collPropItor.next();
                if (value.isPrimitive()) {
                  collection.add(value.asPrimitive().toValue());
                }
                if (value.isComplex()) {
                  final Object collItem = collItemClass.newInstance();
                  populate(metadata, collItem, Property.class, value.asComplex().iterator());
                  collection.add(collItem);
                }
              }
            }
          } catch (Exception e) {
            LOG.error("Could not set property {} on {}", getter, bean, e);
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static Object getValueFromProperty(final Edm metadata, final CommonODataProperty property)
          throws InstantiationException, IllegalAccessException {

    final Object value;

    if (property == null || property.hasNullValue()) {
      value = null;
    } else if (property.hasCollectionValue()) {
      value = new ArrayList<Object>();

      final Iterator<ODataValue> collPropItor = property.getValue().asCollection().iterator();
      while (collPropItor.hasNext()) {
        final ODataValue odataValue = collPropItor.next();
        if (odataValue.isPrimitive()) {
          ((Collection) value).add(odataValue.asPrimitive().toValue());
        }
        if (odataValue.isComplex()) {
          final Object collItem =
                  buildComplexInstance(metadata, property.getName(), odataValue.asComplex().iterator());
          ((Collection) value).add(collItem);
        }
      }
    } else if (property.hasPrimitiveValue()) {
      value = property.getPrimitiveValue().toValue();
    } else if (property.hasComplexValue()) {
      value = buildComplexInstance(
              metadata, property.getValue().asComplex().getTypeName(), property.getValue().asComplex().iterator());
    } else {
      throw new IllegalArgumentException("Invalid property " + property);
    }

    return value;
  }

  @SuppressWarnings("unchecked")
  private static <C extends AbstractComplexType> C buildComplexInstance(
          final Edm metadata, final String name, final Iterator<CommonODataProperty> properties) {

    for (C complex : (Iterable<C>) ServiceLoader.load(AbstractComplexType.class)) {
      final ComplexType ann = complex.getClass().getAnnotation(ComplexType.class);
      final String fn = ann == null ? null : ClassUtils.getNamespace(complex.getClass()) + "." + ann.name();

      if (name.equals(fn)) {
        populate(metadata, complex, Property.class, properties);
        return complex;
      }
    }

    return null;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Object getValueFromProperty(final Edm metadata, final CommonODataProperty property, final Type type)
          throws InstantiationException, IllegalAccessException {

    final Object value;

    if (property == null || property.hasNullValue()) {
      value = null;
    } else if (property.hasCollectionValue()) {
      value = new ArrayList();

      final ParameterizedType collType = (ParameterizedType) type;
      final Class<?> collItemClass = (Class<?>) collType.getActualTypeArguments()[0];

      final Iterator<ODataValue> collPropItor = property.getValue().asCollection().iterator();
      while (collPropItor.hasNext()) {
        final ODataValue odataValue = collPropItor.next();
        if (odataValue.isPrimitive()) {
          ((Collection) value).add(odataValue.asPrimitive().toValue());
        }
        if (odataValue.isComplex()) {
          final Object collItem = collItemClass.newInstance();
          populate(metadata, collItem, Property.class, odataValue.asComplex().iterator());
          ((Collection) value).add(collItem);
        }
      }
    } else if (property.hasPrimitiveValue()) {
      value = property.getPrimitiveValue().toValue();
    } else if (property.hasComplexValue()) {
      value = ((Class<?>) type).newInstance();
      populate(metadata, value, Property.class, property.getValue().asComplex().iterator());
    } else {
      throw new IllegalArgumentException("Invalid property " + property);
    }

    return value;
  }

  private static String firstValidEntityKey(final Class<?> entityTypeRef) {
    for (Method method : entityTypeRef.getDeclaredMethods()) {
      if (method.getAnnotation(Key.class) != null) {
        final Annotation ann = method.getAnnotation(Property.class);
        if (ann != null) {
          return ((Property) ann).name();
        }
      }
    }
    return null;
  }

  public static URI getEditMediaLink(final String name, final CommonODataEntity entity) {
    for (ODataLink editMediaLink : entity.getEditMediaLinks()) {
      if (name.equalsIgnoreCase(editMediaLink.getName())) {
        return editMediaLink.getLink();
      }
    }

    throw new IllegalArgumentException("Invalid streamed property " + name);
  }
}
