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
package com.msopentech.odatajclient.engine.metadata.edm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(using = EnumTypeDeserializer.class)
public abstract class AbstractEnumType extends AbstractEdm {

    private static final long serialVersionUID = 2688487586103418210L;

    private String name;

    private String underlyingType;

    private boolean flags;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUnderlyingType() {
        return underlyingType;
    }

    public void setUnderlyingType(final String underlyingType) {
        this.underlyingType = underlyingType;
    }

    public boolean isFlags() {
        return flags;
    }

    public void setFlags(final boolean flags) {
        this.flags = flags;
    }

    public abstract List<? extends AbstractMember> getMembers();

    public abstract AbstractMember getMember(String name);

    public abstract AbstractMember getMember(Integer value);
}
