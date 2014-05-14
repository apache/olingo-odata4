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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.api.v3.UnsupportedInV3Exception;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.annotations.EnumType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.commons.AbstractTypeInvocationHandler;
import org.apache.olingo.ext.proxy.commons.ComplexTypeInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityTypeInvocationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CoreUtils {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(CoreUtils.class);

  private CoreUtils() {
    // Empty private constructor for static utility classes
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

      final Object oo;
      if (obj instanceof Proxy) {
        oo = Proxy.getInvocationHandler(obj);
      } else {
        oo = obj;
      }

      if (oo instanceof ComplexTypeInvocationHandler<?>) {
        final Class<?> typeRef = ((ComplexTypeInvocationHandler<?>) oo).getTypeRef();
        final Object complex = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {typeRef},
                (ComplexTypeInvocationHandler<?>) oo);

        for (Method method : typeRef.getMethods()) {
          final Property complexPropertyAnn = method.getAnnotation(Property.class);
          try {
            if (complexPropertyAnn != null) {
              value.asComplex().add(getODataComplexProperty(
                      client, type.getFullQualifiedName(), complexPropertyAnn.name(), method.invoke(complex)));
            }
          } catch (Exception ignore) {
            // ignore value
            LOG.warn("Error attaching complex {} for field '{}.{}'",
                    type.getFullQualifiedName(), typeRef.getName(), complexPropertyAnn.name(), ignore);
          }
        }
      } else {
        throw new IllegalArgumentException(
                "Object '" + oo.getClass().getSimpleName() + "' is not a complex value");
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

    final EdmElement edmProperty = client.getCachedEdm().getEntityType(entity).getProperty(property);
    return getODataProperty(client, edmProperty, property, obj);
  }

  private static CommonODataProperty getODataComplexProperty(
          final CommonEdmEnabledODataClient<?> client,
          final FullQualifiedName complex,
          final String property,
          final Object obj) {

    final EdmElement edmProperty = client.getCachedEdm().getComplexType(complex).getProperty(property);
    return getODataProperty(client, edmProperty, property, obj);
  }

  private static CommonODataProperty getODataProperty(
          final CommonEdmEnabledODataClient<?> client,
          final EdmElement edmProperty,
          final String property,
          final Object obj) {

    final EdmType edmType = edmProperty.getType();

    final EdmTypeInfo type = new EdmTypeInfo.Builder().setEdm(client.getCachedEdm()).setTypeExpression(
            edmProperty.isCollection()
            ? "Collection(" + edmType.getFullQualifiedName().toString() + ")"
            : edmType.getFullQualifiedName().toString()).build();

    return getODataProperty(client, property, type, obj);
  }

  public static CommonODataProperty getODataProperty(
          final CommonEdmEnabledODataClient<?> client, final String name, final EdmTypeInfo type, final Object obj) {

    CommonODataProperty oprop;

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
                          ((org.apache.olingo.commons.api.domain.v4.ODataValue) getODataValue(client, type, obj)).
                          asEnum());
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

  public static Object primitiveValueToObject(final ODataPrimitiveValue value) {
    Object obj;

    try {
      obj = value.toValue() instanceof Timestamp
              ? value.toCastValue(Calendar.class)
              : value.toValue();
    } catch (EdmPrimitiveTypeException e) {
      LOG.warn("Could not read temporal value as Calendar, reverting to Timestamp", e);
      obj = value.toValue();
    }

    return obj;
  }

  private static void setPropertyValue(final Object bean, final Method getter, final Object value)
          throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    // Assumption: setter is always prefixed by 'set' word
    final String setterName = getter.getName().replaceFirst("get", "set");
    bean.getClass().getMethod(setterName, getter.getReturnType()).invoke(bean, value);
  }

  public static Object getKey(
          final CommonEdmEnabledODataClient<?> client, final Class<?> entityTypeRef, final CommonODataEntity entity) {

    Object res = null;

    if (!entity.getProperties().isEmpty()) {
      final Class<?> keyRef = ClassUtils.getCompoundKeyRef(entityTypeRef);
      if (keyRef == null) {
        final CommonODataProperty property = entity.getProperty(firstValidEntityKey(entityTypeRef));
        if (property != null && property.hasPrimitiveValue()) {
          res = primitiveValueToObject(property.getPrimitiveValue());
        }
      } else {
        try {
          res = keyRef.newInstance();
          populate(client, res, CompoundKeyElement.class, entity.getProperties().iterator());
        } catch (Exception e) {
          LOG.error("Error population compound key {}", keyRef.getSimpleName(), e);
          throw new IllegalArgumentException("Cannot populate compound key");
        }
      }
    }

    return res;
  }

  public static void populate(
          final CommonEdmEnabledODataClient<?> client,
          final Object bean,
          final Class<? extends Annotation> getterAnn,
          final Iterator<? extends CommonODataProperty> propItor) {

    if (bean != null) {
      final Class<?> typeRef;
      if (bean instanceof Proxy) {
        final InvocationHandler handler = Proxy.getInvocationHandler(bean);
        if (handler instanceof AbstractTypeInvocationHandler) {
          typeRef = ((ComplexTypeInvocationHandler<?>) handler).getTypeRef();
        } else {
          throw new IllegalStateException("Invalid bean " + bean);
        }
      } else {
        typeRef = bean.getClass();
      }
      populate(client, bean, typeRef, getterAnn, propItor);
    }
  }

  @SuppressWarnings({"unchecked"})
  public static void populate(
          final CommonEdmEnabledODataClient<?> client,
          final Object bean,
          final Class<?> reference,
          final Class<? extends Annotation> getterAnn,
          final Iterator<? extends CommonODataProperty> propItor) {

    if (bean != null) {
      while (propItor.hasNext()) {
        final CommonODataProperty property = propItor.next();

        final Method getter = ClassUtils.findGetterByAnnotatedName(reference, getterAnn, property.getName());

        if (getter == null) {
          LOG.warn("Could not find any property annotated as {} in {}",
                  property.getName(), bean.getClass().getName());
        } else {
          try {
            if (property.hasNullValue()) {
              setPropertyValue(bean, getter, null);
            } else if (property.hasPrimitiveValue()) {
              setPropertyValue(bean, getter, primitiveValueToObject(property.getPrimitiveValue()));
            } else if (property.hasComplexValue()) {
              final Object complex = Proxy.newProxyInstance(
                      Thread.currentThread().getContextClassLoader(),
                      new Class<?>[] {getter.getReturnType()},
                      ComplexTypeInvocationHandler.getInstance(
                              client, property.getName(), getter.getReturnType(), null));

              populate(client, complex, Property.class, property.getValue().asComplex().iterator());
              setPropertyValue(bean, getter, complex);
            } else if (property.hasCollectionValue()) {
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
                  collection.add(primitiveValueToObject(value.asPrimitive()));
                } else if (value.isComplex()) {
                  final Object collItem = Proxy.newProxyInstance(
                          Thread.currentThread().getContextClassLoader(),
                          new Class<?>[] {collItemClass},
                          ComplexTypeInvocationHandler.getInstance(
                                  client, property.getName(), collItemClass, null));

                  populate(client, collItem, Property.class, value.asComplex().iterator());
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

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Enum<?> buildEnumInstance(final ODataEnumValue value) {
    try {
      for (String enumTypeName
              : StringUtils.split(IOUtils.toString(CoreUtils.class.getResourceAsStream("/META-INF/enumTypes")), '\n')) {

        final Class<Enum> enumClass =
                (Class<Enum>) Thread.currentThread().getContextClassLoader().loadClass(enumTypeName);
        if (enumClass != null) {
          final Namespace namespace = enumClass.getAnnotation(Namespace.class);
          final EnumType enumType = enumClass.getAnnotation(EnumType.class);
          if (value.getTypeName().equals(namespace.value() + "." + enumType.name())) {
            return Enum.valueOf(enumClass, value.getValue());
          }
        }
      }
    } catch (Exception e) {
      LOG.error("While trying to load enum for {}", value, e);
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public static Object getValueFromProperty(
          final CommonEdmEnabledODataClient<?> client,
          final CommonODataProperty property,
          final Type typeRef,
          final EntityTypeInvocationHandler<?> entityHandler)
          throws InstantiationException, IllegalAccessException {

    Class<?> internalRef;
    if (typeRef == null) {
      internalRef = null;
    } else {
      try {
        internalRef = (Class<?>) ((ParameterizedType) typeRef).getActualTypeArguments()[0];
      } catch (ClassCastException e) {
        internalRef = (Class<?>) typeRef;
      }
    }

    final Object res;

    if (property == null || property.hasNullValue()) {
      res = null;
    } else if (property.hasComplexValue()) {
      res = Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {internalRef},
              ComplexTypeInvocationHandler.getInstance(
                      client, property.getValue().asComplex(), internalRef, entityHandler));
    } else if (property.hasCollectionValue()) {
      final ArrayList<Object> collection = new ArrayList<Object>();

      final Iterator<ODataValue> collPropItor = property.getValue().asCollection().iterator();
      while (collPropItor.hasNext()) {
        final ODataValue value = collPropItor.next();
        if (value.isPrimitive()) {
          collection.add(CoreUtils.primitiveValueToObject(value.asPrimitive()));
        } else if (value.isComplex()) {
          final Object collItem = Proxy.newProxyInstance(
                  Thread.currentThread().getContextClassLoader(),
                  new Class<?>[] {internalRef},
                  ComplexTypeInvocationHandler.getInstance(
                          client, value.asComplex(), internalRef, entityHandler));

          collection.add(collItem);
        }
      }

      res = collection;
    } else if (property instanceof ODataProperty && ((ODataProperty) property).hasEnumValue()) {
      res = buildEnumInstance(((ODataProperty) property).getEnumValue());
    } else {
      res = primitiveValueToObject(property.getPrimitiveValue());
    }

    return res;
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

  public static URI getMediaEditLink(final String name, final CommonODataEntity entity) {
    for (ODataLink link : entity.getMediaEditLinks()) {
      if (name.equalsIgnoreCase(link.getName())) {
        return link.getLink();
      }
    }

    throw new IllegalArgumentException("Invalid streamed property " + name);
  }
}
