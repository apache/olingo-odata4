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

import org.apache.olingo.odata4.client.core.edm.OnDeleteImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.v4.Annotation;
import org.apache.olingo.odata4.client.api.edm.v4.NavigationProperty;
import org.apache.olingo.odata4.client.api.edm.OnDelete;
import org.apache.olingo.odata4.client.core.edm.AbstractNavigationProperty;

@JsonDeserialize(using = NavigationPropertyDeserializer.class)
public class NavigationPropertyImpl extends AbstractNavigationProperty implements NavigationProperty {

  private static final long serialVersionUID = -2889417442815563307L;

  private String type;

  private boolean nullable = true;

  private String partner;

  private boolean containsTarget = false;

  private final List<ReferentialConstraintImpl> referentialConstraints = new ArrayList<ReferentialConstraintImpl>();

  private OnDeleteImpl onDelete;

  private AnnotationImpl annotation;

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public boolean isNullable() {
    return nullable;
  }

  @Override
  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }

  @Override
  public String getPartner() {
    return partner;
  }

  @Override
  public void setPartner(final String partner) {
    this.partner = partner;
  }

  @Override
  public boolean isContainsTarget() {
    return containsTarget;
  }

  @Override
  public void setContainsTarget(final boolean containsTarget) {
    this.containsTarget = containsTarget;
  }

  @Override
  public List<ReferentialConstraintImpl> getReferentialConstraints() {
    return referentialConstraints;
  }

  @Override
  public OnDeleteImpl getOnDelete() {
    return onDelete;
  }

  @Override
  public void setOnDelete(final OnDelete onDelete) {
    this.onDelete = (OnDeleteImpl) onDelete;
  }

  @Override
  public AnnotationImpl getAnnotation() {
    return annotation;
  }

  @Override
  public void setAnnotation(final Annotation annotation) {
    this.annotation = (AnnotationImpl) annotation;
  }

}
