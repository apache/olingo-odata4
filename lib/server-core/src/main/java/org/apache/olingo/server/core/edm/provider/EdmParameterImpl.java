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
package org.apache.olingo.server.core.edm.provider;

import java.util.List;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.core.edm.AbstractEdmParameter;
import org.apache.olingo.server.api.edm.provider.Parameter;

public class EdmParameterImpl extends AbstractEdmParameter {

  private final Parameter parameter;

  public EdmParameterImpl(final Edm edm, final Parameter parameter) {
    super(edm, parameter.getName(), parameter.getType());
    this.parameter = parameter;
  }

  @Override
  public boolean isCollection() {
    return parameter.isCollection();
  }

  @Override
  public EdmMapping getMapping() {
    return parameter.getMapping();
  }

  @Override
  public Boolean isNullable() {
    return parameter.getNullable();
  }

  @Override
  public Integer getMaxLength() {
    return parameter.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return parameter.getPrecision();
  }

  @Override
  public Integer getScale() {
    return parameter.getScale();
  }

  @Override
  public SRID getSrid() {
    return null; // TODO: provide implementation  
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  @Override
  public List<EdmAnnotation> getAnnotations() {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
