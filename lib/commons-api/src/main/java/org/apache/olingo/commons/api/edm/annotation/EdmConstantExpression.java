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

import org.apache.olingo.commons.api.edm.geo.Geospatial;

/**
 * Represents a constant expression
 */
public interface EdmConstantExpression extends EdmExpression {

  // TODO: Is methods

  /**
   * The value object of this expression or null if it is of type enum or geospatial.
   * @return a value object or null
   */
  Object asPrimitive();

  /**
   * A list of enum members or empty list if this expression is of type primitive or geospatial.
   * @return a list of all enum members or empty list
   */
  List<String> asEnumMembers();

  /**
   * Return the Enum type name or null if this expression is of type primitive or geospatial.
   * @return enum type name or null
   */
  String getEnumTypeName();

  /**
   * Return the geospatial object or null if this expression is of type primitive or enum.
   * @return geospatial object or null
   */
  Geospatial asGeospatial();

  /**
   * Returns the value of the expression as String.
   * @return String representation of the expression
   */
  String getValueAsString();
}
