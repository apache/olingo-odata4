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
package com.msopentech.odatajclient.engine.data;

/**
 * REST resource for an <tt>ODataLink</tt>.
 *
 * @see ODataLink
 */
public interface Link {

    /**
     * Gets rel info.
     *
     * @return rel info.
     */
    String getRel();

    /**
     * Sets rel info.
     *
     * @param rel rel info.
     */
    void setRel(String rel);

    /**
     * Gets type.
     *
     * @return type.
     */
    String getType();

    /**
     * Sets type.
     *
     * @param type type.
     */
    void setType(String type);

    /**
     * Gets title.
     *
     * @return title.
     */
    String getTitle();

    /**
     * Sets title.
     *
     * @param title title.
     */
    void setTitle(String title);

    /**
     * Gets href.
     *
     * @return href.
     */
    String getHref();

    /**
     * Sets href.
     *
     * @param href href.
     */
    void setHref(String href);

    /**
     * Gets in-line entry.
     *
     * @return in-line entry.
     */
    Entry getInlineEntry();

    /**
     * Sets in-line entry.
     *
     * @param entry entry.
     */
    void setInlineEntry(Entry entry);

    /**
     * Gets in-line feed.
     *
     * @return in-line feed.
     */
    Feed getInlineFeed();

    /**
     * Sets in-line feed.
     *
     * @param feed feed.
     */
    void setInlineFeed(Feed feed);
}
