/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.msopentech.odatajclient.engine.data.impl.v4;

import com.msopentech.odatajclient.engine.client.ODataV4Client;
import com.msopentech.odatajclient.engine.data.ODataServiceDocument;
import com.msopentech.odatajclient.engine.data.ServiceDocument;
import com.msopentech.odatajclient.engine.data.ServiceDocumentElement;
import com.msopentech.odatajclient.engine.data.impl.AbstractODataBinder;
import com.msopentech.odatajclient.engine.metadata.EdmType;
import com.msopentech.odatajclient.engine.metadata.EdmV4Type;
import com.msopentech.odatajclient.engine.utils.URIUtils;

public class ODataBinderImpl extends AbstractODataBinder {

    private static final long serialVersionUID = -6371110655960799393L;

    public ODataBinderImpl(final ODataV4Client client) {
        super(client);
    }

    @Override
    protected EdmType newEdmType(final String expression) {
        return new EdmV4Type(expression);
    }

    @Override
    public ODataServiceDocument getODataServiceDocument(final ServiceDocument resource) {
        final ODataServiceDocument serviceDocument = super.getODataServiceDocument(resource);

        serviceDocument.setMetadataContext(URIUtils.getURI(resource.getBaseURI(), resource.getMetadataContext()));
        serviceDocument.setMetadataETag(resource.getMetadataETag());

        for (ServiceDocumentElement functionImport : resource.getFunctionImports()) {
            serviceDocument.getFunctionImports().put(functionImport.getTitle(),
                    URIUtils.getURI(resource.getBaseURI(), functionImport.getHref()));
        }
        for (ServiceDocumentElement singleton : resource.getSingletons()) {
            serviceDocument.getSingletons().put(singleton.getTitle(),
                    URIUtils.getURI(resource.getBaseURI(), singleton.getHref()));
        }
        for (ServiceDocumentElement sdoc : resource.getRelatedServiceDocuments()) {
            serviceDocument.getRelatedServiceDocuments().put(sdoc.getTitle(),
                    URIUtils.getURI(resource.getBaseURI(), sdoc.getHref()));
        }

        return serviceDocument;
    }
}
