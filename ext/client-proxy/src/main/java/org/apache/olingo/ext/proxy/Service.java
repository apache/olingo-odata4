/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.ext.proxy;

import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.commons.ComplexInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityCollectionInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityContainerInvocationHandler;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;
import org.apache.olingo.ext.proxy.commons.NonTransactionalPersistenceManagerImpl;
import org.apache.olingo.ext.proxy.commons.TransactionalPersistenceManagerImpl;
import org.apache.olingo.ext.proxy.context.Context;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entry point for proxy mode, gives access to entity container instances.
 *
 * @param <C> actual client class
 */
public final class Service<C extends CommonEdmEnabledODataClient<?>> {

  private static final Map<String, Service<?>> FACTORY_PER_SERVICEROOT =
          new ConcurrentHashMap<String, Service<?>>();

  private final Map<Class<?>, Object> ENTITY_CONTAINERS = new ConcurrentHashMap<Class<?>, Object>();

  @SuppressWarnings("unchecked")
  private static <C extends CommonEdmEnabledODataClient<?>> Service<C> getInstance(
          final C client, final boolean transactional) {

    if (!FACTORY_PER_SERVICEROOT.containsKey(client.getServiceRoot())) {
      client.getConfiguration().setDefaultPubFormat(ODataFormat.JSON_FULL_METADATA);
      final Service<C> instance = new Service<C>(client, transactional);
      FACTORY_PER_SERVICEROOT.put(client.getServiceRoot(), instance);
    }

    return (Service<C>) FACTORY_PER_SERVICEROOT.get(client.getServiceRoot());
  }

  /**
   * Gives an OData 3.0 instance for given service root, operating in transactions (with batch requests).
   *
   * @param serviceRoot OData service root
   * @return OData 3.0 instance for given service root, operating in transactions (with batch requests)
   */
  public static Service<org.apache.olingo.client.api.v3.EdmEnabledODataClient> getV3(
          final String serviceRoot) {

    return getV3(serviceRoot, true);
  }

  /**
   * Gives an OData 3.0 instance for given service root.
   *
   * @param serviceRoot OData service root
   * @param transactional whether operating in transactions (with batch requests) or not
   * @return OData 3.0 instance for given service root
   */
  public static Service<org.apache.olingo.client.api.v3.EdmEnabledODataClient> getV3(
          final String serviceRoot, final boolean transactional) {

    return getInstance(ODataClientFactory.getEdmEnabledV3(serviceRoot), transactional);
  }

  /**
   * Gives an OData 4.0 instance for given service root, operating in transactions (with batch requests).
   *
   * @param serviceRoot OData service root
   * @return OData 4.0 instance for given service root, operating in transactions (with batch requests)
   */
  public static Service<org.apache.olingo.client.api.v4.EdmEnabledODataClient> getV4(
          final String serviceRoot) {

    return getV4(serviceRoot, true);
  }

  /**
   * Gives an OData 4.0 instance for given service root.
   *
   * @param serviceRoot OData service root
   * @param transactional whether operating in transactions (with batch requests) or not
   * @return OData 4.0 instance for given service root
   */
  public static Service<org.apache.olingo.client.api.v4.EdmEnabledODataClient> getV4(
          final String serviceRoot, final boolean transactional) {

    return getInstance(ODataClientFactory.getEdmEnabledV4(serviceRoot), transactional);
  }
  private final CommonEdmEnabledODataClient<?> client;

  private final Context context;

  private final boolean transactional;

  private PersistenceManager persistenceManager;

  private Service(final CommonEdmEnabledODataClient<?> client, final boolean transactional) {
    this.client = client;
    this.context = new Context();
    this.transactional = transactional;
  }

  @SuppressWarnings("unchecked")
  public C getClient() {
    return (C) client;
  }

  public Context getContext() {
    return context;
  }

  public boolean isTransactional() {
    return transactional;
  }

  public PersistenceManager getPersistenceManager() {
    synchronized (this) {
      if (persistenceManager == null) {
        persistenceManager = transactional
                ? new TransactionalPersistenceManagerImpl(this)
                : new NonTransactionalPersistenceManagerImpl(this);
      }
    }
    return persistenceManager;
  }

  /**
   * Return an initialized concrete implementation of the passed EntityContainer interface.
   *
   * @param <T> interface annotated as EntityContainer
   * @param reference class object of the EntityContainer annotated interface
   * @return an initialized concrete implementation of the passed reference
   * @throws IllegalArgumentException if the passed reference is not an interface annotated as EntityContainer
   */
  public <T> T getEntityContainer(final Class<T> reference) throws IllegalStateException, IllegalArgumentException {
    if (!ENTITY_CONTAINERS.containsKey(reference)) {
      final Object entityContainer = Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {reference},
              EntityContainerInvocationHandler.getInstance(reference, this));
      ENTITY_CONTAINERS.put(reference, entityContainer);
    }
    return reference.cast(ENTITY_CONTAINERS.get(reference));
  }

  @SuppressWarnings("unchecked")
  public <NE extends EntityType> NE newEntity(final Class<NE> reference) {
    final EntityInvocationHandler handler = EntityInvocationHandler.getInstance(reference, this);

    return (NE) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {reference},
            handler);
  }

  @SuppressWarnings("unchecked")
  public <T extends EntityType, NEC extends EntityCollection<T>> NEC newEntityCollection(final Class<NEC> reference) {
    final Class<T> ref = (Class<T>) ClassUtils.extractTypeArg(reference, EntityCollection.class);

    return (NEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {reference},
            new EntityCollectionInvocationHandler<T>(this, new ArrayList<T>(), ref));
  }

  @SuppressWarnings("unchecked")
  public <NE extends ComplexType> NE newComplex(final Class<NE> reference) {
    return (NE) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {reference},
            ComplexInvocationHandler.getInstance(reference, this));

  }
}
