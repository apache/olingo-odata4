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
package org.apache.olingo.client.core.edm.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.provider.Annotation;
import org.apache.olingo.commons.api.edm.provider.annotation.AnnotationExpression;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = AnnotationDeserializer.class)
public class AnnotationImpl extends Annotation {

  private static final long serialVersionUID = 5464714417411058033L;

  private String term;

  private String qualifier;

  private AnnotationExpression annotationExpression;

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }
  
  @Override
  public String getTerm() {
    return term;
  }

  public AnnotationImpl setTerm(final String term) {
    this.term = term;
    return this;
  }

  @Override
  public String getQualifier() {
    return qualifier;
  }

  public AnnotationImpl setQualifier(final String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  @Override
  public AnnotationExpression getExpression() {
    return annotationExpression;
  }

  public void setAnnotationExpression(final AnnotationExpression annotationExpression) {
    this.annotationExpression = annotationExpression;
  }

}
