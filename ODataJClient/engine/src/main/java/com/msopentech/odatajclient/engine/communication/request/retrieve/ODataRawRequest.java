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
package com.msopentech.odatajclient.engine.communication.request.retrieve;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.ODataRequestImpl;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.net.URI;

/**
 * This class implements a generic OData request.
 */
public class ODataRawRequest extends ODataRequestImpl<ODataPubFormat> {

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param uri request URI.
     */
    ODataRawRequest(final ODataClient odataClient, final URI uri) {
        super(odataClient, ODataPubFormat.class, HttpMethod.GET, uri);
    }
}
