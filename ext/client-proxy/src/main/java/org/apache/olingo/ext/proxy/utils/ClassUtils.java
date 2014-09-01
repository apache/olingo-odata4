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

import org.apache.olingo.ext.proxy.api.annotations.CompoundKey;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Set;

public final class ClassUtils {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ClassUtils.class);

  private ClassUtils() {
    // Empty private constructor for static utility classes
  }

  public static Type[] extractGenericType(final Class<?> paramType, final Class<?>... references) {
    if (Proxy.class.isAssignableFrom(paramType)) {
      return extractGenericType(Class.class.cast(paramType.getGenericInterfaces()[0]), references);
    }

    if (paramType.getGenericInterfaces().length > 0) {
      if (references == null || references.length == 0) {
        return ((ParameterizedType) paramType.getGenericInterfaces()[0]).getActualTypeArguments();
      } else {
        for (Type type : paramType.getGenericInterfaces()) {
          final Class<?> typeClass = getTypeClass(type);
          for (Class<?> reference : references) {
            if (reference.isAssignableFrom(typeClass)) {
              return ((ParameterizedType) type).getActualTypeArguments();
            }
          }
        }
      }
    }

    throw new IllegalArgumentException("Invalid type argument " + paramType);
  }

  public static Class<?> extractTypeArg(final Class<?> paramType, final Class<?>... references) {
    return Class.class.cast(extractGenericType(paramType, references)[0]);
  }

  public static Method findGetterByAnnotatedName(
          final Class<?> clazz, final Class<? extends Annotation> ann, final String name) {
    final Method[] methods = clazz.getMethods();

    Method result = null;
    for (int i = 0; i < methods.length && result == null; i++) {
      final Annotation annotation = methods[i].getAnnotation(ann);
      try {
        if ((annotation != null)
                && methods[i].getName().startsWith("get") // Assumption: getter is always prefixed by 'get' word 
                && name.equals(ann.getMethod("name").invoke(annotation))) {
          result = methods[i];
        }
      } catch (Exception e) {
        LOG.warn("Error retrieving value annotation name for {}.{}", clazz.getName(), methods[i].getName());
      }
    }

    return result;
  }

  public static <ANN extends Annotation> ANN getAnnotation(final Class<ANN> reference, final AccessibleObject obj) {
    final Annotation ann = obj.getAnnotation(reference);
    return ann == null ? null : reference.cast(ann);
  }

  public static Class<?> getCompoundKeyRef(final Class<?> entityTypeRef) {
    if (entityTypeRef.getAnnotation(EntityType.class) == null) {
      throw new IllegalArgumentException("Invalid annotation for entity type " + entityTypeRef);
    }

    final Annotation ann = entityTypeRef.getAnnotation(KeyRef.class);

    return ann == null || ((KeyRef) ann).value().getAnnotation(CompoundKey.class) == null
            ? null
            : ((KeyRef) ann).value();
  }

  public static Class<?> getKeyRef(final Class<?> entityTypeRef) {
    Class<?> res = getCompoundKeyRef(entityTypeRef);

    if (res == null) {
      final Set<Method> keyGetters = new HashSet<Method>();

      for (Method method : entityTypeRef.getDeclaredMethods()) {
        if (method.getName().startsWith("get") && method.getAnnotation(Key.class) != null) {
          keyGetters.add(method);
        }
      }

      if (keyGetters.size() == 1) {
        res = keyGetters.iterator().next().getReturnType();
      } else {
        throw new IllegalStateException(entityTypeRef.getSimpleName() + "'s key reference not found");
      }
    }

    return res;
  }

  public static String getEntityTypeName(final Class<?> ref) {
    final Annotation annotation = ref.getAnnotation(EntityType.class);
    if (!(annotation instanceof EntityType)) {
      throw new IllegalArgumentException(ref.getPackage().getName()
              + " is not annotated as @" + EntityType.class.getSimpleName());
    }
    return ((EntityType) annotation).name();
  }

  public static String getNamespace(final Class<?> ref) {
    final Annotation annotation = ref.getAnnotation(Namespace.class);
    if (!(annotation instanceof Namespace)) {
      throw new IllegalArgumentException(ref.getName()
              + " is not annotated as @" + Namespace.class.getSimpleName());
    }
    return ((Namespace) annotation).value();
  }

  public static Void returnVoid()
          throws NoSuchMethodException, InstantiationException, IllegalAccessException,
          IllegalArgumentException, InvocationTargetException {

    final Constructor<Void> voidConstructor = Void.class.getDeclaredConstructor();
    voidConstructor.setAccessible(true);
    return voidConstructor.newInstance();
  }

  public static Type[] getTypeArguments(final Type type) {
    if (type instanceof ParameterizedType) {
      return (ParameterizedType.class.cast(type).getActualTypeArguments());
    } else if (type instanceof TypeVariable) {
      return TypeVariable.class.cast(type).getBounds();
    } else {
      return new Type[] {};
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getTypeClass(final Type type) {
    if (type instanceof ParameterizedType) {
      return getTypeClass(ParameterizedType.class.cast(type).getRawType());
    } else if (type instanceof Class) {
      return Class.class.cast(type);
    } else {
      return null;
    }
  }
}
