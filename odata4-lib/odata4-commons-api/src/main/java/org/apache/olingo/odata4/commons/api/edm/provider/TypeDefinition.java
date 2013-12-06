/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.api.edm.provider;

import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;

//TODO: Finish
public class TypeDefinition {

  private String name;
  // UnderlyingType can only be primitve...
  private FullQualifiedName underlyingType;

  // Facets
  private Integer maxLength;
  private Integer precision;
  private Integer scale;
  private Boolean isUnicode;

  // Annotations

  public String getName() {
    return name;
  }

  public TypeDefinition setName(final String name) {
    this.name = name;
    return this;
  }

  public FullQualifiedName getUnderlyingType() {
    return underlyingType;
  }

  public TypeDefinition setUnderlyingType(final FullQualifiedName underlyingType) {
    this.underlyingType = underlyingType;
    return this;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public TypeDefinition setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public Integer getPrecision() {
    return precision;
  }

  public TypeDefinition setPrecision(final Integer precision) {
    this.precision = precision;
    return this;
  }

  public Integer getScale() {
    return scale;
  }

  public TypeDefinition setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  public Boolean getIsUnicode() {
    return isUnicode;
  }

  public TypeDefinition setIsUnicode(final Boolean isUnicode) {
    this.isUnicode = isUnicode;
    return this;
  }

}
