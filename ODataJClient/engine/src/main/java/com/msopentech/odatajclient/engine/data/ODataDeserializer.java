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

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdmx;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.io.InputStream;
import java.io.Serializable;
import org.w3c.dom.Element;

/**
 * Utility class for serialization.
 */
public interface ODataDeserializer extends Serializable {

    AbstractEdmx<?, ?, ?, ?, ?, ?> toMetadata(InputStream input);

    /**
     * Gets the ServiceDocumentResource object represented by the given InputStream.
     *
     * @param input stream to be de-serialized.
     * @param format OData service document format.
     * @return ServiceDocumentResource object.
     */
    ServiceDocument toServiceDocument(InputStream input, ODataFormat format);

    /**
     * Gets a feed object from the given InputStream.
     *
     * @param <T> reference class type
     * @param input stream to be de-serialized.
     * @param reference reference class (AtomFeed.class, JSONFeed.class).
     * @return FeedResource instance.
     */
    <T extends Feed> T toFeed(InputStream input, Class<T> reference);

    /**
     * Gets an entry object from the given InputStream.
     *
     * @param <T> reference class type
     * @param input stream to be de-serialized.
     * @param reference reference class (AtomEntry.class, JSONV3Entry.class).
     * @return EntryResource instance.
     */
    <T extends Entry> T toEntry(InputStream input, Class<T> reference);

    /**
     * Gets a DOM representation of the given InputStream.
     *
     * @param input stream to be de-serialized.
     * @param format OData format.
     * @return DOM.
     */
    Element toPropertyDOM(InputStream input, ODataFormat format);

    /**
     * Gets a list of links from the given InputStream.
     *
     * @param input stream to be de-serialized.
     * @param format OData format.
     * @return de-serialized links.
     */
    LinkCollection toLinkCollection(InputStream input, ODataFormat format);

    /**
     * Gets the ODataError object represented by the given InputStream.
     *
     * @param input stream to be parsed and de-serialized.
     * @param isXML 'TRUE' if the error is represented by XML; 'FALSE' otherwise.
     * @return
     */
    ODataError toODataError(InputStream input, boolean isXML);

    /**
     * Parses the given input into a DOM tree.
     *
     * @param input stream to be parsed and de-serialized.
     * @return DOM tree
     */
    Element toDOM(InputStream input);
}
