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
package org.apache.olingo.commons.api.data;

import org.apache.olingo.commons.api.edm.geo.Geospatial;

import java.util.List;

public interface Valuable {

  String getType();

  void setType(String type);

  boolean isNull();

  /**
   * Check if Valuable contains a PRIMITIVE or COLLECTION_PRIMITIVE ValueType
   *
   * @return true if ValueType is a PRIMITIVE or COLLECTION_PRIMITIVE, otherwise false
   */
  boolean isPrimitive();

  /**
   * Check if Valuable contains a GEOSPATIAL or COLLECTION_GEOSPATIAL ValueType
   *
   * @return true if ValueType is a GEOSPATIAL or COLLECTION_GEOSPATIAL, otherwise false
   */
  boolean isGeospatial();

  /**
   * Check if Valuable contains a ENUM or COLLECTION_ENUM ValueType
   *
   * @return true if ValueType is a ENUM or COLLECTION_ENUM, otherwise false
   */
  boolean isEnum();

  /**
   * Check if Valuable contains a COMPLEX or COLLECTION_COMPLEX ValueType
   *
   * @return true if ValueType is a COMPLEX or COLLECTION_COMPLEX, otherwise false
   */
  boolean isComplex();

  /**
   * Check if Valuable contains a COLLECTION_* ValueType
   *
   * @return true if ValueType is a COLLECTION_*, otherwise false
   */
  boolean isCollection();

  /**
   * Get the value
   *
   * @return the value
   */
  Object getValue();

  /**
   * Get the value in its primitive representation or null if it is not based on a primitive ValueType
   *
   * @return primitive representation or null if it is not based on a primitive ValueType
   */
  Object asPrimitive();

  /**
   * Get the value in its enum representation or null if it is not based on a enum ValueType
   *
   * @return enum representation or null if it is not based on a enum ValueType
   */
  Object asEnum();

  /**
   * Get the value in its geospatial representation or null if it is not based on a geospatial ValueType
   *
   * @return geospatial representation or null if it is not based on a geospatial ValueType
   */
  Geospatial asGeospatial();

  /**
   * Get the value in its complex representation or null if it is not based on a complex ValueType
   *
   * @return primitive complex or null if it is not based on a complex ValueType
   */
  ComplexValue asComplex();

  /**
   * Get the value as collection or null if it is not a collection ValueType
   *
   * @return collection or null if it is not a collection ValueType
   */
  List<?> asCollection();

  void setValue(ValueType valuetype, Object value);

  ValueType getValueType();
}
