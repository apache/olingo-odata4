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
package com.msopentech.odatajclient.engine.communication.request.streamed;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;

/**
 * OData request factory class.
 */
public interface StreamedRequestFactory extends Serializable {

    /**
     * Gets a media entity create request object instance.
     * <br/>
     * Use this kind of request to create a new media entity.
     *
     * @param targetURI entity set URI.
     * @param media entity blob to be created.
     * @return new ODataMediaEntityCreateRequest instance.
     */
    ODataMediaEntityCreateRequest getMediaEntityCreateRequest(URI targetURI, InputStream media);

    /**
     * Gets a stream update request object instance.
     * <br/>
     * Use this kind of request to update a named stream property.
     *
     * @param targetURI target URI.
     * @param stream stream to be updated.
     * @return new ODataStreamUpdateRequest instance.
     */
    ODataStreamUpdateRequest getStreamUpdateRequest(URI targetURI, InputStream stream);

    /**
     * Gets a media entity update request object instance.
     * <br/>
     * Use this kind of request to update a media entity.
     *
     * @param editURI media entity edit link URI.
     * @param media entity blob to be updated.
     * @return new ODataMediaEntityUpdateRequest instance.
     */
    ODataMediaEntityUpdateRequest getMediaEntityUpdateRequest(URI editURI, InputStream media);
}
