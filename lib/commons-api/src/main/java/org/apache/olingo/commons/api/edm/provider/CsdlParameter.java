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

public class CsdlParameter extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  private static final long serialVersionUID = -7360900923880732015L;

  private String name;

  private FullQualifiedName type;

  private boolean isCollection;

  private CsdlMapping mapping;

  // Facets
  private boolean nullable = true;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  private SRID srid;

  private final List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public String getName() {
    return name;
  }

  public CsdlParameter setName(final String name) {
    this.name = name;
    return this;
  }

  public String getType() {
    return type.getFullQualifiedNameAsString();
  }

  public FullQualifiedName getTypeFQN() {
    return type;
  }

  public CsdlParameter setType(final String type) {
    this.type = new FullQualifiedName(type);
    return this;
  }

  public CsdlParameter setType(final FullQualifiedName type) {
    this.type = type;
    return this;
  }

  public boolean isCollection() {
    return isCollection;
  }

  public CsdlParameter setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  public boolean isNullable() {
    return nullable;
  }

  public CsdlParameter setNullable(final boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public CsdlParameter setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public Integer getPrecision() {
    return precision;
  }

  public CsdlParameter setPrecision(final Integer precision) {
    this.precision = precision;
    return this;
  }

  public Integer getScale() {
    return scale;
  }

  public CsdlParameter setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  public SRID getSrid() {
    return srid;
  }

  public CsdlParameter setSrid(final SRID srid) {
    this.srid = srid;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  public CsdlMapping getMapping() {
    return mapping;
  }

  public CsdlParameter setMapping(final CsdlMapping mapping) {
    this.mapping = mapping;
    return this;
  }
}
