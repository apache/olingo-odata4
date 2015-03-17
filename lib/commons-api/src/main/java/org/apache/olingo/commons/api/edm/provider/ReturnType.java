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
package org.apache.olingo.commons.api.edm.provider;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;

public class ReturnType extends AbstractEdmItem{

  private static final long serialVersionUID = 4816954124986010965L;

  private FullQualifiedName type;

  private boolean isCollection;

  // facets
  private boolean nullable = true;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;
  
  private SRID srid;

  public String getType() {
    return type.getFullQualifiedNameAsString();
  }
  
  public FullQualifiedName getTypeFQN() {
    return type;
  }

  public ReturnType setType(final String type) {
    this.type = new FullQualifiedName(type);
    return this;
  }
  
  public ReturnType setType(final FullQualifiedName type) {
    this.type = type;
    return this;
  }

  public boolean isCollection() {
    return isCollection;
  }

  public ReturnType setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  public boolean isNullable() {
    return nullable;
  }

  public ReturnType setNullable(final boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public ReturnType setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public Integer getPrecision() {
    return precision;
  }

  public ReturnType setPrecision(final Integer precision) {
    this.precision = precision;
    return this;
  }

  public Integer getScale() {
    return scale;
  }

  public ReturnType setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  public SRID getSrid() {
    return srid;
  }

  public ReturnType setSrid(final SRID srid) {
    this.srid = srid;
    return this;
  }
}
