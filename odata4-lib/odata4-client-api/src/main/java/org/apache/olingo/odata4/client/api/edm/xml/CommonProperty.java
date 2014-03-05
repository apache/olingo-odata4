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
package org.apache.olingo.odata4.client.api.edm.xml;

import org.apache.olingo.odata4.commons.api.edm.constants.ConcurrencyMode;
import org.apache.olingo.odata4.commons.api.edm.constants.StoreGeneratedPattern;

public interface CommonProperty extends Named {

  String getType();

  void setType(String type);

  boolean isNullable();

  void setNullable(boolean nullable);

  String getDefaultValue();

  void setDefaultValue(String defaultValue);

  Integer getMaxLength();

  void setMaxLength(Integer maxLength);

  boolean isFixedLength();

  void setFixedLength(boolean fixedLength);

  Integer getPrecision();

  void setPrecision(Integer precision);

  Integer getScale();

  void setScale(Integer scale);

  boolean isUnicode();

  void setUnicode(boolean unicode);

  String getCollation();

  void setCollation(String collation);

  String getSrid();

  void setSrid(String srid);

  ConcurrencyMode getConcurrencyMode();

  void setConcurrencyMode(ConcurrencyMode concurrencyMode);

  StoreGeneratedPattern getStoreGeneratedPattern();

  void setStoreGeneratedPattern(StoreGeneratedPattern storeGeneratedPattern);
}
