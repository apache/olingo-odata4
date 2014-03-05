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
package org.apache.olingo.odata4.client.core.edm.xml.v4;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.CommonParameter;
import org.apache.olingo.odata4.client.api.edm.xml.v4.Action;
import org.apache.olingo.odata4.client.api.edm.xml.v4.ReturnType;

@JsonDeserialize(using = ActionDeserializer.class)
public class ActionImpl extends AbstractAnnotatedEdmItem implements Action {

  private static final long serialVersionUID = -99977447455438193L;

  private String name;

  private boolean bound = false;

  private String entitySetPath;

  private final List<CommonParameter> parameters = new ArrayList<CommonParameter>();

  private ReturnTypeImpl returnType;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public boolean isBound() {
    return bound;
  }

  @Override
  public void setBound(final boolean bound) {
    this.bound = bound;
  }

  @Override
  public String getEntitySetPath() {
    return entitySetPath;
  }

  @Override
  public void setEntitySetPath(final String entitySetPath) {
    this.entitySetPath = entitySetPath;
  }

  @Override
  public CommonParameter getParameter(final String name) {
    return getOneByName(name, getParameters());
  }

  @Override
  public List<CommonParameter> getParameters() {
    return parameters;
  }

  @Override
  public ReturnTypeImpl getReturnType() {
    return returnType;
  }

  @Override
  public void setReturnType(final ReturnType returnType) {
    this.returnType = (ReturnTypeImpl) returnType;
  }

}
