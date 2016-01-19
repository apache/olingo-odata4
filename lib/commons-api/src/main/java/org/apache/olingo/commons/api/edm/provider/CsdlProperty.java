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
 * The type Csdl property.
 */
public class CsdlProperty extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  private String name;

  private String type;

  private boolean collection;

  private String mimeType;

  private CsdlMapping mapping;

  // Facets
  private String defaultValue;

  private boolean nullable = true;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  private boolean unicode = true;

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
  public CsdlProperty setName(final String name) {
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
  public CsdlProperty setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Gets type as fQN object.
   *
   * @return the type as fQN object
   */
  public FullQualifiedName getTypeAsFQNObject() {
    return new FullQualifiedName(type);
  }

  /**
   * Sets type.
   *
   * @param fqnName the fqn name
   * @return the type
   */
  public CsdlProperty setType(final FullQualifiedName fqnName) {
    type = fqnName.getFullQualifiedNameAsString();
    return this;
  }

  /**
   * Is collection.
   *
   * @return the boolean
   */
  public boolean isCollection() {
    return collection;
  }

  /**
   * Sets collection.
   *
   * @param isCollection the is collection
   * @return the collection
   */
  public CsdlProperty setCollection(final boolean isCollection) {
    collection = isCollection;
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
  public CsdlProperty setDefaultValue(final String defaultValue) {
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
  public CsdlProperty setNullable(final boolean nullable) {
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
  public CsdlProperty setMaxLength(final Integer maxLength) {
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
  public CsdlProperty setPrecision(final Integer precision) {
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
  public CsdlProperty setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  /**
   * Is unicode.
   *
   * @return the boolean
   */
  public boolean isUnicode() {
    return unicode;
  }

  /**
   * Sets unicode.
   *
   * @param unicode the unicode
   * @return the unicode
   */
  public CsdlProperty setUnicode(final boolean unicode) {
    this.unicode = unicode;
    return this;
  }

  /**
   * Gets mime type.
   *
   * @return the mime type
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Sets mime type.
   *
   * @param mimeType the mime type
   * @return the mime type
   */
  public CsdlProperty setMimeType(final String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  /**
   * Gets mapping.
   *
   * @return the mapping
   */
  public CsdlMapping getMapping() {
    return mapping;
  }

  /**
   * Sets mapping.
   *
   * @param mapping the mapping
   * @return the mapping
   */
  public CsdlProperty setMapping(final CsdlMapping mapping) {
    this.mapping = mapping;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  /**
   * Sets a list of annotations
   * @param annotations list of annotations
   * @return this instance
   */
  public CsdlProperty setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
  
  /**
   * Sets srid.
   *
   * @param srid the srid
   * @return the srid
   */
  public CsdlProperty setSrid(final SRID srid) {
    this.srid = srid;
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
}
