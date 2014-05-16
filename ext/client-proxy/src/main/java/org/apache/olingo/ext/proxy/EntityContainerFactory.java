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

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.ext.proxy.commons.EntityContainerInvocationHandler;
import org.apache.olingo.ext.proxy.context.Context;

/**
 * Entry point for proxy mode, gives access to entity container instances.
 *
 * @param <C> actual client class
 */
public final class EntityContainerFactory<C extends CommonEdmEnabledODataClient<?>> {

  private static final Map<String, EntityContainerFactory<?>> FACTORY_PER_SERVICEROOT =
          new ConcurrentHashMap<String, EntityContainerFactory<?>>();

  private static final Map<Class<?>, Object> ENTITY_CONTAINERS = new ConcurrentHashMap<Class<?>, Object>();

  @SuppressWarnings("unchecked")
  private static <C extends CommonEdmEnabledODataClient<?>> EntityContainerFactory<C> getInstance(
          final C client, final String serviceRoot) {

    if (!FACTORY_PER_SERVICEROOT.containsKey(serviceRoot)) {
      final EntityContainerFactory<C> instance = new EntityContainerFactory<C>(client);
      FACTORY_PER_SERVICEROOT.put(serviceRoot, instance);
    }
    client.getConfiguration().setDefaultPubFormat(ODataPubFormat.JSON_FULL_METADATA);

    return (EntityContainerFactory<C>) FACTORY_PER_SERVICEROOT.get(serviceRoot);
  }

  public static EntityContainerFactory<org.apache.olingo.client.api.v3.EdmEnabledODataClient> getV3(
          final String serviceRoot) {

    return getInstance(ODataClientFactory.getEdmEnabledV3(serviceRoot), serviceRoot);
  }

  public static EntityContainerFactory<org.apache.olingo.client.api.v4.EdmEnabledODataClient> getV4(
          final String serviceRoot) {

    return getInstance(ODataClientFactory.getEdmEnabledV4(serviceRoot), serviceRoot);
  }

  private final CommonEdmEnabledODataClient<?> client;

  private final Context context;

  private EntityContainerFactory(final CommonEdmEnabledODataClient<?> client) {
    this.client = client;
    this.context = new Context();
  }

  @SuppressWarnings("unchecked")
  public C getClient() {
    return (C) client;
  }

  public Context getContext() {
    return context;
  }

  /**
   * Return an initialized concrete implementation of the passed EntityContainer interface.
   *
   * @param <T> interface annotated as EntityContainer
   * @param reference class object of the EntityContainer annotated interface
   * @return an initialized concrete implementation of the passed reference
   * @throws IllegalStateException if <tt>serviceRoot</tt> was not set
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
}
