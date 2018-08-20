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
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlCast)) {
      return false;
    }
    CsdlCast csdlCast = (CsdlCast) obj;
    return (this.getValue() == null ? csdlCast.getValue() == null :
      this.getValue().equals(csdlCast.getValue()))
        && (this.getType() == null ? csdlCast.getType() == null :
        this.getType().equals(csdlCast.getType()))
        && (this.getMaxLength() == null ? csdlCast.getMaxLength() == null :
          this.getMaxLength().equals(csdlCast.getMaxLength()))
        && (this.getPrecision() == null ? csdlCast.getPrecision() == null :
          this.getPrecision().equals(csdlCast.getPrecision()))
        && (this.getScale() == null ? csdlCast.getScale() == null :
         this.getScale().equals(csdlCast.getScale()))
        && (this.getSrid() == null ? csdlCast.getSrid() == null :
          String.valueOf(this.getSrid()).equals(String.valueOf(csdlCast.getSrid())))
        && (this.getAnnotations() == null ? csdlCast.getAnnotations() == null :
          checkAnnotations(csdlCast.getAnnotations()));
  }
  
  private boolean checkAnnotations(List<CsdlAnnotation> csdlCastAnnotations) {
    if (csdlCastAnnotations == null) {
      return false;
    }
    if (this.getAnnotations().size() == csdlCastAnnotations.size()) {
      for (int i = 0; i < this.getAnnotations().size(); i++) {
        if (!this.getAnnotations().get(i).equals(csdlCastAnnotations.get(i))) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((maxLength == null) ? 0 : maxLength.hashCode());
    result = prime * result + ((precision == null) ? 0 : precision.hashCode());
    result = prime * result + ((scale == null) ? 0 : scale.hashCode());
    result = prime * result + ((srid == null) ? 0 : srid.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
    return result;
  }
}
