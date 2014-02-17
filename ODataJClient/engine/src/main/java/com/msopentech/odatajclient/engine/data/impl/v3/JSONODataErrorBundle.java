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
package com.msopentech.odatajclient.engine.data.impl.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msopentech.odatajclient.engine.data.impl.AbstractPayloadObject;

/**
 * This class represents a bundle for an OData error returned as JSON.
 */
public class JSONODataErrorBundle extends AbstractPayloadObject {

    private static final long serialVersionUID = -4784910226259754450L;

    @JsonProperty("odata.error")
    private JSONODataError error;

    /**
     * Gets error.
     *
     * @return OData error object.
     */
    public JSONODataError getError() {
        return error;
    }

    /**
     * Sets error.
     *
     * @param error OData error object.
     */
    public void setError(final JSONODataError error) {
        this.error = error;
    }
}
