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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.api.edm.xml.v3.Parameter;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = FunctionImportDeserializer.class)
public class FunctionImportImpl implements FunctionImport {

  private String name;

  private String returnType;

  private String entitySet;

  private String entitySetPath;

  private boolean composable;

  private boolean sideEffecting = true;

  private boolean bindable;

  private boolean alwaysBindable;

  private String httpMethod;

  private final List<Parameter> parameters = new ArrayList<Parameter>();

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(final String returnType) {
    this.returnType = returnType;
  }

  @Override
  public String getEntitySet() {
    return entitySet;
  }

  public void setEntitySet(final String entitySet) {
    this.entitySet = entitySet;
  }

  @Override
  public String getEntitySetPath() {
    return entitySetPath;
  }

  public void setEntitySetPath(final String entitySetPath) {
    this.entitySetPath = entitySetPath;
  }

  @Override
  public boolean isComposable() {
    return composable;
  }

  public void setComposable(final boolean composable) {
    this.composable = composable;
  }

  @Override
  public boolean isSideEffecting() {
    return sideEffecting;
  }

  public void setSideEffecting(final boolean sideEffecting) {
    this.sideEffecting = sideEffecting;
  }

  @Override
  public boolean isBindable() {
    return bindable;
  }

  public void setBindable(final boolean bindable) {
    this.bindable = bindable;
  }

  @Override
  public boolean isAlwaysBindable() {
    return alwaysBindable;
  }

  public void setAlwaysBindable(final boolean alwaysBindable) {
    this.alwaysBindable = alwaysBindable;
  }

  @Override
  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(final String httpMethod) {
    this.httpMethod = httpMethod;
  }

  @Override
  public List<Parameter> getParameters() {
    return parameters;
  }

}
