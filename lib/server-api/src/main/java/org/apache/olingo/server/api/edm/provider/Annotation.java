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
package org.apache.olingo.server.api.edm.provider;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

import java.util.List;

public class Annotation {

  private FullQualifiedName term;

  // Target should be a target path
//  private String targetPath;
  private String qualifier;

  private Expression expression;

  private List<Annotation> annotation;

  public FullQualifiedName getTerm() {
    return term;
  }

  public Annotation setTerm(final FullQualifiedName term) {
    this.term = term;
    return this;
  }

  public String getQualifier() {
    return qualifier;
  }

  public Annotation setQualifier(final String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  public Expression getExpression() {
    return expression;
  }

  public Annotation setExpression(final Expression expression) {
    this.expression = expression;
    return this;
  }

  public List<Annotation> getAnnotation() {
    return annotation;
  }

  public Annotation setAnnotation(final List<Annotation> annotation) {
    this.annotation = annotation;
    return this;
  }
}
