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
package com.msopentech.odatajclient.engine.data;

import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Helper class for handling datetime and datetime-offset primitive values.
 *
 * @see com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType#DATE_TIME
 * @see com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType#DATE_TIME_OFFSET
 */
public final class ODataTimestamp implements Serializable {

    private static final long serialVersionUID = 4053990618660356004L;

    private final SimpleDateFormat sdf;

    private final Timestamp timestamp;

    private String timezone;

    private final boolean offset;

    public static ODataTimestamp getInstance(final EdmSimpleType type, final Timestamp timestamp) {
        return new ODataTimestamp(new SimpleDateFormat(type.pattern()),
                new Date(timestamp.getTime()), timestamp.getNanos(), type == EdmSimpleType.DateTimeOffset);
    }

    public static ODataTimestamp parse(final EdmSimpleType type, final String input) {
        final ODataTimestamp instance;

        final String[] dateParts = input.split("\\.");
        final SimpleDateFormat sdf = new SimpleDateFormat(type.pattern());
        final boolean isOffset = type == EdmSimpleType.DateTimeOffset;

        try {
            final Date date = sdf.parse(dateParts[0]);
            if (dateParts.length > 1) {
                int idx = dateParts[1].indexOf('+');
                if (idx == -1) {
                    idx = dateParts[1].indexOf('-');
                }
                if (idx == -1) {
                    instance = new ODataTimestamp(sdf, date, Integer.parseInt(dateParts[1]), isOffset);
                } else {
                    instance = new ODataTimestamp(sdf, date,
                            Integer.parseInt(dateParts[1].substring(0, idx)), dateParts[1].substring(idx), isOffset);
                }
            } else {
                instance = new ODataTimestamp(sdf, date, isOffset);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse " + type.pattern(), e);
        }

        return instance;
    }

    private ODataTimestamp(final SimpleDateFormat sdf, final Date date, final boolean offset) {
        this.sdf = sdf;
        this.timestamp = new Timestamp(date.getTime());
        this.offset = offset;
    }

    private ODataTimestamp(final SimpleDateFormat sdf, final Date date, final int nanos, final boolean offset) {
        this(sdf, date, offset);
        this.timestamp.setNanos(nanos);
    }

    private ODataTimestamp(
            final SimpleDateFormat sdf, final Date date, final int nanos, final String timezone, final boolean offset) {
        this(sdf, date, nanos, offset);
        this.timezone = timezone;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isOffset() {
        return offset;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "sdf");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "sdf");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder formatted = new StringBuilder().append(sdf.format(timestamp));
        if (timestamp.getNanos() > 0) {
            formatted.append('.').append(String.valueOf(timestamp.getNanos()));
        }
        if (StringUtils.isNotBlank(timezone)) {
            formatted.append(timezone);
        }
        return formatted.toString();
    }
}
