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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
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

public abstract class AbstractEdmOperation extends EdmTypeImpl implements EdmOperation {

    private final Map<String, EdmParameter> parameters = new LinkedHashMap<String, EdmParameter>();

    private String entitySetPath;

    private boolean isBound;

    private EdmReturnType returnType;

    private List<String> parameterNames;

    protected AbstractEdmOperation(
            final Edm edm,
            final FullQualifiedName fqn,
            final EdmTypeKind kind) {
        super(edm, fqn, kind);
    }

    protected void setParameters(final List<EdmParameter> _parameters) {
        for (EdmParameter parameter : _parameters) {
            parameters.put(parameter.getName(), parameter);
        }
    }

    protected void setEntitySetPath(final String entitySetPath) {
        this.entitySetPath = entitySetPath;
    }

    protected void setIsBound(final boolean isBound) {
        this.isBound = isBound;
    }

    protected void setReturnType(final EdmReturnType returnType) {
        this.returnType = returnType;
    }

    @Override
    public EdmParameter getParameter(final String name) {
        return parameters.get(name);
    }

    @Override
    public List<String> getParameterNames() {
        if (parameterNames == null) {
            parameterNames = new ArrayList<String>(parameters.size());
            for (String parameterName : parameters.keySet()) {
                parameterNames.add(parameterName);
            }
        }
        return parameterNames;
    }

    @Override
    public EdmEntitySet getReturnedEntitySet(final EdmEntitySet bindingParameterEntitySet) {
        EdmEntitySet returnedEntitySet = null;
        if (bindingParameterEntitySet != null && entitySetPath != null) {
            final EdmBindingTarget relatedBindingTarget = bindingParameterEntitySet.
                    getRelatedBindingTarget(entitySetPath);
            if (relatedBindingTarget == null) {
                throw new EdmException("Cannot find entity set with path: " + entitySetPath);
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
        return returnType;
    }

    @Override
    public boolean isBound() {
        return isBound;
    }
}
