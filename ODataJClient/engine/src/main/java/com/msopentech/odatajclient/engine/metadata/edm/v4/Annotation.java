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
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdm;
import com.msopentech.odatajclient.engine.metadata.edm.v4.annotation.ConstExprConstruct;
import com.msopentech.odatajclient.engine.metadata.edm.v4.annotation.DynExprConstruct;

@JsonDeserialize(using = AnnotationDeserializer.class)
public class Annotation extends AbstractEdm {

    private static final long serialVersionUID = -5600031479702563436L;

    private String term;

    private String qualifier;

    private ConstExprConstruct constExpr;

    private DynExprConstruct dynExpr;

    public String getTerm() {
        return term;
    }

    public void setTerm(final String term) {
        this.term = term;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public ConstExprConstruct getConstExpr() {
        return constExpr;
    }

    public void setConstExpr(final ConstExprConstruct constExpr) {
        this.constExpr = constExpr;
    }

    public DynExprConstruct getDynExpr() {
        return dynExpr;
    }

    public void setDynExpr(final DynExprConstruct dynExpr) {
        this.dynExpr = dynExpr;
    }

}
