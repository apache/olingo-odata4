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
package com.msopentech.odatajclient.engine.communication.response;

import com.msopentech.odatajclient.engine.communication.header.ODataHeaders.HeaderName;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchLineIterator;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * Abstract representation of an OData response.
 */
public interface ODataResponse {

    /**
     * Gets header names.
     *
     * @return response header names.
     */
    Collection<String> getHeaderNames();

    /**
     * Gets header value of the given header.
     *
     * @param name header to be retrieved.
     * @return response header value.
     */
    Collection<String> getHeader(final String name);

    /**
     * Gets header value of the given header.
     *
     * @param name header to be retrieved.
     * @return response header value.
     */
    Collection<String> getHeader(final HeaderName name);

    /**
     * Gets 'ETag' header value.
     *
     * @return ETag header value, if provided
     */
    String getEtag();

    /**
     * Gets the content type.
     *
     * @return content type
     */
    String getContentType();

    /**
     * Gets status code.
     *
     * @return status code.
     */
    int getStatusCode();

    /**
     * Gets status message.
     *
     * @return status message.
     */
    String getStatusMessage();

    /**
     * Gets response body as InputStream.
     *
     * @return response body input stream.
     */
    InputStream getRawResponse();

    /**
     * Initializes response from batch response item.
     *
     * @param responseLine response line.
     * @param headers response headers.
     * @param batchLineIterator batch line iterator.
     * @param boundary batch boundary.
     */
    ODataResponse initFromBatch(
            final Map.Entry<Integer, String> responseLine,
            final Map<String, Collection<String>> headers,
            final ODataBatchLineIterator batchLineIterator,
            final String boundary);

    /**
     * Close the underlying message entity input stream (if available and open) as well as releases any other
     * resources associated with the response.
     * <p>
     * This operation is idempotent, i.e. it can be invoked multiple times with the same effect which also means that
     * calling the close() method on an already closed message instance is legal and has no further effect.
     * <p>
     * The close() method should be invoked on all instances that contain an un-consumed entity input stream to ensure
     * the resources associated with the instance are properly cleaned-up and prevent potential memory leaks.
     * This is typical for client-side scenarios where application layer code processes only the response headers and
     * ignores the response entity.
     */
    void close();
}
