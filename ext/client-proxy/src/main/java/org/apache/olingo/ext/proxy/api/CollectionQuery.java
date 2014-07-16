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

import org.apache.olingo.client.api.uri.URIFilter;

public interface CollectionQuery<
        T extends StructuredType, EC extends AbstractEntityCollection<T>, CT extends CollectionQuery<T, EC, ?>>
        extends CommonQuery<CollectionQuery<T, EC, CT>> {

  /**
   * Returns all instances.
   *
   * @return all entities
   */
  EC execute();

  /**
   * Sets the <tt>$filter</tt> expression.
   * <br/>
   * Any of available operators and functions can be embodied here.
   *
   * @param filter the <tt>$filter</tt> expression.
   * @return the same query instance.
   */
  CT filter(String filter);

  /**
   * Sets the filter generating the <tt>$filter</tt> expression.
   *
   * @param filter filter instance (to be obtained via factory): note that <tt>build()</tt> method will be immediately
   * invoked.
   * @return the same query instance.
   */
  CT filter(URIFilter filter);

  /**
   * Sets the <tt>$orderBy</tt> expression.
   *
   * @param sort sort options.
   * @return the same query instance.
   */
  CT orderBy(Sort... sort);

  /**
   * Sets the <tt>$orderBy</tt> expression.
   *
   * @param orderBy the <tt>$orderBy</tt> expression.
   * @return the same query instance.
   */
  CT orderBy(String orderBy);

  /**
   * Sets the maximum number of results to retrieve (<tt>$top</tt>).
   *
   * @param top maximum number of results to retrieve
   * @return the same query instance.
   * @throws IllegalArgumentException if the argument is negative
   */
  CT top(int top) throws IllegalArgumentException;

  /**
   * Sets the position of the first result to retrieve (<tt>$skip</tt>).
   *
   * @param skip position of the first result, numbered from 0
   * @return the same query instance.
   * @throws IllegalArgumentException if the argument is negative
   */
  CT skip(int skip) throws IllegalArgumentException;
}
