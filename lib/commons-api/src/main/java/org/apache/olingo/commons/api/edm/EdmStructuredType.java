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
package org.apache.olingo.commons.api.edm;

import java.util.List;

/**
 * EdmStructuralType is the base for a complex type or an entity type.
 *
 * Complex types and entity types are described in the Conceptual Schema Definition of the OData protocol.
 */
public interface EdmStructuredType extends EdmType {

  /**
   * Get property by name
   *
   * @param name
   * @return simple, complex or navigation property as {@link EdmTyped}
   */
  EdmElement getProperty(String name);

  /**
   * Get all simple and complex property names.
   *
   * @return property names as type List&lt;String&gt;
   */
  List<String> getPropertyNames();

  /**
   * Get structural property by name.
   *
   * @param name
   * @return simple or complex property as {@link EdmTyped}
   */
  EdmProperty getStructuralProperty(String name);

  /**
   * Get navigation property by name.
   *
   * @param name
   * @return navigation property as {@link EdmTyped}
   */
  EdmNavigationProperty getNavigationProperty(String name);

  /**
   * Get all navigation property names.
   *
   * @return navigation property names as type List&lt;String&gt;
   */
  List<String> getNavigationPropertyNames();

  /**
   * Base types are described in the OData protocol specification.
   *
   * @return {@link EdmStructuredType}
   */
  EdmStructuredType getBaseType();

  /**
   * Checks if this type is convertible to parameter {@link targetType}
   *
   * @param targetType
   * @return true if this type is compatible to the testType ( i.e. this type is a subtype of targetType )
   */
  boolean compatibleTo(EdmType targetType);

  /**
   * Indicates if the entity type is an open type.
   *
   * @return <code>true</code> if the entity type is open
   */
  boolean isOpenType();
}
