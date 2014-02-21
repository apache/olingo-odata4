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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import org.apache.olingo.odata4.client.api.edm.v4.ReturnType;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmItem;

public class ReturnTypeImpl extends AbstractEdmItem implements ReturnType {

  private static final long serialVersionUID = -5888231162358116515L;

  @JsonProperty(value = "Type")
  private String type;

  @JsonProperty(value = "Nullable")
  private boolean nullable = true;

  @JsonProperty(value = "MaxLength")
  private String maxLength;

  @JsonProperty(value = "Precision")
  private BigInteger precision;

  @JsonProperty(value = "Scale")
  private BigInteger scale;

  @JsonProperty(value = "SRID")
  private String srid;

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }

  public String getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(final String maxLength) {
    this.maxLength = maxLength;
  }

  public BigInteger getPrecision() {
    return precision;
  }

  public void setPrecision(final BigInteger precision) {
    this.precision = precision;
  }

  public BigInteger getScale() {
    return scale;
  }

  public void setScale(final BigInteger scale) {
    this.scale = scale;
  }

  public String getSrid() {
    return srid;
  }

  public void setSrid(final String srid) {
    this.srid = srid;
  }

}
