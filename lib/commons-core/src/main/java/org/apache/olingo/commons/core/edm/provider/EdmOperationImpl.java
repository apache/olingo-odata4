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
package org.apache.olingo.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.Operation;
import org.apache.olingo.commons.api.edm.provider.Parameter;
import org.apache.olingo.commons.core.edm.AbstractEdmOperation;
import org.apache.olingo.commons.core.edm.EdmAnnotationHelper;

public abstract class EdmOperationImpl extends AbstractEdmOperation {

  protected final Operation operation;
  protected final EdmAnnotationHelper helper;

  protected static <T extends EdmOperationImpl> T getInstance(final T instance) {
    final List<Parameter> providerParameters = instance.operation.getParameters();
    if (providerParameters != null) {
      final List<EdmParameter> _parameters = new ArrayList<EdmParameter>(providerParameters.size());
      for (Parameter parameter : providerParameters) {
        _parameters.add(new EdmParameterImpl(instance.edm, parameter));
      }
      instance.setParameters(_parameters);
    }

    final String entitySetPath = instance.operation.getEntitySetPath();
    if (entitySetPath != null) {
      instance.setEntitySetPath(entitySetPath);
    }

    instance.setIsBound(instance.operation.isBound());

    if (instance.operation.getReturnType() != null) {
      instance.setReturnType(new EdmReturnTypeImpl(instance.edm, instance.operation.getReturnType()));
    }

    return instance;
  }

  protected EdmOperationImpl(final Edm edm, final FullQualifiedName name, final Operation operation,
      final EdmTypeKind kind) {

    super(edm, name, kind);
    this.operation = operation;
    this.helper = new EdmAnnotationHelperImpl(edm, operation);
  }

  @Override
  public FullQualifiedName getBindingParameterTypeFqn() {
    if (isBound()) {
      Parameter bindingParameter = operation.getParameters().get(0);
      return bindingParameter.getTypeFQN();
    }
    return null;
  }

  @Override
  public Boolean isBindingParameterTypeCollection() {
    if (isBound()) {
      Parameter bindingParameter = operation.getParameters().get(0);
      return bindingParameter.isCollection();
    }
    return null;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper.getAnnotations();
  }
}
