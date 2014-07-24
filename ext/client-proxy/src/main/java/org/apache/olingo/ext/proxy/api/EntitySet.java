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

import java.util.Collection;
import java.util.concurrent.Future;

public interface EntitySet<T extends EntityType<?>, EC extends Collection<T>> {

  /**
   * Returns all instances of the given subtype.
   *
   * @param <S>
   * @param <SEC>
   * @param reference entity collection class to be returned
   * @return all entities of the given subtype
   */
  <S extends T, SEC extends EntityCollection<S, ?, ?>> SEC execute(Class<SEC> reference);

  /**
   * Asynchronously returns all instances of the given subtype.
   *
   * @param <S>
   * @param <SEC>
   * @param reference entity collection class to be returned
   * @return future handle on all entities of the given subtype
   */
  <S extends T, SEC extends EntityCollection<S, ?, ?>> Future<SEC> executeAsync(Class<SEC> reference);

  /**
   * Returns all instances.
   *
   * @return all instances
   */
  EC execute();

  /**
   * Asynchronously returns all instances.
   *
   * @return future handle on all instances
   */
  Future<EC> executeAsync();
}
