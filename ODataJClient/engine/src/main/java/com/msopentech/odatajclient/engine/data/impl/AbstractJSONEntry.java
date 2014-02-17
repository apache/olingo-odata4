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
package com.msopentech.odatajclient.engine.data.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONLink;
import com.msopentech.odatajclient.engine.uri.SegmentType;
import java.net.URI;

/**
 * A single entry, represented via JSON.
 */
@JsonSerialize(using = JSONEntrySerializer.class)
@JsonDeserialize(using = JSONEntryDeserializer.class)
public abstract class AbstractJSONEntry extends AbstractEntry<JSONLink> {

    private static final long serialVersionUID = -5275365545400797758L;

    private URI metadata;

    private String mediaETag;

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
     *
     * @return the metadata URI
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

    /**
     * The odata.mediaEtag annotation MAY be included; its value MUST be the ETag of the binary stream represented by
     * this media entity or named stream property.
     *
     * @return odata.mediaEtag annotation value.
     */
    public String getMediaETag() {
        return mediaETag;
    }

    /**
     * The odata.mediaEtag annotation MAY be included; its value MUST be the ETag of the binary stream represented by
     * this media entity or named stream property.
     *
     * @param eTag odata.mediaEtag annotation value.
     */
    public void setMediaETag(String eTag) {
        this.mediaETag = eTag;
    }
}
