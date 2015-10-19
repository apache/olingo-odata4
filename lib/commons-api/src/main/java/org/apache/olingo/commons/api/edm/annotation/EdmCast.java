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
 * Represents an edm:Cast expression.
 * Casts the value obtained from its single child expression to the specified type
 */
public interface EdmCast extends EdmDynamicExpression, EdmAnnotatable {
  /**
   * Returns the facet attribute MaxLength
   * @return Returns the facet attribute MaxLength
   */
  Integer getMaxLength();
  
  /**
   * Returns the facet attribute Precision
   * @return Returns the facet attribute Precision
   */
  Integer getPrecision();
  
  /**
   * Returns the facet attribute Scale
   * @return Returns the facet attribute Scale
   */
  Integer getScale();

  /**
   * Returns the facet attribute SRID
   * @return Returns the facet attribute SRID
   */
  SRID getSrid();

  /**
   * Value cast to
   * @return value cast to
   */
  EdmType getType();
  
  /**
   * Cast value of the expression
   * @return Cast value
   */
  EdmExpression getValue();
}
