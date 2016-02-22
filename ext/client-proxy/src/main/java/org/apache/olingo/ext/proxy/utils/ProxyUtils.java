/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.ext.proxy.utils;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.commons.ComplexInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityCollectionInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntitySetInvocationHandler;
import org.apache.olingo.ext.proxy.commons.InlineEntitySetInvocationHandler;

public class ProxyUtils {

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Object getEntityCollectionProxy(
      final AbstractService<?> service,
      final Class<?> typeRef,
      final Class<?> typeCollectionRef,
      final URI targetEntitySetURI,
      final ClientEntitySet entitySet,
      final URI uri,
      final boolean checkInTheContext) {

    final List<Object> items = extractItems(service, typeRef, entitySet, uri, checkInTheContext);

    return Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class<?>[] { typeCollectionRef },
        new EntityCollectionInvocationHandler(service, items, typeCollectionRef, targetEntitySetURI,
            uri == null ? null : service.getClient().newURIBuilder(uri.toASCIIString())));
  }

  private static List<Object> extractItems(final AbstractService<?> service, final Class<?> typeRef,
      final ClientEntitySet entitySet, final URI uri, final boolean checkInTheContext) {
    final List<Object> items = new ArrayList<Object>();

    if (entitySet != null) {
      for (ClientEntity entityFromSet : entitySet.getEntities()) {
        items.add(getEntityProxy(service, entityFromSet, uri, typeRef, null, checkInTheContext));
      }
    }
    return items;
  }

  public static Object getEntitySetProxy(
      final AbstractService<?> service,
      final Class<?> typeRef,
      final ClientEntitySet entitySet,
      final URI uri,
      final boolean checkInTheContext) {

    final Class<?> entityTypeRef = ClassUtils.extractTypeArg(typeRef, AbstractEntitySet.class,
        AbstractSingleton.class);

    final List<Object> items = extractItems(service, entityTypeRef, entitySet, uri, checkInTheContext);

    return Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class<?>[] { typeRef },
        InlineEntitySetInvocationHandler.getInstance(typeRef, service, uri, items));
  }

  public static Object getEntitySetProxy(
          final AbstractService<?> service,
          final Class<?> typeRef,
          final URI uri) {

    return Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {typeRef},
            EntitySetInvocationHandler.getInstance(typeRef, service, uri));
  }

  public static Object getEntityProxy(
          final AbstractService<?> service,
          final ClientEntity entity,
          final URI entitySetURI,
          final Class<?> type,
          final String eTag,
          final boolean checkInTheContext) {

    EntityInvocationHandler handler = EntityInvocationHandler.getInstance(entity, entitySetURI, type, service);

    if (StringUtils.isNotBlank(eTag)) {
      // override ETag into the wrapped object.
      handler.setETag(eTag);
    }

    if (checkInTheContext && service.getContext().entityContext().isAttached(handler)) {
      handler = service.getContext().entityContext().getEntity(handler.getUUID());
      handler.setEntity(entity);
    }

    return Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {type},
            handler);
  }

  public static Object getComplexProxy(
          final AbstractService<?> service,
          final String name,
          final ClientValue value,
          final Class<?> ref,
          final EntityInvocationHandler handler,
          final URI baseURI,
          final boolean collectionItem) {

    final URIBuilder targetURI;
    if (collectionItem) {
      targetURI = null;
    } else {
      targetURI = baseURI == null
              ? null : service.getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name);
    }

    final ComplexInvocationHandler complexHandler;
    Class<?> actualRef = ref;
    if (value == null) {
      complexHandler = ComplexInvocationHandler.getInstance(
              actualRef,
              service,
              targetURI);
    } else {
      actualRef = CoreUtils.getComplexTypeRef(service, value); // handle derived types
      complexHandler = ComplexInvocationHandler.getInstance(
              value.asComplex(),
              actualRef,
              service,
              targetURI);
    }

    complexHandler.setEntityHandler(handler);

    return Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {actualRef}, complexHandler);
  }
}
