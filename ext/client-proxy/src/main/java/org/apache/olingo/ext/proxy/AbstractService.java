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
package org.apache.olingo.ext.proxy;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.edm.ClientCsdlEdmProvider;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.commons.EntityContainerInvocationHandler;
import org.apache.olingo.ext.proxy.commons.NonTransactionalPersistenceManagerImpl;
import org.apache.olingo.ext.proxy.commons.TransactionalPersistenceManagerImpl;
import org.apache.olingo.ext.proxy.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for proxy mode, gives access to entity container instances.
 *
 * @param <C> actual client class
 */
public abstract class AbstractService<C extends EdmEnabledODataClient> {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractService.class);

  private final Map<Class<?>, Object> ENTITY_CONTAINERS = new ConcurrentHashMap<Class<?>, Object>();

  private final EdmEnabledODataClient client;

  private final Context context;

  private final boolean transactional;

  private PersistenceManager persistenceManager;

  protected AbstractService(final String compressedMetadata, final String metadataETag,
      final ODataServiceVersion version, final String serviceRoot, final boolean transactional) {

    ByteArrayInputStream bais = null;
    GZIPInputStream gzis = null;
    ObjectInputStream ois = null;
    XMLMetadata metadata = null;
    try {
      // use commons codec's Base64 in this fashion to stay compatible with Android
      bais = new ByteArrayInputStream(new Base64().decode(compressedMetadata.getBytes("UTF-8")));
      gzis = new GZIPInputStream(bais);
      ois = new ObjectInputStream(gzis);
      metadata = (XMLMetadata) ois.readObject();
    } catch (Exception e) {
      LOG.error("While deserializing compressed metadata", e);
    } finally {
      IOUtils.closeQuietly(ois);
      IOUtils.closeQuietly(gzis);
      IOUtils.closeQuietly(bais);
    }
    final Edm edm;
    if (metadata != null) {
      ClientCsdlEdmProvider provider = new ClientCsdlEdmProvider(metadata.getSchemaByNsOrAlias());
      edm = new EdmProviderImpl(provider);
    }else{
      edm = null;
    }
    if (version.compareTo(ODataServiceVersion.V40) < 0) {
      throw new ODataRuntimeException("Only OData V4 or higher supported.");
    }

    this.client = ODataClientFactory.getEdmEnabledClient(serviceRoot, edm, metadataETag);
    this.client.getConfiguration().setDefaultPubFormat(ContentType.JSON_FULL_METADATA);
    this.transactional = transactional;
    this.context = new Context();
  }

  public abstract Class<?> getEntityTypeClass(String name);

  public abstract Class<?> getComplexTypeClass(String name);

  public abstract Class<?> getEnumTypeClass(String name);

  public abstract Class<? extends AbstractTerm> getTermClass(String name);

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
          new Class<?>[] { reference },
          EntityContainerInvocationHandler.getInstance(reference, this));
      ENTITY_CONTAINERS.put(reference, entityContainer);
    }
    return reference.cast(ENTITY_CONTAINERS.get(reference));
  }
}
