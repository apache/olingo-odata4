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
package com.msopentech.odatajclient.engine.metadata.edm.v4.annotation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigInteger;

@JsonDeserialize(using = CastDeserializer.class)
public class Cast extends AnnotatedDynExprConstruct {

    private static final long serialVersionUID = -7836626668653004926L;

    private String type;

    private String maxLength;

    private BigInteger precision;

    private BigInteger scale;

    private String srid;

    private DynExprConstruct value;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
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

    public String getSrid() {
        return srid;
    }

    public void setSrid(final String srid) {
        this.srid = srid;
    }

    public DynExprConstruct getValue() {
        return value;
    }

    public void setValue(final DynExprConstruct value) {
        this.value = value;
    }

}
