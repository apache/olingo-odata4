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
package org.apache.olingo.server.core.edmx;

import org.apache.olingo.server.api.edmx.EdmxReferenceIncludeAnnotation;

/**
 */
public class EdmxReferenceIncludeAnnotationImpl implements EdmxReferenceIncludeAnnotation {

  private final String termNamespace;
  private final String qualifier;
  private final String targetNamespace;

  public EdmxReferenceIncludeAnnotationImpl(String termNamespace, String qualifier, String targetNamespace) {
    this.termNamespace = termNamespace;
    this.qualifier = qualifier;
    this.targetNamespace = targetNamespace;
  }

  @Override
  public String getTermNamespace() {
    return termNamespace;
  }

  @Override
  public String getQualifier() {
    return qualifier;
  }

  @Override
  public String getTargetNamespace() {
    return targetNamespace;
  }
}
