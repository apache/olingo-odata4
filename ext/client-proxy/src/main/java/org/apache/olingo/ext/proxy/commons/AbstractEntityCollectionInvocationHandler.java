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
package org.apache.olingo.ext.proxy.commons;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientSingleton;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

//CHECKSTYLE:OFF
public abstract class AbstractEntityCollectionInvocationHandler<T extends EntityType<?>, EC extends EntityCollection<T, ?, ?>>
    extends AbstractCollectionInvocationHandler<T, EC> {
//CHECKSTYLE:ON
  protected URI targetEntitySetURI;

  private boolean isSingleton = false;

  protected final Class<EC> collItemRef;

  @SuppressWarnings("unchecked")
  public AbstractEntityCollectionInvocationHandler(
      final Class<?> ref,
      final AbstractService<?> service,
      final URIBuilder uri) {

    super(service,
        new ArrayList<T>(),
        (Class<T>) ClassUtils.extractTypeArg(
            ref, AbstractEntitySet.class, AbstractSingleton.class, EntityCollection.class),
        uri);

    this.targetEntitySetURI = uri.build();
    this.isSingleton = AbstractSingleton.class.isAssignableFrom(ref);

    final Type[] entitySetParams = ClassUtils.extractGenericType(ref, AbstractEntitySet.class, AbstractSingleton.class);

    if (entitySetParams != null) {
      this.collItemRef = (Class<EC>) entitySetParams[2];
    } else {
      this.collItemRef = null;
    }
  }

  @SuppressWarnings("unchecked")
  public AbstractEntityCollectionInvocationHandler(
      final Class<? extends EntityCollection<T, ?, ?>> ref,
      final AbstractService<?> service,
      final URI targetEntitySetURI,
      final URIBuilder uri) {
    super(service,
        new ArrayList<T>(),
        (Class<T>) ClassUtils.extractTypeArg(ref, EntityCollection.class),
        uri);

    this.uri = uri;
    this.targetEntitySetURI = targetEntitySetURI;
    this.collItemRef = (Class<EC>) ref;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Triple<List<T>, URI, List<ClientAnnotation>> fetchPartial(
      final URI uri, final Class<T> typeRef) {

    final List<ClientEntity> entities = new ArrayList<ClientEntity>();
    final URI next;
    final List<ClientAnnotation> anns = new ArrayList<ClientAnnotation>();

    if (isSingleton) {
      final ODataRetrieveResponse<ClientSingleton> res =
          ((ODataClient) getClient()).getRetrieveRequestFactory().getSingletonRequest(uri).execute();

      entities.add(res.getBody());
      next = null;
    } else {
      final ODataEntitySetRequest<ClientEntitySet> req =
          getClient().getRetrieveRequestFactory().getEntitySetRequest(uri);
      req.setPrefer(getClient().newPreferences().includeAnnotations("*"));

      final ODataRetrieveResponse<ClientEntitySet> res = req.execute();

      final ClientEntitySet entitySet = res.getBody();
      entities.addAll(entitySet.getEntities());
      next = entitySet.getNext();
      anns.addAll(entitySet.getAnnotations());
    }

    final List<T> res = new ArrayList<T>(entities.size());

    for (ClientEntity entity : entities) {
      Class<?> actualRef = null;
      if (entity.getTypeName() != null) {
        actualRef = service.getEntityTypeClass(entity.getTypeName().toString());
      }
      if (actualRef == null) {
        actualRef = typeRef;
      }

      final EntityInvocationHandler handler =
          this instanceof EntitySetInvocationHandler
              ? EntityInvocationHandler.getInstance(
                  entity,
                  EntitySetInvocationHandler.class.cast(this),
                  actualRef)
              : EntityInvocationHandler.getInstance(
                  entity,
                  targetEntitySetURI,
                  actualRef,
                  service);

      final EntityInvocationHandler handlerInTheContext = getContext().entityContext().getEntity(handler.getUUID());

      res.add((T) Proxy.newProxyInstance(
          Thread.currentThread().getContextClassLoader(),
          new Class<?>[] { actualRef },
          handlerInTheContext == null ? handler : handlerInTheContext));
    }

    return new ImmutableTriple<List<T>, URI, List<ClientAnnotation>>(res, next, anns);
  }
}
