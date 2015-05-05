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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlOperation;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;

public abstract class AbstractEdmOperation extends EdmTypeImpl implements EdmOperation {

  private final CsdlOperation operation;
  private Map<String, EdmParameter> parameters;
  private List<String> parameterNames;
  private EdmReturnType returnType;

  protected AbstractEdmOperation(final Edm edm, final FullQualifiedName name, final CsdlOperation operation,
      final EdmTypeKind kind) {

    super(edm, name, kind, operation);
    this.operation = operation;
  }

  @Override
  public EdmParameter getParameter(final String name) {
    if (parameters == null) {
      createParameters();
    }
    return parameters.get(name);
  }

  @Override
  public List<String> getParameterNames() {
    if (parameterNames == null) {
      createParameters();
    }
    return Collections.unmodifiableList(parameterNames);
  }

  private void createParameters() {
    if (parameters == null) {
      final Map<String, EdmParameter> parametersLocal = new LinkedHashMap<String, EdmParameter>();
      final List<CsdlParameter> providerParameters = operation.getParameters();
      if (providerParameters != null) {
        final List<String> parameterNamesLocal = new ArrayList<String>(providerParameters.size());
        for (CsdlParameter parameter : providerParameters) {
          parametersLocal.put(parameter.getName(), new EdmParameterImpl(edm, parameter));
          parameterNamesLocal.add(parameter.getName());
        }

        parameters = parametersLocal;
        parameterNames = parameterNamesLocal;
      } else {
        parameterNames = Collections.emptyList();
      }
    }
  }

  @Override
  public EdmEntitySet getReturnedEntitySet(final EdmEntitySet bindingParameterEntitySet) {
    EdmEntitySet returnedEntitySet = null;
    if (bindingParameterEntitySet != null && operation.getEntitySetPath() != null) {
      final EdmBindingTarget relatedBindingTarget =
          bindingParameterEntitySet.getRelatedBindingTarget(operation.getEntitySetPath());
      if (relatedBindingTarget == null) {
        throw new EdmException("Cannot find entity set with path: " + operation.getEntitySetPath());
      }
      if (relatedBindingTarget instanceof EdmEntitySet) {
        returnedEntitySet = (EdmEntitySet) relatedBindingTarget;
      } else {
        throw new EdmException("BindingTarget with name: " + relatedBindingTarget.getName()
            + " must be an entity set");
      }
    }
    return returnedEntitySet;
  }

  @Override
  public EdmReturnType getReturnType() {
    if (returnType == null && operation.getReturnType() != null) {
      returnType = new EdmReturnTypeImpl(edm, operation.getReturnType());
    }
    return returnType;
  }

  @Override
  public boolean isBound() {
    return operation.isBound();
  }

  @Override
  public FullQualifiedName getBindingParameterTypeFqn() {
    if (isBound()) {
      CsdlParameter bindingParameter = operation.getParameters().get(0);
      return bindingParameter.getTypeFQN();
    }
    return null;
  }

  @Override
  public Boolean isBindingParameterTypeCollection() {
    if (isBound()) {
      CsdlParameter bindingParameter = operation.getParameters().get(0);
      return bindingParameter.isCollection();
    }
    return null;
  }

  @Override
  public String getEntitySetPath() {
    return operation.getEntitySetPath();
  }
}
