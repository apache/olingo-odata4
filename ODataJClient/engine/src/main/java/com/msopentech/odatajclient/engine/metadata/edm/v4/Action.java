/**
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
package com.msopentech.odatajclient.engine.metadata.edm.v4;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ActionDeserializer.class)
public class Action extends AbstractAnnotatedEdm {

    private static final long serialVersionUID = -99977447455438193L;

    private String name;

    private boolean bound = false;

    private String entitySetPath;

    private final List<Parameter> parameters = new ArrayList<Parameter>();

    private ReturnType returnType;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isBound() {
        return bound;
    }

    public void setBound(final boolean bound) {
        this.bound = bound;
    }

    public String getEntitySetPath() {
        return entitySetPath;
    }

    public void setEntitySetPath(final String entitySetPath) {
        this.entitySetPath = entitySetPath;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Parameter getParameter(final String name) {
        Parameter result = null;
        for (Parameter parameter : getParameters()) {
            if (name.equals(parameter.getName())) {
                result = parameter;
            }
        }
        return result;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(final ReturnType returnType) {
        this.returnType = returnType;
    }

}
