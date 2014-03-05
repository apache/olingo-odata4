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
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmParameter;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.odata4.commons.core.edm.AbstractEdmOperation;
import org.apache.olingo.odata4.server.api.edm.provider.EntitySetPath;
import org.apache.olingo.odata4.server.api.edm.provider.Operation;
import org.apache.olingo.odata4.server.api.edm.provider.Parameter;

public abstract class EdmOperationImpl extends AbstractEdmOperation {

  protected final Operation operation;

  protected static <T extends EdmOperationImpl> T getInstance(final T instance) {
    final List<Parameter> providerParameters = instance.operation.getParameters();
    if (providerParameters != null) {
      final List<EdmParameter> _parameters = new ArrayList<EdmParameter>(providerParameters.size());
      for (Parameter parameter : providerParameters) {
        _parameters.add(new EdmParameterImpl(instance.edm, parameter));
      }
      instance.setParameters(_parameters);
    }

    final EntitySetPath entitySetPath = instance.operation.getEntitySetPath();
    if (entitySetPath != null && entitySetPath.getPath() != null) {
      instance.setEntitySetPath(entitySetPath.getPath());
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
  }
}
