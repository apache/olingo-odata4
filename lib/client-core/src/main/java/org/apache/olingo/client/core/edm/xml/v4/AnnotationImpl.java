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
import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.annotation.AnnotationExpression;

@JsonDeserialize(using = AnnotationDeserializer.class)
public class AnnotationImpl extends AbstractAnnotatable implements Annotation {

  private static final long serialVersionUID = -5600031479702563436L;

  private String term;

  private String qualifier;

  private AnnotationExpression annotationExpression;

  @Override
  public String getTerm() {
    return term;
  }

  public void setTerm(final String term) {
    this.term = term;
  }

  @Override
  public String getQualifier() {
    return qualifier;
  }

  public void setQualifier(final String qualifier) {
    this.qualifier = qualifier;
  }

  @Override
  public AnnotationExpression getExpression() {
    return annotationExpression;
  }

  public void setAnnotationExpression(final AnnotationExpression annotationExpression) {
    this.annotationExpression = annotationExpression;
  }

}
