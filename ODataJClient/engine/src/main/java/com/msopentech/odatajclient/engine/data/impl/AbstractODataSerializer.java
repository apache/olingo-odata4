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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.Entry;
import com.msopentech.odatajclient.engine.data.Feed;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataSerializer;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomEntry;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomFeed;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomSerializer;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONFeed;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONProperty;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONEntry;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import com.msopentech.odatajclient.engine.utils.XMLUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractODataSerializer extends AbstractJacksonTool implements ODataSerializer {

    private static final long serialVersionUID = -357777648541325363L;

    private final AtomSerializer atomSerializer;

    public AbstractODataSerializer(final ODataClient client) {
        super(client);
        this.atomSerializer = new AtomSerializer(client);
    }

    @Override
    public <T extends Feed> void feed(final T obj, final OutputStream out) {
        feed(obj, new OutputStreamWriter(out));
    }

    @Override
    public <T extends Feed> void feed(final T obj, final Writer writer) {
        if (obj instanceof AtomFeed) {
            atom((AtomFeed) obj, writer);
        } else {
            json((JSONFeed) obj, writer);
        }
    }

    @Override
    public <T extends Entry> void entry(final T obj, final OutputStream out) {
        entry(obj, new OutputStreamWriter(out));
    }

    @Override
    public <T extends Entry> void entry(final T obj, final Writer writer) {
        if (obj instanceof AtomEntry) {
            atom((AtomEntry) obj, writer);
        } else {
            json((JSONEntry) obj, writer);
        }
    }

    @Override
    public void property(final Element element, final ODataFormat format, final OutputStream out) {
        property(element, format, new OutputStreamWriter(out));
    }

    @Override
    public void property(final Element element, final ODataFormat format, final Writer writer) {
        if (format == ODataFormat.XML) {
            dom(element, writer);
        } else {
            json(element, writer);
        }
    }

    @Override
    public void link(final ODataLink link, final ODataFormat format, final OutputStream out) {
        link(link, format, new OutputStreamWriter(out));
    }

    @Override
    public void link(final ODataLink link, final ODataFormat format, final Writer writer) {
        if (format == ODataFormat.XML) {
            xmlLink(link, writer);
        } else {
            jsonLink(link, writer);
        }
    }

    @Override
    public void dom(final Node content, final OutputStream out) {
        dom(content, new OutputStreamWriter(out));
    }

    @Override
    public void dom(final Node content, final Writer writer) {
        XMLUtils.PARSER.serialize(content, writer);
    }

    /*
     * ------------------ Protected methods ------------------
     */
    protected <T extends AbstractPayloadObject> void atom(final T obj, final Writer writer) {
        try {
            dom(atomSerializer.serialize(obj), writer);
        } catch (Exception e) {
            throw new IllegalArgumentException("While serializing Atom object", e);
        }
    }

    protected <T extends AbstractPayloadObject> void json(final T obj, final Writer writer) {
        try {
            getObjectMapper().writeValue(writer, obj);
        } catch (IOException e) {
            throw new IllegalArgumentException("While serializing JSON object", e);
        }
    }

    protected void json(final Element element, final Writer writer) {
        try {
            final JSONProperty property = new JSONProperty();
            property.setContent(element);
            getObjectMapper().writeValue(writer, property);
        } catch (IOException e) {
            throw new IllegalArgumentException("While serializing JSON property", e);
        }
    }

    protected void xmlLink(final ODataLink link, final Writer writer) {
        try {
            final DocumentBuilder builder = XMLUtils.DOC_BUILDER_FACTORY.newDocumentBuilder();
            final Document doc = builder.newDocument();
            final Element uri = doc.createElementNS(
                    client.getWorkingVersion().getNamespaceMap().get(ODataVersion.NS_DATASERVICES),
                    ODataConstants.ELEM_URI);
            uri.appendChild(doc.createTextNode(link.getLink().toASCIIString()));

            dom(uri, writer);
        } catch (Exception e) {
            throw new IllegalArgumentException("While serializing XML link", e);
        }
    }

    protected void jsonLink(final ODataLink link, final Writer writer) {
        final ObjectMapper mapper = getObjectMapper();
        final ObjectNode uri = mapper.createObjectNode();
        uri.put(ODataConstants.JSON_URL, link.getLink().toASCIIString());

        try {
            mapper.writeValue(writer, uri);
        } catch (Exception e) {
            throw new IllegalArgumentException("While serializing JSON link", e);
        }
    }
}
