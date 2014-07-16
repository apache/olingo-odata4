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
package org.apache.olingo.server.api.edm.provider;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

import java.util.List;

public class Term {

  private String name;

  private FullQualifiedName type;

  private FullQualifiedName baseTerm;

  // TODO: AppliesTo is a list of csdl elements => should we put this list inside an enum?
  private String appliesTo;

  private boolean isCollection;

  // Facets
  private String defaultValue;

  private Boolean nullable;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  // Annotation
  private List<Annotation> annotations;

  public String getName() {
    return name;
  }

  public Term setName(final String name) {
    this.name = name;
    return this;
  }

  public FullQualifiedName getType() {
    return type;
  }

  public Term setType(final FullQualifiedName type) {
    this.type = type;
    return this;
  }

  public FullQualifiedName getBaseTerm() {
    return baseTerm;
  }

  public Term setBaseTerm(final FullQualifiedName baseTerm) {
    this.baseTerm = baseTerm;
    return this;
  }

  public String getAppliesTo() {
    return appliesTo;
  }

  public Term setAppliesTo(final String appliesTo) {
    this.appliesTo = appliesTo;
    return this;
  }

  public boolean isCollection() {
    return isCollection;
  }

  public Term setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public Term setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public Boolean getNullable() {
    return nullable;
  }

  public Term setNullable(final Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public Term setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public Integer getPrecision() {
    return precision;
  }

  public Term setPrecision(final Integer precision) {
    this.precision = precision;
    return this;
  }

  public Integer getScale() {
    return scale;
  }

  public Term setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public Term setAnnotations(final List<Annotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
