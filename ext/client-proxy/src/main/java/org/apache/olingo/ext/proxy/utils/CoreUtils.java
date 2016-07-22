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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.api.domain.ClientAnnotatable;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.client.core.domain.ClientAnnotationImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKey;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.annotations.EnumType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.Term;
import org.apache.olingo.ext.proxy.commons.AbstractStructuredInvocationHandler;
import org.apache.olingo.ext.proxy.commons.ComplexInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
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

  public static ClientValue getODataValue(
      final EdmEnabledODataClient client, final EdmTypeInfo type, final Object obj) {

    final ClientValue value;

    if (type.isCollection()) {
      value = client.getObjectFactory().newCollectionValue(type.getFullQualifiedName().toString());

      final EdmTypeInfo intType = new EdmTypeInfo.Builder().
          setEdm(client.getCachedEdm()).setTypeExpression(type.getFullQualifiedName().toString()).build();

      for (Object collectionItem : (Collection<?>) obj) {
        if (intType.isPrimitiveType()) {
          value.asCollection().add(getODataValue(client, intType, collectionItem).asPrimitive());
        } else if (intType.isEnumType()) {
          value.asCollection().add((getODataValue(client, intType, collectionItem)).asEnum());
        } else if (intType.isComplexType()) {
          value.asCollection().add(getODataValue(client, intType, collectionItem).asComplex());
        } else {
          throw new UnsupportedOperationException("Unsupported object type " + intType.getFullQualifiedName());
        }
      }
    } else if (type.isComplexType()) {

      final Object objHandler = Proxy.getInvocationHandler(obj);
      if (objHandler instanceof ComplexInvocationHandler) {
        value = ((ComplexInvocationHandler) objHandler).getComplex();

        final Class<?> typeRef = ((ComplexInvocationHandler) objHandler).getTypeRef();

        for (Map.Entry<String, Object> changes : ((ComplexInvocationHandler) objHandler).getPropertyChanges()
            .entrySet()) {
          try {
            value.asComplex().add(getODataComplexProperty(
                client, type.getFullQualifiedName(), changes.getKey(), changes.getValue()));
          } catch (Exception ignore) {
            // ignore value
            LOG.warn("Error attaching complex {} for field '{}.{}'",
                type.getFullQualifiedName(), typeRef.getName(), changes.getKey(), ignore);
          }
        }

      } else {
        throw new IllegalArgumentException(objHandler.getClass().getName() + "' is not a complex value");
      }

    } else if (type.isEnumType()) {
      value = client.getObjectFactory().newEnumValue(type.getFullQualifiedName().toString(), ((Enum<?>) obj).name());
    } else {
      value = client.getObjectFactory().newPrimitiveValueBuilder().setType(type.getPrimitiveTypeKind()).setValue(obj).
          build();
    }

    return value;
  }

  private static ClientProperty getODataEntityProperty(
      final EdmEnabledODataClient client,
      final FullQualifiedName entity,
      final String property,
      final Object obj) {

    final EdmElement edmProperty = client.getCachedEdm().getEntityType(entity).getProperty(property);
    return getODataProperty(client, edmProperty, property, obj);
  }

  private static ClientProperty getODataComplexProperty(
      final EdmEnabledODataClient client,
      final FullQualifiedName complex,
      final String property,
      final Object obj) {

    final EdmElement edmProperty = client.getCachedEdm().getComplexType(complex).getProperty(property);
    return getODataProperty(client, edmProperty, property, obj);
  }

  private static ClientProperty getODataProperty(
      final EdmEnabledODataClient client,
      final EdmElement edmProperty,
      final String property,
      final Object obj) {

    final EdmTypeInfo type;
    if (edmProperty == null) {
      // maybe opentype ...
      type = null;
    } else {
      final EdmType edmType = edmProperty.getType();

      type = new EdmTypeInfo.Builder().setEdm(client.getCachedEdm()).setTypeExpression(
          edmProperty.isCollection()
              ? "Collection(" + edmType.getFullQualifiedName().toString() + ")"
              : edmType.getFullQualifiedName().toString()).build();
    }

    return getODataProperty(client, property, type, obj);
  }

  public static ClientAnnotation getODataAnnotation(
      final EdmEnabledODataClient client, final String term, final EdmType type, final Object obj) {

    ClientAnnotation annotation;

    if (obj == null) {
      annotation = new ClientAnnotationImpl(term, null);
    } else {
      final EdmTypeInfo valueType = type == null
          ? guessTypeFromObject(client, obj)
          : new EdmTypeInfo.Builder().setEdm(client.getCachedEdm()).
              setTypeExpression(type.getFullQualifiedName().toString()).build();

      annotation = new ClientAnnotationImpl(term, getODataValue(client, valueType, obj));
    }

    return annotation;
  }

  public static ClientProperty getODataProperty(
      final EdmEnabledODataClient client, final String name, final EdmTypeInfo type, final Object obj) {

    ClientProperty property;

    try {
      if (obj == null) {
        property = client.getObjectFactory().newPrimitiveProperty(name, null);
      } else {
        final EdmTypeInfo valueType = type == null
            ? guessTypeFromObject(client, obj)
            : type;
        final ClientValue value = getODataValue(client, valueType, obj);

        if (valueType.isCollection()) {
          property = client.getObjectFactory().newCollectionProperty(name, value.asCollection());
        } else if (valueType.isPrimitiveType()) {
          property = client.getObjectFactory().newPrimitiveProperty(name, value.asPrimitive());
        } else if (valueType.isComplexType()) {
          property = client.getObjectFactory().newComplexProperty(name, value.asComplex());
        } else if (valueType.isEnumType()) {
          property = client.getObjectFactory().newEnumProperty(name, value.asEnum());
        } else {
          throw new UnsupportedOperationException("Usupported object type " + valueType.getFullQualifiedName());
        }
      }

      return property;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static EdmTypeInfo guessTypeFromObject(
      final EdmEnabledODataClient client, final Object obj) {

    final EdmTypeInfo.Builder edmTypeInfo = new EdmTypeInfo.Builder().setEdm(client.getCachedEdm());

    if (Collection.class.isAssignableFrom(obj.getClass())) {
      final EdmTypeInfo type = guessPrimitiveType(client, ClassUtils.extractTypeArg(obj.getClass(),
          EntityCollection.class, ComplexCollection.class, Collection.class));
      return edmTypeInfo.setTypeExpression("Collection(" + type.getFullQualifiedName() + ")").build();
    } else if (obj instanceof Proxy) {
      final Class<?> typeRef = obj.getClass().getInterfaces()[0];
      final String ns = typeRef.getAnnotation(Namespace.class).value();
      final String name = typeRef.getAnnotation(ComplexType.class).name();
      return edmTypeInfo.setTypeExpression(new FullQualifiedName(ns, name).toString()).build();
    } else if (obj.getClass().getAnnotation(EnumType.class) != null) {
      final Class<?> typeRef = obj.getClass();
      final String ns = typeRef.getAnnotation(Namespace.class).value();
      final String name = typeRef.getAnnotation(EnumType.class).name();
      return edmTypeInfo.setTypeExpression(new FullQualifiedName(ns, name).toString()).build();
    } else {
      return guessPrimitiveType(client, obj.getClass());
    }
  }

  private static EdmTypeInfo guessPrimitiveType(final EdmEnabledODataClient client, final Class<?> clazz) {
    EdmPrimitiveTypeKind bckCandidate = null;

    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final Class<?> target = EdmPrimitiveTypeFactory.getInstance(kind).getDefaultType();

      if (clazz.equals(target)) {
        return new EdmTypeInfo.Builder().setEdm(client.getCachedEdm()).setTypeExpression(kind.toString()).build();
      } else if (target.isAssignableFrom(clazz)) {
        bckCandidate = kind;
      } else if (target == Timestamp.class && kind == EdmPrimitiveTypeKind.DateTimeOffset) {
        bckCandidate = kind;
      }
    }

    if (bckCandidate == null) {
      throw new IllegalArgumentException(clazz.getSimpleName() + " is not a simple type");
    } else {
      return new EdmTypeInfo.Builder().setEdm(client.getCachedEdm()).setTypeExpression(bckCandidate.toString()).build();
    }
  }

  public static void addProperties(
      final EdmEnabledODataClient client,
      final Map<String, Object> changes,
      final ClientEntity entity) {

    for (Map.Entry<String, Object> entry : changes.entrySet()) {
      entity.getProperties()
          .add(getODataEntityProperty(client, entity.getTypeName(), entry.getKey(), entry.getValue()));
    }
  }

  public static void addProperties(
      final EdmEnabledODataClient client,
      final Map<String, Object> changes,
      final ClientComplexValue entity) {

    for (Map.Entry<String, Object> entry : changes.entrySet()) {
      entity.add(getODataComplexProperty(
          client,
          new FullQualifiedName(entity.getTypeName()),
          entry.getKey(),
          entry.getValue()));
    }
  }

  public static void addAnnotations(
      final EdmEnabledODataClient client,
      final Map<Class<? extends AbstractTerm>, Object> annotations,
      final ClientAnnotatable annotatable) {

    for (Map.Entry<Class<? extends AbstractTerm>, Object> entry : annotations.entrySet()) {
      final Namespace nsAnn = entry.getKey().getAnnotation(Namespace.class);
      final Term termAnn = entry.getKey().getAnnotation(Term.class);
      final FullQualifiedName termName = new FullQualifiedName(nsAnn.value(), termAnn.name());
      final EdmTerm term = client.getCachedEdm().getTerm(termName);
      if (term == null) {
        LOG.error("Could not find term for class {}", entry.getKey().getName());
      } else {
        annotatable.getAnnotations().add(getODataAnnotation(
            client, term.getFullQualifiedName().toString(), term.getType(), entry.getValue()));
      }
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static Enum<?> enumValueToObject(final ClientEnumValue value, final Class<?> reference) {
    final Namespace namespace = reference.getAnnotation(Namespace.class);
    final EnumType enumType = reference.getAnnotation(EnumType.class);
    if (value.getTypeName().equals(namespace.value() + "." + enumType.name())) {
      return Enum.valueOf((Class<Enum>) reference, value.getValue());
    }

    return null;
  }

  private static Object primitiveValueToObject(final ClientPrimitiveValue value, final Class<?> reference) {
    Object obj;

    try {
      obj = reference == null
          ? value.toValue()
          : value.toCastValue(reference);
    } catch (EdmPrimitiveTypeException e) {
      LOG.warn("While casting primitive value {} to {}", value, reference, e);
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

  private static Class<?> getPropertyClass(final Class<?> entityClass, final String propertyName) {
    Class<?> propertyClass = null;
    try {
      final Method getter = entityClass.getMethod("get" + StringUtils.capitalize(propertyName));
      if (getter != null) {
        propertyClass = getter.getReturnType();
      }
    } catch (Exception e) {
      LOG.error("Could not determine the Java type of {}", propertyName, e);
    }
    return propertyClass;
  }

  public static URIBuilder buildEditLink(
      final EdmEnabledODataClient client,
      final String entitySetURI,
      final Object key) {

    if (key == null) {
      return null;
    }

    final URIBuilder uriBuilder = StringUtils.isNotBlank(entitySetURI)
        ? client.newURIBuilder(entitySetURI)
        : client.newURIBuilder();

    if (key.getClass().getAnnotation(CompoundKey.class) == null) {
      LOG.debug("Append key segment '{}'", key);
      uriBuilder.appendKeySegment(key);
    } else {
      LOG.debug("Append compound key segment '{}'", key);
      uriBuilder.appendKeySegment(CoreUtils.getCompoundKey(key));
    }

    return uriBuilder;
  }

  public static Map<String, Object> getCompoundKey(final Object key) {
    final Set<CompoundKeyElementWrapper> elements = new TreeSet<CompoundKeyElementWrapper>();

    for (Method method : key.getClass().getMethods()) {
      final Annotation annotation = method.getAnnotation(CompoundKeyElement.class);
      if (annotation instanceof CompoundKeyElement) {
        elements.add(new CompoundKeyElementWrapper(
            ((CompoundKeyElement) annotation).name(), method, ((CompoundKeyElement) annotation).position()));
      }
    }

    final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

    for (CompoundKeyElementWrapper element : elements) {
      try {
        map.put(element.getName(), element.getMethod().invoke(key));
      } catch (Exception e) {
        LOG.warn("Error retrieving compound key element '{}' value", element.getName(), e);
      }
    }

    return map;
  }

  public static Object getKey(
      final EdmEnabledODataClient client,
      final EntityInvocationHandler typeHandler,
      final Class<?> entityTypeRef,
      final ClientEntity entity) {

    Object res = null;

    if (!entity.getProperties().isEmpty()) {
      final Class<?> keyRef = ClassUtils.getCompoundKeyRef(entityTypeRef);
      if (keyRef == null) {
        final ClientProperty property = entity.getProperty(firstValidEntityKey(entityTypeRef));
        if (property != null && property.hasPrimitiveValue()) {
          res = primitiveValueToObject(
              property.getPrimitiveValue(), getPropertyClass(entityTypeRef, property.getName()));
        }
      } else {
        try {
          res = keyRef.newInstance();
          populate(client, typeHandler, res, CompoundKeyElement.class, entity.getProperties().iterator());
        } catch (Exception e) {
          LOG.error("Error population compound key {}", keyRef.getSimpleName(), e);
          throw new IllegalArgumentException("Cannot populate compound key");
        }
      }
    }

    return res;
  }

  private static void populate(
      final EdmEnabledODataClient client,
      final EntityInvocationHandler typeHandler,
      final Object bean,
      final Class<? extends Annotation> getterAnn,
      final Iterator<? extends ClientProperty> propItor) {

    if (bean != null) {
      final Class<?> typeRef;
      if (bean instanceof Proxy) {
        final InvocationHandler handler = Proxy.getInvocationHandler(bean);
        if (handler instanceof AbstractStructuredInvocationHandler) {
          typeRef = ((ComplexInvocationHandler) handler).getTypeRef();
        } else {
          throw new IllegalStateException("Invalid bean " + bean);
        }
      } else {
        typeRef = bean.getClass();
      }
      populate(client, typeHandler, bean, typeRef, getterAnn, propItor);
    }
  }

  @SuppressWarnings({ "unchecked" })
  private static void populate(
      final EdmEnabledODataClient client,
      final EntityInvocationHandler typeHandler,
      final Object bean,
      final Class<?> typeRef,
      final Class<? extends Annotation> getterAnn,
      final Iterator<? extends ClientProperty> propItor) {

    if (bean != null) {
      while (propItor.hasNext()) {
        final ClientProperty property = propItor.next();

        final Method getter = ClassUtils.findGetterByAnnotatedName(typeRef, getterAnn, property.getName());

        if (getter == null) {
          LOG.warn("Could not find any property annotated as {} in {}",
              property.getName(), bean.getClass().getName());
        } else {
          try {
            if (property.hasNullValue()) {
              setPropertyValue(bean, getter, null);
            } else if (property.hasPrimitiveValue()) {
              setPropertyValue(bean, getter, primitiveValueToObject(
                  property.getPrimitiveValue(), getPropertyClass(typeRef, property.getName())));
            } else if (property.hasComplexValue()) {
              final Object complex = Proxy.newProxyInstance(
                  Thread.currentThread().getContextClassLoader(),
                  new Class<?>[] { getter.getReturnType() },
                  ComplexInvocationHandler.getInstance(typeHandler, getter.getReturnType()));

              populate(client, typeHandler, complex, Property.class, property.getValue().asComplex().iterator());
              setPropertyValue(bean, getter, complex);
            } else if (property.hasCollectionValue()) {
              final ParameterizedType collType = (ParameterizedType) getter.getGenericReturnType();
              final Class<?> collItemClass = (Class<?>) collType.getActualTypeArguments()[0];

              Collection<Object> collection = (Collection<Object>) getter.invoke(bean);
              if (collection == null) {
                collection = new ArrayList<Object>();
                setPropertyValue(bean, getter, collection);
              }

              final Iterator<ClientValue> collPropItor = property.getValue().asCollection().iterator();
              while (collPropItor.hasNext()) {
                final ClientValue value = collPropItor.next();
                if (value.isPrimitive()) {
                  collection.add(primitiveValueToObject(
                      value.asPrimitive(), getPropertyClass(typeRef, property.getName())));
                } else if (value.isComplex()) {
                  final Object collItem = Proxy.newProxyInstance(
                      Thread.currentThread().getContextClassLoader(),
                      new Class<?>[] { collItemClass },
                      ComplexInvocationHandler.getInstance(typeHandler, collItemClass));

                  populate(client, typeHandler, collItem, Property.class, value.asComplex().iterator());
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

  public static Object getObjectFromODataValue(
      final ClientValue value,
      final Type typeRef,
      final AbstractService<?> service)
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

    return getObjectFromODataValue(value, internalRef, service);
  }

  public static Object getObjectFromODataValue(
      final ClientValue value,
      final Class<?> ref,
      final AbstractService<?> service)
      throws InstantiationException, IllegalAccessException {

    final Object res;

    if (value == null) {
      res = null;
    } else if (value.isComplex()) {
      // complex types supports inheritance in V4, best to re-read actual type
      Class<?> internalRef = getComplexTypeRef(service, value);
      res = Proxy.newProxyInstance(
          Thread.currentThread().getContextClassLoader(),
          new Class<?>[] { internalRef },
          ComplexInvocationHandler.getInstance(value.asComplex(), internalRef, service));
    } else if (value.isCollection()) {
      final ArrayList<Object> collection = new ArrayList<Object>();

      final Iterator<ClientValue> collPropItor = value.asCollection().iterator();
      while (collPropItor.hasNext()) {
        final ClientValue itemValue = collPropItor.next();
        if (itemValue.isPrimitive()) {
          collection.add(CoreUtils.primitiveValueToObject(itemValue.asPrimitive(), ref));
        } else if (itemValue.isComplex()) {
          Class<?> internalRef = getComplexTypeRef(service, value);
          final Object collItem = Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] { internalRef },
              ComplexInvocationHandler.getInstance(itemValue.asComplex(), internalRef, service));

          collection.add(collItem);
        } else if (itemValue.isEnum()) {
          collection.add(CoreUtils.enumValueToObject(itemValue.asEnum(), ref));
        } else {
          throw new RuntimeException("Unsupported collection item type " + itemValue.getTypeName());
        }
      }

      res = collection;
    } else if (value instanceof ClientEnumValue) {
      res = enumValueToObject((ClientEnumValue) value, ref == null ? getEnumTypeRef(service, value) : ref);
    } else {
      res = primitiveValueToObject(value.asPrimitive(), ref);
    }

    return res;
  }

  public static Collection<Class<? extends AbstractTerm>> getAnnotationTerms(
      final AbstractService<?> service, final List<ClientAnnotation> annotations) {

    final List<Class<? extends AbstractTerm>> res = new ArrayList<Class<? extends AbstractTerm>>();

    for (ClientAnnotation annotation : annotations) {
      final Class<? extends AbstractTerm> clazz = service.getTermClass(annotation.getTerm());
      if (clazz != null) {
        res.add(clazz);
      }
    }

    return res;
  }

  private static Class<?> getEnumTypeRef(final AbstractService<?> service, final ClientValue value) {
    return service.getEnumTypeClass(value.getTypeName().replaceAll("^Collection\\(", "").replaceAll("\\)$", ""));
  }

  public static Class<?> getComplexTypeRef(final AbstractService<?> service, final ClientValue value) {
    return service.getComplexTypeClass(value.getTypeName().replaceAll("^Collection\\(", "").replaceAll("\\)$", ""));
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

  public static URI getMediaEditLink(final String name, final ClientEntity entity) {
    final ClientLink mediaEditLink = entity.getMediaEditLink(name);
    return mediaEditLink == null ? URIUtils.getURI(entity.getEditLink(), name) : mediaEditLink.getLink();
  }

  public static URI getTargetEntitySetURI(final EdmEnabledODataClient client, final NavigationProperty property) {
    final URIBuilder uriBuilder = client.newURIBuilder(client.getServiceRoot());
    uriBuilder.appendEntitySetSegment(property.targetEntitySet());
    return uriBuilder.build();
  }
}
