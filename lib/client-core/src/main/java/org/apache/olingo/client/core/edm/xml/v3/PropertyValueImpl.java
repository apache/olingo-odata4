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
package org.apache.olingo.client.core.edm.xml.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.olingo.client.api.edm.xml.v3.PropertyValue;
import org.apache.olingo.client.core.edm.xml.AbstractEdmItem;

public class PropertyValueImpl extends AbstractEdmItem implements PropertyValue {

  private static final long serialVersionUID = -6580934436491418564L;

  @JsonProperty(value = "Property", required = true)
  private String property;

  @JsonProperty("Path")
  private String path;

  @JsonProperty("String")
  private String string;

  @JsonProperty("Int")
  private BigInteger _int;

  @JsonProperty("Float")
  private Double _float;

  @JsonProperty("Decimal")
  private BigDecimal decimal;

  @JsonProperty("Bool")
  private Boolean bool;

  @JsonProperty("DateTime")
  private Date dateTime;

  @Override
  public String getProperty() {
    return property;
  }

  public void setProperty(final String property) {
    this.property = property;
  }

  @Override
  public String getPath() {
    return path;
  }

  public void setPath(final String path) {
    this.path = path;
  }

  @Override
  public String getString() {
    return string;
  }

  public void setString(final String string) {
    this.string = string;
  }

  @Override
  public BigInteger getInt() {
    return _int;
  }

  public void setInt(final BigInteger _int) {
    this._int = _int;
  }

  @Override
  public Double getFloat() {
    return _float;
  }

  public void setFloat(final Double _float) {
    this._float = _float;
  }

  @Override
  public BigDecimal getDecimal() {
    return decimal;
  }

  public void setDecimal(final BigDecimal decimal) {
    this.decimal = decimal;
  }

  @Override
  public Boolean getBool() {
    return bool;
  }

  public void setBool(final Boolean bool) {
    this.bool = bool;
  }

  @Override
  public Date getDateTime() {
    return dateTime == null ? null : new Date(dateTime.getTime());
  }

  public void setDateTime(final Date dateTime) {
    this.dateTime = dateTime == null ? null : new Date(dateTime.getTime());
  }
}
