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
package org.apache.olingo.client.core.edm;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.CommonParameter;
import org.apache.olingo.client.api.edm.xml.v4.Action;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.edm.AbstractEdmOperation;

public abstract class EdmOperationImpl extends AbstractEdmOperation {

  protected final Action operation;

  protected static <T extends EdmOperationImpl> T getInstance(final T instance) {
    final List<? extends CommonParameter> parameters = instance.operation.getParameters();
    final List<EdmParameter> _parameters = new ArrayList<EdmParameter>(parameters.size());
    for (CommonParameter parameter : parameters) {
      _parameters.add(EdmParameterImpl.getInstance(instance.edm, parameter));
    }
    instance.setParameters(_parameters);

    instance.setEntitySetPath(instance.operation.getEntitySetPath());

    instance.setIsBound(instance.operation.isBound());

    if (instance.operation.getReturnType() != null) {
      instance.setReturnType(EdmReturnTypeImpl.getInstance(instance.edm, instance.operation.getReturnType()));
    }

    return instance;
  }

  protected EdmOperationImpl(final Edm edm, final FullQualifiedName name, final Action operation,
          final EdmTypeKind kind) {

    super(edm, name, kind);
    this.operation = operation;
  }
}
