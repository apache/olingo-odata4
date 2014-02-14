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
package com.msopentech.odatajclient.engine.communication.request.invoke;

import com.msopentech.odatajclient.engine.data.ODataInvokeResult;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.AbstractEdmMetadata;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractComplexType;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractDataServices;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdmx;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityType;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractFunctionImport;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractSchema;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;

/**
 * OData request factory class.
 */
public interface InvokeRequestFactory<META extends AbstractEdmMetadata<
        EDMX, DS, S, EC, E, C, FI>, EDMX extends AbstractEdmx<DS, S, EC, E, C, FI>, DS extends AbstractDataServices<
        S, EC, E, C, FI>, S extends AbstractSchema<EC, E, C, FI>, EC extends AbstractEntityContainer<
        FI>, E extends AbstractEntityType, C extends AbstractComplexType, FI extends AbstractFunctionImport>
        extends Serializable {

    /**
     * Gets an invoke request instance.
     *
     * @param <RES> OData domain object result, derived from return type defined in the function import
     * @param uri URI that identifies the function import
     * @param metadata Edm metadata
     * @param functionImport function import to be invoked
     * @return new ODataInvokeRequest instance.
     */
    <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
            URI uri, META metadata, FI functionImport);

    /**
     * Gets an invoke request instance.
     *
     * @param <RES> OData domain object result, derived from return type defined in the function import
     * @param uri URI that identifies the function import
     * @param metadata Edm metadata
     * @param functionImport function import to be invoked
     * @param parameters parameters to pass to function import invocation
     * @return new ODataInvokeRequest instance.
     */
    <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
            URI uri, META metadata, FI functionImport, Map<String, ODataValue> parameters);
}
