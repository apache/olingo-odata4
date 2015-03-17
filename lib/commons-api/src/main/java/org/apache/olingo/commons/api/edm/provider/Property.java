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

public class Property extends AbstractEdmItem implements Named, Annotatable{
  
  private static final long serialVersionUID = -4224390853690843450L;

  private String name;

  private String type;

  private boolean collection;

  // TODO: Mimetype and mapping what here
  private String mimeType;

  private Mapping mapping;

  // Facets
  private String defaultValue;

  private boolean nullable = true;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  private boolean unicode = true;
  
  private SRID srid;

  private List<Annotation> annotations = new ArrayList<Annotation>();
  

  public String getName() {
    return name;
  }

  public Property setName(final String name) {
    this.name = name;
    return this;
  }

  public String getType() {
    return type;
  }

  public Property setType(final String type) {
    this.type = type;
    return this;
  }
  
  public FullQualifiedName getTypeAsFQNObject(){
    return new FullQualifiedName(type);
  }
  
  public Property setType(FullQualifiedName fqnName){
    this.type = fqnName.getFullQualifiedNameAsString();
    return this;
  }

  public boolean isCollection() {
    return collection;
  }

  public Property setCollection(final boolean isCollection) {
    collection = isCollection;
    return this;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public Property setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public boolean isNullable() {
    return nullable;
  }

  public Property setNullable(final boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public Property setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
    return this;
  }

  public Integer getPrecision() {
    return precision;
  }

  public Property setPrecision(final Integer precision) {
    this.precision = precision;
    return this;
  }

  public Integer getScale() {
    return scale;
  }

  public Property setScale(final Integer scale) {
    this.scale = scale;
    return this;
  }

  public boolean isUnicode() {
    return unicode;
  }

  public Property setUnicode(final boolean unicode) {
    this.unicode = unicode;
    return this;
  }

  public String getMimeType() {
    return mimeType;
  }

  public Property setMimeType(final String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  public Mapping getMapping() {
    return mapping;
  }

  public Property setMapping(final Mapping mapping) {
    this.mapping = mapping;
    return this;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }
  
  public Property setSrid(final SRID srid) {
    this.srid = srid;
    return this;
  }
  
  public SRID getSrid() {
    return srid;
  }
}
