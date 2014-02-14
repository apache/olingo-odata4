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
package com.msopentech.odatajclient.engine.communication.request.cud;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import java.net.URI;

public abstract class AbstractCUDRequestFactory implements CUDRequestFactory {

    private static final long serialVersionUID = -2723641791198745990L;

    protected final ODataClient client;

    protected AbstractCUDRequestFactory(final ODataClient client) {
        this.client = client;
    }

    @Override
    public ODataEntityCreateRequest getEntityCreateRequest(final URI targetURI, final ODataEntity entity) {
        return new ODataEntityCreateRequest(client, targetURI, entity);
    }

    @Override
    public ODataEntityUpdateRequest getEntityUpdateRequest(
            final URI targetURI, final UpdateType type, final ODataEntity changes) {

        final ODataEntityUpdateRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataEntityUpdateRequest(client, HttpMethod.POST, targetURI, changes);
            req.setXHTTPMethod(type.getMethod().name());
        } else {
            req = new ODataEntityUpdateRequest(client, type.getMethod(), targetURI, changes);
        }

        return req;
    }

    @Override
    public ODataEntityUpdateRequest getEntityUpdateRequest(final UpdateType type, final ODataEntity entity) {
        if (entity.getEditLink() == null) {
            throw new IllegalArgumentException("No edit link found");
        }

        final ODataEntityUpdateRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataEntityUpdateRequest(client, HttpMethod.POST, entity.getEditLink(), entity);
            req.setXHTTPMethod(type.getMethod().name());
        } else {
            req = new ODataEntityUpdateRequest(client, type.getMethod(), entity.getEditLink(), entity);
        }

        return req;
    }

    @Override
    public ODataValueUpdateRequest getValueUpdateRequest(
            final URI targetURI, final UpdateType type, final ODataPrimitiveValue value) {

        final ODataValueUpdateRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataValueUpdateRequest(client, HttpMethod.POST, targetURI, value);
            req.setXHTTPMethod(type.getMethod().name());
        } else {
            req = new ODataValueUpdateRequest(client, type.getMethod(), targetURI, value);
        }

        return req;
    }

    @Override
    public ODataPropertyUpdateRequest getPropertyPrimitiveValueUpdateRequest(
            final URI targetURI, final ODataProperty property) {

        if (!property.hasPrimitiveValue()) {
            throw new IllegalArgumentException("A primitive value is required");
        }

        final ODataPropertyUpdateRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataPropertyUpdateRequest(client, HttpMethod.POST, targetURI, property);
            req.setXHTTPMethod(HttpMethod.PUT.name());
        } else {
            req = new ODataPropertyUpdateRequest(client, HttpMethod.PUT, targetURI, property);
        }

        return req;
    }

    @Override
    public ODataPropertyUpdateRequest getPropertyComplexValueUpdateRequest(
            final URI targetURI, final UpdateType type, final ODataProperty property) {

        if (!property.hasComplexValue()) {
            throw new IllegalArgumentException("A complex value is required");
        }

        final ODataPropertyUpdateRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataPropertyUpdateRequest(client, HttpMethod.POST, targetURI, property);
            req.setXHTTPMethod(type.getMethod().name());
        } else {
            req = new ODataPropertyUpdateRequest(client, type.getMethod(), targetURI, property);
        }

        return req;
    }

    @Override
    public ODataPropertyUpdateRequest getPropertyCollectionValueUpdateRequest(
            final URI targetURI, final ODataProperty property) {

        if (!property.hasCollectionValue()) {
            throw new IllegalArgumentException("A collection value is required");
        }

        final ODataPropertyUpdateRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataPropertyUpdateRequest(client, HttpMethod.POST, targetURI, property);
            req.setXHTTPMethod(HttpMethod.PUT.name());
        } else {
            req = new ODataPropertyUpdateRequest(client, HttpMethod.PUT, targetURI, property);
        }

        return req;
    }

    @Override
    public ODataLinkCreateRequest getLinkCreateRequest(final URI targetURI, final ODataLink link) {
        return new ODataLinkCreateRequest(client, targetURI, link);
    }

    @Override
    public ODataLinkUpdateRequest getLinkUpdateRequest(
            final URI targetURI, final UpdateType type, final ODataLink link) {

        final ODataLinkUpdateRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataLinkUpdateRequest(client, HttpMethod.POST, targetURI, link);
            req.setXHTTPMethod(type.getMethod().name());
        } else {
            req = new ODataLinkUpdateRequest(client, type.getMethod(), targetURI, link);
        }

        return req;
    }

    @Override
    public ODataDeleteRequest getDeleteRequest(final URI targetURI) {
        final ODataDeleteRequest req;

        if (client.getConfiguration().isUseXHTTPMethod()) {
            req = new ODataDeleteRequest(client, HttpMethod.POST, targetURI);
            req.setXHTTPMethod(HttpMethod.DELETE.name());
        } else {
            req = new ODataDeleteRequest(client, HttpMethod.DELETE, targetURI);
        }

        return req;
    }
}
