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
 * The type Csdl function import.
 */
public class CsdlFunctionImport extends CsdlOperationImport {

  private FullQualifiedName function;

  // Default include in service document is false for function imports
  private boolean includeInServiceDocument;

  /**
   * Humanreadable title
   */
  private String title;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public CsdlFunctionImport setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public CsdlFunctionImport setEntitySet(final String entitySet) {
    this.entitySet = entitySet;
    return this;
  }

  /**
   * Gets function.
   *
   * @return the function
   */
  public String getFunction() {
    return function.getFullQualifiedNameAsString();
  }

  /**
   * Gets function fQN.
   *
   * @return the function fQN
   */
  public FullQualifiedName getFunctionFQN() {
    return function;
  }

  /**
   * Sets function.
   *
   * @param function the function
   * @return the function
   */
  public CsdlFunctionImport setFunction(final FullQualifiedName function) {
    this.function = function;
    return this;
  }

  /**
   * Sets function.
   *
   * @param function the function
   * @return the function
   */
  public CsdlFunctionImport setFunction(final String function) {
    this.function = new FullQualifiedName(function);
    return this;
  }

  /**
   * Is include in service document.
   *
   * @return the boolean
   */
  public boolean isIncludeInServiceDocument() {
    return includeInServiceDocument;
  }

  /**
   * Sets include in service document.
   *
   * @param includeInServiceDocument the include in service document
   * @return the include in service document
   */
  public CsdlFunctionImport setIncludeInServiceDocument(final boolean includeInServiceDocument) {
    this.includeInServiceDocument = includeInServiceDocument;
    return this;
  }

  @Override
  public CsdlFunctionImport setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
  
  public String getTitle() {
    return title;
  }

  /**
   * A human readable title for this instance
   * @param title
   * @return this instance
   */
  public CsdlFunctionImport setTitle(String title) {
    this.title = title;
    return this;
  }
}
