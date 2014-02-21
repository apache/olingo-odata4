/*
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
package org.apache.olingo.odata4.client.core.data.impl;

import java.io.InputStream;
import org.apache.olingo.odata4.client.api.ODataClient;
import org.apache.olingo.odata4.client.api.data.ODataDeserializer;
import org.apache.olingo.odata4.client.core.utils.XMLUtils;
import org.w3c.dom.Element;

public abstract class AbstractODataDeserializer extends AbstractJacksonTool implements ODataDeserializer {

  private static final long serialVersionUID = -4244158979195609909L;

//    private final AtomDeserializer atomDeserializer;
  public AbstractODataDeserializer(final ODataClient client) {
    super(client);
//        this.atomDeserializer = new AtomDeserializer(client);
  }

//    @Override
//    @SuppressWarnings("unchecked")
//    public <T extends Feed> T toFeed(final InputStream input, final Class<T> reference) {
//        T entry;
//
//        if (AtomFeed.class.equals(reference)) {
//            entry = (T) toAtomFeed(input);
//        } else {
//            entry = (T) toJSONFeed(input);
//        }
//
//        return entry;
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public <T extends Entry> T toEntry(final InputStream input, final Class<T> reference) {
//        T entry;
//
//        if (AtomEntry.class.equals(reference)) {
//            entry = (T) toAtomEntry(input);
//
//        } else {
//            entry = (T) toJSONEntry(input);
//        }
//
//        return entry;
//    }
//
//    @Override
//    public Element toPropertyDOM(final InputStream input, final ODataFormat format) {
//        return format == ODataFormat.XML
//                ? toPropertyDOMFromXML(input)
//                : toPropertyDOMFromJSON(input);
//    }
//
//    @Override
//    public LinkCollection toLinkCollection(final InputStream input, final ODataFormat format) {
//        return format == ODataFormat.XML
//                ? toLinkCollectionFromXML(input)
//                : toLinkCollectionFromJSON(input);
//    }
//
//    @Override
//    public ODataError toODataError(final InputStream input, final boolean isXML) {
//        return isXML
//                ? toODataErrorFromXML(input)
//                : toODataErrorFromJSON(input);
//    }
//
  @Override
  public Element toDOM(final InputStream input) {
    return XMLUtils.PARSER.deserialize(input);
  }
//
//    /*
//     * ------------------ Protected methods ------------------
//     */
//    protected AtomFeed toAtomFeed(final InputStream input) {
//        try {
//            return atomDeserializer.feed(toDOM(input));
//        } catch (Exception e) {
//            throw new IllegalArgumentException("While deserializing Atom feed", e);
//        }
//    }
//
//    protected AtomEntry toAtomEntry(final InputStream input) {
//        try {
//            return atomDeserializer.entry(toDOM(input));
//        } catch (Exception e) {
//            throw new IllegalArgumentException("While deserializing Atom entry", e);
//        }
//    }
//
//    protected JSONFeed toJSONFeed(final InputStream input) {
//        try {
//            return getObjectMapper().readValue(input, JSONFeed.class);
//        } catch (IOException e) {
//            throw new IllegalArgumentException("While deserializing JSON feed", e);
//        }
//    }
//
//    protected abstract AbstractJSONEntry toJSONEntry(final InputStream input);
//
//    protected Element toPropertyDOMFromXML(final InputStream input) {
//        return toDOM(input);
//    }
//
//    protected Element toPropertyDOMFromJSON(final InputStream input) {
//        try {
//            return getObjectMapper().readValue(input, JSONProperty.class).getContent();
//        } catch (IOException e) {
//            throw new IllegalArgumentException("While deserializing JSON property", e);
//        }
//    }
//
//    protected XMLLinkCollection toLinkCollectionFromXML(final InputStream input) {
//        final Element root = toDOM(input);
//
//        final NodeList uris = root.getOwnerDocument().getElementsByTagName(ODataConstants.ELEM_URI);
//
//        final List<URI> links = new ArrayList<URI>();
//        for (int i = 0; i < uris.getLength(); i++) {
//            links.add(URI.create(uris.item(i).getTextContent()));
//        }
//
//        final NodeList next = root.getElementsByTagName(ODataConstants.NEXT_LINK_REL);
//        final XMLLinkCollection linkCollection = next.getLength() > 0
//                ? new XMLLinkCollection(URI.create(next.item(0).getTextContent()))
//                : new XMLLinkCollection();
//        linkCollection.setLinks(links);
//
//        return linkCollection;
//    }
//
//    protected JSONLinkCollection toLinkCollectionFromJSON(final InputStream input) {
//        try {
//            return getObjectMapper().readValue(input, JSONLinkCollection.class);
//        } catch (IOException e) {
//            throw new IllegalArgumentException("While deserializing JSON $links", e);
//        }
//    }
//
//    protected XMLODataError toODataErrorFromXML(final InputStream input) {
//        try {
//            final XmlMapper xmlMapper = new XmlMapper(
//                    new XmlFactory(new InputFactoryImpl(), new OutputFactoryImpl()), new JacksonXmlModule());
//            return xmlMapper.readValue(input, XMLODataError.class);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("While deserializing XML error", e);
//        }
//    }
//
//    protected JSONODataError toODataErrorFromJSON(final InputStream input) {
//        try {
//            return getObjectMapper().readValue(input, JSONODataErrorBundle.class).getError();
//        } catch (IOException e) {
//            throw new IllegalArgumentException("While deserializing JSON error", e);
//        }
//    }
}
