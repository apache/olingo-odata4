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
package com.msopentech.odatajclient.engine.metadata.edm.v3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdm;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = AssociationSetDeserializer.class)
public class AssociationSet extends AbstractEdm {

    private static final long serialVersionUID = 1248430921598774799L;

    private String name;

    private String association;

    private List<AssociationSetEnd> ends = new ArrayList<AssociationSetEnd>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(final String association) {
        this.association = association;
    }

    public List<AssociationSetEnd> getEnds() {
        return ends;
    }

    public void setEnds(final List<AssociationSetEnd> ends) {
        this.ends = ends;
    }
}
