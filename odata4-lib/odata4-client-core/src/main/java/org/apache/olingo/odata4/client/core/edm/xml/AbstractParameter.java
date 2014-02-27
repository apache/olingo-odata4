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
package org.apache.olingo.odata4.client.core.edm.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import org.apache.olingo.odata4.client.api.edm.xml.CommonParameter;

public abstract class AbstractParameter extends AbstractEdmItem implements CommonParameter {

  private static final long serialVersionUID = -4305016554930334342L;

  @JsonProperty(value = "Name", required = true)
  private String name;

  @JsonProperty(value = "Type", required = true)
  private String type;

  @JsonProperty(value = "Nullable")
  private boolean nullable = true;

  @JsonProperty("MaxLength")
  private String maxLength;

  @JsonProperty("Precision")
  private BigInteger precision;

  @JsonProperty("Scale")
  private BigInteger scale;

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
}
