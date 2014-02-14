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
package com.msopentech.odatajclient.engine.communication.header;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * ODataHeaders wraps OData request/response headers.
 *
 * @see com.msopentech.odatajclient.engine.communication.request.ODataRequest
 * @see com.msopentech.odatajclient.engine.communication.response.ODataResponse
 */
public class ODataHeaders {

    /**
     * Major OData request/response header names.
     */
    public enum HeaderName {

        /**
         * The OData protocol uses the Accept request-header field, as specified in [RFC2616].
         */
        accept("Accept"),
        /**
         * The Content-Type header is used as specified in [RFC2616].
         * <br/>
         * OData request/response supports the following types:
         * <ul>
         * <li>application/atom+xml</li>
         * <li>application/atom+xml;type=entry</li>
         * <li>application/atom+xml;type=feed</li>
         * <li>application/json; odata=verbose</li>
         * <li>application/json</li>
         * <li>application/xml</li>
         * <li>text/plain</li>
         * <li>text/xml</li>
         * <li>octet/stream</li>
         * <li>multipart/mixed</li>
         * </ul>
         */
        contentType("Content-Type"),
        /**
         * This header is a custom HTTP header defined for protocol versioning purposes.
         * This header MAY be present on any request or response message.
         */
        dataServiceVersion("DataServiceVersion"),
        /**
         * An ETag (entity tag) is an HTTP response header returned by an HTTP/1.1 compliant web server
         * used to determine change in content of a resource at a given URL. The value of the header is an
         * opaque string representing the state of the resource at the time the response was generated.
         */
        etag("ETag"),
        /**
         * The If-Match request-header field is used with a method to make it conditional. As specified in
         * [RFC2616], "the purpose of this feature is to allow efficient updates of cached information with a
         * minimum amount of transaction overhead. It is also used, on updating requests, to prevent
         * inadvertent modification of the wrong version of a resource".
         */
        ifMatch("If-Match"),
        /**
         * The If-None-Match request header is used with a method to make it conditional. As specified in
         * [RFC2616], "The purpose of this feature is to allow efficient updates of cached information with a
         * minimum amount of transaction overhead. It is also used to prevent a method (for example, PUT)
         * from inadvertently modifying an existing resource when the client believes that the resource does
         * not exist."
         */
        ifNoneMatch("If-None-Match"),
        /**
         * This header is a custom HTTP request only header defined for protocol versioning purposes.
         * This header MAY be present on any request message from client to server.
         */
        maxDataServiceVersion("MaxDataServiceVersion"),
        /**
         * This header is a custom HTTP request only header defined for protocol versioning purposes.
         * This header MAY be present on any request message from client to server.
         */
        minDataServiceVersion("MinDataServiceVersion"),
        /**
         * A Prefer header is included in a request to state the client’s preferred, but not required, server
         * behavior (that is, a hint to the server). The Prefer header MAY be included on any request type
         * (within a standalone or batch request), and a server MAY honor the header for HTTP POST, PUT,
         * PATCH, and MERGE requests. A Prefer header with a value of “return-content” MUST NOT be
         * specified on a DELETE request, a batch request as a whole, or a PUT request to update a named
         * stream.
         */
        prefer("Prefer"),
        /**
         * When a Prefer header value is successfully honored by the server, it MAY include a
         * Preference-Applied response header that states which preference values were honored by the
         * server.
         */
        preferenceApplied("Preference-Applied"),
        /**
         * The DataServiceId response header is returned by the server when the response payload for an
         * HTTP PUT, POST, PATCH, or MERGE request is empty.
         * The value of the header is the identifier of the entity that was acted on by the PUT, POST, PATCH, or
         * MERGE request. The identifier, in this case, is the same identifier that would have been returned in
         * the response payload (for example, as the value of the atom:id element for Atom responses)
         */
        dataServiceId("DataServiceId"),
        /**
         * Location header is used to specify the URL of an entity modified through a Data Modification request, or the
         * request URL to check on the status of an asynchronous operation as described in
         * <code>202 Accepted</code>.
         */
        location("Location"),
        /**
         * A service must include a
         * <code>Retry-After</code> header in a
         * <code>202 Accepted</code>.
         */
        retryAfter("Retry-After"),
        dataServiceUrlConventions("DataServiceUrlConventions"),
        slug("Slug"),
        /**
         * This header is a custom HTTP request header.
         * <br/>
         * It is possible to instruct network intermediaries (proxies, firewalls, and so on) inspecting traffic at
         * the application protocol layer (for example, HTTP) to block requests that contain certain HTTP verbs.
         * In practice, GET and POST verbs are rarely blocked (traditional web pages rely heavily on these
         * HTTP methods), while, for a variety of reasons (such as security vulnerabilities in prior protocols),
         * other HTTP methods (PUT, DELETE, and so on) are at times blocked by intermediaries. Additionally,
         * some existing HTTP libraries do not allow creation of requests using verbs other than GET or POST.
         * Therefore, an alternative way of specifying request types which use verbs other than GET and POST
         * is needed to ensure that this document works well in a wide range of environments.
         * <br/>
         * To address this need, the X-HTTP-Method header can be added to a POST request that signals that
         * the server MUST process the request not as a POST, but as if the HTTP verb specified as the value of
         * the header was used as the method on the HTTP request's request line, as specified in [RFC2616]
         * section 5.1. This technique is often referred to as "verb tunneling".
         * <br/>
         * This header is only valid when on POST requests.
         */
        xHttpMethod("X-HTTP-METHOD");

        private final String headerName;

        private HeaderName(final String headerName) {
            this.headerName = headerName;
        }

        @Override
        public String toString() {
            return headerName;
        }
    }

    /**
     * OData request/response header key/value pairs.
     */
    private final Map<String, String> headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Add the specified (custom) header (header name is case-insensitive).
     *
     * @param name header key.
     * @param value header value.
     * @return the current updated header instance.
     */
    public ODataHeaders setHeader(final String name, final String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * Add the specified header.
     *
     * @param name header key.
     * @param value header value.
     * @return the current updated header instance.
     */
    public ODataHeaders setHeader(final HeaderName name, final String value) {
        headers.put(name.toString(), value);
        return this;
    }

    /**
     * Gets the value of the header identified by the given name.
     * <br/>
     * Please note that header name is case-insensitive.
     *
     * @param name name of the header to be retrieved.
     * @return header value.
     */
    public String getHeader(final HeaderName name) {
        return headers.get(name.toString());
    }

    /**
     * Gets the value of the header identified by the given name.
     * <br/>
     * Please note that header name is case-insensitive.
     *
     * @param name name of the header to be retrieved.
     * @return header value.
     */
    public String getHeader(final String name) {
        return headers.get(name);
    }

    /**
     * Removes the header identified by the given name.
     * <br/>
     * Please note that header name is case-insensitive.
     *
     * @param name name of the header to be retrieved.
     * @return header name (if found).
     */
    public String removeHeader(final HeaderName name) {
        return headers.remove(name.toString());
    }

    /**
     * Removes the header identified by the given name.
     * <br/>
     * Please note that header name is case-insensitive.
     *
     * @param name name of the header to be retrieved.
     * @return header name (if found).
     */
    public String removeHeader(final String name) {
        return headers.remove(name);
    }

    /**
     * Gets header names.
     * <br/>
     * Please note that header name is case-insensitive.
     *
     * @return header names.
     */
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }
}
