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

import org.apache.olingo.client.api.uri.URISearch;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface used to control search execution.
 *
 * @param <T> search result type
 * @param <EC>
 */
public interface Search<T extends EntityType<?>, EC extends Collection<T>> extends Serializable {

  /**
   * Sets the <tt>$search</tt> expression for this search.
   * <br/>
   * Any of available operators and functions can be embodied here.
   *
   * @param search the <tt>$search</tt> expression for this search
   * @return the same search instance
   */
  Search<T, EC> setSearch(String search);

  /**
   * Sets the search generating the <tt>$search</tt> expression for this search.
   *
   * @param search search instance (to be obtained via <tt>ODataSearchFactory</tt>): note that <tt>build()</tt> method
   * will be immediately invoked.
   * @return the same search instance
   */
  Search<T, EC> setSearch(URISearch search);

  /**
   * The <tt>$search</tt> expression for this search.
   *
   * @return the <tt>$search</tt> expression for this search
   */
  String getSearch();

  /**
   * Executes a <tt>$search</tt> search and return the search results as collection.
   *
   * @return an iterable view of the results
   */
  EC getResult();
}
