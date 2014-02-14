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
package com.msopentech.odatajclient.engine.communication.request;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.batch.BatchRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.CUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.invoke.InvokeRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.streamed.StreamedRequestFactory;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

/**
 * Abstract representation of an OData request.
 * Get instance by using factories.
 *
 * @see CUDRequestFactory
 * @see RetrieveRequestFactory
 * @see BatchRequestFactory
 * @see InvokeRequestFactory
 * @see StreamedRequestFactory
 */
public interface ODataRequest {

    /**
     * Returns OData request target URI.
     *
     * @return OData request target URI.
     */
    URI getURI();

    /**
     * Returns HTTP request method.
     *
     * @return HTTP request method.
     */
    HttpMethod getMethod();

    /**
     * Gets all OData request header names.
     *
     * @return all request header names.
     */
    Collection<String> getHeaderNames();

    /**
     * Gets the value of the OData request header identified by the given name.
     *
     * @param name name of the OData request header to be retrieved.
     * @return header value.
     */
    String getHeader(final String name);

    /**
     * Adds <tt>Accept</tt> OData request header.
     *
     * @param value header value.
     * @return current object
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#accept
     */
    ODataRequest setAccept(final String value);

    /**
     * Gets <tt>Accept</tt> OData request header.
     *
     * @return header value.
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#accept
     */
    String getAccept();

    /**
     * Adds <tt>If-Match</tt> OData request header.
     *
     * @param value header value.
     * @return current object
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#ifMatch
     */
    ODataRequest setIfMatch(final String value);

    /**
     * Gets <tt>If-Match</tt> OData request header.
     *
     * @return header value.
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#ifMatch
     */
    String getIfMatch();

    /**
     * Adds <tt>If-None-Match</tt> OData request header.
     *
     * @param value header value.
     * @return current object
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#ifNoneMatch
     */
    ODataRequest setIfNoneMatch(final String value);

    /**
     * Gets <tt>If-None-Match</tt> OData request header.
     *
     * @return header value.
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#ifNoneMatch
     */
    String getIfNoneMatch();

    /**
     * Adds <tt>Prefer</tt> OData request header.
     *
     * @param value header value.
     * @return current object
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#prefer
     */
    ODataRequest setPrefer(final String value);

    /**
     * Gets <tt>Prefer</tt> OData request header.
     *
     * @return header value.
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#prefer
     */
    String getPrefer();

    /**
     * Adds <tt>contentType</tt> OData request header.
     *
     * @param value header value.
     * @return current object
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#contentType
     */
    ODataRequest setContentType(final String value);

    /**
     * Gets <tt>contentType</tt> OData request header.
     *
     * @return header value.
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#contentType
     */
    String getContentType();

    /**
     * Adds <tt>Slug</tt> OData request header.
     *
     * @param value header value.
     * @return current object
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#slug
     */
    ODataRequest setSlug(final String value);

    /**
     * Adds <tt>X-HTTP-METHOD</tt> OData request header.
     *
     * @param value header value.
     * @return current object
     * @see com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName#xHttpMethod
     */
    ODataRequest setXHTTPMethod(final String value);

    /**
     * Adds a custom OData request header.
     *
     * @param name header name.
     * @param value header value.
     * @return current object
     */
    ODataRequest addCustomHeader(final String name, final String value);

    /**
     * Gets byte array representation of the full request header.
     *
     * @return full request header.
     */
    byte[] toByteArray();

    /**
     * Request raw execute.
     *
     * @return raw input stream response.
     */
    InputStream rawExecute();
}
