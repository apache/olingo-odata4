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

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.geo.SRID;

/**
 * The type Csdl term.
 */
public class CsdlTerm extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  private String name;

  private String type;

  private String baseTerm;

  private List<String> appliesTo = new ArrayList<String>();

  // Facets
  private String defaultValue;

  private boolean nullable = true;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  private SRID srid;

  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   * @return the name
   */
  public CsdlTerm setName(final String name) {
    this.name = name;
    return this;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   * @return the type
   */
  public CsdlTerm setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Gets base term.
   *
   * @return the base term
   */
  public String getBaseTerm() {
    return baseTerm;
  }

  /**
   * Sets base term.
   *
   * @param baseTerm the base term
   * @return the base term
   */
  public CsdlTerm setBaseTerm(final String baseTerm) {
    this.baseTerm = baseTerm;
    return this;
  }

  /**
   * Gets applies to.
   *
   * @return the applies to
   */
  public List<String> getAppliesTo() {
    return appliesTo;
  }

  /**
   * Sets applies to.
   *
   * @param appliesTo the applies to
   * @return the applies to
   */
  public CsdlTerm setAppliesTo(final List<String> appliesTo) {
    this.appliesTo = appliesTo;
    return this;
  }

  /**
   * Gets default value.
   *
   * @return the default value
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Sets default value.
   *
   * @param defaultValue the default value
   * @return the default value
   */
  public CsdlTerm setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  /**
   * Is nullable.
   *
   * @return the boolean
   */
  public boolean isNullable() {
    return nullable;
  }

  /**
   * Sets nullable.
   *
   * @param nullable the nullable
   * @return the nullable
   */
  public CsdlTerm setNullable(final boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  /**
   * Gets max length.
   *
   * @return the max length
   */
  public Integer getMaxLength() {
    return maxLength;
  }

  /**
   * Sets max length.
   *
   * @param maxLength the max length
   * @return the max length
   */
  public CsdlTerm setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  /**
   * Gets precision.
   *
   * @return the precision
   */
  public Integer getPrecision() {
    return precision;
  }

  /**
   * Sets precision.
   *
   * @param precision the precision
   * @return the precision
   */
  public CsdlTerm setPrecision(final Integer precision) {
    this.precision = precision;
    return this;
  }

  /**
   * Gets scale.
   *
   * @return the scale
   */
  public Integer getScale() {
    return scale;
  }

  /**
   * Sets scale.
   *
   * @param scale the scale
   * @return the scale
   */
  public CsdlTerm setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  /**
   * Sets annotations.
   *
   * @param annotations the annotations
   * @return the annotations
   */
  public CsdlTerm setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  /**
   * Gets srid.
   *
   * @return the srid
   */
  public SRID getSrid() {
    return srid;
  }

  /**
   * Sets srid.
   *
   * @param srid the srid
   * @return the srid
   */
  public CsdlTerm setSrid(final SRID srid) {
    this.srid = srid;
    return this;
  }
}
