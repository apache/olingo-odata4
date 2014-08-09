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
package org.apache.olingo.ext.proxy.api;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface for synchronous CRUD operations on an EntitySet.
 */
public interface AbstractEntitySet<
        T extends EntityType<?>, KEY extends Serializable, EC extends Collection<T>>
        extends Iterable<T>, Serializable {

  boolean add(final T entity);

  /**
   * Returns whether an entity with the given id exists.
   *
   * @param key must not be null
   * @return true if an entity with the given id exists, false otherwise
   * @throws IllegalArgumentException in case the given key is null
   */
  Boolean exists(KEY key) throws IllegalArgumentException;

  /**
   * Retrieves an entity by its key.
   *
   * @param key must not be null
   * @return the entity with the given id or null if none found
   * @throws IllegalArgumentException in case the given key is null
   */
  T getByKey(KEY key) throws IllegalArgumentException;

  /**
   * Retrieves an entity by its key, considering polymorphism.
   *
   * @param <S>
   * @param key must not be null
   * @param reference entity class to be returned
   * @return the entity with the given id or null if none found
   * @throws IllegalArgumentException in case the given key is null
   */
  <S extends T> S getByKey(KEY key, Class<S> reference) throws IllegalArgumentException;

  /**
   * Returns the number of entities available.
   *
   * @return the number of entities
   */
  Long count();

  /**
   * Deletes the entity with the given key.
   *
   * @param key must not be null
   * @throws IllegalArgumentException in case the given key is null
   */
  void delete(KEY key) throws IllegalArgumentException;

  /**
   * Deletes the given entity in a batch.
   *
   * @param <S>
   * @param entity to be deleted
   */
  <S extends T> void delete(S entity);

  /**
   * Deletes the given entities in a batch.
   *
   * @param <S>
   * @param entities to be deleted
   */
  <S extends T> void delete(Iterable<S> entities);

  /**
   * Create an instance of <tt>Search</tt>.
   *
   * @return the new search instance
   */
  Search<T, EC> createSearch();

  /**
   * Create an instance of <tt>Search</tt>.
   *
   * @param <S>
   * @param <SEC>
   * @param reference
   * @return the new search instance
   */
  <S extends T, SEC extends EntityCollection<S, ?, ?>> Search<S, SEC> createSearch(Class<SEC> reference);
}
