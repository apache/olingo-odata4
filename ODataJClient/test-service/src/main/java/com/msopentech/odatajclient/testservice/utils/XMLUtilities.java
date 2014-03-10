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

import static com.msopentech.odatajclient.testservice.utils.Constants.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotFoundException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

public class XMLUtilities extends AbstractUtilities {

    protected static XMLInputFactory factory = null;

    public XMLUtilities(final ODataVersion version) throws Exception {
        super(version);
    }

    @Override
    protected Accept getDefaultFormat() {
        return Accept.ATOM;
    }

    protected static XMLEventReader getEventReader(final InputStream is) throws XMLStreamException {
        if (factory == null) {
            factory = XMLInputFactory.newInstance();
        }
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        return factory.createXMLEventReader(is);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected NavigationLinks retrieveNavigationInfo(
            final String entitySetName, final InputStream is)
            throws Exception {

        final NavigationLinks links = new NavigationLinks();

        final XMLEventReader reader = getEventReader(is);

        try {
            final List<Map.Entry<String, String>> filter = new ArrayList<Map.Entry<String, String>>();
            filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=entry"));
            filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=feed"));

            int startDepth = 0;

            while (true) {
                // a. search for link with type attribute equals to "application/atom+xml;type=entry/feed"
                final Map.Entry<Integer, XmlElement> linkInfo = getAtomElement(
                        reader, null, LINK, filter, startDepth, 2, 2, true);
                final XmlElement link = linkInfo.getValue();
                startDepth = linkInfo.getKey();

                final String title = link.getStart().getAttributeByName(new QName("title")).getValue();
                final String href = link.getStart().getAttributeByName(new QName("href")).getValue();

                try {
                    final XmlElement inlineElement = getAtomElement(link.getContentReader(), null, INLINE);
                    final XMLEventReader inlineReader = inlineElement.getContentReader();

                    try {
                        while (true) {
                            final XmlElement entry = getAtomElement(inlineReader, null, "entry");
                            links.addInlines(title, entry.toStream());
                        }
                    } catch (Exception e) {
                        // Reached the end of document
                    }

                    inlineReader.close();
                } catch (Exception ignore) {
                    // inline element not found (inlines are not mondatory).
                    if (entityUriPattern.matcher(href).matches()) {
                        links.addLinks(title, href.substring(href.lastIndexOf('/') + 1));
                    }
                }
            }
        } catch (Exception ignore) {
            // ignore
        } finally {
            reader.close();
        }

        return links;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected InputStream normalizeLinks(
            final String entitySetName, final String entityKey, final InputStream is, final NavigationLinks links)
            throws Exception {

        // -----------------------------------------
        // 0. Build reader and writer
        // -----------------------------------------
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        is.close();

        final ByteArrayOutputStream tmpBos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(tmpBos);

        final XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));
        // -----------------------------------------

        // -----------------------------------------
        // 1. Normalize links
        // -----------------------------------------
        final Set<String> added = new HashSet<String>();

        try {
            final List<Map.Entry<String, String>> filter = new ArrayList<Map.Entry<String, String>>();
            filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=entry"));
            filter.add(new AbstractMap.SimpleEntry<String, String>("type", "application/atom+xml;type=feed"));

            Map.Entry<Integer, XmlElement> linkInfo = null;

            while (true) {
                // a. search for link with type attribute equals to "application/atom+xml;type=entry/feed"
                linkInfo = getAtomElement(
                        reader, writer, LINK, filter, linkInfo == null ? 0 : linkInfo.getKey(), 2, 2, true);
                final XmlElement link = linkInfo.getValue();

                final String title = link.getStart().getAttributeByName(new QName("title")).getValue();

                if (!added.contains(title)) {
                    added.add(title);

                    final String normalizedLink = String.format(
                            "<link href=\"%s(%s)/%s\" rel=\"%s\" title=\"%s\" type=\"%s\"/>",
                            entitySetName,
                            entityKey,
                            title,
                            link.getStart().getAttributeByName(new QName("rel")).getValue(),
                            title,
                            link.getStart().getAttributeByName(new QName("type")).getValue());

                    addAtomElement(IOUtils.toInputStream(normalizedLink), writer);
                }
            }
        } catch (Exception ignore) {
            // ignore
        } finally {
            writer.close();
            reader.close();
        }
        // -----------------------------------------

        // -----------------------------------------
        // 2. Add edit link if missing
        // -----------------------------------------
        final InputStream content = addAtomEditLink(
                new ByteArrayInputStream(tmpBos.toByteArray()),
                entitySetName,
                Constants.DEFAULT_SERVICE_URL + entitySetName + "(" + entityKey + ")");
        // -----------------------------------------

        // -----------------------------------------
        // 3. Add content element if missing
        // -----------------------------------------
        return addAtomContent(
                content,
                entitySetName,
                Constants.DEFAULT_SERVICE_URL + entitySetName + "(" + entityKey + ")");
        // -----------------------------------------

    }

    public XmlElement getAtomElement(
            final InputStream is,
            final String name)
            throws Exception {
        return getAtomElement(is, name, -1, -1);
    }

    public static XmlElement getAtomElement(
            final InputStream is,
            final String name,
            final int minDepth,
            final int maxDepth)
            throws Exception {
        final XMLEventReader reader = getEventReader(is);
        final XmlElement res = getAtomElement(reader, null, name, null, 0, minDepth, maxDepth, false).getValue();
        reader.close();

        return res;
    }

    public static XmlElement getAtomElement(
            final XMLEventReader reader,
            final XMLEventWriter discarded,
            final String name)
            throws Exception {
        return getAtomElement(reader, discarded, name, null, 0, -1, -1, false).getValue();
    }

    public XmlElement getAtomElement(
            final XMLEventReader reader,
            final XMLEventWriter discarded,
            final String name,
            final Collection<Map.Entry<String, String>> filterAttrs)
            throws Exception {
        return getAtomElement(reader, discarded, name, filterAttrs, 0, -1, -1, false).getValue();
    }

    public static Map.Entry<Integer, XmlElement> getAtomElement(
            final XMLEventReader reader,
            final XMLEventWriter discarded,
            final String name,
            final Collection<Map.Entry<String, String>> filterAttrs,
            final int initialDepth,
            final int minDepth,
            final int maxDepth,
            final boolean filterInOr)
            throws Exception {

        int depth = initialDepth;
        StartElement start = null;

        while (reader.hasNext() && start == null) {
            final XMLEvent event = reader.nextEvent();

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                depth++;

                if ((StringUtils.isBlank(name) || name.trim().equals(event.asStartElement().getName().getLocalPart()))
                        && (minDepth < 0 || minDepth <= depth) && (maxDepth < 0 || maxDepth >= depth)) {

                    boolean match = filterAttrs == null || filterAttrs.isEmpty() || !filterInOr;

                    for (Map.Entry<String, String> filterAttr : filterAttrs == null
                            ? Collections.<Map.Entry<String, String>>emptySet() : filterAttrs) {
                        final Attribute attr =
                                event.asStartElement().getAttributeByName(new QName(filterAttr.getKey().trim()));

                        if (attr == null || !filterAttr.getValue().trim().equals(attr.getValue())) {
                            match = filterInOr ? match : false;
                        } else {
                            match = filterInOr ? true : match;
                        }
                    }

                    if (match) {
                        start = event.asStartElement();
                    }

                }

            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT) {
                depth--;
            }

            if (start == null) {
                if (discarded != null) {
                    discarded.add(event);
                }
            }
        }

        if (start == null) {
            throw new Exception(String.format("Could not find an element named '%s'", name));
        }

        return new SimpleEntry<Integer, XmlElement>(Integer.valueOf(depth - 1), getAtomElement(start, reader));
    }

    public static XmlElement getAtomElement(
            final StartElement start,
            final XMLEventReader reader)
            throws Exception {

        final XmlElement res = new XmlElement();
        res.setStart(start);

        StringWriter content = new StringWriter();

        int depth = 1;

        while (reader.hasNext() && depth > 0) {
            final XMLEvent event = reader.nextEvent();

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && start.getName().getLocalPart().equals(event.asStartElement().getName().getLocalPart())) {
                depth++;
            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                    && start.getName().getLocalPart().equals(event.asEndElement().getName().getLocalPart())) {
                depth--;
            }

            if (depth == 0) {
                res.setEnd(event.asEndElement());
            } else {
                event.writeAsEncodedUnicode(content);
            }
        }

        content.flush();
        content.close();

        res.setContent(new ByteArrayInputStream(content.toString().getBytes()));

        return res;
    }

    private void addAtomElement(
            final InputStream content,
            final XMLEventWriter writer)
            throws Exception {
        final XMLEventReader reader = getEventReader(content);

        final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent newLine = eventFactory.createSpace("\n");

        try {
            writer.add(newLine);

            while (reader.hasNext()) {
                final XMLEvent event = reader.nextEvent();

                if (event.getEventType() != XMLStreamConstants.START_DOCUMENT
                        && event.getEventType() != XMLStreamConstants.END_DOCUMENT
                        && event.getEventType() != XMLStreamConstants.COMMENT) {
                    writer.add(event);
                }
            }
            writer.add(newLine);
        } finally {
            reader.close();
            IOUtils.closeQuietly(content);
        }
    }

    private InputStream addAtomEditLink(
            final InputStream content, final String title, final String href)
            throws Exception {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();

        final ByteArrayOutputStream copy = new ByteArrayOutputStream();
        IOUtils.copy(content, copy);

        IOUtils.closeQuietly(content);

        XMLEventReader reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEventWriter writer = xof.createXMLEventWriter(bos);

        try {
            // check edit link existence
            final XmlElement editLink = getAtomElement(reader, writer, LINK,
                    Collections.<Map.Entry<String, String>>singletonList(
                    new AbstractMap.SimpleEntry<String, String>("rel", "edit")));
            writer.add(editLink.getStart());
            writer.add(editLink.getContentReader());
            writer.add(editLink.getEnd());
            writer.add(reader);
        } catch (Exception e) {
            reader.close();
            reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

            bos = new ByteArrayOutputStream();
            writer = xof.createXMLEventWriter(bos);

            final XmlElement entryElement = getAtomElement(reader, writer, "entry");

            writer.add(entryElement.getStart());
            addAtomElement(
                    IOUtils.toInputStream(String.format("<link rel=\"edit\" title=\"%s\" href=\"%s\" />", title, href)),
                    writer);
            writer.add(entryElement.getContentReader());
            writer.add(entryElement.getEnd());

            writer.add(reader);

            writer.flush();
            writer.close();
        } finally {
            reader.close();
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public InputStream addAtomContent(
            final InputStream content, final String title, final String href)
            throws Exception {
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();

        final ByteArrayOutputStream copy = new ByteArrayOutputStream();
        IOUtils.copy(content, copy);

        IOUtils.closeQuietly(content);

        XMLEventReader reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEventWriter writer = xof.createXMLEventWriter(bos);

        try {
            // check edit link existence
            XmlElement contentElement = getAtomElement(reader, writer, "content");
            writer.add(contentElement.getStart());
            writer.add(contentElement.getContentReader());
            writer.add(contentElement.getEnd());
            writer.add(reader);
        } catch (Exception e) {
            reader.close();
            reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

            bos = new ByteArrayOutputStream();
            writer = xof.createXMLEventWriter(bos);

            if (isMediaContent(title)) {
                final XmlElement entryElement = getAtomElement(reader, writer, "entry");

                writer.add(entryElement.getStart());
                writer.add(entryElement.getContentReader());

                addAtomElement(
                        IOUtils.toInputStream(String.format("<content type=\"*/*\" src=\"%s/$value\" />", href)),
                        writer);

                writer.add(entryElement.getEnd());
            } else {
                try {
                    final XmlElement entryElement = getAtomElement(reader, writer, PROPERTIES);

                    addAtomElement(
                            IOUtils.toInputStream("<content type=\"application/xml\">"),
                            writer);

                    writer.add(entryElement.getStart());
                    writer.add(entryElement.getContentReader());
                    writer.add(entryElement.getEnd());

                    addAtomElement(
                            IOUtils.toInputStream("</content>"),
                            writer);
                } catch (Exception nf) {
                    reader.close();
                    reader = getEventReader(new ByteArrayInputStream(copy.toByteArray()));

                    bos = new ByteArrayOutputStream();
                    writer = xof.createXMLEventWriter(bos);

                    final XmlElement entryElement = getAtomElement(reader, writer, "entry");
                    writer.add(entryElement.getStart());
                    writer.add(entryElement.getContentReader());

                    addAtomElement(
                            IOUtils.toInputStream("<content type=\"application/xml\"/>"),
                            writer);

                    writer.add(entryElement.getEnd());
                }
            }

            writer.add(reader);

            writer.flush();
            writer.close();
        } finally {
            reader.close();
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public int countAllElements(final String entitySetName) throws Exception {
        final String basePath = entitySetName + File.separatorChar;
        int count = countFeedElements(fsManager.readFile(basePath + FEED, Accept.XML), "entry");

        final String skipTokenDirPath = fsManager.getAbsolutePath(basePath + SKIP_TOKEN, null);


        try {
            final FileObject skipToken = fsManager.resolve(skipTokenDirPath);
            final FileObject[] files = fsManager.findByExtension(skipToken, Accept.XML.getExtension().substring(1));

            for (FileObject file : files) {
                count += countFeedElements(fsManager.readFile(
                        basePath + SKIP_TOKEN + File.separatorChar + file.getName().getBaseName(), null), "entry");
            }
        } catch (FileSystemException fse) {
            LOG.debug("Resource path '{}' not found", skipTokenDirPath);
        }


        return count;
    }

    private int countFeedElements(final InputStream is, final String elementName) throws XMLStreamException {
        final XMLEventReader reader = getEventReader(is);

        int count = 0;

        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && elementName.equals(event.asStartElement().getName().getLocalPart())) {
                count++;
            }
        }

        reader.close();
        return count;
    }

    public static StartElement getPropertyStartTag(final XMLEventReader propReader, final String[] path)
            throws Exception {
        int pos = 0;

        StartElement property = null;

        while (propReader.hasNext() && pos < path.length) {
            final XMLEvent event = propReader.nextEvent();

            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && (ATOM_PROPERTY_PREFIX + path[pos].trim()).equals(
                    event.asStartElement().getName().getLocalPart())) {
                pos++;
                if (path.length == pos) {
                    property = event.asStartElement();
                }
            }
        }

        if (property == null) {
            throw new NotFoundException();
        }

        return property;
    }

    public String getEdmTypeFromXML(final InputStream is, final String[] path)
            throws Exception {
        final XMLEventReader reader = getEventReader(is);

        final Attribute type = getPropertyStartTag(reader, path).getAttributeByName(new QName(TYPE));

        reader.close();

        if (type == null) {
            throw new NotFoundException();
        }

        return type.getValue();
    }

    public InputStream getAtomProperty(final InputStream is, final String[] path)
            throws Exception {
        final XMLEventReader reader = getEventReader(is);

        final XmlElement props = getAtomElement(reader, null, PROPERTIES);
        final XMLEventReader propsReader = props.getContentReader();

        reader.close();

        final InputStream propertyStream = writeFromStartToEndElement(
                getPropertyStartTag(propsReader, path),
                propsReader,
                true);

        if (propertyStream == null) {
            throw new NotFoundException();
        }

        return propertyStream;
    }

    private InputStream writeFromStartToEndElement(
            final StartElement element, final XMLEventReader reader, final boolean document)
            throws XMLStreamException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        final QName name = element.getName();

        if (document) {
            final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            writer.add(eventFactory.createStartDocument("UTF-8", "1.0"));
            writer.add(element);
            writer.add(eventFactory.createNamespace(ATOM_PROPERTY_PREFIX.substring(0, 1), DATASERVICES_NS));
            writer.add(eventFactory.createNamespace(ATOM_METADATA_PREFIX.substring(0, 1), METADATA_NS));
        } else {
            writer.add(element);
        }

        XMLEvent event = element;

        while (reader.hasNext() && !(event.isEndElement() && name.equals(event.asEndElement().getName()))) {
            event = reader.nextEvent();
            writer.add(event);
        }

        writer.flush();
        writer.close();

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public InputStream addAtomInlinecount(
            final InputStream feed, final int count, final Accept accept)
            throws Exception {
        final XMLEventReader reader = getEventReader(feed);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        try {

            final XmlElement feedElement = getAtomElement(reader, writer, "feed");

            writer.add(feedElement.getStart());
            addAtomElement(IOUtils.toInputStream(String.format("<m:count>%d</m:count>", count)), writer);
            writer.add(feedElement.getContentReader());
            writer.add(feedElement.getEnd());

            while (reader.hasNext()) {
                writer.add(reader.nextEvent());
            }

        } finally {
            writer.flush();
            writer.close();
            reader.close();
            IOUtils.closeQuietly(feed);
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public static InputStream getAtomPropertyValue(final InputStream is, final String[] path)
            throws Exception {
        final XmlElement props = getAtomElement(is, PROPERTIES, 2, 3);
        final XMLEventReader propsReader = props.getContentReader();

        // search for property start element
        getPropertyStartTag(propsReader, ArrayUtils.subarray(path, 0, path.length - 1));

        final InputStream res;

        XMLEvent event = propsReader.nextEvent();

        // expected text node
        if (event.isCharacters()) {
            res = new ByteArrayInputStream(event.asCharacters().getData().getBytes());
        } else if (event.isEndElement()) {
            throw new NotFoundException();
        } else {
            throw new Exception("The method or operation is not implemented.");
        }

        return res;
    }

    @Override
    public InputStream selectEntity(final InputStream entity, final String[] propertyNames) throws Exception {
        final XMLEventReader reader = getEventReader(entity);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        final List<String> found = new ArrayList<String>(Arrays.asList(propertyNames));

        boolean inProperties = false;
        boolean writeCurrent = true;
        Boolean writeNext = null;
        String currentName = null;

        final List<String> fieldToBeSaved = new ArrayList<String>(Arrays.asList(propertyNames));

        while (reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && LINK.equals(event.asStartElement().getName().getLocalPart())
                    && !fieldToBeSaved.contains(
                    event.asStartElement().getAttributeByName(new QName("title")).getValue())
                    && !"edit".equals(event.asStartElement().getAttributeByName(new QName("rel")).getValue())) {
                writeCurrent = false;
            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                    && LINK.equals(event.asEndElement().getName().getLocalPart())) {
                writeNext = true;
            } else if (event.getEventType() == XMLStreamConstants.START_ELEMENT
                    && (PROPERTIES).equals(event.asStartElement().getName().getLocalPart())) {
                writeCurrent = true;
                writeNext = false;
                inProperties = true;
            } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                    && (PROPERTIES).equals(event.asEndElement().getName().getLocalPart())) {
                writeCurrent = true;
            } else if (inProperties) {
                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    final String elementName = event.asStartElement().getName().getLocalPart();

                    for (String propertyName : propertyNames) {
                        if ((ATOM_PROPERTY_PREFIX + propertyName.trim()).equals(elementName)) {
                            writeCurrent = true;
                            found.remove(propertyName);
                            currentName = propertyName;
                        }
                    }

                } else if (event.getEventType() == XMLStreamConstants.END_ELEMENT
                        && StringUtils.isNotBlank(currentName)
                        && (ATOM_PROPERTY_PREFIX + currentName.trim()).equals(
                        event.asEndElement().getName().getLocalPart())) {
                    writeNext = false;
                    currentName = null;
                }

            }

            if (writeCurrent) {
                writer.add(event);
            }

            if (writeNext != null) {
                writeCurrent = writeNext;
                writeNext = null;
            }
        }

        writer.flush();
        writer.close();
        reader.close();
        IOUtils.closeQuietly(entity);

        // Do not raise any exception in order to support FC properties as well
        // if (!found.isEmpty()) {
        //     throw new Exception(String.format("Could not find a properties '%s'", found));
        // }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    public InputStream readEntities(
            final List<String> links, final String linkName, final String next, final boolean forceFeed)
            throws Exception {

        if (links.isEmpty()) {
            throw new NotFoundException();
        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (forceFeed || links.size() > 1) {
            // build a feed
            bos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>".getBytes());

            bos.write(("<feed xml:base=\"" + DEFAULT_SERVICE_URL + "\" "
                    + "xmlns=\"http://www.w3.org/2005/Atom\" "
                    + "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" "
                    + "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">")
                    .getBytes());

            bos.write(("<id>" + DEFAULT_SERVICE_URL + "entityset(entityid)/" + linkName + "</id>").getBytes());

            bos.write(("<title type=\"text\">" + linkName + "</title>").getBytes());
            bos.write("<updated>2014-03-03T13:40:49Z</updated>".getBytes());
            bos.write(("<link rel=\"self\" title=\"" + linkName + "\" href=\"" + linkName + "\" />").getBytes());
        }

        for (String link : links) {
            try {
                final Map.Entry<String, String> uri = Commons.parseEntityURI(link);

                final XmlElement entry =
                        getAtomElement(readEntity(uri.getKey(), uri.getValue(), Accept.ATOM).getValue(), "entry");

                IOUtils.copy(entry.toStream(), bos);
            } catch (Exception e) {
                // log and ignore link
                LOG.warn("Error parsing uri {}", link, e);
            }
        }

        if (forceFeed || links.size() > 1) {

            if (StringUtils.isNotBlank(next)) {
                bos.write(String.format("<link rel=\"next\" href=\"%s\" />", next).getBytes());
            }

            bos.write("</feed>".getBytes());
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    public Map<String, InputStream> getChanges(final InputStream src) throws Exception {
        final Map<String, InputStream> res = new HashMap<String, InputStream>();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(src, bos);
        IOUtils.closeQuietly(src);

        // retrieve properties ...
        XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

        final Map.Entry<Integer, XmlElement> propertyElement =
                getAtomElement(reader, null, PROPERTIES, null, 0, 2, 3, false);
        reader.close();

        reader = propertyElement.getValue().getContentReader();

        try {
            while (true) {
                final XmlElement property = getAtomElement(reader, null, null);
                res.put(property.getStart().getName().getLocalPart(), property.toStream());
            }
        } catch (Exception ignore) {
            // end
        }

        reader.close();

        // retrieve links ...
        reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

        try {
            int pos = 0;
            while (true) {
                final Map.Entry<Integer, XmlElement> linkElement =
                        getAtomElement(reader, null, LINK, null, pos, 2, 2, false);

                res.put("[LINK]" + linkElement.getValue().getStart().getAttributeByName(new QName("title")).getValue(),
                        linkElement.getValue().toStream());

                pos = linkElement.getKey();
            }
        } catch (Exception ignore) {
            // end
        }

        return res;
    }

    @Override
    public InputStream setChanges(
            final InputStream toBeChanged,
            final Map<String, InputStream> properties)
            throws Exception {
        XMLEventReader reader = getEventReader(toBeChanged);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLEventWriter writer = xof.createXMLEventWriter(bos);

        // ---------------------------------
        // add property changes
        // ---------------------------------
        Map.Entry<Integer, XmlElement> propertyElement =
                getAtomElement(reader, writer, PROPERTIES, null, 0, 2, 3, false);

        writer.flush();

        ByteArrayOutputStream pbos = new ByteArrayOutputStream();
        OutputStreamWriter pwriter = new OutputStreamWriter(pbos);

        final XMLEventReader propertyReader = propertyElement.getValue().getContentReader();

        try {
            while (true) {
                final XmlElement property = getAtomElement(propertyReader, null, null);
                final String name = property.getStart().getName().getLocalPart();

                if (properties.containsKey(name)) {
                    // replace
                    final InputStream replacement = properties.get(name);
                    properties.remove(property.getStart().getName().getLocalPart());
                    pwriter.append(IOUtils.toString(replacement));
                    IOUtils.closeQuietly(replacement);
                } else {
                    pwriter.append(IOUtils.toString(property.toStream()));
                }
            }
        } catch (Exception ignore) {
            // end
        }

        for (Map.Entry<String, InputStream> remains : properties.entrySet()) {
            if (!remains.getKey().startsWith("[LINK]")) {
                pwriter.append(IOUtils.toString(remains.getValue()));
                IOUtils.closeQuietly(remains.getValue());
            }
        }

        pwriter.flush();
        pwriter.close();

        writer.add(propertyElement.getValue().getStart());
        writer.add(new XMLEventReaderWrapper(new ByteArrayInputStream(pbos.toByteArray())));
        writer.add(propertyElement.getValue().getEnd());

        IOUtils.closeQuietly(pbos);

        writer.add(reader);
        reader.close();
        writer.flush();
        writer.close();
        // ---------------------------------

        // ---------------------------------
        // add navigationm changes
        // ---------------------------------

        // remove existent links
        for (Map.Entry<String, InputStream> remains : properties.entrySet()) {

            if (remains.getKey().startsWith("[LINK]")) {
                reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

                bos.reset();
                writer = xof.createXMLEventWriter(bos);

                try {
                    final String linkName = remains.getKey().substring(remains.getKey().indexOf("]") + 1);

                    getAtomElement(reader, writer, LINK,
                            Collections.<Map.Entry<String, String>>singleton(new SimpleEntry<String, String>(
                            "title", linkName)), 0, 2, 2, false);

                    writer.add(reader);

                } catch (Exception ignore) {
                    // ignore
                }

                writer.flush();
                writer.close();
            }
        }

        reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));

        bos.reset();
        writer = xof.createXMLEventWriter(bos);

        propertyElement = getAtomElement(reader, writer, CONTENT, null, 0, 2, 2, false);
        writer.flush();

        pbos.reset();
        pwriter = new OutputStreamWriter(pbos);

        for (Map.Entry<String, InputStream> remains : properties.entrySet()) {
            if (remains.getKey().startsWith("[LINK]")) {
                pwriter.append(IOUtils.toString(remains.getValue()));
                IOUtils.closeQuietly(remains.getValue());
            }
        }

        pwriter.flush();
        pwriter.close();

        writer.add(new XMLEventReaderWrapper(new ByteArrayInputStream(pbos.toByteArray())));
        IOUtils.closeQuietly(pbos);

        writer.add(propertyElement.getValue().getStart());
        writer.add(propertyElement.getValue().getContentReader());
        writer.add(propertyElement.getValue().getEnd());

        writer.add(reader);
        reader.close();
        writer.flush();
        writer.close();
        // ---------------------------------

        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    protected InputStream replaceLink(
            final InputStream toBeChanged, final String linkName, final InputStream replacement)
            throws Exception {
        final XMLEventReader reader = getEventReader(toBeChanged);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final XMLOutputFactory xof = XMLOutputFactory.newInstance();
        final XMLEventWriter writer = xof.createXMLEventWriter(bos);

        final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent newLine = eventFactory.createSpace("\n");

        try {
            final XmlElement linkElement =
                    getAtomElement(reader, writer, LINK,
                    Collections.<Map.Entry<String, String>>singletonList(
                    new SimpleEntry<String, String>("title", linkName)));
            writer.add(linkElement.getStart());

            // ------------------------------------------
            // write inline ...
            // ------------------------------------------
            writer.add(newLine);
            writer.add(eventFactory.createStartElement("m", null, "inline"));

            addAtomElement(replacement, writer);

            writer.add(eventFactory.createEndElement("m", null, "inline"));
            writer.add(newLine);
            // ------------------------------------------

            writer.add(linkElement.getEnd());

            writer.add(reader);
            writer.flush();
            writer.close();
        } finally {
            reader.close();
            IOUtils.closeQuietly(toBeChanged);
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }

    public static Map.Entry<String, List<String>> extractLinkURIs(final InputStream is)
            throws Exception {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        IOUtils.closeQuietly(is);

        XMLEventReader reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));
        final List<String> links = new ArrayList<String>();
        try {
            while (true) {
                links.add(IOUtils.toString(getAtomElement(reader, null, "uri").getContent()));
            }
        } catch (Exception ignore) {
            // End document reached ...
        }
        reader.close();

        String next;

        reader = getEventReader(new ByteArrayInputStream(bos.toByteArray()));
        try {
            next = IOUtils.toString(getAtomElement(reader, null, "next").getContent());
        } catch (Exception ignore) {
            // next link is not mandatory
            next = null;
        }
        reader.close();

        return new AbstractMap.SimpleEntry<String, List<String>>(next, links);
    }
}
