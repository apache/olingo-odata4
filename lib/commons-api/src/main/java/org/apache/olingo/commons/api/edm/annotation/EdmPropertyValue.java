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

import org.apache.olingo.commons.api.edm.EdmAnnotatable;

/**
 * The edm:PropertyValue element supplies a value to a property on the type instantiated by an 
 * edm:Record expression (See {@link EdmRecord}). The value is obtained by evaluating an expression.
 */
public interface EdmPropertyValue extends EdmAnnotatable {
  /**
   * Property name
   * @return Property name
   */
  String getProperty();
  
  /**
   * Evaluated value of the expression (property value)
   * @return evaluated value of the expression
   */
  EdmExpression getValue();

}
