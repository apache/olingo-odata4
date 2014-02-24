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
package org.apache.olingo.odata4.client.core.edm.v4;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.odata4.client.api.edm.v4.Annotation;
import org.apache.olingo.odata4.client.api.edm.v4.annotation.ConstExprConstruct;
import org.apache.olingo.odata4.client.api.edm.v4.annotation.DynExprConstruct;
import org.apache.olingo.odata4.client.core.edm.AbstractEdmItem;
import org.apache.olingo.odata4.client.core.edm.v4.annotation.ConstExprConstructImpl;
import org.apache.olingo.odata4.client.core.edm.v4.annotation.DynExprConstructImpl;

@JsonDeserialize(using = AnnotationDeserializer.class)
public class AnnotationImpl extends AbstractEdmItem implements Annotation {

  private static final long serialVersionUID = -5600031479702563436L;

  private String term;

  private String qualifier;

  private ConstExprConstructImpl constExpr;

  private DynExprConstructImpl dynExpr;

  @Override
  public String getTerm() {
    return term;
  }

  @Override
  public void setTerm(final String term) {
    this.term = term;
  }

  @Override
  public String getQualifier() {
    return qualifier;
  }

  @Override
  public void setQualifier(final String qualifier) {
    this.qualifier = qualifier;
  }

  @Override
  public ConstExprConstructImpl getConstExpr() {
    return constExpr;
  }

  @Override
  public void setConstExpr(final ConstExprConstruct constExpr) {
    this.constExpr = (ConstExprConstructImpl) constExpr;
  }

  @Override
  public DynExprConstructImpl getDynExpr() {
    return dynExpr;
  }

  @Override
  public void setDynExpr(final DynExprConstruct dynExpr) {
    this.dynExpr = (DynExprConstructImpl) dynExpr;
  }

}
