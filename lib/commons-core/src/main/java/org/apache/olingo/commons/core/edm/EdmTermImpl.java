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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.TargetType;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;

public class EdmTermImpl extends AbstractEdmNamed implements EdmTerm {

  private final CsdlTerm term;
  private final FullQualifiedName fqn;
  private EdmType termType;
  private EdmTerm baseTerm;
  private List<TargetType> appliesTo;

  public EdmTermImpl(final Edm edm, final String namespace, final CsdlTerm term) {
    super(edm, term.getName(), term);
    this.term = term;
    fqn = new FullQualifiedName(namespace, term.getName());
  }

  @Override
  public FullQualifiedName getFullQualifiedName() {
    return fqn;
  }

  @Override
  public EdmType getType() {
    if (termType == null) {
      if (term.getType() == null) {
        throw new EdmException("Terms must hava a full qualified type.");
      }
      termType = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(term.getType()).build().getType();
      if (termType == null) {
        throw new EdmException("Cannot find type with name: " + term.getType());
      }
    }
    return termType;
  }

  @Override
  public EdmTerm getBaseTerm() {
    if (baseTerm == null && term.getBaseTerm() != null) {
      baseTerm = edm.getTerm(new FullQualifiedName(term.getBaseTerm()));
    }
    return baseTerm;
  }

  @Override
  public List<TargetType> getAppliesTo() {
    if (appliesTo == null) {
      ArrayList<TargetType> localAppliesTo = new ArrayList<TargetType>();
      for (String apply : term.getAppliesTo()) {
        try {
          localAppliesTo.add(TargetType.valueOf(apply));
        } catch (IllegalArgumentException e) {
          throw new EdmException("Invalid AppliesTo value: " + apply, e);
        }
      }
      appliesTo = Collections.unmodifiableList(localAppliesTo);
    }
    return appliesTo;
  }

  @Override
  public boolean isNullable() {
    return term.isNullable();
  }

  @Override
  public Integer getMaxLength() {
    return term.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return term.getPrecision();
  }

  @Override
  public Integer getScale() {
    return term.getScale();
  }

  @Override
  public SRID getSrid() {
    return term.getSrid();
  }

  @Override
  public String getDefaultValue() {
    return term.getDefaultValue();
  }
}
