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
package org.apache.olingo.client.api.data;

import java.net.URI;
import java.util.List;

/**
 * REST resource for an <tt>ODataServiceDocument</tt>.
 *
 * @see org.apache.olingo.client.api.domain.ClientServiceDocument
 */
public interface ServiceDocument {

  String getTitle();

  /**
   * Gets base URI.
   *
   * @return base URI.
   */
  URI getBaseURI();

  /**
   * Gets top level entity sets.
   *
   * @return top level entity sets.
   */
  List<ServiceDocumentItem> getEntitySets();

  /**
   * Gets top level entity set with given name.
   *
   * @param name entity set name
   * @return entity set with given name if found, otherwise null
   */
  ServiceDocumentItem getEntitySetByName(String name);

  /**
   * Gets top level function imports.
   *
   * @return top level function imports.
   */
  List<ServiceDocumentItem> getFunctionImports();

  /**
   * Gets top level function import set with given name.
   *
   * @param name function import name
   * @return function import with given name if found, otherwise null
   */
  ServiceDocumentItem getFunctionImportByName(String name);

  /**
   * Gets top level singletons.
   *
   * @return top level singletons.
   */
  List<ServiceDocumentItem> getSingletons();

  /**
   * Gets top level singleton with given name.
   *
   * @param name singleton name
   * @return singleton with given name if found, otherwise null
   */
  ServiceDocumentItem getSingletonByName(String name);

  /**
   * Gets related service documents.
   *
   * @return related service documents.
   */
  List<ServiceDocumentItem> getRelatedServiceDocuments();
}
