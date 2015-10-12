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

import java.util.List;

import org.apache.olingo.commons.api.edm.geo.SRID;

/**
 * An {@link EdmTerm} defines a term in a vocabulary.
 */
public interface EdmTerm extends EdmNamed, EdmAnnotatable {

  /**
   * @return type of value returned by the expression contained in an annotation using this term
   */
  EdmType getType();

  /**
   * @return the fully qualified name of this term
   */
  FullQualifiedName getFullQualifiedName();

  /**
   * When applying a term with a base term,the base term MUST also be applied with the same qualifier, and so on until a
   * term without a base term is reached.
   *
   * @return the base term if found or null otherwise
   */
  EdmTerm getBaseTerm();

  /**
   * @return list of CSDL element that this term can be applied to; if no value is supplied, the term is not restricted
   * in its application.
   */
  List<TargetType> getAppliesTo();

  /**
   * @return true if nullable
   */
  boolean isNullable();

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
   * @return the default value as a String or null if not specified
   */
  String getDefaultValue();

}
