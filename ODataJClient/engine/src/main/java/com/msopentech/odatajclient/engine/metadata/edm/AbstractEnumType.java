/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
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
