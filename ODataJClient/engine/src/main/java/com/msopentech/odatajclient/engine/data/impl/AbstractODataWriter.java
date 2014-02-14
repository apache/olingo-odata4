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
package com.msopentech.odatajclient.engine.data.impl;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataWriter;
import com.msopentech.odatajclient.engine.data.ResourceFactory;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.io.IOUtils;

public abstract class AbstractODataWriter implements ODataWriter {

    private static final long serialVersionUID = 3265794768412314485L;

    protected final ODataClient client;

    protected AbstractODataWriter(final ODataClient client) {
        this.client = client;
    }

    @Override
    public InputStream writeEntities(final Collection<ODataEntity> entities, final ODataPubFormat format) {
        return writeEntities(entities, format, true);
    }

    @Override
    public InputStream writeEntities(
            final Collection<ODataEntity> entities, final ODataPubFormat format, final boolean outputType) {

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            for (ODataEntity entity : entities) {
                client.getSerializer().entry(client.getBinder().
                        getEntry(entity, ResourceFactory.entryClassForFormat(format), outputType), output);
            }

            return new ByteArrayInputStream(output.toByteArray());
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    @Override
    public InputStream writeEntity(final ODataEntity entity, final ODataPubFormat format) {
        return writeEntity(entity, format, true);
    }

    @Override
    public InputStream writeEntity(final ODataEntity entity, final ODataPubFormat format, final boolean outputType) {
        return writeEntities(Collections.<ODataEntity>singleton(entity), format, outputType);
    }

    @Override
    public InputStream writeProperty(final ODataProperty property, final ODataFormat format) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            client.getSerializer().property(client.getBinder().toDOMElement(property), format, output);

            return new ByteArrayInputStream(output.toByteArray());
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    @Override
    public InputStream writeLink(final ODataLink link, final ODataFormat format) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            client.getSerializer().link(link, format, output);

            return new ByteArrayInputStream(output.toByteArray());
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}
