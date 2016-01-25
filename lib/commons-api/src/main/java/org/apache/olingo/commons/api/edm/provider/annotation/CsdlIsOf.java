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
 * The edm:IsOf expression evaluates a child expression and returns a Boolean value indicating whether
 * the child expression returns the specified type
 */
public class CsdlIsOf extends CsdlDynamicExpression implements CsdlAnnotatable {

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
  
  public CsdlIsOf setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
  /**
   * The type which is checked again the child expression
   * @return EdmType type
   */
  public String getType() {
    return type;
  }

  public CsdlIsOf setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Facet MaxLength
   * @return fact MaxLength
   */
  public Integer getMaxLength() {
    return maxLength;
  }

  public CsdlIsOf setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  /**
   * Facet Precision
   * @return fact Precision
   */
  public Integer getPrecision() {
    return precision;
  }

  public CsdlIsOf setPrecision(final Integer precision) {
    this.precision = precision;
return this;
  }

  /**
   * Facet Scale
   * @return facet Scale
   */
  public Integer getScale() {
    return scale;
  }

  public CsdlIsOf setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  /**
   * Facet SRID
   * @return facet SRID
   */
  public SRID getSrid() {
    return srid;
  }

  public CsdlIsOf setSrid(final SRID srid) {
    this.srid = srid;
    return this;
  }

  /**
   * Returns the child expression
   * @return Returns the child expression
   */
  public CsdlExpression getValue() {
    return value;
  }

  public CsdlIsOf setValue(final CsdlExpression value) {
    this.value = value;
    return this;
  }

}
