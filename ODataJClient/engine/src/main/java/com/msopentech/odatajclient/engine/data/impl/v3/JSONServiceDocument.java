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
package com.msopentech.odatajclient.engine.data.impl.v3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msopentech.odatajclient.engine.data.impl.AbstractServiceDocument;
import com.msopentech.odatajclient.engine.uri.SegmentType;
import java.net.URI;

/**
 * Service document, represented via JSON.
 */
public class JSONServiceDocument extends AbstractServiceDocument {

    private static final long serialVersionUID = 4195734928526398830L;

    @JsonProperty(value = "odata.metadata", required = false)
    private URI metadata;

    @JsonIgnore
    @Override
    public URI getBaseURI() {
        URI baseURI = null;
        if (metadata != null) {
            final String metadataURI = getMetadata().toASCIIString();
            baseURI = URI.create(metadataURI.substring(0, metadataURI.indexOf(SegmentType.METADATA.getValue())));
        }

        return baseURI;
    }

    /**
     * Gets the metadata URI.
     */
    public URI getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata URI.
     *
     * @param metadata metadata URI.
     */
    public void setMetadata(final URI metadata) {
        this.metadata = metadata;
    }
}
