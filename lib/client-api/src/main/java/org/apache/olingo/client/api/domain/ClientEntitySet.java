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

import java.net.URI;
import java.util.List;

/**
 * OData entity collection. If pagination was used to get this instance, forward page navigation URI will be available.
 */
public interface ClientEntitySet extends ClientInvokeResult, ClientAnnotatable {

  /**
   * Gets next page link.
   * 
   * @return next page link; null value if single page or last page reached.
   */
  URI getNext();

  /**
   * Gets contained entities.
   * 
   * @return entity set's entities.
   */
  List<ClientEntity> getEntities();

  /**
   * Gets in-line count.
   *
   * @return in-line count value.
   */
  Integer getCount();

  /**
   * Sets in-line count.
   *
   * @param count in-line count value.
   */
  void setCount(final int count);


  /**
   * Gets delta link if exists.
   *
   * @return delta link if exists; null otherwise.
   */
  URI getDeltaLink();

  /**
   * Sets delta link.
   *
   * @param deltaLink delta link.
   */
  void setDeltaLink(URI deltaLink);
  
  /**
   * Searches for operation with given title.
   * 
   * @param title operation to look for
   * @return operation if found with given title, <tt>null</tt> otherwise
   */
  ClientOperation getOperation(String title);

  /**
   * Gets operations.
   * 
   * @return operations.
   */
  List<ClientOperation> getOperations();  
}
