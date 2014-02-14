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
package com.msopentech.odatajclient.engine.data;

import java.net.URI;
import java.util.List;

/**
 * Link collection wrapper.
 */
public class ODataLinkCollection {

    /**
     * Link to the next page.
     */
    private URI next;

    /**
     * Contained links.
     */
    private List<URI> links;

    /**
     * Constructor.
     */
    public ODataLinkCollection() {
    }

    /**
     * Adds link to the collection.
     *
     * @param link link to be added.
     * @return 'TRUE' in case of success; 'FALSE' otherwise.
     */
    public boolean addLink(final URI link) {
        return links.add(link);
    }

    /**
     * Removes a link.
     *
     * @param link link to be removed.
     * @return 'TRUE' in case of success; 'FALSE' otherwise.
     */
    public boolean removeLink(final URI link) {
        return links.remove(link);
    }

    /**
     * Set links.
     *
     * @param links links.
     */
    public void setLinks(final List<URI> links) {
        this.links = links;
    }

    /**
     * Gets contained links.
     *
     * @return list of links.
     */
    public List<URI> getLinks() {
        return links;
    }

    /**
     * Constructor.
     *
     * @param next next page link.
     */
    public ODataLinkCollection(final URI next) {
        this.next = next;
    }

    /**
     * Gets next page link.
     *
     * @return next page link; null value if single page or last page reached.
     */
    public URI getNext() {
        return next;
    }
}
