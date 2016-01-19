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
package org.apache.olingo.commons.api.edm.provider.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;

/**
 * Represents an edm:Cast expression.
 * Casts the value obtained from its single child expression to the specified type
 */
public class CsdlCast extends CsdlDynamicExpression implements CsdlAnnotatable {

  private String type;
  private Integer maxLength;
  private Integer precision;
  private Integer scale;
  private SRID srid;
  private CsdlExpression value;
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  public CsdlCast setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  /**
   * Value cast to
   * @return value cast to
   */
  public String getType() {
    return type;
  }

  public CsdlCast setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Returns the facet attribute MaxLength
   * @return Returns the facet attribute MaxLength
   */
  public Integer getMaxLength() {
    return maxLength;
  }

  public CsdlCast setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  /**
   * Returns the facet attribute Precision
   * @return Returns the facet attribute Precision
   */
  public Integer getPrecision() {
    return precision;
  }

  public CsdlCast setPrecision(final Integer precision) {
    this.precision = precision;
    return this;
  }

  /**
   * Returns the facet attribute Scale
   * @return Returns the facet attribute Scale
   */
  public Integer getScale() {
    return scale;
  }

  public CsdlCast setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  /**
   * Returns the facet attribute SRID
   * @return Returns the facet attribute SRID
   */
  public SRID getSrid() {
    return srid;
  }

  public CsdlCast setSrid(final SRID srid) {
    this.srid = srid;
    return this;
  }

  /**
   * Cast value of the expression
   * @return Cast value
   */
  public CsdlExpression getValue() {
    return value;
  }

  public CsdlCast setValue(final CsdlExpression value) {
    this.value = value;
    return this;
  }
}
