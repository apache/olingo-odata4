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
package org.apache.olingo.client.core.edm.xml.v4;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.client.api.edm.xml.v4.Action;
import org.apache.olingo.client.api.edm.xml.v4.Parameter;
import org.apache.olingo.client.api.edm.xml.v4.ReturnType;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ActionDeserializer.class)
public class ActionImpl extends AbstractAnnotatable implements Action {

  private static final long serialVersionUID = 5321541275349234088L;

  private String name;

  private boolean bound = false;

  private String entitySetPath;

  private final List<Parameter> parameters = new ArrayList<Parameter>();

  private ReturnType returnType;

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public boolean isBound() {
    return bound;
  }

  public void setBound(final boolean bound) {
    this.bound = bound;
  }

  @Override
  public String getEntitySetPath() {
    return entitySetPath;
  }

  public void setEntitySetPath(final String entitySetPath) {
    this.entitySetPath = entitySetPath;
  }

  @Override
  public Parameter getParameter(final String name) {
    return getOneByName(name, getParameters());
  }

  @Override
  public List<Parameter> getParameters() {
    return parameters;
  }

  @Override
  public ReturnType getReturnType() {
    return returnType;
  }

  public void setReturnType(final ReturnType returnType) {
    this.returnType = returnType;
  }

}
