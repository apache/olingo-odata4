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
import com.msopentech.odatajclient.engine.metadata.EdmContentKind;
import java.math.BigInteger;

public abstract class AbstractProperty extends AbstractEdm {

    private static final long serialVersionUID = -6004492361142315153L;

    @JsonProperty(value = "Name", required = true)
    private String name;

    @JsonProperty(value = "Type", required = true)
    private String type;

    @JsonProperty(value = "Nullable")
    private boolean nullable = true;

    @JsonProperty(value = "DefaultValue")
    private String defaultValue;

    @JsonProperty(value = "MaxLength")
    private String maxLength;

    @JsonProperty(value = "FixedLength")
    private boolean fixedLength;

    @JsonProperty(value = "Precision")
    private BigInteger precision;

    @JsonProperty(value = "Scale")
    private BigInteger scale;

    @JsonProperty(value = "Unicode")
    private boolean unicode = true;

    @JsonProperty(value = "Collation")
    private String collation;

    @JsonProperty(value = "SRID")
    private String srid;

    @JsonProperty(value = "ConcurrencyMode")
    private ConcurrencyMode concurrencyMode;

    @JsonProperty("FC_SourcePath")
    private String fcSourcePath;

    @JsonProperty("FC_TargetPath")
    private String fcTargetPath;

    @JsonProperty("FC_ContentKind")
    private EdmContentKind fcContentKind = EdmContentKind.text;

    @JsonProperty("FC_NsPrefix")
    private String fcNSPrefix;

    @JsonProperty("FC_NsUri")
    private String fcNSURI;

    @JsonProperty("FC_KeepInContent")
    private boolean fcKeepInContent = true;

    @JsonProperty("StoreGeneratedPattern")
    private StoreGeneratedPattern storeGeneratedPattern = StoreGeneratedPattern.None;

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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(final String maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isFixedLength() {
        return fixedLength;
    }

    public void setFixedLength(final boolean fixedLength) {
        this.fixedLength = fixedLength;
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

    public boolean isUnicode() {
        return unicode;
    }

    public void setUnicode(final boolean unicode) {
        this.unicode = unicode;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(final String collation) {
        this.collation = collation;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(final String srid) {
        this.srid = srid;
    }

    public ConcurrencyMode getConcurrencyMode() {
        return concurrencyMode;
    }

    public void setConcurrencyMode(final ConcurrencyMode concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
    }

    public String getFcSourcePath() {
        return fcSourcePath;
    }

    public void setFcSourcePath(final String fcSourcePath) {
        this.fcSourcePath = fcSourcePath;
    }

    public String getFcTargetPath() {
        return fcTargetPath;
    }

    public void setFcTargetPath(final String fcTargetPath) {
        this.fcTargetPath = fcTargetPath;
    }

    public EdmContentKind getFcContentKind() {
        return fcContentKind;
    }

    public void setFcContentKind(final EdmContentKind fcContentKind) {
        this.fcContentKind = fcContentKind;
    }

    public String getFcNSPrefix() {
        return fcNSPrefix;
    }

    public void setFcNSPrefix(final String fcNSPrefix) {
        this.fcNSPrefix = fcNSPrefix;
    }

    public String getFcNSURI() {
        return fcNSURI;
    }

    public void setFcNSURI(final String fcNSURI) {
        this.fcNSURI = fcNSURI;
    }

    public boolean isFcKeepInContent() {
        return fcKeepInContent;
    }

    public void setFcKeepInContent(final boolean fcKeepInContent) {
        this.fcKeepInContent = fcKeepInContent;
    }

    public StoreGeneratedPattern getStoreGeneratedPattern() {
        return storeGeneratedPattern;
    }

    public void setStoreGeneratedPattern(final StoreGeneratedPattern storeGeneratedPattern) {
        this.storeGeneratedPattern = storeGeneratedPattern;
    }
}
