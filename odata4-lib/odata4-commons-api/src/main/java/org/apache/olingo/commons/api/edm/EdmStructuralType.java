/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.commons.api.edm;

import java.util.List;

/**
 * EdmStructuralType is the base for a complex type or an entity type.
 * <p>Complex types and entity types are described in the Conceptual Schema Definition of the OData protocol.
 */
public interface EdmStructuralType extends EdmType {

  /**
   * Get property by name
   * @param name
   * @return simple, complex or navigation property as {@link EdmTyped}
   */
  EdmElement getProperty(String name);

  /**
   * Get all simple and complex property names
   * @return property names as type List<String>
   */
  List<String> getPropertyNames();

  /**
   * Get all navigation property names
   * @return navigation property names as type List<String>
   */
  List<String> getNavigationPropertyNames();

  /**
   * Base types are described in the OData protocol specification.
   * 
   * @return {@link EdmStructuralType}
   */
  EdmStructuralType getBaseType();
}