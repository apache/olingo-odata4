/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

public class XMLEventReaderWrapper implements XMLEventReader {

  private static final Charset ENCODING = Charset.forName(org.apache.olingo.commons.api.Constants.UTF8);

  public final static String CONTENT = "CONTENT_TAG";

  public final String CONTENT_STAG;

  public final static String CONTENT_ETAG = "</" + CONTENT + ">";

  private final XMLEventReader wrapped;

  private XMLEvent nextGivenEvent = null;

  public XMLEventReaderWrapper(final InputStream stream)
      throws IOException, XMLStreamException {
    final StringBuilder startBuilder = new StringBuilder();
    startBuilder.append("<").append(CONTENT).
    append(" xmlns:m").append("=\"").append(Constants.get(ConstantKey.METADATA_NS)).append("\"").
    append(" xmlns:d").append("=\"").append(Constants.get(ConstantKey.DATASERVICES_NS)).append("\"").
    append(" xmlns:georss").append("=\"").append(Constants.get(ConstantKey.GEORSS_NS)).append("\"").
    append(" xmlns:gml").append("=\"").append(Constants.get(ConstantKey.GML_NS)).append("\"").
    append(">");

    CONTENT_STAG = startBuilder.toString();

    final XMLInputFactory factory = XMLInputFactory.newInstance();

    final InputStreamReader reader = new InputStreamReader(
        new ByteArrayInputStream((CONTENT_STAG
            + IOUtils.toString(stream, ENCODING).replaceAll("^<\\?xml.*\\?>", "")
            + XMLEventReaderWrapper.CONTENT_ETAG).getBytes(ENCODING)),
            Constants.DECODER);

    wrapped = factory.createXMLEventReader(reader);

    init();
  }

  private void init() {

    try {
      do {

        nextGivenEvent = wrapped.nextEvent();

      } while (nextGivenEvent.isStartDocument()
          || (nextGivenEvent.isStartElement()
              && CONTENT.equals(nextGivenEvent.asStartElement().getName().getLocalPart())));

    } catch (Exception ignore) {
      // ignore
    }
  }

  @Override
  public XMLEvent nextEvent() throws XMLStreamException {
    final XMLEvent event = nextGivenEvent;

    if (!isValidEvent(event)) {
      throw new IllegalStateException("No event found");
    }

    nextGivenEvent = wrapped.hasNext() ? wrapped.nextEvent() : null;

    return event;
  }

  @Override
  public boolean hasNext() {
    return isValidEvent(nextGivenEvent);
  }

  @Override
  public XMLEvent peek() throws XMLStreamException {
    return wrapped.peek();
  }

  @Override
  public String getElementText() throws XMLStreamException {
    return wrapped.getElementText();
  }

  @Override
  public XMLEvent nextTag() throws XMLStreamException {
    XMLEvent tagEvent = wrapped.nextTag();
    if (isValidEvent(tagEvent)) {
      return tagEvent;
    } else {
      return null;
    }
  }

  @Override
  public Object getProperty(final String string) throws IllegalArgumentException {
    return wrapped.getProperty(string);
  }

  @Override
  public void close() throws XMLStreamException {
    wrapped.close();
  }

  @Override
  public Object next() {
    return wrapped.next();
  }

  @Override
  public void remove() {
    wrapped.remove();
  }

  private boolean isValidEvent(final XMLEvent event) {
    // discard content end element tag ...
    return event != null
        && (!event.isEndElement()
            || !CONTENT.equals(event.asEndElement().getName().getLocalPart()));
  }
}
