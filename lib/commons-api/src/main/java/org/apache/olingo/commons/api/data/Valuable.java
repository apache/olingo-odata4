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

  boolean isPrimitive();

  boolean isGeospatial();

  boolean isEnum();

  boolean isComplex();

  boolean isLinkedComplex();

  boolean isCollection();

  Object getValue();

  Object asPrimitive();

  Object asEnum();

  Geospatial asGeospatial();

  List<Property> asComplex();

  LinkedComplexValue asLinkedComplex();

  List<?> asCollection();

  void setValue(ValueType valuetype, Object value);

  ValueType getValueType();
}
