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
package org.apache.olingo.client.api.domain;

import java.util.Collection;

/**
 * OData collection property value.
 * 
 * @param <T> The actual ODataValue interface.
 */
public interface ClientCollectionValue<T extends ClientValue> extends ClientValue, Iterable<T> {

  /**
   * Adds a value to the collection.
   * 
   * @param value value to be added.
   */
  ClientCollectionValue<T> add(ClientValue value);

  /**
   * Checks if collection is empty.
   * 
   * @return 'TRUE' if empty; 'FALSE' otherwise.
   */
  boolean isEmpty();

  /**
   * Gets collection size.
   * 
   * @return collection size.
   */
  int size();

  /**
   * Converts this instance as POJO collection.
   * 
   * @return this instance as POJO collection
   */
  Collection<Object> asJavaCollection();
}
