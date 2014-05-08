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
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.ext.proxy.commons.EntityContainerInvocationHandler;
import org.apache.olingo.ext.proxy.context.Context;

/**
 * Entry point for ODataJClient proxy mode, gives access to entity container instances.
 */
public class EntityContainerFactory {

  private static final Object MONITOR = new Object();

  private static Context context = null;

  private static final Map<String, EntityContainerFactory> FACTORY_PER_SERVICEROOT =
          new ConcurrentHashMap<String, EntityContainerFactory>();

  private static final Map<Class<?>, Object> ENTITY_CONTAINERS =
          new ConcurrentHashMap<Class<?>, Object>();

  private final CommonEdmEnabledODataClient<?> client;

  private final String serviceRoot;

  public static Context getContext() {
    synchronized (MONITOR) {
      if (context == null) {
        context = new Context();
      }
    }

    return context;
  }

  private static <C extends CommonEdmEnabledODataClient<?>> EntityContainerFactory getInstance(
          final C client, final String serviceRoot) {
    if (!FACTORY_PER_SERVICEROOT.containsKey(serviceRoot)) {
      final EntityContainerFactory instance = new EntityContainerFactory(client, serviceRoot);
      FACTORY_PER_SERVICEROOT.put(serviceRoot, instance);
    }
    client.getConfiguration().setDefaultPubFormat(ODataPubFormat.JSON_FULL_METADATA);
    return FACTORY_PER_SERVICEROOT.get(serviceRoot);
  }

  public static EntityContainerFactory getV3Instance(final String serviceRoot) {
    return getInstance(ODataClientFactory.getEdmEnabledV3(serviceRoot), serviceRoot);
  }

  public static EntityContainerFactory getV4Instance(final String serviceRoot) {
    return getInstance(ODataClientFactory.getEdmEnabledV4(serviceRoot), serviceRoot);
  }

  private EntityContainerFactory(final CommonEdmEnabledODataClient<?> client, final String serviceRoot) {
    this.client = client;
    this.serviceRoot = serviceRoot;
  }

  public String getServiceRoot() {
    return serviceRoot;
  }

  /**
   * Return an initialized concrete implementation of the passed EntityContainer interface.
   *
   * @param <T> interface annotated as EntityContainer
   * @param reference class object of the EntityContainer annotated interface
   * @return an initialized concrete implementation of the passed reference
   * @throws IllegalStateException if <tt>serviceRoot</tt> was not set
   * @throws IllegalArgumentException if the passed reference is not an interface annotated as EntityContainer
   * @see com.msopentech.odatajclient.proxy.api.annotations.EntityContainer
   */
  @SuppressWarnings("unchecked")
  public <T> T getEntityContainer(final Class<T> reference) throws IllegalStateException, IllegalArgumentException {
    if (StringUtils.isBlank(serviceRoot)) {
      throw new IllegalStateException("serviceRoot was not set");
    }

    if (!ENTITY_CONTAINERS.containsKey(reference)) {
      final Object entityContainer = Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {reference},
              EntityContainerInvocationHandler.getInstance(client, reference, this));
      ENTITY_CONTAINERS.put(reference, entityContainer);
    }
    return (T) ENTITY_CONTAINERS.get(reference);
  }
}
