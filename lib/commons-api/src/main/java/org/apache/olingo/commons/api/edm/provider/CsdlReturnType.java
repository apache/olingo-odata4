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

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;

/**
 * The type Csdl return type.
 */
public class CsdlReturnType extends CsdlAbstractEdmItem implements CsdlAnnotatable {

  private String type;

  private boolean isCollection;

  // facets
  private boolean nullable = true;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  private SRID srid;
  
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  /**
   * Gets type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Gets type fQN.
   *
   * @return the type fQN
   */
  public FullQualifiedName getTypeFQN() {
    return new FullQualifiedName(type);
  }

  /**
   * Sets type.
   *
   * @param type the type
   * @return the type
   */
  public CsdlReturnType setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Sets type.
   *
   * @param type the type
   * @return the type
   */
  public CsdlReturnType setType(final FullQualifiedName type) {
    this.type = type.getFullQualifiedNameAsString();
    return this;
  }

  /**
   * Is collection.
   *
   * @return the boolean
   */
  public boolean isCollection() {
    return isCollection;
  }

  /**
   * Sets collection.
   *
   * @param isCollection the is collection
   * @return the collection
   */
  public CsdlReturnType setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
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
  public CsdlReturnType setNullable(final boolean nullable) {
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
  public CsdlReturnType setMaxLength(final Integer maxLength) {
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
  public CsdlReturnType setPrecision(final Integer precision) {
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
  public CsdlReturnType setScale(final Integer scale) {
    this.scale = scale;
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
  public CsdlReturnType setSrid(final SRID srid) {
    this.srid = srid;
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
  public CsdlReturnType setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
