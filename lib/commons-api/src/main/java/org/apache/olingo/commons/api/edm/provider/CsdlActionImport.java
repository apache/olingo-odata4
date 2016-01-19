/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.api.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * Represents an action import CSDL item
 */
public class CsdlActionImport extends CsdlOperationImport {

  private FullQualifiedName action;

  @Override
  public CsdlActionImport setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public CsdlActionImport setEntitySet(final String entitySet) {
    this.entitySet = entitySet;
    return this;
  }

  /**
   * Returns the full qualified name of the action as string
   * @return full qualified name
   */
  public String getAction() {
    return action.getFullQualifiedNameAsString();
  }

  /**
   * Returns the full qualified name of the action
   * @return full qualified name
   */
  public FullQualifiedName getActionFQN() {
    return action;
  }

  /**
   * Sets the full qualified name of the action as string
   * @param action full qualified name
   * @return this instance
   */
  public CsdlActionImport setAction(final String action) {
    this.action = new FullQualifiedName(action);
    return this;
  }

  /**
   * Sets the full qualified name of the action
   * @param action full qualified name
   * @return this instance
   */
  public CsdlActionImport setAction(final FullQualifiedName action) {
    this.action = action;
    return this;
  }

  @Override
  public CsdlActionImport setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
