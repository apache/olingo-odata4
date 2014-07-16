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
package org.apache.olingo.client.core.edm.v3;

import org.apache.olingo.client.api.edm.xml.CommonParameter;
import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.api.edm.xml.v3.Parameter;
import org.apache.olingo.client.core.edm.EdmParameterImpl;
import org.apache.olingo.client.core.edm.EdmReturnTypeImpl;
import org.apache.olingo.client.core.edm.xml.v3.ReturnTypeProxy;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.edm.AbstractEdmOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EdmOperationProxy extends AbstractEdmOperation {

  protected final FunctionImport functionImport;

  protected static <T extends EdmOperationProxy> T getInstance(final T instance) {
    final List<Parameter> parameters = instance.functionImport.getParameters();
    final List<EdmParameter> _parameters = new ArrayList<EdmParameter>(parameters.size());
    for (CommonParameter parameter : parameters) {
      _parameters.add(new EdmParameterImpl(instance.edm, parameter));
    }
    instance.setParameters(_parameters);

    instance.setEntitySetPath(instance.functionImport.getEntitySetPath());

    instance.setIsBound(instance.functionImport.isBindable());

    if (instance.functionImport.getReturnType() != null) {
      instance.setReturnType(EdmReturnTypeImpl.getInstance(instance.edm, new ReturnTypeProxy(instance.functionImport)));
    }

    return instance;
  }

  protected EdmOperationProxy(final Edm edm, final FullQualifiedName fqn, final FunctionImport functionImport,
          final EdmTypeKind kind) {

    super(edm, fqn, kind);
    this.functionImport = functionImport;
  }

  @Override
  public FullQualifiedName getBindingParameterTypeFqn() {
    return getParameterNames().isEmpty()
            ? null
            : getParameter(getParameterNames().get(0)).getType().getFullQualifiedName();
  }

  @Override
  public Boolean isBindingParameterTypeCollection() {
    return getParameterNames().isEmpty()
            ? false
            : getParameter(getParameterNames().get(0)).isCollection();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return null;
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return Collections.<EdmAnnotation>emptyList();
  }

}
