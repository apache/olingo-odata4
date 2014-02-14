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

import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Utility class for serialization.
 */
public interface ODataSerializer extends Serializable {

    /**
     * Writes <tt>FeedResource</tt> object onto the given stream.
     *
     * @param <T> feed resource type.
     * @param obj object to be streamed.
     * @param out output stream.
     */
    <T extends Feed> void feed(T obj, OutputStream out);

    /**
     * Writes <tt>FeedResource</tt> object by the given writer.
     *
     * @param <T> feed resource type.
     * @param obj object to be streamed.
     * @param writer writer.
     */
    <T extends Feed> void feed(T obj, Writer writer);

    /**
     * Writes <tt>EntryResource</tt> object onto the given stream.
     *
     * @param <T> entry resource type.
     * @param obj object to be streamed.
     * @param out output stream.
     */
    <T extends Entry> void entry(T obj, OutputStream out);

    /**
     * Writes <tt>EntryResource</tt> object by the given writer.
     *
     * @param <T> entry resource type.
     * @param obj object to be streamed.
     * @param writer writer.
     */
    <T extends Entry> void entry(T obj, Writer writer);

    /**
     * Writes entry content onto the given stream.
     *
     * @param element element to be streamed.
     * @param format streaming format.
     * @param out output stream.
     */
    void property(Element element, ODataFormat format, OutputStream out);

    /**
     * Writes entry content by the given writer.
     *
     * @param element element to be streamed.
     * @param format streaming format.
     * @param writer writer.
     */
    void property(Element element, ODataFormat format, Writer writer);

    /**
     * Writes OData link onto the given stream.
     *
     * @param link OData link to be streamed.
     * @param format streaming format.
     * @param out output stream.
     */
    void link(ODataLink link, ODataFormat format, OutputStream out);

    /**
     * Writes OData link by the given writer.
     *
     * @param link OData link to be streamed.
     * @param format streaming format.
     * @param writer writer.
     */
    void link(ODataLink link, ODataFormat format, Writer writer);

    /**
     * Writes DOM object onto the given stream.
     *
     * @param content DOM to be streamed.
     * @param out output stream.
     */
    void dom(Node content, OutputStream out);

    /**
     * Writes DOM object by the given writer.
     *
     * @param content DOM to be streamed.
     * @param writer writer.
     */
    void dom(Node content, Writer writer);
}
