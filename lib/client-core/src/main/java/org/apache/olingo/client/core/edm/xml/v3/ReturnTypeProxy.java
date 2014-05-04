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

import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.api.edm.xml.v4.ReturnType;
import org.apache.olingo.commons.api.edm.geo.SRID;

public class ReturnTypeProxy implements ReturnType {

  private final FunctionImport functionImport;

  public ReturnTypeProxy(final FunctionImport functionImport) {
    this.functionImport = functionImport;
  }

  @Override
  public Integer getMaxLength() {
    return null;
  }

  @Override
  public Integer getPrecision() {
    return null;
  }

  @Override
  public Integer getScale() {
    return null;
  }

  @Override
  public SRID getSrid() {
    return null;
  }

  @Override
  public String getType() {
    return functionImport.getReturnType();
  }

  @Override
  public boolean isNullable() {
    return false;
  }

}
