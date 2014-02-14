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
package com.msopentech.odatajclient.engine.metadata.edm.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdm;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class PropertyValue extends AbstractEdm {

    private static final long serialVersionUID = -6580934436491418564L;

    @JsonProperty(value = "Property", required = true)
    private String property;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("String")
    private String string;

    @JsonProperty("Int")
    private BigInteger _int;

    @JsonProperty("Float")
    private Double _float;

    @JsonProperty("Decimal")
    private BigDecimal decimal;

    @JsonProperty("Bool")
    private Boolean bool;

    @JsonProperty("DateTime")
    private Date dateTime;

    public String getProperty() {
        return property;
    }

    public void setProperty(final String property) {
        this.property = property;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getString() {
        return string;
    }

    public void setString(final String string) {
        this.string = string;
    }

    public BigInteger getInt() {
        return _int;
    }

    public void setInt(final BigInteger _int) {
        this._int = _int;
    }

    public Double getFloat() {
        return _float;
    }

    public void setFloat(final Double _float) {
        this._float = _float;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(final BigDecimal decimal) {
        this.decimal = decimal;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(final Boolean bool) {
        this.bool = bool;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(final Date dateTime) {
        this.dateTime = dateTime;
    }
}
