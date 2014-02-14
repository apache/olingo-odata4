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

import com.msopentech.odatajclient.engine.data.LinkCollection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class XMLLinkCollection implements LinkCollection {

    private final List<URI> links = new ArrayList<URI>();

    private URI next;

    /**
     * Constructor.
     */
    public XMLLinkCollection() {
    }

    /**
     * Constructor.
     *
     * @param next next page link.
     */
    public XMLLinkCollection(final URI next) {
        this.next = next;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<URI> getLinks() {
        return links;
    }

    /**
     * Set Links.
     *
     * @param links links.
     */
    public void setLinks(final List<URI> links) {
        this.links.clear();
        this.links.addAll(links);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setNext(final URI next) {
        this.next = next;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URI getNext() {
        return next;
    }
}
