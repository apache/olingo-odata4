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
