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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.v4.CSDLElement;
import org.apache.olingo.odata4.client.api.edm.v4.Term;

@JsonDeserialize(using = TermDeserializer.class)
public class TermImpl extends AbstractAnnotatedEdmItem implements Term {

  private static final long serialVersionUID = -5888231162358116515L;

  private String name;

  private String type;

  private String baseTerm;

  private String defaultValue;

  private boolean nullable = true;

  private String maxLength;

  private BigInteger precision;

  private BigInteger scale;

  private String srid;

  private final List<CSDLElement> appliesTo = new ArrayList<CSDLElement>();

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public String getBaseTerm() {
    return baseTerm;
  }

  @Override
  public void setBaseTerm(final String baseTerm) {
    this.baseTerm = baseTerm;
  }

  @Override
  public String getDefaultValue() {
    return defaultValue;
  }

  @Override
  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  public boolean isNullable() {
    return nullable;
  }

  @Override
  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
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
  public String getSrid() {
    return srid;
  }

  @Override
  public void setSrid(final String srid) {
    this.srid = srid;
  }

  @Override
  public List<CSDLElement> getAppliesTo() {
    return appliesTo;
  }

}
