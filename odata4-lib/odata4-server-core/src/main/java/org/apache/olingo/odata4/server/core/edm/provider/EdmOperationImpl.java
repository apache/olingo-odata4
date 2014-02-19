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
package org.apache.olingo.odata4.server.core.edm.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata4.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmOperation;
import org.apache.olingo.odata4.commons.api.edm.EdmParameter;
import org.apache.olingo.odata4.commons.api.edm.EdmReturnType;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.odata4.server.api.edm.provider.EntitySetPath;
import org.apache.olingo.odata4.server.api.edm.provider.Operation;
import org.apache.olingo.odata4.server.api.edm.provider.Parameter;

public class EdmOperationImpl extends EdmTypeImpl implements EdmOperation {

  private final Map<String, EdmParameter> parameters = new HashMap<String, EdmParameter>();
  private final Operation operation;
  private EdmReturnType returnType;
  private List<String> parameterNames;

  public EdmOperationImpl(final EdmProviderImpl edm, final FullQualifiedName name, final Operation operation,
      final EdmTypeKind kind) {
    super(edm, name, kind);
    this.operation = operation;
    List<Parameter> providerParameters = operation.getParameters();
    if (providerParameters != null) {
      for (Parameter parameter : providerParameters) {
        parameters.put(parameter.getName(), new EdmParameterImpl(edm, parameter));
      }
    }
  }

  @Override
  public EdmParameter getParameter(final String name) {
    return parameters.get(name);
  }

  @Override
  public List<String> getParameterNames() {
    if (parameterNames == null) {
      parameterNames = new ArrayList<String>();
      List<Parameter> providerParameters = operation.getParameters();
      if (providerParameters != null) {
        for (Parameter parameter : providerParameters) {
          parameterNames.add(parameter.getName());
        }
      }
    }
    return parameterNames;
  }

  @Override
  public EdmEntitySet getReturnedEntitySet(final EdmEntitySet bindingParameterEntitySet) {
    EntitySetPath entitySetPath = operation.getEntitySetPath();
    EdmEntitySet returnedEntitySet = null;
    if (bindingParameterEntitySet != null && entitySetPath != null && entitySetPath.getBindingParameter() != null
        && entitySetPath.getPath() != null) {
      EdmBindingTarget relatedBindingTarget =
          bindingParameterEntitySet.getRelatedBindingTarget(entitySetPath.getPath());
      if (relatedBindingTarget == null) {
        throw new EdmException("Cannot find entity set with path: " + entitySetPath.getPath());
      }
      if (relatedBindingTarget instanceof EdmEntitySet) {
        returnedEntitySet = (EdmEntitySet) relatedBindingTarget;
      } else {
        throw new EdmException("BindingTarget with name: " + relatedBindingTarget.getName() + " must be an entity set");
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

}
