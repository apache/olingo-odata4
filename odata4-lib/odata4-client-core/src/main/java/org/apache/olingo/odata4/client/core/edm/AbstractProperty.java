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
package org.apache.olingo.odata4.client.core.edm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import org.apache.olingo.odata4.client.api.edm.CommonProperty;
import org.apache.olingo.odata4.commons.api.edm.constants.ConcurrencyMode;
import org.apache.olingo.odata4.commons.api.edm.constants.StoreGeneratedPattern;

public abstract class AbstractProperty extends AbstractEdmItem implements CommonProperty {

  private static final long serialVersionUID = -6004492361142315153L;

  @JsonProperty(value = "Name", required = true)
  private String name;

  @JsonProperty(value = "Type", required = true)
  private String type;

  @JsonProperty(value = "Nullable")
  private boolean nullable = true;

  @JsonProperty(value = "DefaultValue")
  private String defaultValue;

  @JsonProperty(value = "MaxLength")
  private String maxLength;

  @JsonProperty(value = "FixedLength")
  private boolean fixedLength;

  @JsonProperty(value = "Precision")
  private BigInteger precision;

  @JsonProperty(value = "Scale")
  private BigInteger scale;

  @JsonProperty(value = "Unicode")
  private boolean unicode = true;

  @JsonProperty(value = "Collation")
  private String collation;

  @JsonProperty(value = "SRID")
  private String srid;

  @JsonProperty(value = "ConcurrencyMode")
  private ConcurrencyMode concurrencyMode;

  @JsonProperty("StoreGeneratedPattern")
  private StoreGeneratedPattern storeGeneratedPattern = StoreGeneratedPattern.None;

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
  public boolean isNullable() {
    return nullable;
  }

  @Override
  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
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
  public String getMaxLength() {
    return maxLength;
  }

  @Override
  public void setMaxLength(final String maxLength) {
    this.maxLength = maxLength;
  }

  @Override
  public boolean isFixedLength() {
    return fixedLength;
  }

  @Override
  public void setFixedLength(final boolean fixedLength) {
    this.fixedLength = fixedLength;
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
  public String getCollation() {
    return collation;
  }

  @Override
  public void setCollation(final String collation) {
    this.collation = collation;
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
  public ConcurrencyMode getConcurrencyMode() {
    return concurrencyMode;
  }

  @Override
  public void setConcurrencyMode(final ConcurrencyMode concurrencyMode) {
    this.concurrencyMode = concurrencyMode;
  }

  public StoreGeneratedPattern getStoreGeneratedPattern() {
    return storeGeneratedPattern;
  }

  public void setStoreGeneratedPattern(final StoreGeneratedPattern storeGeneratedPattern) {
    this.storeGeneratedPattern = storeGeneratedPattern;
  }
}
