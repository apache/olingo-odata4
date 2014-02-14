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
package com.msopentech.odatajclient.engine.data;

import com.msopentech.odatajclient.engine.client.ODataClient;
import java.net.URI;

/**
 * OData in-line entity.
 */
public class ODataInlineEntity extends ODataLink {

    private static final long serialVersionUID = -4763341581843700743L;

    private final ODataEntity entity;

    /**
     * Constructor.
     *
     * @param uri edit link.
     * @param type type.
     * @param title title.
     * @param entity entity.
     */
    ODataInlineEntity(final ODataClient client,
            final URI uri, final ODataLinkType type, final String title, final ODataEntity entity) {

        super(client, uri, type, title);
        this.entity = entity;
    }

    /**
     * Constructor.
     *
     * @param baseURI base URI.
     * @param href href.
     * @param type type.
     * @param title title.
     * @param entity entity.
     */
    ODataInlineEntity(final ODataClient client, final URI baseURI, final String href, final ODataLinkType type,
            final String title, final ODataEntity entity) {

        super(client, baseURI, href, type, title);
        this.entity = entity;
    }

    /**
     * Gets wrapped entity.
     *
     * @return wrapped entity.
     */
    public ODataEntity getEntity() {
        return entity;
    }
}
