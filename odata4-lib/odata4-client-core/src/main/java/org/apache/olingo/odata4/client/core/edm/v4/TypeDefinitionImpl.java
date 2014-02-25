/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.client.core.edm.v4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.v4.TypeDefinition;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmItem;

public class TypeDefinitionImpl extends AbstractEdmItem implements TypeDefinition {

  private static final long serialVersionUID = -5888231162358116515L;

  private String name;

  private String underlyingType;

  private String maxLength;

  private BigInteger precision;

  private BigInteger scale;

  private boolean unicode = true;

  private String srid;

  private final List<AnnotationImpl> annotations = new ArrayList<AnnotationImpl>();

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getUnderlyingType() {
    return underlyingType;
  }

  @Override
  public void setUnderlyingType(final String underlyingType) {
    this.underlyingType = underlyingType;
  }

  @Override
  public String getMaxLength() {
    return maxLength;
  }

  @Override
  public void setMaxLength(final String maxLength) {
    this.maxLength = maxLength;
  }

  @Override
  public BigInteger getPrecision() {
    return precision;
  }

  @Override
  public void setPrecision(final BigInteger precision) {
    this.precision = precision;
  }

  @Override
  public BigInteger getScale() {
    return scale;
  }

  @Override
  public void setScale(final BigInteger scale) {
    this.scale = scale;
  }

  @Override
  public boolean isUnicode() {
    return unicode;
  }

  @Override
  public void setUnicode(final boolean unicode) {
    this.unicode = unicode;
  }

  @Override
  public String getSrid() {
    return srid;
  }

  @Override
  public void setSrid(final String srid) {
    this.srid = srid;
  }

  @Override
  public List<AnnotationImpl> getAnnotations() {
    return annotations;
  }

}
