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

import java.io.Serializable;
import java.net.URI;
import org.w3c.dom.Element;

public interface ODataBinder extends Serializable {

    /**
     * Gets <tt>ODataServiceDocument</tt> from the given service document resource.
     *
     * @param resource service document resource.
     * @return <tt>ODataServiceDocument</tt> object.
     */
    ODataServiceDocument getODataServiceDocument(ServiceDocument resource);

    /**
     * Gets a <tt>FeedResource</tt> from the given OData entity set.
     *
     * @param <T> feed resource type.
     * @param feed OData entity set.
     * @param reference reference class.
     * @return <tt>FeedResource</tt> object.
     */
    <T extends Feed> T getFeed(ODataEntitySet feed, Class<T> reference);

    /**
     * Gets an <tt>EntryResource</tt> from the given OData entity.
     *
     * @param <T> entry resource type.
     * @param entity OData entity.
     * @param reference reference class.
     * @return <tt>EntryResource</tt> object.
     */
    <T extends Entry> T getEntry(ODataEntity entity, Class<T> reference);

    /**
     * Gets an <tt>EntryResource</tt> from the given OData entity.
     *
     * @param <T> entry resource type.
     * @param entity OData entity.
     * @param reference reference class.
     * @param setType whether to explicitly output type information.
     * @return <tt>EntryResource</tt> object.
     */
    <T extends Entry> T getEntry(ODataEntity entity, Class<T> reference, boolean setType);

    /**
     * Gets the given OData property as DOM element.
     *
     * @param prop OData property.
     * @return <tt>Element</tt> object.
     */
    Element toDOMElement(ODataProperty prop);

    ODataLinkCollection getLinkCollection(LinkCollection linkCollection);

    /**
     * Gets <tt>ODataEntitySet</tt> from the given feed resource.
     *
     * @param resource feed resource.
     * @return <tt>ODataEntitySet</tt> object.
     */
    ODataEntitySet getODataEntitySet(Feed resource);

    /**
     * Gets <tt>ODataEntitySet</tt> from the given feed resource.
     *
     * @param resource feed resource.
     * @param defaultBaseURI default base URI.
     * @return <tt>ODataEntitySet</tt> object.
     */
    ODataEntitySet getODataEntitySet(Feed resource, URI defaultBaseURI);

    /**
     * Gets <tt>ODataEntity</tt> from the given entry resource.
     *
     * @param resource entry resource.
     * @return <tt>ODataEntity</tt> object.
     */
    ODataEntity getODataEntity(Entry resource);

    /**
     * Gets <tt>ODataEntity</tt> from the given entry resource.
     *
     * @param resource entry resource.
     * @param defaultBaseURI default base URI.
     * @return <tt>ODataEntity</tt> object.
     */
    ODataEntity getODataEntity(Entry resource, URI defaultBaseURI);

    /**
     * Gets a <tt>LinkResource</tt> from the given OData link.
     *
     * @param <T> link resource type.
     * @param link OData link.
     * @param reference reference class.
     * @return <tt>LinkResource</tt> object.
     */
    @SuppressWarnings("unchecked")
    <T extends Link> T getLinkResource(ODataLink link, Class<T> reference);

    /**
     * Gets an <tt>ODataProperty</tt> from the given DOM element.
     *
     * @param property content.
     * @return <tt>ODataProperty</tt> object.
     */
    ODataProperty getProperty(Element property);
}
