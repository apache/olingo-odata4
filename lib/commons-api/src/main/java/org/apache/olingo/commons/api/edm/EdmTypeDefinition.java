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
package org.apache.olingo.commons.api.edm;

import org.apache.olingo.commons.api.edm.geo.SRID;

/**
 * An {@link EdmTypeDefinition} defines a specialization of one of the possible primitive types.
 * <br/>
 * For more information on primitive types refer to {@link EdmPrimitiveType}.
 */
public interface EdmTypeDefinition extends EdmPrimitiveType, EdmAnnotatable {

  /**
   * @return {@link EdmPrimitiveType} this type definition is based upon
   */
  EdmPrimitiveType getUnderlyingType();

  /**
   * @return the maximum length as an Integer or null if not specified
   */
  Integer getMaxLength();

  /**
   * @return the precision as an Integer or null if not specified
   */
  Integer getPrecision();

  /**
   * @return the scale as an Integer or null if not specified
   */
  Integer getScale();

  /**
   * @return a non-negative integer or the special value <tt>variable</tt>
   */
  SRID getSrid();

  /**
   * @return true if unicode or null if not specified
   */
  Boolean isUnicode();
}
