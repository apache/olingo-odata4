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
package com.msopentech.odatajclient.testservice.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlElement {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(XmlElement.class);

    private final static String CONTENT = "CONTENT_TAG";

    private final static String CONTENT_STAG = "<" + CONTENT + ">";

    private final static String CONTENT_ETAG = "</" + CONTENT + ">";

    private StartElement start;

    private EndElement end;

    private ByteArrayOutputStream content = new ByteArrayOutputStream();

    public StartElement getStart() {
        return start;
    }

    public void setStart(StartElement start) {
        this.start = start;
    }

    public EndElement getEnd() {
        return end;
    }

    public void setEnd(EndElement end) {
        this.end = end;
    }

    public InputStream getContent() throws XMLStreamException {
        return new ByteArrayInputStream(content.toByteArray());
    }

    public XMLEventReader getContentReader() throws Exception {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);


        final XMLEventReader contentReader = factory.createXMLEventReader(
                new ByteArrayInputStream((CONTENT_STAG + IOUtils.toString(getContent()) + CONTENT_ETAG).getBytes()));

        return new XMLEventReaderWrapper(contentReader);
    }

    public void setContent(final InputStream content) throws IOException {
        this.content.reset();
        IOUtils.copyLarge(content, this.content);
        content.close();
    }

    private class XMLEventReaderWrapper implements XMLEventReader {

        private final XMLEventReader wrapped;

        private XMLEvent nextGivenEvent = null;

        public XMLEventReaderWrapper(XMLEventReader wrapped) {
            this.wrapped = wrapped;

            try {
                this.nextGivenEvent = this.wrapped.nextEvent();

                if (this.nextGivenEvent.isStartDocument()) {
                    this.nextGivenEvent = this.wrapped.nextEvent(); // discard start document    
                }

                if (this.nextGivenEvent.isStartElement()
                        && CONTENT.equals(this.nextGivenEvent.asStartElement().getName().getLocalPart())) {
                    this.nextGivenEvent = this.wrapped.nextEvent(); // discard content start tag
                }

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

    public InputStream toStream() throws Exception {
        InputStream res;
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final OutputStreamWriter osw = new OutputStreamWriter(bos);

            getStart().writeAsEncodedUnicode(osw);
            osw.flush();

            IOUtils.copy(getContent(), bos);

            getEnd().writeAsEncodedUnicode(osw);
            osw.flush();
            osw.close();

            res = new ByteArrayInputStream(bos.toByteArray());
        } catch (Exception e) {
            LOG.error("Error serializing elemnt", e);
            res = null;
        }
        return res;
    }
}
