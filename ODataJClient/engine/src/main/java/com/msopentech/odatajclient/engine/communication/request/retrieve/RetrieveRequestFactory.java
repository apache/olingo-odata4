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

import java.io.Serializable;
import java.net.URI;

/**
 * OData request factory class.
 */
public interface RetrieveRequestFactory extends Serializable {

    /**
     * Gets a service document request instance.
     *
     * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the
     * data service.
     * @return new ODataServiceDocumentRequest instance.
     */
    ODataServiceDocumentRequest getServiceDocumentRequest(String serviceRoot);

    /**
     * Gets a metadata request instance.
     *
     * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the
     * data service.
     * @return new ODataMetadataRequest instance.
     */
    ODataMetadataRequest getMetadataRequest(String serviceRoot);

    /**
     * Gets a query request returning a set of one or more OData entities.
     *
     * @param query query to be performed.
     * @return new ODataEntitySetRequest instance.
     */
    ODataEntitySetRequest getEntitySetRequest(URI query);

    /**
     * Gets a query request returning a set of one or more OData entities.
     * <br/>
     * Returned request gives the possibility to consume entities iterating on them without parsing and loading in
     * memory the entire entity set.
     *
     * @param query query to be performed.
     * @return new ODataEntitySetIteratorRequest instance.
     */
    ODataEntitySetIteratorRequest getEntitySetIteratorRequest(URI query);

    /**
     * Gets a query request returning a single OData entity.
     *
     * @param query query to be performed.
     * @return new ODataEntityRequest instance.
     */
    ODataEntityRequest getEntityRequest(URI query);

    /**
     * Gets a query request returning a single OData entity property.
     *
     * @param query query to be performed.
     * @return new ODataPropertyRequest instance.
     */
    ODataPropertyRequest getPropertyRequest(URI query);

    /**
     * Gets a query request returning a single OData entity property value.
     *
     * @param query query to be performed.
     * @return new ODataValueRequest instance.
     */
    ODataValueRequest getValueRequest(URI query);

    /**
     * Gets a query request returning a single OData link.
     *
     * @param targetURI target URI.
     * @param linkName link name.
     * @return new ODataLinkRequest instance.
     */
    ODataLinkCollectionRequest getLinkCollectionRequest(URI targetURI, String linkName);

    /**
     * Gets a query request returning a media stream.
     *
     * @param query query to be performed.
     * @return new ODataMediaRequest instance.
     */
    ODataMediaRequest getMediaRequest(URI query);

    /**
     * Implements a raw request returning a stream.
     *
     * @param uri query to be performed.
     * @return new ODataRawRequest instance.
     */
    ODataRawRequest getRawRequest(URI uri);

    /**
     * Implements a generic retrieve request without specifying any return type.
     *
     * @param uri query to be performed.
     * @return new ODataGenericRerieveRequest instance.
     */
    ODataGenericRetrieveRequest getGenericRetrieveRequest(URI uri);
}
