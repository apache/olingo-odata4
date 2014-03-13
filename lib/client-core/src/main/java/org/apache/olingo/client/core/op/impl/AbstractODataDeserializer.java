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
package org.apache.olingo.client.core.op.impl;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.client.api.data.Entry;
import org.apache.olingo.client.api.data.Error;
import org.apache.olingo.client.api.data.Feed;
import org.apache.olingo.client.api.data.LinkCollection;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.api.op.ODataDeserializer;
import org.apache.olingo.client.core.data.AtomDeserializer;
import org.apache.olingo.client.core.data.JSONEntryImpl;
import org.apache.olingo.client.core.data.JSONErrorBundle;
import org.apache.olingo.client.core.data.JSONFeedImpl;
import org.apache.olingo.client.core.data.JSONLinkCollectionImpl;
import org.apache.olingo.client.core.data.JSONPropertyImpl;
import org.apache.olingo.client.core.data.XMLErrorImpl;
import org.apache.olingo.client.core.data.XMLLinkCollectionImpl;
import org.apache.olingo.client.core.xml.XMLParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractODataDeserializer extends AbstractJacksonTool implements ODataDeserializer {

  private static final long serialVersionUID = -4244158979195609909L;

  private final AtomDeserializer atomDeserializer;

  public AbstractODataDeserializer(final ODataClient client) {
    super(client);
    this.atomDeserializer = new AtomDeserializer(client);
  }

  @Override
  public Feed toFeed(final InputStream input, final ODataPubFormat format) {
    return format == ODataPubFormat.ATOM
            ? toAtomFeed(input)
            : toJSONFeed(input);
  }

  @Override
  public Entry toEntry(final InputStream input, final ODataPubFormat format) {
    return format == ODataPubFormat.ATOM
            ? toAtomEntry(input)
            : toJSONEntry(input);
  }

  @Override
  public Element toPropertyDOM(final InputStream input, final ODataFormat format) {
    return format == ODataFormat.XML
            ? toPropertyDOMFromXML(input)
            : toPropertyDOMFromJSON(input);
  }

  @Override
  public LinkCollection toLinkCollection(final InputStream input, final ODataFormat format) {
    return format == ODataFormat.XML
            ? toLinkCollectionFromXML(input)
            : toLinkCollectionFromJSON(input);
  }

  @Override
  public Error toError(final InputStream input, final boolean isXML) {
    return isXML
            ? toErrorFromXML(input)
            : toErrorFromJSON(input);
  }

  @Override
  public Element toDOM(final InputStream input) {
    return XMLParser.PARSER.deserialize(input);
  }

  /*
   * ------------------ Protected methods ------------------
   */
  protected Feed toAtomFeed(final InputStream input) {
    try {
      return atomDeserializer.feed(toDOM(input));
    } catch (Exception e) {
      throw new IllegalArgumentException("While deserializing Atom feed", e);
    }
  }

  protected Entry toAtomEntry(final InputStream input) {
    try {
      return atomDeserializer.entry(toDOM(input));
    } catch (Exception e) {
      throw new IllegalArgumentException("While deserializing Atom entry", e);
    }
  }

  protected Feed toJSONFeed(final InputStream input) {
    try {
      return getObjectMapper().readValue(input, JSONFeedImpl.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("While deserializing JSON feed", e);
    }
  }

  protected Entry toJSONEntry(final InputStream input) {
    try {
      return getObjectMapper().readValue(input, JSONEntryImpl.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("While deserializing JSON entry", e);
    }
  }

  protected Element toPropertyDOMFromXML(final InputStream input) {
    return toDOM(input);
  }

  protected Element toPropertyDOMFromJSON(final InputStream input) {
    try {
      return getObjectMapper().readValue(input, JSONPropertyImpl.class).getContent();
    } catch (IOException e) {
      throw new IllegalArgumentException("While deserializing JSON property", e);
    }
  }

  protected XMLLinkCollectionImpl toLinkCollectionFromXML(final InputStream input) {
    final Element root = toDOM(input);

    final NodeList uris = root.getOwnerDocument().getElementsByTagName(Constants.ELEM_URI);

    final NodeList next = root.getElementsByTagName(Constants.NEXT_LINK_REL);
    final XMLLinkCollectionImpl linkCollection = next.getLength() > 0
            ? new XMLLinkCollectionImpl(URI.create(next.item(0).getTextContent()))
            : new XMLLinkCollectionImpl();
    for (int i = 0; i < uris.getLength(); i++) {
      linkCollection.getLinks().add(URI.create(uris.item(i).getTextContent()));
    }

    return linkCollection;
  }

  protected JSONLinkCollectionImpl toLinkCollectionFromJSON(final InputStream input) {
    try {
      return getObjectMapper().readValue(input, JSONLinkCollectionImpl.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("While deserializing JSON $links", e);
    }
  }

  protected Error toErrorFromXML(final InputStream input) {
    try {
      final XmlMapper xmlMapper = new XmlMapper(
              new XmlFactory(new InputFactoryImpl(), new OutputFactoryImpl()), new JacksonXmlModule());
      return xmlMapper.readValue(input, XMLErrorImpl.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("While deserializing XML error", e);
    }
  }

  protected Error toErrorFromJSON(final InputStream input) {
    try {
      return getObjectMapper().readValue(input, JSONErrorBundle.class).getError();
    } catch (IOException e) {
      throw new IllegalArgumentException("While deserializing JSON error", e);
    }
  }
}
