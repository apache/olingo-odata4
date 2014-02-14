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
package com.msopentech.odatajclient.engine.utils;

/**
 * Constant values related to the OData protocol.
 */
public class ODataBatchConstants {

    /**
     * Batch/Changeset content type.
     */
    public static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";

    /**
     * Batch item content type.
     */
    public static final String ITEM_CONTENT_TYPE = "application/http";

    /**
     * Boundary key.
     */
    public static final String BOUNDARY = "boundary";

    /**
     * Item content type.
     */
    public static String ITEM_CONTENT_TYPE_LINE = "Content-Type: application/http";

    /**
     * Item transfer encoding.
     */
    public static String ITEM_TRANSFER_ENCODING_LINE = "Content-Transfer-Encoding: binary";

    /**
     * Content id header name.
     */
    public static String CHANGESET_CONTENT_ID_NAME = "Content-ID";

}
