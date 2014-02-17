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
package com.msopentech.odatajclient.engine.metadata.edm.v4;

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdm;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TypeDefinition extends AbstractEdm {

    private static final long serialVersionUID = -5888231162358116515L;

    private String name;

    private String underlyingType;

    private String maxLength;

    private BigInteger precision;

    private BigInteger scale;

    private boolean unicode = true;

    private String srid;

    private final List<Annotation> annotations = new ArrayList<Annotation>();

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

    public boolean isUnicode() {
        return unicode;
    }

    public void setUnicode(final boolean unicode) {
        this.unicode = unicode;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(final String srid) {
        this.srid = srid;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

}
