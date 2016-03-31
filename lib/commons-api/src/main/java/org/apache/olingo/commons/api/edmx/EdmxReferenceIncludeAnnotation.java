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
package org.apache.olingo.commons.api.edmx;

/**
 * POJO for Edmx Reference Include Annotation.
 */
public class EdmxReferenceIncludeAnnotation {
  private final String termNamespace;
  private String qualifier;
  private String targetNamespace;

  /**
   * Create include annotation with given termNamespace and empty qualifier and targetNamespace.
   *
   * @param termNamespace of include annotation
   */
  public EdmxReferenceIncludeAnnotation(final String termNamespace) {
    this(termNamespace, null, null);
  }

  /**
   * Create include annotation with given termNamespace, qualifier and targetNamespace.
   *
   * @param termNamespace of include annotation
   * @param qualifier of include annotation
   * @param targetNamespace of include annotation
   */
  public EdmxReferenceIncludeAnnotation(final String termNamespace, final String qualifier,
      final String targetNamespace) {
    this.termNamespace = termNamespace;
    this.qualifier = qualifier;
    this.targetNamespace = targetNamespace;
  }

  /**
   * @return TermNamespace of the include annotation
   */
  public String getTermNamespace() {
    return termNamespace;
  }

  /**
   * @return Qualifier if one defined; null otherwise
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * Set qualifier for this include annotation.
   *
   * @param qualifier for include annotation
   * @return this include annotation
   */
  public EdmxReferenceIncludeAnnotation setQualifier(final String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  /**
   * @return targetNamespace if defined; null otherwise
   */
  public String getTargetNamespace() {
    return targetNamespace;
  }

  /**
   * Set target namespace for this include annotation.
   *
   * @param targetNamespace for include annotation
   * @return this include annotation
   */
  public EdmxReferenceIncludeAnnotation setTargetNamespace(final String targetNamespace) {
    this.targetNamespace = targetNamespace;
    return this;
  }
}