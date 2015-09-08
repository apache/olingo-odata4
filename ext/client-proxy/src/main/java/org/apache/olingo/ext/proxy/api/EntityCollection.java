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

public interface EntityCollection<
        T extends StructuredType<?>, EC extends Collection<T>, CT extends StructuredCollection<T, EC, ?>>
        extends StructuredCollection<T, EC, CT> {

  /**
   * Appends ref segment to the URI.
   *
   * @return the same query instance.
   */
  CT refs();

  /**
   * Add entity by its reference ID.
   *
   * @param element entity to be linked.
   * @return <tt>TRUE</tt> if correctly added; <tt>FALSE</tt> otherwise.
   */
  <ET extends EntityType<?>> boolean addRef(ET element);
}
