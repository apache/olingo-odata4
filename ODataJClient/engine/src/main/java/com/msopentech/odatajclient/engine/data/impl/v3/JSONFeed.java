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
import com.msopentech.odatajclient.engine.data.impl.AbstractPayloadObject;
import com.msopentech.odatajclient.engine.data.Entry;
import com.msopentech.odatajclient.engine.data.Feed;
import com.msopentech.odatajclient.engine.uri.SegmentType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * List of entries, represented via JSON.
 *
 * @see JSONEntry
 */
public class JSONFeed extends AbstractPayloadObject implements Feed {

    private static final long serialVersionUID = -3576372289800799417L;

    @JsonProperty(value = "odata.metadata", required = false)
    private URI metadata;

    @JsonProperty(value = "odata.count", required = false)
    private Integer count;

    @JsonProperty("value")
    private final List<JSONEntry> entries;

    @JsonProperty(value = "odata.nextLink", required = false)
    private String next;

    /**
     * Constructor.
     */
    public JSONFeed() {
        super();
        entries = new ArrayList<JSONEntry>();
    }

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

    /**
     * {@inheritDoc }
     */
    @JsonIgnore
    @Override
    public Integer getCount() {
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<JSONEntry> getEntries() {
        return entries;
    }

    /**
     * Add entry.
     *
     * @param entry entry.
     * @return 'TRUE' in case of success; 'FALSE' otherwise.
     */
    public boolean addEntry(final JSONEntry entry) {
        return this.entries.add(entry);
    }

    /**
     * {@inheritDoc }
     */
    @JsonIgnore
    @Override
    public void setEntries(final List<Entry> entries) {
        this.entries.clear();
        for (Entry entry : entries) {
            if (entry instanceof JSONEntry) {
                this.entries.add((JSONEntry) entry);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @JsonIgnore
    @Override
    public void setNext(final URI next) {
        this.next = next.toASCIIString();
    }

    /**
     * {@inheritDoc }
     */
    @JsonIgnore
    @Override
    public URI getNext() {
        return next == null ? null : URI.create(next);
    }
}
