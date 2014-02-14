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

@JsonDeserialize(using = EdmxDeserializer.class)
public abstract class AbstractEdmx<DS extends AbstractDataServices<S, EC, E, C, FI>, S extends AbstractSchema<
        EC, E, C, FI>, EC extends AbstractEntityContainer<
        FI>, E extends AbstractEntityType, C extends AbstractComplexType, FI extends AbstractFunctionImport>
        extends AbstractEdm {

    private static final long serialVersionUID = -5480835122183091469L;

    private String version;

    private DS dataServices;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public DS getDataServices() {
        return dataServices;
    }

    public void setDataServices(final DS dataServices) {
        this.dataServices = dataServices;
    }
}
