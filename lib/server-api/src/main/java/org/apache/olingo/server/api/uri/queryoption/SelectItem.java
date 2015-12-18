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
package org.apache.olingo.server.api.uri.queryoption;

import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.uri.UriInfoResource;

/**
 * Represents a single select item information
 * For example: http://.../Employees?select=name,age
 */
public interface SelectItem {

  /**
   * @return A star is used as select item
   */
  boolean isStar();

  /**
   * @return Namespace and star is used as select item in order to select operations
   */
  boolean isAllOperationsInSchema();

  /**
   * @return Namespace when a star is used in combination with an namespace
   */
  FullQualifiedName getAllOperationsInSchemaNameSpace();

  /**
   * @return A {@link UriInfoResource} object containing the resource path segments to be selected
   */
  UriInfoResource getResourcePath();

  /**
   * @return Before resource path segments which should be selected a type filter may be used.
   * For example: ...Suppliers?$select=Namespace.PreferredSupplier/AccountRepresentative
   */
  EdmType getStartTypeFilter();

}
