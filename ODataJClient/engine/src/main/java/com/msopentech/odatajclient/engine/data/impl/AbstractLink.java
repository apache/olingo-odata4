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
package com.msopentech.odatajclient.engine.data.impl;

import com.msopentech.odatajclient.engine.data.Entry;
import com.msopentech.odatajclient.engine.data.Feed;
import com.msopentech.odatajclient.engine.data.Link;

/**
 * Abstract base for classes implementing <tt>LinkResource</tt>.
 */
public abstract class AbstractLink<ENTRY extends Entry, FEED extends Feed>
        extends AbstractPayloadObject implements Link {

    private static final long serialVersionUID = -3449344217160035501L;

    private String title;

    private String rel;

    private String href;

    private String type;

    private ENTRY entry;

    private FEED feed;

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getRel() {
        return rel;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setRel(final String rel) {
        this.rel = rel;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Entry getInlineEntry() {
        return entry;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setInlineEntry(final Entry entry) {
        this.entry = (ENTRY) entry;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feed getInlineFeed() {
        return feed;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setInlineFeed(final Feed feed) {
        this.feed = (FEED) feed;
    }
}
