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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;

public abstract class AbstractParameter extends AbstractEdm {

    private static final long serialVersionUID = -4305016554930334342L;

    @JsonProperty(value = "Name", required = true)
    private String name;

    @JsonProperty(value = "Type", required = true)
    private String type;

    @JsonProperty(value = "Nullable")
    private boolean nullable = true;

    @JsonProperty("MaxLength")
    private String maxLength;

    @JsonProperty("Precision")
    private BigInteger precision;

    @JsonProperty("Scale")
    private BigInteger scale;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(final boolean nullable) {
        this.nullable = nullable;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(final String maxLength) {
        this.maxLength = maxLength;
    }

    public BigInteger getPrecision() {
        return precision;
    }

    public void setPrecision(final BigInteger precision) {
        this.precision = precision;
    }

    public BigInteger getScale() {
        return scale;
    }

    public void setScale(final BigInteger scale) {
        this.scale = scale;
    }
}
