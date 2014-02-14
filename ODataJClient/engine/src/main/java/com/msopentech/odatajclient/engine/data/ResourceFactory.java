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

import com.msopentech.odatajclient.engine.data.impl.v3.AtomEntry;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomFeed;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomLink;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONEntry;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONFeed;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONLink;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class ResourceFactory {

    /**
     * Gets a new instance of <tt>Feed</tt>.
     *
     * @param <T> resource type.
     * @param resourceClass reference class.
     * @return <tt>Feed</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Feed> T newFeed(final Class<T> resourceClass) {
        T result = null;

        if (AtomFeed.class.equals(resourceClass)) {
            result = (T) new AtomFeed();
        }
        if (JSONFeed.class.equals(resourceClass)) {
            result = (T) new JSONFeed();
        }

        return result;
    }

    /**
     * Gets a new instance of <tt>Entry</tt>.
     *
     * @param <T> resource type.
     * @param resourceClass reference class.
     * @return <tt>Entry</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entry> T newEntry(final Class<T> resourceClass) {
        T result = null;

        if (AtomEntry.class.equals(resourceClass)) {
            result = (T) new AtomEntry();
        }
        if (JSONEntry.class.equals(resourceClass)) {
            result = (T) new JSONEntry();
        }

        return result;
    }

    /**
     * Gets a new instance of <tt>Link</tt>.
     *
     * @param <T> resource type.
     * @param resourceClass reference class.
     * @return <tt>Link</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Link> T newLink(final Class<T> resourceClass) {
        T result = null;

        if (AtomLink.class.equals(resourceClass)) {
            result = (T) new AtomLink();
        }
        if (JSONLink.class.equals(resourceClass)) {
            result = (T) new JSONLink();
        }

        return result;
    }

    /**
     * Gets feed reference class from the given format.
     *
     * @param <T> resource type.
     * @param format format.
     * @return resource reference class.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Feed> Class<T> feedClassForFormat(final ODataPubFormat format) {
        Class<T> result = null;

        switch (format) {
            case ATOM:
                result = (Class<T>) AtomFeed.class;
                break;

            case JSON:
            case JSON_FULL_METADATA:
            case JSON_NO_METADATA:
                result = (Class<T>) JSONFeed.class;
                break;
        }

        return result;
    }

    /**
     * Gets entry reference class from the given format.
     *
     * @param <T> resource type.
     * @param format format.
     * @return resource reference class.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entry> Class<T> entryClassForFormat(final ODataPubFormat format) {
        Class<T> result = null;

        switch (format) {
            case ATOM:
                result = (Class<T>) AtomEntry.class;
                break;

            case JSON:
            case JSON_FULL_METADATA:
            case JSON_NO_METADATA:
                result = (Class<T>) JSONEntry.class;
                break;
        }

        return result;
    }

    /**
     * Gets <tt>Link</tt> object from feed resource.
     *
     * @param <T> link resource type.
     * @param <K> feed resource type.
     * @param resourceClass feed reference class.
     * @return <tt>Link</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Link, K extends Feed> T newLinkForFeed(final Class<K> resourceClass) {
        T result = null;

        if (AtomFeed.class.equals(resourceClass)) {
            result = (T) new AtomLink();
        }
        if (JSONFeed.class.equals(resourceClass)) {
            result = (T) new JSONLink();
        }

        return result;
    }

    /**
     * Gets <tt>Link</tt> object from entry resource.
     *
     * @param <T> link resource type.
     * @param <K> entry resource type.
     * @param resourceClass entry reference class.
     * @return <tt>Link</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Link, K extends Entry> T newLinkForEntry(final Class<K> resourceClass) {
        T result = null;

        if (AtomEntry.class.equals(resourceClass)) {
            result = (T) new AtomLink();
        }
        if (JSONEntry.class.equals(resourceClass)) {
            result = (T) new JSONLink();
        }

        return result;
    }

    /**
     * Gets <tt>Feed</tt> object from link resource.
     *
     * @param <T> link resource type.
     * @param <K> feed resource type.
     * @param resourceClass link reference class.
     * @return <tt>Feed</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Link, K extends Feed> Class<K> feedClassForLink(
            final Class<T> resourceClass) {

        Class<K> result = null;

        if (AtomLink.class.equals(resourceClass)) {
            result = (Class<K>) AtomFeed.class;
        }
        if (JSONLink.class.equals(resourceClass)) {
            result = (Class<K>) JSONFeed.class;
        }

        return result;
    }

    /**
     * Gets <tt>Link</tt> object from entry resource.
     *
     * @param <T> link resource type.
     * @param <K> entry resource type.
     * @param resourceClass entry reference class.
     * @return <tt>Link</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Link, K extends Entry> Class<T> linkClassForEntry(
            final Class<K> resourceClass) {

        Class<T> result = null;

        if (AtomEntry.class.equals(resourceClass)) {
            result = (Class<T>) AtomLink.class;
        }
        if (JSONEntry.class.equals(resourceClass)) {
            result = (Class<T>) JSONLink.class;
        }

        return result;
    }

    /**
     * Gets <tt>Entry</tt> object from link resource.
     *
     * @param <T> link resource type.
     * @param <K> entry resource type.
     * @param resourceClass link reference class.
     * @return <tt>Entry</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Link, K extends Entry> Class<K> entryClassForLink(final Class<T> resourceClass) {
        Class<K> result = null;

        if (AtomLink.class.equals(resourceClass)) {
            result = (Class<K>) AtomEntry.class;
        }
        if (JSONLink.class.equals(resourceClass)) {
            result = (Class<K>) JSONEntry.class;
        }

        return result;
    }

    /**
     * Gets <tt>Entry</tt> object from feed resource.
     *
     * @param <T> feed resource type.
     * @param <K> entry resource type.
     * @param resourceClass feed reference class.
     * @return <tt>Entry</tt> object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Feed, K extends Entry> Class<K> entryClassForFeed(final Class<T> resourceClass) {
        Class<K> result = null;

        if (AtomFeed.class.equals(resourceClass)) {
            result = (Class<K>) AtomEntry.class;
        }
        if (JSONFeed.class.equals(resourceClass)) {
            result = (Class<K>) JSONEntry.class;
        }

        return result;
    }
}
