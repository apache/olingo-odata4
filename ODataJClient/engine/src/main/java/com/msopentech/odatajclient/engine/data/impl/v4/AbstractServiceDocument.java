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
package com.msopentech.odatajclient.engine.data.impl.v4;

import com.msopentech.odatajclient.engine.data.ServiceDocumentElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServiceDocument
        extends com.msopentech.odatajclient.engine.data.impl.AbstractServiceDocument {

    private URI baseURI;

    private String metadataContext;

    private String metadataETag;

    private List<ServiceDocumentElement> functionImports = new ArrayList<ServiceDocumentElement>();

    private List<ServiceDocumentElement> singletons = new ArrayList<ServiceDocumentElement>();

    private List<ServiceDocumentElement> relatedServiceDocuments = new ArrayList<ServiceDocumentElement>();

    @Override
    public URI getBaseURI() {
        return this.baseURI;
    }

    /**
     * Sets base URI.
     *
     * @param baseURI base URI.
     */
    public void setBaseURI(final URI baseURI) {
        this.baseURI = baseURI;
    }

    @Override
    public String getMetadataContext() {
        return metadataContext;
    }

    public void setMetadataContext(final String metadataContext) {
        this.metadataContext = metadataContext;
    }

    @Override
    public String getMetadataETag() {
        return metadataETag;
    }

    public void setMetadataETag(final String metadataETag) {
        this.metadataETag = metadataETag;
    }

    @Override
    public List<ServiceDocumentElement> getFunctionImports() {
        return functionImports;
    }

    @Override
    public List<ServiceDocumentElement> getSingletons() {
        return singletons;
    }

    @Override
    public List<ServiceDocumentElement> getRelatedServiceDocuments() {
        return relatedServiceDocuments;
    }

}
