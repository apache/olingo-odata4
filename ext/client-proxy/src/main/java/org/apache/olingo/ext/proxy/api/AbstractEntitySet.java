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

/**
 * Interface for synchronous CRUD operations on an EntitySet.
 */
public abstract interface AbstractEntitySet<
        T extends Serializable, KEY extends Serializable, EC extends AbstractEntityCollection<T>>
        extends Iterable<T>, Serializable {

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
  T get(KEY key) throws IllegalArgumentException;

  /**
   * Retrieves an entity by its key, considering polymorphism.
   *
   * @param key must not be null
   * @param reference entity class to be returned
   * @return the entity with the given id or null if none found
   * @throws IllegalArgumentException in case the given key is null
   */
  <S extends T> S get(KEY key, Class<S> reference) throws IllegalArgumentException;

  /**
   * Returns the number of entities available.
   *
   * @return the number of entities
   */
  Long count();

  /**
   * Returns all instances.
   *
   * @return all entities
   */
  EC getAll();

  /**
   * Returns all instances of the given subtype.
   *
   * @param reference entity collection class to be returned
   * @return all entities of the given subtype
   */
  <S extends T, SEC extends AbstractEntityCollection<S>> SEC getAll(Class<SEC> reference);

  /**
   * Deletes the entity with the given key.
   *
   * @param key must not be null
   * @throws IllegalArgumentException in case the given key is null
   */
  void delete(KEY key) throws IllegalArgumentException;

  /**
   * Deletes the given entities in a batch.
   *
   * @param entities to be deleted
   */
  <S extends T> void delete(Iterable<S> entities);

  /**
   * Create an instance of <tt>Query</tt>.
   *
   * @return the new query instance
   */
  Query<T, EC> createQuery();

  /**
   * Create an instance of <tt>Query</tt>.
   *
   * @return the new query instance
   */
  <S extends T, SEC extends AbstractEntityCollection<S>> Query<S, SEC> createQuery(Class<SEC> reference);
}
