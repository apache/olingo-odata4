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

@JsonDeserialize(using = DataServicesDeserializer.class)
public abstract class AbstractDataServices<S extends AbstractSchema<EC, E, C, FI>, EC extends AbstractEntityContainer<
        FI>, E extends AbstractEntityType, C extends AbstractComplexType, FI extends AbstractFunctionImport>
        extends AbstractEdm {

    private static final long serialVersionUID = -9126377222393876166L;

    private String dataServiceVersion;

    private String maxDataServiceVersion;

    public String getDataServiceVersion() {
        return dataServiceVersion;
    }

    public void setDataServiceVersion(final String dataServiceVersion) {
        this.dataServiceVersion = dataServiceVersion;
    }

    public String getMaxDataServiceVersion() {
        return maxDataServiceVersion;
    }

    public void setMaxDataServiceVersion(final String maxDataServiceVersion) {
        this.maxDataServiceVersion = maxDataServiceVersion;
    }

    public abstract List<S> getSchemas();
}
