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
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.SRID;

/**
 *  The edm:IsOf expression evaluates a child expression and returns a Boolean value indicating whether 
 *  the child expression returns the specified type
 */
public interface EdmIsOf extends EdmDynamicExpression, EdmAnnotatable {
 
  /**
   * Facet MaxLength
   * @return fact MaxLength
   */
  Integer getMaxLength();

  /**
   * Facet Precision
   * @return fact Precision
   */
  Integer getPrecision();
  
  /**
   * Facet Scale
   * @return facet Scale
   */
  Integer getScale();
  
  /**
   * Facet SRID
   * @return facet SRID
   */
  SRID getSrid();
  
  /**
   * The type which is checked again the child expression
   * @return EdmType
   */
  EdmType getType();
  
  /**
   * Returns true if the child expression returns the specified typed 
   * @return Returns true if the child expression returns the specified typed 
   */
  EdmExpression getValue();
}
