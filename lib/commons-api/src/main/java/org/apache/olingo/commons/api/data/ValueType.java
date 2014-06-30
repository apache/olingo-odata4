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

public enum ValueType {
  PRIMITIVE, GEOSPATIAL, ENUM, COMPLEX, LINKED_COMPLEX,
  COLLECTION_PRIMITIVE(PRIMITIVE),
  COLLECTION_GEOSPATIAL(GEOSPATIAL),
  COLLECTION_ENUM(ENUM),
  COLLECTION_COMPLEX(COMPLEX),
  COLLECTION_LINKED_COMPLEX(LINKED_COMPLEX);

  private final ValueType baseType;

  private ValueType() {
    baseType = this;
  }

  private ValueType(final ValueType baseType) {
    this.baseType = baseType;
  }

  public ValueType getBaseType() {
    return baseType;
  }
}
