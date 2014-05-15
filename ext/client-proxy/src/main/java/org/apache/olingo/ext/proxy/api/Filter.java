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
import org.apache.olingo.client.api.uri.URIFilter;

/**
 * Interface used to control filter execution.
 *
 * @param <T> filter result type
 * @param <EC>
 */
public interface Filter<T extends Serializable, EC extends AbstractEntityCollection<T>> extends Serializable {

  /**
   * Sets the <tt>$filter</tt> expression for this filter.
   * <br/>
   * Any of available operators and functions can be embodied here.
   *
   * @param filter the <tt>$filter</tt> expression for this filter
   * @return the same filter instance
   */
  Filter<T, EC> setFilter(String filter);

  /**
   * Sets the filter generating the <tt>$filter</tt> expression for this filter.
   *
   * @param filter filter instance (to be obtained via factory): note that <tt>build()</tt> method
   * will be immediately invoked.
   * @return the same filter instance
   */
  Filter<T, EC> setFilter(URIFilter filter);

  /**
   * The <tt>$filter</tt> expression for this filter.
   *
   * @return the <tt>$filter</tt> expression for this filter
   */
  String getFilter();

  /**
   * Sets the <tt>$orderBy</tt> expression for this filter via sort options.
   *
   * @param sort sort options
   * @return the same filter instance
   */
  Filter<T, EC> setOrderBy(Sort... sort);

  /**
   * Sets the <tt>$orderBy</tt> expression for this filter.
   *
   * @param orderBy the <tt>$orderBy</tt> expression for this filter
   * @return the same filter instance
   */
  Filter<T, EC> setOrderBy(String orderBy);

  /**
   * The <tt>$orderBy</tt> expression for this filter.
   *
   * @return the <tt>$orderBy</tt> expression for this filter
   */
  String getOrderBy();

  /**
   * Sets the maximum number of results to retrieve (<tt>$top</tt>).
   *
   * @param maxResults maximum number of results to retrieve
   * @return the same filter instance
   * @throws IllegalArgumentException if the argument is negative
   */
  Filter<T, EC> setMaxResults(int maxResults) throws IllegalArgumentException;

  /**
   * The maximum number of results the filter object was set to retrieve (<tt>$top</tt>). Returns
   * <tt>Integer.MAX_VALUE</tt> if setMaxResults was not applied to the filter object.
   *
   * @return maximum number of results
   */
  int getMaxResults();

  /**
   * Sets the position of the first result to retrieve (<tt>$skip</tt>).
   *
   * @param firstResult position of the first result, numbered from 0
   * @return the same filter instance
   * @throws IllegalArgumentException if the argument is negative
   */
  Filter<T, EC> setFirstResult(int firstResult) throws IllegalArgumentException;

  /**
   * The position of the first result the filter object was set to retrieve (<tt>$skip</tt>).
   *
   * Returns 0 if <tt>setFirstResult</tt> was not applied to the filter object.
   *
   * @return position of the first result
   */
  int getFirstResult();

  /**
   * Executes a <tt>$filter</tt> filter that returns a single result.
   *
   * @return the result
   * @throws NoResultException if there is no result
   * @throws NonUniqueResultException if more than one result
   */
  T getSingleResult() throws NoResultException, NonUniqueResultException;

  /**
   * Executes a <tt>$filter</tt> filter and return the filter results as collection.
   *
   * @return an iterable view of the results
   */
  EC getResult();
}
