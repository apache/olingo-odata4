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
package org.apache.olingo.commons.api.edm.annotation;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotatable;
import org.apache.olingo.commons.api.edm.EdmStructuredType;

/**
 * The edm:Record expression enables a new entity type or complex type instance to be constructed.
 * A record expression contains zero or more edm:PropertyValue (See {@link EdmPropertyValue} )elements.
 */
public interface EdmRecord extends EdmDynamicExpression, EdmAnnotatable {
  
  /**
   * List of edm:PropertyValues (See {@link EdmPropertyValue}
   * @return List of edm:PropertyValues (See {@link EdmPropertyValue}
   */
  List<EdmPropertyValue> getPropertyValues();
  
  /**
   * Returns the entity type or complex type to be constructed.
   * @return Entity type or complex type
   */
  EdmStructuredType getType();
}
