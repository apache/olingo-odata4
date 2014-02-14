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
import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This class implements an OData entity property value query request.
 */
public class ODataValueRequest extends AbstractODataRetrieveRequest<ODataValue, ODataValueFormat> {

    /**
     * Private constructor.
     *
     * @param odataClient client instance getting this request
     * @param query query to be executed.
     */
    ODataValueRequest(final ODataClient odataClient, final URI query) {
        super(odataClient, ODataValueFormat.class, query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataRetrieveResponse<ODataValue> execute() {
        final HttpResponse res = doExecute();
        return new ODataValueResponseImpl(httpClient, res);
    }

    /**
     * Response class about an ODataDeleteReODataValueRequestquest.
     */
    protected class ODataValueResponseImpl extends ODataRetrieveResponseImpl {

        private ODataValue value = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataValueResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataValueResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ODataValue getBody() {
            if (value == null) {
                final ODataValueFormat format = ODataValueFormat.fromString(getContentType());

                try {
                    value = new ODataPrimitiveValue.Builder(odataClient).
                            setType(format == ODataValueFormat.TEXT ? EdmSimpleType.String : EdmSimpleType.Stream).
                            setText(IOUtils.toString(getRawResponse())).
                            build();
                } catch (IOException e) {
                    throw new HttpClientException(e);
                } finally {
                    this.close();
                }
            }
            return value;
        }
    }
}
