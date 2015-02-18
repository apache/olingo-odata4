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
package org.apache.olingo.client.core.edm.xml.annotation;

import org.apache.olingo.client.api.edm.xml.annotation.Cast;
import org.apache.olingo.client.api.edm.xml.annotation.DynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.geo.SRID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CastDeserializer.class)
public class CastImpl extends AbstractAnnotatableDynamicAnnotationExpression implements Cast {

  private static final long serialVersionUID = 3312415984116005313L;

  private String type;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  private SRID srid;

  private DynamicAnnotationExpression value;

  @Override
  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public Integer getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
  }

  @Override
  public Integer getPrecision() {
    return precision;
  }

  public void setPrecision(final Integer precision) {
    this.precision = precision;
  }

  @Override
  public Integer getScale() {
    return scale;
  }

  public void setScale(final Integer scale) {
    this.scale = scale;
  }

  @Override
  public SRID getSrid() {
    return srid;
  }

  public void setSrid(final SRID srid) {
    this.srid = srid;
  }

  @Override
  public DynamicAnnotationExpression getValue() {
    return value;
  }

  public void setValue(final DynamicAnnotationExpression value) {
    this.value = value;
  }

}
