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
import org.apache.olingo.odata4.client.api.edm.v4.ReferentialConstraint;
import org.apache.olingo.odata4.client.core.edm.AbstractNavigationProperty;

@JsonDeserialize(using = NavigationPropertyDeserializer.class)
public class NavigationPropertyImpl extends AbstractNavigationProperty implements AnnotatedEdmItem {

  private static final long serialVersionUID = -2889417442815563307L;

  private String type;

  private boolean nullable = true;

  private String partner;

  private boolean containsTarget = false;

  private final List<ReferentialConstraint> referentialConstraints = new ArrayList<ReferentialConstraint>();

  private OnDeleteImpl onDelete;

  private Annotation annotation;

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }

  public String getPartner() {
    return partner;
  }

  public void setPartner(final String partner) {
    this.partner = partner;
  }

  public boolean isContainsTarget() {
    return containsTarget;
  }

  public void setContainsTarget(final boolean containsTarget) {
    this.containsTarget = containsTarget;
  }

  public List<ReferentialConstraint> getReferentialConstraints() {
    return referentialConstraints;
  }

  public OnDeleteImpl getOnDelete() {
    return onDelete;
  }

  public void setOnDelete(final OnDeleteImpl onDelete) {
    this.onDelete = onDelete;
  }

  @Override
  public Annotation getAnnotation() {
    return annotation;
  }

  @Override
  public void setAnnotation(final Annotation annotation) {
    this.annotation = annotation;
  }

}
